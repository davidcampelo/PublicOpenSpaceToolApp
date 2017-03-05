package org.davidcampelo.post.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.utils.Constants;

import java.util.StringTokenizer;

/**
 * QuestionView subclass used to present Questions of QuestionType == VARIABLE_SINGLE_CHOICE)
 *
 * IMPORTANT NOTE: Thia QuestionView was specially created to handle Question 29,
 * where one or many areas can be present in a given P.O.S.
 *
 * Created by davidcampelo on 8/6/16.
 */
public class VariableSingleQuestionView extends QuestionView {
    LinearLayout container;
    /**
     * Internal counter Question set being shown
     */
    int questionCounter;

    Button addButton, removeButton;

    public VariableSingleQuestionView(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void init(Context context, Question question) {
        super.init(context, question);
        container = getContainer();
        questionCounter = 0;
        addMoreAndRemove();
        // by default show one set of answers
        createAndAddQuestionSet();
    }

    /**
     * Append to the main container Add/Remove buttons
     */
    private void addMoreAndRemove() {
        final Context context = getContext();

        removeButton = new Button(context);
        removeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(android.R.drawable.ic_delete), null, null, null);
        removeButton.setText("Remove area");
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // always remove the area with the highest number (the latest)
                removeLastQuestionsSet();
            }
        });
        container.addView(removeButton);
        removeButton.setVisibility(View.INVISIBLE);


        addButton = new Button(context);
        addButton.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(android.R.drawable.ic_input_add), null, null, null);
        addButton.setText("Add area");
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndAddQuestionSet();
            }
        });
        container.addView(addButton);
    }

    /**
     * Remove last added area
     */
    private void removeLastQuestionsSet() {
        // getChildCount()-3 is used by default as at least 2 other View are already added to the
        // main container: Add/Remove buttons
        container.removeView(container.getChildAt(container.getChildCount()-3));
        questionCounter--;
        removeButton.setVisibility(questionCounter > 1? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Create and Add a new set of answers
     */
    private LinearLayout createAndAddQuestionSet() {
        final Context context = getContext();
        final Question question = getQuestion();
        questionCounter++;

        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView areaTitle = new TextView(context);
        areaTitle.setText("Area "+ String.valueOf(questionCounter));
        layout.addView(areaTitle);

        for (Option option : question.getAllOptions()) {
            QuestionCheckBox checkbox = new QuestionCheckBox(context, option, this);
            checkbox.setText(option.getTitle());
            layout.addView(checkbox);
        }

        // always add new area before the remove button
        container.addView(layout, container.getChildCount()-2);

        // At least one Question set must be shown
        removeButton.setVisibility(questionCounter > 1? View.VISIBLE : View.INVISIBLE);

        return layout;
    }

    @Override
    /**
     * Answer is in the format:
     * NUMBER_OF_AREAS | ANSWER_1 | ANSWER_2  | .. | ANSWER_N
     */
    public String getAnswers() throws AnswerMissingException {
        StringBuilder answers = new StringBuilder();
        int answersCount = 0;
        answers.append(questionCounter + Constants.DEFAULT_SEPARATOR);
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount && container.getChildAt(i) instanceof LinearLayout; i++) {
            LinearLayout child = (LinearLayout) container.getChildAt(i);
            for (int j = child.getChildCount(); --j >= 0 && child.getChildAt(j) instanceof QuestionCheckBox; ) {
                QuestionCheckBox checkBox = (QuestionCheckBox) child.getChildAt(j);
                if (checkBox.isChecked()) {
                    answersCount++;
                    answers.append(String.valueOf(checkBox.getOption().getValue()) + Constants.DEFAULT_SEPARATOR);
                    break;
                }
            }
        }

        // Forces to have answers for all areas being shown
        // But accepts if one area is being shown with no answers
        if (answersCount < questionCounter &&
                !(answersCount == 0 && questionCounter == 1)) {
            throw new AnswerMissingException(getQuestion());
        }
        return answers.toString();
    }

    @Override
    /**
     * See the format of answer at {@VariableSingleQuestionView#getAnswers}
     */
    public void setAnswers(String answers){
        if (answers == null || answers.length() == 0)
            return;
        // Clean up the previously added "default" Area
        // This area was added by the constructor
        removeLastQuestionsSet();

        // get count of questions
        int index = answers.indexOf(Constants.DEFAULT_SEPARATOR);
        int areaCount = Integer.valueOf( answers.substring(0, index) );
        answers = answers.substring(index+1);

        StringTokenizer tokenizer = new StringTokenizer(answers, Constants.DEFAULT_SEPARATOR);
        while ( tokenizer.hasMoreElements() && areaCount-- > 0  ) {
            String selectedValue = (String)tokenizer.nextElement();

            LinearLayout layout = createAndAddQuestionSet();
            for (int i = layout.getChildCount(); --i >= 0 && layout.getChildAt(i) instanceof QuestionCheckBox; ) {
                QuestionCheckBox checkBox = (QuestionCheckBox) layout.getChildAt(i);
                if (checkBox.getOption().getValue().equals(selectedValue) ) {
                    checkBox.setCheckedNoNotify(true);
                    continue;
                }
            }
        }
    }

    @Override
    public void notifyChecked(QuestionCheckBox checked){

        LinearLayout parent = (LinearLayout)checked.getParent();
        for (int i = parent.getChildCount(); --i >= 0; ) {
            final View child = parent.getChildAt(i);
            if (child instanceof QuestionCheckBox) {
                QuestionCheckBox checkbox = ((QuestionCheckBox) child);
                if (checkbox != checked)
                    checkbox.setCheckedNoNotify(false);
            }
        }
    }
}

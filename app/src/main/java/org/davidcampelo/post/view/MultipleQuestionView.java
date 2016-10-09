package org.davidcampelo.post.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.utils.Constants;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * QuestionView subclass used to present Questions of QuestionType == MULTIPLE_CHOICE
 *
 * Amount of Option objects is flexible, according to the "Other" options usage
 *
 * NOTE: Answers stored in thee database are the selected Option IDs
 *
 * Created by davidcampelo on 8/6/16.
 */
public class MultipleQuestionView extends QuestionView{
    LinearLayout container;

    public MultipleQuestionView(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void init(Context context, Question question) {
        super.init(context, question);

        container = getContainer();

        boolean addOther = false;
        for (Option option : question.getAllOptions()){
            // XXX Options with ALIAS == OTHER are the options to be added by the user
            if ( !option.getAlias().equals("OTHER") ) {
                QuestionCheckBox checkbox = new QuestionCheckBox(context, option, this);
                checkbox.setText(option.getTitle());
                container.addView(checkbox);
            } else {
                addOther = true;
            }
        }
        if (addOther) {
            addOtherRow(question, context);
        }
    }

    /**
     *  create "Other option" (new option added by the user) view
     *
     * NOTE: This component owns a list of {@QuestionCheckBox} and a set of {@View} to show "Other"
     * Options. In case of having an "Other" Option, his new Option will be stored in the TAG of the
     * respective View :)
     */
    private void addOtherRow(final Question question, final Context context) {

        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View otherView = layoutInflater.inflate(R.layout.question_view_other_row, null);


        // Button of storing this new "Other" Option
        ImageView imageButtonAdd = (ImageView) otherView.findViewById(R.id.otherAddButton);
        imageButtonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stores new Row with new Option
                View otherView = container.findViewById(R.id.otherRow);
                final View addView = layoutInflater.inflate(R.layout.question_view_added_row, null);
                final TextView textOut = (TextView)addView.findViewById(R.id.otherAddedText);
                ImageView imageButtonRemove = (ImageView)addView.findViewById(R.id.otherRemoveButton);
                imageButtonRemove.setOnClickListener(new OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        ((LinearLayout)addView.getParent()).removeView(addView);
                        question.removeOtherOptionByText(textOut.getText()+ "");
                    }
                });

                if (otherView != null) {
                    // Create new Option object
                    String newOptionTitle = ((EditText)container.findViewById(R.id.otherInputText)).getText() + "";
                    Option newOption = new Option("OTHER_ALIAS", "1", newOptionTitle, question);
                    question.addOption(newOption);
                    textOut.setText(newOptionTitle);
                    // XXX if it's an "Other" Option, it will be stored in the TAG of the VIEW
                    addView.setTag(newOption);
                    // Remove old "Other" editable row
                    container.removeView(otherView);
                }

                // add new Option view to the Screen
                container.addView(addView);
                // And finally create new "Other" editable row
                addOtherRow(question, context);
            }
        });

        // add new editable "Other" view
        container.addView(otherView);
        otherView.setId(R.id.otherRow);
    }

    @Override
    public String getAnswers() {
        StringBuilder answers = new StringBuilder();
        for (int i = container.getChildCount(); --i >= 0;) {
            View child = container.getChildAt(i);
            if (child instanceof QuestionCheckBox) {
                QuestionCheckBox checkBox = (QuestionCheckBox) child;
                if (checkBox.isChecked()) {
                    answers.append(String.valueOf(checkBox.getOption().getId()) + Constants.QUESTION_ANSWERS_SEPARATOR);
                }
            }
            else if (child.getTag() instanceof Option) { // it's an "Other" option and is stored in the TAG
                answers.append(String.valueOf( ((Option)child.getTag()).getId() ) + Constants.QUESTION_ANSWERS_SEPARATOR);
            }
        }

        return answers.toString();
    }

    @Override
    public void setAnswers(String answers) {
        if (answers == null || answers.length() == 0)
            return;

        StringTokenizer tokenizer = new StringTokenizer(answers, Constants.QUESTION_ANSWERS_SEPARATOR);
        ArrayList<Long> selectedIds = new ArrayList<>();

        while ( tokenizer.hasMoreElements() ) {
            String selectedId = (String) tokenizer.nextElement();
            if (selectedId == null || selectedId.length() == 0)
                continue;
            selectedIds.add(Long.valueOf( selectedId ));
        }

        for (int i = container.getChildCount(); --i >= 0;) {
            View child = container.getChildAt(i);
            if (child instanceof QuestionCheckBox) {
                QuestionCheckBox checkBox = (QuestionCheckBox) child;
                if (selectedIds.contains( checkBox.getOption().getId() )) {
                    checkBox.setCheckedNoNotify(true);
                }
            }
        }
    }

    @Override
    public void notifyChecked(QuestionCheckBox checked) {
    }
}

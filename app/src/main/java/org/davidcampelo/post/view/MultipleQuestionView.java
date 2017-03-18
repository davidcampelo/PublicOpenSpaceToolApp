package org.davidcampelo.post.view;

import android.content.Context;
import android.graphics.Path;
import android.view.View;
import android.widget.LinearLayout;

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
    QuestionViewOtherContainer otherContainer;

    public MultipleQuestionView(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void init(Context context, Question question) {
        super.init(context, question);

        container = getContainer();

        Option otherOption = null;
        for (Option option : question.getAllOptions()){
            // Options added by user will be handled later :)
            if (option.wasAddedByUser())
                continue;
            // XXX Options with ALIAS == OTHER are the options to be added by the user
            if ( !option.isOtherOption()) {
                QuestionCheckBox checkbox = new QuestionCheckBox(context, option, this);
                checkbox.setText(option.getTitle());
                container.addView(checkbox);
            } else {
                otherOption = option;
            }
        }
        if (otherOption != null) {
            otherContainer = new QuestionViewOtherContainer(context, question, otherOption, this);
            container.addView(otherContainer);
        }
    }

    @Override
    public String getAnswers() {
        if (!isEnabled())
                return Constants.DEFAULT_NOT_ENABLED_QUESTION;
        StringBuilder answers = new StringBuilder();
        for (int i = container.getChildCount(); --i >= 0;) {
            View child = container.getChildAt(i);
            if (child instanceof QuestionCheckBox) {
                QuestionCheckBox checkBox = (QuestionCheckBox) child;
                if (checkBox.isChecked()) {
                    answers.append(String.valueOf(checkBox.getOption().getId()) +
                            Constants.DEFAULT_SEPARATOR);
                }
            }
        }
        /** XXX Answers of Custom added user Options will be handled directly
        //      So in the {@PublicOpenSpaceAsyncTask} all the CustomAddedOptions in the Question object
        //      will be saved
        */
//        if (getQuestion().hasCustomAddedOptions())
//            answers.append(otherContainer.getAnswers());

        return answers.toString();
    }

    @Override
    public void setAnswers(String answers) {
        if (answers == null || answers.length() == 0)
            return;

        StringTokenizer tokenizer = new StringTokenizer(answers, Constants.DEFAULT_SEPARATOR);
        ArrayList<Long> selectedIds = new ArrayList<>();

        while (tokenizer.hasMoreElements()) {
            String selectedId = (String) tokenizer.nextElement();
            if (selectedId == null || selectedId.length() == 0)
                continue;
            selectedIds.add(Long.valueOf(selectedId));
        }

        // Check "default" Options
        for (int i = container.getChildCount(); --i >= 0; ) {
            View child = container.getChildAt(i);
            if (child instanceof QuestionCheckBox) {
                QuestionCheckBox checkBox = (QuestionCheckBox) child;
                if (selectedIds.contains(checkBox.getOption().getId())) {
                    checkBox.setCheckedNoNotify(true);
                }
            }
        }

        if (getQuestion().hasCustomAddedOptions()){
            otherContainer.setAnswers();
        }

    }

    @Override
    public void notifyChecked(QuestionCheckBox checked) {
    }
}

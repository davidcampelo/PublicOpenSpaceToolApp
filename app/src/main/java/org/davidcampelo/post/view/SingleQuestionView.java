package org.davidcampelo.post.view;

import android.content.Context;
import android.widget.LinearLayout;

import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.Question;

/**
 * QuestionView subclass used to present Questions of QuestionType == SINGLE_CHOICE)
 *
 * Created by davidcampelo on 8/6/16.
 */
public class SingleQuestionView extends QuestionView {
    LinearLayout container;

    public SingleQuestionView(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void init(Context context, Question question) {
        super.init(context, question);
        LinearLayout container = getContainer();

        for (Option option : question.getAllOptions()) {
            if (question.getType() == Question.QuestionType.SINGLE_CHOICE) {
                QuestionCheckBox checkbox = new QuestionCheckBox(context, option, this);
                checkbox.setText(option.getText());
                checkbox.setChecked(option.isChecked());
                container.addView(checkbox);
            }
        }
    }

    @Override
    public String getAnswers() {
        for (Option option : getQuestion().getAllOptions()) {
            if (option.isChecked())
                return String.valueOf(option.getId());
        }
        return "";
    }
}

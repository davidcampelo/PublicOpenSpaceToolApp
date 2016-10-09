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
        container = getContainer();

        for (Option option : question.getAllOptions()) {
            QuestionCheckBox checkbox = new QuestionCheckBox(context, option, this);
            checkbox.setText(option.getTitle());
            container.addView(checkbox);
        }
    }

    @Override
    public String getAnswers() {
        for (int i = container.getChildCount(); --i >= 0;) {
            QuestionCheckBox checkBox = (QuestionCheckBox)container.getChildAt(i);
            if (checkBox.isChecked())
                return String.valueOf(checkBox.getOption().getId());
        }
        return "";
    }

    @Override
    public void setAnswers(String text){
        if (text == null || text.length() == 0)
            return;

        int selectedId = Integer.valueOf(text);
        for (int i = container.getChildCount(); --i >= 0;) {
            QuestionCheckBox checkBox = (QuestionCheckBox) container.getChildAt(i);
            if (checkBox.getOption().getId() == selectedId) {
                checkBox.setCheckedNoNotify(true);
                break;
            }
        }
    }

}

package org.davidcampelo.post.view;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.Question;

/**
 * QuestionView subclass used to present Questions of QuestionType == INPUT_NUMBER
 *
 * Created by davidcampelo on 8/6/16.
 */
public class InputNumberQuestionView extends QuestionView {

    EditText containerText;

    public InputNumberQuestionView(Context context, Question question) {
        super(context, question);
    }


    @Override
    protected void init(Context context, Question question) {
        super.init(context, question);
        LinearLayout container = getContainer();
        containerText = new EditText(context);
        containerText.setInputType(InputType.TYPE_CLASS_NUMBER);

        container.addView(containerText);
    }

    @Override
    public String getAnswers() {
        return containerText.getText() + "";
    }

    @Override
    public void setAnswers(String text) {
        containerText.setText(text);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (!enabled)
            containerText.setText("");
    }
}

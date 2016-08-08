package org.davidcampelo.post.view;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.davidcampelo.post.model.Question;

/**
 * QuestionView subclass used to present Questions of QuestionType == INPUT_TEXT
 *
 * Created by davidcampelo on 8/6/16.
 */
public class InputTextQuestionView extends QuestionView {

    EditText containerText;

    public InputTextQuestionView(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void init(Context context, Question question) {
        super.init(context, question);
        LinearLayout container = getContainer();

        containerText = new EditText(context);
        containerText.setInputType(InputType.TYPE_CLASS_TEXT);

        container.addView(containerText);
    }

    public void setAnswers(String text) {
        containerText.setText(text);
    }

    public String getContainerText() {
        return containerText.getText() +"";
    }

    public String getAnswers() {
        return getContainerText();
    }
}

package org.davidcampelo.post.view;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.davidcampelo.post.model.Question;

/**
 * Created by davidcampelo on 8/6/16.
 */
public class InputDecimalQuestionView extends QuestionView {
    public InputDecimalQuestionView(Context context, Question question) {
        super(context, question);
    }


    @Override
    protected void init(Context context, Question question) {
        super.init(context, question);
        LinearLayout container = getContainer();

        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        input.set

        container.addView(input);
    }
}

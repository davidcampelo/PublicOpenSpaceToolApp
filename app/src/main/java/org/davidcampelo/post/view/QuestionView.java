package org.davidcampelo.post.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.Question;

/**
 * Created by davidcampelo on 8/1/16.
 */
public abstract class QuestionView extends RelativeLayout {

    private Question question;
    private Context context;

    private View rootView;

    private TextView title;
    private LinearLayout container;

    public QuestionView(Context context) {
        super(context);
        init(context, null);
    }

    public QuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public QuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, null);
    }

    public QuestionView(Context context, Question question){
        super(context);
        init(context, question);
    }

    /**
     * Sets up Context and Question, then creates Question title and Option's container
     *
     * This method must be called by sub-classes before any other UI operation
     * @param context
     * @param question
     */
    protected void init(final Context context, Question question) {
        this.question = question;
        this.context = context;

        rootView = inflate(context, R.layout.question_view, this);

        title = (TextView) rootView.findViewById(R.id.QuestionView_title);
        title.setText(question.getNumber() +" - "+ question.getTitle());

        container = (LinearLayout)findViewById(R.id.QuestionView_containerOptions);
    }


    public void notifyChecked(QuestionCheckBox checked){
        if (question.getType() == Question.QuestionType.MULTIPLE_CHOICE)
            return;

            for (int i = container.getChildCount() - 1; i >= 0; i--) {
                final View child = container.getChildAt(i);
                if (child instanceof QuestionCheckBox) {
                    QuestionCheckBox checkbox = ((QuestionCheckBox) child);
                    if (checkbox != checked)
                        checkbox.setCheckedNoNotify(false);
                }
            }
    }


    /**
     * Container of options (will be filled according to subclasses - Question types)
     *
     * @return
     */
    public LinearLayout getContainer() {
        return container;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        for (int i = container.getChildCount() - 1; i >= 0; i--) {
            container.getChildAt(i).setEnabled(enabled);
        }
    }

    public Question getQuestion() {
        return question;
    }

    /** Abstract method implemented by subclasses in order to retrieve user answers
     */
    public abstract String getAnswers();
}

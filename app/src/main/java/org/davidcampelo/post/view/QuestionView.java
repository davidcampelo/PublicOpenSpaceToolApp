package org.davidcampelo.post.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.Question;

import java.util.ArrayList;

/**
 * Created by davidcampelo on 8/1/16.
 */
public abstract class QuestionView extends RelativeLayout {

    private Question question;
    private Context context;

    private View rootView;

    private TextView title;
    private ImageView hintButton;
    private LinearLayout container;
    private DisableQuestionNumbersListener disableQuestionNumbersListener;

    public QuestionView(Context context, Question question){
        super(context);
        init(context, question);
    }

    /**
     * Sets up Context and Question, then creates Question title and Option's container
     *
     * IMPORTANT: This method must be called by sub-classes before any other UI operation
     */
    protected void init(final Context context, final Question question) {
        this.question = question;
        this.context = context;

        rootView = inflate(context, R.layout.question_view, this);

        hintButton = (ImageView) rootView.findViewById(R.id.QuestionView_hintButton);
        final String hintText = question.getHint();
        if (hintText != null && hintText.trim().length() > 0) {
            hintButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(QuestionView.this.context);
                    alert.setTitle("Question "+ question.getNumber());
                     alert.setMessage(hintText.trim());

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Your action here
                        }
                    });

//                    alert.setNegativeButton("Cancel",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                }
//                            });

                    alert.show();


                    //Toast.makeText(QuestionView.this.context, hintText.trim(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            hintButton.setVisibility(INVISIBLE);
        }
        title = (TextView) rootView.findViewById(R.id.QuestionView_title);
        String questionNumber = question.getNumber();
        if (questionNumber.startsWith("0"));
            questionNumber.substring(1, questionNumber.length()-1);
        title.setText(questionNumber +" - "+ question.getTitle());

        container = (LinearLayout)findViewById(R.id.QuestionView_containerOptions);
    }

    /**
     * {@QuestionCheckBox} send this notification to its parent QuestionView in order
     * to handle single and multiple option answers
     *
     * NOTE: By default, for {@SingleQuestionView} if an option is checked, all the other should be unchecked
     *       Otherwise, for {@MultipleQuestionView} if an option is checked, nothing must be done
     */
    public void notifyChecked(QuestionCheckBox checked){
        for (int i = container.getChildCount(); --i >= 0; ) {
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
    protected LinearLayout getContainer() {
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
     *
     * Answer text depends on QuestionType:
     * - SINGLE_CHOICE: opt_id
     * - MULTIPLE_CHOICE: opt_id's separated by a character separator (slash == \)
     * - INPUT_TEXT/NUMBER/DECIMAL: Text typed by the user
     */
    public abstract String getAnswers() throws AnswerMissingException;

    /** Abstract method implemented by subclasses in order to show user answers
     *
     * Answer text depends on QuestionType:
     * - SINGLE_CHOICE: opt_id
     * - MULTIPLE_CHOICE: opt_id's separated by a character separator (slash == \)
     * - INPUT_TEXT/NUMBER/DECIMAL: Text typed by the user
     */
    public abstract void setAnswers(String answers);

    /** The methods notifyEnable/notifyDisabled are called by the {@QuestionCheckBox}
     *  if an Option with disable_question_numbers is checked or unchecked. If this
     *  Option is checked, disableQuestions() must be called, otherwise enableQuestions()
     *  is called.
     */
    public void  notifyEnableQuestionNumbers(ArrayList<String> disabledQuestionNumbers) {
        disableQuestionNumbersListener.enableQuestions(disabledQuestionNumbers);
    }

    public void notifyDisableQuestionNumbers(ArrayList<String> disabledQuestionNumbers) {
        disableQuestionNumbersListener.disableQuestions(disabledQuestionNumbers);
    }

    public void setDisableQuestionNumbersListener(DisableQuestionNumbersListener listener) {
        this.disableQuestionNumbersListener = listener;
    }

}

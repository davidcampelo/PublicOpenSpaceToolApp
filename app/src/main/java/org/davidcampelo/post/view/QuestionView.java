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
public class QuestionView extends RelativeLayout {

    Question question;
    Context context;

    View rootView;

    TextView title;
    LinearLayout container;

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

    private void init(final Context context, Question question) {
        this.question = question;
        this.context = context;

        rootView = inflate(context, R.layout.question_view, this);

        title = (TextView) rootView.findViewById(R.id.QuestionView_title);
        title.setText(question.getTitle());

        container = (LinearLayout)findViewById(R.id.QuestionView_containerOptions);

        for (Option option : question.getAllOptions()){
            if (question.getType() == Question.QuestionType.SINGLE_CHOICE){
                QuestionCheckBox checkbox = new QuestionCheckBox(context, option, this);
                checkbox.setText(option.getText());
                checkbox.setChecked(option.isChecked());
                container.addView(checkbox);
            }
            else {
                if (option.getText().indexOf("**%OTHER%**") < 0) {
                    QuestionCheckBox checkbox = new QuestionCheckBox(context, option, this);
                    checkbox.setText(option.getText());
                    checkbox.setChecked(option.isChecked());
                    container.addView(checkbox);
                } else {
                    addOtherRow(container);
                }
            }
        }
    }

    private void addOtherRow(final LinearLayout container) {
        // create "Other" view
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View otherView = layoutInflater.inflate(R.layout.question_view_other_row, null);

        ImageView delete = (ImageView) otherView.findViewById(R.id.otherDeleteButton);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                View otherView = container.findViewById(R.id.otherRow);
                if (otherView != null)
                    container.removeView(otherView);
            }
        });

        ImageView add = (ImageView) otherView.findViewById(R.id.otherAddButton);
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View otherView = container.findViewById(R.id.otherRow);
                final View addView = layoutInflater.inflate(R.layout.question_view_added_row, null);
                final TextView textOut = (TextView)addView.findViewById(R.id.textout);
                ImageView buttonRemove = (ImageView)addView.findViewById(R.id.removeButton);
                buttonRemove.setOnClickListener(new OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        ((LinearLayout)addView.getParent()).removeView(addView);
                        question.removeOtherOptionByText(textOut.getText()+ "");
                    }
                });

                // create a new line with the written item
                if (otherView != null) {
                    String newOptionText = ((EditText)container.findViewById(R.id.otherInputText)).getText() + "";
                    question.addOption(new Option(newOptionText, true));
                    textOut.setText(newOptionText);
                    container.removeView(otherView);
                }

                // add new line and add new Other row :)
                container.addView(addView);
                addOtherRow(container);
            }
        });

        // add "Other" view
        container.addView(otherView);
        otherView.setId(R.id.otherRow);
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
}

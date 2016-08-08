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

/**
 *
 * QuestionView subclass used to present Questions of QuestionType == MULTIPLE_CHOICE
 *
 * Amount of Option objects is flexible, according to the "Other" options usage
 *
 * Created by davidcampelo on 8/6/16.
 */
public class MultipleQuestionView extends QuestionView{

    public MultipleQuestionView(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void init(Context context, Question question) {
        super.init(context, question);

        LinearLayout container = getContainer();

        for (Option option : question.getAllOptions()){
            if (option.getText().toLowerCase().indexOf("other") < 0) {
                QuestionCheckBox checkbox = new QuestionCheckBox(context, option, this);
                checkbox.setText(option.getText());
                checkbox.setChecked(option.isChecked());
                container.addView(checkbox);
            } else {
                addOtherRow(question, context, container);
            }
        }

    }

    private void addOtherRow(final Question question, final Context context, final LinearLayout container) {

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
                final TextView textOut = (TextView)addView.findViewById(R.id.otherAddedText);
                ImageView buttonRemove = (ImageView)addView.findViewById(R.id.otherRemoveButton);
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
                    question.addOption(new Option(newOptionText, true, question));
                    textOut.setText(newOptionText);
                    container.removeView(otherView);
                }

                // add new line and add new Other row :)
                container.addView(addView);
                addOtherRow(question, context, container);
            }
        });

        // add "Other" view
        container.addView(otherView);
        otherView.setId(R.id.otherRow);
    }
}
package org.davidcampelo.post.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.utils.Constants;
import org.w3c.dom.Text;

/**
 * QuestionView subclass used to present Questions of QuestionType == VARIABLE_SINGLE_CHOICE)
 *
 * IMPORTANT NOTE: Thia QuestionView was specially created to handle Question 29,
 * where one or many areas can be present in a given P.O.S.
 *
 * Created by davidcampelo on 8/6/16.
 */
public class VariableSingleQuestionView extends QuestionView {
    LinearLayout container;
    int questionCounter;

    LinearLayout addLayout, removeLayout;
    ImageButton addButton, removeButton;

    public VariableSingleQuestionView(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void init(Context context, Question question) {
        super.init(context, question);
        container = getContainer();
        questionCounter = 0;
        addMoreAndRemove();
        //addArea();

    }

    private void addMoreAndRemove() {
        final Context context = getContext();

        removeLayout = new LinearLayout(context);
        removeLayout.setOrientation(LinearLayout.HORIZONTAL);
        removeLayout.setGravity(Gravity.CENTER|Gravity.LEFT);
        removeButton = new ImageButton(context);
        removeButton.setImageResource(android.R.drawable.ic_delete);
        removeButton.setBackground(null);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // always remove the area with the highest number (the latest)
                container.removeView(container.getChildAt(container.getChildCount()-3));
                questionCounter--;
                removeLayout.setVisibility(questionCounter > 0? View.VISIBLE : View.INVISIBLE);
            }
        });
        removeLayout.addView(removeButton);
        TextView removeText = new TextView(context);
        removeText.setText("Remove area");
        removeText.setGravity(Gravity.CENTER);
        removeLayout.setVisibility(View.INVISIBLE);
        removeLayout.addView(removeText);


        addLayout = new LinearLayout(context);
        addLayout.setOrientation(LinearLayout.HORIZONTAL);
        addLayout.setGravity(Gravity.CENTER|Gravity.LEFT);
        addButton = new ImageButton(context);
        addButton.setImageResource(android.R.drawable.ic_input_add);
        addButton.setBackground(null);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addArea();
            }
        });
        addLayout.addView(addButton);
        TextView addText = new TextView(context);
        addText.setText("Add area");
        addText.setGravity(Gravity.CENTER);
        addLayout.addView(addText);

        container.addView(removeLayout);
        container.addView(addLayout);

    }

    private void addArea() {
        final Context context = getContext();
        final Question question = getQuestion();
        questionCounter++;

        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView areaTitle = new TextView(context);
        areaTitle.setText("Area "+ String.valueOf(questionCounter));
        layout.addView(areaTitle);

        for (Option option : question.getAllOptions()) {
            QuestionCheckBox checkbox = new QuestionCheckBox(context, option, this);
            checkbox.setText(option.getText());
            layout.addView(checkbox);
        }

        // always add new area before the remove button
        container.addView(layout, container.getChildCount()-2);
        layout.setTag(questionCounter);
        removeLayout.setVisibility(questionCounter > 0? View.VISIBLE : View.INVISIBLE);


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

    @Override
    public void notifyChecked(QuestionCheckBox checked){

        LinearLayout parent = (LinearLayout)checked.getParent();
        for (int i = parent.getChildCount(); --i >= 0; ) {
            final View child = parent.getChildAt(i);
            if (child instanceof QuestionCheckBox) {
                QuestionCheckBox checkbox = ((QuestionCheckBox) child);
                if (checkbox != checked)
                    checkbox.setCheckedNoNotify(false);
            }
        }
    }
}

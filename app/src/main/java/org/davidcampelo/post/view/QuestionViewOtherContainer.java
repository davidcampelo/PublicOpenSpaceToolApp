package org.davidcampelo.post.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.utils.Constants;

import java.util.ArrayList;

/** Special type of {@QuestioCheckBox} used by the {@MultipleQuestionView}
 *  when the user can add new {@Option} to a {@Question}
 *
 * Created by davidcampelo on 8/3/16.
 */
public class QuestionViewOtherContainer extends LinearLayout implements OnCheckedChangeListener {
    private Context context;
    private Question question;
    private Option option;
    private QuestionView parent;
    private QuestionCheckBox checkbox;

    public QuestionViewOtherContainer(Context context, Question question, Option option, QuestionView parent) {
        super(context);
        this.context = context;
        this.question = question;
        this.option = option;
        this.parent = parent;

        setOrientation(LinearLayout.VERTICAL);

        checkbox = new QuestionCheckBox(context, option, parent);
        checkbox.setText(option.getTitle());
        checkbox.setOnCheckedChangeListener(this);
        addView(checkbox);
    }

    /**
     * create "Other option" (new option added by the user) view
     * <p/>
     * NOTE: This component owns a list of {@QuestionCheckBox} and a set of {@View} to show "Other"
     * Options. In case of having an "Other" Option, his new Option will be stored in the TAG of the
     * respective View :)
     */
    private void createAndAddOtherRow(final Question question, final Context context) {
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View otherRowView = layoutInflater.inflate(R.layout.question_view_other_row, null);

        // Button of storing this new "Other" Option
        ImageView imageButtonAdd = (ImageView) otherRowView.findViewById(R.id.otherAddButton);
        imageButtonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stores new Row with new Option
                View otherView = QuestionViewOtherContainer.this.findViewById(R.id.otherRow);

                // Now create the Option object and remove the old inputable Other Row
                if (otherView != null) {
                    // Create new Option object
                    Option newOption = new Option(
                            Constants.OPTION_ALIAS_ADDED_BY_USER,
                            "1",
                            ((EditText) findViewById(R.id.otherInputText)).getText() + "",
                            question,
                            new ArrayList<String>()
                    );
                    question.addCustomAddedOption(newOption);

                    // Create new view ith new custom option title
                    View addedOtherRowView = createNewCustomAddedOptionRow(newOption);

                    // Remove old "Other" editable row
                    QuestionViewOtherContainer.this.removeView(otherView);

                    // add new Option view to the Screen
                    QuestionViewOtherContainer.this.addView(addedOtherRowView);

                    // And finally create new "Other" editable row
                    createAndAddOtherRow(question, context);
                }

            }
        });

        // add new editable "Other" view
        QuestionViewOtherContainer.this.addView(otherRowView);

        // scroll down to see the new row
        scrollDownIfNeeded();

    }

    private View createNewCustomAddedOptionRow(Option option) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customAddedOptionRowView = layoutInflater.inflate(R.layout.question_view_added_row, null);
        ImageView imageButtonRemove = (ImageView) customAddedOptionRowView.findViewById(R.id.otherRemoveButton);
        imageButtonRemove.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((LinearLayout) customAddedOptionRowView.getParent()).removeView(customAddedOptionRowView);
                question.removeCustomAddedOption((Option) customAddedOptionRowView.getTag());
            }
        });
        final TextView textOut = (TextView) customAddedOptionRowView.findViewById(R.id.otherAddedText);
        textOut.setText(" - " + option.getTitle());
        // XXX if it's an "Other" Option, it will be stored in the TAG of the VIEW
        customAddedOptionRowView.setTag(option);

        return customAddedOptionRowView;
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
            // Show inputable row for new Other options
            createAndAddOtherRow(question, context);
        }
        else {
            // Remove all Other options from interface
            while (getChildCount() > 1) {
                removeView(getChildAt(getChildCount() - 1));
            }
            // Remove all Other options from the Model (Question object)
            question.removeAllCustomAddedByUser();
        }
    }

    /**
     * Walk through to find the scrollable parent and scroll down if needed
     */
    private void scrollDownIfNeeded() {
        View _parent = parent;
        while (!(_parent instanceof ScrollView)) {
            _parent = (View) _parent.getParent();
        }

        // we know that there's a scrollable parent which has a LinearLayout
        // containing all {@QuestionView} objects
        if (_parent != null) {
            ScrollView scrollParent = ((ScrollView) _parent);
            LinearLayout layoutQuestions = (LinearLayout) scrollParent.getChildAt(0);
            View view = (View) layoutQuestions.getChildAt(layoutQuestions.getChildCount() - 1);
            int diff = (view.getBottom() - (scrollParent.getHeight() + scrollParent.getScrollY()));
            if (diff == 0) {
                scrollParent.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }
    }

    public void setAnswers(){
        for (Option optionToAdd : question.getAllOptions()) {
            if (optionToAdd.wasAddedByUser()) {
                addView(createNewCustomAddedOptionRow(optionToAdd));
            }
        }

        checkbox.setChecked(question.hasCustomAddedOptions());
    }

    public String getAnswers() {
        StringBuilder answers = new StringBuilder();
        for (int i = getChildCount(); --i >= 0;) {
            View child = getChildAt(i);
            if (child.getTag() instanceof Option) { // it's a custom added option and is stored in the TAG
                answers.append(String.valueOf(((Option) child.getTag()).getId()) +
                        Constants.DEFAULT_SEPARATOR);
            }
        }
        return answers.toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).setEnabled(enabled);
        }

        if (!enabled) {
            checkbox.setChecked(false);

            // Remove all Other options from interface
            while (getChildCount() > 1) {
                removeView(getChildAt(getChildCount() - 1));
            }
            // Remove all Other options from the Model (Question object)
            question.removeAllCustomAddedByUser();
        }
    }
}

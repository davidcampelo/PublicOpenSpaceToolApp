package org.davidcampelo.post.view;

import android.content.Context;
import android.widget.CheckBox;

import org.davidcampelo.post.model.Option;

import java.util.ArrayList;

/**
 * Created by davidcampelo on 8/3/16.
 */
public class QuestionCheckBox extends CheckBox {

    ArrayList<String> disabledQuestionNumbers;
    Option option;
    QuestionView parent;

    public QuestionCheckBox(Context context, Option option, final QuestionView parent) {
        super(context);
        this.option = option;
        this.disabledQuestionNumbers = option.getDisabledQuestionNumbers();

        this.parent = parent;
    }

    public Option getOption() {
        return option;
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        if (option == null || parent == null) // do nothing
            return;

        if (checked)
            parent.notifyChecked(this);

        if (disabledQuestionNumbers.size() > 0){
            if (checked)
                parent.notifyDisableQuestionNumbers(disabledQuestionNumbers);
            else
                parent.notifyEnableQuestionNumbers(disabledQuestionNumbers);
        }

    }

    /**
     * Utility method used to avoid recursive loops of notifyChecked() notifications
     * of the Checkbox.
     *
     * XXX However, it notifies the enable/disable questions !!!!!!!!
     *
     */
    void setCheckedNoNotify(boolean checked) {
        super.setChecked(checked);

        if (disabledQuestionNumbers.size() > 0){
            if (checked)
                parent.notifyDisableQuestionNumbers(disabledQuestionNumbers);
            else
                parent.notifyEnableQuestionNumbers(disabledQuestionNumbers);
        }
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (!enabled) {
            setChecked(false);
        }
    }

}

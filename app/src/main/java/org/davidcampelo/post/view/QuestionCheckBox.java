package org.davidcampelo.post.view;

import android.content.Context;
import android.widget.CheckBox;

import org.davidcampelo.post.model.Option;

/**
 * Created by davidcampelo on 8/3/16.
 */
public class QuestionCheckBox extends CheckBox {

    Option option;
    QuestionView parent;

    public QuestionCheckBox(Context context, Option option, final QuestionView parent) {
        super(context);
        this.option = option;

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

    }

    void setCheckedNoNotify(boolean checked) {
        super.setChecked(checked);
    }
}

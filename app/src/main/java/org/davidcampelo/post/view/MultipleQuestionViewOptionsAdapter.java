package org.davidcampelo.post.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidcampelo on 8/2/16.
 *
 * {@deprecated}
 */
public class MultipleQuestionViewOptionsAdapter extends ArrayAdapter<Option> {

    List<Option> options = new ArrayList<Option>();

    public MultipleQuestionViewOptionsAdapter(Context context, int resource, List<Option> objects) {
        super(context, resource, objects);

        options = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.multiple_question_view_spinner_item, null);
        CheckedTextView textView = (CheckedTextView) v.findViewById(R.id.MultipleQuestionView_optionText);
        textView.setText(options.get(position).getText());

        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return  getView(position, convertView, parent);
    }


}

package org.davidcampelo.post.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidcampelo on 8/1/16.
 *
 * {@deprecated}
 */
public class SpinnerQuestionView extends RelativeLayout {

    Question question;
    Context context;

    private boolean isFirst = true;

    View rootView;

    TextView title;
    Spinner listOptions;
    LinearLayout container;

    ArrayAdapter<Option> adapter;


    public SpinnerQuestionView(Context context) {
        super(context);
        init(context, null);
    }

    public SpinnerQuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public SpinnerQuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, null);
    }

    public SpinnerQuestionView(Context context, Question question){
        super(context);
        init(context, question);
    }

    private void init(final Context context, Question question) {
        this.question = question;
        this.context = context;

        rootView = inflate(context, R.layout.question_view_spinner, this);

        title = (TextView) rootView.findViewById(R.id.MultipleQuestionView_title);
        title.setText(question.getTitle());

        container = (LinearLayout)findViewById(R.id.MultipleQuestionView_containerOptions);

        listOptions = (Spinner) findViewById(R.id.MultipleQuestionView_listOptions);
        adapter = new SpinnerQuestionViewOptionsAdapter(context, android.R.layout.simple_spinner_dropdown_item, question.getAllOptions());
        listOptions.setAdapter(adapter);

        // on select spinner item
        isFirst = true;
        listOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                   // avoid bug of first show (item was selected automatically - argh!)
                   if (isFirst){
                       isFirst = false;
                       return;
                   }
                   LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   Option selectedItem = (Option)parent.getItemAtPosition(position);
                   if ( selectedItem.isOtherOption() ) {

                       // create "Other" view
                       final View otherView = layoutInflater.inflate(R.layout.question_view_spinner_other_row, null);

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
                               final View addView = layoutInflater.inflate(R.layout.question_view_spinner_added_row, null);
                               TextView textOut = (TextView)addView.findViewById(R.id.textout);
                               ImageView buttonRemove = (ImageView)addView.findViewById(R.id.removeButton);
                               buttonRemove.setOnClickListener(new OnClickListener(){

                                   @Override
                                   public void onClick(View v) {
                                       ((LinearLayout)addView.getParent()).removeView(addView);
                                   }
                               });

                               // create a new line with the written item
                               if (otherView != null) {
                                   textOut.setText(((EditText)container.findViewById(R.id.otherInputText)).getText());
                                   container.removeView(otherView);
                               }

                               // add new line
                               container.addView(addView);
                           }
                       });

                       // add "Other" view
                       container.addView(otherView);
                       otherView.setId(R.id.otherRow);
                   }
                   else {

                       // create and add new line of option from spinner
                       final View addView = layoutInflater.inflate(R.layout.question_view_spinner_added_row, null);
                       TextView textOut = (TextView)addView.findViewById(R.id.textout);
                       ImageView buttonRemove = (ImageView)addView.findViewById(R.id.removeButton);
                       buttonRemove.setOnClickListener(new OnClickListener(){

                           @Override
                           public void onClick(View v) {
                               ((LinearLayout)addView.getParent()).removeView(addView);
                           }
                       });

                       // create a new line with the new selected or written item
                       textOut.setText(selectedItem.getTitle());

                       // add new line
                       container.addView(addView);
                   }
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {

               }
            });
    }

    class SpinnerQuestionViewOptionsAdapter extends ArrayAdapter<Option> {

        List<Option> options = new ArrayList<Option>();

        public SpinnerQuestionViewOptionsAdapter(Context context, int resource, List<Option> objects) {
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
            v = inflater.inflate(R.layout.question_view_spinner_item, null);
            CheckedTextView textView = (CheckedTextView) v.findViewById(R.id.MultipleQuestionView_optionText);
            textView.setText(options.get(position).getTitle());

            return v;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return  getView(position, convertView, parent);
        }


    }

}

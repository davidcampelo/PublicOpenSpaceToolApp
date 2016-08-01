package org.davidcampelo.post.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.Question;

/**
 * Created by davidcampelo on 8/1/16.
 */
public class MultipleQuestionView extends RelativeLayout {

    Question question;
    Context context;

    private boolean isFirst = true;

    View rootView;

    TextView title;
    Spinner listOptions;
    LinearLayout container;

    ArrayAdapter<Option> adapter;


    public MultipleQuestionView(Context context) {
        super(context);
        init(context, null);
    }

    public MultipleQuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public MultipleQuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, null);
    }

    public MultipleQuestionView(Context context, Question question){
        super(context);
        init(context, question);
    }

    private void init(final Context context, Question question) {
        this.question = question;
        this.context = context;

        rootView = inflate(context, R.layout.multiple_question_view, this);

        title = (TextView) rootView.findViewById(R.id.MultipleQuestionView_title);
        title.setText(question.getTitle());

        container = (LinearLayout)findViewById(R.id.MultipleQuestionView_containerOptions);

        listOptions = (Spinner) findViewById(R.id.MultipleQuestionView_listOptions);
        adapter = new MultipleQuestionViewOptionsAdapter(context, android.R.layout.simple_spinner_dropdown_item, question.getOptions());
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
                   if (selectedItem.getText().toLowerCase().indexOf("other") >= 0) {

                       // create "Other" view
                       final View otherView = layoutInflater.inflate(R.layout.multiple_question_view_other_row, null);

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
                               final View addView = layoutInflater.inflate(R.layout.multiple_question_view_added_row, null);
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
                       final View addView = layoutInflater.inflate(R.layout.multiple_question_view_added_row, null);
                       TextView textOut = (TextView)addView.findViewById(R.id.textout);
                       ImageView buttonRemove = (ImageView)addView.findViewById(R.id.removeButton);
                       buttonRemove.setOnClickListener(new OnClickListener(){

                           @Override
                           public void onClick(View v) {
                               ((LinearLayout)addView.getParent()).removeView(addView);
                           }
                       });

                       // create a new line with the new selected or written item
                       textOut.setText(selectedItem.getText());

                       // add new line
                       container.addView(addView);
                   }
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {

               }
            });
    }
}

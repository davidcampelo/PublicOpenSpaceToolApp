package org.davidcampelo.post;

import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;

import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;
import org.davidcampelo.post.utils.Constants;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    ResetDataListener resetDataListener;
    AlertDialog resetDialogObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddPublicOpenSpace);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(Constants.INTENT_ACTION_EXTRA, Constants.FragmentAction.ADD);
                startActivity(intent);
            }
        });

        buildResetDialog();
    }

    public void setResetDataListener(ResetDataListener resetDataListener) {
        this.resetDataListener = resetDataListener;
    }

    public void populate() {
        Question question = null;
        QuestionDAO questionDAO = new QuestionDAO(this);
        questionDAO.open();
        Option option = null;
        OptionDAO optionDAO = new OptionDAO(this);
        optionDAO.open();
        XmlResourceParser xpp = getResources().getXml(R.xml.questions);

        // check state
        int eventType = -1;
        try {

            eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("question")) {

                        String number = xpp.getAttributeValue(null, "number");
                        String title = xpp.getAttributeValue(null, "title");
                        String type = xpp.getAttributeValue(null, "type");

                        question = questionDAO.insert(new Question(number, title, Question.QuestionType.valueOf(type), null));

                        Log.e(this.getClass().getName(), "===================> Question: = "+ question.getNumber() +question.getType().name().substring(0,1)+" - "+ question.getTitle());

                    }
                } else if(eventType == XmlPullParser.TEXT) {
                    option = optionDAO.insert(new Option(xpp.getText(), question));
                    Log.e(this.getClass().getName(), "=======================> Option: = "+ option.getText() );


                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        xpp.close();
        questionDAO.close();
        optionDAO.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_reset) {
           resetDialogObject.show();
        }
        else if (id == R.id.action_export) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildResetDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_reset_dialog_title);
        builder.setMessage(R.string.action_reset_dialog_question);
        builder.setPositiveButton(R.string.action_reset_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PublicOpenSpaceDAO publicOpenSpaceDAO = new PublicOpenSpaceDAO(MainActivity.this);
                publicOpenSpaceDAO.open();
                publicOpenSpaceDAO.resetData();
                publicOpenSpaceDAO.close();
                QuestionDAO questionDAO = new QuestionDAO(MainActivity.this);
                questionDAO.open();
                questionDAO.resetData();
                questionDAO.close();
                OptionDAO optionDAO = new OptionDAO(MainActivity.this);
                optionDAO.open();
                optionDAO.resetData();
                optionDAO.close();
                AnswersDAO answersDAO = new AnswersDAO(MainActivity.this);
                answersDAO.open();
                answersDAO.resetData();
                answersDAO.close();

                MainActivity.this.populate();

                if (resetDataListener != null){
                    resetDataListener.notifyReset();
                }

            }
        });
        builder.setNegativeButton(R.string.action_reset_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        resetDialogObject = builder.create();
    }
}

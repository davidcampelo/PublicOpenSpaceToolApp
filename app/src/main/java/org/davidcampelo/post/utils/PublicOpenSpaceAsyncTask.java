package org.davidcampelo.post.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by davidcampelo on 10/18/16.
 */
public class PublicOpenSpaceAsyncTask extends AsyncTask<String, String, String> {

    ProgressDialog progressDialog;

    Context context;
    PublicOpenSpace publicOpenSpace;
    HashMap<Question, String> questionToAnswersMap;

    public PublicOpenSpaceAsyncTask(Context context, PublicOpenSpace publicOpenSpace,
                                    HashMap<Question, String> questionToAnswersMap) {
        super();
        this.context = context;
        this.publicOpenSpace = publicOpenSpace;
        this.questionToAnswersMap = questionToAnswersMap;
    }

    @Override
    protected String doInBackground(String... params) {
        // NOW SAVE OBJECT
        if (publicOpenSpace.getId() == 0) // if (id == 0) we're gonna insert it
            publicOpenSpace = PublicOpenSpaceDAO.staticInsert(context, publicOpenSpace);
        else // just update it
            PublicOpenSpaceDAO.staticUpdate(context, publicOpenSpace);

        // SAVE QUESTIONS ANSWERS
        AnswersDAO answersDAO = new AnswersDAO(context);
        answersDAO.open();
        OptionDAO optionDAO = new OptionDAO(context);
        optionDAO.open();
        // firstly, delete all previous answers
        answersDAO.delete(publicOpenSpace);

        for (Question question : questionToAnswersMap.keySet()) { // for each question, an answers it inserted
            if (question.getType() == Question.QuestionType.MULTIPLE_CHOICE) { // save "Other" Options first
                ArrayList<Option> options = question.getAllOptions();

                // Saving the "Other" Option
                for (Option option : options) {
                    if (option.getId() == 0) {
                        option.setPublicOpenSpace(publicOpenSpace);
                        optionDAO.insert(option);
                    }
                }
            }
            answersDAO.insert(publicOpenSpace, question, questionToAnswersMap.get(question));
        }

        optionDAO.close();
        answersDAO.close();

        progressDialog.dismiss();
        progressDialog = null;

        return "Item saved successfully!";
    }

    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Saving...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();

    }


    @Override
    protected void onProgressUpdate(String... text) {
        progressDialog.setMessage(text[0]);
    }
}
package org.davidcampelo.post.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by davidcampelo on 9/18/16.
 */

public class ProjectExportAsyncTask extends AsyncTask<String, String, String> {

    ProgressDialog progressDialog;

    Context context;
    Project project;
    boolean csv;
    boolean kmz;
    boolean spss;

    public ProjectExportAsyncTask(Context context, Project project, boolean csv, boolean kmz, boolean spss) {
        super();
        this.context = context;
        this.project = project;
        this.csv = csv;
        this.kmz = kmz;
        this.spss = spss;
    }

    @Override
    protected String doInBackground(String... params) {


        ////////////////////////////////////////////////////////////////////////////////////////////
        publishProgress("Creating temporary files...");

        File file = null;
        FileWriter out = null;
        try {
            file = File.createTempFile("POST_DataExport", ".csv", context.getExternalCacheDir());
            out = (FileWriter) new FileWriter(file);

        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return "ERROR: Could not create file!";

        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        publishProgress("Retrieving Questions...");
        QuestionDAO questionDAO = new QuestionDAO(context);
        questionDAO.open();
        ArrayList<Question> listQuestions = questionDAO.getAll();
        questionDAO.close();

        // write header
        StringBuilder stringBuilderHeader = new StringBuilder();
        Iterator<Question> questionIterator = listQuestions.iterator();
        while (questionIterator.hasNext()) {
            stringBuilderHeader.append(questionIterator.next().getNumber() + ",");
        }

        try {
            out.write(toStringWithNoEndingCommas(stringBuilderHeader) + "\n");
            Log.e("ANSWERS ", toStringWithNoEndingCommas(stringBuilderHeader));

        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return "ERROR: Could not write header to file!";
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        publishProgress("Retrieving answers...");

        if (project == null) {
            return "ERROR: No project was selected!";
        }
        // get all POS in the project
        PublicOpenSpaceDAO publicOpenSpaceDAO = new PublicOpenSpaceDAO(context);
        publicOpenSpaceDAO.open();
        ArrayList<PublicOpenSpace> listPublicOpenSpaces = publicOpenSpaceDAO.getAllByProject(project);
        publicOpenSpaceDAO.close();

        AnswersDAO answersDAO = new AnswersDAO(context);
        answersDAO.open();

        // get answers for every POS
        Iterator<PublicOpenSpace> publicOpenSpaceIterator = listPublicOpenSpaces.iterator();
        while (publicOpenSpaceIterator.hasNext()) {
            ArrayList<String> list  Answers = answersDAO.getAll(publicOpenSpaceIterator.next());
            Iterator<String> answersIterator = listAnswers.iterator();

            StringBuilder stringBuilderAnswers = new StringBuilder();
            while (answersIterator.hasNext()) {
                stringBuilderAnswers.append(answersIterator.next().replaceAll(",", "\\,") + ",");
            }

            // write answers to file
            try {
                out.write(toStringWithNoEndingCommas(stringBuilderAnswers) + "\n");
                Log.e("ANSWERS ", toStringWithNoEndingCommas(stringBuilderAnswers));
            } catch (IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                return "ERROR: Could not write to file!";
            }
        }
        answersDAO.close();

        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "[Public Open Space Tool] Project \'" +project.getName()+ "\' data export");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        sendIntent.setType("message/rfc822");

        context.startActivity(sendIntent);

        return "Files generated successfully!";
    }

    private String toStringWithNoEndingCommas(StringBuilder stringBuilder) {
        String str = stringBuilder.toString();
        if (str.length() > 0 && str.endsWith(",")){
            str = str.substring(0, str.length()-1);
        }

        return str;
    }

    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Export data");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();

    }


    @Override
    protected void onProgressUpdate(String... text) {
        progressDialog.setMessage(text[0]);
    }
}

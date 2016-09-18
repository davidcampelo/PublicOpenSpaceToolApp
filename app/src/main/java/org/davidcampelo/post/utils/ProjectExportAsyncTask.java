package org.davidcampelo.post.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
        publishProgress("Retrieving Public Open Spaces information...");
        try{ Thread.sleep(100); } catch (InterruptedException e) {}

        // get list of Public Open Spaces
        PublicOpenSpaceDAO publicOpenSpaceDAO = new PublicOpenSpaceDAO(context);
        publicOpenSpaceDAO.open();
        ArrayList<PublicOpenSpace> listPublicOpenSpaces = publicOpenSpaceDAO.getAllByProject(project);
        publicOpenSpaceDAO.close();

        ////////////////////////////////////////////////////////////////////////////////////////////
        publishProgress("Creating files...");
        try{ Thread.sleep(100); } catch (InterruptedException e) {}


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
        publishProgress("Writing to files...");
        String columnString =   "\"PersonName\",\"Gender\",\"Street1\",\"postOffice\",\"Age\"\n" ;
        // get list of Public Open Spaces
        QuestionDAO questionDAO = new QuestionDAO(context);
        questionDAO.open();
        ArrayList<Question> listQuestions = questionDAO.getAll();
        questionDAO.close();
        // write header
        try {
            out.write(columnString);
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return "ERROR: Could not write to file!";
        }

        int i = 0;
        while (i++ <= 10) {
            try{ Thread.sleep(100); } catch (InterruptedException e) {}

            // write POS
            try {
                out.write(columnString);
            } catch (IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                return "ERROR: Could not write to file!";
            }
        }

        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "[Public Open Space Tool] Data export");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        sendIntent.setType("message/rfc822");

        Intent chooser = Intent.createChooser(
                sendIntent, "Choose e-mail");

        context.startActivity(chooser);

        return "Files generated successfully!";
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

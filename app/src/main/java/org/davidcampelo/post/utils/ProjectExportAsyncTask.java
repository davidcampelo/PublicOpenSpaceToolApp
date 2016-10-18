package org.davidcampelo.post.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.davidcampelo.post.model.Project;

import java.io.File;
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
        if (project == null) {
            return "ERROR: No project was selected!";
        }
        if (! csv && !kmz && !spss) {
            return "ERROR: No export option was chosen!";
        }

        try {
            //File dbFile = exportDB();

            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "[Public Open Space Tool] Project \'" +project.getName()+ "\' data export");
            ArrayList<Uri> listFilesToExport = new ArrayList<Uri>();

            if (csv) {
                publishProgress("Creating CSV file...");
                File csvFile = CSVUtils.export( context, project);
                listFilesToExport.add(Uri.fromFile(csvFile));
            }
            if (kmz) {
                publishProgress("Creating KMZ file...");
                File kmzFile = KMZUtils.export( context, project);
                listFilesToExport.add(Uri.fromFile(kmzFile));
            }
            sendIntent.setType("message/rfc822");
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listFilesToExport);

            progressDialog.dismiss();

            context.startActivity(sendIntent);

            return "Files generated successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return e.getLocalizedMessage();
        }
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

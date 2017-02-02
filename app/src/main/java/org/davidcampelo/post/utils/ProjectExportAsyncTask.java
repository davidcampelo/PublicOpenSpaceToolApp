package org.davidcampelo.post.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by davidcampelo on 9/18/16.
 */

public class ProjectExportAsyncTask extends AsyncTask<String, String, String> {

    ProgressDialog progressDialog;
    File externalStorage = Environment.getExternalStorageDirectory();

    Context context;
    Project project;
    boolean csv;
    boolean kmz;
    boolean sql;

    public ProjectExportAsyncTask(Context context, Project project, boolean csv, boolean kmz, boolean sql) {
        super();
        this.context = context;
        this.project = project;
        this.csv = csv;
        this.kmz = kmz;
        this.sql = sql;
    }


    /**
     *  Copy a File from assets directory to the context.getExternalCacheDir() folder
      */
    private void copyAssets(String myFileName) throws Exception {
        File file = null;
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            throw new Exception("ERROR: Codebook could NOT be opened from assets directory!");
        }
        for(String filename : files) {
            InputStream in = null;
            if (filename.equals(myFileName)) {
                file = new File(context.getExternalCacheDir() + "/"+ myFileName);
                if (!file.exists()) {
                    FileOutputStream out = null;
                    try {
                        out = (FileOutputStream) new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        throw new Exception("ERROR: Codebook could NOT be created!");
                    }
                    try {
                        in = assetManager.open(filename);
                        copyFile(in, out);
                    } catch (Exception e) {
                        throw new Exception("ERROR: Codebook could NOT be copied!");
                    }
                    finally {
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            }
        }
    }
    private void copyFile(InputStream in, FileOutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        if (project == null) {
            return "ERROR: No project was selected!";
        }
        if (! csv && !kmz && !sql) {
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
                copyAssets("POST_codebook.pdf");
                listFilesToExport.add(Uri.fromFile(new File(context.getExternalCacheDir() + "/POST_codebook.pdf")));
            }
             if (sql) {
                publishProgress("Creating SQL file...");
                File sqlFile = SQLUtils.export( context, project);
                listFilesToExport.add(Uri.fromFile(sqlFile));
            }
            if (kmz) {
                publishProgress("Creating KMZ file...");
                File kmzFile = KMZUtils.export( context, project);
                listFilesToExport.add(Uri.fromFile(kmzFile));
            }
            publishProgress("Creating package...");
            sendIntent.setType("message/rfc822");
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listFilesToExport);

            progressDialog.dismiss();

            context.startActivity(sendIntent);

            return "Files generated successfully!";
        } catch (Exception e) {
            Log.e("ERROR", "", e);
            progressDialog.dismiss();
            return "ERROR: "+ e.getMessage();
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

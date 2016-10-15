package org.davidcampelo.post.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

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

        try {
            File csvFile = exportCSV();
            //File dbFile = exportDB();

            progressDialog.dismiss();

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "[Public Open Space Tool] Project \'" +project.getName()+ "\' data export");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(csvFile));
            //sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(dbFile));
            sendIntent.setType("message/rfc822");

            context.startActivity(sendIntent);

            return "Files generated successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return e.getMessage();
        }
    }

    private File exportDB() {
        File fromFile = context.getDatabasePath(Constants.DATABASE_NAME);

        return fromFile;
    }

    private File exportCSV() throws Exception {

        ////////////////////////////////////////////////////////////////////////////////////////////
        publishProgress("Creating temporary CSV file...");

        File file = null;
        FileWriter out = null;
        try {
            file = File.createTempFile("POST_DataExport", ".csv", context.getExternalCacheDir());
            out = (FileWriter) new FileWriter(file);

        } catch (IOException e) {
            throw new Exception("ERROR: Could not create file!", e);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        /// CSV HEADER /////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        publishProgress("Retrieving Questions and Options...");
        QuestionDAO questionDAO = new QuestionDAO(context);
        questionDAO.open();
        ArrayList<Question> listQuestions = questionDAO.getAllWithOptions();
        questionDAO.close();

        // build header
        StringBuilder stringBuilderHeader = new StringBuilder();
        for (Question question : listQuestions) {
            // if it's a Question with multiple options, every Option must be a column in the CSV
            Question.QuestionType questionType = question.getType();
            if (questionType == Question.QuestionType.MULTIPLE_CHOICE) {
                Iterator<Option> optionIterator = question.getAllOptions().iterator();
                while (optionIterator.hasNext()) {
                    Option option = optionIterator.next();
                    stringBuilderHeader.append("\"" +option.getAlias()+ "\",");
                    if (option.isOtherOption()) {
                        stringBuilderHeader.append("\"" +option.getAlias() + Constants.MULTIPLE_QUESTION_OTHER_CSV_SUFFIX + "\",");
                    }
                }
            }
            else if (questionType == Question.QuestionType.INPUT_COORDINATES){
                String questionAlias = question.getAlias();
                int pos = questionAlias.indexOf(Constants.POLYGON_POINTS_SEPARATOR);
                stringBuilderHeader.append("\"" +questionAlias.substring(0,pos) + "\", ");
                stringBuilderHeader.append("\"" +questionAlias.substring(pos+1) + "\", ");
            }
            else {
                stringBuilderHeader.append("\"" +question.getAlias() + "\",");
            }
        }
        // write header
        try {
            writeToFile(out, toStringWithNoEndingCommas(stringBuilderHeader));
        } catch (IOException e) {
            throw new Exception("ERROR: Could not write headers to file!", e);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // ANSWERS /////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        publishProgress("Retrieving answers...");

        // get all POS in the project
        PublicOpenSpaceDAO publicOpenSpaceDAO = new PublicOpenSpaceDAO(context);
        publicOpenSpaceDAO.open();
        ArrayList<PublicOpenSpace> listPublicOpenSpaces = publicOpenSpaceDAO.getAllByProject(project);
        publicOpenSpaceDAO.close();

        AnswersDAO answersDAO = new AnswersDAO(context);
        answersDAO.open();

        // get answers for every POS
        for (PublicOpenSpace publicOpenSpace : listPublicOpenSpaces) {
            HashMap<String, String> mapAnswers = answersDAO.getAll(publicOpenSpace);

            // look through all questions and insert answers
            StringBuilder stringBuilderAnswers = new StringBuilder();
            for (Question question : listQuestions) {
                String questionAnswers = mapAnswers.get(question.getNumber());
                if (questionAnswers == null) {
                    questionAnswers = "";
                }
                // if it's a Question with multiple options, every Option must be a column in the CSV
                Question.QuestionType questionType = question.getType();
                if (questionType == Question.QuestionType.MULTIPLE_CHOICE) {
                    ArrayList<Long> selectedIds = splitIntoOptionIds(questionAnswers);
                    for (Option option : question.getAllOptions()) {
                        // if it's an "OTHER option" (an Option created by the user), we must put
                        // them all together in the same column
                        if (option.isOtherOption()) {
                            if (selectedIds.size() == 0) {
                                stringBuilderAnswers.append("\"0\",");
                                stringBuilderAnswers.append("\"\",");
                            }
                            else {
                                stringBuilderAnswers.append("\"1\",");
                                OptionDAO optionDAO = new OptionDAO(context);
                                optionDAO.open();
                                stringBuilderAnswers.append("\"");
                                for (Long optionId : selectedIds) {
                                    // insert OTHER option into the StringBuffer
                                    Option otherOption = optionDAO.get(optionId);
                                    stringBuilderAnswers.append(otherOption.getTitle() + Constants.DEFAULT_SEPARATOR);
                                }
                                // XXX Remove last comma :D
                                stringBuilderAnswers.deleteCharAt(stringBuilderAnswers.length() - 1);
                                stringBuilderAnswers.append("\", ");
                                optionDAO.close();
                            }
                        }
                        else if ( selectedIds.contains(option.getId()) ) {
                            stringBuilderAnswers.append("\"1\",");
                            selectedIds.remove(option.getId());
                        }
                        else{
                            stringBuilderAnswers.append("\"0\",");
                        }
                    }
                }
                else if (question.getType() == Question.QuestionType.INPUT_COORDINATES) {
                    int pos = questionAnswers.indexOf(Constants.POLYGON_POINTS_SEPARATOR);
                    stringBuilderAnswers.append("\"" +questionAnswers.substring(0,pos) + "\", ");
                    stringBuilderAnswers.append("\"" +questionAnswers.substring(pos+1) + "\", ");
                }
                else {
                    stringBuilderAnswers.append("\""+ questionAnswers + "\", ");
                }

            }

            // write answers to file
            try {
                writeToFile(out, toStringWithNoEndingCommas(stringBuilderAnswers));
            } catch (IOException e) {
                throw new Exception("ERROR: Could not write to file!", e);
            }

        }
        answersDAO.close();

        ////////////////////////////////////////////////////////////////////////////////////////////

        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private void writeToFile(FileWriter out, String string) throws IOException {
        out.write(string + "\n");
        Log.e("[EXPORT]", string);
    }

    /**
     * Takes a String with MULTIPLE_CHOICE Question's Opition ID answers
     * and return an array of Option IDs
     */
    private ArrayList<Long> splitIntoOptionIds(String questionAnswers) {
        StringTokenizer tokenizer = new StringTokenizer(questionAnswers, Constants.DEFAULT_SEPARATOR);
        ArrayList<Long> selectedIds = new ArrayList<>();

        while ( tokenizer.hasMoreElements() ) {
            String selectedId = (String) tokenizer.nextElement();
            if (selectedId == null || selectedId.length() == 0)
                continue;
            selectedIds.add(Long.valueOf( selectedId ));
        }

        return selectedIds;
    }

    /**
     * Remove the last character if it's a comma
     */
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

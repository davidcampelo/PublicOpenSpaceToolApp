package org.davidcampelo.post.utils;

import android.content.Context;
import android.util.Log;

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
import java.util.StringTokenizer;

/**
 * Created by davidcampelo on 10/16/16.
 */
public class CSVUtils {

    public static File export(final Context context, final Project project) throws Exception {

        ////////////////////////////////////////////////////////////////////////////////////////////
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
        int MAX_NUMBER_OF_VARIABLE_SINGLE_CHOICE_QUESTION = 0;

        QuestionDAO questionDAO = new QuestionDAO(context);
        questionDAO.open();
        ArrayList<Question> listQuestions = questionDAO.getAllWithOptions();
        questionDAO.close();

        AnswersDAO answersDAO = new AnswersDAO(context);
        answersDAO.open();

        // build header
        StringBuilder stringBuilderHeader = new StringBuilder();
        for (Question question : listQuestions) {
            // if it's a Question with multiple options, every Option must be a column in the CSV
            Question.QuestionType questionType = question.getType();
            if (questionType == Question.QuestionType.MULTIPLE_CHOICE) {
                for (Option option : question.getAllOptions()) {
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
            else if (questionType == Question.QuestionType.VARIABLE_SINGLE_CHOICE){
                ////////////////////////////////////////////////////////////////////////////////////////////
                /// QUESTION 29 HANDLING ///////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////
                // AS the number of answers for Question 29 is variable, we must look through all P.O.S and
                // check the maximum of Answers we had for it
                // Based on this number the number of columns of the header will be known
                MAX_NUMBER_OF_VARIABLE_SINGLE_CHOICE_QUESTION = answersDAO.getMaxNumberOfAnswersByProject(project, question);
                String questionAlias = question.getAlias(); // playground
                for (int i = 1; i <= MAX_NUMBER_OF_VARIABLE_SINGLE_CHOICE_QUESTION; i++){
                    stringBuilderHeader.append("\"" +question.getAlias() + "_" +i+ "\",");
                }
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
        // get all POS in the project
        PublicOpenSpaceDAO publicOpenSpaceDAO = new PublicOpenSpaceDAO(context);
        publicOpenSpaceDAO.open();
        ArrayList<PublicOpenSpace> listPublicOpenSpaces = publicOpenSpaceDAO.getAllByProject(project);
        publicOpenSpaceDAO.close();

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
                else if (question.getType() == Question.QuestionType.INPUT_COORDINATES) { // Question 5
                    int pos = questionAnswers.indexOf(Constants.POLYGON_POINTS_SEPARATOR);
                    stringBuilderAnswers.append("\"" +questionAnswers.substring(0,pos) + "\", ");
                    stringBuilderAnswers.append("\"" +questionAnswers.substring(pos+1) + "\", ");
                }
                else if (questionType == Question.QuestionType.VARIABLE_SINGLE_CHOICE){   // Question 29
                    // get count of questions
                    int pos = questionAnswers.indexOf(Constants.DEFAULT_SEPARATOR);
                    int answersCount = Integer.valueOf( questionAnswers.substring(0, pos) );
                    questionAnswers = questionAnswers.substring(pos + 1);

                    StringTokenizer tokenizer = new StringTokenizer(questionAnswers, Constants.DEFAULT_SEPARATOR);
                    // write variable answers
                    while ( tokenizer.hasMoreElements() ) {
                        stringBuilderAnswers.append("\"" +(String)tokenizer.nextElement()+ "\", ");
                    }
                    // fill up the end with zero
                    for (int i = answersCount + 1; i <= MAX_NUMBER_OF_VARIABLE_SINGLE_CHOICE_QUESTION; i++){
                        stringBuilderAnswers.append("\"0\",");
                    }
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

    private static void writeToFile(FileWriter out, String string) throws IOException {
        out.write(string + "\n");
        Log.e("[EXPORT]", string);
    }

    /**
     * Takes a String with MULTIPLE_CHOICE Question's Opition ID answers
     * and return an array of Option IDs
     */
    private static ArrayList<Long> splitIntoOptionIds(String questionAnswers) {
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
    private static String toStringWithNoEndingCommas(StringBuilder stringBuilder) {
        String str = stringBuilder.toString();
        if (str.length() > 0 && str.endsWith(",")){
            str = str.substring(0, str.length()-1);
        }

        return str;
    }

}

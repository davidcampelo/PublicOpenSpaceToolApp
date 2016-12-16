package org.davidcampelo.post.utils;

import android.content.Context;
import android.util.Log;

import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.ProjectDAO;
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
public class SQLUtils {

    public static File export(final Context context, final Project project) throws Exception {

        ////////////////////////////////////////////////////////////////////////////////////////////
        File file = null;
        FileWriter out = null;
        try {
            file = File.createTempFile("POST_DataExport", ".sql", context.getExternalCacheDir());
            out = (FileWriter) new FileWriter(file);

        } catch (IOException e) {
            throw new Exception("ERROR: Could not create file!", e);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        /// DUMP TABLES /////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        ProjectDAO projectDAO = new ProjectDAO(context);
        projectDAO.dump(out);
        PublicOpenSpaceDAO publicOpenSpaceDAO = new PublicOpenSpaceDAO(context);
        publicOpenSpaceDAO.dump(out);
        QuestionDAO questionDAO = new QuestionDAO(context);
        questionDAO.dump(out);
        OptionDAO optionDAO = new OptionDAO(context);
        optionDAO.dump(out);
        AnswersDAO answersDAO = new AnswersDAO(context);
        answersDAO.dump(out);
        ////////////////////////////////////////////////////////////////////////////////////////////

        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}

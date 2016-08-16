package org.davidcampelo.post.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.davidcampelo.post.MainActivity;
import org.davidcampelo.post.R;
import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by davidcampelo on 8/16/16.
 */
public class Data {

    public static void populateDatabase(Context context, Resources resources) {
        Question question = null;
        QuestionDAO questionDAO = new QuestionDAO(context);
        questionDAO.open();
        Option option = null;
        OptionDAO optionDAO = new OptionDAO(context);
        optionDAO.open();
        XmlResourceParser xpp = resources.getXml(R.xml.questions);

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

                        Log.e("[POPULATE DATA]", "===================> Question: = "+ question.getNumber() +question.getType().name().substring(0,1)+" - "+ question.getTitle());

                    }
                } else if(eventType == XmlPullParser.TEXT) {
                    option = optionDAO.insert(new Option(xpp.getText(), question));
                    Log.e("[POPULATE DATA]", "=======================> Option: = "+ option.getText() );


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

    public static void clearDatabase(Context context) {
        PublicOpenSpaceDAO publicOpenSpaceDAO = new PublicOpenSpaceDAO(context);
        publicOpenSpaceDAO.open();
        publicOpenSpaceDAO.resetData();
        publicOpenSpaceDAO.close();
        QuestionDAO questionDAO = new QuestionDAO(context);
        questionDAO.open();
        questionDAO.resetData();
        questionDAO.close();
        OptionDAO optionDAO = new OptionDAO(context);
        optionDAO.open();
        optionDAO.resetData();
        optionDAO.close();
        AnswersDAO answersDAO = new AnswersDAO(context);
        answersDAO.open();
        answersDAO.resetData();
        answersDAO.close();
    }
}

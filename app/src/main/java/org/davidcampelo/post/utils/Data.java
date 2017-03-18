package org.davidcampelo.post.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.davidcampelo.post.R;
import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.GenericDAO;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.ProjectDAO;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Class for handling data importation
 *
 * Created by davidcampelo on 8/16/16.
 */
public class Data {


    private static final String SQL_FILENAME = "POST_DataExport_2017mar04.sql";
    private static final String XML_FILENAME = "POST_DataExport-1363984559.xml";

    static class XMLDOMParser {
        //Returns the entire XML document
        public Document getDocument(InputStream inputStream) {
            Document document = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder db = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(inputStream);
                document = db.parse(inputSource);
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
            return document;
        }

        /*
         * I take a XML element and the tag name, look for the tag and get
         * the text content i.e for <employee><name>Kumar</name></employee>
         * XML snippet if the Element points to employee node and tagName
         * is name I will return Kumar. Calls the private method
         * getTextNodeValue(node) which returns the text value, say in our
         * example Kumar. */
        public String getValue(Element item, String name) {
            NodeList nodes = item.getElementsByTagName(name);
            return this.getTextNodeValue(nodes.item(0));
        }

        private final String getTextNodeValue(Node node) {
            Node child;
            if (node != null) {
                if (node.hasChildNodes()) {
                    child = node.getFirstChild();
                    while(child != null) {
                        if (child.getNodeType() == Node.TEXT_NODE) {
                            return child.getNodeValue();
                        }
                        child = child.getNextSibling();
                    }
                }
            }
            return "";
        }
    }

    public static void populateDatabaseFromSQL(Context context) {
        BufferedReader br = null;
        GenericDAO genericDAO = null;
        String sCurrentLine;

        try {

            br = new BufferedReader(new InputStreamReader(context.getAssets().open(SQL_FILENAME)));

            genericDAO = new GenericDAO(context);
            genericDAO.open();
            while ((sCurrentLine = br.readLine()) != null && sCurrentLine.length() > 0) {
                //Log.e("[DATA]", "==> "+ sCurrentLine);
                genericDAO.exec(sCurrentLine);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (br != null)
                    br.close();
                genericDAO.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void populateDatabaseFromXML(Context context) {
        XMLDOMParser parser = new XMLDOMParser();
        AssetManager manager = context.getAssets();
        InputStream stream;
        try {
            stream = manager.open(XML_FILENAME);
            Document doc = parser.getDocument(stream);

            // Get elements by name employee
            Element nodeDocument = (Element) (doc.getElementsByTagName("Document")).item(0);

            ArrayList<LatLng> portoPoints = new ArrayList<>();
            portoPoints.add(new LatLng(41.161834,-8.6390192));
            portoPoints.add(new LatLng(41.161834,-8.5703117));
            portoPoints.add(new LatLng(41.140188,-8.5703117));
            portoPoints.add(new LatLng(41.140188,-8.6390192));

            Project project = new Project(
                    parser.getValue(nodeDocument, "name"),
                    parser.getValue(nodeDocument, "description"),
                    portoPoints
                    );
            project = ProjectDAO.staticInsert(context, project);
            /*
             * for each <Placemark> element
             */
            NodeList nodeList = nodeDocument.getElementsByTagName("Placemark");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element e = (Element) nodeList.item(i);
                ArrayList<LatLng> coordinates = new ArrayList<>();
                StringTokenizer tokenizer = new StringTokenizer(parser.getValue(e, "coordinates"), Constants.POLYGON_POINTS_SEPARATOR);
                while ( tokenizer.hasMoreElements() ) {

                    String coords = (String) tokenizer.nextElement();
                    int pos = coords.indexOf(Constants.POLYGON_COORDINATES_SEPARATOR);
                    coordinates.add( new LatLng(Double.valueOf(coords.substring(pos + 1, coords.length() - 2)), Double.valueOf(coords.substring(0, pos))) );
                }

                PublicOpenSpace.Type type;
                String name = parser.getValue(e, "name").toLowerCase();
                if (name.indexOf("parque") >= 0)
                    type = PublicOpenSpace.Type.PARK;
                else if (name.indexOf("jardim") >= 0 || name.indexOf("jardins") >= 0)
                    type = PublicOpenSpace.Type.GARDEN;
                else if (name.indexOf("praca") >= 0 || name.indexOf("praça") >= 0)
                    type = PublicOpenSpace.Type.SQUARE;
                else
                    type = PublicOpenSpace.Type.OTHER;


                PublicOpenSpace publicOpenSpace = new PublicOpenSpace(
                        parser.getValue(e, "name"),
                        type,
                        coordinates,
                        project
                        );

                PublicOpenSpaceDAO.staticInsert(context, publicOpenSpace);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public static void populateDatabaseFromXML(Context context, Resources resources) {
        Log.e("DATA", ">>>>>>>>>>>>>>>>>>>>>>>>>> Resetting  Database... ");
        Question question = null;
        QuestionDAO questionDAO = new QuestionDAO(context);
        questionDAO.open();
        Option option = null;
        OptionDAO optionDAO = new OptionDAO(context);
        optionDAO.open();
        XmlResourceParser xpp = resources.getXml(R.xml.questions_with_alias_and_hint_and_disabled_v4);

        // check state
        int eventType = -1;
        try {

            eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("question")) {

                        String number = xpp.getAttributeValue(null, "number");
                        String alias = xpp.getAttributeValue(null, "alias");
                        String hint = xpp.getAttributeValue(null, "hint");
                        String title = xpp.getAttributeValue(null, "title");
                        String type = xpp.getAttributeValue(null, "type");

                        question = questionDAO.insert(new Question(number, alias, hint, title, Question.QuestionType.valueOf(type), null));

//                        Log.e("[DATA]", "==> "+
//                                question.getNumber()+ " - " +
//                                question.getAlias()+" - "+
//                                question.getType().name().substring(0,1)+" - "+
//                                question.getTitle());

                    }
                    else if (xpp.getName().equals("option")) {

                        String value = xpp.getAttributeValue(null, "value");
                        String alias = xpp.getAttributeValue(null, "alias");
                        String title = xpp.getAttributeValue(null, "title");
                        String disable_question_numbers = xpp.getAttributeValue(null, "disable_question_numbers");

                        option = optionDAO.insert(
                                new Option(
                                        alias,
                                        value,
                                        title,
                                        question,
                                        StringUtils.toArrayList(disable_question_numbers, Constants.DEFAULT_SEPARATOR)
                                )
                        );

//                        Log.e("[DATA]", "====> "+
//                                option.getAlias()+" - "+
//                                option.getValue()+" - "+
//                                option.getTitle());

                    }
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
        Log.e("DATA", ">>>>>>>>>>>>>>>>>>>>>>>>>> Clearing Database... ");
        ProjectDAO projectDAO = new ProjectDAO(context);
        projectDAO.open();
        projectDAO.resetData();
        projectDAO.close();
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

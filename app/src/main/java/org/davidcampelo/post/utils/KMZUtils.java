package org.davidcampelo.post.utils;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by davidcampelo on 10/16/16.
 */
public class KMZUtils {

    private static final String CDATA_PLACEMARK_DESCRIPTION_HEADER =
        "<html xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xmlns:msxsl=\"urn:schemas-microsoft-"+
        "com:xslt\"><head><META http-equiv=\"Content-Type\" content=\"text/html\"><meta http-equiv"+
        "=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body style=\"margin:0px 0p"+
        "x 0px 0px;overflow:auto;background:#FFFFFF;\"><table style=\"font-family:Arial,Verdana,Ti"+
        "mes;font-size:12px;text-align:left;width:100%;border-collapse:collapse;padding:3px 3px 3p"+
        "x 3px\"><tr style=\"text-align:center;font-weight:bold;background:#9CBCE2\"><td>Jardins -"+
        "Parques - Praças</td></tr><tr><td><table style=\"font-family:Arial,Verdana,Times;font-siz"+
        "e:12px;text-align:left;width:100%;border-spacing:0px; padding:3px 3px 3px 3px\"><tr><td>F"+
        "ID</td><td>0</td></tr><tr bgcolor=\"#D4E4F3\"><td>CLASS</td><td>Jardins - Parques - Praça"+
        "s</td></tr><tr><td>nome</td><td>";

    private static final String CDATA_PLACEMARK_DESCRIPTION_FOOTER =
        "</td></tr><tr bgcolor=\"#D4E4F3\"><td>codigo</td><td>30</td></tr><tr><td>Shape_Leng</td><"+
        "td>446,074338</td></tr></table></td></tr></table></body></html>";

    public static File export(Context context, Project project) throws Exception {
        ////////////////////////////////////////////////////////////////////////////////////////////
        File file = null;
        FileWriter out = null;
        try {
            file = File.createTempFile("POST_DataExport", ".kmz", context.getExternalCacheDir());
            out = (FileWriter) new FileWriter(file);

        } catch (IOException e) {
            throw new Exception("ERROR: Could not create file!", e);
        }



        ////////////////////////////////////////////////////////////////////////////////////////////
        // PROJECT /////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        // kml
        Element root = doc.createElement("kml");
        root.setAttribute("xmlns","http://www.opengis.net/kml/2.2");
        root.setAttribute("xmlns:gx", "http://www.google.com/kml/ext/2.2");
        root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:schemaLocation", "http://www.opengis.net/kml/2.2 http://schemas.opengis.net/kml/2.2.0/ogckml22.xsd http://www.google.com/kml/ext/2.2 http://code.google.com/apis/kml/schema/kml22gx.xsd");
        doc.appendChild(root);

        // Document
        Element nodeDocument = doc.createElement("Document");
        nodeDocument.setAttribute("id", String.valueOf(project.getId()));
        nodeDocument.appendChild(createElementWithContent(doc, "name", project.getName()));
        nodeDocument.appendChild(createElementWithContent(doc, "Snippet", ""));

//         The all encapsulating kml element.
//        Kml kml = KmlFactory.createKml();
//        Document document = kml.createAndSetDocument()
//                .withId(String.valueOf(project.getId()))
//                .withName(project.getName())
//                .withSnippet(null);
//        Folder folder = document.createAndAddFolder()
//                .withId(String.valueOf(project.getId()))
//                .withName(project.getName())
//                .withDescription(project.getDesc());
//        Style style = document.createAndAddStyle()
//                .withId("PolyStyle00")
//                .withLabelStyle(new LabelStyle().withColor("00000000").withScale(0.000000))
//                .withPolyStyle(new PolyStyle().withColor("7f73b273").withOutline(false));

        ////////////////////////////////////////////////////////////////////////////////////////////
        // PUBLIC OPEN SPACES //////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        // get all POS in the project
        PublicOpenSpaceDAO publicOpenSpaceDAO = new PublicOpenSpaceDAO(context);
        publicOpenSpaceDAO.open();
        ArrayList<PublicOpenSpace> listPublicOpenSpaces = publicOpenSpaceDAO.getAllByProject(project);
        publicOpenSpaceDAO.close();
//
        // get answers for every POS
        if (listPublicOpenSpaces != null && listPublicOpenSpaces.size() > 0) {

            // Folder
            Element nodeFolder = doc.createElement("Folder");
            nodeFolder.setAttribute("id", "FeatureLayer0");
            nodeFolder.appendChild(createElementWithContent(doc, "name", project.getName()));
            nodeFolder.appendChild(createElementWithContent(doc, "Snippet", ""));
            nodeFolder.appendChild(createElementWithCDATA(doc, "description", project.getDesc()));

            for (PublicOpenSpace publicOpenSpace : listPublicOpenSpaces) {
                // Placemark
                Element nodePlacemark = doc.createElement("Placemark");
                nodePlacemark.setAttribute("id", String.valueOf(publicOpenSpace.getId()));
                nodePlacemark.appendChild(createElementWithContent(doc, "name", publicOpenSpace.getName()));
                nodePlacemark.appendChild(createElementWithContent(doc, "Snippet", ""));
                nodePlacemark.appendChild(createElementWithCDATA(doc, "description",
                        CDATA_PLACEMARK_DESCRIPTION_HEADER + publicOpenSpace.getName() + CDATA_PLACEMARK_DESCRIPTION_FOOTER));

                nodePlacemark.appendChild(createElementWithContent(doc, "StyleUrl", "#PolyStyle00"));
                Element nodeMultiGeometry = doc.createElement("MultiGeometry");
                Element nodePolygon = doc.createElement("Polygon");
                nodePolygon.appendChild(createElementWithContent(doc, "extrude", "0"));
                nodePolygon.appendChild(createElementWithContent(doc, "altitudeMode", "clampToGround"));
                Element nodeouterBoundaryIs = doc.createElement("outerBoundaryIs");
                Element nodeLinearRing = doc.createElement("LinearRing");
                StringBuilder coordinates = new StringBuilder();

                ArrayList<LatLng> polygonPoints = publicOpenSpace.getPolygonPoints();
                if (polygonPoints != null && polygonPoints.size() > 0) {
                    for (LatLng point : polygonPoints) {
                        coordinates.append(point.longitude+Constants.POLYGON_COORDINATES_SEPARATOR
                                +point.latitude+Constants.POLYGON_COORDINATES_SEPARATOR
                                +"0"+ Constants.POLYGON_POINTS_SEPARATOR);
                    }
                }
                nodeLinearRing.appendChild(createElementWithContent(doc, "coordinates", coordinates.toString().trim()));
                nodeouterBoundaryIs.appendChild(nodeLinearRing);
                nodePolygon.appendChild(nodeouterBoundaryIs);
                nodeMultiGeometry.appendChild(nodePolygon);
                nodePlacemark.appendChild(nodeMultiGeometry);
                nodeFolder.appendChild(nodePlacemark);
            }

            nodeDocument.appendChild(nodeFolder);
        }
        // KMZ Styles in the end
        Element nodeStyle = doc.createElement("Style");
        nodeStyle.setAttribute("id", "PolyStyle00");
        Element nodeLabelStyle = doc.createElement("LabelStyle");
        nodeLabelStyle.appendChild(createElementWithContent(doc, "color", "00000000"));
        nodeLabelStyle.appendChild(createElementWithContent(doc, "scale", "0.000000"));
        nodeStyle.appendChild(nodeLabelStyle);

        Element nodePolyStyle = doc.createElement("PolyStyle");
        nodePolyStyle.appendChild(createElementWithContent(doc, "color", "7f73b273"));
        nodePolyStyle.appendChild(createElementWithContent(doc, "outline", "0"));
        nodeStyle.appendChild(nodePolyStyle);
        //
        nodeDocument.appendChild(nodeStyle);
        //
        root.appendChild(nodeDocument);
//
//            Placemark placemark = folder.createAndAddPlacemark()
//                    .withId(String.valueOf(publicOpenSpace.getId()))
//                    .withName(publicOpenSpace.getName())
//                    .withDescription(publicOpenSpace.getName())
//                    .withSnippet(null)
//                    .withStyleUrl("#PolyStyle00");
//
//            MultiGeometry multiGeometry = new MultiGeometry();
//            LinearRing linearRing = multiGeometry.createAndAddPolygon()
//                    .withExtrude(false)
//                    .withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND)
//                    .createAndSetOuterBoundaryIs()
//                    .createAndSetLinearRing();
//
//            ArrayList<LatLng> polygonPoints = publicOpenSpace.getPolygonPoints();
//            if (polygonPoints != null && polygonPoints.size() > 0) {
//                for (LatLng point : polygonPoints) {
//                    linearRing.addToCoordinates(point.longitude, point.latitude);
//                }
//            }
//
//            placemark.setGeometry(multiGeometry);
//
//        // build up KMZ
//        kml.marshal(out);

        // create Transformer object
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
//        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(out);
        transformer.transform(new DOMSource(doc), result);

        ////////////////////////////////////////////////////////////////////////////////////////////

        return file;

    }

    private static Node createElementWithCDATA(Document doc, String tagName, String cdata) {
        Element node = doc.createElement(tagName);
        node.appendChild(doc.createCDATASection(cdata));

        return node;
    }

    private static Element createElementWithContent(Document doc, String tagName, String content) {
        Element node = doc.createElement(tagName);
        node.appendChild(doc.createTextNode(content));

        return node;
    }

}

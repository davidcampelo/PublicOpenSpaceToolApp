package org.davidcampelo.post;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;
import org.davidcampelo.post.utils.Constants;
import org.davidcampelo.post.utils.MapUtility;
import org.davidcampelo.post.view.InputDecimalQuestionView;
import org.davidcampelo.post.view.InputNumberQuestionView;
import org.davidcampelo.post.view.InputTextQuestionView;
import org.davidcampelo.post.view.MultipleQuestionView;
import org.davidcampelo.post.view.QuestionView;
import org.davidcampelo.post.view.SingleQuestionView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class PublicOpenSpaceAddEditFragment extends Fragment
        implements OnMapReadyCallback, OnMarkerClickListener, /*OnMapClickListener,*/ OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private PublicOpenSpace publicOpenSpace;

    View fragmentLayout;

    MapView mapView;
    GoogleMap googleMap;
    private TreeMap<String, LatLng> hashMapPoints = null;
    int markerIdCount;
    String markerIdClick;
    private AlertDialog markerDialog;


    private Button saveButton;
    private ImageButton posType;


    HashMap<String, QuestionView> questionNumberToViewMap;
    QuestionDAO questionDAO;

    private AlertDialog posTypeDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        Intent intent = getActivity().getIntent();
        long id = intent.getLongExtra(Constants.INTENT_ID_EXTRA, 0);

        // get references to components
        fragmentLayout = inflater.inflate(R.layout.fragment_public_open_space_add_edit, container, false);
        saveButton = (Button) fragmentLayout.findViewById(R.id.addEditSaveButton);
        posType = (ImageButton) fragmentLayout.findViewById(R.id.addEditItemType);
        posType.setBackground(null);

        // fill tabs
        fillTabTitles((TabHost) fragmentLayout.findViewById(R.id.addEditItemTabHost));

        publicOpenSpace = PublicOpenSpaceDAO.staticGet(getActivity(), id);
        if (publicOpenSpace == null) { // add screen
            publicOpenSpace = new PublicOpenSpace();
        }

        questionNumberToViewMap = new HashMap<>();
        loadQuestionsAndOptions();
        loadAnswers();
        loadGeneralInfo();

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) fragmentLayout.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveObject();

                Toast.makeText(PublicOpenSpaceAddEditFragment.this.getActivity(), "Item saved successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        posType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posTypeDialog.show();
            }
        });

        buildTypeDialog();

        return fragmentLayout;
    }

    private void buildTypeDialog() {
        final String[] categories = new String[]{"Park", "Square", "Garden", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please choose P.O.S. type");
        builder.setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                posTypeDialog.cancel();
                switch (item) {
                    case 0:
                        publicOpenSpace.setType(PublicOpenSpace.Type.PARK);
                        posType.setImageResource(R.drawable.icon_park);
                        break;
                    case 1:
                        publicOpenSpace.setType(PublicOpenSpace.Type.SQUARE);
                        posType.setImageResource(R.drawable.icon_square);
                        break;
                    case 2:
                        publicOpenSpace.setType(PublicOpenSpace.Type.GARDEN);
                        posType.setImageResource(R.drawable.icon_garden);
                        break;
                    default:
                        publicOpenSpace.setType(PublicOpenSpace.Type.OTHER);
                        posType.setImageResource(R.drawable.icon_other);
                        break;
                }
            }
        });

        posTypeDialog = builder.create();
    }

    private void loadGeneralInfo() {
        if (publicOpenSpace.getId() != 0) {
            // set up name
            ((TextView) fragmentLayout.findViewById(R.id.addEditItemName)).setText(publicOpenSpace.getName());
            ((ImageButton) fragmentLayout.findViewById(R.id.addEditItemType)).setImageResource(publicOpenSpace.getTypeResource());

            // Sets up latlng object to position on map according to question 5
            QuestionView questionView;
            questionView = this.questionNumberToViewMap.get("5");
            if (questionView != null) {
                String geocode = ((InputTextQuestionView) questionView).getAnswers();
//                Log.e(this.getClass().getName(), "-------------------- from question to map = "+ geocode);
                if (geocode.length() > 0) {
                    int pos = geocode.indexOf(",");
                    // TODO list of map points and draw on map
//                    hashMapPoints = new HashMap<String, LatLng>();
                }
            }
            // set up address edittext
            questionView = this.questionNumberToViewMap.get("2");
            if (questionView != null) {
                String address = ((InputTextQuestionView) questionView).getAnswers();
//                Log.e(this.getClass().getName(), "-------------------- from question to map address = "+ address);
                if (address.length() > 0) {
                    ((TextView) fragmentLayout.findViewById(R.id.addEditItemAddress)).setText(address);
                }
            }
        }
    }

    private void fillTabTitles(TabHost host) {
        host.setup();
        TabHost.TabSpec spec;

        int[] tabContents = new int[]{R.id.addEditItemTab0, R.id.addEditItemTab1, R.id.addEditItemTab2, R.id.addEditItemTab3, R.id.addEditItemTab4, R.id.addEditItemTab5};

        for (int i = -1; ++i < Constants.TAB_TITLES.length; ) {
            //Tabs
            spec = host.newTabSpec(Constants.TAB_TITLES[i]);
            spec.setContent(tabContents[i]);
            spec.setIndicator(Constants.TAB_TITLES[i]);
            host.addTab(spec);
            ((TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setAllCaps(false);
        }
    }

    /**
     * Fill UI components with our internal {@link PublicOpenSpace} object
     */
    private void loadQuestionsAndOptions() {

        Context context = getContext();
        questionDAO = new QuestionDAO(context);
        questionDAO.open();

        LinearLayout container1 = (LinearLayout) fragmentLayout.findViewById(R.id.addEditContainer1);
        container1.addView(addToMap(context, "1"));
        container1.addView(addToMap(context, "2"));
        QuestionView view3 = addToMap(context, "3");
        view3.setEnabled(false);
        container1.addView(view3);
        container1.addView(addToMap(context, "4"));
        container1.addView(addToMap(context, "5"));
        container1.addView(addToMap(context, "6"));

        // Tab 1 has a map only
        LinearLayout container2 = (LinearLayout) fragmentLayout.findViewById(R.id.addEditContainer2);
        container2.addView(addToMap(context, "7"));
        container2.addView(addToMap(context, "8"));

        LinearLayout container3 = (LinearLayout) fragmentLayout.findViewById(R.id.addEditContainer3);
        container3.addView(addToMap(context, "9"));
        container3.addView(addToMap(context, "10"));
        container3.addView(addToMap(context, "11"));
        container3.addView(addToMap(context, "12"));
        container3.addView(addToMap(context, "13.a"));
        container3.addView(addToMap(context, "13.b"));
        container3.addView(addToMap(context, "14"));
        container3.addView(addToMap(context, "15"));
        container3.addView(addToMap(context, "16"));
        container3.addView(addToMap(context, "17"));
        container3.addView(addToMap(context, "18.a"));
        container3.addView(addToMap(context, "18.b"));
        container3.addView(addToMap(context, "19"));
        container3.addView(addToMap(context, "20"));
        container3.addView(addToMap(context, "21"));
        container3.addView(addToMap(context, "22"));
        container3.addView(addToMap(context, "23"));
        container3.addView(addToMap(context, "24"));
        container3.addView(addToMap(context, "25"));

        LinearLayout container4 = (LinearLayout) fragmentLayout.findViewById(R.id.addEditContainer4);
        container4.addView(addToMap(context, "26"));
        container4.addView(addToMap(context, "27"));
        container4.addView(addToMap(context, "28"));
//        container4.addView(addToMap(context, "29"));
        container4.addView(addToMap(context, "30"));
        container4.addView(addToMap(context, "31"));
        container4.addView(addToMap(context, "32.a"));
        container4.addView(addToMap(context, "32.b"));
        container4.addView(addToMap(context, "33"));
        container4.addView(addToMap(context, "34"));
        container4.addView(addToMap(context, "35"));
        container4.addView(addToMap(context, "36"));
        container4.addView(addToMap(context, "37"));
        container4.addView(addToMap(context, "38"));
        container4.addView(addToMap(context, "39"));
        container4.addView(addToMap(context, "40"));
        container4.addView(addToMap(context, "41"));
        container4.addView(addToMap(context, "42"));

        LinearLayout container5 = (LinearLayout) fragmentLayout.findViewById(R.id.addEditContainer5);
        container5.addView(addToMap(context, "43"));
        container5.addView(addToMap(context, "44"));
        container5.addView(addToMap(context, "45"));
        container5.addView(addToMap(context, "46.a"));
        container5.addView(addToMap(context, "46.b"));
        container5.addView(addToMap(context, "47"));
        container5.addView(addToMap(context, "48.a"));
        container5.addView(addToMap(context, "48.b"));
//        container5.addView(addToMap(context, "49.a"));
//        container5.addView(addToMap(context, "49.b"));
//        container5.addView(addToMap(context, "49.c"));

        questionDAO.close();
    }

    private void loadAnswers() {
        if (publicOpenSpace.getId() != 0) {     // if not a new object (i.e. if it has some answers)
            Context context = getContext();
            AnswersDAO answersDAO = new AnswersDAO(context);
            answersDAO.open();

            Iterator it = questionNumberToViewMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                QuestionView questionView = (QuestionView) pair.getValue();
                questionView.setAnswers(answersDAO.get(questionView.getQuestion(), publicOpenSpace));
            }

            answersDAO.close();
        }
    }

    private final QuestionView addToMap(Context context, String questionNumber) {
        Question question = questionDAO.get(questionNumber, publicOpenSpace);
        QuestionView view;

        Question.QuestionType type = question.getType();
        if (type == Question.QuestionType.SINGLE_CHOICE)
            view = new SingleQuestionView(context, question);
        else if (type == Question.QuestionType.MULTIPLE_CHOICE)
            view = new MultipleQuestionView(context, question);
        else if (type == Question.QuestionType.INPUT_DECIMAL)
            view = new InputDecimalQuestionView(context, question);
        else if (type == Question.QuestionType.INPUT_TEXT)
            view = new InputTextQuestionView(context, question);
        else if (type == Question.QuestionType.INPUT_NUMBER)
            view = new InputNumberQuestionView(context, question);
        else
            view = new InputTextQuestionView(context, question);

        this.questionNumberToViewMap.put(questionNumber, view);

        return view;
    }

    /**
     * Fill our internal {@link PublicOpenSpace} object with UI components values
     * <p>
     * This method must be called before any persistence procedure :)
     */
    public void saveObject() {
        // NAME
        publicOpenSpace.setName(((TextView) fragmentLayout.findViewById(R.id.addEditItemName)).getText() + "");

        // POLYGON POINTS
        ArrayList<LatLng> points = new ArrayList<>();

        Iterator<Map.Entry<String, LatLng>> iterator = hashMapPoints.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, LatLng> entry = iterator.next();
            googleMap.addMarker(new MarkerOptions().title(entry.getKey()).position(entry.getValue()).draggable(true));
            points.add(entry.getValue());
        }
        publicOpenSpace.setPolygonPoints(points);

        // NOW SAVE OBJECT
        if (publicOpenSpace.getId() == 0) // if (id == 0) we're gonna insert it
            publicOpenSpace = PublicOpenSpaceDAO.staticInsert(getActivity(), publicOpenSpace);
        else // just update it
            PublicOpenSpaceDAO.staticUpdate(getActivity(), publicOpenSpace);

        // SAVE QUESTIONS ANSWERSS
        Context context = getContext();
        AnswersDAO answersDAO = new AnswersDAO(context);
        answersDAO.open();
        OptionDAO optionDAO = new OptionDAO(context);
        optionDAO.open();
        // firstly, delete all previous answers
        answersDAO.delete(publicOpenSpace);

        Iterator it = questionNumberToViewMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            QuestionView questionView = ((QuestionView) pair.getValue());
            Question question = questionView.getQuestion();
            if (question.getType() == Question.QuestionType.MULTIPLE_CHOICE) { // save "Other" Options first
                ArrayList<Option> options = question.getAllOptions();

                // Saving the "Other" Option
                for (Option option : options) {
                    if (option.getId() == 0) {
                        option.setPublicOpenSpace(publicOpenSpace);
                        optionDAO.insert(option);
                    }
                }
            }
            answersDAO.insert(publicOpenSpace, question, questionView.getAnswers());
        }

        optionDAO.close();
        answersDAO.close();
    }





    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////     MAP METHODS   ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void buildMarkerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.marker_click_dialog_question);
        builder.setPositiveButton(R.string.marker_click_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (markerIdClick != null && hashMapPoints.get(markerIdClick) != null) {
                    hashMapPoints.remove(markerIdClick);
                    markerIdClick = null;
                    drawPolygon();
                }
            }
        });
        builder.setNegativeButton(R.string.marker_click_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        markerDialog = builder.create();
    }

    private void addPoint(LatLng latLng){
        hashMapPoints.put(String.valueOf(++markerIdCount), latLng);
    }

    private void drawPolygon() {
        googleMap.clear();
        ArrayList<LatLng> points = new ArrayList<>();
        int size = hashMapPoints.size();

        Iterator<Map.Entry<String, LatLng>> iterator = hashMapPoints.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, LatLng> entry = iterator.next();
            googleMap.addMarker(new MarkerOptions().title(entry.getKey()).position(entry.getValue()).draggable(true));
            if (size >= 3)
                points.add(entry.getValue());
        }

        if (size >= 3) {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(points);
            polygonOptions.strokeColor(Color.argb(0, 0, 0, 100));
            polygonOptions.strokeWidth(7);
            polygonOptions.fillColor(Color.argb(100, 0, 0, 100));
            googleMap.addPolygon(polygonOptions);
        }
        // set question 3 (area) text
        QuestionView questionView = questionNumberToViewMap.get("3");
        String area = "";
        if (questionView != null) {
            if (size > 3) {
                area = MapUtility.calculateAreaAndFormat(points);
            }
            Log.e(this.getClass().getName(), "-------------------->>>> [AREA] from map to question = "+ area);
            ((InputDecimalQuestionView) questionView).setAnswers(area);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        hashMapPoints = new TreeMap<String, LatLng>();
        markerIdCount = 0;
        buildMarkerDialog();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (publicOpenSpace.getPolygonPoints() != null) {
            ArrayList<LatLng> points = publicOpenSpace.getPolygonPoints();
            for (LatLng point : points) {
                addPoint(point);
            }
            drawPolygon();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MapUtility.calculateCentroid(points), 15));
        }
        else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.PORTO_LATITUDE, Constants.PORTO_LONGITUDE), 13));
        }

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    /**
     * Add point to list of coordinates of map polygon
     */
//    @Override
//    public void onMapClick(LatLng latLng) {
//        //
//    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        markerIdClick = marker.getTitle();
        markerDialog.show();

        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        addPoint(latLng);

        drawPolygon();
    }


    @Override
    public void onMarkerDragStart(Marker marker) {
        //
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //
    }
    @Override
    public void onMarkerDragEnd(Marker marker) {
        hashMapPoints.remove(marker.getTitle());
        hashMapPoints.put(marker.getTitle(), marker.getPosition());

        drawPolygon();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
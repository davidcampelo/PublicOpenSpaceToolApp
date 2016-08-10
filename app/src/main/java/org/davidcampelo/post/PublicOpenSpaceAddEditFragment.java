package org.davidcampelo.post;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.Option;
import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;
import org.davidcampelo.post.utils.Constants;
import org.davidcampelo.post.view.InputDecimalQuestionView;
import org.davidcampelo.post.view.InputNumberQuestionView;
import org.davidcampelo.post.view.InputTextQuestionView;
import org.davidcampelo.post.view.MultipleQuestionView;
import org.davidcampelo.post.view.QuestionView;
import org.davidcampelo.post.view.SingleQuestionView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PublicOpenSpaceAddEditFragment extends Fragment implements OnMapReadyCallback {

    private PublicOpenSpace publicOpenSpace;

    View fragmentLayout;

    MapView mapView;
//    GoogleMap googleMap;
    LatLng latlngMap;

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
        fillTabTitles( (TabHost) fragmentLayout.findViewById(R.id.addEditItemTabHost) );

        publicOpenSpace = PublicOpenSpaceDAO.staticGet(getActivity(), id);
        if (publicOpenSpace == null){ // add screen
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

    private void buildTypeDialog(){
        final String[] categories = new String[]{"Park", "Square", "Garden", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please choose P.O.S. type");
        builder.setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                posTypeDialog.cancel();
                switch (item){
                    case 0:
                        publicOpenSpace.setType( PublicOpenSpace.Type.PARK );
                        posType.setImageResource(R.drawable.icon_park);
                        break;
                    case 1:
                        publicOpenSpace.setType( PublicOpenSpace.Type.SQUARE );
                        posType.setImageResource(R.drawable.icon_square);
                        break;
                    case 2:
                        publicOpenSpace.setType( PublicOpenSpace.Type.GARDEN );
                        posType.setImageResource(R.drawable.icon_garden);
                        break;
                    default:
                        publicOpenSpace.setType( PublicOpenSpace.Type.OTHER );
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

            // TODO: set up pos_type (icon)

            // Sets up latlng object to position on map according to question 5
            QuestionView questionView;
            questionView = this.questionNumberToViewMap.get("5");
            if (questionView != null) {
                String geocode = ((InputTextQuestionView)questionView).getAnswers();
//                Log.e(this.getClass().getName(), "-------------------- from question to map = "+ geocode);
                if (geocode.length() > 0 ) {
                    int pos = geocode.indexOf(",");
                    latlngMap = new LatLng(Double.valueOf(geocode.substring(0, pos)), Double.valueOf(geocode.substring(pos + 1)));
                }
            }
            // set up address edittext
            questionView = this.questionNumberToViewMap.get("2");
            if (questionView != null) {
                String address = ((InputTextQuestionView)questionView).getAnswers();
//                Log.e(this.getClass().getName(), "-------------------- from question to map address = "+ address);
                if (address.length() > 0 ) {
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


    public String resolveAddress(double latitude, double longitude) {
        if (latitude == Double.MAX_VALUE || longitude == Double.MAX_VALUE)
            return "";

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this.getActivity(), Locale.getDefault());

        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            StringBuilder stringBuilder = new StringBuilder();
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if (address != null)
                stringBuilder.append(address);
            String city = addresses.get(0).getLocality();
            if (city != null)
                stringBuilder.append(", "+ city);
            String postalCode = addresses.get(0).getPostalCode();
            if (postalCode != null)
                stringBuilder.append(" - "+ postalCode);

            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Address not found!";
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (latlngMap != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latlngMap).title(""));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlngMap, 17));
        }
        else
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.PORTO_LATITUDE, Constants.PORTO_LONGITUDE), 13));

        googleMap.getUiSettings().setZoomControlsEnabled( true );
        googleMap.getUiSettings().setMyLocationButtonEnabled( false );

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                PublicOpenSpaceAddEditFragment.this.latlngMap = latLng;
                // place marker
                String address = resolveAddress(latLng.latitude, latLng.longitude);
                ((TextView) fragmentLayout.findViewById(R.id.addEditItemAddress)).setText(address);
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).title(""));

                // update Questions answers
                if (questionNumberToViewMap != null) {
                    QuestionView questionView;

                    // set question 5 (Geocode) text
                    questionView = questionNumberToViewMap.get("5");
                    if (questionView != null) {
//                        Log.e(this.getClass().getName(), "-------------------- from map to question = "+ latLng.latitude +", "+ latLng.longitude);
                        ((InputTextQuestionView)questionView).setAnswers(latLng.latitude +", "+ latLng.longitude);
                    }

                    // set question 2 (Address) text
                    questionView = questionNumberToViewMap.get("2");
                    if (questionView != null) {
//                        Log.e(this.getClass().getName(), "-------------------- from map/address to question = "+ address);
                        ((InputTextQuestionView)questionView).setAnswers(address);
                    }
                }
            }
        });
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

    /**
     * Fill UI components with our internal {@link PublicOpenSpace} object
     */
    private void loadQuestionsAndOptions() {

        Context context = getContext();
        questionDAO = new QuestionDAO(context);
        questionDAO.open();

        LinearLayout container1 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer1);
        container1.addView(addToMap(context, "1"));
        QuestionView view2= addToMap(context, "2");
        view2.setEnabled(false);
        container1.addView(view2);
        container1.addView(addToMap(context, "3"));
        container1.addView(addToMap(context, "4"));
        QuestionView view5= addToMap(context, "5");
        view5.setEnabled(false);
        container1.addView(view5);
        container1.addView(addToMap(context, "6"));

        // Tab 1 has a map only
        LinearLayout container2 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer2);
        container2.addView(addToMap(context, "7"));
        container2.addView(addToMap(context, "8"));

        LinearLayout container3 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer3);
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

        LinearLayout container4 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer4);
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

        LinearLayout container5 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer5);
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
     *
     * This method must be called before any persistence procedure :)
     */
    public void saveObject(){
        publicOpenSpace.setName(((TextView) fragmentLayout.findViewById(R.id.addEditItemName)).getText() + "");

        // save PublicOpenSpace object
        if (publicOpenSpace.getId() == 0) // if (id == 0) we're gonna insert it
            publicOpenSpace = PublicOpenSpaceDAO.staticInsert(getActivity(), publicOpenSpace);
        else // just update it
            PublicOpenSpaceDAO.staticUpdate(getActivity(), publicOpenSpace);

        // save questions answers
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
}

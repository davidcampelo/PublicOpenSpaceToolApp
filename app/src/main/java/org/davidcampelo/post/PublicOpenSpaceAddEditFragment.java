package org.davidcampelo.post;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;
import org.davidcampelo.post.model.Questions;
import org.davidcampelo.post.utils.Constants;
import org.davidcampelo.post.view.InputDecimalQuestionView;
import org.davidcampelo.post.view.InputNumberQuestionView;
import org.davidcampelo.post.view.InputTextQuestionView;
import org.davidcampelo.post.view.MultipleQuestionView;
import org.davidcampelo.post.view.QuestionView;
import org.davidcampelo.post.view.SingleQuestionView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PublicOpenSpaceAddEditFragment extends Fragment implements OnMapReadyCallback {

    private PublicOpenSpace object;

    View fragmentLayout;

    MapView mapView;
    GoogleMap googleMap;

    private Button saveButton;

    ArrayList<Question> questions;
    QuestionDAO questionDAO;

    private AlertDialog saveButtonDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        Intent intent = getActivity().getIntent();
        long id = intent.getLongExtra(Constants.INTENT_ID_EXTRA, 0);

        // get references to components
        fragmentLayout = inflater.inflate(R.layout.fragment_public_open_space_add_edit, container, false);
        saveButton = (Button) fragmentLayout.findViewById(R.id.addEditSaveButton);

        // fill tabs
        fillTabTitles( (TabHost) fragmentLayout.findViewById(R.id.addEditItemTabHost) );

        object = PublicOpenSpaceDAO.staticGet(getActivity(), id);

        fillData(fragmentLayout);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) fragmentLayout.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // adjust VO fields
                refreshObject();

                if (object.id == 0) // if (id == 0) we're gonna insert it
                    object = PublicOpenSpaceDAO.staticInsert(getActivity(), object);
                else // just update it
                    PublicOpenSpaceDAO.staticUpdate(getActivity(), object);

                Toast.makeText(PublicOpenSpaceAddEditFragment.this.getActivity(), "Item saved successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        return fragmentLayout;
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


    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        CameraUpdate cameraUpdate;
        if (object.latitude != Double.MAX_VALUE && object.latitude != Double.MAX_VALUE) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(object.latitude, object.longitude), 17);

            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(object.latitude, object.longitude)).title("Marker"));
        }
        else
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.PORTO_LATITUDE, Constants.PORTO_LONGITUDE), 13);
        googleMap.animateCamera(cameraUpdate);

        googleMap.getUiSettings().setZoomControlsEnabled( true );
        googleMap.getUiSettings().setMyLocationButtonEnabled( false );

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                object.latitude = latLng.latitude;
                object.longitude = latLng.longitude;

                ((TextView) fragmentLayout.findViewById(R.id.addEditItemAddress)).setText(object.resolveAddress(PublicOpenSpaceAddEditFragment.this.getActivity()));
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(object.latitude, object.longitude)).title(""));
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
     * @param fragmentLayout
     */
    private void fillData(View fragmentLayout) {

        Context context = getContext();
        questionDAO = new QuestionDAO(context);
        questionDAO.open();
        questions = new ArrayList<>();

        LinearLayout container1 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer1);
        container1.addView(addQuestionToList(context, "1"));
        container1.addView(addQuestionToList(context, "2"));
        container1.addView(addQuestionToList(context, "3"));
        container1.addView(addQuestionToList(context, "4"));
        container1.addView(addQuestionToList(context, "5"));
        container1.addView(addQuestionToList(context, "6"));

        // Tab 1 has a map only
        LinearLayout container2 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer2);
        container2.addView(addQuestionToList(context, "7"));
        container2.addView(addQuestionToList(context, "8"));

        LinearLayout container3 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer3);
        container3.addView(addQuestionToList(context, "9"));
        container3.addView(addQuestionToList(context, "10"));
        container3.addView(addQuestionToList(context, "11"));
        container3.addView(addQuestionToList(context, "12"));
        container3.addView(addQuestionToList(context, "13.a"));
        container3.addView(addQuestionToList(context, "13.b"));
        container3.addView(addQuestionToList(context, "14"));
        container3.addView(addQuestionToList(context, "15"));
        container3.addView(addQuestionToList(context, "16"));
        container3.addView(addQuestionToList(context, "17"));
        container3.addView(addQuestionToList(context, "18.a"));
        container3.addView(addQuestionToList(context, "18.b"));
        container3.addView(addQuestionToList(context, "19"));
        container3.addView(addQuestionToList(context, "20"));
        container3.addView(addQuestionToList(context, "21"));
        container3.addView(addQuestionToList(context, "22"));
        container3.addView(addQuestionToList(context, "23"));
        container3.addView(addQuestionToList(context, "24"));
        container3.addView(addQuestionToList(context, "25"));

        LinearLayout container4 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer4);
        container4.addView(addQuestionToList(context, "26"));
        container4.addView(addQuestionToList(context, "27"));
        container4.addView(addQuestionToList(context, "28"));
//        container4.addView(addQuestionToList(context, "29"));
        container4.addView(addQuestionToList(context, "30"));
        container4.addView(addQuestionToList(context, "31"));
        container4.addView(addQuestionToList(context, "32.a"));
        container4.addView(addQuestionToList(context, "32.b"));
        container4.addView(addQuestionToList(context, "33"));
        container4.addView(addQuestionToList(context, "34"));
        container4.addView(addQuestionToList(context, "35"));
        container4.addView(addQuestionToList(context, "36"));
        container4.addView(addQuestionToList(context, "37"));
        container4.addView(addQuestionToList(context, "38"));
        container4.addView(addQuestionToList(context, "39"));
        container4.addView(addQuestionToList(context, "40"));
        container4.addView(addQuestionToList(context, "41"));
        container4.addView(addQuestionToList(context, "42"));

        LinearLayout container5 = (LinearLayout)fragmentLayout.findViewById(R.id.addEditContainer5);
        container5.addView(addQuestionToList(context, "43"));
        container5.addView(addQuestionToList(context, "44"));
        container5.addView(addQuestionToList(context, "45"));
        container5.addView(addQuestionToList(context, "46.a"));
        container5.addView(addQuestionToList(context, "46.b"));
        container5.addView(addQuestionToList(context, "47"));
        container5.addView(addQuestionToList(context, "48.a"));
        container5.addView(addQuestionToList(context, "48.b"));
//        container5.addView(addQuestionToList(context, "49.a"));
//        container5.addView(addQuestionToList(context, "49.b"));
//        container5.addView(addQuestionToList(context, "49.c"));

        questionDAO.close();
    }

    private QuestionView addQuestionToList(Context context, String questionNumber) {
        Question question = questionDAO.getByNumber(questionNumber);
        questions.add(question);

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

        return view;
    }

    /**
     * Fill our internal {@link PublicOpenSpace} object with UI components values
     *
     * This method must be called before any persistence procedure :)
     */
    public void refreshObject(){

   }
}

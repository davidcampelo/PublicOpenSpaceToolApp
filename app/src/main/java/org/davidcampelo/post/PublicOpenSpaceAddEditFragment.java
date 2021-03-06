package org.davidcampelo.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.davidcampelo.post.model.AnswersDAO;
import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.Question;
import org.davidcampelo.post.model.QuestionDAO;
import org.davidcampelo.post.utils.Constants;
import org.davidcampelo.post.utils.MapUtility;
import org.davidcampelo.post.utils.PublicOpenSpaceAsyncTask;
import org.davidcampelo.post.utils.UIUtils;
import org.davidcampelo.post.view.AnswerMissingException;
import org.davidcampelo.post.view.DisableQuestionNumbersListener;
import org.davidcampelo.post.view.InputDecimalQuestionView;
import org.davidcampelo.post.view.InputNumberQuestionView;
import org.davidcampelo.post.view.InputTextQuestionView;
import org.davidcampelo.post.view.InputZipCodeQuestionView;
import org.davidcampelo.post.view.MultipleQuestionView;
import org.davidcampelo.post.view.QuestionView;
import org.davidcampelo.post.view.SingleQuestionView;
import org.davidcampelo.post.view.VariableSingleQuestionView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicOpenSpaceAddEditFragment extends Fragment
        implements OnMapReadyCallback, OnMarkerClickListener, OnMapClickListener, OnMapLongClickListener,
        DisableQuestionNumbersListener{

    private PublicOpenSpace publicOpenSpace;
    private Project project;

    View fragmentLayout;
    TextView posName;
    TabHost tabHost;

    MapView mapView;
    GoogleMap googleMap;
    private ArrayList<LatLng> arrayPoints;
    LatLng markerPointClick;
    private AlertDialog markerDialog;



    private Button saveButton;
//    private ImageButton posType;


    HashMap<String, QuestionView> questionNumberToViewMap;
    QuestionDAO questionDAO;

//    private AlertDialog posTypeDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate layout
        fragmentLayout = inflater.inflate(R.layout.fragment_public_open_space_add_edit, container, false);

        // get fields
        posName = ((TextView) fragmentLayout.findViewById(R.id.addEditItemName));
        posName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                UIUtils.hideKeyboard(getContext(), view);
            }
        });

        // Bundle cannot be null as Project ***MUST*** be set
        Bundle bundle = this.getArguments();
        try{
            publicOpenSpace = (PublicOpenSpace)bundle.getParcelable(Constants.PUBLIC_OPEN_SPACE_EXTRA);

            getActivity().setTitle(R.string.title_public_open_space_edit);

            // fill fields
            posName.setText(publicOpenSpace.getName());
            //((ImageButton) fragmentLayout.findViewById(R.id.addEditItemType)).setImageResource(publicOpenSpace.getTypeResource());
            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_LONG).show();

        }
        catch (NullPointerException e){
            publicOpenSpace = new PublicOpenSpace();
            project = (Project) bundle.getParcelable(Constants.PROJECT_EXTRA);
            publicOpenSpace.setProject(project);

            getActivity().setTitle(R.string.title_public_open_space_add);

        }
        showInstructionsDialog(inflater);


        // get references to components
        saveButton = (Button) fragmentLayout.findViewById(R.id.addEditSaveButton);
//        posType = (ImageButton) fragmentLayout.findViewById(R.id.addEditItemType);

        tabHost = (TabHost) fragmentLayout.findViewById(R.id.addEditItemTabHost);
        // fill tabs
        setupTabs(tabHost);

        questionNumberToViewMap = new HashMap<>();
        loadQuestionsAndOptions();
        loadAnswers();

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) fragmentLayout.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveObject();

                    if (tabHost.getCurrentTab() == tabHost.getTabWidget().getTabCount() - 1 &&
                            percentageOfQuestionsAnswered() > Constants.MINIMUM_QUESTIONS_TO_BE_ANSWERED) {
                        showAddNewPOSDialog();
                    }
                } catch (AnswerMissingException e) {
                    Toast.makeText(PublicOpenSpaceAddEditFragment.this.getActivity(), "Warning: Answers for Question "+ e.getQuestion().getNumber() +" are missing, please review!", Toast.LENGTH_SHORT).show();
                }

            }
        });
//        posType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UIUtils.hideKeyboard(getContext(), view);
//
//                posTypeDialog.show();
//            }
//        });
//
//        buildTypeDialog();

        return fragmentLayout;
    }

    private void showAddNewPOSDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.StackedAlertDialogStyle);
        builder.setTitle(R.string.fragment_public_open_space_add_edit_add_dialog_text);
        builder.setPositiveButton(R.string.fragment_public_open_space_add_edit_add_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Bundle args = new Bundle();
                args.putParcelable(Constants.PROJECT_EXTRA, publicOpenSpace.getProject());
                Fragment fragment = new PublicOpenSpaceAddEditFragment();
                fragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack("")
                        .commit();
            }
        });
        builder.setNegativeButton(R.string.fragment_public_open_space_add_edit_add_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });

        AlertDialog addNewDialogObject = builder.create();

        addNewDialogObject.show();

        // Align options to the left (show() must be called before)
        Button dialogButton1 = addNewDialogObject.getButton(AlertDialog.BUTTON_POSITIVE);
        dialogButton1.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        LinearLayout.LayoutParams dialogButton1LL = (LinearLayout.LayoutParams) dialogButton1.getLayoutParams();
        dialogButton1LL.gravity = Gravity.LEFT;
        dialogButton1.setLayoutParams(dialogButton1LL);
        Button dialogButton2 = addNewDialogObject.getButton(AlertDialog.BUTTON_NEGATIVE);
        dialogButton2.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        LinearLayout.LayoutParams dialogButton2LL = (LinearLayout.LayoutParams) dialogButton2.getLayoutParams();
        dialogButton2LL.gravity = Gravity.LEFT;
        dialogButton2.setLayoutParams(dialogButton2LL);
    }

    private double percentageOfQuestionsAnswered() {
        HashMap<Question, String> questionToAnswersMap = new HashMap<Question, String>();

        int totalQuestions = questionNumberToViewMap.keySet().size();
        int totalValidAnswers = 0;
        for (String questionNumber : questionNumberToViewMap.keySet()) {
            QuestionView questionView = questionNumberToViewMap.get(questionNumber);
            String answer = null;
            try {
                answer = questionView.getAnswers();
            }
            catch (AnswerMissingException e) {}
            if (answer != null && answer.length() > 0){
                totalValidAnswers++;
            }
        }
        return (double)totalValidAnswers/(double)totalQuestions;
    }


    private void showInstructionsDialog(LayoutInflater inflater) {
        // Main dialog with app usage instructions
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String fragment_public_open_space_add_edit_instructions_status = getString(R.string.fragment_public_open_space_add_edit_instructions_status);
        boolean dialog_status = sharedPreferences.getBoolean(fragment_public_open_space_add_edit_instructions_status, false);
        if (!dialog_status) {
            // Set dialog contents
            final View dialogView = inflater.inflate(R.layout.dialog_instructions, null);
            ((TextView)dialogView.findViewById(R.id.dialogInstructionsText)).setText(getString(R.string.fragment_public_open_space_add_edit_instructions_text));
            ((CheckBox)dialogView.findViewById(R.id.dialogInstructionsCheckbox)).setText(getString(R.string.fragment_public_open_space_add_edit_instructions_checkbox));

            //build the dialog
            new AlertDialog.Builder(this.getContext())
//                    .setTitle(R.string.title_splash_dialog_app_instructions)
                    .setView(dialogView)
                    .setPositiveButton(getString(R.string.activity_splash_dialog_app_instructions_button),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,int which) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(fragment_public_open_space_add_edit_instructions_status,
                                            ((CheckBox)dialogView.findViewById(R.id.dialogInstructionsCheckbox)).isChecked());
                                    editor.commit();
                                    dialog.dismiss();
                                }
                            }).create().show();

        }
    }

//    private void buildTypeDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.fragment_public_open_space_add_edit_type_dialog_title);
//        builder.setSingleChoiceItems(getResources().getStringArray(R.array.public_open_space_types), 0, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int item) {
//                posTypeDialog.cancel();
//                switch (item) {
//                    case 0:
//                        publicOpenSpace.setType(PublicOpenSpace.Type.PARK);
//                        posType.setImageResource(R.drawable.icon_park);
//                        break;
//                    case 1:
//                        publicOpenSpace.setType(PublicOpenSpace.Type.SQUARE);
//                        posType.setImageResource(R.drawable.icon_square);
//                        break;
//                    case 2:
//                        publicOpenSpace.setType(PublicOpenSpace.Type.GARDEN);
//                        posType.setImageResource(R.drawable.icon_garden);
//                        break;
//                    default:
//                        publicOpenSpace.setType(PublicOpenSpace.Type.OTHER);
//                        posType.setImageResource(R.drawable.icon_other);
//                        break;
//                }
//            }
//        });
//
//        posTypeDialog = builder.create();
//    }

    private void setupTabs(TabHost host) {
        host.setup();
        TabHost.TabSpec spec;

        // Set up titles
        int[] tabContents = new int[]{R.id.addEditItemTab0, R.id.addEditItemTab1, R.id.addEditItemTab2, R.id.addEditItemTab3, R.id.addEditItemTab4, R.id.addEditItemTab5};

        for (int i = -1; ++i < Constants.TAB_TITLES.length; ) {
            //Tabs
            spec = host.newTabSpec(Constants.TAB_TITLES[i]);
            spec.setContent(tabContents[i]);
            spec.setIndicator(Constants.TAB_TITLES[i]);
            host.addTab(spec);
            ((TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setAllCaps(false);
        }

        // Apply the right style
        TabWidget widget = host.getTabWidget();
        for(int i = 0; i < widget.getChildCount(); i++) {
            View v = widget.getChildAt(i);

            // Look for the title view to ensure this is an indicator and not a divider.
            TextView tv = (TextView)v.findViewById(android.R.id.title);
            if(tv == null) {
                continue;
            }
            v.setBackgroundResource(R.drawable.tab_indicator_ab_green);
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
        container1.addView(addToMap(context, "01"));
        container1.addView(addToMap(context, "02"));
        QuestionView view3=addToMap(context, "03");
        view3.setEnabled(false);
        container1.addView(view3);
        container1.addView(addToMap(context, "04"));
        QuestionView view5=addToMap(context, "05");
        view5.setEnabled(false);
        container1.addView(view5);
        container1.addView(addToMap(context, "06"));

        LinearLayout container2 = (LinearLayout) fragmentLayout.findViewById(R.id.addEditContainer2);
        container2.addView(addToMap(context, "07"));
        container2.addView(addToMap(context, "08"));

        LinearLayout container3 = (LinearLayout) fragmentLayout.findViewById(R.id.addEditContainer3);
        container3.addView(addToMap(context, "09"));
        container3.addView(addToMap(context, "10"));
        container3.addView(addToMap(context, "11"));
        container3.addView(addToMap(context, "12"));
        container3.addView(addToMap(context, "13a"));
        container3.addView(addToMap(context, "13b"));
        container3.addView(addToMap(context, "14"));
        container3.addView(addToMap(context, "15"));
        container3.addView(addToMap(context, "16"));
        container3.addView(addToMap(context, "17"));
        container3.addView(addToMap(context, "18a"));
        container3.addView(addToMap(context, "18b"));
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
        container4.addView(addToMap(context, "29"));
        container4.addView(addToMap(context, "30"));
        container4.addView(addToMap(context, "31"));
        container4.addView(addToMap(context, "32a"));
        container4.addView(addToMap(context, "32b"));
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
        container5.addView(addToMap(context, "46a"));
        container5.addView(addToMap(context, "46b"));
        container5.addView(addToMap(context, "46c"));
        container5.addView(addToMap(context, "47"));
        container5.addView(addToMap(context, "48a"));
        container5.addView(addToMap(context, "48b"));
        container5.addView(addToMap(context, "49a"));
        container5.addView(addToMap(context, "49b"));
        container5.addView(addToMap(context, "49c"));

        questionDAO.close();
    }

    private void loadAnswers() {
        if (publicOpenSpace.getId() != 0) {     // if not a new object (i.e. if it has some answers)
            Context context = getContext();
            AnswersDAO answersDAO = new AnswersDAO(context);
            answersDAO.open();

            for (String questionNumber : questionNumberToViewMap.keySet()) {
                QuestionView questionView = questionNumberToViewMap.get(questionNumber);
                questionView.setAnswers(answersDAO.get(questionView.getQuestion(), publicOpenSpace));
            }

            answersDAO.close();
        }
    }

    private final QuestionView addToMap(Context context, String questionNumber) {
        Question question = questionDAO.get(questionNumber, publicOpenSpace);
        QuestionView view = null;

        Question.QuestionType type = question.getType();
        if (type == Question.QuestionType.SINGLE_CHOICE)
            view = new SingleQuestionView(context, question);
        else if (type == Question.QuestionType.MULTIPLE_CHOICE)
        view = new MultipleQuestionView(context, question);
        else if (type == Question.QuestionType.VARIABLE_SINGLE_CHOICE)
            view = new VariableSingleQuestionView(context, question);
        else if (type == Question.QuestionType.INPUT_DECIMAL)
            view = new InputDecimalQuestionView(context, question);
        else if (type == Question.QuestionType.INPUT_TEXT)
            view = new InputTextQuestionView(context, question);
        else if (type == Question.QuestionType.INPUT_NUMBER)
            view = new InputNumberQuestionView(context, question);
        else if (type == Question.QuestionType.INPUT_ZIPCODE)
            view = new InputZipCodeQuestionView(context, question);
        else  if (type == Question.QuestionType.INPUT_COORDINATES)
            view = new InputTextQuestionView(context, question);

        view.setDisableQuestionNumbersListener(this);
        this.questionNumberToViewMap.put(questionNumber, view);

        return view;
    }

    /**
     * Fill our internal {@link PublicOpenSpace} object with UI components values
     * <p>
     * This method must be called before any persistence procedure :)
     */
    public void saveObject() throws AnswerMissingException {
        // NAME
        publicOpenSpace.setName(((TextView) fragmentLayout.findViewById(R.id.addEditItemName)).getText() + "");

        // POLYGON POINTS
        publicOpenSpace.setPolygonPoints(arrayPoints);

        HashMap<Question, String> questionToAnswersMap = new HashMap<Question, String>();

        for (String questionNumber : questionNumberToViewMap.keySet()) {
            QuestionView questionView = questionNumberToViewMap.get(questionNumber);
            questionToAnswersMap.put(questionView.getQuestion(), questionView.getAnswers());
        }

        PublicOpenSpaceAsyncTask runner = new PublicOpenSpaceAsyncTask(
                getActivity(),
                publicOpenSpace,
                questionToAnswersMap);
        runner.execute();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////   DISABLEQUESTIONNUMBERSLISTENER   ///////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void disableQuestions(ArrayList<String> disableQuestionNumbers) {
        for (String questionNumber : disableQuestionNumbers) {
            QuestionView view = this.questionNumberToViewMap.get(questionNumber);
            view.setEnabled(false);
        }
    }

    @Override
    public void enableQuestions(ArrayList<String> disableQuestionNumbers) {
        for (String questionNumber : disableQuestionNumbers) {
            QuestionView view = this.questionNumberToViewMap.get(questionNumber);
            view.setEnabled(true);
        }
    }





    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////     MAP METHODS   ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void buildMarkerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.StackedAlertDialogStyle);
            builder.setMessage(R.string.marker_click_dialog_question);
        builder.setPositiveButton(R.string.marker_click_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (markerPointClick != null && arrayPoints.contains(markerPointClick) ) {
                    arrayPoints.remove(markerPointClick);
                    markerPointClick = null;
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

    private void drawPolygon() {
        googleMap.clear();

        for (LatLng point : arrayPoints) {
            googleMap.addMarker(new MarkerOptions().position(point));
        }

        if (arrayPoints.size() >= 3) {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(arrayPoints);
            polygonOptions.strokeColor(Color.argb(0, 0, 0, 100));
            polygonOptions.strokeWidth(7);
            polygonOptions.fillColor(Color.argb(100, 0, 0, 100));
            googleMap.addPolygon(polygonOptions);
        }
        // set question 3 (area) text
        QuestionView questionView;
        questionView = questionNumberToViewMap.get("03");
        String area = "";
        if (questionView != null) {
            if (arrayPoints.size() >= 3) {
                area = MapUtility.calculateAreaAndFormat(arrayPoints);
            }
            Log.e(this.getClass().getName(), "-------------------->>>> [AREA] from map to question = "+ area);
            ((InputDecimalQuestionView) questionView).setAnswers(area);
        }
        questionView = questionNumberToViewMap.get("05");
        String centroid = "";
        if (questionView != null) {
            if (arrayPoints.size() >= 1) {
                LatLng latLng = MapUtility.calculateCentroid(arrayPoints);
                centroid = latLng.latitude +" "+ latLng.longitude;
            }
            Log.e(this.getClass().getName(), "-------------------->>>> [CENTROID] from map to question = "+ centroid);
            ((InputTextQuestionView) questionView).setAnswers(centroid);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        arrayPoints = new ArrayList<LatLng>();
        markerPointClick = null;
        buildMarkerDialog();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (publicOpenSpace.getPolygonPoints() != null && publicOpenSpace.getPolygonPoints().size() > 0) {
            MapUtility.moveMapCamera(googleMap, publicOpenSpace.getPolygonPoints());
            arrayPoints = publicOpenSpace.getPolygonPoints();
            drawPolygon();
        }
        else if (publicOpenSpace.getProject().getPolygonPoints() != null && publicOpenSpace.getProject().getPolygonPoints().size() > 0){
            MapUtility.moveMapCamera(googleMap, publicOpenSpace.getProject().getPolygonPoints());
        }

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
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
    @Override
    public void onMapClick(LatLng latLng) {
        UIUtils.hideKeyboard(getContext(), mapView);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        UIUtils.hideKeyboard(getContext(), mapView);

        markerPointClick = marker.getPosition();
        markerDialog.show();

        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        UIUtils.hideKeyboard(getContext(), mapView);

        arrayPoints.add(latLng);

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
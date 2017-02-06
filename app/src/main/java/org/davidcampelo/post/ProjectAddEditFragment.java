package org.davidcampelo.post;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.android.gms.maps.model.PolygonOptions;

import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.ProjectDAO;
import org.davidcampelo.post.utils.Constants;
import org.davidcampelo.post.utils.MapUtility;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectAddEditFragment extends Fragment
        implements OnMapReadyCallback, OnMarkerClickListener, OnMapLongClickListener {

    private Project project;

    View fragmentLayout;

    MapView mapView;
    GoogleMap googleMap;
    private ArrayList<LatLng> arrayPoints;
    LatLng markerPointClick;
    private AlertDialog markerDialog;

    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentLayout = inflater.inflate(R.layout.fragment_project_add_edit, container, false);

        Bundle bundle = this.getArguments();
        try {
            project = (Project) bundle.getParcelable(Constants.PROJECT_EXTRA);

            getActivity().setTitle(R.string.title_project_edit);

            // fill fields
            ((TextView) fragmentLayout.findViewById(R.id.addEditItemName)).setText(project.getName());
            ((TextView) fragmentLayout.findViewById(R.id.addEditItemDesc)).setText(project.getDesc());
        }
        catch (NullPointerException e){
            project = new Project();

            getActivity().setTitle(R.string.title_project_add);
        }

        showInstructionsDialog(inflater);

        // get references to components
        saveButton = (Button) fragmentLayout.findViewById(R.id.addEditSaveButton);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) fragmentLayout.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveObject();

                Toast.makeText(ProjectAddEditFragment.this.getActivity(), "Item saved successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        return fragmentLayout;
    }

    private void showInstructionsDialog(LayoutInflater inflater) {
        // Main dialog with app usage instructions
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String fragment_project_add_edit_instructions_status = getString(R.string.fragment_project_add_edit_instructions_status);
        boolean dialog_status = sharedPreferences.getBoolean(fragment_project_add_edit_instructions_status, false);
        if (!dialog_status) {
            // Set dialog contents
            final View dialogView = inflater.inflate(R.layout.dialog_instructions, null);
            ((TextView)dialogView.findViewById(R.id.dialogInstructionsText)).setText(getString(R.string.fragment_project_add_edit_instructions_text));
            ((CheckBox)dialogView.findViewById(R.id.dialogInstructionsCheckbox)).setText(getString(R.string.fragment_project_add_edit_instructions_checkbox));

            //build the dialog
            new AlertDialog.Builder(this.getContext())
//                    .setTitle(R.string.title_splash_dialog_app_instructions)
                    .setView(dialogView)
                    .setPositiveButton(getString(R.string.activity_splash_dialog_app_instructions_button),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,int which) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(fragment_project_add_edit_instructions_status,
                                            ((CheckBox)dialogView.findViewById(R.id.dialogInstructionsCheckbox)).isChecked());
                                    editor.commit();
                                    dialog.dismiss();
                                }
                            }).create().show();

        }
    }

    /**
     * Fill our internal {@link Project} object with UI components values
     * <p>
     * This method must be called before any persistence procedure :)
     */
    public void saveObject() {
        // NAME
        project.setName(((TextView) fragmentLayout.findViewById(R.id.addEditItemName)).getText() + "");
        project.setDesc(((TextView) fragmentLayout.findViewById(R.id.addEditItemDesc)).getText() + "");

        // POLYGON POINTS
        project.setPolygonPoints(arrayPoints);

        // NOW SAVE OBJECT
        if (project.getId() == 0) // if (id == 0) we're gonna insert it
            project = ProjectDAO.staticInsert(getActivity(), project);
        else // just update it
            ProjectDAO.staticUpdate(getActivity(), project);

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        arrayPoints = new ArrayList<LatLng>();
        markerPointClick = null;
        buildMarkerDialog();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (project.getPolygonPoints() != null && project.getPolygonPoints().size() > 0) {
            MapUtility.moveMapCamera(googleMap, project.getPolygonPoints());
            arrayPoints = project.getPolygonPoints();
            drawPolygon();
        }
        else {
            MapUtility.moveMapCamera(googleMap, Constants.PORTUGAL_BOUNDING_POINTS);
        }


        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);

        ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

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
        markerPointClick = marker.getPosition();
        markerDialog.show();

        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
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
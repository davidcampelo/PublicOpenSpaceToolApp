package org.davidcampelo.post;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDBAdapter;
import org.davidcampelo.post.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class PublicOpenSpaceAddEditFragment extends Fragment implements OnMapReadyCallback {

    private PublicOpenSpace object;

    private EditText nameEditText;
    private TextView addressTextView;

    MapView mapView;
    GoogleMap googleMap;

    private Button saveButton;


    private AlertDialog saveButtonDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        Intent intent = getActivity().getIntent();
        long id = intent.getLongExtra(Constants.INTENT_ID_EXTRA, 0);

        // get references to components
        View fragmentLayout = inflater.inflate(R.layout.fragment_public_open_space_add_edit, container, false);
        nameEditText = (EditText) fragmentLayout.findViewById(R.id.addEditItemName);
        addressTextView = (TextView) fragmentLayout.findViewById(R.id.addEditItemAddress);
        saveButton = (Button) fragmentLayout.findViewById(R.id.addEditSaveButton);

        // fill tabs
        fillTabTitles( (TabHost) fragmentLayout.findViewById(R.id.addEditItemTabHost) );

        object = PublicOpenSpaceDBAdapter.staticGet(getActivity(), id);

        fillData();


        buildSaveButtonDialog();

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) fragmentLayout.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // adjust VO fields
                object.name = nameEditText.getText() + "";
                object.address = addressTextView.getText() + "";

                if (object.id == 0) // if (id == 0) we're gonna insert it
                    object = PublicOpenSpaceDBAdapter.staticInsert(getActivity(), object);
                else // just update it
                    PublicOpenSpaceDBAdapter.staticUpdate(getActivity(), object);

                saveButtonDialog.show();

            }
        });

        return fragmentLayout;
    }

    private void fillTabTitles(TabHost host) {
        host.setup();
        TabHost.TabSpec spec;

        int[] tabContents = new int[]{R.id.addEditItemTab1, R.id.addEditItemTab2, R.id.addEditItemTab3, R.id.addEditItemTab4, R.id.addEditItemTab5};

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

                addressTextView.setText(object.resolveAddress(PublicOpenSpaceAddEditFragment.this.getActivity()));
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


    private void buildSaveButtonDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Save note");
        builder.setMessage("Item saved successfully!");
//        builder.setPositiveButton("Yes, save it!", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // redirect to main (list) activity
//                //startActivity(new Intent(getActivity(), MainActivity.class));
//            }
//        });
//        builder.setNegativeButton("No, don't save!", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                // do nothing
//            }
//        });
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        saveButtonDialog = builder.create();
    }

    private void fillData() {
        nameEditText.setText(object.name);
        addressTextView.setText(object.resolveAddress(this.getActivity()));
    }
}

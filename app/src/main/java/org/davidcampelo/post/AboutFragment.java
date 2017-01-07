package org.davidcampelo.post;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class AboutFragment extends Fragment {

    TextView aboutVersion;
    Button seeManualsButton;

    public AboutFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_about);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_about, container, false);
        aboutVersion = (TextView) fragmentLayout.findViewById(R.id.fragmentAboutVersion);
        seeManualsButton = (Button) fragmentLayout.findViewById(R.id.fragmentAboutSeeManualsButton);
//        seeManualsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle args = new Bundle();
//                Fragment fragment = new ManualsFragment();
//                fragment.setArguments(args);
//
//                getFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.mainContainer, fragment)
//                        .addToBackStack("")
//                        .commit();
//            }
//        });
        aboutVersion.setText("v" + BuildConfig.VERSION_NAME);
        return fragmentLayout;
    }
}

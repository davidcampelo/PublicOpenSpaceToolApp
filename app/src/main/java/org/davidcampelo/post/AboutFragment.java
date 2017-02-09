package org.davidcampelo.post;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.davidcampelo.post.utils.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class AboutFragment extends Fragment {

    TextView aboutVersion;
    ImageView logos;

    int easterCounter = 1;
    AlertDialog defaultDialogObject;


    public AboutFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_about);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_about, container, false);
        logos = (ImageView) fragmentLayout.findViewById(R.id.fragmentAboutLogos);
        logos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (easterCounter++ % 13 == 0) {
                    defaultDialogObject.show();
                }
            }
        });
        aboutVersion = (TextView) fragmentLayout.findViewById(R.id.fragmentAboutVersion);
        aboutVersion.setText("v" + BuildConfig.VERSION_NAME);

        buildDefaultDataDialog();

        return fragmentLayout;
    }


    private void buildDefaultDataDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.fragment_settings_default_dialog_title);
        builder.setMessage(R.string.fragment_settings_default_dialog_question);
        builder.setPositiveButton(R.string.fragment_settings_default_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Data.clearDatabase(getActivity());
                Data.populateDatabaseFromXML(getActivity(), getResources());
                Data.populateDatabaseFromSQL(getActivity());
            }
        });
        builder.setNegativeButton(R.string.fragment_settings_default_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        defaultDialogObject = builder.create();
    }
}

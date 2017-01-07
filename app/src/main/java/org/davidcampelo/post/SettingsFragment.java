package org.davidcampelo.post;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.ProjectDAO;
import org.davidcampelo.post.utils.Data;
import org.davidcampelo.post.utils.ProjectExportAsyncTask;

import java.util.ArrayList;


public class SettingsFragment extends Fragment {

    Button resetDataButton;
    Button defaultDataButton;
    AlertDialog resetDialogObject;

    public SettingsFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_settings);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_settings, container, false);
        resetDataButton = (Button) fragmentLayout.findViewById(R.id.settingsResetDataButton);
        resetDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetDialogObject.show();
            }
        });

//        defaultDataButton = (Button) fragmentLayout.findViewById(R.id.settingsDefaultPortoDataButton);
//        defaultDataButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                defaultDialogObject.show();
//            }
//        });

        buildResetDataDialog();

        return fragmentLayout;
    }

    private void buildResetDataDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.fragment_settings_reset_dialog_title);
        builder.setMessage(R.string.fragment_settings_reset_dialog_question);
        builder.setPositiveButton(R.string.fragment_settings_reset_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Data.clearDatabase(getActivity());
                Data.resetDatabase(getActivity(), getResources());
            }
        });
        builder.setNegativeButton(R.string.fragment_settings_reset_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        resetDialogObject = builder.create();
    }

}

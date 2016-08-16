package org.davidcampelo.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.davidcampelo.post.utils.Data;


public class MosaicFragment extends Fragment {

    ImageButton imageButtonNewProject,
                imageButtonOpenProject,
                imageButtonExportData,
                imageButtonSettings;
    AlertDialog resetDialogObject;

    public MosaicFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_mosaic, container, false);
        imageButtonNewProject = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonNewProject);
        imageButtonOpenProject = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonOpenProject);
        imageButtonSettings = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonSettings);
        imageButtonExportData = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonExportData);

        imageButtonOpenProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new PublicOpenSpaceListFragment(), "PublicOpenSpaceListFragment")
                        .commit();

                getActivity().setTitle(R.string.title_view_public_open_space_fragment);

            }
        });

        // TODO that's a poor hack!!!
        imageButtonExportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetDialogObject.show();
            }
        });


        buildResetDialog();

        return fragmentLayout;
    }

    private void buildResetDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_reset_dialog_title);
        builder.setMessage(R.string.action_reset_dialog_question);
        builder.setPositiveButton(R.string.action_reset_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Data.clearDatabase(getActivity());
                Data.populateDatabase(getActivity(), getResources());
            }
        });
        builder.setNegativeButton(R.string.action_reset_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        resetDialogObject = builder.create();
    }
}

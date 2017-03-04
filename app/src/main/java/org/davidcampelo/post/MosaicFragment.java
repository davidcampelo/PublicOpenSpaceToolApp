package org.davidcampelo.post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class MosaicFragment extends Fragment {

    ImageButton imageButtonNewProject,
                imageButtonOpenProject,
                imageButtonExportData,
                imageButtonSettings,
                imageButtonAbout,
                imageButtonHelp;

    public MosaicFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_mosaic);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_mosaic, container, false);
        imageButtonNewProject = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonNewProject);
        imageButtonOpenProject = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonOpenProject);
        imageButtonSettings = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonSettings);
        imageButtonExportData = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonExportData);
        imageButtonAbout = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonAbout);
        imageButtonHelp = (ImageButton) fragmentLayout.findViewById(R.id.imageButtonHelp);

        imageButtonNewProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new ProjectAddEditFragment())
                        .addToBackStack("")
                        .commit();
            }
        });

        imageButtonOpenProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new ProjectListFragment())
                        .addToBackStack("")
                        .commit();
            }
        });

            imageButtonExportData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new ExportDataFragment())
                        .addToBackStack("")
                        .commit();
            }
        });

        imageButtonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new AboutFragment())
                        .addToBackStack("")
                        .commit();
            }
        });

        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new SettingsFragment())
                        .addToBackStack("")
                        .commit();
            }
        });
        imageButtonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new HelpFragment())
                        .addToBackStack("")
                        .commit();
            }
        });

        return fragmentLayout;
    }
}

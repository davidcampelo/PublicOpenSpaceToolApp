package org.davidcampelo.post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MosaicFragment extends Fragment {

    Button  buttonNewProject,
            buttonOpenProject,
            buttonExportData,
            buttonSettings,
            buttonAbout,
            buttonHelp;

    public MosaicFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_mosaic);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_mosaic, container, false);
        buttonNewProject = (Button) fragmentLayout.findViewById(R.id.buttonNewProject);
        buttonOpenProject = (Button) fragmentLayout.findViewById(R.id.buttonOpenProject);
        buttonSettings = (Button) fragmentLayout.findViewById(R.id.buttonSettings);
        buttonExportData = (Button) fragmentLayout.findViewById(R.id.buttonExportData);
        buttonAbout = (Button) fragmentLayout.findViewById(R.id.buttonAbout);
        buttonHelp = (Button) fragmentLayout.findViewById(R.id.buttonHelp);

        buttonNewProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new ProjectAddEditFragment())
                        .addToBackStack("")
                        .commit();
            }
        });

        buttonOpenProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new ProjectListFragment())
                        .addToBackStack("")
                        .commit();
            }
        });

            buttonExportData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new ExportDataFragment())
                        .addToBackStack("")
                        .commit();
            }
        });

        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new AboutFragment())
                        .addToBackStack("")
                        .commit();
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, new SettingsFragment())
                        .addToBackStack("")
                        .commit();
            }
        });
        buttonHelp.setOnClickListener(new View.OnClickListener() {
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

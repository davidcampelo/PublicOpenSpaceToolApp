package org.davidcampelo.post;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.ProjectDAO;
import org.davidcampelo.post.utils.ProjectExportAsyncTask;

import java.util.ArrayList;


public class ExportDataFragment extends Fragment {
    private ArrayList<Project> list;
    private ProjectListAdapter listAdapter;
    Spinner projects;

    public ExportDataFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_export_data);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_export_data, container, false);
        ProjectDAO projectDAO = new ProjectDAO(getActivity());
        projectDAO.open();
        list = projectDAO.getAll();
        projectDAO.close();

        listAdapter = new ProjectListAdapter(getActivity(), list);
        listAdapter.setNameSize(android.R.style.TextAppearance_Small);
        listAdapter.setShowDate(false);


        projects = (Spinner)fragmentLayout.findViewById(R.id.exportDataSpinner);

        Button exportDataButton = (Button)fragmentLayout.findViewById(R.id.exportDataButton);
        exportDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProjectExportAsyncTask runner = new ProjectExportAsyncTask(
                        ExportDataFragment.this.getActivity(),
                        (Project)ExportDataFragment.this.projects.getSelectedItem(),
                        ((CheckBox)fragmentLayout.findViewById(R.id.exportDataCheckBoxCSV)).isChecked(),
                        ((CheckBox)fragmentLayout.findViewById(R.id.exportDataCheckBoxKMZ)).isChecked(),
                        ((CheckBox)fragmentLayout.findViewById(R.id.exportDataCheckBoxSQL)).isChecked());
                runner.execute();
            }

        });
        projects.setAdapter(listAdapter);

        return fragmentLayout;
    }
}

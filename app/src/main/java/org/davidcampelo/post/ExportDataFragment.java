package org.davidcampelo.post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.ProjectDAO;

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
        ProjectDAO dbAdapter = new ProjectDAO(getActivity());
        dbAdapter.open();
        list = dbAdapter.getAll();
        dbAdapter.close();

        listAdapter = new ProjectListAdapter(getActivity(), list);
        listAdapter.setNameSize(android.R.style.TextAppearance_Small);
        listAdapter.setShowDate(false);


        projects = (Spinner)fragmentLayout.findViewById(R.id.exportDataSpinner);
        projects.setAdapter(listAdapter);

        return fragmentLayout;
    }
}

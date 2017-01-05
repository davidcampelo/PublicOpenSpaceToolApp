package org.davidcampelo.post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class AboutFragment extends Fragment {


    public AboutFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_about);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_about, container, false);

        return fragmentLayout;
    }
}

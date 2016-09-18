package org.davidcampelo.post;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.ProjectDAO;
import org.davidcampelo.post.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link ListFragment} subclass.
 */
public class ProjectListFragment extends ListFragment {

    private ArrayList<Project> list;
    private ProjectListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_project_list, container, false);

        FloatingActionButton fab = (FloatingActionButton) fragmentLayout.findViewById(R.id.fabAddProject);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                Fragment fragment = new ProjectAddEditFragment();
                fragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack("")
                        .commit();
            }
        });
        getActivity().setTitle(R.string.title_project_list);

        return fragmentLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ProjectDAO dbAdapter = new ProjectDAO(getActivity());
        dbAdapter.open();
        list = dbAdapter.getAll();
        dbAdapter.close();

        listAdapter = new ProjectListAdapter(getActivity(), list);

        setListAdapter(listAdapter);

//        getListView().setDivider(ContextCompat.getDrawable(getActivity(), android.R.color.background_dark));
//        getListView().setDividerHeight(2);

        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.fragment_project_list_menu, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Project object = (Project) getListAdapter().getItem(position);

        Bundle args = new Bundle();
        args.putParcelable(Constants.PROJECT_EXTRA, object);
        Fragment fragment = new PublicOpenSpaceListFragment();
        fragment.setArguments(args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .addToBackStack("")
                .commit();
    }

    @Override

    public boolean onContextItemSelected(MenuItem item) {
        // selected item
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Project object = (Project) getListAdapter().getItem(info.position);

        Fragment fragment;
        Bundle args = new Bundle();
        args.putParcelable(Constants.PROJECT_EXTRA, object);

        switch (item.getItemId()){
            case R.id.fragment_project_list_menu_view:
                fragment = new PublicOpenSpaceListFragment();
                fragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack("")
                        .commit();

                return true;

            case R.id.fragment_project_list_menu_add_public_open_space:
                fragment = new PublicOpenSpaceAddEditFragment();
                fragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack("")
                        .commit();

                return true;

            case R.id.fragment_project_list_menu_edit:
                fragment = new ProjectAddEditFragment();
                fragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack("")
                        .commit();

                return true;

            case R.id.fragment_project_list_menu_delete:
                // delete from DB
                // TODO confirm action
                ProjectDAO.staticDelete(this.getActivity(), object);

                // refresh list
//                notes.clear();
//                notes.addAll(dbAdapter.getAllNotes());
//                adapter.notifyDataSetChanged();
                listAdapter.remove(object);
                listAdapter.notifyDataSetChanged();

                return true;
        }


        return super.onContextItemSelected(item);

    }


}

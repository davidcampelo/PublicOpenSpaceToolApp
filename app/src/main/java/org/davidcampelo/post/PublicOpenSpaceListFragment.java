package org.davidcampelo.post;


import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
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
import android.widget.Toast;

import org.davidcampelo.post.model.Project;
import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link ListFragment} subclass.
 */
public class PublicOpenSpaceListFragment extends ListFragment {

    private ArrayList<PublicOpenSpace> list;
    private PublicOpenSpaceListAdapter listAdapter;

    Project project;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();

        // PROJECT ***MUST*** BE PASSED!
        project = (Project) bundle.getParcelable(Constants.PROJECT_EXTRA);
        getActivity().setTitle(R.string.title_public_open_space_list);

        View fragmentLayout = inflater.inflate(R.layout.fragment_public_open_space_list, container, false);

        FloatingActionButton fab = (FloatingActionButton) fragmentLayout.findViewById(R.id.fabAddPublicOpenSpace);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putParcelable(Constants.PROJECT_EXTRA, PublicOpenSpaceListFragment.this.project);
                Fragment fragment = new PublicOpenSpaceAddEditFragment();
                fragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack("")
                        .commit();

            }
        });


        return fragmentLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PublicOpenSpaceDAO dbAdapter = new PublicOpenSpaceDAO(getActivity());
        dbAdapter.open();
        list = dbAdapter.getAllByProject(project);
        dbAdapter.close();

        listAdapter = new PublicOpenSpaceListAdapter(getActivity(), list);

        setListAdapter(listAdapter);

//        getListView().setDivider(ContextCompat.getDrawable(getActivity(), android.R.color.background_dark));
//        getListView().setDividerHeight(2);

        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.fragment_public_open_space_list_menu, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        launch( (PublicOpenSpace) getListAdapter().getItem(position) );

    }

    @Override

    public boolean onContextItemSelected(MenuItem item) {
        // selected item
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        PublicOpenSpace object = (PublicOpenSpace) getListAdapter().getItem(info.position);
        switch (item.getItemId()){
            case R.id.fragment_public_open_space_list_menu_edit:
                launch( object );
                return true;
            case R.id.fragment_public_open_space_list_menu_delete:
                // delete from DB
                // TODO confirm action before
                PublicOpenSpaceDAO.staticDelete(this.getActivity(), object);

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

    private void launch(PublicOpenSpace object) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.PUBLIC_OPEN_SPACE_EXTRA, object);
        args.putParcelable(Constants.PROJECT_EXTRA, this.project);
        Fragment fragment = new PublicOpenSpaceAddEditFragment();
        fragment.setArguments(args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .addToBackStack("")
                .commit();
    }

    // Date format to show date on list
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
    // Utility class to keep list row values
    public static class ViewHolder{
        TextView name;
        TextView date;
        ImageView image;
    }
    /**
     * Inner class for handling list adapter rows
     */
    class PublicOpenSpaceListAdapter extends ArrayAdapter<PublicOpenSpace> {

        public PublicOpenSpaceListAdapter(Context context, ArrayList<PublicOpenSpace> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PublicOpenSpace object = getItem(position);

            ViewHolder viewHolder;

            if (convertView == null){

                viewHolder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_public_open_space_list_row, parent, false);
                if (position % 2 == 0) {
                    convertView.setBackgroundColor(Constants.LIST_ROW_COLOR);
                }

                viewHolder.name = (TextView) convertView.findViewById(R.id.listItemName);
                viewHolder.date = (TextView) convertView.findViewById(R.id.listItemDate);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.listItemType);

                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.name.setText(object.getName());
            viewHolder.date.setText("Added on "+ simpleDateFormat.format(new Date(object.getDateCreation())));
            viewHolder.image.setImageResource(object.getTypeResource());


            return convertView;
        }
    }
}

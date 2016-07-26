package org.davidcampelo.post;


import android.app.ListFragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDBAdapter;
import org.davidcampelo.post.utils.Constants;

import java.util.ArrayList;


/**
 * A simple {@link ListFragment} subclass.
 */
public class PublicOpenSpaceListFragment extends ListFragment {

    private ArrayList<PublicOpenSpace> list;
    private PublicOpenSpaceListAdapter listAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PublicOpenSpaceDBAdapter dbAdapter = new PublicOpenSpaceDBAdapter(getActivity());
        dbAdapter.open();
        list = dbAdapter.getAll();
        dbAdapter.close();

        listAdapter = new PublicOpenSpaceListAdapter(getActivity(), list);

        setListAdapter(listAdapter);

//        getListView().setDivider(ContextCompat.getDrawable(getActivity(), android.R.color.holo_blue_bright));
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

        PublicOpenSpace object = (PublicOpenSpace) getListAdapter().getItem(position);
        launchActivity(object, Constants.FragmentAction.VIEW);

    }

    @Override

    public boolean onContextItemSelected(MenuItem item) {
        // selected item
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        PublicOpenSpace object = (PublicOpenSpace) getListAdapter().getItem(info.position);
        switch (item.getItemId()){
            case R.id.fragment_public_open_space_list_menu_edit:
                launchActivity( object, Constants.FragmentAction.EDIT );
                return true;
            case R.id.fragment_public_open_space_list_menu_delete:
                // delete from DB
                PublicOpenSpaceDBAdapter dbAdapter = new PublicOpenSpaceDBAdapter(getActivity());
                dbAdapter.open();
                dbAdapter.delete(object);

                // refresh list
//                notes.clear();
//                notes.addAll(dbAdapter.getAllNotes());
//                adapter.notifyDataSetChanged();
                listAdapter.remove(object);
                listAdapter.notifyDataSetChanged();

                dbAdapter.close();
                return true;
        }


        return super.onContextItemSelected(item);

    }

    private void launchActivity(PublicOpenSpace object, Constants.FragmentAction action) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);

        intent.putExtra(Constants.INTENT_ID_EXTRA, object.id);
        intent.putExtra(Constants.INTENT_ACTION_EXTRA, action);

        startActivity(intent);

    }
}

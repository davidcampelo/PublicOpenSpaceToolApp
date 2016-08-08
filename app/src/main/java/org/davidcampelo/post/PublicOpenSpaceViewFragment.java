package org.davidcampelo.post;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDAO;
import org.davidcampelo.post.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 * {@deprecated}
 */
public class PublicOpenSpaceViewFragment extends Fragment {


    private PublicOpenSpace object;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.fragment_public_open_space_view, container, false);

        TextView nameTextView = (TextView) fragmentLayout.findViewById(R.id.viewItemName);
        TextView addressTextView = (TextView) fragmentLayout.findViewById(R.id.viewItemAddress);

        Intent intent = getActivity().getIntent();
        long id = intent.getLongExtra(Constants.INTENT_ID_EXTRA, 0);

        // get references to components

        // fill data and components
        PublicOpenSpaceDAO dbAdapter = new PublicOpenSpaceDAO(getActivity());
        dbAdapter.open();
        object = dbAdapter.get(id);
        dbAdapter.close();

        nameTextView.setText(object.name);
//        addressTextView.setText(object.address);

        return fragmentLayout;
    }

}

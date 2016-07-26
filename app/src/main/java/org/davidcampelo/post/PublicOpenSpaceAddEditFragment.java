package org.davidcampelo.post;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.davidcampelo.post.model.PublicOpenSpace;
import org.davidcampelo.post.model.PublicOpenSpaceDBAdapter;
import org.davidcampelo.post.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class PublicOpenSpaceAddEditFragment extends Fragment {

    private PublicOpenSpace object;
    private ImageButton imageButton;
    private EditText nameEditText;
    private EditText addressEditText;
    private Button saveButton;

    private AlertDialog saveButtonDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent intent = getActivity().getIntent();
        long id = intent.getLongExtra(Constants.INTENT_ID_EXTRA, 0);

        // get references to components
        View fragmentLayout = inflater.inflate(R.layout.fragment_public_open_space_add_edit, container, false);
        nameEditText = (EditText) fragmentLayout.findViewById(R.id.addEditItemName);
        addressEditText = (EditText) fragmentLayout.findViewById(R.id.addEditItemAddress);
        saveButton = (Button) fragmentLayout.findViewById(R.id.addEditButtonSave);

        // fill data and components
        PublicOpenSpaceDBAdapter dbAdapter = new PublicOpenSpaceDBAdapter(getActivity());
        dbAdapter.open();
        object = dbAdapter.get(id);
        dbAdapter.close();

        nameEditText.setText(object.name);
        addressEditText.setText(object.address);

        buildSaveButtonDialog();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButtonDialog.show();
            }
        });

        return fragmentLayout;
    }

    private void buildSaveButtonDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Save note");
        builder.setMessage("Are you sure you want to save this Note?");
        builder.setPositiveButton("Yes, save it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // adjust VO fields
                object.name = nameEditText.getText() + "";
                object.address = addressEditText.getText() + "";

                if (object.id == 0){ // if (id == 0) we're gonna insert it
                    PublicOpenSpaceDBAdapter dbAdapter = new PublicOpenSpaceDBAdapter(getActivity());
                    dbAdapter.open();
                    object = dbAdapter.insert(object);
                    dbAdapter.close();

                }
                else { // just update it
                    PublicOpenSpaceDBAdapter dbAdapter = new PublicOpenSpaceDBAdapter(getActivity());
                    dbAdapter.open();
                    dbAdapter.update(object);
                    dbAdapter.close();
                }

                // redirect to main (list) activity
                //startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        builder.setNegativeButton("No, don't save!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        saveButtonDialog = builder.create();
    }

}

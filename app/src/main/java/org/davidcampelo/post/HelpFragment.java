package org.davidcampelo.post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class HelpFragment extends Fragment {

    Button seeManualsButton;

    public HelpFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_help);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_help, container, false);
        seeManualsButton = (Button) fragmentLayout.findViewById(R.id.fragmentHelpSeeManualsButton);
        seeManualsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Not available right now, sorry...", Toast.LENGTH_LONG).show();
            }
        });

        return fragmentLayout;
    }

}

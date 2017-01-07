package org.davidcampelo.post;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ManualsFragment extends Fragment {

    TextView aboutVersion;
    Button seeManualsButton;

    public ManualsFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_about);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_about, container, false);
        aboutVersion = (TextView)fragmentLayout.findViewById(R.id.fragmentAboutVersion);
        seeManualsButton = (Button) fragmentLayout.findViewById(R.id.fragmentAboutSeeManualsButton);
        seeManualsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeManuals();
            }
        });
        aboutVersion.setText("v"+BuildConfig.VERSION_NAME);
        return fragmentLayout;
    }

    private void seeManuals() {
        File fileBrochure = new File(Environment.getExternalStorageDirectory() + "/" + "POST_Manual.pdf");
        if (!fileBrochure.exists())
        {
            CopyAssetsbrochure();
        }

        /** PDF reader code */
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "POST_Manual.pdf");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try
        {
            getActivity().getApplicationContext().startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
        }
    }

    //method to write the PDFs file to sd card
    private void CopyAssetsbrochure() {
        AssetManager assetManager = getActivity().getAssets();
        InputStream in = null;
        try {
            in = assetManager.open("POST_Manual.pdf");
            OutputStream out = null;
            out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/POST_Manual.pdf");
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}

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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


    public class ManualsFragment extends Fragment {

    WebView manuals;

    public ManualsFragment() {
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_POST_manual);

        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_about_manuals    , container, false);

        manuals = (WebView) fragmentLayout.findViewById(R.id.fragmentManualsWebView);
        manuals.getSettings().setJavaScriptEnabled(true);
//        loadManuals();
        manuals.loadUrl("file:///android_asset/POST_Manual.pdf");

        return fragmentLayout;
    }

    private void loadManuals() {
        File fileBrochure = new File(Environment.getExternalStorageDirectory() + "/" + "POST_Manual.pdf");
        if (!fileBrochure.exists()) {
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
            }        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}

package org.davidcampelo.post;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer,  new MosaicFragment(), "MosaicFragment")
                .commit();

        setTitle(R.string.app_name);

    }

}

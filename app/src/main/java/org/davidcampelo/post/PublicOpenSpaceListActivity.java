package org.davidcampelo.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.davidcampelo.post.utils.Constants;


public class PublicOpenSpaceListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_open_space_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddPublicOpenSpace);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicOpenSpaceListActivity.this, PublicOpenSpaceAddEditActivity.class);
                intent.putExtra(Constants.INTENT_ACTION_EXTRA, Constants.FragmentAction.ADD);
                startActivity(intent);
            }
        });

    }
}

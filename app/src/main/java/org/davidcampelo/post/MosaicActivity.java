package org.davidcampelo.post;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MosaicActivity extends AppCompatActivity {

    ImageButton imageButtonNewProject,
                imageButtonOpenProject,
                imageButtonSettings,
                imageButtonExportData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosaic);

        imageButtonNewProject = (ImageButton) findViewById(R.id.imageButtonNewProject);
        imageButtonOpenProject = (ImageButton) findViewById(R.id.imageButtonOpenProject);
        imageButtonSettings = (ImageButton) findViewById(R.id.imageButtonSettings);
        imageButtonExportData = (ImageButton) findViewById(R.id.imageButtonExportData);

        imageButtonOpenProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MosaicActivity.this, MainActivity.class));
            }
        });
    }
}

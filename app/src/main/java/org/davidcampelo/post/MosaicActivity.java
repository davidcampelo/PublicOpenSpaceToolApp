package org.davidcampelo.post;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import org.davidcampelo.post.utils.Data;

public class MosaicActivity extends AppCompatActivity {

    ImageButton imageButtonNewProject,
                imageButtonOpenProject,
                imageButtonSettings,
                imageButtonExportData;
    AlertDialog resetDialogObject;

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

        // TODO that's a poor hack!!!
        imageButtonExportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetDialogObject.show();
            }
        });


        buildResetDialog();

    }
    private void buildResetDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_reset_dialog_title);
        builder.setMessage(R.string.action_reset_dialog_question);
        builder.setPositiveButton(R.string.action_reset_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Data.populateDatabase(MosaicActivity.this, getResources());
                Data.clearDatabase(MosaicActivity.this);
            }
        });
        builder.setNegativeButton(R.string.action_reset_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        resetDialogObject = builder.create();
    }
}

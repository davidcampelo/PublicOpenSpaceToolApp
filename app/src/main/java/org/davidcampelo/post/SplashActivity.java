package org.davidcampelo.post;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.QuestionDAO;
import org.davidcampelo.post.utils.Data;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkDatabase();

        showInstructionsAndGo();
    }

    private void showInstructionsAndGo() {

        // Main dialog with app usage instructions
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        final String activity_splash_dialog_app_instructions_status = getString(R.string.activity_splash_dialog_app_instructions_status);
        boolean dialog_status = sharedPreferences.getBoolean(activity_splash_dialog_app_instructions_status, false);
        if (!dialog_status) {
            // Set dialog contents
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_instructions, null);
            ((TextView)dialogView.findViewById(R.id.dialogInstructionsText)).setText(getString(R.string.activity_splash_dialog_app_instructions_text));
            ((CheckBox)dialogView.findViewById(R.id.dialogInstructionsCheckbox)).setText(getString(R.string.activity_splash_dialog_app_instructions_checkbox));

            //build the dialog
            new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setPositiveButton(getString(R.string.activity_splash_dialog_app_instructions_button),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,int which) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(activity_splash_dialog_app_instructions_status,
                                            ((CheckBox)dialogView.findViewById(R.id.dialogInstructionsCheckbox)).isChecked());
                                    editor.commit();
                                    dialog.dismiss();

                                    // show MainActivity, get the things going! :)
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }).create().show();

        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


    private void checkDatabase() {
        QuestionDAO questionDAO = new QuestionDAO(this);
        questionDAO.open();
        OptionDAO optionDAO = new OptionDAO(this);
        optionDAO.open();
        if (!questionDAO.isPopulated() || !optionDAO.isPopulated()){
            Log.e(this.getClass().getName(), ">>>>>>>>>>>>>>>>>>>>>>>>>> Database empty! ");

            // XXX hacking to HINT
            // dropping table
            Data.clearDatabase(this);
            Data.populateDatabaseFromXML(this, this.getResources());

        }

        questionDAO.close();
        optionDAO.close();
    }
}
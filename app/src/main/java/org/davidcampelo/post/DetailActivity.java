package org.davidcampelo.post;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.davidcampelo.post.utils.Constants;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch ((Constants.FragmentAction)getIntent().getSerializableExtra(Constants.INTENT_ACTION_EXTRA)) {
            case VIEW:
                fragmentTransaction.add(R.id.detailActivityContainer,  new PublicOpenSpaceViewFragment(), "VIEW_FRAGMENT");
                setTitle(R.string.title_view_public_open_space_fragment);
                break;

            case EDIT:
                fragmentTransaction.add(R.id.detailActivityContainer, new PublicOpenSpaceAddEditFragment(), "EDIT_FRAGMENT");
                setTitle(R.string.title_edit_public_open_space_fragment);
                break;

            case ADD:
                fragmentTransaction.add(R.id.detailActivityContainer, new PublicOpenSpaceAddEditFragment(), "ADD_FRAGMENT");
                setTitle(R.string.title_add_public_open_space_fragment);
                break;
        }
        fragmentTransaction.commit();

    }
}

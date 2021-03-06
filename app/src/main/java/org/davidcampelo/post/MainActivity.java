package org.davidcampelo.post;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.davidcampelo.post.model.OptionDAO;
import org.davidcampelo.post.model.QuestionDAO;
import org.davidcampelo.post.utils.Data;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        // Mosaic fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.  mainContainer, new MosaicFragment(), "MosaicFragment")
                .commit();

        // action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        mDrawerList = (ListView) findViewById(R.id.mainLeftDrawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        //mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
//        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        String[] menuList = getResources().getStringArray(R.array.menu_titles);
        for (int i = menuList.length; --i>=0;) {
            menuList[i] = menuList[i].toUpperCase();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.drawer_layout_simple_list, android.R.id.text1, menuList);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Fragment fragment;
                switch (position) {
                    case 0 : // new project
                        fragment = new ProjectAddEditFragment();
                        break;
                    case 1 : // list projects
                        fragment = new ProjectListFragment();
                        break;
                    case 2 : // export data
                        fragment = new ExportDataFragment();
                        break;
                    case 3 : // settings
                        fragment = new SettingsFragment();
                        break;
                    case 4 : // about
                        fragment = new AboutFragment();
                        break;
                    case 5 : // help
                        fragment = new HelpFragment();
                        break;
                    default:
                        fragment = null;
                }

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainContainer, fragment)
                            .addToBackStack("")
                            .commit();
                }

                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        if (!canAccessLocation()) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, 0);
        }
    }
      private static final String[] INITIAL_PERMS={
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private boolean canAccessLocation() {
        return(hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private boolean hasPermission(String perm) {
            return(PackageManager.PERMISSION_GRANTED==ActivityCompat.checkSelfPermission(this,perm));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    long lastBackPressTime = System.currentTimeMillis();
    @Override
    public void onBackPressed() {

        // initialize variables
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // check to see if stack is empty
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            fragmentTransaction.commit();
        }
        else {
            if ( System.currentTimeMillis() - lastBackPressTime < 4000) {
                lastBackPressTime = System.currentTimeMillis();
            } else{
                super.onBackPressed();
            }
        }
    }
}

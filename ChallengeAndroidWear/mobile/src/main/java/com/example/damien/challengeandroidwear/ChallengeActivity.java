package com.example.damien.challengeandroidwear;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.damien.challengeandroidwear.designerfinger.DesignerFingerActivity;
import com.example.damien.challengeandroidwear.freefeature.FreeFeatureActivity;
import com.example.damien.challengeandroidwear.searchinstagramtags.SearchTagsActivity;

public class ChallengeActivity extends ActionBarActivity {

    private static FragmentManager fragmentManager;
    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Toolbar mToolbar;
    private String[] mNavigationDrawerItems;
    private int actualPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerItems = getResources().getStringArray(R.array.navigation_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        mListView = (ListView) findViewById(R.id.list_item_drawer);
        mListView.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mNavigationDrawerItems));
        mListView.setOnItemClickListener(new DrawerItemClickListener());

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            selectItem(actualPosition);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectItem(actualPosition);
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        actualPosition = position;
        switch (position) {
            case 0:
                fragment = new SearchTagsActivity();
                break;
            case 1:
                fragment = new DesignerFingerActivity();
                break;
            case 2:
                fragment = new FreeFeatureActivity();
                break;
            default:
                break;
        }
        args.putInt("num_index", position);
        if (fragment != null) {
            fragment.setArguments(args);
        }
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        mListView.setItemChecked(position, true);

        mDrawerLayout.closeDrawer(mListView);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}

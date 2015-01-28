package com.example.damien.challengeandroidwear;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.damien.challengeandroidwear.designerfinger.DesignerFingerActivity;
import com.example.damien.challengeandroidwear.freefeature.FreeFeatureActivity;
import com.example.damien.challengeandroidwear.searchinstagramtags.SearchTagsActivity;

public class ChallengeActivity extends TabActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_challenge);

        TabHost tabHost = getTabHost();

        //first feature tab
        TabHost.TabSpec searchTags = tabHost.newTabSpec("Search Tags");
        searchTags.setIndicator("Search Tags");
        Intent stIntent = new Intent(this, SearchTagsActivity.class);
        searchTags.setContent(stIntent);

        //second feature tab
        TabHost.TabSpec designerFinger = tabHost.newTabSpec("Designer Finger");
        designerFinger.setIndicator("Designer Finger");
        Intent dfIntent = new Intent(this, DesignerFingerActivity.class);
        designerFinger.setContent(dfIntent);

        //third feature tab
        TabHost.TabSpec freeFeature = tabHost.newTabSpec("Free Feature");
        freeFeature.setIndicator("Free Feature");
        Intent ffIntent = new Intent(this, FreeFeatureActivity.class);
        freeFeature.setContent(ffIntent);

        tabHost.addTab(searchTags);
        tabHost.addTab(designerFinger);
        tabHost.addTab(freeFeature);
    }

}

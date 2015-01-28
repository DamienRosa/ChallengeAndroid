package com.example.damien.challengeandroidwear.freefeature;

import android.app.Activity;
import android.os.Bundle;

import com.example.damien.challengeandroidwear.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class FreeFeatureActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "UnCMI49DZrDWYsxqtHKOg728V";
    private static final String TWITTER_SECRET = "Kh9jW4SEDoDOVHXAVJf8QwZyah44nYxj1sOSNF8wXuzvzDdiUd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_free_feature);
    }

}

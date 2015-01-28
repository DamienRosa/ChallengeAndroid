package com.example.damien.challengeandroidwear;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.List;

public class WearActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DataApi.DataListener {

    private static final String TAG = WearActivity.class.getSimpleName();

    private TextView mUsername;
    private TextView mTextDescription;
    private ImageView mImageView;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);

        mUsername = (TextView) findViewById(R.id.username_sent_text_view);
        mTextDescription = (TextView) findViewById(R.id.text_sent_text_view);
        mImageView = (ImageView) findViewById(R.id.image_sent_image);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        //listen data layer and change UI with new objects
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                final DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                final String username = dataMapItem.getDataMap().getString("username");
                final String text = dataMapItem.getDataMap().getString("description");
                Asset assetImage = dataMapItem.getDataMap().getAsset("url_image");
                final Bitmap bitmap = loadBitmapFromAsset(assetImage);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mUsername.setText(username);
                        mTextDescription.setText(text);
                        mImageView.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    //convert asset to bitmap
    private Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        return BitmapFactory.decodeStream(assetInputStream);
    }


}

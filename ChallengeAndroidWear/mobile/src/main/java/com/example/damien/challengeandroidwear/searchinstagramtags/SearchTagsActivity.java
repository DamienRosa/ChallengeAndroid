package com.example.damien.challengeandroidwear.searchinstagramtags;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.damien.challengeandroidwear.R;
import com.example.damien.challengeandroidwear.searchinstagramtags.LazyImageLoader.ImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SearchTagsActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SearchTagsActivity.class.getSimpleName();

    private static final int COUNT = 20;
    private static final String CLIENT_ID = "da4641a8d7d94fa1b771cdd438143773";
    private static final String TAGR = "fcporto";
    private static final String WEARABLE_DATA_PATH = "/wearable_data";
    private static String REQUEST_URL = null;
    private Button mConnectInstagram;
    private ListView mListInstaObjects;
    private CustomListAdapter mListAdapterObjects;
    private GoogleApiClient mGoogleClient;

    private String mActualPagination = "";
    private ArrayList<InstaObject> importedObjects;

    private ProgressDialog mProgressDialog;

    //convert bitmap to asset
    private static Asset toAsset(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        } finally {
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tags);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");

        mGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mListInstaObjects = (ListView) findViewById(R.id.scrollable_list_view);
        mListInstaObjects.setOnScrollListener(new EndlessScroll());
        mListInstaObjects.setOnItemClickListener(new OnItemClickObject());

        mConnectInstagram = (Button) findViewById(R.id.connect_button);
        mConnectInstagram.setOnClickListener(new OnClickConnectInstragram());

        REQUEST_URL = "https://api.instagram.com/v1/tags/" + TAGR +
                "/media/recent?client_id=" + CLIENT_ID + "&count=" + COUNT;

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleClient.connect();
    }

    @Override
    protected void onDestroy() {
        mListAdapterObjects.imageLoader.clearCache();
        mListInstaObjects.setAdapter(null);
        mGoogleClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void loadListObjects(ArrayList<InstaObject> newlist) {
        for (InstaObject aNewlist : newlist) {
            importedObjects.add(aNewlist);
        }
        mListAdapterObjects.notifyDataSetChanged();
        mProgressDialog.dismiss();
    }

    private class OnClickConnectInstragram implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            importedObjects = new ArrayList<>();
            mListAdapterObjects = new CustomListAdapter(getApplicationContext(), importedObjects);
            mListInstaObjects.setAdapter(mListAdapterObjects);
            //Request from URL
            GetRequestItemsTask getItemsTask = new GetRequestItemsTask();
            getItemsTask.execute(REQUEST_URL);
        }
    }

    private class GetRequestItemsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return ConnectionURL.RequestWithURL(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            InstagramParserTask parserTask = new InstagramParserTask();
            parserTask.execute(s);
        }
    }

    private class InstagramParserTask extends AsyncTask<String, Void, PaginationObject> {
        @Override
        protected PaginationObject doInBackground(String... strings) {
            JSONObject jsonObject;
            PaginationObject itemList = null;

            try {
                jsonObject = new JSONObject(strings[0]);
                JSONObject metaObject = jsonObject.getJSONObject("meta");
                int code = metaObject.getInt("code");
                if (code == 200) {
                    InstagramParse parser = new InstagramParse();
                    itemList = parser.parse(jsonObject);
                } else {
                    Log.e(TAG, code + ": " + metaObject.getString("error_message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(PaginationObject instaObjHM) {
            super.onPostExecute(instaObjHM);
            mActualPagination = instaObjHM.getNextURL();
            loadListObjects(instaObjHM.getListObjects());
        }
    }

    private class EndlessScroll implements AbsListView.OnScrollListener {

        private int previousCount = 0;
        private boolean loading = true;

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //caso o total seja 0 é porque não tem mais itens
            if (totalItemCount < previousCount) {
                previousCount = totalItemCount;
                if (totalItemCount == 0) {
                    loading = true;
                }
            }

            //caso já  tenha feito load e ainda não estiver no fim do scroll
            if (loading && (totalItemCount > previousCount)) {
                loading = false;
                previousCount = totalItemCount;
            }

            //caso estiver no fim do scroll
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 2)) {
                loading = true;
                GetRequestItemsTask getItemsTask = new GetRequestItemsTask();
                getItemsTask.execute(mActualPagination);
            }
        }
    }

    //onClickItem send dataMap
    private class OnItemClickObject implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DataMap dataMap = new DataMap();

            String username = ((InstaObject) mListAdapterObjects.getItem(position)).getUsername();
            String description = ((InstaObject) mListAdapterObjects.getItem(position)).getText();
            String urlImage = ((InstaObject) mListAdapterObjects.getItem(position)).getImageURL();

            dataMap.putString("username", username);
            dataMap.putString("description", description);

            ImageLoader image = new ImageLoader(getApplicationContext());
            Bitmap bitmap = image.getBitmap(urlImage);
            Asset asset = toAsset(bitmap);
            dataMap.putAsset("url_image", asset);

            SendToDataLayerTask sendToLayer = new SendToDataLayerTask(WEARABLE_DATA_PATH, dataMap);
            sendToLayer.execute();
        }
    }

    // send data map to data layer
    private class SendToDataLayerTask extends AsyncTask<Void, Void, Void> {

        private String dataPath;
        private DataMap dataMap;

        public SendToDataLayerTask(String dataPath, DataMap dataMap) {
            this.dataPath = dataPath;
            this.dataMap = dataMap;
        }

        @Override
        protected Void doInBackground(Void... params) {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleClient).await();
            for (Node node : nodes.getNodes()) {
                PutDataMapRequest putData = PutDataMapRequest.create(dataPath);
                putData.getDataMap().putAll(dataMap);
                PutDataRequest putRequest = putData.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleClient, putRequest).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "Success sent to: " + node.getDisplayName());
                } else {
                    Log.v(TAG, "ERROR: failed to send DataMap");
                }
            }
            return null;
        }
    }
}

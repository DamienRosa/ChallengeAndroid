package com.example.damien.challengeandroidwear.searchinstagramtags;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.damien.challengeandroidwear.R;
import com.example.damien.challengeandroidwear.searchinstagramtags.LazyImageLoader.ImageLoader;
import com.example.damien.challengeandroidwear.searchinstagramtags.instagram.InstagramObject;
import com.example.damien.challengeandroidwear.searchinstagramtags.instagram.InstagramParse;
import com.example.damien.challengeandroidwear.searchinstagramtags.instagram.PaginationObject;
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

public class SearchTagsActivity extends Fragment {

    private static final String TAG = SearchTagsActivity.class.getSimpleName();
    private static String REQUEST_URL = null;

    private Button mRequestButton;
    private ListView mListView;
    private CustomListAdapter mListAdapter;
    private GoogleApiClient mGoogleClient;
    private ProgressDialog mProgressDialog;

    private String actualPagination = "";
    private ArrayList<InstagramObject> listImportedObjects;

    public SearchTagsActivity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_search_tags, container, false);
        String text = String.format("Search Tags");
        getActivity().setTitle(text);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Loading...");

        mGoogleClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .build();

        mListView = (ListView) rootView.findViewById(R.id.scrollable_list_view);
        mListView.setOnScrollListener(new EndlessScroll());
        mListView.setOnItemClickListener(new OnItemClickObject());

        mRequestButton = (Button) rootView.findViewById(R.id.connect_button);
        mRequestButton.setOnClickListener(new OnClickConnectInstragram());

        REQUEST_URL = "https://api.instagram.com/v1/tags/" + SearchConstants.TAGR +
                "/media/recent?client_id=" + SearchConstants.CLIENT_ID + "&count=" + SearchConstants.COUNT;

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleClient.connect();
    }

    @Override
    public void onDestroy() {
        mListView.setAdapter(null);
        mGoogleClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        if (null != mGoogleClient && mGoogleClient.isConnected()) {
            mGoogleClient.disconnect();
        }
        super.onStop();
    }

    private void loadListObjects(ArrayList<InstagramObject> newlist) {
        for (InstagramObject aNewlist : newlist) {
            listImportedObjects.add(aNewlist);
        }
        mListAdapter.notifyDataSetChanged();
        mProgressDialog.dismiss();
    }

    //convert bitmap to asset
    private Asset toAsset(Bitmap bitmap) {
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

    private class OnClickConnectInstragram implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            listImportedObjects = new ArrayList<>();
            mListAdapter = new CustomListAdapter(getActivity(), listImportedObjects);
            mListView.setAdapter(mListAdapter);
            //Request from URL
            GetRequestItemsTask getItemsTask = new GetRequestItemsTask();
            getItemsTask.execute(REQUEST_URL);
        }
    }

    //task to make connection and request parse
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

    //parse instaobjects
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
            //actualize url pagination and add new objects to list
            actualPagination = instaObjHM.getNextURL();
            loadListObjects(instaObjHM.getListObjects());
        }
    }

    private class EndlessScroll implements AbsListView.OnScrollListener {

        private int previousCount = 0;
        private boolean loading = true;

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
            //nothing
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
                getItemsTask.execute(actualPagination);
            }
        }
    }

    //onClickItem send dataMap
    private class OnItemClickObject implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DataMap dataMap = new DataMap();

            String username = ((InstagramObject) mListAdapter.getItem(position)).getUsername();
            String description = ((InstagramObject) mListAdapter.getItem(position)).getText();
            String urlImage = ((InstagramObject) mListAdapter.getItem(position)).getImageURL();

            dataMap.putString("username", username);
            dataMap.putString("description", description);

            ImageLoader image = new ImageLoader(getActivity());
            Bitmap bitmap = image.getBitmap(urlImage);
            Asset asset = toAsset(bitmap);
            dataMap.putAsset("url_image", asset);

            SendToDataLayerTask sendToLayer = new SendToDataLayerTask(SearchConstants.WEARABLE_DATA_PATH, dataMap);
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
            if (nodes.getNodes().size() == 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "No Wearable near", Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
            for (Node node : nodes.getNodes()) {
                PutDataMapRequest putData = PutDataMapRequest.create(dataPath);
                putData.getDataMap().putAll(dataMap);
                PutDataRequest putRequest = putData.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleClient, putRequest).await();
                if (result.getStatus().isSuccess()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Data Sent to Wearable", Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.v(TAG, "Success sent to: " + node.getDisplayName());
                } else {
                    Log.v(TAG, "ERROR: failed to send DataMap");
                }
            }
            return null;
        }
    }
}

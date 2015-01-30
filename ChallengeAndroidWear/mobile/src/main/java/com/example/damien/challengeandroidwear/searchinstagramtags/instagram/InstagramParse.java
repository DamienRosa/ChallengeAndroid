package com.example.damien.challengeandroidwear.searchinstagramtags.instagram;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InstagramParse {

    private static final String TAG = InstagramParse.class.getSimpleName();

    public PaginationObject parse(JSONObject jsonObject) {

        PaginationObject resultData = null;
        ArrayList<InstagramObject> listObj = new ArrayList<>();
        JSONArray jData;

        try {

            jData = jsonObject.getJSONArray("data");
            for (int i = 0; i < jData.length(); i++) {
                JSONObject jAData = (JSONObject) jData.get(i);

                JSONObject imageObj = jAData.getJSONObject("images");
                String imageURL = imageObj.getJSONObject("low_resolution").getString("url");

                JSONObject userObj = jAData.getJSONObject("user");
                String userName = userObj.getString("full_name");

                String text = "";
                if (!jAData.isNull("caption")) {
                    JSONObject captionObj = jAData.getJSONObject("caption");
                    text = captionObj.getString("text");
                }

                InstagramObject instObj = new InstagramObject(userName, text, imageURL);
                listObj.add(instObj);
            }

            String nextURL = "";
            if (!jsonObject.isNull("pagination")) {
                nextURL = jsonObject.getJSONObject("pagination").getString("next_url");
            }

            resultData = new PaginationObject(nextURL, listObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultData;
    }
}

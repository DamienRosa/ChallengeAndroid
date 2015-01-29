package com.example.damien.challengeandroidwear.searchinstagramtags.instagram;

import java.util.ArrayList;

public class PaginationObject {
    private String nextURL;
    private ArrayList<InstagramObject> listObjects;

    public PaginationObject(String nextURL, ArrayList<InstagramObject> listObj) {
        this.nextURL = nextURL;
        this.listObjects = listObj;
    }

    public String getNextURL() {
        return nextURL;
    }

    public ArrayList<InstagramObject> getListObjects() {
        return listObjects;
    }
}

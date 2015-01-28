package com.example.damien.challengeandroidwear.searchinstagramtags;

import java.util.ArrayList;

public class PaginationObject {
    private String nextURL;
    private ArrayList<InstaObject> listObjects;

    public PaginationObject(String nextURL, ArrayList<InstaObject> listObj) {
        this.nextURL = nextURL;
        listObjects = listObj;
    }

    public String getNextURL() {
        return nextURL;
    }

    public ArrayList<InstaObject> getListObjects() {
        return listObjects;
    }
}

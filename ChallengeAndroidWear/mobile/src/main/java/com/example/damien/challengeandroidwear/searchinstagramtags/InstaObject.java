package com.example.damien.challengeandroidwear.searchinstagramtags;

public class InstaObject {

    private String username;
    private String text;
    private String imageURL;

    public InstaObject(String userName, String text, String imageURL) {
        this.username = userName;
        this.text = text;
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public String getImageURL() {
        return imageURL;
    }
}

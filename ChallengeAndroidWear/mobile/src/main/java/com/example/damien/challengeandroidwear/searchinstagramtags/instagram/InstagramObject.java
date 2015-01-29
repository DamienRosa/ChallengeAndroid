package com.example.damien.challengeandroidwear.searchinstagramtags.instagram;

public class InstagramObject {

    private String username;
    private String text;
    private String imageURL;

    public InstagramObject(String userName, String text, String imageURL) {
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

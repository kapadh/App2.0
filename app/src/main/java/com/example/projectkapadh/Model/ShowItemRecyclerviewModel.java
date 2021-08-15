package com.example.projectkapadh.Model;

public class ShowItemRecyclerviewModel {

    String ImageUrl,Title;


    ShowItemRecyclerviewModel(){

    }

    public ShowItemRecyclerviewModel(String imageUrl, String title) {
        ImageUrl = imageUrl;
        Title = title;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}

package com.example.projectkapadh.Model;

public class ConsultancyModel {

    String Title, ImageUrl;

    ConsultancyModel(){

    }

    public ConsultancyModel(String title, String imageUrl) {
        Title = title;
        ImageUrl = imageUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}

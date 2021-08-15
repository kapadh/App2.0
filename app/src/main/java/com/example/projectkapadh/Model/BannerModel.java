package com.example.projectkapadh.Model;

public class BannerModel {

    private String BannerImage;

    BannerModel(){

    }

    public BannerModel(String bannerImage) {
        BannerImage = bannerImage;
    }

    public String getBannerImage() {
        return BannerImage;
    }

    public void setBannerImage(String bannerImage) {
        BannerImage = bannerImage;
    }
}

package com.example.projectkapadh.Model;

public class ServiceProviderListItemModel {

    String SpImage, SpName, SpRatings, SpFee, Description, SpUID;
    boolean Visibility;

    ServiceProviderListItemModel(){

    }

    public ServiceProviderListItemModel(String spImage, String spName, String spRatings, String spFee, String description, String spUID, boolean visibility) {
        SpImage = spImage;
        SpName = spName;
        SpRatings = spRatings;
        SpFee = spFee;
        Description = description;
        SpUID = spUID;
        Visibility = visibility;
    }

    public String getSpImage() {
        return SpImage;
    }

    public void setSpImage(String spImage) {
        SpImage = spImage;
    }

    public String getSpName() {
        return SpName;
    }

    public void setSpName(String spName) {
        SpName = spName;
    }

    public String getSpRatings() {
        return SpRatings;
    }

    public void setSpRatings(String spRatings) {
        SpRatings = spRatings;
    }

    public String getSpFee() {
        return SpFee;
    }

    public void setSpFee(String spFee) {
        SpFee = spFee;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSpUID() {
        return SpUID;
    }

    public void setSpUID(String spUID) {
        SpUID = spUID;
    }

    public boolean isVisibility() {
        return Visibility;
    }

    public void setVisibility(boolean visibility) {
        Visibility = visibility;
    }
}

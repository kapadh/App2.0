package com.example.projectkapadh.Model;

public class HistoryRecyclerviewItemModel {

    String SpName, SpFee, OrderStatus, SpTiming, OrderDateTime, SpRating, SpSubCategory;
    
    HistoryRecyclerviewItemModel(){
        
    }

    public HistoryRecyclerviewItemModel(String spName, String spFee, String orderStatus, String spTiming, String orderDateTime, String spRating, String spSubCategory) {
        SpName = spName;
        SpFee = spFee;
        OrderStatus = orderStatus;
        SpTiming = spTiming;
        OrderDateTime = orderDateTime;
        SpRating = spRating;
        SpSubCategory = spSubCategory;
    }

    public String getSpName() {
        return SpName;
    }

    public void setSpName(String spName) {
        SpName = spName;
    }

    public String getSpFee() {
        return SpFee;
    }

    public void setSpFee(String spFee) {
        SpFee = spFee;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getSpTiming() {
        return SpTiming;
    }

    public void setSpTiming(String spTiming) {
        SpTiming = spTiming;
    }

    public String getOrderDateTime() {
        return OrderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        OrderDateTime = orderDateTime;
    }

    public String getSpRating() {
        return SpRating;
    }

    public void setSpRating(String spRating) {
        SpRating = spRating;
    }

    public String getSpSubCategory() {
        return SpSubCategory;
    }

    public void setSpSubCategory(String spSubCategory) {
        SpSubCategory = spSubCategory;
    }
}

package com.example.projectkapadh.Model;

import java.util.List;

public class HomepageModel {
    public static final int BANNER_SLIDER = 0;
    public static final int GRID_PRODUCT_VIEW = 1;

    private int type;

    ///////////////Banner
    private List<BannerModel> bannerModelList;
    public HomepageModel(int type, List<BannerModel> bannerModelList) {
        this.type = type;
        this.bannerModelList = bannerModelList;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public List<BannerModel> getBannerModelList() {
        return bannerModelList;
    }
    public void setBannerModelList(List<BannerModel> bannerModelList) {
        this.bannerModelList = bannerModelList;
    }
    //////////Banner

    /////////Grid Product

    private String title;
    private List<GridProductLayoutModel> gridProductLayoutModelList;

    public HomepageModel(int type, String title, List<GridProductLayoutModel> gridProductLayoutModelList) {
        this.type = type;
        this.title = title;
        this.gridProductLayoutModelList = gridProductLayoutModelList;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<GridProductLayoutModel> getGridProductLayoutModelList() {
        return gridProductLayoutModelList;
    }
    public void setGridProductLayoutModelList(List<GridProductLayoutModel> horizontalProductModelList) {
        this.gridProductLayoutModelList = horizontalProductModelList;
    }
    /////////Gris Product
}

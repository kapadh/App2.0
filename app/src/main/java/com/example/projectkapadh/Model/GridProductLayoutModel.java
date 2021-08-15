package com.example.projectkapadh.Model;

public class GridProductLayoutModel {

    private int ConsultancyImage;
    private String ConsultancyTitle;

    GridProductLayoutModel(){

    }

    public GridProductLayoutModel(int ConsultancyImage, String ConsultancyTitle) {
        this.ConsultancyImage = ConsultancyImage;
        this.ConsultancyTitle = ConsultancyTitle;
    }

    public int getConsultancyImage() {
        return ConsultancyImage;
    }

    public void setConsultancyImage(int ConsultancyImage) {
        this.ConsultancyImage = ConsultancyImage;
    }

    public String getConsultancyTitle() {
        return ConsultancyTitle;
    }

    public void setConsultancyTitle(String ConsultancyTitle) {
        this.ConsultancyTitle = ConsultancyTitle;
    }
}

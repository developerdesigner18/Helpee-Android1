package com.dds.helpee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Number
{
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("english_name")
    @Expose
    private String englishName;
    @SerializedName("french_name")
    @Expose
    private String frenchName;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("police_no")
    @Expose
    private String policeNo;
    @SerializedName("rescue_no")
    @Expose
    private String rescueNo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getFrenchName() {
        return frenchName;
    }

    public void setFrenchName(String frenchName) {
        this.frenchName = frenchName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPoliceNo() {
        return policeNo;
    }

    public void setPoliceNo(String policeNo) {
        this.policeNo = policeNo;
    }

    public String getRescueNo() {
        return rescueNo;
    }

    public void setRescueNo(String rescueNo) {
        this.rescueNo = rescueNo;
    }
}

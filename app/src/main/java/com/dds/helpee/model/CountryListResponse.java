package com.dds.helpee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryListResponse
{
    @SerializedName("success")
    @Expose int success ;

    @SerializedName("number")
    @Expose
    private List<Number> numberList = null;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public List<Number> getNumberList() {
        return numberList;
    }

    public void setNumberList(List<Number> numberList) {
        this.numberList = numberList;
    }
}

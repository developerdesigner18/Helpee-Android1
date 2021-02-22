package com.dds.helpee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response
{
    @SerializedName("success")
    @Expose int success ;

    @SerializedName("message")
    @Expose Object message;

    @SerializedName("number")
    @Expose Number number;



    @SerializedName("emergency")
    @Expose
    private List<Emergency> emergency = null;

    @SerializedName("alerts")
    @Expose
    private List<Alerts> alerts = null;

    @SerializedName("data")
    @Expose
    Data data;


    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public List<Alerts> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alerts> alerts) {
        this.alerts = alerts;
    }

    public List<Emergency> getEmergency() {
        return emergency;
    }

    public void setEmergency(List<Emergency> emergency) {
        this.emergency = emergency;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

//    public Number getNumber() {
//        return number;
//    }

//    public void setNumber(Number number) {
//        this.number = number;
//    }

    //    @SerializedName("message")
//    Message objMessage;

    public int getSuccess() {
        return success;
    }

//    public Message getObjMessage() {
//        return objMessage;
//    }

//    public void setObjMessage(Message objMessage) {
//        this.objMessage = objMessage;
//    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public Object getMessage()
    {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }
}

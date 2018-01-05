package com.tintin.mat.winecellar.ResponseModels;

/**
 * Created by Mat & Audrey on 04/01/2018.
 */

import com.google.gson.annotations.SerializedName;

/**
 * Created by putuguna on 1/24/2017.
 */

public class InsertCaveResponseModel {
    @SerializedName("success")
    private int status;
    @SerializedName("message")
    private String message;

    public InsertCaveResponseModel(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public InsertCaveResponseModel() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
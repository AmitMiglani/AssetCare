package com.toyota.assetcare.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Status implements Serializable {

    @SerializedName("name")
    public String name;
    @SerializedName("id")
    public String id;

    public void Status(String id) {
        this.id = id;
    }
}
package com.toyota.assetcare.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shubham_Aggarwal03 on 8/2/2018.
 */

public class AssetResponse {
    @SerializedName("assets")
    public List<Asset> assetList;

    public AssetResponse() {
        assetList = new ArrayList<Asset>();
    }
}

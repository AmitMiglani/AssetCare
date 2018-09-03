package com.toyota.assetcare.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shubham_Aggarwal03 on 8/2/2018.
 */

public class Asset implements Serializable {

    @SerializedName("key")
    private String issueId;


    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    private Fields fields;

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }
}

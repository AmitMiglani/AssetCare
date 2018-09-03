package com.toyota.assetcare.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Fields implements Serializable {

    public void Fields(Status status, ProjectDetails project, String summary, String description) {
        this.status = status;
        this.project = project;
        this.summary = summary;
        this.description = description;
    }
    public String summary;

    public Status status;

    public ProjectDetails project;

    public IssueType issuetype;

    public Reporter reporter;

    public CommentResponse comment;

    public String description;

    public AssigneeDetails assignee;

    //required for creating issue
    @SerializedName("customfield_10010")
    public String customfield;


}
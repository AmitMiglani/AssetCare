package com.toyota.assetcare.textrecognition.model;


import java.util.List;

public class OCR {
    public boolean isAngleDetected;
    public float textAngle;
    public String orientation;
    public String language;
    public List<Region> regions;

    public OCR() {
    }
}

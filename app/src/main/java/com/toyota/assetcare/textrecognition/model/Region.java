package com.toyota.assetcare.textrecognition.model;

import java.util.List;

/**
 * Created by Shubham_Aggarwal03 on 8/22/2018.
 */

public class Region {
    public String boundingBox;
    public List<Line> lines;

    public Region() {
    }
}

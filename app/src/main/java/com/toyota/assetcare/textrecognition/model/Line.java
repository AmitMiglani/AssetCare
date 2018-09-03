package com.toyota.assetcare.textrecognition.model;


import java.util.List;

public class Line {
    public boolean isVertical;
    public List<Word> words;
    public String boundingBox;

    public Line() {
    }
}

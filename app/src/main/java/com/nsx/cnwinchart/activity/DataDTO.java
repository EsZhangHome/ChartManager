package com.nsx.cnwinchart.activity;

import java.util.List;

public class DataDTO {
    private List<List<Double>> weight;
    private List<List<Double>> height;
    private List<List<Double>> waistline;

    public List<List<Double>> getWeight() {
        return weight;
    }

    public void setWeight(List<List<Double>> weight) {
        this.weight = weight;
    }

    public List<List<Double>> getHeight() {
        return height;
    }

    public void setHeight(List<List<Double>> height) {
        this.height = height;
    }

    public List<List<Double>> getWaistline() {
        return waistline;
    }

    public void setWaistline(List<List<Double>> waistline) {
        this.waistline = waistline;
    }
}

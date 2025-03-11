package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Miles {
    @JsonProperty("estimated_diameter_min")
    private double estimatedDiameterMin;
    @JsonProperty("estimated_diameter_max")
    private double getEstimatedDiameterMax;

    public double getEstimatedDiameterMin() {
        return estimatedDiameterMin;
    }

    public void setEstimatedDiameterMin(double estimatedDiameterMin) {
        this.estimatedDiameterMin = estimatedDiameterMin;
    }

    public double getGetEstimatedDiameterMax() {
        return getEstimatedDiameterMax;
    }

    public void setGetEstimatedDiameterMax(double getEstimatedDiameterMax) {
        this.getEstimatedDiameterMax = getEstimatedDiameterMax;
    }
}

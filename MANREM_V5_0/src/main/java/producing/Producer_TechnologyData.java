package producing;

import selling.*;


public class Producer_TechnologyData {
    private String technology;
    private String fuelType;
    private double minCap;
    private double maxCap;

    public Producer_TechnologyData() {
        technology = "";
        fuelType = "";
        minCap = -1;
        maxCap = -1;
    }
    
    public Producer_TechnologyData(String technology, String fuelType, double minCap, double maxCap) {
        this.technology = technology;
        this.fuelType = fuelType;
        this.minCap = minCap;
        this.maxCap = maxCap;
    }
    
    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public double getMinCap() {
        return minCap;
    }

    public void setMinCap(double minCap) {
        this.minCap = minCap;
    }

    public double getMaxCap() {
        return maxCap;
    }

    public void setMaxCap(double maxCap) {
        this.maxCap = maxCap;
    }
    
    
}
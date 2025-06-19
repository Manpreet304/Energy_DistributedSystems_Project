package com.example.restapi.dto;

public class TotalEnergyBetweenDates {
    private double totalCommunityProduced;
    private double totalCommunityUsed;
    private double totalGridUsed;

    public TotalEnergyBetweenDates(double totalCommunityProduced, double totalCommunityUsed, double totalGridUsed) {
        this.totalCommunityProduced = totalCommunityProduced;
        this.totalCommunityUsed = totalCommunityUsed;
        this.totalGridUsed = totalGridUsed;
    }

    public double getTotalCommunityProduced() {
        return totalCommunityProduced;
    }

    public void setTotalCommunityProduced(double totalCommunityProduced) {
        this.totalCommunityProduced = totalCommunityProduced;
    }

    public double getTotalCommunityUsed() {
        return totalCommunityUsed;
    }

    public void setTotalCommunityUsed(double totalCommunityUsed) {
        this.totalCommunityUsed = totalCommunityUsed;
    }

    public double getTotalGridUsed() {
        return totalGridUsed;
    }

    @Override
    public String toString() {
        return "TotalEnergyBetweenDates{" +
                "totalCommunityProduced=" + totalCommunityProduced +
                ", totalCommunityUsed=" + totalCommunityUsed +
                ", totalGridUsed=" + totalGridUsed +
                '}';
    }

    public void setTotalGridUsed(double totalGridUsed) {
        this.totalGridUsed = totalGridUsed;
    }
}

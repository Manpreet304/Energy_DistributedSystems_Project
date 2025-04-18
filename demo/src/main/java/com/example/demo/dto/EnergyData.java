package com.example.demo.dto;

import java.util.Date;

public class EnergyData {
    private Date hour;
    private double community_produced;
    private double community_used;
    private double grid_used;

    public EnergyData() {
    }

    public EnergyData(Date hour, double community_produced, double community_used, double grid_used) {
        this.hour = hour;
        this.community_produced = community_produced;
        this.community_used = community_used;
        this.grid_used = grid_used;
    }

    public Date getHour() {
        return hour;
    }

    public void setHour(Date hour) {
        this.hour = hour;
    }

    public double getCommunity_produced() {
        return community_produced;
    }

    public void setCommunity_produced(double community_produced) {
        this.community_produced = community_produced;
    }

    public double getCommunity_used() {
        return community_used;
    }

    public void setCommunity_used(double community_used) {
        this.community_used = community_used;
    }

    public double getGrid_used() {
        return grid_used;
    }

    public void setGrid_used(double grid_used) {
        this.grid_used = grid_used;
    }
}


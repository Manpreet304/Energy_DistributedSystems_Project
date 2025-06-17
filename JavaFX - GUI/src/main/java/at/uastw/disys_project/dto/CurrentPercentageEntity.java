package at.uastw.disys_project.dto;

public class CurrentPercentageEntity {
    private String hour;
    private double community_depleted;
    private double grid_portion;

    public double getCommunity_depleted() {
        return community_depleted;
    }

    public double getGrid_portion() {
        return grid_portion;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setCommunity_depleted(double community_depleted) {
        this.community_depleted = community_depleted;
    }

    public void setGrid_portion(double grid_portion) {
        this.grid_portion = grid_portion;
    }

    public String getHour() {
        return hour;
    }}

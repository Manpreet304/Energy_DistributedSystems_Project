package at.uastw.disys_project.dto;

public class CurrentPercentageEntity {
    private String hour;
    private double communityDepleted;
    private double gridPortion;

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setCommunityDepleted(double community_depleted) {
        this.communityDepleted = community_depleted;
    }

    public void setGridPortion(double grid_portion) {
        this.gridPortion = grid_portion;
    }

    public String getHour() {
        return hour;
    }}

package at.uastw.disys_project.dto;

public class TotalEnergyBetweenDates {
    private double totalCommunityProduced;
    private double totalCommunityUsed;
    private double totalGridUsed;

    public double getTotalCommunityProduced() { return totalCommunityProduced; }
    public double getTotalCommunityUsed()    { return totalCommunityUsed; }
    public double getTotalGridUsed()         { return totalGridUsed; }
}

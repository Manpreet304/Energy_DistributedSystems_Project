package at.uastw.disys_project;

import java.util.List;

public class EnergyResponse {
    private EnergyData current;
    private List<EnergyData> historical;

    public EnergyData getCurrent() {
        return current;
    }

    public List<EnergyData> getHistorical() {
        return historical;
    }
}

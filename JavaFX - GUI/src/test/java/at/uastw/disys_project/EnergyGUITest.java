package at.uastw.disys_project;

import org.junit.jupiter.api.Test;

class EnergyGUITest {

    @Test
    void canCreateEnergyGUIInstance() {
        EnergyGUI gui = new EnergyGUI();
        assert gui != null;
        System.out.println("EnergyGUI Instance created.");
    }
}

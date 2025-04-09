module at.uastw.disys_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.uastw.disys_project to javafx.fxml;
    exports at.uastw.disys_project;
}
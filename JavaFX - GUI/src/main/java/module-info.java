module at.uastw.disys_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;
    requires com.google.gson;

    opens at.uastw.disys_project to javafx.fxml
            , com.google.gson;


    exports at.uastw.disys_project;
}

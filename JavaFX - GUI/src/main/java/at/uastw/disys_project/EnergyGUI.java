package at.uastw.disys_project;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EnergyGUI {

    @FXML private Label labelCommunityPercent;
    @FXML private Label labelGridPercent;
    @FXML private DatePicker startDate;
    @FXML private TextField startTime;
    @FXML private DatePicker endDate;
    @FXML private TextField endTime;
    @FXML private Label labelProduced;
    @FXML private Label labelUsed;
    @FXML private Label labelGridUsed;

    @FXML
    public void initialize() {
        // REFRESH BUTTON
        // Holt aktuelle Werte von /energy/current
        labelCommunityPercent.setText("");
        labelGridPercent.setText("");
        labelProduced.setText("");
        labelUsed.setText("");
        labelGridUsed.setText("");

        HttpClient client = HttpClient.newBuilder().build();

        // Refresh Button
        // Muss in FXML als fx:id="btnRefresh" vorhanden sein und mit fx:controller verknüpft
        // Oder durch z.B. Button-Handler aus FXML über @FXML public void handleRefreshButton()...

        // SHOW DATA Button (historische Werte)
        // Beispielhaft über separate Methode aufgerufen
    }

    @FXML
    protected void onRefreshClick() {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/current"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            String community = body.split("communityDepleted\":")[1].split(",")[0];
            String grid = body.split("gridPortion\":")[1].split("}")[0];

            labelCommunityPercent.setText(community + " % aus Community-Strom verbraucht");
            labelGridPercent.setText(grid + " % aus öffentlichem Netz");

        } catch (Exception e) {
            System.err.println("Fehler bei Refresh: " + e.getMessage());
        }
    }

    @FXML
    protected void onShowDataClick() {
        try {
            // Eingaben prüfen
            if (startDate.getValue() == null || endDate.getValue() == null ||
                    startTime.getText().isEmpty() || endTime.getText().isEmpty()) {
                System.out.println("Bitte alle Zeitangaben ausfüllen!");
                return;
            }

            LocalDate startD = startDate.getValue();
            LocalTime startT = LocalTime.parse(startTime.getText());
            LocalDate endD = endDate.getValue();
            LocalTime endT = LocalTime.parse(endTime.getText());

            String dateStart = startD.atTime(startT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            String dateEnd = endD.atTime(endT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

            String url = "http://localhost:8080/energy/historical?dateStart=" + dateStart + "&dateEnd=" + dateEnd;

            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();




            String produced = body.split("totalCommunityProduced\":")[1].split(",")[0];
            String used = body.split("totalCommunityUsed\":")[1].split(",")[0];
            String grid = body.split("totalGridUsed\":")[1].split("}")[0];

            labelProduced.setText(produced + " kWh");
            labelUsed.setText(used + " kWh");
            labelGridUsed.setText(grid + " kWh");

        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der Daten: " + e.getMessage());
        }
    }
}
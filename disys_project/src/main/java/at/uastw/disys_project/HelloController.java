package at.uastw.disys_project;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HelloController {

    @FXML private Label labelCommunityPercent;
    @FXML private Label labelGridPercent;
    @FXML private DatePicker startDate;
    @FXML private TextField startTime;
    @FXML private DatePicker endDate;
    @FXML private TextField endTime;
    @FXML private Label labelProduced;
    @FXML private Label labelUsed;
    @FXML private Label labelGridUsed;

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    protected void handleRefresh() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/energy/current"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        EnergyData[] dataArray = gson.fromJson(json, EnergyData[].class);

                        if (dataArray.length > 0) {
                            EnergyData latest = dataArray[dataArray.length - 1];

                            double used = latest.getCommunity_used();
                            double total = used + latest.getGrid_used();
                            double percentUsed = (total > 0) ? (used / total) * 100 : 0;
                            double percentGrid = 100 - percentUsed;

                            Platform.runLater(() -> {
                                labelCommunityPercent.setText(String.format("%.2f%% used", percentUsed));
                                labelGridPercent.setText(String.format("%.2f%%", percentGrid));
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @FXML
    protected void handleShowData() {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        String startTimeText = startTime.getText();
        String endTimeText = endTime.getText();

        if (start == null || end == null || startTimeText.isEmpty() || endTimeText.isEmpty()) {
            System.out.println("Bitte Datum und Uhrzeit eingeben.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String dateStart = start.atTime(LocalTime.parse(startTimeText)).format(formatter);
        String dateEnd = end.atTime(LocalTime.parse(endTimeText)).format(formatter);

        String url = "http://localhost:8080/energy/historical?dateStart=" + dateStart + "&dateEnd=" + dateEnd;
        System.out.println("Sende Request an:\n" + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        EnergyData[] dataArray = gson.fromJson(json, EnergyData[].class);

                        if (dataArray.length == 0) {
                            System.out.println("Keine Daten aus /historical erhalten.");
                            return;
                        }

                        double produced = 0, used = 0, grid = 0;

                        for (EnergyData d : dataArray) {
                            produced += d.getCommunity_produced();
                            used += d.getCommunity_used();
                            grid += d.getGrid_used();
                        }

                        double finalProduced = produced;
                        double finalUsed = used;
                        double finalGrid = grid;

                        Platform.runLater(() -> {
                            labelProduced.setText(String.format("%.3f kWh", finalProduced));
                            labelUsed.setText(String.format("%.3f kWh", finalUsed));
                            labelGridUsed.setText(String.format("%.3f kWh", finalGrid));
                        });

                    } catch (Exception e) {
                        System.out.println("Fehler beim Parsen von /historical:");
                        e.printStackTrace();
                    }
                });
    }
}

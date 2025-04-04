import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MaintenanceLogger {
    private Stage stage;

    public MaintenanceLogger(Stage stage) {
        this.stage = stage;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label title = new Label("All Maintenance Logs:");
        ListView<String> listView = new ListView<>();

        try (Connection con = DBUtils.establishConnection()) {
            String query = "SELECT equipment, description, date FROM maintenance";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String equipment = rs.getString("equipment");
                String description = rs.getString("description");
                String date = rs.getDate("date").toString();
                String record = equipment + " | " + description + " | " + date;
                listView.getItems().add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
            listView.getItems().add("Error loading data.");
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> new Dashboard(stage).initializeComponents());

        layout.getChildren().addAll(title, listView, backBtn);

        stage.setScene(new Scene(layout, 500, 400));
        stage.setTitle("Maintenance Logger");
        stage.show();
    }
}

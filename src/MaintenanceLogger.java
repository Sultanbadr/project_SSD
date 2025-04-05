import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class MaintenanceLogger {
    private Stage stage;

    public MaintenanceLogger(Stage stage) {
        this.stage = stage;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label title = new Label(" All Maintenance Logs:");
        ListView<String> listView = new ListView<>();

        try (Connection con = DBUtils.establishConnection()) {
            String query = "SELECT equipment, description, date FROM maintenance ORDER BY date DESC";
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
            listView.getItems().add("⚠Error loading data.");
        }

        Button addBtn = new Button(" Add Log");
        addBtn.setOnAction(e -> showAddLogDialog(listView));

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> new Dashboard(stage).initializeComponents());

        layout.getChildren().addAll(title, listView, addBtn, backBtn);

        stage.setScene(new Scene(layout, 500, 400));
        stage.setTitle("Maintenance Logger");
        stage.show();
    }

    private void showAddLogDialog(ListView<String> listView) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Maintenance Log");

        Label equipmentLabel = new Label("Equipment:");
        TextField equipmentField = new TextField();

        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();

        VBox dialogVBox = new VBox(10, equipmentLabel, equipmentField, descriptionLabel, descriptionField);
        dialogVBox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(dialogVBox);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String equipment = equipmentField.getText().trim();
                String description = descriptionField.getText().trim();
                if (!equipment.isEmpty() && !description.isEmpty()) {
                    try (Connection con = DBUtils.establishConnection()) {
                        String insert = "INSERT INTO maintenance (equipment, description, date) VALUES (?, ?, CURDATE())";
                        PreparedStatement stmt = con.prepareStatement(insert);
                        stmt.setString(1, equipment);
                        stmt.setString(2, description);
                        stmt.executeUpdate();

                        String record = equipment + " | " + description + " | " + java.time.LocalDate.now();
                        listView.getItems().add(0, record);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
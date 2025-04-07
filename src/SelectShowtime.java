import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectShowtime {
    private Stage stage;
    private List<Integer> showtimeIds = new ArrayList<>(); // List to keep track of selected seats
    private ListView<String> showtimeList;

    public SelectShowtime(Stage stage) {
        this.stage = stage;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label label = new Label("Select a Showtime");
        showtimeList = new ListView<>();
        Button backBtn = new Button("Back");

        layout.getChildren().addAll(label, showtimeList, backBtn);

        try (Connection con = DBUtils.establishConnection()) {
            String query = "SELECT s.id, m.title, s.room_number, s.show_date, s.show_time " + "FROM showtimes s JOIN movies m ON s.movie_id = m.id ORDER BY s.show_date DESC";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            showtimeIds.clear();
            showtimeList.getItems().clear();

            while (rs.next()) {
                int showtimeId = rs.getInt("id");
                String movieTitle = rs.getString("title");
                String room = rs.getString("room_number");
                String date = rs.getDate("show_date").toString();
                String time = rs.getTime("show_time").toString();

                String display = movieTitle + " | Room " + room + " | " + date + " " + time;

                showtimeIds.add(showtimeId);
                showtimeList.getItems().add(display);
            }

            showtimeList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    int index = showtimeList.getSelectionModel().getSelectedIndex();
                    if (index >= 0) {
                        int selectedShowtimeId = showtimeIds.get(index);
                        new SeatSelection(stage, selectedShowtimeId).initializeComponents();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showtimeList.getItems().add("Error loading showtimes.");
            showAlert("Database Error", "Error fetching showtimes from the database.");
        }


        backBtn.setOnAction(e -> new Dashboard(stage).initializeComponents());

        stage.setScene(new Scene(layout, 500, 400));
        stage.setTitle("Select Showtime");
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
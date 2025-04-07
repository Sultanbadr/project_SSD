import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatSelection {
    private Stage stage;
    private final int showtimeId;
    private List<CheckBox> seatCheckBoxes = new ArrayList<>(); // List to keep track of selected seats

    public SeatSelection(Stage stage, int showtimeId) {
        this.stage = stage;
        this.showtimeId = showtimeId;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label label = new Label("Select Seats for Showtime ID: " + showtimeId);
        Button confirmBtn = new Button("Confirm Selection");
        confirmBtn.setDisable(true); // Initially disabled

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(10);
        seatGrid.setVgap(10);

        // Fetch available seats from the database
        try (Connection con = DBUtils.establishConnection()) {
            String query = "SELECT seat_number, is_booked FROM seats WHERE showtime_id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, showtimeId);
            ResultSet rs = stmt.executeQuery();

            int row = 0;
            int col = 0;

            while (rs.next()) {
                int seatNumber = rs.getInt("seat_number");
                boolean isBooked = rs.getBoolean("is_booked");

                // Display seat number and availability
                CheckBox seat = new CheckBox("Seat " + seatNumber);
                seat.setDisable(isBooked); // Disable checkbox if the seat is booked
                if (!isBooked) {
                    seat.setOnAction(e -> handleSeatSelection(seat)); // Enable seat selection if available
                }

                seatGrid.add(seat, col, row);
                seatCheckBoxes.add(seat);

                col++;
                if (col >= 5) { // Start a new row after 5 seats
                    col = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not fetch seat availability.");
        }

        // Enable confirm button when at least one seat is selected
        for (CheckBox seat : seatCheckBoxes) {
            seat.selectedProperty().addListener((observable, oldValue, newValue) -> {
                // Enable the confirm button only when at least one seat is selected
                boolean anySeatSelected = seatCheckBoxes.stream().anyMatch(CheckBox::isSelected);
                confirmBtn.setDisable(!anySeatSelected);
            });
        }

        // Confirm button action
        confirmBtn.setOnAction(e -> {
            // Collect selected seats and proceed with confirmation
            List<String> selectedSeats = new ArrayList<>();
            for (CheckBox seat : seatCheckBoxes) {
                if (seat.isSelected()) {
                    selectedSeats.add(seat.getText());
                }
            }

            if (selectedSeats.isEmpty()) {
                showAlert("No seats selected", "Please select at least one seat.");
            } else {
                try (Connection con = DBUtils.establishConnection()) {
                    String updateQuery = "UPDATE seats SET is_booked = ? WHERE seat_number = ? AND showtime_id = ?";
                    PreparedStatement updateStmt = con.prepareStatement(updateQuery);

                    for (String seatText : selectedSeats) {
                        int seatNumber = Integer.parseInt(seatText.split(" ")[1]);
                        updateStmt.setBoolean(1, true);
                        updateStmt.setInt(2, seatNumber);
                        updateStmt.setInt(3, showtimeId);
                        updateStmt.executeUpdate();
                    }

                    showAlert("Booking confirmed", "Seats " + String.join(", ", selectedSeats) + " have been successfully booked.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Failed to confirm booking.");
                }
            }
        });

        // Add all components to Layout
        layout.getChildren().addAll(label, seatGrid, confirmBtn);

        // Set up the scene and show the stage
        stage.setScene(new Scene(layout, 300, 300));
        stage.setTitle("Seat Selection");
        stage.show();
    }

    // Simple alert to display messages to the user
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Handle seat selection and check if any seat is selected
    private void handleSeatSelection(CheckBox seat) {
        System.out.println(seat.getText() + " selected: " + seat.isSelected());
    }
}

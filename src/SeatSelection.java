import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeatSelection {
    private Stage stage;
    private int showtimeId;
    private List<Seat> selectedSeats = new ArrayList<>(); // List to keep track of selected seats

    public SeatSelection(Stage stage, int showtimeId) {
        this.stage = stage;
        this.showtimeId = showtimeId;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label titleLabel = new Label("Select Your Seats");
        layout.getChildren().add(titleLabel);

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(10);
        seatGrid.setVgap(10);

        List<Seat> seats = fetchAvailableSeats();

        int row = 0;
        int col = 0;
        for (Seat seat : seats) {
            Button seatBtn = new Button(seat.getSeatNumber());
            seatBtn.setMinSize(50, 50);
            if (seat.isBooked()) {
                seatBtn.setStyle("-fx-background-color: red;");
                seatBtn.setDisable(true); // Booked seats are disabled
            } else {
                seatBtn.setStyle("-fx-background-color: green;");
                seatBtn.setOnAction(e -> {
                    // Toggle seat selection
                    if (selectedSeats.contains(seat)) {
                        selectedSeats.remove(seat);
                        seatBtn.setStyle("-fx-background-color: green;");
                    } else {
                        selectedSeats.add(seat);
                        seatBtn.setStyle("-fx-background-color: blue;");
                    }
                });
            }

            seatGrid.add(seatBtn, col, row);
            col++;
            if (col == 5) {  // Assuming 5 seats per row
                col = 0;
                row++;
            }
        }

        // Confirm button
        Button confirmBtn = new Button("Confirm Selection");
        confirmBtn.setOnAction(e -> confirmBooking());

        // Back button to return to the dashboard
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> new Dashboard(stage).initializeComponents());

        // Add the buttons and grid to the layout
        layout.getChildren().addAll(seatGrid, confirmBtn, backBtn);

        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Seat Selection");
        stage.show();
    }

    private List<Seat> fetchAvailableSeats() {
        List<Seat> seats = new ArrayList<>();
        try (Connection con = DBUtils.establishConnection()) {
            String query = "SELECT seat_number, is_booked FROM seats WHERE showtime_id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, showtimeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String seatNumber = rs.getString("seat_number");
                boolean isBooked = rs.getBoolean("is_booked");
                seats.add(new Seat(seatNumber, isBooked));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    private void confirmBooking() {
        if (selectedSeats.isEmpty()) {
            // If no seat is selected, show an alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select at least one seat.");
            alert.showAndWait();
            return;
        }

        // Perform booking (e.g., mark seats as booked in the database)
        for (Seat seat : selectedSeats) {
            bookSeat(seat);  // Update the database for the selected seat
        }

        // Show confirmation alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Your seats have been booked successfully.");
        alert.showAndWait();

        // Return to dashboard
        new Dashboard(stage).initializeComponents();
    }

    private void bookSeat(Seat seat) {
        try (Connection con = DBUtils.establishConnection()) {
            String query = "UPDATE seats SET is_booked = true WHERE seat_number = ? AND showtime_id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, seat.getSeatNumber());
            stmt.setInt(2, showtimeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class Seat {
        private String seatNumber;
        private boolean isBooked;

        public Seat(String seatNumber, boolean isBooked) {
            this.seatNumber = seatNumber;
            this.isBooked = isBooked;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public boolean isBooked() {
            return isBooked;
        }
    }
}

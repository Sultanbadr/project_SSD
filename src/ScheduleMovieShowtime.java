import java.sql.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScheduleMovieShowtime {
    private Stage stage;

    public ScheduleMovieShowtime(Stage stage) {
        this.stage = stage;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TextField titleField = new TextField();
        titleField.setPromptText("Movie Title");

        TextField genreField = new TextField();
        genreField.setPromptText("Genre");

        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");

        TextField roomField = new TextField();
        roomField.setPromptText("Room Number");

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        timeField.setPromptText("Show Time (HH:MM)");

        Button submitBtn = new Button("Schedule");
        Button backBtn = new Button("Back");
        Label statusLabel = new Label();

        submitBtn.setOnAction(e -> {
            try (Connection con = DBUtils.establishConnection()) {
                String insertMovie = "INSERT INTO movies (title, genre, duration) VALUES (?, ?, ?)";
                PreparedStatement movieStmt = con.prepareStatement(insertMovie, Statement.RETURN_GENERATED_KEYS);
                movieStmt.setString(1, titleField.getText());
                movieStmt.setString(2, genreField.getText());
                movieStmt.setInt(3, Integer.parseInt(durationField.getText()));
                movieStmt.executeUpdate();

                ResultSet rs = movieStmt.getGeneratedKeys();
                int movieId = 0;
                if (rs.next()) movieId = rs.getInt(1);

                String insertShowtime = "INSERT INTO showtimes (movie_id, room_number, show_date, show_time) VALUES (?, ?, ?, ?)";
                PreparedStatement showStmt = con.prepareStatement(insertShowtime);
                showStmt.setInt(1, movieId);
                showStmt.setInt(2, Integer.parseInt(roomField.getText()));
                showStmt.setDate(3, java.sql.Date.valueOf(datePicker.getValue()));
                showStmt.setTime(4, java.sql.Time.valueOf(timeField.getText() + ":00"));
                showStmt.executeUpdate();

                statusLabel.setText("Movie scheduled successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> new Dashboard(stage).initializeComponents());

        layout.getChildren().addAll(titleField, genreField, durationField, roomField, datePicker, timeField, submitBtn, backBtn, statusLabel);
        stage.setScene(new Scene(layout, 400, 400));
        stage.setTitle("Schedule Movie Showtime");
        stage.show();
    }
}
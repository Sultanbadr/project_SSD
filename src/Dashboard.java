import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {
    private Stage stage;

    public Dashboard(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Button scheduleBtn = new Button("Schedule Movie Showtime");
        Button seatsBtn = new Button("Seat Selection");
        Button analyticsBtn = new Button("Movie Analytics");
        Button maintenanceBtn = new Button("Maintenance Logger");
        Button backBtn = new Button("Logout");

        scheduleBtn.setOnAction(e -> new ScheduleMovieShowtime(stage).initializeComponents());
        seatsBtn.setOnAction(e -> new SeatSelection(stage).initializeComponents());
        analyticsBtn.setOnAction(e -> new MovieAnalytics(stage).initializeComponents());
        maintenanceBtn.setOnAction(e -> new MaintenanceLogger(stage).initializeComponents());
        backBtn.setOnAction(e -> new UserLogin(stage).initializeComponents());

        layout.getChildren().addAll(scheduleBtn, seatsBtn, analyticsBtn, maintenanceBtn, backBtn);
        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Dashboard");
        stage.show();
    }
}
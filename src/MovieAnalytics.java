import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MovieAnalytics {
    private Stage stage;

    public MovieAnalytics(Stage stage) {
        this.stage = stage;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().add(new Label("Movie Analytics Screen Coming Soon"));

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> new Dashboard(stage).initializeComponents());
        layout.getChildren().add(backBtn);

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Movie Analytics");
        stage.show();
    }
}


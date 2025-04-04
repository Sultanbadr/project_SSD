import java.sql.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserLogin {
    private Stage stage;
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();

    public UserLogin(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label loginLabel = new Label("User Login");
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Sign In");
        Button signupButton = new Button("Create Account");
        Label statusLabel = new Label();

        loginButton.setOnAction(this::authenticate);
        signupButton.setOnAction(e -> {
            UserSignup signup = new UserSignup(stage);
            signup.initializeComponents();
        });

        layout.getChildren().addAll(
                loginLabel,
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                loginButton,
                signupButton,
                statusLabel
        );

        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("User Login");
        stage.show();
    }

    private void authenticate(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Missing Fields", "Please enter both username and password.");
            return;
        }

        try (Connection con = DBUtils.establishConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Dashboard dashboard = new Dashboard(stage);
                dashboard.initializeComponents();
            } else {
                showAlert("Authentication Failed", "Invalid username or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not connect to the database.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserSignup {
    private Stage stage;

    public UserSignup(Stage stage) {
        this.stage = stage;
    }

    public void initializeComponents() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Button signupButton = new Button("Sign Up");
        Button backButton = new Button("Back");
        Label statusLabel = new Label();

        signupButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
                return;
            }

            try (Connection con = DBUtils.establishConnection()) {
                String insertUser = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement stmt = con.prepareStatement(insertUser);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();

                statusLabel.setText("Account created successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        backButton.setOnAction(e -> {
            UserLogin login = new UserLogin(stage);
            login.initializeComponents();
        });

        layout.getChildren().addAll(
                new Label("Create Account"),
                usernameField, passwordField,
                signupButton, backButton,
                statusLabel
        );
        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("User Sign Up");
        stage.show();
    }
}
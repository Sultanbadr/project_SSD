import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class AddCustomer {
    private Stage stage;

    public AddCustomer(Stage stage) {
        this.stage = stage;
    }

    public void initializeComponents() {
        System.out.println("Initializing AddCustomer Components");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        Button submitBtn = new Button("Add Customer");
        Button backBtn = new Button("Back");
        Label statusLabel = new Label();

        submitBtn.setOnAction(e -> {
            System.out.println("Submit Button Pressed");
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                statusLabel.setText("All fields are required.");
                return;
            }

            try (Connection con = DBUtils.establishConnection()){
                String query = "INSERT INTO customers (name, email, phone_number) VALUES (?, ?, ?)";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    statusLabel.setText("Customer added successfully!");
                } else {
                    statusLabel.setText("Failed to add customer.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> {
            System.out.println("Back Button Pressed");
            new Dashboard(stage).initializeComponents();
        });

        layout.getChildren().addAll(nameField, emailField, phoneField, submitBtn, backBtn, statusLabel);

        stage.setScene(new Scene(layout, 400, 300));
        stage.setTitle("Add Customer");
        stage.show();
    }
}

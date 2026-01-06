
package ui;

import com.mycompany.inventory.Inventory;
import db.DBConnection;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.geometry.Insets;

public class LoginUI {

    private final Scene scene;

    public LoginUI(Inventory app) {

        Label title = new Label("INVENTORY MANAGEMENT APPLICATION");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label userLabel = new Label("USER ID  :");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        TextField username = new TextField();
        username.setPrefSize(220, 35);

        Label passLabel = new Label("PASSWORD  :");
        passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        PasswordField password = new PasswordField();
        password.setPrefSize(220, 35);

        // ---------- GRID ----------
        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(20);
        form.setVgap(15);

        ColumnConstraints col1 = new ColumnConstraints(120);
        ColumnConstraints col2 = new ColumnConstraints(250);
        form.getColumnConstraints().addAll(col1, col2);

        form.add(userLabel, 0, 0);
        form.add(username, 1, 0);
        form.add(passLabel, 0, 1);
        form.add(password, 1, 1);

        Hyperlink changePassword = new Hyperlink("CHANGE PASSWORD");
        changePassword.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        changePassword.setTextFill(Color.BLUE);

        Button loginBtn = new Button("LOGIN");
        loginBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        loginBtn.setPrefSize(160, 45);
        loginBtn.setStyle(
                "-fx-background-color:#6b4a2d;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:30;"
        );

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        loginBtn.setOnAction(e -> {
            errorLabel.setText("");

            String userId = username.getText().trim();
            String pass = password.getText().trim();

            if (userId.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("User ID and Password cannot be empty");
                return;
            }

            if (authenticate(userId, pass)) {
                app.showDashboard();
            } else {
                errorLabel.setText("Invalid User ID or Password");
                password.clear();
            }
        });

        changePassword.setOnAction(e -> showAdminAuthUI());

        VBox root = new VBox(25, title, form, changePassword, loginBtn, errorLabel);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#d2b48c;");

        scene = new Scene(root, 1024, 768);
    }

    public Scene getScene() {
        return scene;
    }

    // ================= LOGIN AUTH =================
    private boolean authenticate(String userId, String pass) {
        String sql = """
            SELECT 1
            FROM user
            WHERE TRIM(user_id) = ?
            AND TRIM(password) = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("LOGIN DB: " + con.getCatalog());

            ps.setString(1, userId);
            ps.setString(2, pass);

            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= ADMIN AUTH =================
    private void showAdminAuthUI() {
        Stage stage = new Stage();
        stage.setTitle("Authorize Password Change");

        Label info = new Label("Dean / HOD / Manager Authorization");
        info.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField userField = new TextField();
        userField.setPromptText("User ID");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button authBtn = new Button("Authorize");
        Label status = new Label();
        status.setTextFill(Color.RED);

        authBtn.setOnAction(e -> {
            if (authenticateAdmin(userField.getText().trim(), passField.getText().trim())) {
                stage.close();
                showForgotPasswordUI();
            } else {
                status.setText("Unauthorized access!");
            }
        });

        VBox layout = new VBox(10, info, userField, passField, authBtn, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color:#d2b48c;");

        stage.setScene(new Scene(layout, 350, 280));
        stage.show();
    }

    private boolean authenticateAdmin(String userId, String pass) {
        String sql = """
            SELECT 1
            FROM user u
            JOIN role r ON u.role_id = r.role_id
            WHERE TRIM(u.user_id) = ?
            AND TRIM(u.password) = ?
            AND r.role_name IN ('Dean','HOD','Manager')
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("ADMIN DB: " + con.getCatalog());

            ps.setString(1, userId);
            ps.setString(2, pass);

            return ps.executeQuery().next();

        } catch (Exception e) {
            return false;
        }
    }

    // ================= CHANGE PASSWORD =================
    private void showForgotPasswordUI() {
        Stage stage = new Stage();
        stage.setTitle("Change Password");

        ComboBox<String> users = new ComboBox<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT user_id FROM user");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) users.getItems().add(rs.getString(1));

        } catch (Exception e) {
            e.printStackTrace();
        }

        PasswordField newPass = new PasswordField();
        PasswordField confirm = new PasswordField();

        Button changeBtn = new Button("Change Password");
        Label status = new Label();

        changeBtn.setOnAction(e -> {
            if (users.getValue() == null) {
                status.setText("Select a user!");
                return;
            }
            if (!newPass.getText().equals(confirm.getText())) {
                status.setText("Passwords do not match!");
                return;
            }

            String sql = "UPDATE user SET password = TRIM(?)";

try (Connection con = DBConnection.getConnection();
     PreparedStatement ps = con.prepareStatement(sql)) {

    ps.setString(1, newPass.getText().trim()); // only this one

    int rows = ps.executeUpdate();
    status.setText(rows > 0
            ? "Password updated for ALL users!"
            : "Update failed");

} catch (Exception ex) {
    status.setText("Error occurred");
}

        });

        VBox layout = new VBox(12,
                new Label("Select User"), users,
                new Label("New Password"), newPass,
                new Label("Confirm Password"), confirm,
                changeBtn, status
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color:#d2b48c;");

        stage.setScene(new Scene(layout, 400, 420));
        stage.show();
    }
}

package ui;

import com.mycompany.inventory.Inventory;
import db.DBConnection;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.ColumnConstraints;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.scene.effect.GaussianBlur;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class LoginUI {

    private final Scene scene;
    private String currentUserForPasswordChange; // Added to track user
    
    public LoginUI(Inventory app) {

    Label title = new Label("INVENTORY MANAGEMENT APPLICATION");
    title.setStyle(
        "-fx-font-size: 28px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: #2C3E50;"
    );
    title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 28));
    title.setTextFill(Color.web("#1F3A5F"));
    
    Label userLabel = new Label("USER ID :");
    userLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495E;");
    userLabel.setTextFill(Color.BLACK);

    TextField username = new TextField();
    username.setPromptText("Enter User ID");
    username.setPrefSize(220, 35);

    Label passLabel = new Label("PASSWORD :");
    passLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495E;");
    passLabel.setTextFill(Color.BLACK);
    
    username.setStyle(
    "-fx-background-radius: 6;" +
    "-fx-border-radius: 6;" +
    "-fx-border-color: #1E88E5;" +
    "-fx-border-width: 2;" +
    "-fx-background-color: white;" +
    "-fx-text-fill: black;" +
    "-fx-font-size: 15px;"
);

    PasswordField password = new PasswordField();
    password.setPromptText("Enter Password");
    password.setPrefSize(220, 35);
    
    password.setStyle(
    "-fx-background-radius: 6;" +
    "-fx-border-radius: 6;" +
    "-fx-border-color: #b0b0b0;" +
    "-fx-border-width: 1.5;" +
    "-fx-background-color: white;" +
    "-fx-text-fill: black;" +
    "-fx-font-size: 15px;"
);

    GridPane form = new GridPane();
    form.setAlignment(Pos.CENTER);
    ColumnConstraints col1 = new ColumnConstraints();
    col1.setMinWidth(150);

    ColumnConstraints col2 = new ColumnConstraints();
    col2.setMinWidth(300);

    form.getColumnConstraints().addAll(col1, col2);

    form.setHgap(20);
    form.setVgap(15);
    form.add(userLabel, 0, 0);
    form.add(username, 1, 0);
    form.add(passLabel, 0, 1);
    form.add(password, 1, 1);

    Hyperlink retrievePassword = new Hyperlink("RETRIEVE PASSWORD");
    retrievePassword.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
    retrievePassword.setTextFill(Color.web("#4DA3FF"));
    retrievePassword.setUnderline(false);
    retrievePassword.setVisited(false);
    retrievePassword.setFocusTraversable(false);

    retrievePassword.setStyle(
            "-fx-padding: 0;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;"
    );

    Button loginBtn = new Button("LOGIN");
    loginBtn.setDefaultButton(true);   // 👈 ENTER press karte hi login hoga
    loginBtn.setPrefSize(160, 45);
    loginBtn.setStyle(
    "-fx-background-color: #8B5E34;" +
    "-fx-text-fill: white;" +
    "-fx-font-size: 16px;" +
    "-fx-font-weight: bold;" +
    "-fx-background-radius: 8;" +
    "-fx-border-radius: 8;"
);

    
        loginBtn.setOnMousePressed(e ->
    loginBtn.setStyle(
        "-fx-background-color: #7A4F2A;" +   // slightly darker brown
        "-fx-text-fill: white;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-border-radius: 8;"
    )
);

loginBtn.setOnMouseReleased(e ->
    loginBtn.setStyle(
        "-fx-background-color: #8B5E34;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-border-radius: 8;"
    )
);

    Label errorLabel = new Label();
    errorLabel.setTextFill(Color.RED);

    loginBtn.setOnAction(e -> {
        if (authenticate(username.getText().trim(), password.getText().trim())) {
            app.showDashboard();
        } else {
            errorLabel.setText("Invalid credentials");
        }
    });

    retrievePassword.setOnAction(e -> showAdminAuthUI());

    // ===== BACKGROUND IMAGE VIEW =====
    ImageView background = new ImageView(
            new javafx.scene.image.Image(
                    getClass().getResource("/images/pexels-canmiless-5860937.jpg").toExternalForm()
            )
    );
    background.setPreserveRatio(false);

    // ===== BLUR EFFECT =====
    GaussianBlur blur = new GaussianBlur(15);
    background.setEffect(blur);

    // ===== DARK OVERLAY =====
    Rectangle overlay = new Rectangle();
    overlay.setFill(Color.rgb(0, 0, 0, 0.35)); // 35% dark overlay

    // ===== LOGO =====
    ImageView logo = new ImageView(
            new javafx.scene.image.Image(
                    getClass().getResource("/images/banasthali_logo.jpeg").toExternalForm()
            )
    );
    logo.setFitWidth(140);
    logo.setFitHeight(140);
    logo.setPreserveRatio(false);

    // Create circular clip
    Circle clip = new Circle(70, 70, 70);
    logo.setClip(clip);

    logo.setEffect(new javafx.scene.effect.DropShadow(25, Color.rgb(0,0,0,0.5)));

    // ===== LOGIN BOX =====
    VBox loginBox = new VBox(20, title, form, retrievePassword, loginBtn, errorLabel);
    loginBox.setAlignment(Pos.CENTER);
    loginBox.setPadding(new Insets(30));
    loginBox.setMaxWidth(650);
   loginBox.setStyle(
        "-fx-background-color: rgba(245, 222, 179, 0.92);" +   // soft beige
        "-fx-background-radius: 8;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 35, 0.3, 0, 8);"
);

    // ===== CENTER LAYOUT =====
    VBox centerLayout = new VBox(25, logo, loginBox);
    centerLayout.setAlignment(Pos.CENTER);
    centerLayout.setPadding(new Insets(40));

    // ===== ROOT =====
    StackPane root = new StackPane();
    root.getChildren().addAll(background, overlay, centerLayout);

    scene = new Scene(root, 1024, 768);

    // ===== MAKE BACKGROUND RESPONSIVE =====
    background.fitWidthProperty().bind(scene.widthProperty());
    background.fitHeightProperty().bind(scene.heightProperty());

    overlay.widthProperty().bind(scene.widthProperty());
    overlay.heightProperty().bind(scene.heightProperty());
    }
    public Scene getScene() {
        return scene;
    }

    // ================= AUTH METHODS =================

    private boolean authenticate(String userId, String pass) {

    String sql = """
        SELECT r.role_name
        FROM user u
        JOIN role r ON u.role_id = r.role_id
        WHERE u.user_id=? AND u.password=?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role_name");

                Inventory.setUserRole(role);   // 👈 THIS IS THE IMPORTANT ADDITION
                Inventory.setLoggedUser(userId);

                return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean authenticateAdmin(String userId, String pass) {
        String sql = """
                SELECT 1 FROM user u
                JOIN role r ON u.role_id = r.role_id
                WHERE u.user_id=? AND u.password=?
                AND r.role_name IN ('Dean','HOD','Manager')
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, pass);
            return ps.executeQuery().next();
        } catch (Exception e) {
            return false;
        }
    }

    // ================= ADMIN AUTH =================

    private void showAdminAuthUI() {
        Stage stage = new Stage();

        TextField adminId = new TextField();
        adminId.setPromptText("Admin User ID");

        PasswordField adminPass = new PasswordField();
        adminPass.setPromptText("Admin Password");

        Label status = new Label();

        Button authBtn = new Button("Authorize");

        authBtn.setOnAction(e -> {
            if (authenticateAdmin(adminId.getText().trim(), adminPass.getText().trim())) {
                stage.close();
                showSelectUserUI();
            } else {
                status.setText("Unauthorized");
            }
        });

        VBox layout = new VBox(12,
                new Label("Admin Authorization"),
                adminId, adminPass, authBtn, status
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(new Scene(layout, 300, 250));
        stage.show();
    }

    // ================= SELECT USER =================

    private void showSelectUserUI() {
        Stage stage = new Stage();

        ComboBox<String> users = new ComboBox<>();
        users.setPromptText("Select User ID");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT user_id FROM user");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.getItems().add(rs.getString(1));
            }
        } catch (Exception ignored) {}

        Button next = new Button("Next");

        next.setOnAction(e -> {
            if (users.getValue() != null) {
                stage.close();
                currentUserForPasswordChange = users.getValue(); // Track selected user
                showSecretQuestionUI(users.getValue());
            }
        });

        VBox layout = new VBox(12, new Label("Select User"), users, next);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(new Scene(layout, 300, 220));
        stage.show();
    }

    // ================= SECRET QUESTION =================

    private void showSecretQuestionUI(String userId) {
        Stage stage = new Stage();

        Label heading = new Label("Verify Identity");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        PasswordField currentPass = new PasswordField();
        currentPass.setPromptText("Enter current password");

        Label question = new Label("What is your senior secondary school name?");
        question.setWrapText(true);

        TextField answer = new TextField();
        answer.setPromptText("Enter your answer");

        Label status = new Label();
        status.setTextFill(Color.RED);

        Button verify = new Button("Verify");

        verify.setOnAction(e -> {
            if (!authenticate(userId, currentPass.getText().trim())) {
                status.setText("Wrong current password");
                return;
            }

            if (verifySecretAnswer(userId, answer.getText().trim())) {
                stage.close();
                showChangePasswordUI();
            } else {
                status.setText("Wrong secret answer");
            }
        });

        VBox layout = new VBox(14,
                heading,
                currentPass,
                question,
                answer,
                verify,
                status
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(new Scene(layout, 420, 300));
        stage.show();
    }

    // ================= VERIFY SECRET ANSWER =================

    private boolean verifySecretAnswer(String userId, String answer) {
        String sql = "SELECT 1 FROM user WHERE user_id=? AND secret_answer=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, answer);
            return ps.executeQuery().next();

        } catch (Exception e) {
            return false;
        }
    }

    // ================= PASSWORD CHANGE =================

    private void showChangePasswordUI() {
        Stage stage = new Stage();

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New system password");

        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm new password");

        Label status = new Label();

        Label warningLabel = new Label();
        warningLabel.setTextFill(Color.DARKRED);
        warningLabel.setStyle("-fx-font-weight: bold;");

        Button change = new Button("Update Password");

        change.setOnAction(e -> {
            if (!newPass.getText().equals(confirm.getText())) {
                status.setText("Passwords do not match");
                return;
            }

            String sql = "UPDATE user SET password=? WHERE user_id=?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, newPass.getText().trim());
                ps.setString(2, currentUserForPasswordChange); // Only current user
                ps.executeUpdate();

                warningLabel.setText("Password has been changed successfully for " + currentUserForPasswordChange);
                status.setText("");

            } catch (Exception ex) {
                status.setText("Error updating password");
            }
        });

        VBox layout = new VBox(12,
                new Label("Applies only to the selected user"),
                newPass, confirm,
                change,
                warningLabel,
                status
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(new Scene(layout, 370, 320));
        stage.show();
    }
}

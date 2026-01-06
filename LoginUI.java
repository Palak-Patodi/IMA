package ui;

import com.mycompany.inventory.Inventory;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginUI {

    private final Scene scene;

    public LoginUI(Inventory app) {

        Label title = new Label("INVENTORY MANAGEMENT APPLICATION");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label userLabel = new Label("USERNAME  :");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        TextField username = new TextField();
        username.setPrefWidth(220);

        HBox userRow = new HBox(15, userLabel, username);
        userRow.setAlignment(Pos.CENTER);

        Label passLabel = new Label("PASSWORD  :");
        passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        PasswordField password = new PasswordField();
        password.setPrefWidth(220);

        HBox passRow = new HBox(15, passLabel, password);
        passRow.setAlignment(Pos.CENTER);

        Hyperlink forgot = new Hyperlink("FORGOT PASSWORD?");
        forgot.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        forgot.setTextFill(Color.BLUE);

        Button loginBtn = new Button("LOGIN");
        loginBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        loginBtn.setPrefSize(160, 45);
        loginBtn.setStyle(
                "-fx-background-color:#6b4a2d;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:30;"
        );

        // LOGIN CLICK → DASHBOARD
        loginBtn.setOnAction(e -> app.showDashboard());

        VBox root = new VBox(22, title, userRow, passRow, forgot, loginBtn);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color:#d2b48c;");

        scene = new Scene(root, 1024, 768);
    }

    public Scene getScene() {
        return scene;
    }
}

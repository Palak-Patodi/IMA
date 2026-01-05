package ui;

import com.mycompany.inventory.Inventory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;


public class DashboardUI {

    private final Scene scene;
    private void showLowInventoryAlert(String itemName, int quantity) 
    {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Low Inventory Alert");
        alert.setHeaderText("Low Stock Detected");
        alert.setContentText(
            "Item: " + itemName + "\n" +
            "Available Quantity: " + quantity + "\n\n" +
            "Please restock this item."
        );
        alert.showAndWait();
    }
    
    private void checkLowInventoryFromUser() 
    {
        TextInputDialog itemDialog = new TextInputDialog();
        itemDialog.setTitle("Inventory Input");
        itemDialog.setHeaderText("Enter Item Name");
        itemDialog.setContentText("Item:");

        Optional<String> itemResult = itemDialog.showAndWait();
        if (!itemResult.isPresent()) return;

        String itemName = itemResult.get();

        TextInputDialog qtyDialog = new TextInputDialog();
        qtyDialog.setTitle("Inventory Input");
        qtyDialog.setHeaderText("Enter Available Quantity");
        qtyDialog.setContentText("Quantity:");

        Optional<String> qtyResult = qtyDialog.showAndWait();
        if (!qtyResult.isPresent()) return;

        int quantity;
        try {
            quantity = Integer.parseInt(qtyResult.get());
        } catch (NumberFormatException e) {
            return;
        }

        if (quantity < 10) {
            showLowInventoryAlert(itemName, quantity);
        }
    }

    public DashboardUI(Inventory app) {
        

        // ===== TOP BROWN BAR =====
        Label userManual = new Label("User Manual");
        userManual.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label logout = new Label("Logout");
        logout.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logout.setOnMouseClicked(e -> app.showLogin());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(25, spacer, userManual, logout);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(30, 10, 30, 10));
        topBar.setStyle("-fx-background-color:#d2b48c;");

        // ===== BUTTON GRID =====
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(35);
        grid.setVgap(35);
        grid.setPadding(new Insets(20, 0, 0, 0)); // VERY LESS GAP from bar

        String[] names = {
            "Update", "Issue", "Return",
            "View", "Report", "Budget Analysis",
            "Track Usage", "Request Inventory"
        };

        int index = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (index >= names.length) break;

                Button btn = new Button(names[index++]);
                btn.setOnAction(e -> checkLowInventoryFromUser());
                btn.setFont(Font.font("Arial", FontWeight.NORMAL, 30)); // NOT bold
                btn.setPrefSize(320, 120);
                btn.setStyle(
                    "-fx-background-color:#d2b48c;" +
                    "-fx-background-radius:45;"
                );

                grid.add(btn, c, r);
            }
        }

        // ===== ROOT =====
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(grid);

        scene = new Scene(root, 1024, 768);
    }

    public Scene getScene() {
        return scene;
    }
}

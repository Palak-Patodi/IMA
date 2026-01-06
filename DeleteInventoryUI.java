package ui;

import com.mycompany.inventory.Inventory;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DeleteInventoryUI {

    private final Scene scene;

    public DeleteInventoryUI(Inventory app) {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(60));
        grid.setHgap(50);
        grid.setVgap(40);
        grid.setStyle("-fx-background-color:#d2b48c;");

        Font labelFont = Font.font("Arial", FontWeight.BOLD, 22);

        // Labels
        Label typeLabel = new Label("Product Type ID");
        Label nameLabel = new Label("Product Name");
        Label idLabel = new Label("Product ID");

        typeLabel.setFont(labelFont);
        nameLabel.setFont(labelFont);
        idLabel.setFont(labelFont);

        // Dropdowns (ComboBox)
        ComboBox<String> typeBox = new ComboBox<>();
        ComboBox<String> nameBox = new ComboBox<>();
        ComboBox<String> idBox = new ComboBox<>();

        // Size & style
        ComboBox<?>[] boxes = { typeBox, nameBox, idBox };
        for (ComboBox<?> box : boxes) {
            box.setPrefWidth(450);
            box.setPrefHeight(50);
            box.setStyle("-fx-font-size:18px;");
        }

        // Layout
        grid.add(typeLabel, 0, 0);
        grid.add(typeBox, 1, 0);

        grid.add(nameLabel, 0, 1);
        grid.add(nameBox, 1, 1);

        grid.add(idLabel, 0, 2);
        grid.add(idBox, 1, 2);

        // Buttons
        Button deleteBtn = new Button("Delete");
        deleteBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        deleteBtn.setPrefSize(150, 45);

        Button backBtn = new Button("Back");
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        backBtn.setPrefSize(120, 40);

        backBtn.setOnAction(e -> app.showDashboard());

        grid.add(deleteBtn, 1, 4);
        grid.add(backBtn, 1, 5);

        scene = new Scene(grid, 1024, 768);
    }

    public Scene getScene() {
        return scene;
    }
}

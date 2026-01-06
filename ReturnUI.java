package ui;

import com.mycompany.inventory.Inventory;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ReturnUI {

    private final Scene scene;

    public ReturnUI(Inventory app) {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(50));
        grid.setHgap(40);
        grid.setVgap(35);
        grid.setStyle("-fx-background-color:#d2b48c;");

        Font labelFont = Font.font("Arial", FontWeight.BOLD, 20);
        Font fieldFont = Font.font("Arial", 18);

        String[] labels = {
            "Product Name",
            "Return To",
            "Return By",
            "Quantity",
            "Date"
        };

        int row = 0;
        for (String text : labels) {

            Label label = new Label(text);
            label.setFont(labelFont);

            TextField field = new TextField();
            field.setPrefWidth(420);
            field.setPrefHeight(45);
            field.setFont(fieldFont);

            grid.add(label, 0, row);
            grid.add(field, 1, row);

            row++;
        }

        // Back button
        Button back = new Button("Back");
        back.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        back.setPrefSize(120, 40);
        back.setOnAction(e -> app.showDashboard());

        grid.add(back, 1, row + 1);

        scene = new Scene(grid, 1024, 768);
    }

    public Scene getScene() {
        return scene;
    }
}

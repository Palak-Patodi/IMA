package ui;

import com.mycompany.inventory.Inventory;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AddInventoryUI {

    private final Scene scene;

    public AddInventoryUI(Inventory app) {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setHgap(40);
        grid.setVgap(18);
        grid.setStyle("-fx-background-color:#d2b48c;");

        // ---------- COMMON FONT ----------
        Font labelFont = Font.font("Arial", FontWeight.BOLD, 16);

        // ---------- LEFT SIDE ----------
        String[] leftLabels = {
            "Supplier Name", "Email", "Address", "Contact number",
            "Product Type ID", "Product Name",
            "Product Desc", "Bill Invoice"
        };

        int row = 0;
        for (String text : leftLabels) {
            Label label = new Label(text);
            label.setFont(labelFont);

            TextField field = new TextField();
            field.setPrefWidth(280);
            field.setFont(Font.font("Arial", 16));

            grid.add(label, 0, row);
            grid.add(field, 1, row);
            row++;
        }

        // ---------- RIGHT SIDE ----------
        Label billLabel = new Label("Bill");
        billLabel.setFont(labelFont);

        Button uploadBtn = new Button("UPLOAD  ⬆");
        uploadBtn.setPrefSize(220, 55);
        uploadBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        uploadBtn.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 25;" +
            "-fx-font-weight: bold;"
        );

        grid.add(billLabel, 2, 0);
        grid.add(uploadBtn, 3, 0);

        Label billBy = new Label("Bill Recieved by");
        billBy.setFont(labelFont);
        TextField billByField = new TextField();
        billByField.setPrefWidth(280);

        grid.add(billBy, 2, 2);
        grid.add(billByField, 3, 2);

        Label datePurchase = new Label("Purchase Date");
        datePurchase.setFont(labelFont);
        TextField datePurchaseField = new TextField();
        datePurchaseField.setPrefWidth(280);

        grid.add(datePurchase, 2, 3);
        grid.add(datePurchaseField, 3, 3);

        Label qty = new Label("Quantity Ordered");
        qty.setFont(labelFont);
        TextField qtyField = new TextField();
        qtyField.setPrefWidth(280);

        grid.add(qty, 2, 4);
        grid.add(qtyField, 3, 4);

        // ---------- BACK BUTTON ----------
        Button back = new Button("Back");
        back.setPrefSize(120, 40);
        back.setOnAction(e -> app.showDashboard());

        grid.add(back, 3, 7);
        GridPane.setMargin(back, new Insets(30, 0, 0, 0));
        GridPane.setHalignment(back, javafx.geometry.HPos.RIGHT);

        scene = new Scene(grid, 1024, 768);
    }

    public Scene getScene() {
        return scene;
    }
}

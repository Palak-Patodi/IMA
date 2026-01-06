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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Alert;
import javafx.geometry.Side;
import javafx.scene.control.CustomMenuItem;
import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.input.MouseEvent;




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
    
    private void showReportAlert(String type) 
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText(type + " Report");
        alert.setContentText(type + " report selected.");
        alert.showAndWait();
    }


    public DashboardUI(Inventory app) {
       
        // ===== TOP BROWN BAR =====
        Label userManual = new Label("User Manual");
        userManual.setOnMouseClicked(e -> openUserManual());
        userManual.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label logout = new Label("Logout");
        logout.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logout.setOnMouseClicked((MouseEvent e) -> {app.showLogin();});


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
                
                // ---------- UPDATE DROPDOWN ----------
                if (btn.getText().equals("Update")) {

                    ContextMenu updateMenu = new ContextMenu();

                    // ===== ADD BUTTON =====
                    Button addBtn = new Button("Add");
                    addBtn.setPrefSize(200, 60);

                    // ===== DELETE BUTTON =====
                    Button deleteBtn = new Button("Delete");
                    deleteBtn.setPrefSize(200, 60);

                    // ===== MODIFY BUTTON =====
                    Button modifyBtn = new Button("Modify");
                    modifyBtn.setPrefSize(200, 60);

                    String bigMenuStyle =
                        "-fx-font-size: 22px;" +
                        "-fx-background-color: #d2b48c;" +
                        "-fx-background-radius: 30;" +
                        "-fx-text-fill: #333333;";

                    addBtn.setStyle(bigMenuStyle);
                    deleteBtn.setStyle(bigMenuStyle);
                    modifyBtn.setStyle(bigMenuStyle);

                    CustomMenuItem addItem = new CustomMenuItem(addBtn, true);
                    CustomMenuItem deleteItem = new CustomMenuItem(deleteBtn, true);
                    CustomMenuItem modifyItem = new CustomMenuItem(modifyBtn, false); // IMPORTANT

                    updateMenu.getItems().addAll(addItem, deleteItem, modifyItem);

                    // SHOW FIRST DROPDOWN
                    btn.setOnAction(e ->
                        updateMenu.show(btn, Side.BOTTOM, 0, 0)
                    );

                    // ===== EXISTING ACTIONS (UNCHANGED) =====
                    addBtn.setOnAction(e -> {
                        updateMenu.hide();
                        app.showAddInventory();
                    });

                    deleteBtn.setOnAction(e -> {
                        updateMenu.hide();
                        app.showDeleteInventory();
                    });

                    // ==============================
                    // MODIFY → SECOND DROPDOWN
                    // ==============================
                    ContextMenu modifyMenu = new ContextMenu();

                    Button productBtn = new Button("Product");
                    Button supplierBtn = new Button("Supplier");
                    Button billBtn = new Button("Bill");

                    productBtn.setPrefSize(200, 60);
                    supplierBtn.setPrefSize(200, 60);
                    billBtn.setPrefSize(200, 60);

                    productBtn.setStyle(bigMenuStyle);
                    supplierBtn.setStyle(bigMenuStyle);
                    billBtn.setStyle(bigMenuStyle);

                    CustomMenuItem productItem = new CustomMenuItem(productBtn, true);
                    CustomMenuItem supplierItem = new CustomMenuItem(supplierBtn, true);
                    CustomMenuItem billItem = new CustomMenuItem(billBtn, true);

                    modifyMenu.getItems().addAll(
                        productItem,
                        supplierItem,
                        billItem
                    );

                    // SHOW MODIFY DROPDOWN
                    modifyBtn.setOnAction(e -> {
                        modifyMenu.show(modifyBtn, Side.RIGHT, 0, 0);
                    });

                    // ===== MODIFY ACTIONS (PLACEHOLDERS) =====
                    productBtn.setOnAction(e -> {
                        modifyMenu.hide();
                        System.out.println("Modify Product clicked");
                        // app.showModifyProduct();
                    });

                    supplierBtn.setOnAction(e -> {
                        modifyMenu.hide();
                        System.out.println("Modify Supplier clicked");
                        // app.showModifySupplier();
                    });

                    billBtn.setOnAction(e -> {
                        modifyMenu.hide();
                        System.out.println("Modify Bill clicked");
                        // app.showModifyBill();
                    });
                }

                // ---------- REPORT DROPDOWN ----------
                if (btn.getText().equals("Report"))
                {
                    ContextMenu reportMenu = new ContextMenu();

                    Button weeklyBtn = new Button("Weekly");
                    Button monthlyBtn = new Button("Monthly");
                    Button yearlyBtn = new Button("Yearly");

                    String bigMenuStyle =
                        "-fx-font-size: 22px;" +
                        "-fx-background-color: #d2b48c;" +
                        "-fx-background-radius: 30;" +
                        "-fx-text-fill: #333333;";

                    weeklyBtn.setStyle(bigMenuStyle);
                    monthlyBtn.setStyle(bigMenuStyle);
                    yearlyBtn.setStyle(bigMenuStyle);

                    // SAME SIZE AS UPDATE DROPDOWN
                    weeklyBtn.setPrefSize(200, 60);
                    monthlyBtn.setPrefSize(200, 60);
                    yearlyBtn.setPrefSize(200, 60);

                    CustomMenuItem weeklyItem = new CustomMenuItem(weeklyBtn);
                    CustomMenuItem monthlyItem = new CustomMenuItem(monthlyBtn);
                    CustomMenuItem yearlyItem = new CustomMenuItem(yearlyBtn);

                    weeklyItem.setHideOnClick(true);
                    monthlyItem.setHideOnClick(true);
                    yearlyItem.setHideOnClick(true);

                    reportMenu.getItems().addAll(weeklyItem, monthlyItem, yearlyItem);

                    btn.setOnAction(e ->
                        reportMenu.show(btn, Side.BOTTOM, 0, 0)
                    );

                    weeklyBtn.setOnAction(e -> showReportAlert("Weekly"));
                    monthlyBtn.setOnAction(e -> showReportAlert("Monthly"));
                    yearlyBtn.setOnAction(e -> showReportAlert("Yearly"));
                }
                        //ISSUE
                if (btn.getText().equals("Issue")) 
                {
                    btn.setOnAction(e -> app.showIssue());
                }
                        //RETURN
                if (btn.getText().equals("Return")) 
                {
                    btn.setOnAction(e -> app.showReturn());
                }
                 

                grid.add(btn, c, r);
            }
        }

        // ===== ROOT =====
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(grid);

        scene = new Scene(root, 1024, 768);
    }

    private void openUserManual() 
    {
        try {
            InputStream  is = getClass().getResourceAsStream(
                "/USER MANUAL.pdf"
            );

            if (is == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("User Manual Not Found");
                alert.setContentText("USER_MANUAL.pdf not found in resources folder.");
                alert.showAndWait();
                return;
            }

            Path tempFile = Files.createTempFile("UserManual", ".pdf");
            Files.copy(is, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Desktop.getDesktop().open(tempFile.toFile());

        } catch (IOException e) {
        }
    }

    public Scene getScene() {
        return scene;
    }
}

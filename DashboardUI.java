package ui;

import com.mycompany.inventory.Inventory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.binding.Bindings;
import service.NotificationService;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;


public class DashboardUI {

    private final Scene scene;

    // ---------------------- LOW INVENTORY ALERT ----------------------
    private void showLowInventoryAlert(String itemName, int quantity) {
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

    private void checkLowInventoryFromUser() {
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

    // ---------------------- REPORT ALERT ----------------------
    private void showReportAlert(String type) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText(type + " Report");
        alert.setContentText(type + " report selected.");
        alert.showAndWait();
    }

    // ---------------------- CONSTRUCTOR ----------------------
    public DashboardUI(Inventory app) {

        // ===== TOP BROWN BAR =====
        Label userManual = new Label("User Manual");
        userManual.setOnMouseClicked(e -> openUserManual());
        userManual.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label logout = new Label("Logout");
        logout.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logout.setOnMouseClicked((MouseEvent e) -> app.showLogin());
        
        Label greetingLabel = new Label(getGreeting());
        greetingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
       String user = Inventory.getLoggedUser();

        String firstLetter = user.substring(0,1).toUpperCase();

        Label letter = new Label(firstLetter);
        letter.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:14;" +
                "-fx-font-weight:bold;"
        );

        Circle circle = new Circle(14);
        circle.setFill(Color.web("#8B5E34"));

        StackPane profile = new StackPane(circle, letter);
        profile.setPrefSize(28,28);

        ContextMenu profileMenu = new ContextMenu();

        profile.setOnMouseClicked(e ->
                profileMenu.show(profile, Side.BOTTOM, 0, 0)
        );
        StackPane profileIcon = new StackPane(profile);
        
        MenuItem userItem = new MenuItem("User : " + Inventory.getLoggedUser());
        MenuItem roleItem = new MenuItem("Role : " + Inventory.getUserRole());
        MenuItem notificationItem = new MenuItem("Notifications");
      
       profileMenu.getItems().addAll(userItem, roleItem, notificationItem);
       
       notificationItem.setOnAction(e -> {

    Stage stage = new Stage();

    VBox box = new VBox(15);
    box.setPadding(new Insets(25));
    box.setAlignment(Pos.TOP_LEFT);

    box.setStyle(
        "-fx-background-color:#F5DEB3;" +
        "-fx-border-color:#8B5E34;" +
        "-fx-border-width:2;" +
        "-fx-background-radius:8;"
    );

    if(NotificationService.getNotifications().isEmpty()){

    Label empty = new Label("No Notifications Yet");
    empty.setStyle(
        "-fx-font-size:14;" +
        "-fx-text-fill:#3E2723;" +
        "-fx-font-weight:bold;"
    );

    box.getChildren().add(empty);
}
    for(String msg : NotificationService.getNotifications()) {

        Label lbl = new Label(msg);
        lbl.setWrapText(true);
        lbl.setStyle(
            "-fx-font-size:14;" +
            "-fx-text-fill:#3E2723;" +
            "-fx-font-weight:bold;"
        );

        box.getChildren().add(lbl);
    }

    ScrollPane scroll = new ScrollPane(box);
    scroll.setFitToWidth(true);
    scroll.setStyle(
        "-fx-background:#F5DEB3;" +
        "-fx-border-color:#8B5E34;"
    );
    scroll.setPadding(new Insets(10));

    Scene scene = new Scene(scroll,350,250);

    stage.setScene(scene);
    stage.setTitle("Notifications");
    stage.show();
});       
      
       
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(25, greetingLabel, spacer, profileIcon, userManual, logout);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(12, 30, 12, 30)); // slimmer height
        topBar.setStyle(
            "-fx-background-color:#F5DEB3;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0.2, 0, 3);"
        );

        // ===== BUTTON GRID =====
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(35);
        grid.setVgap(35);
        grid.setPadding(new Insets(60));
        StackPane.setAlignment(grid, Pos.CENTER);

        String[] names = { "Update", "Issue", "Return", "View", "Report",
                "Budget Analysis", "Request Inventory" };

        int index = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (index >= names.length) break;

                Button btn = new Button(names[index++]);
                btn.setFont(Font.font("Arial", FontWeight.NORMAL, 30));
                btn.setPrefSize(320, 120);
                String btnStyle = 
                    "-fx-background-color:#F5DEB3;" +
                    "-fx-border-color:#b08968;" +
                    "-fx-border-width:2;" +
                    "-fx-background-radius:10;" +
                    "-fx-font-size:22px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-text-fill:#2E2E2E;";
                btn.setStyle(btnStyle);
                btn.setOnMouseEntered(e ->
                    btn.setStyle(btnStyle + "-fx-background-color:#c19a6b;")
                );

                btn.setOnMouseExited(e ->
                    btn.setStyle(btnStyle)
                );

                String role = Inventory.getUserRole();
                // HOD and Dean restrictions
                if(role.equalsIgnoreCase("HOD") || role.equalsIgnoreCase("Dean")){

                    if(!btn.getText().equals("Report") &&
                       !btn.getText().equals("Budget Analysis")){

                        btn.setDisable(true);
                        btn.setOpacity(0.5);
                    }
                }

                // Manager restriction
                if(role.equalsIgnoreCase("Manager")){

                    if(btn.getText().equals("Budget Analysis")){

                        btn.setDisable(true);
                        btn.setOpacity(0.5);
                    }
                }
                // ---------------- UPDATE DROPDOWN ----------------
                if (btn.getText().equals("Update")) {
                    ContextMenu updateMenu = new ContextMenu();
                    Button addBtn = new Button("Add");
                    Button deleteBtn = new Button("Delete");
                    Button modifyBtn = new Button("Modify");
                    
                    Button orderPlacedBtn = new Button("Order Placed");

                    String bigMenuStyle = "-fx-font-size: 22px;" +
                        "-fx-background-color: #F5DEB3;" +   // same beige as login
                        "-fx-background-radius: 6;" +        // small radius = rectangle
                        "-fx-border-radius: 6;" +
                        "-fx-border-color: #8B5E34;" +
                        "-fx-border-width: 2;" +
                        "-fx-text-fill: #2C3E50;";

                    orderPlacedBtn.setStyle(bigMenuStyle);
                    
                    addBtn.setStyle(bigMenuStyle);
                    deleteBtn.setStyle(bigMenuStyle);
                    modifyBtn.setStyle(bigMenuStyle);

                    orderPlacedBtn.setPrefSize(200, 60);
                    
                    addBtn.setPrefSize(200, 60);
                    deleteBtn.setPrefSize(200, 60);
                    modifyBtn.setPrefSize(200, 60);

                    CustomMenuItem addItem = new CustomMenuItem(addBtn, true);
                    CustomMenuItem deleteItem = new CustomMenuItem(deleteBtn, true);
                    CustomMenuItem modifyItem = new CustomMenuItem(modifyBtn, false);
                    CustomMenuItem orderItem = new CustomMenuItem(orderPlacedBtn, true);
                    updateMenu.getItems().addAll(addItem, deleteItem, modifyItem, orderItem);

                    btn.setOnAction(e -> updateMenu.show(btn, Side.BOTTOM, 0, 0));

                    addBtn.setOnAction(e -> {
                        updateMenu.hide();
                        app.showAddInventory();
                    });
                    orderPlacedBtn.setOnAction(e -> {
                        updateMenu.hide();
                        app.showAddOrder();
                    });
                    deleteBtn.setOnAction(e -> {
                        updateMenu.hide();
                        app.showDeleteInventory();
                    });

                    // --------- MODIFY DROPDOWN ----------
                    ContextMenu modifyMenu = new ContextMenu();
                    Button productBtn = new Button("Product");
                    Button supplierBtn = new Button("Supplier");
                    Button billBtn = new Button("Bill");

                    productBtn.setStyle(bigMenuStyle);
                    supplierBtn.setStyle(bigMenuStyle);
                    billBtn.setStyle(bigMenuStyle);

                    productBtn.setPrefSize(200, 60);
                    supplierBtn.setPrefSize(200, 60);
                    billBtn.setPrefSize(200, 60);

                    modifyMenu.getItems().addAll(
                            new CustomMenuItem(productBtn, true),
                            new CustomMenuItem(supplierBtn, true),
                            new CustomMenuItem(billBtn, true)
                    );

                    modifyBtn.setOnAction(e -> modifyMenu.show(modifyBtn, Side.RIGHT, 0, 0));

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

                // ---------------- REPORT DROPDOWN ----------------
                if (btn.getText().equals("Report")) {
                    ContextMenu reportMenu = new ContextMenu();
                    Button weeklyBtn = new Button("Weekly");
                    Button monthlyBtn = new Button("Monthly");
                    Button yearlyBtn = new Button("Yearly");

                    String bigMenuStyle = "-fx-font-size: 22px;" +
                        "-fx-background-color: #F5DEB3;" +   // same beige as login
                        "-fx-background-radius: 6;" +        // small radius = rectangle
                        "-fx-border-radius: 6;" +
                        "-fx-border-color: #8B5E34;" +
                        "-fx-border-width: 2;" +
                        "-fx-text-fill: #2C3E50;";

                    weeklyBtn.setStyle(bigMenuStyle);
                    monthlyBtn.setStyle(bigMenuStyle);
                    yearlyBtn.setStyle(bigMenuStyle);

                    weeklyBtn.setPrefSize(200, 60);
                    monthlyBtn.setPrefSize(200, 60);
                    yearlyBtn.setPrefSize(200, 60);

                    reportMenu.getItems().addAll(
                            new CustomMenuItem(weeklyBtn),
                            new CustomMenuItem(monthlyBtn),
                            new CustomMenuItem(yearlyBtn)
                    );

                    btn.setOnAction(e -> reportMenu.show(btn, Side.BOTTOM, 0, 0));
                    weeklyBtn.setOnAction(e -> {
                        ReportUI report = new ReportUI("Weekly");
                        report.show();
                    });

                    monthlyBtn.setOnAction(e -> {
                        ReportUI report = new ReportUI("Monthly");
                        report.show();
                    });

                    yearlyBtn.setOnAction(e -> {
                        ReportUI report = new ReportUI("Yearly");
                        report.show();
                    });
                }

                // ---------------- BUDGET ANALYSIS ----------------
             
                if (btn.getText().equals("Budget Analysis")) {


                    btn.setOnAction(e -> {
                         BudgetAnalysisUI ui = new BudgetAnalysisUI();
                         Stage stage = new Stage();
                         stage.setScene(ui.getScene());
                         stage.setTitle("Budget Analysis");
                         stage.show();
                     });
                }


                // ---------------- REQUEST INVENTORY ----------------
                if (btn.getText().equals("Request Inventory")) {
                    btn.setOnAction(e -> showRecipientSelection());
                }

                // ---------------- VIEW DROPDOWN ----------------
                if (btn.getText().equals("View")) {
                    ContextMenu viewMenu = new ContextMenu();
                    Button productBtn = new Button("Product Details");
                    Button billBtn = new Button("Bill Details");
                    Button supplierBtn = new Button("Supplier Details");

                    String bigMenuStyle = "-fx-font-size: 22px;" +
                        "-fx-background-color: #F5DEB3;" +   // same beige as login
                        "-fx-background-radius: 6;" +        // small radius = rectangle
                        "-fx-border-radius: 6;" +
                        "-fx-border-color: #8B5E34;" +
                        "-fx-border-width: 2;" +
                        "-fx-text-fill: #2C3E50;";

                    productBtn.setStyle(bigMenuStyle);
                    billBtn.setStyle(bigMenuStyle);
                    supplierBtn.setStyle(bigMenuStyle);

                    productBtn.setPrefSize(220, 60);
                    billBtn.setPrefSize(220, 60);
                    supplierBtn.setPrefSize(220, 60);

                    viewMenu.getItems().addAll(
                            new CustomMenuItem(productBtn, true),
                            new CustomMenuItem(billBtn, true),
                            new CustomMenuItem(supplierBtn, true)
                    );

                    btn.setOnAction(e -> viewMenu.show(btn, Side.BOTTOM, 0, 0));

                    productBtn.setOnAction(e -> {
                        viewMenu.hide();
                        showProductTable();
                    });
                    billBtn.setOnAction(e -> {
                        viewMenu.hide();
                        showBillTable();
                    });
                    supplierBtn.setOnAction(e -> {
                        viewMenu.hide();
                        showSupplierTable();
                    });
                }

                // ---------------- ISSUE & RETURN ----------------
                if (btn.getText().equals("Issue")) {
                    btn.setOnAction(e -> app.showIssue());
                }
                if (btn.getText().equals("Return")) {

                ContextMenu returnMenu = new ContextMenu();

                Button inventoryToSupplier = new Button("Inventory → Supplier");
                Button issuedReturn = new Button("Issued Person → Inventory");

                String style = "-fx-font-size: 18px;" +
                        "-fx-background-color:#F5DEB3;" +
                        "-fx-border-color:#8B5E34;" +
                        "-fx-border-width:2;";


                inventoryToSupplier.setStyle(style);
                issuedReturn.setStyle(style);


                inventoryToSupplier.setPrefSize(250, 50);
                issuedReturn.setPrefSize(250, 50);

                returnMenu.getItems().addAll(
                    new CustomMenuItem(inventoryToSupplier, true),
                    new CustomMenuItem(issuedReturn, true)
                );

                btn.setOnAction(e -> returnMenu.show(btn, Side.BOTTOM, 0, 0));


                inventoryToSupplier.setOnAction(e -> {
                    returnMenu.hide();
                    app.showReturn("INVENTORY_TO_SUPPLIER");
                });

                issuedReturn.setOnAction(e -> {
                    returnMenu.hide();
                    app.showReturn(ReturnUI.ISSUE_TO_INVENTORY);
                });
            }

                grid.add(btn, c, r);
            }
        }
            
        // ===== BACKGROUND IMAGE =====
        ImageView bgView = new ImageView(
                new Image(getClass().getResource("/images/pexels-web-buz-29454379.jpg").toExternalForm())
        );

        bgView.setFitWidth(1024);
        bgView.setFitHeight(768);
        bgView.setPreserveRatio(false);

        // ===== BLUR EFFECT =====
        bgView.setEffect(new GaussianBlur(15));

        // ===== DARK OVERLAY =====
        Rectangle darkOverlay = new Rectangle(1024, 768);
        darkOverlay.setFill(Color.rgb(0, 0, 0, 0.5));

        // ===== CENTER STACK =====
        StackPane centerStack = new StackPane(
                bgView,
                darkOverlay,
                grid
        );

        // ===== ROOT =====
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(centerStack);

        scene = new Scene(root, 1024, 768);

        // ===== RESPONSIVE =====
        bgView.fitWidthProperty().bind(scene.widthProperty());
        bgView.fitHeightProperty().bind(scene.heightProperty());
        darkOverlay.widthProperty().bind(scene.widthProperty());
        darkOverlay.heightProperty().bind(scene.heightProperty());

    }

    // ---------------------- USER MANUAL ----------------------
    private void openUserManual() {
        try {
            InputStream is = getClass().getResourceAsStream("/USER MANUAL.pdf");
            if (is == null) {
                Alert alert = new Alert(AlertType.ERROR);
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

    // ---------------------- TABLE VIEWS ----------------------
    private void showProductTable() {
        Stage stage = new Stage();
        TableView<model.Product> table = new TableView<>();

        TableColumn<model.Product, String> name = new TableColumn<>("Product Name");
        name.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<model.Product, Integer> qty = new TableColumn<>("Stock");
        qty.setCellValueFactory(d -> d.getValue().qtyProperty().asObject());

        TableColumn<model.Product, String> date = new TableColumn<>("Qty Updated Date");
        date.setCellValueFactory(d -> d.getValue().updatedDateProperty());

        table.getColumns().addAll(name, qty, date);

        ObservableList<model.Product> data = FXCollections.observableArrayList();
        try (Connection con = db.DBConnection.getConnection();
     Statement st = con.createStatement();
     ResultSet rs = st.executeQuery("SELECT product_name, qty_in_stock, qty_updated_date FROM product WHERE status='ACTIVE'")) {

    // This loop must be outside the parentheses
    while (rs.next()) {
        data.add(new model.Product(
                rs.getString("product_name"),
                rs.getInt("qty_in_stock"),
                rs.getString("qty_updated_date")
        ));
    }

} catch (Exception e) {
}

        table.setItems(data);
        stage.setScene(new Scene(new VBox(table), 700, 400));
        stage.setTitle("Product Details");
        stage.show();
    }

    private void showBillTable() {
        Stage stage = new Stage();
        TableView<model.Bill> table = new TableView<>();

        TableColumn<model.Bill, String> billNo = new TableColumn<>("Bill No");
        billNo.setCellValueFactory(d -> d.getValue().billNoProperty());

        TableColumn<model.Bill, Double> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(d -> d.getValue().amountProperty().asObject());

        table.getColumns().addAll(billNo, amount);

        ObservableList<model.Bill> data = FXCollections.observableArrayList();
        try (Connection con = db.DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT bill_no, bill_amount FROM bill_invoice")) {

            while (rs.next()) {
                data.add(new model.Bill(
                        rs.getString("bill_no"),
                        rs.getDouble("bill_amount")
                ));
            }

        } catch (Exception e) {
        }

        table.setItems(data);
        stage.setScene(new Scene(new VBox(table), 600, 400));
        stage.setTitle("Bill Details");
        stage.show();
    }

    private void showSupplierTable() {
        Stage stage = new Stage();
        TableView<model.Supplier> table = new TableView<>();

        TableColumn<model.Supplier, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(d -> d.getValue().nameProperty());

        TableColumn<model.Supplier, String> contact = new TableColumn<>("Contact");
        contact.setCellValueFactory(d -> d.getValue().contactProperty());

        TableColumn<model.Supplier, String> address = new TableColumn<>("Address");
        address.setCellValueFactory(d -> d.getValue().addressProperty());

        TableColumn<model.Supplier, String> ptype = new TableColumn<>("Product Type ID");
ptype.setCellValueFactory(d -> d.getValue().ptypeIdProperty());


        table.getColumns().addAll(name, contact, address, ptype);

        ObservableList<model.Supplier> data = FXCollections.observableArrayList();
        try (Connection con = db.DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT name, contact_no, address, ptype_id FROM supplier")) {

            while (rs.next()) {
                data.add(new model.Supplier(
    rs.getString("name"),
    rs.getString("contact_no"),
    rs.getString("address"),
    rs.getString("ptype_id") // ✅ correct type
));

            }

        } catch (Exception e) {
        }

        table.setItems(data);
        stage.setScene(new Scene(new VBox(table), 800, 400));
        stage.setTitle("Supplier Details");
        stage.show();
    }

    // ---------------------- BUDGET ANALYSIS ----------------------
    private void showBudgetAnalysis() {

    Stage stage = new Stage();
    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color:#F5DEB3;");

    // ================= TITLE =================
    Label title = new Label("Budget Analysis Report");
    title.setStyle("-fx-font-size:38px; -fx-font-weight:bold; -fx-text-fill:black;");
    title.setPadding(new Insets(30,0,20,40));

    // ================= BUDGET DATA =================
    double totalBudget = 250000;
    double furniture = 80000;
    double stationery = 60000;
    double miscellaneous = 50000;
    double computer = 60000;
    double spent = 150000;
    double remaining = totalBudget - spent;

    // ================= TOP CARDS =================
    HBox cards = new HBox(40);
    cards.setAlignment(Pos.CENTER);

    cards.getChildren().addAll(
            createCard("Total Budget", totalBudget, "#0D47A1"),
            createCard("Amount Spent", spent, "#E65100"),
            createCard("Remaining Balance", remaining, "#1B5E20")
    );

    // ================= DONUT CHART =================
    PieChart pieChart = new PieChart();
    pieChart.setLabelsVisible(false);
    pieChart.setLegendVisible(false);

    PieChart.Data d1 = new PieChart.Data("Furniture", furniture);
    PieChart.Data d2 = new PieChart.Data("Stationery", stationery);
    PieChart.Data d3 = new PieChart.Data("Miscellaneous", miscellaneous);
    PieChart.Data d4 = new PieChart.Data("Computer", computer);

    pieChart.getData().addAll(d1,d2,d3,d4);

    pieChart.setPrefSize(400,400);

    d1.getNode().setStyle("-fx-pie-color:#0D47A1;");
    d2.getNode().setStyle("-fx-pie-color:#E65100;");
    d3.getNode().setStyle("-fx-pie-color:#6A1B9A;");
    d4.getNode().setStyle("-fx-pie-color:#1B5E20;");

    // Create donut hole
    StackPane donutPane = new StackPane();
    Circle hole = new Circle(90, Color.web("#F5DEB3"));
    Label centerAmount = new Label("₹ " + totalBudget);
    centerAmount.setStyle("-fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:black;");
    donutPane.getChildren().addAll(pieChart, hole, centerAmount);

    // ================= PIE LEGEND =================
    VBox pieLegend = new VBox(15);
    pieLegend.setAlignment(Pos.CENTER_LEFT);

    pieLegend.getChildren().addAll(
            createLegendItem("#0D47A1","Furniture",furniture),
            createLegendItem("#E65100","Stationery",stationery),
            createLegendItem("#6A1B9A","Miscellaneous",miscellaneous),
            createLegendItem("#1B5E20","Computer Sets",computer)
    );

    HBox pieSection = new HBox(40, donutPane, pieLegend);
    pieSection.setAlignment(Pos.CENTER);

    // ================= BAR CHART =================
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();

    xAxis.setLabel("Category");
    yAxis.setLabel("Amount");

    xAxis.setTickLabelFill(Color.BLACK);
    yAxis.setTickLabelFill(Color.BLACK);

    BarChart<String,Number> barChart = new BarChart<>(xAxis,yAxis);
    barChart.setLegendVisible(false);
    barChart.setCategoryGap(25);
    barChart.setBarGap(5);
    barChart.setPrefSize(500,400);

    XYChart.Series<String,Number> series = new XYChart.Series<>();

    series.getData().add(new XYChart.Data<>("Furniture", furniture));
    series.getData().add(new XYChart.Data<>("Stationery", stationery));
    series.getData().add(new XYChart.Data<>("Miscellaneous", miscellaneous));
    series.getData().add(new XYChart.Data<>("Computer", computer));

    barChart.getData().add(series);

    // Color bars individually
    for (XYChart.Data<String,Number> data : series.getData()) {
        data.nodeProperty().addListener((obs,node1,node2)->{
            if(data.getXValue().equals("Furniture"))
                node2.setStyle("-fx-bar-fill:#0D47A1;");
            else if(data.getXValue().equals("Stationery"))
                node2.setStyle("-fx-bar-fill:#E65100;");
            else if(data.getXValue().equals("Miscellaneous"))
                node2.setStyle("-fx-bar-fill:#6A1B9A;");
            else
                node2.setStyle("-fx-bar-fill:#1B5E20;");
        });
    }

    // ================= BAR LEGEND =================
    HBox barLegend = new HBox(40);
    barLegend.setAlignment(Pos.CENTER);

    barLegend.getChildren().addAll(
            createLegendCircle("#0D47A1","Furniture"),
            createLegendCircle("#E65100","Stationery"),
            createLegendCircle("#6A1B9A","Miscellaneous"),
            createLegendCircle("#1B5E20","Computer Sets")
    );

    VBox barSection = new VBox(20, barChart, barLegend);
    barSection.setAlignment(Pos.CENTER);

    // ================= MAIN CENTER =================
    HBox center = new HBox(80, pieSection, barSection);
    center.setAlignment(Pos.CENTER);
    center.setPadding(new Insets(40));

    root.setTop(title);
    root.setCenter(new VBox(20, cards, center));

    Scene scene = new Scene(root, 1200, 750);
    stage.setScene(scene);
    stage.setTitle("Budget Analysis Report");
    stage.show();
}

    private VBox createCard(String title,double amount,String color){
    Label t = new Label(title);
    t.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:white;");
    Label amt = new Label("₹ " + amount);
    amt.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:white;");

    VBox box = new VBox(10,t,amt);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(25));
    box.setStyle("-fx-background-color:"+color+"; -fx-background-radius:15;");
    box.setPrefWidth(250);

    // simple animation
    box.setOnMouseEntered(e -> {
        box.setScaleX(1.05);
        box.setScaleY(1.05);
    });
    box.setOnMouseExited(e -> { box.setScaleX(1); box.setScaleY(1); });

    return box;
}

private HBox createLegendItem(String color,String name,double amount){
    Circle c = new Circle(8,Color.web(color));
    Label label = new Label(name + "  ₹ " + amount);
    label.setStyle("-fx-font-weight:bold; -fx-text-fill:black;");
    return new HBox(10,c,label);
}

private HBox createLegendCircle(String color,String name){
    Circle c = new Circle(8,Color.web(color));
    Label label = new Label(name);
    label.setStyle("-fx-font-weight:bold; -fx-text-fill:black;");
    return new HBox(10,c,label);
}

    private void showInfoPanel(String title, String message) {
        Label lbl = new Label(message);
        lbl.setWrapText(true);

        VBox box = new VBox(lbl);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color:#e7f3ff;" +
                "-fx-border-color:#2196f3;" +
                "-fx-border-radius:10;");

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(box, 350, 200));
        stage.show();
    }
    //------GREETING----------
    private String getGreeting() {

    int hour = java.time.LocalTime.now().getHour();

    String greeting;

    if (hour >= 5 && hour < 12) {
        greeting = "Good Morning";
    } else if (hour >= 12 && hour < 17) {
        greeting = "Good Afternoon";
    } else {
        greeting = "Good Evening";
    }

    String role = Inventory.getUserRole();

    if (role == null) role = "User";

    return greeting + ", " + role;
    }
    
    // ---------------------- REQUEST INVENTORY ----------------------

    private void showRecipientSelection() {

        Stage stage = new Stage();

        Label title = new Label("Select Recipient");
        title.setStyle("-fx-font-size:20; -fx-font-weight:bold; -fx-text-fill:#3E2723;");

        Button hodBtn = new Button("Send to HOD");
        Button deanBtn = new Button("Send to Dean");

        String style =
                "-fx-background-color:#8B5E34;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:16;" +
                "-fx-background-radius:8;";

        hodBtn.setStyle(style);
        deanBtn.setStyle(style);

        hodBtn.setPrefWidth(160);
        deanBtn.setPrefWidth(160);

        hodBtn.setOnAction(e -> {
            stage.close();
            showProductForm("HOD");
        });

        deanBtn.setOnAction(e -> {
            stage.close();
            showProductForm("Dean");
        });

        HBox buttons = new HBox(20, hodBtn, deanBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(30, title, buttons);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        root.setStyle(
                "-fx-background-color:#F5DEB3;" +
                "-fx-background-radius:10;" +
                "-fx-border-color:#8B5E34;" +
                "-fx-border-width:2;"
        );

        stage.setScene(new Scene(root,350,200));
        stage.setTitle("Request Inventory");
        stage.show();
    }
    private void showProductForm(String recipient) {

    Stage stage = new Stage();

    Label title = new Label("Inventory Request");
    title.setStyle("-fx-font-size:22; -fx-font-weight:bold;");

    TextField product = new TextField();
    product.setPromptText("Product Name");

    TextField qty = new TextField();
    qty.setPromptText("Product Quantity");

    Button sendBtn = new Button("Send Request");
    sendBtn.setStyle(
            "-fx-background-color:#8B5E34;" +
            "-fx-text-fill:white;" +
            "-fx-font-size:16;"
    );

    sendBtn.setOnAction(e -> {
        stage.close();
        confirmEmailSend(recipient, product.getText(), qty.getText());
    });

    VBox root = new VBox(15,
            title,
            new Label("Product Name"), product,
            new Label("Quantity"), qty,
            sendBtn
    );

    root.setPadding(new Insets(30));
    root.setAlignment(Pos.CENTER);
    root.setStyle("-fx-background-color:#F5DEB3;");

    stage.setScene(new Scene(root,350,300));
    stage.show();
}
    private void confirmEmailSend(String recipient,
                              String productName,
                              String quantity) {

    Stage stage = new Stage();

    Label title = new Label("Confirm Request");
    title.setStyle("-fx-font-size:20; -fx-font-weight:bold;");

    Label msg = new Label("Do you really want to send the email?");
    msg.setStyle("-fx-font-size:14;");

    Button confirmBtn = new Button("Confirm");
    Button cancelBtn = new Button("Cancel");

    confirmBtn.setStyle(
            "-fx-background-color:#8B5E34;" +
            "-fx-text-fill:white;" +
            "-fx-font-size:14;"
    );

    cancelBtn.setStyle(
            "-fx-background-color:#D7B98E;" +
            "-fx-text-fill:black;" +
            "-fx-font-size:14;"
    );

    confirmBtn.setOnAction(e -> {

        stage.close();
        
        sendEmail(recipient, productName, quantity);   // ⭐ EMAIL SEND

        // Notification add
        NotificationService.addNotification(
            "Inventory request sent to " + recipient +
            "\nProduct : " + productName +
            "\nQuantity : " + quantity
        );

        showSuccessMessage();
    });

    cancelBtn.setOnAction(e -> stage.close());

    HBox buttons = new HBox(15, confirmBtn, cancelBtn);
    buttons.setAlignment(Pos.CENTER);

    VBox root = new VBox(20, title, msg, buttons);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(30));

    root.setStyle(
            "-fx-background-color:#F5DEB3;" +
            "-fx-border-color:#8B5E34;" +
            "-fx-border-width:2;" +
            "-fx-background-radius:10;"
    );

    stage.setScene(new Scene(root,350,180));
    stage.show();
}
    private void sendEmail(String recipient, String productName, String quantity) {

    String toEmail = "";

    try(Connection con = db.DBConnection.getConnection();
        Statement st = con.createStatement()) {

        String query;

        // Fetch email from database
        if(recipient.equals("HOD")) {
            query = "SELECT email FROM user WHERE role_id = 2";
        } else {
            query = "SELECT email FROM user WHERE role_id = 3";
        }

        ResultSet rs = st.executeQuery(query);

        if(rs.next()){
            toEmail = rs.getString("email");
        }

        System.out.println("Recipient Role : " + recipient);
        System.out.println("Email fetched from DB : " + toEmail);

    } catch(Exception e){
        e.printStackTrace();
    }

    // Email subject
    String subject = "Inventory Request";

    // Email body
    String body = "Respected " + recipient + ",\n\n"
            + "An inventory request has been generated.\n\n"
            + "Product Name : " + productName + "\n"
            + "Quantity : " + quantity + "\n\n"
            + "Regards,\nInventory Manager";

    try {

        Properties props = new Properties();

        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.ssl.trust","smtp.gmail.com");

        Session session = Session.getInstance(props,
            new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication(
                        "g7447931@gmail.com",   // sender email
                        "gqkyzcrkzjngfzow"      // app password
                    );
                }
        });
        session.setDebug(true);   // ⭐ SMTP debug

        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress("g7447931@gmail.com"));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
        );

        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);

        System.out.println("Email Sent Successfully to : " + toEmail);
        showSuccessMessage();
        
        if(toEmail.isEmpty()){
            System.out.println("No email found for role : " + recipient);
            return;
        }

    } catch(Exception e){
        e.printStackTrace();
    }
}
    private void showSuccessMessage() {

    Stage stage = new Stage();

    Label title = new Label("Email Sent");
    title.setStyle("-fx-font-size:20; -fx-font-weight:bold;");

    Label msg = new Label("Inventory request email has been sent successfully.");
    msg.setWrapText(true);

    Button ok = new Button("OK");

    ok.setStyle(
            "-fx-background-color:#8B5E34;" +
            "-fx-text-fill:white;" +
            "-fx-font-size:14;"
    );

    ok.setOnAction(e -> stage.close());

    VBox root = new VBox(20, title, msg, ok);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(30));

    root.setStyle(
            "-fx-background-color:#F5DEB3;" +
            "-fx-border-color:#8B5E34;" +
            "-fx-border-width:2;" +
            "-fx-background-radius:10;"
    );

    stage.setScene(new Scene(root,350,180));
    stage.show();
}
    public Scene getScene() {
        return scene;
    }
}

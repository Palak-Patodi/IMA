package ui;

import com.mycompany.inventory.Inventory;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.*;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AddInventoryUI {

    private final Scene scene;

    public AddInventoryUI(Inventory app) {

        GridPane grid = new GridPane();
        Label formTitle = new Label("ADD INVENTORY FORM");
        formTitle.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 26));
        formTitle.setStyle("-fx-text-fill: #3E2723;");

        grid.setPadding(new Insets(30));
        grid.setHgap(40);
        grid.setVgap(18);
        grid.setStyle("-fx-background-color: #E2C49F;");

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(220);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(300);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPrefWidth(220);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPrefWidth(300);

        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        Font labelFont = Font.font("Arial", FontWeight.BOLD, 16);

        String fieldStyle = """
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #C49A6C;
            -fx-border-width: 1.5;
            -fx-background-color: #FFF6E9;
            -fx-font-size: 15px;
        """;

        // ---------------- LEFT SIDE ----------------
        Label orderNoLbl = new Label("Order No");
        orderNoLbl.setFont(labelFont);

        ComboBox<String> orderNo = new ComboBox<>();
        setupComboBox(orderNo,
                "SELECT order_no FROM order_table WHERE order_status='IN_PROCESS'",
                fieldStyle);

        Label billLbl = new Label("Bill No");
        billLbl.setFont(labelFont);

        Label amountLbl = new Label("Bill Amount");
        amountLbl.setFont(labelFont);

        TextField billAmount = new TextField();
        billAmount.setPrefSize(320, 42);
        billAmount.setStyle(fieldStyle);

        TextField billNo = new TextField();
        billNo.setPrefSize(320, 42);
        billNo.setStyle(fieldStyle);

        grid.add(orderNoLbl, 0, 0);
        grid.add(orderNo, 1, 0);
        
        grid.add(billLbl, 0, 1);
        grid.add(billNo, 1, 1);

        // ---------------- RIGHT SIDE ----------------
        Label dateLbl = new Label("Received Date");
        dateLbl.setFont(labelFont);

        DatePicker orderDate = new DatePicker();
        orderDate.setPrefSize(320, 42);
        orderDate.setStyle(fieldStyle);

        Label qtyLbl = new Label("Quantity Received");
        qtyLbl.setFont(labelFont);

        TextField qtyReceived = new TextField();
        qtyReceived.setPrefSize(320, 42);
        qtyReceived.setStyle(fieldStyle);

        grid.add(amountLbl, 2, 0);
        grid.add(billAmount, 3, 0);

        grid.add(dateLbl, 2, 1);
        grid.add(orderDate, 3, 1);

        grid.add(qtyLbl, 2, 2);
        grid.add(qtyReceived, 3, 2);


        // ---------------- BUTTONS ----------------
        Button save = new Button("SAVE");
        Button back = new Button("BACK");

        String buttonStyle = """
            -fx-background-color: #8B5E34;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-padding: 12 35 12 35;
        """;

        save.setStyle(buttonStyle);
        back.setStyle(buttonStyle);
        save.setPrefSize(180, 50);
        back.setPrefSize(180, 50);

        back.setOnAction(e -> app.showDashboard());

        HBox buttonBox = new HBox(40);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(save, back);
        grid.add(buttonBox, 2, 6, 2, 1);

        // ---------------- DATABASE LOGIC ----------------
        save.setOnAction(e -> {
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inventory_system",
                    "root",
                    "Somya@2005")) {

                if (orderNo.getValue() == null)
                    throw new Exception("Select Order No");

                if (billNo.getText().isEmpty())
                    throw new Exception("Enter Bill No");

                if (billAmount.getText().isEmpty())
                    throw new Exception("Enter Bill Amount");

                if (qtyReceived.getText().isEmpty())
                    throw new Exception("Enter Quantity Received");

                if (orderDate.getValue() == null)
                    throw new Exception("Select Received Date");

                // 1️⃣ Get entry_id, pid, sid from order_table
                // 1️⃣ Get entry_id, pid, sid from order_table
                PreparedStatement getOrder = con.prepareStatement(
                        "SELECT entry_id, pid, sid FROM order_table WHERE order_no=?");

                getOrder.setString(1, orderNo.getValue());   // 👈 ADD IT HERE

                ResultSet rs = getOrder.executeQuery();
                if (!rs.next()) {
                    throw new Exception("Order not found!");
                }


                int entryId = rs.getInt("entry_id");
                String pid = rs.getString("pid");
                int sid = rs.getInt("sid");


                // 2️⃣ Insert into bill_invoice
                PreparedStatement insertBill = con.prepareStatement(
                        "INSERT INTO bill_invoice " +
                                "(bill_no, bill_received_by, bill_amount, " +
                                "pid, sid, entry_id, qty_received, received_date, bill_status, record_status) " +
                                "VALUES (?,?,?,?,?,?,?,?,'INCOMPLETE','ACTIVE')");

                insertBill.setString(1, billNo.getText());
                insertBill.setString(2, "Inventory Manager");
                insertBill.setDouble(3, Double.parseDouble(billAmount.getText()));
                insertBill.setString(4, pid);
                insertBill.setInt(5, sid);
                insertBill.setInt(6, entryId);
                insertBill.setInt(7, Integer.parseInt(qtyReceived.getText()));
                insertBill.setDate(8, java.sql.Date.valueOf(orderDate.getValue()));

                insertBill.executeUpdate();

                new Alert(Alert.AlertType.INFORMATION,
                        "Bill added successfully").show();

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        });



        VBox root = new VBox();
        root.setPadding(new Insets(50));
        root.setSpacing(35);
        root.setStyle("-fx-background-color: #E2C49F;");

        VBox cardContent = new VBox(30);
        cardContent.setAlignment(Pos.TOP_CENTER);
        cardContent.getChildren().addAll(formTitle, grid);

        StackPane card = new StackPane(cardContent);
        card.setPadding(new Insets(40));
        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.92);
            -fx-background-radius: 20;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 30, 0.4, 0, 10);
        """);

        root.getChildren().add(card);
        scene = new Scene(root, 1024, 768);
    }

    public Scene getScene() {
        return scene;
    }

    private void loadData(ComboBox<String> combo, String query) {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/inventory_system",
                "root",
                "Somya@2005");
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                combo.getItems().add(rs.getString(1));
            }
        } catch (Exception e) {
        }
    }

    private void setupComboBox(ComboBox<String> combo, String query, String fieldStyle) {

        combo.setEditable(true);
        combo.setPrefSize(320, 42);
        combo.setStyle(fieldStyle);

        loadData(combo, query);

        combo.setOnMouseEntered(e ->
                combo.setStyle(fieldStyle + "-fx-border-color: #8B5E34;")
        );

        combo.setOnMouseExited(e ->
                combo.setStyle(fieldStyle)
        );

        combo.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            combo.show();
        });
    }
}

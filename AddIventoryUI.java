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
        grid.setStyle("""
            -fx-background-color: #E2C49F;
        """);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(220);  // wider for full labels

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


        // ---------- LEFT SIDE FIELDS ----------
        ComboBox<String> supplierName = new ComboBox<>();
        ComboBox<String> email = new ComboBox<>();
        ComboBox<String> address = new ComboBox<>();
        ComboBox<String> contact = new ComboBox<>();
        ComboBox<String> ptypeId = new ComboBox<>();
        ComboBox<String> productName = new ComboBox<>();
        ComboBox<String> productDesc = new ComboBox<>();
        ComboBox<String> billNo = new ComboBox<>();
        
        setupComboBox(supplierName, "SELECT DISTINCT name FROM supplier", fieldStyle);

        setupComboBox(email, "SELECT DISTINCT email FROM supplier", fieldStyle);

        setupComboBox(address, "SELECT DISTINCT address FROM supplier", fieldStyle);

        setupComboBox(contact, "SELECT DISTINCT contact_no FROM supplier", fieldStyle);

        setupComboBox(ptypeId, "SELECT DISTINCT ptype_id FROM supplier", fieldStyle);

        setupComboBox(productName, "SELECT DISTINCT product_name FROM product", fieldStyle);

        setupComboBox(productDesc, "SELECT DISTINCT description FROM product", fieldStyle);

        setupComboBox(billNo, "SELECT DISTINCT bill_no FROM bill_invoice", fieldStyle);
        // --------- STYLE ALL TEXT FIELDS ----------
        supplierName.setPrefSize(320, 42);
        email.setPrefSize(320, 42);
        address.setPrefSize(320, 42);
        contact.setPrefSize(320, 42);
        ptypeId.setPrefSize(320, 42);
        productDesc.setPrefSize(320, 42);
        billNo.setPrefSize(320, 42);

        supplierName.setStyle(fieldStyle);
        email.setStyle(fieldStyle);
        address.setStyle(fieldStyle);
        contact.setStyle(fieldStyle);
        ptypeId.setStyle(fieldStyle);
        productDesc.setStyle(fieldStyle);
        billNo.setStyle(fieldStyle);


        Control[] fields = {
            supplierName, email, address, contact,
            ptypeId, productName, productDesc, billNo
        };

        String[] labels = {
            "Supplier Name", "Email", "Address", "Contact number",
            "Product Type ID", "Product Name",
            "Product Desc", "Bill Invoice"
        };

        for (int i = 0; i < labels.length; i++) {
            Label l = new Label(labels[i]);
            l.setFont(labelFont);
            grid.add(l, 0, i);
            grid.add(fields[i], 1, i);
        }

        // ---------- RIGHT SIDE ----------
        Label billByLbl = new Label("Bill Received by");
        billByLbl.setFont(labelFont);
        ComboBox<String> billBy = new ComboBox<>();
        billBy.setPrefSize(320, 42);
        billBy.setStyle(fieldStyle);
        setupComboBox(billBy, "SELECT DISTINCT bill_received_by FROM bill_invoice", fieldStyle);

        Label dateLbl = new Label("Purchase Date");
        dateLbl.setFont(labelFont);
        DatePicker purchaseDate = new DatePicker();
        purchaseDate.setPrefSize(320, 42);
        purchaseDate.setStyle(fieldStyle);

        Label qtyLbl = new Label("Quantity");
        qtyLbl.setFont(labelFont);
        ComboBox<String> qty = new ComboBox<>();
        qty.setPrefSize(320, 42);
        qty.setStyle(fieldStyle);
        setupComboBox(qty, "SELECT DISTINCT qty_ordered FROM bill_invoice", fieldStyle);

        Label amountLbl = new Label("Bill Amount");
        amountLbl.setFont(labelFont);
        ComboBox<String> amount = new ComboBox<>();
        amount.setPrefSize(320, 42);
        amount.setStyle(fieldStyle);
        setupComboBox(amount, "SELECT DISTINCT bill_amount FROM bill_invoice", fieldStyle);

        grid.add(billByLbl, 2, 1);
        grid.add(billBy, 3, 1);

        grid.add(dateLbl, 2, 2);
        grid.add(purchaseDate, 3, 2);

        grid.add(qtyLbl, 2, 3);
        grid.add(qty, 3, 3);

        grid.add(amountLbl, 2, 4);
        grid.add(amount, 3, 4);
        
        // ---------- SAVE BUTTON ----------
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

        // ------------BUTTON BOX -----------
        HBox buttonBox = new HBox(40);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(save, back);
        // Span across right side
        grid.add(buttonBox, 2,6,2,1);

        save.setOnAction(e -> {
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inventory_system",
                    "root",
                    "Somya@2005")) {

                con.setAutoCommit(false);

                // 1️⃣ supplier
                PreparedStatement ps1 = con.prepareStatement(
                        "INSERT INTO supplier(name,email,contact_no,address,ptype_id) VALUES(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps1.setString(1, supplierName.getEditor().getText());
                ps1.setString(2, email.getEditor().getText());
                ps1.setString(3, contact.getEditor().getText());
                ps1.setString(4, address.getEditor().getText());
                ps1.setString(5, ptypeId.getEditor().getText());
                ps1.executeUpdate();

                ResultSet rs1 = ps1.getGeneratedKeys();
                rs1.next();
                int sid = rs1.getInt(1);

                // 2️⃣ product
                PreparedStatement ps2 = con.prepareStatement(
                        "INSERT INTO product(product_name,description,qty_in_stock,min_qty_required,qty_updated_date,ptype_id) " +
                                "VALUES(?,?,?,5,CURDATE(),?)",
                        Statement.RETURN_GENERATED_KEYS);
               ps2.setString(1, productName.getEditor().getText());
                ps2.setString(2, productDesc.getEditor().getText());
                ps2.setInt(3, Integer.parseInt(qty.getEditor().getText()));
                ps2.setString(4, ptypeId.getEditor().getText());
                ps2.executeUpdate();

                ResultSet rs2 = ps2.getGeneratedKeys();
                rs2.next();
                int pid = rs2.getInt(1);

                // 3️⃣ supplies
                PreparedStatement ps3 = con.prepareStatement(
                        "INSERT INTO supplies(sid,pid) VALUES(?,?)");
                ps3.setInt(1, sid);
                ps3.setInt(2, pid);
                ps3.executeUpdate();

                // 4️⃣ bill
                PreparedStatement ps4 = con.prepareStatement(
                        "INSERT INTO bill_invoice(bill_no,date,bill_received_by,qty_ordered,bill_amount,pid,sid) " +
                                "VALUES(?,?,?,?,?,?,?)");
                ps4.setString(1, billNo.getEditor().getText());
                ps4.setDate(2, java.sql.Date.valueOf(purchaseDate.getValue()));
                ps4.setString(3, billBy.getEditor().getText());
                ps4.setInt(4, Integer.parseInt(qty.getEditor().getText()));
                ps4.setDouble(5, Double.parseDouble(amount.getEditor().getText()));
                ps4.setInt(6, pid);
                ps4.setInt(7, sid);
                ps4.executeUpdate();

                con.commit();

                new Alert(Alert.AlertType.INFORMATION, "Inventory added successfully").show();

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        });

        // ---------- ROOT ----------
        VBox root = new VBox();
        root.setPadding(new Insets(50));
        root.setSpacing(35);
        root.setStyle("-fx-background-color: #E2C49F;");


        // Card wrapper
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
    
            private void loadData(ComboBox<String> combo, String query) 
            {
                try (Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/inventory_system",
                        "root",
                        "Somya@2005");
                     PreparedStatement ps = con.prepareStatement(query);
                     ResultSet rs = ps.executeQuery()) 
                    {

                    while (rs.next()) {
                        combo.getItems().add(rs.getString(1));
                        }
                    } 
                catch (Exception e) {
                }
            }
        private void setupComboBox(ComboBox<String> combo, String query, String fieldStyle) 
        {

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

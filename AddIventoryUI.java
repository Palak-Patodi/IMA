package ui;

import com.mycompany.inventory.Inventory;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.*;

public class AddInventoryUI {

    private final Scene scene;

    public AddInventoryUI(Inventory app) {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setHgap(40);
        grid.setVgap(18);
        grid.setStyle("-fx-background-color:#d2b48c;");

        Font labelFont = Font.font("Arial", FontWeight.BOLD, 16);

        // ---------- LEFT SIDE FIELDS ----------
        TextField supplierName = new TextField();
        TextField email = new TextField();
        TextField address = new TextField();
        TextField contact = new TextField();
        TextField ptypeId = new TextField();
        TextField productName = new TextField();
        TextField productDesc = new TextField();
        TextField billNo = new TextField();

        TextField[] fields = {
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
            fields[i].setPrefWidth(280);
            grid.add(l, 0, i);
            grid.add(fields[i], 1, i);
        }

        // ---------- RIGHT SIDE ----------
        Label billByLbl = new Label("Bill Received by");
        billByLbl.setFont(labelFont);
        TextField billBy = new TextField();

        Label dateLbl = new Label("Purchase Date (YYYY-MM-DD)");
        dateLbl.setFont(labelFont);
        TextField purchaseDate = new TextField();

        Label qtyLbl = new Label("Quantity Ordered");
        qtyLbl.setFont(labelFont);
        TextField qty = new TextField();

        Label amountLbl = new Label("Bill Amount");
        amountLbl.setFont(labelFont);
        TextField amount = new TextField();

        grid.add(billByLbl, 2, 1);
        grid.add(billBy, 3, 1);

        grid.add(dateLbl, 2, 2);
        grid.add(purchaseDate, 3, 2);

        grid.add(qtyLbl, 2, 3);
        grid.add(qty, 3, 3);

        grid.add(amountLbl, 2, 4);
        grid.add(amount, 3, 4);

        // ---------- SAVE BUTTON ----------
        Button save = new Button("Save");
        save.setPrefSize(120, 40);

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
                ps1.setString(1, supplierName.getText());
                ps1.setString(2, email.getText());
                ps1.setString(3, contact.getText());
                ps1.setString(4, address.getText());
                ps1.setString(5, ptypeId.getText());
                ps1.executeUpdate();

                ResultSet rs1 = ps1.getGeneratedKeys();
                rs1.next();
                int sid = rs1.getInt(1);

                // 2️⃣ product
                PreparedStatement ps2 = con.prepareStatement(
                        "INSERT INTO product(product_name,description,qty_in_stock,min_qty_required,qty_updated_date,ptype_id) " +
                                "VALUES(?,?,?,5,CURDATE(),?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps2.setString(1, productName.getText());
                ps2.setString(2, productDesc.getText());
                ps2.setInt(3, Integer.parseInt(qty.getText()));
                ps2.setString(4, ptypeId.getText());
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
                ps4.setString(1, billNo.getText());
                ps4.setDate(2, Date.valueOf(purchaseDate.getText()));
                ps4.setString(3, billBy.getText());
                ps4.setInt(4, Integer.parseInt(qty.getText()));
                ps4.setDouble(5, Double.parseDouble(amount.getText()));
                ps4.setInt(6, pid);
                ps4.setInt(7, sid);
                ps4.executeUpdate();

                con.commit();

                new Alert(Alert.AlertType.INFORMATION, "Inventory added successfully").show();

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        });

        // ---------- BACK BUTTON ----------
        Button back = new Button("Back");
        back.setPrefSize(120, 40);
        back.setOnAction(e -> app.showDashboard());

        grid.add(save, 2, 6);
        grid.add(back, 3, 6);

        scene = new Scene(grid, 1024, 768);
    }

    public Scene getScene() {
        return scene;
    }
}

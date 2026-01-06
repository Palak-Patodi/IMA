package ui;

import com.mycompany.inventory.Inventory;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Product;

import java.sql.*;
import java.time.LocalDate;

public class IssueUI {

    private final Scene scene;

    public IssueUI(Inventory app) {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(40));
        grid.setHgap(40);
        grid.setVgap(30);
        grid.setStyle("-fx-background-color:#d2b48c;");

        Font labelFont = Font.font("Arial", FontWeight.BOLD, 18);
        Font fieldFont = Font.font("Arial", 16);

        ComboBox<Product> productBox = new ComboBox<>();
        productBox.setPrefWidth(420);
        loadProducts(productBox);

        TextField issueTo = new TextField();
        TextField issuedBy = new TextField();
        TextField dept = new TextField();
        TextField qty = new TextField();
        TextField reason = new TextField();
        DatePicker date = new DatePicker();

        TextField[] fields = { issueTo, issuedBy, dept, qty, reason };
        for (TextField f : fields) {
            f.setPrefWidth(420);
            f.setPrefHeight(40);
            f.setFont(fieldFont);
        }

        int row = 0;

        grid.add(styledLabel("Product Name", labelFont), 0, row);
        grid.add(productBox, 1, row++);

        grid.add(styledLabel("Issued To", labelFont), 0, row);
        grid.add(issueTo, 1, row++);

        grid.add(styledLabel("Issued By", labelFont), 0, row);
        grid.add(issuedBy, 1, row++);

        grid.add(styledLabel("Dept Name", labelFont), 0, row);
        grid.add(dept, 1, row++);

        grid.add(styledLabel("Quantity Issued", labelFont), 0, row);
        grid.add(qty, 1, row++);

        grid.add(styledLabel("Reason", labelFont), 0, row);
        grid.add(reason, 1, row++);

        grid.add(styledLabel("Issued Date", labelFont), 0, row);
        grid.add(date, 1, row++);

        Button issueBtn = new Button("Issue");
        issueBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        issueBtn.setOnAction(e ->
                issueProduct(
                        productBox.getValue(),
                        issueTo.getText(),
                        issuedBy.getText(),
                        dept.getText(),
                        qty.getText(),
                        reason.getText(),
                        date.getValue()
                )
        );

        Button back = new Button("Back");
        back.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        back.setOnAction(e -> app.showDashboard());

        grid.add(issueBtn, 1, row);
        grid.add(back, 1, row + 1);

        scene = new Scene(grid, 1024, 768);
    }

    public Scene getScene() {
        return scene;
    }

    // ---------------- DATABASE LOGIC ----------------

    private void issueProduct(Product product, String to, String by, String dept,
                              String qtyStr, String reason, LocalDate date) {

        if (product == null || to.isEmpty() || by.isEmpty()
                || dept.isEmpty() || qtyStr.isEmpty() || date == null) {
            showAlert("All fields are mandatory");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            showAlert("Quantity must be a number");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            PreparedStatement check =
                    con.prepareStatement("SELECT qty_in_stock FROM product WHERE pid=?");
            check.setInt(1, product.getPid());
            ResultSet rs = check.executeQuery();

            if (!rs.next() || rs.getInt("qty_in_stock") < qty) {
                showAlert("Insufficient stock!");
                con.rollback();
                return;
            }

            PreparedStatement insert =
                    con.prepareStatement(
                            "INSERT INTO issue (pid, issue_to, issued_by, dept_name, qty_issued, reason, date) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
            insert.setInt(1, product.getPid());
            insert.setString(2, to);
            insert.setString(3, by);
            insert.setString(4, dept);
            insert.setInt(5, qty);
            insert.setString(6, reason);
            insert.setDate(7, Date.valueOf(date));
            insert.executeUpdate();

            PreparedStatement update =
                    con.prepareStatement(
                            "UPDATE product SET qty_in_stock = qty_in_stock - ? WHERE pid=?");
            update.setInt(1, qty);
            update.setInt(2, product.getPid());
            update.executeUpdate();

            con.commit();
            showAlert("Product issued successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Database error occurred");
        }
    }

    private void loadProducts(ComboBox<Product> box) {

        ObservableList<Product> list = FXCollections.observableArrayList();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs =
                     st.executeQuery("SELECT pid, product_name, qty_in_stock FROM product")) {

            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("pid"),
                        rs.getString("product_name"),
                        rs.getInt("qty_in_stock")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        box.setItems(list);
    }

    private Label styledLabel(String text, Font font) {
        Label l = new Label(text);
        l.setFont(font);
        return l;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}

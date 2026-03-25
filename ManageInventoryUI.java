package ui;

import com.mycompany.inventory.Inventory;
import db.DBConnection;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.sql.*;

public class ManageInventoryUI {

    private Scene scene;

    // ===================== STYLE CONSTANTS =====================
    private static final String BG_COLOR   = "#E2C49F";
    private static final String CARD_STYLE = """
            -fx-background-color: rgba(255,255,255,0.92);
            -fx-background-radius: 20;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 30, 0.4, 0, 10);
            """;
    private static final String FIELD_STYLE = """
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #C49A6C;
            -fx-border-width: 1.5;
            -fx-background-color: #FFF6E9;
            -fx-font-size: 15px;
            """;
    private static final String BTN_BROWN =
            "-fx-background-color:#8B5E34; -fx-text-fill:white; " +
            "-fx-font-size:14px; -fx-font-weight:bold; " +
            "-fx-background-radius:8; -fx-padding:10 28 10 28;";
    private static final String BTN_RED =
            "-fx-background-color:#c0392b; -fx-text-fill:white; " +
            "-fx-font-size:14px; -fx-font-weight:bold; " +
            "-fx-background-radius:8; -fx-padding:10 28 10 28;";
    private static final String TAB_STYLE =
            "-fx-font-size:15px; -fx-font-weight:bold;";

    // ===================== CONSTRUCTOR =====================
    public ManageInventoryUI(Inventory app) {

        Label title = new Label("Manage Inventory Data");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 26));
        title.setStyle("-fx-text-fill:#3E2723;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-height:40px;");

        Tab productTab   = new Tab("  Product  ");
        Tab supplierTab  = new Tab("  Supplier  ");
        Tab orderBillTab = new Tab("  Order & Bill  ");

        productTab.setStyle(TAB_STYLE);
        supplierTab.setStyle(TAB_STYLE);
        orderBillTab.setStyle(TAB_STYLE);

        productTab.setContent(buildProductContent());
        supplierTab.setContent(buildSupplierContent());
        orderBillTab.setContent(buildOrderBillContent());

        tabPane.getTabs().addAll(productTab, supplierTab, orderBillTab);

        Button addBtn  = new Button("+ Add New");
        Button backBtn = new Button("Back");

        addBtn.setStyle(BTN_BROWN);
        backBtn.setStyle(BTN_BROWN);

        // Hide Add on Order & Bill tab
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, now) ->
                        addBtn.setVisible(!now.equals(orderBillTab)));

        addBtn.setOnAction(e -> {
            Tab current = tabPane.getSelectionModel().getSelectedItem();
            if (current.equals(productTab)) {
                showAddProductForm(
                    (VBox)((ScrollPane) productTab.getContent()).getContent());
            } else if (current.equals(supplierTab)) {
                showAddSupplierForm(
                    (VBox)((ScrollPane) supplierTab.getContent()).getContent());
            }
        });

        backBtn.setOnAction(e -> app.showDashboard());

        HBox bottomBar = new HBox(20, addBtn, backBtn);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(15, 30, 15, 30));
        bottomBar.setStyle(
                "-fx-background-color:#d9b896; -fx-background-radius:0 0 15 15;");

        VBox card = new VBox(20, title, tabPane);
        card.setPadding(new Insets(30));
        card.setStyle(CARD_STYLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG_COLOR + ";");
        root.setCenter(card);
        root.setBottom(bottomBar);
        BorderPane.setMargin(card, new Insets(30, 30, 10, 30));

        scene = new Scene(root, 1100, 720);
    }

    // =====================================================================
    //  PRODUCT TAB
    // =====================================================================

    private static class PRow {
        StringProperty  pid    = new SimpleStringProperty();
        StringProperty  name   = new SimpleStringProperty();
        IntegerProperty qty    = new SimpleIntegerProperty();
        StringProperty  desc   = new SimpleStringProperty();
        IntegerProperty minQty = new SimpleIntegerProperty();
        StringProperty  ptype  = new SimpleStringProperty();

        PRow(String pid, String name, int qty, String desc,
             int minQty, String ptype) {
            this.pid.set(pid);     this.name.set(name);
            this.qty.set(qty);     this.desc.set(desc);
            this.minQty.set(minQty); this.ptype.set(ptype);
        }
    }

    private ScrollPane buildProductContent() {
        TableView<PRow> table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size:14px;");

        TableColumn<PRow,String>  pidCol  = col("PID",         150);
        TableColumn<PRow,String>  nameCol = col("Name",        200);
        TableColumn<PRow,Integer> qtyCol  = colInt("Stock",    100);
        TableColumn<PRow,String>  descCol = col("Description", 250);
        TableColumn<PRow,Integer> minCol  = colInt("Min Qty",  100);
        TableColumn<PRow,String>  ptypeCol= col("Type",        120);

        pidCol.setCellValueFactory(d -> d.getValue().pid);
        nameCol.setCellValueFactory(d -> d.getValue().name);
        qtyCol.setCellValueFactory(d -> d.getValue().qty.asObject());
        descCol.setCellValueFactory(d -> d.getValue().desc);
        minCol.setCellValueFactory(d -> d.getValue().minQty.asObject());
        ptypeCol.setCellValueFactory(d -> d.getValue().ptype);

        // ✅ Editable columns — PID and Type are NOT editable (PID is PK, Type is FK)
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(e -> {
            e.getRowValue().name.set(e.getNewValue());
            updateProduct(e.getRowValue());
        });

        descCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descCol.setOnEditCommit(e -> {
            e.getRowValue().desc.set(e.getNewValue());
            updateProduct(e.getRowValue());
        });

        qtyCol.setCellFactory(TextFieldTableCell.forTableColumn(
                new javafx.util.converter.IntegerStringConverter()));
        qtyCol.setOnEditCommit(e -> {
            e.getRowValue().qty.set(e.getNewValue());
            updateProduct(e.getRowValue());
        });

        minCol.setCellFactory(TextFieldTableCell.forTableColumn(
                new javafx.util.converter.IntegerStringConverter()));
        minCol.setOnEditCommit(e -> {
            e.getRowValue().minQty.set(e.getNewValue());
            updateProduct(e.getRowValue());
        });

        // ✅ No delete column for Product
        table.getColumns().addAll(pidCol, nameCol, qtyCol, descCol, minCol, ptypeCol);

        loadProducts(table);

        VBox box = new VBox(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.setUserData(table);

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:transparent;");
        return sp;
    }

    private void loadProducts(TableView<PRow> table) {
        table.getItems().clear();
        String sql = "SELECT pid, product_name, qty_in_stock, description, " +
                     "min_qty_required, ptype_id FROM product";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                table.getItems().add(new PRow(
                        rs.getString("pid"),
                        rs.getString("product_name"),
                        rs.getInt("qty_in_stock"),
                        rs.getString("description"),
                        rs.getInt("min_qty_required"),
                        rs.getString("ptype_id")
                ));
            }
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateProduct(PRow row) {
        String sql = "UPDATE product SET product_name=?, qty_in_stock=?, " +
                     "description=?, min_qty_required=? WHERE pid=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, row.name.get());
            ps.setInt(2,    row.qty.get());
            ps.setString(3, row.desc.get());
            ps.setInt(4,    row.minQty.get());
            ps.setString(5, row.pid.get());
            ps.executeUpdate();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void showAddProductForm(VBox contentBox) {
        @SuppressWarnings("unchecked")
        TableView<PRow> table = (TableView<PRow>) contentBox.getUserData();

        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Add New Product");
        dlg.setHeaderText("Enter product details");

        GridPane g = formGrid();

        TextField pidF   = field();
        TextField nameF  = field();
        TextField qtyF   = field();
        TextField descF  = field();
        TextField minF   = field();
        ComboBox<String> ptypeBox = new ComboBox<>();
        ptypeBox.getItems().addAll("STN", "FUR", "CS", "Misc");
        ptypeBox.setStyle(FIELD_STYLE);
        ptypeBox.setPrefSize(260, 42);

        g.addRow(0, lbl("PID"),         pidF);
        g.addRow(1, lbl("Name"),        nameF);
        g.addRow(2, lbl("Description"), descF);
        g.addRow(3, lbl("Stock"),       qtyF);
        g.addRow(4, lbl("Min Qty"),     minF);
        g.addRow(5, lbl("Type"),        ptypeBox);

        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().setStyle("-fx-background-color:#F5DEB3;");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            String sql = "INSERT INTO product " +
                    "(pid, product_name, description, qty_in_stock, " +
                    "min_qty_required, ptype_id) VALUES (?,?,?,?,?,?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pidF.getText().trim());
                ps.setString(2, nameF.getText().trim());
                ps.setString(3, descF.getText().trim());
                ps.setInt(4,    Integer.parseInt(qtyF.getText().trim()));
                ps.setInt(5,    Integer.parseInt(minF.getText().trim()));
                ps.setString(6, ptypeBox.getValue());
                ps.executeUpdate();
                loadProducts(table);
            } catch (Exception e) { showError(e.getMessage()); }
        });
    }

    // =====================================================================
    //  SUPPLIER TAB
    // =====================================================================

    private static class SRow {
        IntegerProperty sid     = new SimpleIntegerProperty();
        StringProperty  name    = new SimpleStringProperty();
        StringProperty  email   = new SimpleStringProperty();
        StringProperty  contact = new SimpleStringProperty();
        StringProperty  address = new SimpleStringProperty();
        StringProperty  ptype   = new SimpleStringProperty();

        SRow(int sid, String name, String email, String contact,
             String address, String ptype) {
            this.sid.set(sid);       this.name.set(name);
            this.email.set(email);   this.contact.set(contact);
            this.address.set(address); this.ptype.set(ptype);
        }
    }

    private ScrollPane buildSupplierContent() {
        TableView<SRow> table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size:14px;");

        TableColumn<SRow,Integer> sidCol     = colInt("SID",    80);
        TableColumn<SRow,String>  nameCol    = col("Name",     180);
        TableColumn<SRow,String>  emailCol   = col("Email",    200);
        TableColumn<SRow,String>  contactCol = col("Contact",  140);
        TableColumn<SRow,String>  addrCol    = col("Address",  220);
        TableColumn<SRow,String>  ptypeCol   = col("Type",     100);

        sidCol.setCellValueFactory(d -> d.getValue().sid.asObject());
        nameCol.setCellValueFactory(d -> d.getValue().name);
        emailCol.setCellValueFactory(d -> d.getValue().email);
        contactCol.setCellValueFactory(d -> d.getValue().contact);
        addrCol.setCellValueFactory(d -> d.getValue().address);
        ptypeCol.setCellValueFactory(d -> d.getValue().ptype);

        // ✅ Editable columns — SID and Type are NOT editable (SID is PK, Type is FK)
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(e -> {
            e.getRowValue().name.set(e.getNewValue());
            updateSupplier(e.getRowValue());
        });

        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setOnEditCommit(e -> {
            e.getRowValue().email.set(e.getNewValue());
            updateSupplier(e.getRowValue());
        });

        contactCol.setCellFactory(TextFieldTableCell.forTableColumn());
        contactCol.setOnEditCommit(e -> {
            e.getRowValue().contact.set(e.getNewValue());
            updateSupplier(e.getRowValue());
        });

        addrCol.setCellFactory(TextFieldTableCell.forTableColumn());
        addrCol.setOnEditCommit(e -> {
            e.getRowValue().address.set(e.getNewValue());
            updateSupplier(e.getRowValue());
        });

        // ✅ No delete column for Supplier
        table.getColumns().addAll(
                sidCol, nameCol, emailCol, contactCol, addrCol, ptypeCol);

        loadSuppliers(table);

        VBox box = new VBox(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.setUserData(table);

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:transparent;");
        return sp;
    }

    private void loadSuppliers(TableView<SRow> table) {
        table.getItems().clear();
        String sql =
            "SELECT sid, name, email, contact_no, address, ptype_id FROM supplier";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                table.getItems().add(new SRow(
                        rs.getInt("sid"),
                        rs.getString("name"),
                        rs.getString("email") == null ? "" : rs.getString("email"),
                        rs.getString("contact_no"),
                        rs.getString("address"),
                        rs.getString("ptype_id")
                ));
            }
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateSupplier(SRow row) {
        String sql =
            "UPDATE supplier SET name=?, email=?, contact_no=?, address=? WHERE sid=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, row.name.get());
            ps.setString(2, row.email.get());
            ps.setString(3, row.contact.get());
            ps.setString(4, row.address.get());
            ps.setInt(5,    row.sid.get());
            ps.executeUpdate();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void showAddSupplierForm(VBox contentBox) {
        @SuppressWarnings("unchecked")
        TableView<SRow> table = (TableView<SRow>) contentBox.getUserData();

        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Add New Supplier");
        dlg.setHeaderText("Enter supplier details");

        GridPane g = formGrid();

        TextField nameF    = field();
        TextField emailF   = field();
        TextField contactF = field();
        TextField addrF    = field();
        ComboBox<String> ptypeBox = new ComboBox<>();
        ptypeBox.getItems().addAll("STN", "FUR", "CS", "Misc");
        ptypeBox.setStyle(FIELD_STYLE);
        ptypeBox.setPrefSize(260, 42);

        g.addRow(0, lbl("Name"),    nameF);
        g.addRow(1, lbl("Email"),   emailF);
        g.addRow(2, lbl("Contact"), contactF);
        g.addRow(3, lbl("Address"), addrF);
        g.addRow(4, lbl("Type"),    ptypeBox);

        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().setStyle("-fx-background-color:#F5DEB3;");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            String sql =
                "INSERT INTO supplier (name, email, contact_no, address, ptype_id) " +
                "VALUES (?,?,?,?,?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nameF.getText().trim());
                ps.setString(2, emailF.getText().trim());
                ps.setString(3, contactF.getText().trim());
                ps.setString(4, addrF.getText().trim());
                ps.setString(5, ptypeBox.getValue());
                ps.executeUpdate();
                loadSuppliers(table);
            } catch (Exception e) { showError(e.getMessage()); }
        });
    }

    // =====================================================================
    //  ORDER & BILL TAB — no Add, has Delete (soft)
    // =====================================================================

    private static class OBRow {
        IntegerProperty entryId      = new SimpleIntegerProperty();
        StringProperty  orderNo      = new SimpleStringProperty();
        StringProperty  pid          = new SimpleStringProperty();
        IntegerProperty sid          = new SimpleIntegerProperty();
        StringProperty  orderDate    = new SimpleStringProperty();
        IntegerProperty qtyOrdered   = new SimpleIntegerProperty();
        StringProperty  orderStatus  = new SimpleStringProperty();
        IntegerProperty billId       = new SimpleIntegerProperty();
        StringProperty  billNo       = new SimpleStringProperty();
        StringProperty  receivedBy   = new SimpleStringProperty();
        DoubleProperty  billAmount   = new SimpleDoubleProperty();
        IntegerProperty qtyReceived  = new SimpleIntegerProperty();
        StringProperty  receivedDate = new SimpleStringProperty();
        StringProperty  billStatus   = new SimpleStringProperty();
        StringProperty  recordStatus = new SimpleStringProperty();

        OBRow(int entryId, String orderNo, String pid, int sid,
              String orderDate, int qtyOrdered, String orderStatus,
              int billId, String billNo, String receivedBy,
              double billAmount, int qtyReceived, String receivedDate,
              String billStatus, String recordStatus) {
            this.entryId.set(entryId);       this.orderNo.set(orderNo);
            this.pid.set(pid);               this.sid.set(sid);
            this.orderDate.set(orderDate);   this.qtyOrdered.set(qtyOrdered);
            this.orderStatus.set(orderStatus);
            this.billId.set(billId);         this.billNo.set(billNo);
            this.receivedBy.set(receivedBy); this.billAmount.set(billAmount);
            this.qtyReceived.set(qtyReceived);
            this.receivedDate.set(receivedDate == null ? "" : receivedDate);
            this.billStatus.set(billStatus);
            this.recordStatus.set(recordStatus);
        }
    }

    private ScrollPane buildOrderBillContent() {
        TableView<OBRow> table = new TableView<>();
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size:14px;");

        // Order columns
        TableColumn<OBRow,Integer> entryCol   = colInt("Entry ID",    80);
        TableColumn<OBRow,String>  orderNoCol = col("Order No",      120);
        TableColumn<OBRow,String>  pidCol     = col("PID",            80);
        TableColumn<OBRow,Integer> sidCol     = colInt("SID",         60);
        TableColumn<OBRow,String>  oDateCol   = col("Order Date",    110);
        TableColumn<OBRow,Integer> qtyOrdCol  = colInt("Qty Ordered", 100);
        TableColumn<OBRow,String>  oStatusCol = col("Order Status",  110);
        // Bill columns
        TableColumn<OBRow,Integer> billIdCol  = colInt("Bill ID",     70);
        TableColumn<OBRow,String>  billNoCol  = col("Bill No",       110);
        TableColumn<OBRow,String>  recByCol   = col("Received By",   130);
        TableColumn<OBRow,Double>  amtCol     = new TableColumn<>("Amount");
        amtCol.setPrefWidth(100);
        TableColumn<OBRow,Integer> qtyRecCol  = colInt("Qty Received",110);
        TableColumn<OBRow,String>  recDateCol = col("Received Date", 120);
        TableColumn<OBRow,String>  bStatusCol = col("Bill Status",   110);
        TableColumn<OBRow,String>  recStatCol = col("Record Status", 110);
        TableColumn<OBRow,Void>    delCol     = new TableColumn<>("Delete");
        delCol.setPrefWidth(90);

        entryCol.setCellValueFactory(d -> d.getValue().entryId.asObject());
        orderNoCol.setCellValueFactory(d -> d.getValue().orderNo);
        pidCol.setCellValueFactory(d -> d.getValue().pid);
        sidCol.setCellValueFactory(d -> d.getValue().sid.asObject());
        oDateCol.setCellValueFactory(d -> d.getValue().orderDate);
        qtyOrdCol.setCellValueFactory(d -> d.getValue().qtyOrdered.asObject());
        oStatusCol.setCellValueFactory(d -> d.getValue().orderStatus);
        billIdCol.setCellValueFactory(d -> d.getValue().billId.asObject());
        billNoCol.setCellValueFactory(d -> d.getValue().billNo);
        recByCol.setCellValueFactory(d -> d.getValue().receivedBy);
        amtCol.setCellValueFactory(d -> d.getValue().billAmount.asObject());
        qtyRecCol.setCellValueFactory(d -> d.getValue().qtyReceived.asObject());
        recDateCol.setCellValueFactory(d -> d.getValue().receivedDate);
        bStatusCol.setCellValueFactory(d -> d.getValue().billStatus);
        recStatCol.setCellValueFactory(d -> d.getValue().recordStatus);

        // ✅ Editable: only safe fields
        recByCol.setCellFactory(TextFieldTableCell.forTableColumn());
        recByCol.setOnEditCommit(e -> {
            e.getRowValue().receivedBy.set(e.getNewValue());
            updateBill(e.getRowValue());
        });

        amtCol.setCellFactory(TextFieldTableCell.forTableColumn(
                new javafx.util.converter.DoubleStringConverter()));
        amtCol.setOnEditCommit(e -> {
            e.getRowValue().billAmount.set(e.getNewValue());
            updateBill(e.getRowValue());
        });

        qtyRecCol.setCellFactory(TextFieldTableCell.forTableColumn(
                new javafx.util.converter.IntegerStringConverter()));
        qtyRecCol.setOnEditCommit(e -> {
            e.getRowValue().qtyReceived.set(e.getNewValue());
            updateBill(e.getRowValue());
        });

        qtyOrdCol.setCellFactory(TextFieldTableCell.forTableColumn(
                new javafx.util.converter.IntegerStringConverter()));
        qtyOrdCol.setOnEditCommit(e -> {
            e.getRowValue().qtyOrdered.set(e.getNewValue());
            updateOrder(e.getRowValue());
        });

        // ✅ Delete: soft delete both order and bill
        delCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Delete");
            {
                btn.setStyle(BTN_RED);
                btn.setPrefWidth(75);
                btn.setOnAction(e -> {
                    OBRow row = getTableView().getItems().get(getIndex());
                    if (confirmDelete("bill '" + row.billNo.get() + "'")) {
                        softDeleteBill(row.billId.get());
                        softDeleteOrder(row.entryId.get());
                        getTableView().getItems().remove(row);
                    }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(
                entryCol, orderNoCol, pidCol, sidCol,
                oDateCol, qtyOrdCol, oStatusCol,
                billIdCol, billNoCol, recByCol, amtCol,
                qtyRecCol, recDateCol, bStatusCol, recStatCol, delCol);

        loadOrderBill(table);

        ScrollPane sp = new ScrollPane(table);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:transparent;");
        return sp;
    }

    private void loadOrderBill(TableView<OBRow> table) {
        table.getItems().clear();
        String sql =
            "SELECT o.entry_id, o.order_no, o.pid, o.sid, o.order_date, " +
            "       o.qty_ordered, o.order_status, " +
            "       b.bill_id, b.bill_no, b.bill_received_by, b.bill_amount, " +
            "       b.qty_received, b.received_date, b.bill_status, b.record_status " +
            "FROM order_table o " +
            "LEFT JOIN bill_invoice b ON b.entry_id = o.entry_id " +
            "WHERE (b.record_status = 'ACTIVE' OR b.record_status IS NULL) " +
            "AND (o.record_status = 'ACTIVE' OR o.record_status IS NULL)";
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                table.getItems().add(new OBRow(
                        rs.getInt("entry_id"),
                        rs.getString("order_no"),
                        rs.getString("pid"),
                        rs.getInt("sid"),
                        rs.getString("order_date") == null ? ""
                                : rs.getString("order_date"),
                        rs.getInt("qty_ordered"),
                        rs.getString("order_status"),
                        rs.getInt("bill_id"),
                        rs.getString("bill_no") == null ? ""
                                : rs.getString("bill_no"),
                        rs.getString("bill_received_by") == null ? ""
                                : rs.getString("bill_received_by"),
                        rs.getDouble("bill_amount"),
                        rs.getInt("qty_received"),
                        rs.getString("received_date"),
                        rs.getString("bill_status") == null ? ""
                                : rs.getString("bill_status"),
                        rs.getString("record_status") == null ? ""
                                : rs.getString("record_status")
                ));
            }
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateBill(OBRow row) {
        String sql =
            "UPDATE bill_invoice SET bill_received_by=?, bill_amount=?, " +
            "qty_received=? WHERE bill_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, row.receivedBy.get());
            ps.setDouble(2, row.billAmount.get());
            ps.setInt(3,    row.qtyReceived.get());
            ps.setInt(4,    row.billId.get());
            ps.executeUpdate();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateOrder(OBRow row) {
        String sql = "UPDATE order_table SET qty_ordered=? WHERE entry_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, row.qtyOrdered.get());
            ps.setInt(2, row.entryId.get());
            ps.executeUpdate();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void softDeleteBill(int billId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE bill_invoice SET record_status='INACTIVE' WHERE bill_id=?")) {
            ps.setInt(1, billId);
            ps.executeUpdate();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void softDeleteOrder(int entryId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE order_table SET record_status='INACTIVE' WHERE entry_id=?")) {
            ps.setInt(1, entryId);
            ps.executeUpdate();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    // =====================================================================
    //  HELPERS
    // =====================================================================

    private <T> TableColumn<T,String> col(String title, double width) {
        TableColumn<T,String> c = new TableColumn<>(title);
        c.setPrefWidth(width);
        return c;
    }

    private <T> TableColumn<T,Integer> colInt(String title, double width) {
        TableColumn<T,Integer> c = new TableColumn<>(title);
        c.setPrefWidth(width);
        return c;
    }

    private Label lbl(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        l.setStyle("-fx-text-fill:#3E2723;");
        return l;
    }

    private TextField field() {
        TextField tf = new TextField();
        tf.setPrefSize(260, 42);
        tf.setStyle(FIELD_STYLE);
        return tf;
    }

    private GridPane formGrid() {
        GridPane g = new GridPane();
        g.setHgap(20);
        g.setVgap(15);
        g.setPadding(new Insets(20));
        g.setStyle("-fx-background-color:#F5DEB3;");
        return g;
    }

    private boolean confirmDelete(String what) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete " + what + "?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Delete");
        alert.getDialogPane().setStyle("-fx-background-color:#F5DEB3;");
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

    public Scene getScene() {
        return scene;
    }
}

package model;

import javafx.beans.property.*;

public class Product {

    private final IntegerProperty pid;
    private final StringProperty name;
    private final IntegerProperty qty;
    private final StringProperty updatedDate;

    // ===== Constructor for ISSUE =====
    public Product(int pid, String name, int qty) {
        this.pid = new SimpleIntegerProperty(pid);
        this.name = new SimpleStringProperty(name);
        this.qty = new SimpleIntegerProperty(qty);
        this.updatedDate = null;
    }

    // ===== Constructor for VIEW =====
    public Product(String name, int qty, String updatedDate) {
        this.pid = null;
        this.name = new SimpleStringProperty(name);
        this.qty = new SimpleIntegerProperty(qty);
        this.updatedDate = new SimpleStringProperty(updatedDate);
    }

    public int getPid() {
        return pid.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty qtyProperty() {
        return qty;
    }

    public StringProperty updatedDateProperty() {
        return updatedDate;
    }

    // ✅ FOR COMBOBOX DISPLAY
    @Override
    public String toString() {
        return name.get() + " (Stock: " + qty.get() + ")";
    }
}

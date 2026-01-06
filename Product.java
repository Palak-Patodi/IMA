package model;

import javafx.beans.property.*;

public class Product {

    private final IntegerProperty pid;
    private final StringProperty name;
    private final IntegerProperty qty;

    public Product(int pid, String name, int qty) {
        this.pid = new SimpleIntegerProperty(pid);
        this.name = new SimpleStringProperty(name);
        this.qty = new SimpleIntegerProperty(qty);
    }

    public IntegerProperty pidProperty() {
        return pid;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty qtyProperty() {
        return qty;
    }
}

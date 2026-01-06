package model;

import javafx.beans.property.*;

public class Supplier {

    private final StringProperty name;
    private final StringProperty contact;

    public Supplier(String name, String contact) {
        this.name = new SimpleStringProperty(name);
        this.contact = new SimpleStringProperty(contact);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty contactProperty() {
        return contact;
    }
}

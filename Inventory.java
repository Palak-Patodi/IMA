package com.mycompany.inventory;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.LoginUI;
import ui.DashboardUI;
import ui.AddInventoryUI;
import ui.IssueUI;
import ui.ReturnUI;
import ui.DeleteInventoryUI;

public class Inventory extends Application {

    private Stage stage;

    // 🔐 STORE LOGGED-IN USER ROLE
    private static String userRole;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Inventory Management Application");
        stage.setResizable(true);

        showLogin();   // 🚀 ALWAYS start from login

        stage.show();
    }

    // ================= AUTH STATE =================

    public static void setUserRole(String role) {
        userRole = role;
    }

    public static String getUserRole() {
        return userRole;
    }

    // ================= NAVIGATION =================

    // Show Login Page
    public void showLogin() {
        LoginUI loginUI = new LoginUI(this);
        stage.setScene(loginUI.getScene());
    }

    // Show Dashboard Page
    public void showDashboard() {
        DashboardUI dashboardUI = new DashboardUI(this);
        stage.setScene(dashboardUI.getScene());
    }

    // 🔴 PROPER LOGOUT (FIXES YOUR BUG)
    public void logout() {
    userRole = null;
    stage.setTitle("Inventory Management Application");
    showLogin();
}


    // ================= FEATURE SCREENS =================

    public void showAddInventory() {
    AddInventoryUI ui = new AddInventoryUI(this);
    if (ui.getScene() != null) {
        stage.setScene(ui.getScene());
    }
}


    public void showDeleteInventory() {
    DeleteInventoryUI ui = new DeleteInventoryUI(this);
    if (ui.getScene() != null) {
        stage.setScene(ui.getScene());
    }
}


    public void showIssueUI() {
    IssueUI ui = new IssueUI(this);
    if (ui.getScene() != null) {
        stage.setScene(ui.getScene());
    }
}


    public void showReturn() {
    ReturnUI ui = new ReturnUI(this);
    if (ui.getScene() != null) {
        stage.setScene(ui.getScene());
    }
}

    // ================= MAIN =================

    public static void main(String[] args) {
        launch(args);
    }
}

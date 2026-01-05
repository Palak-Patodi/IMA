package com.mycompany.inventory;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.LoginUI;
import ui.DashboardUI;

public class Inventory extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Inventory Management Application");
        stage.setResizable(true);   // enables maximize
        showLogin();
        stage.show();
    }

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

    public static void main(String[] args) {
        launch(args);
    }
}

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

    /**
     *
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Inventory Management Application");
        stage.setResizable(true);   // enables maximize
        showDashboard();
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

    public void showAddInventory() 
    {
        AddInventoryUI ui = new AddInventoryUI(this);
        stage.setScene(ui.getScene());
    }
    
    public void showIssue() 
    {
        IssueUI issueUI = new IssueUI(this);
        stage.setScene(issueUI.getScene());
    }
    
    public void showReturn() 
    {
        ReturnUI returnUI = new ReturnUI(this);
        stage.setScene(returnUI.getScene());
    }

    public void showDeleteInventory() 
    {
        DeleteInventoryUI ui = new DeleteInventoryUI(this);
        stage.setScene(ui.getScene());
    }


}

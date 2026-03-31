package service;

import model.BudgetData;
import java.util.HashMap;
import java.util.Map;

public class BudgetService {

    public BudgetData getBudgetAnalysis() {

        double totalBudget = 0;
        double amountSpent = 0;

        Map<String, Double> distribution = new HashMap<>();

        try (java.sql.Connection con = db.DBConnection.getConnection()) {

            // ✅ TOTAL BUDGET (MONEY)
            String totalSql = "SELECT COALESCE(SUM(bill_amount),0) FROM bill_invoice";
            java.sql.PreparedStatement ps1 = con.prepareStatement(totalSql);
            java.sql.ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                totalBudget = rs1.getDouble(1);
            }

            // ✅ CATEGORY WISE (MONEY)
            String categorySql = """
                SELECT pt.ptype_name, COALESCE(SUM(b.bill_amount),0)
                FROM bill_invoice b
                JOIN product p ON b.pid = p.pid
                JOIN product_type pt ON p.ptype_id = pt.ptype_id
                GROUP BY pt.ptype_name
            """;

            java.sql.PreparedStatement ps2 = con.prepareStatement(categorySql);
            java.sql.ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
                String category = rs2.getString(1);
                double value = rs2.getDouble(2);

                distribution.put(category, value);
            }

            // ✅ AMOUNT SPENT (FROM ISSUE TABLE)
            String spentSql = """
                SELECT COALESCE(SUM(i.qty_issued * 
                (b.bill_amount / b.qty_received)),0)
                FROM issue i
                JOIN bill_invoice b ON i.pid = b.pid
            """;

            java.sql.PreparedStatement ps3 = con.prepareStatement(spentSql);
            java.sql.ResultSet rs3 = ps3.executeQuery();

            if (rs3.next()) {
                amountSpent = rs3.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        double remaining = totalBudget - amountSpent;

        return new BudgetData(totalBudget, amountSpent, remaining, distribution);
    }
}

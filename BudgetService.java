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

            // ✅ TOTAL BUDGET — from order_table (ACTIVE only)
            // We need a price per unit from bill_invoice to calculate order value.
            // Using: SUM(o.qty_ordered * (b.bill_amount / b.qty_received))
            // joining order_table → bill_invoice on entry_id
            String totalSql = """
                SELECT COALESCE(
                    SUM(o.qty_ordered * (b.bill_amount / b.qty_received)), 0
                )
                FROM order_table o
                JOIN bill_invoice b ON b.entry_id = o.entry_id
                WHERE o.record_status = 'ACTIVE'
                  AND b.record_status = 'ACTIVE'
            """;
            java.sql.PreparedStatement ps1 = con.prepareStatement(totalSql);
            java.sql.ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                totalBudget = rs1.getDouble(1);
            }

            // ✅ AMOUNT SPENT — from bill_invoice (ACTIVE only)
            String spentSql = """
                SELECT COALESCE(SUM(bill_amount), 0)
                FROM bill_invoice
                WHERE record_status = 'ACTIVE'
            """;
            java.sql.PreparedStatement ps2 = con.prepareStatement(spentSql);
            java.sql.ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                amountSpent = rs2.getDouble(1);
            }

            // ✅ CATEGORY DISTRIBUTION — based on order_table amounts (ACTIVE only)
            String categorySql = """
                SELECT pt.ptype_name,
                       COALESCE(SUM(o.qty_ordered * (b.bill_amount / b.qty_received)), 0)
                FROM order_table o
                JOIN bill_invoice b ON b.entry_id = o.entry_id
                JOIN product p ON o.pid = p.pid
                JOIN product_type pt ON p.ptype_id = pt.ptype_id
                WHERE o.record_status = 'ACTIVE'
                  AND b.record_status = 'ACTIVE'
                GROUP BY pt.ptype_name
            """;
            java.sql.PreparedStatement ps3 = con.prepareStatement(categorySql);
            java.sql.ResultSet rs3 = ps3.executeQuery();
            while (rs3.next()) {
                String category = rs3.getString(1);
                double value = rs3.getDouble(2);
                distribution.put(category, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        double remaining = totalBudget - amountSpent;
        return new BudgetData(totalBudget, amountSpent, remaining, distribution);
    }
}

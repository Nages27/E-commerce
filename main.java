import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== E-Commerce System ===");
            System.out.println("1. View Customers");
            System.out.println("2. Add Customer");
            System.out.println("3. Place Order");
            System.out.println("4. Generate Invoice");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            try (Connection con = DBConnection.getConnection()) {
                switch (choice) {
                    case 1 -> viewCustomers(con);
                    case 2 -> addCustomer(con, sc);
                    case 3 -> placeOrder(con, sc);
                    case 4 -> generateInvoice(con, sc);
                    case 5 -> {
                        System.out.println("Exiting...");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    // ✅ View Customers
    private static void viewCustomers(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Customers");
        while (rs.next()) {
            System.out.println(rs.getInt("customer_id") + " - " + rs.getString("name") +
                               " (" + rs.getString("email") + ")");
        }
    }

    // ✅ Add Customer
    private static void addCustomer(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter customer name: ");
        sc.nextLine(); // consume newline
        String name = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.nextLine();

        PreparedStatement ps = con.prepareStatement("INSERT INTO Customers (name, email) VALUES (?, ?)");
        ps.setString(1, name);
        ps.setString(2, email);
        ps.executeUpdate();
        System.out.println("Customer added successfully!");
    }

    // ✅ Place Order
    private static void placeOrder(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter customer ID: ");
        int customerId = sc.nextInt();
        System.out.print("Enter product ID: ");
        int productId = sc.nextInt();
        System.out.print("Enter quantity: ");
        int qty = sc.nextInt();

        // Insert into Orders

        
        PreparedStatement psOrder = con.prepareStatement(
            "INSERT INTO Orders (customer_id, order_date, total_amount) VALUES (?, NOW(), 0)",
            Statement.RETURN_GENERATED_KEYS);
        psOrder.setInt(1, customerId);
        psOrder.executeUpdate();

        // Get order_id
        ResultSet keys = psOrder.getGeneratedKeys();
        int orderId = 0;
        if (keys.next()) {
            orderId = keys.getInt(1);
        }

        // Insert into OrderItems
        PreparedStatement psItem = con.prepareStatement(
            "INSERT INTO OrderItems (order_id, product_id, quantity) VALUES (?, ?, ?)");
        psItem.setInt(1, orderId);
        psItem.setInt(2, productId);
        psItem.setInt(3, qty);
        psItem.executeUpdate();

        System.out.println("Order placed successfully! Order ID = " + orderId);
    }

    // ✅ Generate Invoice
    private static void generateInvoice(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter Order ID: ");
        int orderId = sc.nextInt();

        CallableStatement cs = con.prepareCall("{call GenerateInvoice(?)}");
        cs.setInt(1, orderId);
        ResultSet rs = cs.executeQuery();

        System.out.println("\n--- Invoice ---");
        while (rs.next()) {
            System.out.println(
                "Order ID: " + rs.getInt("order_id") +
                ", Customer: " + rs.getString("customer_name") +
                ", Product: " + rs.getString("product_name") +
                ", Qty: " + rs.getInt("quantity") +
                ", Price: " + rs.getDouble("price") +
                ", Total: " + rs.getDouble("total_price")
            );
        }
    }
}

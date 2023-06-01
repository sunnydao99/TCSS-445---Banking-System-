import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class Banking  {

    private static final String url = "jdbc:sqlserver://localhost:1434;database=Banking;integratedSecurity=true;trustServerCertificate=true;";
    private Connection conn;

    public static void main(String[] args) {
        Banking app = new Banking();
        app.run();
    }

    public void run() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url);
            System.out.println("Congrats! Connect successful.");
            // Display initial menu
            Scanner scanner = new Scanner(System.in);

            int choice;
            do {
                System.out.println("1. Customer");
                System.out.println("2. Employee");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Enter customer ID: ");
                        int customerID = scanner.nextInt();

                        if (authenticateCustomer(customerID)) {
                            int customerChoice;
                            do {
                                System.out.println("1. View Balance");
                                System.out.println("2. Deposit");
                                System.out.println("3. Withdraw");
                                System.out.println("4. Transfer Money");
                                System.out.println("5. View Customer Information");
                                System.out.println("6. Back");
                                System.out.print("Enter your choice: ");
                                customerChoice = scanner.nextInt();

                                switch (customerChoice) {
                                    case 1:
                                        double balance = getAccountBalance(customerID);
                                        System.out.println("Balance: " + balance);
                                        break;
                                    case 2:
                                        performDeposit(customerID);
                                        break;
                                    case 3:
                                        performWithdrawal(customerID);
                                        break;
                                    case 4:
                                        performTransfer(customerID);
                                        break;
                                    case 5:
                                        viewCustomerInformation(customerID);
                                        break;
                                    case 6:
                                        System.out.println("Returning to the main menu.");
                                        break;
                                    default:
                                        System.out.println("Invalid choice. Please try again.");
                                        break;
                                }
                            } while (customerChoice != 6);
                        } else {
                            System.out.println("Invalid customer ID. Please try again.");
                        }
                        break;
                    case 2:
                        System.out.print("Enter employee ID: ");
                        int employeeID = scanner.nextInt();

                        if (authenticateEmployee(employeeID)) {
                            // Logic for employee actions
                            // ...
                            System.out.println("Employee actions placeholder");
                        } else {
                            System.out.println("Invalid employee ID. Please try again.");
                        }
                        break;
                    case 3:
                        System.out.println("Exiting the application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } while (choice != 3);

            disconnect(); // Disconnect from the database
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
    private void performTransfer(int senderID) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter recipient ID: ");
        int recipientID = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter transfer amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (amount < 0) {
            System.out.println("Invalid amount. Transfer amount cannot be negative.");
            return;
        }

        double senderBalance = getAccountBalance(senderID);
        if (amount > senderBalance) {
            System.out.println("Insufficient funds. Transfer amount exceeds sender's account balance.");
            return;
        }

        double recipientBalance = getAccountBalance(recipientID);

        double newSenderBalance = senderBalance - amount;
        double newRecipientBalance = recipientBalance + amount;

        String updateSenderQuery = "UPDATE Accounts SET Balance = ? WHERE CustomerID = ?";
        String updateRecipientQuery = "UPDATE Accounts SET Balance = ? WHERE CustomerID = ?";

        try (PreparedStatement updateSenderStatement = conn.prepareStatement(updateSenderQuery);
             PreparedStatement updateRecipientStatement = conn.prepareStatement(updateRecipientQuery)) {
            conn.setAutoCommit(false);

            updateSenderStatement.setDouble(1, newSenderBalance);
            updateSenderStatement.setInt(2, senderID);

            updateRecipientStatement.setDouble(1, newRecipientBalance);
            updateRecipientStatement.setInt(2, recipientID);

            int rowsUpdatedSender = updateSenderStatement.executeUpdate();
            int rowsUpdatedRecipient = updateRecipientStatement.executeUpdate();

            if (rowsUpdatedSender > 0 && rowsUpdatedRecipient > 0) {
                conn.commit();
                System.out.println("Transfer successful.");
                System.out.println("Sender's new balance: " + newSenderBalance);
            } else {
                conn.rollback();
                System.out.println("Failed to perform transfer.");
            }
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Failed to perform transfer: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private boolean authenticateCustomer(int customerID) throws SQLException {
        String query = "SELECT * FROM Customer WHERE CustomerID = " + customerID;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();

        }
    }

    private double getAccountBalance(int customerID) throws SQLException {
        String query = "SELECT Balance FROM Account WHERE CustomerID = " + customerID;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                double balance = rs.getDouble("Balance");
                System.out.println("Your account balance is: $" + balance);
            } else {
                System.out.println("No account found for the customer.");
            }
        }
        return 0;
    }

    private void viewCustomerInformation(int customerID) throws SQLException {
        String query = "SELECT * FROM Customer WHERE CustomerID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, customerID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String LastName = resultSet.getString("LastName");
                String FirstName = resultSet.getString("FirstName");
                String email = resultSet.getString("Email");
                String  DOB  = resultSet.getString("DateOfBirth");
                String Phone =  resultSet.getString("PhoneNumber");
                System.out.println("Customer ID: " + customerID);
                System.out.println("Last Name: " + LastName);
                System.out.println("First Name: " + FirstName);
                System.out.println("Date Of Birth: " + DOB);
                System.out.println("Phone: " + Phone);
                System.out.println("Email: " + email);
            } else {
                System.out.println("Customer not found.");
            }
        }
    }


    private void performDeposit(int customerID) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter amount to deposit: ");
            double amount = scanner.nextDouble();

            if (amount <= 0) {
                System.out.println("Invalid amount. Deposit failed.");
                return;
            }

            // Perform deposit transaction
            // Update the database with the deposit transaction details

            System.out.println("Deposit of " + amount + " successfully made for customer ID: " + customerID);
            // ...
        } catch (Exception e) {
            System.out.println("Deposit failed. " + e.getMessage());
        }
    }


    private void performMoneyTransfer(int senderID, int receiverID, double amount) throws SQLException {
        double senderBalance = getAccountBalance(senderID);
        double receiverBalance = getAccountBalance(receiverID);

        double updatedSenderBalance = senderBalance - amount;
        double updatedReceiverBalance = receiverBalance + amount;

        // Update the sender's account balance
        String updateSenderQuery = "UPDATE Account SET Balance = " + updatedSenderBalance + " WHERE CustomerID = " + senderID;
        try (Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(updateSenderQuery);
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update sender's account balance.");
            }
        }
    }
        private void performWithdrawal(int customerID) throws SQLException {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter withdrawal amount: ");
            double amountt = scanner.nextDouble();
            scanner.nextLine();

            if (amountt < 0) {
                System.out.println("Invalid amount. Withdrawal amount cannot be negative.");
                return;
            }

            double balance = getAccountBalance(customerID);
            if (amountt > balance) {
                System.out.println("Insufficient funds. Withdrawal amount exceeds account balance.");
                return;
            }

            double newBalance = balance - amountt;

            String updateQuery = "UPDATE Accounts SET Balance = ? WHERE CustomerID = ?";
            try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                updateStatement.setDouble(1, newBalance);
                updateStatement.setInt(2, customerID);
                int rowsUpdated = updateStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Withdrawal successful. New balance: " + newBalance);
                } else {
                    System.out.println("Failed to perform withdrawal.");
                }
            }
        }
    private void handleEmployeeOptions(int employeeID) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("1. View Customer Information");
            System.out.println("2. Back");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter customer ID: ");
                    int customerID = scanner.nextInt();
                    scanner.nextLine();
                    viewCustomerInformation(customerID);
                    break;
                case 2:
                    System.out.println("Going back to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 2);
    }

    private boolean authenticateEmployee(int employeeID) throws SQLException {
        String query = "SELECT * FROM Employee WHERE EmployeeID = " + employeeID;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        }
    }
    private void disconnect() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to disconnect from the database: " + e.getMessage());
        }
    }


}



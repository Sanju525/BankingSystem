package banking;

import java.sql.*;

public class DataBase {
    private String url = "jdbc:sqlite:";

    DataBase(String databasePath) {
        url = url + databasePath;
        System.out.println("Database URL:" + url);
    }

    protected Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    protected void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS card(" +
                "id INTEGER PRIMARY KEY," +
                "number TEXT NOT NULL," +
                "pin TEXT NOT NULL," +
                "balance INTEGER DEFAULT 0);";

        try (Connection connection = connect();
             Statement stmt = connection.createStatement()) {
            // Create New Table
            stmt.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected void createRecord(String number, String pin) {
        String sql = "INSERT INTO card(number, pin) VALUES (?, ?)";
        createTable();
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, number);
            preparedStatement.setString(2, pin);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected String getPin(String number) {
        String sql = "SELECT pin FROM card WHERE number="+number;
        String pin = null;
        try (Connection connection = connect();
             Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery(sql);
            pin = rs.getString("pin");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pin;
    }

    protected int getBalance(String number) {
        String sql = "SELECT balance FROM card WHERE number="+number;
        int balance = 0;
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            balance = rs.getInt("balance");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return balance;
    }

    protected void updateBalance(int balance, String number) {
        String sql = "UPDATE card SET balance=? WHERE number=?";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, balance);
            preparedStatement.setString(2, number);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected boolean cardExists(String number) {
        String sql = "SELECT * FROM card WHERE number="+number;
        try (Connection connection = connect();
             Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery(sql);
            return rs.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    // Login Required :: Close Account
    protected void deleteRecord(String number) {
        String sql = "DELETE FROM card WHERE number=?";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, number);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // print every record
    protected void select() {
        String sql = "SELECT * FROM card;";
        try (Connection connection = connect();
        Statement statement = connection.createStatement()) {
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            System.out.printf("\nnumber: %s\npin: %s\nbalance: %d\n",rs.getString("number"), rs.getString("pin"), rs.getInt("balance"));
        }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}


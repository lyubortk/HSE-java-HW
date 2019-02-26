package ru.hse.lyubortk.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PhoneBook {
    private String name;

    public static class Record {
        public String name;
        public String number;
        public Record(String name, String number) {
            this.name = name;
            this.number = number;
        }
    }

    public PhoneBook(String name) throws SQLException {
        this.name = name;
        executeUpdate("CREATE TABLE IF NOT EXISTS contacts (" +
                "name TEXT," +
                "number TEXT," +
                "UNIQUE(name, number));", Collections.emptyList());
    }

    public void addRecord(Record record) throws SQLException {
        executeUpdate("INSERT OR IGNORE INTO contacts VALUES (?, ?)",
                Arrays.asList(record.name, record.number));
    }

    public List<String> getNumbersByName(String name) throws SQLException {
        return executeQuery("SELECT number FROM contacts WHERE name = ?",
                Collections.singletonList(name));
    }

    public List<String> getNamesByNumber(String number) throws SQLException {
        return executeQuery("SELECT name FROM contacts WHERE number = ? ORDER BY name ASC",
                Collections.singletonList(number));
    }

    public void eraseRecord(Record record) throws SQLException {
        executeUpdate("DELETE FROM contacts WHERE name = ? AND number = ?",
                Arrays.asList(record.name, record.number));
    }

    public void changeNameOfRecord(Record record, String newName) throws SQLException {
        executeUpdate("UPDATE contacts SET name = ? WHERE name = ? AND number = ?",
                Arrays.asList(newName, record.name, record.number));
    }

    public void changeNumberOfRecord(Record record, String newNumber) throws SQLException {
        executeUpdate("UPDATE contacts SET number = ? WHERE name = ? AND number = ?",
                Arrays.asList(newNumber, record.name, record.number));
    }

    public List<Record> getAllRecords() throws SQLException {
        var outputList = new ArrayList<Record>();
        List<String> queryResult = executeQuery("SELECT * FROM contacts ORDER BY name ASC",
                Collections.emptyList());
        for (int i = 0; i < queryResult.size(); i += 2) {
            outputList.add(new Record(queryResult.get(i), queryResult.get(i+1)));
        }
        return outputList;
    }

    private void executeUpdate(String query, List<String> names) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + name + ".db")) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < names.size(); ++i) {
                    statement.setString(i+1, names.get(i));
                }
                statement.executeUpdate();
            }
        }
    }

    private List<String> executeQuery(String query, List<String> names) throws SQLException {
        var list = new ArrayList<String>();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + name + ".db")) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < names.size(); ++i) {
                    statement.setString(i+1, names.get(i));
                }

                try (ResultSet result = statement.executeQuery()) {
                    int numberOfColumns = result.getMetaData().getColumnCount();
                    while (result.next()) {
                        for (int i = 0; i < numberOfColumns; ++i) {
                            list.add(result.getString(i+1));
                        }
                    }
                }
            }
        }
        return list;
    }
}

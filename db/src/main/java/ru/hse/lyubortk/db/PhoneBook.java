package ru.hse.lyubortk.db;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

/**
 * Phone book class which contains records of names and corresponding phone numbers.
 * Implementation uses SQLite-jdbc for database.
 */
public class PhoneBook {
    private final @NotNull String name;

    /** Pair of strings: name and phone number */
    public static class Record {
        private @NotNull String name;
        private @NotNull String number;

        /** Record constructor which accepts record's name and phone number */
        public Record(@NotNull String name, @NotNull String number) {
            this.name = name;
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Record record = (Record) o;
            return name.equals(record.name) &&
                    number.equals(record.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, number);
        }

        public @NotNull String getName() {
            return name;
        }

        public void setName(@NotNull String name) {
            this.name = name;
        }

        public @NotNull String getNumber() {
            return number;
        }

        public void setNumber(@NotNull String number) {
            this.number = number;
        }
    }

    /**
     * Constructor accepts name/path to sqlite database file.
     * If file doesn't exist SQLite-jdbc automatically creates it.
     * @param name the name of database file
     * @throws SQLException if database file could not be properly created
     */
    public PhoneBook(@NotNull String name) throws SQLException {
        this.name = name;
        executeUpdate("CREATE TABLE IF NOT EXISTS " + getTableSchema(),
                Collections.emptyList());
    }

    /**
     * Adds record to database. If database already contains this record then
     * the record won't be added twice.
     * @param record a record to add to phone book
     * @throws SQLException in case of error with database file
     */
    public void addRecord(@NotNull Record record) throws SQLException {
        executeUpdate("INSERT OR IGNORE INTO contacts VALUES (?, ?)",
                Arrays.asList(record.name, record.number));
    }

    /**
     * Returns list of telephone numbers which belong to a specified name.
     * @param name a name to search for numbers
     * @return list of found numbers
     * @throws SQLException in case of error with database file
     */
    public @NotNull List<String> getNumbersByName(@NotNull String name) throws SQLException {
        return executeQuery("SELECT number FROM contacts WHERE name = ? ORDER BY number",
                Collections.singletonList(name));
    }

    /**
     * Returns list of names which use specified number.
     * @param number a number to search for names of owners
     * @return list of found names
     * @throws SQLException in case of error with database file
     */
    public @NotNull List<String> getNamesByNumber(@NotNull String number) throws SQLException {
        return executeQuery("SELECT name FROM contacts WHERE number = ? ORDER BY name",
                Collections.singletonList(number));
    }

    /**
     * Deletes specified record from database. If database does not contain this record
     * then nothing is changed.
     * @param record a record to delete.
     * @throws SQLException in case of error with database file
     */
    public void eraseRecord(@NotNull Record record) throws SQLException {
        executeUpdate("DELETE FROM contacts WHERE name = ? AND number = ?",
                Arrays.asList(record.name, record.number));
    }

    /**
     * Changes name in the specified record. If changed record is identical
     * to another record in database then duplicates are merged into one record.
     * @param newName new value of the name field of the given record
     * @throws SQLException in case of error with database file
     */
    public void changeNameOfRecord(@NotNull Record record,
                                   @NotNull String newName) throws SQLException {
        executeUpdate("UPDATE OR REPLACE contacts SET name = ? WHERE name = ? AND number = ?",
                Arrays.asList(newName, record.name, record.number));
    }

    /**
     * Changes phone number in the specified record. If changed record is identical
     * to another record in database then duplicates are merged into one record
     * @param newNumber new value of the number field of the given record
     * @throws SQLException in case of error with database file
     */
    public void changeNumberOfRecord(@NotNull Record record,
                                     @NotNull String newNumber) throws SQLException {
        executeUpdate("UPDATE OR REPLACE contacts SET number = ? WHERE name = ? AND number = ?",
                Arrays.asList(newNumber, record.name, record.number));
    }

    /**
     * Returns list of all records in the database.
     * @throws SQLException in case of error with database file
     */
    public @NotNull List<Record> getAllRecords() throws SQLException {
        var outputList = new ArrayList<Record>();
        List<String> queryResult = executeQuery("SELECT * FROM contacts ORDER BY name, number",
                Collections.emptyList());
        for (int i = 0; i < queryResult.size(); i += 2) {
            outputList.add(new Record(queryResult.get(i), queryResult.get(i+1)));
        }
        return outputList;
    }

    /**
     * Deletes all records from the database leaving it empty.
     * @throws SQLException in case of error with database file
     */
    public void clear() throws SQLException {
       executeUpdate("DELETE FROM contacts", Collections.emptyList());
    }

    private void executeUpdate(@NotNull String query,
                               @NotNull List<String> names) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + name)) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < names.size(); ++i) {
                    statement.setString(i+1, names.get(i));
                }
                statement.executeUpdate();
            }
        }
    }

    private @NotNull List<String> executeQuery(@NotNull String query,
                                               @NotNull List<String> names) throws SQLException {
        var list = new ArrayList<String>();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + name)) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < names.size(); ++i) {
                    statement.setString(i + 1, names.get(i));
                }

                try (ResultSet result = statement.executeQuery()) {
                    int numberOfColumns = result.getMetaData().getColumnCount();
                    while (result.next()) {
                        for (int i = 0; i < numberOfColumns; ++i) {
                            list.add(result.getString(i + 1));
                        }
                    }
                }
            }
        }
        return list;
    }

    private static @NotNull String getTableSchema() {
        InputStream schemaStream = PhoneBook.class.getResourceAsStream("/tableSchema");
        return new Scanner(schemaStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
    }
}

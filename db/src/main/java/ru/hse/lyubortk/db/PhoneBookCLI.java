package ru.hse.lyubortk.db;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/** Command Line Interface for PhoneBook class */
public class PhoneBookCLI {
    private static final String RECORD_NOT_FOUND_MESSAGE = "PhoneBook does not contain this "
                                                           + "record. Leaving database unchanged.";
    private static final String AMBIGUOUS_RECORD_MESSAGE = "Performed operation could result in "
                                                           + "identical records in the PhoneBook. "
                                                           + "Leaving database unchanged.";

    /**
     * Endless loop with 8 different options for phone book management;
     * @throws SQLException in case of error with database file
     */
    public static void main(String[] args) throws SQLException {
        var phoneBook = new PhoneBook("phoneBook.db");
        var in = new Scanner(System.in);
        boolean finished = false;
        while (!finished) {
            switch (getCommand(in)) {
                case 0:
                    finished = true;
                    break;
                case 1:
                    System.out.println("Enter name and number on two separate lines:");
                    try {
                        phoneBook.addRecord(new PhoneBook.Record(in.nextLine(), in.nextLine()));
                    } catch (AmbiguousRecordException e) {
                        System.out.println(AMBIGUOUS_RECORD_MESSAGE);
                    }
                    break;
                case 2:
                    System.out.println("Enter name:");
                    phoneBook.getNumbersByName(in.nextLine()).forEach(System.out::println);
                    break;
                case 3:
                    System.out.println("Enter number:");
                    phoneBook.getNamesByNumber(in.nextLine()).forEach(System.out::println);
                    break;
                case 4:
                    System.out.println("Enter name and number on two separate lines:");
                    try {
                        phoneBook.eraseRecord(new PhoneBook.Record(in.nextLine(), in.nextLine()));
                    } catch (RecordNotFoundException e) {
                        System.out.println(RECORD_NOT_FOUND_MESSAGE);
                    }
                    break;
                case 5:
                    System.out.println("Enter old name, number and new name "
                                       + "on three separate lines:");
                    try {
                        phoneBook.changeNameOfRecord(
                                new PhoneBook.Record(in.nextLine(), in.nextLine()), in.nextLine());
                    } catch (RecordNotFoundException e) {
                        System.out.println(RECORD_NOT_FOUND_MESSAGE);
                    } catch (AmbiguousRecordException e) {
                        System.out.println(AMBIGUOUS_RECORD_MESSAGE);
                    }
                    break;
                case 6:
                    System.out.println("Enter name, old number and new number "
                                       + "on three separate lines:");
                    try {
                        phoneBook.changeNumberOfRecord(
                                new PhoneBook.Record(in.nextLine(), in.nextLine()), in.nextLine());
                    } catch (RecordNotFoundException e) {
                        System.out.println(RECORD_NOT_FOUND_MESSAGE);
                    } catch (AmbiguousRecordException e) {
                        System.out.println(AMBIGUOUS_RECORD_MESSAGE);
                    }
                    break;
                case 7:
                    List<PhoneBook.Record> records = phoneBook.getAllRecords();
                    for (var r: records) {
                        System.out.println(r.getName() + " " + r.getNumber());
                    }
                    break;
                case 8:
                    phoneBook.clear();
                    break;
                case 9:
                    printHelp();
                    break;
                default:
                    System.out.println("Unknown command. Enter 9 to show list of commands.");
                    break;
            }
        }
    }

    private static int getCommand(@NotNull Scanner in) {
        System.out.println("\nEnter command number (9 to show list of available commands).");
        int result;
        try {
            result = Integer.parseInt(in.nextLine());
        } catch (NumberFormatException e) {
            result = -1;
        }
        return result;
    }

    private static void printHelp() {
        System.out.println("o--------------------------------o");
        System.out.println("| 0 - exit                       |");
        System.out.println("| 1 - add record                 |");
        System.out.println("| 2 - print all numbers by name  |");
        System.out.println("| 3 - print all names by number  |");
        System.out.println("| 4 - erase record               |");
        System.out.println("| 5 - change name of record      |");
        System.out.println("| 6 - change number of record    |");
        System.out.println("| 7 - print all records          |");
        System.out.println("| 8 - erase all records          |");
        System.out.println("| 9 - print help                 |");
        System.out.println("o--------------------------------o");
    }
}

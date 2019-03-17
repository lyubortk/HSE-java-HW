package ru.hse.lyubortk.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/** Command Line Interface for PhoneBook class */
public class PhoneBookCLI {
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
                    phoneBook.addRecord(new PhoneBook.Record(in.nextLine(), in.nextLine()));
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
                    System.out.println("Enter name and number on two different lines:");
                    phoneBook.eraseRecord(new PhoneBook.Record(in.nextLine(), in.nextLine()));
                    break;
                case 5:
                    System.out.println("Enter old name, number and new name " +
                            "on three separate lines:");
                    phoneBook.changeNameOfRecord(
                            new PhoneBook.Record(in.nextLine(), in.nextLine()), in.nextLine());
                    break;
                case 6:
                    System.out.println("Enter name, old number and new number " +
                            "on three separate lines:");
                    phoneBook.changeNumberOfRecord(
                            new PhoneBook.Record(in.nextLine(), in.nextLine()), in.nextLine());
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
                default:
                    System.out.println("Unknown command");
                    break;
            }
        }
    }

    private static int getCommand(Scanner in) {
        System.out.println("\nEnter command number");
        int result;
        try {
            result = Integer.parseInt(in.nextLine());
        } catch (NumberFormatException e) {
            result = -1;
        }
        return result;
    }
}

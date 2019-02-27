package ru.hse.lyubortk.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

class PhoneBookTest {
    private PhoneBook phoneBook;
    private File dataBaseFile;
    final static PhoneBook.Record records[] = new PhoneBook.Record[6];

    @BeforeAll
    private static void initialize() throws SQLException, IOException {
        records[0] = new PhoneBook.Record("Division of Consumer Protection", "(518) 474-8583");
        records[1] = new PhoneBook.Record("Athletic Commission", "1 (800) 697-1220");
        records[2] = new PhoneBook.Record("Committee on Open Government", "(518) 474-2518");
        records[3] = new PhoneBook.Record("Committee on Open Government", "(518) 474-1927");
        records[4] = new PhoneBook.Record("Barak Obama", "(518) 474-1927");
        records[5] = new PhoneBook.Record("Office of Administrative Hearings", "0110");
    }

    @BeforeEach
    private void clearDB() throws SQLException, IOException {
        dataBaseFile = File.createTempFile("tmp",null);
        phoneBook = new PhoneBook(dataBaseFile.toPath().toString());
    }

    @AfterEach
    void deleteTempFile() {
        dataBaseFile.deleteOnExit();
    }

    @Test
    void addRecordBasic() throws SQLException {
        phoneBook.addRecord(records[4]);
        assertEquals(Collections.singletonList(records[4]), phoneBook.getAllRecords());
        assertEquals(Collections.singletonList(records[4].name),
                phoneBook.getNamesByNumber(records[4].number));
        assertEquals(Collections.singletonList(records[4].number),
                phoneBook.getNumbersByName(records[4].name));
    }

    @Test
    void addMultipleRecords() throws SQLException {
        for (int i = 0; i < records.length; i++) {
            assertEquals(i, phoneBook.getAllRecords().size());
            phoneBook.addRecord(records[i]);
        }
        var sortedRecords = new ArrayList<>(Arrays.asList(records));
        sortedRecords.sort(Comparator.comparing((PhoneBook.Record a) -> a.name).
                thenComparing(a -> a.number));
        assertEquals(phoneBook.getAllRecords(), sortedRecords);
    }

    @Test
    void addSameRecord() throws SQLException {
        for (int i = 0; i < 10; i++) {
            phoneBook.addRecord(records[0]);
        }
        assertEquals(1, phoneBook.getAllRecords().size());
    }

    @Test
    void getNumbersByName() throws SQLException {
        for (var r : records) {
            phoneBook.addRecord(r);
        }
        var expectedResult = Arrays.asList(records[2].number, records[3].number);
        expectedResult.sort(Comparator.comparing(a -> a));
        assertEquals(phoneBook.getNumbersByName(records[2].name), expectedResult);
    }

    @Test
    void getNamesByNumber() throws SQLException {
        for (var r : records) {
            phoneBook.addRecord(r);
        }
        var expectedResult = Arrays.asList(records[3].name, records[4].name);
        expectedResult.sort(Comparator.comparing(a -> a));
        assertEquals(phoneBook.getNamesByNumber(records[3].number), expectedResult);
    }

    @Test
    void eraseRecord() throws SQLException {
        assertEquals(phoneBook.getAllRecords(), Collections.emptyList());
        phoneBook.addRecord(records[2]);
        phoneBook.addRecord(records[3]);

        assertEquals(2, phoneBook.getAllRecords().size());

        phoneBook.eraseRecord(records[2]);
        assertEquals(Collections.singletonList(records[3]), phoneBook.getAllRecords());

        phoneBook.eraseRecord(records[3]);
        assertEquals(0, phoneBook.getAllRecords().size());
    }

    @Test
    void changeNameOfRecordBasic() throws SQLException {
        var record = new PhoneBook.Record("Miku Miku", "12345 56789");
        phoneBook.addRecord(record);
        phoneBook.changeNameOfRecord(record, "Hatsune Miku");
        assertEquals("Hatsune Miku", phoneBook.getNamesByNumber(record.number).get(0));
        assertEquals("Hatsune Miku", phoneBook.getNamesByNumber("12345 56789").get(0));
    }

    @Test
    void changeNameOfRecordCollision() throws SQLException {
        var anotherRecord = new PhoneBook.Record("Trump", records[4].number);
        phoneBook.addRecord(records[4]);
        phoneBook.addRecord(anotherRecord);
        assertEquals(2, phoneBook.getAllRecords().size());

        phoneBook.changeNameOfRecord(records[4], "Trump");
        assertEquals(1, phoneBook.getAllRecords().size());
    }

    @Test
    void changeNumberOfRecordBasic() throws SQLException {
        var record = new PhoneBook.Record("Miku Miku", "12345 56789");
        phoneBook.addRecord(record);
        phoneBook.changeNumberOfRecord(record, "2019");
        assertEquals("2019", phoneBook.getNumbersByName(record.name).get(0));
        assertEquals("2019", phoneBook.getNumbersByName("Miku Miku").get(0));
    }

    @Test
    void changeNumberOfRecordCollision() throws SQLException {
        var anotherRecord = new PhoneBook.Record(records[4].name, ":-)");
        phoneBook.addRecord(records[4]);
        phoneBook.addRecord(anotherRecord);
        assertEquals(2, phoneBook.getAllRecords().size());

        phoneBook.changeNumberOfRecord(records[4], ":-)");
        assertEquals(1, phoneBook.getAllRecords().size());
    }

    @Test
    void getAllRecordsAndClear() throws SQLException {
        var list = new ArrayList<PhoneBook.Record>();
        for (int i = 0; i < records.length; i++) {
            assertEquals(list, phoneBook.getAllRecords());
            phoneBook.addRecord(records[i]);
            list.add(i, records[i]);
            list.sort(Comparator.comparing((PhoneBook.Record a) -> a.name).
                    thenComparing(a -> a.number));
        }

        assertEquals(list, phoneBook.getAllRecords());
    }


    @Test
    void recordEquals() {
        var record1 = new PhoneBook.Record("aba ca", "1");
        var record2 = new PhoneBook.Record("aba ca", "1");
        assertEquals(record1, record2);
    }
}
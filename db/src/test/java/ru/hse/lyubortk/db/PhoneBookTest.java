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
        assertDoesNotThrow(() -> phoneBook.addRecord(records[4]));
        assertEquals(Collections.singletonList(records[4]), phoneBook.getAllRecords());
        assertEquals(Collections.singletonList(records[4].getName()),
                phoneBook.getNamesByNumber(records[4].getNumber()));
        assertEquals(Collections.singletonList(records[4].getNumber()),
                phoneBook.getNumbersByName(records[4].getName()));
    }

    @Test
    void addMultipleRecords() throws SQLException {
        for (int i = 0; i < records.length; i++) {
            int index = i;
            assertEquals(i, phoneBook.getAllRecords().size());
            assertDoesNotThrow(() -> phoneBook.addRecord(records[index]));
        }
        var sortedRecords = new ArrayList<>(Arrays.asList(records));
        sortedRecords.sort(Comparator.comparing(PhoneBook.Record::getName).
                thenComparing(PhoneBook.Record::getNumber));
        assertEquals(sortedRecords, phoneBook.getAllRecords());
    }

    @Test
    void addSameRecord() throws SQLException {
        assertDoesNotThrow(() -> phoneBook.addRecord(records[0]));
        for (int i = 0; i < 10; i++) {
            assertThrows(AmbiguousRecordException.class, () -> phoneBook.addRecord(records[0]));
        }
        assertEquals(1, phoneBook.getAllRecords().size());
    }

    @Test
    void getNumbersByName() throws SQLException {
        for (var r : records) {
            assertDoesNotThrow(() -> phoneBook.addRecord(r));
        }
        var expectedResult = Arrays.asList(records[2].getNumber(), records[3].getNumber());
        expectedResult.sort(Comparator.comparing(a -> a));
        assertEquals(expectedResult, phoneBook.getNumbersByName(records[2].getName()));
    }

    @Test
    void getNamesByNumber() throws SQLException {
        for (var r : records) {
            assertDoesNotThrow(() -> phoneBook.addRecord(r));
        }
        var expectedResult = Arrays.asList(records[3].getName(), records[4].getName());
        expectedResult.sort(Comparator.comparing(a -> a));
        assertEquals(phoneBook.getNamesByNumber(records[3].getNumber()), expectedResult);
    }

    @Test
    void eraseRecord() throws SQLException {
        assertEquals(phoneBook.getAllRecords(), Collections.emptyList());
        assertDoesNotThrow(() -> phoneBook.addRecord(records[2]));
        assertDoesNotThrow(() -> phoneBook.addRecord(records[3]));

        assertEquals(2, phoneBook.getAllRecords().size());

        assertDoesNotThrow(() -> phoneBook.eraseRecord(records[2]));
        assertEquals(Collections.singletonList(records[3]), phoneBook.getAllRecords());

        assertDoesNotThrow(() -> phoneBook.eraseRecord(records[3]));
        assertEquals(0, phoneBook.getAllRecords().size());
    }

    @Test
    void changeNameOfRecordBasic() throws SQLException {
        var record = new PhoneBook.Record("Miku Miku", "12345 56789");
        assertDoesNotThrow(() -> phoneBook.addRecord(record));
        assertDoesNotThrow(() -> phoneBook.changeNameOfRecord(record, "Hatsune Miku"));
        assertEquals("Hatsune Miku", phoneBook.getNamesByNumber(record.getNumber()).get(0));
        assertEquals("Hatsune Miku", phoneBook.getNamesByNumber("12345 56789").get(0));
    }

    @Test
    void changeNameOfRecordCollision() throws SQLException {
        var anotherRecord = new PhoneBook.Record("Trump", records[4].getNumber());
        assertDoesNotThrow(() -> phoneBook.addRecord(records[4]));
        assertDoesNotThrow(() -> phoneBook.addRecord(anotherRecord));
        assertEquals(2, phoneBook.getAllRecords().size());

        assertThrows(AmbiguousRecordException.class,
                () -> phoneBook.changeNameOfRecord(records[4], "Trump"));
        assertEquals(2, phoneBook.getAllRecords().size());
    }

    @Test
    void changeNumberOfRecordBasic() throws SQLException {
        var record = new PhoneBook.Record("Miku Miku", "12345 56789");
        assertDoesNotThrow(() -> phoneBook.addRecord(record));
        assertDoesNotThrow(() -> phoneBook.changeNumberOfRecord(record, "2019"));
        assertEquals("2019", phoneBook.getNumbersByName(record.getName()).get(0));
        assertEquals("2019", phoneBook.getNumbersByName("Miku Miku").get(0));
    }

    @Test
    void changeNumberOfRecordCollision() throws SQLException {
        var anotherRecord = new PhoneBook.Record(records[4].getName(), ":-)");
        assertDoesNotThrow(() -> phoneBook.addRecord(records[4]));
        assertDoesNotThrow(() -> phoneBook.addRecord(anotherRecord));
        assertEquals(2, phoneBook.getAllRecords().size());

        assertThrows(AmbiguousRecordException.class,
                () -> phoneBook.changeNumberOfRecord(records[4], ":-)"));
        assertEquals(2, phoneBook.getAllRecords().size());
    }

    @Test
    void getAllRecordsAndClear() throws SQLException {
        var list = new ArrayList<PhoneBook.Record>();
        for (int i = 0; i < records.length; i++) {
            int index = i;
            assertEquals(list, phoneBook.getAllRecords());
            assertDoesNotThrow(() -> phoneBook.addRecord(records[index]));
            list.add(i, records[i]);
            list.sort(Comparator.comparing(PhoneBook.Record::getName).
                    thenComparing(PhoneBook.Record::getNumber));
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
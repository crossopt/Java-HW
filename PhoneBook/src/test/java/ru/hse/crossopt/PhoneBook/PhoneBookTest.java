package ru.hse.crossopt.PhoneBook;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {
    private PhoneBook phoneBook;
    private PhoneBookEntry oldGhostbusters = new PhoneBookEntry("Ghostbusters", "1-800-555-2368");
    private PhoneBookEntry newGhostbusters = new PhoneBookEntry("Ghostbusters", "0800 222 9911");
    private PhoneBookEntry carRental = new PhoneBookEntry("Car rental", "1-US-RENT-A-WRECK");
    private PhoneBookEntry longNumber = new PhoneBookEntry("Long number guy", "+0 23895 1289421 29873287 543543 923897 1105712");
    private PhoneBookEntry valjean = new PhoneBookEntry("Jean Valjean", "24601");
    private PhoneBookEntry emergencyNumber = new PhoneBookEntry("Emergency", "911");

    @BeforeEach
    void setUp() {
        phoneBook = new PhoneBook();
        phoneBook.add(longNumber);
        phoneBook.add(oldGhostbusters);
        phoneBook.add(carRental);
    }

    @AfterEach
    void tearDown() {
        phoneBook.clear();
    }
    
    @Test
    void add_missingElement() {
        assertTrue(phoneBook.add(emergencyNumber));
        assertTrue(phoneBook.add(new PhoneBookEntry("Report a fire!", emergencyNumber.getNumber())));
        assertTrue(phoneBook.add(newGhostbusters));
    }

    @Test
    void add_existingElement() {
        assertTrue(phoneBook.add(emergencyNumber));
        assertFalse(phoneBook.add(emergencyNumber));
        assertFalse(phoneBook.add(oldGhostbusters));
    }

    @Test
    void remove_missingElement() {
        assertFalse(phoneBook.remove(new PhoneBookEntry(longNumber.getName(), "0")));
        assertFalse(phoneBook.remove(newGhostbusters));
        assertFalse(phoneBook.remove(valjean));
    }

    @Test
    void remove_existingElement() {
        assertTrue(phoneBook.remove(carRental));
        assertFalse(phoneBook.contains(carRental));
        assertTrue(phoneBook.remove(oldGhostbusters));
        assertFalse(phoneBook.contains(oldGhostbusters));
    }

    @Test
    void findByName_oneEntry() {
        var result = phoneBook.findByName(carRental.getName());
        assertEquals(1, result.size());
        assertEquals(carRental.getName(), result.get(0).getName());
        assertEquals(carRental.getNumber(), result.get(0).getNumber());
    }

    @Test
    void findByName_manyEntries() {
        assertTrue(phoneBook.add(newGhostbusters));
        var result = phoneBook.findByName(newGhostbusters.getName());
        assertEquals(2, result.size());
        assertEquals(newGhostbusters.getName(), result.get(0).getName());
        assertEquals(newGhostbusters.getName(), result.get(1).getName());
        assertEquals(oldGhostbusters.getNumber(), result.get(0).getNumber());
        assertEquals(newGhostbusters.getNumber(), result.get(1).getNumber());
    }

    @Test
    void findByName_noEntries() {
        var result = phoneBook.findByName("Boyfriend");
        assertEquals(0, result.size());
    }

    @Test
    void findByNumber_oneEntry() {
        var result = phoneBook.findByNumber(carRental.getNumber());
        assertEquals(1, result.size());
        assertEquals(carRental.getNumber(), result.get(0).getNumber());
        assertEquals(carRental.getName(), result.get(0).getName());
    }

    @Test
    void findByNumber_manyEntries() {
        assertTrue(phoneBook.add(new PhoneBookEntry("Stop a crime!", emergencyNumber.getNumber())));
        assertTrue(phoneBook.add(new PhoneBookEntry("Report a fire!", emergencyNumber.getNumber())));
        assertTrue(phoneBook.add(new PhoneBookEntry("Save a life!", emergencyNumber.getNumber())));
        var result = phoneBook.findByNumber(emergencyNumber.getNumber());
        assertEquals(3, result.size());
        assertEquals(emergencyNumber.getNumber(), result.get(0).getNumber());
        assertEquals(emergencyNumber.getNumber(), result.get(1).getNumber());
        assertEquals(emergencyNumber.getNumber(), result.get(2).getNumber());
        assertEquals("Stop a crime!", result.get(0).getName());
        assertEquals("Report a fire!", result.get(1).getName());
        assertEquals("Save a life!", result.get(2).getName());
    }

    @Test
    void findByNumber_noEntries() {
        var result = phoneBook.findByNumber(valjean.getNumber());
        assertEquals(0, result.size());
    }

    @Test
    void changeName_missingElement() {
        assertFalse(phoneBook.changeName(valjean, "Javert"));
    }

    @Test
    void changeName_existingElement() {
        assertTrue(phoneBook.changeName(carRental, "Rent a wreck"));
        var result = phoneBook.findByNumber(carRental.getNumber());
        assertEquals(1, result.size());
        assertEquals("Rent a wreck", result.get(0).getName());
    }

    @Test
    void changeNumber_missingElement() {
        assertFalse(phoneBook.changeNumber(valjean, "0"));
    }

    @Test
    void changeNumber_existingElement() {
        assertTrue(phoneBook.changeNumber(oldGhostbusters, newGhostbusters.getNumber()));
        var result = phoneBook.findByName(oldGhostbusters.getName());
        assertEquals(1, result.size());
        assertEquals(newGhostbusters.getNumber(), result.get(0).getNumber());
    }

    @Test
    void getAll() {
        var result = phoneBook.getAll();
        assertEquals(3, result.size());
        assertEquals(longNumber.getName(), result.get(0).getName());
        assertEquals(longNumber.getNumber(), result.get(0).getNumber());
        assertEquals(oldGhostbusters.getName(), result.get(1).getName());
        assertEquals(oldGhostbusters.getNumber(), result.get(1).getNumber());
        assertEquals(carRental.getName(), result.get(2).getName());
        assertEquals(carRental.getNumber(), result.get(2).getNumber());
    }

    @Test
    void contains_existingElement() {
        assertTrue(phoneBook.contains(oldGhostbusters));
        assertTrue(phoneBook.contains(longNumber));
        assertTrue(phoneBook.contains(carRental));
    }

    @Test
    void contains_missingElement() {
        assertFalse(phoneBook.contains(new PhoneBookEntry(carRental.getName(), "1-US-RENT")));
        assertFalse(phoneBook.contains(new PhoneBookEntry("GB", oldGhostbusters.getNumber())));
        assertFalse(phoneBook.contains(new PhoneBookEntry("Short number guy", "0")));
    }
}
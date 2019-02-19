package ru.hse.crossopt.PhoneBook;

import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.List;

/**  Implementation of a phone book using Morphia and MongoDB. */
public class PhoneBook {
    @NotNull private final Datastore datastore;

    /**
     * Creates a new phone book with given name.
     * @param phoneBookName the name of the phone book.
     */
    public PhoneBook(@NotNull String phoneBookName) {
        Morphia morphia = new Morphia();
        morphia.mapPackage("ru.hse.crossopt.PhoneBook");
        datastore = morphia.createDatastore(new MongoClient(), phoneBookName);
        clear();
    }

    /**
     * Adds the given entry to the phone book.
     * @param entry an entry to be added to the phone book.
     * @return false if entry was in phone book previously or true otherwise.
     */
    public boolean add(@NotNull PhoneBookEntry entry) {
        if (contains(entry)) {
            return false;
        }
        datastore.save(entry);
        return true;
    }

    /**
     * Removes the given entry from the phone book.
     * @param entry an entry to be removed from the phone book.
     * @return true if entry was in phone book previously or false otherwise.
     */
    public boolean remove(@NotNull PhoneBookEntry entry) {
        if (!contains(entry)) {
            return false;
        }
        PhoneBookEntry matchingEntry = datastore.find(PhoneBookEntry.class).field("name").
                equal(entry.getName()).field("number").equal(entry.getNumber()).get();
        datastore.delete(matchingEntry);
        return true;
    }

    /**
     * Finds all of the entries with given name in phone book.
     * @param name a name to search for.
     * @return list of entries whose name matches the given name.
     */
    @NotNull public List<PhoneBookEntry> findByName(@NotNull String name) {
        return datastore.find(PhoneBookEntry.class).field("name").equal(name).asList();
    }

    /**
     * Finds all of the entries with given number in phone book.
     * @param number a number to search for.
     * @return list of entries whose number matches the given number.
     */
    @NotNull public List<PhoneBookEntry> findByNumber(@NotNull String number) {
        return datastore.find(PhoneBookEntry.class).field("number").equal(number).asList();
    }

    /**
     * Changes the name of the given entry.
     * Deletes the given entry and adds a new entry with the same number and a new name.
     * @param entry an entry whose name should be changed.
     * @param newName the new name for the entry.
     * @return true if entry was in phone book previously or false otherwise.
     */
    public boolean changeName(@NotNull PhoneBookEntry entry, @NotNull String newName) {
        var updatedEntry = new PhoneBookEntry(newName, entry.getNumber());
        if (!remove(entry)) {
            return false;
        }
        add(updatedEntry);
        return true;
    }

    /**
     * Changes the number of the given entry.
     * Deletes the given entry and adds a new entry with the same name and a new number.
     * @param entry an entry whose number should be changed.
     * @param newNumber the new number for the entry.
     * @return true if entry was in phone book previously or false otherwise.
     */
    public boolean changeNumber(@NotNull PhoneBookEntry entry, @NotNull String newNumber) {
        var updatedEntry = new PhoneBookEntry(entry.getName(), newNumber);
        if (!remove(entry)) {
            return false;
        }
        add(updatedEntry);
        return true;
    }

    /**
     * Gets all of the entries in the phone book.
     * @return a list of the entries in the phone book.
     */
    @NotNull public List<PhoneBookEntry> getAll() {
        return datastore.find(PhoneBookEntry.class).asList();
    }

    /**
     * Checks if given entry is in the phone book.
     * @param entry an entry to check.
     * @return true if the given entry is in the phone book, false otherwise
     */
    public boolean contains(@NotNull PhoneBookEntry entry) {
        return datastore.find(PhoneBookEntry.class).field("name").equal(entry.getName()).
                field("number").equal(entry.getNumber()).get() != null;
    }

    /** Removes all entries from the phone book. */
    public void clear() {
        datastore.getDB().dropDatabase();
    }
}

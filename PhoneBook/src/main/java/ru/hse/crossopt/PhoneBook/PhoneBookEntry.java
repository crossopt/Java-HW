package ru.hse.crossopt.PhoneBook;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/** Class that stores entry for phone book. */
@Entity
public class PhoneBookEntry {
    @Id
    private ObjectId id;
    private String name;
    private String number;

    public PhoneBookEntry() {}

    public PhoneBookEntry(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "name: " + getName() + " number: " + getNumber();
    }
}

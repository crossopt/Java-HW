package ru.hse.crossopt.PhoneBook;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * Console application that is implementation of interactive phone book.
 * Supports commands:
 * 0:   exit program
 * 1:   add entry
 * 2:   find phone numbers by name
 * 3:   find names by phone number
 * 4:   remove entry
 * 5:   change name for entry
 * 6:   change phone number for entry
 * 7:   print all entries
 * 8:   print help
 */
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final PhoneBook phonebook = new PhoneBook();

    /** Prints a list of the available commands to stdout. */
    private static void printPossibleCommands() {
        System.out.println("Format is:");
        System.out.println("0: exit program");
        System.out.println("1: add entry");
        System.out.println("2: find phone numbers by name");
        System.out.println("3: find names by phone number");
        System.out.println("4: remove entry");
        System.out.println("5: change name for entry");
        System.out.println("6: change phone number for entry");
        System.out.println("7: print all entries");
        System.out.println("8: print help");
    }

    /**
     * Tries to add entry to phone book and prints to stdout
     * either a message that the entry was added successfully,
     * or a message that the entry was already in the phone book.
     * @param entry an entry to add.
     */
    private static void addEntry(@NotNull PhoneBookEntry entry) {
        if (phonebook.add(entry)) {
            System.out.println("Entry added successfully!");
        } else {
            System.out.println("Entry already exists in the phone book.");
        }
    }

    /**
     * Tries to remove entry from phone book and prints to stdout
     * either a message that the entry was removed successfully,
     * or a message that the entry wasn't in the phone book.
     * @param entry an entry to remove.
     */
    private static void removeEntry(@NotNull PhoneBookEntry entry) {
        if (phonebook.remove(entry)) {
            System.out.println("Entry removed successfully!");
        } else {
            System.out.println("Entry does not exist in the phone book.");
        }
    }

    /**
     * Prints all phone numbers with the given name to stdout.
     * Prints an extra message if no phone numbers with the given name exist.
     * @param name a name to find all numbers for.
     */
    private static void findNumbersByName(@NotNull String name) {
        var numbersByName = phonebook.findByName(name);
        if (numbersByName.size() == 0) {
            System.out.println("There are no entries with this name in the phone book.");
        }
        numbersByName.forEach(entry -> System.out.println(entry.getNumber()));
    }

    /**
     * Prints all names with the given phone number to stdout.
     * Prints an extra message if no names with the given phone number exist.
     * @param number a number to find all names for.
     */
    private static void findNamesByNumber(@NotNull String number) {
        var namesByNumber = phonebook.findByNumber(number);
        if (namesByNumber.size() == 0) {
            System.out.println("There are no entries with this number in the phone book.");
        }
        namesByNumber.forEach(entry -> System.out.println(entry.getName()));
    }

    /**
     * Changes name for an entry given by user to a new name, also given by the user.
     * Prints either a message that the entry was changed successfully,
     * or a message that the entry wasn't in the phone book to stdout.
     * @param entry an entry to change.
     * @param newName the new name for the entry.
     */
    private static void changeNameOfEntry(@NotNull PhoneBookEntry entry, @NotNull String newName) {
        if (phonebook.changeName(entry, newName)) {
            System.out.println("Entry name changed successfully!");
        } else {
            System.out.println("Entry does not exist in the phone book.");
        }
    }

    /**
     * Changes phone number for an entry to a new number.
     * Prints either a message that the entry was changed successfully,
     * or a message that the entry wasn't in the phone book to stdout.
     * @param entry an entry to change.
     * @param newNumber the new number for the entry.
     */
    private static void changeNumberOfEntry(@NotNull PhoneBookEntry entry, @NotNull String newNumber) {
        if (phonebook.changeNumber(entry, newNumber)) {
            System.out.println("Entry number changed successfully!");
        } else {
            System.out.println("Entry does not exist in the phone book.");
        }
    }

    /**
     * Prints all of the entries in the phone book to stdout.
     * Also prints an extra message if phone book was empty.
     */
    private static void printAllEntries() {
        var entries = phonebook.getAll();
        if (entries.size() == 0) {
            System.out.println("Phone book is empty.");
        }
        entries.forEach(System.out::println);
    }

    /** If input was incorrect prompts user for a correct command. */
    private static void unknownCommand() {
        System.out.println("Command should be number from 0 to 8.");
        System.out.println("Enter 8 for a list of commands.");
    }

    /**
     * Prompts user for an existing name and reads it from stdin.
     * @throws NoSuchElementException if no name was inputted and stdin is empty.
     * @return a correct name.
     */
    @NotNull private static String readName() throws NoSuchElementException {
        System.out.println("Enter name of entry in the phone book:");
        return SCANNER.next();
    }

    /**
     * Prompts user for a new name and reads it from stdin.
     * @throws NoSuchElementException if no name was inputted and stdin is empty.
     * @return a correct name.
     */
    @NotNull private static String readNewName() throws NoSuchElementException {
        System.out.println("Enter new name:");
        return SCANNER.next();
    }

    /**
     * Prompts user for an existing telephone number and reads it from stdin.
     * @throws NoSuchElementException if no number was inputted and stdin is empty.
     * @return a correct telephone number.
     */
    @NotNull private static String readNumber() throws NoSuchElementException {
        System.out.println("Enter telephone number of entry in the phone book:");
        return SCANNER.next();
    }

    /**
     * Prompts user for a new telephone number and reads it from stdin.
     * @throws NoSuchElementException if no number was inputted and stdin is empty.
     * @return a correct telephone number.
     */
    @NotNull private static String readNewNumber() throws NoSuchElementException {
        System.out.println("Enter new phone number:");
        return SCANNER.next();
    }

    /**
     * Prompts user for an existing phone book entry and reads it from stdin.
     * @throws NoSuchElementException if no entry was inputted and stdin is empty.
     * @return a correct phone book entry.
     */
    @NotNull private static PhoneBookEntry readEntry() throws NoSuchElementException {
        return new PhoneBookEntry(readName(), readNumber());
    }

    /**
     * Prompts user for a new phone book entry and reads it from stdin.
     * @throws NoSuchElementException if no entry was inputted and stdin is empty.
     * @return a correct phone book entry.
     */
    @NotNull private static PhoneBookEntry readNewEntry() throws NoSuchElementException {
        return new PhoneBookEntry(readNewName(), readNewNumber());
    }

    /**
     * Prompts user for a command number and reads it from stdin.
     * @throws NoSuchElementException if no command was inputted and stdin is empty.
     * @return a string that is a command number.
     */
    @NotNull private static String readCommand() throws NoSuchElementException {
        System.out.println("Enter command number:");
        return SCANNER.next();
    }

    /** main function that reads commands from stdin and executes them. */
    public static void main(String[] args) {
        printPossibleCommands();
        while (true) {
            try {
                switch (readCommand()) {
                    case "0": {
                        phonebook.clear();
                        return;
                    } case "1": {
                        addEntry(readNewEntry());
                        break;
                    } case "2": {
                        findNumbersByName(readName());
                        break;
                    } case "3": {
                        findNamesByNumber(readNumber());
                        break;
                    } case "4": {
                        removeEntry(readEntry());
                        break;
                    } case "5": {
                        changeNameOfEntry(readEntry(), readNewName());
                        break;
                    } case "6": {
                        changeNumberOfEntry(readEntry(), readNewNumber());
                        break;
                    } case "7": {
                        printAllEntries();
                        break;
                    } case "8": {
                        printPossibleCommands();
                        break;
                    } default: {
                        unknownCommand();
                        break;
                    }
                }
            } catch (NoSuchElementException e) { //stdin is empty, need to exit
                phonebook.clear();
                return;
            }
        }
    }
}

package ru.hse.crossopt.PhoneBook;

import org.jetbrains.annotations.NotNull;

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
    private static final PhoneBook phonebook = new PhoneBook("myPhoneBook");

    /** Prints a list of the available commands to stdout. */
    private static void outputCommands() {
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
     * Tries to add entry given by user to phone book and prints result to stdout.
     * Result is either a message that the entry was added successfully,
     * or that the entry already was in the phone book.
     */
    private static void addEntry() {
        var entry = readEntry();
        if (phonebook.add(entry)) {
            System.out.println("Entry added successfully!");
        } else {
            System.out.println("Entry already exists in the phone book.");
        }
    }

    /**
     * Tries to remove entry given by user from phone book and prints result to stdout.
     * Result is either a message that the entry was removed successfully,
     * or that the entry wasn't in the phone book.
     */
    private static void removeEntry() {
        var entry = readEntry();
        if (phonebook.remove(entry)) {
            System.out.println("Entry removed successfully!");
        } else {
            System.out.println("Entry does not exist in the phone book.");
        }
    }

    /**
     * Prints all phone numbers with the name given by the user to stdout.
     * Prints an extra message if no entries with the given name exist.
     */
    private static void findNumbersByName() {
        String name = readName();
        var numbersByName = phonebook.findByName(name);
        if (numbersByName.size() == 0) {
            System.out.println("There are no entries with this name in the phone book.");
        }
        numbersByName.forEach(entry -> System.out.println(entry.getNumber()));
    }

    /**
     * Prints all names with the phone number given by the user to stdout.
     * Prints an extra message if no names with the given phone number exist.
     */
    private static void findNamesByNumber() {
        String number = readNumber();
        var namesByNumber = phonebook.findByNumber(number);
        if (namesByNumber.size() == 0) {
            System.out.println("There are no entries with this number in the phone book.");
        }
        namesByNumber.forEach(entry -> System.out.println(entry.getName()));
    }

    /**
     * Changes name for an entry given by user to a new name, also given by the user.
     * Prints result to stdout.
     * Result is either a message that the entry was changed successfully,
     * or that the entry wasn't in the phone book.
     */
    private static void changeName() {
        var entry = readEntry();
        System.out.println("Enter new name: ");
        String newName = SCANNER.next();
        if (phonebook.changeName(entry, newName)) {
            System.out.println("Entry name changed successfully!");
        } else {
            System.out.println("Entry does not exist in the phone book.");
        }
    }

    /**
     * Changes phone number for an entry given by user to a new number, also given by the user.
     * Prints result to stdout.
     * Result is either a message that the entry was changed successfully,
     * or that the entry wasn't in the phone book.
     */
    private static void changeNumber() {
        var entry = readEntry();
        System.out.println("Enter new number: ");
        String newNumber = SCANNER.next();
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
     * Prompts user for a name and reads it from stdin.
     * @return a correct name.
     */
    @NotNull private static String readName() {
        System.out.println("Enter name:");
        return SCANNER.next();
    }

    /**
     * Prompts user for a telephone number and reads it from stdin.
     * @return a correct telephone number.
     */
    @NotNull private static String readNumber() {
        System.out.println("Enter phone number:");
        return SCANNER.next();
    }

    /**
     * Prompts user for a phone book entry and reads it from stdin.
     * @return a correct phone book entry.
     */
    @NotNull private static PhoneBookEntry readEntry() {
        return new PhoneBookEntry(readName(), readNumber());
    }

    /** main function that reads commands from stdin and executes them. */
    public static void main(String[] args) {
        outputCommands();
        while (true) {
            System.out.println("Enter command number:");
            String command = SCANNER.next();
            switch (command) {
                case "0": {
                    return;
                } case "1": {
                    addEntry();
                    break;
                } case "2": {
                    findNumbersByName();
                    break;
                } case "3": {
                    findNamesByNumber();
                    break;
                } case "4": {
                    removeEntry();
                    break;
                } case "5": {
                    changeName();
                    break;
                } case "6": {
                    changeNumber();
                    break;
                } case "7": {
                    printAllEntries();
                    break;
                } case "8": {
                    outputCommands();
                    break;
                } default: {
                    unknownCommand();
                    break;
                }
            }
        }
    }
}

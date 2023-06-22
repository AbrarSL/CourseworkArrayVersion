import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner scan = new Scanner(System.in);

        String[][] cashiers = new String[3][];
        cashiers[0] = new String[2];
        cashiers[1] = new String[3];
        cashiers[2] = new String[5];

        int burgerStock = 50;

        displayHeader("Food Queue Management");
        displayCommands();

        while (true) {
            String command = inputPrompt(scan, "Enter a command (press \"H\" for help): ").strip().toUpperCase();

            if (command.equals("EXT") || command.equals("999")) {
                System.out.println("Exiting program...");
                break;
            }

            switch (command) {
                case "H":
                    displayCommands();
                    break;
                case "VFQ", "100":
                    viewAllQueues(cashiers);
                    break;
                case "VEQ", "101":
                    viewEmptyQueues(cashiers);
                    break;
                case "ACQ", "102":
                    addCustomerToQueue(scan, cashiers);
                    break;
                case "RCQ", "103":
                    removeCustomerFromQueue(scan, cashiers);
                    break;
                case "PCQ", "104":
                    burgerStock = removeServedCustomer(scan, cashiers, burgerStock);
                    break;
                case "VCS", "105":
                    viewSortedCustomers(cashiers);
                    break;
                case "SPD", "106":
                    storeProgramData(cashiers, burgerStock);
                    break;
                case "LPD", "107":
                    burgerStock = loadProgramData(cashiers, burgerStock);
                    break;
                case "STK", "108":
                    viewBurgerStock(burgerStock);
                    break;
                case "AFS", "109":
                    burgerStock = addToBurgerStock(scan, burgerStock);
                    break;
                default:
                    System.out.println("Unknown Command!");
            }
        }
    }

    private static void displayCommands() {
        final String commands = """
                101 or VEQ: View all empty queues.
                102 or ACQ: Add a customer to a queue.
                103 or RCQ: Remove a customer from a specific location on a queue.
                104 or PCQ: Remove a served customer.
                105 or VCS: View customers in alphabetical order.
                106 or SPD: Store program data into file.
                107 or LPD: Load program data from file.
                108 or STK: View remaining burger stock.
                109 or AFS: Add burgers to stock.
                999 or EXT: Exit the program.
                """;

        System.out.println(commands);
    }

    private static void displayHeader(String headerText) {
        final int headerLength = headerText.length() + 10;
        final int titlePaddingLength = (headerLength - 2 - headerText.length()) / 2;

        System.out.println("*".repeat(headerLength));
        System.out.println("*" + " ".repeat(titlePaddingLength) + headerText + " ".repeat(titlePaddingLength) + "*");
        System.out.println("*".repeat(headerLength));
    }

    private static String inputPrompt(Scanner scan, String prompt) {
        System.out.print(prompt);
        return scan.nextLine();
    }

    private static boolean addToQueue(String[] queue, String customer) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] == null) {
                queue[i] = customer;
                return true;
            }
        }

        return false;
    }

    private static String popFromQueue(String[] queue, int positionIndex) {
        String element = queue[positionIndex];
        shiftLeftQueue(queue, positionIndex);
        return element;
    }

    private static void shiftLeftQueue(String[] queue, int positionIndex) {
        for (int i = positionIndex; i < queue.length - 1; i++) {
            queue[i] = queue[i + 1];
        }

        queue[queue.length - 1] = null;
    }

    private static int getInteger(Scanner scan, String prompt, int rangeStart, int rangeEnd) {
        while (true) {
            try {
                int selectedNumber = Integer.parseInt(inputPrompt(scan, prompt));

                if (selectedNumber <= rangeEnd && selectedNumber >= rangeStart) {
                    return selectedNumber;
                }

                System.out.println("Number out of range! Range is " + rangeStart + " to " + rangeEnd);
            } catch (Exception error) {
                System.out.println("Please enter an integer!");
            }
        }
    }

    private static String[] concatenateQueues(String[][] cashiers) {
        int totalLength = 0;

        for (String[] queue : cashiers) {
            totalLength += queue.length;
        }

        String[] tempQueue = new String[totalLength];
        int queueIndex = 0;

        for (String[] queue : cashiers) {
            for (String name : queue) {
                if (name != null) {
                    tempQueue[queueIndex] = name;
                    queueIndex++;
                }
            }
        }

        int validItemsLength = tempQueue.length;

        for (int i = 0; i < tempQueue.length; i++) {
            if (tempQueue[i] == null) {
                validItemsLength = i;
                break;
            }
        }

        if (validItemsLength == tempQueue.length) {
            return tempQueue;
        }

        String[] newQueue = new String[validItemsLength];

        for (int i = 0; i < newQueue.length; i++) {
            newQueue[i] = tempQueue[i];
        }

        return newQueue;
    }

    private static void bubbleSortQueue(String[] queue) {
        boolean swapped = true;

        while (swapped) {
            swapped = false;

            for (int i = 0; i < queue.length - 1; i++) {
                if (stringIsGreater(queue[i], queue[i + 1])) {
                    String temp = queue[i];
                    queue[i] = queue[i + 1];
                    queue[i + 1] = temp;

                    swapped = true;
                }
            }
        }
    }

    private static boolean stringIsGreater(String firstString, String secondString) {
        String shortestString = firstString.length() > secondString.length() ? secondString : firstString;

        for (int i = 0; i < shortestString.length(); i++) {
            if (firstString.charAt(i) == secondString.charAt(i)) {
                continue;
            }

            return firstString.charAt(i) > secondString.charAt(i);
        }

        return false;
    }

    private static void viewAllQueues(String[][] cashiers) {
        final String titleText = "Cashier";
        final int longestQueueLength = 5;
        final int headerLength = titleText.length() + 10;
        final int paddingLength = ((headerLength / cashiers.length) - 1) / 2;

        displayHeader(titleText);

        for (int i = 0; i < longestQueueLength; i++) {
            for (String[] queue : cashiers) {
                System.out.print(" ".repeat(paddingLength));

                if (queue.length <= i) {
                    System.out.print(" ");
                } else if (queue[i] == null) {
                    System.out.print("X");
                } else {
                    System.out.print("O");
                }

                System.out.print(" ".repeat(paddingLength));
            }

            System.out.print("\n");
        }

        System.out.println("O - Occupied");
        System.out.println("X - Not Occupied");
    }

    private static void viewEmptyQueues(String[][] cashiers) {
        String[][] tempQueues = new String[3][];

        for (int i = 0; i < cashiers.length; i++) {
            if (cashiers[i][cashiers[i].length] == null) {
                tempQueues[i] = cashiers[i];
            } else {
                tempQueues[i] = new String[0];
            }
        }

        viewAllQueues(tempQueues);
    }

    private static void addCustomerToQueue(Scanner scan, String[][] cashiers) {
        int cashierNumber = getInteger(scan, "Enter cashier number: ", 0, cashiers.length - 1);
        String customerName = inputPrompt(scan, "Enter name of customer: ");

        if (addToQueue(cashiers[cashierNumber], customerName)) {
            System.out.println("Successfully added customer to queue!");
            return;
        }

        System.out.println("Couldn't add customer to queue! (Selected queue is full!)");
    }

    private static void removeCustomerFromQueue(Scanner scan, String[][] cashiers) {
        int cashierNumber = getInteger(scan, "Enter cashier number: ", 0, cashiers.length - 1);
        int positionIndex = getInteger(scan, "Enter customer position: ", 0, cashiers[cashierNumber].length - 1);
        String customerName = popFromQueue(cashiers[cashierNumber], positionIndex);

        if (customerName == null) {
            System.out.println("No customer found in that position!");
            return;
        }

        System.out.printf("Successfully removed customer %s from queue!\n", customerName);
    }

    private static int removeServedCustomer(Scanner scan, String[][] cashiers, int burgerStock) {
        int newStock = burgerStock - 5;

        if (newStock < 0) {
            System.out.println("Not enough items in stock! Cannot serve customer!");
            return burgerStock;
        }

        int cashierNumber = getInteger(scan, "Enter cashier number: ", 0, cashiers.length - 1);
        String customerName = popFromQueue(cashiers[cashierNumber], 0);

        if (customerName == null) {
            System.out.println("No customer found at the cashier!");
            return burgerStock;
        }

        System.out.printf("Successfully served customer %s!\n", customerName);
        return newStock;
    }

    private static void viewSortedCustomers(String[][] cashiers) {
        String[] allCustomers = concatenateQueues(cashiers);
        bubbleSortQueue(allCustomers);

        System.out.println("Customers in queue,");

        for (String customer : allCustomers) {
            System.out.println(customer);
        }
    }

    private static void storeProgramData(String[][] cashiers, int burgerStock) {
        String fileName = "programState.csv";

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(burgerStock + "\n");

            for (String[] queue : cashiers) {
                for (String name : queue) {
                    fileWriter.write(name + ",");
                }

                fileWriter.write("\n");
            }

            System.out.println("Successfully stored data in file: " + fileName);

            fileWriter.flush();
        } catch (IOException error) {
            System.out.println("Could not create or read file! Data not stored!");
            System.out.println("Message is: " + error.getMessage());
        }
    }

    private static int loadProgramData(String[][] cashiers, int burgerStock) {
        String fileName = "programState.csv";
        File file = new File(fileName);

        try {
            Scanner fileReader = new Scanner(file);

            if (!fileReader.hasNextInt()) {
                System.out.println("Stock data is not available! Save data may be corrupted!");
                return burgerStock;
            }

            int savedStock = fileReader.nextInt();
            fileReader.nextLine();

            for (String[] cashier : cashiers) {
                if (!fileReader.hasNextLine()) {
                    System.out.println("Complete data is not available in save file!");
                    System.out.println("Data may have been partially loaded!");
                    return savedStock;
                }

                String[] dataLine = fileReader.nextLine().split(",");

                if (dataLine.length < cashier.length) {
                    System.out.println("Complete data is not available in save file!");
                    System.out.println("Data may have been partially loaded!");
                    return savedStock;
                }

                for (int i = 0; i < cashier.length; i++) {
                    if (dataLine[i].equals("null")) {
                        cashier[i] = null;
                    } else {
                        cashier[i] = dataLine[i];
                    }
                }
            }

            System.out.println("Successfully loaded data from file: " + fileName);
            return savedStock;
        } catch (FileNotFoundException fileError) {
            System.out.println("File " + fileName + " not found!");
            System.out.println("Cannot load data!");
            return burgerStock;
        }
    }

    private static void viewBurgerStock(int burgerStock) {
        System.out.println("Available stock is: " + burgerStock);
    }

    private static int addToBurgerStock(Scanner scan, int burgerStock) {
        int newStock = burgerStock + getInteger(scan, "Enter the number of burgers to add: ", 0, 50);

        if (newStock > 50) {
            System.out.println("Stock capacity exceeded! Stock will not be updated!");
            System.out.println("Maximum capacity is 50 items!");
            return burgerStock;
        }

        return newStock;
    }
}

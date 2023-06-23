import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String[][] cashiers = new String[3][];
    private static int burgerStock = 50;

    public static void main(String[] args) {
        initQueues();
        displayCommands();

        while (true) {
            String command = inputPrompt("Enter a command (press \"H\" for help): ").strip().toUpperCase();

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
                    viewEmptyQueues();
                    break;
                case "ACQ", "102":
                    addCustomerToQueue();
                    break;
                case "RCQ", "103":
                    removeCustomerFromQueue();
                    break;
                case "PCQ", "104":
                    removeServedCustomer();
                    break;
                case "VCS", "105":
                    viewSortedCustomers();
                    break;
                case "SPD", "106":
                    storeProgramData();
                    break;
                case "LPD", "107":
                   loadProgramData();
                    break;
                case "STK", "108":
                    viewBurgerStock();
                    break;
                case "AFS", "109":
                    addToBurgerStock();
                    break;
                default:
                    System.out.println("Unknown Command!");
            }
        }
    }

    /**
     * Initializes the queues.
     * This must *always* be called at the beginning of the main() method.
     * This method mutates {@link Main#cashiers}
     */
    private static void initQueues() {
        cashiers[0] = new String[2];
        cashiers[1] = new String[3];
        cashiers[2] = new String[5];
    }

    /**
     * Displays all the available commands to the user via stdout.
     */
    private static void displayCommands() {
        displayHeader("Foodies Fave Food Center");

        final String commands = """
                100 or VFQ: View all queues.
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

    /**
     * Displays some header text with added decorations.
     * @param headerText The text that will be displayed.
     */
    private static void displayHeader(String headerText) {
        final int headerLength = headerText.length() + 10;
        final int titlePaddingLength = (headerLength - 2 - headerText.length()) / 2;

        System.out.println("*".repeat(headerLength));
        System.out.println("*" + " ".repeat(titlePaddingLength) + headerText + " ".repeat(titlePaddingLength) + "*");
        System.out.println("*".repeat(headerLength));
    }

    /**
     * Prompts the user for an input.
     * Do note that the method does not add a newline to the prompt.
     * @param prompt The prompt text that will be shown to the user.
     * @return A String object that is not validated.
     */
    private static String inputPrompt(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Prompts the user for an integer input within a given range.
     * This method will block until a valid input is given.
     * @param prompt The prompt text that will be shown to the user.
     * @param rangeStart The inclusive starting point.
     * @param rangeEnd The inclusive ending point.
     * @return An integer within the given range.
     */
    private static int intInputPrompt(String prompt, int rangeStart, int rangeEnd) {
        while (true) {
            try {
                int selectedNumber = Integer.parseInt(inputPrompt(prompt));

                if (selectedNumber <= rangeEnd && selectedNumber >= rangeStart) {
                    return selectedNumber;
                }

                System.out.println("Number out of range! Range is " + rangeStart + " to " + rangeEnd);
            } catch (Exception error) {
                System.out.println("Please enter an integer!");
            }
        }
    }

    /**
     * Prompts the user for validated input.
     * This method will block until a valid input is given.
     * The validation cases are hardcoded within the method.
     * @param prompt The prompt text that will be shown to the user.
     * @return A validated String object.
     */
    private static String validatedInputPrompt(String prompt) {
        String[] forbiddenStrings = {"null"};
        String userInput = "";
        boolean notValid = true;

        System.out.println("The following strings are forbidden,");
        for (String forbiddenText : forbiddenStrings) {
            System.out.println(forbiddenText);
        }

        while (notValid) {
            userInput = inputPrompt(prompt);

            if (userInput.length() == 0) {
                System.out.println("Input can not be empty!");
                continue;
            }

            notValid = false;

            for (String forbiddenText : forbiddenStrings) {
                if (userInput.equals(forbiddenText)) {
                    notValid = true;
                    break;
                }
            }
        }

        return userInput;
    }

    /**
     * Adds the given string to the given queue/array.
     * The string is always added to the first 'null' position found in the array.
     * The given arrays are mutated in-place
     * @param queue A 1-dimensional String array that will be mutated.
     * @param customer The string to be added to the array.
     * @return 'false' if the operation failed (No free space in the array).
     */
    private static boolean addToQueue(String[] queue, String customer) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] == null) {
                queue[i] = customer;
                return true;
            }
        }

        return false;
    }

    /**
     * Removes a string from the given array at the given index.
     * This method will reorder the array to fill any 'null' holes.
     * The given array is mutated in-place.
     * @param queue A 1-dimensional String array that will be mutated.
     * @param positionIndex The index of the String to be removed.
     * @return The String that was removed from the array.
     */
    private static String popFromQueue(String[] queue, int positionIndex) {
        String element = queue[positionIndex];
        shiftLeftQueue(queue, positionIndex);
        return element;
    }

    /**
     * Shifts all elements after the starting index to the left by 1 position.
     * The element at the starting index is overwritten and the last element is made null.
     * The given array is mutated in-place.
     * @param queue A 1-dimensional String array that will be mutated.
     * @param positionIndex The index at which the shift operation should start.
     */
    private static void shiftLeftQueue(String[] queue, int positionIndex) {
        for (int i = positionIndex; i < queue.length - 1; i++) {
            queue[i] = queue[i + 1];
        }

        queue[queue.length - 1] = null;
    }

    /**
     * Find the longest child queue/array from a 2-dimensional array.
     * @param queues A 2-dimensional String array.
     * @return A reference to the longest 1-dimensional array.
     */
    private static String[] getLongestQueue(String[][] queues) {
        String[] longest = queues[0];

        for (String[] queue : queues) {
            if (queue.length > longest.length) {
                longest = queue;
            }
        }

        return longest;
    }

    /**
     * Flattens a 2-dimensional array into a 1-dimensional array.
     * This method will not copy null pointers.
     * The size of the returned array will be equal to the number of valid (non-null) String refs from the input array.
     * @param queues A 2-dimensional String array.
     * @return A 1-dimensional array that contains all the valid elements from the input array.
     */
    private static String[] flattenQueues(String[][] queues) {
        int totalLength = 0;

        for (String[] queue : queues) {
            totalLength += queue.length;
        }

        String[] tempQueue = new String[totalLength];
        int queueIndex = 0;

        for (String[] queue : queues) {
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

    /**
     * Sorts a given 1-dimensional String array according to alphabetical order.
     * The method implements the Bubble Sort algorithm.
     * The given array is sorted in-place.
     * @param queue A 1-dimensional String array that will be mutated.
     * @see <a href="https://www.geeksforgeeks.org/bubble-sort/">Algorithm reference used.</a>
     */
    private static void bubbleSortQueue(String[] queue) {
        boolean swapped = true;

        while (swapped) {
            swapped = false;

            for (int i = 0; i < queue.length - 1; i++) {
                if (stringShouldSwap(queue[i], queue[i + 1])) {
                    String temp = queue[i];
                    queue[i] = queue[i + 1];
                    queue[i + 1] = temp;

                    swapped = true;
                }
            }
        }
    }

    /**
     * Indicates if two strings should be swapped according to alphabetical order.
     * Case is ignored as all strings are converted to lowercase.
     * Shorter strings are considered alphabetically higher.
     * Equivalent strings maintain position.
     * @param firstString The first string.
     * @param secondString The second string.
     * @return 'true' if strings should be swapped to maintain alphabetical order.
     */
    private static boolean stringShouldSwap(String firstString, String secondString) {
        firstString = firstString.toLowerCase();
        secondString = secondString.toLowerCase();
        String shortestString = firstString.length() > secondString.length() ? secondString : firstString;
        boolean isLengthEqual = firstString.length() == secondString.length();

        for (int i = 0; i < shortestString.length(); i++) {
            if (firstString.charAt(i) == secondString.charAt(i)) {
                continue;
            }

            return firstString.charAt(i) > secondString.charAt(i);
        }

        if (isLengthEqual) {
            return false;
        }

        return secondString == shortestString;
    }

    /**
     * Implements the VFQ/100 option for the program.
     * The vacancy of all the spots in all queues are printed in a formatted manner.
     * @param queues A 2-dimensional string array.
     */
    private static void viewAllQueues(String[][] queues) {
        final String titleText = "Cashier";
        final int longestQueueLength = getLongestQueue(queues).length;
        final int headerLength = titleText.length() + 10;
        final int paddingLength = ((headerLength / queues.length) - 1) / 2;

        displayHeader(titleText);

        for (int i = 0; i < longestQueueLength; i++) {
            for (String[] queue : queues) {
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

    /**
     * Implements the VEQ/101 option for the program.
     * Displays all queues that have at least 1 empty slot.
     */
    private static void viewEmptyQueues() {
        String[][] tempQueues = new String[3][];

        for (int i = 0; i < cashiers.length; i++) {
            if (cashiers[i][cashiers[i].length - 1] == null) {
                tempQueues[i] = cashiers[i];
            } else {
                tempQueues[i] = new String[0];
            }
        }

        viewAllQueues(tempQueues);
    }

    /**
     * Implements the ACQ/102 option for the program.
     * The user is prompted for the cashier number (0-indexed) and the name of the customer to be added.
     * This method mutates {@link Main#cashiers}.
     */
    private static void addCustomerToQueue() {
        int cashierNumber = intInputPrompt("Enter cashier number: ", 0, cashiers.length - 1);
        String customerName = validatedInputPrompt("Enter name of customer: ");

        if (addToQueue(cashiers[cashierNumber], customerName)) {
            System.out.println("Successfully added customer to queue!");
            return;
        }

        System.out.println("Couldn't add customer to queue! (Selected queue is full!)");
    }

    /**
     * Implements the RCQ/103 option for the program.
     * The user is prompted for the cashier number (0-indexed) and the position (0-indexed) of the customer to remove.
     * This method mutates {@link Main#cashiers}.
     */
    private static void removeCustomerFromQueue() {
        int cashierNumber = intInputPrompt("Enter cashier number: ", 0, cashiers.length - 1);
        int positionIndex = intInputPrompt("Enter customer position: ", 0, cashiers[cashierNumber].length - 1);
        String customerName = popFromQueue(cashiers[cashierNumber], positionIndex);

        if (customerName == null) {
            System.out.println("No customer found in that position!");
            return;
        }

        System.out.printf("Successfully removed customer %s from queue!\n", customerName);
    }

    /**
     * Implements the PCQ/104 option for the program.
     * The user is prompted for the cashier number (0-indexed) to serve customers from. Served customers are removed.
     * This method may mutate the {@link Main#burgerStock} field if doing so would not set {@link Main#burgerStock}
     * to a negative value.
     */
    private static void removeServedCustomer() {
        int newStock = burgerStock - 5;

        if (newStock < 0) {
            System.out.println("Not enough items in stock! Cannot serve customer!");
            return;
        }

        int cashierNumber = intInputPrompt("Enter cashier number: ", 0, cashiers.length - 1);
        String customerName = popFromQueue(cashiers[cashierNumber], 0);

        if (customerName == null) {
            System.out.println("No customer found at the cashier!");
            return;
        }

        if (newStock <= 10) {
            System.out.println("Warning: Stock is running low!");
        }

        burgerStock = newStock;
        System.out.printf("Successfully served customer %s!\n", customerName);
    }

    /**
     * Implements the VCS/105 option for the program.
     * All the customer names stored in {@link Main#cashiers} are displayed to the user in alphabetical order.
     */
    private static void viewSortedCustomers() {
        String[] allCustomers = flattenQueues(cashiers);
        bubbleSortQueue(allCustomers);

        displayHeader("Customer Names (Sorted View)");

        for (String customer : allCustomers) {
            System.out.println(customer);
        }
    }

    /**
     * Implements the SPD/106 option for the program.
     * This method stores data from {@link Main#burgerStock} and {@link Main#cashiers}
     * into a file in the current directory.
     */
    private static void storeProgramData() {
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

    /**
     * Implements the LPD/107 option for the program.
     * This method loads data from a file in the current directory into {@link Main#burgerStock} and
     * {@link Main#cashiers}
     */
    private static void loadProgramData() {
        String fileName = "programState.csv";
        File file = new File(fileName);

        try {
            Scanner fileReader = new Scanner(file);

            if (!fileReader.hasNextInt()) {
                System.out.println("Stock data is not available! Save data may be corrupted!");
                return;
            }

            int savedStock = fileReader.nextInt();
            fileReader.nextLine();

            for (String[] queue : cashiers) {
                if (!fileReader.hasNextLine()) {
                    System.out.println("Complete data is not available in save file!");
                    System.out.println("Data may have been partially loaded!");
                    burgerStock = savedStock;
                    return;
                }

                String[] dataLine = fileReader.nextLine().split(",");

                if (dataLine.length < queue.length) {
                    System.out.println("Complete data is not available in save file!");
                    System.out.println("Data may have been partially loaded!");
                    burgerStock = savedStock;
                    return;
                }

                for (int i = 0; i < queue.length; i++) {
                    if (dataLine[i].equals("null")) {
                        queue[i] = null;
                    } else {
                        queue[i] = dataLine[i];
                    }
                }
            }

            System.out.println("Successfully loaded data from file: " + fileName);
            burgerStock = savedStock;
        } catch (FileNotFoundException fileError) {
            System.out.println("File " + fileName + " not found!");
            System.out.println("Cannot load data!");
        }
    }

    /**
     * Implements STK/108 option for the program.
     * This method displays the remaining stock ({@link Main#burgerStock}) to the user.
     */
    private static void viewBurgerStock() {
        displayHeader("Stock Information");
        System.out.println("Available stock is: " + burgerStock);
    }

    /**
     * Implements AFS/109 option for the program.
     * This method prompts the user for a number and adds that amount to the stock ({@link Main#burgerStock}).
     * This method will only mutate {@link Main#burgerStock} if it will not exceed 50.
     */
    private static void addToBurgerStock() {
        int newStock = burgerStock + intInputPrompt("Enter the number of burgers to add: ", 0, 50);

        if (newStock > 50) {
            System.out.println("Stock capacity exceeded! Stock will not be updated!");
            System.out.println("Maximum capacity is 50 items!");
            return;
        }

        burgerStock = newStock;
    }
}

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final Scanner INPUT = new Scanner(System.in);
    private static final String[][] CASHIERS = new String[3][];
    private static final int BURGERS_PER_CUSTOMER = 5;
    private static final int MAX_BURGER_STOCK = 50;
    private static final int LOW_BURGER_STOCK = 10;
    private static final String DEFAULT_FILE_NAME = "programState.txt";
    private static int burgerStock = 0;

    public static void main(String[] args) {
        initQueues(); // Queues must always be initialized before doing anything else
        displayCommands();

        while (true) {
            // All commands are converted to uppercase for easier parsing
            String command = inputPrompt("Enter a command (\"H\" or 000 for Help Menu): ").strip().toUpperCase();

            if (command.equals("EXT") || command.equals("999")) {
                System.out.println("Exiting program...");
                break;
            }

            // Option selection
            switch (command) {
                case "H", "000":
                    displayCommands();
                    break;
                case "VFQ", "100":
                    viewAllQueues(CASHIERS);
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
     * Initializes the child queues.
     * This method mutates {@link Main#CASHIERS}
     */
    private static void initQueues() {
        CASHIERS[0] = new String[2];
        CASHIERS[1] = new String[3];
        CASHIERS[2] = new String[5];
    }

    /**
     * Implements H/000 option for the program.
     * Displays all the available commands to the user via stdout.
     */
    private static void displayCommands() {
        displayHeader("Foodies Fave Food Center");

        final String commands = """
                000 or H: View help menu.
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
     *
     * @param headerText The text that will be displayed.
     */
    private static void displayHeader(String headerText) {
        final int headerLength = headerText.length() + 10; // 10 is added to give the header some horizontal padding
        final int titlePaddingLength = (headerLength - 2 - headerText.length()) / 2;

        System.out.println("*".repeat(headerLength));
        System.out.println("*" + " ".repeat(titlePaddingLength) + headerText + " ".repeat(titlePaddingLength) + "*");
        System.out.println("*".repeat(headerLength));
    }

    /**
     * Prompts the user for an input.
     * Do note that the method does not add a newline to the prompt.
     *
     * @param prompt The prompt text that will be shown to the user.
     * @return A String object that is not validated.
     */
    private static String inputPrompt(String prompt) {
        System.out.print(prompt);
        return INPUT.nextLine();
    }

    /**
     * Prompts the user for an integer input within a given range.
     * This method will block until a valid input is given.
     *
     * @param prompt     The prompt text that will be shown to the user.
     * @param rangeStart The inclusive starting point.
     * @param rangeEnd   The inclusive ending point.
     * @return An integer within the given range.
     */
    private static int intInputPrompt(String prompt, int rangeStart, int rangeEnd) {
        while (true) {
            try {
                int selectedNumber = Integer.parseInt(inputPrompt(prompt));

                if (selectedNumber <= rangeEnd && selectedNumber >= rangeStart) { // Ensure number is within range
                    return selectedNumber;
                }

                System.out.printf("Number out of range! Range is %d to %d!\n", rangeStart, rangeEnd);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter an integer!");
            }
        }
    }

    /**
     * Prompts the user for validated input.
     * This method will block until a valid input is given.
     * The validation cases are hardcoded within the method.
     *
     * @param prompt The prompt text that will be shown to the user.
     * @return A validated String object.
     */
    private static String validatedInputPrompt(String prompt) {
        while (true) {
            String userInput = inputPrompt(prompt).strip();

            if (userInput.length() == 0) {
                System.out.println("Input cannot be empty!");
                continue;
            }

            if (userInput.contains(",")) {
                System.out.println("Input cannot contain \",\"!");
                continue;
            }

            if (userInput.equals("null")) {
                System.out.println("Input cannot be \"null\"!");
                continue;
            }

            return userInput;
        }
    }

    /**
     * Adds the given string to the given queue/array.
     * The string is always added to the first 'null' position found in the array.
     * The given arrays are mutated in-place
     *
     * @param queue    A 1-dimensional String array that will be mutated.
     * @param customer The string to be added to the array.
     * @return 'false' if the operation failed (No free space in the array).
     */
    private static boolean addToQueue(String[] queue, String customer) {
        for (int i = 0; i < queue.length; i++) { // Linear search for the first null position
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
     *
     * @param queue         A 1-dimensional String array that will be mutated.
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
     *
     * @param queue         A 1-dimensional String array that will be mutated.
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
     *
     * @param queues A 2-dimensional String array.
     * @return A reference to the longest 1-dimensional array.
     */
    private static String[] getLongestQueue(String[][] queues) {
        String[] longest = queues[0];

        for (String[] queue : queues) { // Linear search
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
     *
     * @param queues A 2-dimensional String array.
     * @return A 1-dimensional array that contains all the valid elements from the input array.
     */
    private static String[] flattenQueues(String[][] queues) {
        int totalLength = 0;

        for (String[] queue : queues) { // Sum all child array sizes
            totalLength += queue.length;
        }

        String[] tempQueue = new String[totalLength];
        int queueIndex = 0;

        for (String[] queue : queues) { // Copy all non-null refs
            for (String name : queue) {
                if (name != null) {
                    tempQueue[queueIndex] = name;
                    queueIndex++;
                }
            }
        }

        int validItemsLength = tempQueue.length;

        for (int i = 0; i < tempQueue.length; i++) { // Find extent of valid elements
            if (tempQueue[i] == null) {
                validItemsLength = i;
                break;
            }
        }

        if (validItemsLength == tempQueue.length) { // Avoid copy if all elements were valid
            return tempQueue;
        }

        String[] newQueue = new String[validItemsLength]; // Properly sized array for valid elements

        for (int i = 0; i < newQueue.length; i++) { // Copy element refs to new array
            newQueue[i] = tempQueue[i];
        }

        return newQueue;
    }

    /**
     * Sorts a given 1-dimensional String array according to alphabetical order.
     * The method implements the Bubble Sort algorithm.
     * The given array is sorted in-place.
     *
     * @param queue A 1-dimensional String array that will be mutated.
     * @see <a href="https://www.geeksforgeeks.org/bubble-sort/">Algorithm Reference.</a>
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
     *
     * @param firstString  The first string.
     * @param secondString The second string.
     * @return 'true' if strings should be swapped to maintain alphabetical order.
     */
    private static boolean stringShouldSwap(String firstString, String secondString) {
        // Lowercase all arguments to ignore case
        firstString = firstString.toLowerCase();
        secondString = secondString.toLowerCase();

        // Use the shortest string to avoid indexing out of bounds
        String shortestString = firstString.length() > secondString.length() ? secondString : firstString;
        boolean isLengthEqual = firstString.length() == secondString.length();

        for (int i = 0; i < shortestString.length(); i++) { // Compare order of each character pair
            // ASCII values are checked
            if (firstString.charAt(i) == secondString.charAt(i)) {
                continue;
            }

            return firstString.charAt(i) > secondString.charAt(i);
        }

        if (isLengthEqual) {
            return false;
        }

        return secondString == shortestString; // The references are compared intentionally
    }

    /**
     * Validate the contents of the given file.
     * The file is checked to ensure that the data in the file is in the proper format and length to
     * fill the structures in the program memory.
     *
     * @param saveFile The File object that is to be validated.
     * @return 'true' will be returned if the file passed all validations.
     * @throws FileNotFoundException This exception will be thrown if the file cannot be found.
     */
    private static boolean validateSaveFile(File saveFile) throws FileNotFoundException {
        Scanner fileReader = new Scanner(saveFile);

        if (!fileReader.hasNextInt()) { // Ensure stock data is available
            System.out.println("File Validation Error: Stock data not available!");
            fileReader.close();
            return false;
        }

        fileReader.nextLine();

        for (int i = 0; i < CASHIERS.length; i++) { // Ensure file contains enough data for all queues
            if (!fileReader.hasNextLine()) {
                System.out.println("File Validation Error: File does not contain all queues!");
                fileReader.close();
                return false;
            }

            String[] dataLine = fileReader.nextLine().split(",");

            if (dataLine.length < CASHIERS[i].length) {
                System.out.println("File Validation Error: Queues in file do not contain enough data!");
                fileReader.close();
                return false;
            }
        }

        fileReader.close();
        return true;
    }

    /**
     * Implements the VFQ/100 option for the program.
     * The vacancy of all the spots in all queues are printed in a formatted manner.
     *
     * @param queues A 2-dimensional string array.
     */
    private static void viewAllQueues(String[][] queues) {
        final String titleText = "Cashier";
        final int longestQueueLength = getLongestQueue(queues).length; // Ensures that all elements are considered
        final int headerLength = titleText.length() + 10; // Ensure queue padding matches up with header
        final int paddingLength = ((headerLength / queues.length) - 1) / 2;

        displayHeader(titleText);

        for (int i = 0; i < longestQueueLength; i++) { // Calculate child array index in outer loop
            for (String[] queue : queues) {
                System.out.print(" ".repeat(paddingLength));

                if (queue.length <= i) { // Ensure index is not out of bounds
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

        for (int i = 0; i < CASHIERS.length; i++) { // Find all full queues
            int lastIndex = CASHIERS[i].length - 1;

            if (CASHIERS[i][lastIndex] == null) { // Checks if tail of queue is free
                tempQueues[i] = CASHIERS[i];
            } else {
                tempQueues[i] = new String[0]; // Dummy placeholder queue if the original is full
            }
        }

        viewAllQueues(tempQueues);
    }

    /**
     * Implements the ACQ/102 option for the program.
     * The user is prompted for the cashier number (0-indexed) and the name of the customer to be added.
     * This method mutates {@link Main#CASHIERS}.
     */
    private static void addCustomerToQueue() {
        int cashierNumber = intInputPrompt("Enter cashier number: ", 0, CASHIERS.length - 1);
        String customerName = validatedInputPrompt("Enter name of customer: ");

        if (!addToQueue(CASHIERS[cashierNumber], customerName)) { // Check if op failed
            System.out.println("Couldn't add customer to queue! (Selected queue is full!)");
            return;
        }

        System.out.printf("Successfully added customer %s to queue %d!\n", customerName, cashierNumber);
    }

    /**
     * Implements the RCQ/103 option for the program.
     * The user is prompted for the cashier number (0-indexed) and the position (0-indexed) of the customer to remove.
     * This method mutates {@link Main#CASHIERS}.
     */
    private static void removeCustomerFromQueue() {
        int cashierNumber = intInputPrompt("Enter cashier number: ", 0, CASHIERS.length - 1);
        int positionIndex = intInputPrompt("Enter customer position: ", 0, CASHIERS[cashierNumber].length - 1);
        String customerName = popFromQueue(CASHIERS[cashierNumber], positionIndex);

        if (customerName == null) { // Check if op failed
            System.out.println("No customer found in that position!");
            return;
        }

        System.out.printf("Successfully removed customer %s from queue %d!\n", customerName, cashierNumber);
    }

    /**
     * Implements the PCQ/104 option for the program.
     * The user is prompted for the cashier number (0-indexed) to serve customers from.
     * Served customers are removed and {@link Main#BURGERS_PER_CUSTOMER} is subtracted from {@link Main#burgerStock}
     * only if doing so would not set {@link Main#burgerStock} to a negative value.
     */
    private static void removeServedCustomer() {
        int newStock = burgerStock - BURGERS_PER_CUSTOMER;

        if (newStock < 0) { // Ensure stock will not be negative
            System.out.println("Not enough items in stock! Cannot serve customer!");
            return;
        }

        int cashierNumber = intInputPrompt("Enter cashier number: ", 0, CASHIERS.length - 1);
        String customerName = popFromQueue(CASHIERS[cashierNumber], 0);

        if (customerName == null) { // Ensure that customer is present in queue
            System.out.println("No customer found at the cashier!");
            return;
        }

        if (newStock <= LOW_BURGER_STOCK) { // Low stock warning
            System.out.println("Warning: Stock is running low!");
        }

        burgerStock = newStock; // Update stock if no validation failures occur
        System.out.printf("Successfully served customer %s!\n", customerName);
    }

    /**
     * Implements the VCS/105 option for the program.
     * All the customer names stored in {@link Main#CASHIERS} are displayed to the user in alphabetical order.
     */
    private static void viewSortedCustomers() {
        String[] allCustomers = flattenQueues(CASHIERS);
        bubbleSortQueue(allCustomers);

        displayHeader("Customer Names (Sorted View)");

        for (String customer : allCustomers) {
            System.out.println(customer);
        }
    }

    /**
     * Implements the SPD/106 option for the program.
     * This method stores data from {@link Main#burgerStock} and {@link Main#CASHIERS}
     * into the file {@link Main#DEFAULT_FILE_NAME} in the current directory.
     * Data is stored as text in a CSV format.
     */
    private static void storeProgramData() {
        try (FileWriter fileWriter = new FileWriter((DEFAULT_FILE_NAME))) {
            fileWriter.write(burgerStock + "\n");

            for (String[] queue : CASHIERS) {
                for (String name : queue) {
                    fileWriter.write(name + ",");
                }

                fileWriter.write("\n");
            }

            System.out.printf("Successfully stored data in file \"%s\"\n", DEFAULT_FILE_NAME);

            fileWriter.flush();
        } catch (IOException error) {
            System.out.printf("File \"%s\" could not be created or read!\n", DEFAULT_FILE_NAME);
            System.out.println("Data not stored!");
        }
    }

    /**
     * Implements the LPD/107 option for the program.
     * This method loads data from the file {@link Main#DEFAULT_FILE_NAME} in the current directory into {@link Main#burgerStock} and
     * {@link Main#CASHIERS}.
     * The file is validated before loading.
     * The file must contain text in a CSV format in the right order.
     */
    private static void loadProgramData() {
        File saveFile = new File(DEFAULT_FILE_NAME);

        try (Scanner fileReader = new Scanner(saveFile)) {
            if (!validateSaveFile(saveFile)) {
                System.out.println("Data not loaded!");
                return;
            }

            int savedStock = fileReader.nextInt();
            fileReader.nextLine();

            for (String[] queue : CASHIERS) {
                String[] dataLine = fileReader.nextLine().split(",");

                for (int i = 0; i < queue.length; i++) { // Parse data and store in program memory
                    if (dataLine[i].equals("null")) {
                        queue[i] = null;
                    } else {
                        queue[i] = dataLine[i];
                    }
                }
            }

            burgerStock = savedStock;
            System.out.printf("Successfully loaded data from file \"%s\"\n", saveFile.getName());
        } catch (FileNotFoundException fileError) {
            System.out.printf("File \"%s\" could not be found or read!\n", saveFile.getName());
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
     * This method will only mutate {@link Main#burgerStock} if it will not exceed {@link Main#MAX_BURGER_STOCK}.
     */
    private static void addToBurgerStock() {
        int newStock = burgerStock + intInputPrompt("Enter the number of burgers to add: ", 0, MAX_BURGER_STOCK);

        if (newStock > MAX_BURGER_STOCK) {
            System.out.println("Stock capacity exceeded! Stock will not be updated!");
            System.out.printf("Maximum capacity is %s items!\n", MAX_BURGER_STOCK);
            return;
        }

        burgerStock = newStock;
    }
}

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner scan = new Scanner(System.in);

        String[][] cashiers = new String[3][];
        cashiers[0] = new String[2];
        cashiers[1] = new String[3];
        cashiers[2] = new String[5];

        int burgerStock = 50;

        while (true) {
            String command = inputPrompt(scan, "Enter a command: ").strip().toUpperCase();

            if (command.equals("EXT") || command.equals("999")) {
                System.out.println("Exiting program...");
                break;
            }

            switch (command) {
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
//                case "SPD", "106":
//                    storeProgramData();
//                    break;
//                case "LPD", "107":
//                    loadProgramData();
//                    break;
//                case "STK", "108":
//                    viewBurgerStock();
//                    break;
//                case "AFS", "109":
//                    addToBurgerStock();
//                    break;
                default:
                    System.out.println("Unknown Command!");
            }
        }
    }

    private static String inputPrompt(Scanner scan, String prompt) {
        System.out.print(prompt);
        return scan.nextLine();
    }

    private static boolean isQueueFull(String[] queue) {
        for (String customer : queue) {
            if (customer == null) {
                return false;
            }
        }

        return true;
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

    private static boolean recursiveCharCompare(String firstString, String secondString, int comparisonIndex) {
        if (firstString.charAt(comparisonIndex) == secondString.charAt(comparisonIndex)) {
            return recursiveCharCompare(firstString, secondString, ++comparisonIndex);
        }

        return firstString.charAt(comparisonIndex) > secondString.charAt(comparisonIndex);
    }

    private static boolean stringIsGreater(String firstString, String secondString) {
        if (firstString.equals(secondString)) {
            return false;
        }

        if (firstString.length() > secondString.length()) {
            String paddedString = secondString + " ".repeat(firstString.length() - secondString.length());
            return recursiveCharCompare(firstString, paddedString, 0);
        }

        String paddedString = firstString + " ".repeat(secondString.length() - firstString.length());
        return recursiveCharCompare(paddedString, secondString, 0);
    }

    private static void viewAllQueues(String[][] cashiers) {
        final int longestQueueLength = 5;
        final int headerLength = 21;
        final int paddingLength = ((headerLength / cashiers.length) - 1) / 2;
        final String titleText = "Cashier";
        final int titlePaddingLength = (headerLength - 2 - titleText.length()) / 2;

        System.out.println("*".repeat(headerLength));
        System.out.println("*" + " ".repeat(titlePaddingLength) + titleText + " ".repeat(titlePaddingLength) + "*");
        System.out.println("*".repeat(headerLength));

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
            if (!isQueueFull(cashiers[i])) {
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

    private static int removeServedCustomer(Scanner scan, String[][] cashiers, int stock) {
        int newStock = stock - 5;

        if (newStock < 0) {
            System.out.println("Not enough items in stock! Cannot serve customer!");
            return stock;
        }

        int cashierNumber = getInteger(scan, "Enter cashier number: ", 0, cashiers.length - 1);
        String customerName = popFromQueue(cashiers[cashierNumber], 0);

        if (customerName == null) {
            System.out.println("No customer found at the cashier!");
            return stock;
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
}

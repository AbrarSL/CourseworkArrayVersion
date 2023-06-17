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
//                case "RCQ", "103":
//                    removeCustomerFromQueue(scan, cashiers);
//                    break;
//                case "PCQ", "104":
//                    removeServedCustomer();
//                    break;
//                case "VCS", "105":
//                    viewSortedCustomers();
//                    break;
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

    private static int getInteger(Scanner scan, String prompt, int rangeStart, int rangeEnd) {
        int selectedNumber = 0;

        while (true) {
            try {
                selectedNumber = Integer.parseInt(inputPrompt(scan, prompt));

                if (selectedNumber <= rangeEnd && selectedNumber >= rangeStart) {
                    break;
                }

                System.out.println("Number out of range! Range is " + rangeStart + " to " + rangeEnd);
            } catch (Exception error) {
                System.out.println("Please enter an integer!");
            }
        }

        return selectedNumber;
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
        } else {
            System.out.println("Couldn't add customer to queue! (Selected queue is full!)");
        }
    }
}

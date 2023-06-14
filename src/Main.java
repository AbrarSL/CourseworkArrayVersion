import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner scan = new Scanner(System.in);

        String[][] cashiers = new String[3][];
        cashiers[0] = new String[2];
        cashiers[1] = new String[3];
        cashiers[2] = new String[5];

        int burgerStock = 50;

        cashiers[0][0] = "Saman";

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
//                case "ACQ", "102":
//                    addCustomerToQueue();
//                    break;
//                case "RCQ", "103":
//                    removeCustomerFromQueue();
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

    private static boolean isQueueEmpty(String[] queue) {
        for (String customer : queue) {
            if (customer != null) {
                return false;
            }
        }

        return true;
    }

    private static void viewAllQueues(String[][] cashiers) {
        final int longestQueueLength = 5;
        final int headerLength = 21;
        final int paddingLength = ((headerLength / cashiers.length) - 1) / 2;

        System.out.println("*".repeat(headerLength));
        System.out.println("*" + " ".repeat(6) + "Cashier" + " ".repeat(6) + "*");
        System.out.println("*".repeat(headerLength));

        for (int i = 0; i < longestQueueLength; i++) {
            for (String[] queue : cashiers) {
                System.out.print(" ".repeat(paddingLength));

                if (queue.length <= i) {
                    System.out.print(" ");
                } else if (queue[i] == null) {
                    System.out.print("O");
                } else {
                    System.out.print("X");
                }

                System.out.print(" ".repeat(paddingLength));
            }

            System.out.print("\n");
        }

        System.out.println("X - Occupied");
        System.out.println("O - Not Occupied");
    }

    private static void viewEmptyQueues(String[][] cashiers) {
        String[][] tempQueues = new String[3][];

        for (int i = 0; i < cashiers.length; i++) {
            if (isQueueEmpty(cashiers[i])) {
                tempQueues[i] = cashiers[i];
            } else {
                tempQueues[i] = new String[0];
            }
        }

        viewAllQueues(tempQueues);
    }
}
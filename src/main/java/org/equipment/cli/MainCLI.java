package org.equipment.cli;

import static org.equipment.utils.ConsoleColors.CYAN_BOLD_BRIGHT;
import static org.equipment.utils.ConsoleColors.RESET;

import java.util.Scanner;

public final class MainCLI {

    private MainCLI() {
    }

    public static void start(Scanner scanner) {
        boolean running = true;
        while (running) {
            printDivider();
            System.out.println("Equipment Rental Manager");
            System.out.println("1. Manager Portal");
            System.out.println("2. Customer Portal");
            System.out.println("3. Exit");
            System.out.print("Select option: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    ManagerCLI.open(scanner);
                    break;
                case "2":
                    CustomerCLI.open(scanner);
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }
        printDivider();
        System.out.println("Exiting... bye!");
    }

    static void printDivider() {
        System.out.println(CYAN_BOLD_BRIGHT + "------------------------------------------" + RESET);
    }
}


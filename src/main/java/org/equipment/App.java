package org.equipment;

import java.io.IOException;
import java.util.Scanner;

import org.equipment.cli.MainCLI;
import org.equipment.data.DataStore;

public class App {
    public static void main(String[] args) throws IOException {
        DataStore.seed();

        try (Scanner scanner = new Scanner(System.in)) {
            MainCLI.start(scanner);
        }
    }
}


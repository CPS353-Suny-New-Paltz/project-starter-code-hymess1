package project.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;


  // Step A: console based client for the compute engine.
  // Uses Scanner to accept user input and prints results.

public class ConsoleClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EngineComputeAPI engine = new EngineComputeAPIImpl();

        System.out.println("=== Largest Prime Factor Calculator ===");
        System.out.println("Enter integers one at a time.");
        System.out.println("Type 'done' when finished.\n");

        List<Integer> inputs = new ArrayList<>();

        // ---- Read numbers ----
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("done")) {
                break;
            }

            try {
                inputs.add(Integer.parseInt(line));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer or 'done'.");
            }
        }

        if (inputs.isEmpty()) {
            System.out.println("No inputs provided. Exiting.");
            scanner.close();
            return;
        }

        System.out.println("\nResults:");

        boolean allSucceeded = true;

        // ---- Run computation ----
        for (int value : inputs) {

            EngineComputeAPI.ComputeResponse response =
                engine.computeSingle(value, ":");

            if (!response.success()) {
                allSucceeded = false;
                System.out.println("❌ Error for input " + value + ": "
                                   + response.asFormatted());
            } else {
                System.out.println(response.asFormatted());
            }
        }

        // ---- Final status ----
        if (allSucceeded) {
            System.out.println("\n✅ All computations completed successfully.");
        } else {
            System.out.println("\n⚠️  Some computations failed.");
        }

        scanner.close();
    }
}

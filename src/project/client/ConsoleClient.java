package project.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import network.Network;
import network.NetworkServiceGrpc;

/**
 * Console-based gRPC client for the compute engine.
 * Uses Scanner to accept user input and submits a job via the Network API.
 */
public class ConsoleClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ---------- gRPC SETUP ----------
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        NetworkServiceGrpc.NetworkServiceBlockingStub stub =
                NetworkServiceGrpc.newBlockingStub(channel);

        System.out.println("=== Largest Prime Factor Calculator ===");
        System.out.println("Enter integers one at a time.");
        System.out.println("Type 'done' when finished.\n");

        List<Integer> inputs = new ArrayList<>();

        // ---------- READ NUMBERS ----------
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
            channel.shutdown();
            return;
        }

        // ---------- READ OUTPUT FILE ----------
        System.out.print("\nEnter output file path: ");
        String outputPath = scanner.nextLine().trim();

        if (outputPath.isEmpty()) {
            System.out.println("Invalid output path. Exiting.");
            scanner.close();
            channel.shutdown();
            return;
        }

        // ---------- OPTIONAL DELIMITERS ----------
        System.out.print("Key/value delimiter (default ':'): ");
        String kvDelim = scanner.nextLine().trim();
        if (kvDelim.isEmpty()) {
            kvDelim = ":";
        }

        System.out.print("Pair delimiter (default ';'): ");
        String pairDelim = scanner.nextLine().trim();
        if (pairDelim.isEmpty()) {
            pairDelim = ";";
        }

        // ---------- WRITE INPUTS TO TEMP FILE ----------
        String inputPath = "client_input.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputPath))) {
            for (int i = 0; i < inputs.size(); i++) {
                if (i > 0) {
                    writer.write(",");
                }
                writer.write(String.valueOf(inputs.get(i)));
            }
        } catch (Exception e) {
            System.out.println("Failed to write input file: " + e.getMessage());
            scanner.close();
            channel.shutdown();
            return;
        }

        // ---------- BUILD gRPC REQUEST ----------
        Network.JobRequest request =
                Network.JobRequest.newBuilder()
                        .setInputSourcePointer(inputPath)
                        .setOutputDestinationPointer(outputPath)
                        .setKeyValueDelimiter(kvDelim)
                        .setPairDelimiter(pairDelim)
                        .build();

        // ---------- SUBMIT JOB ----------
        network.Network.JobResult result;
        try {
            result = stub.submitJob(request);
        } catch (Exception e) {
            System.out.println("❌ Failed to contact server: " + e.getMessage());
            scanner.close();
            channel.shutdown();
            return;
        }

        // ---------- REPORT RESULT ----------
        if (result.getSuccess()) {
            System.out.println("\n✅ Job completed successfully.");
            System.out.println("Results:");
            System.out.println(result.getResultText());
        } else {
            System.out.println("\n❌ Job failed:");
            System.out.println(result.getErrorMessage());
        }

        // ---------- CLEANUP ----------
        scanner.close();
        channel.shutdown();
    }
}

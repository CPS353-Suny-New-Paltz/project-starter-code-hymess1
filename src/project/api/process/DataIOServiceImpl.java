package project.api.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;

/**
 * Reads integers from a file and writes results back to a file.
 * Updated with full validation + defensive exception handling for Checkpoint 5.
 */
public class DataIOServiceImpl implements DataIOService {

    @Override
    public DataReadResponse read(DataReadRequest request) {
        try {
            // -------- VALIDATION --------
            if (request == null) {
                return emptyResponse();
            }
            if (request.source() == null) {
                return emptyResponse();
            }

            String path = request.source().asString();
            if (path == null || path.isBlank()) {
                return emptyResponse();
            }

            // -------- READ FILE --------
            List<Integer> numbers = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

                String line = reader.readLine();
                if (line != null) {
                    line = line.trim();

                    if (!line.isEmpty()) {
                        for (String part : line.split(",")) {
                            try {
                                numbers.add(Integer.parseInt(part.trim()));
                            } catch (NumberFormatException ignored) {
                                // Bad integers are ignored by design.
                            }
                        }
                    }
                }

            } catch (IOException e) {
                // IO failure → safe empty list
                return emptyResponse();
            }

            List<Integer> finalList = numbers;
            return () -> finalList;

        } catch (Exception e) {
            // Hard failure isolation for Checkpoint 5
            return emptyResponse();
        }
    }


    @Override
    public DataWriteResponse write(DataWriteRequest request) {
        try {
            // -------- VALIDATION --------
            if (request == null) {
                return basicFailure("Write request was null.");
            }
            if (request.destination() == null) {
                return basicFailure("Destination pointer was null.");
            }

            String path = request.destination().asString();
            if (path == null || path.isBlank()) {
                return basicFailure("Invalid destination path.");
            }

            String payload = request.payload();
            if (payload == null) {
                // No validation needed — empty payload is allowed.
                payload = "";
            }

            java.io.File outFile = new java.io.File(path);

            // -------- WRITE FILE --------
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile, true))) {
                if (outFile.exists() && outFile.length() > 0) {
                    writer.write(",");
                }

                writer.write(payload);
                return basicSuccess("Successfully wrote to file.");

            } catch (IOException e) {
                return basicFailure("Failed to write to file: " + e.getMessage());
            }

        } catch (Exception e) {
            // Catch-all unexpected failure
            return basicFailure("Unexpected error: " + e.getMessage());
        }
    }


    // -------- Helpers --------

    private DataReadResponse emptyResponse() {
        return () -> List.of();
    }

    private DataWriteResponse basicSuccess(String msg) {
        return new DataWriteResponse() {
            @Override
            public StatusCode code() {
                return StatusCode.SUCCESS;
            }

            @Override
            public String message() {
                return msg;
            }
        };
    }

    private DataWriteResponse basicFailure(String msg) {
        return new DataWriteResponse() {
            @Override
            public StatusCode code() {
                return StatusCode.FAILURE;
            }

            @Override
            public String message() {
                return msg;
            }
        };
    }
}

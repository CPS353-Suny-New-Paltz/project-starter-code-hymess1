package project.api.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;

/**
 * This version reads integers from a user-specified file and writes result
 * strings back to a user-specified file. 
 */
public class DataIOServiceImpl implements DataIOService {

    /**
     * Reads data from the file pointed to by request.source().
     * Expected format: comma-separated integers on a single line.
     */
    @Override
    public DataReadResponse read(DataReadRequest request) {
        if (request == null || request.source() == null) {
            return () -> "";
        }

        String path = request.source().asString();
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();

            // If the file is empty or doesn’t exist, return an empty payload
            if (line != null) {
                builder.append(line.trim());
            }

        } catch (IOException e) {
            return () -> "";
        }

        // DataReadResponse is a functional interface so lambda is allowed
        return () -> builder.toString();
    }

    /**
     * Writes a string payload to the destination file.
     * For this project, multiple writes to the same file are appended as a single
     * comma-separated line (no newlines), so Checkpoint4 can see all results
     * on one line.
     */
    @Override
    public DataWriteResponse write(DataWriteRequest request) {
        if (request == null || request.destination() == null) {
            return basicFailure("Destination pointer was null.");
        }

        String path = request.destination().asString();
        String payload = request.payload();
        if (payload == null) {
            payload = "";
        }

        java.io.File outFile = new java.io.File(path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile, true))) {
            // If the file already has content, separate entries with a comma.
            if (outFile.exists() && outFile.length() > 0) {
                writer.write(",");
            }

            writer.write(payload);
            // No newline: keep everything on a single comma-separated line.

            return basicSuccess("Successfully wrote to file.");
        } catch (IOException e) {
            return basicFailure("Failed to write to file: " + e.getMessage());
        }
    }


    // Helpers to build clean responses
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

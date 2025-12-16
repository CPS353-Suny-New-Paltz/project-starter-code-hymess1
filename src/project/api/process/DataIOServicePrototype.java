package project.api.process;

import project.annotations.ProcessAPIPrototype;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;

import java.util.List;
import java.util.stream.Collectors;

public class DataIOServicePrototype {

    @ProcessAPIPrototype
    public void prototype(DataIOService api) {
        // Create a source pointer (client-specified)
        DataPointer source = () -> "in://client-input";

        // Wrap it in a read request
        DataReadRequest readRequest = () -> source;

        // Call the read() method
        DataReadResponse readResponse = api.read(readRequest);

        // Convert List<Integer> to comma-separated string for writing
        List<Integer> ints = readResponse.payload();
        String joined = (ints == null)
                ? ""
                : ints.stream()
                      .map(String::valueOf)
                      .collect(Collectors.joining(","));

        // Build a destination pointer
        DataPointer destination = () -> "out://destination";

        // Build a write request using the converted string
        DataWriteRequest writeRequest = new DataWriteRequest() {
            @Override
            public DataPointer destination() {
                return destination;
            }

            @Override
            public String payload() {
                return joined;
            }
        };

        // Perform the write
        DataWriteResponse writeResponse = api.write(writeRequest);

        // Prototype-only: ensure all return types are non-null
        if (readResponse == null || writeResponse == null || writeResponse.message() == null) {
            String ignore = "";
        }
    }
}

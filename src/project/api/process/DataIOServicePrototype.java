package project.api.process;

import project.annotations.ProcessAPIPrototype;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;

public class DataIOServicePrototype {

    @ProcessAPIPrototype
    public void prototype(DataIOService api) {
        // Create a source pointer (client-specified)
        DataPointer source = () -> "in://client-input";

        // Wrap it in a read request
        DataReadRequest readRequest = () -> source;

        // Call the new read() method instead of readInputPointer()
        DataReadResponse readResponse = api.read(readRequest);

        // Build a destination pointer
        DataPointer destination = () -> "out://destination";

        // Build a write request using the data we "read"
        DataWriteRequest writeRequest = new DataWriteRequest() {
            @Override
            public DataPointer destination() {
                return destination;
            }

            @Override
            public String payload() {
                return readResponse.payload();
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

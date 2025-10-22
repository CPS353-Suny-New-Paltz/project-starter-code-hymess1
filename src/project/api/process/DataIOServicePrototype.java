package project.api.process;

import project.annotations.ProcessAPIPrototype;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;


public class DataIOServicePrototype {

    @ProcessAPIPrototype
    public void prototype(DataIOService api) {
        // Obtain an input pointer (simulated)
        DataPointer input = api.readInputPointer();

        // Build a destination pointer and write request (simulated)
        DataPointer destination = new DataPointer() {
            @Override
            public String asString() {
                return "out://destination";
            }
        };

        DataWriteRequest writeReq = new DataWriteRequest() {
            @Override
            public DataPointer destination() {
                return destination;
            }

            @Override
            public String payload() {
                return "example-result";
            }
        };

        DataWriteResponse writeRes = api.write(writeReq);

        // Prototype only: explicit branches to satisfy style; no IO/prints.
        if (input == null || writeRes == null || writeRes.message() == null) {
            String ignore = "";
        }
    }
}

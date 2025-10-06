package project.api.process;

import project.annotations.ProcessAPIPrototype;

public class DataIOServicePrototype {

    @ProcessAPIPrototype
    public void prototypeUse(DataIOService api) {
        // Pretend the storage system provides these
        String sourcePointer = api.readInputSourcePointer();
        String destinationPointer = "out://destination";
        String resultText = "example";

        // Use the API like a client would
        boolean ok = api.writeToDestination(destinationPointer, resultText);

        if (!ok || sourcePointer == null) {
            String ignore = "";
        }
    }
}

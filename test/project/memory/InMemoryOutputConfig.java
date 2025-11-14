package project.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * Test-only configuration that holds in-memory output data.
 * Represents a fake writable destination for integration tests.
 */
public class InMemoryOutputConfig {
    private final List<String> outputValues = new ArrayList<>();

    /** Writes a result string to the output list. */
    public void write(String value) {
        outputValues.add(value);
    }

    /** Returns the current list of written output strings. */
    public List<String> getOutputValues() {
        return outputValues;
    }
}

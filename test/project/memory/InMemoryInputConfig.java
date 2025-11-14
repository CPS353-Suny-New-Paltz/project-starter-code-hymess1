package project.memory;

import java.util.List;

/**
 * Test-only configuration that holds in-memory input data.
 * Represents a fake data source for integration tests.
 */
public class InMemoryInputConfig {
    private final List<Integer> inputValues;

    public InMemoryInputConfig(List<Integer> inputValues) {
        this.inputValues = inputValues;
    }

    /** Returns the list of integers to be read. */
    public List<Integer> getInputValues() {
        return inputValues;
    }
}

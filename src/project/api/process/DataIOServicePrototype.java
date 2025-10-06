package project.api.process;

import project.annotations.ProcessAPIPrototype;

/**
 * Prototype for the process boundary.
 * Demonstrates reading a few integers from a "source".
 */
public class DataIOServicePrototype {

    /**
     * Single public prototype method.
     * Returns an int[] (assignment allows arrays) and never null.
     */
    @ProcessAPIPrototype
    public int[] prototypeReadIntegers(String sourcePointer) {
        if (sourcePointer == null || sourcePointer.isEmpty()) {
            // Non-null, empty payload on error-like conditions.
            return new int[0];
        }
        // Trivial sample payload for exercising the conceptual API.
        return new int[] { 10, 29, 42 };
    }
}


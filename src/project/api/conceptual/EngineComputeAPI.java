package project.api.conceptual;

import project.annotations.ConceptualAPI;

/**
 * Conceptual boundary API: orchestrator ↔ core compute.
 * - Stateless transformation of inputs to outputs
 * - Provide defaults to make common use easy
 */
@ConceptualAPI
public interface EngineComputeAPI {

    /**
     * Convenience overload that applies the default key-value delimiter ":".
     * Never returns null; always a formatted string.
     */
    default String computeForSingleInput(int n) {
        return computeForSingleInput(n, ":");
    }

    /**
     * Compute a formatted "n<delimiter>result" string for a single input.
     * Never returns null; delimiter may be "" (caller-chosen).
     */
    String computeForSingleInput(int n, String keyValueDelimiter);
}


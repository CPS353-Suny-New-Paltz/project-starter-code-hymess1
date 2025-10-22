package project.api.conceptual;

import project.annotations.ConceptualAPI;

/**
 * Conceptual boundary API: orchestrator ↔ core compute. Contract is stateless
 * and returns wrapper types (never null).
 */
@ConceptualAPI
public interface EngineComputeAPI {

    /**
     * Primary compute method. Never returns null.
     */
    ComputeResponse compute(ComputeRequest request);

    /**
     * Convenience: compute for a single input using a delimiter. Delegates to the
     * primary method. Never returns null.
     */
    default ComputeResponse computeSingle(int n, String keyValueDelimiter) {
        return compute(new ComputeRequest() {
            @Override
            public int input() {
                return n;
            }

            @Override
            public String delimiter() {
                return keyValueDelimiter;
            }
        });
    }

    /**
     * Request wrapper for conceptual compute. (Interfaces nested to avoid extra
     * files for the prototype phase.)
     */
    interface ComputeRequest {
        int input();

        String delimiter();
    }

    /**
     * Response wrapper for conceptual compute. Callers can render to text or
     * inspect fields. Never implemented here (prototype phase only).
     */
    interface ComputeResponse {
        /**
         * Human-readable representation like "n:result". Never null.
         */
        String asFormatted();
    }
}

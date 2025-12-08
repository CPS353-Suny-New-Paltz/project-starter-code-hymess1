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
        return compute(
            new ComputeRequest() {
                @Override
                public int input() {
                    return n;
                }

                @Override
                public String delimiter() {
                    return keyValueDelimiter;
                }
            }
        );
    }

    interface ComputeRequest {
        int input();
        String delimiter();
    }

    
    /**
     * Response wrapper for conceptual compute.
     * Callers can render to text or inspect fields. Never null.
     */
    interface ComputeResponse {

        /**
         * Status of the compute operation.
         */
        ComputeStatusCode status();

        /**
         * Human-readable representation like "n:result".
         * Only meaningful when status().success() is true.
         */
        String asFormatted();

        /**
         * Convenience helper so callers can just ask "did this work?".
         */
        default boolean success() {
            return status().success();
        }
    }

    /**
     * Status code for compute operations.
     */
    enum ComputeStatusCode {
        SUCCESS(true),
        INVALID_INPUT(false),
        INTERNAL_ERROR(false);

        private final boolean success;

        ComputeStatusCode(boolean success) {
            this.success = success;
        }

        public boolean success() {
            return success;
        }
    }
}

package project.api.conceptual;

import project.api.conceptual.EngineComputeAPI.ComputeRequest;
import project.api.conceptual.EngineComputeAPI.ComputeResponse;

/**
 * Empty implementation for Checkpoint 3.
 * Stateless; returns a non-null placeholder response.
 */
public class EngineComputeAPIImpl implements EngineComputeAPI {

    @Override
    public ComputeResponse compute(ComputeRequest request) {
        final int n = (request == null) ? 0 : request.input();
        final String delim = (request == null || request.delimiter() == null) ? ":" : request.delimiter();

        // Placeholder response; real compute logic comes later.
        return new ComputeResponse() {
            @Override
            public String asFormatted() {
                return n + delim + "TBD";
            }
        };
    }
}

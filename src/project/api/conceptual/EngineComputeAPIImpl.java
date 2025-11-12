package project.api.conceptual;

import project.api.conceptual.EngineComputeAPI.ComputeRequest;
import project.api.conceptual.EngineComputeAPI.ComputeResponse;

/**
 * Empty implementation for Checkpoint 3.
 * Stateless conceptual compute placeholder.
 */
public class EngineComputeAPIImpl implements EngineComputeAPI {

    @Override
    public ComputeResponse compute(ComputeRequest request) {
        final int n = (request == null) ? 0 : request.input();
        final String delim = (request == null || request.delimiter() == null) ? ":" : request.delimiter();

        return () -> n + delim + "TBD";
    }
}

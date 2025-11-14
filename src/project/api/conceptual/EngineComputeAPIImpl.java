package project.api.conceptual;

import project.api.conceptual.EngineComputeAPI.ComputeRequest;
import project.api.conceptual.EngineComputeAPI.ComputeResponse;

/**
 * Empty implementation for Checkpoint 3.
 * Does NOT compute yet. Returns empty placeholders.
 */
public class EngineComputeAPIImpl implements EngineComputeAPI {

    @Override
    public ComputeResponse compute(ComputeRequest request) {
        return () -> "";   // Completely empty output for CP3
    }
}

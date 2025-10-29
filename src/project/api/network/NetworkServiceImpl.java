package project.api.network;

import project.api.conceptual.EngineComputeAPI;
import project.api.process.DataIOService;
import project.model.JobRequest;
import project.model.JobResult;

/**
 * Empty implementation for Checkpoint 3.
 * Wires together dependencies in later checkpoints.
 */
public class NetworkServiceImpl implements NetworkService {

    private final EngineComputeAPI engineCompute;
    private final DataIOService dataIO;

    public NetworkServiceImpl(EngineComputeAPI engineCompute, DataIOService dataIO) {
        this.engineCompute = engineCompute;
        this.dataIO = dataIO;
    }

    @Override
    public JobResult submitJob(JobRequest request) {
        // Placeholder for now (tests will drive real behavior later).
        return null;
    }
}

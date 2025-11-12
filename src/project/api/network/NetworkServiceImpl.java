package project.api.network;

import project.api.process.DataIOService;
import project.model.JobRequest;
import project.model.JobResult;

/**
 * Empty implementation for Checkpoint 3.
 * Talks to the DataIOService.
 */
public class NetworkServiceImpl implements NetworkService {

    private final DataIOService dataIO;

    public NetworkServiceImpl(DataIOService dataIO) {
        this.dataIO = dataIO;
    }

    @Override
    public JobResult submitJob(JobRequest request) {
        // Placeholder – no real networking yet
        return new JobResult(false, "TBD", "");
    }
}

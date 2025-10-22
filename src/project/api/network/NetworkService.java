package project.api.network;

import project.annotations.NetworkAPI;
import project.model.JobRequest;
import project.model.JobResult;

/**
 * Network boundary API: user ↔ compute engine.
 * Returns a wrapper (JobResult) and never throws across the boundary.
 */
@NetworkAPI
public interface NetworkService {

    /**
     * Submit a job describing input/output sources and delimiters.
     * Never returns null. Failures are represented in the JobResult.
     */
    JobResult submitJob(JobRequest request);
}

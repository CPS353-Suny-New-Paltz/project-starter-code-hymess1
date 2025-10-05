package project.api.network;

import project.annotations.NetworkAPI;
import project.model.JobRequest;
import project.model.JobResult;

/**
 * Network boundary API: user ↔ compute engine.
 * - Stateless, stable contract
 * - Returns a wrapper (JobResult) instead of void
 */
@NetworkAPI
public interface NetworkService {

    /**
     * Submit a job containing input/output pointers and delimiters.
     * Never returns null. On failure, returns JobResult with success=false
     * and a non-empty error message.
     */
    JobResult submitJob(JobRequest request);
}


package project.api.network;

import project.annotations.NetworkAPIPrototype;
import project.model.JobRequest;
import project.model.JobResult;

/**
 * Prototype for the network boundary.
 * Keep logic minimal—just demonstrate the shape of the call/response.
 */
public class NetworkServiceProto {

    /**
     * Single public prototype method for this boundary.
     * Returns non-null JobResult; fields never null.
     */
    @NetworkAPIPrototype
    public JobResult prototypeSubmit(JobRequest request) {
        if (request == null) {
            return new JobResult(false, "", "Null request");
        }

        final String pair = request.getDelimiterSpec() != null
                ? safe(request.getDelimiterSpec().getPairDelimiter()) : ";";
        final String kv = request.getDelimiterSpec() != null
                ? safe(request.getDelimiterSpec().getKeyValueDelimiter()) : ":";

        // Minimal echo to prove wiring; no real orchestration here.
        final String formatted = "n" + kv + "result" + pair;
        return new JobResult(true, formatted, "");
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}


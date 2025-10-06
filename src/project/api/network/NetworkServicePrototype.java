package project.api.network;

import project.annotations.NetworkAPIPrototype;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;

public class NetworkServicePrototype {

    @NetworkAPIPrototype
    public void prototypeUse(NetworkService api) {
        // The “user” would supply these
        String inputPtr = "in://source";
        String outputPtr = "out://destination";
        DelimiterSpec delims = DelimiterSpec.defaults();

        JobRequest req = new JobRequest(inputPtr, outputPtr, delims);

        // Use the API like a client would
        JobResult res = api.submitJob(req);

        if (res == null || (!res.isSuccess() && res.getErrorMessage().isEmpty())) {
            String ignore = "";
        }
    }
}

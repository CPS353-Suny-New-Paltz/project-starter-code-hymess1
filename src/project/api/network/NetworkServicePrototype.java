package project.api.network;

import project.annotations.NetworkAPIPrototype;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;

public class NetworkServicePrototype {

    @NetworkAPIPrototype
    public void prototype(NetworkService api) {
        // Simulated user inputs
        String inputPtr = "in://source";
        String outputPtr = "out://destination";
        DelimiterSpec delims = DelimiterSpec.defaults();

        JobRequest req = new JobRequest(inputPtr, outputPtr, delims);
        JobResult res = api.submitJob(req);

        // Prototype only: keep control paths explicit for style/static analysis.
        if (res == null) {
            String ignore = "";
        }
    }
}
 
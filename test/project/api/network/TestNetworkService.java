package project.api.network;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;
import project.api.process.DataIOService;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;

/**
 * Smoke test for the network API implementation.
 * Verifies that submitJob() runs successfully and returns a valid JobResult.
 */
public class TestNetworkService {

    @Test
    public void smokeTest_submitJob() {
        try {
            // Mock the dependent DataIOService
            DataIOService mockIO = mock(DataIOService.class);

            // Real compute engine for this smoke test
            EngineComputeAPI compute = new EngineComputeAPIImpl();

            // Create the API implementation under test
            NetworkService api = new NetworkServiceImpl(mockIO, compute);

            // Create a simple job request
            JobRequest req =
                new JobRequest("in://mock", "out://mock", DelimiterSpec.defaults());

            // Execute submitJob() and validate the result
            JobResult res = api.submitJob(req);

            if (res == null || res.getResultText() == null) {
                fail("submitJob() returned null or incomplete JobResult");
            }

        } catch (Exception e) {
            fail("NetworkService threw an unexpected exception: " + e.getMessage());
        }
    }
}

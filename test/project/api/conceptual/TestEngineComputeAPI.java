package project.api.conceptual;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 * Basic smoke test for the conceptual API implementation.
 * Verifies that compute() can be called and returns a non-null response.
 */
public class TestEngineComputeAPI {

    @Test
    public void smokeTest_computeRuns() {
        try {
            // Create the API implementation to test
            EngineComputeAPI api = new EngineComputeAPIImpl();

            // Create a simple input request
            EngineComputeAPI.ComputeRequest req = new EngineComputeAPI.ComputeRequest() {
                public int input() { return 5; }
                public String delimiter() { return ":"; }
            };

            // Call the compute method
            EngineComputeAPI.ComputeResponse res = api.compute(req);

            // Validate the response object
            if (res == null || res.asFormatted() == null) {
                fail("compute() returned null or incomplete response");
            }

        } catch (Exception e) {
            fail("compute() threw an unexpected exception: " + e.getMessage());
        }
    }
}

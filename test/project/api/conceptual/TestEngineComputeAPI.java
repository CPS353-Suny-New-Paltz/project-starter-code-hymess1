package project.api.conceptual;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

/**
 * Basic smoke test for the conceptual API implementation.
 * Verifies that compute() can be called and returns a non-null response.
 */
public class TestEngineComputeAPI {

    @Test
    public void smokeTest_computeRuns() {
        
            // Create the API implementation to test
            EngineComputeAPI api = new EngineComputeAPIImpl();

            // Create a simple input request
            EngineComputeAPI.ComputeRequest req = new EngineComputeAPI.ComputeRequest() {
                @Override
                public int input() {
                    return 5;
                }

                @Override
                public String delimiter() {
                    return ":";
                }
            };

            // Call the compute method
            EngineComputeAPI.ComputeResponse res = api.compute(req);

            Assertions.assertNotNull(res, "compute() returned null response");
            
            String formatted = res.asFormatted();
            Assertions.assertNotNull(formatted, "asFormatted( returned null:");
            
            //Expected behavior
            Assertions.assertEquals("5:5", formatted);
       
    }
}

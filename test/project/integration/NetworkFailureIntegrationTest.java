package project.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import project.api.conceptual.EngineComputeAPIOptimizedImpl;
import project.api.network.NetworkService;
import project.api.network.NetworkServiceImpl;
import project.api.process.DataIOService;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;

public class NetworkFailureIntegrationTest {

    /**
     * Fake DataIOService that deliberately throws in read().
     */
    private static class ExplodingDataIO implements DataIOService {

        @Override
        public DataReadResponse read(DataReadRequest request) {
            throw new RuntimeException("boom");
        }

        @Override
        public DataWriteResponse write(DataWriteRequest request) {
            throw new RuntimeException("should not reach write()");
        }
    }

    @Test
    public void testExceptionInDataIOIsCaughtByNetworkLayer() {
        // --- Real compute engine ---
    	var engine = new EngineComputeAPIOptimizedImpl();

        // --- Failing DataIO ---
        var dataIO = new ExplodingDataIO();

        // --- Network layer under test ---
        NetworkService network = new NetworkServiceImpl(dataIO, engine);

        // --- Valid request ---
        JobRequest req = new JobRequest(
                "in://explode",
                "out://unused",
                DelimiterSpec.defaults()
        );

        // --- NetworkService must NOT throw ---
        JobResult result = network.submitJob(req);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Unexpected"));
    }
}

package project.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIOptimizedImpl;
import project.api.network.NetworkService;
import project.api.network.NetworkServiceImpl;
import project.api.network.MultithreadedNetworkService;
import project.api.process.DataIOService;
import project.memory.InMemoryInputConfig;
import project.memory.InMemoryOutputConfig;
import project.memory.InMemoryDataIOService;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;


public class ComputeEngineIntegrationTest {

    @Test
    public void testNetworkIntegration_defaultDelimiters() {

        // ----- 1. Configure in-memory data sources -----
        InMemoryInputConfig input = new InMemoryInputConfig(List.of(1, 10, 25));
        InMemoryOutputConfig output = new InMemoryOutputConfig();
        DataIOService dataIO = new InMemoryDataIOService(input, output);

        // ----- 2. Real implementations for integration -----
        EngineComputeAPI engine = new EngineComputeAPIOptimizedImpl();
        int maxThreads = 4; // same upper bound you use elsewhere
        NetworkService network =
                new MultithreadedNetworkService(dataIO, engine, maxThreads);

        // ----- 3. Build JobRequest (single API entry point) -----
        JobRequest req = new JobRequest(
                "in://memory",
                "out://memory",
                DelimiterSpec.defaults()
        );

        // ----- 4. Call ONLY the Network API -----
        JobResult result = network.submitJob(req);

        // ----- 5. Assertions: Network + Compute + DataIO worked together -----
        assertNotNull(result);
        assertTrue(result.isSuccess());

        // Expected formatted compute results
        String expectedCombined = "1:-1;10:5;25:5";
        assertEquals(expectedCombined, result.getResultText());

        // Also check that DataIOService recorded each output correctly
        List<String> expectedIndividual = List.of("1:-1", "10:5", "25:5");
        assertEquals(expectedIndividual, output.getOutputValues());
    }
}

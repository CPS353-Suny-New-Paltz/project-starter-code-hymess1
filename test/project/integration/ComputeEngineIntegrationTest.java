package project.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import project.api.conceptual.EngineComputeAPI;
import project.api.process.DataIOService;
import project.memory.InMemoryInputConfig;
import project.memory.InMemoryOutputConfig;
import project.memory.InMemoryDataIOService;
import project.model.DelimiterSpec;

/**
 * Integration test connecting EngineComputeAPI and DataIOService
 */
public class ComputeEngineIntegrationTest {

    @Test
    public void testComputeEngineIntegration_noDelimiterSpecified() {

        
        // Test input [1, 10, 25]
        InMemoryInputConfig inputConfig = new InMemoryInputConfig(List.of(1, 10, 25));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();
        DataIOService dataStore = new InMemoryDataIOService(inputConfig, outputConfig);

        // Empty (prototype) compute engine implementation
        EngineComputeAPI engine = new EngineComputeAPI() {
            @Override
            public ComputeResponse compute(ComputeRequest request) {
                // Prototype placeholder – no computation yet
                return () -> "";
            }
        };

        
        // Simulate reading, computing, and writing through the in-memory store
        DataIOService.DataPointer sourcePtr = () -> "in://memory";
        DataIOService.DataReadRequest readReq = () -> sourcePtr;
        DataIOService.DataReadResponse readRes = dataStore.read(readReq);

        // Compute using the conceptual API 
        EngineComputeAPI.ComputeRequest computeReq = new EngineComputeAPI.ComputeRequest() {
            @Override
            public int input() { return 0; } // placeholder
            @Override
            public String delimiter() { return DelimiterSpec.defaults().getKeyValueDelimiter(); }
        };
        EngineComputeAPI.ComputeResponse computeRes = engine.compute(computeReq);

        // Write result (whatever engine computed) to the output store
        DataIOService.DataPointer destPtr = () -> "out://memory";
        DataIOService.DataWriteRequest writeReq = new DataIOService.DataWriteRequest() {
            @Override
            public DataIOService.DataPointer destination() { return destPtr; }
            @Override
            public String payload() { return computeRes.asFormatted(); }
        };
        DataIOService.DataWriteResponse writeRes = dataStore.write(writeReq);

        
        // represents what the engine *should eventually compute*.
        // It will fail for now (expected, since engine.compute() is not implemented).
        List<String> expectedOutput = List.of("1:1", "10:100", "25:625");
        assertEquals(expectedOutput, outputConfig.getOutputValues(),
            "Output should match the expected compute results (will fail until engine is implemented)");

        // check that the write operation reported success
        assertTrue(writeRes.code().success(), "Write should complete successfully");
    }
}

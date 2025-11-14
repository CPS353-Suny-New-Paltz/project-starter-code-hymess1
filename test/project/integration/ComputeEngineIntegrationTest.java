package project.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

import project.api.conceptual.EngineComputeAPI;
import project.api.process.DataIOService;
import project.memory.InMemoryInputConfig;
import project.memory.InMemoryOutputConfig;
import project.memory.InMemoryDataIOService;

/**
 * Integration test for the conceptual engine and data store.
 * This is EXPECTED TO FAIL for Checkpoint 3 (engine not implemented yet).
 */
public class ComputeEngineIntegrationTest {

    @Test
    public void testComputeEngineIntegration_noDelimiterSpecified() {

        // Input: [1, 10, 25]
        InMemoryInputConfig inputConfig = new InMemoryInputConfig(List.of(1, 10, 25));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();
        DataIOService dataStore = new InMemoryDataIOService(inputConfig, outputConfig);

        // Empty conceptual engine implementation
        EngineComputeAPI engine = new EngineComputeAPI() {
            @Override
            public ComputeResponse compute(ComputeRequest request) {
                // placeholder
                return () -> "";
            }
        };

        // Read the inputs from the in-memory store
        DataIOService.DataReadRequest readReq = () -> () -> "in://memory";
        DataIOService.DataReadResponse readRes = dataStore.read(readReq);

        // The test input values
        List<Integer> inputs = inputConfig.getInputValues();

        // Compute once per input
        for (int value : inputs) {

            EngineComputeAPI.ComputeRequest computeReq = new EngineComputeAPI.ComputeRequest() {

                @Override
                public int input() {
                    return value;
                }

                @Override
                public String delimiter() {
                    return null;  // NO delimiter specified
                }
            };

            EngineComputeAPI.ComputeResponse computeRes = engine.compute(computeReq);

            // Write to output store
            DataIOService.DataWriteRequest writeReq = new DataIOService.DataWriteRequest() {

                @Override
                public DataIOService.DataPointer destination() {
                    return () -> "out://memory";
                }

                @Override
                public String payload() {
                    return computeRes.asFormatted();
                }
            };

            DataIOService.DataWriteResponse writeRes = dataStore.write(writeReq);
            assertTrue(writeRes.code().success(), "Write should succeed");
        }

        // Future expected output (will fail now — correct)
        List<String> expectedOutput = List.of("1:1", "10:100", "25:625");

        assertEquals(expectedOutput, outputConfig.getOutputValues(),
                "Output should match what the engine will eventually compute");
    }
}

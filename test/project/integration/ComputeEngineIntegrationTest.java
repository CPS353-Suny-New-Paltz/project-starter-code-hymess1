package project.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;
import project.api.process.DataIOService;
import project.memory.InMemoryDataIOService;
import project.memory.InMemoryInputConfig;
import project.memory.InMemoryOutputConfig;
import project.model.DelimiterSpec;

public class ComputeEngineIntegrationTest {

    @Test
    public void testComputeEngineIntegration_noDelimiterSpecified() {

        // Input [1, 10, 25]
        InMemoryInputConfig inputConfig = new InMemoryInputConfig(List.of(1, 10, 25));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();
        DataIOService dataStore = new InMemoryDataIOService(inputConfig, outputConfig);

        // Use your REAL implementation (required for checkpoint 3)
        EngineComputeAPI engine = new EngineComputeAPIImpl();

        // Read the input list
        DataIOService.DataReadRequest readReq = () -> 
            (DataIOService.DataPointer) () -> "in://memory";

        DataIOService.DataReadResponse readRes = dataStore.read(readReq);

        // Parse payload → "1,10,25" or similar depending on your test store
        String payload = readRes.payload();
        String[] tokens = payload.split(",");

        List<String> resultsToWrite = new ArrayList<>();
        String delim = DelimiterSpec.defaults().getKeyValueDelimiter();

        // LOOP: compute each value and collect formatted results
        for (String t : tokens) {
            int n = Integer.parseInt(t.trim());
            String formatted = engine.computeSingle(n, delim).asFormatted();
            resultsToWrite.add(formatted);
        }

        // Write results to output
        DataIOService.DataWriteRequest writeReq = new DataIOService.DataWriteRequest() {
            @Override
            public DataIOService.DataPointer destination() {
                return () -> "out://memory";
            }

            @Override
            public String payload() {
                // our test datastore writes each line separately
                return String.join("\n", resultsToWrite);
            }
        };

        DataIOService.DataWriteResponse writeRes = dataStore.write(writeReq);

        // Expected FINAL behavior (this test FAILS until CP4 compute is implemented)
        List<String> expectedOutput = List.of("1:1", "10:100", "25:625");

        assertEquals(expectedOutput, outputConfig.getOutputValues(),
                "Output should match the expected compute results (will fail until engine is implemented)");

        assertTrue(writeRes.code().success(), "Write should complete successfully");
    }
}

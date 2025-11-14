package project.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;
import project.api.network.NetworkService;
import project.api.network.NetworkServiceImpl;
import project.api.process.DataIOService;
import project.memory.InMemoryInputConfig;
import project.memory.InMemoryOutputConfig;
import project.memory.InMemoryDataIOService;

public class ComputeEngineIntegrationTest {

    @Test
    public void testComputeEngineIntegration_noDelimiterSpecified() {

        InMemoryInputConfig inputConfig = new InMemoryInputConfig(List.of(1, 10, 25));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();
        DataIOService dataStore = new InMemoryDataIOService(inputConfig, outputConfig);

        EngineComputeAPI engine = new EngineComputeAPIImpl();

        NetworkService network = new NetworkServiceImpl(dataStore);

        // Read inputs
        DataIOService.DataReadRequest readReq = () -> () -> "in://memory";
        DataIOService.DataReadResponse readRes = dataStore.read(readReq);

        List<Integer> inputs = inputConfig.getInputValues();

        for (int value : inputs) {

            EngineComputeAPI.ComputeRequest computeReq = new EngineComputeAPI.ComputeRequest() {

                @Override
                public int input() {
                    return value;
                }

                @Override
                public String delimiter() {
                    return null;
                }
            };

            EngineComputeAPI.ComputeResponse computeRes = engine.compute(computeReq);

            DataIOService.DataWriteRequest writeReq = new DataIOService.DataWriteRequest() {
                @Override
                public DataPointer destination() {
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

        List<String> expectedOutput = List.of("1:1", "10:100", "25:625");
        assertEquals(expectedOutput, outputConfig.getOutputValues(),
                "Output should match what the engine will eventually compute");
    }
}

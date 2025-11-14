package project.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;
import project.api.network.NetworkService;
import project.api.network.NetworkServiceImpl;
import project.api.process.DataIOService;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;
import project.memory.InMemoryInputConfig;
import project.memory.InMemoryOutputConfig;
import project.memory.InMemoryDataIOService;

public class ComputeEngineIntegrationTest {

    @Test
    public void testComputeEngineIntegration_noDelimiterSpecified() {

        // Test-only data configurations
        InMemoryInputConfig inputConfig =
                new InMemoryInputConfig(List.of(1, 10, 25));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();

        // Required by Checkpoint 3: test-only DataIOService
        DataIOService dataStore =
                new InMemoryDataIOService(inputConfig, outputConfig);

        // Required by Checkpoint 3: real conceptual implementation
        EngineComputeAPI engine = new EngineComputeAPIImpl();

        // Required by Checkpoint 3: real network implementation
        NetworkService network = new NetworkServiceImpl(dataStore);

        // Read from the in-memory data store
        DataPointer srcPtr = new DataPointer() {
            @Override
            public String asString() {
                return "in://memory";
            }
        };

        DataReadRequest readRequest = new DataReadRequest() {
            @Override
            public DataPointer source() {
                return srcPtr;
            }
        };

        dataStore.read(readRequest);

        List<Integer> inputs = inputConfig.getInputValues();

        for (int value : inputs) {

            EngineComputeAPI.ComputeRequest computeReq =
                    new EngineComputeAPI.ComputeRequest() {
                        @Override
                        public int input() {
                            return value;
                        }

                        @Override
                        public String delimiter() {
                            return null; // no delimiter specified
                        }
                    };

            String formatted = engine.compute(computeReq).asFormatted();

            DataPointer dstPtr = new DataPointer() {
                @Override
                public String asString() {
                    return "out://memory";
                }
            };

            DataWriteRequest writeRequest =
                    new DataWriteRequest() {
                        @Override
                        public DataPointer destination() {
                            return dstPtr;
                        }

                        @Override
                        public String payload() {
                            return formatted;
                        }
                    };

            DataWriteResponse writeRes = dataStore.write(writeRequest);
            assertTrue(writeRes.code().success(), "Write should succeed");
        }

        List<String> expected =
                List.of("1:1", "10:100", "25:625");

        assertEquals(expected, outputConfig.getOutputValues(),
                "Output should match eventual compute results");
    }
}

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

        InMemoryInputConfig inputConfig =
                new InMemoryInputConfig(List.of(1, 10, 25));
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();
        DataIOService dataStore =
                new InMemoryDataIOService(inputConfig, outputConfig);

        // Required real implementations for Checkpoint 3
        EngineComputeAPI engine = new EngineComputeAPIImpl();
        NetworkService network = new NetworkServiceImpl(dataStore);

        // Read
        DataPointer srcPtr = new DataPointer() {
            @Override
            public String asString() {
                return "in://memory";
            }
        };

        DataReadRequest readReq = new DataReadRequest() {
            @Override
            public DataPointer source() {
                return srcPtr;
            }
        };

        dataStore.read(readReq);

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
                            return null;
                        }
                    };

            String formatted = engine.compute(computeReq).asFormatted();

            DataPointer dstPtr = new DataPointer() {
                @Override
                public String asString() {
                    return "out://memory";
                }
            };

            DataWriteRequest writeReq =
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

            DataWriteResponse writeRes = dataStore.write(writeReq);
            assertTrue(writeRes.code().success());
        }

        List<String> expected =
                List.of("1:1", "10:100", "25:625");

        assertEquals(expected, outputConfig.getOutputValues());
    }
}

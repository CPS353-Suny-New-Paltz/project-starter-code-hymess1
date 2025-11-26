package project.memory;

import java.util.List;
import project.api.process.DataIOService;

/**
 * Test-only in-memory implementation of DataIOService.
 * Reads integers from an InMemoryInputConfig and writes formatted strings
 * to an InMemoryOutputConfig.
 */
public class InMemoryDataIOService implements DataIOService {

    private final InMemoryInputConfig inputConfig;
    private final InMemoryOutputConfig outputConfig;

    public InMemoryDataIOService(InMemoryInputConfig inputConfig, InMemoryOutputConfig outputConfig) {
        this.inputConfig = inputConfig;
        this.outputConfig = outputConfig;
    }

    @Override
    public DataReadResponse read(DataReadRequest request) {
        // Return the input values directly as a List<Integer>
        List<Integer> inputs = inputConfig.getInputValues();

        return new DataReadResponse() {
            @Override
            public List<Integer> payload() {
                return inputs; // already a List<Integer>
            }
        };
    }

    @Override
    public DataWriteResponse write(DataWriteRequest request) {
        // Append the payload string to the output config list
        outputConfig.write(request.payload());

        return new DataWriteResponse() {
            @Override
            public StatusCode code() {
                return StatusCode.SUCCESS;
            }

            @Override
            public String message() {
                return "Wrote payload to in-memory output list";
            }
        };
    }
}

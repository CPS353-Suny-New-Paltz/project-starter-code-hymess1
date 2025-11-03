package project.api.process;

/**
 * Empty implementation for Checkpoint 3. Returns benign placeholders; no real
 * I/O yet.
 */
public class DataIOServiceImpl implements DataIOService {

    @Override
    public DataPointer readInputPointer() {
        return new DataPointer() {
            @Override
            public String asString() {
                return "in://placeholder";
            }
        };
    }

    @Override
    public DataWriteResponse write(DataWriteRequest request) {
        return new DataWriteResponse() {
            @Override
            public StatusCode code() {
                return StatusCode.SUCCESS;
            }

            @Override
            public String message() {
                return "";
            }
        };
    }
}

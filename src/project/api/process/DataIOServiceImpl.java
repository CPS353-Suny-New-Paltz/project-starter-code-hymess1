package project.api.process;

/**
 * Empty implementation for Checkpoint 3.
 * Performs no real I/O; returns default responses.
 */
public class DataIOServiceImpl implements DataIOService {

    @Override
    public DataReadResponse read(DataReadRequest request) {
        return () -> ""; // empty payload
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
                return "TBD";
            }
        };
    }
}

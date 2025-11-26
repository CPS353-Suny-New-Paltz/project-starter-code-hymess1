package project.api.process;
import java.util.List;
import project.annotations.ProcessAPI;

/**
 * Process boundary API: data storage system ↔ compute engine.
 * Abstracts IO details behind stable wrapper types.
 */
@ProcessAPI
public interface DataIOService {

    /**
     * Reads data from a client-specified source.
     * Never returns null.
     */
    DataReadResponse read(DataReadRequest request);

    /**
     * Writes result data to a destination pointer.
     * Never returns null; failures are encoded in the response.
     */
    DataWriteResponse write(DataWriteRequest request);

    // -------- Wrapper types (nested for prototype phase) --------

    /**
     * Opaque pointer to a data location (file path, URI, DB key, etc.).
     */
    interface DataPointer {
        String asString();
    }

    /**
     * Request to read data from a source.
     */
    interface DataReadRequest {
        DataPointer source();
    }

    /**
     * Response from a read operation.
     */
    interface DataReadResponse {
    	List<Integer> payload(); // the retrieved data
    }

    /**
     * Request to write data to a destination.
     */
    interface DataWriteRequest {
        DataPointer destination();
        String payload();
    }

    /**
     * Response from a write operation.
     */
    interface DataWriteResponse {
        StatusCode code();
        String message(); // optional details; may be empty but never null

        enum StatusCode {
            SUCCESS,
            FAILURE;

            public boolean success() {
                return this == SUCCESS;
            }
        }
    }
}


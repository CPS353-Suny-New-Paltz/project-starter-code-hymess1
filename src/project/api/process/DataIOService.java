package project.api.process;

import project.annotations.ProcessAPI;

/**
 * Process boundary API: data storage system ↔ compute engine.
 * Abstracts IO details behind stable wrapper types.
 */
@ProcessAPI
public interface DataIOService {

    /**
     * Returns a pointer to the configured input source.
     * Never returns null.
     */
    DataPointer readInputPointer();

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

            public boolean success() { return this == SUCCESS; }
        }
    }
}

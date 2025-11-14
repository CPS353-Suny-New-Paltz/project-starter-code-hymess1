package project.api.process;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;

/**
 * Smoke test for the process API implementation.
 */
public class TestDataIOService {

    @Test
    public void smokeTest_readAndWrite() {
        try {
            // Create the API implementation to test
            DataIOService api = new DataIOServiceImpl();

            // Mock a data pointer for reading/writing
            DataPointer mockPtr = mock(DataPointer.class);
            when(mockPtr.asString()).thenReturn("mock://data");

            // Build a simple read request
            DataReadRequest readReq = new DataReadRequest() {
                @Override
                public DataPointer source() {
                    return mockPtr;
                }
            };

            // Execute read() and check response
            DataReadResponse readRes = api.read(readReq);
            if (readRes == null) {
                fail("read() returned null");
            }

            // Build a simple write request using the same pointer
            DataWriteRequest writeReq = new DataWriteRequest() {
                @Override
                public DataPointer destination() {
                    return mockPtr;
                }

                @Override
                public String payload() {
                    return "payload";
                }
            };

            // Execute write() and check response
            DataWriteResponse writeRes = api.write(writeReq);
            if (writeRes == null || writeRes.message() == null) {
                fail("write() returned null or invalid message");
            }

        } catch (Exception e) {
            fail("DataIOService threw an unexpected exception: " + e.getMessage());
        }
    }
}

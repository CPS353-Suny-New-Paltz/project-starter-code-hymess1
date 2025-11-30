package project.api.process;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;
/**
 * Smoke test for the process API implementation.
 */
public class TestDataIOService {
	// Test that read returns an empty list for missing file 
    @Test
    public void testRead() {
        
            // Create the API implementation to test
            DataIOService api = new DataIOServiceImpl();

            // Mock a data pointer for reading/writing
            DataPointer ptr = Mockito.mock(DataPointer.class);
            Mockito.when(ptr.asString()).thenReturn("fake_file_12345.tmp");

            // Build a read request/response
            DataReadRequest req = () -> ptr;


           
            DataReadResponse res = api.read(req);

            Assertions.assertNotNull(res, "read() should never return null");
            Assertions.assertNotNull(res.payload(), "payload() should never be null");

            // CP5 meaningful requirement:
            Assertions.assertEquals(0, res.payload().size(),
                    "Expected empty list when file is missing");

        
    }
    @Test
    public void testWrite_successfulWrite() {
        DataIOService api = new DataIOServiceImpl();

        DataPointer ptr = Mockito.mock(DataPointer.class);
        Mockito.when(ptr.asString()).thenReturn("temp_output.tmp");

        DataWriteRequest writeReq = new DataWriteRequest() {
            @Override
            public DataPointer destination() {
                return ptr;
            }

            @Override
            public String payload() {
                return "42";
            }
        };

        DataWriteResponse res = api.write(writeReq);

        Assertions.assertNotNull(res);
        Assertions.assertNotNull(res.message());
        Assertions.assertTrue(res.code().success(),
                "Expected SUCCESS status when writing to a valid path");
    }

}

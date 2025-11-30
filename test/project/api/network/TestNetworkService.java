package project.api.network;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;
import project.api.process.DataIOService;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;

/**
 * Smoke test for the network API implementation.
 * Verifies that submitJob() runs successfully and returns a valid JobResult.
 */
public class TestNetworkService {

    @Test
    public void smokeTest_submitJob() {
        
            // Mock the dependent DataIOService
            DataIOService mockIO = Mockito.mock(DataIOService.class);
            
            //Mock read() return 5
            DataReadResponse goodRead = Mockito.mock(DataReadResponse.class);
            Mockito.when(goodRead.payload()).thenReturn(List.of(5));
            Mockito.when(mockIO.read(Mockito.any(DataReadRequest.class))).thenReturn(goodRead);
            
            // Pretend every write succeeds.
            DataWriteResponse goodWrite = Mockito.mock(DataWriteResponse.class);
            Mockito.when(goodWrite.code()).thenReturn(DataWriteResponse.StatusCode.SUCCESS);
            Mockito.when(goodWrite.message()).thenReturn("ok");
            Mockito.when(mockIO.write(Mockito.any(DataWriteRequest.class))).thenReturn(goodWrite);
            
            // Real compute engine for this smoke test
            EngineComputeAPI compute = new EngineComputeAPIImpl();

            // Create the API implementation under test
            NetworkService api = new NetworkServiceImpl(mockIO, compute);

            // Create a simple job request
            JobRequest request =
                new JobRequest("in://mock", "out://mock", DelimiterSpec.defaults());

            // Execute submitJob() and validate the result
            JobResult result = api.submitJob(request);
            
            // Make sure the job actually succeeded.
            Assertions.assertTrue(result.isSuccess(), "Expected job to succeed");

            // make sure the output matches what the real compute engine produces.
            Assertions.assertEquals("5:5", result.getResultText());
        }

            

        
    }


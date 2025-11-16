package project.api.network;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;
import project.api.process.DataIOService;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;

public class TestNetworkServiceInvalidRead {

    @Test
    public void testSubmitJob_whenReadReturnsNullPayload() {
        // Mock the data store
        DataIOService mockIO = Mockito.mock(DataIOService.class);

        // Return a DataReadResponse whose payload() is null
        DataReadResponse badResponse = Mockito.mock(DataReadResponse.class);
        Mockito.when(badResponse.payload()).thenReturn(null);

        Mockito.when(mockIO.read(Mockito.any(DataReadRequest.class)))
               .thenReturn(badResponse);

        // Real compute engine (not used here)
        EngineComputeAPI compute = new EngineComputeAPIImpl();

        NetworkService api = new NetworkServiceImpl(mockIO, compute);

        JobRequest req =
            new JobRequest("in://mock", "out://mock", DelimiterSpec.defaults());

        JobResult result = api.submitJob(req);

        // Assertions without static imports
        org.junit.jupiter.api.Assertions.assertFalse(
                result.isSuccess(),
                "Job should fail when payload is null"
        );

        org.junit.jupiter.api.Assertions.assertTrue(
                result.getErrorMessage().contains("Failed to read"),
                "Error message should mention read failure"
        );
    }
}

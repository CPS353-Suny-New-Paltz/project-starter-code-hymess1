package project.api.network;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import project.api.process.DataIOService;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;
import project.model.DelimiterSpec;
import project.model.JobRequest;

public class TestNetworkServiceInvalidRead {

    @Test
    public void testSubmitJob_whenReadReturnsNullPayload() {
        // Mock the data store
        DataIOService mockIO = Mockito.mock(DataIOService.class);

        // Make read() return a response whose payload() is null
        DataReadResponse badResponse = Mockito.mock(DataReadResponse.class);
        Mockito.when(badResponse.payload()).thenReturn(null);
        Mockito.when(mockIO.read(Mockito.any(DataReadRequest.class)))
               .thenReturn(badResponse);

        // Real compute engine (doesn't matter for this test)
        EngineComputeAPI compute = new EngineComputeAPIImpl();

        NetworkService api = new NetworkServiceImpl(mockIO, compute);

        JobRequest req = new JobRequest(
                "in://mock",
                "out://mock",
                DelimiterSpec.defaults()
        );

        var result = api.submitJob(req);

        assertFalse(result.isSuccess(), "Job should fail when payload is null");
        assertTrue(result.getErrorMessage().contains("Failed to read"),
                   "Error message should indicate read failure");
    }
}

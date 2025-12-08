package project.checkpointtests;

import java.io.File;

import project.api.network.NetworkService;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;

public class TestUser {
	
	// TODO 3: change the type of this variable to the name you're using for your
	// @NetworkAPI interface; also update the parameter passed to the constructor
    private final NetworkService coordinator;

	public TestUser(NetworkService coordinator) {
		this.coordinator = coordinator;
	}

	public void run(String outputPath) {
		char delimiter = ';';
		String inputPath = "test" + File.separatorChar + "testInputFile.test";
		
		// TODO 4: Call the appropriate method(s) on the coordinator to get it to 
		// run the compute job specified by inputPath, outputPath, and delimiter
        // Use ';' as the pair delimiter and ':' as the key/value delimiter.
        DelimiterSpec delims =
                new DelimiterSpec(false, String.valueOf(delimiter), ":");
        JobRequest request = new JobRequest(inputPath, outputPath, delims);
        JobResult result = coordinator.submitJob(request);
        
        // Fail if something went wrong
        if (result == null || !result.isSuccess()) {
            String msg = (result == null)
                    ? "JobResult was null"
                    : result.getErrorMessage();
            throw new IllegalStateException("Job failed: " + msg);
        }
        
	}

}

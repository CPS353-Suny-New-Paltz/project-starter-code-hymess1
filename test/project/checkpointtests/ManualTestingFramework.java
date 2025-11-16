package project.checkpointtests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;
import project.api.network.NetworkService;
import project.api.network.NetworkServiceImpl;
import project.api.process.DataIOService;
import project.api.process.DataIOServiceImpl;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;

public class ManualTestingFramework {
    
    public static final String INPUT = "manualTestInput.txt";
    public static final String OUTPUT = "manualTestOutput.txt";

    public static void main(String[] args) {
        // TODO 1:
        // Instantiate a real (ie, class definition lives in the src/ folder) implementation 
        // of all 3 APIs
        
    	 DataIOService dataIO = new DataIOServiceImpl();
         EngineComputeAPI engine = new EngineComputeAPIImpl();
         NetworkService network = new NetworkServiceImpl(dataIO, engine);
      // make sure the output file starts empty for each run 
         Path outputPath = Paths.get(OUTPUT);
         try {
             Files.deleteIfExists(outputPath);
         } catch (IOException e) {
             // Manual harness: ignore delete failures, just try to continue
         }
        // TODO 2:
        // Run a computation with an input file of <root project dir>/manualTestInput.txt
        // and an output of <root project dir>/manualTestOutput.txt, with a delimiter of ',' 
         DelimiterSpec delims = new DelimiterSpec(false, ",", ":");

         JobRequest job = new JobRequest(INPUT, OUTPUT, delims);

         JobResult result = network.submitJob(job);
         
         if (!result.isSuccess()) {
             String ignore = result.getErrorMessage();
         }
        // Helpful hint: the working directory of this program is <root project dir>,
        // so you can refer to the files just using the INPUT/OUTPUT constants. You do not 
        // need to (and should not) actually create those files in your repo
    }
}

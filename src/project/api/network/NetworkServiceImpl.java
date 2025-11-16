package project.api.network;

import project.api.conceptual.EngineComputeAPI;
import project.api.process.DataIOService;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataReadResponse;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;
import project.model.JobRequest;
import project.model.JobResult;

import java.util.ArrayList;
import java.util.List;


public class NetworkServiceImpl implements NetworkService {

    private final DataIOService dataIO;
    private final EngineComputeAPI computeEngine;

    /**
     * The network layer depends on the data storage component and
     * the compute engine. keep references to both.
     */
    public NetworkServiceImpl(DataIOService dataIO, EngineComputeAPI computeEngine) {
        this.dataIO = dataIO;
        this.computeEngine = computeEngine;
    }

    @Override
    public JobResult submitJob(JobRequest request) {
        if (request == null) {
            return new JobResult(false, "", "No job request was provided.");
        }

        /*
         * DataPointer is an interface (one abstract method: asString()).
         * Writing `() -> request.getInputSourcePointer()` is the short form of:
         *
         *   new DataPointer() {
         *       @Override
         *       public String asString() {
         *           return request.getInputSourcePointer();
         *       }
         *   };
         *
         *  using the lambda because DataPointer is tiny and only needs to supply
         * the string for where the data lives.
         */
        DataPointer inputPtr = () -> request.getInputSourcePointer();

        DataReadRequest readReq = () -> inputPtr;
        DataReadResponse readRes = dataIO.read(readReq);

        if (readRes == null || readRes.payload() == null) {
            return new JobResult(false, "", "Failed to read input.");
        }

        String payload = readRes.payload().trim();
        if (payload.isEmpty()) {
            return new JobResult(false, "", "No input values found.");
        }

        // Parse the comma-separated list of integers from the storage layer
        List<Integer> inputs = new ArrayList<>();
        try {
            for (String piece : payload.split(",")) {
                inputs.add(Integer.parseInt(piece.trim()));
            }
        } catch (NumberFormatException e) {
            return new JobResult(false, "", "Input contained non-integer values.");
        }

        // Another DataPointer, this time for where results get written.
        //really just a shorter way of writing:

        	//DataPointer outputPtr = new DataPointer() {
        	  //  @Override
        	  //  public String asString() {
        	  //      return request.getOutputDestinationPointer();
        	   // }
        //	};
        DataPointer outputPtr = () -> request.getOutputDestinationPointer();

        List<String> writtenResults = new ArrayList<>();

        // Run the compute engine for each integer and push the results out
        for (int value : inputs) {

            /*
             * ComputeRequest is an interface: it just needs
             * to provide input() and delimiter().
             */
            var computeReq = new EngineComputeAPI.ComputeRequest() {
                @Override
                public int input() {
                    return value;
                }

                @Override
                public String delimiter() {
                    return request.getDelimiterSpec().getKeyValueDelimiter();
                }
            };

            String formatted = computeEngine.compute(computeReq).asFormatted();

           
            DataWriteRequest writeReq = new DataWriteRequest() {
                @Override
                public DataPointer destination() {
                    return outputPtr;
                }

                @Override
                public String payload() {
                    return formatted;
                }
            };

            DataWriteResponse writeRes = dataIO.write(writeReq);
            if (writeRes == null || !writeRes.code().success()) {
                return new JobResult(false, "", "Failed to write output.");
            }

            writtenResults.add(formatted);
        }

        // Join all the results using whatever pair-delimiter the user requested
        String combined = String.join(
                request.getDelimiterSpec().getPairDelimiter(),
                writtenResults
        );

        return new JobResult(true, combined, "");
    }
}

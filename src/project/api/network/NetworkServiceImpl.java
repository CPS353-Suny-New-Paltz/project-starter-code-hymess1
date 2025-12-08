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

    protected final DataIOService dataIO;
    protected final EngineComputeAPI computeEngine;

    /**
     * The network layer depends on the data storage component and
     * the compute engine. Keep references to both.
     */
    public NetworkServiceImpl(
        DataIOService dataIO,
        EngineComputeAPI computeEngine
    ) {
        this.dataIO = dataIO;
        this.computeEngine = computeEngine;
    }

    @Override
    public JobResult submitJob(JobRequest request) {
        try {

            // ---------- VALIDATION ----------
            if (request == null) {
                return new JobResult(
                    false,
                    "",
                    "No job request was provided."
                );
            }

            // Validate input pointer
            String inPtrString = request.getInputSourcePointer();
            if (inPtrString == null || inPtrString.isBlank()) {
                return new JobResult(
                    false,
                    "",
                    "Invalid input pointer."
                );
            }

            // Validate output pointer
            String outPtrString = request.getOutputDestinationPointer();
            if (outPtrString == null || outPtrString.isBlank()) {
                return new JobResult(
                    false,
                    "",
                    "Invalid output pointer."
                );
            }

            // Validate delimiter spec
            if (request.getDelimiterSpec() == null
                || request.getDelimiterSpec().getKeyValueDelimiter() == null
                || request.getDelimiterSpec()
                    .getKeyValueDelimiter()
                    .isBlank()
                || request.getDelimiterSpec().getPairDelimiter() == null
                || request.getDelimiterSpec()
                    .getPairDelimiter()
                    .isBlank()) {

                return new JobResult(
                    false,
                    "",
                    "Invalid delimiter specification."
                );
            }

            // ---------- READ INPUT ----------
            DataPointer inputPtr = () -> inPtrString;
            DataReadRequest readReq = () -> inputPtr;

            DataReadResponse readRes = dataIO.read(readReq);
            if (readRes == null || readRes.payload() == null) {
                return new JobResult(
                    false,
                    "",
                    "Failed to read input."
                );
            }

            List<Integer> inputs = readRes.payload();
            if (inputs.isEmpty()) {
                return new JobResult(
                    false,
                    "",
                    "No input values found."
                );
            }

            // ---------- PREPARE OUTPUT POINTER ----------
            DataPointer outputPtr = () -> outPtrString;

            // ---------- DELEGATE TO HOOK ----------
            return processInputs(request, inputs, outputPtr);

        } catch (Exception e) {
            return new JobResult(
                false,
                "",
                "Unexpected error: " + e.getMessage()
            );
        }
    }
    /**
     * Hook for processing all inputs in a job.
     * Default implementation is single-threaded.
     * Subclasses (like multi-threaded) can override this.
     */
    protected JobResult processInputs(
        JobRequest request,
        List<Integer> inputs,
        DataPointer outputPtr
    ) {
        List<String> writtenResults = new ArrayList<>();

        for (int value : inputs) {

            // Validate each value
            if (value < 0) {
                return new JobResult(
                    false,
                    "",
                    "Negative input encountered."
                );
            }

            // Build conceptual compute request
            EngineComputeAPI.ComputeRequest computeReq =
                new EngineComputeAPI.ComputeRequest() {
                    @Override
                    public int input() {
                        return value;
                    }

                    @Override
                    public String delimiter() {
                        return request
                            .getDelimiterSpec()
                            .getKeyValueDelimiter();
                    }
                };

            EngineComputeAPI.ComputeResponse computeRes =
                computeEngine.compute(computeReq);

            // Notice errors from EngineComputeAPI
            if (computeRes == null || !computeRes.success()) {
                String msg;

                if (computeRes == null) {
                    msg = "Compute engine returned null response.";
                } else {
                    switch (computeRes.status()) {
                        case INVALID_INPUT:
                            msg = "Invalid input encountered in compute "
                                + "engine for value " + value;
                            break;
                        case INTERNAL_ERROR:
                            msg = "Internal compute error for value "
                                + value;
                            break;
                        case SUCCESS:
                        default:
                            msg = "Unexpected compute status.";
                            break;
                    }
                }

                return new JobResult(false, "", msg);
            }

            String formatted = computeRes.asFormatted();

            // Write result out
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
                return new JobResult(
                    false,
                    "",
                    "Failed to write output."
                );
            }

            writtenResults.add(formatted);
        }

        // ---------- COMBINE RESULTS ----------
        String combined = String.join(
            request.getDelimiterSpec().getPairDelimiter(),
            writtenResults
        );

        return new JobResult(true, combined, "");
    }

}

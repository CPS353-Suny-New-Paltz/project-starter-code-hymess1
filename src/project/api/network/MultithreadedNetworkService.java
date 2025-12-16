package project.api.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import project.api.conceptual.EngineComputeAPI;
import project.api.process.DataIOService;
import project.api.process.DataIOService.DataPointer;
import project.api.process.DataIOService.DataWriteRequest;
import project.api.process.DataIOService.DataWriteResponse;
import project.model.JobRequest;
import project.model.JobResult;

/**
 * Multi-threaded implementation of the NetworkService.
 *
 * Uses a fixed thread pool to run conceptual compute calls in parallel.
 * All I/O (DataIOService.read/write) is still done on a single thread.
 */
public class MultithreadedNetworkService extends NetworkServiceImpl implements AutoCloseable {

    private final ExecutorService executor;

    /**
     * @param dataIO      shared DataIOService implementation
     * @param computeEngine shared EngineComputeAPI implementation
     * @param maxThreads  upper bound on the number of worker threads
     */
    public MultithreadedNetworkService(
        DataIOService dataIO,
        EngineComputeAPI computeEngine,
        int maxThreads
    ) {
        super(dataIO, computeEngine);
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("maxThreads must be positive");
        }
        this.executor = Executors.newFixedThreadPool(maxThreads);
    }

    /**
     * Override the hook to run conceptual compute in parallel.
     *
     * Validation and input reading still happens in the parent submitJob().
     */
    @Override
    protected JobResult processInputs(
        JobRequest request,
        List<Integer> inputs,
        DataPointer outputPtr
    ) {
        try {
            // ---------- SUBMIT COMPUTE TASKS IN PARALLEL ----------
            List<Future<EngineComputeAPI.ComputeResponse>> futures =
                new ArrayList<>();

            for (int value : inputs) {

                if (value < 0) {
                    return new JobResult(
                        false,
                        "",
                        "Negative input encountered."
                    );
                }

                final int current = value;

                EngineComputeAPI.ComputeRequest computeReq =
                    new EngineComputeAPI.ComputeRequest() {
                        @Override
                        public int input() {
                            return current;
                        }

                        @Override
                        public String delimiter() {
                            return request
                                .getDelimiterSpec()
                                .getKeyValueDelimiter();
                        }
                    };

                futures.add(executor.submit(
                    () -> computeEngine.compute(computeReq)
                ));
            }

            // ---------- COLLECT RESULTS (IN ORDER) ----------
            List<String> formattedResults = new ArrayList<>();

            for (int i = 0; i < futures.size(); i++) {
                Future<EngineComputeAPI.ComputeResponse> future = futures.get(i);
                EngineComputeAPI.ComputeResponse computeRes;
                int value = inputs.get(i);

                try {
                    computeRes = future.get();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return new JobResult(
                        false,
                        "",
                        "Compute interrupted."
                    );
                } catch (ExecutionException ee) {
                    // Treat as an internal error for that value
                    return new JobResult(
                        false,
                        "",
                        "Internal compute error for value " + value
                    );
                }

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

                formattedResults.add(computeRes.asFormatted());
            }

            // ---------- WRITE OUTPUTS SEQUENTIALLY ----------
            List<String> writtenResults = new ArrayList<>();

            for (String formatted : formattedResults) {

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

            String combined = String.join(
                request.getDelimiterSpec().getPairDelimiter(),
                writtenResults
            );

            return new JobResult(true, combined, "");

        } catch (RuntimeException ex) {
            // Let the parent submitJob() catch block turn this into
            // "Unexpected error: ..." for consistency with CP5.
            throw ex;
        }
    }

    /**
     * Allow tests to cleanly shut down the thread pool.
     */
    public void shutdown() {
        executor.shutdown();
    }
    @Override
    public void close() {
        executor.shutdown();
    }
}

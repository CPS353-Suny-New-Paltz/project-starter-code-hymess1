package project.api.conceptual;

import project.api.conceptual.EngineComputeAPI.ComputeRequest;
import project.api.conceptual.EngineComputeAPI.ComputeResponse;
import project.api.conceptual.EngineComputeAPI.ComputeStatusCode;


/*
 * Performance optimization:
 * Bottleneck identified by measuring execution time of largestPrimeFactor
 * in EngineComputeAPIImpl using a JUnit performance comparison test.
 *
 * Optimization used:
 * - Put sqrt(n) outside the factor loop to avoid repeated multiplication
 *
 */

public class EngineComputeAPIOptimizedImpl implements EngineComputeAPI {

    @Override
    public ComputeResponse compute(ComputeRequest request) {
        try {
        	// ----- VALIDATION -----
            if (request == null) {
                return error(
                    ComputeStatusCode.INVALID_INPUT,
                    "request was null"
                );
            }

            String delimiter = request.delimiter();
            if (delimiter == null || delimiter.isBlank()) {
                return error(
                    ComputeStatusCode.INVALID_INPUT,
                    "delimiter was null or blank"
                );
            }

            int n = request.input();
            if (n < 0) {
                return error(
                    ComputeStatusCode.INVALID_INPUT,
                    "input must be non-negative"
                );
            }

            // ----- NORMAL EXECUTION -----
            int result = largestPrimeFactor(n);
            String formatted = n + delimiter + result;

            return success(formatted);

        } catch (Exception ex) {
            // ----- EXCEPTION TRANSLATION -----
        	return error(
                    ComputeStatusCode.INTERNAL_ERROR,
                    "unexpected exception: " + ex.getMessage()
                );
            }
        }
    /**
     * success response wrapper.
     */
    private ComputeResponse success(final String formatted) {
        return new ComputeResponse() {
            @Override
            public ComputeStatusCode status() {
                return ComputeStatusCode.SUCCESS;
            }

            @Override
            public String asFormatted() {
                return formatted;
            }
        };
    }
    
    /**
     *  error response wrapper with status + message.
     */
    private ComputeResponse error(
        final ComputeStatusCode code,
        final String msg
    ) {
        final String formatted = "error:" + msg;
        return new ComputeResponse() {
            @Override
            public ComputeStatusCode status() {
                return code;
            }

            @Override
            public String asFormatted() {
                return formatted;
            }
        };
    }

    
    private int largestPrimeFactor(int n) {
        if (n <= 1) {
            return -1;
        }

        int maxPrime = -1;

        while (n % 2 == 0) {
            maxPrime = 2;
            n /= 2;
        }

        int limit = (int) Math.sqrt(n);
        for (int i = 3; i <= limit; i += 2) {
            while (n % i == 0) {
                maxPrime = i;
                n /= i;
            }
        }

        if (n > 2) {
            maxPrime = n;
        }

        return maxPrime;
    }

}


package project.api.conceptual;

import project.api.conceptual.EngineComputeAPI.ComputeRequest;
import project.api.conceptual.EngineComputeAPI.ComputeResponse;
import project.api.conceptual.EngineComputeAPI.ComputeStatusCode;


/**
 * Implementation of the conceptual compute API.
 * This class takes an integer and returns a formatted string containing
 * the input and its largest prime factor.
 */
public class EngineComputeAPIImpl implements EngineComputeAPI {

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
     * Helper: success response wrapper.
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
     * Helper: error response wrapper with status + message.
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

    /** PRIVATE: no validation needed unless YOU choose to add some. */
    private int largestPrimeFactor(int n) {
        if (n <= 1) {
            return -1;
        }

        int maxPrime = -1;

        while (n % 2 == 0) {
            maxPrime = 2;
            n /= 2;
        }

        for (int i = 3; i * i <= n; i += 2) {
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


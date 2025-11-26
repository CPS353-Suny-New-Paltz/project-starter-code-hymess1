package project.api.conceptual;

import project.api.conceptual.EngineComputeAPI.ComputeRequest;
import project.api.conceptual.EngineComputeAPI.ComputeResponse;

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
                return error("request was null");
            }

            String delimiter = request.delimiter();
            if (delimiter == null || delimiter.isBlank()) {
                return error("delimiter was null or blank");
            }

            int n = request.input();
            if (n < 0) {
                return error("input must be non-negative");
            }

            // ----- NORMAL EXECUTION -----
            int result = largestPrimeFactor(n);
            String formatted = n + delimiter + result;

            return () -> formatted;

        } catch (Exception ex) {
            // ----- EXCEPTION TRANSLATION -----
            return error("unexpected exception: " + ex.getMessage());
        }
    }

    /** Helper: wrap error messages in a ComputeResponse sentinel. */
    private ComputeResponse error(String msg) {
        final String formatted = "error:" + msg;
        return () -> formatted;
    }

    /** PRIVATE: no validation needed unless YOU choose to add some. */
    private int largestPrimeFactor(int n) {
        if (n <= 1) return -1;

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

        if (n > 2) maxPrime = n;

        return maxPrime;
    }
}


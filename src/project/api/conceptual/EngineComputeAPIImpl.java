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

        // Safely pull out the input. If the request is somehow null,
        // treat the input as 0 so we don't crash.
        final int n = (request == null) ? 0 : request.input();

        // Use the delimiter from the request unless it's null;
        // in that case, fall back to ":".
        final String delimiter =
                (request == null || request.delimiter() == null)
                        ? ":"
                        : request.delimiter();

        // Compute the largest prime factor for the input.
        final int result = largestPrimeFactor(n);
        

        // Build the final "n:result" style string.
        final String formatted = n + delimiter + result;

        /*
         * ComputeResponse is a functional interface with exactly one method: asFormatted().
         *
         * Writing `return () -> formatted;` means:
         *   "create a small anonymous object where calling asFormatted() simply
         *    returns the string we just built."
         *
         * It’s just shorthand for:
         *
         *   return new ComputeResponse() {
         *       @Override
         *       public String asFormatted() {
         *           return formatted;
         *       }
         *   };
         *
         * Both forms are equivalent — the lambda is just cleaner.
         */
        return () -> formatted;
    }

    /**
     * Computes the largest prime factor of n.
     * Returns -1 if n doesn't have one (meaning n <= 1).
     *
     * The method repeatedly pulls out small prime factors so that whatever
     * remains at the end must be the largest one.
     */
    private int largestPrimeFactor(int n) {

        // For numbers 0, 1, or negatives, there is no prime factor.
        if (n <= 1) {
            return -1;
        }

        int maxPrime = -1;

        // First remove all factors of 2.
        // This handles even numbers before we start checking odd ones.
        while (n % 2 == 0) {
            maxPrime = 2;
            n /= 2;
        }

        // Now check odd potential factors up to sqrt(n).
        // Each time we find one, divide it out completely.
        for (int i = 3; i * i <= n; i += 2) {
            while (n % i == 0) {
                maxPrime = i;
                n /= i;
            }
        }

        // If there's anything left at this point, it's a prime number
        // larger than all earlier factors we removed.
        if (n > 2) {
            maxPrime = n;
        }

        return maxPrime;
    }
}

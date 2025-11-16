package project.api.conceptual;

import project.api.conceptual.EngineComputeAPI.ComputeRequest;
import project.api.conceptual.EngineComputeAPI.ComputeResponse;

/**
 * Empty implementation for Checkpoint 3.
 * Stateless conceptual compute placeholder.
 */
public class EngineComputeAPIImpl implements EngineComputeAPI {

	@Override
	public ComputeResponse compute(ComputeRequest request) {
	    final int n = (request == null) ? 0 : request.input();
	    final String delim =
	            (request == null || request.delimiter() == null) ? ":" : request.delimiter();

	    final int result = largestPrimeFactor(n);

	    final String formatted = n + delim + result;

	    return () -> formatted;
	}
	/**
	 * Returns the largest prime factor of n.
	 * For n <= 1, returns -1 as a sentinel meaning "no prime factor".
	 */
	private int largestPrimeFactor(int n) {
	    if (n <= 1) {
	        return -1;
	    }

	    int maxPrime = -1;

	    // Remove factors of 2
	    while (n % 2 == 0) {
	        maxPrime = 2;
	        n /= 2;
	    }

	    // Remove odd factors
	    for (int i = 3; i * i <= n; i += 2) {
	        while (n % i == 0) {
	            maxPrime = i;
	            n /= i;
	        }
	    }

	    // If n is still > 2, then it is prime
	    if (n > 2) {
	        maxPrime = n;
	    }

	    return maxPrime;
	}

	

}

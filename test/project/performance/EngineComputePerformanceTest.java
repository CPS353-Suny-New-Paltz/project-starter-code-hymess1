package project.performance;

import org.junit.jupiter.api.Test;
import project.api.conceptual.EngineComputeAPI;
import project.api.conceptual.EngineComputeAPIImpl;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EngineComputePerformanceTest {

    @Test
    public void measureComputeEnginePerformance() {

        EngineComputeAPI engine = new EngineComputeAPIImpl();

        // Larger inputs can cause worst-case behavior
        int[] inputs = {
        	    999_983,     // prime near 1M
        	    999_979,     // prime
        	    999_961      // prime
        	};




        int iterations = 10000;

        long start = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            for (int n : inputs) {
                engine.computeSingle(n, ":");
            }
        }

        long end = System.nanoTime();
        long elapsedMs = (end - start) / 1_000_000;

        System.out.println("Baseline compute time: " + elapsedMs + " ms");

        // Assert that timing occurred 
        assertTrue(elapsedMs > 0);
    }
}


package project.api.conceptual;

import project.annotations.ConceptualAPIPrototype;

/**
 * Prototype for the conceptual boundary.
 * Implements a small, CPU-bound example: largest prime ≤ n.
 */
public class EngineComputeAPIProto {

    /**
     * Single public prototype method. Non-null return always.
     * Example: n=10, kv=":"  ->  "10:7"
     */
    @ConceptualAPIPrototype
    public String prototypeCompute(int n, String keyValueDelimiter) {
        final String kv = (keyValueDelimiter == null) ? ":" : keyValueDelimiter;
        if (n < 2) {
            // Keep behavior defined even for out-of-range small inputs.
            return "1" + kv + "1";
        }
        int p = n;
        while (p >= 2 && !isPrime(p)) {
            p--;
        }
        return n + kv + p;
    }

    
    private boolean isPrime(int x) {
        if (x < 2) return false;
        if (x % 2 == 0) return x == 2;
        for (int i = 3; i * i <= x; i += 2) {
            if (x % i == 0) return false;
        }
        return true;
    }
}

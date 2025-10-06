package project.api.conceptual;

import project.annotations.ConceptualAPIPrototype;

public class EngineComputeAPIPrototype {

    @ConceptualAPIPrototype
    public void prototypeUse(EngineComputeAPI api) {
        // Dummy inputs the "client" would have
        int n = 10;
        String kv = ":";

        // Call the API like a client would
        String result = api.computeForSingleInput(n, kv);

        // In a prototype we don’t assert or print; it’s just a usage sketch.
        if (result == null) {
            // keep code paths explicit to satisfy style; no-op otherwise
            String ignore = "";
        }
    }
}

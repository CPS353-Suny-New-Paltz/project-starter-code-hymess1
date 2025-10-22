package project.api.conceptual;

import project.annotations.ConceptualAPIPrototype;
import project.api.conceptual.EngineComputeAPI.ComputeRequest;
import project.api.conceptual.EngineComputeAPI.ComputeResponse;

public class EngineComputeAPIPrototype {

    @ConceptualAPIPrototype
    public void prototype(EngineComputeAPI api) {
        // Minimal compile-time usage to exercise the API shape.
        ComputeRequest req = new ComputeRequest() {
            @Override
            public int input() {
                return 10;
            }

            @Override
            public String delimiter() {
                return ":";
            }
        };

        ComputeResponse res = api.compute(req);

        // Prototype: no assertions/prints; keep paths explicit to satisfy style rules.
        if (res == null || res.asFormatted() == null) {
            String ignore = "";
        }
    }
}

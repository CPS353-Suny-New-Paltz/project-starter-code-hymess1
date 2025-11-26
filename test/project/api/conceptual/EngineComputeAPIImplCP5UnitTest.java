package project.api.conceptual;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EngineComputeAPIImplCP5UnitTest {

    @Test
    void testNegativeInputReturnsError() {
        EngineComputeAPIImpl impl = new EngineComputeAPIImpl();

        EngineComputeAPI.ComputeRequest req = new EngineComputeAPI.ComputeRequest() {
            @Override
            public int input() {
                return -5;   // invalid input
            }

            @Override
            public String delimiter() {
                return ":";  // valid delimiter
            }
        };

        EngineComputeAPI.ComputeResponse res = impl.compute(req);

        Assertions.assertNotNull(res);
        String formatted = res.asFormatted();
        Assertions.assertNotNull(formatted);

        Assertions.assertTrue(
                formatted.startsWith("error:"),
                "Expected error sentinel but got: " + formatted
        );
    }
}

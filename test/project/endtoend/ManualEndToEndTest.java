package project.endtoend;

import project.api.conceptual.EngineComputeAPIImpl;
import project.api.network.NetworkService;
import project.api.network.MultithreadedNetworkService;
import project.grpc.GrpcDataIOService;
import project.model.DelimiterSpec;
import project.model.JobRequest;
import project.model.JobResult;

 // 1. Start Process gRPC server
 // 2. Start Network gRPC server
 // 3. Run this class
 
public class ManualEndToEndTest {

    public static void main(String[] args) {
        System.out.println("=== Manual End-to-End Test ===");
        GrpcDataIOService dataIO =
                new GrpcDataIOService("localhost", 50052);
        try {
            var compute = new EngineComputeAPIImpl();

            NetworkService network =
                    new MultithreadedNetworkService(dataIO, compute, 4);

            JobRequest req = new JobRequest(
                    "input_numbers.txt",     
                    "output_results.txt",
                    DelimiterSpec.defaults()
            );

            JobResult result = network.submitJob(req);

            if (result.isSuccess()) {
                System.out.println("Job succeeded");
                System.out.println("Result text:");
                System.out.println(result.getResultText());
            } else {
                System.out.println("Job failed");
                System.out.println(result.getErrorMessage());
            }

        } finally {
            dataIO.shutdown();
        }
    }
}

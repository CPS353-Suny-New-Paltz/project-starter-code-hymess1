package project.grpc;
import io.grpc.Server; 
import project.api.conceptual.EngineComputeAPIImpl;
import project.api.network.MultithreadedNetworkService;
import project.api.network.NetworkService;
import project.api.process.DataIOService;
import project.model.DelimiterSpec;
import project.grpc.GrpcDataIOService;
import project.api.conceptual.EngineComputeAPIOptimizedImpl;

public class RunNetworkServer {
    public static void main(String[] args) throws Exception {

        DataIOService dataIO = new GrpcDataIOService("localhost", 50052);
        var compute = new EngineComputeAPIOptimizedImpl();
        NetworkService network =
                new MultithreadedNetworkService(dataIO, compute, 4);

        NetworkGrpcServer
                .startServer(50051, network)
                .awaitTermination();
    }
}

package project.grpc;
import io.grpc.Server;    
import project.api.process.DataIOServiceImpl;

public class RunProcessServer {
    public static void main(String[] args) throws Exception {
        ProcessGrpcServer
                .startServer(50052, new DataIOServiceImpl())
                .awaitTermination();
    }
}

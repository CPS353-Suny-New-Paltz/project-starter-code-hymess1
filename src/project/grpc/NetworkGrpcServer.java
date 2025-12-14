package project.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import network.NetworkServiceGrpc;
import network.JobRequest;
import network.JobResult;

import project.api.network.NetworkService;
import project.model.DelimiterSpec;


 // Turns gRPC requests into NetworkService calls.
 
public class NetworkGrpcServer extends NetworkServiceGrpc.NetworkServiceImplBase {

    private final NetworkService networkService;

    public NetworkGrpcServer(NetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public void submitJob(
        JobRequest request,
        StreamObserver<JobResult> responseObserver
    ) {
        try {
            // ---------- Convert proto to model ----------
            DelimiterSpec delims = new DelimiterSpec(
                false,
                request.getPairDelimiter(),
                request.getKeyValueDelimiter()
            );

            project.model.JobRequest modelReq =
                new project.model.JobRequest(
                    request.getInputSourcePointer(),
                    request.getOutputDestinationPointer(),
                    delims
                );

            // ---------- Call NetworkService ----------
            project.model.JobResult modelRes =
                networkService.submitJob(modelReq);

            // ---------- Convert model to proto ----------
            JobResult grpcRes = JobResult.newBuilder()
                .setSuccess(modelRes.isSuccess())
                .setResultText(modelRes.getResultText())
                .setErrorMessage(modelRes.getErrorMessage())
                .build();

            responseObserver.onNext(grpcRes);
            responseObserver.onCompleted();

        } catch (Exception e) {
            // gRPC boundary shouldn't throw
            JobResult error = JobResult.newBuilder()
                .setSuccess(false)
                .setResultText("")
                .setErrorMessage("Unexpected server error: " + e.getMessage())
                .build();

            responseObserver.onNext(error);
            responseObserver.onCompleted();
        }
    }

    
     // start a gRPC server.
     
    public static Server startServer(
        int port,
        NetworkService networkService
    ) throws Exception {

        Server server = ServerBuilder
            .forPort(port)
            .addService(new NetworkGrpcServer(networkService))
            .build()
            .start();

        System.out.println("Network gRPC server started on port " + port);

        return server;
    }
}

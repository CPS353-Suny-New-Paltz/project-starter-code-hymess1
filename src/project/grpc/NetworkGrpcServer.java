package project.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import network.Network;
import network.NetworkServiceGrpc;

import project.api.network.NetworkService;
import project.model.DelimiterSpec;

public class NetworkGrpcServer extends NetworkServiceGrpc.NetworkServiceImplBase {

    private final NetworkService networkService;

    public NetworkGrpcServer(NetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public void submitJob(
            network.Network.JobRequest request,
            StreamObserver<network.Network.JobResult> responseObserver) {

        try {
            // ---- proto to model ----
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

            // ---- call NetworkService ----
            project.model.JobResult modelRes =
                    networkService.submitJob(modelReq);

            // ---- model to proto ----
            network.Network.JobResult grpcRes =
                    Network.JobResult.newBuilder()
                            .setSuccess(modelRes.isSuccess())
                            .setResultText(modelRes.getResultText())
                            .setErrorMessage(modelRes.getErrorMessage())
                            .build();

            responseObserver.onNext(grpcRes);
            responseObserver.onCompleted();

        } catch (Exception e) {
            network.Network.JobResult error =
                    Network.JobResult.newBuilder()
                            .setSuccess(false)
                            .setResultText("")
                            .setErrorMessage("Unexpected server error: " + e.getMessage())
                            .build();

            responseObserver.onNext(error);
            responseObserver.onCompleted();
        }
    }

    public static Server startServer(
            int port,
            NetworkService networkService) throws Exception {

        Server server = ServerBuilder
                .forPort(port)
                .addService(new NetworkGrpcServer(networkService))
                .build()
                .start();

        System.out.println("Network gRPC server started on port " + port);
        return server;
    }
}

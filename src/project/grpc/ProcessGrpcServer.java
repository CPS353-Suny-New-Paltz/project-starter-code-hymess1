package project.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import process.Process;
import process.ProcessServiceGrpc;

import project.api.process.DataIOService;
import project.api.process.DataIOService.DataReadRequest;
import project.api.process.DataIOService.DataWriteRequest;


  //gRPC server for the Process API.
 
 // Bridges proto requests to a DataIOService implementation.
 
public class ProcessGrpcServer extends ProcessServiceGrpc.ProcessServiceImplBase {

    private final DataIOService dataIO;

    public ProcessGrpcServer(DataIOService dataIO) {
        this.dataIO = dataIO;
    }

    @Override
    public void read(
            Process.ReadRequest request,
            StreamObserver<Process.ReadResponse> responseObserver) {

        try {
            var res = dataIO.read(() -> () -> request.getSourcePointer());

            Process.ReadResponse grpcRes =
                    Process.ReadResponse.newBuilder()
                            .addAllValues(res.payload())
                            .build();

            responseObserver.onNext(grpcRes);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onNext(
                    Process.ReadResponse.newBuilder().build()
            );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void write(
            Process.WriteRequest request,
            StreamObserver<Process.WriteResponse> responseObserver) {

        try {
            var res = dataIO.write(new DataWriteRequest() {
                @Override
                public DataIOService.DataPointer destination() {
                    return () -> request.getDestinationPointer();
                }

                @Override
                public String payload() {
                    return request.getPayload();
                }
            });

            Process.WriteResponse grpcRes =
                    Process.WriteResponse.newBuilder()
                            .setSuccess(res.code().success())
                            .setMessage(res.message())
                            .build();

            responseObserver.onNext(grpcRes);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onNext(
                    Process.WriteResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage("Unexpected error: " + e.getMessage())
                            .build()
            );
            responseObserver.onCompleted();
        }
    }

    public static Server startServer(int port, DataIOService dataIO) throws Exception {
        Server server = ServerBuilder
                .forPort(port)
                .addService(new ProcessGrpcServer(dataIO))
                .build()
                .start();

        System.out.println("Process gRPC server started on port " + port);
        return server;
    }
}

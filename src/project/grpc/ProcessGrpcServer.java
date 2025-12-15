package project.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import project.grpc.process.ProcessProto;
import project.grpc.process.ProcessServiceGrpc;

import project.api.process.DataIOService;
import project.api.process.DataIOService.DataWriteRequest;

/**
 * gRPC server for the Process API.
 *
 * Bridges proto requests to a DataIOService implementation.
 */
public class ProcessGrpcServer extends ProcessServiceGrpc.ProcessServiceImplBase {

    private final DataIOService dataIO;

    public ProcessGrpcServer(DataIOService dataIO) {
        this.dataIO = dataIO;
    }

    @Override
    public void read(
            ProcessProto.ReadRequest request,
            StreamObserver<ProcessProto.ReadResponse> responseObserver) {

        try {
            var res = dataIO.read(() -> () -> request.getSourcePointer());

            ProcessProto.ReadResponse grpcRes =
                    ProcessProto.ReadResponse.newBuilder()
                            .addAllValues(res.payload())
                            .build();

            responseObserver.onNext(grpcRes);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onNext(
                    ProcessProto.ReadResponse.newBuilder().build()
            );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void write(
            ProcessProto.WriteRequest request,
            StreamObserver<ProcessProto.WriteResponse> responseObserver) {

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

            ProcessProto.WriteResponse grpcRes =
                    ProcessProto.WriteResponse.newBuilder()
                            .setCode(
                                res.code() == DataIOService.DataWriteResponse.StatusCode.SUCCESS
                                    ? ProcessProto.StatusCode.SUCCESS
                                    : ProcessProto.StatusCode.FAILURE
                            )
                            .setMessage(res.message())
                            .build();

            responseObserver.onNext(grpcRes);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onNext(
                    ProcessProto.WriteResponse.newBuilder()
                            .setCode(ProcessProto.StatusCode.FAILURE)
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

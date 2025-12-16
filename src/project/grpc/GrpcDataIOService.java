package project.grpc;

import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import project.grpc.process.ProcessProto;
import project.grpc.process.ProcessServiceGrpc;

import project.api.process.DataIOService;

// gRPC adapter for the Process API.
//
// Implements DataIOService but gives the work to a
// Process gRPC server.
public class GrpcDataIOService implements DataIOService {

    private final ManagedChannel channel;
    private final ProcessServiceGrpc.ProcessServiceBlockingStub stub;

    public GrpcDataIOService(String host, int port) {
        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        this.stub = ProcessServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public DataReadResponse read(DataReadRequest request) {

        // -------- validation --------
        if (request == null || request.source() == null) {
            return () -> List.of();
        }

        String path = request.source().asString();
        if (path == null || path.isBlank()) {
            return () -> List.of();
        }

        // -------- model to Proto --------
        ProcessProto.ReadRequest protoReq =
                ProcessProto.ReadRequest.newBuilder()
                        .setSourcePointer(path)
                        .build();

        // -------- gRPC call --------
        ProcessProto.ReadResponse protoRes;
        try {
            protoRes = stub.read(protoReq);
        } catch (Exception e) {
            return () -> List.of();
        }

        // -------- proto to model --------
        List<Integer> payload = protoRes.getValuesList();
        return () -> payload;
    }

    @Override
    public DataWriteResponse write(DataWriteRequest request) {

        // -------- validation --------
        if (request == null || request.destination() == null) {
            return failure("Write request or destination was null.");
        }

        String path = request.destination().asString();
        if (path == null || path.isBlank()) {
            return failure("Invalid destination path.");
        }

        String payload = request.payload();
        if (payload == null) {
            payload = "";
        }

        // -------- model to proto --------
        ProcessProto.WriteRequest protoReq =
                ProcessProto.WriteRequest.newBuilder()
                        .setDestinationPointer(path)
                        .setPayload(payload)
                        .build();

        // -------- gRPC call --------
        ProcessProto.WriteResponse protoRes;
        try {
            protoRes = stub.write(protoReq);
        } catch (Exception e) {
            return failure("gRPC write failed: " + e.getMessage());
        }

        // -------- proto to model --------
        if (protoRes.getCode() == ProcessProto.StatusCode.SUCCESS) {
            return success(protoRes.getMessage());
        } else {
            return failure(protoRes.getMessage());
        }
    }

    public void shutdown() {
        channel.shutdown();
    }

    // -------- helpers --------

    private DataWriteResponse success(String msg) {
        return new DataWriteResponse() {
            @Override
            public StatusCode code() {
                return StatusCode.SUCCESS;
            }

            @Override
            public String message() {
                return msg == null ? "" : msg;
            }
        };
    }

    private DataWriteResponse failure(String msg) {
        return new DataWriteResponse() {
            @Override
            public StatusCode code() {
                return StatusCode.FAILURE;
            }

            @Override
            public String message() {
                return msg == null ? "" : msg;
            }
        };
    }
}

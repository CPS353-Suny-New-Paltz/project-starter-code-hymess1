package project.endtoend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;

import project.api.conceptual.EngineComputeAPIOptimizedImpl;
import project.api.network.MultithreadedNetworkService;
import project.api.network.NetworkService;
import project.api.process.DataIOServiceImpl;
import project.grpc.GrpcDataIOService;
import project.grpc.NetworkGrpcServer;
import project.grpc.ProcessGrpcServer;
import project.grpc.network.NetworkProto;
import project.grpc.network.NetworkServiceGrpc;

public class GrpcEndToEndTest {

    private Server processServer;
    private Server networkServer;
    private ManagedChannel channel;
    private GrpcDataIOService grpcDataIO;

    @AfterEach
    void cleanup() {
    	if (channel != null) {
    	    channel.shutdownNow();
    	}
    	if (networkServer != null) {
    	    networkServer.shutdownNow();
    	}
    	if (grpcDataIO != null) {
    	    grpcDataIO.shutdown();
    	}
    	if (processServer != null) {
    	    processServer.shutdownNow();
    	}

    }

    @Test
    void grpc_end_to_end_writes_expected_output() throws Exception {

        // temp input/output files
        Path input = Files.createTempFile("grpc-e2e-input", ".txt");
        Path output = Files.createTempFile("grpc-e2e-output", ".txt");

        // DataIOServiceImpl reads comma-separated numbers
        Files.writeString(input, "1,10,25");

        // Start Process server on ephemeral port
        processServer = ProcessGrpcServer.startServer(0, new DataIOServiceImpl());
        int processPort = processServer.getPort();

        // Start Network server on ephemeral port, pointing at process server
        grpcDataIO = new GrpcDataIOService("localhost", processPort);
        var engine = new EngineComputeAPIOptimizedImpl();
        NetworkService networkService =
                new MultithreadedNetworkService(grpcDataIO, engine, 4);

        networkServer = NetworkGrpcServer.startServer(0, networkService);
        int networkPort = networkServer.getPort();

        // Create real gRPC client to Network server
        channel = ManagedChannelBuilder.forAddress("localhost", networkPort)
                .usePlaintext()
                .build();

        NetworkServiceGrpc.NetworkServiceBlockingStub stub =
                NetworkServiceGrpc.newBlockingStub(channel);

        // Call gRPC
        NetworkProto.JobRequest req =
                NetworkProto.JobRequest.newBuilder()
                        .setInputSourcePointer(input.toString())
                        .setOutputDestinationPointer(output.toString())
                        .setKeyValueDelimiter(":")
                        .setPairDelimiter(";")
                        .build();

        NetworkProto.JobResult resp = stub.submitJob(req);

        assertTrue(resp.getSuccess(), "Expected success but got: " + resp.getErrorMessage());

        // Output file uses DataIOServiceImpl which appends comma-separated payloads
        String fileOut = Files.readString(output).trim();
        assertEquals("1:-1,10:5,25:5", fileOut);

        // And the result text returned by NetworkServiceImpl is pair-delimited
        assertEquals("1:-1;10:5;25:5", resp.getResultText());
    }
}

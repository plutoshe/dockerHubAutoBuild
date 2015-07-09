package mapreduce;

import static io.grpc.stub.ClientCalls.createMethodDescriptor;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.duplexStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.unaryFutureCall;
import static io.grpc.stub.ServerCalls.createMethodDefinition;
import static io.grpc.stub.ServerCalls.asyncUnaryRequestCall;
import static io.grpc.stub.ServerCalls.asyncStreamingRequestCall;

@javax.annotation.Generated("by gRPC proto compiler")
public class ReducerStreamGrpc {

  private static final io.grpc.stub.Method<mapreduce.ReducerRequest,
      mapreduce.ReducerResponse> METHOD_GET_STREAM_COLLECT_RESULT =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.DUPLEX_STREAMING, "GetStreamCollectResult",
          io.grpc.protobuf.ProtoUtils.marshaller(mapreduce.ReducerRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(mapreduce.ReducerResponse.PARSER));

  public static ReducerStreamStub newStub(io.grpc.Channel channel) {
    return new ReducerStreamStub(channel, CONFIG);
  }

  public static ReducerStreamBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ReducerStreamBlockingStub(channel, CONFIG);
  }

  public static ReducerStreamFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ReducerStreamFutureStub(channel, CONFIG);
  }

  public static final ReducerStreamServiceDescriptor CONFIG =
      new ReducerStreamServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class ReducerStreamServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<ReducerStreamServiceDescriptor> {
    public final io.grpc.MethodDescriptor<mapreduce.ReducerRequest,
        mapreduce.ReducerResponse> getStreamCollectResult;

    private ReducerStreamServiceDescriptor() {
      getStreamCollectResult = createMethodDescriptor(
          "mapreduce.ReducerStream", METHOD_GET_STREAM_COLLECT_RESULT);
    }

    @SuppressWarnings("unchecked")
    private ReducerStreamServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      getStreamCollectResult = (io.grpc.MethodDescriptor<mapreduce.ReducerRequest,
          mapreduce.ReducerResponse>) methodMap.get(
          CONFIG.getStreamCollectResult.getName());
    }

    @java.lang.Override
    protected ReducerStreamServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new ReducerStreamServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          getStreamCollectResult);
    }
  }

  public static interface ReducerStream {

    public io.grpc.stub.StreamObserver<mapreduce.ReducerRequest> getStreamCollectResult(
        io.grpc.stub.StreamObserver<mapreduce.ReducerResponse> responseObserver);
  }

  public static interface ReducerStreamBlockingClient {
  }

  public static interface ReducerStreamFutureClient {
  }

  public static class ReducerStreamStub extends
      io.grpc.stub.AbstractStub<ReducerStreamStub, ReducerStreamServiceDescriptor>
      implements ReducerStream {
    private ReducerStreamStub(io.grpc.Channel channel,
        ReducerStreamServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected ReducerStreamStub build(io.grpc.Channel channel,
        ReducerStreamServiceDescriptor config) {
      return new ReducerStreamStub(channel, config);
    }

    @java.lang.Override
    public io.grpc.stub.StreamObserver<mapreduce.ReducerRequest> getStreamCollectResult(
        io.grpc.stub.StreamObserver<mapreduce.ReducerResponse> responseObserver) {
      return duplexStreamingCall(
          channel.newCall(config.getStreamCollectResult), responseObserver);
    }
  }

  public static class ReducerStreamBlockingStub extends
      io.grpc.stub.AbstractStub<ReducerStreamBlockingStub, ReducerStreamServiceDescriptor>
      implements ReducerStreamBlockingClient {
    private ReducerStreamBlockingStub(io.grpc.Channel channel,
        ReducerStreamServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected ReducerStreamBlockingStub build(io.grpc.Channel channel,
        ReducerStreamServiceDescriptor config) {
      return new ReducerStreamBlockingStub(channel, config);
    }
  }

  public static class ReducerStreamFutureStub extends
      io.grpc.stub.AbstractStub<ReducerStreamFutureStub, ReducerStreamServiceDescriptor>
      implements ReducerStreamFutureClient {
    private ReducerStreamFutureStub(io.grpc.Channel channel,
        ReducerStreamServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected ReducerStreamFutureStub build(io.grpc.Channel channel,
        ReducerStreamServiceDescriptor config) {
      return new ReducerStreamFutureStub(channel, config);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final ReducerStream serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("mapreduce.ReducerStream")
      .addMethod(createMethodDefinition(
          METHOD_GET_STREAM_COLLECT_RESULT,
          asyncStreamingRequestCall(
            new io.grpc.stub.ServerCalls.StreamingRequestMethod<
                mapreduce.ReducerRequest,
                mapreduce.ReducerResponse>() {
              @java.lang.Override
              public io.grpc.stub.StreamObserver<mapreduce.ReducerRequest> invoke(
                  io.grpc.stub.StreamObserver<mapreduce.ReducerResponse> responseObserver) {
                return serviceImpl.getStreamCollectResult(responseObserver);
              }
            }))).build();
  }
}

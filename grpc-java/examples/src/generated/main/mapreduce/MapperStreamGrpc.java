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
public class MapperStreamGrpc {

  private static final io.grpc.stub.Method<mapreduce.MapperRequest,
      mapreduce.MapperResponse> METHOD_GET_STREAM_EMIT_RESULT =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.DUPLEX_STREAMING, "GetStreamEmitResult",
          io.grpc.protobuf.ProtoUtils.marshaller(mapreduce.MapperRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(mapreduce.MapperResponse.PARSER));

  public static MapperStreamStub newStub(io.grpc.Channel channel) {
    return new MapperStreamStub(channel, CONFIG);
  }

  public static MapperStreamBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new MapperStreamBlockingStub(channel, CONFIG);
  }

  public static MapperStreamFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new MapperStreamFutureStub(channel, CONFIG);
  }

  public static final MapperStreamServiceDescriptor CONFIG =
      new MapperStreamServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class MapperStreamServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<MapperStreamServiceDescriptor> {
    public final io.grpc.MethodDescriptor<mapreduce.MapperRequest,
        mapreduce.MapperResponse> getStreamEmitResult;

    private MapperStreamServiceDescriptor() {
      getStreamEmitResult = createMethodDescriptor(
          "mapreduce.MapperStream", METHOD_GET_STREAM_EMIT_RESULT);
    }

    @SuppressWarnings("unchecked")
    private MapperStreamServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      getStreamEmitResult = (io.grpc.MethodDescriptor<mapreduce.MapperRequest,
          mapreduce.MapperResponse>) methodMap.get(
          CONFIG.getStreamEmitResult.getName());
    }

    @java.lang.Override
    protected MapperStreamServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new MapperStreamServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          getStreamEmitResult);
    }
  }

  public static interface MapperStream {

    public io.grpc.stub.StreamObserver<mapreduce.MapperRequest> getStreamEmitResult(
        io.grpc.stub.StreamObserver<mapreduce.MapperResponse> responseObserver);
  }

  public static interface MapperStreamBlockingClient {
  }

  public static interface MapperStreamFutureClient {
  }

  public static class MapperStreamStub extends
      io.grpc.stub.AbstractStub<MapperStreamStub, MapperStreamServiceDescriptor>
      implements MapperStream {
    private MapperStreamStub(io.grpc.Channel channel,
        MapperStreamServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected MapperStreamStub build(io.grpc.Channel channel,
        MapperStreamServiceDescriptor config) {
      return new MapperStreamStub(channel, config);
    }

    @java.lang.Override
    public io.grpc.stub.StreamObserver<mapreduce.MapperRequest> getStreamEmitResult(
        io.grpc.stub.StreamObserver<mapreduce.MapperResponse> responseObserver) {
      return duplexStreamingCall(
          channel.newCall(config.getStreamEmitResult), responseObserver);
    }
  }

  public static class MapperStreamBlockingStub extends
      io.grpc.stub.AbstractStub<MapperStreamBlockingStub, MapperStreamServiceDescriptor>
      implements MapperStreamBlockingClient {
    private MapperStreamBlockingStub(io.grpc.Channel channel,
        MapperStreamServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected MapperStreamBlockingStub build(io.grpc.Channel channel,
        MapperStreamServiceDescriptor config) {
      return new MapperStreamBlockingStub(channel, config);
    }
  }

  public static class MapperStreamFutureStub extends
      io.grpc.stub.AbstractStub<MapperStreamFutureStub, MapperStreamServiceDescriptor>
      implements MapperStreamFutureClient {
    private MapperStreamFutureStub(io.grpc.Channel channel,
        MapperStreamServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected MapperStreamFutureStub build(io.grpc.Channel channel,
        MapperStreamServiceDescriptor config) {
      return new MapperStreamFutureStub(channel, config);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final MapperStream serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("mapreduce.MapperStream")
      .addMethod(createMethodDefinition(
          METHOD_GET_STREAM_EMIT_RESULT,
          asyncStreamingRequestCall(
            new io.grpc.stub.ServerCalls.StreamingRequestMethod<
                mapreduce.MapperRequest,
                mapreduce.MapperResponse>() {
              @java.lang.Override
              public io.grpc.stub.StreamObserver<mapreduce.MapperRequest> invoke(
                  io.grpc.stub.StreamObserver<mapreduce.MapperResponse> responseObserver) {
                return serviceImpl.getStreamEmitResult(responseObserver);
              }
            }))).build();
  }
}

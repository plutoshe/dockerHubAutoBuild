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
public class MasterGrpc {

  private static final io.grpc.stub.Method<mapreduce.WorkRequest,
      mapreduce.WorkConfigResponse> METHOD_GET_WORK =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.SERVER_STREAMING, "GetWork",
          io.grpc.protobuf.ProtoUtils.marshaller(mapreduce.WorkRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(mapreduce.WorkConfigResponse.PARSER));

  public static MasterStub newStub(io.grpc.Channel channel) {
    return new MasterStub(channel, CONFIG);
  }

  public static MasterBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new MasterBlockingStub(channel, CONFIG);
  }

  public static MasterFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new MasterFutureStub(channel, CONFIG);
  }

  public static final MasterServiceDescriptor CONFIG =
      new MasterServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class MasterServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<MasterServiceDescriptor> {
    public final io.grpc.MethodDescriptor<mapreduce.WorkRequest,
        mapreduce.WorkConfigResponse> getWork;

    private MasterServiceDescriptor() {
      getWork = createMethodDescriptor(
          "mapreduce.Master", METHOD_GET_WORK);
    }

    @SuppressWarnings("unchecked")
    private MasterServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      getWork = (io.grpc.MethodDescriptor<mapreduce.WorkRequest,
          mapreduce.WorkConfigResponse>) methodMap.get(
          CONFIG.getWork.getName());
    }

    @java.lang.Override
    protected MasterServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new MasterServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          getWork);
    }
  }

  public static interface Master {

    public void getWork(mapreduce.WorkRequest request,
        io.grpc.stub.StreamObserver<mapreduce.WorkConfigResponse> responseObserver);
  }

  public static interface MasterBlockingClient {

    public java.util.Iterator<mapreduce.WorkConfigResponse> getWork(
        mapreduce.WorkRequest request);
  }

  public static interface MasterFutureClient {
  }

  public static class MasterStub extends
      io.grpc.stub.AbstractStub<MasterStub, MasterServiceDescriptor>
      implements Master {
    private MasterStub(io.grpc.Channel channel,
        MasterServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected MasterStub build(io.grpc.Channel channel,
        MasterServiceDescriptor config) {
      return new MasterStub(channel, config);
    }

    @java.lang.Override
    public void getWork(mapreduce.WorkRequest request,
        io.grpc.stub.StreamObserver<mapreduce.WorkConfigResponse> responseObserver) {
      asyncServerStreamingCall(
          channel.newCall(config.getWork), request, responseObserver);
    }
  }

  public static class MasterBlockingStub extends
      io.grpc.stub.AbstractStub<MasterBlockingStub, MasterServiceDescriptor>
      implements MasterBlockingClient {
    private MasterBlockingStub(io.grpc.Channel channel,
        MasterServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected MasterBlockingStub build(io.grpc.Channel channel,
        MasterServiceDescriptor config) {
      return new MasterBlockingStub(channel, config);
    }

    @java.lang.Override
    public java.util.Iterator<mapreduce.WorkConfigResponse> getWork(
        mapreduce.WorkRequest request) {
      return blockingServerStreamingCall(
          channel.newCall(config.getWork), request);
    }
  }

  public static class MasterFutureStub extends
      io.grpc.stub.AbstractStub<MasterFutureStub, MasterServiceDescriptor>
      implements MasterFutureClient {
    private MasterFutureStub(io.grpc.Channel channel,
        MasterServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected MasterFutureStub build(io.grpc.Channel channel,
        MasterServiceDescriptor config) {
      return new MasterFutureStub(channel, config);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final Master serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("mapreduce.Master")
      .addMethod(createMethodDefinition(
          METHOD_GET_WORK,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                mapreduce.WorkRequest,
                mapreduce.WorkConfigResponse>() {
              @java.lang.Override
              public void invoke(
                  mapreduce.WorkRequest request,
                  io.grpc.stub.StreamObserver<mapreduce.WorkConfigResponse> responseObserver) {
                serviceImpl.getWork(request, responseObserver);
              }
            }))).build();
  }
}

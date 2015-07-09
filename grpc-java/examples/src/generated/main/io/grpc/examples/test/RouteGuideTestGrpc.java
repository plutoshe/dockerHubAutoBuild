package io.grpc.examples.test;

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
public class RouteGuideTestGrpc {

  private static final io.grpc.stub.Method<io.grpc.examples.test.Point1,
      io.grpc.examples.test.Point1> METHOD_GET_FEATURE =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "GetFeature",
          io.grpc.protobuf.ProtoUtils.marshaller(io.grpc.examples.test.Point1.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(io.grpc.examples.test.Point1.PARSER));

  public static RouteGuideTestStub newStub(io.grpc.Channel channel) {
    return new RouteGuideTestStub(channel, CONFIG);
  }

  public static RouteGuideTestBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new RouteGuideTestBlockingStub(channel, CONFIG);
  }

  public static RouteGuideTestFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new RouteGuideTestFutureStub(channel, CONFIG);
  }

  public static final RouteGuideTestServiceDescriptor CONFIG =
      new RouteGuideTestServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class RouteGuideTestServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<RouteGuideTestServiceDescriptor> {
    public final io.grpc.MethodDescriptor<io.grpc.examples.test.Point1,
        io.grpc.examples.test.Point1> getFeature;

    private RouteGuideTestServiceDescriptor() {
      getFeature = createMethodDescriptor(
          "routeguide.RouteGuideTest", METHOD_GET_FEATURE);
    }

    @SuppressWarnings("unchecked")
    private RouteGuideTestServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      getFeature = (io.grpc.MethodDescriptor<io.grpc.examples.test.Point1,
          io.grpc.examples.test.Point1>) methodMap.get(
          CONFIG.getFeature.getName());
    }

    @java.lang.Override
    protected RouteGuideTestServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new RouteGuideTestServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          getFeature);
    }
  }

  public static interface RouteGuideTest {

    public void getFeature(io.grpc.examples.test.Point1 request,
        io.grpc.stub.StreamObserver<io.grpc.examples.test.Point1> responseObserver);
  }

  public static interface RouteGuideTestBlockingClient {

    public io.grpc.examples.test.Point1 getFeature(io.grpc.examples.test.Point1 request);
  }

  public static interface RouteGuideTestFutureClient {

    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.test.Point1> getFeature(
        io.grpc.examples.test.Point1 request);
  }

  public static class RouteGuideTestStub extends
      io.grpc.stub.AbstractStub<RouteGuideTestStub, RouteGuideTestServiceDescriptor>
      implements RouteGuideTest {
    private RouteGuideTestStub(io.grpc.Channel channel,
        RouteGuideTestServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected RouteGuideTestStub build(io.grpc.Channel channel,
        RouteGuideTestServiceDescriptor config) {
      return new RouteGuideTestStub(channel, config);
    }

    @java.lang.Override
    public void getFeature(io.grpc.examples.test.Point1 request,
        io.grpc.stub.StreamObserver<io.grpc.examples.test.Point1> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.getFeature), request, responseObserver);
    }
  }

  public static class RouteGuideTestBlockingStub extends
      io.grpc.stub.AbstractStub<RouteGuideTestBlockingStub, RouteGuideTestServiceDescriptor>
      implements RouteGuideTestBlockingClient {
    private RouteGuideTestBlockingStub(io.grpc.Channel channel,
        RouteGuideTestServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected RouteGuideTestBlockingStub build(io.grpc.Channel channel,
        RouteGuideTestServiceDescriptor config) {
      return new RouteGuideTestBlockingStub(channel, config);
    }

    @java.lang.Override
    public io.grpc.examples.test.Point1 getFeature(io.grpc.examples.test.Point1 request) {
      return blockingUnaryCall(
          channel.newCall(config.getFeature), request);
    }
  }

  public static class RouteGuideTestFutureStub extends
      io.grpc.stub.AbstractStub<RouteGuideTestFutureStub, RouteGuideTestServiceDescriptor>
      implements RouteGuideTestFutureClient {
    private RouteGuideTestFutureStub(io.grpc.Channel channel,
        RouteGuideTestServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected RouteGuideTestFutureStub build(io.grpc.Channel channel,
        RouteGuideTestServiceDescriptor config) {
      return new RouteGuideTestFutureStub(channel, config);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.test.Point1> getFeature(
        io.grpc.examples.test.Point1 request) {
      return unaryFutureCall(
          channel.newCall(config.getFeature), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final RouteGuideTest serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("routeguide.RouteGuideTest")
      .addMethod(createMethodDefinition(
          METHOD_GET_FEATURE,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                io.grpc.examples.test.Point1,
                io.grpc.examples.test.Point1>() {
              @java.lang.Override
              public void invoke(
                  io.grpc.examples.test.Point1 request,
                  io.grpc.stub.StreamObserver<io.grpc.examples.test.Point1> responseObserver) {
                serviceImpl.getFeature(request, responseObserver);
              }
            }))).build();
  }
}

package org.spin.grpc.util;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * The greeting service definition.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.17.0)",
    comments = "Source: dictionary.proto")
public final class DictionaryServiceGrpc {

  private DictionaryServiceGrpc() {}

  public static final String SERVICE_NAME = "dictionary.DictionaryService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ClientRequest,
      org.spin.grpc.util.WindowDefinition> getRequestWindowMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestWindow",
      requestType = org.spin.grpc.util.ClientRequest.class,
      responseType = org.spin.grpc.util.WindowDefinition.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ClientRequest,
      org.spin.grpc.util.WindowDefinition> getRequestWindowMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ClientRequest, org.spin.grpc.util.WindowDefinition> getRequestWindowMethod;
    if ((getRequestWindowMethod = DictionaryServiceGrpc.getRequestWindowMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getRequestWindowMethod = DictionaryServiceGrpc.getRequestWindowMethod) == null) {
          DictionaryServiceGrpc.getRequestWindowMethod = getRequestWindowMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ClientRequest, org.spin.grpc.util.WindowDefinition>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "RequestWindow"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ClientRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.WindowDefinition.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("RequestWindow"))
                  .build();
          }
        }
     }
     return getRequestWindowMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DictionaryServiceStub newStub(io.grpc.Channel channel) {
    return new DictionaryServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DictionaryServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DictionaryServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DictionaryServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DictionaryServiceFutureStub(channel);
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static abstract class DictionaryServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Request a Window
     * </pre>
     */
    public void requestWindow(org.spin.grpc.util.ClientRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.WindowDefinition> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestWindowMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestWindowMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ClientRequest,
                org.spin.grpc.util.WindowDefinition>(
                  this, METHODID_REQUEST_WINDOW)))
          .build();
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class DictionaryServiceStub extends io.grpc.stub.AbstractStub<DictionaryServiceStub> {
    private DictionaryServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DictionaryServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DictionaryServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DictionaryServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request a Window
     * </pre>
     */
    public void requestWindow(org.spin.grpc.util.ClientRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.WindowDefinition> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestWindowMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class DictionaryServiceBlockingStub extends io.grpc.stub.AbstractStub<DictionaryServiceBlockingStub> {
    private DictionaryServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DictionaryServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DictionaryServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DictionaryServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request a Window
     * </pre>
     */
    public org.spin.grpc.util.WindowDefinition requestWindow(org.spin.grpc.util.ClientRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestWindowMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class DictionaryServiceFutureStub extends io.grpc.stub.AbstractStub<DictionaryServiceFutureStub> {
    private DictionaryServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DictionaryServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DictionaryServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DictionaryServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request a Window
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.WindowDefinition> requestWindow(
        org.spin.grpc.util.ClientRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestWindowMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_WINDOW = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DictionaryServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DictionaryServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_WINDOW:
          serviceImpl.requestWindow((org.spin.grpc.util.ClientRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.WindowDefinition>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class DictionaryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DictionaryServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.spin.grpc.util.ADempiereDictionary.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DictionaryService");
    }
  }

  private static final class DictionaryServiceFileDescriptorSupplier
      extends DictionaryServiceBaseDescriptorSupplier {
    DictionaryServiceFileDescriptorSupplier() {}
  }

  private static final class DictionaryServiceMethodDescriptorSupplier
      extends DictionaryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DictionaryServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (DictionaryServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DictionaryServiceFileDescriptorSupplier())
              .addMethod(getRequestWindowMethod())
              .build();
        }
      }
    }
    return result;
  }
}

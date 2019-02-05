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
    comments = "Source: data.proto")
public final class DataServiceGrpc {

  private DataServiceGrpc() {}

  public static final String SERVICE_NAME = "data.DataService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObject> getRequestPOMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestPO",
      requestType = org.spin.grpc.util.ValueObjectRequest.class,
      responseType = org.spin.grpc.util.ValueObject.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObject> getRequestPOMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObject> getRequestPOMethod;
    if ((getRequestPOMethod = DataServiceGrpc.getRequestPOMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRequestPOMethod = DataServiceGrpc.getRequestPOMethod) == null) {
          DataServiceGrpc.getRequestPOMethod = getRequestPOMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObject>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RequestPO"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObject.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RequestPO"))
                  .build();
          }
        }
     }
     return getRequestPOMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObjectList> getRequestPOListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestPOList",
      requestType = org.spin.grpc.util.ValueObjectRequest.class,
      responseType = org.spin.grpc.util.ValueObjectList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObjectList> getRequestPOListMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObjectList> getRequestPOListMethod;
    if ((getRequestPOListMethod = DataServiceGrpc.getRequestPOListMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRequestPOListMethod = DataServiceGrpc.getRequestPOListMethod) == null) {
          DataServiceGrpc.getRequestPOListMethod = getRequestPOListMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObjectList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RequestPOList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectList.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RequestPOList"))
                  .build();
          }
        }
     }
     return getRequestPOListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.CalloutRequest,
      org.spin.grpc.util.CalloutResponse> getRequestCalloutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestCallout",
      requestType = org.spin.grpc.util.CalloutRequest.class,
      responseType = org.spin.grpc.util.CalloutResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.CalloutRequest,
      org.spin.grpc.util.CalloutResponse> getRequestCalloutMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.CalloutRequest, org.spin.grpc.util.CalloutResponse> getRequestCalloutMethod;
    if ((getRequestCalloutMethod = DataServiceGrpc.getRequestCalloutMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRequestCalloutMethod = DataServiceGrpc.getRequestCalloutMethod) == null) {
          DataServiceGrpc.getRequestCalloutMethod = getRequestCalloutMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.CalloutRequest, org.spin.grpc.util.CalloutResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RequestCallout"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.CalloutRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.CalloutResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RequestCallout"))
                  .build();
          }
        }
     }
     return getRequestCalloutMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DataServiceStub newStub(io.grpc.Channel channel) {
    return new DataServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DataServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DataServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DataServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DataServiceFutureStub(channel);
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static abstract class DataServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Request PO entity from UUID (Optional ID)
     * </pre>
     */
    public void requestPO(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObject> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestPOMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request PO List
     * </pre>
     */
    public void requestPOList(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestPOListMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Callout
     * </pre>
     */
    public void requestCallout(org.spin.grpc.util.CalloutRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.CalloutResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestCalloutMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestPOMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ValueObjectRequest,
                org.spin.grpc.util.ValueObject>(
                  this, METHODID_REQUEST_PO)))
          .addMethod(
            getRequestPOListMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ValueObjectRequest,
                org.spin.grpc.util.ValueObjectList>(
                  this, METHODID_REQUEST_POLIST)))
          .addMethod(
            getRequestCalloutMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.CalloutRequest,
                org.spin.grpc.util.CalloutResponse>(
                  this, METHODID_REQUEST_CALLOUT)))
          .build();
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class DataServiceStub extends io.grpc.stub.AbstractStub<DataServiceStub> {
    private DataServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DataServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DataServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request PO entity from UUID (Optional ID)
     * </pre>
     */
    public void requestPO(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObject> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestPOMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request PO List
     * </pre>
     */
    public void requestPOList(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestPOListMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Callout
     * </pre>
     */
    public void requestCallout(org.spin.grpc.util.CalloutRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.CalloutResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestCalloutMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class DataServiceBlockingStub extends io.grpc.stub.AbstractStub<DataServiceBlockingStub> {
    private DataServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DataServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DataServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request PO entity from UUID (Optional ID)
     * </pre>
     */
    public org.spin.grpc.util.ValueObject requestPO(org.spin.grpc.util.ValueObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestPOMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request PO List
     * </pre>
     */
    public org.spin.grpc.util.ValueObjectList requestPOList(org.spin.grpc.util.ValueObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestPOListMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Callout
     * </pre>
     */
    public org.spin.grpc.util.CalloutResponse requestCallout(org.spin.grpc.util.CalloutRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestCalloutMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class DataServiceFutureStub extends io.grpc.stub.AbstractStub<DataServiceFutureStub> {
    private DataServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DataServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DataServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request PO entity from UUID (Optional ID)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ValueObject> requestPO(
        org.spin.grpc.util.ValueObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestPOMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request PO List
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ValueObjectList> requestPOList(
        org.spin.grpc.util.ValueObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestPOListMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Callout
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.CalloutResponse> requestCallout(
        org.spin.grpc.util.CalloutRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestCalloutMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_PO = 0;
  private static final int METHODID_REQUEST_POLIST = 1;
  private static final int METHODID_REQUEST_CALLOUT = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DataServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DataServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_PO:
          serviceImpl.requestPO((org.spin.grpc.util.ValueObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObject>) responseObserver);
          break;
        case METHODID_REQUEST_POLIST:
          serviceImpl.requestPOList((org.spin.grpc.util.ValueObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList>) responseObserver);
          break;
        case METHODID_REQUEST_CALLOUT:
          serviceImpl.requestCallout((org.spin.grpc.util.CalloutRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.CalloutResponse>) responseObserver);
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

  private static abstract class DataServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DataServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.spin.grpc.util.ADempiereData.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DataService");
    }
  }

  private static final class DataServiceFileDescriptorSupplier
      extends DataServiceBaseDescriptorSupplier {
    DataServiceFileDescriptorSupplier() {}
  }

  private static final class DataServiceMethodDescriptorSupplier
      extends DataServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DataServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (DataServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DataServiceFileDescriptorSupplier())
              .addMethod(getRequestPOMethod())
              .addMethod(getRequestPOListMethod())
              .addMethod(getRequestCalloutMethod())
              .build();
        }
      }
    }
    return result;
  }
}

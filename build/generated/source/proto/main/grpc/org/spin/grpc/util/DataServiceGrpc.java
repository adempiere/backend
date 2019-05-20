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
      org.spin.grpc.util.ValueObject> getRequestObjectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestObject",
      requestType = org.spin.grpc.util.ValueObjectRequest.class,
      responseType = org.spin.grpc.util.ValueObject.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObject> getRequestObjectMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObject> getRequestObjectMethod;
    if ((getRequestObjectMethod = DataServiceGrpc.getRequestObjectMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRequestObjectMethod = DataServiceGrpc.getRequestObjectMethod) == null) {
          DataServiceGrpc.getRequestObjectMethod = getRequestObjectMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObject>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RequestObject"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObject.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RequestObject"))
                  .build();
          }
        }
     }
     return getRequestObjectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObjectList> getRequestObjectListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestObjectList",
      requestType = org.spin.grpc.util.ValueObjectRequest.class,
      responseType = org.spin.grpc.util.ValueObjectList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObjectList> getRequestObjectListMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObjectList> getRequestObjectListMethod;
    if ((getRequestObjectListMethod = DataServiceGrpc.getRequestObjectListMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRequestObjectListMethod = DataServiceGrpc.getRequestObjectListMethod) == null) {
          DataServiceGrpc.getRequestObjectListMethod = getRequestObjectListMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObjectList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RequestObjectList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectList.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RequestObjectList"))
                  .build();
          }
        }
     }
     return getRequestObjectListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObject> getRequestLookupMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestLookup",
      requestType = org.spin.grpc.util.ValueObjectRequest.class,
      responseType = org.spin.grpc.util.ValueObject.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObject> getRequestLookupMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObject> getRequestLookupMethod;
    if ((getRequestLookupMethod = DataServiceGrpc.getRequestLookupMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRequestLookupMethod = DataServiceGrpc.getRequestLookupMethod) == null) {
          DataServiceGrpc.getRequestLookupMethod = getRequestLookupMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObject>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RequestLookup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObject.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RequestLookup"))
                  .build();
          }
        }
     }
     return getRequestLookupMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObjectList> getRequestLookupListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestLookupList",
      requestType = org.spin.grpc.util.ValueObjectRequest.class,
      responseType = org.spin.grpc.util.ValueObjectList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObjectList> getRequestLookupListMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObjectList> getRequestLookupListMethod;
    if ((getRequestLookupListMethod = DataServiceGrpc.getRequestLookupListMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRequestLookupListMethod = DataServiceGrpc.getRequestLookupListMethod) == null) {
          DataServiceGrpc.getRequestLookupListMethod = getRequestLookupListMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObjectList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RequestLookupList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectList.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RequestLookupList"))
                  .build();
          }
        }
     }
     return getRequestLookupListMethod;
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

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ProcessRequest,
      org.spin.grpc.util.ProcessResponse> getRequestProcessMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestProcess",
      requestType = org.spin.grpc.util.ProcessRequest.class,
      responseType = org.spin.grpc.util.ProcessResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ProcessRequest,
      org.spin.grpc.util.ProcessResponse> getRequestProcessMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ProcessRequest, org.spin.grpc.util.ProcessResponse> getRequestProcessMethod;
    if ((getRequestProcessMethod = DataServiceGrpc.getRequestProcessMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRequestProcessMethod = DataServiceGrpc.getRequestProcessMethod) == null) {
          DataServiceGrpc.getRequestProcessMethod = getRequestProcessMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ProcessRequest, org.spin.grpc.util.ProcessResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RequestProcess"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ProcessRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ProcessResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RequestProcess"))
                  .build();
          }
        }
     }
     return getRequestProcessMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObjectList> getRequestBrowserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestBrowser",
      requestType = org.spin.grpc.util.ValueObjectRequest.class,
      responseType = org.spin.grpc.util.ValueObjectList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest,
      org.spin.grpc.util.ValueObjectList> getRequestBrowserMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObjectList> getRequestBrowserMethod;
    if ((getRequestBrowserMethod = DataServiceGrpc.getRequestBrowserMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRequestBrowserMethod = DataServiceGrpc.getRequestBrowserMethod) == null) {
          DataServiceGrpc.getRequestBrowserMethod = getRequestBrowserMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ValueObjectRequest, org.spin.grpc.util.ValueObjectList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RequestBrowser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ValueObjectList.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RequestBrowser"))
                  .build();
          }
        }
     }
     return getRequestBrowserMethod;
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
     * Request Object entity from UUID (Optional ID)
     * </pre>
     */
    public void requestObject(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObject> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestObjectMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Object List
     * </pre>
     */
    public void requestObjectList(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestObjectListMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Lookup
     * </pre>
     */
    public void requestLookup(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObject> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestLookupMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Lookup List
     * </pre>
     */
    public void requestLookupList(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestLookupListMethod(), responseObserver);
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

    /**
     * <pre>
     *	Request a Process / Report
     * </pre>
     */
    public void requestProcess(org.spin.grpc.util.ProcessRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ProcessResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestProcessMethod(), responseObserver);
    }

    /**
     * <pre>
     *	Request Browser Data
     * </pre>
     */
    public void requestBrowser(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestBrowserMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestObjectMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ValueObjectRequest,
                org.spin.grpc.util.ValueObject>(
                  this, METHODID_REQUEST_OBJECT)))
          .addMethod(
            getRequestObjectListMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ValueObjectRequest,
                org.spin.grpc.util.ValueObjectList>(
                  this, METHODID_REQUEST_OBJECT_LIST)))
          .addMethod(
            getRequestLookupMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ValueObjectRequest,
                org.spin.grpc.util.ValueObject>(
                  this, METHODID_REQUEST_LOOKUP)))
          .addMethod(
            getRequestLookupListMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ValueObjectRequest,
                org.spin.grpc.util.ValueObjectList>(
                  this, METHODID_REQUEST_LOOKUP_LIST)))
          .addMethod(
            getRequestCalloutMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.CalloutRequest,
                org.spin.grpc.util.CalloutResponse>(
                  this, METHODID_REQUEST_CALLOUT)))
          .addMethod(
            getRequestProcessMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ProcessRequest,
                org.spin.grpc.util.ProcessResponse>(
                  this, METHODID_REQUEST_PROCESS)))
          .addMethod(
            getRequestBrowserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ValueObjectRequest,
                org.spin.grpc.util.ValueObjectList>(
                  this, METHODID_REQUEST_BROWSER)))
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
     * Request Object entity from UUID (Optional ID)
     * </pre>
     */
    public void requestObject(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObject> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestObjectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Object List
     * </pre>
     */
    public void requestObjectList(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestObjectListMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Lookup
     * </pre>
     */
    public void requestLookup(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObject> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestLookupMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Lookup List
     * </pre>
     */
    public void requestLookupList(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestLookupListMethod(), getCallOptions()), request, responseObserver);
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

    /**
     * <pre>
     *	Request a Process / Report
     * </pre>
     */
    public void requestProcess(org.spin.grpc.util.ProcessRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ProcessResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestProcessMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *	Request Browser Data
     * </pre>
     */
    public void requestBrowser(org.spin.grpc.util.ValueObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestBrowserMethod(), getCallOptions()), request, responseObserver);
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
     * Request Object entity from UUID (Optional ID)
     * </pre>
     */
    public org.spin.grpc.util.ValueObject requestObject(org.spin.grpc.util.ValueObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestObjectMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Object List
     * </pre>
     */
    public org.spin.grpc.util.ValueObjectList requestObjectList(org.spin.grpc.util.ValueObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestObjectListMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Lookup
     * </pre>
     */
    public org.spin.grpc.util.ValueObject requestLookup(org.spin.grpc.util.ValueObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestLookupMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Lookup List
     * </pre>
     */
    public org.spin.grpc.util.ValueObjectList requestLookupList(org.spin.grpc.util.ValueObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestLookupListMethod(), getCallOptions(), request);
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

    /**
     * <pre>
     *	Request a Process / Report
     * </pre>
     */
    public org.spin.grpc.util.ProcessResponse requestProcess(org.spin.grpc.util.ProcessRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestProcessMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *	Request Browser Data
     * </pre>
     */
    public org.spin.grpc.util.ValueObjectList requestBrowser(org.spin.grpc.util.ValueObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestBrowserMethod(), getCallOptions(), request);
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
     * Request Object entity from UUID (Optional ID)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ValueObject> requestObject(
        org.spin.grpc.util.ValueObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestObjectMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Object List
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ValueObjectList> requestObjectList(
        org.spin.grpc.util.ValueObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestObjectListMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Lookup
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ValueObject> requestLookup(
        org.spin.grpc.util.ValueObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestLookupMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Lookup List
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ValueObjectList> requestLookupList(
        org.spin.grpc.util.ValueObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestLookupListMethod(), getCallOptions()), request);
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

    /**
     * <pre>
     *	Request a Process / Report
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ProcessResponse> requestProcess(
        org.spin.grpc.util.ProcessRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestProcessMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *	Request Browser Data
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ValueObjectList> requestBrowser(
        org.spin.grpc.util.ValueObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestBrowserMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_OBJECT = 0;
  private static final int METHODID_REQUEST_OBJECT_LIST = 1;
  private static final int METHODID_REQUEST_LOOKUP = 2;
  private static final int METHODID_REQUEST_LOOKUP_LIST = 3;
  private static final int METHODID_REQUEST_CALLOUT = 4;
  private static final int METHODID_REQUEST_PROCESS = 5;
  private static final int METHODID_REQUEST_BROWSER = 6;

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
        case METHODID_REQUEST_OBJECT:
          serviceImpl.requestObject((org.spin.grpc.util.ValueObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObject>) responseObserver);
          break;
        case METHODID_REQUEST_OBJECT_LIST:
          serviceImpl.requestObjectList((org.spin.grpc.util.ValueObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList>) responseObserver);
          break;
        case METHODID_REQUEST_LOOKUP:
          serviceImpl.requestLookup((org.spin.grpc.util.ValueObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObject>) responseObserver);
          break;
        case METHODID_REQUEST_LOOKUP_LIST:
          serviceImpl.requestLookupList((org.spin.grpc.util.ValueObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList>) responseObserver);
          break;
        case METHODID_REQUEST_CALLOUT:
          serviceImpl.requestCallout((org.spin.grpc.util.CalloutRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.CalloutResponse>) responseObserver);
          break;
        case METHODID_REQUEST_PROCESS:
          serviceImpl.requestProcess((org.spin.grpc.util.ProcessRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ProcessResponse>) responseObserver);
          break;
        case METHODID_REQUEST_BROWSER:
          serviceImpl.requestBrowser((org.spin.grpc.util.ValueObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ValueObjectList>) responseObserver);
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
              .addMethod(getRequestObjectMethod())
              .addMethod(getRequestObjectListMethod())
              .addMethod(getRequestLookupMethod())
              .addMethod(getRequestLookupListMethod())
              .addMethod(getRequestCalloutMethod())
              .addMethod(getRequestProcessMethod())
              .addMethod(getRequestBrowserMethod())
              .build();
        }
      }
    }
    return result;
  }
}

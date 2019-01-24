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
  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Menu> getRequestMenuFromParentUuidMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestMenuFromParentUuid",
      requestType = org.spin.grpc.util.ObjectRequest.class,
      responseType = org.spin.grpc.util.Menu.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Menu> getRequestMenuFromParentUuidMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Menu> getRequestMenuFromParentUuidMethod;
    if ((getRequestMenuFromParentUuidMethod = DictionaryServiceGrpc.getRequestMenuFromParentUuidMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getRequestMenuFromParentUuidMethod = DictionaryServiceGrpc.getRequestMenuFromParentUuidMethod) == null) {
          DictionaryServiceGrpc.getRequestMenuFromParentUuidMethod = getRequestMenuFromParentUuidMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Menu>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "RequestMenuFromParentUuid"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Menu.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("RequestMenuFromParentUuid"))
                  .build();
          }
        }
     }
     return getRequestMenuFromParentUuidMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Window> getRequestWindowMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestWindow",
      requestType = org.spin.grpc.util.ObjectRequest.class,
      responseType = org.spin.grpc.util.Window.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Window> getRequestWindowMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Window> getRequestWindowMethod;
    if ((getRequestWindowMethod = DictionaryServiceGrpc.getRequestWindowMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getRequestWindowMethod = DictionaryServiceGrpc.getRequestWindowMethod) == null) {
          DictionaryServiceGrpc.getRequestWindowMethod = getRequestWindowMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Window>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "RequestWindow"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Window.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("RequestWindow"))
                  .build();
          }
        }
     }
     return getRequestWindowMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Window> getRequestWindowAndTabsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestWindowAndTabs",
      requestType = org.spin.grpc.util.ObjectRequest.class,
      responseType = org.spin.grpc.util.Window.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Window> getRequestWindowAndTabsMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Window> getRequestWindowAndTabsMethod;
    if ((getRequestWindowAndTabsMethod = DictionaryServiceGrpc.getRequestWindowAndTabsMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getRequestWindowAndTabsMethod = DictionaryServiceGrpc.getRequestWindowAndTabsMethod) == null) {
          DictionaryServiceGrpc.getRequestWindowAndTabsMethod = getRequestWindowAndTabsMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Window>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "RequestWindowAndTabs"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Window.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("RequestWindowAndTabs"))
                  .build();
          }
        }
     }
     return getRequestWindowAndTabsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Tab> getRequestTabMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestTab",
      requestType = org.spin.grpc.util.ObjectRequest.class,
      responseType = org.spin.grpc.util.Tab.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Tab> getRequestTabMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Tab> getRequestTabMethod;
    if ((getRequestTabMethod = DictionaryServiceGrpc.getRequestTabMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getRequestTabMethod = DictionaryServiceGrpc.getRequestTabMethod) == null) {
          DictionaryServiceGrpc.getRequestTabMethod = getRequestTabMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Tab>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "RequestTab"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Tab.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("RequestTab"))
                  .build();
          }
        }
     }
     return getRequestTabMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Tab> getRequestTabAndFieldsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestTabAndFields",
      requestType = org.spin.grpc.util.ObjectRequest.class,
      responseType = org.spin.grpc.util.Tab.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Tab> getRequestTabAndFieldsMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Tab> getRequestTabAndFieldsMethod;
    if ((getRequestTabAndFieldsMethod = DictionaryServiceGrpc.getRequestTabAndFieldsMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getRequestTabAndFieldsMethod = DictionaryServiceGrpc.getRequestTabAndFieldsMethod) == null) {
          DictionaryServiceGrpc.getRequestTabAndFieldsMethod = getRequestTabAndFieldsMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Tab>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "RequestTabAndFields"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Tab.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("RequestTabAndFields"))
                  .build();
          }
        }
     }
     return getRequestTabAndFieldsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Field> getRequestFieldMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestField",
      requestType = org.spin.grpc.util.ObjectRequest.class,
      responseType = org.spin.grpc.util.Field.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest,
      org.spin.grpc.util.Field> getRequestFieldMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Field> getRequestFieldMethod;
    if ((getRequestFieldMethod = DictionaryServiceGrpc.getRequestFieldMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getRequestFieldMethod = DictionaryServiceGrpc.getRequestFieldMethod) == null) {
          DictionaryServiceGrpc.getRequestFieldMethod = getRequestFieldMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ObjectRequest, org.spin.grpc.util.Field>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "RequestField"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ObjectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Field.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("RequestField"))
                  .build();
          }
        }
     }
     return getRequestFieldMethod;
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
     * Request Menu from Parent UUID
     * </pre>
     */
    public void requestMenuFromParentUuid(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Menu> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestMenuFromParentUuidMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Window
     * </pre>
     */
    public void requestWindow(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Window> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestWindowMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Window and tabs
     * </pre>
     */
    public void requestWindowAndTabs(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Window> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestWindowAndTabsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Tab
     * </pre>
     */
    public void requestTab(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestTabMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Tab and Fields
     * </pre>
     */
    public void requestTabAndFields(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestTabAndFieldsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Field
     * </pre>
     */
    public void requestField(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Field> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestFieldMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestMenuFromParentUuidMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ObjectRequest,
                org.spin.grpc.util.Menu>(
                  this, METHODID_REQUEST_MENU_FROM_PARENT_UUID)))
          .addMethod(
            getRequestWindowMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ObjectRequest,
                org.spin.grpc.util.Window>(
                  this, METHODID_REQUEST_WINDOW)))
          .addMethod(
            getRequestWindowAndTabsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ObjectRequest,
                org.spin.grpc.util.Window>(
                  this, METHODID_REQUEST_WINDOW_AND_TABS)))
          .addMethod(
            getRequestTabMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ObjectRequest,
                org.spin.grpc.util.Tab>(
                  this, METHODID_REQUEST_TAB)))
          .addMethod(
            getRequestTabAndFieldsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ObjectRequest,
                org.spin.grpc.util.Tab>(
                  this, METHODID_REQUEST_TAB_AND_FIELDS)))
          .addMethod(
            getRequestFieldMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ObjectRequest,
                org.spin.grpc.util.Field>(
                  this, METHODID_REQUEST_FIELD)))
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
     * Request Menu from Parent UUID
     * </pre>
     */
    public void requestMenuFromParentUuid(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Menu> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestMenuFromParentUuidMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Window
     * </pre>
     */
    public void requestWindow(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Window> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestWindowMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Window and tabs
     * </pre>
     */
    public void requestWindowAndTabs(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Window> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestWindowAndTabsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Tab
     * </pre>
     */
    public void requestTab(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestTabMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Tab and Fields
     * </pre>
     */
    public void requestTabAndFields(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestTabAndFieldsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Field
     * </pre>
     */
    public void requestField(org.spin.grpc.util.ObjectRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Field> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestFieldMethod(), getCallOptions()), request, responseObserver);
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
     * Request Menu from Parent UUID
     * </pre>
     */
    public org.spin.grpc.util.Menu requestMenuFromParentUuid(org.spin.grpc.util.ObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestMenuFromParentUuidMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Window
     * </pre>
     */
    public org.spin.grpc.util.Window requestWindow(org.spin.grpc.util.ObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestWindowMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Window and tabs
     * </pre>
     */
    public org.spin.grpc.util.Window requestWindowAndTabs(org.spin.grpc.util.ObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestWindowAndTabsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Tab
     * </pre>
     */
    public org.spin.grpc.util.Tab requestTab(org.spin.grpc.util.ObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestTabMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Tab and Fields
     * </pre>
     */
    public org.spin.grpc.util.Tab requestTabAndFields(org.spin.grpc.util.ObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestTabAndFieldsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Field
     * </pre>
     */
    public org.spin.grpc.util.Field requestField(org.spin.grpc.util.ObjectRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestFieldMethod(), getCallOptions(), request);
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
     * Request Menu from Parent UUID
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Menu> requestMenuFromParentUuid(
        org.spin.grpc.util.ObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestMenuFromParentUuidMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Window
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Window> requestWindow(
        org.spin.grpc.util.ObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestWindowMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Window and tabs
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Window> requestWindowAndTabs(
        org.spin.grpc.util.ObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestWindowAndTabsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Tab
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Tab> requestTab(
        org.spin.grpc.util.ObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestTabMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Tab and Fields
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Tab> requestTabAndFields(
        org.spin.grpc.util.ObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestTabAndFieldsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Field
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Field> requestField(
        org.spin.grpc.util.ObjectRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestFieldMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_MENU_FROM_PARENT_UUID = 0;
  private static final int METHODID_REQUEST_WINDOW = 1;
  private static final int METHODID_REQUEST_WINDOW_AND_TABS = 2;
  private static final int METHODID_REQUEST_TAB = 3;
  private static final int METHODID_REQUEST_TAB_AND_FIELDS = 4;
  private static final int METHODID_REQUEST_FIELD = 5;

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
        case METHODID_REQUEST_MENU_FROM_PARENT_UUID:
          serviceImpl.requestMenuFromParentUuid((org.spin.grpc.util.ObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Menu>) responseObserver);
          break;
        case METHODID_REQUEST_WINDOW:
          serviceImpl.requestWindow((org.spin.grpc.util.ObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Window>) responseObserver);
          break;
        case METHODID_REQUEST_WINDOW_AND_TABS:
          serviceImpl.requestWindowAndTabs((org.spin.grpc.util.ObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Window>) responseObserver);
          break;
        case METHODID_REQUEST_TAB:
          serviceImpl.requestTab((org.spin.grpc.util.ObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab>) responseObserver);
          break;
        case METHODID_REQUEST_TAB_AND_FIELDS:
          serviceImpl.requestTabAndFields((org.spin.grpc.util.ObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab>) responseObserver);
          break;
        case METHODID_REQUEST_FIELD:
          serviceImpl.requestField((org.spin.grpc.util.ObjectRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Field>) responseObserver);
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
              .addMethod(getRequestMenuFromParentUuidMethod())
              .addMethod(getRequestWindowMethod())
              .addMethod(getRequestWindowAndTabsMethod())
              .addMethod(getRequestTabMethod())
              .addMethod(getRequestTabAndFieldsMethod())
              .addMethod(getRequestFieldMethod())
              .build();
        }
      }
    }
    return result;
  }
}

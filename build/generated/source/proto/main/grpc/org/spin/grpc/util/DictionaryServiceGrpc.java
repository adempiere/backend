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
  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Window> getGetWindowMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetWindow",
      requestType = org.spin.grpc.util.EntityRequest.class,
      responseType = org.spin.grpc.util.Window.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Window> getGetWindowMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Window> getGetWindowMethod;
    if ((getGetWindowMethod = DictionaryServiceGrpc.getGetWindowMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getGetWindowMethod = DictionaryServiceGrpc.getGetWindowMethod) == null) {
          DictionaryServiceGrpc.getGetWindowMethod = getGetWindowMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Window>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "GetWindow"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.EntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Window.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("GetWindow"))
                  .build();
          }
        }
     }
     return getGetWindowMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Window> getGetWindowAndTabsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetWindowAndTabs",
      requestType = org.spin.grpc.util.EntityRequest.class,
      responseType = org.spin.grpc.util.Window.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Window> getGetWindowAndTabsMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Window> getGetWindowAndTabsMethod;
    if ((getGetWindowAndTabsMethod = DictionaryServiceGrpc.getGetWindowAndTabsMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getGetWindowAndTabsMethod = DictionaryServiceGrpc.getGetWindowAndTabsMethod) == null) {
          DictionaryServiceGrpc.getGetWindowAndTabsMethod = getGetWindowAndTabsMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Window>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "GetWindowAndTabs"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.EntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Window.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("GetWindowAndTabs"))
                  .build();
          }
        }
     }
     return getGetWindowAndTabsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Tab> getGetTabMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetTab",
      requestType = org.spin.grpc.util.EntityRequest.class,
      responseType = org.spin.grpc.util.Tab.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Tab> getGetTabMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Tab> getGetTabMethod;
    if ((getGetTabMethod = DictionaryServiceGrpc.getGetTabMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getGetTabMethod = DictionaryServiceGrpc.getGetTabMethod) == null) {
          DictionaryServiceGrpc.getGetTabMethod = getGetTabMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Tab>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "GetTab"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.EntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Tab.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("GetTab"))
                  .build();
          }
        }
     }
     return getGetTabMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Tab> getGetTabAndFieldsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetTabAndFields",
      requestType = org.spin.grpc.util.EntityRequest.class,
      responseType = org.spin.grpc.util.Tab.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Tab> getGetTabAndFieldsMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Tab> getGetTabAndFieldsMethod;
    if ((getGetTabAndFieldsMethod = DictionaryServiceGrpc.getGetTabAndFieldsMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getGetTabAndFieldsMethod = DictionaryServiceGrpc.getGetTabAndFieldsMethod) == null) {
          DictionaryServiceGrpc.getGetTabAndFieldsMethod = getGetTabAndFieldsMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Tab>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "GetTabAndFields"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.EntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Tab.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("GetTabAndFields"))
                  .build();
          }
        }
     }
     return getGetTabAndFieldsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Field> getGetFieldMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetField",
      requestType = org.spin.grpc.util.EntityRequest.class,
      responseType = org.spin.grpc.util.Field.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Field> getGetFieldMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Field> getGetFieldMethod;
    if ((getGetFieldMethod = DictionaryServiceGrpc.getGetFieldMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getGetFieldMethod = DictionaryServiceGrpc.getGetFieldMethod) == null) {
          DictionaryServiceGrpc.getGetFieldMethod = getGetFieldMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Field>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "GetField"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.EntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Field.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("GetField"))
                  .build();
          }
        }
     }
     return getGetFieldMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Process> getGetProcessMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetProcess",
      requestType = org.spin.grpc.util.EntityRequest.class,
      responseType = org.spin.grpc.util.Process.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Process> getGetProcessMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Process> getGetProcessMethod;
    if ((getGetProcessMethod = DictionaryServiceGrpc.getGetProcessMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getGetProcessMethod = DictionaryServiceGrpc.getGetProcessMethod) == null) {
          DictionaryServiceGrpc.getGetProcessMethod = getGetProcessMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Process>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "GetProcess"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.EntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Process.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("GetProcess"))
                  .build();
          }
        }
     }
     return getGetProcessMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Browser> getGetBrowserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetBrowser",
      requestType = org.spin.grpc.util.EntityRequest.class,
      responseType = org.spin.grpc.util.Browser.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest,
      org.spin.grpc.util.Browser> getGetBrowserMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Browser> getGetBrowserMethod;
    if ((getGetBrowserMethod = DictionaryServiceGrpc.getGetBrowserMethod) == null) {
      synchronized (DictionaryServiceGrpc.class) {
        if ((getGetBrowserMethod = DictionaryServiceGrpc.getGetBrowserMethod) == null) {
          DictionaryServiceGrpc.getGetBrowserMethod = getGetBrowserMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.EntityRequest, org.spin.grpc.util.Browser>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "dictionary.DictionaryService", "GetBrowser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.EntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Browser.getDefaultInstance()))
                  .setSchemaDescriptor(new DictionaryServiceMethodDescriptorSupplier("GetBrowser"))
                  .build();
          }
        }
     }
     return getGetBrowserMethod;
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
    public void getWindow(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Window> responseObserver) {
      asyncUnimplementedUnaryCall(getGetWindowMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Window and tabs
     * </pre>
     */
    public void getWindowAndTabs(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Window> responseObserver) {
      asyncUnimplementedUnaryCall(getGetWindowAndTabsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Tab
     * </pre>
     */
    public void getTab(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTabMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Tab and Fields
     * </pre>
     */
    public void getTabAndFields(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTabAndFieldsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Field
     * </pre>
     */
    public void getField(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Field> responseObserver) {
      asyncUnimplementedUnaryCall(getGetFieldMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Process
     * </pre>
     */
    public void getProcess(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Process> responseObserver) {
      asyncUnimplementedUnaryCall(getGetProcessMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Browser
     * </pre>
     */
    public void getBrowser(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Browser> responseObserver) {
      asyncUnimplementedUnaryCall(getGetBrowserMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetWindowMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.EntityRequest,
                org.spin.grpc.util.Window>(
                  this, METHODID_GET_WINDOW)))
          .addMethod(
            getGetWindowAndTabsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.EntityRequest,
                org.spin.grpc.util.Window>(
                  this, METHODID_GET_WINDOW_AND_TABS)))
          .addMethod(
            getGetTabMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.EntityRequest,
                org.spin.grpc.util.Tab>(
                  this, METHODID_GET_TAB)))
          .addMethod(
            getGetTabAndFieldsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.EntityRequest,
                org.spin.grpc.util.Tab>(
                  this, METHODID_GET_TAB_AND_FIELDS)))
          .addMethod(
            getGetFieldMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.EntityRequest,
                org.spin.grpc.util.Field>(
                  this, METHODID_GET_FIELD)))
          .addMethod(
            getGetProcessMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.EntityRequest,
                org.spin.grpc.util.Process>(
                  this, METHODID_GET_PROCESS)))
          .addMethod(
            getGetBrowserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.EntityRequest,
                org.spin.grpc.util.Browser>(
                  this, METHODID_GET_BROWSER)))
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
    public void getWindow(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Window> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetWindowMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Window and tabs
     * </pre>
     */
    public void getWindowAndTabs(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Window> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetWindowAndTabsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Tab
     * </pre>
     */
    public void getTab(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetTabMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Tab and Fields
     * </pre>
     */
    public void getTabAndFields(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetTabAndFieldsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Field
     * </pre>
     */
    public void getField(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Field> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetFieldMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Process
     * </pre>
     */
    public void getProcess(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Process> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetProcessMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Browser
     * </pre>
     */
    public void getBrowser(org.spin.grpc.util.EntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Browser> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetBrowserMethod(), getCallOptions()), request, responseObserver);
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
    public org.spin.grpc.util.Window getWindow(org.spin.grpc.util.EntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetWindowMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Window and tabs
     * </pre>
     */
    public org.spin.grpc.util.Window getWindowAndTabs(org.spin.grpc.util.EntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetWindowAndTabsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Tab
     * </pre>
     */
    public org.spin.grpc.util.Tab getTab(org.spin.grpc.util.EntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetTabMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Tab and Fields
     * </pre>
     */
    public org.spin.grpc.util.Tab getTabAndFields(org.spin.grpc.util.EntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetTabAndFieldsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Field
     * </pre>
     */
    public org.spin.grpc.util.Field getField(org.spin.grpc.util.EntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetFieldMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Process
     * </pre>
     */
    public org.spin.grpc.util.Process getProcess(org.spin.grpc.util.EntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetProcessMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Browser
     * </pre>
     */
    public org.spin.grpc.util.Browser getBrowser(org.spin.grpc.util.EntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetBrowserMethod(), getCallOptions(), request);
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
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Window> getWindow(
        org.spin.grpc.util.EntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetWindowMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Window and tabs
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Window> getWindowAndTabs(
        org.spin.grpc.util.EntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetWindowAndTabsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Tab
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Tab> getTab(
        org.spin.grpc.util.EntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetTabMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Tab and Fields
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Tab> getTabAndFields(
        org.spin.grpc.util.EntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetTabAndFieldsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Field
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Field> getField(
        org.spin.grpc.util.EntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetFieldMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Process
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Process> getProcess(
        org.spin.grpc.util.EntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetProcessMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Browser
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Browser> getBrowser(
        org.spin.grpc.util.EntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetBrowserMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_WINDOW = 0;
  private static final int METHODID_GET_WINDOW_AND_TABS = 1;
  private static final int METHODID_GET_TAB = 2;
  private static final int METHODID_GET_TAB_AND_FIELDS = 3;
  private static final int METHODID_GET_FIELD = 4;
  private static final int METHODID_GET_PROCESS = 5;
  private static final int METHODID_GET_BROWSER = 6;

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
        case METHODID_GET_WINDOW:
          serviceImpl.getWindow((org.spin.grpc.util.EntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Window>) responseObserver);
          break;
        case METHODID_GET_WINDOW_AND_TABS:
          serviceImpl.getWindowAndTabs((org.spin.grpc.util.EntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Window>) responseObserver);
          break;
        case METHODID_GET_TAB:
          serviceImpl.getTab((org.spin.grpc.util.EntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab>) responseObserver);
          break;
        case METHODID_GET_TAB_AND_FIELDS:
          serviceImpl.getTabAndFields((org.spin.grpc.util.EntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Tab>) responseObserver);
          break;
        case METHODID_GET_FIELD:
          serviceImpl.getField((org.spin.grpc.util.EntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Field>) responseObserver);
          break;
        case METHODID_GET_PROCESS:
          serviceImpl.getProcess((org.spin.grpc.util.EntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Process>) responseObserver);
          break;
        case METHODID_GET_BROWSER:
          serviceImpl.getBrowser((org.spin.grpc.util.EntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Browser>) responseObserver);
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
              .addMethod(getGetWindowMethod())
              .addMethod(getGetWindowAndTabsMethod())
              .addMethod(getGetTabMethod())
              .addMethod(getGetTabAndFieldsMethod())
              .addMethod(getGetFieldMethod())
              .addMethod(getGetProcessMethod())
              .addMethod(getGetBrowserMethod())
              .build();
        }
      }
    }
    return result;
  }
}

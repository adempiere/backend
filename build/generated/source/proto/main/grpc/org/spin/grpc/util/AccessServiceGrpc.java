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
    comments = "Source: access.proto")
public final class AccessServiceGrpc {

  private AccessServiceGrpc() {}

  public static final String SERVICE_NAME = "access.AccessService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.UserInfoValue> getRequestUserInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestUserInfo",
      requestType = org.spin.grpc.util.LoginRequest.class,
      responseType = org.spin.grpc.util.UserInfoValue.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.UserInfoValue> getRequestUserInfoMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.UserInfoValue> getRequestUserInfoMethod;
    if ((getRequestUserInfoMethod = AccessServiceGrpc.getRequestUserInfoMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRequestUserInfoMethod = AccessServiceGrpc.getRequestUserInfoMethod) == null) {
          AccessServiceGrpc.getRequestUserInfoMethod = getRequestUserInfoMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.UserInfoValue>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RequestUserInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoValue.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RequestUserInfo"))
                  .build();
          }
        }
     }
     return getRequestUserInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.Session> getRequestLoginMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestLogin",
      requestType = org.spin.grpc.util.LoginRequest.class,
      responseType = org.spin.grpc.util.Session.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.Session> getRequestLoginMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.Session> getRequestLoginMethod;
    if ((getRequestLoginMethod = AccessServiceGrpc.getRequestLoginMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRequestLoginMethod = AccessServiceGrpc.getRequestLoginMethod) == null) {
          AccessServiceGrpc.getRequestLoginMethod = getRequestLoginMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.Session>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RequestLogin"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Session.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RequestLogin"))
                  .build();
          }
        }
     }
     return getRequestLoginMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.Session> getRequestLoginDefaultMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestLoginDefault",
      requestType = org.spin.grpc.util.LoginRequest.class,
      responseType = org.spin.grpc.util.Session.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.Session> getRequestLoginDefaultMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.Session> getRequestLoginDefaultMethod;
    if ((getRequestLoginDefaultMethod = AccessServiceGrpc.getRequestLoginDefaultMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRequestLoginDefaultMethod = AccessServiceGrpc.getRequestLoginDefaultMethod) == null) {
          AccessServiceGrpc.getRequestLoginDefaultMethod = getRequestLoginDefaultMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.Session>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RequestLoginDefault"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Session.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RequestLoginDefault"))
                  .build();
          }
        }
     }
     return getRequestLoginDefaultMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.LogoutRequest,
      org.spin.grpc.util.Session> getRequestLogoutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestLogout",
      requestType = org.spin.grpc.util.LogoutRequest.class,
      responseType = org.spin.grpc.util.Session.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.LogoutRequest,
      org.spin.grpc.util.Session> getRequestLogoutMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.LogoutRequest, org.spin.grpc.util.Session> getRequestLogoutMethod;
    if ((getRequestLogoutMethod = AccessServiceGrpc.getRequestLogoutMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRequestLogoutMethod = AccessServiceGrpc.getRequestLogoutMethod) == null) {
          AccessServiceGrpc.getRequestLogoutMethod = getRequestLogoutMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.LogoutRequest, org.spin.grpc.util.Session>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RequestLogout"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.LogoutRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Session.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RequestLogout"))
                  .build();
          }
        }
     }
     return getRequestLogoutMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.UserInfoValue> getRequestUserInfoFromSessionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestUserInfoFromSession",
      requestType = org.spin.grpc.util.UserInfoRequest.class,
      responseType = org.spin.grpc.util.UserInfoValue.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.UserInfoValue> getRequestUserInfoFromSessionMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.UserInfoValue> getRequestUserInfoFromSessionMethod;
    if ((getRequestUserInfoFromSessionMethod = AccessServiceGrpc.getRequestUserInfoFromSessionMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRequestUserInfoFromSessionMethod = AccessServiceGrpc.getRequestUserInfoFromSessionMethod) == null) {
          AccessServiceGrpc.getRequestUserInfoFromSessionMethod = getRequestUserInfoFromSessionMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.UserInfoValue>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RequestUserInfoFromSession"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoValue.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RequestUserInfoFromSession"))
                  .build();
          }
        }
     }
     return getRequestUserInfoFromSessionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.Menu> getRequestMenuAndChildMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestMenuAndChild",
      requestType = org.spin.grpc.util.UserInfoRequest.class,
      responseType = org.spin.grpc.util.Menu.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.Menu> getRequestMenuAndChildMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.Menu> getRequestMenuAndChildMethod;
    if ((getRequestMenuAndChildMethod = AccessServiceGrpc.getRequestMenuAndChildMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRequestMenuAndChildMethod = AccessServiceGrpc.getRequestMenuAndChildMethod) == null) {
          AccessServiceGrpc.getRequestMenuAndChildMethod = getRequestMenuAndChildMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.Menu>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RequestMenuAndChild"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Menu.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RequestMenuAndChild"))
                  .build();
          }
        }
     }
     return getRequestMenuAndChildMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.Session> getRequestChangeRoleMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestChangeRole",
      requestType = org.spin.grpc.util.UserInfoRequest.class,
      responseType = org.spin.grpc.util.Session.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.Session> getRequestChangeRoleMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.Session> getRequestChangeRoleMethod;
    if ((getRequestChangeRoleMethod = AccessServiceGrpc.getRequestChangeRoleMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRequestChangeRoleMethod = AccessServiceGrpc.getRequestChangeRoleMethod) == null) {
          AccessServiceGrpc.getRequestChangeRoleMethod = getRequestChangeRoleMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.Session>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RequestChangeRole"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Session.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RequestChangeRole"))
                  .build();
          }
        }
     }
     return getRequestChangeRoleMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AccessServiceStub newStub(io.grpc.Channel channel) {
    return new AccessServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AccessServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new AccessServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AccessServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new AccessServiceFutureStub(channel);
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static abstract class AccessServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Request user roles
     * </pre>
     */
    public void requestUserInfo(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestUserInfoMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request login from user
     * </pre>
     */
    public void requestLogin(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestLoginMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request login and role
     * </pre>
     */
    public void requestLoginDefault(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestLoginDefaultMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public void requestLogout(org.spin.grpc.util.LogoutRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestLogoutMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request user roles from Session
     * </pre>
     */
    public void requestUserInfoFromSession(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestUserInfoFromSessionMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Menu from Parent UUID
     * </pre>
     */
    public void requestMenuAndChild(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Menu> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestMenuAndChildMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request change role
     * </pre>
     */
    public void requestChangeRole(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestChangeRoleMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestUserInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.LoginRequest,
                org.spin.grpc.util.UserInfoValue>(
                  this, METHODID_REQUEST_USER_INFO)))
          .addMethod(
            getRequestLoginMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.LoginRequest,
                org.spin.grpc.util.Session>(
                  this, METHODID_REQUEST_LOGIN)))
          .addMethod(
            getRequestLoginDefaultMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.LoginRequest,
                org.spin.grpc.util.Session>(
                  this, METHODID_REQUEST_LOGIN_DEFAULT)))
          .addMethod(
            getRequestLogoutMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.LogoutRequest,
                org.spin.grpc.util.Session>(
                  this, METHODID_REQUEST_LOGOUT)))
          .addMethod(
            getRequestUserInfoFromSessionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.UserInfoRequest,
                org.spin.grpc.util.UserInfoValue>(
                  this, METHODID_REQUEST_USER_INFO_FROM_SESSION)))
          .addMethod(
            getRequestMenuAndChildMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.UserInfoRequest,
                org.spin.grpc.util.Menu>(
                  this, METHODID_REQUEST_MENU_AND_CHILD)))
          .addMethod(
            getRequestChangeRoleMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.UserInfoRequest,
                org.spin.grpc.util.Session>(
                  this, METHODID_REQUEST_CHANGE_ROLE)))
          .build();
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class AccessServiceStub extends io.grpc.stub.AbstractStub<AccessServiceStub> {
    private AccessServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AccessServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccessServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AccessServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request user roles
     * </pre>
     */
    public void requestUserInfo(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestUserInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request login from user
     * </pre>
     */
    public void requestLogin(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestLoginMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request login and role
     * </pre>
     */
    public void requestLoginDefault(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestLoginDefaultMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public void requestLogout(org.spin.grpc.util.LogoutRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestLogoutMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request user roles from Session
     * </pre>
     */
    public void requestUserInfoFromSession(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestUserInfoFromSessionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Menu from Parent UUID
     * </pre>
     */
    public void requestMenuAndChild(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Menu> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestMenuAndChildMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request change role
     * </pre>
     */
    public void requestChangeRole(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestChangeRoleMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class AccessServiceBlockingStub extends io.grpc.stub.AbstractStub<AccessServiceBlockingStub> {
    private AccessServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AccessServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccessServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AccessServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request user roles
     * </pre>
     */
    public org.spin.grpc.util.UserInfoValue requestUserInfo(org.spin.grpc.util.LoginRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestUserInfoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request login from user
     * </pre>
     */
    public org.spin.grpc.util.Session requestLogin(org.spin.grpc.util.LoginRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestLoginMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request login and role
     * </pre>
     */
    public org.spin.grpc.util.Session requestLoginDefault(org.spin.grpc.util.LoginRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestLoginDefaultMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public org.spin.grpc.util.Session requestLogout(org.spin.grpc.util.LogoutRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestLogoutMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request user roles from Session
     * </pre>
     */
    public org.spin.grpc.util.UserInfoValue requestUserInfoFromSession(org.spin.grpc.util.UserInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestUserInfoFromSessionMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Menu from Parent UUID
     * </pre>
     */
    public org.spin.grpc.util.Menu requestMenuAndChild(org.spin.grpc.util.UserInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestMenuAndChildMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request change role
     * </pre>
     */
    public org.spin.grpc.util.Session requestChangeRole(org.spin.grpc.util.UserInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestChangeRoleMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class AccessServiceFutureStub extends io.grpc.stub.AbstractStub<AccessServiceFutureStub> {
    private AccessServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AccessServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccessServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AccessServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request user roles
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.UserInfoValue> requestUserInfo(
        org.spin.grpc.util.LoginRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestUserInfoMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request login from user
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Session> requestLogin(
        org.spin.grpc.util.LoginRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestLoginMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request login and role
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Session> requestLoginDefault(
        org.spin.grpc.util.LoginRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestLoginDefaultMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Session> requestLogout(
        org.spin.grpc.util.LogoutRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestLogoutMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request user roles from Session
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.UserInfoValue> requestUserInfoFromSession(
        org.spin.grpc.util.UserInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestUserInfoFromSessionMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Menu from Parent UUID
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Menu> requestMenuAndChild(
        org.spin.grpc.util.UserInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestMenuAndChildMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request change role
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Session> requestChangeRole(
        org.spin.grpc.util.UserInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestChangeRoleMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_USER_INFO = 0;
  private static final int METHODID_REQUEST_LOGIN = 1;
  private static final int METHODID_REQUEST_LOGIN_DEFAULT = 2;
  private static final int METHODID_REQUEST_LOGOUT = 3;
  private static final int METHODID_REQUEST_USER_INFO_FROM_SESSION = 4;
  private static final int METHODID_REQUEST_MENU_AND_CHILD = 5;
  private static final int METHODID_REQUEST_CHANGE_ROLE = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AccessServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AccessServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_USER_INFO:
          serviceImpl.requestUserInfo((org.spin.grpc.util.LoginRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue>) responseObserver);
          break;
        case METHODID_REQUEST_LOGIN:
          serviceImpl.requestLogin((org.spin.grpc.util.LoginRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Session>) responseObserver);
          break;
        case METHODID_REQUEST_LOGIN_DEFAULT:
          serviceImpl.requestLoginDefault((org.spin.grpc.util.LoginRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Session>) responseObserver);
          break;
        case METHODID_REQUEST_LOGOUT:
          serviceImpl.requestLogout((org.spin.grpc.util.LogoutRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Session>) responseObserver);
          break;
        case METHODID_REQUEST_USER_INFO_FROM_SESSION:
          serviceImpl.requestUserInfoFromSession((org.spin.grpc.util.UserInfoRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue>) responseObserver);
          break;
        case METHODID_REQUEST_MENU_AND_CHILD:
          serviceImpl.requestMenuAndChild((org.spin.grpc.util.UserInfoRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Menu>) responseObserver);
          break;
        case METHODID_REQUEST_CHANGE_ROLE:
          serviceImpl.requestChangeRole((org.spin.grpc.util.UserInfoRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Session>) responseObserver);
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

  private static abstract class AccessServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AccessServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.spin.grpc.util.ADempiereAccess.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AccessService");
    }
  }

  private static final class AccessServiceFileDescriptorSupplier
      extends AccessServiceBaseDescriptorSupplier {
    AccessServiceFileDescriptorSupplier() {}
  }

  private static final class AccessServiceMethodDescriptorSupplier
      extends AccessServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AccessServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (AccessServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AccessServiceFileDescriptorSupplier())
              .addMethod(getRequestUserInfoMethod())
              .addMethod(getRequestLoginMethod())
              .addMethod(getRequestLoginDefaultMethod())
              .addMethod(getRequestLogoutMethod())
              .addMethod(getRequestUserInfoFromSessionMethod())
              .addMethod(getRequestMenuAndChildMethod())
              .addMethod(getRequestChangeRoleMethod())
              .build();
        }
      }
    }
    return result;
  }
}

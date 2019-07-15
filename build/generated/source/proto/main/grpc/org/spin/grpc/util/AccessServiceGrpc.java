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
      org.spin.grpc.util.UserInfoValue> getGetUserInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserInfo",
      requestType = org.spin.grpc.util.LoginRequest.class,
      responseType = org.spin.grpc.util.UserInfoValue.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.UserInfoValue> getGetUserInfoMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.UserInfoValue> getGetUserInfoMethod;
    if ((getGetUserInfoMethod = AccessServiceGrpc.getGetUserInfoMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getGetUserInfoMethod = AccessServiceGrpc.getGetUserInfoMethod) == null) {
          AccessServiceGrpc.getGetUserInfoMethod = getGetUserInfoMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.UserInfoValue>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "GetUserInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoValue.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("GetUserInfo"))
                  .build();
          }
        }
     }
     return getGetUserInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.Session> getRunLoginMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RunLogin",
      requestType = org.spin.grpc.util.LoginRequest.class,
      responseType = org.spin.grpc.util.Session.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.Session> getRunLoginMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.Session> getRunLoginMethod;
    if ((getRunLoginMethod = AccessServiceGrpc.getRunLoginMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRunLoginMethod = AccessServiceGrpc.getRunLoginMethod) == null) {
          AccessServiceGrpc.getRunLoginMethod = getRunLoginMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.Session>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RunLogin"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Session.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RunLogin"))
                  .build();
          }
        }
     }
     return getRunLoginMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.Session> getRunLoginDefaultMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RunLoginDefault",
      requestType = org.spin.grpc.util.LoginRequest.class,
      responseType = org.spin.grpc.util.Session.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest,
      org.spin.grpc.util.Session> getRunLoginDefaultMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.Session> getRunLoginDefaultMethod;
    if ((getRunLoginDefaultMethod = AccessServiceGrpc.getRunLoginDefaultMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRunLoginDefaultMethod = AccessServiceGrpc.getRunLoginDefaultMethod) == null) {
          AccessServiceGrpc.getRunLoginDefaultMethod = getRunLoginDefaultMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.LoginRequest, org.spin.grpc.util.Session>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RunLoginDefault"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Session.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RunLoginDefault"))
                  .build();
          }
        }
     }
     return getRunLoginDefaultMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.LogoutRequest,
      org.spin.grpc.util.Session> getRunLogoutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RunLogout",
      requestType = org.spin.grpc.util.LogoutRequest.class,
      responseType = org.spin.grpc.util.Session.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.LogoutRequest,
      org.spin.grpc.util.Session> getRunLogoutMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.LogoutRequest, org.spin.grpc.util.Session> getRunLogoutMethod;
    if ((getRunLogoutMethod = AccessServiceGrpc.getRunLogoutMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRunLogoutMethod = AccessServiceGrpc.getRunLogoutMethod) == null) {
          AccessServiceGrpc.getRunLogoutMethod = getRunLogoutMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.LogoutRequest, org.spin.grpc.util.Session>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RunLogout"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.LogoutRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Session.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RunLogout"))
                  .build();
          }
        }
     }
     return getRunLogoutMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.UserInfoValue> getGetUserInfoFromSessionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserInfoFromSession",
      requestType = org.spin.grpc.util.UserInfoRequest.class,
      responseType = org.spin.grpc.util.UserInfoValue.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.UserInfoValue> getGetUserInfoFromSessionMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.UserInfoValue> getGetUserInfoFromSessionMethod;
    if ((getGetUserInfoFromSessionMethod = AccessServiceGrpc.getGetUserInfoFromSessionMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getGetUserInfoFromSessionMethod = AccessServiceGrpc.getGetUserInfoFromSessionMethod) == null) {
          AccessServiceGrpc.getGetUserInfoFromSessionMethod = getGetUserInfoFromSessionMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.UserInfoValue>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "GetUserInfoFromSession"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoValue.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("GetUserInfoFromSession"))
                  .build();
          }
        }
     }
     return getGetUserInfoFromSessionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.Menu> getGetMenuAndChildMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMenuAndChild",
      requestType = org.spin.grpc.util.UserInfoRequest.class,
      responseType = org.spin.grpc.util.Menu.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.Menu> getGetMenuAndChildMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.Menu> getGetMenuAndChildMethod;
    if ((getGetMenuAndChildMethod = AccessServiceGrpc.getGetMenuAndChildMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getGetMenuAndChildMethod = AccessServiceGrpc.getGetMenuAndChildMethod) == null) {
          AccessServiceGrpc.getGetMenuAndChildMethod = getGetMenuAndChildMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.Menu>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "GetMenuAndChild"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Menu.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("GetMenuAndChild"))
                  .build();
          }
        }
     }
     return getGetMenuAndChildMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.Session> getRunChangeRoleMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RunChangeRole",
      requestType = org.spin.grpc.util.UserInfoRequest.class,
      responseType = org.spin.grpc.util.Session.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest,
      org.spin.grpc.util.Session> getRunChangeRoleMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.Session> getRunChangeRoleMethod;
    if ((getRunChangeRoleMethod = AccessServiceGrpc.getRunChangeRoleMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRunChangeRoleMethod = AccessServiceGrpc.getRunChangeRoleMethod) == null) {
          AccessServiceGrpc.getRunChangeRoleMethod = getRunChangeRoleMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.UserInfoRequest, org.spin.grpc.util.Session>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RunChangeRole"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Session.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RunChangeRole"))
                  .build();
          }
        }
     }
     return getRunChangeRoleMethod;
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
    public void getUserInfo(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue> responseObserver) {
      asyncUnimplementedUnaryCall(getGetUserInfoMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request login from user
     * </pre>
     */
    public void runLogin(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnimplementedUnaryCall(getRunLoginMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request login and role
     * </pre>
     */
    public void runLoginDefault(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnimplementedUnaryCall(getRunLoginDefaultMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public void runLogout(org.spin.grpc.util.LogoutRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnimplementedUnaryCall(getRunLogoutMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request user roles from Session
     * </pre>
     */
    public void getUserInfoFromSession(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue> responseObserver) {
      asyncUnimplementedUnaryCall(getGetUserInfoFromSessionMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request Menu from Parent UUID
     * </pre>
     */
    public void getMenuAndChild(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Menu> responseObserver) {
      asyncUnimplementedUnaryCall(getGetMenuAndChildMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request change role
     * </pre>
     */
    public void runChangeRole(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnimplementedUnaryCall(getRunChangeRoleMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetUserInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.LoginRequest,
                org.spin.grpc.util.UserInfoValue>(
                  this, METHODID_GET_USER_INFO)))
          .addMethod(
            getRunLoginMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.LoginRequest,
                org.spin.grpc.util.Session>(
                  this, METHODID_RUN_LOGIN)))
          .addMethod(
            getRunLoginDefaultMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.LoginRequest,
                org.spin.grpc.util.Session>(
                  this, METHODID_RUN_LOGIN_DEFAULT)))
          .addMethod(
            getRunLogoutMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.LogoutRequest,
                org.spin.grpc.util.Session>(
                  this, METHODID_RUN_LOGOUT)))
          .addMethod(
            getGetUserInfoFromSessionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.UserInfoRequest,
                org.spin.grpc.util.UserInfoValue>(
                  this, METHODID_GET_USER_INFO_FROM_SESSION)))
          .addMethod(
            getGetMenuAndChildMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.UserInfoRequest,
                org.spin.grpc.util.Menu>(
                  this, METHODID_GET_MENU_AND_CHILD)))
          .addMethod(
            getRunChangeRoleMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.UserInfoRequest,
                org.spin.grpc.util.Session>(
                  this, METHODID_RUN_CHANGE_ROLE)))
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
    public void getUserInfo(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetUserInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request login from user
     * </pre>
     */
    public void runLogin(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRunLoginMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request login and role
     * </pre>
     */
    public void runLoginDefault(org.spin.grpc.util.LoginRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRunLoginDefaultMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public void runLogout(org.spin.grpc.util.LogoutRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRunLogoutMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request user roles from Session
     * </pre>
     */
    public void getUserInfoFromSession(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetUserInfoFromSessionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request Menu from Parent UUID
     * </pre>
     */
    public void getMenuAndChild(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Menu> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetMenuAndChildMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request change role
     * </pre>
     */
    public void runChangeRole(org.spin.grpc.util.UserInfoRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Session> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRunChangeRoleMethod(), getCallOptions()), request, responseObserver);
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
    public org.spin.grpc.util.UserInfoValue getUserInfo(org.spin.grpc.util.LoginRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetUserInfoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request login from user
     * </pre>
     */
    public org.spin.grpc.util.Session runLogin(org.spin.grpc.util.LoginRequest request) {
      return blockingUnaryCall(
          getChannel(), getRunLoginMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request login and role
     * </pre>
     */
    public org.spin.grpc.util.Session runLoginDefault(org.spin.grpc.util.LoginRequest request) {
      return blockingUnaryCall(
          getChannel(), getRunLoginDefaultMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public org.spin.grpc.util.Session runLogout(org.spin.grpc.util.LogoutRequest request) {
      return blockingUnaryCall(
          getChannel(), getRunLogoutMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request user roles from Session
     * </pre>
     */
    public org.spin.grpc.util.UserInfoValue getUserInfoFromSession(org.spin.grpc.util.UserInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetUserInfoFromSessionMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request Menu from Parent UUID
     * </pre>
     */
    public org.spin.grpc.util.Menu getMenuAndChild(org.spin.grpc.util.UserInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetMenuAndChildMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request change role
     * </pre>
     */
    public org.spin.grpc.util.Session runChangeRole(org.spin.grpc.util.UserInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getRunChangeRoleMethod(), getCallOptions(), request);
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
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.UserInfoValue> getUserInfo(
        org.spin.grpc.util.LoginRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetUserInfoMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request login from user
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Session> runLogin(
        org.spin.grpc.util.LoginRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRunLoginMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request login and role
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Session> runLoginDefault(
        org.spin.grpc.util.LoginRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRunLoginDefaultMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Session> runLogout(
        org.spin.grpc.util.LogoutRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRunLogoutMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request user roles from Session
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.UserInfoValue> getUserInfoFromSession(
        org.spin.grpc.util.UserInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetUserInfoFromSessionMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request Menu from Parent UUID
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Menu> getMenuAndChild(
        org.spin.grpc.util.UserInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetMenuAndChildMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request change role
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Session> runChangeRole(
        org.spin.grpc.util.UserInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRunChangeRoleMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_USER_INFO = 0;
  private static final int METHODID_RUN_LOGIN = 1;
  private static final int METHODID_RUN_LOGIN_DEFAULT = 2;
  private static final int METHODID_RUN_LOGOUT = 3;
  private static final int METHODID_GET_USER_INFO_FROM_SESSION = 4;
  private static final int METHODID_GET_MENU_AND_CHILD = 5;
  private static final int METHODID_RUN_CHANGE_ROLE = 6;

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
        case METHODID_GET_USER_INFO:
          serviceImpl.getUserInfo((org.spin.grpc.util.LoginRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue>) responseObserver);
          break;
        case METHODID_RUN_LOGIN:
          serviceImpl.runLogin((org.spin.grpc.util.LoginRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Session>) responseObserver);
          break;
        case METHODID_RUN_LOGIN_DEFAULT:
          serviceImpl.runLoginDefault((org.spin.grpc.util.LoginRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Session>) responseObserver);
          break;
        case METHODID_RUN_LOGOUT:
          serviceImpl.runLogout((org.spin.grpc.util.LogoutRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Session>) responseObserver);
          break;
        case METHODID_GET_USER_INFO_FROM_SESSION:
          serviceImpl.getUserInfoFromSession((org.spin.grpc.util.UserInfoRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.UserInfoValue>) responseObserver);
          break;
        case METHODID_GET_MENU_AND_CHILD:
          serviceImpl.getMenuAndChild((org.spin.grpc.util.UserInfoRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Menu>) responseObserver);
          break;
        case METHODID_RUN_CHANGE_ROLE:
          serviceImpl.runChangeRole((org.spin.grpc.util.UserInfoRequest) request,
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
              .addMethod(getGetUserInfoMethod())
              .addMethod(getRunLoginMethod())
              .addMethod(getRunLoginDefaultMethod())
              .addMethod(getRunLogoutMethod())
              .addMethod(getGetUserInfoFromSessionMethod())
              .addMethod(getGetMenuAndChildMethod())
              .addMethod(getRunChangeRoleMethod())
              .build();
        }
      }
    }
    return result;
  }
}

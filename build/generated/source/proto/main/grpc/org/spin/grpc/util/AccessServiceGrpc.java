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
  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.UserRequest,
      org.spin.grpc.util.UserRoles> getRequestUserRolesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestUserRoles",
      requestType = org.spin.grpc.util.UserRequest.class,
      responseType = org.spin.grpc.util.UserRoles.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.UserRequest,
      org.spin.grpc.util.UserRoles> getRequestUserRolesMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.UserRequest, org.spin.grpc.util.UserRoles> getRequestUserRolesMethod;
    if ((getRequestUserRolesMethod = AccessServiceGrpc.getRequestUserRolesMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRequestUserRolesMethod = AccessServiceGrpc.getRequestUserRolesMethod) == null) {
          AccessServiceGrpc.getRequestUserRolesMethod = getRequestUserRolesMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.UserRequest, org.spin.grpc.util.UserRoles>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RequestUserRoles"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserRoles.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RequestUserRoles"))
                  .build();
          }
        }
     }
     return getRequestUserRolesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.UserRequest,
      org.spin.grpc.util.Role> getRequestRoleMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestRole",
      requestType = org.spin.grpc.util.UserRequest.class,
      responseType = org.spin.grpc.util.Role.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.UserRequest,
      org.spin.grpc.util.Role> getRequestRoleMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.UserRequest, org.spin.grpc.util.Role> getRequestRoleMethod;
    if ((getRequestRoleMethod = AccessServiceGrpc.getRequestRoleMethod) == null) {
      synchronized (AccessServiceGrpc.class) {
        if ((getRequestRoleMethod = AccessServiceGrpc.getRequestRoleMethod) == null) {
          AccessServiceGrpc.getRequestRoleMethod = getRequestRoleMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.UserRequest, org.spin.grpc.util.Role>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "access.AccessService", "RequestRole"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Role.getDefaultInstance()))
                  .setSchemaDescriptor(new AccessServiceMethodDescriptorSupplier("RequestRole"))
                  .build();
          }
        }
     }
     return getRequestRoleMethod;
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
    public void requestUserRoles(org.spin.grpc.util.UserRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserRoles> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestUserRolesMethod(), responseObserver);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public void requestRole(org.spin.grpc.util.UserRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Role> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestRoleMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestUserRolesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.UserRequest,
                org.spin.grpc.util.UserRoles>(
                  this, METHODID_REQUEST_USER_ROLES)))
          .addMethod(
            getRequestRoleMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.UserRequest,
                org.spin.grpc.util.Role>(
                  this, METHODID_REQUEST_ROLE)))
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
    public void requestUserRoles(org.spin.grpc.util.UserRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.UserRoles> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestUserRolesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public void requestRole(org.spin.grpc.util.UserRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Role> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestRoleMethod(), getCallOptions()), request, responseObserver);
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
    public org.spin.grpc.util.UserRoles requestUserRoles(org.spin.grpc.util.UserRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestUserRolesMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public org.spin.grpc.util.Role requestRole(org.spin.grpc.util.UserRequest request) {
      return blockingUnaryCall(
          getChannel(), getRequestRoleMethod(), getCallOptions(), request);
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
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.UserRoles> requestUserRoles(
        org.spin.grpc.util.UserRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestUserRolesMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Request a Role from uuid
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Role> requestRole(
        org.spin.grpc.util.UserRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestRoleMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_USER_ROLES = 0;
  private static final int METHODID_REQUEST_ROLE = 1;

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
        case METHODID_REQUEST_USER_ROLES:
          serviceImpl.requestUserRoles((org.spin.grpc.util.UserRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.UserRoles>) responseObserver);
          break;
        case METHODID_REQUEST_ROLE:
          serviceImpl.requestRole((org.spin.grpc.util.UserRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Role>) responseObserver);
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
              .addMethod(getRequestUserRolesMethod())
              .addMethod(getRequestRoleMethod())
              .build();
        }
      }
    }
    return result;
  }
}

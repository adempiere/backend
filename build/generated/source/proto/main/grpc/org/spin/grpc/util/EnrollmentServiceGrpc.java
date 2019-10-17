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
    comments = "Source: enrollment.proto")
public final class EnrollmentServiceGrpc {

  private EnrollmentServiceGrpc() {}

  public static final String SERVICE_NAME = "enrollment.EnrollmentService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.EnrollUserRequest,
      org.spin.grpc.util.User> getEnrollUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "EnrollUser",
      requestType = org.spin.grpc.util.EnrollUserRequest.class,
      responseType = org.spin.grpc.util.User.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.EnrollUserRequest,
      org.spin.grpc.util.User> getEnrollUserMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.EnrollUserRequest, org.spin.grpc.util.User> getEnrollUserMethod;
    if ((getEnrollUserMethod = EnrollmentServiceGrpc.getEnrollUserMethod) == null) {
      synchronized (EnrollmentServiceGrpc.class) {
        if ((getEnrollUserMethod = EnrollmentServiceGrpc.getEnrollUserMethod) == null) {
          EnrollmentServiceGrpc.getEnrollUserMethod = getEnrollUserMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.EnrollUserRequest, org.spin.grpc.util.User>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "enrollment.EnrollmentService", "EnrollUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.EnrollUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.User.getDefaultInstance()))
                  .setSchemaDescriptor(new EnrollmentServiceMethodDescriptorSupplier("EnrollUser"))
                  .build();
          }
        }
     }
     return getEnrollUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ResetPasswordRequest,
      org.spin.grpc.util.ResetPasswordResponse> getResetPasswordMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ResetPassword",
      requestType = org.spin.grpc.util.ResetPasswordRequest.class,
      responseType = org.spin.grpc.util.ResetPasswordResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ResetPasswordRequest,
      org.spin.grpc.util.ResetPasswordResponse> getResetPasswordMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ResetPasswordRequest, org.spin.grpc.util.ResetPasswordResponse> getResetPasswordMethod;
    if ((getResetPasswordMethod = EnrollmentServiceGrpc.getResetPasswordMethod) == null) {
      synchronized (EnrollmentServiceGrpc.class) {
        if ((getResetPasswordMethod = EnrollmentServiceGrpc.getResetPasswordMethod) == null) {
          EnrollmentServiceGrpc.getResetPasswordMethod = getResetPasswordMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ResetPasswordRequest, org.spin.grpc.util.ResetPasswordResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "enrollment.EnrollmentService", "ResetPassword"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ResetPasswordRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ResetPasswordResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new EnrollmentServiceMethodDescriptorSupplier("ResetPassword"))
                  .build();
          }
        }
     }
     return getResetPasswordMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static EnrollmentServiceStub newStub(io.grpc.Channel channel) {
    return new EnrollmentServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static EnrollmentServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new EnrollmentServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static EnrollmentServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new EnrollmentServiceFutureStub(channel);
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static abstract class EnrollmentServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Request enroll User
     * </pre>
     */
    public void enrollUser(org.spin.grpc.util.EnrollUserRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.User> responseObserver) {
      asyncUnimplementedUnaryCall(getEnrollUserMethod(), responseObserver);
    }

    /**
     * <pre>
     * 
     * </pre>
     */
    public void resetPassword(org.spin.grpc.util.ResetPasswordRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ResetPasswordResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getResetPasswordMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getEnrollUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.EnrollUserRequest,
                org.spin.grpc.util.User>(
                  this, METHODID_ENROLL_USER)))
          .addMethod(
            getResetPasswordMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ResetPasswordRequest,
                org.spin.grpc.util.ResetPasswordResponse>(
                  this, METHODID_RESET_PASSWORD)))
          .build();
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class EnrollmentServiceStub extends io.grpc.stub.AbstractStub<EnrollmentServiceStub> {
    private EnrollmentServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private EnrollmentServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EnrollmentServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new EnrollmentServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request enroll User
     * </pre>
     */
    public void enrollUser(org.spin.grpc.util.EnrollUserRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.User> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getEnrollUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 
     * </pre>
     */
    public void resetPassword(org.spin.grpc.util.ResetPasswordRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ResetPasswordResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getResetPasswordMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class EnrollmentServiceBlockingStub extends io.grpc.stub.AbstractStub<EnrollmentServiceBlockingStub> {
    private EnrollmentServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private EnrollmentServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EnrollmentServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new EnrollmentServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request enroll User
     * </pre>
     */
    public org.spin.grpc.util.User enrollUser(org.spin.grpc.util.EnrollUserRequest request) {
      return blockingUnaryCall(
          getChannel(), getEnrollUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 
     * </pre>
     */
    public org.spin.grpc.util.ResetPasswordResponse resetPassword(org.spin.grpc.util.ResetPasswordRequest request) {
      return blockingUnaryCall(
          getChannel(), getResetPasswordMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class EnrollmentServiceFutureStub extends io.grpc.stub.AbstractStub<EnrollmentServiceFutureStub> {
    private EnrollmentServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private EnrollmentServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EnrollmentServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new EnrollmentServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Request enroll User
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.User> enrollUser(
        org.spin.grpc.util.EnrollUserRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getEnrollUserMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ResetPasswordResponse> resetPassword(
        org.spin.grpc.util.ResetPasswordRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getResetPasswordMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ENROLL_USER = 0;
  private static final int METHODID_RESET_PASSWORD = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final EnrollmentServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(EnrollmentServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ENROLL_USER:
          serviceImpl.enrollUser((org.spin.grpc.util.EnrollUserRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.User>) responseObserver);
          break;
        case METHODID_RESET_PASSWORD:
          serviceImpl.resetPassword((org.spin.grpc.util.ResetPasswordRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ResetPasswordResponse>) responseObserver);
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

  private static abstract class EnrollmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    EnrollmentServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.spin.grpc.util.Enrollment.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("EnrollmentService");
    }
  }

  private static final class EnrollmentServiceFileDescriptorSupplier
      extends EnrollmentServiceBaseDescriptorSupplier {
    EnrollmentServiceFileDescriptorSupplier() {}
  }

  private static final class EnrollmentServiceMethodDescriptorSupplier
      extends EnrollmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    EnrollmentServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (EnrollmentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new EnrollmentServiceFileDescriptorSupplier())
              .addMethod(getEnrollUserMethod())
              .addMethod(getResetPasswordMethod())
              .build();
        }
      }
    }
    return result;
  }
}

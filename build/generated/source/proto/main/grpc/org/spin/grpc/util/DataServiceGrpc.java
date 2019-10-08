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
    comments = "Source: businessdata.proto")
public final class DataServiceGrpc {

  private DataServiceGrpc() {}

  public static final String SERVICE_NAME = "data.DataService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.GetEntityRequest,
      org.spin.grpc.util.Entity> getGetEntityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetEntity",
      requestType = org.spin.grpc.util.GetEntityRequest.class,
      responseType = org.spin.grpc.util.Entity.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.GetEntityRequest,
      org.spin.grpc.util.Entity> getGetEntityMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.GetEntityRequest, org.spin.grpc.util.Entity> getGetEntityMethod;
    if ((getGetEntityMethod = DataServiceGrpc.getGetEntityMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getGetEntityMethod = DataServiceGrpc.getGetEntityMethod) == null) {
          DataServiceGrpc.getGetEntityMethod = getGetEntityMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.GetEntityRequest, org.spin.grpc.util.Entity>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "GetEntity"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.GetEntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Entity.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("GetEntity"))
                  .build();
          }
        }
     }
     return getGetEntityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.CreateEntityRequest,
      org.spin.grpc.util.Entity> getCreateEntityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateEntity",
      requestType = org.spin.grpc.util.CreateEntityRequest.class,
      responseType = org.spin.grpc.util.Entity.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.CreateEntityRequest,
      org.spin.grpc.util.Entity> getCreateEntityMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.CreateEntityRequest, org.spin.grpc.util.Entity> getCreateEntityMethod;
    if ((getCreateEntityMethod = DataServiceGrpc.getCreateEntityMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getCreateEntityMethod = DataServiceGrpc.getCreateEntityMethod) == null) {
          DataServiceGrpc.getCreateEntityMethod = getCreateEntityMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.CreateEntityRequest, org.spin.grpc.util.Entity>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "CreateEntity"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.CreateEntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Entity.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("CreateEntity"))
                  .build();
          }
        }
     }
     return getCreateEntityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.UpdateEntityRequest,
      org.spin.grpc.util.Entity> getUpdateEntityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateEntity",
      requestType = org.spin.grpc.util.UpdateEntityRequest.class,
      responseType = org.spin.grpc.util.Entity.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.UpdateEntityRequest,
      org.spin.grpc.util.Entity> getUpdateEntityMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.UpdateEntityRequest, org.spin.grpc.util.Entity> getUpdateEntityMethod;
    if ((getUpdateEntityMethod = DataServiceGrpc.getUpdateEntityMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getUpdateEntityMethod = DataServiceGrpc.getUpdateEntityMethod) == null) {
          DataServiceGrpc.getUpdateEntityMethod = getUpdateEntityMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.UpdateEntityRequest, org.spin.grpc.util.Entity>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "UpdateEntity"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.UpdateEntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Entity.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("UpdateEntity"))
                  .build();
          }
        }
     }
     return getUpdateEntityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.DeleteEntityRequest,
      org.spin.grpc.util.Empty> getDeleteEntityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteEntity",
      requestType = org.spin.grpc.util.DeleteEntityRequest.class,
      responseType = org.spin.grpc.util.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.DeleteEntityRequest,
      org.spin.grpc.util.Empty> getDeleteEntityMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.DeleteEntityRequest, org.spin.grpc.util.Empty> getDeleteEntityMethod;
    if ((getDeleteEntityMethod = DataServiceGrpc.getDeleteEntityMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getDeleteEntityMethod = DataServiceGrpc.getDeleteEntityMethod) == null) {
          DataServiceGrpc.getDeleteEntityMethod = getDeleteEntityMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.DeleteEntityRequest, org.spin.grpc.util.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "DeleteEntity"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.DeleteEntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("DeleteEntity"))
                  .build();
          }
        }
     }
     return getDeleteEntityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.RollbackEntityRequest,
      org.spin.grpc.util.Entity> getRollbackEntityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RollbackEntity",
      requestType = org.spin.grpc.util.RollbackEntityRequest.class,
      responseType = org.spin.grpc.util.Entity.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.RollbackEntityRequest,
      org.spin.grpc.util.Entity> getRollbackEntityMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.RollbackEntityRequest, org.spin.grpc.util.Entity> getRollbackEntityMethod;
    if ((getRollbackEntityMethod = DataServiceGrpc.getRollbackEntityMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRollbackEntityMethod = DataServiceGrpc.getRollbackEntityMethod) == null) {
          DataServiceGrpc.getRollbackEntityMethod = getRollbackEntityMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.RollbackEntityRequest, org.spin.grpc.util.Entity>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RollbackEntity"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.RollbackEntityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Entity.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RollbackEntity"))
                  .build();
          }
        }
     }
     return getRollbackEntityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ListEntitiesRequest,
      org.spin.grpc.util.ListEntitiesResponse> getListEntitiesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListEntities",
      requestType = org.spin.grpc.util.ListEntitiesRequest.class,
      responseType = org.spin.grpc.util.ListEntitiesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ListEntitiesRequest,
      org.spin.grpc.util.ListEntitiesResponse> getListEntitiesMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ListEntitiesRequest, org.spin.grpc.util.ListEntitiesResponse> getListEntitiesMethod;
    if ((getListEntitiesMethod = DataServiceGrpc.getListEntitiesMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getListEntitiesMethod = DataServiceGrpc.getListEntitiesMethod) == null) {
          DataServiceGrpc.getListEntitiesMethod = getListEntitiesMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ListEntitiesRequest, org.spin.grpc.util.ListEntitiesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "ListEntities"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListEntitiesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListEntitiesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("ListEntities"))
                  .build();
          }
        }
     }
     return getListEntitiesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.GetLookupItemRequest,
      org.spin.grpc.util.LookupItem> getGetLookupItemMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetLookupItem",
      requestType = org.spin.grpc.util.GetLookupItemRequest.class,
      responseType = org.spin.grpc.util.LookupItem.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.GetLookupItemRequest,
      org.spin.grpc.util.LookupItem> getGetLookupItemMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.GetLookupItemRequest, org.spin.grpc.util.LookupItem> getGetLookupItemMethod;
    if ((getGetLookupItemMethod = DataServiceGrpc.getGetLookupItemMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getGetLookupItemMethod = DataServiceGrpc.getGetLookupItemMethod) == null) {
          DataServiceGrpc.getGetLookupItemMethod = getGetLookupItemMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.GetLookupItemRequest, org.spin.grpc.util.LookupItem>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "GetLookupItem"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.GetLookupItemRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.LookupItem.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("GetLookupItem"))
                  .build();
          }
        }
     }
     return getGetLookupItemMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ListLookupItemsRequest,
      org.spin.grpc.util.ListLookupItemsResponse> getListLookupItemsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListLookupItems",
      requestType = org.spin.grpc.util.ListLookupItemsRequest.class,
      responseType = org.spin.grpc.util.ListLookupItemsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ListLookupItemsRequest,
      org.spin.grpc.util.ListLookupItemsResponse> getListLookupItemsMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ListLookupItemsRequest, org.spin.grpc.util.ListLookupItemsResponse> getListLookupItemsMethod;
    if ((getListLookupItemsMethod = DataServiceGrpc.getListLookupItemsMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getListLookupItemsMethod = DataServiceGrpc.getListLookupItemsMethod) == null) {
          DataServiceGrpc.getListLookupItemsMethod = getListLookupItemsMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ListLookupItemsRequest, org.spin.grpc.util.ListLookupItemsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "ListLookupItems"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListLookupItemsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListLookupItemsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("ListLookupItems"))
                  .build();
          }
        }
     }
     return getListLookupItemsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.RunCalloutRequest,
      org.spin.grpc.util.Callout> getRunCalloutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RunCallout",
      requestType = org.spin.grpc.util.RunCalloutRequest.class,
      responseType = org.spin.grpc.util.Callout.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.RunCalloutRequest,
      org.spin.grpc.util.Callout> getRunCalloutMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.RunCalloutRequest, org.spin.grpc.util.Callout> getRunCalloutMethod;
    if ((getRunCalloutMethod = DataServiceGrpc.getRunCalloutMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRunCalloutMethod = DataServiceGrpc.getRunCalloutMethod) == null) {
          DataServiceGrpc.getRunCalloutMethod = getRunCalloutMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.RunCalloutRequest, org.spin.grpc.util.Callout>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RunCallout"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.RunCalloutRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.Callout.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RunCallout"))
                  .build();
          }
        }
     }
     return getRunCalloutMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.RunBusinessProcessRequest,
      org.spin.grpc.util.BusinessProcess> getRunBusinessProcessMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RunBusinessProcess",
      requestType = org.spin.grpc.util.RunBusinessProcessRequest.class,
      responseType = org.spin.grpc.util.BusinessProcess.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.RunBusinessProcessRequest,
      org.spin.grpc.util.BusinessProcess> getRunBusinessProcessMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.RunBusinessProcessRequest, org.spin.grpc.util.BusinessProcess> getRunBusinessProcessMethod;
    if ((getRunBusinessProcessMethod = DataServiceGrpc.getRunBusinessProcessMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getRunBusinessProcessMethod = DataServiceGrpc.getRunBusinessProcessMethod) == null) {
          DataServiceGrpc.getRunBusinessProcessMethod = getRunBusinessProcessMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.RunBusinessProcessRequest, org.spin.grpc.util.BusinessProcess>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "RunBusinessProcess"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.RunBusinessProcessRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.BusinessProcess.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("RunBusinessProcess"))
                  .build();
          }
        }
     }
     return getRunBusinessProcessMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ListBrowserItemsRequest,
      org.spin.grpc.util.ListBrowserItemsResponse> getListBrowserItemsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListBrowserItems",
      requestType = org.spin.grpc.util.ListBrowserItemsRequest.class,
      responseType = org.spin.grpc.util.ListBrowserItemsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ListBrowserItemsRequest,
      org.spin.grpc.util.ListBrowserItemsResponse> getListBrowserItemsMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ListBrowserItemsRequest, org.spin.grpc.util.ListBrowserItemsResponse> getListBrowserItemsMethod;
    if ((getListBrowserItemsMethod = DataServiceGrpc.getListBrowserItemsMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getListBrowserItemsMethod = DataServiceGrpc.getListBrowserItemsMethod) == null) {
          DataServiceGrpc.getListBrowserItemsMethod = getListBrowserItemsMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ListBrowserItemsRequest, org.spin.grpc.util.ListBrowserItemsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "ListBrowserItems"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListBrowserItemsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListBrowserItemsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("ListBrowserItems"))
                  .build();
          }
        }
     }
     return getListBrowserItemsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ListActivitiesRequest,
      org.spin.grpc.util.ListActivitiesResponse> getListActivitiesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListActivities",
      requestType = org.spin.grpc.util.ListActivitiesRequest.class,
      responseType = org.spin.grpc.util.ListActivitiesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ListActivitiesRequest,
      org.spin.grpc.util.ListActivitiesResponse> getListActivitiesMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ListActivitiesRequest, org.spin.grpc.util.ListActivitiesResponse> getListActivitiesMethod;
    if ((getListActivitiesMethod = DataServiceGrpc.getListActivitiesMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getListActivitiesMethod = DataServiceGrpc.getListActivitiesMethod) == null) {
          DataServiceGrpc.getListActivitiesMethod = getListActivitiesMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ListActivitiesRequest, org.spin.grpc.util.ListActivitiesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "ListActivities"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListActivitiesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListActivitiesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("ListActivities"))
                  .build();
          }
        }
     }
     return getListActivitiesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ListRecentItemsRequest,
      org.spin.grpc.util.ListRecentItemsResponse> getListRecentItemsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListRecentItems",
      requestType = org.spin.grpc.util.ListRecentItemsRequest.class,
      responseType = org.spin.grpc.util.ListRecentItemsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ListRecentItemsRequest,
      org.spin.grpc.util.ListRecentItemsResponse> getListRecentItemsMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ListRecentItemsRequest, org.spin.grpc.util.ListRecentItemsResponse> getListRecentItemsMethod;
    if ((getListRecentItemsMethod = DataServiceGrpc.getListRecentItemsMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getListRecentItemsMethod = DataServiceGrpc.getListRecentItemsMethod) == null) {
          DataServiceGrpc.getListRecentItemsMethod = getListRecentItemsMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ListRecentItemsRequest, org.spin.grpc.util.ListRecentItemsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "ListRecentItems"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListRecentItemsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListRecentItemsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("ListRecentItems"))
                  .build();
          }
        }
     }
     return getListRecentItemsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.spin.grpc.util.ListReferencesRequest,
      org.spin.grpc.util.ListReferencesResponse> getListReferencesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListReferences",
      requestType = org.spin.grpc.util.ListReferencesRequest.class,
      responseType = org.spin.grpc.util.ListReferencesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.spin.grpc.util.ListReferencesRequest,
      org.spin.grpc.util.ListReferencesResponse> getListReferencesMethod() {
    io.grpc.MethodDescriptor<org.spin.grpc.util.ListReferencesRequest, org.spin.grpc.util.ListReferencesResponse> getListReferencesMethod;
    if ((getListReferencesMethod = DataServiceGrpc.getListReferencesMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getListReferencesMethod = DataServiceGrpc.getListReferencesMethod) == null) {
          DataServiceGrpc.getListReferencesMethod = getListReferencesMethod = 
              io.grpc.MethodDescriptor.<org.spin.grpc.util.ListReferencesRequest, org.spin.grpc.util.ListReferencesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data.DataService", "ListReferences"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListReferencesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.spin.grpc.util.ListReferencesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("ListReferences"))
                  .build();
          }
        }
     }
     return getListReferencesMethod;
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
     * Get a Entity
     * </pre>
     */
    public void getEntity(org.spin.grpc.util.GetEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity> responseObserver) {
      asyncUnimplementedUnaryCall(getGetEntityMethod(), responseObserver);
    }

    /**
     * <pre>
     * Create Entity Request
     * </pre>
     */
    public void createEntity(org.spin.grpc.util.CreateEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateEntityMethod(), responseObserver);
    }

    /**
     * <pre>
     * Update Entity Request
     * </pre>
     */
    public void updateEntity(org.spin.grpc.util.UpdateEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateEntityMethod(), responseObserver);
    }

    /**
     * <pre>
     * Delete Entity Request
     * </pre>
     */
    public void deleteEntity(org.spin.grpc.util.DeleteEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteEntityMethod(), responseObserver);
    }

    /**
     * <pre>
     * Rollback Entity Request
     * </pre>
     */
    public void rollbackEntity(org.spin.grpc.util.RollbackEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity> responseObserver) {
      asyncUnimplementedUnaryCall(getRollbackEntityMethod(), responseObserver);
    }

    /**
     * <pre>
     * List a Entities
     * </pre>
     */
    public void listEntities(org.spin.grpc.util.ListEntitiesRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListEntitiesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListEntitiesMethod(), responseObserver);
    }

    /**
     * <pre>
     * Get Lookup Item
     * </pre>
     */
    public void getLookupItem(org.spin.grpc.util.GetLookupItemRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.LookupItem> responseObserver) {
      asyncUnimplementedUnaryCall(getGetLookupItemMethod(), responseObserver);
    }

    /**
     * <pre>
     * List Lookup Item
     * </pre>
     */
    public void listLookupItems(org.spin.grpc.util.ListLookupItemsRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListLookupItemsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListLookupItemsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Run a Callout
     * </pre>
     */
    public void runCallout(org.spin.grpc.util.RunCalloutRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Callout> responseObserver) {
      asyncUnimplementedUnaryCall(getRunCalloutMethod(), responseObserver);
    }

    /**
     * <pre>
     *	Request a BusinessProcess / Report
     * </pre>
     */
    public void runBusinessProcess(org.spin.grpc.util.RunBusinessProcessRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.BusinessProcess> responseObserver) {
      asyncUnimplementedUnaryCall(getRunBusinessProcessMethod(), responseObserver);
    }

    /**
     * <pre>
     *	Request Browser Data
     * </pre>
     */
    public void listBrowserItems(org.spin.grpc.util.ListBrowserItemsRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListBrowserItemsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListBrowserItemsMethod(), responseObserver);
    }

    /**
     * <pre>
     *	Request BusinessProcess Activity from current session
     * </pre>
     */
    public void listActivities(org.spin.grpc.util.ListActivitiesRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListActivitiesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListActivitiesMethod(), responseObserver);
    }

    /**
     * <pre>
     *	Request Recent Items
     * </pre>
     */
    public void listRecentItems(org.spin.grpc.util.ListRecentItemsRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListRecentItemsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListRecentItemsMethod(), responseObserver);
    }

    /**
     * <pre>
     * List a References
     * </pre>
     */
    public void listReferences(org.spin.grpc.util.ListReferencesRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListReferencesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListReferencesMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetEntityMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.GetEntityRequest,
                org.spin.grpc.util.Entity>(
                  this, METHODID_GET_ENTITY)))
          .addMethod(
            getCreateEntityMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.CreateEntityRequest,
                org.spin.grpc.util.Entity>(
                  this, METHODID_CREATE_ENTITY)))
          .addMethod(
            getUpdateEntityMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.UpdateEntityRequest,
                org.spin.grpc.util.Entity>(
                  this, METHODID_UPDATE_ENTITY)))
          .addMethod(
            getDeleteEntityMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.DeleteEntityRequest,
                org.spin.grpc.util.Empty>(
                  this, METHODID_DELETE_ENTITY)))
          .addMethod(
            getRollbackEntityMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.RollbackEntityRequest,
                org.spin.grpc.util.Entity>(
                  this, METHODID_ROLLBACK_ENTITY)))
          .addMethod(
            getListEntitiesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ListEntitiesRequest,
                org.spin.grpc.util.ListEntitiesResponse>(
                  this, METHODID_LIST_ENTITIES)))
          .addMethod(
            getGetLookupItemMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.GetLookupItemRequest,
                org.spin.grpc.util.LookupItem>(
                  this, METHODID_GET_LOOKUP_ITEM)))
          .addMethod(
            getListLookupItemsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ListLookupItemsRequest,
                org.spin.grpc.util.ListLookupItemsResponse>(
                  this, METHODID_LIST_LOOKUP_ITEMS)))
          .addMethod(
            getRunCalloutMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.RunCalloutRequest,
                org.spin.grpc.util.Callout>(
                  this, METHODID_RUN_CALLOUT)))
          .addMethod(
            getRunBusinessProcessMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.RunBusinessProcessRequest,
                org.spin.grpc.util.BusinessProcess>(
                  this, METHODID_RUN_BUSINESS_PROCESS)))
          .addMethod(
            getListBrowserItemsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ListBrowserItemsRequest,
                org.spin.grpc.util.ListBrowserItemsResponse>(
                  this, METHODID_LIST_BROWSER_ITEMS)))
          .addMethod(
            getListActivitiesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ListActivitiesRequest,
                org.spin.grpc.util.ListActivitiesResponse>(
                  this, METHODID_LIST_ACTIVITIES)))
          .addMethod(
            getListRecentItemsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ListRecentItemsRequest,
                org.spin.grpc.util.ListRecentItemsResponse>(
                  this, METHODID_LIST_RECENT_ITEMS)))
          .addMethod(
            getListReferencesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.spin.grpc.util.ListReferencesRequest,
                org.spin.grpc.util.ListReferencesResponse>(
                  this, METHODID_LIST_REFERENCES)))
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
     * Get a Entity
     * </pre>
     */
    public void getEntity(org.spin.grpc.util.GetEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetEntityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Create Entity Request
     * </pre>
     */
    public void createEntity(org.spin.grpc.util.CreateEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateEntityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Update Entity Request
     * </pre>
     */
    public void updateEntity(org.spin.grpc.util.UpdateEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateEntityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Delete Entity Request
     * </pre>
     */
    public void deleteEntity(org.spin.grpc.util.DeleteEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteEntityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Rollback Entity Request
     * </pre>
     */
    public void rollbackEntity(org.spin.grpc.util.RollbackEntityRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRollbackEntityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * List a Entities
     * </pre>
     */
    public void listEntities(org.spin.grpc.util.ListEntitiesRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListEntitiesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListEntitiesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get Lookup Item
     * </pre>
     */
    public void getLookupItem(org.spin.grpc.util.GetLookupItemRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.LookupItem> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetLookupItemMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * List Lookup Item
     * </pre>
     */
    public void listLookupItems(org.spin.grpc.util.ListLookupItemsRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListLookupItemsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListLookupItemsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Run a Callout
     * </pre>
     */
    public void runCallout(org.spin.grpc.util.RunCalloutRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.Callout> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRunCalloutMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *	Request a BusinessProcess / Report
     * </pre>
     */
    public void runBusinessProcess(org.spin.grpc.util.RunBusinessProcessRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.BusinessProcess> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRunBusinessProcessMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *	Request Browser Data
     * </pre>
     */
    public void listBrowserItems(org.spin.grpc.util.ListBrowserItemsRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListBrowserItemsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListBrowserItemsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *	Request BusinessProcess Activity from current session
     * </pre>
     */
    public void listActivities(org.spin.grpc.util.ListActivitiesRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListActivitiesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListActivitiesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *	Request Recent Items
     * </pre>
     */
    public void listRecentItems(org.spin.grpc.util.ListRecentItemsRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListRecentItemsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListRecentItemsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * List a References
     * </pre>
     */
    public void listReferences(org.spin.grpc.util.ListReferencesRequest request,
        io.grpc.stub.StreamObserver<org.spin.grpc.util.ListReferencesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListReferencesMethod(), getCallOptions()), request, responseObserver);
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
     * Get a Entity
     * </pre>
     */
    public org.spin.grpc.util.Entity getEntity(org.spin.grpc.util.GetEntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetEntityMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Create Entity Request
     * </pre>
     */
    public org.spin.grpc.util.Entity createEntity(org.spin.grpc.util.CreateEntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateEntityMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Update Entity Request
     * </pre>
     */
    public org.spin.grpc.util.Entity updateEntity(org.spin.grpc.util.UpdateEntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateEntityMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Delete Entity Request
     * </pre>
     */
    public org.spin.grpc.util.Empty deleteEntity(org.spin.grpc.util.DeleteEntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getDeleteEntityMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Rollback Entity Request
     * </pre>
     */
    public org.spin.grpc.util.Entity rollbackEntity(org.spin.grpc.util.RollbackEntityRequest request) {
      return blockingUnaryCall(
          getChannel(), getRollbackEntityMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * List a Entities
     * </pre>
     */
    public org.spin.grpc.util.ListEntitiesResponse listEntities(org.spin.grpc.util.ListEntitiesRequest request) {
      return blockingUnaryCall(
          getChannel(), getListEntitiesMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Get Lookup Item
     * </pre>
     */
    public org.spin.grpc.util.LookupItem getLookupItem(org.spin.grpc.util.GetLookupItemRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetLookupItemMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * List Lookup Item
     * </pre>
     */
    public org.spin.grpc.util.ListLookupItemsResponse listLookupItems(org.spin.grpc.util.ListLookupItemsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListLookupItemsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Run a Callout
     * </pre>
     */
    public org.spin.grpc.util.Callout runCallout(org.spin.grpc.util.RunCalloutRequest request) {
      return blockingUnaryCall(
          getChannel(), getRunCalloutMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *	Request a BusinessProcess / Report
     * </pre>
     */
    public org.spin.grpc.util.BusinessProcess runBusinessProcess(org.spin.grpc.util.RunBusinessProcessRequest request) {
      return blockingUnaryCall(
          getChannel(), getRunBusinessProcessMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *	Request Browser Data
     * </pre>
     */
    public org.spin.grpc.util.ListBrowserItemsResponse listBrowserItems(org.spin.grpc.util.ListBrowserItemsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListBrowserItemsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *	Request BusinessProcess Activity from current session
     * </pre>
     */
    public org.spin.grpc.util.ListActivitiesResponse listActivities(org.spin.grpc.util.ListActivitiesRequest request) {
      return blockingUnaryCall(
          getChannel(), getListActivitiesMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *	Request Recent Items
     * </pre>
     */
    public org.spin.grpc.util.ListRecentItemsResponse listRecentItems(org.spin.grpc.util.ListRecentItemsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListRecentItemsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * List a References
     * </pre>
     */
    public org.spin.grpc.util.ListReferencesResponse listReferences(org.spin.grpc.util.ListReferencesRequest request) {
      return blockingUnaryCall(
          getChannel(), getListReferencesMethod(), getCallOptions(), request);
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
     * Get a Entity
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Entity> getEntity(
        org.spin.grpc.util.GetEntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetEntityMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Create Entity Request
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Entity> createEntity(
        org.spin.grpc.util.CreateEntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateEntityMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Update Entity Request
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Entity> updateEntity(
        org.spin.grpc.util.UpdateEntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateEntityMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Delete Entity Request
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Empty> deleteEntity(
        org.spin.grpc.util.DeleteEntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteEntityMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Rollback Entity Request
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Entity> rollbackEntity(
        org.spin.grpc.util.RollbackEntityRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRollbackEntityMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * List a Entities
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ListEntitiesResponse> listEntities(
        org.spin.grpc.util.ListEntitiesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListEntitiesMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Get Lookup Item
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.LookupItem> getLookupItem(
        org.spin.grpc.util.GetLookupItemRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetLookupItemMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * List Lookup Item
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ListLookupItemsResponse> listLookupItems(
        org.spin.grpc.util.ListLookupItemsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListLookupItemsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Run a Callout
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.Callout> runCallout(
        org.spin.grpc.util.RunCalloutRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRunCalloutMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *	Request a BusinessProcess / Report
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.BusinessProcess> runBusinessProcess(
        org.spin.grpc.util.RunBusinessProcessRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRunBusinessProcessMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *	Request Browser Data
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ListBrowserItemsResponse> listBrowserItems(
        org.spin.grpc.util.ListBrowserItemsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListBrowserItemsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *	Request BusinessProcess Activity from current session
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ListActivitiesResponse> listActivities(
        org.spin.grpc.util.ListActivitiesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListActivitiesMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *	Request Recent Items
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ListRecentItemsResponse> listRecentItems(
        org.spin.grpc.util.ListRecentItemsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListRecentItemsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * List a References
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.spin.grpc.util.ListReferencesResponse> listReferences(
        org.spin.grpc.util.ListReferencesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListReferencesMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_ENTITY = 0;
  private static final int METHODID_CREATE_ENTITY = 1;
  private static final int METHODID_UPDATE_ENTITY = 2;
  private static final int METHODID_DELETE_ENTITY = 3;
  private static final int METHODID_ROLLBACK_ENTITY = 4;
  private static final int METHODID_LIST_ENTITIES = 5;
  private static final int METHODID_GET_LOOKUP_ITEM = 6;
  private static final int METHODID_LIST_LOOKUP_ITEMS = 7;
  private static final int METHODID_RUN_CALLOUT = 8;
  private static final int METHODID_RUN_BUSINESS_PROCESS = 9;
  private static final int METHODID_LIST_BROWSER_ITEMS = 10;
  private static final int METHODID_LIST_ACTIVITIES = 11;
  private static final int METHODID_LIST_RECENT_ITEMS = 12;
  private static final int METHODID_LIST_REFERENCES = 13;

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
        case METHODID_GET_ENTITY:
          serviceImpl.getEntity((org.spin.grpc.util.GetEntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity>) responseObserver);
          break;
        case METHODID_CREATE_ENTITY:
          serviceImpl.createEntity((org.spin.grpc.util.CreateEntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity>) responseObserver);
          break;
        case METHODID_UPDATE_ENTITY:
          serviceImpl.updateEntity((org.spin.grpc.util.UpdateEntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity>) responseObserver);
          break;
        case METHODID_DELETE_ENTITY:
          serviceImpl.deleteEntity((org.spin.grpc.util.DeleteEntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Empty>) responseObserver);
          break;
        case METHODID_ROLLBACK_ENTITY:
          serviceImpl.rollbackEntity((org.spin.grpc.util.RollbackEntityRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Entity>) responseObserver);
          break;
        case METHODID_LIST_ENTITIES:
          serviceImpl.listEntities((org.spin.grpc.util.ListEntitiesRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ListEntitiesResponse>) responseObserver);
          break;
        case METHODID_GET_LOOKUP_ITEM:
          serviceImpl.getLookupItem((org.spin.grpc.util.GetLookupItemRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.LookupItem>) responseObserver);
          break;
        case METHODID_LIST_LOOKUP_ITEMS:
          serviceImpl.listLookupItems((org.spin.grpc.util.ListLookupItemsRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ListLookupItemsResponse>) responseObserver);
          break;
        case METHODID_RUN_CALLOUT:
          serviceImpl.runCallout((org.spin.grpc.util.RunCalloutRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.Callout>) responseObserver);
          break;
        case METHODID_RUN_BUSINESS_PROCESS:
          serviceImpl.runBusinessProcess((org.spin.grpc.util.RunBusinessProcessRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.BusinessProcess>) responseObserver);
          break;
        case METHODID_LIST_BROWSER_ITEMS:
          serviceImpl.listBrowserItems((org.spin.grpc.util.ListBrowserItemsRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ListBrowserItemsResponse>) responseObserver);
          break;
        case METHODID_LIST_ACTIVITIES:
          serviceImpl.listActivities((org.spin.grpc.util.ListActivitiesRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ListActivitiesResponse>) responseObserver);
          break;
        case METHODID_LIST_RECENT_ITEMS:
          serviceImpl.listRecentItems((org.spin.grpc.util.ListRecentItemsRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ListRecentItemsResponse>) responseObserver);
          break;
        case METHODID_LIST_REFERENCES:
          serviceImpl.listReferences((org.spin.grpc.util.ListReferencesRequest) request,
              (io.grpc.stub.StreamObserver<org.spin.grpc.util.ListReferencesResponse>) responseObserver);
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
              .addMethod(getGetEntityMethod())
              .addMethod(getCreateEntityMethod())
              .addMethod(getUpdateEntityMethod())
              .addMethod(getDeleteEntityMethod())
              .addMethod(getRollbackEntityMethod())
              .addMethod(getListEntitiesMethod())
              .addMethod(getGetLookupItemMethod())
              .addMethod(getListLookupItemsMethod())
              .addMethod(getRunCalloutMethod())
              .addMethod(getRunBusinessProcessMethod())
              .addMethod(getListBrowserItemsMethod())
              .addMethod(getListActivitiesMethod())
              .addMethod(getListRecentItemsMethod())
              .addMethod(getListReferencesMethod())
              .build();
        }
      }
    }
    return result;
  }
}

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: data.proto

package org.spin.grpc.util;

public final class ADempiereData {
  private ADempiereData() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_ClientRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_ClientRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_ValueObjectList_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_ValueObjectList_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_ValueObject_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_ValueObject_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_ValueObject_ValuesEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_ValueObject_ValuesEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_Value_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_Value_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_ValueObjectRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_ValueObjectRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_Criteria_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_Criteria_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_OrderByProperty_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_OrderByProperty_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_Condition_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_Condition_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_CalloutRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_CalloutRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_CalloutResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_CalloutResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\ndata.proto\022\004data\"6\n\rClientRequest\022\023\n\013s" +
      "essionUuid\030\001 \001(\t\022\020\n\010language\030\002 \001(\t\"J\n\017Va" +
      "lueObjectList\022\023\n\013recordCount\030\001 \001(\003\022\"\n\007re" +
      "cords\030\002 \003(\0132\021.data.ValueObject\"\245\001\n\013Value" +
      "Object\022\n\n\002id\030\001 \001(\005\022\014\n\004uuid\030\002 \001(\t\022\021\n\ttabl" +
      "eName\030\003 \001(\t\022-\n\006values\030\004 \003(\0132\035.data.Value" +
      "Object.ValuesEntry\032:\n\013ValuesEntry\022\013\n\003key" +
      "\030\001 \001(\t\022\032\n\005value\030\002 \001(\0132\013.data.Value:\0028\001\"\351" +
      "\001\n\005Value\022\020\n\010intValue\030\001 \001(\005\022\021\n\tlongValue\030" +
      "\002 \001(\003\022\023\n\013doubleValue\030\003 \001(\001\022\024\n\014booleanVal" +
      "ue\030\004 \001(\010\022\023\n\013stringValue\030\005 \001(\t\022(\n\tvalueTy" +
      "pe\030\006 \001(\0162\025.data.Value.ValueType\"Q\n\tValue" +
      "Type\022\013\n\007INTEGER\020\000\022\010\n\004LONG\020\001\022\n\n\006DOUBLE\020\002\022" +
      "\013\n\007BOOLEAN\020\003\022\n\n\006STRING\020\004\022\010\n\004DATE\020\005\"|\n\022Va" +
      "lueObjectRequest\022\n\n\002id\030\001 \001(\005\022\014\n\004uuid\030\002 \001" +
      "(\t\022*\n\rclientRequest\030\003 \001(\0132\023.data.ClientR" +
      "equest\022 \n\010criteria\030\004 \001(\0132\016.data.Criteria" +
      "\"\327\001\n\010Criteria\022\021\n\ttableName\030\001 \001(\t\022\r\n\005quer" +
      "y\030\002 \001(\t\022\023\n\013whereClause\030\003 \001(\t\022\025\n\rorderByC" +
      "lause\030\004 \001(\t\022#\n\nconditions\030\005 \003(\0132\017.data.C" +
      "ondition\022\033\n\006values\030\006 \003(\0132\013.data.Value\022,\n" +
      "\rorderByColumn\030\007 \003(\0132\025.data.OrderByPrope" +
      "rty\022\r\n\005limit\030\010 \001(\003\"\205\001\n\017OrderByProperty\022\022" +
      "\n\ncolumnName\030\001 \001(\t\0222\n\torderType\030\002 \001(\0162\037." +
      "data.OrderByProperty.OrderType\"*\n\tOrderT" +
      "ype\022\r\n\tASCENDING\020\000\022\016\n\nDESCENDING\020\001\"\300\002\n\tC" +
      "ondition\022\032\n\005value\030\001 \001(\0132\013.data.Value\022\034\n\007" +
      "valueTo\030\002 \001(\0132\013.data.Value\022\033\n\006values\030\003 \003" +
      "(\0132\013.data.Value\022*\n\010operator\030\004 \001(\0162\030.data" +
      ".Condition.Operator\"\257\001\n\010Operator\022\t\n\005EQUA" +
      "L\020\000\022\r\n\tNOT_EQUAL\020\001\022\010\n\004LIKE\020\002\022\014\n\010NOT_LIKE" +
      "\020\003\022\013\n\007GREATER\020\004\022\021\n\rGREATER_EQUAL\020\005\022\010\n\004LE" +
      "SS\020\006\022\016\n\nLESS_EQUAL\020\007\022\013\n\007BETWEEN\020\010\022\014\n\010NOT" +
      "_NULL\020\t\022\010\n\004NULL\020\n\022\006\n\002IN\020\013\022\n\n\006NOT_IN\020\014\"u\n" +
      "\016CalloutRequest\022*\n\rclientRequest\030\001 \001(\0132\023" +
      ".data.ClientRequest\022\017\n\007callout\030\002 \001(\t\022&\n\013" +
      "valueObject\030\003 \001(\0132\021.data.ValueObject\">\n\017" +
      "CalloutResponse\022\016\n\006result\030\001 \001(\t\022\033\n\006value" +
      "s\030\002 \003(\0132\013.data.Value2\336\002\n\013DataService\022>\n\r" +
      "RequestObject\022\030.data.ValueObjectRequest\032" +
      "\021.data.ValueObject\"\000\022F\n\021RequestObjectLis" +
      "t\022\030.data.ValueObjectRequest\032\025.data.Value" +
      "ObjectList\"\000\022>\n\rRequestLookup\022\030.data.Val" +
      "ueObjectRequest\032\021.data.ValueObject\"\000\022F\n\021" +
      "RequestLookupList\022\030.data.ValueObjectRequ" +
      "est\032\025.data.ValueObjectList\"\000\022?\n\016RequestC" +
      "allout\022\024.data.CalloutRequest\032\025.data.Call" +
      "outResponse\"\000B+\n\022org.spin.grpc.utilB\rADe" +
      "mpiereDataP\001\242\002\003HLWb\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_data_ClientRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_data_ClientRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_ClientRequest_descriptor,
        new java.lang.String[] { "SessionUuid", "Language", });
    internal_static_data_ValueObjectList_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_data_ValueObjectList_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_ValueObjectList_descriptor,
        new java.lang.String[] { "RecordCount", "Records", });
    internal_static_data_ValueObject_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_data_ValueObject_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_ValueObject_descriptor,
        new java.lang.String[] { "Id", "Uuid", "TableName", "Values", });
    internal_static_data_ValueObject_ValuesEntry_descriptor =
      internal_static_data_ValueObject_descriptor.getNestedTypes().get(0);
    internal_static_data_ValueObject_ValuesEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_ValueObject_ValuesEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_data_Value_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_data_Value_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_Value_descriptor,
        new java.lang.String[] { "IntValue", "LongValue", "DoubleValue", "BooleanValue", "StringValue", "ValueType", });
    internal_static_data_ValueObjectRequest_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_data_ValueObjectRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_ValueObjectRequest_descriptor,
        new java.lang.String[] { "Id", "Uuid", "ClientRequest", "Criteria", });
    internal_static_data_Criteria_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_data_Criteria_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_Criteria_descriptor,
        new java.lang.String[] { "TableName", "Query", "WhereClause", "OrderByClause", "Conditions", "Values", "OrderByColumn", "Limit", });
    internal_static_data_OrderByProperty_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_data_OrderByProperty_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_OrderByProperty_descriptor,
        new java.lang.String[] { "ColumnName", "OrderType", });
    internal_static_data_Condition_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_data_Condition_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_Condition_descriptor,
        new java.lang.String[] { "Value", "ValueTo", "Values", "Operator", });
    internal_static_data_CalloutRequest_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_data_CalloutRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_CalloutRequest_descriptor,
        new java.lang.String[] { "ClientRequest", "Callout", "ValueObject", });
    internal_static_data_CalloutResponse_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_data_CalloutResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_CalloutResponse_descriptor,
        new java.lang.String[] { "Result", "Values", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
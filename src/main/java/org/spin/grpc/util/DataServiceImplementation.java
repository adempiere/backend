/************************************************************************************
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                     *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program.	If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.grpc.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import javax.script.ScriptEngine;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.Callout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_AD_Element;
import org.compiere.model.I_AD_Session;
import org.compiere.model.MRole;
import org.compiere.model.MRule;
import org.compiere.model.MSession;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.grpc.util.DataServiceGrpc.DataServiceImplBase;
import org.spin.grpc.util.Value.ValueType;

import io.grpc.stub.StreamObserver;

public class DataServiceImplementation extends DataServiceImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(DataServiceImplementation.class);
	/**	Key column constant	*/
	private final String KEY_COLUMN_KEY = "KeyColumn";
	/**	Key column constant	*/
	private final String DISPLAY_COLUMN_KEY = "DisplayColumn";
	/**	Key column constant	*/
	private final String VALUE_COLUMN_KEY = "ValueColumn";
	/**	Session Context	*/
	private static CCache<String, Properties> sessionsContext = new CCache<String, Properties>("DataServiceImplementation", 30, 0);	//	no time-out
	
	@Override
	public void requestObject(ValueObjectRequest request, StreamObserver<ValueObject> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			ValueObject.Builder entityValue = convertObject(context, request);
			responseObserver.onNext(entityValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestCallout(CalloutRequest request, StreamObserver<CalloutResponse> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getCallout())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Callout Requested = " + request.getCallout());
			Properties context = getContext(request.getClientRequest());
			CalloutResponse.Builder calloutResponse = runcallout(context, request);
			responseObserver.onNext(calloutResponse.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestObjectList(ValueObjectRequest request, StreamObserver<ValueObjectList> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object List Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			ValueObjectList.Builder entutyValueList = convertObjectList(context, request);
			responseObserver.onNext(entutyValueList.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestLookup(ValueObjectRequest request, StreamObserver<ValueObject> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Lookup Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			ValueObject.Builder lookupValue = convertLookup(context, request);
			responseObserver.onNext(lookupValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestLookupList(ValueObjectRequest request, StreamObserver<ValueObjectList> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Lookup List Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			ValueObjectList.Builder entutyValueList = convertLookupList(context, request);
			responseObserver.onNext(entutyValueList.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	/**
	 * Convert a PO from query
	 * @param request
	 * @return
	 */
	private ValueObject.Builder convertObject(Properties context, ValueObjectRequest request) {
		Criteria criteria = request.getCriteria();
		StringBuffer whereClause = new StringBuffer();
		List<Object> params = new ArrayList<>();
		if(!Util.isEmpty(request.getUuid())) {
			whereClause.append(I_AD_Element.COLUMNNAME_UUID + " = ?");
			params.add(request.getUuid());
		} else if(!Util.isEmpty(criteria.getWhereClause())) {
			whereClause.append("(").append(criteria.getWhereClause()).append(")");
		}
		PO entity = new Query(context, request.getCriteria().getTableName(), whereClause.toString(), null)
				.setParameters(params)
				.first();
		//	Return
		return convertObject(context, entity);
	}
	
	/**
	 * Get context from session
	 * @param request
	 * @return
	 */
	private Properties getContext(ClientRequest request) {
		Properties context = sessionsContext.get(request.getSessionUuid());
		if(context != null) {
			return context;
		}
		context = Env.getCtx();
		DB.validateSupportedUUIDFromDB();
		MSession session = new Query(context, I_AD_Session.Table_Name, I_AD_Session.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getSessionUuid())
				.first();
		if(session == null
				|| session.getAD_Session_ID() <= 0) {
			throw new AdempiereException("@AD_Session_ID@ @NotFound@");
		}
		Env.setContext (context, "#AD_Session_ID", session.getAD_Session_ID());
		Env.setContext(context, "#AD_User_ID", session.getCreatedBy());
		Env.setContext(context, "#AD_Role_ID", session.getAD_Role_ID());
		Env.setContext(context, "#AD_Client_ID", session.getAD_Client_ID());
		Env.setContext(context, "#AD_Org_ID", session.getAD_Org_ID());
		Env.setContext(context, "#Date", new Timestamp(System.currentTimeMillis()));
		Env.setContext(context, Env.LANGUAGE, request.getLanguage());
		//	Save to Cache
		sessionsContext.put(request.getSessionUuid(), context);
		return context;
	}
	
	/**
	 * Convert Lookup from query
	 * @param request
	 * @return
	 */
	private ValueObject.Builder convertLookup(Properties context, ValueObjectRequest request) {
		Criteria criteria = request.getCriteria();
		String sql = criteria.getQuery();
		List<Value> values = criteria.getValuesList();
		ValueObject.Builder builder = ValueObject.newBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			//	SELECT Key, Value, Name FROM ...
			pstmt = DB.prepareStatement(sql, null);
			AtomicInteger parameterIndex = new AtomicInteger(1);
			for(Value value : values) {
				setParameterFromValue(pstmt, value, parameterIndex.getAndIncrement());
			}
			//	Get from Query
			rs = pstmt.executeQuery();
			if (rs.next()) {
				//	1 = Key Column
				//	2 = Optional Value
				//	3 = Display Value
				ResultSetMetaData metaData = rs.getMetaData();
				int keyValueType = metaData.getColumnType(1);
				Object keyValue = null;
				if(keyValueType == Types.VARCHAR
						|| keyValueType == Types.NVARCHAR
						|| keyValueType == Types.CHAR
						|| keyValueType == Types.NCHAR) {
					keyValue = rs.getString(1);
				} else {
					keyValue = rs.getInt(1);
				}
				//	
				builder = convertObjectFromResult(keyValue, null, rs.getString(2), rs.getString(3));
			}
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
		} finally {
			DB.close(rs, pstmt);
		}
		//	Return values
		return builder;
	}
	
	/**
	 * Convert Object to list
	 * @param request
	 * @return
	 */
	private ValueObjectList.Builder convertLookupList(Properties context, ValueObjectRequest request) {
		Criteria criteria = request.getCriteria();
		String sql = criteria.getQuery();
		sql = MRole.getDefault(context, false).addAccessSQL(sql,
				criteria.getTableName(), MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
		List<Value> values = criteria.getValuesList();
		ValueObjectList.Builder builder = ValueObjectList.newBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long recordCount = 0;
		try {
			//	SELECT Key, Value, Name FROM ...
			pstmt = DB.prepareStatement(sql, null);
			AtomicInteger parameterIndex = new AtomicInteger(1);
			for(Value value : values) {
				setParameterFromValue(pstmt, value, parameterIndex.getAndIncrement());
			}
			//	Get from Query
			rs = pstmt.executeQuery();
			while(rs.next()) {
				//	1 = Key Column
				//	2 = Optional Value
				//	3 = Display Value
				ResultSetMetaData metaData = rs.getMetaData();
				int keyValueType = metaData.getColumnType(1);
				Object keyValue = null;
				if(keyValueType == Types.VARCHAR
						|| keyValueType == Types.NVARCHAR
						|| keyValueType == Types.CHAR
						|| keyValueType == Types.NCHAR) {
					keyValue = rs.getString(1);
				} else {
					keyValue = rs.getInt(1);
				}
				//	
				ValueObject.Builder valueObject = convertObjectFromResult(keyValue, null, rs.getString(2), rs.getString(3));
				builder.addRecords(valueObject.build());
				recordCount++;
			}
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
		} finally {
			DB.close(rs, pstmt);
		}
		//	Set record counts
		builder.setRecordCount(recordCount);
		//	Return
		return builder;
	}
	
	/**
	 * Set Parameter for Statement from value
	 * @param pstmt
	 * @param value
	 * @param index
	 * @throws SQLException
	 */
	private void setParameterFromValue(PreparedStatement pstmt, Value value, int index) throws SQLException {
		if(value.getValueType().equals(ValueType.INTEGER)) {
			pstmt.setInt(index, value.getIntValue());
		} else if(value.getValueType().equals(ValueType.DOUBLE)) {
			pstmt.setDouble(index, value.getDoubleValue());
		} else if(value.getValueType().equals(ValueType.LONG)) {
			pstmt.setLong(index, value.getLongValue());
		} else if(value.getValueType().equals(ValueType.STRING)) {
			pstmt.setString(index, value.getStringValue());
		} else if(value.getValueType().equals(ValueType.DATE)) {
			pstmt.setTimestamp(index, new Timestamp(value.getLongValue()));
		}
	}
	
	/**
	 * Convert Object to list
	 * @param request
	 * @return
	 */
	private ValueObjectList.Builder convertObjectList(Properties context, ValueObjectRequest request) {
		Criteria criteria = request.getCriteria();
		StringBuffer whereClause = new StringBuffer();
		List<Object> params = new ArrayList<>();
		if(!Util.isEmpty(request.getUuid())) {
			whereClause.append(I_AD_Element.COLUMNNAME_UUID + " = ?");
			params.add(request.getUuid());
		} else if(!Util.isEmpty(criteria.getWhereClause())) {
			whereClause.append("(").append(criteria.getWhereClause()).append(")");
		}
		//	
		List<PO> entityList = new Query(context, criteria.getTableName(), whereClause.toString(), null)
				.setParameters(params)
				.<PO>list();
		//	
		ValueObjectList.Builder builder = ValueObjectList.newBuilder()
				.setRecordCount(entityList.size());
		//	Convert List
		for(PO entity : entityList) {
			ValueObject.Builder valueObject = convertObject(context, entity);
			builder.addRecords(valueObject.build());
		}
		//	Return
		return builder;
	}
	
	/**
	 * Run callout with data from server
	 * @param request
	 * @return
	 */
	private CalloutResponse.Builder runcallout(Properties context, CalloutRequest request) {
		CalloutResponse.Builder calloutBuilder = CalloutResponse.newBuilder();
		//	TODO: GridTab and GridField bust be instanced
		String result = processCallout(context, null, null);
		calloutBuilder.setResult(validateNull(result));
		return calloutBuilder;
	}
	
	/**
	 * Process Callout
	 * @param gridTab
	 * @param field
	 * @return
	 */
	private String processCallout (Properties context, GridTab gridTab, GridField field) {
		String callout = field.getCallout();
		if (callout.length() == 0)
			return "";
		//
		int windowNo = 0;
		Object value = field.getValue();
		Object oldValue = field.getOldValue();
		log.fine(field.getColumnName() + "=" + value
			+ " (" + callout + ") - old=" + oldValue);

		StringTokenizer st = new StringTokenizer(callout, ";,", false);
		while (st.hasMoreTokens()) {
			String cmd = st.nextToken().trim();
			String retValue = "";
			// FR [1877902]
			// CarlosRuiz - globalqss - implement beanshell callout
			// Victor Perez  - vpj-cd implement JSR 223 Scripting
			if (cmd.toLowerCase().startsWith(MRule.SCRIPT_PREFIX)) {
				
				MRule rule = MRule.get(context, cmd.substring(MRule.SCRIPT_PREFIX.length()));
				if (rule == null) {
					retValue = "Callout " + cmd + " not found"; 
					log.log(Level.SEVERE, retValue);
					return retValue;
				}
				if ( !  (rule.getEventType().equals(MRule.EVENTTYPE_Callout) 
					  && rule.getRuleType().equals(MRule.RULETYPE_JSR223ScriptingAPIs))) {
					retValue = "Callout " + cmd
						+ " must be of type JSR 223 and event Callout"; 
					log.log(Level.SEVERE, retValue);
					return retValue;
				}

				ScriptEngine engine = rule.getScriptEngine();

				// Window context are    W_
				// Login context  are    G_
				MRule.setContext(engine, context, windowNo);
				// now add the callout parameters windowNo, tab, field, value, oldValue to the engine 
				// Method arguments context are A_
				engine.put(MRule.ARGUMENTS_PREFIX + "WindowNo", windowNo);
				engine.put(MRule.ARGUMENTS_PREFIX + "Tab", this);
				engine.put(MRule.ARGUMENTS_PREFIX + "Field", field);
				engine.put(MRule.ARGUMENTS_PREFIX + "Value", value);
				engine.put(MRule.ARGUMENTS_PREFIX + "OldValue", oldValue);
				engine.put(MRule.ARGUMENTS_PREFIX + "Ctx", context);

				try  {
					retValue = engine.eval(rule.getScript()).toString();
				} catch (Exception e) {
					log.log(Level.SEVERE, "", e);
					retValue = 	"Callout Invalid: " + e.toString();
					return retValue;
				}
			} else {
				Callout call = null;
				String method = null;
				int methodStart = cmd.lastIndexOf('.');
				try {
					if (methodStart != -1) {
						Class<?> cClass = Class.forName(cmd.substring(0,methodStart));
						call = (Callout)cClass.newInstance();
						method = cmd.substring(methodStart+1);
					}
				} catch (Exception e) {
					log.log(Level.SEVERE, "class", e);
					return "Callout Invalid: " + cmd + " (" + e.toString() + ")";
				}

				if (call == null || method == null || method.length() == 0)
					return "Callout Invalid: " + method;

				try {
					retValue = call.start(context, method, windowNo, gridTab, field, value, oldValue);
				} catch (Exception e) {
					log.log(Level.SEVERE, "start", e);
					retValue = 	"Callout Invalid: " + e.toString();
					return retValue;
				}
				
			}			
			if (!Util.isEmpty(retValue)) {	//	interrupt on first error
				log.severe (retValue);
				return retValue;
			}
		}   //  for each callout
		return "";
	}	//	processCallout
	
	/**
	 * Convert PO to Value Object
	 * @param entity
	 * @return
	 */
	private ValueObject.Builder convertObject(Properties context, PO entity) {
		ValueObject.Builder builder = ValueObject.newBuilder();
		if(entity == null) {
			return builder;
		}
		builder.setUuid(validateNull(entity.get_ValueAsString(I_AD_Element.COLUMNNAME_UUID)))
			.setId(entity.get_ID());
		//	Convert attributes
		POInfo poInfo = POInfo.getPOInfo(context, entity.get_Table_ID());
		for(int index = 0; index < poInfo.getColumnCount(); index++) {
			String columnName = poInfo.getColumnName(index);
			Object value = entity.get_Value(index);
			if(value == null) {
				continue;
			}
			Value.Builder builderValue = Value.newBuilder();
			Class<?> clazz = poInfo.getColumnClass(index);
			if (clazz == BigDecimal.class) {
				BigDecimal bigdecimalValue = (BigDecimal) value;
				builderValue.setValueType(ValueType.DOUBLE);
				builderValue.setDoubleValue(bigdecimalValue.doubleValue());
			} else if (clazz == Integer.class) {
				builderValue.setValueType(ValueType.INTEGER);
				builderValue.setIntValue(entity.get_ValueAsInt(index));
			} else if (clazz == String.class) {
				builderValue.setValueType(ValueType.STRING);
				builderValue.setStringValue(validateNull(entity.get_ValueAsString(columnName)));
			} else if (clazz == Boolean.class) {
				builderValue.setValueType(ValueType.BOOLEAN);
				builderValue.setBooleanValue(entity.get_ValueAsBoolean(columnName));
			} else {
				continue;
			}
			//	TODO: Timestamp support
			//	Add
			builder.putValues(columnName, builderValue.build());
		}
		//	
		return builder;
	}
	
	/**
	 * Convert Values from result
	 * @param keyValue
	 * @param uuidValue
	 * @param value
	 * @param displayValue
	 * @return
	 */
	private ValueObject.Builder convertObjectFromResult(Object keyValue, String uuidValue, String value, String displayValue) {
		ValueObject.Builder builder = ValueObject.newBuilder();
		if(keyValue == null) {
			return builder;
		}
		builder.setUuid(validateNull(uuidValue));
		
		if(keyValue instanceof Integer) {
			builder.setId((Integer) keyValue);
		} else {
			builder.putValues(KEY_COLUMN_KEY, Value.newBuilder().setValueType(ValueType.STRING).setStringValue(validateNull((String) keyValue)).build());
		}
		//	Set Value
		if(!Util.isEmpty(value)) {
			builder.putValues(VALUE_COLUMN_KEY, Value.newBuilder().setValueType(ValueType.STRING).setStringValue(validateNull(value)).build());
		}
		//	Display column
		if(!Util.isEmpty(displayValue)) {
			builder.putValues(DISPLAY_COLUMN_KEY, Value.newBuilder().setValueType(ValueType.STRING).setStringValue(validateNull(displayValue)).build());
		}
		//	
		return builder;
	}
	
	/**
	 * Convert null on ""
	 * @param value
	 * @return
	 */
	private String validateNull(String value) {
		if(value == null) {
			value = "";
		}
		//	
		return value;
	}
}

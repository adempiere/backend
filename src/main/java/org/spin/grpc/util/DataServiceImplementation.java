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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import javax.script.ScriptEngine;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.I_AD_Browse;
import org.adempiere.model.MBrowse;
import org.adempiere.model.MBrowseField;
import org.compiere.model.Callout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_AD_Element;
import org.compiere.model.I_AD_PInstance;
import org.compiere.model.I_AD_PInstance_Log;
import org.compiere.model.I_AD_Process;
import org.compiere.model.I_AD_Session;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MProcess;
import org.compiere.model.MRole;
import org.compiere.model.MRule;
import org.compiere.model.MSession;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.model.Query;
import org.compiere.model.X_AD_PInstance_Log;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.eevolution.service.dsl.ProcessBuilder;
import org.spin.grpc.util.DataServiceGrpc.DataServiceImplBase;
import org.spin.grpc.util.Value.ValueType;

import com.google.protobuf.ByteString;

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
	/**	Session Context	*/
	private static CCache<String, MBrowse> browserRequested = new CCache<String, MBrowse>(I_AD_Browse.Table_Name + "_UUID", 30, 0);	//	no time-out
	
	@Override
	public void requestObject(ValueObjectRequest request, StreamObserver<ValueObject> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			ValueObject.Builder entityValue = convertObject(context, request);
			responseObserver.onNext(entityValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
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
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestObjectList(ValueObjectRequest request, StreamObserver<ValueObjectList> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object List Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			ValueObjectList.Builder entutyValueList = convertObjectList(context, request);
			responseObserver.onNext(entutyValueList.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestLookup(ValueObjectRequest request, StreamObserver<ValueObject> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Lookup Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			ValueObject.Builder lookupValue = convertLookup(context, request);
			responseObserver.onNext(lookupValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestLookupList(ValueObjectRequest request, StreamObserver<ValueObjectList> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Lookup Request Null");
			}
			log.fine("Lookup List Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			ValueObjectList.Builder entityValueList = convertLookupList(context, request);
			responseObserver.onNext(entityValueList.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestProcess(ProcessRequest request, StreamObserver<ProcessResponse> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Lookup List Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			ProcessResponse.Builder processReponse = runProcess(context, request, request.getClientRequest().getLanguage());
			responseObserver.onNext(processReponse.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestBrowser(BrowserRequest request, StreamObserver<ValueObjectList> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Browser Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = getContext(request.getClientRequest());
			
			ValueObjectList.Builder entityValueList = convertBrowserList(context, request);
			
			responseObserver.onNext(entityValueList.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestProcessActivity(ProcessActivityRequest request, StreamObserver<ProcessResponseList> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Process Activity Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = getContext(request.getClientRequest());
			ProcessResponseList.Builder entityValueList = convertProcessActivity(context, request);
			responseObserver.onNext(entityValueList.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(e);
		}
	}
	
	/**
	 * Run a process from request
	 * @param context
	 * @param request
	 * @param language
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private ProcessResponse.Builder runProcess(Properties context, ProcessRequest request, String language) throws FileNotFoundException, IOException {
		ProcessResponse.Builder response = ProcessResponse.newBuilder();
		//	Get Process definition
		MProcess process = getProcess(context, request.getUuid(), language);
		if(process == null
				|| process.getAD_Process_ID() <= 0) {
			throw new AdempiereException("@AD_Process_ID@ @NotFound@");
		}
		//	Call process builder
		ProcessBuilder builder = ProcessBuilder.create(context)
				.process(process.getAD_Process_ID())
				.withRecordId(request.getTableId(), request.getRecordId())
				.withBatchMode()
				.withoutPrintPreview()
				.withWindowNo(0)
				.withTitle(process.getName());
		//	Set Report Export Type
		if(process.isReport()) {
			builder.withReportExportFormat(request.getReportExportType());
		}
		//	Selection
		if(request.getSelectionsCount() > 0) {
			List<Integer> selectionKeys = new ArrayList<>();
			LinkedHashMap<Integer, LinkedHashMap<String, Object>> selection = new LinkedHashMap<>();
			for(Selection selectionKey : request.getSelectionsList()) {
				selectionKeys.add(selectionKey.getSelectionId());
				if(selectionKey.getValuesCount() > 0) {
					selection.put(selectionKey.getSelectionId(), convertValues(selectionKey.getValuesList()));
				}
			}
			builder.withSelectedRecordsIds(request.getTableSelectedId(), selectionKeys, selection);
		}
		//	Parameters
		if(request.getParametersCount() > 0) {
			for(KeyValue parameter : request.getParametersList()) {
				Object value = getValueFromType(parameter.getValue());
				if(value != null) {
					builder.withParameter(parameter.getKey(), value);
				}
			}
		}
		//	Execute Process
		ProcessInfo result = builder.execute();
		String instanceUuid = DB.getSQLValueString(null, "SELECT UUID FROM AD_PInstance WHERE AD_PInstance_ID = ?", result.getAD_PInstance_ID());
		response.setInstanceUuid(validateNull(instanceUuid));
		response.setIsError(result.isError());
		if(!Util.isEmpty(result.getSummary())) {
			response.setSummary(result.getSummary());
		}
		//	Convert Log
		if(result.getLogList() != null) {
			for(org.compiere.process.ProcessInfoLog log : result.getLogList()) {
				response.addLogs(convertProcessInfoLog(log).build());
			}
		}
		//	Verify Output
		if(process.isReport()) {
			File reportFile = result.getReportAsFile();
			if(reportFile != null
					&& reportFile.exists()) {
				ProcessOutput.Builder output = ProcessOutput.newBuilder();
				output.setFileName(validateNull(reportFile.getName()));
				output.setName(result.getTitle());
				output.setDescription(validateNull(process.getDescription()));
				//	Type
				output.setReportExportType(request.getReportExportType());
				ByteString resultFile = ByteString.readFrom(new FileInputStream(reportFile));
				if(request.getReportExportType().endsWith("html")
						|| request.getReportExportType().endsWith("txt")) {
					output.setOutputBytes(resultFile);
				}
				output.setOutputStream(resultFile);
				response.setOutput(output.build());
			}
		}
		
		return response;
	}
	
	/**
	 * Convert Log to gRPC
	 * @param log
	 * @return
	 */
	private ProcessInfoLog.Builder convertProcessInfoLog(org.compiere.process.ProcessInfoLog log) {
		ProcessInfoLog.Builder processLog = ProcessInfoLog.newBuilder();
		processLog.setRecordId(log.getP_ID());
		processLog.setLog(validateNull(log.getP_Msg()));
		return processLog;
	}
	
	/**
	 * Convert Selection values from gRPC to ADempiere values
	 * @param values
	 * @return
	 */
//	private LinkedHashMap<String, Object> convertValues(Map<String, Value> values) {
//		LinkedHashMap<String, Object> convertedValues = new LinkedHashMap<>();
//		for(Entry<String, Value> value : values.entrySet()) {
//			//	Convert value
//			convertedValues.put(value.getKey(), getValueFromType(value.getValue()));
//		}
//		//	
//		return convertedValues;
//	}
	
	/**
	 * Convert Selection values from gRPC to ADempiere values
	 * @param values
	 * @return
	 */
	private LinkedHashMap<String, Object> convertValues(List<KeyValue> values) {
		LinkedHashMap<String, Object> convertedValues = new LinkedHashMap<>();
		for(KeyValue value : values) {
			convertedValues.put(value.getKey(), getValueFromType(value.getValue()));
		}
		//	
		return convertedValues;
	}
	
	/**
	 * Get value from parameter type
	 * @param valueToConvert
	 * @return
	 */
	private Object getValueFromType(Value valueToConvert) {
		Object value = null;
		if(valueToConvert.getValueType().equals(ValueType.BOOLEAN)) {
			value = valueToConvert.getBooleanValue();
		} else if(valueToConvert.getValueType().equals(ValueType.DOUBLE)
				|| valueToConvert.getValueType().equals(ValueType.LONG)) {
			value = new BigDecimal(valueToConvert.getDoubleValue());
		} else if(valueToConvert.getValueType().equals(ValueType.INTEGER)) {
			value = valueToConvert.getIntValue();
		} else if(valueToConvert.getValueType().equals(ValueType.STRING)) {
			value = valueToConvert.getStringValue();
		} else if(valueToConvert.getValueType().equals(ValueType.DATE)) {
			if(valueToConvert.getLongValue() > 0) {
				value = new Timestamp(valueToConvert.getLongValue());
			}
		}
		return value;
	}
	
	/**
	 * Get Process from UUID
	 * @param uuid
	 * @param language
	 * @return
	 */
	private MProcess getProcess(Properties context, String uuid, String language) {
		return new Query(context, I_AD_Process.Table_Name, I_AD_Process.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
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
			Env.setContext(context, Env.LANGUAGE, request.getLanguage());
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
	 * Set Parameter for Statement from object
	 * @param pstmt
	 * @param value
	 * @param index
	 * @throws SQLException
	 */
	private void setParameterFromObject(PreparedStatement pstmt, Object value, int index) throws SQLException {
		if(value instanceof Integer) {
			pstmt.setInt(index, (Integer) value);
		} else if(value instanceof Double) {
			pstmt.setDouble(index, (Double) value);
		} else if(value instanceof Long) {
			pstmt.setLong(index, (Long) value);
		} else if(value instanceof BigDecimal) {
			pstmt.setBigDecimal(index, (BigDecimal) value);
		} else if(value instanceof String) {
			pstmt.setString(index, (String) value);
		} else if(value instanceof Timestamp) {
			pstmt.setTimestamp(index, (Timestamp) value);
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
	 * Get Where clause for Smart Browse
	 * @param browser
	 * @param parsedWhereClause
	 * @param values
	 * @return
	 */
	private String getBrowserWhereClause(MBrowse browser, String parsedWhereClause, HashMap<String, Object> parameterMap, List<Object> values) {
		StringBuilder browserWhereClause = new StringBuilder();
		List<MBrowseField> fields = browser.getFields();
		LinkedHashMap<String, MBrowseField> fieldsMap = new LinkedHashMap<>();
		//	Add field to map
		for(MBrowseField field: fields) {
			fieldsMap.put(field.getAD_View_Column().getColumnName(), field);
		}
		//	
		boolean onRange = false;
		if(parameterMap.size() > 0) {
			for(Entry<String, Object> parameter : parameterMap.entrySet()) {
				MBrowseField field = fieldsMap.get(parameter.getKey());
				if(field == null) {
					continue;
				}
				String columnName = field.getAD_View_Column().getColumnSQL();
				Object parameterValue = parameter.getValue();
				if (!onRange) {
					if (parameterValue != null && !field.isRange()) {
						if(browserWhereClause.length() > 0) {
							browserWhereClause.append(" AND ");
						}
						if(DisplayType.String == field.getAD_Reference_ID()) {
							String value = (String) parameterValue;
							if (value.contains(",")) {
								value = value.replace(" ", "");
								String inStr = new String(value);
								StringBuffer outStr = new StringBuffer("(");
								int i = inStr.indexOf(',');
								while (i != -1)
								{
									outStr.append("'" + inStr.substring(0, i) + "',");
									inStr = inStr.substring(i+1, inStr.length());
									i = inStr.indexOf(',');

								}
								outStr.append("'" + inStr + "')");
								//	
								browserWhereClause.append(columnName).append(" IN ")
								.append(outStr);
							}
							else if (value.contains("%")) {
								browserWhereClause.append(" lower( ").append(columnName).append(") LIKE ? ");
								values.add(parameterValue.toString().toLowerCase());
							} else {
								browserWhereClause.append(" lower( ").append(columnName).append(") = ? ");
								values.add(parameterValue.toString().toLowerCase());
							}
						} else {
							browserWhereClause.append(columnName).append("=? ");
							values.add(parameterValue);
						}
					} else if (parameterValue != null && field.isRange()) {
						if(browserWhereClause.length() > 0) {
							browserWhereClause.append(" AND ");
						}
						if(DisplayType.String == field.getAD_Reference_ID()) {
							browserWhereClause.append(" lower( ").append(columnName).append(") >= ? ");
							values.add(parameterValue.toString().toLowerCase());
						}
						else {
							browserWhereClause.append(columnName).append(" >= ? ");
							values.add(parameterValue);
						}
						onRange = true;
					}
					else if (parameterValue == null && field.isRange()) {
						onRange = true;
					} else
						continue;
				} else if (parameterValue != null) {
					if(browserWhereClause.length() > 0) {
						browserWhereClause.append(" AND ");
					}
					if(DisplayType.String == field.getAD_Reference_ID()) {
						browserWhereClause.append(" lower( ").append(columnName).append(") <= ? ");
						values.add(parameterValue.toString().toLowerCase());
					} else {
						browserWhereClause.append(columnName).append(" <= ? ");
						values.add(parameterValue);
					}
					onRange = false;
				} else {
					onRange = false;
				}
			}
		}
		//	
		String whereClause = null;
		//	
		if(!Util.isEmpty(parsedWhereClause)) {
			whereClause = parsedWhereClause.toString();
		}
		if(browserWhereClause.length() > 0) {
			if(Util.isEmpty(whereClause)) {
				whereClause = browserWhereClause.toString();
			} else {
				whereClause = whereClause + " AND (" + browserWhereClause + ")";
			}
		}
		return whereClause;
	}
	
	/**
	 * Convert Object to list
	 * @param request
	 * @return
	 */
	private ValueObjectList.Builder convertBrowserList(Properties context, BrowserRequest request) {
		ValueObjectList.Builder builder = ValueObjectList.newBuilder();
		MBrowse browser = getBrowser(context, request.getUuid());
		if(browser == null) {
			return builder;
		}
		Criteria criteria = request.getCriteria();
		HashMap<String, Object> parameterMap = new HashMap<>();
		//	Populate map
		for(KeyValue parameter : request.getParametersList()) {
			parameterMap.put(parameter.getKey(), getValueFromType(parameter.getValue()));
		}
		List<Object> values = new ArrayList<Object>();
		String whereClause = getBrowserWhereClause(browser, criteria.getWhereClause(), parameterMap, values);
		//	Add SQL
		//	TODO: Add parsed query from client
		StringBuilder sql = new StringBuilder(criteria.getQuery());
		if (whereClause.length() > 0) {
			sql.append(" WHERE ").append(whereClause); // includes first AND
		}
		String tableName = browser.getAD_View().getParentEntityAliasName();
		//	
		String parsedSQL = MRole.getDefault().addAccessSQL(sql.toString(),
				tableName, MRole.SQL_FULLYQUALIFIED,
				MRole.SQL_RO);
		String orderByClause = criteria.getOrderByClause();
		if(Util.isEmpty(orderByClause)) {
			orderByClause = "";
		} else {
			orderByClause = " ORDER BY " + orderByClause;
		}
		//	Add Order By
		parsedSQL = parsedSQL + orderByClause;
		//	Return
		return convertBrowserResult(browser, parsedSQL, values);
	}
	
	/**
	 * Convert SQL to list values
	 * @param sql
	 * @param values
	 * @return
	 */
	private ValueObjectList.Builder convertBrowserResult(MBrowse browser, String sql, List<Object> values) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ValueObjectList.Builder builder = ValueObjectList.newBuilder();
		long recordCount = 0;
		try {
			LinkedHashMap<String, MBrowseField> fieldsMap = new LinkedHashMap<>();
			//	Add field to map
			for(MBrowseField field: browser.getFields()) {
				fieldsMap.put(field.getAD_View_Column().getColumnName().toUpperCase(), field);
			}
			//	SELECT Key, Value, Name FROM ...
			pstmt = DB.prepareStatement(sql, null);
			AtomicInteger parameterIndex = new AtomicInteger(1);
			for(Object value : values) {
				setParameterFromObject(pstmt, value, parameterIndex.getAndIncrement());
			}
			//	Get from Query
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ValueObject.Builder valueObjectBuilder = ValueObject.newBuilder();
				ResultSetMetaData metaData = rs.getMetaData();
				for (int index = 1; index <= metaData.getColumnCount(); index++) {
					String columnName = metaData.getColumnName (index);
					MBrowseField field = fieldsMap.get(columnName.toUpperCase());
					Value.Builder valueBuilder = Value.newBuilder();
					boolean isFilled = false;
					//	Validate Type
					if(DisplayType.isID(field.getAD_Reference_ID())) {
						valueBuilder.setIntValue(rs.getInt(index));
						valueBuilder.setValueType(ValueType.INTEGER);
					} if(DisplayType.isNumeric(field.getAD_Reference_ID())) {
						BigDecimal value = rs.getBigDecimal(index);
						if(value != null) {
							isFilled = true;
							valueBuilder.setDoubleValue(value.doubleValue());
							valueBuilder.setValueType(ValueType.DOUBLE);
						}
					} if(DisplayType.YesNo == field.getAD_Reference_ID()) {
						isFilled = true;
						String value = rs.getString(index);
						valueBuilder.setBooleanValue(!Util.isEmpty(value) && value.equals("Y"));
						valueBuilder.setValueType(ValueType.BOOLEAN);
					} if(DisplayType.isDate(field.getAD_Reference_ID())) {
						Timestamp value = rs.getTimestamp(index);
						if(value != null) {
							isFilled = true;
							valueBuilder.setLongValue(value.getTime());
						}
						valueBuilder.setValueType(ValueType.DATE);
					} else if(DisplayType.isText(field.getAD_Reference_ID())) {
						String value = rs.getString(index);
						if(!Util.isEmpty(value)) {
							isFilled = true;
							valueBuilder.setStringValue(value);
							valueBuilder.setValueType(ValueType.STRING);
						}
					}
					if(isFilled) {
						valueObjectBuilder.putValues(field.getAD_View_Column().getColumnName(), valueBuilder.build());
					}
				}
				//	
				builder.addRecords(valueObjectBuilder.build());
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
	 * get browser
	 * @param context
	 * @param uuid
	 * @return
	 */
	private MBrowse getBrowser(Properties context, String uuid) {
		MBrowse browser = browserRequested.get(uuid);
		if(browser == null) {
			browser = new Query(context, I_AD_Browse.Table_Name, I_AD_Process.COLUMNNAME_UUID + " = ?", null)
					.setParameters(uuid)
					.setOnlyActiveRecords(true)
					.first();
		}
		//	Put on Cache
		if(browser != null) {
			browserRequested.put(uuid, browser);
		}
		//	
		return browser;
	}
	
	/**
	 * Convert request for process activity to builder
	 * @param context
	 * @param request
	 * @return
	 */
	private ProcessResponseList.Builder convertProcessActivity(Properties context, ProcessActivityRequest request) {
		String sql = null;
		String uuid = null;
		if(!Util.isEmpty(request.getUserUuid())) {
			uuid = request.getUserUuid();
			sql = "EXISTS(SELECT 1 FROM AD_User WHERE UUID = ? AND AD_User_ID = AD_PInstance.AD_User_ID)";
		} else if(!Util.isEmpty(request.getInstanceUuid())) {
			uuid = request.getInstanceUuid();
			sql = "UUID = ?";
		} else {
			uuid = request.getClientRequest().getSessionUuid();
			sql = "EXISTS(SELECT 1 FROM AD_Session WHERE UUID = ? AND CreatedBy = AD_PInstance.AD_User_ID)";
		}
		List<MPInstance> processInstanceList = new Query(context, I_AD_PInstance.Table_Name, 
				sql, null)
				.setParameters(uuid)
				.setOrderBy(I_AD_PInstance.COLUMNNAME_Created + " DESC")
				.<MPInstance>list();
		//	
		ProcessResponseList.Builder builder = ProcessResponseList.newBuilder();
		//	Convert Process Instance
		for(MPInstance processInstance : processInstanceList) {
			ProcessResponse.Builder valueObject = convertProcessInstance(processInstance);
			builder.addResponses(valueObject.build());
		}
		//	Return
		return builder;
	}
	
	/**
	 * Convert Process Instance
	 * @param instance
	 * @return
	 */
	private ProcessResponse.Builder convertProcessInstance(MPInstance instance) {
		ProcessResponse.Builder builder = ProcessResponse.newBuilder();
		builder.setInstanceUuid(validateNull(instance.getUUID()));
		builder.setIsError(!instance.isOK());
		builder.setIsProcessing(instance.isProcessing());
		String summary = instance.getErrorMsg();
		if(!Util.isEmpty(summary)) {
			summary = Msg.parseTranslation(Env.getCtx(), summary);
		}
		//	for report
		MProcess process = MProcess.get(Env.getCtx(), instance.getAD_Process_ID());
		if(process.isReport()) {
			ProcessOutput.Builder outputBuilder = ProcessOutput.newBuilder();
			outputBuilder.setReportExportType(validateNull(instance.getReportType()));
			outputBuilder.setName(validateNull(instance.getName()));
			builder.setOutput(outputBuilder.build());
		}
		builder.setSummary(validateNull(summary));
		List<X_AD_PInstance_Log> logList = new Query(Env.getCtx(), I_AD_PInstance_Log.Table_Name, 
				I_AD_PInstance.COLUMNNAME_AD_PInstance_ID + " = ?", null)
				.setParameters(instance.getAD_PInstance_ID())
				.<X_AD_PInstance_Log>list();
		//	Add Output
		for(X_AD_PInstance_Log log : logList) {
			ProcessInfoLog.Builder logBuilder = ProcessInfoLog.newBuilder();
			logBuilder.setRecordId(log.getAD_PInstance_Log_ID());
			String message = log.getP_Msg();
			if(!Util.isEmpty(message)) {
				message = Msg.parseTranslation(Env.getCtx(), message);
			}
			logBuilder.setLog(validateNull((message)));
			builder.addLogs(logBuilder.build());
		}
		//	
		for(MPInstancePara parameter : instance.getParameters()) {
			Value.Builder parameterBuilder = Value.newBuilder();
			Value.Builder parameterToBuilder = Value.newBuilder();
			boolean hasFromParameter = false;
			boolean hasToParameter = false;
			String parameterName = parameter.getParameterName();
			int displayType = parameter.getDisplayType();
			if(displayType == -1) {
				displayType = DisplayType.String;
			}
			//	Validate
			if(DisplayType.isID(displayType)) {
				BigDecimal number = parameter.getP_Number();
				BigDecimal numberTo = parameter.getP_Number_To();
				//	Validate
				if(number != null 
						&& !number.equals(Env.ZERO)) {
					hasFromParameter = true;
					parameterBuilder.setIntValue(number.intValue());
					parameterBuilder.setValueType(ValueType.INTEGER);
				}
				if(numberTo != null
						&& !numberTo.equals(Env.ZERO)) {
					hasToParameter = true;
					parameterToBuilder.setIntValue(numberTo.intValue());
					parameterToBuilder.setValueType(ValueType.INTEGER);
				}
			} else if(DisplayType.isNumeric(displayType)) {
				BigDecimal number = parameter.getP_Number();
				BigDecimal numberTo = parameter.getP_Number_To();
				//	Validate
				if(number != null 
						&& !number.equals(Env.ZERO)) {
					hasFromParameter = true;
					parameterBuilder.setDoubleValue(number.doubleValue());
					parameterBuilder.setValueType(ValueType.DOUBLE);
				}
				if(numberTo != null
						&& !numberTo.equals(Env.ZERO)) {
					hasToParameter = true;
					parameterToBuilder.setDoubleValue(numberTo.doubleValue());
					parameterToBuilder.setValueType(ValueType.DOUBLE);
				}
			} else if(DisplayType.isDate(displayType)) {
				Timestamp date = parameter.getP_Date();
				Timestamp dateTo = parameter.getP_Date_To();
				//	Validate
				if(date != null) {
					hasFromParameter = true;
					parameterBuilder.setLongValue(date.getTime());
					parameterBuilder.setValueType(ValueType.DATE);
				}
				if(dateTo != null) {
					hasToParameter = true;
					parameterToBuilder.setLongValue(dateTo.getTime());
					parameterToBuilder.setValueType(ValueType.DATE);
				}
			} else if(DisplayType.YesNo == displayType) {
				String value = parameter.getP_String();
				if(!Util.isEmpty(value)) {
					hasFromParameter = true;
					parameterBuilder.setBooleanValue(!Util.isEmpty(value) && value.equals("Y"));
					parameterBuilder.setValueType(ValueType.BOOLEAN);
				}
			} else {
				String value = parameter.getP_String();
				String valueTo = parameter.getP_String_To();
				//	Validate
				if(!Util.isEmpty(value)) {
					hasFromParameter = true;
					parameterBuilder.setStringValue(validateNull(value));
					parameterBuilder.setValueType(ValueType.STRING);
				}
				if(!Util.isEmpty(valueTo)) {
					hasToParameter = true;
					parameterToBuilder.setStringValue(validateNull(valueTo));
					parameterToBuilder.setValueType(ValueType.STRING);
				}
			}
			//	For parameter
			if(hasFromParameter) {
				builder.putParameters(parameterName, parameterBuilder.build());
			}
			//	For to parameter
			if(hasToParameter) {
				builder.putParameters(parameterName + "_To", parameterToBuilder.build());
			}
		}
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
			} else if(clazz == Timestamp.class) {
				builderValue.setValueType(ValueType.DATE);
				Timestamp date = (Timestamp) entity.get_Value(columnName);
				builderValue.setLongValue(date.getTime());
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
			builder.putValues(KEY_COLUMN_KEY, Value.newBuilder().setValueType(ValueType.INTEGER).setIntValue((Integer) keyValue).build());
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

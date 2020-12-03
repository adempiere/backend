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
package org.spin.grpc.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.I_AD_Browse;
import org.compiere.model.I_AD_Form;
import org.compiere.model.I_AD_Menu;
import org.compiere.model.I_AD_PInstance;
import org.compiere.model.I_AD_PrintFormat;
import org.compiere.model.I_AD_Process;
import org.compiere.model.I_AD_Window;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.I_C_Order;
import org.compiere.model.MColumn;
import org.compiere.model.MMenu;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.MRecentItem;
import org.compiere.model.MReportView;
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.MimeType;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.eevolution.service.dsl.ProcessBuilder;
import org.spin.base.util.ContextManager;
import org.spin.base.util.ConvertUtil;
import org.spin.base.util.RecordUtil;
import org.spin.base.util.ValueUtil;
import org.spin.grpc.util.BusinessDataGrpc.BusinessDataImplBase;
import org.spin.grpc.util.CreateEntityRequest;
import org.spin.grpc.util.Criteria;
import org.spin.grpc.util.DeleteEntityRequest;
import org.spin.grpc.util.Empty;
import org.spin.grpc.util.Entity;
import org.spin.grpc.util.GetEntityRequest;
import org.spin.grpc.util.KeyValue;
import org.spin.grpc.util.KeyValueSelection;
import org.spin.grpc.util.ListEntitiesRequest;
import org.spin.grpc.util.ListEntitiesResponse;
import org.spin.grpc.util.ProcessInfoLog;
import org.spin.grpc.util.ProcessLog;
import org.spin.grpc.util.ReportOutput;
import org.spin.grpc.util.RunBusinessProcessRequest;
import org.spin.grpc.util.UpdateEntityRequest;
import org.spin.grpc.util.Value;

import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * https://itnext.io/customizing-grpc-generated-code-5909a2551ca1
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Business data service
 */
public class BusinessDataServiceImplementation extends BusinessDataImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(BusinessDataServiceImplementation.class);
	/**	Reference cache	*/
	private static CCache<String, String> referenceWhereClauseCache = new CCache<String, String>("Reference_WhereClause", 30, 0);	//	no time-out
	@Override
	public void getEntity(GetEntityRequest request, StreamObserver<Entity> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Entity.Builder entityValue = getEntity(request);
			responseObserver.onNext(entityValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.augmentDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void createEntity(CreateEntityRequest request, StreamObserver<Entity> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			Entity.Builder entityValue = createEntity(context, request);
			responseObserver.onNext(entityValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.augmentDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void updateEntity(UpdateEntityRequest request, StreamObserver<Entity> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			Entity.Builder entityValue = updateEntity(context, request);
			responseObserver.onNext(entityValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.augmentDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void deleteEntity(DeleteEntityRequest request, StreamObserver<Empty> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			Empty.Builder entityValue = deleteEntity(context, request);
			responseObserver.onNext(entityValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.augmentDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void listEntities(ListEntitiesRequest request, StreamObserver<ListEntitiesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListEntitiesResponse.Builder entityValueList = convertEntitiesList(context, request);
			responseObserver.onNext(entityValueList.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.augmentDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void runBusinessProcess(RunBusinessProcessRequest request, StreamObserver<ProcessLog> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getProcessUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Lookup List Requested = " + request.getUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ProcessLog.Builder processReponse = runProcess(context, request);
			responseObserver.onNext(processReponse.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.augmentDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	/**
	 * Run a process from request
	 * @param context
	 * @param request
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private ProcessLog.Builder runProcess(Properties context, RunBusinessProcessRequest request) throws FileNotFoundException, IOException {
		ProcessLog.Builder response = ProcessLog.newBuilder();
		//	Get Process definition
		MProcess process = MProcess.get(context, RecordUtil.getIdFromUuid(I_AD_Process.Table_Name, request.getProcessUuid(), null));
		if(process == null
				|| process.getAD_Process_ID() <= 0) {
			throw new AdempiereException("@AD_Process_ID@ @NotFound@");
		}
		int tableId = 0;
		int recordId = request.getId();
		if(!Util.isEmpty(request.getTableName())) {
			MTable table = MTable.get(context, request.getTableName());
			if(table != null
					&& table.getAD_Table_ID() != 0) {
				tableId = table.getAD_Table_ID();
			}
		}
		PO entity = null;
		if((recordId != 0
				|| !Util.isEmpty(request.getUuid()))
				&& !Util.isEmpty(request.getTableName())) {
			String uuid = request.getUuid();
			if(recordId != 0) {
				uuid = null;
			}
			entity = RecordUtil.getEntity(context, request.getTableName(), uuid, recordId, null);
			if(entity != null) {
				recordId = entity.get_ID();
			}
		}
		//	Add to recent Item
		addToRecentItem(MMenu.ACTION_Process, process.getAD_Process_ID());
		//	Call process builder
		ProcessBuilder builder = ProcessBuilder.create(context)
				.process(process.getAD_Process_ID())
				.withRecordId(tableId, recordId)
				.withoutPrintPreview()
				.withoutBatchMode()
				.withWindowNo(0)
				.withTitle(process.getName());
		//	Set Report Export Type
		if(process.isReport()) {
			builder.withReportExportFormat(request.getReportType());
		}
		//	Selection
		if(request.getSelectionsCount() > 0) {
			List<Integer> selectionKeys = new ArrayList<>();
			LinkedHashMap<Integer, LinkedHashMap<String, Object>> selection = new LinkedHashMap<>();
			for(KeyValueSelection selectionKey : request.getSelectionsList()) {
				selectionKeys.add(selectionKey.getSelectionId());
				if(selectionKey.getValuesCount() > 0) {
					selection.put(selectionKey.getSelectionId(), new LinkedHashMap<>(ValueUtil.convertValuesToObjects(selectionKey.getValuesList())));
				}
			}
			builder.withSelectedRecordsIds(request.getTableSelectedId(), selectionKeys, selection);
		}
		//	get document action
		String documentAction = null;
		//	Parameters
		if(request.getParametersCount() > 0) {
			for(KeyValue parameter : request.getParametersList()) {
				Object value = ValueUtil.getObjectFromValue(parameter.getValue());
				if(value != null) {
					builder.withParameter(parameter.getKey(), value);
					if(parameter.getKey().equals(I_C_Order.COLUMNNAME_DocAction)) {
						documentAction = (String) value;
					}
				}
			}
		}
		//	For Document
		if(!Util.isEmpty(documentAction)
				&& process.getAD_Workflow_ID() != 0
				&& entity != null
				&& DocAction.class.isAssignableFrom(entity.getClass())) {
			entity.set_ValueOfColumn(I_C_Order.COLUMNNAME_DocAction, documentAction);
			entity.saveEx();
			builder.withoutTransactionClose();
		}
		//	Execute Process
		ProcessInfo result = null;
		try {
			result = builder.execute();
		} catch (Exception e) {
			result = builder.getProcessInfo();
			//	Set error message
			if(Util.isEmpty(result.getSummary())) {
				result.setSummary(e.getLocalizedMessage());
			}
		}
		String reportViewUuid = null;
		String printFormatUuid = request.getPrintFormatUuid();
		String tableName = null;
		//	Get process instance from identifier
		if(result.getAD_PInstance_ID() != 0) {
			MPInstance instance = new Query(context, I_AD_PInstance.Table_Name, I_AD_PInstance.COLUMNNAME_AD_PInstance_ID + " = ?", null)
					.setParameters(result.getAD_PInstance_ID())
					.first();
			response.setInstanceUuid(ValueUtil.validateNull(instance.getUUID()));
			response.setLastRun(instance.getUpdated().getTime());
			if(process.isReport()) {
				int printFormatId = 0;
				int reportViewId = 0;
				if(instance.getAD_PrintFormat_ID() != 0) {
					printFormatId = instance.getAD_PrintFormat_ID();
				} else if(process.getAD_PrintFormat_ID() != 0) {
					printFormatId = process.getAD_PrintFormat_ID();
				} else if(process.getAD_ReportView_ID() != 0) {
					reportViewId = process.getAD_ReportView_ID();
				}
				//	Get from report view or print format
				MPrintFormat printFormat = null;
				if(!Util.isEmpty(printFormatUuid)) {
					printFormat = new Query(context, I_AD_PrintFormat.Table_Name, I_AD_PrintFormat.COLUMNNAME_UUID + " = ?", null)
							.setParameters(printFormatUuid)
							.first();
					tableName = printFormat.getAD_Table().getTableName();
					if(printFormat.getAD_ReportView_ID() != 0) {
						MReportView reportView = MReportView.get(context, printFormat.getAD_ReportView_ID());
						reportViewUuid = reportView.getUUID();
					}
				} else if(printFormatId != 0) {
					printFormat = MPrintFormat.get(context, printFormatId, false);
					printFormatUuid = printFormat.getUUID();
					tableName = printFormat.getAD_Table().getTableName();
					if(printFormat.getAD_ReportView_ID() != 0) {
						MReportView reportView = MReportView.get(context, printFormat.getAD_ReportView_ID());
						reportViewUuid = reportView.getUUID();
					}
				} else if(reportViewId != 0) {
					MReportView reportView = MReportView.get(context, reportViewId);
					reportViewUuid = reportView.getUUID();
					tableName = reportView.getAD_Table().getTableName();
					printFormat = MPrintFormat.get(context, reportViewId, 0);
					if(printFormat != null) {
						printFormatUuid = printFormat.getUUID();
					}
				}
			}
		}
		//	Validate print format
		if(Util.isEmpty(printFormatUuid)) {
			printFormatUuid = request.getPrintFormatUuid();
		}
		//	Validate report view
		if(Util.isEmpty(reportViewUuid)) {
			reportViewUuid = request.getReportViewUuid();
		}
		//	
		response.setIsError(result.isError());
		if(!Util.isEmpty(result.getSummary())) {
			response.setSummary(Msg.parseTranslation(context, result.getSummary()));
		}
		//	
		response.setResultTableName(ValueUtil.validateNull(result.getResultTableName()));
		//	Convert Log
		if(result.getLogList() != null) {
			for(org.compiere.process.ProcessInfoLog log : result.getLogList()) {
				response.addLogs(convertProcessInfoLog(log).build());
			}
		}
		//	Verify Output
		if(process.isReport()) {
			File reportFile = Optional.ofNullable(result.getReportAsFile()).orElse(result.getPDFReport());
			if(reportFile != null
					&& reportFile.exists()) {
				String validFileName = getValidName(reportFile.getName());
				ReportOutput.Builder output = ReportOutput.newBuilder();
				output.setFileName(ValueUtil.validateNull(validFileName));
				output.setName(result.getTitle());
				output.setMimeType(ValueUtil.validateNull(MimeType.getMimeType(validFileName)));
				output.setDescription(ValueUtil.validateNull(process.getDescription()));
				//	Type
				String reportType = result.getReportType();
				if(Util.isEmpty(result.getReportType())) {
					reportType = result.getReportType();
				}
				if(!Util.isEmpty(getExtension(validFileName))
						&& !getExtension(validFileName).equals(reportType)) {
					reportType = getExtension(validFileName);
				}
				output.setReportType(request.getReportType());
				ByteString resultFile = ByteString.readFrom(new FileInputStream(reportFile));
				if(reportType.endsWith("html") || reportType.endsWith("txt")) {
					output.setOutputBytes(resultFile);
				}
				output.setReportType(reportType);
				output.setOutputStream(resultFile);
				output.setReportViewUuid(ValueUtil.validateNull(reportViewUuid));
				output.setPrintFormatUuid(ValueUtil.validateNull(printFormatUuid));
				output.setTableName(ValueUtil.validateNull(tableName));
				response.setOutput(output.build());
			}
		}
		return response;
	}
	
	/**
	 * Add element to recent item
	 * @param action
	 * @param optionId
	 */
	private void addToRecentItem(String action, int optionId) {
		if(Util.isEmpty(action)) {
			return;
		}
		String whereClause = null;
		if(action.equals(MMenu.ACTION_Window)) {
			whereClause = I_AD_Window.COLUMNNAME_AD_Window_ID + " = ?";
		} else if(action.equals(MMenu.ACTION_Form)) {
			whereClause = I_AD_Form.COLUMNNAME_AD_Form_ID + " = ?";
		} else if(action.equals(MMenu.ACTION_Process) || action.equals(MMenu.ACTION_Report)) {
			whereClause = I_AD_Process.COLUMNNAME_AD_Process_ID + " = ?";
		} else if(action.equals(MMenu.ACTION_WorkFlow)) {
			whereClause = I_AD_Workflow.COLUMNNAME_AD_Workflow_ID + " = ?";
		} else if(action.equals(MMenu.ACTION_SmartBrowse)) {
			whereClause = I_AD_Browse.COLUMNNAME_AD_Browse_ID + " = ?";
		}
		//	Get menu
		int menuId = new Query(Env.getCtx(), I_AD_Menu.Table_Name, whereClause, null)
			.setParameters(optionId)
			.firstId();
		MRecentItem.addMenuOption(Env.getCtx(), menuId, 0);
	}
	
	/**
	 * Convert Name
	 * @param name
	 * @return
	 */
	private String getValidName(String fileName) {
		if(Util.isEmpty(fileName)) {
			return "";
		}
		return fileName.replaceAll("[+^:&áàäéèëíìïóòöúùñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ$()*#/><]", "").replaceAll(" ", "-");
	}
	
	/**
	 * get file extension
	 * @param fileName
	 * @return
	 */
	private String getExtension(String fileName) {
		if(Util.isEmpty(fileName)) {
			return "";
		}
		int index = fileName.lastIndexOf(".");
		if(index <= -1) {
			return "";
		}
		//	return
		return fileName.substring(index + 1);
	}
	
	/**
	 * Convert Log to gRPC
	 * @param log
	 * @return
	 */
	private ProcessInfoLog.Builder convertProcessInfoLog(org.compiere.process.ProcessInfoLog log) {
		ProcessInfoLog.Builder processLog = ProcessInfoLog.newBuilder();
		processLog.setRecordId(log.getP_ID());
		processLog.setLog(ValueUtil.validateNull(Msg.parseTranslation(Env.getCtx(), log.getP_Msg())));
		return processLog;
	}
	
	/**
	 * Convert a PO from query
	 * @param request
	 * @return
	 */
	private Entity.Builder getEntity(GetEntityRequest request) {
		String tableName = request.getTableName();
		if(Util.isEmpty(request.getTableName())) {
			if(request.getCriteria() != null) {
				tableName = request.getCriteria().getTableName();
			}
		}
		PO entity = null;
		if(!Util.isEmpty(request.getUuid())
				|| request.getId() != 0) {
			entity = RecordUtil.getEntity(Env.getCtx(), tableName, request.getUuid(), request.getId(), null);
		} else if(request.getCriteria() != null) {
			List<Object> parameters = new ArrayList<Object>();
			String whereClause = ValueUtil.getWhereClauseFromCriteria(request.getCriteria(), parameters);
			entity = RecordUtil.getEntity(Env.getCtx(), tableName, whereClause, parameters, null);
		}
		//	Return
		return ConvertUtil.convertEntity(entity);
	}
	
	/**
	 * Delete a entity
	 * @param context
	 * @param request
	 * @return
	 */
	private Empty.Builder deleteEntity(Properties context, DeleteEntityRequest request) {
		Trx.run(transactionName -> {
			PO entity = RecordUtil.getEntity(context, request.getTableName(), request.getUuid(), request.getId(), transactionName);
			if(entity != null
					&& entity.get_ID() >= 0) {
				entity.deleteEx(true);
			}
		});
		//	Return
		return Empty.newBuilder();
	}
	
	/**
	 * Create Entity
	 * @param context
	 * @param request
	 * @return
	 */
	private Entity.Builder createEntity(Properties context, CreateEntityRequest request) {
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		String tableName = request.getTableName();
		MTable table = MTable.get(context, tableName);
		PO entity = table.getPO(0, null);
		if(entity == null) {
			throw new AdempiereException("@Error@ PO is null");
		}
		request.getAttributesList().forEach(attribute -> {
			int referenceId = getReferenceId(entity.get_Table_ID(), attribute.getKey());
			Object value = null;
			if(referenceId > 0) {
				value = ValueUtil.getObjectFromReference(attribute.getValue(), referenceId);
			} 
			if(value == null) {
				value = ValueUtil.getObjectFromValue(attribute.getValue());
			}
			entity.set_ValueOfColumn(attribute.getKey(), value);
		});
		//	Save entity
		entity.saveEx();
		//	Return
		return ConvertUtil.convertEntity(entity);
	}
	
	/**
	 * Update Entity
	 * @param context
	 * @param request
	 * @return
	 */
	private Entity.Builder updateEntity(Properties context, UpdateEntityRequest request) {
		PO entity = RecordUtil.getEntity(context, request.getTableName(), request.getUuid(), request.getId(), null);
		if(entity != null
				&& entity.get_ID() >= 0) {
			request.getAttributesList().forEach(attribute -> {
				int referenceId = getReferenceId(entity.get_Table_ID(), attribute.getKey());
				Object value = null;
				if(referenceId > 0) {
					value = ValueUtil.getObjectFromReference(attribute.getValue(), referenceId);
				} 
				if(value == null) {
					value = ValueUtil.getObjectFromValue(attribute.getValue());
				}
				entity.set_ValueOfColumn(attribute.getKey(), value);
			});
			//	Save entity
			entity.saveEx();
		}
		//	Return
		return ConvertUtil.convertEntity(entity);
	}
	
	/**
	 * Get reference from column name and table
	 * @param tableId
	 * @param columnName
	 * @return
	 */
	private int getReferenceId(int tableId, String columnName) {
		MColumn column = MTable.get(Env.getCtx(), tableId).getColumn(columnName);
		if(column == null) {
			return -1;
		}
		return column.getAD_Reference_ID();
	}
	
	/**
	 * Convert Object to list
	 * @param request
	 * @return
	 */
	private ListEntitiesResponse.Builder convertEntitiesList(Properties context, ListEntitiesRequest request) {
		Criteria criteria = request.getCriteria();
		StringBuffer whereClause = new StringBuffer();
		List<Object> params = new ArrayList<>();
		if(!Util.isEmpty(criteria.getWhereClause())) {
			whereClause.append("(").append(criteria.getWhereClause()).append(")");
		}
		//	For dynamic condition
		String dynamicWhere = ValueUtil.getWhereClauseFromCriteria(criteria, params);
		if(!Util.isEmpty(dynamicWhere)) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
			//	Add
			whereClause.append(dynamicWhere);
		}
		//	Add from reference
		if(!Util.isEmpty(criteria.getReferenceUuid())) {
			String referenceWhereClause = referenceWhereClauseCache.get(criteria.getReferenceUuid());
			if(!Util.isEmpty(referenceWhereClause)) {
				if(whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append("(").append(referenceWhereClause).append(")");
			}
		}
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		Query query = new Query(context, criteria.getTableName(), whereClause.toString(), null)
				.setParameters(params);
		int count = query.count();
		ListEntitiesResponse.Builder builder = ListEntitiesResponse.newBuilder();
		//	
		if(Util.isEmpty(criteria.getQuery())) {
			if(!Util.isEmpty(criteria.getOrderByClause())) {
				query.setOrderBy(criteria.getOrderByClause());
			}
			List<PO> entityList = query
					.setLimit(limit, offset)
					.<PO>list();
			//	
			for(PO entity : entityList) {
				Entity.Builder valueObject = ConvertUtil.convertEntity(entity);
				builder.addRecords(valueObject.build());
			}
		} else {
			StringBuilder sql = new StringBuilder(criteria.getQuery());
			if (whereClause.length() > 0) {
				sql.append(" WHERE ").append(whereClause); // includes first AND
			}
			//	
			String parsedSQL = MRole.getDefault().addAccessSQL(sql.toString(),
					criteria.getTableName(), MRole.SQL_FULLYQUALIFIED,
					MRole.SQL_RO);
			String orderByClause = criteria.getOrderByClause();
			if(Util.isEmpty(orderByClause)) {
				orderByClause = "";
			} else {
				orderByClause = " ORDER BY " + orderByClause;
			}
			//	Count records
			count = countRecords(context, parsedSQL, criteria.getTableName(), params);
			//	Add Row Number
			parsedSQL = parsedSQL + " AND ROWNUM >= " + offset + " AND ROWNUM <= " + limit;
			//	Add Order By
			parsedSQL = parsedSQL + orderByClause;
			builder = convertListEntitiesResult(MTable.get(context, criteria.getTableName()), parsedSQL, params);
		}
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set netxt page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	Return
		return builder;
	}
	
	/**
	 * Convert Entities List
	 * @param table
	 * @param sql
	 * @return
	 */
	private ListEntitiesResponse.Builder convertListEntitiesResult(MTable table, String sql, List<Object> params) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ListEntitiesResponse.Builder builder = ListEntitiesResponse.newBuilder();
		long recordCount = 0;
		try {
			LinkedHashMap<String, MColumn> columnsMap = new LinkedHashMap<>();
			//	Add field to map
			for(MColumn column: table.getColumnsAsList()) {
				columnsMap.put(column.getColumnName().toUpperCase(), column);
			}
			//	SELECT Key, Value, Name FROM ...
			pstmt = DB.prepareStatement(sql, null);
			AtomicInteger parameterIndex = new AtomicInteger(1);
			for(Object value : params) {
				ValueUtil.setParameterFromObject(pstmt, value, parameterIndex.getAndIncrement());
			} 
			//	Get from Query
			rs = pstmt.executeQuery();
			while(rs.next()) {
				Entity.Builder valueObjectBuilder = Entity.newBuilder();
				ResultSetMetaData metaData = rs.getMetaData();
				for (int index = 1; index <= metaData.getColumnCount(); index++) {
					try {
						String columnName = metaData.getColumnName (index);
						MColumn field = columnsMap.get(columnName.toUpperCase());
						Value.Builder valueBuilder = Value.newBuilder();
						//	Display Columns
						if(field == null) {
							String value = rs.getString(index);
							if(!Util.isEmpty(value)) {
								valueBuilder = ValueUtil.getValueFromString(value);
								valueObjectBuilder.putValues(columnName, valueBuilder.build());
							}
							continue;
						}
						//	From field
						String fieldColumnName = field.getColumnName();
						valueBuilder = ValueUtil.getValueFromReference(rs.getObject(index), field.getAD_Reference_ID());
						if(!valueBuilder.getValueType().equals(Value.ValueType.UNRECOGNIZED)) {
							valueObjectBuilder.putValues(fieldColumnName, valueBuilder.build());
						}
					} catch (Exception e) {
						log.severe(e.getLocalizedMessage());
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
	 * Count records
	 * @param context
	 * @param sql
	 * @param tableName
	 * @param parameters
	 * @return
	 */
	private int countRecords(Properties context, String sql, String tableName, List<Object> parameters) {
		int positionFrom = sql.lastIndexOf(" FROM " + tableName);
		String queryCount = "SELECT COUNT(*) " + sql.substring(positionFrom, sql.length());
		return DB.getSQLValueEx(null, queryCount, parameters);
	}
}

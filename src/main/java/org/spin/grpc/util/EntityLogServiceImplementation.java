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
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.MBrowse;
import org.compiere.model.I_AD_ChangeLog;
import org.compiere.model.I_AD_PInstance;
import org.compiere.model.I_AD_PInstance_Log;
import org.compiere.model.I_AD_WF_EventAudit;
import org.compiere.model.I_AD_WF_Process;
import org.compiere.model.I_CM_Chat;
import org.compiere.model.I_CM_ChatEntry;
import org.compiere.model.I_C_Order;
import org.compiere.model.MChangeLog;
import org.compiere.model.MChat;
import org.compiere.model.MChatEntry;
import org.compiere.model.MChatType;
import org.compiere.model.MColumn;
import org.compiere.model.MForm;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MMenu;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MProcess;
import org.compiere.model.MRecentItem;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MWindow;
import org.compiere.model.M_Element;
import org.compiere.model.Query;
import org.compiere.model.X_AD_PInstance_Log;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.NamePair;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;
import org.compiere.wf.MWFEventAudit;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFProcess;
import org.compiere.wf.MWFResponsible;
import org.compiere.wf.MWorkflow;
import org.spin.grpc.util.ChatEntry.ModeratorStatus;
import org.spin.grpc.util.EntityLogGrpc.EntityLogImplBase;
import org.spin.grpc.util.RecordChat.ConfidentialType;
import org.spin.grpc.util.RecordChat.ModerationType;
import org.spin.grpc.util.WorkflowProcess.WorkflowState;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * https://itnext.io/customizing-grpc-generated-code-5909a2551ca1
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Business data service
 */
public class EntityLogServiceImplementation extends EntityLogImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(EntityLogServiceImplementation.class);
	/** Date Time Format		*/
	private SimpleDateFormat	dateTimeFormat = DisplayType.getDateFormat
		(DisplayType.DateTime, Env.getLanguage(Env.getCtx()));
	/** Date Format			*/
	private SimpleDateFormat	dateFormat = DisplayType.getDateFormat
		(DisplayType.DateTime, Env.getLanguage(Env.getCtx()));
	/** Number Format		*/
	private DecimalFormat		numberFormat = DisplayType.getNumberFormat
		(DisplayType.Number, Env.getLanguage(Env.getCtx()));
	/** Amount Format		*/
	private DecimalFormat		amountFormat = DisplayType.getNumberFormat
		(DisplayType.Amount, Env.getLanguage(Env.getCtx()));
	/** Number Format		*/
	private DecimalFormat		intFormat = DisplayType.getNumberFormat
		(DisplayType.Integer, Env.getLanguage(Env.getCtx()));
	
	@Override
	public void listProcessLogs(ListProcessLogsRequest request, StreamObserver<ListProcessLogsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Process Activity Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListProcessLogsResponse.Builder entityValueList = convertProcessLogs(context, request);
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
	public void listRecentItems(ListRecentItemsRequest request, StreamObserver<ListRecentItemsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Process Activity Requested is Null");
			}
			log.fine("Recent Items Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListRecentItemsResponse.Builder entityValueList = convertRecentItems(context, request);
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
	public void listRecordLogs(ListRecordLogsRequest request, StreamObserver<ListRecordLogsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Record Logs Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListRecordLogsResponse.Builder entityValueList = convertRecordLogs(context, request);
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
	public void listWorkflowLogs(ListWorkflowLogsRequest request,
			StreamObserver<ListWorkflowLogsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Workflow Logs Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListWorkflowLogsResponse.Builder entityValueList = convertWorkflowLogs(context, request);
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
	public void listRecordChats(ListRecordChatsRequest request,
			StreamObserver<ListRecordChatsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Record Chats Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListRecordChatsResponse.Builder entityValueList = convertRecordChats(context, request);
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
	public void listChatEntries(ListChatEntriesRequest request,
			StreamObserver<ListChatEntriesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Chat Entries Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListChatEntriesResponse.Builder entityValueList = convertChatEntries(context, request);
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
	
	/**
	 * Convert request for process log to builder
	 * @param context
	 * @param request
	 * @return
	 */
	private ListProcessLogsResponse.Builder convertProcessLogs(Properties context, ListProcessLogsRequest request) {
		String sql = null;
		List<Object> parameters = new ArrayList<>();
		if(!Util.isEmpty(request.getTableName())) {
			MTable table = MTable.get(context, request.getTableName());
			if(table == null
					|| table.getAD_Table_ID() == 0) {
				throw new AdempiereException("@AD_Table_ID@ @Invalid@");
			}
			parameters.add(request.getRecordId());
			parameters.add(table.getAD_Table_ID());
			sql = "Record_ID = ? AND EXISTS(SELECT 1 FROM AD_Process WHERE AD_Table_ID = ? AND AD_Process_ID = AD_PInstance.AD_Process_ID)";
		} if(!Util.isEmpty(request.getUserUuid())) {
			parameters.add(request.getUserUuid());
			sql = "EXISTS(SELECT 1 FROM AD_User WHERE UUID = ? AND AD_User_ID = AD_PInstance.AD_User_ID)";
		} else if(!Util.isEmpty(request.getInstanceUuid())) {
			parameters.add(request.getInstanceUuid());
			sql = "UUID = ?";
		} else {
			parameters.add(request.getClientRequest().getSessionUuid());
			sql = "EXISTS(SELECT 1 FROM AD_Session WHERE UUID = ? AND AD_Session_ID = AD_PInstance.AD_Session_ID)";
		}
		List<MPInstance> processInstanceList = new Query(context, I_AD_PInstance.Table_Name, 
				sql, null)
				.setParameters(parameters)
				.setOrderBy(I_AD_PInstance.COLUMNNAME_Created + " DESC")
				.<MPInstance>list();
		//	
		ListProcessLogsResponse.Builder builder = ListProcessLogsResponse.newBuilder();
		//	Convert Process Instance
		for(MPInstance processInstance : processInstanceList) {
			ProcessLog.Builder valueObject = convertProcessLog(processInstance);
			builder.addProcessLogs(valueObject.build());
		}
		//	Return
		return builder;
	}
	
	/**
	 * Convert request for workflow log to builder
	 * @param context
	 * @param request
	 * @return
	 */
	private ListWorkflowLogsResponse.Builder convertWorkflowLogs(Properties context, ListWorkflowLogsRequest request) {
		StringBuffer whereClause = new StringBuffer();
		List<Object> parameters = new ArrayList<>();
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		//	
		MTable table = MTable.get(context, request.getTableName());
		if(table == null
				|| table.getAD_Table_ID() == 0) {
			throw new AdempiereException("@AD_Table_ID@ @Invalid@");
		}
		whereClause
			.append(I_AD_WF_Process.COLUMNNAME_AD_Table_ID).append(" = ?")
			.append(" AND ")
			.append(I_AD_WF_Process.COLUMNNAME_Record_ID).append(" = ?");
		//	Set parameters
		parameters.add(table.getAD_Table_ID());
		parameters.add(request.getRecordId());
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		Query query = new Query(context, I_AD_WF_Process.Table_Name, whereClause.toString(), null)
				.setParameters(parameters);
		int count = query.count();
		List<MWFProcess> workflowProcessLogList = query
				.setLimit(limit, offset)
				.<MWFProcess>list();
		//	
		ListWorkflowLogsResponse.Builder builder = ListWorkflowLogsResponse.newBuilder();
		//	Convert Record Log
		for(MWFProcess workflowProcessLog : workflowProcessLogList) {
			WorkflowProcess.Builder valueObject = convertWorkflowLog(workflowProcessLog);
			builder.addWorkflowLogs(valueObject.build());
		}
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	Return
		return builder;
	}
	
	/**
	 * Convert PO class from Workflow process to builder
	 * @param workflowProcess
	 * @return
	 */
	private WorkflowProcess.Builder convertWorkflowLog(MWFProcess workflowProcess) {
		MTable table = MTable.get(workflowProcess.getCtx(), workflowProcess.getAD_Table_ID());
		WorkflowProcess.Builder builder = WorkflowProcess.newBuilder();
		builder.setProcessUuid(ValueUtil.validateNull(workflowProcess.getUUID()));
		MWorkflow workflow = MWorkflow.get(workflowProcess.getCtx(), workflowProcess.getAD_Workflow_ID());
		builder.setWorkflowUuid(ValueUtil.validateNull(workflow.getUUID()));
		String workflowName = workflow.getName();
		if(!Env.isBaseLanguage(workflowProcess.getCtx(), "")) {
			String translation = workflow.get_Translation(MWorkflow.COLUMNNAME_Name);
			if(!Util.isEmpty(translation)) {
				workflowName = translation;
			}
		}
		if(workflowProcess.getAD_WF_Responsible_ID() != 0) {
			MWFResponsible responsible = MWFResponsible.get(workflowProcess.getCtx(), workflowProcess.getAD_WF_Responsible_ID());
			builder.setResponsibleUuid(ValueUtil.validateNull(responsible.getUUID()));
			builder.setResponsibleName(ValueUtil.validateNull(responsible.getName()));
		}
		if(workflowProcess.getAD_User_ID() != 0) {
			MUser user = MUser.get(workflowProcess.getCtx(), workflowProcess.getAD_User_ID());
			builder.setUserUuid(ValueUtil.validateNull(user.getUUID()));
			builder.setUserName(ValueUtil.validateNull(user.getName()));
		}
		builder.setWorkflowName(ValueUtil.validateNull(workflowName));
		builder.setRecordId(workflowProcess.getRecord_ID());
		builder.setTableName(ValueUtil.validateNull(table.getTableName()));
		builder.setTextMessage(ValueUtil.validateNull(Msg.parseTranslation(workflowProcess.getCtx(), workflowProcess.getTextMsg())));
		builder.setProcessed(workflowProcess.isProcessed());
		builder.setLogDate(workflowProcess.getCreated().getTime());
		//	State
		if(!Util.isEmpty(workflowProcess.getWFState())) {
			if(workflowProcess.getWFState().equals(MWFProcess.WFSTATE_Running)) {
				builder.setWorkflowState(WorkflowState.RUNNING);
			} else if(workflowProcess.getWFState().equals(MWFProcess.WFSTATE_Completed)) {
				builder.setWorkflowState(WorkflowState.COMPLETED);
			} else if(workflowProcess.getWFState().equals(MWFProcess.WFSTATE_Aborted)) {
				builder.setWorkflowState(WorkflowState.ABORTED);
			} else if(workflowProcess.getWFState().equals(MWFProcess.WFSTATE_Terminated)) {
				builder.setWorkflowState(WorkflowState.TERMINATED);
			} else if(workflowProcess.getWFState().equals(MWFProcess.WFSTATE_Suspended)) {
				builder.setWorkflowState(WorkflowState.SUSPENDED);
			} else if(workflowProcess.getWFState().equals(MWFProcess.WFSTATE_NotStarted)) {
				builder.setWorkflowState(WorkflowState.NOT_STARTED);
			}
		}
		builder.setPriorityValue(workflowProcess.getPriority());
		//	Get Events
		List<MWFEventAudit> workflowEventsList = new Query(workflowProcess.getCtx(), I_AD_WF_EventAudit.Table_Name, I_AD_WF_EventAudit.COLUMNNAME_AD_WF_Process_ID + " = ?", null)
			.setParameters(workflowProcess.getAD_WF_Process_ID())
			.<MWFEventAudit>list();
		//	populate
		for(MWFEventAudit eventAudit : workflowEventsList) {
			WorkflowEvent.Builder valueObject = convertWorkflowEventAudit(eventAudit);
			builder.addWorkflowEvents(valueObject.build());
		}
  		return builder;
	}
	
	/**
	 * Convert PO class from Workflow event audit to builder
	 * @param workflowEventAudit
	 * @return
	 */
	private WorkflowEvent.Builder convertWorkflowEventAudit(MWFEventAudit workflowEventAudit) {
		MTable table = MTable.get(workflowEventAudit.getCtx(), workflowEventAudit.getAD_Table_ID());
		WorkflowEvent.Builder builder = WorkflowEvent.newBuilder();
		MWFNode node = MWFNode.get(workflowEventAudit.getCtx(), workflowEventAudit.getAD_WF_Node_ID());
		builder.setNodeUuid(ValueUtil.validateNull(node.getUUID()));
		String nodeName = node.getName();
		if(!Env.isBaseLanguage(workflowEventAudit.getCtx(), "")) {
			String translation = node.get_Translation(MWFNode.COLUMNNAME_Name);
			if(!Util.isEmpty(translation)) {
				nodeName = translation;
			}
		}
		builder.setNodeName(ValueUtil.validateNull(nodeName));
		if(workflowEventAudit.getAD_WF_Responsible_ID() != 0) {
			MWFResponsible responsible = MWFResponsible.get(workflowEventAudit.getCtx(), workflowEventAudit.getAD_WF_Responsible_ID());
			builder.setResponsibleUuid(ValueUtil.validateNull(responsible.getUUID()));
			builder.setResponsibleName(ValueUtil.validateNull(responsible.getName()));
		}
		if(workflowEventAudit.getAD_User_ID() != 0) {
			MUser user = MUser.get(workflowEventAudit.getCtx(), workflowEventAudit.getAD_User_ID());
			builder.setUserUuid(ValueUtil.validateNull(user.getUUID()));
			builder.setUserName(ValueUtil.validateNull(user.getName()));
		}
		builder.setRecordId(workflowEventAudit.getRecord_ID());
		builder.setTableName(ValueUtil.validateNull(table.getTableName()));
		builder.setTextMessage(ValueUtil.validateNull(Msg.parseTranslation(workflowEventAudit.getCtx(), workflowEventAudit.getTextMsg())));
		builder.setLogDate(workflowEventAudit.getCreated().getTime());
		if(workflowEventAudit.getElapsedTimeMS() != null) {
			builder.setTimeElapsed(workflowEventAudit.getElapsedTimeMS().longValue());
		}
		//	State
		if(!Util.isEmpty(workflowEventAudit.getWFState())) {
			if(workflowEventAudit.getWFState().equals(MWFProcess.WFSTATE_Running)) {
				builder.setWorkflowState(org.spin.grpc.util.WorkflowEvent.WorkflowState.RUNNING);
			} else if(workflowEventAudit.getWFState().equals(MWFProcess.WFSTATE_Completed)) {
				builder.setWorkflowState(org.spin.grpc.util.WorkflowEvent.WorkflowState.COMPLETED);
			} else if(workflowEventAudit.getWFState().equals(MWFProcess.WFSTATE_Aborted)) {
				builder.setWorkflowState(org.spin.grpc.util.WorkflowEvent.WorkflowState.ABORTED);
			} else if(workflowEventAudit.getWFState().equals(MWFProcess.WFSTATE_Terminated)) {
				builder.setWorkflowState(org.spin.grpc.util.WorkflowEvent.WorkflowState.TERMINATED);
			} else if(workflowEventAudit.getWFState().equals(MWFProcess.WFSTATE_Suspended)) {
				builder.setWorkflowState(org.spin.grpc.util.WorkflowEvent.WorkflowState.SUSPENDED);
			} else if(workflowEventAudit.getWFState().equals(MWFProcess.WFSTATE_NotStarted)) {
				builder.setWorkflowState(org.spin.grpc.util.WorkflowEvent.WorkflowState.NOT_STARTED);
			}
		}
		//	
		builder.setAttributeName(ValueUtil.validateNull(workflowEventAudit.getAttributeName()));
		builder.setOldValue(ValueUtil.validateNull(workflowEventAudit.getOldValue()));
		builder.setNewValue(ValueUtil.validateNull(workflowEventAudit.getNewValue()));
  		return builder;
	}
	
	/**
	 * Convert request for record log to builder
	 * @param context
	 * @param request
	 * @return
	 */
	private ListRecordLogsResponse.Builder convertRecordLogs(Properties context, ListRecordLogsRequest request) {
		StringBuffer whereClause = new StringBuffer();
		List<Object> parameters = new ArrayList<>();
		if(!Util.isEmpty(request.getTableName())) {
			MTable table = MTable.get(context, request.getTableName());
			if(table == null
					|| table.getAD_Table_ID() == 0) {
				throw new AdempiereException("@AD_Table_ID@ @Invalid@");
			}
			whereClause
				.append(I_AD_ChangeLog.COLUMNNAME_AD_Table_ID).append(" = ?")
				.append(" AND ")
				.append(I_AD_ChangeLog.COLUMNNAME_Record_ID).append(" = ?");
			//	Set parameters
			parameters.add(table.getAD_Table_ID());
			parameters.add(request.getRecordId());
		} else {
			whereClause.append("EXISTS(SELECT 1 FROM AD_Session WHERE UUID = ? AND AD_Session_ID = AD_ChangeLog.AD_Session_ID)");
			parameters.add(request.getClientRequest().getSessionUuid());
		}
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		Query query = new Query(context, I_AD_ChangeLog.Table_Name, whereClause.toString(), null)
				.setParameters(parameters);
		int count = query.count();
		List<MChangeLog> recordLogList = query
				.setLimit(limit, offset)
				.<MChangeLog>list();
		//	Convert Record Log
		ListRecordLogsResponse.Builder builder = convertRecordLog(recordLogList);
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	Return
		return builder;
	}
	
	/**
	 * Convert a change log for a set of changes to builder
	 * @param recordLog
	 * @return
	 */
	private RecordLog.Builder convertRecordLogHeader(MChangeLog recordLog) {
		MTable table = MTable.get(recordLog.getCtx(), recordLog.getAD_Table_ID());
		MUser user = MUser.get(recordLog.getCtx(), recordLog.getCreatedBy());
		RecordLog.Builder builder = RecordLog.newBuilder();
		builder.setLogId(recordLog.getAD_ChangeLog_ID());
		builder.setRecordId(recordLog.getRecord_ID());
		builder.setTableName(ValueUtil.validateNull(table.getTableName()));
		builder.setSessionUuid(ValueUtil.validateNull(recordLog.getAD_Session().getUUID()));
		builder.setUserUuid(ValueUtil.validateNull(user.getUUID()));
		builder.setUserName(ValueUtil.validateNull(user.getName()));
		builder.setTransactionName(ValueUtil.validateNull(recordLog.getTrxName()));
		builder.setLogDate(recordLog.getCreated().getTime());
		if(recordLog.getEventChangeLog().endsWith(MChangeLog.EVENTCHANGELOG_Insert)) {
			builder.setEventType(org.spin.grpc.util.RecordLog.EventType.INSERT);
		} else if(recordLog.getEventChangeLog().endsWith(MChangeLog.EVENTCHANGELOG_Update)) {
			builder.setEventType(org.spin.grpc.util.RecordLog.EventType.UPDATE);
		} else if(recordLog.getEventChangeLog().endsWith(MChangeLog.EVENTCHANGELOG_Delete)) {
			builder.setEventType(org.spin.grpc.util.RecordLog.EventType.DELETE);
		}
		//	Return
		return builder;
	}
	
	/**
	 * Convert PO class from change log  list to builder
	 * @param recordLog
	 * @return
	 */
	private ListRecordLogsResponse.Builder convertRecordLog(List<MChangeLog> recordLogList) {
		Map<Integer, RecordLog.Builder> indexMap = new HashMap<Integer, RecordLog.Builder>();
		recordLogList.stream().filter(recordLog -> !indexMap.containsKey(recordLog.getAD_ChangeLog_ID())).forEach(recordLog -> {
			indexMap.put(recordLog.getAD_ChangeLog_ID(), convertRecordLogHeader(recordLog));
		});
		//	convert changes
		recordLogList.forEach(recordLog -> {
			ChangeLog.Builder changeLog = convertChangeLog(recordLog);
			RecordLog.Builder recordLogBuilder = indexMap.get(recordLog.getAD_ChangeLog_ID());
			recordLogBuilder.addChangeLogs(changeLog);
			indexMap.put(recordLog.getAD_ChangeLog_ID(), recordLogBuilder);
		});
		ListRecordLogsResponse.Builder builder = ListRecordLogsResponse.newBuilder();
		indexMap.values().stream().forEach(recordLog -> builder.addRecordLogs(recordLog));
		return builder;
	}
	
	/**
	 * Convert PO class from change log to builder
	 * @param recordLog
	 * @return
	 */
	private ChangeLog.Builder convertChangeLog(MChangeLog recordLog) {
		ChangeLog.Builder builder = ChangeLog.newBuilder();
		MColumn column = MColumn.get(recordLog.getCtx(), recordLog.getAD_Column_ID());
		builder.setColumnName(ValueUtil.validateNull(column.getColumnName()));
		String displayColumnName = column.getName();
		if(column.getColumnName().equals("ProcessedOn")) {
			M_Element element = M_Element.get(recordLog.getCtx(), "LastRun");
			displayColumnName = element.getName();
			if(!Env.isBaseLanguage(recordLog.getCtx(), "")) {
				String translation = element.get_Translation(MColumn.COLUMNNAME_Name);
				if(!Util.isEmpty(translation)) {
					displayColumnName = translation;
				}
			}
		} else {
			if(!Env.isBaseLanguage(recordLog.getCtx(), "")) {
				String translation = column.get_Translation(MColumn.COLUMNNAME_Name);
				if(!Util.isEmpty(translation)) {
					displayColumnName = translation;
				}
			}
		}
		builder.setDisplayColumnName(ValueUtil.validateNull(displayColumnName));
		builder.setDescription(ValueUtil.validateNull(recordLog.getDescription()));
		String oldValue = recordLog.getOldValue();
		String newValue = recordLog.getNewValue();
		//	Set Old Value
		builder.setOldValue(ValueUtil.validateNull(oldValue));
		builder.setNewValue(ValueUtil.validateNull(newValue));
		//	Set Display Values
		if (oldValue != null && oldValue.equals(MChangeLog.NULL))
			oldValue = null;
		String displayOldValue = oldValue;
		if (newValue != null && newValue.equals(MChangeLog.NULL))
			newValue = null;
		String displayNewValue = newValue;
		//
		try {
			if (DisplayType.isText (column.getAD_Reference_ID ())) {
				;
			} else if (column.getAD_Reference_ID() == DisplayType.YesNo) {
				if (oldValue != null) {
					boolean yes = oldValue.equals("true") || oldValue.equals("Y");
					displayOldValue = Msg.getMsg(Env.getCtx(), yes ? "Y" : "N");
				}
				if (newValue != null) {
					boolean yes = newValue.equals("true") || newValue.equals("Y");
					displayNewValue = Msg.getMsg(Env.getCtx(), yes ? "Y" : "N");
				}
			} else if (column.getAD_Reference_ID() == DisplayType.Amount) {
				if (oldValue != null)
					displayOldValue = amountFormat
						.format (new BigDecimal (oldValue));
				if (newValue != null)
					displayNewValue = amountFormat
						.format (new BigDecimal (newValue));
			} else if (column.getAD_Reference_ID() == DisplayType.Integer) {
				if (oldValue != null)
					displayOldValue = intFormat.format (new Integer (oldValue));
				if (newValue != null)
					displayNewValue = intFormat.format (new Integer (newValue));
			} else if (DisplayType.isNumeric (column.getAD_Reference_ID ())) {
				if(column.getColumnName().equals(I_C_Order.COLUMNNAME_ProcessedOn)) {
					if (oldValue != null) {
						if(oldValue.indexOf(".") > 0) {
							oldValue = oldValue.substring(0, oldValue.indexOf("."));
						}
						displayOldValue = TimeUtil.formatElapsed(System.currentTimeMillis() - new BigDecimal (oldValue).longValue());
					}
					if (newValue != null) {
						if(newValue.indexOf(".") > 0) {
							newValue = newValue.substring(0, newValue.indexOf("."));
						}
						displayNewValue = TimeUtil.formatElapsed(System.currentTimeMillis() - new BigDecimal (newValue).longValue());
					}
				} else {
					if (oldValue != null)
						displayOldValue = numberFormat.format (new BigDecimal (oldValue));
					if (newValue != null)
						displayNewValue = numberFormat.format (new BigDecimal (newValue));
				}
			} else if (column.getAD_Reference_ID() == DisplayType.Date) {
				if (oldValue != null)
					displayOldValue = dateFormat.format (Timestamp.valueOf (oldValue));
				if (newValue != null)
					displayNewValue = dateFormat.format (Timestamp.valueOf (newValue));
			} else if (column.getAD_Reference_ID() == DisplayType.DateTime) {
				if (oldValue != null)
					displayOldValue = dateTimeFormat.format (Timestamp.valueOf (oldValue));
				if (newValue != null)
					displayNewValue = dateTimeFormat.format (Timestamp.valueOf (newValue));
			} else if (DisplayType.isLookup(column.getAD_Reference_ID())
					&& column.getAD_Reference_ID() != DisplayType.Button
					&& column.getAD_Reference_ID() != DisplayType.List) {
				MLookup lookup = MLookupFactory.get (Env.getCtx(), 0,
						column.getAD_Column_ID(), column.getAD_Reference_ID(),
					Env.getLanguage(Env.getCtx()), column.getColumnName(),
					column.getAD_Reference_Value_ID(),
					column.isParent(), null);
				if (oldValue != null) {
					Object key = oldValue; 
					NamePair pp = lookup.get(key);
					if (pp != null)
						displayOldValue = pp.getName();
				}
				if (newValue != null) {
					Object key = newValue; 
					NamePair pp = lookup.get(key);
					if (pp != null)
						displayNewValue = pp.getName();
				}
			} else if((DisplayType.Button == column.getAD_Reference_ID()
					|| DisplayType.List == column.getAD_Reference_ID())
					&& column.getAD_Reference_Value_ID() != 0) {
				MLookupInfo lookupInfo = MLookupFactory.getLookup_List(Env.getLanguage(Env.getCtx()), column.getAD_Reference_Value_ID());
				MLookup lookup = new MLookup(lookupInfo, 0);
				if (oldValue != null) {
					Object key = oldValue; 
					NamePair pp = lookup.get(key);
					if (pp != null)
						displayOldValue = pp.getName();
				}
				if (newValue != null) {
					Object key = newValue; 
					NamePair pp = lookup.get(key);
					if (pp != null)
						displayNewValue = pp.getName();
				}
			} else if (DisplayType.isLOB (column.getAD_Reference_ID ())) {
				;
			}
		} catch (Exception e) {
			log.log(Level.WARNING, oldValue + "->" + newValue, e);
		}
		//	Set display values
		builder.setOldDisplayValue(ValueUtil.validateNull(displayOldValue));
		builder.setNewDisplayValue(ValueUtil.validateNull(displayNewValue));
		return builder;
	}
	
	/**
	 * Convert Recent Items
	 * @param context
	 * @param request
	 * @return
	 */
	private ListRecentItemsResponse.Builder convertRecentItems(Properties context, ListRecentItemsRequest request) {
		ListRecentItemsResponse.Builder builder = ListRecentItemsResponse.newBuilder();
		List<MRecentItem> recentItemsList = MRecentItem.getFromUserAndRole(context);
		if(recentItemsList != null) {
			for(MRecentItem recentItem : recentItemsList) {
				try {
					RecentItem.Builder recentItemBuilder = RecentItem.newBuilder()
							.setRecordId(recentItem.getRecord_ID())
							.setTableId(recentItem.getAD_Table_ID())
							.setDisplayName(ValueUtil.validateNull(recentItem.getLabel()));
					String menuName = "";
					String menuDescription = "";
					String referenceUuid = null;
					if(recentItem.getAD_Tab_ID() > 0) {
						MTab tab = MTab.get(context, recentItem.getAD_Tab_ID());
						recentItemBuilder.setTabUuid(ValueUtil.validateNull(tab.getUUID()));
						menuName = tab.getName();
						menuDescription = tab.getDescription();
						if(!Env.isBaseLanguage(context, "")) {
							menuName = tab.get_Translation("Name");
							menuDescription = tab.get_Translation("Description");
						}
						//	Add Action
						recentItemBuilder.setAction(ValueUtil.validateNull(MMenu.ACTION_Window));
					}
					if(recentItem.getAD_Window_ID() > 0) {
						MWindow window = MWindow.get(context, recentItem.getAD_Window_ID());
						recentItemBuilder.setWindowUuid(ValueUtil.validateNull(window.getUUID()));
						menuName = window.getName();
						menuDescription = window.getDescription();
						referenceUuid = window.getUUID();
						if(!Env.isBaseLanguage(context, "")) {
							menuName = window.get_Translation("Name");
							menuDescription = window.get_Translation("Description");
						}
						//	Add Action
						recentItemBuilder.setAction(ValueUtil.validateNull(MMenu.ACTION_Window));
					}
					if(recentItem.getAD_Menu_ID() > 0) {
						MMenu menu = MMenu.getFromId(context, recentItem.getAD_Menu_ID());
						recentItemBuilder.setMenuUuid(ValueUtil.validateNull(menu.getUUID()));
						if(!menu.isCentrallyMaintained()) {
							menuName = menu.getName();
							menuDescription = menu.getDescription();
							if(!Env.isBaseLanguage(context, "")) {
								menuName = menu.get_Translation("Name");
								menuDescription = menu.get_Translation("Description");
							}
						}
						//	Add Action
						recentItemBuilder.setAction(ValueUtil.validateNull(menu.getAction()));
						//	Supported actions
						if(!Util.isEmpty(menu.getAction())) {
							if(menu.getAction().equals(MMenu.ACTION_Form)) {
								if(menu.getAD_Form_ID() > 0) {
									MForm form = new MForm(context, menu.getAD_Form_ID(), null);
									referenceUuid = form.getUUID();
								}
							} else if(menu.getAction().equals(MMenu.ACTION_Window)) {
								if(menu.getAD_Window_ID() > 0) {
									MWindow window = new MWindow(context, menu.getAD_Window_ID(), null);
									referenceUuid = window.getUUID();
								}
							} else if(menu.getAction().equals(MMenu.ACTION_Process)
								|| menu.getAction().equals(MMenu.ACTION_Report)) {
								if(menu.getAD_Process_ID() > 0) {
									MProcess process = MProcess.get(context, menu.getAD_Process_ID());
									referenceUuid = process.getUUID();
								}
							} else if(menu.getAction().equals(MMenu.ACTION_SmartBrowse)) {
								if(menu.getAD_Browse_ID() > 0) {
									MBrowse smartBrowser = MBrowse.get(context, menu.getAD_Browse_ID());
									referenceUuid = smartBrowser.getUUID();
								}
							}
						}
					}
					//	Add time
					recentItemBuilder.setMenuName(ValueUtil.validateNull(menuName));
					recentItemBuilder.setMenuDescription(ValueUtil.validateNull(menuDescription));
					recentItemBuilder.setUpdated(recentItem.getUpdated().getTime());
					recentItemBuilder.setReferenceUuid(ValueUtil.validateNull(referenceUuid));
					//	For uuid
					if(recentItem.getAD_Table_ID() != 0
							&& recentItem.getRecord_ID() != 0) {
						MTable table = MTable.get(context, recentItem.getAD_Table_ID());
						if(table != null
								&& table.getAD_Table_ID() != 0) {
							recentItemBuilder.setRecordUuid(ValueUtil.validateNull(table.getPO(recentItem.getRecord_ID(), null).get_UUID()));
						}
					}
					//	
					builder.addRecentItems(recentItemBuilder.build());	
				} catch (Exception e) {
					log.severe(e.getLocalizedMessage());
				}
			}
		}
		//	Return
		return builder;
	}
	
	/**
	 * Convert Process Instance
	 * @param instance
	 * @return
	 */
	private ProcessLog.Builder convertProcessLog(MPInstance instance) {
		ProcessLog.Builder builder = ProcessLog.newBuilder();
		builder.setInstanceUuid(ValueUtil.validateNull(instance.getUUID()));
		builder.setIsError(!instance.isOK());
		builder.setIsProcessing(instance.isProcessing());
		builder.setLastRun(instance.getUpdated().getTime());
		String summary = instance.getErrorMsg();
		if(!Util.isEmpty(summary)) {
			summary = Msg.parseTranslation(Env.getCtx(), summary);
		}
		//	for report
		MProcess process = MProcess.get(Env.getCtx(), instance.getAD_Process_ID());
		builder.setUuid(ValueUtil.validateNull(process.getUUID()));
		if(process.isReport()) {
			ReportOutput.Builder outputBuilder = ReportOutput.newBuilder();
			outputBuilder.setReportType(ValueUtil.validateNull(instance.getReportType()));
			outputBuilder.setName(ValueUtil.validateNull(instance.getName()));
			builder.setOutput(outputBuilder.build());
		}
		builder.setSummary(ValueUtil.validateNull(summary));
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
			logBuilder.setLog(ValueUtil.validateNull((message)));
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
					parameterBuilder = ValueUtil.getValueFromInteger(number.intValue());
				}
				if(numberTo != null
						&& !numberTo.equals(Env.ZERO)) {
					hasToParameter = true;
					parameterBuilder = ValueUtil.getValueFromInteger(numberTo.intValue());
				}
			} else if(DisplayType.isNumeric(displayType)) {
				BigDecimal number = parameter.getP_Number();
				BigDecimal numberTo = parameter.getP_Number_To();
				//	Validate
				if(number != null 
						&& !number.equals(Env.ZERO)) {
					hasFromParameter = true;
					parameterBuilder = ValueUtil.getValueFromDecimal(number);
				}
				if(numberTo != null
						&& !numberTo.equals(Env.ZERO)) {
					hasToParameter = true;
					parameterBuilder = ValueUtil.getValueFromDecimal(numberTo);
				}
			} else if(DisplayType.isDate(displayType)) {
				Timestamp date = parameter.getP_Date();
				Timestamp dateTo = parameter.getP_Date_To();
				//	Validate
				if(date != null) {
					hasFromParameter = true;
					parameterBuilder = ValueUtil.getValueFromDate(date);
				}
				if(dateTo != null) {
					hasToParameter = true;
					parameterBuilder = ValueUtil.getValueFromDate(dateTo);
				}
			} else if(DisplayType.YesNo == displayType) {
				String value = parameter.getP_String();
				if(!Util.isEmpty(value)) {
					hasFromParameter = true;
					parameterBuilder = ValueUtil.getValueFromBoolean(!Util.isEmpty(value) && value.equals("Y"));
				}
			} else {
				String value = parameter.getP_String();
				String valueTo = parameter.getP_String_To();
				//	Validate
				if(!Util.isEmpty(value)) {
					hasFromParameter = true;
					parameterBuilder = ValueUtil.getValueFromString(value);
				}
				if(!Util.isEmpty(valueTo)) {
					hasToParameter = true;
					parameterBuilder = ValueUtil.getValueFromString(valueTo);
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
	 * Convert request for record chats to builder
	 * @param context
	 * @param request
	 * @return
	 */
	private ListChatEntriesResponse.Builder convertChatEntries(Properties context, ListChatEntriesRequest request) {
		if(Util.isEmpty(request.getChatUuid())) {
			throw new AdempiereException("@CM_Chat_ID@ @NotFound@");
		}
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		Query query = new Query(context, I_CM_ChatEntry.Table_Name, "EXISTS(SELECT 1 FROM CM_Chat c WHERE c.CM_Chat_ID = CM_ChatEntry.CM_Chat_ID AND c.UUID = ?)", null)
				.setParameters(request.getChatUuid());
		int count = query.count();
		List<MChatEntry> chatEntryList = query
				.setLimit(limit, offset)
				.<MChatEntry>list();
		//	
		ListChatEntriesResponse.Builder builder = ListChatEntriesResponse.newBuilder();
		//	Convert Record Log
		for(MChatEntry chatEntry : chatEntryList) {
			ChatEntry.Builder valueObject = convertChatEntry(chatEntry);
			builder.addChatEntries(valueObject.build());
		}
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	Return
		return builder;
	}
	
	/**
	 * Convert PO class from Chat Entry process to builder
	 * @param chatEntry
	 * @return
	 */
	private ChatEntry.Builder convertChatEntry(MChatEntry chatEntry) {
		ChatEntry.Builder builder = ChatEntry.newBuilder();
		builder.setChatEntryUuid(ValueUtil.validateNull(chatEntry.getUUID()));
		builder.setChatUuid(ValueUtil.validateNull(chatEntry.getCM_Chat().getUUID()));
		builder.setSubject(ValueUtil.validateNull(chatEntry.getSubject()));
		builder.setCharacterData(ValueUtil.validateNull(chatEntry.getCharacterData()));
		if(chatEntry.getAD_User_ID() != 0) {
			MUser user = MUser.get(chatEntry.getCtx(), chatEntry.getAD_User_ID());
			builder.setUserUuid(ValueUtil.validateNull(user.getUUID()));
			builder.setUserName(ValueUtil.validateNull(user.getName()));
		}
		builder.setLogDate(chatEntry.getCreated().getTime());
		//	Confidential Type
		if(!Util.isEmpty(chatEntry.getConfidentialType())) {
			if(chatEntry.getConfidentialType().equals(MChatEntry.CONFIDENTIALTYPE_PublicInformation)) {
				builder.setConfidentialType(org.spin.grpc.util.ChatEntry.ConfidentialType.PUBLIC);
			} else if(chatEntry.getConfidentialType().equals(MChatEntry.CONFIDENTIALTYPE_PartnerConfidential)) {
				builder.setConfidentialType(org.spin.grpc.util.ChatEntry.ConfidentialType.PARTER);
			} else if(chatEntry.getConfidentialType().equals(MChatEntry.CONFIDENTIALTYPE_Internal)) {
				builder.setConfidentialType(org.spin.grpc.util.ChatEntry.ConfidentialType.INTERNAL);
			}
		}
		//	Moderator Status
		if(!Util.isEmpty(chatEntry.getModeratorStatus())) {
			if(chatEntry.getModeratorStatus().equals(MChatEntry.MODERATORSTATUS_NotDisplayed)) {
				builder.setModeratorStatus(ModeratorStatus.NOT_DISPLAYED);
			} else if(chatEntry.getModeratorStatus().equals(MChatEntry.MODERATORSTATUS_Published)) {
				builder.setModeratorStatus(ModeratorStatus.PUBLISHED);
			} else if(chatEntry.getModeratorStatus().equals(MChatEntry.MODERATORSTATUS_Suspicious)) {
				builder.setModeratorStatus(ModeratorStatus.SUSPICIUS);
			} else if(chatEntry.getModeratorStatus().equals(MChatEntry.MODERATORSTATUS_ToBeReviewed)) {
				builder.setModeratorStatus(ModeratorStatus.TO_BE_REVIEWED);
			}
		}
		//	Chat entry type
		if(!Util.isEmpty(chatEntry.getChatEntryType())) {
			if(chatEntry.getChatEntryType().equals(MChatEntry.CHATENTRYTYPE_NoteFlat)) {
				builder.setChatEntryType(org.spin.grpc.util.ChatEntry.ChatEntryType.NOTE_FLAT);
			} else if(chatEntry.getChatEntryType().equals(MChatEntry.CHATENTRYTYPE_ForumThreaded)) {
				builder.setChatEntryType(org.spin.grpc.util.ChatEntry.ChatEntryType.NOTE_FLAT);
			} else if(chatEntry.getChatEntryType().equals(MChatEntry.CHATENTRYTYPE_Wiki)) {
				builder.setChatEntryType(org.spin.grpc.util.ChatEntry.ChatEntryType.NOTE_FLAT);
			}
		}
  		return builder;
	}
	
	/**
	 * Convert request for record chats to builder
	 * @param context
	 * @param request
	 * @return
	 */
	private ListRecordChatsResponse.Builder convertRecordChats(Properties context, ListRecordChatsRequest request) {
		StringBuffer whereClause = new StringBuffer();
		List<Object> parameters = new ArrayList<>();
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		//	
		MTable table = MTable.get(context, request.getTableName());
		if(table == null
				|| table.getAD_Table_ID() == 0) {
			throw new AdempiereException("@AD_Table_ID@ @Invalid@");
		}
		whereClause
			.append(I_CM_Chat.COLUMNNAME_AD_Table_ID).append(" = ?")
			.append(" AND ")
			.append(I_CM_Chat.COLUMNNAME_Record_ID).append(" = ?");
		//	Set parameters
		parameters.add(table.getAD_Table_ID());
		parameters.add(request.getRecordId());
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		Query query = new Query(context, I_CM_Chat.Table_Name, whereClause.toString(), null)
				.setParameters(parameters);
		int count = query.count();
		List<MChat> chatList = query
				.setLimit(limit, offset)
				.<MChat>list();
		//	
		ListRecordChatsResponse.Builder builder = ListRecordChatsResponse.newBuilder();
		//	Convert Record Log
		for(MChat chat : chatList) {
			RecordChat.Builder valueObject = convertRecordChat(chat);
			builder.addRecordChats(valueObject.build());
		}
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	Return
		return builder;
	}
	
	/**
	 * Convert PO class from Record Chat process to builder
	 * @param recordChat
	 * @return
	 */
	private RecordChat.Builder convertRecordChat(MChat recordChat) {
		MTable table = MTable.get(recordChat.getCtx(), recordChat.getAD_Table_ID());
		RecordChat.Builder builder = RecordChat.newBuilder();
		builder.setChatUuid(ValueUtil.validateNull(recordChat.getUUID()));
		builder.setTableName(ValueUtil.validateNull(table.getTableName()));
		if(recordChat.getCM_ChatType_ID() != 0) {
			MChatType chatType = MChatType.get(recordChat.getCtx(), recordChat.getCM_Chat_ID());
			builder.setChatTypeUuid(ValueUtil.validateNull(chatType.getUUID()));
		}
		builder.setRecordId(recordChat.getRecord_ID());
		builder.setDescription(ValueUtil.validateNull(recordChat.getDescription()));
		builder.setLogDate(recordChat.getCreated().getTime());
		//	Confidential Type
		if(!Util.isEmpty(recordChat.getConfidentialType())) {
			if(recordChat.getConfidentialType().equals(MChat.CONFIDENTIALTYPE_PublicInformation)) {
				builder.setConfidentialType(ConfidentialType.PUBLIC);
			} else if(recordChat.getConfidentialType().equals(MChat.CONFIDENTIALTYPE_PartnerConfidential)) {
				builder.setConfidentialType(ConfidentialType.PARTER);
			} else if(recordChat.getConfidentialType().equals(MChat.CONFIDENTIALTYPE_Internal)) {
				builder.setConfidentialType(ConfidentialType.INTERNAL);
			}
		}
		//	Moderation Type
		if(!Util.isEmpty(recordChat.getModerationType())) {
			if(recordChat.getModerationType().equals(MChat.MODERATIONTYPE_NotModerated)) {
				builder.setModerationType(ModerationType.NOT_MODERATED);
			} else if(recordChat.getModerationType().equals(MChat.MODERATIONTYPE_BeforePublishing)) {
				builder.setModerationType(ModerationType.BEFORE_PUBLISHING);
			} else if(recordChat.getModerationType().equals(MChat.MODERATIONTYPE_AfterPublishing)) {
				builder.setModerationType(ModerationType.AFTER_PUBLISHING);
			}
		}
  		return builder;
	}
}

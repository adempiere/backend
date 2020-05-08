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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_WF_NextCondition;
import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.I_AD_WF_NodeNext;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.I_C_Order;
import org.compiere.model.MColumn;
import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFNextCondition;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFNodeNext;
import org.compiere.wf.MWFResponsible;
import org.compiere.wf.MWorkflow;
import org.spin.grpc.util.WorkflowCondition.ConditionType;
import org.spin.grpc.util.WorkflowCondition.Operation;
import org.spin.grpc.util.WorkflowDefinition.DurationUnit;
import org.spin.grpc.util.WorkflowDefinition.PublishStatus;
import org.spin.grpc.util.WorkflowGrpc.WorkflowImplBase;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * https://itnext.io/customizing-grpc-generated-code-5909a2551ca1
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Business data service
 */
public class WorkflowServiceImplementation extends WorkflowImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(WorkflowServiceImplementation.class);
	
	@Override
	public void listWorkflows(ListWorkflowsRequest request, StreamObserver<ListWorkflowsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Workflow Logs Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListWorkflowsResponse.Builder entityValueList = convertWorkflows(context, request);
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
	public void listDocumentActions(ListDocumentActionsRequest request,
			StreamObserver<ListDocumentActionsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Document Actions is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListDocumentActionsResponse.Builder entityValueList = convertDocumentActions(context, request);
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
	public void listDocumentStatuses(ListDocumentStatusesRequest request,
			StreamObserver<ListDocumentStatusesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Document Statuses is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListDocumentStatusesResponse.Builder entityValueList = convertDocumentStatuses(context, request);
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
	 * Convert document statuses
	 * @param context
	 * @param request
	 * @return
	 */
	private ListDocumentStatusesResponse.Builder convertDocumentStatuses(Properties context, ListDocumentStatusesRequest request) {
		/** Drafted = DR */
		List<String> statusesList = new ArrayList<>();
		String documentStatus = null;
		String documentAction = null;
		String orderType = "--";
		String isSOTrx = "Y";
		Object isProcessing = "N";
		//	New
		int documentTypeId = 0;
		statusesList.add(DocumentEngine.STATUS_Drafted);
		//	Get Table from Name
		MTable table = MTable.get(context, request.getTableName());
		int recordId = 0;
		//	Get entity
		PO entity = RecordUtil.getEntity(context, request.getTableName(), request.getRecordUuid(), request.getRecordId());
		if(entity != null) {
			//	
			documentStatus = entity.get_ValueAsString(I_C_Order.COLUMNNAME_DocStatus);
			documentAction = entity.get_ValueAsString(I_C_Order.COLUMNNAME_DocAction);
			//
			isProcessing = entity.get_ValueAsBoolean(I_C_Order.COLUMNNAME_Processing)? "Y": "N";
			documentTypeId = entity.get_ValueAsInt(I_C_Order.COLUMNNAME_C_DocTypeTarget_ID);
			if(documentTypeId == 0) {
				documentTypeId = entity.get_ValueAsInt(I_C_Order.COLUMNNAME_C_DocType_ID);
			}
			if(documentTypeId != 0) {
				MDocType documentType = MDocType.get(context, documentTypeId);
				if(documentType != null
						&& !Util.isEmpty(documentType.getDocBaseType())) {
					orderType = documentType.getDocBaseType();
				}
			}
			isSOTrx = entity.get_ValueAsBoolean(I_C_Order.COLUMNNAME_IsSOTrx)? "Y": "N";
			recordId = entity.get_ID();
		}
		//	
		if (documentStatus == null) {
			documentStatus = DocumentEngine.STATUS_Drafted;
		}
		if (documentAction == null) {
			documentAction = DocumentEngine.ACTION_Prepare;
		}
		//	Standard
		if(documentStatus.equals(DocumentEngine.STATUS_Completed)
				|| documentStatus.equals(DocumentEngine.STATUS_Voided)
				|| documentStatus.equals(DocumentEngine.STATUS_Reversed)
				|| documentStatus.equals(DocumentEngine.STATUS_Unknown)
				|| documentStatus.equals(DocumentEngine.STATUS_Closed)) {
			/** In Progress = IP */
			statusesList.add(DocumentEngine.STATUS_InProgress);
			/** Approved = AP */
			statusesList.add(DocumentEngine.STATUS_Approved);
			//	For Prepaid Order
			if(orderType.equals(MOrder.DocSubTypeSO_Prepay)) {
				/** Waiting Payment = WP */
				statusesList.add(DocumentEngine.STATUS_WaitingPayment);
				/** Waiting Confirmation = WC */
				statusesList.add(DocumentEngine.STATUS_WaitingConfirmation);
			}
		}
		//	Add status
		statusesList.add(documentStatus);
		//	Get All document Actions
		ArrayList<String> valueList = new ArrayList<String>();
		ArrayList<String> nameList = new ArrayList<String>();
		ArrayList<String> descriptionList = new ArrayList<String>();
		//	Load all reference
		readDocumentStatusList(valueList, nameList, descriptionList);
		//	
		ListDocumentStatusesResponse.Builder builder = ListDocumentStatusesResponse.newBuilder();
		statusesList.stream().filter(status -> status != null).forEach(status -> {
			for (int i = 0; i < valueList.size(); i++) {
				if (status.equals(valueList.get(i))) {
					DocumentStatus.Builder documentStatusBuilder = DocumentStatus.newBuilder();
					documentStatusBuilder.setValue(ValueUtil.validateNull(valueList.get(i)));
					documentStatusBuilder.setName(ValueUtil.validateNull(nameList.get(i)));
					documentStatusBuilder.setDescription(ValueUtil.validateNull(descriptionList.get(i)));
					builder.addDocumentStatuses(documentStatusBuilder);
				}
			}
		});
		//	Get Actions
		
		
		log.fine("DocStatus=" + documentStatus 
			+ ", DocAction=" + documentAction + ", OrderType=" + orderType 
			+ ", IsSOTrx=" + isSOTrx + ", Processing=" + isProcessing 
			+ ", AD_Table_ID=" + table.getAD_Table_ID() + ", Record_ID=" + recordId);
		//
		String[] options = new String[valueList.size()];
		int index = 0;
		
		/*******************
		 *  General Actions
		 */
		String[] docActionHolder = new String[] {documentAction};
		index = DocumentEngine.getValidActions(documentStatus, isProcessing, orderType, isSOTrx, table.getAD_Table_ID(), docActionHolder, options);

		if (entity != null
				&& entity instanceof DocOptions) {
			index = ((DocOptions) entity).customizeValidActions(documentStatus, isProcessing, orderType, isSOTrx, entity.get_Table_ID(), docActionHolder, options, index);
		}

		log.fine("get doctype: " + documentTypeId);
		if (documentTypeId != 0) {
			index = DocumentEngine.checkActionAccess(Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Role_ID(Env.getCtx()), documentTypeId, options, index);
		}
		//	
		documentAction = docActionHolder[0];
		//	
		//	Get
		Arrays.asList(options).stream().filter(option -> option != null).forEach(option -> {
			for (int i = 0; i < valueList.size(); i++) {
				if (option.equals(valueList.get(i))) {
					DocumentStatus.Builder documentActionBuilder = DocumentStatus.newBuilder();
					documentActionBuilder.setValue(ValueUtil.validateNull(valueList.get(i)));
					documentActionBuilder.setName(ValueUtil.validateNull(nameList.get(i)));
					documentActionBuilder.setDescription(ValueUtil.validateNull(descriptionList.get(i)));
					builder.addDocumentStatuses(documentActionBuilder);
				}
			}
		});
		//	Add record count
		builder.setRecordCount(builder.getDocumentStatusesCount());
		//	Return
		return builder;
	}
	
	/**
	 * Fill Vector with DocAction Ref_List(135) values
	 * @param v_value
	 * @param v_name
	 * @param v_description
	 */
	private void readDocumentStatusList(ArrayList<String> v_value, ArrayList<String> v_name, ArrayList<String> v_description) {
		if (v_value == null) 
			throw new IllegalArgumentException("v_value parameter is null");
		if (v_name == null)
			throw new IllegalArgumentException("v_name parameter is null");
		if (v_description == null)
			throw new IllegalArgumentException("v_description parameter is null");
		
		String sql;
		if (Env.isBaseLanguage(Env.getCtx(), "AD_Ref_List"))
			sql = "SELECT Value, Name, Description FROM AD_Ref_List "
				+ "WHERE AD_Reference_ID=? ORDER BY Name";
		else
			sql = "SELECT l.Value, t.Name, t.Description "
				+ "FROM AD_Ref_List l, AD_Ref_List_Trl t "
				+ "WHERE l.AD_Ref_List_ID=t.AD_Ref_List_ID"
				+ " AND t.AD_Language='" + Env.getAD_Language(Env.getCtx()) + "'"
				+ " AND l.AD_Reference_ID=? ORDER BY t.Name";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try
		{
			preparedStatement = DB.prepareStatement(sql, null);
			preparedStatement.setInt(1, DocAction.DOCSTATUS_AD_REFERENCE_ID);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next())
			{
				String value = resultSet.getString(1);
				String name = resultSet.getString(2);
				String description = resultSet.getString(3);
				if (description == null)
					description = "";
				//
				v_value.add(value);
				v_name.add(name);
				v_description.add(description);
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally {
			DB.close(resultSet, preparedStatement);
			resultSet = null;
			preparedStatement = null;
		}

	}
	
	/**
	 * Convert request for workflow to builder
	 * @param context
	 * @param request
	 * @return
	 */
	private ListWorkflowsResponse.Builder convertWorkflows(Properties context, ListWorkflowsRequest request) {
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
			.append(I_AD_Workflow.COLUMNNAME_AD_Table_ID).append(" = ?");
		//	Set parameters
		parameters.add(table.getAD_Table_ID());
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		Query query = new Query(context, I_AD_Workflow.Table_Name, whereClause.toString(), null)
				.setParameters(parameters);
		int count = query.count();
		List<MWorkflow> workflowList = query
				.setLimit(limit, offset)
				.<MWorkflow>list();
		//	
		ListWorkflowsResponse.Builder builder = ListWorkflowsResponse.newBuilder();
		//	Convert Record Log
		for(MWorkflow workflowDefinition : workflowList) {
			WorkflowDefinition.Builder valueObject = convertWorkflowDefinition(workflowDefinition);
			builder.addWorkflows(valueObject.build());
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
	 * Convert PO class from Workflow to builder
	 * @param workflow
	 * @return
	 */
	private WorkflowDefinition.Builder convertWorkflowDefinition(MWorkflow workflow) {
		MTable table = MTable.get(workflow.getCtx(), workflow.getAD_Table_ID());
		WorkflowDefinition.Builder builder = WorkflowDefinition.newBuilder();
		builder.setWorkflowUuid(ValueUtil.validateNull(workflow.getUUID()));
		builder.setValue(ValueUtil.validateNull(workflow.getValue()));
		String name = workflow.getName();
		String description = workflow.getDescription();
		String help = workflow.getHelp();
		if(!Env.isBaseLanguage(workflow.getCtx(), "")) {
			String translation = workflow.get_Translation(MWorkflow.COLUMNNAME_Name);
			if(!Util.isEmpty(translation)) {
				name = translation;
			}
			translation = workflow.get_Translation(MWorkflow.COLUMNNAME_Description);
			if(!Util.isEmpty(translation)) {
				description = translation;
			}
			translation = workflow.get_Translation(MWorkflow.COLUMNNAME_Help);
			if(!Util.isEmpty(translation)) {
				help = translation;
			}
		}
		builder.setName(ValueUtil.validateNull(name));
		builder.setDescription(ValueUtil.validateNull(description));
		builder.setHelp(ValueUtil.validateNull(help));
		
		if(workflow.getAD_WF_Responsible_ID() != 0) {
			MWFResponsible responsible = MWFResponsible.get(workflow.getCtx(), workflow.getAD_WF_Responsible_ID());
			builder.setResponsibleUuid(ValueUtil.validateNull(responsible.getUUID()));
			builder.setResponsibleName(ValueUtil.validateNull(responsible.getName()));
		}
		builder.setPriority(workflow.getPriority());
		builder.setTableName(ValueUtil.validateNull(table.getTableName()));
		builder.setIsDefault(workflow.isDefault());
		builder.setIsValid(workflow.isValid());
		if(workflow.getValidFrom() != null) {
			builder.setValidFrom(workflow.getValidFrom().getTime());
		}
		//	Duration Unit
		if(!Util.isEmpty(workflow.getDurationUnit())) {
			if(workflow.getDurationUnit().equals(MWorkflow.DURATIONUNIT_Day)) {
				builder.setDurationUnitValue(DurationUnit.HOUR_VALUE);
			} else if(workflow.getDurationUnit().equals(MWorkflow.DURATIONUNIT_Minute)) {
				builder.setDurationUnitValue(DurationUnit.MINUTE_VALUE);
			} else if(workflow.getDurationUnit().equals(MWorkflow.DURATIONUNIT_Month)) {
				builder.setDurationUnitValue(DurationUnit.MONTH_VALUE);
			} else if(workflow.getDurationUnit().equals(MWorkflow.DURATIONUNIT_Second)) {
				builder.setDurationUnitValue(DurationUnit.SECOND_VALUE);
			} else if(workflow.getDurationUnit().equals(MWorkflow.DURATIONUNIT_Year)) {
				builder.setDurationUnitValue(DurationUnit.YEAR_VALUE);
			}
		}
		//	Publish Status
		if(!Util.isEmpty(workflow.getPublishStatus())) {
			if(workflow.getPublishStatus().equals(MWorkflow.PUBLISHSTATUS_Released)) {
				builder.setPublishStatusValue(PublishStatus.RELEASED_VALUE);
			} else if(workflow.getPublishStatus().equals(MWorkflow.PUBLISHSTATUS_Test)) {
				builder.setDurationUnitValue(PublishStatus.TEST_VALUE);
			} else if(workflow.getPublishStatus().equals(MWorkflow.PUBLISHSTATUS_UnderRevision)) {
				builder.setDurationUnitValue(PublishStatus.UNDER_REVISION_VALUE);
			} else if(workflow.getPublishStatus().equals(MWorkflow.PUBLISHSTATUS_Void)) {
				builder.setDurationUnitValue(PublishStatus.VOID_VALUE);
			}
		}
		//	Next node
		if(workflow.getAD_WF_Node_ID() != 0) {
			MWFNode startNode = MWFNode.get(workflow.getCtx(), workflow.getAD_WF_Node_ID());
			builder.setStartNode(convertWorkflowNode(startNode));
		}
		//	Get Events
		List<MWFNode> workflowNodeList = new Query(workflow.getCtx(), I_AD_WF_Node.Table_Name, I_AD_WF_Node.COLUMNNAME_AD_Workflow_ID + " = ?", null)
			.setParameters(workflow.getAD_Workflow_ID())
			.<MWFNode>list();
		//	populate
		for(MWFNode node : workflowNodeList) {
			WorkflowNode.Builder valueObject = convertWorkflowNode(node);
			builder.addWorkflowNodes(valueObject.build());
		}
  		return builder;
	}
	
	/**
	 * Convert PO class from Workflow node to builder
	 * @param node
	 * @return
	 */
	private WorkflowNode.Builder convertWorkflowNode(MWFNode node) {
		WorkflowNode.Builder builder = WorkflowNode.newBuilder();
		builder.setNodeUuid(ValueUtil.validateNull(node.getUUID()));
		builder.setValue(ValueUtil.validateNull(node.getValue()));
		String name = node.getName();
		String description = node.getDescription();
		String help = node.getHelp();
		if(!Env.isBaseLanguage(node.getCtx(), "")) {
			String translation = node.get_Translation(MWFNode.COLUMNNAME_Name);
			if(!Util.isEmpty(translation)) {
				name = translation;
			}
			translation = node.get_Translation(MWFNode.COLUMNNAME_Description);
			if(!Util.isEmpty(translation)) {
				description = translation;
			}
			translation = node.get_Translation(MWFNode.COLUMNNAME_Help);
			if(!Util.isEmpty(translation)) {
				help = translation;
			}
		}
		builder.setName(ValueUtil.validateNull(name));
		builder.setDescription(ValueUtil.validateNull(description));
		builder.setHelp(ValueUtil.validateNull(help));
		
		if(node.getAD_WF_Responsible_ID() != 0) {
			MWFResponsible responsible = MWFResponsible.get(node.getCtx(), node.getAD_WF_Responsible_ID());
			builder.setResponsibleUuid(ValueUtil.validateNull(responsible.getUUID()));
			builder.setResponsibleName(ValueUtil.validateNull(responsible.getName()));
		}
		builder.setPriority(node.getPriority());
		//	Get Events
		List<MWFNodeNext> workflowNodeTransitionList = new Query(node.getCtx(), I_AD_WF_NodeNext.Table_Name, I_AD_WF_NodeNext.COLUMNNAME_AD_WF_Node_ID + " = ?", null)
			.setParameters(node.getAD_WF_Node_ID())
			.<MWFNodeNext>list();
		//	populate
		for(MWFNodeNext nodeNext : workflowNodeTransitionList) {
			WorkflowTransition.Builder valueObject = convertTransition(nodeNext);
			builder.addTransitions(valueObject.build());
		}
  		return builder;
	}
	
	/**
	 * Convert PO class from Transition to builder
	 * @param transition
	 * @return
	 */
	private WorkflowTransition.Builder convertTransition(MWFNodeNext transition) {
		WorkflowTransition.Builder builder = WorkflowTransition.newBuilder();
		MWFNode nodeNext = MWFNode.get(transition.getCtx(), transition.getAD_WF_NodeNext_ID());
		builder.setNodeNextUuid(ValueUtil.validateNull(nodeNext.getUUID()));
		builder.setDescription(ValueUtil.validateNull(transition.getDescription()));
		builder.setSequence(transition.getSeqNo());
		builder.setIsStdUserWorkflow(transition.isStdUserWorkflow());
		//	Get Events
		List<MWFNextCondition> workflowNodeTransitionList = new Query(transition.getCtx(), I_AD_WF_NextCondition.Table_Name, I_AD_WF_NextCondition.COLUMNNAME_AD_WF_NodeNext_ID + " = ?", null)
			.setParameters(transition.getAD_WF_Node_ID())
			.<MWFNextCondition>list();
		//	populate
		for(MWFNextCondition nextCondition : workflowNodeTransitionList) {
			WorkflowCondition.Builder valueObject = convertWorkflowCondition(nextCondition);
			builder.addWorkflowConditions(valueObject.build());
		}
  		return builder;
	}
	
	/**
	 * Convert PO class from Workflow condition to builder
	 * @param condition
	 * @return
	 */
	private WorkflowCondition.Builder convertWorkflowCondition(MWFNextCondition condition) {
		WorkflowCondition.Builder builder = WorkflowCondition.newBuilder();
		builder.setSequence(condition.getSeqNo());
		MColumn column = MColumn.get(condition.getCtx(), condition.getAD_Column_ID());
		builder.setColumnName(ValueUtil.validateNull(column.getColumnName()));
		builder.setValue(ValueUtil.validateNull(condition.getValue()));
		//	Condition Type
		if(!Util.isEmpty(condition.getAndOr())) {
			if(condition.getAndOr().equals(MWFNextCondition.ANDOR_And)) {
				builder.setConditionTypeValue(ConditionType.AND_VALUE);
			} else if(condition.getAndOr().equals(MWFNextCondition.ANDOR_Or)) {
				builder.setConditionTypeValue(ConditionType.OR_VALUE);
			}
		}
		//	Operation
		if(!Util.isEmpty(condition.getOperation())) {
			if(condition.getOperation().equals(MWFNextCondition.OPERATION_Eq)) {
				builder.setOperation(Operation.EQUAL);
			} else if(condition.getOperation().equals(MWFNextCondition.OPERATION_NotEq)) {
				builder.setOperation(Operation.NOT_EQUAL);
			} else if(condition.getOperation().equals(MWFNextCondition.OPERATION_Like)) {
				builder.setOperation(Operation.LIKE);
			} else if(condition.getOperation().equals(MWFNextCondition.OPERATION_Gt)) {
				builder.setOperation(Operation.GREATER);
			} else if(condition.getOperation().equals(MWFNextCondition.OPERATION_GtEq)) {
				builder.setOperation(Operation.GREATER_EQUAL);
			} else if(condition.getOperation().equals(MWFNextCondition.OPERATION_Le)) {
				builder.setOperation(Operation.LESS);
			} else if(condition.getOperation().equals(MWFNextCondition.OPERATION_LeEq)) {
				builder.setOperation(Operation.LESS_EQUAL);
			} else if(condition.getOperation().equals(MWFNextCondition.OPERATION_X)) {
				builder.setOperation(Operation.BETWEEN);
			} else if(condition.getOperation().equals(MWFNextCondition.OPERATION_Sql)) {
				builder.setOperation(Operation.SQL);
			}
		}
  		return builder;
	}
	
	/**
	 * Convert document actions
	 * @param context
	 * @param request
	 * @return
	 */
	private ListDocumentActionsResponse.Builder convertDocumentActions(Properties context, ListDocumentActionsRequest request) {
		PO entity = RecordUtil.getEntity(context, request.getTableName(), request.getRecordUuid(), request.getRecordId());
		//	
		String documentStatus = entity.get_ValueAsString(I_C_Order.COLUMNNAME_DocStatus);
		String documentAction = entity.get_ValueAsString(I_C_Order.COLUMNNAME_DocAction);
		//
		Object isProcessing = entity.get_ValueAsBoolean(I_C_Order.COLUMNNAME_Processing)? "Y": "N";
		String orderType = "--";
		int documentTypeId = entity.get_ValueAsInt(I_C_Order.COLUMNNAME_C_DocTypeTarget_ID);
		if(documentTypeId == 0) {
			documentTypeId = entity.get_ValueAsInt(I_C_Order.COLUMNNAME_C_DocType_ID);
		}
		if(documentTypeId != 0) {
			MDocType documentType = MDocType.get(context, documentTypeId);
			if(documentType != null
					&& !Util.isEmpty(documentType.getDocBaseType())) {
				orderType = documentType.getDocBaseType();
			}
		}
		String isSOTrx = entity.get_ValueAsBoolean(I_C_Order.COLUMNNAME_IsSOTrx)? "Y": "N";
		//	
		if (documentStatus == null) {
			throw new AdempiereException("@DocStatus@ @NotFound@");
		}
		
		//	Get All document Actions
		ArrayList<String> valueList = new ArrayList<String>();
		ArrayList<String> nameList = new ArrayList<String>();
		ArrayList<String> descriptionList = new ArrayList<String>();
		//	Load all reference
		DocumentEngine.readReferenceList(valueList, nameList, descriptionList);

		log.fine("DocStatus=" + documentStatus 
			+ ", DocAction=" + documentAction + ", OrderType=" + orderType 
			+ ", IsSOTrx=" + isSOTrx + ", Processing=" + isProcessing 
			+ ", AD_Table_ID=" + entity.get_Table_ID() + ", Record_ID=" + entity.get_ID());
		//
		String[] options = new String[valueList.size()];
		int index = 0;

		/**
		 * 	Check Existence of Workflow Activities
		 */
		String workflowStatus = MWFActivity.getActiveInfo(Env.getCtx(), entity.get_Table_ID(), entity.get_ID()); 
		if (workflowStatus != null) {
			throw new AdempiereException("@WFActiveForRecord@");
		}
		
		/*******************
		 *  General Actions
		 */
		String[] docActionHolder = new String[] {documentAction};
		index = DocumentEngine.getValidActions(documentStatus, isProcessing, orderType, isSOTrx, entity.get_Table_ID(), docActionHolder, options);

		if (entity instanceof DocOptions) {
			index = ((DocOptions) entity).customizeValidActions(documentStatus, isProcessing, orderType, isSOTrx, entity.get_Table_ID(), docActionHolder, options, index);
		}

		log.fine("get doctype: " + documentTypeId);
		if (documentTypeId != 0) {
			index = DocumentEngine.checkActionAccess(Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Role_ID(Env.getCtx()), documentTypeId, options, index);
		}
		//	
		documentAction = docActionHolder[0];
		//	
		ListDocumentActionsResponse.Builder builder = ListDocumentActionsResponse.newBuilder();
		//	Get
		Arrays.asList(options).stream().filter(option -> option != null).forEach(option -> {
			for (int i = 0; i < valueList.size(); i++) {
				if (option.equals(valueList.get(i))) {
					builder.addDocumentActions(ConvertUtil.convertDocumentAction(
							valueList.get(i), 
							nameList.get(i), 
							descriptionList.get(i)));
				}
			}
		});
		//	setDefault
		if (documentAction.equals("--"))		//	If None, suggest closing
			documentAction = DocumentEngine.ACTION_Close;
		String defaultValue = "";
		String defaultName = "";
		String defaultDescription = "";
		for (int i = 0; i < valueList.size() && defaultName.equals(""); i++) {
			if (documentAction.equals(valueList.get(i))) {
				defaultValue = valueList.get(i);
				defaultName = nameList.get(i);
				defaultDescription = descriptionList.get(i);
			}
		}
		//	Set default value
		if (!defaultName.equals("")) {
			builder.setDefaultDocumentAction(ConvertUtil.convertDocumentAction(
					defaultValue, 
					defaultName, 
					defaultDescription));
		}
		//	Add record count
		builder.setRecordCount(builder.getDocumentActionsCount());
		//	Return
		return builder;
	}
}

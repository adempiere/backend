/*************************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                              *
 * This program is free software; you can redistribute it and/or modify it    		 *
 * under the terms version 2 or later of the GNU General Public License as published *
 * by the Free Software Foundation. This program is distributed in the hope   		 *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 		 *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           		 *
 * See the GNU General Public License for more details.                       		 *
 * You should have received a copy of the GNU General Public License along    		 *
 * with this program; if not, write to the Free Software Foundation, Inc.,    		 *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     		 *
 * For the text or an alternative of this public license, you may reach us    		 *
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, S.A. All Rights Reserved. *
 * Contributor(s): Yamel Senih www.erpya.com				  		                 *
 *************************************************************************************/
package org.spin.base.util;

import java.util.List;

import org.compiere.model.I_AD_WF_EventAudit;
import org.compiere.model.I_AD_WF_NextCondition;
import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.I_AD_WF_NodeNext;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFEventAudit;
import org.compiere.wf.MWFNextCondition;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFNodeNext;
import org.compiere.wf.MWFProcess;
import org.compiere.wf.MWFResponsible;
import org.compiere.wf.MWorkflow;
import org.spin.grpc.util.WorkflowActivity;
import org.spin.grpc.util.WorkflowCondition;
import org.spin.grpc.util.WorkflowDefinition;
import org.spin.grpc.util.WorkflowEvent;
import org.spin.grpc.util.WorkflowNode;
import org.spin.grpc.util.WorkflowProcess;
import org.spin.grpc.util.WorkflowTransition;
import org.spin.grpc.util.WorkflowCondition.ConditionType;
import org.spin.grpc.util.WorkflowCondition.Operation;
import org.spin.grpc.util.WorkflowDefinition.DurationUnit;
import org.spin.grpc.util.WorkflowDefinition.PublishStatus;
import org.spin.grpc.util.WorkflowProcess.WorkflowState;

/**
 * Class for handle workflow conversion values
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class WorkflowUtil {
	
	/**
	 * Convert PO class from Workflow process to builder
	 * @param workflowProcess
	 * @return
	 */
	public static WorkflowProcess.Builder convertWorkflowProcess(MWFProcess workflowProcess) {
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
		builder.setId(workflowProcess.getRecord_ID());
		builder.setUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(table.getTableName(), workflowProcess.getRecord_ID())));
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
	 * Convert PO class from Workflow to builder
	 * @param workflow
	 * @return
	 */
	public static WorkflowDefinition.Builder convertWorkflowDefinition(MWorkflow workflow) {
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
	public static WorkflowNode.Builder convertWorkflowNode(MWFNode node) {
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
	public static WorkflowTransition.Builder convertTransition(MWFNodeNext transition) {
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
	public static WorkflowCondition.Builder convertWorkflowCondition(MWFNextCondition condition) {
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
	 * Convert PO class from Workflow event audit to builder
	 * @param workflowEventAudit
	 * @return
	 */
	public static WorkflowEvent.Builder convertWorkflowEventAudit(MWFEventAudit workflowEventAudit) {
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
		builder.setId(workflowEventAudit.getRecord_ID());
		builder.setUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(table.getTableName(), workflowEventAudit.getRecord_ID())));
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
	 * Convert Activity for gRPC
	 * @param workflowActivity
	 * @return
	 */
	public static WorkflowActivity.Builder convertWorkflowActivity(MWFActivity workflowActivity) {
		MTable table = MTable.get(workflowActivity.getCtx(), workflowActivity.getAD_Table_ID());
		WorkflowActivity.Builder builder = WorkflowActivity.newBuilder();
		MWorkflow workflow = MWorkflow.get(workflowActivity.getCtx(), workflowActivity.getAD_Workflow_ID());
		MWFProcess workflowProcess = (MWFProcess) workflowActivity.getAD_WF_Process();
		MWFNode workflowNode = MWFNode.get(Env.getCtx(), workflowActivity.getAD_WF_Node_ID());
		builder.setWorkflowProcess(WorkflowUtil.convertWorkflowProcess(workflowProcess));
		builder.setWorkflow(WorkflowUtil.convertWorkflowDefinition(workflow));
		builder.setNode(WorkflowUtil.convertWorkflowNode(workflowNode));
		if(workflowActivity.getAD_WF_Responsible_ID() != 0) {
			MWFResponsible responsible = MWFResponsible.get(workflowActivity.getCtx(), workflowActivity.getAD_WF_Responsible_ID());
			builder.setResponsibleUuid(ValueUtil.validateNull(responsible.getUUID()));
			builder.setResponsibleName(ValueUtil.validateNull(responsible.getName()));
		}
		if(workflowActivity.getAD_User_ID() != 0) {
			MUser user = MUser.get(workflowActivity.getCtx(), workflowActivity.getAD_User_ID());
			builder.setUserUuid(ValueUtil.validateNull(user.getUUID()));
			builder.setUserName(ValueUtil.validateNull(user.getName()));
		}
		builder.setId(workflowActivity.getAD_WF_Activity_ID());
		builder.setUuid(ValueUtil.validateNull(workflowActivity.getUUID()));
		builder.setRecordUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(table.getTableName(), workflowActivity.getRecord_ID())));
		builder.setTableName(ValueUtil.validateNull(table.getTableName()));
		builder.setTextMessage(ValueUtil.validateNull(Msg.parseTranslation(workflowActivity.getCtx(), workflowActivity.getTextMsg())));
		builder.setProcessed(workflowActivity.isProcessed());
		builder.setCreated(workflowActivity.getCreated().getTime());
		if(workflowActivity.getDateLastAlert() != null) {
			builder.setLastAlert(workflowActivity.getDateLastAlert().getTime());
		}
		//	
  		return builder;
	}
}

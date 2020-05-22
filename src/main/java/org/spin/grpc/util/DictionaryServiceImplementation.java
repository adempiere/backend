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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.I_AD_Browse;
import org.adempiere.model.MBrowse;
import org.adempiere.model.MBrowseField;
import org.adempiere.model.MView;
import org.compiere.model.I_AD_Column;
import org.compiere.model.I_AD_Element;
import org.compiere.model.I_AD_Field;
import org.compiere.model.I_AD_FieldGroup;
import org.compiere.model.I_AD_Form;
import org.compiere.model.I_AD_Menu;
import org.compiere.model.I_AD_Message;
import org.compiere.model.I_AD_Process;
import org.compiere.model.I_AD_Reference;
import org.compiere.model.I_AD_Tab;
import org.compiere.model.I_AD_Val_Rule;
import org.compiere.model.I_AD_Window;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MForm;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MMenu;
import org.compiere.model.MMessage;
import org.compiere.model.MProcess;
import org.compiere.model.MProcessPara;
import org.compiere.model.MRecentItem;
import org.compiere.model.MReportView;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MValRule;
import org.compiere.model.MWindow;
import org.compiere.model.M_Element;
import org.compiere.model.Query;
import org.compiere.model.X_AD_FieldGroup;
import org.compiere.model.X_AD_Reference;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Util;
import org.spin.grpc.util.DictionaryGrpc.DictionaryImplBase;
import org.spin.model.MADContextInfo;
import org.spin.model.MADFieldCondition;
import org.spin.model.MADFieldDefinition;
import org.spin.util.ASPUtil;
import org.spin.util.AbstractExportFormat;
import org.spin.util.ReportExportHandler;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Dictionary service
 * Get all dictionary meta-data
 */
public class DictionaryServiceImplementation extends DictionaryImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(DictionaryServiceImplementation.class);
	
	@Override
	public void getWindow(EntityRequest request, StreamObserver<Window> responseObserver) {
		requestWindow(request, responseObserver, false);
	}
	
	@Override
	public void getWindowAndTabs(EntityRequest request, StreamObserver<Window> responseObserver) {
		requestWindow(request, responseObserver, true);
	}
	
	@Override
	public void getTab(EntityRequest request, StreamObserver<Tab> responseObserver) {
		requestTab(request, responseObserver, false);
	}
	
	@Override
	public void getTabAndFields(EntityRequest request, StreamObserver<Tab> responseObserver) {
		requestTab(request, responseObserver, true);
	}
	
	@Override
	public void getField(FieldRequest request, StreamObserver<Field> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Menu Requested = " + request.getFieldUuid());
			ApplicationRequest applicationInfo = request.getApplicationRequest();
			if(applicationInfo == null
					|| Util.isEmpty(applicationInfo.getSessionUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getApplicationRequest().getSessionUuid(), request.getApplicationRequest().getLanguage());
			Field.Builder fieldBuilder = convertField(context, request);
			responseObserver.onNext(fieldBuilder.build());
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
	public void getReference(ReferenceRequest request, StreamObserver<Reference> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Menu Requested = " + request.getReferenceUuid());
			ApplicationRequest applicationInfo = request.getApplicationRequest();
			if(applicationInfo == null
					|| Util.isEmpty(applicationInfo.getSessionUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getApplicationRequest().getSessionUuid(), request.getApplicationRequest().getLanguage());
			Reference.Builder fieldBuilder = convertReference(context, request);
			responseObserver.onNext(fieldBuilder.build());
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
	public void getValidationRule(ValidationRuleRequest request, StreamObserver<ValidationRule> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Menu Requested = " + request.getValidationRuleUuid());
			ApplicationRequest applicationInfo = request.getApplicationRequest();
			if(applicationInfo == null
					|| Util.isEmpty(applicationInfo.getSessionUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getApplicationRequest().getSessionUuid(), request.getApplicationRequest().getLanguage());
			ValidationRule.Builder fieldBuilder = convertValidationRule(context, request);
			responseObserver.onNext(fieldBuilder.build());
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
	public void getProcess(EntityRequest request, StreamObserver<Process> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Menu Requested = " + request.getUuid());
			ApplicationRequest applicationInfo = request.getApplicationRequest();
			if(applicationInfo == null
					|| Util.isEmpty(applicationInfo.getSessionUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getApplicationRequest().getSessionUuid(), request.getApplicationRequest().getLanguage());
			Process.Builder processBuilder = convertProcess(context, request.getUuid(), true);
			responseObserver.onNext(processBuilder.build());
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
	public void getBrowser(EntityRequest request, StreamObserver<Browser> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Menu Requested = " + request.getUuid());
			ApplicationRequest applicationInfo = request.getApplicationRequest();
			if(applicationInfo == null
					|| Util.isEmpty(applicationInfo.getSessionUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getApplicationRequest().getSessionUuid(), request.getApplicationRequest().getLanguage());
			Browser.Builder browserBuilder = convertBrowser(context, request.getUuid(), true);
			responseObserver.onNext(browserBuilder.build());
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
	public void getForm(EntityRequest request, StreamObserver<Form> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Menu Requested = " + request.getUuid());
			ApplicationRequest applicationInfo = request.getApplicationRequest();
			if(applicationInfo == null
					|| Util.isEmpty(applicationInfo.getSessionUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getApplicationRequest().getSessionUuid(), request.getApplicationRequest().getLanguage());
			Form.Builder formBuilder = convertForm(context, request.getUuid());
			responseObserver.onNext(formBuilder.build());
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
	 * Request with parameters
	 */
	public void requestWindow(EntityRequest request, StreamObserver<Window> responseObserver, boolean withTabs) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Menu Requested = " + request.getUuid());
			ApplicationRequest applicationInfo = request.getApplicationRequest();
			if(applicationInfo == null
					|| Util.isEmpty(applicationInfo.getSessionUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getApplicationRequest().getSessionUuid(), request.getApplicationRequest().getLanguage());
			Window.Builder windowBuilder = convertWindow(context, request.getUuid(), withTabs);
			responseObserver.onNext(windowBuilder.build());
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
	 * Request with parameter
	 * @param request
	 * @param responseObserver
	 * @param withFields
	 */
	public void requestTab(EntityRequest request, StreamObserver<Tab> responseObserver, boolean withFields) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Menu Requested = " + request.getUuid());
			ApplicationRequest applicationInfo = request.getApplicationRequest();
			if(applicationInfo == null
					|| Util.isEmpty(applicationInfo.getSessionUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getApplicationRequest().getSessionUuid(), request.getApplicationRequest().getLanguage());
			Tab.Builder tabBuilder = convertTab(context, request.getUuid(), withFields);
			responseObserver.onNext(tabBuilder.build());
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
	 * Request Window: can be only window or child
	 * @param request
	 * @param responseObserver
	 * @param withTabs
	 */
	private Window.Builder convertWindow(Properties context, String uuid, boolean withTabs) {
		MWindow window = new Query(context, I_AD_Window.Table_Name, I_AD_Window.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		return convertWindow(context, window, withTabs);
	}
	
	/**
	 * Request Form from uuid
	 * @param request
	 * @param responseObserver
	 */
	private Form.Builder convertForm(Properties context, String uuid) {
		MForm form = new Query(context, I_AD_Form.Table_Name, I_AD_Window.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		return convertForm(context, form);
	}
	
	/**
	 * Convert Window from Window Model
	 * @param form
	 * @return
	 */
	private Form.Builder convertForm(Properties context, MForm form) {
		Form.Builder builder = null;
		//	
		builder = Form.newBuilder()
				.setId(form.getAD_Form_ID())
				.setUuid(ValueUtil.validateNull(form.getUUID()))
				.setName(ValueUtil.validateNull(ValueUtil.getTranslation(form, MForm.COLUMNNAME_Name)))
				.setDescription(ValueUtil.validateNull(ValueUtil.getTranslation(form, MForm.COLUMNNAME_Description)))
				.setHelp(ValueUtil.validateNull(ValueUtil.getTranslation(form, MForm.COLUMNNAME_Help)))
				.setIsActive(form.isActive());
		//	File Name
		String fileName = form.getClassname();
		if(!Util.isEmpty(fileName)) {
			int endIndex = fileName.lastIndexOf(".");
			int beginIndex = fileName.lastIndexOf("/");
			if(beginIndex == -1) {
				beginIndex = fileName.lastIndexOf(".");
				endIndex = -1;
			}
			if(beginIndex == -1) {
				beginIndex = 0;
			} else {
				beginIndex++;
			}
			if(endIndex == -1) {
				endIndex = fileName.length();
			}
			//	Set
			builder.setFileName(ValueUtil.validateNull(fileName.substring(beginIndex, endIndex)));
		}
		//	Add to recent Item
		addToRecentItem(MMenu.ACTION_Form, form.getAD_Form_ID());
		//	return
		return builder;
	}
	
	/**
	 * Convert Window from Window Model
	 * @param window
	 * @param withTabs
	 * @return
	 */
	private Window.Builder convertWindow(Properties context, MWindow window, boolean withTabs) {
		window = ASPUtil.getInstance(context).getWindow(window.getAD_Window_ID());
		Window.Builder builder = null;
		ContextInfo.Builder contextInfoBuilder = convertContextInfo(context, window.getAD_ContextInfo_ID());
		//	
		builder = Window.newBuilder()
				.setId(window.getAD_Window_ID())
				.setUuid(ValueUtil.validateNull(window.getUUID()))
				.setName(window.getName())
				.setDescription(ValueUtil.validateNull(window.getDescription()))
				.setHelp(ValueUtil.validateNull(window.getHelp()))
				.setWindowType(ValueUtil.validateNull(window.getWindowType()))
				.setIsSOTrx(window.isSOTrx())
				.setIsActive(window.isActive());
		if(contextInfoBuilder != null) {
			builder.setContextInfo(contextInfoBuilder.build());
		}
		//	With Tabs
		if(withTabs) {
			List<Tab.Builder> tabListForGroup = new ArrayList<>();
			List<MTab> tabs = ASPUtil.getInstance(context).getWindowTabs(window.getAD_Window_ID());
			for(MTab tab : tabs) {
				if(!tab.isActive()) {
					continue;
				}
				Tab.Builder tabBuilder = convertTab(context, tab, tabs, false);
				builder.addTabs(tabBuilder.build());
				//	Get field group
				int [] fieldGroupIdArray = getFieldGroupIdsFromTab(tab.getAD_Tab_ID());
				if(fieldGroupIdArray != null) {
					for(int fieldGroupId : fieldGroupIdArray) {
						Tab.Builder tabFieldGroup = convertTab(context, tab, false);
						FieldGroup.Builder fieldGroup = convertFieldGroup(context, fieldGroupId);
						tabFieldGroup.setFieldGroup(fieldGroup);
						tabFieldGroup.setName(fieldGroup.getName());
						//	Add to list
						tabListForGroup.add(tabFieldGroup);
					}
				}
			}
			//	Add Field Group Tabs
			for(Tab.Builder tabFieldGroup : tabListForGroup) {
				builder.addTabs(tabFieldGroup.build());
			}
		}
		//	Add to recent Item
		addToRecentItem(MMenu.ACTION_Window, window.getAD_Window_ID());
		//	return
		return builder;
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
		MRecentItem.addMenuOption(Env.getCtx(), menuId, optionId);
	}
	
	/**
	 * Get Field group from Tab
	 * @param tabId
	 * @return
	 */
	private int[] getFieldGroupIdsFromTab(int tabId) {
		return DB.getIDsEx(null, "SELECT f.AD_FieldGroup_ID "
				+ "FROM AD_Field f "
				+ "INNER JOIN AD_FieldGroup fg ON(fg.AD_FieldGroup_ID = f.AD_FieldGroup_ID) "
				+ "WHERE f.AD_Tab_ID = ? "
				+ "AND fg.FieldGroupType = ? "
				+ "GROUP BY f.AD_FieldGroup_ID", tabId, X_AD_FieldGroup.FIELDGROUPTYPE_Tab);
	}
	
	/**
	 * Convert Tabs from UUID
	 * @param uuid
	 * @param withFields
	 * @return
	 */
	private Tab.Builder convertTab(Properties context, String uuid, boolean withFields) {
		MTab tab = new Query(context, I_AD_Tab.Table_Name, I_AD_Tab.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertTab(context, tab, withFields);
	}
	
	/**
	 * Convert Process from UUID
	 * @param uuid
	 * @param withParameters
	 * @return
	 */
	private Process.Builder convertProcess(Properties context, String uuid, boolean withParameters) {
		MProcess process = new Query(context, I_AD_Process.Table_Name, I_AD_Process.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertProcess(context, process, withParameters);
	}
	
	/**
	 * Convert Browser from UUID
	 * @param uuid
	 * @param withFields
	 * @return
	 */
	private Browser.Builder convertBrowser(Properties context, String uuid, boolean withFields) {
		MBrowse browser = new Query(context, I_AD_Browse.Table_Name, I_AD_Process.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertBrowser(context, browser, withFields);
	}
	
	/**
	 * Convert Model tab to builder tab
	 * @param tab
	 * @return
	 */
	private Tab.Builder convertTab(Properties context, MTab tab, boolean withFields) {
		return convertTab(context, tab, null, withFields);
	}
	
	/**
	 * Convert Model tab to builder tab
	 * @param tab
	 * @return
	 */
	private Tab.Builder convertTab(Properties context, MTab tab, List<MTab> tabs, boolean withFields) {
		String parentTabUuid = null;
		int tabId = tab.getAD_Tab_ID();
		int seqNo = tab.getSeqNo();
		int tabLevel = tab.getTabLevel();
		Optional<MTab> currentOptionalTab = ASPUtil.getInstance(context).getWindowTabs(tab.getAD_Window_ID()).stream().filter(filterTab -> filterTab.getAD_Tab_ID() == tabId).findFirst();
		tab = currentOptionalTab.get();
		//	Get table attributes
		MTable table = MTable.get(context, tab.getAD_Table_ID());
		boolean isReadOnly = tab.isReadOnly() || table.isView();
		int contextInfoId = tab.getAD_ContextInfo_ID();
		if(contextInfoId <= 0) {
			contextInfoId = table.getAD_ContextInfo_ID();
		}
		StringBuffer whereClause = new StringBuffer();
		//	Create where clause for children
		if(tab.getTabLevel() > 0
				&& tabs != null) {
			Optional<MTab> optionalTab = tabs.stream()
					.filter(parentTab -> parentTab.getAD_Tab_ID() != tabId)
					.filter(parentTab -> parentTab.getTabLevel() == 0)
					.findFirst();
			String mainColumnName = null;
			MTable mainTable = null;
			if(optionalTab.isPresent()) {
				mainTable = MTable.get(context, optionalTab.get().getAD_Table_ID());
				mainColumnName = mainTable.getKeyColumns()[0];
			}
			List<MTab> tabList = tabs.stream()
					.filter(parentTab -> parentTab.getAD_Tab_ID() != tabId)
					.filter(parentTab -> parentTab.getAD_Tab_ID() != optionalTab.get().getAD_Tab_ID())
					.filter(parentTab -> parentTab.getSeqNo() < seqNo)
					.filter(parentTab -> parentTab.getTabLevel() < tabLevel)
					.filter(parentTab -> !parentTab.isTranslationTab())
					.sorted(Comparator.comparing(MTab::getSeqNo).thenComparing(MTab::getTabLevel).reversed())
					.collect(Collectors.toList());
			//	Validate direct child
			if(tabList.size() == 0) {
				if(tab.getParent_Column_ID() != 0) {
					mainColumnName = MColumn.getColumnName(context, tab.getParent_Column_ID());
				}
				String childColumn = mainColumnName;
				if(tab.getAD_Column_ID() != 0) {
					childColumn = MColumn.getColumnName(context, tab.getAD_Column_ID());
				}
				//	
				whereClause.append(table.getTableName()).append(".").append(childColumn).append(" = ").append("@").append(mainColumnName).append("@");
				if(optionalTab.isPresent()) {
					parentTabUuid = optionalTab.get().getUUID();
				}
			} else {
				whereClause.append("EXISTS(SELECT 1 FROM");
				Map<Integer, MTab> tablesMap = new HashMap<>();
				int aliasIndex = 0;
				boolean firstResult = true;
				for(MTab currentTab : tabList) {
					tablesMap.put(aliasIndex, currentTab);
					MTable currentTable = MTable.get(context, currentTab.getAD_Table_ID());
					if(firstResult) {
						whereClause.append(" ").append(currentTable.getTableName()).append(" AS t").append(aliasIndex);
						firstResult = false;
					} else {
						MTab childTab = tablesMap.get(aliasIndex -1);
						String childColumnName = getParentColumnNameFromTab(childTab);
						String childLinkColumnName = getLinkColumnNameFromTab(childTab);
						//	Get from parent
						if(Util.isEmpty(childColumnName)) {
							MTable childTable = MTable.get(context, currentTab.getAD_Table_ID());
							childColumnName = childTable.getKeyColumns()[0];
						}
						if(Util.isEmpty(childLinkColumnName)) {
							childLinkColumnName = childColumnName;
						}
						whereClause.append(" INNER JOIN ").append(currentTable.getTableName()).append(" AS t").append(aliasIndex)
							.append(" ON(").append("t").append(aliasIndex).append(".").append(childLinkColumnName)
							.append("=").append("t").append(aliasIndex - 1).append(".").append(childColumnName).append(")");
					}
					aliasIndex++;
					if(Util.isEmpty(parentTabUuid)) {
						parentTabUuid = currentTab.getUUID();
					}
				}
				whereClause.append(" WHERE t").append(aliasIndex - 1).append(".").append(mainColumnName).append(" = ").append("@").append(mainColumnName).append("@");
				//	Add support to child
				MTab parentTab = tablesMap.get(aliasIndex -1);
				String parentColumnName = getParentColumnNameFromTab(tab);
				String linkColumnName = getLinkColumnNameFromTab(tab);
				if(Util.isEmpty(parentColumnName)) {
					MTable parentTable = MTable.get(context, parentTab.getAD_Table_ID());
					parentColumnName = parentTable.getKeyColumns()[0];
				}
				if(Util.isEmpty(linkColumnName)) {
					linkColumnName = parentColumnName;
				}
				whereClause.append(" AND t").append(0).append(".").append(parentColumnName).append(" = ").append(table.getTableName()).append(".").append(linkColumnName);
				whereClause.append(")");
			}
		}
		//	Set where clause for tab
		if(whereClause.length() > 0) {
			if(!Util.isEmpty(tab.getWhereClause())) {
				whereClause.append(" AND ").append("(").append(tab.getWhereClause()).append(")");
			}
		} else {
			whereClause.append(ValueUtil.validateNull(tab.getWhereClause()));
		}
		//	create build
		Tab.Builder builder = Tab.newBuilder()
				.setId(tab.getAD_Tab_ID())
				.setUuid(ValueUtil.validateNull(tab.getUUID()))
				.setName(ValueUtil.validateNull(tab.getName()))
				.setDescription(ValueUtil.validateNull(tab.getDescription()))
				.setHelp(ValueUtil.validateNull(tab.getHelp()))
				.setAccessLevel(Integer.parseInt(table.getAccessLevel()))
				.setCommitWarning(ValueUtil.validateNull(tab.getCommitWarning()))
				.setSequence(tab.getSeqNo())
				.setDisplayLogic(ValueUtil.validateNull(tab.getDisplayLogic()))
				.setIsAdvancedTab(tab.isAdvancedTab())
				.setIsDeleteable(table.isDeleteable())
				.setIsDocument(table.isDocument())
				.setIsHasTree(tab.isHasTree())
				.setIsInfoTab(tab.isInfoTab())
				.setIsInsertRecord(!isReadOnly && tab.isInsertRecord())
				.setIsReadOnly(isReadOnly)
				.setIsSingleRow(tab.isSingleRow())
				.setIsSortTab(tab.isSortTab())
				.setIsTranslationTab(tab.isTranslationTab())
				.setIsView(table.isView())
				.setTabLevel(tab.getTabLevel())
				.setTableName(ValueUtil.validateNull(table.getTableName()))
				.setQuery(ValueUtil.validateNull(getQueryWithReferencesFromTab(tab)))
				.setWhereClause(whereClause.toString())
				.setOrderByClause(ValueUtil.validateNull(tab.getOrderByClause()))
				.setParentTabUuid(ValueUtil.validateNull(parentTabUuid))
				.setIsChangeLog(table.isChangeLog())
				.setIsActive(tab.isActive());
		//	For link
		if(contextInfoId > 0) {
			ContextInfo.Builder contextInfoBuilder = convertContextInfo(context, contextInfoId);
			builder.setContextInfo(contextInfoBuilder.build());
		}
		//	Parent Link Column Name
		if(tab.getParent_Column_ID() > 0) {
			MColumn column = MColumn.get(context, tab.getParent_Column_ID());
			builder.setParentColumnName(column.getColumnName());
		}
		//	Link Column Name
		if(tab.getAD_Column_ID() > 0) {
			MColumn column = MColumn.get(context, tab.getAD_Column_ID());
			builder.setLinkColumnName(column.getColumnName());
		}
		if(tab.isSortTab()) {
			//	Sort Column
			if(tab.getAD_ColumnSortOrder_ID() > 0) {
				MColumn column = MColumn.get(context, tab.getAD_ColumnSortOrder_ID());
				builder.setSortOrderColumnName(column.getColumnName());
			}
			//	Sort Yes / No
			if(tab.getAD_ColumnSortYesNo_ID() > 0) {
				MColumn column = MColumn.get(context, tab.getAD_ColumnSortYesNo_ID());
				builder.setSortYesNoColumnName(column.getColumnName());
			}
		}
		//	Process
		List<MProcess> processList = getProcessActionFromTab(context, tab);
		if(processList != null
				&& processList.size() > 0) {
			for(MProcess process : processList) {
				Process.Builder processBuilder = convertProcess(context, process, true);
				builder.addProcesses(processBuilder.build());
			}
		}
		if(withFields) {
			for(MField field : ASPUtil.getInstance(context).getWindowFields(tab.getAD_Tab_ID())) {
				Field.Builder fieldBuilder = convertField(context, field, false);
				builder.addFields(fieldBuilder.build());
			}
		}
		//	
		return builder;
	}
	
	/**
	 * Get Parent column name from tab
	 * @param tab
	 * @return
	 */
	private String getParentColumnNameFromTab(MTab tab) {
		String parentColumnName = null;
		if(tab.getParent_Column_ID() != 0) {
			parentColumnName = MColumn.getColumnName(tab.getCtx(), tab.getParent_Column_ID());
		}
		return parentColumnName;
	}
	
	/**
	 * Get Link column name from tab
	 * @param tab
	 * @return
	 */
	private String getLinkColumnNameFromTab(MTab tab) {
		String parentColumnName = null;
		if(tab.getAD_Column_ID() != 0) {
			parentColumnName = MColumn.getColumnName(tab.getCtx(), tab.getAD_Column_ID());
		}
		return parentColumnName;
	}
	
	/**
	 * Convert Context Info to builder
	 * @param contextInfoId
	 * @return
	 */
	private ContextInfo.Builder convertContextInfo(Properties context, int contextInfoId) {
		ContextInfo.Builder builder = null;
		if(contextInfoId > 0) {
			MADContextInfo contextInfoValue = MADContextInfo.getById(context, contextInfoId);
			MMessage message = MMessage.get(context, contextInfoValue.getAD_Message_ID());
			//	Get translation
			String msgText = null;
			String msgTip = null;
			String language = Env.getAD_Language(context);
			if(!Util.isEmpty(language)) {
				msgText = message.get_Translation(I_AD_Message.COLUMNNAME_MsgText, language);
				msgTip = message.get_Translation(I_AD_Message.COLUMNNAME_MsgTip, language);
			}
			//	Validate for default
			if(Util.isEmpty(msgText)) {
				msgText = message.getMsgText();
			}
			if(Util.isEmpty(msgTip)) {
				msgTip = message.getMsgTip();
			}
			//	Add message text
			MessageText messageText = MessageText.newBuilder()
					.setId(message.getAD_Message_ID())
					.setUuid(ValueUtil.validateNull(message.getUUID()))
					.setValue(ValueUtil.validateNull(message.getValue()))
					.setMsgText(ValueUtil.validateNull(msgText))
					.setMsgTip(ValueUtil.validateNull(msgTip))
					.build();
			builder = ContextInfo.newBuilder()
					.setId(contextInfoValue.getAD_ContextInfo_ID())
					.setUuid(ValueUtil.validateNull(contextInfoValue.getUUID()))
					.setName(ValueUtil.validateNull(contextInfoValue.getName()))
					.setDescription(ValueUtil.validateNull(contextInfoValue.getDescription()))
					.setMessageText(messageText)
					.setSqlStatement(ValueUtil.validateNull(contextInfoValue.getSQLStatement()));
		}
		return builder;
	}
	
	/**
	 * Convert process to builder
	 * @param process
	 * @return
	 */
	private Process.Builder convertProcess(Properties context, MProcess process, boolean withParams) {
		process = ASPUtil.getInstance(context).getProcess(process.getAD_Process_ID());
		Process.Builder builder = Process.newBuilder()
				.setId(process.getAD_Process_ID())
				.setUuid(ValueUtil.validateNull(process.getUUID()))
				.setValue(ValueUtil.validateNull(process.getValue()))
				.setName(ValueUtil.validateNull(process.getName()))
				.setDescription(ValueUtil.validateNull(process.getDescription()))
				.setHelp(ValueUtil.validateNull(process.getHelp()))
				.setAccessLevel(Integer.parseInt(process.getAccessLevel()))
				.setIsDirectPrint(process.isDirectPrint())
				.setIsReport(process.isReport())
				.setIsActive(process.isActive());
		//	Report Types
		if(process.isReport()) {
			MReportView reportView = null;
			if(process.getAD_ReportView_ID() > 0) {
				reportView = MReportView.get(context, process.getAD_ReportView_ID());
			}
			ReportExportHandler exportHandler = new ReportExportHandler(Env.getCtx(), reportView);
			for(AbstractExportFormat reportType : exportHandler.getExportFormatList()) {
				ReportExportType.Builder reportExportType = ReportExportType.newBuilder();
				reportExportType.setName(ValueUtil.validateNull(reportType.getName()));
				reportExportType.setDescription(ValueUtil.validateNull(reportType.getName()));
				reportExportType.setType(ValueUtil.validateNull(reportType.getExtension()));
				builder.addReportExportTypes(reportExportType.build());
			}
		}
		//	For parameters
		if(withParams) {
			for(MProcessPara parameter : ASPUtil.getInstance(context).getProcessParameters(process.getAD_Process_ID())) {
				Field.Builder fieldBuilder = convertProcessParameter(context, parameter);
				builder.addParameters(fieldBuilder.build());
			}
		}
		return builder;
	}
	
	/**
	 * Convert process to builder
	 * @param browser
	 * @param withFields
	 * @return
	 */
	private Browser.Builder convertBrowser(Properties context, MBrowse browser, boolean withFields) {
		browser = ASPUtil.getInstance(context).getBrowse(browser.getAD_Browse_ID());
		String query = addQueryReferencesFromBrowser(browser, MView.getSQLFromView(browser.getAD_View_ID(), null));
		String orderByClause = getSQLOrderBy(browser);
		Browser.Builder builder = Browser.newBuilder()
				.setId(browser.getAD_Process_ID())
				.setUuid(ValueUtil.validateNull(browser.getUUID()))
				.setValue(ValueUtil.validateNull(browser.getValue()))
				.setName(browser.getName())
				.setDescription(ValueUtil.validateNull(browser.getDescription()))
				.setHelp(ValueUtil.validateNull(browser.getHelp()))
				.setAccessLevel(Integer.parseInt(browser.getAccessLevel()))
				.setIsActive(browser.isActive())
				.setIsCollapsibleByDefault(browser.isCollapsibleByDefault())
				.setIsDeleteable(browser.isDeleteable())
				.setIsExecutedQueryByDefault(browser.isExecutedQueryByDefault())
				.setIsSelectedByDefault(browser.isSelectedByDefault())
				.setIsShowTotal(browser.isShowTotal())
				.setIsUpdateable(browser.isUpdateable())
				.setQuery(ValueUtil.validateNull(query))
				.setWhereClause(ValueUtil.validateNull(browser.getWhereClause()))
				.setOrderByClause(ValueUtil.validateNull(orderByClause));
		//	Set View UUID
		if(browser.getAD_View_ID() > 0) {
			builder.setViewUuid(ValueUtil.validateNull(browser.getAD_View().getUUID()));
		}
		//	Window Reference
		if(browser.getAD_Window_ID() > 0) {
			MWindow window = new MWindow(context, browser.getAD_Window_ID(), null);
			Window.Builder windowBuilder = convertWindow(context, window, false);
			builder.setWindow(windowBuilder.build());
		}
		//	Process Reference
		if(browser.getAD_Process_ID() > 0) {
			Process.Builder processBuilder = convertProcess(context, MProcess.get(context, browser.getAD_Process_ID()), false);
			builder.setProcess(processBuilder.build());
		}
		//	For parameters
		if(withFields) {
			for(MBrowseField field : ASPUtil.getInstance(context).getBrowseFields(browser.getAD_Browse_ID())) {
				Field.Builder fieldBuilder = convertBrowseField(context, field);
				builder.addFields(fieldBuilder.build());
			}
		}
		//	Add to recent Item
		addToRecentItem(MMenu.ACTION_SmartBrowse, browser.getAD_Window_ID());
		return builder;
	}
	
	/**
	 * Add references to original query from smart browser
	 * @param originalQuery
	 * @return
	 */
	private String addQueryReferencesFromBrowser(MBrowse browser, String originalQuery) {
		int fromIndex = originalQuery.toUpperCase().indexOf(" FROM ");
		StringBuffer queryToAdd = new StringBuffer(originalQuery.substring(0, fromIndex));
		StringBuffer joinsToAdd = new StringBuffer(originalQuery.substring(fromIndex, originalQuery.length() - 1));
		for (MBrowseField browseField : browser.getDisplayFields()) {
			int displayTypeId = browseField.getAD_Reference_ID();
			if(DisplayType.isLookup(displayTypeId)) {
				//	Reference Value
				int referenceValueId = browseField.getAD_Reference_Value_ID();
				//	Validation Code
				String columnName = browseField.getAD_Element().getColumnName();
				String tableName = browseField.getAD_View_Column().getAD_View_Definition().getTableAlias();
				if(browseField.getAD_View_Column().getAD_Column_ID() > 0) {
					columnName = browseField.getAD_View_Column().getAD_Column().getColumnName();
				}
				queryToAdd.append(", ");
				ReferenceInfo referenceInfo = ReferenceUtil.getInstance(Env.getCtx()).getReferenceInfo(displayTypeId, referenceValueId, columnName, Env.getAD_Language(Env.getCtx()), tableName);
				if(referenceInfo != null) {
					queryToAdd.append(referenceInfo.getDisplayValue(browseField.getAD_View_Column().getColumnName()));
					joinsToAdd.append(referenceInfo.getJoinValue(columnName, tableName));
				}
			}
		}
		queryToAdd.append(joinsToAdd);
		return queryToAdd.toString();
	}
	
	/**
	 * Add references to original query from tab
	 * @param originalQuery
	 * @return
	 */
	private String getQueryWithReferencesFromTab(MTab tab) {
		MTable table = MTable.get(Env.getCtx(), tab.getAD_Table_ID());
		String originalQuery = "SELECT " + table.getTableName() + ".* FROM " + table.getTableName() + " AS " + table.getTableName() + " ";
		int fromIndex = originalQuery.toUpperCase().indexOf(" FROM ");
		StringBuffer queryToAdd = new StringBuffer(originalQuery.substring(0, fromIndex));
		StringBuffer joinsToAdd = new StringBuffer(originalQuery.substring(fromIndex, originalQuery.length() - 1));
		Language language = Language.getLanguage(Env.getAD_Language(Env.getCtx()));
		for (MField field : tab.getFields(false, null)) {
			if(!field.isDisplayed()) {
				continue;
			}
			MColumn column = MColumn.get(Env.getCtx(), field.getAD_Column_ID());
			int displayTypeId = field.getAD_Reference_ID();
			if(displayTypeId == 0) {
				displayTypeId = column.getAD_Reference_ID();
			}
			if(DisplayType.isLookup(displayTypeId)) {
				//	Reference Value
				int referenceValueId = field.getAD_Reference_Value_ID();
				if(referenceValueId == 0) {
					referenceValueId = column.getAD_Reference_Value_ID();
				}
				//	Validation Code
				String columnName = column.getColumnName();
				String tableName = table.getTableName();
				queryToAdd.append(", ");
				ReferenceInfo referenceInfo = ReferenceUtil.getInstance(Env.getCtx()).getReferenceInfo(displayTypeId, referenceValueId, columnName, language.getAD_Language(), tableName);
				if(referenceInfo != null) {
					queryToAdd.append(referenceInfo.getDisplayValue(columnName));
					joinsToAdd.append(referenceInfo.getJoinValue(columnName, tableName));
				}
			}
		}
		queryToAdd.append(joinsToAdd);
		return queryToAdd.toString();
	}
	
	/**
	 * Get Order By
	 * @param browser
	 * @return
	 */
	public String getSQLOrderBy(MBrowse browser) {
		StringBuilder sqlOrderBy = new StringBuilder();
		for (MBrowseField field : browser.getOrderByFields()) {
			if (field.isOrderBy()) {
				int orderByPosition = getOrderByPosition(browser, field);
				if (orderByPosition <= 0)
					continue;

				if (sqlOrderBy.length() > 0) {
					sqlOrderBy.append(",");
				}
				sqlOrderBy.append(orderByPosition);
			}
		}
		return sqlOrderBy.length() > 0 ? sqlOrderBy.toString(): "";
	}
	
	/**
	 * Get Order By Postirion for SB
	 * @param BrowserField
	 * @return
	 */
	private int getOrderByPosition(MBrowse browser, MBrowseField BrowserField) {
		int colOffset = 1; // columns start with 1
		int col = 0;
		for (MBrowseField field : browser.getFields()) {
			int sortBySqlNo = col + colOffset;
			if (BrowserField.getAD_Browse_Field_ID() == field.getAD_Browse_Field_ID())
				return sortBySqlNo;
			col ++;
		}
		return -1;
	}
	
	/**
	 * Get process action from tab
	 * @param tab
	 * @return
	 */
	private List<MProcess> getProcessActionFromTab(Properties context, MTab tab) {
		//	First Process Tab
		List<MProcess> processList = new ArrayList<>();
		if(tab.getAD_Process_ID() > 0) {
			processList.add(MProcess.get(context, tab.getAD_Process_ID()));
		}
		//	Process from tab
		List<MProcess> processFromTabList = new Query(tab.getCtx(), I_AD_Process.Table_Name, "EXISTS(SELECT 1 FROM AD_Field f "
				+ "INNER JOIN AD_Column c ON(c.AD_Column_ID = f.AD_Column_ID) "
				+ "WHERE c.AD_Process_ID = AD_Process.AD_Process_ID "
				+ "AND f.AD_Tab_ID = ? "
				+ "AND f.IsActive = 'Y')", null)
				.setParameters(tab.getAD_Tab_ID())
				.setOnlyActiveRecords(true)
				.<MProcess>list();
		for(MProcess process : processFromTabList) {
			processList.add(process);
		}
		//	Process from table
		List<MProcess> processFromTableList = new Query(tab.getCtx(), I_AD_Process.Table_Name, 
				"EXISTS(SELECT 1 FROM AD_Table_Process WHERE AD_Process_ID = AD_Process.AD_Process_ID AND AD_Table_ID = ?)", null)
				.setParameters(tab.getAD_Table_ID())
				.setOnlyActiveRecords(true)
				.<MProcess>list();
		for(MProcess process : processFromTableList) {
			processList.add(process);
		}
		return processList;
	}
	
	/**
	 * Convert Process Parameter
	 * @param processParameter
	 * @return
	 */
	private Field.Builder convertProcessParameter(Properties context, MProcessPara processParameter) {
		//	Convert
		Field.Builder builder = Field.newBuilder()
				.setId(processParameter.getAD_Process_Para_ID())
				.setUuid(ValueUtil.validateNull(processParameter.getUUID()))
				.setName(ValueUtil.validateNull(processParameter.getName()))
				.setDescription(ValueUtil.validateNull(processParameter.getDescription()))
				.setHelp(ValueUtil.validateNull(processParameter.getHelp()))
				.setColumnName(ValueUtil.validateNull(processParameter.getColumnName()))
				.setElementName(ValueUtil.validateNull(processParameter.getColumnName()))
				.setDefaultValue(ValueUtil.validateNull(processParameter.getDefaultValue()))
				.setDefaultValueTo(ValueUtil.validateNull(processParameter.getDefaultValue2()))
				.setDisplayLogic(ValueUtil.validateNull(processParameter.getDisplayLogic()))
				.setDisplayType(processParameter.getAD_Reference_ID())
				.setIsDisplayed(true)
				.setIsInfoOnly(processParameter.isInfoOnly())
				.setIsMandatory(processParameter.isMandatory())
				.setIsRange(processParameter.isRange())
				.setReadOnlyLogic(ValueUtil.validateNull(processParameter.getReadOnlyLogic()))
				.setSequence(processParameter.getSeqNo())
				.setValueMax(ValueUtil.validateNull(processParameter.getValueMax()))
				.setValueMin(ValueUtil.validateNull(processParameter.getValueMin()))
				.setVFormat(ValueUtil.validateNull(processParameter.getVFormat()))
				.setFieldLength(processParameter.getFieldLength())
				.setIsActive(processParameter.isActive());
		//	
		int displayTypeId = processParameter.getAD_Reference_ID();
		if(DisplayType.isLookup(displayTypeId)) {
			//	Reference Value
			int referenceValueId = processParameter.getAD_Reference_Value_ID();
			//	Validation Code
			int validationRuleId = processParameter.getAD_Val_Rule_ID();
			//	Set Validation Code
			String validationCode = null;
			if(validationRuleId > 0) {
				MValRule validationRule = MValRule.get(context, validationRuleId);
				validationCode = validationRule.getCode();
			}
			String columnName = processParameter.getColumnName();
			if(processParameter.getAD_Element_ID() > 0) {
				columnName = processParameter.getAD_Element().getColumnName();
			}
			MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, 0, displayTypeId, Language.getLanguage(Env.getAD_Language(context)), columnName, referenceValueId, false, validationCode, false);
			Reference.Builder referenceBuilder = convertReference(context, info);
			builder.setReference(referenceBuilder.build());
		}
		return builder;
	}
	
	/**
	 * Convert Browse Field
	 * @param browseField
	 * @return
	 */
	private Field.Builder convertBrowseField(Properties context, MBrowseField browseField) {
		//	Convert
		Field.Builder builder = Field.newBuilder()
				.setId(browseField.getAD_Browse_Field_ID())
				.setUuid(ValueUtil.validateNull(browseField.getUUID()))
				.setName(ValueUtil.validateNull(browseField.getName()))
				.setDescription(ValueUtil.validateNull(browseField.getDescription()))
				.setHelp(ValueUtil.validateNull(browseField.getHelp()))
				.setDefaultValue(ValueUtil.validateNull(browseField.getDefaultValue()))
				.setDefaultValueTo(ValueUtil.validateNull(browseField.getDefaultValue2()))
				.setDisplayLogic(ValueUtil.validateNull(browseField.getDisplayLogic()))
				.setDisplayType(browseField.getAD_Reference_ID())
				.setIsDisplayed(browseField.isDisplayed())
				.setIsQueryCriteria(browseField.isQueryCriteria())
				.setIsOrderBy(browseField.isOrderBy())
				.setIsInfoOnly(browseField.isInfoOnly())
				.setIsMandatory(browseField.isMandatory())
				.setIsRange(browseField.isRange())
				.setIsReadOnly(browseField.isReadOnly())
				.setReadOnlyLogic(ValueUtil.validateNull(browseField.getReadOnlyLogic()))
				.setIsKey(browseField.isKey())
				.setIsIdentifier(browseField.isIdentifier())
				.setSeqNoGrid(browseField.getSeqNoGrid())
				.setSequence(browseField.getSeqNo())
				.setValueMax(ValueUtil.validateNull(browseField.getValueMax()))
				.setValueMin(ValueUtil.validateNull(browseField.getValueMin()))
				.setVFormat(ValueUtil.validateNull(browseField.getVFormat()))
				.setIsActive(browseField.isActive())
				.setCallout(ValueUtil.validateNull(browseField.getCallout()))
				.setFieldLength(browseField.getFieldLength())
				.setDisplayType(browseField.getAD_Reference_ID());
		builder.setColumnName(ValueUtil.validateNull(browseField.getAD_View_Column().getColumnName()));
		String elementName = null;
		if(browseField.getAD_View_Column().getAD_Column_ID() != 0) {
			MColumn column = MColumn.get(context, browseField.getAD_View_Column().getAD_Column_ID());
			elementName = column.getColumnName();
		}
		//	Default element
		if(Util.isEmpty(elementName)) {
			elementName = browseField.getAD_Element().getColumnName();
		}
		builder.setElementName(ValueUtil.validateNull(elementName));
		//	
		int displayTypeId = browseField.getAD_Reference_ID();
		if(DisplayType.isLookup(displayTypeId)) {
			//	Reference Value
			int referenceValueId = browseField.getAD_Reference_Value_ID();
			//	Validation Code
			int validationRuleId = browseField.getAD_Val_Rule_ID();
			//	Set Validation Code
			String validationCode = null;
			if(validationRuleId > 0) {
				MValRule validationRule = MValRule.get(context, validationRuleId);
				validationCode = validationRule.getCode();
			}
			String columnName = browseField.getAD_Element().getColumnName();
			if(browseField.getAD_View_Column().getAD_Column_ID() > 0) {
				columnName = browseField.getAD_View_Column().getAD_Column().getColumnName();
			}
			
			MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, 0, displayTypeId, Language.getLanguage(Env.getAD_Language(context)), columnName, referenceValueId, false, validationCode, false);
			if(info != null) {
				Reference.Builder referenceBuilder = convertReference(context, info);
				builder.setReference(referenceBuilder.build());
			} else {
				builder.setDisplayType(DisplayType.String);
			}
		}
		return builder;
	}
	
	/**
	 * Convert field from request
	 * @param context
	 * @param request
	 * @return
	 */
	private Field.Builder convertField(Properties context, FieldRequest request) {
		Field.Builder builder = Field.newBuilder();
		//	For UUID
		if(!Util.isEmpty(request.getFieldUuid())) {
			builder = convertField(context, request.getFieldUuid());
		} else if(!Util.isEmpty(request.getColumnUuid())) {
			MColumn column = new Query(context, I_AD_Column.Table_Name, I_AD_Column.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getColumnUuid())
					.setOnlyActiveRecords(true)
					.first();
			builder = convertField(context, column);
		} else if(!Util.isEmpty(request.getElementUuid())) {
			M_Element element = new Query(context, I_AD_Element.Table_Name, I_AD_Element.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getElementUuid())
					.setOnlyActiveRecords(true)
					.first();
			builder = convertField(context, element);
		} else if(!Util.isEmpty(request.getElementColumnName())) {
			M_Element element = new Query(context, I_AD_Element.Table_Name, I_AD_Element.COLUMNNAME_ColumnName+ " = ?", null)
					.setParameters(request.getElementColumnName())
					.setOnlyActiveRecords(true)
					.first();
			builder = convertField(context, element);
		} else if(!Util.isEmpty(request.getTableName()) 
				&& !Util.isEmpty(request.getColumnName())) {
			MTable table = MTable.get(context, request.getTableName());
			if(table != null) {
				MColumn column = table.getColumn(request.getColumnName());
				builder = convertField(context, column);
			}
		}
		return builder;
	}
	
	/**
	 * Convert Field from UUID
	 * @param uuid
	 * @return
	 */
	private Field.Builder convertField(Properties context, String uuid) {
		MField field = new Query(context, I_AD_Field.Table_Name, I_AD_Field.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertField(context, field, true);
	}
	
	/**
	 * Convert field to builder
	 * @param column
	 * @param language
	 * @return
	 */
	private Field.Builder convertField(Properties context, MColumn column) {
		String defaultValue = column.getDefaultValue();
		if(Util.isEmpty(defaultValue)) {
			defaultValue = column.getDefaultValue();
		}
		//	Display Type
		int displayTypeId = column.getAD_Reference_ID();
		if(column.getAD_Reference_ID() > 0) {
			displayTypeId = column.getAD_Reference_ID();
		}
		//	Convert
		Field.Builder builder = Field.newBuilder()
				.setId(column.getAD_Column_ID())
				.setUuid(ValueUtil.validateNull(column.getUUID()))
				.setName(ValueUtil.validateNull(column.getName()))
				.setDescription(ValueUtil.validateNull(column.getDescription()))
				.setHelp(ValueUtil.validateNull(column.getHelp()))
				.setCallout(ValueUtil.validateNull(column.getCallout()))
				.setColumnName(ValueUtil.validateNull(column.getColumnName()))
				.setElementName(ValueUtil.validateNull(column.getColumnName()))
				.setColumnSQL(ValueUtil.validateNull(column.getColumnSQL()))
				.setDefaultValue(ValueUtil.validateNull(defaultValue))
				.setDisplayType(displayTypeId)
				.setFormatPattern(ValueUtil.validateNull(column.getFormatPattern()))
				.setIdentifierSequence(column.getSeqNo())
				.setIsAllowCopy(column.isAllowCopy())
				.setIsAllowLogging(column.isAllowLogging())
				.setIsAlwaysUpdateable(column.isAlwaysUpdateable())
				.setIsEncrypted(column.isEncrypted())
				.setIsIdentifier(column.isIdentifier())
				.setIsKey(column.isKey())
				.setIsMandatory(column.isMandatory())
				.setIsParent(column.isParent())
				.setIsRange(column.isRange())
				.setIsSelectionColumn(column.isSelectionColumn())
				.setIsTranslated(column.isTranslated())
				.setIsUpdateable(column.isUpdateable())
				.setMandatoryLogic(ValueUtil.validateNull(column.getMandatoryLogic()))
				.setReadOnlyLogic(ValueUtil.validateNull(column.getReadOnlyLogic()))
				.setSequence(column.getSeqNo())
				.setValueMax(ValueUtil.validateNull(column.getValueMax()))
				.setValueMin(ValueUtil.validateNull(column.getValueMin()))
				.setFieldLength(column.getFieldLength())
				.setIsActive(column.isActive());
		//	Process
		if(column.getAD_Process_ID() > 0) {
			MProcess process = MProcess.get(context, column.getAD_Process_ID());
			Process.Builder processBuilder = convertProcess(context, process, false);
			builder.setProcess(processBuilder.build());
		}
		//	
		if(DisplayType.isLookup(displayTypeId)) {
			//	Reference Value
			int referenceValueId = column.getAD_Reference_Value_ID();
			if(column.getAD_Reference_Value_ID() > 0) {
				referenceValueId = column.getAD_Reference_Value_ID();
			}
			//	Validation Code
			int validationRuleId = column.getAD_Val_Rule_ID();
			if(column.getAD_Val_Rule_ID() > 0) {
				validationRuleId = column.getAD_Val_Rule_ID();
			}
			//	Set Validation Code
			String validationCode = null;
			if(validationRuleId > 0) {
				MValRule validationRule = MValRule.get(context, validationRuleId);
				validationCode = validationRule.getCode();
			}
			MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, column.getAD_Column_ID(), displayTypeId, Language.getLanguage(Env.getAD_Language(context)), column.getColumnName(), referenceValueId, false, validationCode, false);
			if(info != null) {
				Reference.Builder referenceBuilder = convertReference(context, info);
				builder.setReference(referenceBuilder.build());
			} else {
				builder.setDisplayType(DisplayType.String);
			}
		}
		return builder;
	}
	
	/**
	 * Convert field to builder
	 * @param element
	 * @return
	 */
	private Field.Builder convertField(Properties context, M_Element element) {
		//	Display Type
		int displayTypeId = element.getAD_Reference_ID();
		if(element.getAD_Reference_ID() > 0) {
			displayTypeId = element.getAD_Reference_ID();
		}
		//	Convert
		Field.Builder builder = Field.newBuilder()
				.setId(element.getAD_Element_ID())
				.setUuid(ValueUtil.validateNull(element.getUUID()))
				.setName(ValueUtil.validateNull(ValueUtil.getTranslation(element, M_Element.COLUMNNAME_Name)))
				.setDescription(ValueUtil.validateNull(ValueUtil.getTranslation(element, M_Element.COLUMNNAME_Description)))
				.setHelp(ValueUtil.validateNull(ValueUtil.getTranslation(element, M_Element.COLUMNNAME_Help)))
				.setColumnName(ValueUtil.validateNull(element.getColumnName()))
				.setElementName(ValueUtil.validateNull(element.getColumnName()))
				.setDisplayType(displayTypeId)
				.setFieldLength(element.getFieldLength())
				.setIsActive(element.isActive());
		//	
		if(DisplayType.isLookup(displayTypeId)) {
			//	Reference Value
			int referenceValueId = element.getAD_Reference_Value_ID();
			if(element.getAD_Reference_Value_ID() > 0) {
				referenceValueId = element.getAD_Reference_Value_ID();
			}
			MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, element.getAD_Element_ID(), displayTypeId, Language.getLanguage(Env.getAD_Language(context)), element.getColumnName(), referenceValueId, false, null, false);
			if(info != null) {
				Reference.Builder referenceBuilder = convertReference(context, info);
				builder.setReference(referenceBuilder.build());
			} else {
				builder.setDisplayType(DisplayType.String);
			}
		}
		return builder;
	}
	
	/**
	 * Convert field to builder
	 * @param field
	 * @param translate
	 * @return
	 */
	private Field.Builder convertField(Properties context, MField field, boolean translate) {
		//`Column reference
		MColumn column = MColumn.get(context, field.getAD_Column_ID());
		String defaultValue = field.getDefaultValue();
		if(Util.isEmpty(defaultValue)) {
			defaultValue = column.getDefaultValue();
		}
		//	Display Type
		int displayTypeId = column.getAD_Reference_ID();
		if(field.getAD_Reference_ID() > 0) {
			displayTypeId = field.getAD_Reference_ID();
		}
		//	Mandatory Property
		boolean isMandatory = column.isMandatory();
		if(!Util.isEmpty(field.getIsMandatory())) {
			isMandatory = !Util.isEmpty(field.getIsMandatory()) && field.getIsMandatory().equals("Y");
		}
		//	Convert
		Field.Builder builder = Field.newBuilder()
				.setId(field.getAD_Field_ID())
				.setUuid(ValueUtil.validateNull(field.getUUID()))
				.setName(ValueUtil.validateNull(ValueUtil.getTranslation(field, MField.COLUMNNAME_Name)))
				.setDescription(ValueUtil.validateNull(ValueUtil.getTranslation(field, MField.COLUMNNAME_Description)))
				.setHelp(ValueUtil.validateNull(ValueUtil.getTranslation(field, MField.COLUMNNAME_Help)))
				.setCallout(ValueUtil.validateNull(column.getCallout()))
				.setColumnName(ValueUtil.validateNull(column.getColumnName()))
				.setElementName(ValueUtil.validateNull(column.getColumnName()))
				.setColumnSQL(ValueUtil.validateNull(column.getColumnSQL()))
				.setDefaultValue(ValueUtil.validateNull(defaultValue))
				.setDisplayLogic(ValueUtil.validateNull(field.getDisplayLogic()))
				.setDisplayType(displayTypeId)
				.setFormatPattern(ValueUtil.validateNull(column.getFormatPattern()))
				.setIdentifierSequence(column.getSeqNo())
				.setIsAllowCopy(field.isAllowCopy())
				.setIsAllowLogging(column.isAllowLogging())
				.setIsDisplayed(field.isDisplayed())
				.setIsAlwaysUpdateable(column.isAlwaysUpdateable())
				.setIsDisplayedGrid(field.isDisplayedGrid())
				.setIsEncrypted(field.isEncrypted() || column.isEncrypted())
				.setIsFieldOnly(field.isFieldOnly())
				.setIsHeading(field.isHeading())
				.setIsIdentifier(column.isIdentifier())
				.setIsKey(column.isKey())
				.setIsMandatory(isMandatory)
				.setIsParent(column.isParent())
				.setIsQuickEntry(field.isQuickEntry())
				.setIsRange(column.isRange())
				.setIsReadOnly(field.isReadOnly())
				.setIsSameLine(field.isSameLine())
				.setIsSelectionColumn(column.isSelectionColumn())
				.setIsTranslated(column.isTranslated())
				.setIsUpdateable(column.isUpdateable())
				.setMandatoryLogic(ValueUtil.validateNull(column.getMandatoryLogic()))
				.setReadOnlyLogic(ValueUtil.validateNull(column.getReadOnlyLogic()))
				.setSequence(field.getSeqNo())
				.setValueMax(ValueUtil.validateNull(column.getValueMax()))
				.setValueMin(ValueUtil.validateNull(column.getValueMin()))
				.setFieldLength(column.getFieldLength())
				.setIsActive(field.isActive());
		//	Context Info
		if(field.getAD_ContextInfo_ID() > 0) {
			ContextInfo.Builder contextInfoBuilder = convertContextInfo(context, field.getAD_ContextInfo_ID());
			builder.setContextInfo(contextInfoBuilder.build());
		}
		//	Process
		if(column.getAD_Process_ID() > 0) {
			MProcess process = MProcess.get(context, column.getAD_Process_ID());
			Process.Builder processBuilder = convertProcess(context, process, false);
			builder.setProcess(processBuilder.build());
		}
		//	
		if(DisplayType.isLookup(displayTypeId)) {
			//	Reference Value
			int referenceValueId = column.getAD_Reference_Value_ID();
			if(field.getAD_Reference_Value_ID() > 0) {
				referenceValueId = field.getAD_Reference_Value_ID();
			}
			//	Validation Code
			int validationRuleId = column.getAD_Val_Rule_ID();
			if(field.getAD_Val_Rule_ID() > 0) {
				validationRuleId = field.getAD_Val_Rule_ID();
			}
			//	Set Validation Code
			String validationCode = null;
			if(validationRuleId > 0) {
				MValRule validationRule = MValRule.get(context, validationRuleId);
				validationCode = validationRule.getCode();
			}
			MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, column.getAD_Column_ID(), displayTypeId, Language.getLanguage(Env.getAD_Language(context)), column.getColumnName(), referenceValueId, false, validationCode, false);
			if(info != null) {
				Reference.Builder referenceBuilder = convertReference(context, info);
				builder.setReference(referenceBuilder.build());
			} else {
				builder.setDisplayType(DisplayType.String);
			}
		}
		
		//	Field Definition
		if(field.getAD_FieldDefinition_ID() > 0) {
			FieldDefinition.Builder fieldDefinitionBuilder = convertFieldDefinition(context, field.getAD_FieldDefinition_ID());
			builder.setFieldDefinition(fieldDefinitionBuilder);
		}
		//	Field Group
		if(field.getAD_FieldGroup_ID() > 0) {
			FieldGroup.Builder fieldGroup = convertFieldGroup(context, field.getAD_FieldGroup_ID());
			builder.setFieldGroup(fieldGroup.build());
		}
		return builder;
	}
	
	/**
	 * Convert Field Definition to builder
	 * @param fieldDefinitionId
	 * @return
	 */
	private FieldDefinition.Builder convertFieldDefinition(Properties context, int fieldDefinitionId) {
		FieldDefinition.Builder builder = null;
		if(fieldDefinitionId > 0) {
			MADFieldDefinition fieldDefinition  = new MADFieldDefinition(context, fieldDefinitionId, null);
			//	Reference
			builder = FieldDefinition.newBuilder()
					.setId(fieldDefinition.getAD_FieldDefinition_ID())
					.setUuid(ValueUtil.validateNull(fieldDefinition.getUUID()))
					.setValue(ValueUtil.validateNull(fieldDefinition.getValue()))
					.setName(ValueUtil.validateNull(fieldDefinition.getName()));
			//	Get conditions
			for(MADFieldCondition condition : fieldDefinition.getConditions()) {
				if(!condition.isActive()) {
					continue;
				}
				FieldCondition.Builder fieldConditionBuilder = FieldCondition.newBuilder()
						.setId(fieldDefinition.getAD_FieldDefinition_ID())
						.setUuid(ValueUtil.validateNull(condition.getUUID()))
						.setCondition(ValueUtil.validateNull(condition.getCondition()))
						.setStylesheet(ValueUtil.validateNull(condition.getStylesheet()))
						.setIsActive(fieldDefinition.isActive());
				//	Add to parent
				builder.addConditions(fieldConditionBuilder);
			}
		}
		return builder;
	}
	
	/**
	 * Convert Field Group to builder
	 * @param fieldGroupId
	 * @return
	 */
	private FieldGroup.Builder convertFieldGroup(Properties context, int fieldGroupId) {
		FieldGroup.Builder builder = null;
		if(fieldGroupId > 0) {
			X_AD_FieldGroup fieldGroup  = new X_AD_FieldGroup(context, fieldGroupId, null);
			//	Get translation
			String name = null;
			String language = Env.getAD_Language(context);
			if(!Util.isEmpty(language)) {
				name = fieldGroup.get_Translation(I_AD_FieldGroup.COLUMNNAME_Name, language);
			}
			//	Validate for default
			if(Util.isEmpty(name)) {
				name = fieldGroup.getName();
			}
			//	Field Group
			builder = FieldGroup.newBuilder()
					.setId(fieldGroup.getAD_FieldGroup_ID())
					.setUuid(ValueUtil.validateNull(fieldGroup.getUUID()))
					.setName(ValueUtil.validateNull(name))
					.setFieldGroupType(fieldGroup.getFieldGroupType())
					.setIsActive(fieldGroup.isActive());
		}
		return builder;
	}
	
	/**
	 * Convert reference from a request
	 * @param context
	 * @param request
	 * @return
	 */
	private Reference.Builder convertReference(Properties context, ReferenceRequest request) {
		Reference.Builder builder = Reference.newBuilder();
		if(!Util.isEmpty(request.getReferenceUuid())) {
			X_AD_Reference reference = new Query(context, I_AD_Reference.Table_Name, I_AD_Reference.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getReferenceUuid())
					.first();
			if(reference.getValidationType().equals(X_AD_Reference.VALIDATIONTYPE_TableValidation)) {
				MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, 0, DisplayType.Search, Language.getLanguage(Env.getAD_Language(context)), null, reference.getAD_Reference_ID(), false, null, false);
				if(info != null) {
					builder = convertReference(context, info);
				}
			} else if(reference.getValidationType().equals(X_AD_Reference.VALIDATIONTYPE_ListValidation)) {
				MLookupInfo info = MLookupFactory.getLookup_List(Language.getLanguage(Env.getAD_Language(context)), reference.getAD_Reference_ID());
				if(info != null) {
					builder = convertReference(context, info);
				}
			}
		} else if(!Util.isEmpty(request.getColumnName())) {
			MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, 0, DisplayType.TableDir, Language.getLanguage(Env.getAD_Language(context)), request.getColumnName(), 0, false, null, false);
			if(info != null) {
				builder = convertReference(context, info);
			}
		}
		return builder;
	}
	
	/**
	 * Convert Validation rule
	 * @param context
	 * @param request
	 * @return
	 */
	private ValidationRule.Builder convertValidationRule(Properties context, ValidationRuleRequest request) {
		MValRule validationRule = new Query(context, I_AD_Val_Rule.Table_Name, I_AD_Val_Rule.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getValidationRuleUuid())
				.first();
		return ValidationRule.newBuilder()
				.setValidationRuleUuid(ValueUtil.validateNull(validationRule.getUUID()))
				.setName(ValueUtil.validateNull(validationRule.getName()))
				.setDescription(ValueUtil.validateNull(validationRule.getDescription()))
				.setValidationCode(ValueUtil.validateNull(validationRule.getCode()))
				.setType(ValueUtil.validateNull(validationRule.getType()))
				;
	}
	
	/**
	 * Convert Reference to builder
	 * @param info
	 * @return
	 */
	private Reference.Builder convertReference(Properties context, MLookupInfo info) {
		Reference.Builder builder = Reference.newBuilder()
				.setTableName(ValueUtil.validateNull(info.TableName))
				.setKeyColumnName(ValueUtil.validateNull(info.KeyColumn))
				.setDisplayColumnName(ValueUtil.validateNull(info.DisplayColumn))
				.setDirectQuery(ValueUtil.validateNull(info.QueryDirect))
				.setValidationCode(ValueUtil.validateNull(info.ValidationCode));
		//	For validation
		String queryForLookup = info.Query;
		if(!Util.isEmpty(info.ValidationCode)) {
			int positionFrom = queryForLookup.lastIndexOf(" FROM ");
			boolean hasWhereClause = queryForLookup.indexOf(" WHERE ", positionFrom) != -1;
			//
			int positionOrder = queryForLookup.lastIndexOf(" ORDER BY ");
			if (positionOrder != -1) {
				queryForLookup = queryForLookup.substring(0, positionOrder) 
						+ (hasWhereClause ? " AND " : " WHERE ") 
						+ info.ValidationCode
						+ queryForLookup.substring(positionOrder);
			} else {			
				queryForLookup += (hasWhereClause ? " AND " : " WHERE ") + info.ValidationCode;
			}
		}
		//	For Query
		builder.setQuery(ValueUtil.validateNull(queryForLookup));
		//	Window Reference
		if(info.ZoomWindow > 0) {
			builder.addWindows(convertZoomWindow(context, info.ZoomWindow).build());
		}
		if(info.ZoomWindowPO > 0) {
			builder.addWindows(convertZoomWindow(context, info.ZoomWindowPO).build());
		}
		//	Return
		return builder;
	}
	
	/**
	 * Convert Zoom Window from ID
	 * @param windowId
	 * @return
	 */
	private ZoomWindow.Builder convertZoomWindow(Properties context, int windowId) {
		MWindow window = new MWindow(context, windowId, null);
		//	Get translation
		String name = null;
		String description = null;
		String language = Env.getAD_Language(context);
		if(!Util.isEmpty(language)) {
			name = window.get_Translation(I_AD_Window.COLUMNNAME_Name, language);
			description = window.get_Translation(I_AD_Window.COLUMNNAME_Description, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = window.getName();
		}
		if(Util.isEmpty(description)) {
			description = window.getDescription();
		}
		//	Return
		return ZoomWindow.newBuilder()
				.setId(window.getAD_Window_ID())
				.setUuid(ValueUtil.validateNull(window.getUUID()))
				.setName(ValueUtil.validateNull(name))
				.setDescription(ValueUtil.validateNull(description))
				.setIsSOTrx(window.isSOTrx());
	}
}

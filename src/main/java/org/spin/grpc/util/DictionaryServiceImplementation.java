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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.I_AD_Browse;
import org.adempiere.model.I_AD_Browse_Field;
import org.adempiere.model.MBrowse;
import org.adempiere.model.MBrowseField;
import org.adempiere.model.MView;
import org.compiere.model.I_AD_Field;
import org.compiere.model.I_AD_FieldGroup;
import org.compiere.model.I_AD_Form;
import org.compiere.model.I_AD_Menu;
import org.compiere.model.I_AD_Message;
import org.compiere.model.I_AD_Process;
import org.compiere.model.I_AD_Process_Para;
import org.compiere.model.I_AD_Session;
import org.compiere.model.I_AD_Tab;
import org.compiere.model.I_AD_Window;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MMenu;
import org.compiere.model.MMessage;
import org.compiere.model.MProcess;
import org.compiere.model.MProcessPara;
import org.compiere.model.MRecentItem;
import org.compiere.model.MReportView;
import org.compiere.model.MSession;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MValRule;
import org.compiere.model.MWindow;
import org.compiere.model.Query;
import org.compiere.model.X_AD_FieldGroup;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Util;
import org.spin.grpc.util.DictionaryServiceGrpc.DictionaryServiceImplBase;
import org.spin.model.MADContextInfo;
import org.spin.model.MADFieldCondition;
import org.spin.model.MADFieldDefinition;
import org.spin.util.AbstractExportFormat;
import org.spin.util.ReportExportHandler;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class DictionaryServiceImplementation extends DictionaryServiceImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(DictionaryServiceImplementation.class);
	/**	Session Context	*/
	private static CCache<String, Properties> sessionsContext = new CCache<String, Properties>("DictionaryServiceImplementation", 30, 0);	//	no time-out	
	/**	Language */
	private static CCache<String, String> languageCache = new CCache<String, String>("Language_ISO_Code", 30, 0);	//	no time-out
	
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
	
	/**
	 * Get context from session for System
	 * @param request
	 * @return
	 */
	private Properties getContext(ApplicationRequest request) {
		Properties context = sessionsContext.get(request.getSessionUuid());
		if(context != null) {
			Env.setContext(context, Env.LANGUAGE, getDefaultLanguage(request.getLanguage()));
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
		Env.setContext(context, Env.LANGUAGE, getDefaultLanguage(request.getLanguage()));
		//	Save to Cache
		sessionsContext.put(request.getSessionUuid(), context);
		return context;
	}
	
	/**
	 * Get Default from language
	 * @param language
	 * @return
	 */
	//	TODO: Change it for a class and reuse
	private String getDefaultLanguage(String language) {
		String defaultLanguage = language;
		if(Util.isEmpty(language)) {
			language = Language.AD_Language_en_US;
		}
		//	Using es / en instead es_VE / en_US
		//	get default
		if(language.length() == 2) {
			defaultLanguage = languageCache.get(language);
			if(!Util.isEmpty(defaultLanguage)) {
				return defaultLanguage;
			}
			defaultLanguage = DB.getSQLValueString(null, "SELECT AD_Language "
					+ "FROM AD_Language "
					+ "WHERE LanguageISO = ? "
					+ "AND IsSystemLanguage = 'Y'", language);
		}
		if(Util.isEmpty(defaultLanguage)) {
			defaultLanguage = Language.AD_Language_en_US;
		}
		//	Default return
		return defaultLanguage;
	}
	
	@Override
	public void getField(EntityRequest request, StreamObserver<Field> responseObserver) {
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
			String language = null;
			if(applicationInfo != null) {
				language = getDefaultLanguage(applicationInfo.getLanguage());
			}
			Properties context = getContext(request.getApplicationRequest());
			Field.Builder fieldBuilder = convertField(context, request.getUuid(), language);
			responseObserver.onNext(fieldBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
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
			String language = null;
			if(applicationInfo != null) {
				language = getDefaultLanguage(applicationInfo.getLanguage());
			}
			Properties context = getContext(request.getApplicationRequest());
			Process.Builder processBuilder = convertProcess(context, request.getUuid(), language, true);
			responseObserver.onNext(processBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
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
			String language = null;
			if(applicationInfo != null) {
				language = getDefaultLanguage(applicationInfo.getLanguage());
			}
			Properties context = getContext(request.getApplicationRequest());
			Browser.Builder browserBuilder = convertBrowser(context, request.getUuid(), language, true);
			responseObserver.onNext(browserBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
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
			String language = null;
			if(applicationInfo != null) {
				language = getDefaultLanguage(applicationInfo.getLanguage());
			}
			Properties context = getContext(request.getApplicationRequest());
			Window.Builder windowBuilder = convertWindow(context, request.getUuid(), language, withTabs);
			responseObserver.onNext(windowBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
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
			String language = null;
			if(applicationInfo != null) {
				language = getDefaultLanguage(applicationInfo.getLanguage());
			}
			Properties context = getContext(request.getApplicationRequest());
			Tab.Builder tabBuilder = convertTab(context, request.getUuid(), language, withFields);
			responseObserver.onNext(tabBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
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
	private Window.Builder convertWindow(Properties context, String uuid, String language, boolean withTabs) {
		MWindow window = new Query(context, I_AD_Window.Table_Name, I_AD_Window.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		return convertWindow(context, window, language, withTabs);
	}
	
	/**
	 * Convert Window from Window Model
	 * @param window
	 * @param language
	 * @param withTabs
	 * @return
	 */
	private Window.Builder convertWindow(Properties context, MWindow window, String language, boolean withTabs) {
		//	
		Window.Builder builder = null;
		//	Validate
		if(window != null) {
			//	
			ContextInfo.Builder contextInfoBuilder = convertContextInfo(context, window.getAD_ContextInfo_ID(), language);
			//	Translation
			String name = null;
			String description = null;
			String help = null;
			if(!Util.isEmpty(language)) {
				name = window.get_Translation(I_AD_Window.COLUMNNAME_Name, language);
				description = window.get_Translation(I_AD_Window.COLUMNNAME_Description, language);
				help = window.get_Translation(I_AD_Window.COLUMNNAME_Help, language);
			}
			//	Validate for default
			if(Util.isEmpty(name)) {
				name = window.getName();
			}
			if(Util.isEmpty(description)) {
				description = window.getDescription();
			}
			if(Util.isEmpty(help)) {
				help = window.getHelp();
			}
			//	
			builder = Window.newBuilder()
					.setId(window.getAD_Window_ID())
					.setUuid(validateNull(window.getUUID()))
					.setName(name)
					.setDescription(validateNull(description))
					.setHelp(validateNull(help))
					.setWindowType(validateNull(window.getWindowType()))
					.setIsSOTrx(window.isSOTrx())
					.setIsActive(window.isActive());
			if(contextInfoBuilder != null) {
				builder.setContextInfo(contextInfoBuilder.build());
			}
			//	With Tabs
			if(withTabs) {
				List<Tab.Builder> tabListForGroup = new ArrayList<>();
				List<MTab> tabs = Arrays.asList(window.getTabs(false, null));
				for(MTab tab : tabs) {
					if(!tab.isActive()) {
						continue;
					}
					Tab.Builder tabBuilder = convertTab(context, tab, tabs, language, false);
					builder.addTabs(tabBuilder.build());
					//	Get field group
					int [] fieldGroupIdArray = getFieldGroupIdsFromTab(tab.getAD_Tab_ID());
					if(fieldGroupIdArray != null) {
						for(int fieldGroupId : fieldGroupIdArray) {
							Tab.Builder tabFieldGroup = convertTab(context, tab, language, false);
							FieldGroup.Builder fieldGroup = convertFieldGroup(context, fieldGroupId, language);
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
		}
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
	 * @param language
	 * @param withFields
	 * @return
	 */
	private Tab.Builder convertTab(Properties context, String uuid, String language, boolean withFields) {
		MTab tab = new Query(context, I_AD_Tab.Table_Name, I_AD_Tab.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertTab(context, tab, language, withFields);
	}
	
	/**
	 * Convert Process from UUID
	 * @param uuid
	 * @param language
	 * @param withParameters
	 * @return
	 */
	private Process.Builder convertProcess(Properties context, String uuid, String language, boolean withParameters) {
		MProcess process = new Query(context, I_AD_Process.Table_Name, I_AD_Process.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertProcess(context, process, language, withParameters);
	}
	
	/**
	 * Convert Browser from UUID
	 * @param uuid
	 * @param language
	 * @param withFields
	 * @return
	 */
	private Browser.Builder convertBrowser(Properties context, String uuid, String language, boolean withFields) {
		MBrowse browser = new Query(context, I_AD_Browse.Table_Name, I_AD_Process.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertBrowser(context, browser, language, withFields);
	}
	
	/**
	 * Convert Model tab to builder tab
	 * @param tab
	 * @return
	 */
	private Tab.Builder convertTab(Properties context, MTab tab, String language, boolean withFields) {
		return convertTab(context, tab, null, language, withFields);
	}
	
	/**
	 * Convert Model tab to builder tab
	 * @param tab
	 * @return
	 */
	private Tab.Builder convertTab(Properties context, MTab tab, List<MTab> tabs, String language, boolean withFields) {
		//	Translation
		String name = null;
		String description = null;
		String help = null;
		String commitWarning = null;
		String parentTabUuid = null;
		if(!Util.isEmpty(language)) {
			name = tab.get_Translation(I_AD_Tab.COLUMNNAME_Name, language);
			description = tab.get_Translation(I_AD_Tab.COLUMNNAME_Description, language);
			help = tab.get_Translation(I_AD_Tab.COLUMNNAME_Help, language);
			commitWarning = tab.get_Translation(I_AD_Tab.COLUMNNAME_CommitWarning, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = tab.getName();
		}
		if(Util.isEmpty(description)) {
			description = tab.getDescription();
		}
		if(Util.isEmpty(help)) {
			help = tab.getHelp();
		}
		if(Util.isEmpty(commitWarning)) {
			commitWarning = tab.getCommitWarning();
		}
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
					.filter(parentTab -> parentTab.getAD_Tab_ID() != tab.getAD_Tab_ID())
					.filter(parentTab -> parentTab.getTabLevel() == 0)
					.findFirst();
			String mainColumnName = null;
			MTable mainTable = null;
			if(optionalTab.isPresent()) {
				mainTable = MTable.get(context, optionalTab.get().getAD_Table_ID());
				mainColumnName = mainTable.getKeyColumns()[0];
			}
			List<MTab> tabList = tabs.stream()
					.filter(parentTab -> parentTab.getAD_Tab_ID() != tab.getAD_Tab_ID())
					.filter(parentTab -> parentTab.getAD_Tab_ID() != optionalTab.get().getAD_Tab_ID())
					.filter(parentTab -> parentTab.getSeqNo() < tab.getSeqNo())
					.filter(parentTab -> parentTab.getTabLevel() < tab.getTabLevel())
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
			whereClause.append(validateNull(tab.getWhereClause()));
		}
		//	create build
		Tab.Builder builder = Tab.newBuilder()
				.setId(tab.getAD_Tab_ID())
				.setUuid(validateNull(tab.getUUID()))
				.setName(validateNull(name))
				.setDescription(validateNull(description))
				.setHelp(validateNull(help))
				.setAccessLevel(Integer.parseInt(table.getAccessLevel()))
				.setCommitWarning(validateNull(commitWarning))
				.setSequence(tab.getSeqNo())
				.setDisplayLogic(validateNull(tab.getDisplayLogic()))
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
				.setTableName(validateNull(table.getTableName()))
				.setQuery(validateNull(getQueryWithReferencesFromTab(tab)))
				.setWhereClause(whereClause.toString())
				.setOrderByClause(validateNull(tab.getOrderByClause()))
				.setParentTabUuid(validateNull(parentTabUuid))
				.setIsChangeLog(table.isChangeLog())
				.setIsActive(tab.isActive());
		//	For link
		if(contextInfoId > 0) {
			ContextInfo.Builder contextInfoBuilder = convertContextInfo(context, contextInfoId, language);
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
				Process.Builder processBuilder = convertProcess(context, process, language, false);
				builder.addProcesses(processBuilder.build());
			}
		}
		if(withFields) {
			for(MField field : tab.getFields(false, null)) {
				Field.Builder fieldBuilder = convertField(context, field, language);
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
	 * @param language
	 * @return
	 */
	private ContextInfo.Builder convertContextInfo(Properties context, int contextInfoId, String language) {
		ContextInfo.Builder builder = null;
		if(contextInfoId > 0) {
			MADContextInfo contextInfoValue = MADContextInfo.getById(context, contextInfoId);
			MMessage message = MMessage.get(context, contextInfoValue.getAD_Message_ID());
			//	Get translation
			String msgText = null;
			String msgTip = null;
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
					.setUuid(validateNull(message.getUUID()))
					.setValue(validateNull(message.getValue()))
					.setMsgText(validateNull(msgText))
					.setMsgTip(validateNull(msgTip))
					.build();
			builder = ContextInfo.newBuilder()
					.setId(contextInfoValue.getAD_ContextInfo_ID())
					.setUuid(validateNull(contextInfoValue.getUUID()))
					.setName(validateNull(contextInfoValue.getName()))
					.setDescription(validateNull(contextInfoValue.getDescription()))
					.setMessageText(messageText)
					.setSqlStatement(validateNull(contextInfoValue.getSQLStatement()));
		}
		return builder;
	}
	
	/**
	 * Convert process to builder
	 * @param process
	 * @param language
	 * @return
	 */
	private Process.Builder convertProcess(Properties context, MProcess process, String language, boolean withParams) {
		String name = null;
		String description = null;
		String help = null;
		if(!Util.isEmpty(language)) {
			name = process.get_Translation(I_AD_Process.COLUMNNAME_Name, language);
			description = process.get_Translation(I_AD_Process.COLUMNNAME_Description, language);
			help = process.get_Translation(I_AD_Process.COLUMNNAME_Help, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = process.getName();
		}
		if(Util.isEmpty(description)) {
			description = process.getDescription();
		}
		if(Util.isEmpty(help)) {
			help = process.getHelp();
		}
		Process.Builder builder = Process.newBuilder()
				.setId(process.getAD_Process_ID())
				.setUuid(validateNull(process.getUUID()))
				.setValue(validateNull(process.getValue()))
				.setName(validateNull(name))
				.setDescription(validateNull(description))
				.setHelp(validateNull(help))
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
				reportExportType.setName(validateNull(reportType.getName()));
				reportExportType.setDescription(validateNull(reportType.getName()));
				reportExportType.setType(validateNull(reportType.getExtension()));
				builder.addReportExportTypes(reportExportType.build());
			}
		}
		//	For parameters
		if(withParams) {
			for(MProcessPara parameter : process.getParameters()) {
				Field.Builder fieldBuilder = convertProcessParameter(context, parameter, language);
				builder.addParameters(fieldBuilder.build());
			}
		}
		return builder;
	}
	
	/**
	 * Convert process to builder
	 * @param browser
	 * @param language
	 * @param withFields
	 * @return
	 */
	private Browser.Builder convertBrowser(Properties context, MBrowse browser, String language, boolean withFields) {
		String name = null;
		String description = null;
		String help = null;
		if(!Util.isEmpty(language)) {
			name = browser.get_Translation(I_AD_Browse.COLUMNNAME_Name, language);
			description = browser.get_Translation(I_AD_Browse.COLUMNNAME_Description, language);
			help = browser.get_Translation(I_AD_Browse.COLUMNNAME_Help, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = browser.getName();
		}
		if(Util.isEmpty(description)) {
			description = browser.getDescription();
		}
		if(Util.isEmpty(help)) {
			help = browser.getHelp();
		}
		String query = addQueryReferencesFromBrowser(browser, MView.getSQLFromView(browser.getAD_View_ID(), null));
		String orderByClause = getSQLOrderBy(browser);
		Browser.Builder builder = Browser.newBuilder()
				.setId(browser.getAD_Process_ID())
				.setUuid(validateNull(browser.getUUID()))
				.setValue(validateNull(browser.getValue()))
				.setName(name)
				.setDescription(validateNull(description))
				.setHelp(validateNull(help))
				.setAccessLevel(Integer.parseInt(browser.getAccessLevel()))
				.setIsActive(browser.isActive())
				.setIsCollapsibleByDefault(browser.isCollapsibleByDefault())
				.setIsDeleteable(browser.isDeleteable())
				.setIsExecutedQueryByDefault(browser.isExecutedQueryByDefault())
				.setIsSelectedByDefault(browser.isSelectedByDefault())
				.setIsShowTotal(browser.isShowTotal())
				.setIsUpdateable(browser.isUpdateable())
				.setQuery(validateNull(query))
				.setWhereClause(validateNull(browser.getWhereClause()))
				.setOrderByClause(validateNull(orderByClause));
		//	Set View UUID
		if(browser.getAD_View_ID() > 0) {
			builder.setViewUuid(validateNull(browser.getAD_View().getUUID()));
		}
		//	Window Reference
		if(browser.getAD_Window_ID() > 0) {
			MWindow window = new MWindow(context, browser.getAD_Window_ID(), null);
			Window.Builder windowBuilder = convertWindow(context, window, language, false);
			builder.setWindow(windowBuilder.build());
		}
		//	Process Reference
		if(browser.getAD_Process_ID() > 0) {
			Process.Builder processBuilder = convertProcess(context, MProcess.get(context, browser.getAD_Process_ID()), language, false);
			builder.setProcess(processBuilder.build());
		}
		//	For parameters
		if(withFields) {
			for(MBrowseField field : browser.getFields()) {
				Field.Builder fieldBuilder = convertBrowseField(context, field, language);
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
		Language language = Language.getLanguage(Env.getAD_Language(Env.getCtx()));
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
				ReferenceInfo referenceInfo = ReferenceUtil.getInstance(Env.getCtx()).getReferenceInfo(displayTypeId, referenceValueId, columnName, language.getAD_Language(), tableName);
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
	 * @param language
	 * @return
	 */
	private Field.Builder convertProcessParameter(Properties context, MProcessPara processParameter, String language) {
		String name = null;
		String description = null;
		String help = null;
		if(!Util.isEmpty(language)) {
			name = processParameter.get_Translation(I_AD_Process_Para.COLUMNNAME_Name, language);
			description = processParameter.get_Translation(I_AD_Process_Para.COLUMNNAME_Description, language);
			help = processParameter.get_Translation(I_AD_Process_Para.COLUMNNAME_Help, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = processParameter.getName();
		}
		if(Util.isEmpty(description)) {
			description = processParameter.getDescription();
		}
		if(Util.isEmpty(help)) {
			help = processParameter.getHelp();
		}
		//	Convert
		Field.Builder builder = Field.newBuilder()
				.setId(processParameter.getAD_Process_Para_ID())
				.setUuid(validateNull(processParameter.getUUID()))
				.setName(validateNull(name))
				.setDescription(validateNull(description))
				.setHelp(validateNull(help))
				.setColumnName(validateNull(processParameter.getColumnName()))
				.setElementName(validateNull(processParameter.getColumnName()))
				.setDefaultValue(validateNull(processParameter.getDefaultValue()))
				.setDefaultValueTo(validateNull(processParameter.getDefaultValue2()))
				.setDisplayLogic(validateNull(processParameter.getDisplayLogic()))
				.setDisplayType(processParameter.getAD_Reference_ID())
				.setIsDisplayed(true)
				.setIsInfoOnly(processParameter.isInfoOnly())
				.setIsMandatory(processParameter.isMandatory())
				.setIsRange(processParameter.isRange())
				.setReadOnlyLogic(validateNull(processParameter.getReadOnlyLogic()))
				.setSequence(processParameter.getSeqNo())
				.setValueMax(validateNull(processParameter.getValueMax()))
				.setValueMin(validateNull(processParameter.getValueMin()))
				.setVFormat(validateNull(processParameter.getVFormat()))
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
			MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, 0, displayTypeId, Language.getLanguage(language), columnName, referenceValueId, false, validationCode, false);
			Reference.Builder referenceBuilder = convertReference(context, info, language);
			builder.setReference(referenceBuilder.build());
		}
		return builder;
	}
	
	/**
	 * Convert Browse Field
	 * @param browseField
	 * @param language
	 * @return
	 */
	private Field.Builder convertBrowseField(Properties context, MBrowseField browseField, String language) {
		String name = null;
		String description = null;
		String help = null;
		if(!Util.isEmpty(language)) {
			name = browseField.get_Translation(I_AD_Browse_Field.COLUMNNAME_Name, language);
			description = browseField.get_Translation(I_AD_Browse_Field.COLUMNNAME_Description, language);
			help = browseField.get_Translation(I_AD_Browse_Field.COLUMNNAME_Help, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = browseField.getName();
		}
		if(Util.isEmpty(description)) {
			description = browseField.getDescription();
		}
		if(Util.isEmpty(help)) {
			help = browseField.getHelp();
		}
		//	Convert
		Field.Builder builder = Field.newBuilder()
				.setId(browseField.getAD_Browse_Field_ID())
				.setUuid(validateNull(browseField.getUUID()))
				.setName(validateNull(name))
				.setDescription(validateNull(description))
				.setHelp(validateNull(help))
				.setDefaultValue(validateNull(browseField.getDefaultValue()))
				.setDefaultValueTo(validateNull(browseField.getDefaultValue2()))
				.setDisplayLogic(validateNull(browseField.getDisplayLogic()))
				.setDisplayType(browseField.getAD_Reference_ID())
				.setIsDisplayed(browseField.isDisplayed())
				.setIsQueryCriteria(browseField.isQueryCriteria())
				.setIsOrderBy(browseField.isOrderBy())
				.setIsInfoOnly(browseField.isInfoOnly())
				.setIsMandatory(browseField.isMandatory())
				.setIsRange(browseField.isRange())
				.setIsReadOnly(browseField.isReadOnly())
				.setReadOnlyLogic(validateNull(browseField.getReadOnlyLogic()))
				.setIsKey(browseField.isKey())
				.setIsIdentifier(browseField.isIdentifier())
				.setSeqNoGrid(browseField.getSeqNoGrid())
				.setSequence(browseField.getSeqNo())
				.setValueMax(validateNull(browseField.getValueMax()))
				.setValueMin(validateNull(browseField.getValueMin()))
				.setVFormat(validateNull(browseField.getVFormat()))
				.setIsActive(browseField.isActive())
				.setCallout(validateNull(browseField.getCallout()))
				.setFieldLength(browseField.getFieldLength())
				.setDisplayType(browseField.getAD_Reference_ID());
		builder.setColumnName(validateNull(browseField.getAD_View_Column().getColumnName()));
		String elementName = null;
		if(browseField.getAD_View_Column().getAD_Column_ID() != 0) {
			MColumn column = MColumn.get(context, browseField.getAD_View_Column().getAD_Column_ID());
			elementName = column.getColumnName();
		}
		//	Default element
		if(Util.isEmpty(elementName)) {
			elementName = browseField.getAD_Element().getColumnName();
		}
		builder.setElementName(validateNull(elementName));
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
			
			MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, 0, displayTypeId, Language.getLanguage(language), columnName, referenceValueId, false, validationCode, false);
			if(info != null) {
				Reference.Builder referenceBuilder = convertReference(context, info, language);
				builder.setReference(referenceBuilder.build());
			} else {
				builder.setDisplayType(DisplayType.String);
			}
		}
		return builder;
	}
	
	/**
	 * Convert Field from UUID
	 * @param uuid
	 * @param language
	 * @return
	 */
	private Field.Builder convertField(Properties context, String uuid, String language) {
		MField field = new Query(context, I_AD_Field.Table_Name, I_AD_Field.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertField(context, field, language);
	}
	
	/**
	 * Convert field to builder
	 * @param field
	 * @param language
	 * @return
	 */
	private Field.Builder convertField(Properties context, MField field, String language) {
		String name = null;
		String description = null;
		String help = null;
		if(!Util.isEmpty(language)) {
			name = field.get_Translation(I_AD_Field.COLUMNNAME_Name, language);
			description = field.get_Translation(I_AD_Field.COLUMNNAME_Description, language);
			help = field.get_Translation(I_AD_Field.COLUMNNAME_Help, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = field.getName();
		}
		if(Util.isEmpty(description)) {
			description = field.getDescription();
		}
		if(Util.isEmpty(help)) {
			help = field.getHelp();
		}
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
				.setUuid(validateNull(field.getUUID()))
				.setName(validateNull(name))
				.setDescription(validateNull(description))
				.setHelp(validateNull(help))
				.setCallout(validateNull(column.getCallout()))
				.setColumnName(validateNull(column.getColumnName()))
				.setElementName(validateNull(column.getColumnName()))
				.setColumnSQL(validateNull(column.getColumnSQL()))
				.setDefaultValue(validateNull(defaultValue))
				.setDisplayLogic(validateNull(field.getDisplayLogic()))
				.setDisplayType(displayTypeId)
				.setFormatPattern(validateNull(column.getFormatPattern()))
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
				.setMandatoryLogic(validateNull(column.getMandatoryLogic()))
				.setReadOnlyLogic(validateNull(column.getReadOnlyLogic()))
				.setSequence(field.getSeqNo())
				.setValueMax(validateNull(column.getValueMax()))
				.setValueMin(validateNull(column.getValueMin()))
				.setFieldLength(column.getFieldLength())
				.setIsActive(field.isActive());
		//	Context Info
		if(field.getAD_ContextInfo_ID() > 0) {
			ContextInfo.Builder contextInfoBuilder = convertContextInfo(context, field.getAD_ContextInfo_ID(), language);
			builder.setContextInfo(contextInfoBuilder.build());
		}
		//	Process
		if(column.getAD_Process_ID() > 0) {
			MProcess process = MProcess.get(context, column.getAD_Process_ID());
			Process.Builder processBuilder = convertProcess(context, process, language, false);
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
			MLookupInfo info = MLookupFactory.getLookupInfo(context, 0, column.getAD_Column_ID(), displayTypeId, Language.getLanguage(language), column.getColumnName(), referenceValueId, false, validationCode, false);
			if(info != null) {
				Reference.Builder referenceBuilder = convertReference(context, info, language);
				builder.setReference(referenceBuilder.build());
			} else {
				builder.setDisplayType(DisplayType.String);
			}
		}
		
		//	Field Definition
		if(field.getAD_FieldDefinition_ID() > 0) {
			FieldDefinition.Builder fieldDefinitionBuilder = convertFieldDefinition(context, field.getAD_FieldDefinition_ID(), language);
			builder.setFieldDefinition(fieldDefinitionBuilder);
		}
		//	Field Group
		if(field.getAD_FieldGroup_ID() > 0) {
			FieldGroup.Builder fieldGroup = convertFieldGroup(context, field.getAD_FieldGroup_ID(), language);
			builder.setFieldGroup(fieldGroup.build());
		}
		return builder;
	}
	
	/**
	 * Convert Field Definition to builder
	 * @param fieldDefinitionId
	 * @param language
	 * @return
	 */
	private FieldDefinition.Builder convertFieldDefinition(Properties context, int fieldDefinitionId, String language) {
		FieldDefinition.Builder builder = null;
		if(fieldDefinitionId > 0) {
			MADFieldDefinition fieldDefinition  = new MADFieldDefinition(context, fieldDefinitionId, null);
			//	Reference
			builder = FieldDefinition.newBuilder()
					.setId(fieldDefinition.getAD_FieldDefinition_ID())
					.setUuid(validateNull(fieldDefinition.getUUID()))
					.setValue(validateNull(fieldDefinition.getValue()))
					.setName(validateNull(fieldDefinition.getName()));
			//	Get conditions
			for(MADFieldCondition condition : fieldDefinition.getConditions()) {
				if(!condition.isActive()) {
					continue;
				}
				FieldCondition.Builder fieldConditionBuilder = FieldCondition.newBuilder()
						.setId(fieldDefinition.getAD_FieldDefinition_ID())
						.setUuid(validateNull(condition.getUUID()))
						.setCondition(validateNull(condition.getCondition()))
						.setStylesheet(validateNull(condition.getStylesheet()))
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
	 * @param language
	 * @return
	 */
	private FieldGroup.Builder convertFieldGroup(Properties context, int fieldGroupId, String language) {
		FieldGroup.Builder builder = null;
		if(fieldGroupId > 0) {
			X_AD_FieldGroup fieldGroup  = new X_AD_FieldGroup(context, fieldGroupId, null);
			//	Get translation
			String name = null;
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
					.setUuid(validateNull(fieldGroup.getUUID()))
					.setName(validateNull(name))
					.setFieldGroupType(fieldGroup.getFieldGroupType())
					.setIsActive(fieldGroup.isActive());
		}
		return builder;
	}
	
	/**
	 * Convert Reference to builder
	 * @param info
	 * @param language
	 * @return
	 */
	private Reference.Builder convertReference(Properties context, MLookupInfo info, String language) {
		Reference.Builder builder = Reference.newBuilder()
				.setTableName(validateNull(info.TableName))
				.setKeyColumnName(validateNull(info.KeyColumn))
				.setDisplayColumnName(validateNull(info.DisplayColumn))
				.setDirectQuery(validateNull(info.QueryDirect))
				.setValidationCode(validateNull(info.ValidationCode));
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
		builder.setQuery(validateNull(queryForLookup));
		//	Window Reference
		if(info.ZoomWindow > 0) {
			builder.addWindows(convertZoomWindow(context, info.ZoomWindow, language).build());
		}
		if(info.ZoomWindowPO > 0) {
			builder.addWindows(convertZoomWindow(context, info.ZoomWindowPO, language).build());
		}
		//	Return
		return builder;
	}
	
	/**
	 * Convert Zoom Window from ID
	 * @param windowId
	 * @param language
	 * @return
	 */
	private ZoomWindow.Builder convertZoomWindow(Properties context, int windowId, String language) {
		MWindow window = new MWindow(context, windowId, null);
		//	Get translation
		String name = null;
		String description = null;
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
				.setUuid(validateNull(window.getUUID()))
				.setName(validateNull(name))
				.setDescription(validateNull(description))
				.setIsSOTrx(window.isSOTrx());
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

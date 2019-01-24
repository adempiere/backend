package org.spin.grpc.util;

import java.util.List;

import org.adempiere.model.MBrowse;
import org.compiere.model.I_AD_Field;
import org.compiere.model.I_AD_FieldGroup;
import org.compiere.model.I_AD_Menu;
import org.compiere.model.I_AD_Message;
import org.compiere.model.I_AD_Process;
import org.compiere.model.I_AD_Ref_List;
import org.compiere.model.I_AD_Tab;
import org.compiere.model.I_AD_Window;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MForm;
import org.compiere.model.MMenu;
import org.compiere.model.MMessage;
import org.compiere.model.MProcess;
import org.compiere.model.MRefList;
import org.compiere.model.MRefTable;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MTree;
import org.compiere.model.MTree_NodeMM;
import org.compiere.model.MWindow;
import org.compiere.model.Query;
import org.compiere.model.X_AD_FieldGroup;
import org.compiere.model.X_AD_Reference;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.grpc.util.DictionaryServiceGrpc.DictionaryServiceImplBase;
import org.spin.model.MADContextInfo;
import org.spin.model.MADFieldCondition;
import org.spin.model.MADFieldDefinition;

import io.grpc.stub.StreamObserver;

public class DictionaryServiceImplementation extends DictionaryServiceImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(DictionaryServiceImplementation.class);

	
	@Override
	public void requestWindow(ObjectRequest request, StreamObserver<Window> responseObserver) {
		requestWindow(request, responseObserver, false);
	}
	
	@Override
	public void requestWindowAndTabs(ObjectRequest request, StreamObserver<Window> responseObserver) {
		requestWindow(request, responseObserver, true);
	}
	
	@Override
	public void requestTab(ObjectRequest request, StreamObserver<Tab> responseObserver) {
		requestTab(request, responseObserver, false);
	}
	
	@Override
	public void requestTabAndFields(ObjectRequest request, StreamObserver<Tab> responseObserver) {
		requestTab(request, responseObserver, true);
	}
	
	@Override
	public void requestMenu(ObjectRequest request, StreamObserver<Menu> responseObserver) {
		requestMenu(request, responseObserver, false);
	}
	
	@Override
	public void requestMenuAndChild(ObjectRequest request, StreamObserver<Menu> responseObserver) {
		requestMenu(request, responseObserver, true);
	}
	
	@Override
	public void requestField(ObjectRequest request, StreamObserver<Field> responseObserver) {
		if(request == null
				|| Util.isEmpty(request.getUuid())) {
			log.fine("Object Request Null");
			return;
		}
		log.fine("Field Requested = " + request.getUuid());
		ClientRequest clientInfo = request.getClientRequest();
		String language = null;
		if(clientInfo != null) {
			language = clientInfo.getLanguage();
		}
		try {
			Field.Builder fieldBuilder = convertField(request.getUuid(), language);
			responseObserver.onNext(fieldBuilder.build());
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	    responseObserver.onCompleted();
	}
	
	/**
	 * Request Menu
	 * @param request
	 * @param responseObserver
	 * @param withChild
	 */
	public void requestMenu(ObjectRequest request, StreamObserver<Menu> responseObserver, boolean withChild) {
		if(request == null
				|| Util.isEmpty(request.getUuid())) {
			log.fine("Object Request Null");
			return;
		}
		log.fine("Field Requested = " + request.getUuid());
		ClientRequest clientInfo = request.getClientRequest();
		String language = null;
		if(clientInfo != null) {
			language = clientInfo.getLanguage();
		}
		Menu.Builder menuBuilder = convertMenu(request.getUuid(), language, withChild);
		try {
			responseObserver.onNext(menuBuilder.build());
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	    responseObserver.onCompleted();
	}
	
	
	/**
	 * Request with parameters
	 */
	public void requestWindow(ObjectRequest request, StreamObserver<Window> responseObserver, boolean withTabs) {
		if(request == null
				|| Util.isEmpty(request.getUuid())) {
			log.fine("Object Request Null");
			return;
		}
		log.fine("Window Requested = " + request.getUuid());
		ClientRequest clientInfo = request.getClientRequest();
		String language = null;
		if(clientInfo != null) {
			language = clientInfo.getLanguage();
		}
		try {
			Window.Builder windowBuilder = convertWindow(request.getUuid(), language, withTabs);
			responseObserver.onNext(windowBuilder.build());
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	    responseObserver.onCompleted();
	}
	
	/**
	 * Request with parameter
	 * @param request
	 * @param responseObserver
	 * @param withFields
	 */
	public void requestTab(ObjectRequest request, StreamObserver<Tab> responseObserver, boolean withFields) {
		if(request == null
				|| Util.isEmpty(request.getUuid())) {
			log.fine("Object Request Null");
			return;
		}
		log.fine("Tab Requested = " + request.getUuid());
		ClientRequest clientInfo = request.getClientRequest();
		String language = null;
		if(clientInfo != null) {
			language = clientInfo.getLanguage();
		}
		try {
			Tab.Builder tabBuilder = convertTab(request.getUuid(), language, withFields);
			responseObserver.onNext(tabBuilder.build());
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	    responseObserver.onCompleted();
	}
	
	/**
	 * Request Window: can be only window or child
	 * @param request
	 * @param responseObserver
	 * @param withTabs
	 */
	private Window.Builder convertWindow(String uuid, String language, boolean withTabs) {
		//	
		Window.Builder builder = null;
		MWindow window = new Query(Env.getCtx(), I_AD_Window.Table_Name, I_AD_Window.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Validate
		if(window != null) {
			//	
			ContextInfo.Builder contextInfoBuilder = convertContextInfo(window.getAD_ContextInfo_ID(), language);
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
					.setUuid(window.getUUID())
					.setName(name)
					.setDescription(validateNull(description))
					.setHelp(validateNull(help))
					.setIsSOTrx(window.isSOTrx());
			if(contextInfoBuilder != null) {
				builder.setContextInfo(contextInfoBuilder.build());
			}
			//	With Tabs
			if(withTabs) {
				for(MTab tab : window.getTabs(false, null)) {
					Tab.Builder tabBuilder = convertTab(tab, language, false);
					builder.addTabs(tabBuilder.build());
				}
			}
		}
		//	return
		return builder;
	}
	
	/**
	 * Convert Tabs from UUID
	 * @param uuid
	 * @param language
	 * @param withFields
	 * @return
	 */
	private Tab.Builder convertTab(String uuid, String language, boolean withFields) {
		MTab tab = new Query(Env.getCtx(), I_AD_Tab.Table_Name, I_AD_Tab.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertTab(tab, language, withFields);
	}
	
	/**
	 * Convert Model tab to builder tab
	 * @param tab
	 * @return
	 */
	private Tab.Builder convertTab(MTab tab, String language, boolean withFields) {
		//	Translation
		String name = null;
		String description = null;
		String help = null;
		String commitWarning = null;
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
		MTable table = MTable.get(Env.getCtx(), tab.getAD_Table_ID());
		boolean isReadOnly = tab.isReadOnly() || table.isView();
		int contextInfoId = tab.getAD_ContextInfo_ID();
		if(contextInfoId <= 0) {
			contextInfoId = table.getAD_ContextInfo_ID();
		}
		//	create build
		Tab.Builder builder = Tab.newBuilder()
				.setUuid(tab.getUUID())
				.setName(name)
				.setDescription(validateNull(description))
				.setHelp(validateNull(help))
				.setCommitWarning(validateNull(commitWarning))
				.setSequence(tab.getSeqNo())
				.setDisplayLogic(validateNull(tab.getDisplayLogic()))
				.setIsAdvancedTab(tab.isAdvancedTab())
				.setIsDeleteable(table.isDeleteable())
				.setIsDocument(table.isDocument())
				.setIsHasTree(tab.isHasTree())
				.setIsInfoTab(tab.isInfoTab())
				.setIsInsertRecord(isReadOnly && tab.isInsertRecord())
				.setIsReadOnly(isReadOnly)
				.setIsSingleRow(tab.isSingleRow())
				.setIsSortTab(tab.isSortTab())
				.setIsTranslationTab(tab.isTranslationTab())
				.setIsView(table.isView())
				.setTabLevel(tab.getTabLevel())
				.setTableName(validateNull(table.getTableName()))
				.setOrderByClause(validateNull(tab.getOrderByClause()));
		//	For link
		if(contextInfoId > 0) {
			ContextInfo.Builder contextInfoBuilder = convertContextInfo(contextInfoId, language);
			builder.setContextInfo(contextInfoBuilder.build());
		}
		//	Parent Link Column Name
		if(tab.getParent_Column_ID() > 0) {
			MColumn column = MColumn.get(Env.getCtx(), tab.getParent_Column_ID());
			builder.setParentColumnName(column.getColumnName());
		}
		//	Link Column Name
		if(tab.getAD_Column_ID() > 0) {
			MColumn column = MColumn.get(Env.getCtx(), tab.getAD_Column_ID());
			builder.setLinkColumnName(column.getColumnName());
		}
		//	Process
		if(tab.getAD_Process_ID() > 0) {
			Process.Builder processBuilder = convertProcess(MProcess.get(Env.getCtx(), tab.getAD_Process_ID()), language, false);
			if(processBuilder != null) {
				builder.setProcess(processBuilder.build());
			}
		}
		if(withFields) {
			for(MField field : tab.getFields(false, null)) {
				Field.Builder fieldBuilder = convertField(field, language);
				builder.addFields(fieldBuilder.build());
			}
		}
		//	
		return builder;
	}
	
	/**
	 * Convert Context Info to builder
	 * @param contextInfoId
	 * @param language
	 * @return
	 */
	private ContextInfo.Builder convertContextInfo(int contextInfoId, String language) {
		ContextInfo.Builder builder = null;
		if(contextInfoId > 0) {
			MADContextInfo contextInfoValue = MADContextInfo.getById(Env.getCtx(), contextInfoId);
			MMessage message = MMessage.get(Env.getCtx(), contextInfoValue.getAD_Message_ID());
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
					.setUuid(message.getUUID())
					.setValue(message.getValue())
					.setMsgText(validateNull(msgText))
					.setMsgTip(validateNull(msgTip))
					.build();
			builder = ContextInfo.newBuilder()
					.setUuid(contextInfoValue.getUUID())
					.setName(contextInfoValue.getName())
					.setDescription(validateNull(contextInfoValue.getDescription()))
					.setMessageText(messageText);
		}
		return builder;
	}
	
	/**
	 * Convert process to builder
	 * @param process
	 * @param language
	 * @return
	 */
	private Process.Builder convertProcess(MProcess process, String language, boolean withParams) {
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
				.setUuid(process.getUUID())
				.setName(name)
				.setDescription(validateNull(description))
				.setHelp(validateNull(help))
				.setIsDirectPrint(process.isDirectPrint())
				.setIsReport(process.isReport());
		
		return builder;
	}
	
	/**
	 * Convert Field from UUID
	 * @param uuid
	 * @param language
	 * @return
	 */
	private Field.Builder convertField(String uuid, String language) {
		MField field = new Query(Env.getCtx(), I_AD_Field.Table_Name, I_AD_Field.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertField(field, language);
	}
	
	/**
	 * Convert field to builder
	 * @param field
	 * @param language
	 * @return
	 */
	private Field.Builder convertField(MField field, String language) {
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
		MColumn column = MColumn.get(Env.getCtx(), field.getAD_Column_ID());
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
				.setUuid(field.getUUID())
				.setName(name)
				.setDescription(validateNull(description))
				.setHelp(validateNull(help))
				.setCallout(validateNull(column.getCallout()))
				.setColumnName(validateNull(column.getColumnName()))
				.setColumnSQL(validateNull(column.getColumnSQL()))
				.setDefaultValue(validateNull(defaultValue))
				.setDisplayLogic(validateNull(field.getDisplayLogic()))
				.setDisplayType(displayTypeId)
				.setFormatPattern(validateNull(column.getFormatPattern()))
				.setIdentifierSequence(field.getSeqNoGrid())
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
				.setSequence(field.getSeqNo());
		//	Context Info
		if(field.getAD_ContextInfo_ID() > 0) {
			ContextInfo.Builder contextInfoBuilder = convertContextInfo(field.getAD_ContextInfo_ID(), language);
			builder.setContextInfo(contextInfoBuilder.build());
		}
		//	Process
		if(column.getAD_Process_ID() > 0) {
			MProcess process = MProcess.get(Env.getCtx(), column.getAD_Process_ID());
			Process.Builder processBuilder = convertProcess(process, language, false);
			builder.setProcess(processBuilder.build());
		}
		//	Reference Value
		int referenceValueId = column.getAD_Reference_Value_ID();
		if(field.getAD_Reference_Value_ID() > 0) {
			referenceValueId = field.getAD_Reference_Value_ID();
		}
		//	Set reference
		if(referenceValueId > 0) {
			Reference.Builder reference = convertReference(referenceValueId, language);
			builder.setReference(reference.build());
		}
		//	Field Definition
		if(field.getAD_FieldDefinition_ID() > 0) {
			FieldDefinition.Builder fieldDefinitionBuilder = convertFieldDefinition(field.getAD_FieldDefinition_ID(), language);
			builder.setFieldDefinition(fieldDefinitionBuilder);
		}
		//	Field Group
		if(field.getAD_FieldGroup_ID() > 0) {
			FieldGroup.Builder fieldGroup = convertFieldGroup(field.getAD_FieldGroup_ID(), language);
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
	private FieldDefinition.Builder convertFieldDefinition(int fieldDefinitionId, String language) {
		FieldDefinition.Builder builder = null;
		if(fieldDefinitionId > 0) {
			MADFieldDefinition fieldDefinition  = new MADFieldDefinition(Env.getCtx(), fieldDefinitionId, null);
			//	Reference
			builder = FieldDefinition.newBuilder()
					.setUuid(fieldDefinition.getUUID())
					.setValue(fieldDefinition.getValue())
					.setName(fieldDefinition.getName());
			//	Get conditions
			for(MADFieldCondition condition : fieldDefinition.getConditions()) {
				FieldCondition.Builder fieldConditionBuilder = FieldCondition.newBuilder()
						.setUuid(condition.getUUID())
						.setCondition(validateNull(condition.getCondition()))
						.setStylesheet(validateNull(condition.getStylesheet()));
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
	private FieldGroup.Builder convertFieldGroup(int fieldGroupId, String language) {
		FieldGroup.Builder builder = null;
		if(fieldGroupId > 0) {
			X_AD_FieldGroup fieldGroup  = new X_AD_FieldGroup(Env.getCtx(), fieldGroupId, null);
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
					.setUuid(fieldGroup.getUUID())
					.setName(validateNull(name))
					.setFieldGroupType(fieldGroup.getFieldGroupType());
		}
		return builder;
	}
	
	/**
	 * Convert Reference Value to builder
	 * @param referenceId
	 * @param language
	 * @return
	 */
	private Reference.Builder convertReference(int referenceId, String language) {
		Reference.Builder builder = null;
		if(referenceId > 0) {
			X_AD_Reference reference = new X_AD_Reference(Env.getCtx(), referenceId, null);
			//	Reference
			builder = Reference.newBuilder()
					.setUuid(reference.getUUID())
					.setName(reference.getName())
					.setValidationType(reference.getValidationType());
			//	For Value
			if(reference.getValidationType().equals(X_AD_Reference.VALIDATIONTYPE_ListValidation)) {
				List<MRefList> referenceValueList = new Query(Env.getCtx(), I_AD_Ref_List.Table_Name, I_AD_Ref_List.COLUMNNAME_AD_Reference_ID + " = ?", null)
					.setParameters(reference.getAD_Reference_ID())
					.setOnlyActiveRecords(true)
					.<MRefList>list();
				for(MRefList referenceValue : referenceValueList) {
					ReferenceValue.Builder referenceValueBuilder = convertReferenceValue(referenceValue, language);
					builder.addValues(referenceValueBuilder.build());
				}
			} else if(reference.getValidationType().equals(X_AD_Reference.VALIDATIONTYPE_TableValidation)) {
				MRefTable referenceTable = MRefTable.getById(Env.getCtx(), referenceId);
				ReferenceTable.Builder referenceTableBuilder = convertReferenceTable(referenceTable, language);
				builder.setReferenceTable(referenceTableBuilder.build());
			}
			
		}
		return builder;
	}
	
	/**
	 * Convert reference Value to builder
	 * @param referenceValue
	 * @param language
	 * @return
	 */
	private ReferenceValue.Builder convertReferenceValue(MRefList referenceValue, String language) {
		String name = null;
		String description = null;
		if(!Util.isEmpty(language)) {
			name = referenceValue.get_Translation(I_AD_Ref_List.COLUMNNAME_Name, language);
			description = referenceValue.get_Translation(I_AD_Ref_List.COLUMNNAME_Description, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = referenceValue.getName();
		}
		if(Util.isEmpty(description)) {
			description = referenceValue.getDescription();
		}
		ReferenceValue.Builder builder = ReferenceValue.newBuilder()
				.setUuid(referenceValue.getUUID())
				.setValue(referenceValue.getValue())
				.setName(validateNull(name))
				.setDescription(validateNull(description));
		//	
		return builder;
	}
	
	/**
	 * Convert Reference Table to builder
	 * @param referenceTable
	 * @param language
	 * @return
	 */
	private ReferenceTable.Builder convertReferenceTable(MRefTable referenceTable, String language) {
		MTable table = MTable.get(Env.getCtx(), referenceTable.getAD_Table_ID());
		MColumn displayColumn = MColumn.get(Env.getCtx(), referenceTable.getAD_Display());
		MColumn keyColumn = MColumn.get(Env.getCtx(), referenceTable.getAD_Key());
		ReferenceTable.Builder builder = ReferenceTable.newBuilder()
				.setUuid(referenceTable.getUUID())
				.setIsDisplayIdentifier(referenceTable.isDisplayIdentifier())
				.setIsValueDisplayed(referenceTable.isValueDisplayed())
				.setDisplaySQL(validateNull(referenceTable.getDisplaySQL()))
				.setWhereClause(validateNull(referenceTable.getWhereClause()))
				.setTableName(validateNull(table.getTableName()))
				.setDisplayColumnName(validateNull(displayColumn.getColumnName()))
				.setKeyColumnName(validateNull(keyColumn.getColumnName()));
		//	
		return builder;
	}
	
	/**
	 * Convert Menu to Builder
	 * @param uuid
	 * @param language
	 * @param withChild
	 * @return
	 */
	private Menu.Builder convertMenu(String uuid, String language, boolean withChild) {
		MMenu menu = new Query(Env.getCtx(), I_AD_Menu.Table_Name, I_AD_Menu.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		//	Convert
		return convertMenu(menu, language, withChild);
	}
	
	/**
	 * Convert Menu to builder
	 * @param menu
	 * @param language
	 * @param withChild
	 * @return
	 */
	private Menu.Builder convertMenu(MMenu menu, String language, boolean withChild) {
		String name = null;
		String description = null;
		if(!Util.isEmpty(language)) {
			name = menu.get_Translation(I_AD_Menu.COLUMNNAME_Name, language);
			description = menu.get_Translation(I_AD_Menu.COLUMNNAME_Description, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = menu.getName();
		}
		if(Util.isEmpty(description)) {
			description = menu.getDescription();
		}
		Menu.Builder builder = Menu.newBuilder()
				.setUuid(menu.getUUID())
				.setName(validateNull(name))
				.setDescription(validateNull(description))
				.setAction(validateNull(menu.getAction()))
				.setIsSOTrx(menu.isSOTrx())
				.setIsSummary(menu.isSummary())
				.setIsReadOnly(menu.isReadOnly());
		//	Supported actions
		if(!Util.isEmpty(menu.getAction())) {
			if(menu.getAction().equals(MMenu.ACTION_Form)) {
				if(menu.getAD_Form_ID() > 0) {
					MForm form = new MForm(Env.getCtx(), menu.getAD_Form_ID(), null);
					builder.setFormUuid(form.getUUID());
				}
			} else if(menu.getAction().equals(MMenu.ACTION_Window)) {
				if(menu.getAD_Window_ID() > 0) {
					MWindow window = new MWindow(Env.getCtx(), menu.getAD_Window_ID(), null);
					builder.setWindowUuid(window.getUUID());
				}
			} else if(menu.getAction().equals(MMenu.ACTION_Process)) {
				if(menu.getAD_Process_ID() > 0) {
					MProcess process = MProcess.get(Env.getCtx(), menu.getAD_Process_ID());
					builder.setProcessUuid(process.getUUID());
				}
			} else if(menu.getAction().equals(MMenu.ACTION_SmartBrowse)) {
				if(menu.getAD_Browse_ID() > 0) {
					MBrowse smartBrowser = MBrowse.get(Env.getCtx(), menu.getAD_Browse_ID());
					builder.setSmartBrowserUuid(smartBrowser.getUUID());
				}
			}
		}
		//	Get Reference
		int treeId = MTree.getDefaultTreeIdFromTableId(menu.getAD_Client_ID(), I_AD_Menu.Table_ID);
		if(treeId != 0) {
			MTree tree = MTree.get(Env.getCtx(), treeId, null);
			MTree_NodeMM menuNode = new MTree_NodeMM(tree, menu.getAD_Menu_ID());
			//	
			if(menuNode.getParent_ID() > 0) {
				MMenu pareentMenu = MMenu.getFromId(Env.getCtx(), menuNode.getParent_ID());
				builder.setParentUuid(validateNull(pareentMenu.getUUID()));
			}
		}
		//	Load child
		if(withChild) {
			List<MMenu> childList = new Query(Env.getCtx(), I_AD_Menu.Table_Name, "EXISTS(SELECT 1 FROM AD_TreeNodeMM tnm "
					+ "WHERE tnm.Node_ID = AD_Menu.AD_Menu_ID "
					+ "AND tnm.Parent_ID = ?)", null)
				.setParameters(menu.getAD_Menu_ID())
				.setOnlyActiveRecords(true)
				.list();
			//	Convert Child
			for(MMenu child : childList) {
				Menu.Builder childBuilder = convertMenu(child, language, false);
				builder.addChilds(childBuilder.build());
			}
		}
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

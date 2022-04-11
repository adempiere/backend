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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import javax.script.ScriptEngine;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.I_AD_Browse;
import org.adempiere.model.I_AD_Browse_Field;
import org.adempiere.model.MBrowse;
import org.adempiere.model.MBrowseField;
import org.adempiere.model.MView;
import org.adempiere.model.MViewColumn;
import org.adempiere.model.MViewDefinition;
import org.adempiere.model.ZoomInfoFactory;
import org.compiere.model.Callout;
import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.GridTab;
import org.compiere.model.GridTabVO;
import org.compiere.model.GridWindow;
import org.compiere.model.GridWindowVO;
import org.compiere.model.I_AD_ChangeLog;
import org.compiere.model.I_AD_Client;
import org.compiere.model.I_AD_Column;
import org.compiere.model.I_AD_Element;
import org.compiere.model.I_AD_Field;
import org.compiere.model.I_AD_Org;
import org.compiere.model.I_AD_Preference;
import org.compiere.model.I_AD_PrintFormat;
import org.compiere.model.I_AD_Private_Access;
import org.compiere.model.I_AD_Process;
import org.compiere.model.I_AD_Process_Para;
import org.compiere.model.I_AD_Record_Access;
import org.compiere.model.I_AD_Reference;
import org.compiere.model.I_AD_ReportView;
import org.compiere.model.I_AD_Role;
import org.compiere.model.I_AD_Tab;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_AD_Window;
import org.compiere.model.I_CM_Chat;
import org.compiere.model.MAttachment;
import org.compiere.model.MChangeLog;
import org.compiere.model.MChat;
import org.compiere.model.MChatEntry;
import org.compiere.model.MClientInfo;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MMessage;
import org.compiere.model.MPreference;
import org.compiere.model.MPrivateAccess;
import org.compiere.model.MProcessPara;
import org.compiere.model.MQuery;
import org.compiere.model.MRecordAccess;
import org.compiere.model.MRefList;
import org.compiere.model.MReportView;
import org.compiere.model.MRole;
import org.compiere.model.MRule;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MWindow;
import org.compiere.model.M_Element;
import org.compiere.model.PO;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.MimeType;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.spin.base.util.ContextManager;
import org.spin.base.util.ConvertUtil;
import org.spin.base.util.DictionaryUtil;
import org.spin.base.util.RecordUtil;
import org.spin.base.util.ReferenceUtil;
import org.spin.base.util.ValueUtil;
import org.spin.grpc.util.Attachment;
import org.spin.grpc.util.ChatEntry;
import org.spin.grpc.util.Condition.Operator;
import org.spin.grpc.util.ContextInfoValue;
import org.spin.grpc.util.CreateChatEntryRequest;
import org.spin.grpc.util.Criteria;
import org.spin.grpc.util.DefaultValue;
import org.spin.grpc.util.DeletePreferenceRequest;
import org.spin.grpc.util.DrillTable;
import org.spin.grpc.util.Empty;
import org.spin.grpc.util.Entity;
import org.spin.grpc.util.GetAttachmentRequest;
import org.spin.grpc.util.GetContextInfoValueRequest;
import org.spin.grpc.util.GetDefaultValueRequest;
import org.spin.grpc.util.GetLookupItemRequest;
import org.spin.grpc.util.GetPrivateAccessRequest;
import org.spin.grpc.util.GetRecordAccessRequest;
import org.spin.grpc.util.GetReportOutputRequest;
import org.spin.grpc.util.GetResourceReferenceRequest;
import org.spin.grpc.util.GetResourceRequest;
import org.spin.grpc.util.KeyValue;
import org.spin.grpc.util.ListBrowserItemsRequest;
import org.spin.grpc.util.ListBrowserItemsResponse;
import org.spin.grpc.util.ListDrillTablesRequest;
import org.spin.grpc.util.ListDrillTablesResponse;
import org.spin.grpc.util.ListLookupItemsRequest;
import org.spin.grpc.util.ListLookupItemsResponse;
import org.spin.grpc.util.ListPrintFormatsRequest;
import org.spin.grpc.util.ListPrintFormatsResponse;
import org.spin.grpc.util.ListReferencesRequest;
import org.spin.grpc.util.ListReferencesResponse;
import org.spin.grpc.util.ListReportViewsRequest;
import org.spin.grpc.util.ListReportViewsResponse;
import org.spin.grpc.util.ListTabEntitiesRequest;
import org.spin.grpc.util.ListTabEntitiesResponse;
import org.spin.grpc.util.ListTranslationsRequest;
import org.spin.grpc.util.ListTranslationsResponse;
import org.spin.grpc.util.LockPrivateAccessRequest;
import org.spin.grpc.util.LookupItem;
import org.spin.grpc.util.Preference;
import org.spin.grpc.util.PrintFormat;
import org.spin.grpc.util.PrivateAccess;
import org.spin.grpc.util.RecordAccess;
import org.spin.grpc.util.RecordAccessRole;
import org.spin.grpc.util.RecordReferenceInfo;
import org.spin.grpc.util.ReportOutput;
import org.spin.grpc.util.ReportView;
import org.spin.grpc.util.Resource;
import org.spin.grpc.util.ResourceReference;
import org.spin.grpc.util.RollbackEntityRequest;
import org.spin.grpc.util.RunCalloutRequest;
import org.spin.grpc.util.SetPreferenceRequest;
import org.spin.grpc.util.SetRecordAccessRequest;
import org.spin.grpc.util.Translation;
import org.spin.grpc.util.UnlockPrivateAccessRequest;
import org.spin.grpc.util.UserInterfaceGrpc.UserInterfaceImplBase;
import org.spin.grpc.util.Value;
import org.spin.model.I_AD_AttachmentReference;
import org.spin.model.I_AD_ContextInfo;
import org.spin.model.MADAttachmentReference;
import org.spin.model.MADContextInfo;
import org.spin.util.ASPUtil;
import org.spin.util.AbstractExportFormat;
import org.spin.util.AttachmentUtil;
import org.spin.util.ReportExportHandler;

import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * https://itnext.io/customizing-grpc-generated-code-5909a2551ca1
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Business data service
 */
public class UserInterfaceServiceImplementation extends UserInterfaceImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(UserInterfaceServiceImplementation.class);
	/**	Key column constant	*/
	private final String KEY_COLUMN_KEY = "KeyColumn";
	/**	Key column constant	*/
	private final String DISPLAY_COLUMN_KEY = "DisplayColumn";
	/**	Key column constant	*/
	private final String VALUE_COLUMN_KEY = "ValueColumn";
	/**	Browse Requested	*/
	private static CCache<String, MBrowse> browserRequested = new CCache<String, MBrowse>(I_AD_Browse.Table_Name + "_UUID", 30, 0);	//	no time-out
	/**	window Requested	*/
	private static CCache<String, MTab> tabRequested = new CCache<String, MTab>(I_AD_Tab.Table_Name + "_UUID", 30, 0);	//	no time-out
	/**	Reference cache	*/
	private static CCache<String, String> referenceWhereClauseCache = new CCache<String, String>("Reference_WhereClause", 30, 0);	//	no time-out
	/**	Window emulation	*/
	private AtomicInteger windowNoEmulation = new AtomicInteger(1);
	
	@Override
	public void rollbackEntity(RollbackEntityRequest request, StreamObserver<Entity> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Rollback Requested = " + request.getId());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Entity.Builder entityValue = rollbackLastEntityAction(request);
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
	public void runCallout(RunCalloutRequest request, StreamObserver<org.spin.grpc.util.Callout> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getCallout())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Callout Requested = " + request.getCallout());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			org.spin.grpc.util.Callout.Builder calloutResponse = runcallout(request);
			responseObserver.onNext(calloutResponse.build());
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
	public void getLookupItem(GetLookupItemRequest request, StreamObserver<LookupItem> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Lookup Requested = " + request.getUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			LookupItem.Builder lookupValue = convertLookupItem(request);
			responseObserver.onNext(lookupValue.build());
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
	public void listLookupItems(ListLookupItemsRequest request, StreamObserver<ListLookupItemsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Lookup Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListLookupItemsResponse.Builder entityValueList = convertLookupItemsList(request);
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
	public void listBrowserItems(ListBrowserItemsRequest request, StreamObserver<ListBrowserItemsResponse> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Browser Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListBrowserItemsResponse.Builder entityValueList = convertBrowserList(request);
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
	public void listReferences(ListReferencesRequest request, StreamObserver<ListReferencesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Process Activity Requested is Null");
			}
			log.fine("References Info Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListReferencesResponse.Builder entityValueList = convertRecordReferences(context, request);
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
	public void getDefaultValue(GetDefaultValueRequest request, StreamObserver<DefaultValue> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			DefaultValue.Builder defaultValue = getInfoFromDefaultValueRequest(request);
			responseObserver.onNext(defaultValue.build());
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
	/**
	 * TODO: Replace LockPrivateAccessRequest with GetPrivateAccessRequest
	 * @param request
	 * @param responseObserver
	 */
	public void lockPrivateAccess(LockPrivateAccessRequest request, StreamObserver<PrivateAccess> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}

			int recordId = request.getId();
			if (recordId <= 0
					&& Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("@Record_ID@ / @UUID@ @NotFound@");
			}
			if(recordId <= 0) {
				recordId = RecordUtil.getIdFromUuid(request.getTableName(), request.getUuid(), null);
			}

			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());

			MUser user = MUser.get(context);
			PrivateAccess.Builder privateaccess = lockUnlockPrivateAccess(context, request.getTableName(), recordId, user.getAD_User_ID(), true, null);
			responseObserver.onNext(privateaccess.build());
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
	/**
	 * TODO: Replace UnlockPrivateAccessRequest with GetPrivateAccessRequest
	 * @param request
	 * @param responseObserver
	 */
	public void unlockPrivateAccess(UnlockPrivateAccessRequest request, StreamObserver<PrivateAccess> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}

			int recordId = request.getId();
			if (recordId <= 0
					&& Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("@Record_ID@ / @UUID@ @NotFound@");
			}
			if (recordId <= 0) {
				recordId = RecordUtil.getIdFromUuid(request.getTableName(), request.getUuid(), null);
			}

			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());

			MUser user = MUser.get(context);
			PrivateAccess.Builder privateaccess = lockUnlockPrivateAccess(context, request.getTableName(), recordId, user.getAD_User_ID(), false, null);
			responseObserver.onNext(privateaccess.build());
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
	public void getPrivateAccess(GetPrivateAccessRequest request, StreamObserver<PrivateAccess> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}

			int recordId = request.getId();
			if (recordId <= 0 && Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("@Record_ID@ / @UUID@ @NotFound@");
			}
			if (recordId <= 0) {
				recordId = RecordUtil.getIdFromUuid(request.getTableName(), request.getUuid(), null);
			}

			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			MUser user = MUser.get(context);
			MPrivateAccess privateAccess = getPrivateAccess(context, request.getTableName(), recordId, user.getAD_User_ID(), null);
			if(privateAccess == null
					|| privateAccess.getAD_Table_ID() == 0) {
				MTable table = MTable.get(context, request.getTableName());
				//	Set values
				privateAccess = new MPrivateAccess(context, user.getAD_User_ID(), table.getAD_Table_ID(), recordId);
				privateAccess.setIsActive(false);
			}
			PrivateAccess.Builder privateaccess = convertPrivateAccess(context, privateAccess);
			responseObserver.onNext(privateaccess.build());
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
	public void getContextInfoValue(GetContextInfoValueRequest request, StreamObserver<ContextInfoValue> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ContextInfoValue.Builder contextInfoValue = convertContextInfoValue(context, request);
			responseObserver.onNext(contextInfoValue.build());
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
	public void listTranslations(ListTranslationsRequest request, StreamObserver<ListTranslationsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListTranslationsResponse.Builder translationsList = convertTranslationsList(context, request);
			responseObserver.onNext(translationsList.build());
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
	public void listPrintFormats(ListPrintFormatsRequest request, StreamObserver<ListPrintFormatsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListPrintFormatsResponse.Builder printFormatsList = convertPrintFormatsList(context, request);
			responseObserver.onNext(printFormatsList.build());
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
	public void listReportViews(ListReportViewsRequest request, StreamObserver<ListReportViewsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListReportViewsResponse.Builder reportViewsList = convertReportViewsList(context, request);
			responseObserver.onNext(reportViewsList.build());
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
	public void listDrillTables(ListDrillTablesRequest request, StreamObserver<ListDrillTablesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListDrillTablesResponse.Builder drillTablesList = convertDrillTablesList(request);
			responseObserver.onNext(drillTablesList.build());
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
	public void getReportOutput(GetReportOutputRequest request, StreamObserver<ReportOutput> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ReportOutput.Builder reportOutput = getReportOutput(request);
			responseObserver.onNext(reportOutput.build());
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
	public void createChatEntry(CreateChatEntryRequest request, StreamObserver<ChatEntry> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ChatEntry.Builder chatEntryValue = addChatEntry(context, request);
			responseObserver.onNext(chatEntryValue.build());
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
	public void getResource(GetResourceRequest request, StreamObserver<Resource> responseObserver) {
		try {
			if(request == null
					|| (Util.isEmpty(request.getResourceUuid()) 
							&& Util.isEmpty(request.getResourceName()))) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Download Requested = " + request.getResourceUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			//	Get resource
			getResource(request.getResourceUuid(), request.getResourceName(), responseObserver);
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
	 * Get File from fileName
	 * @param resourceUuid
	 * @param responseObserver
	 * @throws Exception 
	 */
	private void getResource(String resourceUuid, String resourceName, StreamObserver<Resource> responseObserver) throws Exception {
		if(!AttachmentUtil.getInstance().isValidForClient(Env.getAD_Client_ID(Env.getCtx()))) {
			responseObserver.onError(new AdempiereException("@NotFound@"));
			return;
		}
		//	Validate by name
		if(!Util.isEmpty(resourceName)) {
			MClientInfo clientInfo = MClientInfo.get(Env.getCtx());
			MADAttachmentReference reference = new Query(Env.getCtx(), I_AD_AttachmentReference.Table_Name, "(UUID || '-' || FileName) = ? AND FileHandler_ID = ?", null)
					.setOrderBy(I_AD_AttachmentReference.COLUMNNAME_AD_Attachment_ID + " DESC")
					.setParameters(resourceName, clientInfo.getFileHandler_ID())
					.first();
			if(reference == null
					|| reference.getAD_AttachmentReference_ID() <= 0) {
				responseObserver.onError(new AdempiereException("@NotFound@"));
				return;
			}
			resourceUuid = reference.getUUID();
		} else if(Util.isEmpty(resourceUuid)) {
			responseObserver.onError(new AdempiereException("@NotFound@"));
			return;
		}
		byte[] data = AttachmentUtil.getInstance()
			.withClientId(Env.getAD_Client_ID(Env.getCtx()))
			.withAttachmentReferenceId(RecordUtil.getIdFromUuid(I_AD_AttachmentReference.Table_Name, resourceUuid, null))
			.getAttachment();
		if(data == null) {
			responseObserver.onError(new AdempiereException("@NotFound@"));
			return;
		}
		//	For all
		int bufferSize = 256 * 1024;// 256k
        byte[] buffer = new byte[bufferSize];
        int length;
        InputStream is = new ByteArrayInputStream(data);
        while ((length = is.read(buffer, 0, bufferSize)) != -1) {
          responseObserver.onNext(
        		  Resource.newBuilder().setData(ByteString.copyFrom(buffer, 0, length)).build()
          );
        }
        //	Completed
        responseObserver.onCompleted();
	}
	
	@Override
	public void getResourceReference(GetResourceReferenceRequest request, StreamObserver<ResourceReference> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ResourceReference.Builder resourceReference = getResourceReferenceFromImageId(request.getImageId());
			responseObserver.onNext(resourceReference.build());
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
	public void getAttachment(GetAttachmentRequest request, StreamObserver<Attachment> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Attachment.Builder attachment = getAttachmentFromEntity(request);
			responseObserver.onNext(attachment.build());
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
	public void setPreference(SetPreferenceRequest request, StreamObserver<Preference> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			//	Create Preference
			MPreference preference = getPreference(request.getTypeValue(), request.getColumnName(), request.getIsForCurrentClient(), request.getIsForCurrentOrganization(), request.getIsForCurrentUser(), request.getIsForCurrentContainer(), request.getContainerUuid());
			if(preference == null
					|| preference.getAD_Preference_ID() <= 0) {
				preference = new MPreference(Env.getCtx(), 0, null);
			}
			//	Save preference
			Preference.Builder preferenceBuilder = savePreference(preference, request.getTypeValue(), request.getColumnName(), request.getIsForCurrentClient(), request.getIsForCurrentOrganization(), request.getIsForCurrentUser(), request.getIsForCurrentContainer(), request.getContainerUuid(), request.getValue());
			responseObserver.onNext(preferenceBuilder.build());
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
	public void deletePreference(DeletePreferenceRequest request, StreamObserver<Empty> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			//	Delete Preference
			Empty.Builder empty = Empty.newBuilder();
			MPreference preference = getPreference(request.getTypeValue(), request.getColumnName(), request.getIsForCurrentClient(), request.getIsForCurrentOrganization(), request.getIsForCurrentUser(), request.getIsForCurrentContainer(), request.getContainerUuid());
			if(preference != null
					&& preference.getAD_Preference_ID() > 0) {
				preference.deleteEx(true);
			}
			responseObserver.onNext(empty.build());
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
	public void getRecordAccess(GetRecordAccessRequest request, StreamObserver<RecordAccess> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			
			RecordAccess.Builder recordAccess = convertRecordAccess(request);
			responseObserver.onNext(recordAccess.build());
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
	public void setRecordAccess(SetRecordAccessRequest request, StreamObserver<RecordAccess> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			
			RecordAccess.Builder recordAccess = saveRecordAccess(request);
			responseObserver.onNext(recordAccess.build());
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
	public void listTabEntities(ListTabEntitiesRequest request, StreamObserver<ListTabEntitiesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListTabEntitiesResponse.Builder entityValueList = convertEntitiesList(context, request);
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
	 * Convert Object to list
	 * @param request
	 * @return
	 */
	private ListTabEntitiesResponse.Builder convertEntitiesList(Properties context, ListTabEntitiesRequest request) {
		int tabId = RecordUtil.getIdFromUuid(I_AD_Tab.Table_Name, request.getTabUuid(), null);
		if(tabId <= 0) {
			throw new AdempiereException("@AD_Tab_ID@ @NotFound@");
		}
		//	
		MTab tab = MTab.get(context, tabId);
		String tableName = MTable.getTableName(context, tab.getAD_Table_ID());
		int windowNo = ThreadLocalRandom.current().nextInt(1, 8996 + 1);
		Env.clearWinContext(windowNo);
		Map<String, Object> attributes = ValueUtil.convertValuesToObjects(request.getContextAttributesList());
		//	Fill context
		attributes.entrySet().forEach(attribute -> {
			if(attribute.getValue() instanceof Integer) {
				Env.setContext(context, windowNo, attribute.getKey(), (Integer) attribute.getValue());
			} else if(attribute.getValue() instanceof Timestamp) {
				Env.setContext(context, windowNo, attribute.getKey(), (Timestamp) attribute.getValue());
			} else if(attribute.getValue() instanceof Boolean) {
				Env.setContext(context, windowNo, attribute.getKey(), (Boolean) attribute.getValue());
			} else if(attribute.getValue() instanceof String) {
				Env.setContext(context, windowNo, attribute.getKey(), (String) attribute.getValue());
			}
		});
		String where = Env.parseContext(context, windowNo, tab.getWhereClause(), false);
		if(Util.isEmpty(where)
				&& !Util.isEmpty(tab.getWhereClause())) {
			throw new AdempiereException("@AD_Tab_ID@ @WhereClause@ @Unparseable@");
		}
		Criteria criteria = request.getFilters();
		StringBuffer whereClause = new StringBuffer(where);
		List<Object> params = new ArrayList<>();
		//	For dynamic condition
		String dynamicWhere = ValueUtil.getWhereClauseFromCriteria(criteria, tableName, params);
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
		int limit = RecordUtil.getPageSize(request.getPageSize());
		int offset = pageNumber * RecordUtil.getPageSize(request.getPageSize());
		int count = 0;
		ListTabEntitiesResponse.Builder builder = ListTabEntitiesResponse.newBuilder();
		//	
		StringBuilder sql = new StringBuilder(DictionaryUtil.getQueryWithReferencesFromTab(tab));
		if (whereClause.length() > 0) {
			sql.append(" WHERE ").append(whereClause); // includes first AND
		}
		//	
		String parsedSQL = RecordUtil.addSearchValueAndGet(sql.toString(), tableName, request.getSearchValue(), params);
		parsedSQL = MRole.getDefault().addAccessSQL(parsedSQL,
				null, MRole.SQL_FULLYQUALIFIED,
				MRole.SQL_RO);
		String orderByClause = criteria.getOrderByClause();
		if(Util.isEmpty(orderByClause)) {
			orderByClause = "";
		} else {
			orderByClause = " ORDER BY " + orderByClause;
		}
		//	Count records
		count = RecordUtil.countRecords(parsedSQL, tableName, params);
		//	Add Row Number
		parsedSQL = RecordUtil.getQueryWithLimit(parsedSQL, limit, offset);
		//	Add Order By
		parsedSQL = parsedSQL + orderByClause;
		builder = convertListEntitiesResult(MTable.get(context, tableName), parsedSQL, params);
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(RecordUtil.isValidNextPageToken(count, offset, limit)) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
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
	private ListTabEntitiesResponse.Builder convertListEntitiesResult(MTable table, String sql, List<Object> params) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ListTabEntitiesResponse.Builder builder = ListTabEntitiesResponse.newBuilder();
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
	 * Convert Record Access
	 * @param request
	 * @return
	 */
	private RecordAccess.Builder convertRecordAccess(GetRecordAccessRequest request) {
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		if(Util.isEmpty(request.getUuid())
				&& request.getId() <= 0) {
			throw new AdempiereException("@Record_ID@ @NotFound@");
		}
		//	
		int tableId = MTable.getTable_ID(request.getTableName());
		int recordId = request.getId();
		String uuid = request.getUuid();
		if(recordId <= 0) {
			recordId = RecordUtil.getIdFromUuid(request.getTableName(), request.getUuid(), null);
		}
		if(Util.isEmpty(uuid)) {
			uuid = RecordUtil.getUuidFromId(request.getTableName(), recordId);
		}
		RecordAccess.Builder builder = RecordAccess.newBuilder().setTableName(ValueUtil.validateNull(request.getTableName()))
				.setUuid(ValueUtil.validateNull(uuid))
				.setId(recordId);
		//	Populate access List
		getRecordAccess(tableId, recordId, null).forEach(recordAccess -> {
			MRole role = MRole.get(Env.getCtx(), recordAccess.getAD_Role_ID());
			builder.addCurrentRoles(RecordAccessRole.newBuilder()
				.setRoleId(role.getAD_Role_ID())
				.setRoleUuid(ValueUtil.validateNull(role.getUUID()))
				.setRoleName(ValueUtil.validateNull(role.getName()))
				.setIsActive(recordAccess.isActive())
				.setIsDependentEntities(recordAccess.isDependentEntities())
				.setIsExclude(recordAccess.isExclude())
				.setIsReadOnly(recordAccess.isReadOnly()));
		});
		//	Populate roles list
		getRolesList(null).forEach(role -> {
			builder.addAvailableRoles(RecordAccessRole.newBuilder()
					.setRoleId(role.getAD_Role_ID())
					.setRoleUuid(ValueUtil.validateNull(role.getUUID()))
					.setRoleName(ValueUtil.validateNull(role.getName())));
		});
		return builder;
	}
	
	/**
	 * Get record access from client, role , table id and record id
	 * @param tableId
	 * @param recordId
	 * @param transactionName
	 * @return
	 */
	private List<MRecordAccess> getRecordAccess(int tableId, int recordId, String transactionName) {
		return new Query(Env.getCtx(), I_AD_Record_Access.Table_Name,"AD_Table_ID = ? "
				+ "AND Record_ID = ? "
				+ "AND AD_Client_ID = ?", transactionName)
			.setParameters(tableId, recordId, Env.getAD_Client_ID(Env.getCtx()))
			.list();
	}
	
	/**
	 * Get role for this client
	 * @param transactionName
	 * @return
	 */
	private List<MRole> getRolesList(String transactionName) {
		return new Query(Env.getCtx(), I_AD_Role.Table_Name, null, transactionName)
			.setClient_ID()
			.setOnlyActiveRecords(true)
			.list();
	}
	
	/**
	 * save record Access
	 * @param request
	 * @return
	 */
	private RecordAccess.Builder saveRecordAccess(SetRecordAccessRequest request) {
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		if(Util.isEmpty(request.getUuid())
				&& request.getId() <= 0) {
			throw new AdempiereException("@Record_ID@ @NotFound@");
		}
		//	
		RecordAccess.Builder builder = RecordAccess.newBuilder();
		Trx.run(transactionName -> {
			int tableId = MTable.getTable_ID(request.getTableName());
			AtomicInteger recordId = new AtomicInteger(request.getId());
			String uuid = request.getUuid();
			if(recordId.get() <= 0) {
				recordId.set(RecordUtil.getIdFromUuid(request.getTableName(), request.getUuid(), transactionName));
			}
			if(Util.isEmpty(uuid)) {
				uuid = RecordUtil.getUuidFromId(request.getTableName(), recordId.get(), transactionName);
			}
			builder.setTableName(ValueUtil.validateNull(request.getTableName()))
				.setUuid(ValueUtil.validateNull(uuid))
				.setId(recordId.get());
			//	Delete old
			DB.executeUpdateEx("DELETE FROM AD_Record_Access "
					+ "WHERE AD_Table_ID = ? "
					+ "AND Record_ID = ? "
					+ "AND AD_Client_ID = ?", new Object[]{tableId, recordId.get(), Env.getAD_Client_ID(Env.getCtx())}, transactionName);
			//	Add new record access
			request.getRecordAccessesList().forEach(recordAccessToSet -> {
				int roleId = recordAccessToSet.getRoleId();
				if(roleId <= 0) {
					roleId = RecordUtil.getIdFromUuid(I_AD_Role.Table_Name, recordAccessToSet.getRoleUuid(), transactionName);
				}
				if(roleId <= 0) {
					throw new AdempiereException("@AD_Role_ID@ @NotFound@");
				}
				MRole role = MRole.get(Env.getCtx(), roleId);
				MRecordAccess recordAccess = new MRecordAccess(Env.getCtx(), role.getAD_Role_ID(), tableId, recordId.get(), transactionName);
				recordAccess.setIsActive(recordAccessToSet.getIsActive());
				recordAccess.setIsExclude(recordAccessToSet.getIsExclude());
				recordAccess.setIsDependentEntities(recordAccessToSet.getIsDependentEntities());
				recordAccess.setIsReadOnly(recordAccessToSet.getIsReadOnly());
				recordAccess.saveEx();
				//	Add current roles
				builder.addCurrentRoles(RecordAccessRole.newBuilder()
						.setRoleId(role.getAD_Role_ID())
						.setRoleUuid(ValueUtil.validateNull(role.getUUID()))
						.setRoleName(ValueUtil.validateNull(role.getName()))
						.setIsActive(recordAccess.isActive())
						.setIsDependentEntities(recordAccess.isDependentEntities())
						.setIsExclude(recordAccess.isExclude())
						.setIsReadOnly(recordAccess.isReadOnly()));
			});
			//	Populate roles list
			getRolesList(transactionName).forEach(roleToGet -> {
				builder.addAvailableRoles(RecordAccessRole.newBuilder()
						.setRoleId(roleToGet.getAD_Role_ID())
						.setRoleUuid(ValueUtil.validateNull(roleToGet.getUUID())));
			});
		});
		//	
		return builder;
	}
	
	/**
	 * Save preference from values
	 * @param preference
	 * @param preferenceType
	 * @param attribute
	 * @param isCurrentClient
	 * @param isCurrentOrganization
	 * @param isCurrentUser
	 * @param isCurrentContainer
	 * @param uuid
	 * @param value
	 * @return
	 */
	private Preference.Builder savePreference(MPreference preference, int preferenceType, String attribute, boolean isCurrentClient, boolean isCurrentOrganization, boolean isCurrentUser, boolean isCurrentContainer, String uuid, String value) {
		Preference.Builder builder = Preference.newBuilder();
		if(preferenceType == SetPreferenceRequest.Type.WINDOW_VALUE) {
			int windowId = RecordUtil.getIdFromUuid(I_AD_Window.Table_Name, uuid, null);
			int clientId = Env.getAD_Client_ID(Env.getCtx());
			int orgId = Env.getAD_Org_ID(Env.getCtx());
			int userId = Env.getAD_User_ID(Env.getCtx());
			//	For client
			if(!isCurrentClient) {
				clientId = 0;
			}
			//	For Organization
			if(!isCurrentOrganization) {
				orgId = 0;
			}
			//For User
			if(!isCurrentUser) {
				userId = -1;
			}
			//	For Window
			if(!isCurrentContainer) {
				windowId = -1;
			}
			//	Set values
			preference.set_ValueOfColumn(I_AD_Client.COLUMNNAME_AD_Client_ID, clientId);
			preference.setAD_Org_ID(orgId);
			preference.setAD_User_ID(userId);
			preference.setAD_Window_ID(windowId);
			preference.setAttribute(attribute);
			preference.setValue(value);
			//	
			preference.saveEx();
			builder.setClientUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_AD_Client.Table_Name, preference.getAD_Client_ID())))
				.setOrganizationUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_AD_Org.Table_Name, preference.getAD_Org_ID())))
				.setUserUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_AD_User.Table_Name, preference.getAD_User_ID())))
				.setContainerUuid(ValueUtil.validateNull(uuid))
				.setColumnName(ValueUtil.validateNull(preference.getAttribute()))
				.setValue(preference.getValue());
		}
		//	
		return builder;
	}
	
	/**
	 * Get preference
	 * @param preferenceType
	 * @param attribute
	 * @param isCurrentClient
	 * @param isCurrentOrganization
	 * @param isCurrentUser
	 * @param isCurrentContainer
	 * @param uuid
	 * @return
	 */
	private MPreference getPreference(int preferenceType, String attribute, boolean isCurrentClient, boolean isCurrentOrganization, boolean isCurrentUser, boolean isCurrentContainer, String uuid) {
		if(preferenceType == SetPreferenceRequest.Type.WINDOW_VALUE) {
			int windowId = RecordUtil.getIdFromUuid(I_AD_Window.Table_Name, uuid, null);
			StringBuffer whereClause = new StringBuffer("Attribute = ?");
			List<Object> parameters = new ArrayList<>();
			parameters.add(attribute);
			//	For client
			whereClause.append(" AND AD_Client_ID = ?");
			if(isCurrentClient) {
				parameters.add(Env.getAD_Client_ID(Env.getCtx()));
			} else {
				parameters.add(0);
			}
			//	For Organization
			whereClause.append(" AND AD_Org_ID = ?");
			if(isCurrentOrganization) {
				parameters.add(Env.getAD_Org_ID(Env.getCtx()));
			} else {
				parameters.add(0);
			}
			//For User
			if(isCurrentUser) {
				parameters.add(Env.getAD_User_ID(Env.getCtx()));
				whereClause.append(" AND AD_User_ID = ?");
			} else {
				whereClause.append(" AND AD_User_ID IS NULL");
			}
			//	For Window
			if(isCurrentContainer) {
				parameters.add(windowId);
				whereClause.append(" AND AD_Window_ID = ?");
			} else {
				whereClause.append(" AND AD_Window_ID IS NULL");
			}
			return new Query(Env.getCtx(), I_AD_Preference.Table_Name, whereClause.toString(), null)
					.setParameters(parameters)
					.first();
		}
		//	
		return null;
	}
	
	/**
	 * Get Attachment related to entity
	 * @param request
	 * @return
	 */
	private Attachment.Builder getAttachmentFromEntity(GetAttachmentRequest request) {
		int tableId = 0;
		if(!Util.isEmpty(request.getTableName())) {
			MTable table = MTable.get(Env.getCtx(), request.getTableName());
			if(table != null
					&& table.getAD_Table_ID() != 0) {
				tableId = table.getAD_Table_ID();
			}
		}
		int recordId = request.getId();
		if(recordId <= 0) {
			recordId = RecordUtil.getIdFromUuid(request.getTableName(), request.getUuid(), null);
		}
		if(tableId != 0
				&& recordId !=  0) {
			return ConvertUtil.convertAttachment(MAttachment.get(Env.getCtx(), tableId, recordId));
		}
		return Attachment.newBuilder();
	}
	
	/**
	 * Get resource from Image Id
	 * @param imageId
	 * @return
	 */
	private ResourceReference.Builder getResourceReferenceFromImageId(int imageId) {
		return ConvertUtil.convertResourceReference(RecordUtil.getResourceFromImageId(imageId));
	}
	
	/**
	 * Convert Object to list
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private ReportOutput.Builder getReportOutput(GetReportOutputRequest request) throws FileNotFoundException, IOException {
		Criteria criteria = request.getCriteria();
		if(Util.isEmpty(criteria.getTableName())) {
			throw new AdempiereException("@TableName@ @NotFound@");
		}
		//	Validate print format
		if(Util.isEmpty(request.getPrintFormatUuid())) {
			throw new AdempiereException("@AD_PrintFormat_ID@ @NotFound@");
		}
		MTable table = MTable.get(Env.getCtx(), criteria.getTableName());
		//	
		if(!MRole.getDefault().isCanReport(table.getAD_Table_ID())) {
			throw new AdempiereException("@AccessCannotReport@");
		}
		//	
		ReportOutput.Builder builder = ReportOutput.newBuilder();
		MQuery query = getReportQueryFromCriteria(criteria);
		if(!Util.isEmpty(criteria.getWhereClause())) {
			query.addRestriction(criteria.getWhereClause());
		}
		//	
		PrintInfo printInformation = new PrintInfo(request.getReportName(), table.getAD_Table_ID(), 0, 0);
		//	Get Print Format
		MPrintFormat printFormat = null;
		MReportView reportView = null;
		if(!Util.isEmpty(request.getPrintFormatUuid())) {
			printFormat = new Query(Env.getCtx(), I_AD_PrintFormat.Table_Name, I_AD_PrintFormat.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getPrintFormatUuid())
					.first();
		}
		//	Get Report View
		if(!Util.isEmpty(request.getReportViewUuid())) {
			reportView = new Query(Env.getCtx(), I_AD_ReportView.Table_Name, I_AD_ReportView.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getReportViewUuid())
					.first();
		}
		//	Get Default
		if(printFormat == null) {
			int reportViewId = 0;
			if(reportView != null) {
				reportViewId = reportView.getAD_ReportView_ID();
			}
			printFormat = MPrintFormat.get(Env.getCtx(), reportViewId, table.getAD_Table_ID());
		}
		//	Validate print format
		if(printFormat == null) {
			throw new AdempiereException("@AD_PrintGormat_ID@ @NotFound@");
		}
		if(table.getAD_Table_ID() != printFormat.getAD_Table_ID()) {
			table = MTable.get(Env.getCtx(), printFormat.getAD_Table_ID());
		}
		//	Run report engine
		ReportEngine reportEngine = new ReportEngine(Env.getCtx(), printFormat, query, printInformation);
		//	Set report view
		if(reportView != null) {
			reportEngine.setAD_ReportView_ID(reportView.getAD_ReportView_ID());
		} else {
			reportView = MReportView.get(Env.getCtx(), reportEngine.getAD_ReportView_ID());
		}
		//	Set Summary
		reportEngine.setSummary(request.getIsSummary());
		//	
		File reportFile = createOutput(reportEngine, request.getReportType());
		if(reportFile != null
				&& reportFile.exists()) {
			String validFileName = getValidName(reportFile.getName());
			builder.setFileName(ValueUtil.validateNull(validFileName));
			builder.setName(ValueUtil.validateNull(reportEngine.getName()));
			builder.setMimeType(ValueUtil.validateNull(MimeType.getMimeType(validFileName)));
			String headerName = Msg.getMsg(Env.getCtx(), "Report") + ": " + reportEngine.getName() + "  " + Env.getHeader(Env.getCtx(), 0);
			builder.setHeaderName(ValueUtil.validateNull(headerName));
			StringBuffer footerName = new StringBuffer ();
			footerName.append(Msg.getMsg(Env.getCtx(), "DataCols")).append("=")
				.append(reportEngine.getColumnCount())
				.append(", ").append(Msg.getMsg(Env.getCtx(), "DataRows")).append("=")
				.append(reportEngine.getRowCount());
			builder.setFooterName(ValueUtil.validateNull(footerName.toString()));
			//	Type
			builder.setReportType(request.getReportType());
			ByteString resultFile = ByteString.readFrom(new FileInputStream(reportFile));
			if(request.getReportType().endsWith("html")
					|| request.getReportType().endsWith("txt")) {
				builder.setOutputBytes(resultFile);
			}
			if(reportView != null) {
				builder.setReportViewUuid(ValueUtil.validateNull(reportView.getUUID()));
			}
			builder.setPrintFormatUuid(ValueUtil.validateNull(printFormat.getUUID()));
			builder.setTableName(ValueUtil.validateNull(table.getTableName()));
			builder.setOutputStream(resultFile);
		}
		//	Return
		return builder;
	}
	
	/**
	 * Create output
	 * @param reportEngine
	 * @param reportType
	 */
	private File createOutput(ReportEngine reportEngine, String reportType) {
		//	Export
		File file = null;
		try {
			ReportExportHandler exportHandler = new ReportExportHandler(Env.getCtx(), reportEngine);
			AbstractExportFormat exporter = exportHandler.getExporterFromExtension(reportType);
			if(exporter != null) {
				//	Get File
				file = File.createTempFile(reportEngine.getName() + "_" + System.currentTimeMillis(), "." + exporter.getExtension());
				exporter.exportTo(file);
			}	
		} catch (IOException e) {
			return null;
		}
		return file;
	}
	
	/**
	 * Get private access from table, record id and user id
	 * @param context
	 * @param tableName
	 * @param recordId
	 * @param userUuid
	 * @param transactionName
	 * @return
	 */
	private MPrivateAccess getPrivateAccess(Properties context, String tableName, int recordId, int userId, String transactionName) {
		return new Query(context, I_AD_Private_Access.Table_Name, "EXISTS(SELECT 1 FROM AD_Table t WHERE t.AD_Table_ID = AD_Private_Access.AD_Table_ID AND t.TableName = ?) "
				+ "AND Record_ID = ? "
				+ "AND AD_User_ID = ?", transactionName)
			.setParameters(tableName, recordId, userId)
			.first();
	}
	
	/**
	 * Lock and unlock private access
	 * @param context
	 * @param request
	 * @param lock
	 * @param transactionName
	 * @return
	 */
	private PrivateAccess.Builder lockUnlockPrivateAccess(Properties context, String tableName, int recordId, int userId, boolean lock, String transactionName) {
		MPrivateAccess privateAccess = getPrivateAccess(context, tableName, recordId, userId, transactionName);
		//	Create new
		if(privateAccess == null
				|| privateAccess.getAD_Table_ID() == 0) {
			MTable table = MTable.get(context, tableName);
			//	Set values
			privateAccess = new MPrivateAccess(context, userId, table.getAD_Table_ID(), recordId);
		}
		//	Set active
		privateAccess.setIsActive(lock);
		privateAccess.saveEx(transactionName);
		//	Convert Private Access
		return convertPrivateAccess(context, privateAccess);
	}
	
	/**
	 * Convert references to gRPC
	 * @param context
	 * @param request
	 * @return
	 */
	private ListReferencesResponse.Builder convertRecordReferences(Properties context, ListReferencesRequest request) {
		ListReferencesResponse.Builder builder = ListReferencesResponse.newBuilder();
		//	Get entity
		if(request.getId() == 0
				&& Util.isEmpty(request.getUuid())) {
			throw new AdempiereException("@Record_ID@ @NotFound@");
		}
		
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		String tableName = request.getTableName();
		StringBuffer whereClause = new StringBuffer();
		List<Object> params = new ArrayList<>();
		if(!Util.isEmpty(request.getUuid())) {
			whereClause.append(I_AD_Element.COLUMNNAME_UUID + " = ?");
			params.add(request.getUuid());
		} else if(request.getId() > 0) {
			whereClause.append(tableName + "_ID = ?");
			params.add(request.getId());
		} else {
			throw new AdempiereException("@Record_ID@ @NotFound@");
		}
		PO entity = new Query(context, tableName, whereClause.toString(), null)
				.setParameters(params)
				.first();
		if(entity != null
				&& entity.get_ID() >= 0) {
			MWindow window = new Query(context, I_AD_Window.Table_Name, I_AD_Window.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getWindowUuid())
					.setOnlyActiveRecords(true)
					.first();
			if(window != null
					&& window.get_ID() > 0) {
				for (ZoomInfoFactory.ZoomInfo zoomInfo : ZoomInfoFactory.retrieveZoomInfos(entity, window.getAD_Window_ID())) {
					if (zoomInfo.query.getRecordCount() == 0) {
						continue;
					}
					MWindow referenceWindow = MWindow.get(context, zoomInfo.windowId);
					//	
					String uuid = UUID.randomUUID().toString();
					RecordReferenceInfo.Builder recordReferenceBuilder = RecordReferenceInfo.newBuilder();
					recordReferenceBuilder.setDisplayName(zoomInfo.destinationDisplay + " (#" + zoomInfo.query.getRecordCount() + ")");
					recordReferenceBuilder.setRecordCount(zoomInfo.query.getRecordCount());
					recordReferenceBuilder.setWindowUuid(ValueUtil.validateNull(referenceWindow.get_UUID()));
					recordReferenceBuilder.setTableName(ValueUtil.validateNull(zoomInfo.query.getZoomTableName()));
					recordReferenceBuilder.setWhereClause(ValueUtil.validateNull(zoomInfo.query.getWhereClause()));
					recordReferenceBuilder.setUuid(uuid);
					referenceWhereClauseCache.put(uuid, zoomInfo.query.getWhereClause());
					//	Add to list
					builder.addReferences(recordReferenceBuilder.build());
				}
			}
		}
		//	Return
		return builder;
	}
	
	
	/**
	 * Convert languages to gRPC
	 * @param context
	 * @param request
	 * @return
	 */
	private ListTranslationsResponse.Builder convertTranslationsList(Properties context, ListTranslationsRequest request) {
		ListTranslationsResponse.Builder builder = ListTranslationsResponse.newBuilder();
		String tableName = request.getTableName();
		if(Util.isEmpty(tableName)) {
			throw new AdempiereException("@TableName@ @NotFound@");
		}
		Trx.run(transactionName -> {
			MTable table = MTable.get(context, tableName);
			PO entity = RecordUtil.getEntity(context, tableName, request.getUuid(), request.getId(), transactionName);
			List<Object> parameters = new ArrayList<>();
			StringBuffer whereClause = new StringBuffer(entity.get_KeyColumns()[0] + " = ?");
			parameters.add(entity.get_ID());
			if(!Util.isEmpty(request.getLanguage())) {
				whereClause.append(" AND AD_Language = ?");
				parameters.add(request.getLanguage());
			}
			new Query(context, tableName + "_Trl", whereClause.toString(), transactionName)
				.setParameters(parameters)
				.<PO>list()
				.forEach(translation -> {
					Translation.Builder translationBuilder = Translation.newBuilder();
					table.getColumnsAsList().stream().filter(column -> column.isTranslated()).forEach(column -> {
						Object value = translation.get_Value(column.getColumnName());
						if(value != null) {
							Value.Builder builderValue = ValueUtil.getValueFromObject(value);
							if(builderValue != null) {
								translationBuilder.putValues(column.getColumnName(), builderValue.build());
							}
							//	Set uuid
							if(Util.isEmpty(translationBuilder.getUuid())) {
								translationBuilder.setUuid(ValueUtil.validateNull(translation.get_UUID()));
							}
							//	Set Language
							if(Util.isEmpty(translationBuilder.getLanguage())) {
								translationBuilder.setLanguage(ValueUtil.validateNull(translation.get_ValueAsString("AD_Language")));
							}
						}
					});
					builder.addTranslations(translationBuilder);
				});
		});
		//	Return
		return builder;
	}
	
	/**
	 * Convert Report View to gRPC
	 * @param context
	 * @param request
	 * @return
	 */
	private ListReportViewsResponse.Builder convertReportViewsList(Properties context, ListReportViewsRequest request) {
		ListReportViewsResponse.Builder builder = ListReportViewsResponse.newBuilder();
		//	Get entity
		if(Util.isEmpty(request.getTableName())
				&& Util.isEmpty(request.getProcessUuid())) {
			throw new AdempiereException("@TableName@ / @AD_Process_ID@ @NotFound@");
		}
		String whereClause = null;
		List<Object> parameters = new ArrayList<>();
		//	For Table Name
		if(!Util.isEmpty(request.getTableName())) {
			MTable table = MTable.get(context, request.getTableName());
			if(table == null) {
				throw new AdempiereException("@TableName@ @NotFound@");
			}
			whereClause = "AD_Table_ID = ?";
			parameters.add(table.getAD_Table_ID());
		} else if(!Util.isEmpty(request.getProcessUuid())) {
			whereClause = "EXISTS(SELECT 1 FROM AD_Process p WHERE p.UUID = ? AND p.AD_ReportView_ID = AD_ReportView.AD_ReportView_ID)";
			parameters.add(request.getProcessUuid());
		}
		//	Get List
		new Query(context, I_AD_ReportView.Table_Name, whereClause, null)
			.setParameters(parameters)
			.setOrderBy(I_AD_ReportView.COLUMNNAME_PrintName + ", " + I_AD_ReportView.COLUMNNAME_Name)
			.<MReportView>list().forEach(reportViewReference -> {
				ReportView.Builder reportViewBuilder = ReportView.newBuilder();
				String name = reportViewReference.getName();
				String description = reportViewReference.getDescription();
				if(!Env.isBaseLanguage(context, "")) {
					String translation = reportViewReference.get_Translation("Name");
					if(!Util.isEmpty(translation)) {
						name = translation;
					}
					translation = reportViewReference.get_Translation("Description");
					if(!Util.isEmpty(translation)) {
						description = translation;
					}
				}
				reportViewBuilder.setUuid(ValueUtil.validateNull(reportViewReference.getUUID()));
				reportViewBuilder.setName(ValueUtil.validateNull(name));
				reportViewBuilder.setDescription(ValueUtil.validateNull(description));
				MTable table = MTable.get(context, reportViewReference.getAD_Table_ID());
				reportViewBuilder.setTableName(ValueUtil.validateNull(table.getTableName()));
				//	add
				builder.addReportViews(reportViewBuilder);
			});
		//	Return
		return builder;
	}
	
	/**
	 * Convert Report View to gRPC
	 * @param request
	 * @return
	 */
	private ListDrillTablesResponse.Builder convertDrillTablesList(ListDrillTablesRequest request) {
		ListDrillTablesResponse.Builder builder = ListDrillTablesResponse.newBuilder();
		//	Get entity
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@TableName@ @NotFound@");
		}
		MTable table = MTable.get(Env.getCtx(), request.getTableName());
		String sql = "SELECT t.TableName, e.ColumnName, NULLIF(e.PO_PrintName,e.PrintName) "
				+ "FROM AD_Column c "
				+ " INNER JOIN AD_Column used ON (c.ColumnName=used.ColumnName)"
				+ " INNER JOIN AD_Table t ON (used.AD_Table_ID=t.AD_Table_ID AND t.IsView='N' AND t.AD_Table_ID <> c.AD_Table_ID)"
				+ " INNER JOIN AD_Column cKey ON (t.AD_Table_ID=cKey.AD_Table_ID AND cKey.IsKey='Y')"
				+ " INNER JOIN AD_Element e ON (cKey.ColumnName=e.ColumnName) "
				+ "WHERE c.AD_Table_ID=? AND c.IsKey='Y' "
				+ "ORDER BY 3";
			PreparedStatement pstmt = null;
			ResultSet resultSet = null;
			try {
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, table.getAD_Table_ID());
				resultSet = pstmt.executeQuery();
				while (resultSet.next()) {
					String drillTableName = resultSet.getString("TableName");
					String columnName = resultSet.getString("ColumnName");
					M_Element element = M_Element.get(Env.getCtx(), columnName);
					//	Add here
					DrillTable.Builder drillTable = DrillTable.newBuilder();
					drillTable.setTableName(ValueUtil.validateNull(drillTableName));
					String name = element.getPrintName();
					String poName = element.getPO_PrintName();
					if(!Env.isBaseLanguage(Env.getCtx(), "")) {
						String translation = element.get_Translation("PrintName");
						if(!Util.isEmpty(translation)) {
							name = translation;
						}
						translation = element.get_Translation("PO_PrintName");
						if(!Util.isEmpty(translation)) {
							poName = translation;
						}
					}
					if(!Util.isEmpty(poName)) {
						name = name + "/" + poName;
					}
					//	Print Name
					drillTable.setPrintName(ValueUtil.validateNull(name));
					//	Add to list
					builder.addDrillTables(drillTable);
				}
				resultSet.close();
				pstmt.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, sql, e);
			} finally {
				DB.close(resultSet, pstmt);
			}
		//	Return
		return builder;
	}
	
	/**
	 * Convert print formats to gRPC
	 * @param context
	 * @param request
	 * @return
	 */
	private ListPrintFormatsResponse.Builder convertPrintFormatsList(Properties context, ListPrintFormatsRequest request) {
		ListPrintFormatsResponse.Builder builder = ListPrintFormatsResponse.newBuilder();
		//	Get entity
		if(Util.isEmpty(request.getTableName())
				&& Util.isEmpty(request.getProcessUuid())
				&& Util.isEmpty(request.getReportViewUuid())) {
			throw new AdempiereException("@TableName@ / @AD_Process_ID@ / @AD_ReportView_ID@ @NotFound@");
		}
		String whereClause = null;
		List<Object> parameters = new ArrayList<>();
		//	For Table Name
		if(!Util.isEmpty(request.getTableName())) {
			MTable table = MTable.get(context, request.getTableName());
			whereClause = "AD_Table_ID = ?";
			parameters.add(table.getAD_Table_ID());
		} else if(!Util.isEmpty(request.getProcessUuid())) {
			whereClause = "EXISTS(SELECT 1 FROM AD_Process p WHERE p.UUID = ? AND (p.AD_PrintFormat_ID = AD_PrintFormat.AD_PrintFormat_ID OR p.AD_ReportView_ID = AD_PrintFormat.AD_ReportView_ID))";
			parameters.add(request.getProcessUuid());
		} else if(!Util.isEmpty(request.getReportViewUuid())) {
			MReportView reportView = new Query(context, I_AD_ReportView.Table_Name, I_AD_ReportView.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getReportViewUuid())
				.first();
			whereClause = "AD_ReportView_ID = ?";
			parameters.add(reportView.getUUID());
		}
		//	Get List
		new Query(context, I_AD_PrintFormat.Table_Name, whereClause, null)
			.setParameters(parameters)
			.setOrderBy(I_AD_PrintFormat.COLUMNNAME_Name)
			.setClient_ID()
			.<MPrintFormat>list().forEach(printFormatReference -> {
				PrintFormat.Builder printFormatBuilder = PrintFormat.newBuilder();
				printFormatBuilder.setUuid(ValueUtil.validateNull(printFormatReference.getUUID()));
				printFormatBuilder.setName(ValueUtil.validateNull(printFormatReference.getName()));
				printFormatBuilder.setDescription(ValueUtil.validateNull(printFormatReference.getDescription()));
				printFormatBuilder.setIsDefault(printFormatReference.isDefault());
				MTable table = MTable.get(context, printFormatReference.getAD_Table_ID());
				printFormatBuilder.setTableName(ValueUtil.validateNull(table.getTableName()));
				if(printFormatReference.getAD_ReportView_ID() != 0) {
					MReportView reportView = MReportView.get(context, printFormatReference.getAD_ReportView_ID());
					printFormatBuilder.setReportViewUuid(ValueUtil.validateNull(reportView.getUUID()));
				}
				//	add
				builder.addPrintFormats(printFormatBuilder);
			});
		//	Return
		return builder;
	}
	
	/**
	 * Convert Name
	 * @param name
	 * @return
	 */
	private String getValidName(String name) {
		if(Util.isEmpty(name)) {
			return "";
		}
		return name.replaceAll("[+^:&áàäéèëíìïóòöúùñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ$()*#/><]", "").replaceAll(" ", "-");
	}
	
	/**
	 * Convert operator from gRPC to SQL
	 * @param gRpcOperator
	 * @return
	 */
	private String convertOperator(int gRpcOperator) {
		String operator = MQuery.EQUAL;
		switch (gRpcOperator) {
			case Operator.BETWEEN_VALUE:
				operator = MQuery.BETWEEN;
				break;
			case Operator.EQUAL_VALUE:
				operator = MQuery.EQUAL;
				break;
			case Operator.GREATER_EQUAL_VALUE:
				operator = MQuery.GREATER_EQUAL;
				break;
			case Operator.GREATER_VALUE:
				operator = MQuery.GREATER;
				break;
			case Operator.IN_VALUE:
				operator = " IN ";
				break;
			case Operator.LESS_EQUAL_VALUE:
				operator = MQuery.LESS_EQUAL;
				break;
			case Operator.LESS_VALUE:
				operator = MQuery.LESS;
				break;
			case Operator.LIKE_VALUE:
				operator = MQuery.LIKE;
				break;
			case Operator.NOT_EQUAL_VALUE:
				operator = MQuery.NOT_EQUAL;
				break;
			case Operator.NOT_IN_VALUE:
				operator = " NOT IN ";
				break;
			case Operator.NOT_LIKE_VALUE:
				operator = MQuery.NOT_LIKE;
				break;
			case Operator.NOT_NULL_VALUE:
				operator = MQuery.NOT_NULL;
				break;
			case Operator.NULL_VALUE:
				operator = MQuery.NULL;
				break;
			default:
				break;
			}
		return operator;
	}
	
	/**
	 * Rollback entity
	 * @param request
	 * @return
	 */
	private Entity.Builder rollbackLastEntityAction(RollbackEntityRequest request) {
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		MTable table = MTable.get(Env.getCtx(), request.getTableName());
		if(table == null
				|| table.getAD_Table_ID() == 0) {
			throw new AdempiereException("@AD_Table_ID@ @Invalid@");
		}
		AtomicReference<PO> entityWrapper = new AtomicReference<PO>();
		Trx.run(transactionName -> {
			int id = request.getId();
			if(id <= 0) {
				id = RecordUtil.getIdFromUuid(request.getTableName(), request.getUuid(), transactionName);
			}
			//	get Table from table name
			int logId = request.getLogId();
			if(logId <= 0) {
				logId = getLastChangeLogId(table.getAD_Table_ID(), id, transactionName);
			}
			if(logId > 0) {
				List<MChangeLog> changeLogList = new Query(Env.getCtx(), I_AD_ChangeLog.Table_Name, I_AD_ChangeLog.COLUMNNAME_AD_ChangeLog_ID + " = ?", transactionName)
						.setParameters(logId)
						.<MChangeLog>list();
				String eventType = MChangeLog.EVENTCHANGELOG_Update;
				if(changeLogList.size() > 0) {
					MChangeLog log = changeLogList.get(0);
					eventType = log.getEventChangeLog();
					if(eventType.equals(MChangeLog.EVENTCHANGELOG_Insert)) {
						MChangeLog changeLog = new MChangeLog(Env.getCtx(), logId, transactionName);
						PO entity = RecordUtil.getEntity(Env.getCtx(), table.getTableName(), request.getUuid(), changeLog.getRecord_ID(), transactionName);
						if(entity != null
								&& entity.get_ID() >= 0) {
							entity.delete(true);
						}
					} else if(eventType.equals(MChangeLog.EVENTCHANGELOG_Delete)
							|| eventType.equals(MChangeLog.EVENTCHANGELOG_Update)) {
						PO entity = table.getPO(id, transactionName);
						if(entity == null
								|| entity.get_ID() <= 0) {
							throw new AdempiereException("@Error@ @PO@ @NotFound@");
						}
						changeLogList.forEach(changeLog -> {
							setValueFromChangeLog(entity, changeLog);
						});
						entity.saveEx(transactionName);
						entityWrapper.set(entity);
					}
				}
			} else {
				throw new AdempiereException("@AD_ChangeLog_ID@ @NotFound@");
			}
		});
		//	Return
		if(entityWrapper.get() != null) {
			return ConvertUtil.convertEntity(entityWrapper.get());
		}
		//	Instead
		return Entity.newBuilder();
	}

	/**
	 * set value for PO from change log
	 * @param entity
	 * @param changeLog
	 */
	private void setValueFromChangeLog(PO entity, MChangeLog changeLog) {
		Object value = null;
		try {
			if(!changeLog.isOldNull()) {
				MColumn column = MColumn.get(Env.getCtx(), changeLog.getAD_Column_ID());
				value = stringToObject(column, changeLog.getOldValue());
			}
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
		}
		//	Set value
		entity.set_ValueOfColumn(changeLog.getAD_Column_ID(), value);
	}
	
	/**
	 * Convert string representation to appropriate object type
	 * for column
	 * @param column
	 * @param value
	 * @return
	 */
	private Object stringToObject(MColumn column, String value) {
		if ( value == null )
			return null;
		
		if ( DisplayType.isText(column.getAD_Reference_ID()) 
				|| column.getAD_Reference_ID() == DisplayType.List  
				|| column.getColumnName().equals("EntityType") 
				|| column.getColumnName().equals("AD_Language")) {
			return value;
		}
		else if ( DisplayType.isNumeric(column.getAD_Reference_ID()) ){
			return new BigDecimal(value);
		}
		else if (DisplayType.isID(column.getAD_Reference_ID()) ) {
			return Integer.valueOf(value);
		}	
		else if (DisplayType.YesNo == column.getAD_Reference_ID() ) {
			return "true".equalsIgnoreCase(value);
		}
		else if (DisplayType.Button == column.getAD_Reference_ID() && column.getAD_Reference_Value_ID() == 0) {
			return "true".equalsIgnoreCase(value) ? "Y" : "N";
		}
		else if (DisplayType.Button == column.getAD_Reference_ID() && column.getAD_Reference_Value_ID() != 0) {
			return value;
		}
		else if (DisplayType.isDate(column.getAD_Reference_ID())) {
			return Timestamp.valueOf(value);
		}
	//Binary,  Radio, RowID, Image not supported
		else 
			return null;
	}
	
	/**
	 * Create Chat Entry
	 * @param context
	 * @param request
	 * @return
	 */
	private ChatEntry.Builder addChatEntry(Properties context, CreateChatEntryRequest request) {
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		AtomicReference<MChatEntry> entryReference = new AtomicReference<>();
		Trx.run(transactionName -> {
			String tableName = request.getTableName();
			MTable table = MTable.get(context, tableName);
			PO entity = RecordUtil.getEntity(context, tableName, request.getUuid(), request.getId(), transactionName);
			//	
			StringBuffer whereClause = new StringBuffer();
			List<Object> parameters = new ArrayList<>();
			//	
			whereClause
				.append(I_CM_Chat.COLUMNNAME_AD_Table_ID).append(" = ?")
				.append(" AND ")
				.append(I_CM_Chat.COLUMNNAME_Record_ID).append(" = ?");
			//	Set parameters
			parameters.add(table.getAD_Table_ID());
			parameters.add(request.getId());
			MChat chat = new Query(context, I_CM_Chat.Table_Name, whereClause.toString(), transactionName)
					.setParameters(parameters)
					.setClient_ID()
					.first();
			//	Add or create chat
			if (chat == null 
					|| chat.getCM_Chat_ID() == 0) {
				chat = new MChat (context, table.getAD_Table_ID(), entity.get_ID(), entity.getDisplayValue(), transactionName);
				chat.saveEx();
			}
			//	Add entry PO
			MChatEntry entry = new MChatEntry(chat, request.getComment());
			entry.saveEx(transactionName);
			entryReference.set(entry);
		});
		//	Return
		return ConvertUtil.convertChatEntry(entryReference.get());
	}
	
	/**
	 * Get Last change Log
	 * @param tableId
	 * @param recordId
	 * @param transactionName
	 * @return
	 */
	private int getLastChangeLogId(int tableId, int recordId, String transactionName) {
		return DB.getSQLValue(null, "SELECT AD_ChangeLog_ID "
				+ "FROM AD_ChangeLog "
				+ "WHERE AD_Table_ID = ? "
				+ "AND Record_ID = ? "
				+ "AND ROWNUM <= 1 "
				+ "ORDER BY Updated DESC", tableId, recordId);
	}
	
	/**
	 * Convert Default Value from query
	 * @param sql
	 * @return
	 */
	private Object convertDefaultValue(String sql) {
		Object defaultValue = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			//	SELECT Key, Value, Name FROM ...
			pstmt = DB.prepareStatement(sql, null);
			//	Get from Query
			rs = pstmt.executeQuery();
			if (rs.next()) {
				defaultValue = rs.getObject(1);
			}
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
		} finally {
			DB.close(rs, pstmt);
		}
		//	Return values
		return defaultValue;
	}
	
	/**
	 * Get default value base on field, process parameter, browse field or column
	 * @param request
	 * @return
	 */
	private DefaultValue.Builder getInfoFromDefaultValueRequest(GetDefaultValueRequest request) {
		int referenceId = 0;
		int referenceValueId = 0;
		int validationRuleId = 0;
		String columnName = null;
		String defaultValue = null;
		if(!Util.isEmpty(request.getFieldUuid())) {
			MField field = (MField) RecordUtil.getEntity(Env.getCtx(), I_AD_Field.Table_Name, request.getFieldUuid(), 0, null);
			int fieldId = field.getAD_Field_ID();
			List<MField> customFields = ASPUtil.getInstance(Env.getCtx()).getWindowFields(field.getAD_Tab_ID());
			if(customFields != null) {
				Optional<MField> maybeField = customFields.stream().filter(customField -> customField.getAD_Field_ID() == fieldId).findFirst();
				if(maybeField.isPresent()) {
					field = maybeField.get();
					defaultValue = field.getDefaultValue();
					MColumn column = MColumn.get(Env.getCtx(), field.getAD_Column_ID());
					//	Display Type
					referenceId = column.getAD_Reference_ID();
					referenceValueId = column.getAD_Reference_Value_ID();
					validationRuleId = column.getAD_Val_Rule_ID();
					columnName = column.getColumnName();
					if(field.getAD_Reference_ID() > 0) {
						referenceId = field.getAD_Reference_ID();
					}
					if(field.getAD_Reference_Value_ID() > 0) {
						referenceValueId = field.getAD_Reference_Value_ID();
					}
					if(field.getAD_Val_Rule_ID() > 0) {
						validationRuleId = field.getAD_Val_Rule_ID();
					}
					if(Util.isEmpty(defaultValue)
							&& !Util.isEmpty(column.getDefaultValue())) {
						defaultValue = column.getDefaultValue();
					}
				}
			}
		} else if(!Util.isEmpty(request.getBrowseFieldUuid())) {
			MBrowseField browseField = (MBrowseField) RecordUtil.getEntity(Env.getCtx(), I_AD_Browse_Field.Table_Name, request.getBrowseFieldUuid(), 0, null);
			int browseFieldId = browseField.getAD_Browse_Field_ID();
			List<MBrowseField> customFields = ASPUtil.getInstance(Env.getCtx()).getBrowseFields(browseField.getAD_Browse_ID());
			if(customFields != null) {
				Optional<MBrowseField> maybeField = customFields.stream().filter(customField -> customField.getAD_Browse_Field_ID() == browseFieldId).findFirst();
				if(maybeField.isPresent()) {
					browseField = maybeField.get();
					defaultValue = browseField.getDefaultValue();
					referenceId = browseField.getAD_Reference_ID();
					referenceValueId = browseField.getAD_Reference_Value_ID();
					validationRuleId = browseField.getAD_Val_Rule_ID();
					MViewColumn viewColumn = browseField.getAD_View_Column();
					if(viewColumn.getAD_Column_ID() > 0) {
						MColumn column = MColumn.get(Env.getCtx(), viewColumn.getAD_Column_ID());
						columnName = column.getColumnName();
						if(Util.isEmpty(defaultValue)
								&& !Util.isEmpty(column.getDefaultValue())) {
							defaultValue = column.getDefaultValue();
						}
					} else {
						columnName = browseField.getAD_Element().getColumnName();
					}
				}
			}
		} else if(!Util.isEmpty(request.getProcessParameterUuid())) {
			MProcessPara processParameter = (MProcessPara) RecordUtil.getEntity(Env.getCtx(), I_AD_Process_Para.Table_Name, request.getProcessParameterUuid(), 0, null);
			int processParameterId = processParameter.getAD_Process_Para_ID();
			List<MProcessPara> customParameters = ASPUtil.getInstance(Env.getCtx()).getProcessParameters(processParameter.getAD_Process_ID());
			if(customParameters != null) {
				Optional<MProcessPara> maybeParameter = customParameters.stream().filter(customField -> customField.getAD_Process_Para_ID() == processParameterId).findFirst();
				if(maybeParameter.isPresent()) {
					processParameter = maybeParameter.get();
					referenceId = processParameter.getAD_Reference_ID();
					referenceValueId = processParameter.getAD_Reference_Value_ID();
					validationRuleId = processParameter.getAD_Val_Rule_ID();
					columnName = processParameter.getColumnName();
					defaultValue = processParameter.getDefaultValue();
				}
			}
		} else if(!Util.isEmpty(request.getColumnUuid())) {
			int columnId = RecordUtil.getIdFromUuid(I_AD_Column.Table_Name, request.getColumnUuid(), null);
			if(columnId > 0) {
				MColumn column = MColumn.get(Env.getCtx(), columnId);
				referenceId = column.getAD_Reference_ID();
				referenceValueId = column.getAD_Reference_Value_ID();
				validationRuleId = column.getAD_Val_Rule_ID();
				columnName = column.getColumnName();
				defaultValue = column.getDefaultValue();
			}
		} else {
			throw new AdempiereException("@AD_Reference_ID@ / @AD_Column_ID@ / @AD_Table_ID@ / @AD_Process_Para_ID@ / @IsMandatory@");
		}
		//	Validate SQL
		return getDefaultKeyAndValue(request.getContextAttributesList(), defaultValue, referenceId, referenceValueId, columnName, validationRuleId);
	}
	
	
	/**
	 * Get Default value, also convert it to lookup value if is necessary
	 * @param contextAttributes
	 * @param defaultValue
	 * @param referenceId
	 * @param referenceValueId
	 * @param columnName
	 * @param validationRuleId
	 * @return
	 */
	private DefaultValue.Builder getDefaultKeyAndValue(List<KeyValue> contextAttributes, String defaultValue, int referenceId, int referenceValueId, String columnName, int validationRuleId) {
		DefaultValue.Builder builder = DefaultValue.newBuilder();
		if(!Util.isEmpty(defaultValue)) {
			Object defaultValueAsObject = null;
			Properties context = Env.getCtx();
			int windowNo = ThreadLocalRandom.current().nextInt(1, 8996 + 1);
			Env.clearWinContext(windowNo);
			Map<String, Object> attributes = ValueUtil.convertValuesToObjects(contextAttributes);
			//	Fill context
			attributes.entrySet().forEach(attribute -> {
				if(attribute.getValue() instanceof Integer) {
					Env.setContext(context, windowNo, attribute.getKey(), (Integer) attribute.getValue());
				} else if(attribute.getValue() instanceof Timestamp) {
					Env.setContext(context, windowNo, attribute.getKey(), (Timestamp) attribute.getValue());
				} else if(attribute.getValue() instanceof Boolean) {
					Env.setContext(context, windowNo, attribute.getKey(), (Boolean) attribute.getValue());
				} else if(attribute.getValue() instanceof String) {
					Env.setContext(context, windowNo, attribute.getKey(), (String) attribute.getValue());
				}
			});
			if(defaultValue.trim().startsWith("@SQL=")) {
				defaultValue = defaultValue.replace("@SQL=", "");
				defaultValue = Env.parseContext(context, windowNo, defaultValue, false);
				defaultValueAsObject = convertDefaultValue(defaultValue);
			} else {
				defaultValueAsObject = Env.parseContext(context, windowNo, defaultValue, false);
			}
			//	 For lookups
			if(defaultValueAsObject != null) {
				//	Convert value from type
				if(DisplayType.isID(referenceId)
						|| referenceId == DisplayType.Integer) {
					try {
						defaultValueAsObject = Integer.parseInt(String.valueOf(defaultValueAsObject));
					} catch (Exception e) {
						log.warning(e.getLocalizedMessage());
					}
				} else if(DisplayType.isNumeric(validationRuleId)) {
					try {
						defaultValueAsObject = new BigDecimal(String.valueOf(defaultValueAsObject));
					} catch (Exception e) {
						log.warning(e.getLocalizedMessage());
					}
				}
				if(DisplayType.isLookup(referenceId)) {
					if(referenceId == DisplayType.List) {
						MRefList referenceList = MRefList.get(Env.getCtx(), referenceValueId, String.valueOf(defaultValueAsObject), null);
						builder = convertDefaultValueFromResult(referenceList.getValue(), referenceList.getUUID(), referenceList.getValue(), referenceList.get_Translation(MRefList.COLUMNNAME_Name));
					} else {
						MLookupInfo lookupInfo = ReferenceUtil.getReferenceLookupInfo(referenceId, referenceValueId, columnName, validationRuleId);
						if(!Util.isEmpty(lookupInfo.QueryDirect)) {
							String sql = MRole.getDefault(Env.getCtx(), false).addAccessSQL(lookupInfo.QueryDirect,
									lookupInfo.TableName, MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
							PreparedStatement pstmt = null;
							ResultSet rs = null;
							try {
								//	SELECT Key, Value, Name FROM ...
								pstmt = DB.prepareStatement(sql.toString(), null);
								ValueUtil.setParameterFromObject(pstmt, defaultValueAsObject, 1);
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
										keyValue = rs.getString(2);
									} else {
										keyValue = rs.getInt(1);
									}
									String uuid = null;
									//	Validate if exist UUID
									int uuidIndex = getColumnIndex(metaData, I_AD_Element.COLUMNNAME_UUID);
									if(uuidIndex != -1) {
										uuid = rs.getString(uuidIndex);
									}
									//	
									builder = convertDefaultValueFromResult(keyValue, uuid, rs.getString(2), rs.getString(3));
								}
							} catch (Exception e) {
								log.severe(e.getLocalizedMessage());
								throw new AdempiereException(e);
							} finally {
								DB.close(rs, pstmt);
							}
						}
					}
				} else {
					builder.putValues(columnName, ValueUtil.getValueFromObject(defaultValueAsObject).build());
				}
			}
		}
		return builder;
	}
	
	/**
	 * Convert Context Info Value from query
	 * @param request
	 * @return
	 */
	private ContextInfoValue.Builder convertContextInfoValue(Properties context, GetContextInfoValueRequest request) {
		ContextInfoValue.Builder builder = ContextInfoValue.newBuilder();
		if(request == null) {
			throw new AdempiereException("Object Request Null");
		}
		if(request.getId() <= 0
				&& Util.isEmpty(request.getUuid())) {
			throw new AdempiereException("@Record_ID@ / @UUID@ @NotFound@");
		}
		int id = request.getId();
		if(id <= 0) {
			id = RecordUtil.getIdFromUuid(I_AD_ContextInfo.Table_Name, request.getUuid(), null);
		}
		MADContextInfo contextInfo = MADContextInfo.getById(context, id);
		if(contextInfo != null
				&& contextInfo.getAD_ContextInfo_ID() > 0) {
			try {
				//	Set value for parse no save
				contextInfo.setSQLStatement(request.getQuery());
				MMessage message = MMessage.get(Env.getCtx(), contextInfo.getAD_Message_ID());
				if(message != null) {
					//	Parse
					Object[] arguments = contextInfo.getArguments(0);
					if(arguments == null) {
						return builder;
					}
					//	
					String messageText = Msg.getMsg(Env.getAD_Language(Env.getCtx()), message.getValue(), arguments);
					//	Set result message
					builder.setMessageText(ValueUtil.validateNull(messageText));
				}
			} catch (Exception e) {
				log.log(Level.WARNING, e.getLocalizedMessage());
			}
		}
		//	Return values
		return builder;
	}
	
	/**
	 * Convert Context Info Value from query
	 * @param request
	 * @return
	 */
	private PrivateAccess.Builder convertPrivateAccess(Properties context, MPrivateAccess privateAccess) {
		PrivateAccess.Builder builder = PrivateAccess.newBuilder();
		if(privateAccess == null) {
			return builder;
		}
		//	Table
		MTable table = MTable.get(context, privateAccess.getAD_Table_ID());
		//	Set values
		builder.setTableName(table.getTableName());
		MUser user = MUser.get(context, privateAccess.getAD_User_ID());
		builder.setUuid(ValueUtil.validateNull(user.getUUID()));
		builder.setId(privateAccess.getRecord_ID());
		builder.setIsLocked(privateAccess.isActive());
		//	Return values
		return builder;
	}
	
	/**
	 * Convert Lookup from query
	 * @param request
	 * @return
	 */
	private LookupItem.Builder convertLookupItem(GetLookupItemRequest request) {
		MLookupInfo reference = getInfoFromRequest(request.getReferenceUuid(), request.getFieldUuid(), request.getProcessParameterUuid(), request.getBrowseFieldUuid(), request.getColumnUuid(), request.getColumnName(), request.getTableName());
		if(reference == null) {
			throw new AdempiereException("@AD_Reference_ID@ @NotFound@");
		}
		String sql = reference.QueryDirect;
		Properties context = Env.getCtx();
		int windowNo = ThreadLocalRandom.current().nextInt(1, 8996 + 1);
		Env.clearWinContext(windowNo);
		Map<String, Object> attributes = ValueUtil.convertValuesToObjects(request.getContextAttributesList());
		//	Fill context
		attributes.entrySet().forEach(attribute -> {
			if(attribute.getValue() instanceof Integer) {
				Env.setContext(context, windowNo, attribute.getKey(), (Integer) attribute.getValue());
			} else if(attribute.getValue() instanceof Timestamp) {
				Env.setContext(context, windowNo, attribute.getKey(), (Timestamp) attribute.getValue());
			} else if(attribute.getValue() instanceof Boolean) {
				Env.setContext(context, windowNo, attribute.getKey(), (Boolean) attribute.getValue());
			} else if(attribute.getValue() instanceof String) {
				Env.setContext(context, windowNo, attribute.getKey(), (String) attribute.getValue());
			}
		});
		sql = Env.parseContext(context, windowNo, sql, false);
		if(Util.isEmpty(sql)
				&& !Util.isEmpty(reference.QueryDirect)) {
			throw new AdempiereException("@AD_Tab_ID@ @WhereClause@ @Unparseable@");
		}
		sql = MRole.getDefault(Env.getCtx(), false).addAccessSQL(sql,
				reference.TableName, MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
		LookupItem.Builder builder = LookupItem.newBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			//	SELECT Key, Value, Name FROM ...
			pstmt = DB.prepareStatement(sql.toString(), null);
			ValueUtil.setParameterFromObject(pstmt, request.getId(), 1);
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
					keyValue = rs.getString(2);
				} else {
					keyValue = rs.getInt(1);
				}
				String uuid = null;
				//	Validate if exist UUID
				int uuidIndex = getColumnIndex(metaData, I_AD_Element.COLUMNNAME_UUID);
				if(uuidIndex != -1) {
					uuid = rs.getString(uuidIndex);
				}
				//	
				builder = convertObjectFromResult(keyValue, uuid, rs.getString(2), rs.getString(3));
			}
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			throw new AdempiereException(e);
		} finally {
			DB.close(rs, pstmt);
		}
		//	Return values
		return builder;
	}
	
	/**
	 * Get reference Info from request
	 * @param request
	 * @return
	 */
	private MLookupInfo getInfoFromRequest(String referenceUuid, String fieldUuid, String processParameterUuid, String browseFieldUuid, String columnUuid, String columnName, String tableName) {
		int referenceId = 0;
		int referenceValueId = 0;
		int validationRuleId = 0;
		if(!Util.isEmpty(referenceUuid)) {
			referenceId = RecordUtil.getIdFromUuid(I_AD_Reference.Table_Name, referenceUuid, null);
		} else if(!Util.isEmpty(fieldUuid)) {
			MField field = (MField) RecordUtil.getEntity(Env.getCtx(), I_AD_Field.Table_Name, fieldUuid, 0, null);
			int fieldId = field.getAD_Field_ID();
			List<MField> customFields = ASPUtil.getInstance(Env.getCtx()).getWindowFields(field.getAD_Tab_ID());
			if(customFields != null) {
				Optional<MField> maybeField = customFields.stream().filter(customField -> customField.getAD_Field_ID() == fieldId).findFirst();
				if(maybeField.isPresent()) {
					field = maybeField.get();
					MColumn column = MColumn.get(Env.getCtx(), field.getAD_Column_ID());
					//	Display Type
					referenceId = column.getAD_Reference_ID();
					referenceValueId = column.getAD_Reference_Value_ID();
					validationRuleId = column.getAD_Val_Rule_ID();
					columnName = column.getColumnName();
					if(field.getAD_Reference_ID() > 0) {
						referenceId = field.getAD_Reference_ID();
					}
					if(field.getAD_Reference_Value_ID() > 0) {
						referenceValueId = field.getAD_Reference_Value_ID();
					}
					if(field.getAD_Val_Rule_ID() > 0) {
						validationRuleId = field.getAD_Val_Rule_ID();
					}
				}
			}
		} else if(!Util.isEmpty(browseFieldUuid)) {
			MBrowseField browseField = (MBrowseField) RecordUtil.getEntity(Env.getCtx(), I_AD_Browse_Field.Table_Name, browseFieldUuid, 0, null);
			int browseFieldId = browseField.getAD_Browse_Field_ID();
			List<MBrowseField> customFields = ASPUtil.getInstance(Env.getCtx()).getBrowseFields(browseField.getAD_Browse_ID());
			if(customFields != null) {
				Optional<MBrowseField> maybeField = customFields.stream().filter(customField -> customField.getAD_Browse_Field_ID() == browseFieldId).findFirst();
				if(maybeField.isPresent()) {
					browseField = maybeField.get();
					referenceId = browseField.getAD_Reference_ID();
					referenceValueId = browseField.getAD_Reference_Value_ID();
					validationRuleId = browseField.getAD_Val_Rule_ID();
					MViewColumn viewColumn = browseField.getAD_View_Column();
					if(viewColumn.getAD_Column_ID() > 0) {
						columnName = MColumn.getColumnName(Env.getCtx(), viewColumn.getAD_Column_ID());
					} else {
						columnName = browseField.getAD_Element().getColumnName();
					}
				}
			}
		} else if(!Util.isEmpty(processParameterUuid)) {
			MProcessPara processParameter = (MProcessPara) RecordUtil.getEntity(Env.getCtx(), I_AD_Process_Para.Table_Name, processParameterUuid, 0, null);
			int processParameterId = processParameter.getAD_Process_Para_ID();
			List<MProcessPara> customParameters = ASPUtil.getInstance(Env.getCtx()).getProcessParameters(processParameter.getAD_Process_ID());
			if(customParameters != null) {
				Optional<MProcessPara> maybeParameter = customParameters.stream().filter(customField -> customField.getAD_Process_Para_ID() == processParameterId).findFirst();
				if(maybeParameter.isPresent()) {
					processParameter = maybeParameter.get();
					referenceId = processParameter.getAD_Reference_ID();
					referenceValueId = processParameter.getAD_Reference_Value_ID();
					validationRuleId = processParameter.getAD_Val_Rule_ID();
					columnName = processParameter.getColumnName();
				}
			}
		} else if(!Util.isEmpty(columnUuid)) {
			int columnId = RecordUtil.getIdFromUuid(I_AD_Column.Table_Name, columnUuid, null);
			if(columnId > 0) {
				MColumn column = MColumn.get(Env.getCtx(), columnId);
				referenceId = column.getAD_Reference_ID();
				referenceValueId = column.getAD_Reference_Value_ID();
				validationRuleId = column.getAD_Val_Rule_ID();
				columnName = column.getColumnName();
			}
		} else if(!Util.isEmpty(columnName)) {
			referenceId = DisplayType.TableDir;
		} else if(!Util.isEmpty(tableName)) {	//	Is a Table Direct
			referenceId = DisplayType.TableDir;
			columnName = tableName + "_ID";
		} else {
			throw new AdempiereException("@AD_Reference_ID@ / @AD_Column_ID@ / @AD_Table_ID@ / @AD_Process_Para_ID@ / @IsMandatory@");
		}
		return ReferenceUtil.getReferenceLookupInfo(referenceId, referenceValueId, columnName, validationRuleId);
	}
	
	/**
	 * Convert Object to list
	 * @param request
	 * @return
	 */
	private ListLookupItemsResponse.Builder convertLookupItemsList(ListLookupItemsRequest request) {
		MLookupInfo reference = getInfoFromRequest(request.getReferenceUuid(), request.getFieldUuid(), request.getProcessParameterUuid(), request.getBrowseFieldUuid(), request.getColumnUuid(), request.getColumnName(), request.getTableName());
		if(reference == null) {
			throw new AdempiereException("@AD_Reference_ID@ @NotFound@");
		}
		String sql = reference.Query;
		Properties context = Env.getCtx();
		int windowNo = ThreadLocalRandom.current().nextInt(1, 8996 + 1);
		Env.clearWinContext(windowNo);
		Map<String, Object> attributes = ValueUtil.convertValuesToObjects(request.getContextAttributesList());
		//	Fill context
		attributes.entrySet().forEach(attribute -> {
			if(attribute.getValue() instanceof Integer) {
				Env.setContext(context, windowNo, attribute.getKey(), (Integer) attribute.getValue());
			} else if(attribute.getValue() instanceof Timestamp) {
				Env.setContext(context, windowNo, attribute.getKey(), (Timestamp) attribute.getValue());
			} else if(attribute.getValue() instanceof Boolean) {
				Env.setContext(context, windowNo, attribute.getKey(), (Boolean) attribute.getValue());
			} else if(attribute.getValue() instanceof String) {
				Env.setContext(context, windowNo, attribute.getKey(), (String) attribute.getValue());
			}
		});
		sql = Env.parseContext(context, windowNo, sql, false);
		if(Util.isEmpty(sql)
				&& !Util.isEmpty(reference.Query)) {
			throw new AdempiereException("@AD_Tab_ID@ @WhereClause@ @Unparseable@");
		}
		List<Object> parameters = new ArrayList<>();
		sql = RecordUtil.addSearchValueAndGet(sql, reference.TableName, request.getSearchValue(), parameters);
		sql = MRole.getDefault(Env.getCtx(), false).addAccessSQL(sql,
				reference.TableName, MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.getPageSize(request.getPageSize());
		int offset = pageNumber * RecordUtil.getPageSize(request.getPageSize());
		int count = 0;
		//	Count records
		count = RecordUtil.countRecords(sql, reference.TableName, parameters);
		//	Add Row Number
		sql = RecordUtil.getQueryWithLimit(sql, limit, offset);
		ListLookupItemsResponse.Builder builder = ListLookupItemsResponse.newBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			//	SELECT Key, Value, Name FROM ...
			pstmt = DB.prepareStatement(sql, null);
			AtomicInteger parameterIndex = new AtomicInteger(1);
			for(Object value : parameters) {
				ValueUtil.setParameterFromObject(pstmt, value, parameterIndex.getAndIncrement());
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
						|| keyValueType == Types.NCHAR
						|| keyValueType == Types.OTHER) {
					keyValue = rs.getString(2);
				} else {
					keyValue = rs.getInt(1);
				}
				String uuid = null;
				//	Validate if exist UUID
				int uuidIndex = getColumnIndex(metaData, I_AD_Element.COLUMNNAME_UUID);
				if(uuidIndex != -1) {
					uuid = rs.getString(uuidIndex);
				}
				//	
				LookupItem.Builder valueObject = convertObjectFromResult(keyValue, uuid, rs.getString(2), rs.getString(3));
				builder.addRecords(valueObject.build());
			}
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			throw new AdempiereException(e);
		} finally {
			DB.close(rs, pstmt);
		}
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(RecordUtil.isValidNextPageToken(count, offset, limit)) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	Return
		return builder;
	}
	
	/**
	 * Verify if exist a column
	 * @param metaData
	 * @param columnName
	 * @return
	 * @throws SQLException 
	 */
	private int getColumnIndex(ResultSetMetaData metaData, String columnName) throws SQLException {
		for(int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
			if(metaData.getColumnName(columnIndex).toLowerCase().equals(columnName.toLowerCase())) {
				return columnIndex;
			}
		}
		return -1;
	}
	
	/**
	 * Get Report Query from Criteria
	 * @param criteria
	 * @return
	 */
	private MQuery getReportQueryFromCriteria(Criteria criteria) {
		MTable table = MTable.get(Env.getCtx(), criteria.getTableName());
		MQuery query = new MQuery(table.getTableName());
		criteria.getConditionsList().stream()
		.filter(condition -> !Util.isEmpty(condition.getColumnName()))
		.forEach(condition -> {
			String columnName = condition.getColumnName();
			String operator = convertOperator(condition.getOperatorValue());
			if(condition.getOperatorValue() == Operator.LIKE_VALUE
					|| condition.getOperatorValue() == Operator.NOT_LIKE_VALUE) {
				columnName = "UPPER(" + columnName + ")";
				query.addRestriction(columnName, operator, ValueUtil.getObjectFromValue(condition.getValue(), true));
			}
			//	For in or not in
			if(condition.getOperatorValue() == Operator.IN_VALUE
					|| condition.getOperatorValue() == Operator.NOT_IN_VALUE) {
				StringBuffer whereClause = new StringBuffer();
				whereClause.append(columnName).append(convertOperator(condition.getOperatorValue()));
				StringBuffer parameter = new StringBuffer();
				condition.getValuesList().forEach(value -> {
					if(parameter.length() > 0) {
						parameter.append(", ");
					}
					Object convertedValue = ValueUtil.getObjectFromValue(value);
					if(convertedValue instanceof String) {
						convertedValue = "'" + convertedValue + "'";
					}
					parameter.append(convertedValue);
				});
				whereClause.append("(").append(parameter).append(")");
				query.addRestriction(whereClause.toString());
			} else if(condition.getOperatorValue() == Operator.BETWEEN_VALUE) {
				query.addRangeRestriction(columnName, ValueUtil.getObjectFromValue(condition.getValue()), ValueUtil.getObjectFromValue(condition.getValueTo()));
			} else if(condition.getOperatorValue() == Operator.NULL_VALUE
					|| condition.getOperatorValue() == Operator.NOT_NULL_VALUE) {
				query.addRestriction(columnName, operator, null);
			} else {
				query.addRestriction(columnName, operator, ValueUtil.getObjectFromValue(condition.getValue()));
			}
		});
		return query;
	}
	
	/**
	 * Get Where clause for Smart Browse
	 * @param browser
	 * @param parsedWhereClause
	 * @param values
	 * @return
	 */
	private String getBrowserWhereClause(MBrowse browser, String parsedWhereClause, List<KeyValue> contextAttributes, HashMap<String, Object> parameterMap, List<Object> values) {
		StringBuilder browserWhereClause = new StringBuilder();
		List<MBrowseField> fields = ASPUtil.getInstance().getBrowseFields(browser.getAD_Browse_ID());
		LinkedHashMap<String, MBrowseField> fieldsMap = new LinkedHashMap<>();
		AtomicReference<String> convertedWhereClause = new AtomicReference<String>(parsedWhereClause);
		if(parsedWhereClause != null
				&& contextAttributes != null) {
			contextAttributes.forEach(contextValue -> {
				convertedWhereClause.set(convertedWhereClause.get().replaceAll("@" + contextValue.getKey() + "@", String.valueOf(ValueUtil.getObjectFromValue(contextValue.getValue()))));
			});
		}
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
		if(!Util.isEmpty(convertedWhereClause.get())) {
			whereClause = convertedWhereClause.get();
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
	private ListBrowserItemsResponse.Builder convertBrowserList(ListBrowserItemsRequest request) {
		ListBrowserItemsResponse.Builder builder = ListBrowserItemsResponse.newBuilder();
		MBrowse browser = getBrowser(request.getUuid());
		if(browser == null) {
			return builder;
		}
		Criteria criteria = request.getCriteria();
		HashMap<String, Object> parameterMap = new HashMap<>();
		//	Populate map
		criteria.getConditionsList().forEach(condition -> parameterMap.put(condition.getColumnName(), ValueUtil.getObjectFromValue(condition.getValue())));
		List<Object> values = new ArrayList<Object>();
		String whereClause = getBrowserWhereClause(browser, browser.getWhereClause(), request.getContextAttributesList(), parameterMap, values);
		//	Page prefix
		int page = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		String query = DictionaryUtil.addQueryReferencesFromBrowser(browser);
		String orderByClause = DictionaryUtil.getSQLOrderBy(browser);
		StringBuilder sql = new StringBuilder(query);
		if (!Util.isEmpty(whereClause)) {
			sql.append(" WHERE ").append(whereClause); // includes first AND
		} else {
			sql.append(" WHERE 1=1");
		}
		MView view = browser.getAD_View();
		MViewDefinition parentDefinition = view.getParentViewDefinition();
		String tableNameAlias = parentDefinition.getTableAlias();
		String tableName = parentDefinition.getAD_Table().getTableName();
		//	
		String parsedSQL = MRole.getDefault().addAccessSQL(sql.toString(),
				tableNameAlias, MRole.SQL_FULLYQUALIFIED,
				MRole.SQL_RO);
		if(Util.isEmpty(orderByClause)) {
			orderByClause = "";
		} else {
			orderByClause = " ORDER BY " + orderByClause;
		}
		//	Count records
		int count = RecordUtil.countRecords(parsedSQL, tableName, values);
		String nexPageToken = null;
		int pageMultiplier = page == 0? 1: page;
		if(count > (RecordUtil.getPageSize(request.getPageSize()) * pageMultiplier)) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (page + 1);
		}
		//	Add Row Number
		parsedSQL = RecordUtil.getQueryWithLimit(parsedSQL, count, RecordUtil.getPageSize(request.getPageSize()));
		//	Add Order By
		parsedSQL = parsedSQL + orderByClause;
		//	Return
		builder = convertBrowserResult(browser, parsedSQL, values);
		//	Validate page token
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	Return
		return builder;
	}
	
	/**
	 * Convert SQL to list values
	 * @param pagePrefix
	 * @param browser
	 * @param sql
	 * @param values
	 * @return
	 */
	private ListBrowserItemsResponse.Builder convertBrowserResult(MBrowse browser, String sql, List<Object> values) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ListBrowserItemsResponse.Builder builder = ListBrowserItemsResponse.newBuilder();
		long recordCount = 0;
		try {
			LinkedHashMap<String, MBrowseField> fieldsMap = new LinkedHashMap<>();
			//	Add field to map
			for(MBrowseField field: ASPUtil.getInstance().getBrowseFields(browser.getAD_Browse_ID())) {
				fieldsMap.put(field.getAD_View_Column().getColumnName().toUpperCase(), field);
			}
			//	SELECT Key, Value, Name FROM ...
			pstmt = DB.prepareStatement(sql, null);
			AtomicInteger parameterIndex = new AtomicInteger(1);
			for(Object value : values) {
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
						MBrowseField field = fieldsMap.get(columnName.toUpperCase());
						Value.Builder valueBuilder = null;
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
						String fieldColumnName = field.getAD_View_Column().getColumnName();
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
	 * get browser
	 * @param context
	 * @param uuid
	 * @return
	 */
	private MBrowse getBrowser(String uuid) {
		String key = uuid + "|" + Env.getAD_Language(Env.getCtx());
		MBrowse browser = browserRequested.get(key);
		if(browser == null) {
			browser = new Query(Env.getCtx(), I_AD_Browse.Table_Name, I_AD_Process.COLUMNNAME_UUID + " = ?", null)
					.setParameters(uuid)
					.setOnlyActiveRecords(true)
					.first();
			browser = ASPUtil.getInstance(Env.getCtx()).getBrowse(browser.getAD_Browse_ID());
		}
		//	Put on Cache
		if(browser != null) {
			browserRequested.put(key, browser);
		}
		//	
		return browser;
	}
	
	/**
	 * Run callout with data from server
	 * @param request
	 * @return
	 */
	private org.spin.grpc.util.Callout.Builder runcallout(RunCalloutRequest request) {
		org.spin.grpc.util.Callout.Builder calloutBuilder = org.spin.grpc.util.Callout.newBuilder();
		Trx.run(transactionName -> {
			MTab tab = tabRequested.get(request.getTabUuid());
			if(tab == null) {
				tab = MTab.get(Env.getCtx(), RecordUtil.getIdFromUuid(I_AD_Tab.Table_Name, request.getTabUuid(), transactionName));
			}
			if(tab != null) {
				MField field = null;
				if(tab != null) {
					Optional<MField> searchedValue = Arrays.asList(tab.getFields(false, null)).stream().filter(searchField -> searchField.getAD_Column().getColumnName().equals(request.getColumnName())).findFirst();
					if(searchedValue.isPresent()) {
						field = searchedValue.get();
					}
				}
				int tabNo = (tab.getSeqNo() / 10) - 1;
				if(tabNo < 0) {
					tabNo = 0;
				}
				//	window
				int windowNo = request.getWindowNo();
				if(windowNo <= 0) {
					windowNo = windowNoEmulation.getAndIncrement();
				}
				//	Initial load for callout wrapper
				GridWindowVO gridWindowVo = GridWindowVO.create(Env.getCtx(), windowNo, tab.getAD_Window_ID());
				GridWindow gridWindow = new GridWindow(gridWindowVo, true);
				GridTabVO gridTabVo = GridTabVO.create(gridWindowVo, tabNo, tab, false, true);
				GridFieldVO gridFieldVo = GridFieldVO.create(Env.getCtx(), windowNo, tabNo, tab.getAD_Window_ID(), tab.getAD_Tab_ID(), false, field);
				GridField gridField = new GridField(gridFieldVo);
				GridTab gridTab = new GridTab(gridTabVo, gridWindow, true);
				//	Init tab
				gridTab.query(false);
				gridTab.clearSelection();
				gridTab.dataNew(false);
				//	load values
				Map<String, Object> attributes = ValueUtil.convertValuesToObjects(request.getContextAttributesList());
				for(Entry<String, Object> attribute : attributes.entrySet()) {
					gridTab.setValue(attribute.getKey(), attribute.getValue());
				}
				//	Load value for field
				gridField.setValue(ValueUtil.getObjectFromValue(request.getOldValue()), false);
				gridField.setValue(ValueUtil.getObjectFromValue(request.getValue()), false);
				//	Run it
				String result = processCallout(windowNo, gridTab, gridField);
				Arrays.asList(gridTab.getFields()).stream().filter(fieldValue -> isValidChange(fieldValue))
				.forEach(fieldValue -> calloutBuilder.putValues(fieldValue.getColumnName(), ValueUtil.getValueFromObject(fieldValue.getValue()).build()));
				calloutBuilder.setResult(ValueUtil.validateNull(result));
			}
		});
		return calloutBuilder;
	}
	
	/**
	 * Verify if a value has been changed
	 * @param gridField
	 * @return
	 */
	private boolean isValidChange(GridField gridField) {
		//	Standard columns
		if(gridField.getColumnName().equals(I_AD_Element.COLUMNNAME_Created) 
				|| gridField.getColumnName().equals(I_AD_Element.COLUMNNAME_CreatedBy) 
				|| gridField.getColumnName().equals(I_AD_Element.COLUMNNAME_Updated) 
				|| gridField.getColumnName().equals(I_AD_Element.COLUMNNAME_UpdatedBy) 
				|| gridField.getColumnName().equals(I_AD_Element.COLUMNNAME_UUID)) {
			return false;
		}
		//	Oly Displayed
		if(!gridField.isDisplayed()) {
			return false;
		}
		//	Key
		if(gridField.isKey()) {
			return false;
		}
		//	new value like null
		if(gridField.getValue() == null
				&& gridField.getOldValue() == null) {
			return false;
		}
		//	validate with old value
		if(gridField.getOldValue() != null
				&& gridField.getValue() != null
				&& gridField.getValue().equals(gridField.getOldValue())) {
			return false;
		}
		//	Default
		return true;
	}
	
	/**
	 * Process Callout
	 * @param gridTab
	 * @param field
	 * @return
	 */
	private String processCallout (int windowNo, GridTab gridTab, GridField field) {
		String callout = field.getCallout();
		if (callout.length() == 0)
			return "";
		//
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
				
				MRule rule = MRule.get(Env.getCtx(), cmd.substring(MRule.SCRIPT_PREFIX.length()));
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
				MRule.setContext(engine, Env.getCtx(), windowNo);
				// now add the callout parameters windowNo, tab, field, value, oldValue to the engine 
				// Method arguments context are A_
				engine.put(MRule.ARGUMENTS_PREFIX + "WindowNo", windowNo);
				engine.put(MRule.ARGUMENTS_PREFIX + "Tab", this);
				engine.put(MRule.ARGUMENTS_PREFIX + "Field", field);
				engine.put(MRule.ARGUMENTS_PREFIX + "Value", value);
				engine.put(MRule.ARGUMENTS_PREFIX + "OldValue", oldValue);
				engine.put(MRule.ARGUMENTS_PREFIX + "Ctx", Env.getCtx());

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
					retValue = call.start(Env.getCtx(), method, windowNo, gridTab, field, value, oldValue);
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
	 * Convert Values from result
	 * @param keyValue
	 * @param uuidValue
	 * @param value
	 * @param displayValue
	 * @return
	 */
	private LookupItem.Builder convertObjectFromResult(Object keyValue, String uuidValue, String value, String displayValue) {
		LookupItem.Builder builder = LookupItem.newBuilder();
		if(keyValue == null) {
			return builder;
		}
		builder.setUuid(ValueUtil.validateNull(uuidValue));
		
		if(keyValue instanceof Integer) {
			builder.setId((Integer) keyValue);
			builder.putValues(KEY_COLUMN_KEY, ValueUtil.getValueFromInteger((Integer) keyValue).build());
		} else {
			builder.putValues(KEY_COLUMN_KEY, ValueUtil.getValueFromString((String) keyValue).build());
		}
		//	Set Value
		if(!Util.isEmpty(value)) {
			builder.putValues(VALUE_COLUMN_KEY, ValueUtil.getValueFromString(value).build());
		}
		//	Display column
		if(!Util.isEmpty(displayValue)) {
			builder.putValues(DISPLAY_COLUMN_KEY, ValueUtil.getValueFromString(displayValue).build());
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
	private DefaultValue.Builder convertDefaultValueFromResult(Object keyValue, String uuidValue, String value, String displayValue) {
		DefaultValue.Builder builder = DefaultValue.newBuilder();
		if(keyValue == null) {
			return builder;
		}
		builder.setUuid(ValueUtil.validateNull(uuidValue));
		
		if(keyValue instanceof Integer) {
			builder.setId((Integer) keyValue);
			builder.putValues(KEY_COLUMN_KEY, ValueUtil.getValueFromInteger((Integer) keyValue).build());
		} else {
			builder.putValues(KEY_COLUMN_KEY, ValueUtil.getValueFromString((String) keyValue).build());
		}
		//	Set Value
		if(!Util.isEmpty(value)) {
			builder.putValues(VALUE_COLUMN_KEY, ValueUtil.getValueFromString(value).build());
		}
		//	Display column
		if(!Util.isEmpty(displayValue)) {
			builder.putValues(DISPLAY_COLUMN_KEY, ValueUtil.getValueFromString(displayValue).build());
		}
		//	
		return builder;
	}
}

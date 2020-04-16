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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import javax.script.ScriptEngine;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.I_AD_Browse;
import org.adempiere.model.MBrowse;
import org.adempiere.model.MBrowseField;
import org.adempiere.model.MDocumentStatus;
import org.adempiere.model.MView;
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
import org.compiere.model.I_AD_Element;
import org.compiere.model.I_AD_Form;
import org.compiere.model.I_AD_Language;
import org.compiere.model.I_AD_Menu;
import org.compiere.model.I_AD_PInstance;
import org.compiere.model.I_AD_PInstance_Log;
import org.compiere.model.I_AD_PrintFormat;
import org.compiere.model.I_AD_Private_Access;
import org.compiere.model.I_AD_Process;
import org.compiere.model.I_AD_ReportView;
import org.compiere.model.I_AD_Role;
import org.compiere.model.I_AD_Tab;
import org.compiere.model.I_AD_TreeNodeMM;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_AD_WF_EventAudit;
import org.compiere.model.I_AD_WF_NextCondition;
import org.compiere.model.I_AD_WF_Node;
import org.compiere.model.I_AD_WF_NodeNext;
import org.compiere.model.I_AD_WF_Process;
import org.compiere.model.I_AD_Window;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.I_CM_Chat;
import org.compiere.model.I_CM_ChatEntry;
import org.compiere.model.I_C_Country;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_PA_DashboardContent;
import org.compiere.model.MChangeLog;
import org.compiere.model.MChat;
import org.compiere.model.MChatEntry;
import org.compiere.model.MChatType;
import org.compiere.model.MColumn;
import org.compiere.model.MCountry;
import org.compiere.model.MCurrency;
import org.compiere.model.MDashboardContent;
import org.compiere.model.MDocType;
import org.compiere.model.MField;
import org.compiere.model.MForm;
import org.compiere.model.MLanguage;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MMenu;
import org.compiere.model.MMessage;
import org.compiere.model.MOrder;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MPrivateAccess;
import org.compiere.model.MProcess;
import org.compiere.model.MQuery;
import org.compiere.model.MRecentItem;
import org.compiere.model.MReportView;
import org.compiere.model.MRole;
import org.compiere.model.MRule;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MWindow;
import org.compiere.model.M_Element;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.model.X_AD_PInstance_Log;
import org.compiere.model.X_AD_TreeNodeMM;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.MimeType;
import org.compiere.util.Msg;
import org.compiere.util.NamePair;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFEventAudit;
import org.compiere.wf.MWFNextCondition;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFNodeNext;
import org.compiere.wf.MWFProcess;
import org.compiere.wf.MWFResponsible;
import org.compiere.wf.MWorkflow;
import org.eevolution.service.dsl.ProcessBuilder;
import org.spin.grpc.util.BusinessDataServiceGrpc.BusinessDataServiceImplBase;
import org.spin.grpc.util.ChatEntry.ModeratorStatus;
import org.spin.grpc.util.Condition.Operator;
import org.spin.grpc.util.RecordChat.ConfidentialType;
import org.spin.grpc.util.RecordChat.ModerationType;
import org.spin.grpc.util.RollbackEntityRequest.EventType;
import org.spin.grpc.util.Value.ValueType;
import org.spin.grpc.util.WorkflowCondition.ConditionType;
import org.spin.grpc.util.WorkflowCondition.Operation;
import org.spin.grpc.util.WorkflowDefinition.DurationUnit;
import org.spin.grpc.util.WorkflowDefinition.PublishStatus;
import org.spin.grpc.util.WorkflowProcess.WorkflowState;
import org.spin.model.I_AD_ContextInfo;
import org.spin.model.MADContextInfo;
import org.spin.util.ASPUtil;
import org.spin.util.AbstractExportFormat;
import org.spin.util.ReportExportHandler;

import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * https://itnext.io/customizing-grpc-generated-code-5909a2551ca1
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Business data service
 */
public class BusinessDataServiceImplementation extends BusinessDataServiceImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(BusinessDataServiceImplementation.class);
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
	/**	Country */
	private static CCache<String, MCountry> countryCache = new CCache<String, MCountry>(I_C_Country.Table_Name + "_UUID", 30, 0);	//	no time-out
	/**	Reference cache	*/
	private static CCache<String, String> referenceWhereClauseCache = new CCache<String, String>("Reference_WhereClause", 30, 0);	//	no time-out
	/**	Window emulation	*/
	private AtomicInteger windowNoEmulation = new AtomicInteger(1);
	/**	Page Size	*/
	private final int PAGE_SIZE = 50;
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
	public void getEntity(GetEntityRequest request, StreamObserver<Entity> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			Entity.Builder entityValue = getEntity(context, request);
			responseObserver.onNext(entityValue.build());
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
	public void createEntity(CreateEntityRequest request, StreamObserver<Entity> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			Entity.Builder entityValue = createEntity(context, request);
			responseObserver.onNext(entityValue.build());
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
	public void updateEntity(UpdateEntityRequest request, StreamObserver<Entity> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			Entity.Builder entityValue = updateEntity(context, request);
			responseObserver.onNext(entityValue.build());
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
	public void deleteEntity(DeleteEntityRequest request, StreamObserver<Empty> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			Empty.Builder entityValue = deleteEntity(context, request);
			responseObserver.onNext(entityValue.build());
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
	public void rollbackEntity(RollbackEntityRequest request, StreamObserver<Entity> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Rollback Requested = " + request.getRecordId());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			Entity.Builder entityValue = rollbackLastEntityAction(context, request);
			responseObserver.onNext(entityValue.build());
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
	public void runCallout(RunCalloutRequest request, StreamObserver<org.spin.grpc.util.Callout> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getCallout())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Callout Requested = " + request.getCallout());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			org.spin.grpc.util.Callout.Builder calloutResponse = runcallout(context, request);
			responseObserver.onNext(calloutResponse.build());
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
	public void listEntities(ListEntitiesRequest request, StreamObserver<ListEntitiesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListEntitiesResponse.Builder entityValueList = convertEntitiesList(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void getLookupItem(GetLookupItemRequest request, StreamObserver<LookupItem> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Lookup Requested = " + request.getUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			LookupItem.Builder lookupValue = convertLookupItem(context, request);
			responseObserver.onNext(lookupValue.build());
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
	public void listLookupItems(ListLookupItemsRequest request, StreamObserver<ListLookupItemsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Lookup Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListLookupItemsResponse.Builder entityValueList = convertLookupItemsList(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void runBusinessProcess(RunBusinessProcessRequest request, StreamObserver<ProcessLog> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Lookup List Requested = " + request.getUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ProcessLog.Builder processReponse = runProcess(context, request);
			responseObserver.onNext(processReponse.build());
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
	public void listBrowserItems(ListBrowserItemsRequest request, StreamObserver<ListBrowserItemsResponse> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Browser Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListBrowserItemsResponse.Builder entityValueList = convertBrowserList(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void listProcessLogs(ListProcessLogsRequest request, StreamObserver<ListProcessLogsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Process Activity Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListProcessLogsResponse.Builder entityValueList = convertProcessLogs(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void listRecentItems(ListRecentItemsRequest request, StreamObserver<ListRecentItemsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Process Activity Requested is Null");
			}
			log.fine("Recent Items Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListRecentItemsResponse.Builder entityValueList = convertRecentItems(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void listReferences(ListReferencesRequest request, StreamObserver<ListReferencesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Process Activity Requested is Null");
			}
			log.fine("References Info Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListReferencesResponse.Builder entityValueList = convertRecordReferences(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void getDefaultValue(GetDefaultValueRequest request, StreamObserver<Value> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			Value.Builder defaultValue = convertDefaultValue(context, request);
			responseObserver.onNext(defaultValue.build());
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
	public void lockPrivateAccess(LockPrivateAccessRequest request, StreamObserver<PrivateAccess> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			PrivateAccess.Builder privateaccess = lockUnlockPrivateAccess(context, request.getTableName(), request.getRecordId(), request.getUserUuid(), true);
			responseObserver.onNext(privateaccess.build());
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
	public void unlockPrivateAccess(UnlockPrivateAccessRequest request,
			StreamObserver<PrivateAccess> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			PrivateAccess.Builder privateaccess = lockUnlockPrivateAccess(context, request.getTableName(), request.getRecordId(), request.getUserUuid(), false);
			responseObserver.onNext(privateaccess.build());
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
	public void getPrivateAccess(GetPrivateAccessRequest request, StreamObserver<PrivateAccess> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			PrivateAccess.Builder privateaccess = convertPrivateAccess(context, getPrivateAccess(context, request.getTableName(), request.getRecordId(), request.getUserUuid()));
			responseObserver.onNext(privateaccess.build());
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
	public void getContextInfoValue(GetContextInfoValueRequest request, StreamObserver<ContextInfoValue> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ContextInfoValue.Builder contextInfoValue = convertContextInfoValue(context, request);
			responseObserver.onNext(contextInfoValue.build());
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
	public void listPendingDocuments(ListPendingDocumentsRequest request, StreamObserver<ListPendingDocumentsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListPendingDocumentsResponse.Builder pendingDocumentsList = convertPendingDocumentList(context, request);
			responseObserver.onNext(pendingDocumentsList.build());
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
	public void listFavorites(ListFavoritesRequest request, StreamObserver<ListFavoritesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListFavoritesResponse.Builder favoritesList = convertFavoritesList(context, request);
			responseObserver.onNext(favoritesList.build());
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
	public void listDashboards(ListDashboardsRequest request, StreamObserver<ListDashboardsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListDashboardsResponse.Builder dashboardsList = convertDashboarsList(context, request);
			responseObserver.onNext(dashboardsList.build());
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
	public void listLanguages(ListLanguagesRequest request, StreamObserver<ListLanguagesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListLanguagesResponse.Builder languagesList = convertLanguagesList(context, request);
			responseObserver.onNext(languagesList.build());
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
	public void listTranslations(ListTranslationsRequest request, StreamObserver<ListTranslationsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListTranslationsResponse.Builder translationsList = convertTranslationsList(context, request);
			responseObserver.onNext(translationsList.build());
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
	public void listPrintFormats(ListPrintFormatsRequest request, StreamObserver<ListPrintFormatsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListPrintFormatsResponse.Builder printFormatsList = convertPrintFormatsList(context, request);
			responseObserver.onNext(printFormatsList.build());
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
	public void listReportViews(ListReportViewsRequest request, StreamObserver<ListReportViewsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListReportViewsResponse.Builder reportViewsList = convertReportViewsList(context, request);
			responseObserver.onNext(reportViewsList.build());
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
	public void listDrillTables(ListDrillTablesRequest request, StreamObserver<ListDrillTablesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListDrillTablesResponse.Builder drillTablesList = convertDrillTablesList(context, request);
			responseObserver.onNext(drillTablesList.build());
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
	public void getReportOutput(GetReportOutputRequest request, StreamObserver<ReportOutput> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ReportOutput.Builder reportOutput = getReportOutput(context, request);
			responseObserver.onNext(reportOutput.build());
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
	public void listRecordLogs(ListRecordLogsRequest request, StreamObserver<ListRecordLogsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Record Logs Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListRecordLogsResponse.Builder entityValueList = convertRecordLogs(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void listWorkflowLogs(ListWorkflowLogsRequest request,
			StreamObserver<ListWorkflowLogsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Workflow Logs Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListWorkflowLogsResponse.Builder entityValueList = convertWorkflowLogs(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void listRecordChats(ListRecordChatsRequest request,
			StreamObserver<ListRecordChatsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Record Chats Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListRecordChatsResponse.Builder entityValueList = convertRecordChats(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void listChatEntries(ListChatEntriesRequest request,
			StreamObserver<ListChatEntriesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Chat Entries Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListChatEntriesResponse.Builder entityValueList = convertChatEntries(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void listWorkflows(ListWorkflowsRequest request, StreamObserver<ListWorkflowsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Workflow Logs Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListWorkflowsResponse.Builder entityValueList = convertWorkflows(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void createChatEntry(CreateChatEntryRequest request, StreamObserver<ChatEntry> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ChatEntry.Builder chatEntryValue = addChatEntry(context, request);
			responseObserver.onNext(chatEntryValue.build());
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
	public void listDocumentActions(ListDocumentActionsRequest request,
			StreamObserver<ListDocumentActionsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Document Actions is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListDocumentActionsResponse.Builder entityValueList = convertDocumentActions(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void listDocumentStatuses(ListDocumentStatusesRequest request,
			StreamObserver<ListDocumentStatusesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Document Statuses is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			ListDocumentStatusesResponse.Builder entityValueList = convertDocumentStatuses(context, request);
			responseObserver.onNext(entityValueList.build());
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
	public void getCountry(GetCountryRequest request, StreamObserver<Country> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Country Request Null");
			}
			log.fine("Country Requested = " + request.getCountryUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage());
			Country.Builder country = getCountry(context, request);
			responseObserver.onNext(country.build());
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
	 * Convert Object to list
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private ReportOutput.Builder getReportOutput(Properties context, GetReportOutputRequest request) throws FileNotFoundException, IOException {
		Criteria criteria = request.getCriteria();
		if(Util.isEmpty(criteria.getTableName())) {
			throw new AdempiereException("@TableName@ @NotFound@");
		}
		//	Validate print format
		if(Util.isEmpty(request.getPrintFormatUuid())) {
			throw new AdempiereException("@AD_PrintFormat_ID@ @NotFound@");
		}
		MTable table = MTable.get(context, criteria.getTableName());
		//	
		if(!MRole.getDefault().isCanReport(table.getAD_Table_ID())) {
			throw new AdempiereException("@AccessCannotReport@");
		}
		//	
		ReportOutput.Builder builder = ReportOutput.newBuilder();
		MQuery query = getReportQueryFromCriteria(context, criteria);
		if(!Util.isEmpty(criteria.getWhereClause())) {
			query.addRestriction(criteria.getWhereClause());
		}
		//	
		PrintInfo printInformation = new PrintInfo(request.getReportName(), table.getAD_Table_ID(), 0, 0);
		//	Get Print Format
		MPrintFormat printFormat = null;
		MReportView reportView = null;
		if(!Util.isEmpty(request.getPrintFormatUuid())) {
			printFormat = new Query(context, I_AD_PrintFormat.Table_Name, I_AD_PrintFormat.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getPrintFormatUuid())
					.first();
		}
		//	Get Report View
		if(!Util.isEmpty(request.getReportViewUuid())) {
			reportView = new Query(context, I_AD_ReportView.Table_Name, I_AD_ReportView.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getReportViewUuid())
					.first();
		}
		//	Get Default
		if(printFormat == null) {
			int reportViewId = 0;
			if(reportView != null) {
				reportViewId = reportView.getAD_ReportView_ID();
			}
			printFormat = MPrintFormat.get(context, reportViewId, table.getAD_Table_ID());
		}
		//	Validate print format
		if(printFormat == null) {
			throw new AdempiereException("@AD_PrintGormat_ID@ @NotFound@");
		}
		if(table.getAD_Table_ID() != printFormat.getAD_Table_ID()) {
			table = MTable.get(context, printFormat.getAD_Table_ID());
		}
		//	Run report engine
		ReportEngine reportEngine = new ReportEngine(Env.getCtx(), printFormat, query, printInformation);
		//	Set report view
		if(reportView != null) {
			reportEngine.setAD_ReportView_ID(reportView.getAD_ReportView_ID());
		} else {
			reportView = MReportView.get(context, reportEngine.getAD_ReportView_ID());
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
			String headerName = Msg.getMsg(context, "Report") + ": " + reportEngine.getName() + "  " + Env.getHeader(context, 0);
			builder.setHeaderName(ValueUtil.validateNull(headerName));
			StringBuffer footerName = new StringBuffer ();
			footerName.append(Msg.getMsg(context, "DataCols")).append("=")
				.append(reportEngine.getColumnCount())
				.append(", ").append(Msg.getMsg(context, "DataRows")).append("=")
				.append(reportEngine.getRowCount());
			builder.setFooterName(ValueUtil.validateNull(footerName.toString()));
			//	Type
			builder.setReportType(request.getReportType());
			ByteString resultFile = ByteString.readFrom(new FileInputStream(reportFile));
			if(request.getReportType().endsWith("html")
					|| request.getReportType().endsWith("txt")) {
				builder.setOutputBytes(resultFile);
			}
			builder.setReportViewUuid(ValueUtil.validateNull(reportView.getUUID()));
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
	 * @return
	 */
	private MPrivateAccess getPrivateAccess(Properties context, String tableName, int recordId, String userUuid) {
		return new Query(context, I_AD_Private_Access.Table_Name, "EXISTS(SELECT 1 FROM AD_Table t WHERE t.AD_Table_ID = AD_Private_Access.AD_Table_ID AND t.TableName = ?) "
				+ "AND Record_ID = ? "
				+ "AND EXISTS(SELECT 1 FROM AD_User u WHERE u.AD_User_ID = AD_Private_Access.AD_User_ID AND u.UUID = ?)", null)
			.setParameters(tableName, recordId, userUuid)
			.first();
	}
	
	/**
	 * Lock and unlock private access
	 * @param context
	 * @param request
	 * @param lock
	 * @return
	 */
	private PrivateAccess.Builder lockUnlockPrivateAccess(Properties context, String tableName, int recordId, String userUuid, boolean lock) {
		MPrivateAccess privateAccess = getPrivateAccess(context, tableName, recordId, userUuid);
		//	Create new
		if(privateAccess == null
				|| privateAccess.getAD_Table_ID() == 0) {
			MTable table = MTable.get(context, tableName);
			//	Set values
			MUser user = new Query(context, I_AD_User.Table_Name, I_AD_User.COLUMNNAME_UUID + " = ? ", null).setParameters(userUuid).first();
			if(user == null
					|| user.getAD_User_ID() == 0) {
				throw new AdempiereException("@AD_User_ID@ @NotFound@");
			}
			privateAccess = new MPrivateAccess(context, user.getAD_User_ID(), table.getAD_Table_ID(), recordId);
		}
		//	Set active
		privateAccess.setIsActive(lock);
		privateAccess.saveEx();
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
		if(request.getRecordId() == 0
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
		} else if(request.getRecordId() > 0) {
			whereClause.append(tableName + "_ID = ?");
			params.add(request.getRecordId());
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
	 * Convert pending documents to gRPC
	 * @param context
	 * @param request
	 * @return
	 */
	private ListPendingDocumentsResponse.Builder convertPendingDocumentList(Properties context, ListPendingDocumentsRequest request) {
		ListPendingDocumentsResponse.Builder builder = ListPendingDocumentsResponse.newBuilder();
		//	Get entity
		if(Util.isEmpty(request.getUserUuid())
				|| Util.isEmpty(request.getRoleUuid())) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ @NotFound@");
		}
		//	Get user
		int userId = new Query(context, I_AD_User.Table_Name, I_AD_User.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getUserUuid())
				.firstId();
		//	Get role
		int roleId = new Query(context, I_AD_Role.Table_Name, I_AD_Role.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getRoleUuid())
				.firstId();
		//	Get from document status
		Arrays.asList(MDocumentStatus.getDocumentStatusIndicators(context, userId, roleId)).forEach(documentStatus -> {
			PendingDocument.Builder pendingDocument = PendingDocument.newBuilder();
			pendingDocument.setDocumentName(ValueUtil.validateNull(documentStatus.getName()));
			// for Reference
			if(documentStatus.getAD_Window_ID() != 0) {
				MWindow window = MWindow.get(context, documentStatus.getAD_Window_ID());
				pendingDocument.setWindowUuid(ValueUtil.validateNull(window.getUUID()));
			} else if(documentStatus.getAD_Form_ID() != 0) {
				MForm form = new MForm(context, documentStatus.getAD_Form_ID(), null);
				pendingDocument.setFormUuid(ValueUtil.validateNull(form.getUUID()));
			}
			//	Criteria
			MTable table = MTable.get(context, documentStatus.getAD_Table_ID());
			pendingDocument.setCriteria(Criteria.newBuilder()
					.setTableName(ValueUtil.validateNull(table.getTableName()))
					.setWhereClause(ValueUtil.validateNull(documentStatus.getWhereClause())));
			//	Set quantity
			pendingDocument.setRecordCount(MDocumentStatus.evaluate(documentStatus));
			//	TODO: Add description for interface
			builder.addPendingDocuments(pendingDocument);
		});
		//	Return
		return builder;
	}
	
	/**
	 * Convert languages to gRPC
	 * @param context
	 * @param request
	 * @return
	 */
	private ListLanguagesResponse.Builder convertLanguagesList(Properties context, ListLanguagesRequest request) {
		ListLanguagesResponse.Builder builder = ListLanguagesResponse.newBuilder();
		new Query(context, I_AD_Language.Table_Name, "(IsSystemLanguage=? OR IsBaseLanguage=?)", null)
			.setParameters(true, true)
			.setOnlyActiveRecords(true)
			.<MLanguage>list()
			.forEach(language -> {
				org.spin.grpc.util.Language.Builder languageBuilder = org.spin.grpc.util.Language.newBuilder();
				languageBuilder.setLanguage(ValueUtil.validateNull(language.getAD_Language()));
				languageBuilder.setCountryCode(ValueUtil.validateNull(language.getCountryCode()));
				languageBuilder.setLanguageISO(ValueUtil.validateNull(language.getLanguageISO()));
				languageBuilder.setLanguageName(ValueUtil.validateNull(language.getName()));
				languageBuilder.setDatePattern(ValueUtil.validateNull(language.getDatePattern()));
				languageBuilder.setTimePattern(ValueUtil.validateNull(language.getTimePattern()));
				languageBuilder.setIsBaseLanguage(language.isBaseLanguage());
				languageBuilder.setIsSystemLanguage(language.isSystemLanguage());
				languageBuilder.setIsDecimalPoint(language.isDecimalPoint());
				builder.addLanguages(languageBuilder);
			});
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
		MTable table = MTable.get(context, tableName);
		PO entity = getEntity(context, tableName, request.getRecordUuid(), request.getRecordId());
		List<Object> parameters = new ArrayList<>();
		StringBuffer whereClause = new StringBuffer(entity.get_KeyColumns()[0] + " = ?");
		parameters.add(entity.get_ID());
		if(!Util.isEmpty(request.getLanguage())) {
			whereClause.append(" AND AD_Language = ?");
			parameters.add(request.getLanguage());
		}
		new Query(context, tableName + "_Trl", whereClause.toString(), null)
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
						if(Util.isEmpty(translationBuilder.getTranslationUuid())) {
							translationBuilder.setTranslationUuid(ValueUtil.validateNull(translation.get_UUID()));
						}
						//	Set Language
						if(Util.isEmpty(translationBuilder.getLanguage())) {
							translationBuilder.setLanguage(ValueUtil.validateNull(translation.get_ValueAsString("AD_Language")));
						}
					}
				});
				builder.addTranslations(translationBuilder);
			});
		//	Return
		return builder;
	}
	
	/**
	 * Convert dashboards to gRPC
	 * @param context
	 * @param request
	 * @return
	 */
	private ListDashboardsResponse.Builder convertDashboarsList(Properties context, ListDashboardsRequest request) {
		ListDashboardsResponse.Builder builder = ListDashboardsResponse.newBuilder();
		//	Get entity
		if(Util.isEmpty(request.getRoleUuid())) {
			throw new AdempiereException("@AD_Role_ID@ @NotFound@");
		}
		//	Get role
		int roleId = new Query(context, I_AD_Role.Table_Name, I_AD_Role.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getRoleUuid())
				.firstId();
		new Query(context, I_PA_DashboardContent.Table_Name, 
				"EXISTS(SELECT 1 FROM AD_Dashboard_Access da WHERE da.PA_DashboardContent_ID = PA_DashboardContent.PA_DashboardContent_ID AND da.AD_Role_ID = ?)", null)
			.setParameters(roleId)
			.setOnlyActiveRecords(true)
			.setOrderBy(I_PA_DashboardContent.COLUMNNAME_ColumnNo + "," + I_PA_DashboardContent.COLUMNNAME_AD_Client_ID + "," + I_PA_DashboardContent.COLUMNNAME_Line)
			.<MDashboardContent>list()
			.forEach(dashboard -> {
				Dashboard.Builder dashboardBuilder = Dashboard.newBuilder();
				dashboardBuilder.setDashboardName(ValueUtil.validateNull(dashboard.getName()));
				dashboardBuilder.setDashboardDescription(ValueUtil.validateNull(dashboard.getDescription()));
				dashboardBuilder.setDashboardHtml(ValueUtil.validateNull(dashboard.getHTML()));
				dashboardBuilder.setColumnNo(dashboard.getColumnNo());
				dashboardBuilder.setLineNo(dashboard.getLine());
				dashboardBuilder.setIsEventRequired(dashboard.isEventRequired());
				dashboardBuilder.setIsCollapsible(dashboard.isCollapsible());
				dashboardBuilder.setIsOpenByDefault(dashboard.isOpenByDefault());
				//	For Window
				if(dashboard.getAD_Window_ID() != 0) {
					MWindow window = MWindow.get(context, dashboard.getAD_Window_ID());
					dashboardBuilder.setWindowUuid(ValueUtil.validateNull(window.getUUID()));
				}
				//	For Smart Browser
				if(dashboard.getAD_Browse_ID() != 0) {
					MBrowse browser = MBrowse.get(context, dashboard.getAD_Browse_ID());
					dashboardBuilder.setWindowUuid(ValueUtil.validateNull(browser.getUUID()));
				}
				//	File Name
				String fileName = dashboard.getZulFilePath();
				if(!Util.isEmpty(fileName)) {
					int endIndex = fileName.lastIndexOf(".");
					int beginIndex = fileName.lastIndexOf("/");
					if(beginIndex == -1) {
						beginIndex = 0;
					} else {
						beginIndex++;
					}
					if(endIndex == -1) {
						endIndex = fileName.length();
					}
					//	Set
					dashboardBuilder.setFileName(ValueUtil.validateNull(fileName.substring(beginIndex, endIndex)));
				}
				builder.addDashboards(dashboardBuilder);
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
			whereClause = "AD_Table_ID = ?";
			parameters.add(table);
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
	 * @param context
	 * @param request
	 * @return
	 */
	private ListDrillTablesResponse.Builder convertDrillTablesList(Properties context, ListDrillTablesRequest request) {
		ListDrillTablesResponse.Builder builder = ListDrillTablesResponse.newBuilder();
		//	Get entity
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@TableName@ @NotFound@");
		}
		MTable table = MTable.get(context, request.getTableName());
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
					M_Element element = M_Element.get(context, columnName);
					//	Add here
					DrillTable.Builder drillTable = DrillTable.newBuilder();
					drillTable.setTableName(ValueUtil.validateNull(drillTableName));
					String name = element.getPrintName();
					String poName = element.getPO_PrintName();
					if(!Env.isBaseLanguage(context, "")) {
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
	 * Convert favorites to gRPC
	 * @param context
	 * @param request
	 * @return
	 */
	private ListFavoritesResponse.Builder convertFavoritesList(Properties context, ListFavoritesRequest request) {
		ListFavoritesResponse.Builder builder = ListFavoritesResponse.newBuilder();
		//	Get entity
		if(Util.isEmpty(request.getUserUuid())) {
			throw new AdempiereException("@AD_User_ID@ @NotFound@");
		}
		//	Get user
		int userId = new Query(context, I_AD_User.Table_Name, I_AD_User.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getUserUuid())
				.firstId();
		//	TODO: add tree criteria
		new Query(context, I_AD_TreeNodeMM.Table_Name, "EXISTS(SELECT 1 "
				+ "FROM AD_TreeBar tb "
				+ "WHERE tb.AD_Tree_ID = AD_TreeNodeMM.AD_Tree_ID "
				+ "AND tb.Node_ID = AD_TreeNodeMM.Node_ID "
				+ "AND tb.AD_User_ID = ?)", null)
			.setParameters(userId)
			.setClient_ID()
			.<X_AD_TreeNodeMM>list().forEach(treeNodeMenu -> {
				Favorite.Builder favorite = Favorite.newBuilder();
				String menuName = "";
				String menuDescription = "";
				MMenu menu = MMenu.getFromId(context, treeNodeMenu.getNode_ID());
				favorite.setMenuUuid(ValueUtil.validateNull(menu.getUUID()));
				String action = MMenu.ACTION_Window;
				if(!menu.isCentrallyMaintained()) {
					menuName = menu.getName();
					menuDescription = menu.getDescription();
					if(!Env.isBaseLanguage(context, "")) {
						String translation = menu.get_Translation("Name");
						if(!Util.isEmpty(translation)) {
							menuName = translation;
						}
						translation = menu.get_Translation("Description");
						if(!Util.isEmpty(translation)) {
							menuDescription = translation;
						}
					}
				}
				//	Supported actions
				if(!Util.isEmpty(menu.getAction())) {
					action = menu.getAction();
					String referenceUuid = null;
					if(menu.getAction().equals(MMenu.ACTION_Form)) {
						if(menu.getAD_Form_ID() > 0) {
							MForm form = new MForm(context, menu.getAD_Form_ID(), null);
							referenceUuid = form.getUUID();
							if(menu.isCentrallyMaintained()) {
								menuName = form.getName();
								menuDescription = form.getDescription();
								if(!Env.isBaseLanguage(context, "")) {
									String translation = form.get_Translation("Name");
									if(!Util.isEmpty(translation)) {
										menuName = translation;
									}
									translation = form.get_Translation("Description");
									if(!Util.isEmpty(translation)) {
										menuDescription = translation;
									}
								}
							}
						}
					} else if(menu.getAction().equals(MMenu.ACTION_Window)) {
						if(menu.getAD_Window_ID() > 0) {
							MWindow window = new MWindow(context, menu.getAD_Window_ID(), null);
							referenceUuid = window.getUUID();
							if(menu.isCentrallyMaintained()) {
								menuName = window.getName();
								menuDescription = window.getDescription();
								if(!Env.isBaseLanguage(context, "")) {
									String translation = window.get_Translation("Name");
									if(!Util.isEmpty(translation)) {
										menuName = translation;
									}
									translation = window.get_Translation("Description");
									if(!Util.isEmpty(translation)) {
										menuDescription = translation;
									}
								}
							}
						}
					} else if(menu.getAction().equals(MMenu.ACTION_Process)
						|| menu.getAction().equals(MMenu.ACTION_Report)) {
						if(menu.getAD_Process_ID() > 0) {
							MProcess process = MProcess.get(context, menu.getAD_Process_ID());
							referenceUuid = process.getUUID();
							if(menu.isCentrallyMaintained()) {
								menuName = process.getName();
								menuDescription = process.getDescription();
								if(!Env.isBaseLanguage(context, "")) {
									String translation = process.get_Translation("Name");
									if(!Util.isEmpty(translation)) {
										menuName = translation;
									}
									translation = process.get_Translation("Description");
									if(!Util.isEmpty(translation)) {
										menuDescription = translation;
									}
								}
							}
						}
					} else if(menu.getAction().equals(MMenu.ACTION_SmartBrowse)) {
						if(menu.getAD_Browse_ID() > 0) {
							MBrowse smartBrowser = MBrowse.get(context, menu.getAD_Browse_ID());
							referenceUuid = smartBrowser.getUUID();
							if(menu.isCentrallyMaintained()) {
								menuName = smartBrowser.getName();
								menuDescription = smartBrowser.getDescription();
								if(!Env.isBaseLanguage(context, "")) {
									String translation = smartBrowser.get_Translation("Name");
									if(!Util.isEmpty(translation)) {
										menuName = translation;
									}
									translation = smartBrowser.get_Translation("Description");
									if(!Util.isEmpty(translation)) {
										menuDescription = translation;
									}
								}
							}
						}
					}
					favorite.setReferenceUuid(ValueUtil.validateNull(referenceUuid));
					favorite.setAction(ValueUtil.validateNull(action));
				}
				//	Set name and description
				favorite.setMenuName(ValueUtil.validateNull(menuName));
				favorite.setMenuDescription(ValueUtil.validateNull(menuDescription));
				builder.addFavorites(favorite);
			});
		//	Return
		return builder;
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
		MProcess process = getProcess(context, request.getUuid());
		if(process == null
				|| process.getAD_Process_ID() <= 0) {
			throw new AdempiereException("@AD_Process_ID@ @NotFound@");
		}
		int tableId = 0;
		int recordId = request.getRecordId();
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
			entity = getEntity(context, request.getTableName(), uuid, recordId);
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
			File reportFile = result.getReportAsFile();
			if(reportFile != null
					&& reportFile.exists()) {
				String validFileName = getValidName(reportFile.getName());
				ReportOutput.Builder output = ReportOutput.newBuilder();
				output.setFileName(ValueUtil.validateNull(validFileName));
				output.setName(result.getTitle());
				output.setMimeType(ValueUtil.validateNull(MimeType.getMimeType(validFileName)));
				output.setDescription(ValueUtil.validateNull(process.getDescription()));
				//	Type
				output.setReportType(request.getReportType());
				ByteString resultFile = ByteString.readFrom(new FileInputStream(reportFile));
				if(request.getReportType().endsWith("html")
						|| request.getReportType().endsWith("txt")) {
					output.setOutputBytes(resultFile);
				}
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
	private String getValidName(String name) {
		if(Util.isEmpty(name)) {
			return "";
		}
		return name.replaceAll("[+^:&áàäéèëíìïóòöúùñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ$()*#/><]", "").replaceAll(" ", "-");
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
	 * Get Process from UUID
	 * @param uuid
	 * @param language
	 * @return
	 */
	private MProcess getProcess(Properties context, String uuid) {
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
	private Entity.Builder getEntity(Properties context, GetEntityRequest request) {
		String tableName = request.getTableName();
		if(Util.isEmpty(request.getTableName())) {
			if(request.getCriteria() != null) {
				tableName = request.getCriteria().getTableName();
			}
		}
		PO entity = getEntity(context, tableName, request.getUuid(), request.getRecordId());
		//	Return
		return convertEntity(context, entity);
	}
	
	/**
	 * Convert a Country
	 * @param context
	 * @param request
	 * @return
	 */
	private Country.Builder getCountry(Properties context, GetCountryRequest request) {
		String key = null;
		MCountry country = null;
		if(Util.isEmpty(request.getCountryUuid()) && request.getCountryId() == 0) {
			key = "Default";
			country = countryCache.get(key);
			if(country == null) {
				country = MCountry.getDefault(context);
			}
		}
		//	By UUID
		if(!Util.isEmpty(request.getCountryUuid())
				&& country == null) {
			key = request.getCountryUuid();
			country = countryCache.put(key, country);
			if(country == null) {
				country = new Query(context, I_C_Country.Table_Name, I_C_Country.COLUMNNAME_UUID + " = ?", null).first();
			}
		}
		if(request.getCountryId() != 0
				&& country == null) {
			key = "ID:|" + request.getCountryId();
			country = countryCache.put(key, country);
			if(country == null) {
				country = MCountry.get(context, request.getCountryId());
			}
		}
		if(country != null) {
			countryCache.put(key, country);
		}
		//	Return
		return convertCountry(context, country);
	}
	
	/**
	 * Convert Country
	 * @param context
	 * @param country
	 * @return
	 */
	private Country.Builder convertCountry(Properties context, MCountry country) {
		Country.Builder builder = Country.newBuilder();
		if(country == null) {
			return builder;
		}
		builder.setUuid(ValueUtil.validateNull(country.getUUID()))
			.setId(country.getC_Country_ID())
			.setCountryCode(ValueUtil.validateNull(country.getCountryCode()))
			.setName(ValueUtil.validateNull(country.getName()))
			.setDescription(ValueUtil.validateNull(country.getDescription()))
			.setHasRegion(country.isHasRegion())
			.setRegionName(ValueUtil.validateNull(country.getRegionName()))
			.setDisplaySequence(ValueUtil.validateNull(country.getDisplaySequence()))
			.setIsAddressLinesReverse(country.isAddressLinesReverse())
			.setCaptureSequence(ValueUtil.validateNull(country.getCaptureSequence()))
			.setDisplaySequenceLocal(ValueUtil.validateNull(country.getDisplaySequenceLocal()))
			.setIsAddressLinesLocalReverse(country.isAddressLinesLocalReverse())
			.setHasPostalAdd(country.isHasPostal_Add())
			.setExpressionPhone(ValueUtil.validateNull(country.getExpressionPhone()))
			.setMediaSize(ValueUtil.validateNull(country.getMediaSize()))
			.setExpressionBankRoutingNo(ValueUtil.validateNull(country.getExpressionBankRoutingNo()))
			.setExpressionBankAccountNo(ValueUtil.validateNull(country.getExpressionBankAccountNo()))
			.setAllowCitiesOutOfList(country.isAllowCitiesOutOfList())
			.setIsPostcodeLookup(country.isPostcodeLookup())
			.setLanguage(ValueUtil.validateNull(country.getAD_Language()));
		//	Set Currency
		if(country.getC_Currency_ID() != 0) {
			builder.setCurrency(convertCurrency(MCurrency.get(context, country.getC_Currency_ID())));
		}
		//	
		return builder;
	}
	
	/**
	 * Convert Currency
	 * @param currency
	 * @return
	 */
	private Currency.Builder convertCurrency(MCurrency currency) {
		Currency.Builder builder = Currency.newBuilder();
		if(currency == null) {
			return builder;
		}
		//	Set values
		return builder.setUuid(ValueUtil.validateNull(currency.getUUID()))
			.setId(currency.getC_Currency_ID())
			.setISOCode(ValueUtil.validateNull(currency.getISO_Code()))
			.setCurSymbol(ValueUtil.validateNull(currency.getCurSymbol()))
			.setDescription(ValueUtil.validateNull(currency.getDescription()))
			.setStdPrecision(currency.getStdPrecision())
			.setCostingPrecision(currency.getCostingPrecision());
	}
	
//	string uuid = 1;
//	string iSOCode = 2;
//	string curSymbol = 3;
//	string description = 4;
//	int32 stdPrecision = 5;
//	int32 costingPrecision = 6;
	/**
	 * Delete a entity
	 * @param context
	 * @param request
	 * @return
	 */
	private Empty.Builder deleteEntity(Properties context, DeleteEntityRequest request) {
		PO entity = getEntity(context, request.getTableName(), request.getUuid(), request.getRecordId());
		if(entity != null
				&& entity.get_ID() >= 0) {
			entity.deleteEx(true);
		}
		//	Return
		return Empty.newBuilder();
	}
	
	/**
	 * Rollback entity
	 * @param context
	 * @param request
	 * @return
	 */
	private Entity.Builder rollbackLastEntityAction(Properties context, RollbackEntityRequest request) {
		if(Util.isEmpty(request.getTableName())) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		MTable table = MTable.get(context, request.getTableName());
		if(table == null
				|| table.getAD_Table_ID() == 0) {
			throw new AdempiereException("@AD_Table_ID@ @Invalid@");
		}
		String eventType = MChangeLog.EVENTCHANGELOG_Delete;
		if(request.getEventTypeValue() == EventType.INSERT_VALUE) {
			eventType = MChangeLog.EVENTCHANGELOG_Insert;
		} else if(request.getEventTypeValue() == EventType.UPDATE_VALUE) {
			eventType = MChangeLog.EVENTCHANGELOG_Update;
		}
		Entity.Builder builder = Entity.newBuilder();
		//	get Table from table name
		int lastChangeLogId = getLastChangeLogId(Env.getContextAsInt(context, "#AD_Session_ID"), table.getAD_Table_ID(), request.getRecordId(), eventType);
		if(lastChangeLogId > 0) {
			if(eventType.equals(MChangeLog.EVENTCHANGELOG_Insert)) {
				MChangeLog changeLog = new MChangeLog(context, lastChangeLogId, null);
				PO entity = getEntity(context, table.getTableName(), null, changeLog.getRecord_ID());
				if(entity != null
						&& entity.get_ID() >= 0) {
					entity.delete(true);
				}
			} else if(eventType.equals(MChangeLog.EVENTCHANGELOG_Delete)
					|| eventType.equals(MChangeLog.EVENTCHANGELOG_Update)) {
				PO entity = table.getPO(0, null);
				if(entity == null) {
					throw new AdempiereException("@Error@ PO is null");
				}
				new Query(context, I_AD_ChangeLog.Table_Name, I_AD_ChangeLog.COLUMNNAME_AD_ChangeLog_ID + " = ?", null)
					.setParameters(lastChangeLogId)
					.<MChangeLog>list().forEach(changeLog -> {
						setValueFromChangeLog(entity, changeLog);
					});
			}
		} else {
			throw new AdempiereException("@AD_ChangeLog_ID@ @NotFound@");
		}
		//	Return
		return builder;
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
			log.severe(e.getMessage());
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
	 * get Entity from Table and (UUID / Record ID)
	 * @param context
	 * @param tableName
	 * @param uuid
	 * @param recordId
	 * @return
	 */
	private PO getEntity(Properties context, String tableName, String uuid, int recordId) {
		//	Validate ID
		if(recordId == 0
				&& Util.isEmpty(uuid)) {
			throw new AdempiereException("@Record_ID@ @NotFound@");
		}
		
		if(Util.isEmpty(tableName)) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		StringBuffer whereClause = new StringBuffer();
		List<Object> params = new ArrayList<>();
		if(!Util.isEmpty(uuid)) {
			whereClause.append(I_AD_Element.COLUMNNAME_UUID + " = ?");
			params.add(uuid);
		} else if(recordId > 0) {
			whereClause.append(tableName + "_ID = ?");
			params.add(recordId);
		} else {
			throw new AdempiereException("@Record_ID@ @NotFound@");
		}
		//	Default
		return new Query(context, tableName, whereClause.toString(), null)
				.setParameters(params)
				.first();
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
		return convertEntity(context, entity);
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
		String tableName = request.getTableName();
		MTable table = MTable.get(context, tableName);
		PO entity = getEntity(context, tableName, null, request.getRecordId());
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
		parameters.add(request.getRecordId());
		MChat chat = new Query(context, I_CM_Chat.Table_Name, whereClause.toString(), null)
				.setParameters(parameters)
				.setClient_ID()
				.first();
		//	Add or create chat
		if (chat == null 
				|| chat.getCM_Chat_ID() == 0) {
			chat = new MChat (context, table.getAD_Table_ID(), entity.get_ID(), entity.getDisplayValue(), null);
			chat.saveEx();
		}
		//	Add entry PO
		MChatEntry entry = new MChatEntry(chat, request.getComment());
		entry.saveEx();
		//	Return
		return convertChatEntry(entry);
	}
	
	/**
	 * Update Entity
	 * @param context
	 * @param request
	 * @return
	 */
	private Entity.Builder updateEntity(Properties context, UpdateEntityRequest request) {
		PO entity = getEntity(context, request.getTableName(), request.getUuid(), request.getRecordId());
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
		return convertEntity(context, entity);
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
	 * Get Last change Log
	 * @param sessionId
	 * @param tableId
	 * @param recordId
	 * @param eventType
	 * @return
	 */
	private int getLastChangeLogId(int sessionId, int tableId, int recordId, String eventType) {
		return DB.getSQLValue(null, "SELECT AD_ChangeLog_ID "
				+ "FROM AD_ChangeLog "
				+ "WHERE AD_Session_ID = ? "
				+ "AND AD_Table_ID = ? "
				+ "AND Record_ID = ? "
				+ "AND EventChangeLog = ? "
				+ "AND ROWNUM <= 1 "
				+ "ORDER BY Updated DESC", sessionId, tableId, recordId, eventType);
	}
	
	/**
	 * Convert Default Value from query
	 * @param request
	 * @return
	 */
	private Value.Builder convertDefaultValue(Properties context, GetDefaultValueRequest request) {
		String sql = request.getQuery();
		Value.Builder builder = Value.newBuilder();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			//	SELECT Key, Value, Name FROM ...
			pstmt = DB.prepareStatement(sql, null);
			//	Get from Query
			rs = pstmt.executeQuery();
			if (rs.next()) {
				builder = ValueUtil.getValueFromObject(rs.getObject(1));
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
	 * Convert Context Info Value from query
	 * @param request
	 * @return
	 */
	private ContextInfoValue.Builder convertContextInfoValue(Properties context, GetContextInfoValueRequest request) {
		ContextInfoValue.Builder builder = ContextInfoValue.newBuilder();
		MADContextInfo contextInfo = new Query(context, I_AD_ContextInfo.Table_Name, I_AD_ContextInfo.COLUMNNAME_UUID + " = ?", null)
			.setParameters(request.getUuid())
			.first();
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
		builder.setUserUuid(ValueUtil.validateNull(user.getUUID()));
		builder.setRecordId(privateAccess.getRecord_ID());
		//	Return values
		return builder;
	}
	
	/**
	 * Convert Lookup from query
	 * @param request
	 * @return
	 */
	private LookupItem.Builder convertLookupItem(Properties context, GetLookupItemRequest request) {
		Criteria criteria = request.getCriteria();
		String sql = criteria.getQuery();
		List<Value> values = criteria.getValuesList();
		LookupItem.Builder builder = LookupItem.newBuilder();
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
					keyValue = rs.getString(2);
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
	private ListLookupItemsResponse.Builder convertLookupItemsList(Properties context, ListLookupItemsRequest request) {
		Criteria criteria = request.getCriteria();
		String sql = criteria.getQuery();
		sql = MRole.getDefault(context, false).addAccessSQL(sql,
				criteria.getTableName(), MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);
		List<Value> values = criteria.getValuesList();
		ListLookupItemsResponse.Builder builder = ListLookupItemsResponse.newBuilder();
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
						|| keyValueType == Types.NCHAR
						|| keyValueType == Types.OTHER) {
					keyValue = rs.getString(2);
				} else {
					keyValue = rs.getInt(1);
				}
				//	
				LookupItem.Builder valueObject = convertObjectFromResult(keyValue, null, rs.getString(2), rs.getString(3));
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
			pstmt.setInt(index, ValueUtil.getIntegerFromValue(value));
		} else if(value.getValueType().equals(ValueType.DECIMAL)) {
			pstmt.setBigDecimal(index, ValueUtil.getDecimalFromValue(value));
		} else if(value.getValueType().equals(ValueType.STRING)) {
			pstmt.setString(index, ValueUtil.getStringFromValue(value));
		} else if(value.getValueType().equals(ValueType.DATE)) {
			pstmt.setTimestamp(index, ValueUtil.getDateFromValue(value));
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
		} else if(value instanceof Boolean) {
			pstmt.setString(index, ((Boolean) value)? "Y": "N");
		}
	}
	
	/**
	 * Get Report Query from Criteria
	 * @param context
	 * @param criteria
	 * @return
	 */
	private MQuery getReportQueryFromCriteria(Properties context, Criteria criteria) {
		MTable table = MTable.get(context, criteria.getTableName());
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
	 * Get Where Clause from criteria and dynamic condition
	 * @param criteria
	 * @param params
	 * @return
	 */
	private String getWhereClauseFromCriteria(Criteria criteria, List<Object> params) {
		StringBuffer whereClause = new StringBuffer();
		criteria.getConditionsList().stream()
			.filter(condition -> !Util.isEmpty(condition.getColumnName()))
			.forEach(condition -> {
				if(whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				String colummName = criteria.getTableName() + "." + condition.getColumnName(); 
				//	Open
				whereClause.append("(");
				if(condition.getOperatorValue() == Operator.LIKE_VALUE
						|| condition.getOperatorValue() == Operator.NOT_LIKE_VALUE) {
					colummName = "UPPER(" + colummName + ")";
				}
				//	Add operator
				whereClause.append(colummName).append(convertOperator(condition.getOperatorValue()));
				//	For in or not in
				if(condition.getOperatorValue() == Operator.IN_VALUE
						|| condition.getOperatorValue() == Operator.NOT_IN_VALUE) {
					StringBuffer parameter = new StringBuffer();
					condition.getValuesList().forEach(value -> {
						if(parameter.length() > 0) {
							parameter.append(", ");
						}
						parameter.append("?");
						params.add(ValueUtil.getObjectFromValue(value));
					});
					whereClause.append("(").append(parameter).append(")");
				} else if(condition.getOperatorValue() == Operator.BETWEEN_VALUE) {
					whereClause.append(" ? ").append(" AND ").append(" ?");
					params.add(ValueUtil.getObjectFromValue(condition.getValue()));
					params.add(ValueUtil.getObjectFromValue(condition.getValueTo()));
				} else if(condition.getOperatorValue() == Operator.LIKE_VALUE
						|| condition.getOperatorValue() == Operator.NOT_LIKE_VALUE) {
					whereClause.append("?");
					params.add(ValueUtil.getObjectFromValue(condition.getValue(), true));
				} else if(condition.getOperatorValue() != Operator.NULL_VALUE
						&& condition.getOperatorValue() != Operator.NOT_NULL_VALUE) {
					whereClause.append("?");
					params.add(ValueUtil.getObjectFromValue(condition.getValue()));
				}
				//	Close
				whereClause.append(")");
		});
		//	Return where clause
		return whereClause.toString();
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
		criteria.getValuesList().forEach(value -> params.add(ValueUtil.getObjectFromValue(value)));
		//	For dynamic condition
		String dynamicWhere = getWhereClauseFromCriteria(criteria, params);
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
		int pageNumber = getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * PAGE_SIZE;
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
				Entity.Builder valueObject = convertEntity(context, entity);
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
			nexPageToken = getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
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
				setParameterFromObject(pstmt, value, parameterIndex.getAndIncrement());
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
	 * Get Where clause for Smart Browse
	 * @param browser
	 * @param parsedWhereClause
	 * @param values
	 * @return
	 */
	private String getBrowserWhereClause(MBrowse browser, String parsedWhereClause, HashMap<String, Object> parameterMap, List<Object> values) {
		StringBuilder browserWhereClause = new StringBuilder();
		List<MBrowseField> fields = ASPUtil.getInstance().getBrowseFields(browser.getAD_Browse_ID());
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
	private ListBrowserItemsResponse.Builder convertBrowserList(Properties context, ListBrowserItemsRequest request) {
		ListBrowserItemsResponse.Builder builder = ListBrowserItemsResponse.newBuilder();
		MBrowse browser = getBrowser(context, request.getUuid());
		if(browser == null) {
			return builder;
		}
		Criteria criteria = request.getCriteria();
		HashMap<String, Object> parameterMap = new HashMap<>();
		//	Populate map
		request.getParametersList().forEach(parameter -> parameterMap.put(parameter.getKey(), ValueUtil.getObjectFromValue(parameter.getValue())));
		List<Object> values = new ArrayList<Object>();
		String whereClause = getBrowserWhereClause(browser, criteria.getWhereClause(), parameterMap, values);
		//	Page prefix
		int page = getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		StringBuilder sql = new StringBuilder(criteria.getQuery());
		if (!Util.isEmpty(whereClause)) {
			sql.append(" WHERE ").append(whereClause); // includes first AND
		}
		MView view = browser.getAD_View();
		MViewDefinition parentDefinition = view.getParentViewDefinition();
		String tableNameAlias = parentDefinition.getTableAlias();
		String tableName = parentDefinition.getAD_Table().getTableName();
		//	
		String parsedSQL = MRole.getDefault().addAccessSQL(sql.toString(),
				tableNameAlias, MRole.SQL_FULLYQUALIFIED,
				MRole.SQL_RO);
		String orderByClause = criteria.getOrderByClause();
		if(Util.isEmpty(orderByClause)) {
			orderByClause = "";
		} else {
			orderByClause = " ORDER BY " + orderByClause;
		}
		//	Count records
		int count = countRecords(context, parsedSQL, tableName, values);
		String nexPageToken = null;
		int pageMultiplier = page == 0? 1: page;
		if(count > (PAGE_SIZE * pageMultiplier)) {
			nexPageToken = getPagePrefix(request.getClientRequest().getSessionUuid()) + (page + 1);
		}
		//	Add Row Number
		if(!Util.isEmpty(whereClause)) {
			parsedSQL = parsedSQL + " AND ROWNUM >= " + page + " AND ROWNUM <= " + PAGE_SIZE;
		} else {
			parsedSQL = parsedSQL + " WHERE ROWNUM >= " + page + " AND ROWNUM <= " + PAGE_SIZE;	
		}
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
	
	/**
	 * Get Page Number
	 * @param sessionUuid
	 * @param pageToken
	 * @return
	 */
	private int getPageNumber(String sessionUuid, String pageToken) {
		int page = 0;
		String pagePrefix = getPagePrefix(sessionUuid);
		if(!Util.isEmpty(pageToken)) {
			if(pageToken.startsWith(pagePrefix)) {
				try {
					page = Integer.parseInt(pageToken.replace(pagePrefix, ""));
				} catch (Exception e) {
					log.severe(e.getMessage());
				}
			}
		}
		//	
		return page;
	}
	
	/**
	 * Get Page Prefix
	 * @param sessionUuid
	 * @return
	 */
	private String getPagePrefix(String sessionUuid) {
		return sessionUuid + "-";
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
				setParameterFromObject(pstmt, value, parameterIndex.getAndIncrement());
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
	private MBrowse getBrowser(Properties context, String uuid) {
		String key = uuid + "|" + Env.getAD_Language(context);
		MBrowse browser = browserRequested.get(key);
		if(browser == null) {
			browser = new Query(context, I_AD_Browse.Table_Name, I_AD_Process.COLUMNNAME_UUID + " = ?", null)
					.setParameters(uuid)
					.setOnlyActiveRecords(true)
					.first();
			browser = ASPUtil.getInstance(context).getBrowse(browser.getAD_Browse_ID());
		}
		//	Put on Cache
		if(browser != null) {
			browserRequested.put(key, browser);
		}
		//	
		return browser;
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
	 * Convert document actions
	 * @param context
	 * @param request
	 * @return
	 */
	private ListDocumentActionsResponse.Builder convertDocumentActions(Properties context, ListDocumentActionsRequest request) {
		PO entity = getEntity(context, request.getTableName(), request.getRecordUuid(), request.getRecordId());
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
					DocumentAction.Builder documentActionBuilder = DocumentAction.newBuilder();
					documentActionBuilder.setValue(ValueUtil.validateNull(valueList.get(i)));
					documentActionBuilder.setName(ValueUtil.validateNull(nameList.get(i)));
					documentActionBuilder.setDescription(ValueUtil.validateNull(descriptionList.get(i)));
					builder.addDocumentActions(documentActionBuilder);
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
			DocumentAction.Builder documentActionBuilder = DocumentAction.newBuilder();
			documentActionBuilder.setValue(ValueUtil.validateNull(defaultValue));
			documentActionBuilder.setName(ValueUtil.validateNull(defaultName));
			documentActionBuilder.setDescription(ValueUtil.validateNull(defaultDescription));
			builder.setDefaultDocumentAction(documentActionBuilder);
		}
		//	Add record count
		builder.setRecordCount(builder.getDocumentActionsCount());
		//	Return
		return builder;
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
		PO entity = getEntity(context, request.getTableName(), request.getRecordUuid(), request.getRecordId());
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
		int pageNumber = getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * PAGE_SIZE;
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
			nexPageToken = getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
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
		int pageNumber = getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * PAGE_SIZE;
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
			nexPageToken = getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
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
		int pageNumber = getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * PAGE_SIZE;
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
			nexPageToken = getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
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
		int pageNumber = getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * PAGE_SIZE;
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
			nexPageToken = getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
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
		int pageNumber = getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * PAGE_SIZE;
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
			nexPageToken = getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
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
					|| (DisplayType.Button == column.getAD_Reference_ID()
							&& column.getAD_Reference_Value_ID() != 0)) {
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
	 * Run callout with data from server
	 * @param request
	 * @return
	 */
	private org.spin.grpc.util.Callout.Builder runcallout(Properties context, RunCalloutRequest request) {
		org.spin.grpc.util.Callout.Builder calloutBuilder = org.spin.grpc.util.Callout.newBuilder();
		MTab tab = tabRequested.get(request.getTabUuid());
		if(tab == null) {
			tab = new Query(context, I_AD_Tab.Table_Name, I_AD_Tab.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getTabUuid())
					.first();
		}
		if(tab == null) {
			return calloutBuilder;
		}
		//	
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
		GridWindowVO gridWindowVo = GridWindowVO.create(context, windowNo, tab.getAD_Window_ID());
		GridWindow gridWindow = new GridWindow(gridWindowVo, true);
		GridTabVO gridTabVo = GridTabVO.create(gridWindowVo, tabNo, tab, false, true);
		GridFieldVO gridFieldVo = GridFieldVO.create(context, windowNo, tabNo, tab.getAD_Window_ID(), tab.getAD_Tab_ID(), false, field);
		GridField gridField = new GridField(gridFieldVo);
		GridTab gridTab = new GridTab(gridTabVo, gridWindow, true);
		//	Init tab
		gridTab.query(false);
		gridTab.clearSelection();
		gridTab.dataNew(false);
		//	load values
		Map<String, Object> attributes = ValueUtil.convertValuesToObjects(request.getAttributesList());
		for(Entry<String, Object> attribute : attributes.entrySet()) {
			gridTab.setValue(attribute.getKey(), attribute.getValue());
		}
		//	Load value for field
		gridField.setValue(ValueUtil.getObjectFromValue(request.getOldValue()), false);
		gridField.setValue(ValueUtil.getObjectFromValue(request.getValue()), false);
		//	Run it
		String result = processCallout(context, windowNo, gridTab, gridField);
		Arrays.asList(gridTab.getFields()).stream().filter(fieldValue -> isValidChange(fieldValue))
		.forEach(fieldValue -> calloutBuilder.putValues(fieldValue.getColumnName(), ValueUtil.getValueFromObject(fieldValue.getValue()).build()));
		calloutBuilder.setResult(ValueUtil.validateNull(result));
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
	private String processCallout (Properties context, int windowNo, GridTab gridTab, GridField field) {
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
	private Entity.Builder convertEntity(Properties context, PO entity) {
		Entity.Builder builder = Entity.newBuilder();
		if(entity == null) {
			return builder;
		}
		builder.setUuid(ValueUtil.validateNull(entity.get_ValueAsString(I_AD_Element.COLUMNNAME_UUID)))
			.setId(entity.get_ID());
		//	Convert attributes
		POInfo poInfo = POInfo.getPOInfo(context, entity.get_Table_ID());
		for(int index = 0; index < poInfo.getColumnCount(); index++) {
			String columnName = poInfo.getColumnName(index);
			int referenceId = poInfo.getColumnDisplayType(index);
			Object value = entity.get_Value(index);
			if(value == null) {
				continue;
			}
			Value.Builder builderValue = ValueUtil.getValueFromReference(value, referenceId);
			if(builderValue == null) {
				continue;
			}
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
}

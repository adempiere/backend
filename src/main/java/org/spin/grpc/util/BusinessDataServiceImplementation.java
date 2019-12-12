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
import org.compiere.model.I_AD_Menu;
import org.compiere.model.I_AD_PInstance;
import org.compiere.model.I_AD_PInstance_Log;
import org.compiere.model.I_AD_PrintFormat;
import org.compiere.model.I_AD_Private_Access;
import org.compiere.model.I_AD_Process;
import org.compiere.model.I_AD_ReportView;
import org.compiere.model.I_AD_Role;
import org.compiere.model.I_AD_Session;
import org.compiere.model.I_AD_Tab;
import org.compiere.model.I_AD_TreeNodeMM;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_AD_Window;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.MChangeLog;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MForm;
import org.compiere.model.MMenu;
import org.compiere.model.MMessage;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MPrivateAccess;
import org.compiere.model.MProcess;
import org.compiere.model.MQuery;
import org.compiere.model.MRecentItem;
import org.compiere.model.MReportView;
import org.compiere.model.MRole;
import org.compiere.model.MRule;
import org.compiere.model.MSession;
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
import org.compiere.process.ProcessInfo;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.MimeType;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.eevolution.service.dsl.ProcessBuilder;
import org.spin.grpc.util.BusinessDataServiceGrpc.BusinessDataServiceImplBase;
import org.spin.grpc.util.Condition.Operator;
import org.spin.grpc.util.RollbackEntityRequest.EventType;
import org.spin.grpc.util.Value.ValueType;
import org.spin.model.I_AD_ContextInfo;
import org.spin.model.MADContextInfo;
import org.spin.util.AbstractExportFormat;
import org.spin.util.ReportExportHandler;

import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BusinessDataServiceImplementation extends BusinessDataServiceImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(BusinessDataServiceImplementation.class);
	/**	Key column constant	*/
	private final String KEY_COLUMN_KEY = "KeyColumn";
	/**	Key column constant	*/
	private final String DISPLAY_COLUMN_KEY = "DisplayColumn";
	/**	Key column constant	*/
	private final String VALUE_COLUMN_KEY = "ValueColumn";
	/**	Session Context	*/
	private static CCache<String, Properties> sessionsContext = new CCache<String, Properties>("DataServiceImplementation", 30, 0);	//	no time-out
	/**	Browse Requested	*/
	private static CCache<String, MBrowse> browserRequested = new CCache<String, MBrowse>(I_AD_Browse.Table_Name + "_UUID", 30, 0);	//	no time-out
	/**	window Requested	*/
	private static CCache<String, MTab> tabRequested = new CCache<String, MTab>(I_AD_Tab.Table_Name + "_UUID", 30, 0);	//	no time-out
	/**	Language */
	private static CCache<String, String> languageCache = new CCache<String, String>("Language_ISO_Code", 30, 0);	//	no time-out
	/**	Reference cache	*/
	private static CCache<String, String> referenceWhereClauseCache = new CCache<String, String>("Reference_WhereClause", 30, 0);	//	no time-out
	/**	Window emulation	*/
	private AtomicInteger windowNoEmulation = new AtomicInteger(1);
	/**	Page Size	*/
	private final int PAGE_SIZE = 50;
	
	@Override
	public void getEntity(GetEntityRequest request, StreamObserver<Entity> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
	public void runBusinessProcess(RunBusinessProcessRequest request, StreamObserver<BusinessProcess> responseObserver) {
		try {
			if(request == null
					|| Util.isEmpty(request.getUuid())) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Lookup List Requested = " + request.getUuid());
			Properties context = getContext(request.getClientRequest());
			String language = getDefaultLanguage(request.getClientRequest().getLanguage());
			BusinessProcess.Builder processReponse = runProcess(context, request, language);
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
			Properties context = getContext(request.getClientRequest());
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
	public void listActivities(ListActivitiesRequest request, StreamObserver<ListActivitiesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Process Activity Requested is Null");
			}
			log.fine("Object List Requested = " + request);
			Properties context = getContext(request.getClientRequest());
			ListActivitiesResponse.Builder entityValueList = convertProcessActivity(context, request);
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
	public void listPrintFormats(ListPrintFormatsRequest request, StreamObserver<ListPrintFormatsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
			Properties context = getContext(request.getClientRequest());
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
	
	/**
	 * Ger Report Query from Criteria
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
				query.addRestriction(columnName, operator, getValueFromType(condition.getValue(), true));
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
					Object convertedValue = getValueFromType(value);
					if(convertedValue instanceof String) {
						convertedValue = "'" + convertedValue + "'";
					}
					parameter.append(convertedValue);
				});
				whereClause.append("(").append(parameter).append(")");
				query.addRestriction(whereClause.toString());
			} else if(condition.getOperatorValue() == Operator.BETWEEN_VALUE) {
				query.addRangeRestriction(columnName, getValueFromType(condition.getValue()), getValueFromType(condition.getValueTo()));
			} else {
				query.addRestriction(columnName, operator, getValueFromType(condition.getValue()));
			}
		});
		return query;
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
			builder.setFileName(validateNull(validFileName));
			builder.setName(validateNull(reportEngine.getName()));
			builder.setMimeType(validateNull(MimeType.getMimeType(validFileName)));
			String headerName = Msg.getMsg(context, "Report") + ": " + reportEngine.getName() + "  " + Env.getHeader(context, 0);
			builder.setHeaderName(validateNull(headerName));
			StringBuffer footerName = new StringBuffer ();
			footerName.append(Msg.getMsg(context, "DataCols")).append("=")
				.append(reportEngine.getColumnCount())
				.append(", ").append(Msg.getMsg(context, "DataRows")).append("=")
				.append(reportEngine.getRowCount());
			builder.setFooterName(validateNull(footerName.toString()));
			//	Type
			builder.setReportType(request.getReportType());
			ByteString resultFile = ByteString.readFrom(new FileInputStream(reportFile));
			if(request.getReportType().endsWith("html")
					|| request.getReportType().endsWith("txt")) {
				builder.setOutputBytes(resultFile);
			}
			builder.setReportViewUuid(validateNull(reportView.getUUID()));
			builder.setPrintFormatUuid(validateNull(printFormat.getUUID()));
			builder.setTableName(validateNull(printFormat.getAD_Table().getTableName()));
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
					recordReferenceBuilder.setWindowUuid(validateNull(referenceWindow.get_UUID()));
					recordReferenceBuilder.setTableName(validateNull(zoomInfo.query.getZoomTableName()));
					recordReferenceBuilder.setWhereClause(validateNull(zoomInfo.query.getWhereClause()));
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
			pendingDocument.setDocumentName(validateNull(documentStatus.getName()));
			// for Reference
			if(documentStatus.getAD_Window_ID() != 0) {
				MWindow window = MWindow.get(context, documentStatus.getAD_Window_ID());
				pendingDocument.setWindowUuid(validateNull(window.getUUID()));
			} else if(documentStatus.getAD_Form_ID() != 0) {
				MForm form = new MForm(context, documentStatus.getAD_Form_ID(), null);
				pendingDocument.setFormUuid(validateNull(form.getUUID()));
			}
			//	Criteria
			MTable table = MTable.get(context, documentStatus.getAD_Table_ID());
			pendingDocument.setCriteria(Criteria.newBuilder()
					.setTableName(validateNull(table.getTableName()))
					.setWhereClause(validateNull(documentStatus.getWhereClause())));
			//	Set quantity
			pendingDocument.setRecordCount(MDocumentStatus.evaluate(documentStatus));
			//	TODO: Add description for interface
			builder.addPendingDocuments(pendingDocument);
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
				reportViewBuilder.setUuid(validateNull(reportViewReference.getUUID()));
				reportViewBuilder.setName(validateNull(name));
				reportViewBuilder.setDescription(validateNull(description));
				MTable table = MTable.get(context, reportViewReference.getAD_Table_ID());
				reportViewBuilder.setTableName(validateNull(table.getTableName()));
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
					drillTable.setTableName(validateNull(drillTableName));
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
					drillTable.setPrintName(validateNull(name));
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
			parameters.add(table);
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
				printFormatBuilder.setUuid(validateNull(printFormatReference.getUUID()));
				printFormatBuilder.setName(validateNull(printFormatReference.getName()));
				printFormatBuilder.setDescription(validateNull(printFormatReference.getDescription()));
				printFormatBuilder.setIsDefault(printFormatReference.isDefault());
				MTable table = MTable.get(context, printFormatReference.getAD_Table_ID());
				printFormatBuilder.setTableName(validateNull(table.getTableName()));
				if(printFormatReference.getAD_ReportView_ID() != 0) {
					MReportView reportView = MReportView.get(context, printFormatReference.getAD_ReportView_ID());
					printFormatBuilder.setReportViewUuid(validateNull(reportView.getUUID()));
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
				favorite.setMenuUuid(validateNull(menu.getUUID()));
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
				//	Set reference
				favorite.setAction(validateNull(MMenu.ACTION_Window));
				//	Supported actions
				if(!Util.isEmpty(menu.getAction())) {
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
					favorite.setReferenceUuid(validateNull(referenceUuid));
				}
				//	Set name and description
				favorite.setMenuName(validateNull(menuName));
				favorite.setMenuDescription(validateNull(menuDescription));
				builder.addFavorites(favorite);
			});
		//	Return
		return builder;
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
	private BusinessProcess.Builder runProcess(Properties context, RunBusinessProcessRequest request, String language) throws FileNotFoundException, IOException {
		BusinessProcess.Builder response = BusinessProcess.newBuilder();
		//	Get Process definition
		MProcess process = getProcess(context, request.getUuid(), language);
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
		if(recordId == 0
				&& !Util.isEmpty(request.getUuid())
				&& !Util.isEmpty(request.getTableName())) {
			PO entity = getEntity(context, request.getTableName(), request.getUuid(), request.getRecordId());
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
				.withBatchMode()
				.withoutPrintPreview()
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
					selection.put(selectionKey.getSelectionId(), new LinkedHashMap<>(convertValues(selectionKey.getValuesList())));
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
		ProcessInfo result = null;
		try {
			result = builder.execute();
		} catch (Exception e) {
			result = builder.getProcessInfo();
		}
		String reportViewUuid = null;
		String printFormatUuid = null;
		String tableName = null;
		//	Get process instance from identifier
		if(result.getAD_PInstance_ID() != 0) {
			MPInstance instance = new Query(context, I_AD_PInstance.Table_Name, I_AD_PInstance.COLUMNNAME_AD_PInstance_ID + " = ?", null)
					.setParameters(result.getAD_PInstance_ID())
					.first();
			response.setInstanceUuid(validateNull(instance.getUUID()));
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
				if(printFormatId != 0) {
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
		response.setResultTableName(validateNull(result.getResultTableName()));
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
				output.setFileName(validateNull(validFileName));
				output.setName(result.getTitle());
				output.setMimeType(validateNull(MimeType.getMimeType(validFileName)));
				output.setDescription(validateNull(process.getDescription()));
				//	Type
				output.setReportType(request.getReportType());
				ByteString resultFile = ByteString.readFrom(new FileInputStream(reportFile));
				if(request.getReportType().endsWith("html")
						|| request.getReportType().endsWith("txt")) {
					output.setOutputBytes(resultFile);
				}
				output.setOutputStream(resultFile);
				output.setReportViewUuid(validateNull(reportViewUuid));
				output.setPrintFormatUuid(validateNull(printFormatUuid));
				output.setTableName(validateNull(tableName));
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
		processLog.setLog(validateNull(Msg.parseTranslation(Env.getCtx(), log.getP_Msg())));
		return processLog;
	}
	
	/**
	 * Convert Selection values from gRPC to ADempiere values
	 * @param values
	 * @return
	 */
	private Map<String, Object> convertValues(List<KeyValue> values) {
		Map<String, Object> convertedValues = new HashMap<>();
		for(KeyValue value : values) {
			convertedValues.put(value.getKey(), getValueFromType(value.getValue()));
		}
		//	
		return convertedValues;
	}
	
	/**
	 * Default get value from type
	 * @param valueToConvert
	 * @return
	 */
	private Object getValueFromType(Value valueToConvert) {
		return getValueFromType(valueToConvert, false);
	}
	
	/**
	 * Get value from parameter type
	 * @param valueToConvert
	 * @return
	 */
	private Object getValueFromType(Value valueToConvert, boolean uppercase) {
		Object value = null;
		if(valueToConvert.getValueType().equals(ValueType.BOOLEAN)) {
			value = valueToConvert.getBooleanValue();
		} else if(valueToConvert.getValueType().equals(ValueType.DOUBLE)
				|| valueToConvert.getValueType().equals(ValueType.LONG)) {
			value = new BigDecimal(valueToConvert.getDoubleValue());
		} else if(valueToConvert.getValueType().equals(ValueType.INTEGER)) {
			value = valueToConvert.getIntValue();
		} else if(valueToConvert.getValueType().equals(ValueType.STRING)) {
			String stringValue = valueToConvert.getStringValue();
			if(Util.isEmpty(stringValue)) {
				stringValue = null;
			}
			//	To Upper case
			if(uppercase) {
				stringValue = stringValue.toUpperCase();
			}
			value = stringValue;
		} else if(valueToConvert.getValueType().equals(ValueType.DATE)) {
			if(valueToConvert.getLongValue() > 0) {
				value = new Timestamp(valueToConvert.getLongValue());
			}
		}
		return value;
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
		Map<String, Object> attributes = convertValues(request.getAttributesList());
		for(Entry<String, Object> attribute : attributes.entrySet()) {
			entity.set_ValueOfColumn(attribute.getKey(), attribute.getValue());
		}
		//	Save entity
		entity.saveEx();
		//	Return
		return convertEntity(context, entity);
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
			Map<String, Object> attributes = convertValues(request.getAttributesList());
			for(Entry<String, Object> attribute : attributes.entrySet()) {
				entity.set_ValueOfColumn(attribute.getKey(), attribute.getValue());
			}
			//	Save entity
			entity.saveEx();
		}
		//	Return
		return convertEntity(context, entity);
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
	 * Get context from session
	 * @param request
	 * @return
	 */
	private Properties getContext(ClientRequest request) {
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
				builder = getKeyValueFromValue(rs.getObject(1));
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
					builder.setMessageText(validateNull(messageText));
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
		builder.setUserUuid(validateNull(user.getUUID()));
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
		} else if(value instanceof Boolean) {
			pstmt.setString(index, ((Boolean) value)? "Y": "N");
		}
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
						params.add(getValueFromType(value));
					});
					whereClause.append("(").append(parameter).append(")");
				} else if(condition.getOperatorValue() == Operator.BETWEEN_VALUE) {
					whereClause.append(" ? ").append(" AND ").append(" ?");
					params.add(getValueFromType(condition.getValue()));
					params.add(getValueFromType(condition.getValueTo()));
				} else if(condition.getOperatorValue() == Operator.LIKE_VALUE
						|| condition.getOperatorValue() == Operator.NOT_LIKE_VALUE) {
					whereClause.append("?");
					params.add(getValueFromType(condition.getValue(), true));
				} else {
					whereClause.append("?");
					params.add(getValueFromType(condition.getValue()));
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
		criteria.getValuesList().forEach(value -> {
			params.add(getValueFromType(value));
		});
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
		builder.setNextPageToken(validateNull(nexPageToken));
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
						boolean isFilled = false;
						//	Display Columns
						if(field == null) {
							String value = rs.getString(index);
							if(!Util.isEmpty(value)) {
								isFilled = true;
								valueBuilder.setStringValue(value);
								valueBuilder.setValueType(ValueType.STRING);
								valueObjectBuilder.putValues(columnName, valueBuilder.build());
							}
							continue;
						}
						//	From field
						String fieldColumnName = field.getColumnName();
						if(isLookup(field.getAD_Reference_ID())
								|| DisplayType.isID(field.getAD_Reference_ID())) {
							isFilled = true;
							int type = metaData.getColumnType(index);
							if(type == Types.DECIMAL
									|| type == Types.NUMERIC
									|| type == Types.FLOAT
									|| type == Types.DOUBLE) {
								valueBuilder.setIntValue(rs.getInt(index));
								valueBuilder.setValueType(ValueType.INTEGER);
							} else {
								valueBuilder.setStringValue(rs.getString(index));
								valueBuilder.setValueType(ValueType.STRING);
							}
						} else if(DisplayType.isNumeric(field.getAD_Reference_ID())) {
							BigDecimal value = rs.getBigDecimal(index);
							if(value != null) {
								isFilled = true;
								valueBuilder.setDoubleValue(value.doubleValue());
								valueBuilder.setValueType(ValueType.DOUBLE);
							}
						} else if(DisplayType.YesNo == field.getAD_Reference_ID()) {
							isFilled = true;
							String value = rs.getString(index);
							valueBuilder.setBooleanValue(!Util.isEmpty(value) && value.equals("Y"));
							valueBuilder.setValueType(ValueType.BOOLEAN);
						} else if(DisplayType.isDate(field.getAD_Reference_ID())) {
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
	private ListBrowserItemsResponse.Builder convertBrowserList(Properties context, ListBrowserItemsRequest request) {
		ListBrowserItemsResponse.Builder builder = ListBrowserItemsResponse.newBuilder();
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
		builder.setNextPageToken(validateNull(nexPageToken));
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
				Entity.Builder valueObjectBuilder = Entity.newBuilder();
				ResultSetMetaData metaData = rs.getMetaData();
				for (int index = 1; index <= metaData.getColumnCount(); index++) {
					try {
						String columnName = metaData.getColumnName (index);
						MBrowseField field = fieldsMap.get(columnName.toUpperCase());
						Value.Builder valueBuilder = Value.newBuilder();
						boolean isFilled = false;
						//	Display Columns
						if(field == null) {
							String value = rs.getString(index);
							if(!Util.isEmpty(value)) {
								isFilled = true;
								valueBuilder.setStringValue(value);
								valueBuilder.setValueType(ValueType.STRING);
								valueObjectBuilder.putValues(columnName, valueBuilder.build());
							}
							continue;
						}
						//	From field
						String fieldColumnName = field.getAD_View_Column().getColumnName();
						if(isLookup(field.getAD_Reference_ID())
								|| DisplayType.isID(field.getAD_Reference_ID())) {
							isFilled = true;
							if(metaData.getColumnType(index) != Types.DECIMAL) {
								valueBuilder.setStringValue(rs.getString(index));
								valueBuilder.setValueType(ValueType.STRING);
							} else {
								valueBuilder.setIntValue(rs.getInt(index));
								valueBuilder.setValueType(ValueType.INTEGER);
							}
						} else if(DisplayType.isNumeric(field.getAD_Reference_ID())) {
							BigDecimal value = rs.getBigDecimal(index);
							if(value != null) {
								isFilled = true;
								valueBuilder.setDoubleValue(value.doubleValue());
								valueBuilder.setValueType(ValueType.DOUBLE);
							}
						} else if(DisplayType.YesNo == field.getAD_Reference_ID()) {
							isFilled = true;
							String value = rs.getString(index);
							valueBuilder.setBooleanValue(!Util.isEmpty(value) && value.equals("Y"));
							valueBuilder.setValueType(ValueType.BOOLEAN);
						} else if(DisplayType.isDate(field.getAD_Reference_ID())) {
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
	private ListActivitiesResponse.Builder convertProcessActivity(Properties context, ListActivitiesRequest request) {
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
			sql = "EXISTS(SELECT 1 FROM AD_Session WHERE UUID = ? AND AD_Session_ID = AD_PInstance.AD_Session_ID)";
		}
		List<MPInstance> processInstanceList = new Query(context, I_AD_PInstance.Table_Name, 
				sql, null)
				.setParameters(uuid)
				.setOrderBy(I_AD_PInstance.COLUMNNAME_Created + " DESC")
				.<MPInstance>list();
		//	
		ListActivitiesResponse.Builder builder = ListActivitiesResponse.newBuilder();
		//	Convert Process Instance
		for(MPInstance processInstance : processInstanceList) {
			BusinessProcess.Builder valueObject = convertProcessInstance(processInstance);
			builder.addResponses(valueObject.build());
		}
		//	Return
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
							.setDisplayName(validateNull(recentItem.getLabel()));
					String menuName = "";
					String menuDescription = "";
					String referenceUuid = null;
					if(recentItem.getAD_Tab_ID() > 0) {
						MTab tab = MTab.get(context, recentItem.getAD_Tab_ID());
						recentItemBuilder.setTabUuid(validateNull(tab.getUUID()));
						menuName = tab.getName();
						menuDescription = tab.getDescription();
						if(!Env.isBaseLanguage(context, "")) {
							menuName = tab.get_Translation("Name");
							menuDescription = tab.get_Translation("Description");
						}
						//	Add Action
						recentItemBuilder.setAction(validateNull(MMenu.ACTION_Window));
					}
					if(recentItem.getAD_Window_ID() > 0) {
						MWindow window = MWindow.get(context, recentItem.getAD_Window_ID());
						recentItemBuilder.setWindowUuid(validateNull(window.getUUID()));
						menuName = window.getName();
						menuDescription = window.getDescription();
						referenceUuid = window.getUUID();
						if(!Env.isBaseLanguage(context, "")) {
							menuName = window.get_Translation("Name");
							menuDescription = window.get_Translation("Description");
						}
						//	Add Action
						recentItemBuilder.setAction(validateNull(MMenu.ACTION_Window));
					}
					if(recentItem.getAD_Menu_ID() > 0) {
						MMenu menu = MMenu.getFromId(context, recentItem.getAD_Menu_ID());
						recentItemBuilder.setMenuUuid(validateNull(menu.getUUID()));
						if(!menu.isCentrallyMaintained()) {
							menuName = menu.getName();
							menuDescription = menu.getDescription();
							if(!Env.isBaseLanguage(context, "")) {
								menuName = menu.get_Translation("Name");
								menuDescription = menu.get_Translation("Description");
							}
						}
						//	Add Action
						recentItemBuilder.setAction(validateNull(menu.getAction()));
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
					recentItemBuilder.setMenuName(validateNull(menuName));
					recentItemBuilder.setMenuDescription(validateNull(menuDescription));
					recentItemBuilder.setUpdated(recentItem.getUpdated().getTime());
					recentItemBuilder.setReferenceUuid(validateNull(referenceUuid));
					//	For uuid
					if(recentItem.getAD_Table_ID() != 0
							&& recentItem.getRecord_ID() != 0) {
						MTable table = MTable.get(context, recentItem.getAD_Table_ID());
						if(table != null
								&& table.getAD_Table_ID() != 0) {
							recentItemBuilder.setRecordUuid(validateNull(table.getPO(recentItem.getRecord_ID(), null).get_UUID()));
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
	private BusinessProcess.Builder convertProcessInstance(MPInstance instance) {
		BusinessProcess.Builder builder = BusinessProcess.newBuilder();
		builder.setInstanceUuid(validateNull(instance.getUUID()));
		builder.setIsError(!instance.isOK());
		builder.setIsProcessing(instance.isProcessing());
		builder.setLastRun(instance.getUpdated().getTime());
		String summary = instance.getErrorMsg();
		if(!Util.isEmpty(summary)) {
			summary = Msg.parseTranslation(Env.getCtx(), summary);
		}
		//	for report
		MProcess process = MProcess.get(Env.getCtx(), instance.getAD_Process_ID());
		builder.setUuid(validateNull(process.getUUID()));
		if(process.isReport()) {
			ReportOutput.Builder outputBuilder = ReportOutput.newBuilder();
			outputBuilder.setReportType(validateNull(instance.getReportType()));
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
		Map<String, Object> attributes = convertValues(request.getAttributesList());
		for(Entry<String, Object> attribute : attributes.entrySet()) {
			gridTab.setValue(attribute.getKey(), attribute.getValue());
		}
		//	Load value for field
		gridField.setValue(getValueFromType(request.getOldValue()), false);
		gridField.setValue(getValueFromType(request.getValue()), false);
		//	Run it
		String result = processCallout(context, windowNo, gridTab, gridField);
		Arrays.asList(gridTab.getFields()).stream().filter(fieldValue -> isValidChange(fieldValue))
		.forEach(fieldValue -> calloutBuilder.putValues(fieldValue.getColumnName(), getKeyValueFromValue(fieldValue.getValue()).build()));
		calloutBuilder.setResult(validateNull(result));
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
			//	Add
			builder.putValues(columnName, builderValue.build());
		}
		//	
		return builder;
	}
	
	/**
	 * Get Value 
	 * @param value
	 * @return
	 */
	private Value.Builder getKeyValueFromValue(Object value) {
		Value.Builder builderValue = Value.newBuilder();
		if(value == null) {
			return builderValue;
		}
		//	Validate value
		if(value instanceof BigDecimal) {
			BigDecimal bigdecimalValue = (BigDecimal) value;
			builderValue.setValueType(ValueType.DOUBLE);
			builderValue.setDoubleValue(bigdecimalValue.doubleValue());
		} else if (value instanceof Integer) {
			builderValue.setValueType(ValueType.INTEGER);
			builderValue.setIntValue((Integer)value);
		} else if (value instanceof String) {
			builderValue.setValueType(ValueType.STRING);
			builderValue.setStringValue(validateNull((String)value));
		} else if (value instanceof Boolean) {
			builderValue.setValueType(ValueType.BOOLEAN);
			builderValue.setBooleanValue((Boolean) value);
		} else if(value instanceof Timestamp) {
			builderValue.setValueType(ValueType.DATE);
			Timestamp date = (Timestamp) value;
			builderValue.setLongValue(date.getTime());
		}
		//	
		return builderValue;
	}
	
	/**
	 * Is lookup include location
	 * @param displayType
	 * @return
	 */
	private boolean isLookup(int displayType) {
		return DisplayType.isLookup(displayType)
				|| DisplayType.Account == displayType
				|| DisplayType.Location == displayType
				|| DisplayType.Locator == displayType
				|| DisplayType.PAttribute == displayType;
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

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

import java.util.Arrays;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.MBrowse;
import org.adempiere.model.MDocumentStatus;
import org.compiere.model.I_AD_Role;
import org.compiere.model.I_AD_TreeNodeMM;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_PA_DashboardContent;
import org.compiere.model.MDashboardContent;
import org.compiere.model.MForm;
import org.compiere.model.MMenu;
import org.compiere.model.MProcess;
import org.compiere.model.MTable;
import org.compiere.model.MWindow;
import org.compiere.model.Query;
import org.compiere.model.X_AD_TreeNodeMM;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.grpc.util.DashboardingGrpc.DashboardingImplBase;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * https://itnext.io/customizing-grpc-generated-code-5909a2551ca1
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Business data service
 */
public class DashboardingServiceImplementation extends DashboardingImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(DashboardingServiceImplementation.class);
	@Override
	public void listPendingDocuments(ListPendingDocumentsRequest request, StreamObserver<ListPendingDocumentsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListPendingDocumentsResponse.Builder pendingDocumentsList = convertPendingDocumentList(context, request);
			responseObserver.onNext(pendingDocumentsList.build());
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
	public void listFavorites(ListFavoritesRequest request, StreamObserver<ListFavoritesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListFavoritesResponse.Builder favoritesList = convertFavoritesList(context, request);
			responseObserver.onNext(favoritesList.build());
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
	public void listDashboards(ListDashboardsRequest request, StreamObserver<ListDashboardsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListDashboardsResponse.Builder dashboardsList = convertDashboarsList(context, request);
			responseObserver.onNext(dashboardsList.build());
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
	
}

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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.adempiere.apps.graph.GraphColumn;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.MBrowse;
import org.adempiere.model.MDocumentStatus;
import org.compiere.model.I_AD_Role;
import org.compiere.model.I_AD_TreeNodeMM;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_PA_DashboardContent;
import org.compiere.model.I_PA_Goal;
import org.compiere.model.MChart;
import org.compiere.model.MColorSchema;
import org.compiere.model.MDashboardContent;
import org.compiere.model.MForm;
import org.compiere.model.MGoal;
import org.compiere.model.MMeasure;
import org.compiere.model.MMenu;
import org.compiere.model.MProcess;
import org.compiere.model.MTable;
import org.compiere.model.MWindow;
import org.compiere.model.Query;
import org.compiere.model.X_AD_TreeNodeMM;
import org.compiere.print.MPrintColor;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.jfree.data.category.CategoryDataset;
import org.spin.base.util.ContextManager;
import org.spin.base.util.RecordUtil;
import org.spin.base.util.ValueUtil;
import org.spin.grpc.util.Chart;
import org.spin.grpc.util.ChartData;
import org.spin.grpc.util.ChartSerie;
import org.spin.grpc.util.ColorSchema;
import org.spin.grpc.util.Criteria;
import org.spin.grpc.util.Dashboard;
import org.spin.grpc.util.DashboardingGrpc.DashboardingImplBase;
import org.spin.grpc.util.Favorite;
import org.spin.grpc.util.GetChartRequest;
import org.spin.grpc.util.ListDashboardsRequest;
import org.spin.grpc.util.ListDashboardsResponse;
import org.spin.grpc.util.ListFavoritesRequest;
import org.spin.grpc.util.ListFavoritesResponse;
import org.spin.grpc.util.ListPendingDocumentsRequest;
import org.spin.grpc.util.ListPendingDocumentsResponse;
import org.spin.grpc.util.PendingDocument;

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
	
	@Override
	public void getChart(GetChartRequest request, StreamObserver<Chart> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			Chart.Builder chart = convertChart(request);
			responseObserver.onNext(chart.build());
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
	 * Convert chart and data
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Chart.Builder convertChart(GetChartRequest request) {
		Chart.Builder builder = Chart.newBuilder();
		MGoal goal = (MGoal) RecordUtil.getEntity(Env.getCtx(), I_PA_Goal.Table_Name, request.getUuid(), request.getId(), null);
		if(goal == null) {
			throw new AdempiereException("@PA_Goal_ID@ @NotFound@");
		}
		//	Load
		Map<String, List<ChartData>> chartSeries = new HashMap<>();
		if(goal.get_ValueAsInt("AD_Chart_ID") > 0) {
			MChart chart = new MChart(Env.getCtx(), goal.get_ValueAsInt("AD_Chart_ID"), null);
			CategoryDataset dataSet = chart.getCategoryDataset();
			dataSet.getRowKeys();
			dataSet.getColumnKeys().forEach(column -> {
				dataSet.getRowKeys().forEach(row -> {
					//	Get from map
					List<ChartData> serie = chartSeries.get(row);
					if(serie == null) {
						serie = new ArrayList<>();
					}
					//	Add
					Number value = dataSet.getValue((Comparable<?>)row, (Comparable<?>)column);
					BigDecimal numberValue = (value != null? new BigDecimal(value.doubleValue()): Env.ZERO);
					serie.add(ChartData.newBuilder()
							.setName(column.toString())
							.setValue(ValueUtil.getDecimalFromBigDecimal(numberValue))
							.build());
					chartSeries.put(row.toString(), serie);
				});
			});
		} else {
			MMeasure measure = goal.getMeasure();
			List<GraphColumn> chartData = measure.getGraphColumnList(goal);
			//	Set values
			builder.setName(ValueUtil.validateNull(goal.getName()));
			builder.setDescription(ValueUtil.validateNull(goal.getDescription()));
			builder.setId(goal.getPA_Goal_ID());
			builder.setUuid(ValueUtil.validateNull(goal.getUUID()));
			builder.setXAxisLabel(ValueUtil.validateNull(goal.getXAxisText()));
			builder.setYAxisLabel(ValueUtil.validateNull(goal.getName()));
			chartData.forEach(data -> {
				String key = "";
				if (data.getDate() != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(data.getDate());
					key = Integer.toString(cal.get(Calendar.YEAR));
				}
				//	Get from map
				List<ChartData> serie = chartSeries.get(key);
				if(serie == null) {
					serie = new ArrayList<>();
				}
				//	Add
				serie.add(ChartData.newBuilder()
						.setName(data.getLabel())
						.setValue(ValueUtil.getDecimalFromBigDecimal(new BigDecimal(data.getValue())))
						.build());
				chartSeries.put(key, serie);
			});
		}
		//	Add measure color
		MColorSchema colorSchema = goal.getColorSchema();
		builder.setMeasureTarget(ValueUtil.getDecimalFromBigDecimal(goal.getMeasureTarget()));
		//	Add first mark
		builder.addColorSchemas(ColorSchema.newBuilder()
				.setPercent(ValueUtil.getDecimalFromBigDecimal(new BigDecimal(colorSchema.getMark1Percent())))
				.setColor(getColorAsHex(colorSchema.getAD_PrintColor1_ID())));
		//	Second Mark
		builder.addColorSchemas(ColorSchema.newBuilder()
				.setPercent(ValueUtil.getDecimalFromBigDecimal(new BigDecimal(colorSchema.getMark2Percent())))
				.setColor(getColorAsHex(colorSchema.getAD_PrintColor2_ID())));
		//	Third Mark
		builder.addColorSchemas(ColorSchema.newBuilder()
				.setPercent(ValueUtil.getDecimalFromBigDecimal(new BigDecimal(colorSchema.getMark3Percent())))
				.setColor(getColorAsHex(colorSchema.getAD_PrintColor3_ID())));
		//	Four Mark
		builder.addColorSchemas(ColorSchema.newBuilder()
				.setPercent(ValueUtil.getDecimalFromBigDecimal(new BigDecimal(colorSchema.getMark4Percent())))
				.setColor(getColorAsHex(colorSchema.getAD_PrintColor4_ID())));
		//	Add all
		chartSeries.keySet().stream().sorted().forEach(serie -> {
			builder.addSeries(ChartSerie.newBuilder().setName(serie).addAllDataSet(chartSeries.get(serie)));
		});
		return builder;
	}
	
	/**
	 * Get color as hex
	 * @param printColorId
	 * @return
	 */
	private String getColorAsHex(int printColorId) {
		if(printColorId <= 0) {
			return "";
		}
		MPrintColor printColor = MPrintColor.get(Env.getCtx(), printColorId);
		int color = 0;
		try {
			color = Integer.parseInt(printColor.getCode());
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
		}
		return String.format("#%06X", (0xFFFFFF & color));
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
		if(request.getUserId() <= 0
				&& Util.isEmpty(request.getUserUuid())
				&& request.getRoleId() <= 0
				&& Util.isEmpty(request.getRoleUuid())) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ @NotFound@");
		}
		//	Get user
		int userId = request.getUserId();
		if(userId <= 0) {
			userId = RecordUtil.getIdFromUuid(I_AD_User.Table_Name, request.getUserUuid(), null);
		}
		//	Get role
		int roleId = request.getRoleId();
		if(roleId <= 0) {
			roleId = RecordUtil.getIdFromUuid(I_AD_Role.Table_Name, request.getRoleUuid(), null);
		}
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
		if(request.getRoleId() <= 0
				&& Util.isEmpty(request.getRoleUuid())) {
			throw new AdempiereException("@AD_Role_ID@ @NotFound@");
		}
		//	Get role
		int roleId = request.getRoleId();
		if(roleId <= 0) {
			roleId = RecordUtil.getIdFromUuid(I_AD_Role.Table_Name, request.getRoleUuid(), null);
		}
		//	Get from Charts
		new Query(Env.getCtx(), I_PA_Goal.Table_Name, 
				"((AD_User_ID IS NULL AND AD_Role_ID IS NULL)"
						+ " OR AD_Role_ID=?"	//	#2
						+ " OR EXISTS (SELECT 1 FROM AD_User_Roles ur "
							+ "WHERE ur.AD_User_ID=PA_Goal.AD_User_ID AND ur.AD_Role_ID = ? AND ur.IsActive='Y')) ", null)
			.setParameters(roleId, roleId)
			.setOnlyActiveRecords(true)
			.setClient_ID()
			.setOrderBy(I_PA_Goal.COLUMNNAME_SeqNo)
			.<MGoal>list()
			.forEach(chartDefinition -> {
				Dashboard.Builder dashboardBuilder = Dashboard.newBuilder();
				dashboardBuilder.setId(chartDefinition.getPA_Goal_ID());
				dashboardBuilder.setUuid(ValueUtil.validateNull(chartDefinition.getUUID()));
				dashboardBuilder.setName(ValueUtil.validateNull(chartDefinition.getName()));
				dashboardBuilder.setDescription(ValueUtil.validateNull(chartDefinition.getDescription()));
				dashboardBuilder.setDashboardType("chart");
				dashboardBuilder.setChartType(ValueUtil.validateNull(chartDefinition.getChartType()));
				dashboardBuilder.setIsCollapsible(true);
				dashboardBuilder.setIsOpenByDefault(true);
				//	Add to builder
				builder.addDashboards(dashboardBuilder);
			});
		//	Get from activity
		new Query(context, I_PA_DashboardContent.Table_Name, 
				"EXISTS(SELECT 1 FROM AD_Dashboard_Access da WHERE da.PA_DashboardContent_ID = PA_DashboardContent.PA_DashboardContent_ID AND da.AD_Role_ID = ?)", null)
			.setParameters(roleId)
			.setOnlyActiveRecords(true)
			.setOrderBy(I_PA_DashboardContent.COLUMNNAME_ColumnNo + "," + I_PA_DashboardContent.COLUMNNAME_AD_Client_ID + "," + I_PA_DashboardContent.COLUMNNAME_Line)
			.<MDashboardContent>list()
			.forEach(dashboard -> {
				Dashboard.Builder dashboardBuilder = Dashboard.newBuilder();
				dashboardBuilder.setId(dashboard.getPA_DashboardContent_ID());
				dashboardBuilder.setUuid(ValueUtil.validateNull(dashboard.getUUID()));
				dashboardBuilder.setName(ValueUtil.validateNull(dashboard.getName()));
				dashboardBuilder.setDescription(ValueUtil.validateNull(dashboard.getDescription()));
				dashboardBuilder.setHtml(ValueUtil.validateNull(dashboard.getHTML()));
				dashboardBuilder.setColumnNo(dashboard.getColumnNo());
				dashboardBuilder.setLineNo(dashboard.getLine());
				dashboardBuilder.setIsEventRequired(dashboard.isEventRequired());
				dashboardBuilder.setIsCollapsible(dashboard.isCollapsible());
				dashboardBuilder.setIsOpenByDefault(dashboard.isOpenByDefault());
				dashboardBuilder.setDashboardType("dashboard");
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
		if(request.getUserId() <= 0
				&& Util.isEmpty(request.getUserUuid())) {
			throw new AdempiereException("@AD_User_ID@ @NotFound@");
		}
		//	Get user
		int userId = request.getUserId();
		if(userId <= 0) {
			userId = RecordUtil.getIdFromUuid(I_AD_Role.Table_Name, request.getUserUuid(), null);
		}
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

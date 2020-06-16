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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_Ref_List;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_C_BP_Group;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Charge;
import org.compiere.model.I_C_Country;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_C_POS;
import org.compiere.model.I_C_POSKeyLayout;
import org.compiere.model.I_C_Region;
import org.compiere.model.I_M_PriceList;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MCharge;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPOS;
import org.compiere.model.MPOSKey;
import org.compiere.model.MPOSKeyLayout;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.MProductPricing;
import org.compiere.model.MRefList;
import org.compiere.model.MStorage;
import org.compiere.model.MTax;
import org.compiere.model.MUser;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;
import org.spin.grpc.util.StoreGrpc.StoreImplBase;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Service for backend of POS
 */
public class PointOfSalesServiceImplementation extends StoreImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(PointOfSalesServiceImplementation.class);
	/**	Product Cache	*/
	private static CCache<String, MProduct> productCache = new CCache<String, MProduct>(I_M_Product.Table_Name, 30, 0);	//	no time-out
	
	@Override
	public void getProductPrice(GetProductPriceRequest request, StreamObserver<ProductPrice> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getSearchValue());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ProductPrice.Builder productPrice = getProductPrice(request);
			responseObserver.onNext(productPrice.build());
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
	public void createOrder(CreateOrderRequest request, StreamObserver<Order> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Create Order = " + request.getPosUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			Order.Builder order = createOrder(context, request);
			responseObserver.onNext(order.build());
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
	public void getPointOfSales(PointOfSalesRequest request, StreamObserver<PointOfSales> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Get Point of Sales = " + request.getPointOfSalesUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			PointOfSales.Builder pos = getPosBuilder(context, request);
			responseObserver.onNext(pos.build());
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
	public void listPointOfSales(ListPointOfSalesRequest request, StreamObserver<ListPointOfSalesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Get Point of Sales List = " + request.getUserUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListPointOfSalesResponse.Builder posList = convertPointOfSalesList(context, request);
			responseObserver.onNext(posList.build());
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
	public void createOrderLine(CreateOrderLineRequest request, StreamObserver<OrderLine> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Add Line for Order = " + request.getOrderUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			OrderLine.Builder orderLine = createAndConvertOrderLine(context, request);
			responseObserver.onNext(orderLine.build());
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
	public void deleteOrderLine(DeleteOrderLineRequest request, StreamObserver<Empty> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Add Line for Order = " + request.getOrderLineUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Empty.Builder orderLine = deleteOrderLine(request);
			responseObserver.onNext(orderLine.build());
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
	public void deleteOrder(DeleteOrderRequest request, StreamObserver<Empty> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Add Line for Order = " + request.getOrderUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Empty.Builder order = deleteOrder(request);
			responseObserver.onNext(order.build());
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
	public void updateOrderLine(UpdateOrderLineRequest request, StreamObserver<OrderLine> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Add Line for Order = " + request.getOrderLineUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			OrderLine.Builder orderLine = updateAndConvertOrderLine(request);
			responseObserver.onNext(orderLine.build());
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
	public void listProductPrice(ListProductPriceRequest request, StreamObserver<ListProductPriceResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Add Line for Order = " + request.getSearchValue());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListProductPriceResponse.Builder productPriceList = getProductPriceList(request);
			responseObserver.onNext(productPriceList.build());
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
	public void getOrder(GetOrderRequest request, StreamObserver<Order> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Get Order = " + request.getOrderUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Order.Builder order = convertOrder(getOrder(request.getOrderUuid()));
			responseObserver.onNext(order.build());
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
	public void listOrders(ListOrdersRequest request, StreamObserver<ListOrdersResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Add Line for Order = " + request.getPosUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListOrdersResponse.Builder ordersList = listOrders(request);
			responseObserver.onNext(ordersList.build());
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
	public void listOrderLines(ListOrderLinesRequest request, StreamObserver<ListOrderLinesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("List Order Lines from order = " + request.getOrderUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListOrderLinesResponse.Builder orderLinesList = listOrderLines(request);
			responseObserver.onNext(orderLinesList.build());
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
	public void getKeyLayout(GetKeyLayoutRequest request, StreamObserver<KeyLayout> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Get Key Layout = " + request.getKeyLayoutUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			KeyLayout.Builder keyLayout = convertKeyLayout(RecordUtil.getIdFromUuid(I_C_POSKeyLayout.Table_Name, request.getKeyLayoutUuid()));
			responseObserver.onNext(keyLayout.build());
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
	public void listBusinessPartner(ListBusinessPartnerRequest request,
			StreamObserver<ListBusinessPartnerResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Add Line for Order = " + request.getSearchValue());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListBusinessPartnerResponse.Builder businessPartnerList = getBusinessPartnerList(request);
			responseObserver.onNext(businessPartnerList.build());
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
	public void getBusinessPartner(GetBusinessPartnerRequest request,
			StreamObserver<BusinessPartner> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getSearchValue());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			BusinessPartner.Builder businessPartner = getBusinessPartner(request);
			responseObserver.onNext(businessPartner.build());
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
	public void createBusinessPartner(CreateBusinessPartnerRequest request,
			StreamObserver<BusinessPartner> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getValue());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			BusinessPartner.Builder businessPartner = createBusinessPartner(request);
			responseObserver.onNext(businessPartner.build());
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
	 * List Orders from POS UUID
	 * @param request
	 * @return
	 */
	private ListOrdersResponse.Builder listOrders(ListOrdersRequest request) {
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		ListOrdersResponse.Builder builder = ListOrdersResponse.newBuilder();
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		//	Get Product list
		Query query = new Query(Env.getCtx(), I_C_Order.Table_Name, "EXISTS(SELECT 1 FROM C_POS p WHERE p.C_POS_ID = C_Order.C_POS_ID AND p.UUID = ?)", null)
				.setParameters(request.getPosUuid())
				.setClient_ID()
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.<MOrder>list()
		.forEach(order -> {
			builder.addOrders(convertOrder(order));
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * List Orders Lines from Order UUID
	 * @param request
	 * @return
	 */
	private ListOrderLinesResponse.Builder listOrderLines(ListOrderLinesRequest request) {
		if(Util.isEmpty(request.getOrderUuid())) {
			throw new AdempiereException("@C_Order_ID@ @NotFound@");
		}
		ListOrderLinesResponse.Builder builder = ListOrderLinesResponse.newBuilder();
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		//	Get Product list
		Query query = new Query(Env.getCtx(), I_C_OrderLine.Table_Name, "EXISTS(SELECT 1 FROM C_Order o WHERE o.C_Order_ID = C_OrderLine.C_Order_ID AND o.UUID = ?)", null)
				.setParameters(request.getOrderUuid())
				.setClient_ID()
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.<MOrderLine>list()
		.forEach(order -> {
			builder.addOrderLines(convertOrderLine(order));
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * Get Order from UUID
	 * @param uuid
	 * @return
	 */
	private MOrder getOrder(String uuid) {
		return (MOrder) RecordUtil.getEntity(Env.getCtx(), I_C_Order.Table_Name, uuid, 0);
	}
	
	/**
	 * Delete order line from uuid
	 * @param request
	 * @return
	 */
	private Empty.Builder deleteOrderLine(DeleteOrderLineRequest request) {
		if(Util.isEmpty(request.getOrderLineUuid())) {
			throw new AdempiereException("@C_OrderLine_ID@ @NotFound@");
		}
		MOrderLine orderLine = new Query(Env.getCtx(), I_C_OrderLine.Table_Name, I_C_OrderLine.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getOrderLineUuid())
				.setClient_ID()
				.first();
		if(orderLine == null
				|| orderLine.getC_OrderLine_ID() == 0) {
			return Empty.newBuilder();
		}
		//	Validate processed Order
		if(orderLine.isProcessed()) {
			throw new AdempiereException("@C_OrderLine_ID@ @Processed@");
		}
		if(orderLine != null
				&& orderLine.getC_Order_ID() >= 0) {
			orderLine.deleteEx(true);
		}
		//	Return
		return Empty.newBuilder();
	}
	
	
	/**
	 * Delete order from uuid
	 * @param request
	 * @return
	 */
	private Empty.Builder deleteOrder(DeleteOrderRequest request) {
		if(Util.isEmpty(request.getOrderUuid())) {
			throw new AdempiereException("@C_Order_ID@ @NotFound@");
		}
		MOrder order = new Query(Env.getCtx(), I_C_Order.Table_Name, " = ?", null)
				.setParameters(request.getOrderUuid())
				.setClient_ID()
				.first();
		if(order == null
				|| order.getC_Order_ID() == 0) {
			return Empty.newBuilder();
		}
		//	Validate drafted
		if(!DocumentUtil.isDrafted(order)) {
			throw new AdempiereException("@C_Order_ID@ @Processed@");
		}
		//	Validate processed Order
		if(order.isProcessed()) {
			throw new AdempiereException("@C_Order_ID@ @Processed@");
		}
		//	
		if(order != null
				&& order.getC_Order_ID() >= 0) {
			order.deleteEx(true);
		}
		//	Return
		return Empty.newBuilder();
	}
	
	/**
	 * Create order line and return this
	 * @param request
	 * @return
	 */
	private OrderLine.Builder updateAndConvertOrderLine(UpdateOrderLineRequest request) {
		//	Validate Order
		if(Util.isEmpty(request.getOrderLineUuid())) {
			throw new AdempiereException("@C_OrderLine_ID@ @NotFound@");
		}
		//	
		int orderLineId = RecordUtil.getIdFromUuid(I_C_OrderLine.Table_Name, request.getOrderLineUuid());
		if(orderLineId <= 0) {
			return OrderLine.newBuilder();
		}
		//	Quantity
		return convertOrderLine(
				updateOrderLine(orderLineId, 
						ValueUtil.getBigDecimalFromDecimal(request.getQuantity()), 
						ValueUtil.getBigDecimalFromDecimal(request.getPrice()), 
						ValueUtil.getBigDecimalFromDecimal(request.getDiscountRate()),
						request.getIsAddQuantity()));
	}
	
	/**
	 * Create order line and return this
	 * @param context
	 * @param request
	 * @return
	 */
	private OrderLine.Builder createAndConvertOrderLine(Properties context, CreateOrderLineRequest request) {
		//	Validate Order
		if(Util.isEmpty(request.getOrderUuid())) {
			throw new AdempiereException("@C_Order_ID@ @NotFound@");
		}
		//	Validate Product and charge
		if(Util.isEmpty(request.getProductUuid())
				&& Util.isEmpty(request.getChargeUuid())) {
			throw new AdempiereException("@M_Product_ID@ / @C_Charge_ID@ @NotFound@");
		}
		int orderId = RecordUtil.getIdFromUuid(I_C_Order.Table_Name, request.getOrderUuid());
		if(orderId <= 0) {
			return OrderLine.newBuilder();
		}
		//	Quantity
		return convertOrderLine(
				addOrderLine(orderId, 
						RecordUtil.getIdFromUuid(I_M_Product.Table_Name, request.getProductUuid()), 
						RecordUtil.getIdFromUuid(I_C_Charge.Table_Name, request.getChargeUuid()), 
						RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid()), 
						ValueUtil.getBigDecimalFromDecimal(request.getQuantity())));
	}
	
	/**
	 * Convert order line to stub
	 * @param orderLine
	 * @return
	 */
	private OrderLine.Builder convertOrderLine(MOrderLine orderLine) {
		OrderLine.Builder builder = OrderLine.newBuilder();
		if(orderLine == null) {
			return builder;
		}
		//	Convert
		return builder
				.setUuid(ValueUtil.validateNull(orderLine.getUUID()))
				.setOrderUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_Order.Table_Name, orderLine.getC_Order_ID())))
				.setLine(orderLine.getLine())
				.setDescription(ValueUtil.validateNull(orderLine.getDescription()))
				.setLineDescription(ValueUtil.validateNull(orderLine.getName()))
				.setProduct(convertProduct(orderLine.getM_Product_ID()))
				.setCharge(convertCharge(orderLine.getC_Charge_ID()))
				.setWarehouse(convertWarehouse(orderLine.getM_Warehouse_ID()))
				.setQuantity(ValueUtil.getDecimalFromBigDecimal(orderLine.getQtyOrdered()))
				.setPrice(ValueUtil.getDecimalFromBigDecimal(orderLine.getPriceActual()))
				.setDiscountRate(ValueUtil.getDecimalFromBigDecimal(orderLine.getDiscount()))
				.setTaxRate(ConvertUtil.convertTaxRate(MTax.get(Env.getCtx(), orderLine.getC_Tax_ID())))
				.setLineNetAmount(ValueUtil.getDecimalFromBigDecimal(orderLine.getLineNetAmt()));
	}
	
	/**
	 * Convert product
	 * @param productId
	 * @return
	 */
	private Product.Builder convertProduct(int productId) {
		Product.Builder builder = Product.newBuilder();
		if(productId <= 0) {
			return builder;
		}
		return ConvertUtil.convertProduct(MProduct.get(Env.getCtx(), productId));
	}
	
	/**
	 * Convert charge
	 * @param chargeId
	 * @return
	 */
	private Charge.Builder convertCharge(int chargeId) {
		Charge.Builder builder = Charge.newBuilder();
		if(chargeId <= 0) {
			return builder;
		}
		return ConvertUtil.convertCharge(MCharge.get(Env.getCtx(), chargeId));
	}
	
	/**
	 * convert warehouse from id
	 * @param warehouseId
	 * @return
	 */
	private Warehouse.Builder convertWarehouse(int warehouseId) {
		Warehouse.Builder builder = Warehouse.newBuilder();
		if(warehouseId <= 0) {
			return builder;
		}
		return ConvertUtil.convertWarehouse(MWarehouse.get(Env.getCtx(), warehouseId));
	}
	
	/**
	 * Convert key layout from id
	 * @param keyLayoutId
	 * @return
	 */
	private KeyLayout.Builder convertKeyLayout(int keyLayoutId) {
		KeyLayout.Builder builder = KeyLayout.newBuilder();
		if(keyLayoutId <= 0) {
			return builder;
		}
		return convertKeyLayout(MPOSKeyLayout.get(Env.getCtx(), keyLayoutId));
	}
	
	/**
	 * Convert Key Layout from PO
	 * @param keyLayout
	 * @return
	 */
	private KeyLayout.Builder convertKeyLayout(MPOSKeyLayout keyLayout) {
		KeyLayout.Builder builder = KeyLayout.newBuilder()
				.setUuid(ValueUtil.validateNull(keyLayout.getUUID()))
				.setId(keyLayout.getC_POSKeyLayout_ID())
				.setName(ValueUtil.validateNull(keyLayout.getName()))
				.setDescription(ValueUtil.validateNull(keyLayout.getDescription()))
				.setHelp(ValueUtil.validateNull(keyLayout.getHelp()))
				.setLayoutType(ValueUtil.validateNull(keyLayout.getPOSKeyLayoutType()))
				.setColumns(keyLayout.getColumns());
				//	TODO: Color
		//	Add keys
		Arrays.asList(keyLayout.getKeys(false)).stream().filter(key -> key.isActive()).forEach(key -> builder.addKeys(convertKey(key)));
		return builder;
	}
	
	/**
	 * Convet key for layout
	 * @param key
	 * @return
	 */
	private Key.Builder convertKey(MPOSKey key) {
		return Key.newBuilder()
				.setUuid(ValueUtil.validateNull(key.getUUID()))
				.setId(key.getC_POSKeyLayout_ID())
				.setName(ValueUtil.validateNull(key.getName()))
				//	TODO: Color
				.setSequence(key.getSeqNo())
				.setSpanX(key.getSpanX())
				.setSpanY(key.getSpanY())
				.setSubKeyLayoutUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_POSKeyLayout.Table_Name, key.getSubKeyLayout_ID())))
				.setQuantity(ValueUtil.getDecimalFromBigDecimal(key.getQty() == null || key.getQty().equals(Env.ZERO)? Env.ONE: key.getQty()))
				.setProductUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_M_Product.Table_Name, key.getM_Product_ID())))
				.setResourceReference(ConvertUtil.convertResourceReference(RecordUtil.getResourceFromImageId(key.getAD_Image_ID())));
	}
	
	/***
	 * Add order line
	 * @param orderId
	 * @param productId
	 * @param chargeId
	 * @param warehouseId
	 * @param quantity
	 * @return
	 */
	private MOrderLine addOrderLine(int orderId, int productId, int chargeId, int warehouseId, BigDecimal quantity) {
		if(orderId <= 0) {
			return null;
		}
		MOrder order = new MOrder(Env.getCtx(), orderId, null);
		//	Valid Complete
		if (!DocumentUtil.isDrafted(order))
			return null;
		// catch Exceptions at order.getLines()
		Optional<MOrderLine> maybeOrderLine = Arrays.asList(order.getLines(true, "Line"))
				.stream()
				.filter(orderLine -> (productId != 0 && productId == orderLine.getM_Product_ID()) 
						|| (chargeId != 0 && chargeId == orderLine.getC_Charge_ID()))
				.findFirst();
		if(maybeOrderLine.isPresent()) {
			MOrderLine orderLine = maybeOrderLine.get();
			BigDecimal currentPrice = orderLine.getPriceEntered();
			//	Set Quantity
			if(quantity == null) {
				quantity = orderLine.getQtyOrdered();
				quantity = quantity.add(Env.ONE);
			}
			orderLine.setQty(quantity);
			orderLine.setPrice(currentPrice); //	sets List/limit
			orderLine.saveEx();
			return orderLine;
		}
		if(quantity == null) {
			quantity = Env.ONE;
		}
        //create new line
		MOrderLine orderLine = new MOrderLine(order);
		if(chargeId > 0) {
			orderLine.setC_Charge_ID(chargeId);
		} else if(productId > 0) {
			orderLine.setProduct(MProduct.get(order.getCtx(), productId));
		}
		orderLine.setQty(quantity);
		orderLine.setPrice();
		//	Save Line
		orderLine.saveEx();
		return orderLine;
			
	} //	addOrUpdateLine
	
	/***
	 * Update order line
	 * @param orderLineId
	 * @param quantity
	 * @param price
	 * @param discountRate
	 * @param isAddQuantity
	 * @return
	 */
	private MOrderLine updateOrderLine(int orderLineId, BigDecimal quantity, BigDecimal price, BigDecimal discountRate, boolean isAddQuantity) {
		if(orderLineId <= 0) {
			return null;
		}
		MOrderLine orderLine = new MOrderLine(Env.getCtx(), orderLineId, null);
		MOrder order = orderLine.getParent();
		orderLine.setHeaderInfo(order);
		//	Valid Complete
		if (!DocumentUtil.isDrafted(order))
			return null;
		if((quantity == null || quantity.equals(Env.ZERO))
				&& (price == null || price.equals(Env.ZERO))
				&& (discountRate == null || discountRate.equals(Env.ZERO))) {
			return null;
		}
		//	Get if is null
		if(quantity == null
				|| quantity.equals(Env.ZERO)) {
			quantity = orderLine.getQtyEntered();
		} else if(isAddQuantity) {
			BigDecimal currentQuantity = orderLine.getQtyEntered();
			if(currentQuantity == null) {
				currentQuantity = Env.ZERO;
			}
			quantity = currentQuantity.add(quantity);
		}
		if(price == null
				|| price.equals(Env.ZERO)) {
			price = orderLine.getPriceEntered();
		}
		if(discountRate == null
				|| discountRate.equals(Env.ZERO)) {
			discountRate = orderLine.getDiscount();
		} else {
			BigDecimal discountAmount = orderLine.getPriceList().multiply(discountRate.divide(Env.ONEHUNDRED));
			price = orderLine.getPriceList().subtract(discountAmount);
		}
		//	Set values
		orderLine.setPrice(price); //	sets List/limit
		orderLine.setQty(quantity);
		orderLine.setTax();
		orderLine.saveEx();
		return orderLine;
			
	} //	UpdateLine
	
	/**
	 * Get list from user
	 * @param context
	 * @param request
	 * @return
	 */
	private ListPointOfSalesResponse.Builder convertPointOfSalesList(Properties context, ListPointOfSalesRequest request) {
		ListPointOfSalesResponse.Builder builder = ListPointOfSalesResponse.newBuilder();
		if(Util.isEmpty(request.getUserUuid())) {
			throw new AdempiereException("@SalesRep_ID@ @NotFound@");
		}
		int salesRepresentativeId = RecordUtil.getIdFromUuid(I_AD_User.Table_Name,request.getUserUuid());
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		//	Get POS List
		Query query = new Query(context , I_C_POS.Table_Name , "(AD_Org_ID = ? OR SalesRep_ID = ?)", null)
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.setParameters(Env.getAD_Org_ID(context), salesRepresentativeId)
				.setOrderBy(I_C_POS.COLUMNNAME_Name);
		int count = query.count();
		query
			.setLimit(limit, offset)
			.<MPOS>list()
			.forEach(pos -> builder.addSellingPoints(convertPointOfSales(pos)));
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * Get POS builder
	 * @param context
	 * @param request
	 * @return
	 */
	private PointOfSales.Builder getPosBuilder(Properties context, PointOfSalesRequest request) {
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPointOfSalesUuid());
		if(posId <= 0) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		//	
		return convertPointOfSales(MPOS.get(context, posId));
	}
	
	/**
	 * Convert POS
	 * @param pos
	 * @return
	 */
	private PointOfSales.Builder convertPointOfSales(MPOS pos) {
		PointOfSales.Builder build = PointOfSales.newBuilder()
				.setUuid(ValueUtil.validateNull(pos.getUUID()))
				.setId(pos.getC_POS_ID())
				.setName(ValueUtil.validateNull(pos.getName()))
				.setDescription(ValueUtil.validateNull(pos.getDescription()))
				.setHelp(ValueUtil.validateNull(pos.getHelp()))
				.setIsModifyPrice(pos.isModifyPrice())
				.setIsPOSRequiredPIN(pos.isPOSRequiredPIN())
				.setSalesRepresentative(convertSalesRepresentative(MUser.get(pos.getCtx(), pos.getSalesRep_ID())))
				.setTemplateBusinessPartner(ConvertUtil.convertBusinessPartner(pos.getBPartner()))
				.setKeyLayoutUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_POSKeyLayout.Table_Name, pos.getC_POSKeyLayout_ID())));
		//	Set Price List adn currency
		if(pos.getM_PriceList_ID() != 0) {
			MPriceList priceList = MPriceList.get(Env.getCtx(), pos.getM_PriceList_ID(), null);
			MCurrency currency = MCurrency.get(Env.getCtx(), priceList.getC_Currency_ID());
			build.setPriceListUuid(ValueUtil.validateNull(priceList.getUUID()))
				.setCurrency(ConvertUtil.convertCurrency(currency));
		}
		return build;
	}
	
	/**
	 * Convert Sales Representative
	 * @param salesRepresentative
	 * @return
	 */
	private SalesRepresentative.Builder convertSalesRepresentative(MUser salesRepresentative) {
		return SalesRepresentative.newBuilder()
				.setUuid(ValueUtil.validateNull(salesRepresentative.getUUID()))
				.setId(salesRepresentative.getAD_User_ID())
				.setName(ValueUtil.validateNull(salesRepresentative.getName()))
				.setDescription(ValueUtil.validateNull(salesRepresentative.getDescription()));
	}
	
	/**
	 * Create Order from request
	 * @param context
	 * @param request
	 * @return
	 */
	private Order.Builder createOrder(Properties context, CreateOrderRequest request) {
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid());
		if(posId <= 0) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		MPOS pos = MPOS.get(context, posId);
		//	
		MOrder salesOrder = new Query(context, I_C_Order.Table_Name, 
				"DocStatus = 'DR' "
				+ "AND C_POS_ID = ? "
				+ "AND NOT EXISTS(SELECT 1 "
				+ "					FROM C_OrderLine ol "
				+ "					WHERE ol.C_Order_ID = C_Order.C_Order_ID) ", null)
				.setParameters(pos.getC_POS_ID())
				.first();
		//	Validate
		if(salesOrder == null) {
			salesOrder = new MOrder(context, 0, null);
		} else {
			salesOrder.setDateOrdered(getDate());
			salesOrder.setDateAcct(getDate());
			salesOrder.setDatePromised(getDate());
		}
		//	Default values
		salesOrder.setIsSOTrx(true);
		salesOrder.setAD_Org_ID(pos.getAD_Org_ID());
		salesOrder.setC_POS_ID(pos.getC_POS_ID());
		//	Warehouse
		if(pos.getM_Warehouse_ID() != 0) {
			salesOrder.setM_Warehouse_ID(pos.getM_Warehouse_ID());
		}
		//	Price List
		if(pos.getM_PriceList_ID() != 0) {
			salesOrder.setM_PriceList_ID(pos.getM_PriceList_ID());
		}
		//	Document Type
		int documentTypeId = 0;
		if(!Util.isEmpty(request.getDocumentTypeUuid())) {
			documentTypeId = RecordUtil.getIdFromUuid(I_C_DocType.Table_Name, request.getDocumentTypeUuid());
		}
		//	Validate
		if(documentTypeId <= 0
				&& pos.getC_DocType_ID() != 0) {
			documentTypeId = pos.getC_DocType_ID();
		}
		//	Validate
		if(documentTypeId > 0) {
			salesOrder.setC_DocTypeTarget_ID(documentTypeId);
		} else {
			salesOrder.setC_DocTypeTarget_ID(MOrder.DocSubTypeSO_POS);
		}
		//	Delivery Rules
		if (pos.getDeliveryRule() != null) {
			salesOrder.setDeliveryRule(pos.getDeliveryRule());
		}
		//	Invoice Rule
		if (pos.getInvoiceRule() != null) {
			salesOrder.setInvoiceRule(pos.getInvoiceRule());
		}
		//	Set business partner
		setBPartner(pos, salesOrder, request.getCustomerUuid());
		//	Convert order
		return convertOrder(salesOrder);
	}
	
	/**
	 * Set business partner from uuid
	 * @param pos
	 * @param salesOrder
	 * @param businessPartnerUuid
	 */
	private void setBPartner(MPOS pos, MOrder salesOrder, String businessPartnerUuid) {
		//	Valid if has a Order
		if(DocumentUtil.isCompleted(salesOrder)
				|| DocumentUtil.isVoided(salesOrder)) {
			return;
		}
		//	Get BP
		MBPartner businessPartner = null;
		if(!Util.isEmpty(businessPartnerUuid)) {
			int businessPartnerId = RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, businessPartnerUuid);
			if(businessPartnerId > 0) {
				businessPartner = MBPartner.get(Env.getCtx(), businessPartnerId);
			}
		}
		boolean isSamePOSPartner = false;
		if(businessPartner == null) {
			businessPartner = pos.getBPartner();
			isSamePOSPartner = true;
		}
		//	Validate business partner
		if(businessPartner == null) {
			throw new AdempiereException("@C_BPartner_ID@ @NotFound@");
		}
		int businessPartnerId = businessPartner.getC_BPartner_ID();
		log.fine( "CPOS.setC_BPartner_ID=" + businessPartner.getC_BPartner_ID());
		salesOrder.setBPartner(businessPartner);
		//	
		MBPartnerLocation [] partnerLocations = businessPartner.getLocations(true);
		if(partnerLocations.length > 0) {
			for(MBPartnerLocation partnerLocation : partnerLocations) {
				if(partnerLocation.isBillTo())
					salesOrder.setBill_Location_ID(partnerLocation.getC_BPartner_Location_ID());
				if(partnerLocation.isShipTo())
					salesOrder.setShip_Location_ID(partnerLocation.getC_BPartner_Location_ID());
			}				
		}
		//	Validate Same BPartner
		if(isSamePOSPartner) {
			if(salesOrder.getPaymentRule()==null)
				salesOrder.setPaymentRule(MOrder.PAYMENTRULE_Cash);
		}
		//	Set Sales Representative
		if(pos.get_ValueAsBoolean("IsSharedPOS")) {
			salesOrder.setSalesRep_ID(Env.getAD_User_ID(salesOrder.getCtx()));
		} else if (businessPartner.getSalesRep_ID() != 0) {
			salesOrder.setSalesRep_ID(salesOrder.getC_BPartner().getSalesRep_ID());
		} else {
			salesOrder.setSalesRep_ID(pos.getSalesRep_ID());
		}
		//	Save Header
		salesOrder.saveEx();
		//	Load Price List Version
		MPriceList priceList = MPriceList.get(Env.getCtx(), salesOrder.getM_PriceList_ID(), null);
		//
		MPriceListVersion priceListVersion = priceList.getPriceListVersion (getDate());
		List<MProductPrice> productPrices = Arrays.asList(priceListVersion.getProductPrice("AND EXISTS("
				+ "SELECT 1 "
				+ "FROM C_OrderLine ol "
				+ "WHERE ol.C_Order_ID = " + salesOrder.getC_Order_ID() + " "
				+ "AND ol.M_Product_ID = M_ProductPrice.M_Product_ID)"));
		//	Update Lines
		Arrays.asList(salesOrder.getLines())
			.forEach(orderLine -> {
				//	Verify if exist
				if(productPrices
					.stream()
					.filter(productPrice -> productPrice.getM_Product_ID() == orderLine.getM_Product_ID())
					.findFirst()
					.isPresent()) {
					orderLine.setC_BPartner_ID(businessPartnerId);
					orderLine.setC_BPartner_Location_ID(salesOrder.getC_BPartner_Location_ID());
					orderLine.setPrice();
					orderLine.setTax();
					orderLine.saveEx();
				} else {
					orderLine.deleteEx(true);
				}
			});
	}
	
	/**
	 * Get Date
	 * @return
	 */
	private Timestamp getDate() {
		return TimeUtil.getDay(System.currentTimeMillis());
	}
	
	/**
	 * Convert Order from entity
	 * @param order
	 * @return
	 */
	private Order.Builder convertOrder(MOrder order) {
		Order.Builder builder = Order.newBuilder();
		if(order == null) {
			return builder;
		}
		MRefList reference = MRefList.get(Env.getCtx(), MOrder.DOCSTATUS_AD_REFERENCE_ID, order.getDocStatus(), null);
		//	Convert
		return builder
			.setUuid(ValueUtil.validateNull(order.getUUID()))
			.setId(order.getC_Order_ID())
			.setDocumentType(ConvertUtil.convertDocumentType(MDocType.get(Env.getCtx(), order.getC_DocTypeTarget_ID())))
			.setDocumentNo(ValueUtil.validateNull(order.getDocumentNo()))
			.setSalesRepresentative(convertSalesRepresentative(MUser.get(Env.getCtx(), order.getSalesRep_ID())))
			.setDocumentStatus(ConvertUtil.convertDocumentStatus(
					ValueUtil.validateNull(order.getDocStatus()), 
					ValueUtil.validateNull(ValueUtil.getTranslation(reference, I_AD_Ref_List.COLUMNNAME_Name)), 
					ValueUtil.validateNull(ValueUtil.getTranslation(reference, I_AD_Ref_List.COLUMNNAME_Description))))
			.setTotalLines(ValueUtil.getDecimalFromBigDecimal(order.getTotalLines()))
			.setGrandTotal(ValueUtil.getDecimalFromBigDecimal(order.getGrandTotal()))
			.setDateOrdered(order.getDateOrdered().getTime())
			.setBusinessPartner(ConvertUtil.convertBusinessPartner((MBPartner) order.getC_BPartner()));
	}
	
	/**
	 * Get PriceList
	 * @param priceListUuid
	 * @return
	 */
	private MPriceList getPriceList(String priceListUuid) {
		MPriceList priceList = null;
		if(Util.isEmpty(priceListUuid)) {
			priceList = new Query(Env.getCtx(), I_M_PriceList.Table_Name, "EXISTS(SELECT 1 FROM C_POS p WHERE p.M_PriceList_ID = M_PriceList.M_PriceList_ID AND p.AD_Org_ID IN(0, ?))", null)
					.setParameters(Env.getAD_Org_ID(Env.getCtx()))
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.first();
		} else {
			int priceListId = RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, priceListUuid);
			if(priceListId > 0) {
				priceList = MPriceList.get(Env.getCtx(), priceListId, null);
			}
		}
		if(priceList == null) {
			throw new AdempiereException("@M_PriceList_ID@ @NotFound@");
		}
		return priceList;
	}
	
	/**
	 * Get Product Price Method
	 * @param context
	 * @param request
	 * @return
	 */
	private ListProductPriceResponse.Builder getProductPriceList(ListProductPriceRequest request) {
		ListProductPriceResponse.Builder builder = ListProductPriceResponse.newBuilder();
		//	Validate Price List
		MPriceList priceList = getPriceList(request.getPriceListUuid());
		//	Get Valid From
		Timestamp validFrom = TimeUtil.getDay(request.getValidFrom() > 0? request.getValidFrom(): System.currentTimeMillis());
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		//	Dynamic where clause
		StringBuffer whereClause = new StringBuffer();
		//	Parameters
		List<Object> parameters = new ArrayList<Object>();
		//	For search value
		if(!Util.isEmpty(request.getSearchValue())) {
			whereClause.append("("
				+ "UPPER(Value) = UPPER(?)"
				+ "OR UPPER(Name) = UPPER(?)"
				+ "OR UPPER(UPC) = UPPER(?)"
				+ "OR UPPER(SKU) = UPPER(?)"
				+ ")");
			//	Add parameters
			parameters.add(request.getSearchValue());
			parameters.add(request.getSearchValue());
			parameters.add(request.getSearchValue());
			parameters.add(request.getSearchValue());
		} 
		//	for price list
		if(!Util.isEmpty(request.getPriceListUuid())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("(EXISTS(SELECT 1 FROM M_PriceList_Version plv "
						+ "INNER JOIN M_ProductPrice pp ON(pp.M_PriceList_Version_ID = plv.M_PriceList_Version_ID) "
						+ "WHERE plv.M_PriceList_ID = ? "
						+ "AND pp.M_Product_ID = M_Product.M_Product_ID))");
				//	Add parameters
				parameters.add(RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, request.getPriceListUuid()));
			}
		}
		//	Get Product list
		Query query = new Query(Env.getCtx(), I_M_Product.Table_Name, 
				whereClause.toString(), null)
				.setParameters(parameters)
				.setClient_ID()
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.<MProduct>list()
		.forEach(product -> {
			builder.addProductPrices(
					convertProductPrice(
							product, 
							RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getBusinessPartnerUuid()), 
							priceList, 
							RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid()), 
							validFrom, 
							null));
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * List business partner
	 * @param context
	 * @param request
	 * @return
	 */
	private ListBusinessPartnerResponse.Builder getBusinessPartnerList(ListBusinessPartnerRequest request) {
		ListBusinessPartnerResponse.Builder builder = ListBusinessPartnerResponse.newBuilder();
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		//	Get business partner list
		//	Dynamic where clause
		StringBuffer whereClause = new StringBuffer();
		//	Parameters
		List<Object> parameters = new ArrayList<Object>();
		//	For search value
		if(!Util.isEmpty(request.getSearchValue())) {
			whereClause.append("("
				+ "UPPER(Value) = UPPER(?)"
				+ "OR UPPER(Name) = UPPER(?)"
				+ ")");
			//	Add parameters
			parameters.add(request.getSearchValue());
			parameters.add(request.getSearchValue());
		}
		//	For value
		if(!Util.isEmpty(request.getValue())) {
			whereClause.append("("
				+ "UPPER(Value) = UPPER(?)"
				+ ")");
			//	Add parameters
			parameters.add(request.getValue());
		}
		//	For name
		if(!Util.isEmpty(request.getName())) {
			whereClause.append("("
				+ "UPPER(Name) = UPPER(?)"
				+ ")");
			//	Add parameters
			parameters.add(request.getName());
		}
		//	for contact name
		if(!Util.isEmpty(request.getContactName())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("(EXISTS(SELECT 1 FROM AD_User u WHERE u.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(u.Name) = UPPER(?)))");
				//	Add parameters
				parameters.add(request.getContactName());
			}
		}
		//	EMail
		if(!Util.isEmpty(request.getEMail())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("(EXISTS(SELECT 1 FROM AD_User u WHERE u.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(u.EMail) = UPPER(?)))");
				//	Add parameters
				parameters.add(request.getEMail());
			}
		}
		//	Phone
		if(!Util.isEmpty(request.getPhone())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("("
						+ "EXISTS(SELECT 1 FROM AD_User u WHERE u.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(u.Phone) = UPPER(?)) "
						+ "OR EXISTS(SELECT 1 FROM C_BPartner_Location bpl WHERE bpl.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(bpl.Phone) = UPPER(?))"
						+ ")");
				//	Add parameters
				parameters.add(request.getPhone());
				parameters.add(request.getPhone());
			}
		}
		//	Postal Code
		if(!Util.isEmpty(request.getPostalCode())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("(EXISTS(SELECT 1 FROM C_BPartner_Location bpl "
						+ "INNER JOIN C_Location l ON(l.C_Location_ID = bpl.C_Location_ID) "
						+ "WHERE bpl.C_BPartner_ID = C_BPartner.C_BPartner_ID "
						+ "AND UPPER(l.Postal) = UPPER(?)))");
				//	Add parameters
				parameters.add(request.getPostalCode());
			}
		}
		//	Get Product list
		Query query = new Query(Env.getCtx(), I_C_BPartner.Table_Name, 
				whereClause.toString(), null)
				.setParameters(parameters)
				.setClient_ID()
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.<MBPartner>list()
		.forEach(businessPartner -> builder.addBusinessPartners(ConvertUtil.convertBusinessPartner(businessPartner)));
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * Get 
	 * @param product
	 * @param businessPartnerId
	 * @param priceList
	 * @param warehouseId
	 * @param validFrom
	 * @param quantity
	 * @return
	 */
	private ProductPrice.Builder convertProductPrice(MProduct product, int businessPartnerId, MPriceList priceList, int warehouseId, Timestamp validFrom, BigDecimal priceQuantity) {
		ProductPrice.Builder builder = ProductPrice.newBuilder();
		//	Get Price
		MProductPricing productPricing = new MProductPricing(product.getM_Product_ID(), businessPartnerId, priceQuantity, true, null);
		productPricing.setM_PriceList_ID(priceList.getM_PriceList_ID());
		productPricing.setPriceDate(validFrom);
		builder.setProduct(ConvertUtil.convertProduct(product));
		int taxCategoryId = product.getC_TaxCategory_ID();
		Optional<MTax> optionalTax = Arrays.asList(MTax.getAll(Env.getCtx()))
		.stream()
		.filter(tax -> tax.getC_TaxCategory_ID() == taxCategoryId 
							&& (tax.isSalesTax() 
									|| (!Util.isEmpty(tax.getSOPOType()) 
											&& (tax.getSOPOType().equals(MTax.SOPOTYPE_Both) 
													|| tax.getSOPOType().equals(MTax.SOPOTYPE_SalesTax)))))
		.findFirst();
		//	Validate
		if(optionalTax.isPresent()) {
			builder.setTaxRate(ConvertUtil.convertTaxRate(optionalTax.get()));
		}
		//	Set currency
		builder.setCurrency(ConvertUtil.convertCurrency(MCurrency.get(Env.getCtx(), priceList.getC_Currency_ID())));
		//	Price List Attributes
		builder.setIsTaxIncluded(priceList.isTaxIncluded());
		builder.setValidFrom(productPricing.getPriceDate().getTime());
		builder.setPriceListName(ValueUtil.validateNull(priceList.getName()));
		//	Pricing
		builder.setPricePrecision(productPricing.getPrecision());
		builder.setPriceList(ValueUtil.getDecimalFromBigDecimal(productPricing.getPriceList()));
		builder.setPriceStd(ValueUtil.getDecimalFromBigDecimal(productPricing.getPriceStd()));
		builder.setPriceLimit(ValueUtil.getDecimalFromBigDecimal(productPricing.getPriceLimit()));
		//	Get Storage
		if(warehouseId > 0) {
			AtomicReference<BigDecimal> quantityOnHand = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> quantityReserved = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> quantityOrdered = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> quantityAvailable = new AtomicReference<BigDecimal>(Env.ZERO);
			//	
			Arrays.asList(MStorage.getOfProduct(Env.getCtx(), product.getM_Product_ID(), null))
				.stream()
				.filter(storage -> storage.getM_Warehouse_ID() == warehouseId)
				.forEach(storage -> {
					quantityOnHand.updateAndGet(quantity -> quantity.add(storage.getQtyOnHand()));
					quantityReserved.updateAndGet(quantity -> quantity.add(storage.getQtyReserved()));
					quantityOrdered.updateAndGet(quantity -> quantity.add(storage.getQtyOrdered()));
					quantityAvailable.updateAndGet(quantity -> quantity.add(storage.getQtyOnHand().subtract(storage.getQtyReserved())));
				});
			builder.setQuantityOnHand(ValueUtil.getDecimalFromBigDecimal(quantityOnHand.get()));
			builder.setQuantityReserved(ValueUtil.getDecimalFromBigDecimal(quantityReserved.get()));
			builder.setQuantityOrdered(ValueUtil.getDecimalFromBigDecimal(quantityOrdered.get()));
			builder.setQuantityAvailable(ValueUtil.getDecimalFromBigDecimal(quantityAvailable.get()));
		}
		return builder;
	}
	
	/**
	 * Get Product Price Method
	 * @param request
	 * @return
	 */
	private ProductPrice.Builder getProductPrice(GetProductPriceRequest request) {
		//	Get Product
		MProduct product = null;
		String key = Env.getAD_Client_ID(Env.getCtx()) + "|";
		if(!Util.isEmpty(request.getSearchValue())) {
			key = key + "SearchValue|" + request.getSearchValue();
			product = productCache.get(key);
			if(product == null) {
				product = new Query(Env.getCtx(), I_M_Product.Table_Name, 
						"("
						+ "UPPER(Value) = UPPER(?)"
						+ "OR UPPER(Name) = UPPER(?)"
						+ "OR UPPER(UPC) = UPPER(?)"
						+ "OR UPPER(SKU) = UPPER(?)"
						+ ")", null)
						.setParameters(request.getSearchValue(), request.getSearchValue(), request.getSearchValue(), request.getSearchValue())
						.setClient_ID()
						.setOnlyActiveRecords(true)
						.first();
			}
		} else if(!Util.isEmpty(request.getUpc())) {
			key = key + "Upc|" + request.getUpc();
			product = productCache.get(key);
			if(product == null) {
				Optional<MProduct> optionalProduct = MProduct.getByUPC(Env.getCtx(), request.getUpc(), null).stream().findAny();
				if(optionalProduct.isPresent()) {
					product = optionalProduct.get();
				}
			}
		} else if(!Util.isEmpty(request.getValue())) {
			key = key + "Value|" + request.getValue();
			product = productCache.get(key);
			if(product == null) {
				product = new Query(Env.getCtx(), I_M_Product.Table_Name, "UPPER(Value) = UPPER(?)", null)
						.setParameters(request.getValue())
						.setClient_ID()
						.setOnlyActiveRecords(true)
						.first();
			}
		} else if(!Util.isEmpty(request.getName())) {
			key = key + "Name|" + request.getName();
			product = productCache.get(key);
			if(product == null) {
				product = new Query(Env.getCtx(), I_M_Product.Table_Name, "UPPER(Name) LIKE UPPER(?)", null)
						.setParameters(request.getName())
						.setClient_ID()
						.setOnlyActiveRecords(true)
						.first();
			}
		}
		//	Validate product
		if(product == null) {
			throw new AdempiereException("@M_Product_ID@ @NotFound@");
		} else {
			productCache.put(key, product);
		}
		//	Validate Price List
		MPriceList priceList = null;
		if(Util.isEmpty(request.getPriceListUuid())) {
			priceList = new Query(Env.getCtx(), I_M_PriceList.Table_Name, "EXISTS(SELECT 1 FROM C_POS p WHERE p.M_PriceList_ID = M_PriceList.M_PriceList_ID AND p.AD_Org_ID IN(0, ?))", null)
					.setParameters(Env.getAD_Org_ID(Env.getCtx()))
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.first();
		} else {
			int priceListId = RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, request.getPriceListUuid());
			if(priceListId > 0) {
				priceList = MPriceList.get(Env.getCtx(), priceListId, null);
			}
		}
		if(priceList == null) {
			priceList = new Query(Env.getCtx(), I_M_PriceList.Table_Name, I_M_PriceList.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getPriceListUuid())
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.first();
			if(priceList == null) {
				throw new AdempiereException("@M_PriceList_ID@ @NotFound@");
			}
		}
		//	Get Valid From
		Timestamp validFrom = TimeUtil.getDay(request.getValidFrom() > 0? request.getValidFrom(): System.currentTimeMillis());
		return convertProductPrice(product,
				RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getBusinessPartnerUuid()), 
				priceList, 
				RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid()), 
				validFrom,
				Env.ONE);
	}
	
	/**
	 * Create business partner
	 * @param request
	 * @return
	 */
	private BusinessPartner.Builder createBusinessPartner(CreateBusinessPartnerRequest request) {
		//	Validate name
		if(Util.isEmpty(request.getName())) {
			throw new AdempiereException("@Name@ @IsMandatory@");
		}
		//	POS Uuid
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @IsMandatory@");
		}
		//	Create it
		MBPartner businessPartner = MBPartner.getTemplate(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()), RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid()));
		businessPartner.setAD_Org_ID(0);
		businessPartner.setIsCustomer (true);
		businessPartner.setIsVendor (false);
		//	Set Value
		if(Util.isEmpty(request.getValue())) {
			String value = DB.getDocumentNo (Env.getAD_Client_ID(Env.getCtx()), "C_BPartner", null, businessPartner);
			businessPartner.setValue(value);
		}
		//	Tax Id
		if(!Util.isEmpty(request.getTaxId())) {
			businessPartner.setTaxID(request.getTaxId());
		}
		//	Duns
		if(!Util.isEmpty(request.getDuns())) {
			businessPartner.setDUNS(request.getDuns());
		}
		//	Naics
		if(!Util.isEmpty(request.getNaics())) {
			businessPartner.setNAICS(request.getNaics());
		}
		//	Name
		businessPartner.setName(request.getName());
		//	Last name
		if(!Util.isEmpty(request.getLastName())) {
			businessPartner.setName2(request.getLastName());
		}
		//	Description
		if(!Util.isEmpty(request.getDescription())) {
			businessPartner.setDescription(request.getDescription());
		}
		//	Business partner group
		if(!Util.isEmpty(request.getBusinessPartnerGroupUuid())) {
			int businessPartnerGroupId = RecordUtil.getIdFromUuid(I_C_BP_Group.Table_Name, request.getBusinessPartnerGroupUuid());
			if(businessPartnerGroupId != 0) {
				businessPartner.setC_BP_Group_ID(businessPartnerGroupId);
			}
		}
		//	Save it
		businessPartner.saveEx();
		//	Contact
		if(!Util.isEmpty(request.getContactName()) || !Util.isEmpty(request.getEMail()) || !Util.isEmpty(request.getPhone())) {
			MUser contact = new MUser(businessPartner);
			//	Name
			if(!Util.isEmpty(request.getContactName())) {
				contact.setName(request.getContactName());
			}
			//	EMail
			if(!Util.isEmpty(request.getEMail())) {
				contact.setEMail(request.getEMail());
			}
			//	Phone
			if(!Util.isEmpty(request.getPhone())) {
				contact.setPhone(request.getPhone());
			}
			//	Description
			if(!Util.isEmpty(request.getDescription())) {
				contact.setDescription(request.getDescription());
			}
			//	Save
			contact.saveEx();
			//	Location
			int countryId = 0;
			if(!Util.isEmpty(request.getCountryUuid())) {
				countryId = RecordUtil.getIdFromUuid(I_C_Country.Table_Name, request.getCountryUuid());
			}
			if(countryId <= 0) {
				countryId = Env.getContextAsInt(Env.getCtx(), "#C_Country_ID");
			}
			//	
			int regionId = 0;
			if(!Util.isEmpty(request.getRegionUuid())) {
				regionId = RecordUtil.getIdFromUuid(I_C_Region.Table_Name, request.getRegionUuid());
			}
			String cityName = null;
			int cityId = 0;
			//	City Name
			if(!Util.isEmpty(request.getCityName())) {
				cityName = request.getCityName();
			}
			//	City Reference
			if(!Util.isEmpty(request.getCityUuid())) {
				cityId = RecordUtil.getIdFromUuid(I_C_Region.Table_Name, request.getRegionUuid());
				if(cityId > 0) {
					cityName = null;
				}
			}
			//	Instance it
			MLocation location = new MLocation(Env.getCtx(), countryId, regionId, cityName, null);
			if(cityId > 0) {
				location.setC_City_ID(cityId);
			}
			//	Postal Code
			if(!Util.isEmpty(request.getPostalCode())) {
				location.setPostal(request.getPostalCode());
			}
			location.saveEx();
			//	Create BP location
			MBPartnerLocation businessPartnerLocation = new MBPartnerLocation(businessPartner);
			businessPartnerLocation.setC_Location_ID(location.getC_Location_ID());
			//	Phone
			if(!Util.isEmpty(request.getPhone())) {
				businessPartnerLocation.setPhone(request.getPhone());
			}
			//	Contact
			if(!Util.isEmpty(request.getContactName())) {
				businessPartnerLocation.setContactPerson(request.getContactName());
			}
			//	Save
			businessPartnerLocation.saveEx();
			//	Set Location
			contact.setC_BPartner_Location_ID(businessPartnerLocation.getC_BPartner_Location_ID());
			contact.saveEx();
 		}
		//	Default return
		return ConvertUtil.convertBusinessPartner(businessPartner);
	}
	
	/**
	 * Get business partner
	 * @param request
	 * @return
	 */
	private BusinessPartner.Builder getBusinessPartner(GetBusinessPartnerRequest request) {
		//	Dynamic where clause
		StringBuffer whereClause = new StringBuffer();
		//	Parameters
		List<Object> parameters = new ArrayList<Object>();
		//	For search value
		if(!Util.isEmpty(request.getSearchValue())) {
			whereClause.append("("
				+ "UPPER(Value) = UPPER(?)"
				+ "OR UPPER(Name) = UPPER(?)"
				+ ")");
			//	Add parameters
			parameters.add(request.getSearchValue());
			parameters.add(request.getSearchValue());
		}
		//	For value
		if(!Util.isEmpty(request.getValue())) {
			whereClause.append("("
				+ "UPPER(Value) = UPPER(?)"
				+ ")");
			//	Add parameters
			parameters.add(request.getValue());
		}
		//	For name
		if(!Util.isEmpty(request.getName())) {
			whereClause.append("("
				+ "UPPER(Name) = UPPER(?)"
				+ ")");
			//	Add parameters
			parameters.add(request.getName());
		}
		//	for contact name
		if(!Util.isEmpty(request.getContactName())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("(EXISTS(SELECT 1 FROM AD_User u WHERE u.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(u.Name) = UPPER(?)))");
				//	Add parameters
				parameters.add(request.getContactName());
			}
		}
		//	EMail
		if(!Util.isEmpty(request.getEMail())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("(EXISTS(SELECT 1 FROM AD_User u WHERE u.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(u.EMail) = UPPER(?)))");
				//	Add parameters
				parameters.add(request.getEMail());
			}
		}
		//	Phone
		if(!Util.isEmpty(request.getPhone())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("("
						+ "EXISTS(SELECT 1 FROM AD_User u WHERE u.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(u.Phone) = UPPER(?)) "
						+ "OR EXISTS(SELECT 1 FROM C_BPartner_Location bpl WHERE bpl.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(bpl.Phone) = UPPER(?))"
						+ ")");
				//	Add parameters
				parameters.add(request.getPhone());
				parameters.add(request.getPhone());
			}
		}
		//	Postal Code
		if(!Util.isEmpty(request.getPostalCode())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("(EXISTS(SELECT 1 FROM C_BPartner_Location bpl "
						+ "INNER JOIN C_Location l ON(l.C_Location_ID = bpl.C_Location_ID) "
						+ "WHERE bpl.C_BPartner_ID = C_BPartner.C_BPartner_ID "
						+ "AND UPPER(l.Postal) = UPPER(?)))");
				//	Add parameters
				parameters.add(request.getPostalCode());
			}
		}
		//	Get business partner
		MBPartner businessPartner = new Query(Env.getCtx(), I_C_BPartner.Table_Name, 
				whereClause.toString(), null)
				.setParameters(parameters)
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.first();
		//	Default return
		return ConvertUtil.convertBusinessPartner(businessPartner);
	}
}

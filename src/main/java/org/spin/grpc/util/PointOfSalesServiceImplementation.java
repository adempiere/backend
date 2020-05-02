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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_POS;
import org.compiere.model.I_M_PriceList;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MPOS;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategory;
import org.compiere.model.MProductPrice;
import org.compiere.model.MProductPricing;
import org.compiere.model.MStorage;
import org.compiere.model.MTax;
import org.compiere.model.MUOM;
import org.compiere.model.MUser;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
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
	/**	Warehouse Cache	*/
	private static CCache<String, MWarehouse> warehouseCache = new CCache<String, MWarehouse>(I_M_Warehouse.Table_Name, 30, 0);	//	no time-out
	/**	Price List Cache	*/
	private static CCache<String, MPriceList> priceListCache = new CCache<String, MPriceList>(I_M_PriceList.Table_Name, 30, 0);	//	no time-out
	/**	POS Cache	*/
	private static CCache<String, MPOS> posCache = new CCache<String, MPOS>(I_C_POS.Table_Name, 30, 0);	//	no time-out
	/**	Business Partner	*/
	private static CCache<String, MBPartner> businessPartnerCache = new CCache<String, MBPartner>(I_C_BPartner.Table_Name, 30, 0);	//	no time-out
	/**	Document Type	*/
	private static CCache<String, MDocType> documentTypeCache = new CCache<String, MDocType>(I_C_DocType.Table_Name, 30, 0);	//	no time-out
	
	@Override
	public void getProductPrice(GetProductPriceRequest request, StreamObserver<ProductPrice> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getSearchValue());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ProductPrice.Builder productPrice = getProductPrice(context, request);
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
	
	/**
	 * Get POS builder
	 * @param context
	 * @param request
	 * @return
	 */
	private PointOfSales.Builder getPosBuilder(Properties context, PointOfSalesRequest request) {
		MPOS pos = getPos(request.getPointOfSalesUuid());
		if(pos == null) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		//	
		return convertPos(pos);
	}
	
	/**
	 * Convert POS
	 * @param pos
	 * @return
	 */
	private PointOfSales.Builder convertPos(MPOS pos) {
		return PointOfSales.newBuilder()
				.setUuid(ValueUtil.validateNull(pos.getUUID()))
				.setId(pos.getC_POS_ID())
				.setName(ValueUtil.validateNull(pos.getName()))
				.setDescription(ValueUtil.validateNull(pos.getDescription()))
				.setHelp(ValueUtil.validateNull(pos.getHelp()))
				.setIsModifyPrice(pos.isModifyPrice())
				.setIsPOSRequiredPIN(pos.isPOSRequiredPIN())
				.setSalesRepresentative(convertSalesRepresentative(MUser.get(pos.getCtx(), pos.getSalesRep_ID())))
				.setTemplateBusinessPartner(convertBusinessPartner(pos.getBPartner()));
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
	 * Convert business partner
	 * @param businessPartner
	 * @return
	 */
	private BusinessPartner.Builder convertBusinessPartner(MBPartner businessPartner) {
		return BusinessPartner.newBuilder()
				.setUuid(ValueUtil.validateNull(businessPartner.getUUID()))
				.setId(businessPartner.getC_BPartner_ID())
				.setValue(ValueUtil.validateNull(businessPartner.getValue()))
				.setTaxId(ValueUtil.validateNull(businessPartner.getTaxID()))
				.setDuns(ValueUtil.validateNull(businessPartner.getDUNS()))
				.setNaics(ValueUtil.validateNull(businessPartner.getNAICS()))
				.setName(ValueUtil.validateNull(businessPartner.getName()))
				.setLastName(ValueUtil.validateNull(businessPartner.getName2()))
				.setDescription(ValueUtil.validateNull(businessPartner.getDescription()));
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
		MPOS pos = getPos(request.getPosUuid());
		if(pos == null) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		//	
		MOrder salesOrder = new Query(context, I_C_Order.Table_Name, 
				"DocStatus NOT IN('CO', 'CL') "
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
		MDocType documentType = null;
		if(!Util.isEmpty(request.getDocumentTypeUuid())) {
			documentType = getDocumentType(request.getDocumentTypeUuid());
		}
		//	Validate
		if(documentType == null
				&& pos.getC_DocType_ID() != 0) {
			documentType = MDocType.get(context, pos.getC_DocType_ID());
		}
		//	Validate
		if(documentType != null) {
			salesOrder.setC_DocTypeTarget_ID(documentType.getC_DocType_ID());
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
		if(Util.isEmpty(businessPartnerUuid)
				|| DocumentUtil.isCompleted(salesOrder)
				|| DocumentUtil.isVoided(salesOrder)) {
			return;
		}
		MBPartner businessPartner = getBusinessPartner(businessPartnerUuid);
		boolean isSamePOSPartner = false;
		if(businessPartner == null) {
			businessPartner = pos.getBPartner();
			isSamePOSPartner = true;
		}
		//	Validate business partner
		if(businessPartner == null) {
			throw new AdempiereException("@C_BPartner_ID@ @NotFound@");
		}
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
//		if(pos.isSharedPOS()) {
//			salesOrder.setSalesRep_ID(Env.getAD_User_ID(salesOrder.getCtx()));
//		} else 
		if (salesOrder.getC_BPartner().getSalesRep_ID() != 0) {
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
		//	Business partner
		int businessPartnerId = businessPartner.getC_BPartner_ID();
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
	 * Get POS from uuid
	 * @param posUuid
	 * @return
	 */
	private MPOS getPos(String posUuid) {
		MPOS pos = posCache.get(posUuid);
		if(pos == null) {
			pos = new Query(Env.getCtx(), I_C_POS.Table_Name, I_C_POS.COLUMNNAME_UUID + " = ?", null)
					.setParameters(posUuid)
					.first();
			if(pos != null
					&& pos.getC_POS_ID() != 0) {
				posCache.put(posUuid, pos);
			}
		}
		return pos;
	}
	
	/**
	 * Get Business Partner uuid
	 * @param businessPartnerUuid
	 * @return
	 */
	private MBPartner getBusinessPartner(String businessPartnerUuid) {
		MBPartner businessPartner = businessPartnerCache.get(businessPartnerUuid);
		if(businessPartner == null) {
			businessPartner = new Query(Env.getCtx(), I_C_BPartner.Table_Name, I_C_BPartner.COLUMNNAME_UUID + " = ?", null)
					.setParameters(businessPartnerUuid)
					.first();
			if(businessPartner != null
					&& businessPartner.getC_BPartner_ID() != 0) {
				businessPartnerCache.put(businessPartnerUuid, businessPartner);
			}
		}
		return businessPartner;
	}
	
	/**
	 * Get Document Type from uuid
	 * @param documentTypeUuid
	 * @return
	 */
	private MDocType getDocumentType(String documentTypeUuid) {
		MDocType documentType = documentTypeCache.get(documentTypeUuid);
		if(documentType == null) {
			documentType = new Query(Env.getCtx(), I_C_DocType.Table_Name, I_C_DocType.COLUMNNAME_UUID + " = ?", null)
					.setParameters(documentTypeUuid)
					.first();
			if(documentType != null
					&& documentType.getC_DocType_ID() != 0) {
				documentTypeCache.put(documentTypeUuid, documentType);
			}
		}
		return documentType;
	}
	
	/**
	 * Get Warehouse from uuid
	 * @param warehouseUuid
	 * @return
	 */
	private MWarehouse getWarehouse(String warehouseUuid) {
		MWarehouse warehouse = warehouseCache.get(warehouseUuid);
		if(warehouse == null) {
			warehouse = new Query(Env.getCtx(), I_M_Warehouse.Table_Name, I_M_Warehouse.COLUMNNAME_UUID + " = ?", null)
					.setParameters(warehouseUuid)
					.first();
			if(warehouse != null
					&& warehouse.getM_Warehouse_ID() != 0) {
				warehouseCache.put(warehouseUuid, warehouse);
			}
		}
		return warehouse;
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
		//	Convert
		
		//	
		return builder;
	}
	
	/**
	 * Get Product Price Method
	 * @param context
	 * @param request
	 * @return
	 */
	private ProductPrice.Builder getProductPrice(Properties context, GetProductPriceRequest request) {
		ProductPrice.Builder builder = ProductPrice.newBuilder();
		//	Get Product
		MProduct product = null;
		String key = Env.getAD_Client_ID(context) + "|";
		if(!Util.isEmpty(request.getSearchValue())) {
			key = key + "SearchValue|" + request.getSearchValue();
			product = productCache.get(key);
			if(product == null) {
				product = new Query(context, I_M_Product.Table_Name, 
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
				Optional<MProduct> optionalProduct = MProduct.getByUPC(context, request.getUpc(), null).stream().findAny();
				if(optionalProduct.isPresent()) {
					product = optionalProduct.get();
				}
			}
		} else if(!Util.isEmpty(request.getValue())) {
			key = key + "Value|" + request.getValue();
			product = productCache.get(key);
			if(product == null) {
				product = new Query(context, I_M_Product.Table_Name, "UPPER(Value) = UPPER(?)", null)
						.setParameters(request.getValue())
						.setClient_ID()
						.setOnlyActiveRecords(true)
						.first();
			}
		} else if(!Util.isEmpty(request.getName())) {
			key = key + "Name|" + request.getName();
			product = productCache.get(key);
			if(product == null) {
				product = new Query(context, I_M_Product.Table_Name, "UPPER(Name) LIKE UPPER(?)", null)
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
		int businessPartnerId = 0;
		if(!Util.isEmpty(request.getBusinessPartnerUuid())) {
			MBPartner businessPartner = new Query(context, I_C_BPartner.Table_Name, I_C_BPartner.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getBusinessPartnerUuid())
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.first();
			if(businessPartner != null
					&& businessPartner.getC_BPartner_ID() > 0) {
				businessPartnerId = businessPartner.getC_BPartner_ID();
			}
		}
		//	Validate Price List
		MPriceList priceList = null;
		if(Util.isEmpty(request.getPriceListUuid())) {
			priceList = new Query(context, I_M_PriceList.Table_Name, "EXISTS(SELECT 1 FROM C_POS p WHERE p.M_PriceList_ID = M_PriceList.M_PriceList_ID AND p.AD_Org_ID IN(0, ?))", null)
					.setParameters(Env.getAD_Org_ID(Env.getCtx()))
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.first();
		} else {
			priceList = priceListCache.get(request.getPriceListUuid());
		}
		if(priceList == null) {
			priceList = new Query(context, I_M_PriceList.Table_Name, I_M_PriceList.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getPriceListUuid())
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.first();
			if(priceList == null) {
				throw new AdempiereException("@M_PriceList_ID@ @NotFound@");
			}
			priceListCache.put(request.getPriceListUuid(), priceList);
		}
		//	Get Valid From
		Timestamp validFrom = TimeUtil.getDay(request.getValidFrom() > 0? request.getValidFrom(): System.currentTimeMillis());
		//	Get Price
		MProductPricing productPricing = new MProductPricing(product.getM_Product_ID(), businessPartnerId, Env.ONE, true, null);
		productPricing.setM_PriceList_ID(priceList.getM_PriceList_ID());
		productPricing.setPriceDate(validFrom);
		//	Populate
		builder.setProduct(convertProduct(product));
		int taxCategoryId = product.getC_TaxCategory_ID();
		Optional<MTax> optionalTax = Arrays.asList(MTax.getAll(context))
		.stream()
		.filter(tax -> tax.getC_TaxCategory_ID() == taxCategoryId 
							&& (tax.isSalesTax() 
									|| (!Util.isEmpty(tax.getSOPOType()) 
											&& (tax.getSOPOType().equals(MTax.SOPOTYPE_Both) 
													|| tax.getSOPOType().equals(MTax.SOPOTYPE_SalesTax)))))
		.findFirst();
		//	Validate
		if(optionalTax.isPresent()) {
			builder.setTaxRate(convertTaxRate(optionalTax.get()));
		}
		//	Set currency
		builder.setCurrency(convertCurrency(MCurrency.get(context, priceList.getC_Currency_ID())));
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
		
		if(!Util.isEmpty(request.getWarehouseUuid())) {
			MWarehouse warehouse = getWarehouse(request.getWarehouseUuid());
			if(warehouse != null) {
				int warehouseId = warehouse.getM_Warehouse_ID();
				AtomicReference<BigDecimal> quantityOnHand = new AtomicReference<BigDecimal>(Env.ZERO);
				AtomicReference<BigDecimal> quantityReserved = new AtomicReference<BigDecimal>(Env.ZERO);
				AtomicReference<BigDecimal> quantityOrdered = new AtomicReference<BigDecimal>(Env.ZERO);
				AtomicReference<BigDecimal> quantityAvailable = new AtomicReference<BigDecimal>(Env.ZERO);
				//	
				Arrays.asList(MStorage.getOfProduct(context, product.getM_Product_ID(), null))
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
		}
		
		return builder;
	}
	
	/**
	 * Convert tax to gRPC
	 * @param tax
	 * @return
	 */
	private TaxRate.Builder convertTaxRate(MTax tax) {
		return TaxRate.newBuilder().setName(ValueUtil.validateNull(tax.getName()))
			.setDescription(ValueUtil.validateNull(tax.getDescription()))
			.setTaxIndicator(ValueUtil.validateNull(tax.getTaxIndicator()))
			.setRate(ValueUtil.getDecimalFromBigDecimal(tax.getRate()));
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
	
	/**
	 * Convert Product to 
	 * @param product
	 * @return
	 */
	private Product.Builder convertProduct(MProduct product) {
		Product.Builder builder = Product.newBuilder();
		builder.setUuid(ValueUtil.validateNull(product.getUUID()))
				.setId(product.getM_Product_ID())
				.setValue(ValueUtil.validateNull(product.getValue()))
				.setName(ValueUtil.validateNull(product.getName()))
				.setDescription(ValueUtil.validateNull(product.getDescription()))
				.setHelp(ValueUtil.validateNull(product.getHelp()))
				.setDocumentNote(ValueUtil.validateNull(product.getDocumentNote()))
				.setUomName(ValueUtil.validateNull(MUOM.get(product.getCtx(), product.getC_UOM_ID()).getName()))
				.setDescriptionURL(ValueUtil.validateNull(product.getDescriptionURL()))
				//	Product Type
				.setIsStocked(product.isStocked())
				.setIsDropShip(product.isDropShip())
				.setIsPurchased(product.isPurchased())
				.setIsSold(product.isSold())
				.setImageURL(ValueUtil.validateNull(product.getImageURL()))
				.setUpc(ValueUtil.validateNull(product.getUPC()))
				.setSku(ValueUtil.validateNull(product.getSKU()))
				.setVersionNo(ValueUtil.validateNull(product.getVersionNo()))
				.setGuaranteeDays(product.getGuaranteeDays())
				.setWeight(ValueUtil.getDecimalFromBigDecimal(product.getWeight()))
				.setVolume(ValueUtil.getDecimalFromBigDecimal(product.getVolume()))
				.setShelfDepth(product.getShelfDepth())
				.setShelfHeight(ValueUtil.getDecimalFromBigDecimal(product.getShelfHeight()))
				.setShelfWidth(product.getShelfWidth())
				.setUnitsPerPallet(ValueUtil.getDecimalFromBigDecimal(product.getUnitsPerPallet()))
				.setUnitsPerPack(product.getUnitsPerPack())
				.setTaxCategory(ValueUtil.validateNull(product.getC_TaxCategory().getName()))
				.setProductCategoryName(ValueUtil.validateNull(MProductCategory.get(product.getCtx(), product.getM_Product_Category_ID()).getName()));
		//	Group
		if(product.getM_Product_Group_ID() != 0) {
			builder.setProductGroupName(ValueUtil.validateNull(product.getM_Product_Group().getName()));
		}
		//	Class
		if(product.getM_Product_Class_ID() != 0) {
			builder.setProductClassName(ValueUtil.validateNull(product.getM_Product_Class().getName()));
		}
		//	Classification
		if(product.getM_Product_Classification_ID() != 0) {
			builder.setProductClassificationName(ValueUtil.validateNull(product.getM_Product_Classification().getName()));
		}
		return builder;
	}
}

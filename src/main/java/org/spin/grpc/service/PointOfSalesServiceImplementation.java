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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.pos.service.CPOS;
import org.adempiere.pos.util.POSTicketHandler;
import org.compiere.model.I_AD_Ref_List;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_C_BP_BankAccount;
import org.compiere.model.I_C_BP_Group;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Bank;
import org.compiere.model.I_C_Charge;
import org.compiere.model.I_C_City;
import org.compiere.model.I_C_ConversionType;
import org.compiere.model.I_C_Country;
import org.compiere.model.I_C_Currency;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_C_POS;
import org.compiere.model.I_C_POSKeyLayout;
import org.compiere.model.I_C_Payment;
import org.compiere.model.I_C_Region;
import org.compiere.model.I_M_PriceList;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MBank;
import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MCharge;
import org.compiere.model.MCity;
import org.compiere.model.MConversionRate;
import org.compiere.model.MCountry;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPOS;
import org.compiere.model.MPOSKey;
import org.compiere.model.MPOSKeyLayout;
import org.compiere.model.MPayment;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.MProductPricing;
import org.compiere.model.MRefList;
import org.compiere.model.MRegion;
import org.compiere.model.MStorage;
import org.compiere.model.MTable;
import org.compiere.model.MTax;
import org.compiere.model.MUser;
import org.compiere.model.MWarehouse;
import org.compiere.model.M_Element;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.spin.base.util.ContextManager;
import org.spin.base.util.ConvertUtil;
import org.spin.base.util.DocumentUtil;
import org.spin.base.util.RecordUtil;
import org.spin.base.util.ValueUtil;
import org.spin.grpc.util.Address;
import org.spin.grpc.util.AvailableDocumentType;
import org.spin.grpc.util.AvailablePaymentMethod;
import org.spin.grpc.util.AvailablePriceList;
import org.spin.grpc.util.AvailableRefund;
import org.spin.grpc.util.AvailableWarehouse;
import org.spin.grpc.util.Charge;
import org.spin.grpc.util.City;
import org.spin.grpc.util.CreateCustomerBankAccountRequest;
import org.spin.grpc.util.CreateCustomerRequest;
import org.spin.grpc.util.CreateOrderLineRequest;
import org.spin.grpc.util.CreateOrderRequest;
import org.spin.grpc.util.CreatePaymentRequest;
import org.spin.grpc.util.Customer;
import org.spin.grpc.util.CustomerBankAccount;
import org.spin.grpc.util.DeleteCustomerBankAccountRequest;
import org.spin.grpc.util.DeleteOrderLineRequest;
import org.spin.grpc.util.DeleteOrderRequest;
import org.spin.grpc.util.DeletePaymentRequest;
import org.spin.grpc.util.Empty;
import org.spin.grpc.util.GetAvailableRefundRequest;
import org.spin.grpc.util.GetCustomerBankAccountRequest;
import org.spin.grpc.util.GetCustomerRequest;
import org.spin.grpc.util.GetKeyLayoutRequest;
import org.spin.grpc.util.GetOrderRequest;
import org.spin.grpc.util.GetProductPriceRequest;
import org.spin.grpc.util.Key;
import org.spin.grpc.util.KeyLayout;
import org.spin.grpc.util.ListAvailableCurrenciesRequest;
import org.spin.grpc.util.ListAvailableCurrenciesResponse;
import org.spin.grpc.util.ListAvailableDocumentTypesRequest;
import org.spin.grpc.util.ListAvailableDocumentTypesResponse;
import org.spin.grpc.util.ListAvailablePaymentMethodsRequest;
import org.spin.grpc.util.ListAvailablePaymentMethodsResponse;
import org.spin.grpc.util.ListAvailablePriceListRequest;
import org.spin.grpc.util.ListAvailablePriceListResponse;
import org.spin.grpc.util.ListAvailableWarehousesRequest;
import org.spin.grpc.util.ListAvailableWarehousesResponse;
import org.spin.grpc.util.ListCustomerBankAccountsRequest;
import org.spin.grpc.util.ListCustomerBankAccountsResponse;
import org.spin.grpc.util.ListOrderLinesRequest;
import org.spin.grpc.util.ListOrderLinesResponse;
import org.spin.grpc.util.ListOrdersRequest;
import org.spin.grpc.util.ListOrdersResponse;
import org.spin.grpc.util.ListPaymentsRequest;
import org.spin.grpc.util.ListPaymentsResponse;
import org.spin.grpc.util.ListPointOfSalesRequest;
import org.spin.grpc.util.ListPointOfSalesResponse;
import org.spin.grpc.util.ListProductPriceRequest;
import org.spin.grpc.util.ListProductPriceResponse;
import org.spin.grpc.util.Order;
import org.spin.grpc.util.OrderLine;
import org.spin.grpc.util.Payment;
import org.spin.grpc.util.PointOfSales;
import org.spin.grpc.util.PointOfSalesRequest;
import org.spin.grpc.util.PrintTicketRequest;
import org.spin.grpc.util.PrintTicketResponse;
import org.spin.grpc.util.ProcessOrderRequest;
import org.spin.grpc.util.Product;
import org.spin.grpc.util.ProductPrice;
import org.spin.grpc.util.Region;
import org.spin.grpc.util.SalesRepresentative;
import org.spin.grpc.util.StoreGrpc.StoreImplBase;
import org.spin.grpc.util.UpdateCustomerBankAccountRequest;
import org.spin.grpc.util.UpdateCustomerRequest;
import org.spin.grpc.util.UpdateOrderLineRequest;
import org.spin.grpc.util.UpdateOrderRequest;
import org.spin.grpc.util.UpdatePaymentRequest;
import org.spin.grpc.util.ValidatePINRequest;
import org.spin.grpc.util.Warehouse;
import org.spin.model.I_C_PaymentMethod;
import org.spin.util.VueStoreFrontUtil;

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
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Order.Builder order = createOrder(request);
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
			log.fine("Get Point of Sales = " + request.getPosUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			PointOfSales.Builder pos = getPosBuilder(request);
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
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListPointOfSalesResponse.Builder posList = convertPointOfSalesList(request);
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
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			OrderLine.Builder orderLine = createAndConvertOrderLine(request);
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
			log.fine("Create Order = " + request.getOrderUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Order.Builder order = convertOrder(getOrder(request.getOrderUuid(), null));
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
	public void createPayment(CreatePaymentRequest request, StreamObserver<Payment> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Create Order = " + request.getOrderUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Payment.Builder payment = convertPayment(createPayment(request));
			responseObserver.onNext(payment.build());
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
	public void updatePayment(UpdatePaymentRequest request, StreamObserver<Payment> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Create Order = " + request.getPaymentUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Payment.Builder payment = convertPayment(updatePayment(request));
			responseObserver.onNext(payment.build());
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
	public void deletePayment(DeletePaymentRequest request, StreamObserver<Empty> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Create Order = " + request.getPaymentUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Empty.Builder empty = deletePayment(request);
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
	public void listPayments(ListPaymentsRequest request, StreamObserver<ListPaymentsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("List Payment = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListPaymentsResponse.Builder paymentList = listPayments(request);
			responseObserver.onNext(paymentList.build());
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
			KeyLayout.Builder keyLayout = convertKeyLayout(RecordUtil.getIdFromUuid(I_C_POSKeyLayout.Table_Name, request.getKeyLayoutUuid(), null));
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
	public void updateOrder(UpdateOrderRequest request, StreamObserver<Order> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Update Order = " + request.getOrderUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Order.Builder order = convertOrder(updateOrder(request));
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
	public void getAvailableRefund(GetAvailableRefundRequest request, StreamObserver<AvailableRefund> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Available Refund = " + request.getDate());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			AvailableRefund.Builder availableRefund = getAvailableRefund(request);
			responseObserver.onNext(availableRefund.build());
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
	 * Calculate Available refund from daily operations
	 * @param request
	 * @return
	 * @return AvailableRefund.Builder
	 */
	private AvailableRefund.Builder getAvailableRefund(GetAvailableRefundRequest request) {
		return AvailableRefund.newBuilder();
	}
	
	@Override
	public void processOrder(ProcessOrderRequest request, StreamObserver<Order> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Update Order = " + request.getOrderUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Order.Builder order = convertOrder(processOrder(request));
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
	public void validatePIN(ValidatePINRequest request, StreamObserver<Empty> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Validate PIN = " + request.getPosUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Empty.Builder empty = validatePIN(request);
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
	public void listAvailableWarehouses(ListAvailableWarehousesRequest request,
			StreamObserver<ListAvailableWarehousesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("List Available Warehouses = " + request.getPosUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListAvailableWarehousesResponse.Builder warehouses = listWarehouses(request);
			responseObserver.onNext(warehouses.build());
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
	public void listAvailablePriceList(ListAvailablePriceListRequest request,
			StreamObserver<ListAvailablePriceListResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("List Available Price List = " + request.getPosUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListAvailablePriceListResponse.Builder priceList = listPriceList(request);
			responseObserver.onNext(priceList.build());
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
	public void listAvailablePaymentMethods(ListAvailablePaymentMethodsRequest request,
			StreamObserver<ListAvailablePaymentMethodsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("List Available Tender Types = " + request.getPosUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListAvailablePaymentMethodsResponse.Builder tenderTypes = listPaymentMethods(request);
			responseObserver.onNext(tenderTypes.build());
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
	public void listAvailableDocumentTypes(ListAvailableDocumentTypesRequest request,
			StreamObserver<ListAvailableDocumentTypesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("List Available Tender Types = " + request.getPosUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListAvailableDocumentTypesResponse.Builder documentTypes = listDocumentTypes(request);
			responseObserver.onNext(documentTypes.build());
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
	public void listAvailableCurrencies(ListAvailableCurrenciesRequest request,
			StreamObserver<ListAvailableCurrenciesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("List Available Warehouses = " + request.getPosUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListAvailableCurrenciesResponse.Builder currencies = listCurrencies(request);
			responseObserver.onNext(currencies.build());
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
	public void createCustomer(CreateCustomerRequest request, StreamObserver<Customer> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Create customer = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Customer.Builder customer = createCustomer(request);
			responseObserver.onNext(customer.build());
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
	public void updateCustomer(UpdateCustomerRequest request, StreamObserver<Customer> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Update customer = " + request.getUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Customer.Builder customer = updateCustomer(request);
			responseObserver.onNext(customer.build());
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
	public void getCustomer(GetCustomerRequest request, StreamObserver<Customer> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Get customer = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Customer.Builder customer = getCustomer(request);
			responseObserver.onNext(customer.build());
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
	public void printTicket(PrintTicketRequest request, StreamObserver<PrintTicketResponse> responseObserver) {
		try {
			if(Util.isEmpty(request.getPosUuid())) {
				throw new AdempiereException("@C_POS_ID@ @NotFound@");
			}
			if(Util.isEmpty(request.getOrderUuid())) {
				throw new AdempiereException("@C_Order_ID@ @NotFound@");
			}
			log.fine("Print Ticket = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			//	
			int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
			int orderId = RecordUtil.getIdFromUuid(I_C_Order.Table_Name, request.getOrderUuid(), null);
			MPOS pos = MPOS.get(Env.getCtx(), posId);
			Env.clearWinContext(1);
			CPOS posController = new CPOS();
			posController.setOrder(orderId);
			posController.setM_POS(pos);
			posController.setWindowNo(1);
			POSTicketHandler handler = POSTicketHandler.getTicketHandler(posController);
			if(handler == null) {
				throw new AdempiereException("@TicketClassName@ " + pos.getTicketClassName() + " @NotFound@");
			}
			//	Print it
			handler.printTicket();
			PrintTicketResponse.Builder ticket = PrintTicketResponse.newBuilder().setResult("Ok");
			responseObserver.onNext(ticket.build());
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
	public void createCustomerBankAccount(CreateCustomerBankAccountRequest request, StreamObserver<CustomerBankAccount> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Get customer = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			//	Validate name
			if(Util.isEmpty(request.getCustomerUuid())) {
				throw new AdempiereException("@C_BPartner_ID@ @IsMandatory@");
			}
			MBPartner businessPartner = MBPartner.get(Env.getCtx(), RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getCustomerUuid(), null));
			//	For data
			MBPBankAccount businessPartnerBankAccount = new MBPBankAccount(Env.getCtx(), 0, null);
			businessPartnerBankAccount.setC_BPartner_ID(businessPartner.getC_BPartner_ID());
			businessPartnerBankAccount.setIsACH(request.getIsAch());
			//	Validate all data
			Optional.ofNullable(request.getCity()).ifPresent(value -> businessPartnerBankAccount.setA_City(value));
			Optional.ofNullable(request.getCountry()).ifPresent(value -> businessPartnerBankAccount.setA_Country(value));
			Optional.ofNullable(request.getEmail()).ifPresent(value -> businessPartnerBankAccount.setA_EMail(value));
			Optional.ofNullable(request.getDriverLicense()).ifPresent(value -> businessPartnerBankAccount.setA_Ident_DL(value));
			Optional.ofNullable(request.getSocialSecurityNumber()).ifPresent(value -> businessPartnerBankAccount.setA_Ident_SSN(value));
			Optional.ofNullable(request.getName()).ifPresent(value -> businessPartnerBankAccount.setA_Name(value));
			Optional.ofNullable(request.getState()).ifPresent(value -> businessPartnerBankAccount.setA_State(value));
			Optional.ofNullable(request.getStreet()).ifPresent(value -> businessPartnerBankAccount.setA_Street(value));
			Optional.ofNullable(request.getZip()).ifPresent(value -> businessPartnerBankAccount.setA_Zip(value));
			if(!Util.isEmpty(request.getBankUuid())) {
				businessPartnerBankAccount.setC_Bank_ID(RecordUtil.getIdFromUuid(I_C_Bank.Table_Name, request.getBankUuid(), null));
			}
			Optional.ofNullable(request.getAddressVerified()).ifPresent(value -> businessPartnerBankAccount.setR_AvsAddr(value));
			Optional.ofNullable(request.getZipVerified()).ifPresent(value -> businessPartnerBankAccount.setR_AvsZip(value));
			Optional.ofNullable(request.getRoutingNo()).ifPresent(value -> businessPartnerBankAccount.setAccountNo(value));
			Optional.ofNullable(request.getIban()).ifPresent(value -> businessPartnerBankAccount.setIBAN(value));
			//	Bank Account Type
			if(Util.isEmpty(request.getBankAccountType())) {
				businessPartnerBankAccount.setBankAccountType(MBPBankAccount.BANKACCOUNTTYPE_Savings);
			} else {
				businessPartnerBankAccount.setBankAccountType(request.getBankAccountType());
			}
			businessPartnerBankAccount.saveEx();
			responseObserver.onNext(convertCustomerBankAccount(businessPartnerBankAccount).build());
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
	public void updateCustomerBankAccount(UpdateCustomerBankAccountRequest request, StreamObserver<CustomerBankAccount> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Update customer bank account = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			//	Validate name
			if(Util.isEmpty(request.getCustomerBankAccountUuid())) {
				throw new AdempiereException("@C_BPBankAccount_ID@ @IsMandatory@");
			}
			//	For data
			MBPBankAccount businessPartnerBankAccount = new MBPBankAccount(Env.getCtx(), RecordUtil.getIdFromUuid(I_C_BP_BankAccount.Table_Name, request.getCustomerBankAccountUuid(), null), null);
			businessPartnerBankAccount.setIsACH(request.getIsAch());
			//	Validate all data
			Optional.ofNullable(request.getCity()).ifPresent(value -> businessPartnerBankAccount.setA_City(value));
			Optional.ofNullable(request.getCountry()).ifPresent(value -> businessPartnerBankAccount.setA_Country(value));
			Optional.ofNullable(request.getEmail()).ifPresent(value -> businessPartnerBankAccount.setA_EMail(value));
			Optional.ofNullable(request.getDriverLicense()).ifPresent(value -> businessPartnerBankAccount.setA_Ident_DL(value));
			Optional.ofNullable(request.getSocialSecurityNumber()).ifPresent(value -> businessPartnerBankAccount.setA_Ident_SSN(value));
			Optional.ofNullable(request.getName()).ifPresent(value -> businessPartnerBankAccount.setA_Name(value));
			Optional.ofNullable(request.getState()).ifPresent(value -> businessPartnerBankAccount.setA_State(value));
			Optional.ofNullable(request.getStreet()).ifPresent(value -> businessPartnerBankAccount.setA_Street(value));
			Optional.ofNullable(request.getZip()).ifPresent(value -> businessPartnerBankAccount.setA_Zip(value));
			if(!Util.isEmpty(request.getBankUuid())) {
				businessPartnerBankAccount.setC_Bank_ID(RecordUtil.getIdFromUuid(I_C_Bank.Table_Name, request.getBankUuid(), null));
			}
			Optional.ofNullable(request.getAddressVerified()).ifPresent(value -> businessPartnerBankAccount.setR_AvsAddr(value));
			Optional.ofNullable(request.getZipVerified()).ifPresent(value -> businessPartnerBankAccount.setR_AvsZip(value));
			Optional.ofNullable(request.getRoutingNo()).ifPresent(value -> businessPartnerBankAccount.setAccountNo(value));
			Optional.ofNullable(request.getIban()).ifPresent(value -> businessPartnerBankAccount.setIBAN(value));
			//	Bank Account Type
			if(Util.isEmpty(request.getBankAccountType())) {
				businessPartnerBankAccount.setBankAccountType(MBPBankAccount.BANKACCOUNTTYPE_Savings);
			} else {
				businessPartnerBankAccount.setBankAccountType(request.getBankAccountType());
			}
			businessPartnerBankAccount.saveEx();
			responseObserver.onNext(convertCustomerBankAccount(businessPartnerBankAccount).build());
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
	public void getCustomerBankAccount(GetCustomerBankAccountRequest request, StreamObserver<CustomerBankAccount> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Get customer bank account = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			//	Validate name
			if(Util.isEmpty(request.getCustomerBankAccountUuid())) {
				throw new AdempiereException("@C_BP_BankAccount_ID@ @IsMandatory@");
			}
			//	For data
			MBPBankAccount businessPartnerBankAccount = new MBPBankAccount(Env.getCtx(), RecordUtil.getIdFromUuid(I_C_BP_BankAccount.Table_Name, request.getCustomerBankAccountUuid(), null), null);
			responseObserver.onNext(convertCustomerBankAccount(businessPartnerBankAccount).build());
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
	public void deleteCustomerBankAccount(DeleteCustomerBankAccountRequest request, StreamObserver<Empty> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Delete customer bank account = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			//	Validate name
			if(Util.isEmpty(request.getCustomerBankAccountUuid())) {
				throw new AdempiereException("@C_BP_BankAccount_ID@ @IsMandatory@");
			}
			//	For data
			MBPBankAccount businessPartnerBankAccount = new MBPBankAccount(Env.getCtx(), RecordUtil.getIdFromUuid(I_C_BP_BankAccount.Table_Name, request.getCustomerBankAccountUuid(), null), null);
			businessPartnerBankAccount.deleteEx(true);
			responseObserver.onNext(Empty.newBuilder().build());
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
	public void listCustomerBankAccounts(ListCustomerBankAccountsRequest request, StreamObserver<ListCustomerBankAccountsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("list customer bank accounts = " + request);
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			//	Validate name
			if(Util.isEmpty(request.getCustomerUuid())) {
				throw new AdempiereException("@C_BPartner_ID@ @IsMandatory@");
			}
			//	For data
			ListCustomerBankAccountsResponse.Builder builder = ListCustomerBankAccountsResponse.newBuilder();
			int customerId = RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getCustomerUuid(), null);
			String nexPageToken = null;
			int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
			int limit = RecordUtil.PAGE_SIZE;
			int offset = pageNumber * RecordUtil.PAGE_SIZE;
			//	Dynamic where clause
			//	Get Product list
			Query query = new Query(Env.getCtx(), I_C_BP_BankAccount.Table_Name, I_C_BP_BankAccount.COLUMNNAME_C_BPartner_ID + " = ?", null)
					.setParameters(customerId)
					.setClient_ID()
					.setOnlyActiveRecords(true);
			int count = query.count();
			query
			.setLimit(limit, offset)
			.<MBPBankAccount>list()
			.forEach(customerBankAccount -> {
				builder.addCustomerBankAccounts(convertCustomerBankAccount(customerBankAccount));
			});
			//	
			builder.setRecordCount(count);
			//	Set page token
			if(count > offset && count > limit) {
				nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
			}
			//	Set next page
			builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
			responseObserver.onNext(builder.build());
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
	 * Convert customer bank account
	 * @param customerBankAccount
	 * @return
	 * @return CustomerBankAccount.Builder
	 */
	private CustomerBankAccount.Builder convertCustomerBankAccount(MBPBankAccount customerBankAccount) {
		CustomerBankAccount.Builder builder = CustomerBankAccount.newBuilder();
		builder.setCustomerBankAccountUuid(ValueUtil.validateNull(customerBankAccount.getUUID()))
			.setCity(ValueUtil.validateNull(customerBankAccount.getA_City()))
			.setCountry(ValueUtil.validateNull(customerBankAccount.getA_Country()))
			.setEmail(ValueUtil.validateNull(customerBankAccount.getA_EMail()))
			.setDriverLicense(ValueUtil.validateNull(customerBankAccount.getA_Ident_DL()))
			.setSocialSecurityNumber(ValueUtil.validateNull(customerBankAccount.getA_Ident_SSN()))
			.setName(ValueUtil.validateNull(customerBankAccount.getA_Name()))
			.setState(ValueUtil.validateNull(customerBankAccount.getA_State()))
			.setStreet(ValueUtil.validateNull(customerBankAccount.getA_Street()))
			.setZip(ValueUtil.validateNull(customerBankAccount.getA_Zip()))
			.setBankAccountType(ValueUtil.validateNull(customerBankAccount.getBankAccountType()));
		if(customerBankAccount.getC_Bank_ID() > 0) {
			MBank bank = MBank.get(Env.getCtx(), customerBankAccount.getC_Bank_ID());
			builder.setBankUuid(ValueUtil.validateNull(bank.getUUID()));
		}
		MBPartner customer = MBPartner.get(Env.getCtx(), customerBankAccount.getC_BPartner_ID());
		builder.setCustomerUuid(ValueUtil.validateNull(customer.getUUID()));
		builder.setAddressVerified(ValueUtil.validateNull(customerBankAccount.getR_AvsAddr()))
			.setZipVerified(ValueUtil.validateNull(customerBankAccount.getR_AvsZip()))
			.setRoutingNo(ValueUtil.validateNull(customerBankAccount.getRoutingNo()))
			.setIban(ValueUtil.validateNull(customerBankAccount.getIBAN())) ;
		return builder;
	}
	
	/**
	 * Get Customer
	 * @param request
	 * @return
	 */
	private Customer.Builder getCustomer(GetCustomerRequest request) {
		//	Dynamic where clause
		StringBuffer whereClause = new StringBuffer();
		//	Parameters
		List<Object> parameters = new ArrayList<Object>();
		//	For search value
		if(!Util.isEmpty(request.getSearchValue())) {
			whereClause.append("("
				+ "UPPER(Value) = UPPER(?) "
				+ "OR UPPER(Name) = UPPER(?)"
				+ ")");
			//	Add parameters
			parameters.add(request.getSearchValue());
			parameters.add(request.getSearchValue());
		}
		//	For value
		if(!Util.isEmpty(request.getValue())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
			whereClause.append("("
				+ "UPPER(Value) = UPPER(?)"
				+ ")");
			//	Add parameters
			parameters.add(request.getValue());
		}
		//	For name
		if(!Util.isEmpty(request.getName())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
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
			}
			whereClause.append("(EXISTS(SELECT 1 FROM AD_User u WHERE u.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(u.Name) = UPPER(?)))");
			//	Add parameters
			parameters.add(request.getContactName());
		}
		//	EMail
		if(!Util.isEmpty(request.getEmail())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
			whereClause.append("(EXISTS(SELECT 1 FROM AD_User u WHERE u.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(u.EMail) = UPPER(?)))");
			//	Add parameters
			parameters.add(request.getEmail());
		}
		//	Phone
		if(!Util.isEmpty(request.getPhone())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
			whereClause.append("("
					+ "EXISTS(SELECT 1 FROM AD_User u WHERE u.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(u.Phone) = UPPER(?)) "
					+ "OR EXISTS(SELECT 1 FROM C_BPartner_Location bpl WHERE bpl.C_BPartner_ID = C_BPartner.C_BPartner_ID AND UPPER(bpl.Phone) = UPPER(?))"
					+ ")");
			//	Add parameters
			parameters.add(request.getPhone());
			parameters.add(request.getPhone());
		}
		//	Postal Code
		if(!Util.isEmpty(request.getPostalCode())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
			whereClause.append("(EXISTS(SELECT 1 FROM C_BPartner_Location bpl "
					+ "INNER JOIN C_Location l ON(l.C_Location_ID = bpl.C_Location_ID) "
					+ "WHERE bpl.C_BPartner_ID = C_BPartner.C_BPartner_ID "
					+ "AND UPPER(l.Postal) = UPPER(?)))");
			//	Add parameters
			parameters.add(request.getPostalCode());
		}
		//	Get business partner
		MBPartner businessPartner = new Query(Env.getCtx(), I_C_BPartner.Table_Name, 
				whereClause.toString(), null)
				.setParameters(parameters)
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.first();
		//	Default return
		return convertCustomer(businessPartner);
	}
	
	/**
	 * Create Customer
	 * @param request
	 * @return
	 */
	private Customer.Builder createCustomer(CreateCustomerRequest request) {
		//	Validate name
		if(Util.isEmpty(request.getName())) {
			throw new AdempiereException("@Name@ @IsMandatory@");
		}
		//	POS Uuid
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @IsMandatory@");
		}
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		MPOS pos = MPOS.get(Env.getCtx(), posId);
		MBPartner businessPartner = MBPartner.getTemplate(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()), posId);
		//	Validate Template
		if(pos.getC_BPartnerCashTrx_ID() <= 0) {
			throw new AdempiereException("@C_BPartnerCashTrx_ID@ @NotFound@");
		}
		MBPartner template = MBPartner.get(Env.getCtx(), pos.getC_BPartnerCashTrx_ID());
		Optional<MBPartnerLocation> maybeTemplateLocation = Arrays.asList(template.getLocations(false)).stream().findFirst();
		if(!maybeTemplateLocation.isPresent()) {
			throw new AdempiereException("@C_BPartnerCashTrx_ID@ @C_BPartner_Location_ID@ @NotFound@");
		}
		//	Get location from template
		MLocation templateLocation = maybeTemplateLocation.get().getLocation(false);
		if(templateLocation == null
				|| templateLocation.getC_Location_ID() <= 0) {
			throw new AdempiereException("@C_Location_ID@ @NotFound@");
		}
		Trx.run(transactionName -> {
			//	Create it
			businessPartner.setAD_Org_ID(0);
			businessPartner.setIsCustomer (true);
			businessPartner.setIsVendor (false);
			businessPartner.set_TrxName(transactionName);
			//	Set Value
			String code = request.getValue();
			if(Util.isEmpty(code)) {
				code = DB.getDocumentNo(Env.getAD_Client_ID(Env.getCtx()), "C_BPartner", transactionName, businessPartner);
			}
			//	
			businessPartner.setValue(code);
			//	Set Value
			Optional.ofNullable(request.getValue()).ifPresent(value -> businessPartner.setValue(value));			
			//	Tax Id
			Optional.ofNullable(request.getTaxId()).ifPresent(value -> businessPartner.setTaxID(value));
			//	Duns
			Optional.ofNullable(request.getDuns()).ifPresent(value -> businessPartner.setDUNS(value));
			//	Naics
			Optional.ofNullable(request.getNaics()).ifPresent(value -> businessPartner.setNAICS(value));
			//	Name
			Optional.ofNullable(request.getName()).ifPresent(value -> businessPartner.setName(value));
			//	Last name
			Optional.ofNullable(request.getLastName()).ifPresent(value -> businessPartner.setName2(value));
			//	Description
			Optional.ofNullable(request.getDescription()).ifPresent(value -> businessPartner.setDescription(value));
			//	Business partner group
			if(!Util.isEmpty(request.getBusinessPartnerGroupUuid())) {
				int businessPartnerGroupId = RecordUtil.getIdFromUuid(I_C_BP_Group.Table_Name, request.getBusinessPartnerGroupUuid(), transactionName);
				if(businessPartnerGroupId != 0) {
					businessPartner.setC_BP_Group_ID(businessPartnerGroupId);
				}
			}
			//	Save it
			businessPartner.saveEx(transactionName);
			//	Location
			request.getAddressesList().forEach(address -> {
				int countryId = 0;
				if(!Util.isEmpty(address.getCountryUuid())) {
					countryId = RecordUtil.getIdFromUuid(I_C_Country.Table_Name, address.getCountryUuid(), transactionName);
				}
				//	Instance it
				MLocation location = new MLocation(Env.getCtx(), 0, transactionName);
				if(countryId > 0) {
					int regionId = 0;
					int cityId = 0;
					String cityName = null;
					//	
					if(!Util.isEmpty(address.getRegionUuid())) {
						regionId = RecordUtil.getIdFromUuid(I_C_Region.Table_Name, address.getRegionUuid(), transactionName);
					}
					//	City Name
					if(!Util.isEmpty(address.getCityName())) {
						cityName = address.getCityName();
					}
					//	City Reference
					if(!Util.isEmpty(address.getCityUuid())) {
						cityId = RecordUtil.getIdFromUuid(I_C_City.Table_Name, address.getRegionUuid(), transactionName);
					}
					location.setC_Country_ID(countryId);
					location.setC_Region_ID(regionId);
					location.setCity(cityName);
					if(cityId > 0) {
						location.setC_City_ID(cityId);
					}
				} else {
					//	Copy
					PO.copyValues(templateLocation, location);
				}
				//	Postal Code
				if(!Util.isEmpty(address.getPostalCode())) {
					location.setPostal(address.getPostalCode());
				}
				//	Address
				Optional.ofNullable(address.getAddress1()).ifPresent(addressValue -> location.setAddress1(addressValue));
				Optional.ofNullable(address.getAddress2()).ifPresent(addressValue -> location.setAddress2(addressValue));
				Optional.ofNullable(address.getAddress3()).ifPresent(addressValue -> location.setAddress3(addressValue));
				Optional.ofNullable(address.getAddress4()).ifPresent(addressValue -> location.setAddress4(addressValue));
				Optional.ofNullable(address.getPostalCode()).ifPresent(postalCode -> location.setPostal(postalCode));
				//	
				location.saveEx(transactionName);
				//	Create BP location
				MBPartnerLocation businessPartnerLocation = new MBPartnerLocation(businessPartner);
				businessPartnerLocation.setC_Location_ID(location.getC_Location_ID());
				//	Default
				businessPartnerLocation.setIsBillTo(address.getIsDefaultBilling());
				businessPartnerLocation.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_IsDefaultBilling, address.getIsDefaultBilling());
				businessPartnerLocation.setIsShipTo(address.getIsDefaultShipping());
				businessPartnerLocation.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_IsDefaultShipping, address.getIsDefaultShipping());
				Optional.ofNullable(address.getContactName()).ifPresent(contactName -> businessPartnerLocation.set_ValueOfColumn("ContactName", contactName));
				Optional.ofNullable(address.getContactName()).ifPresent(contact -> businessPartnerLocation.setContactPerson(contact));
				Optional.ofNullable(address.getFirstName()).ifPresent(firstName -> businessPartnerLocation.setName(firstName));
				Optional.ofNullable(address.getLastName()).ifPresent(lastName -> businessPartnerLocation.set_ValueOfColumn("Name2", lastName));
				Optional.ofNullable(address.getEmail()).ifPresent(email -> businessPartnerLocation.setEMail(email));
				Optional.ofNullable(address.getPhone()).ifPresent(phome -> businessPartnerLocation.setPhone(phome));
				Optional.ofNullable(address.getDescription()).ifPresent(description -> businessPartnerLocation.set_ValueOfColumn("Description", description));
				businessPartnerLocation.saveEx(transactionName);
				//	Contact
				if(!Util.isEmpty(address.getContactName()) || !Util.isEmpty(address.getEmail()) || !Util.isEmpty(address.getPhone())) {
					MUser contact = new MUser(businessPartner);
					Optional.ofNullable(address.getEmail()).ifPresent(email -> contact.setEMail(email));
					Optional.ofNullable(address.getPhone()).ifPresent(phome -> contact.setPhone(phome));
					Optional.ofNullable(address.getDescription()).ifPresent(description -> contact.setDescription(description));
					String contactName = address.getContactName();
					if(Util.isEmpty(contactName)) {
						contactName = address.getEmail();
					}
					if(Util.isEmpty(contactName)) {
						contactName = address.getPhone();
					}
					contact.setName(contactName);
					//	Save
					contact.setC_BPartner_Location_ID(businessPartnerLocation.getC_BPartner_Location_ID());
					contact.saveEx(transactionName);
		 		}
			});
		});
		//	Default return
		return convertCustomer(businessPartner);
	}
	
	/**
	 * update Customer
	 * @param request
	 * @return
	 */
	private Customer.Builder updateCustomer(UpdateCustomerRequest request) {
		//	Customer Uuid
		if(Util.isEmpty(request.getUuid())) {
			throw new AdempiereException("@C_BPartner_ID@ @IsMandatory@");
		}
		//	
		AtomicReference<MBPartner> customer = new AtomicReference<MBPartner>();
		Trx.run(transactionName -> {
			//	Create it
			MBPartner businessPartner = MBPartner.get(Env.getCtx(), RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getUuid(), transactionName));
			businessPartner.set_TrxName(transactionName);
			//	Set Value
			Optional.ofNullable(request.getValue()).ifPresent(value -> businessPartner.setValue(value));			
			//	Tax Id
			Optional.ofNullable(request.getTaxId()).ifPresent(value -> businessPartner.setTaxID(value));
			//	Duns
			Optional.ofNullable(request.getDuns()).ifPresent(value -> businessPartner.setDUNS(value));
			//	Naics
			Optional.ofNullable(request.getNaics()).ifPresent(value -> businessPartner.setNAICS(value));
			//	Name
			Optional.ofNullable(request.getName()).ifPresent(value -> businessPartner.setName(value));
			//	Last name
			Optional.ofNullable(request.getLastName()).ifPresent(value -> businessPartner.setName2(value));
			//	Description
			Optional.ofNullable(request.getDescription()).ifPresent(value -> businessPartner.setDescription(value));
			//	Save it
			businessPartner.saveEx(transactionName);
			//	Location
			request.getAddressesList().forEach(address -> {
				int countryId = 0;
				if(!Util.isEmpty(address.getCountryUuid())) {
					countryId = RecordUtil.getIdFromUuid(I_C_Country.Table_Name, address.getCountryUuid(), transactionName);
				}
				//	
				int regionId = 0;
				if(!Util.isEmpty(address.getRegionUuid())) {
					regionId = RecordUtil.getIdFromUuid(I_C_Region.Table_Name, address.getRegionUuid(), transactionName);
				}
				String cityName = null;
				int cityId = 0;
				//	City Name
				if(!Util.isEmpty(address.getCityName())) {
					cityName = address.getCityName();
				}
				//	City Reference
				if(!Util.isEmpty(address.getCityUuid())) {
					cityId = RecordUtil.getIdFromUuid(I_C_City.Table_Name, address.getRegionUuid(), transactionName);
				}
				//	Validate it
				if(countryId > 0
						|| regionId > 0
						|| cityId > 0
						|| !Util.isEmpty(cityName)) {
					//	Find it
					Optional<MBPartnerLocation> maybeCustomerLocation = Arrays.asList(businessPartner.getLocations(true)).stream().filter(customerLocation -> ValueUtil.validateNull(customerLocation.getUUID()).equals(ValueUtil.validateNull(address.getUuid()))).findFirst();
					if(maybeCustomerLocation.isPresent()) {
						MBPartnerLocation businessPartnerLocation = maybeCustomerLocation.get();
						MLocation location = businessPartnerLocation.getLocation(true);
						location.set_TrxName(transactionName);
						if(countryId > 0) {
							location.setC_Country_ID(countryId);
						}
						if(regionId > 0) {
							location.setC_Region_ID(regionId);
						}
						if(cityId > 0) {
							location.setC_City_ID(cityId);
						}
						Optional.ofNullable(cityName).ifPresent(city -> location.setCity(city));
						if(countryId > 0) {
							location.setC_Country_ID(countryId);
						}
						//	Address
						Optional.ofNullable(address.getAddress1()).ifPresent(addressValue -> location.setAddress1(addressValue));
						Optional.ofNullable(address.getAddress2()).ifPresent(addressValue -> location.setAddress2(addressValue));
						Optional.ofNullable(address.getAddress3()).ifPresent(addressValue -> location.setAddress3(addressValue));
						Optional.ofNullable(address.getAddress4()).ifPresent(addressValue -> location.setAddress4(addressValue));
						Optional.ofNullable(address.getPostalCode()).ifPresent(postalCode -> location.setPostal(postalCode));
						//	Save
						location.saveEx();
						//	Update business partner location
						businessPartnerLocation.setIsBillTo(address.getIsDefaultBilling());
						businessPartnerLocation.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_IsDefaultBilling, address.getIsDefaultBilling());
						businessPartnerLocation.setIsShipTo(address.getIsDefaultShipping());
						businessPartnerLocation.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_IsDefaultShipping, address.getIsDefaultShipping());
						Optional.ofNullable(address.getContactName()).ifPresent(contactName -> businessPartnerLocation.set_ValueOfColumn("ContactName", contactName));
						Optional.ofNullable(address.getContactName()).ifPresent(contact -> businessPartnerLocation.setContactPerson(contact));
						Optional.ofNullable(address.getFirstName()).ifPresent(firstName -> businessPartnerLocation.setName(firstName));
						Optional.ofNullable(address.getLastName()).ifPresent(lastName -> businessPartnerLocation.set_ValueOfColumn("Name2", lastName));
						Optional.ofNullable(address.getEmail()).ifPresent(email -> businessPartnerLocation.setEMail(email));
						Optional.ofNullable(address.getPhone()).ifPresent(phome -> businessPartnerLocation.setPhone(phome));
						Optional.ofNullable(address.getDescription()).ifPresent(description -> businessPartnerLocation.set_ValueOfColumn("Description", description));
						businessPartnerLocation.saveEx(transactionName);
						//	Contact
						AtomicReference<MUser> contactReference = new AtomicReference<MUser>(getOfBusinessPartnerLocation(businessPartnerLocation, transactionName));
						if(contactReference.get() == null
								|| contactReference.get().getAD_User_ID() <= 0) {
							contactReference.set(new MUser(businessPartner));
						}
						if(!Util.isEmpty(address.getContactName()) || !Util.isEmpty(address.getEmail()) || !Util.isEmpty(address.getPhone())) {
							MUser contact = contactReference.get();
							Optional.ofNullable(address.getEmail()).ifPresent(email -> contact.setEMail(email));
							Optional.ofNullable(address.getPhone()).ifPresent(phome -> contact.setPhone(phome));
							Optional.ofNullable(address.getDescription()).ifPresent(description -> contact.setDescription(description));
							String contactName = address.getContactName();
							if(Util.isEmpty(contactName)) {
								contactName = address.getEmail();
							}
							if(Util.isEmpty(contactName)) {
								contactName = address.getPhone();
							}
							contact.setName(contactName);
							//	Save
							contact.setC_BPartner_Location_ID(businessPartnerLocation.getC_BPartner_Location_ID());
							contact.saveEx(transactionName);
				 		}
					}
					customer.set(businessPartner);
				}
			});
		});
		//	Default return
		return convertCustomer(customer.get());
	}
	
	/**
	 * 
	 * @param businessPartnerLocation
	 * @param transactionName
	 * @return
	 * @return MUser
	 */
	private MUser getOfBusinessPartnerLocation(MBPartnerLocation businessPartnerLocation, String transactionName) {
		return new Query(businessPartnerLocation.getCtx(), MUser.Table_Name, "C_BPartner_Location_ID = ?", transactionName)
				.setParameters(businessPartnerLocation.getC_BPartner_Location_ID())
				.first();
	}
	
	/**
	 * Convert customer
	 * @param businessPartner
	 * @return
	 */
	private Customer.Builder convertCustomer(MBPartner businessPartner) {
		if(businessPartner == null) {
			return Customer.newBuilder();
		}
		Customer.Builder customer = Customer.newBuilder()
				.setUuid(ValueUtil.validateNull(businessPartner.getUUID()))
				.setId(businessPartner.getC_BPartner_ID())
				.setValue(ValueUtil.validateNull(businessPartner.getValue()))
				.setTaxId(ValueUtil.validateNull(businessPartner.getTaxID()))
				.setDuns(ValueUtil.validateNull(businessPartner.getDUNS()))
				.setNaics(ValueUtil.validateNull(businessPartner.getNAICS()))
				.setName(ValueUtil.validateNull(businessPartner.getName()))
				.setLastName(ValueUtil.validateNull(businessPartner.getName2()))
				.setDescription(ValueUtil.validateNull(businessPartner.getDescription()));
		//	Add Address
		Arrays.asList(businessPartner.getLocations(true)).stream().filter(customerLocation -> customerLocation.isActive()).forEach(address -> customer.addAddresses(convertCustomerAddress(address)));
		return customer;
	}
	
	
	/**
	 * Convert Address
	 * @param businessPartnerLocation
	 * @return
	 * @return Address.Builder
	 */
	private Address.Builder convertCustomerAddress(MBPartnerLocation businessPartnerLocation) {
		if(businessPartnerLocation == null) {
			return Address.newBuilder();
		}
		MLocation location = businessPartnerLocation.getLocation(true);
		Address.Builder builder =  Address.newBuilder()
				.setUuid(ValueUtil.validateNull(businessPartnerLocation.getUUID()))
				.setId(businessPartnerLocation.getC_BPartner_Location_ID())
				.setPostalCode(ValueUtil.validateNull(location.getPostal()))
				.setAddress1(ValueUtil.validateNull(location.getAddress1()))
				.setAddress2(ValueUtil.validateNull(location.getAddress2()))
				.setAddress3(ValueUtil.validateNull(location.getAddress3()))
				.setAddress4(ValueUtil.validateNull(location.getAddress4()))
				.setPostalCode(ValueUtil.validateNull(location.getPostal()))
				.setDescription(ValueUtil.validateNull(businessPartnerLocation.get_ValueAsString("Description")))
				.setFirstName(ValueUtil.validateNull(businessPartnerLocation.getName()))
				.setLastName(ValueUtil.validateNull(businessPartnerLocation.get_ValueAsString("Name2")))
				.setContactName(ValueUtil.validateNull(businessPartnerLocation.get_ValueAsString("ContactName")))
				.setEmail(ValueUtil.validateNull(businessPartnerLocation.getEMail()))
				.setPhone(ValueUtil.validateNull(businessPartnerLocation.getPhone()))
				.setCountryCode(ValueUtil.validateNull(MCountry.get(Env.getCtx(), location.getC_Country_ID()).getCountryCode()))
				.setIsDefaultShipping(businessPartnerLocation.get_ValueAsBoolean(VueStoreFrontUtil.COLUMNNAME_IsDefaultShipping))
				.setIsDefaultShipping(businessPartnerLocation.get_ValueAsBoolean(VueStoreFrontUtil.COLUMNNAME_IsDefaultBilling));
		//	Get user from location
		MUser user = new Query(Env.getCtx(), I_AD_User.Table_Name, I_AD_User.COLUMNNAME_C_BPartner_Location_ID + " = ?", businessPartnerLocation.get_TrxName())
				.setParameters(businessPartnerLocation.getC_BPartner_Location_ID())
				.setOnlyActiveRecords(true)
				.first();
		String phone = null;
		if(user != null
				&& user.getAD_User_ID() > 0
				&& Util.isEmpty(user.getPhone())) {
			phone = user.getPhone();
		}
		//	
		builder.setPhone(ValueUtil.validateNull(Optional.ofNullable(businessPartnerLocation.getPhone()).orElse(Optional.ofNullable(phone).orElse(""))));
		//	City
		if(location.getC_City_ID() > 0) {
			MCity city = MCity.get(Env.getCtx(), location.getC_City_ID());
			builder.setCity(City.newBuilder()
					.setId(city.getC_City_ID())
					.setName(ValueUtil.validateNull(city.getName())));
		} else {
			builder.setCity(City.newBuilder()
					.setName(ValueUtil.validateNull(location.getCity())));
		}
		//	Region
		if(location.getC_Region_ID() > 0) {
			MRegion region = MRegion.get(Env.getCtx(), location.getC_Region_ID());
			builder.setRegion(Region.newBuilder()
					.setId(region.getC_Region_ID())
					.setName(ValueUtil.validateNull(region.getName())));
		} else {
			builder.setCity(City.newBuilder()
					.setName(ValueUtil.validateNull(location.getCity())));
		}
		//	
		return builder;
	}
	
	
	/**
	 * List Warehouses from POS UUID
	 * @param request
	 * @return
	 */
	private ListAvailableWarehousesResponse.Builder listWarehouses(ListAvailableWarehousesRequest request) {
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		ListAvailableWarehousesResponse.Builder builder = ListAvailableWarehousesResponse.newBuilder();
		final String TABLE_NAME = "C_POSWarehouseAllocation";
		if(MTable.getTable_ID(TABLE_NAME) <= 0) {
			return builder;
		}
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Aisle Seller
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		//	Get Product list
		Query query = new Query(Env.getCtx(), TABLE_NAME, "C_POS_ID = ?", null)
				.setParameters(posId)
				.setClient_ID()
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.list()
		.forEach(availableWarehouse -> {
			MWarehouse warehouse = MWarehouse.get(Env.getCtx(), availableWarehouse.get_ValueAsInt("M_Warehouse_ID"));
			builder.addWarehouses(AvailableWarehouse.newBuilder()
					.setId(warehouse.getM_Warehouse_ID())
					.setUuid(ValueUtil.validateNull(warehouse.getUUID()))
					.setKey(ValueUtil.validateNull(warehouse.getValue()))
					.setName(ValueUtil.validateNull(warehouse.getName()))
					.setIsPosRequiredPin(availableWarehouse.get_ValueAsBoolean(I_C_POS.COLUMNNAME_IsPOSRequiredPIN)));
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * List Price List from POS UUID
	 * @param request
	 * @return
	 */
	private ListAvailablePriceListResponse.Builder listPriceList(ListAvailablePriceListRequest request) {
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		ListAvailablePriceListResponse.Builder builder = ListAvailablePriceListResponse.newBuilder();
		final String TABLE_NAME = "C_POSPriceListAllocation";
		if(MTable.getTable_ID(TABLE_NAME) <= 0) {
			return builder;
		}
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Dynamic where clause
		//	Aisle Seller
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		//	Get Product list
		Query query = new Query(Env.getCtx(), TABLE_NAME, "C_POS_ID = ?", null)
				.setParameters(posId)
				.setClient_ID()
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.list()
		.forEach(availablePriceList -> {
			MPriceList priceList = MPriceList.get(Env.getCtx(), availablePriceList.get_ValueAsInt("M_PriceList_ID"), null);
			builder.addPriceList(AvailablePriceList.newBuilder()
					.setId(priceList.getM_PriceList_ID())
					.setUuid(ValueUtil.validateNull(priceList.getUUID()))
					.setKey(ValueUtil.validateNull(priceList.getName()))
					.setName(ValueUtil.validateNull(priceList.getName()))
					.setIsPosRequiredPin(availablePriceList.get_ValueAsBoolean(I_C_POS.COLUMNNAME_IsPOSRequiredPIN)));
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * List tender Types from POS UUID
	 * @param request
	 * @return
	 */
	private ListAvailablePaymentMethodsResponse.Builder listPaymentMethods(ListAvailablePaymentMethodsRequest request) {
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		ListAvailablePaymentMethodsResponse.Builder builder = ListAvailablePaymentMethodsResponse.newBuilder();
		final String TABLE_NAME = "C_POSPaymentTypeAllocation";
		if(MTable.getTable_ID(TABLE_NAME) <= 0) {
			return builder;
		}
		final String PAYMENT_METHOD_TABLE_NAME = "C_PaymentMethod";
		MTable paymentTypeTable = MTable.get(Env.getCtx(), PAYMENT_METHOD_TABLE_NAME);
		if(paymentTypeTable == null
				|| paymentTypeTable.getAD_Table_ID() <= 0) {
			return builder;
		}
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Dynamic where clause
		//	Aisle Seller
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		//	Get Product list
		Query query = new Query(Env.getCtx(), TABLE_NAME, "C_POS_ID = ?", null)
				.setParameters(posId)
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.list()
		.forEach(availablePaymentMethod -> {
			PO paymentMethod = paymentTypeTable.getPO(availablePaymentMethod.get_ValueAsInt("C_PaymentMethod_ID"), null);
			AvailablePaymentMethod.Builder tenderTypeValue = AvailablePaymentMethod.newBuilder();
			tenderTypeValue.setId(paymentMethod.get_ID())
					.setUuid(ValueUtil.validateNull(paymentMethod.get_UUID()))
					.setKey(ValueUtil.validateNull(paymentMethod.get_ValueAsString("Value")))
					.setName(ValueUtil.validateNull(paymentMethod.get_ValueAsString(I_AD_Ref_List.COLUMNNAME_Name)))
					.setTenderType(ValueUtil.validateNull(paymentMethod.get_ValueAsString(I_C_Payment.COLUMNNAME_TenderType)))
					.setIsPosRequiredPin(availablePaymentMethod.get_ValueAsBoolean(I_C_POS.COLUMNNAME_IsPOSRequiredPIN))
					.setIsAllowedToRefund(availablePaymentMethod.get_ValueAsBoolean("IsAllowedToRefund"))
					.setIsAllowedToRefundOpen(availablePaymentMethod.get_ValueAsBoolean("IsAllowedToRefundOpen"))
					.setMaximumRefundAllowed(ValueUtil.getDecimalFromBigDecimal((BigDecimal) availablePaymentMethod.get_Value("MaximumRefundAllowed")))
					.setMaximumDailyRefundAllowed(ValueUtil.getDecimalFromBigDecimal((BigDecimal) availablePaymentMethod.get_Value("MaximumDailyRefundAllowed")));
					if(availablePaymentMethod.get_ValueAsInt("RefundReferenceCurrency_ID") > 0) {
						tenderTypeValue.setRefundReferenceCurrency(ConvertUtil.convertCurrency(MCurrency.get(Env.getCtx(), availablePaymentMethod.get_ValueAsInt("RefundReferenceCurrency_ID"))));
					}
					if(availablePaymentMethod.get_ValueAsInt("ReferenceCurrency_ID") > 0) {
						tenderTypeValue.setReferenceCurrency(ConvertUtil.convertCurrency(MCurrency.get(Env.getCtx(), availablePaymentMethod.get_ValueAsInt("ReferenceCurrency_ID"))));
					}
			builder.addPaymentMethods(tenderTypeValue);
			//	
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * List Document Types from POS UUID
	 * @param request
	 * @return
	 */
	private ListAvailableDocumentTypesResponse.Builder listDocumentTypes(ListAvailableDocumentTypesRequest request) {
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		ListAvailableDocumentTypesResponse.Builder builder = ListAvailableDocumentTypesResponse.newBuilder();
		final String TABLE_NAME = "C_POSDocumentTypeAllocation";
		if(MTable.getTable_ID(TABLE_NAME) <= 0) {
			return builder;
		}
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Dynamic where clause
		//	Aisle Seller
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		//	Get Product list
		Query query = new Query(Env.getCtx(), TABLE_NAME, "C_POS_ID = ?", null)
				.setParameters(posId)
				.setClient_ID()
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.list()
		.forEach(availableDocumentType -> {
			MDocType documentType = MDocType.get(Env.getCtx(), availableDocumentType.get_ValueAsInt("C_DocType_ID"));
			builder.addDocumentTypes(AvailableDocumentType.newBuilder()
					.setId(documentType.getC_DocType_ID())
					.setUuid(ValueUtil.validateNull(documentType.getUUID()))
					.setKey(ValueUtil.validateNull(documentType.getName()))
					.setName(ValueUtil.validateNull(documentType.getPrintName()))
					.setIsPosRequiredPin(availableDocumentType.get_ValueAsBoolean(I_C_POS.COLUMNNAME_IsPOSRequiredPIN)));
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * List Currencies from POS UUID
	 * @param request
	 * @return
	 */
	private ListAvailableCurrenciesResponse.Builder listCurrencies(ListAvailableCurrenciesRequest request) {
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		ListAvailableCurrenciesResponse.Builder builder = ListAvailableCurrenciesResponse.newBuilder();
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Dynamic where clause
		String whereClause = "EXISTS(SELECT 1 FROM C_Conversion_Rate cr "
				+ "WHERE (cr.C_Currency_ID = C_Currency.C_Currency_ID  OR cr.C_Currency_ID_To = C_Currency.C_Currency_ID) "
				+ "AND cr.C_ConversionType_ID = ? AND cr.ValidFrom >= ? AND cr.ValidTo <= ?)";
		//	Aisle Seller
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		MPOS pos = MPOS.get(Env.getCtx(), posId);
		Timestamp now = TimeUtil.getDay(System.currentTimeMillis());
		//	Get Product list
		Query query = new Query(Env.getCtx(), I_C_Currency.Table_Name, whereClause.toString(), null)
				.setParameters(pos.get_ValueAsInt(I_C_ConversionType.COLUMNNAME_C_ConversionType_ID), now, now)
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.<MCurrency>list()
		.forEach(currency -> {
			builder.addCurrencies(ConvertUtil.convertCurrency(currency));
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * Process Order from Point of Sales
	 * @param request
	 * @return
	 */
	private MOrder processOrder(ProcessOrderRequest request) {
		AtomicReference<MOrder> orderReference = new AtomicReference<MOrder>();
		if(!Util.isEmpty(request.getOrderUuid())) {
			Trx.run(transactionName -> {
				MOrder salesOrder = getOrder(request.getOrderUuid(), transactionName);
				if(salesOrder == null) {
					throw new AdempiereException("@C_Order_ID@ @NotFound@");
				}
				if(!DocumentUtil.isDrafted(salesOrder)) {
					throw new AdempiereException("@C_Order_ID@ @IsCompleted@");
				}
				int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), transactionName);
				if(posId <= 0) {
					throw new AdempiereException("@C_POS_ID@ @NotFound@");
				}
				MPOS pos = MPOS.get(Env.getCtx(), posId);
				// In case the Order is Invalid, set to In Progress; otherwise it will not be completed
				if (salesOrder.getDocStatus().equalsIgnoreCase(MOrder.STATUS_Invalid))  {
					salesOrder.setDocStatus(MOrder.STATUS_InProgress);
				}
				//	Set default values
				salesOrder.setDocAction(DocAction.ACTION_Complete);
				salesOrder.setC_POS_ID(posId);
				setCurrentDate(salesOrder);
				salesOrder.saveEx();
				//	Update Process if exists
				if (!salesOrder.processIt(MOrder.DOCACTION_Complete)) {
					log.warning("@ProcessFailed@ :" + salesOrder.getProcessMsg());
					throw new AdempiereException("@ProcessFailed@ :" + salesOrder.getProcessMsg());
				}
				salesOrder.saveEx();
				//	Create or process payments
				if(request.getCreatePayments()) {
					if(request.getPaymentsList().size() == 0) {
						throw new AdempiereException("@C_Payment_ID@ @NotFound@");
					}
					//	Create
					request.getPaymentsList().forEach(paymentRequest -> createPayment(salesOrder, paymentRequest, pos, transactionName));
				}
				processPayments(salesOrder, pos, request.getIsOpenRefund(), transactionName);
				//	Create
				orderReference.set(salesOrder);
			});
			//	Allocate All payments
			
		}
		//	Return order
		return orderReference.get();
	}
	
	/**
	 * Process payment of Order
	 * @param salesOrder
	 * @param pos
	 * @param isOpenRefund
	 * @param transactionName
	 * @return void
	 */
	private void processPayments(MOrder salesOrder, MPOS pos, boolean isOpenRefund, String transactionName) {
		//	Get invoice if exists
		int invoiceId = salesOrder.getC_Invoice_ID();
		AtomicReference<BigDecimal> openAmount = new AtomicReference<BigDecimal>(salesOrder.getGrandTotal());
		List<Integer> paymentsIds = new ArrayList<Integer>();
		//	Complete Payments
		MPayment.getOfOrder(salesOrder).stream().sorted(Comparator.comparing(MPayment::getCreated)).forEach(payment -> {
			payment.setIsPrepayment(true);
			BigDecimal convertedAmount = getConvetedAmount(salesOrder, payment, payment.getPayAmt());
			//	Get current open amount
			if(payment.isReceipt()) {
				openAmount.updateAndGet(amount -> amount.subtract(convertedAmount));
			} else {
				openAmount.set(convertedAmount.negate());
			}
			payment.setOverUnderAmt(getConvetedRemainingAmountToPaymentCurrency(openAmount.get(), salesOrder, payment));
			payment.setDocAction(MPayment.DOCACTION_Complete);
			setCurrentDate(payment);
			payment.saveEx(transactionName);
			if (!payment.processIt(MPayment.DOCACTION_Complete)) {
				log.warning("@ProcessFailed@ :" + payment.getProcessMsg());
				throw new AdempiereException("@ProcessFailed@ :" + payment.getProcessMsg());
			}
			payment.saveEx(transactionName);
			paymentsIds.add(payment.getC_Payment_ID());
			salesOrder.saveEx(transactionName);
			MBankStatement.addPayment(payment);
		});
		//	Allocate all payments
		if(paymentsIds.size() > 0) {
			String description = Msg.parseTranslation(Env.getCtx(), "@C_POS_ID@: " + pos.getName() + " - " + salesOrder.getDocumentNo());
			//	
			MAllocationHdr paymentAllocation = new MAllocationHdr (Env.getCtx(), true, getDate(), salesOrder.getC_Currency_ID(), description, transactionName);
			paymentAllocation.setAD_Org_ID(salesOrder.getAD_Org_ID());
			//	Set Description
			paymentAllocation.saveEx();
			//	Add lines
			paymentsIds.stream().map(paymentId -> new MPayment(Env.getCtx(), paymentId, transactionName)).forEach(payment -> {
				BigDecimal multiplier = Env.ONE;
				if(!payment.isReceipt()) {
					multiplier = Env.ONE.negate();
				}
				BigDecimal paymentAmount = getConvetedAmount(salesOrder, payment, payment.getPayAmt());
				BigDecimal discountAmount = getConvetedAmount(salesOrder, payment, payment.getDiscountAmt());
				BigDecimal overUnderAmount = getConvetedAmount(salesOrder, payment, payment.getOverUnderAmt());
				BigDecimal writeOffAmount = getConvetedAmount(salesOrder, payment, payment.getWriteOffAmt());
				if (overUnderAmount.signum() < 0 && paymentAmount.signum() > 0) {
					paymentAmount = paymentAmount.add(overUnderAmount);
				}
				MAllocationLine paymentAllocationLine = new MAllocationLine (paymentAllocation, paymentAmount.multiply(multiplier), discountAmount.multiply(multiplier), writeOffAmount.multiply(multiplier), overUnderAmount.multiply(multiplier));
				paymentAllocationLine.setDocInfo(salesOrder.getC_BPartner_ID(), salesOrder.getC_Order_ID(), invoiceId);
				paymentAllocationLine.setPaymentInfo(payment.getC_Payment_ID(), 0);
				paymentAllocationLine.saveEx();
			});
			//	Add write off
			if(!isOpenRefund) {
				if(openAmount.get().compareTo(Env.ZERO) != 0) {
					MAllocationLine paymentAllocationLine = new MAllocationLine (paymentAllocation, Env.ZERO, Env.ZERO, openAmount.get(), Env.ZERO);
					paymentAllocationLine.setDocInfo(salesOrder.getC_BPartner_ID(), salesOrder.getC_Order_ID(), invoiceId);
					paymentAllocationLine.saveEx();
				}
			}
			//	Complete
			if (!paymentAllocation.processIt(MAllocationHdr.DOCACTION_Complete)) {
				log.warning("@ProcessFailed@ :" + paymentAllocation.getProcessMsg());
				throw new AdempiereException("@ProcessFailed@ :" + paymentAllocation.getProcessMsg());
			}
			paymentAllocation.saveEx();
			//	Test allocation
			paymentsIds.stream().map(paymentId -> new MPayment(Env.getCtx(), paymentId, transactionName)).forEach(payment -> {
				payment.setIsAllocated(true);
				payment.saveEx();
			});
		}
	}
	
	/**
	 * Get Converted Amount based on Order currency
	 * @param order
	 * @param payment
	 * @return
	 * @return BigDecimal
	 */
	private BigDecimal getConvetedAmount(MOrder order, MPayment payment, BigDecimal amount) {
		if(payment.getC_Currency_ID() == order.getC_Currency_ID()
				|| amount == null
				|| amount.compareTo(Env.ZERO) == 0) {
			return amount;
		}
		BigDecimal convertedAmount = MConversionRate.convert(payment.getCtx(), amount, payment.getC_Currency_ID(), order.getC_Currency_ID(), payment.getDateAcct(), payment.getC_ConversionType_ID(), payment.getAD_Client_ID(), payment.getAD_Org_ID());
		if(convertedAmount == null
				|| convertedAmount.compareTo(Env.ZERO) == 0) {
			throw new AdempiereException(MConversionRate.getErrorMessage(payment.getCtx(), "ErrorConvertingDocumentCurrencyToBaseCurrency", payment.getC_Currency_ID(), order.getC_Currency_ID(), payment.getC_ConversionType_ID(), payment.getDateAcct(), payment.get_TrxName()));
		}
		//	
		return convertedAmount;
	}
	
	/**
	 * Get Converted Amount based on Payment currency
	 * @param order
	 * @param payment
	 * @return
	 * @return BigDecimal
	 */
	private BigDecimal getConvetedRemainingAmountToPaymentCurrency(BigDecimal remainingAmount, MOrder order, MPayment payment) {
		if(payment.getC_Currency_ID() == order.getC_Currency_ID()
				|| remainingAmount == null
				|| remainingAmount.compareTo(Env.ZERO) == 0) {
			return remainingAmount;
		}
		BigDecimal convertedAmount = MConversionRate.convert(payment.getCtx(), remainingAmount, order.getC_Currency_ID(), payment.getC_Currency_ID(), payment.getDateAcct(), order.getC_ConversionType_ID(), order.getAD_Client_ID(), order.getAD_Org_ID());
		if(convertedAmount == null) {
			convertedAmount = Env.ZERO;
		}
		//	
		return convertedAmount;
	}
	
//	/**
//	 * Processes different kinds of payment types
//	 * For Cash: if there is a return amount, modify the payment amount accordingly.
//	 * If there are no payment methods, nothing happens
//	 * @param trxName
//	 * @param openAmt
//	 */
//	public void processTenderTypes(String trxName, BigDecimal openAmt) {
//		cleanErrorMsg();
//		this.trxName = trxName;
//		//
//		AtomicReference<BigDecimal> cashPayment = new AtomicReference<BigDecimal>(Env.ZERO);
//		AtomicReference<BigDecimal> otherPayment = new AtomicReference<BigDecimal>(Env.ZERO);
//		//	Get payments without cash
//		collectDetails
//			.stream()
//			.filter(collect -> !collect.getTenderType().equals(X_C_Payment.TENDERTYPE_Cash) 
//					&& !collect.getTenderType().equals(X_C_Payment.TENDERTYPE_Account))
//			.forEach(collectDetail -> otherPayment.updateAndGet(amount -> amount = amount.add(collectDetail.getConvertedPayAmt())));
//		//	Get cash
//		collectDetails
//		.stream()
//		.filter(collect -> collect.getTenderType().equals(X_C_Payment.TENDERTYPE_Cash) 
//				|| collect.getTenderType().equals(X_C_Payment.TENDERTYPE_Account))
//			.forEach(collectDetail -> cashPayment.updateAndGet(amount -> amount = amount.add(collectDetail.getConvertedPayAmt())));
//		//	
//		//	Save Cash Payment
//		//	Validate if payment consists credit card or cash -> payment amount must be exact
//		BigDecimal amountRefunded = openAmt.subtract(otherPayment.get().add(cashPayment.get()));
//		amountConverted = order.getGrandTotal();
//		if(amountRefunded.signum() == -1
//				&& cashPayment.get().doubleValue() > 0) {
//			if(amountRefunded.abs().doubleValue() > cashPayment.get().doubleValue()) {
//				addErrorMsg("@POS.validatePayment.PaymentBustBeExact@");
//			}
//		}
//		collectDetails
//		.forEach(collectDetail -> {
//			boolean result;
//			if(collectDetail.getTenderType().equals(X_C_Payment.TENDERTYPE_Cash)
//					|| collectDetail.getTenderType().equals(X_C_Payment.TENDERTYPE_Account)) {	//	For Cash
//				BigDecimal payAmt = collectDetail.getConvertedPayAmt();
//				BigDecimal amountRefundedConverted = amountRefunded.multiply(collectDetail.getConversionRateFromCurrency(order.getC_Currency_ID(), order.getAD_Org_ID())).negate();
//				
//				amountConverted = amountConverted.subtract(payAmt);
//				if(amountConverted.signum() == -1)
//					payAmt = payAmt.add(amountConverted);
//				
//				if(!isCompleteCollect())
//					payAmt = payAmt.multiply(collectDetail.getConversionRateFromCurrency(order.getC_Currency_ID(), order.getAD_Org_ID()));
//				else
//					payAmt = collectDetail.getPayAmt();
//				
//				result = payCash(payAmt, collectDetail.getCurrencyId(), Env.ZERO, amountRefundedConverted, collectDetail.getReferenceNo());
//			} else if(collectDetail.getTenderType().equals(X_C_Payment.TENDERTYPE_DirectDebit)) {	//	For Direct Debit
//				result = payDirectDebit(collectDetail.getPayAmt(), collectDetail.getCurrencyId(), collectDetail.getRoutingNo(),
//						collectDetail.getA_Country(), collectDetail.getCreditCardVV(), collectDetail.getC_Bank_ID(), collectDetail.getReferenceNo());
//				if (!result) {					
//					addErrorMsg("@POS.ErrorPaymentDirectDebit@");
//					return;
//				}
//			} else if(collectDetail.getTenderType().equals(X_C_Payment.TENDERTYPE_Check)) {	//	For Check
//				result = payCheck(collectDetail.getPayAmt(), collectDetail.getCurrencyId(), null, collectDetail.getRoutingNo(), collectDetail.getReferenceNo(), collectDetail.getDateTrx());
//				if (!result) {					
//					addErrorMsg("@POS.ErrorPaymentCheck@");
//					return;
//				}
//			} else if(collectDetail.getTenderType().equals(X_C_Payment.TENDERTYPE_CreditCard)) {	//	For Credit
//				//	Valid Expedition
//				String mmyy = collectDetail.getCreditCardExpMM() + collectDetail.getCreditCardExpYY();
//				//	Valid Month and Year
//				int month = MPaymentValidate.getCreditCardExpMM(mmyy);
//				int year = MPaymentValidate.getCreditCardExpYY(mmyy);
//				//	Pay from Credit Card
//				result = payCreditCard(collectDetail.getPayAmt(), collectDetail.getCurrencyId(), collectDetail.getA_Name(),
//						month, year, collectDetail.getCreditCardNumber(), collectDetail.getCreditCardVV(), collectDetail.getCreditCardType(), collectDetail.getReferenceNo());
//				if (!result) {					
//					addErrorMsg("@POS.ErrorPaymentCreditCard@");
//					return;
//				}
//			} else if(collectDetail.getTenderType().equals(X_C_Payment.TENDERTYPE_CreditMemo)) {
//				if(isAllowsPartialPayment()) {
//					addErrorMsg("@POS.PrePayment.NoCreditMemoAllowed@");
//					return;
//				}
//				result = payCreditMemo(collectDetail.getM_InvCreditMemo(), collectDetail.getPayAmt());
//				if (!result) {					
//					addErrorMsg("@POS.ErrorPaymentCreditMemo@");
//					return;
//				}
//			} else {
//				payment(collectDetail.getPayAmt(), collectDetail.getCurrencyId(), collectDetail.getTenderType(), collectDetail.getReferenceNo(), collectDetail.getC_Bank_ID());
//			}
//		});
//	}  // processPayment
	
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
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Dynamic where clause
		StringBuffer whereClause = new StringBuffer();
		List<Object> parameters = new ArrayList<Object>();
		//	Aisle Seller
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		boolean isWithAisleSeller = M_Element.get(Env.getCtx(), "IsAisleSeller") != null;
		if(isWithAisleSeller 
				&& request.getIsAisleSeller()) {
			whereClause.append("(C_Order.C_POS_ID = ? OR EXISTS(SELECT 1 FROM C_POS p WHERE p.C_POS_ID = C_Order.C_POS_ID AND p.IsAisleSeller = 'Y'))");
			parameters.add(posId);
		} else {
			whereClause.append("C_Order.C_POS_ID = ?");
			parameters.add(posId);
		}
		//	Document No
		if(!Util.isEmpty(request.getDocumentNo())) {
			whereClause.append(" AND UPPER(DocumentNo) LIKE '%' || UPPER(?) || '%'");
			parameters.add(request.getDocumentNo());
		}
		//	Business Partner
		if(!Util.isEmpty(request.getBusinessPartnerUuid())) {
			int businessPartnerId = RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getBusinessPartnerUuid(), null);
			whereClause.append(" AND C_BPartner_ID = ?");
			parameters.add(businessPartnerId);
		}
		//	Grand Total
		BigDecimal grandTotal = ValueUtil.getBigDecimalFromDecimal(request.getGrandTotal());
		if(grandTotal != null
				&& !grandTotal.equals(Env.ZERO)) {
			whereClause.append(" AND GrandTotal = ?");
			parameters.add(grandTotal);
		}
		//	Support Open Amount
		BigDecimal openAmount = ValueUtil.getBigDecimalFromDecimal(request.getOpenAmount());
		if(openAmount != null
				&& !openAmount.equals(Env.ZERO)) {
			whereClause.append(" (EXISTS(SELECT 1 FROM C_Invoice i WHERE i.C_Order_ID = C_Order.C_Order_ID GROUP BY i.C_Order_ID HAVING(SUM(invoiceopen(i.C_Invoice_ID, 0)) = ?))"
					+ " OR EXISTS(SELECT 1 FROM C_Payment p WHERE C_Order_ID = C_Order.C_Order_ID GROUP BY p.C_Order_ID HAVING(SUM(p.PayAmt) = ?)"
					+ ")");
			parameters.add(openAmount);
			parameters.add(openAmount);
		}
		whereClause.append(" AND Processed = ?");
		parameters.add(request.getIsProcessed()? "Y": "N");
		//	Is Invoiced
		if(request.getIsInvoiced()
				|| request.getIsPaid()) {
			whereClause.append(" AND EXISTS(SELECT 1 FROM C_Invoice i WHERE i.C_Order_ID = C_Order.C_Order_ID AND i.DocStatus IN('CO', 'CL') AND i.IsPaid = ?)");
			parameters.add(request.getIsPaid()? "Y": "N");
		}
		//	Date Order From
		if(!Util.isEmpty(request.getDateOrderedFrom())) {
			whereClause.append(" AND DateOrdered >= ?");
			parameters.add(TimeUtil.getDay(ValueUtil.convertStringToDate(request.getDateOrderedFrom())));
		}
		//	Date Order To
		if(!Util.isEmpty(request.getDateOrderedTo())) {
			whereClause.append(" AND DateOrdered <= ?");
			parameters.add(TimeUtil.getDay(ValueUtil.convertStringToDate(request.getDateOrderedTo())));
		}
		//	Sales Representative
		if(!Util.isEmpty(request.getSalesRepresentativeUuid())) {
			int salesRepresentativeId = RecordUtil.getIdFromUuid(I_AD_User.Table_Name, request.getSalesRepresentativeUuid(), null);
			whereClause.append(" AND SalesRep_ID = ?");
			parameters.add(salesRepresentativeId);
		}
		//	Get Product list
		Query query = new Query(Env.getCtx(), I_C_Order.Table_Name, whereClause.toString(), null)
				.setParameters(parameters)
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.setOrderBy(I_C_Order.COLUMNNAME_DateOrdered);
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
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * List Payments from POS UUID
	 * @param request
	 * @return
	 */
	private ListPaymentsResponse.Builder listPayments(ListPaymentsRequest request) {
		if(Util.isEmpty(request.getPosUuid())
				&& Util.isEmpty(request.getOrderUuid())) {
			throw new AdempiereException("@C_POS_ID@ / @C_Order_ID@ @NotFound@");
		}
		ListPaymentsResponse.Builder builder = ListPaymentsResponse.newBuilder();
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Dynamic where clause
		StringBuffer whereClause = new StringBuffer();
		//	Parameters
		List<Object> parameters = new ArrayList<Object>();
		//	Aisle Seller
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		int orderId = RecordUtil.getIdFromUuid(I_C_Order.Table_Name, request.getOrderUuid(), null);
		if(posId > 0) {
			whereClause.append("C_Payment.C_POS_ID = ?");
			parameters.add(posId);
		}
		//	For order
		if(orderId > 0) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
			whereClause.append("C_Payment.C_Order_ID = ?");
			parameters.add(orderId);
		}
		//	Get Product list
		Query query = new Query(Env.getCtx(), I_C_Payment.Table_Name, whereClause.toString(), null)
				.setParameters(parameters)
				.setClient_ID()
				.setOnlyActiveRecords(true);
		int count = query.count();
		query
		.setLimit(limit, offset)
		.<MPayment>list()
		.forEach(payment -> {
			builder.addPayments(convertPayment(payment));
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
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
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
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
		if(count > offset && count > limit) {
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
	private MOrder getOrder(String uuid, String transactionName) {
		return (MOrder) RecordUtil.getEntity(Env.getCtx(), I_C_Order.Table_Name, uuid, 0, transactionName);
	}
	
	/**
	 * Update Order from UUID
	 * @param request
	 * @return
	 */
	private MOrder updateOrder(UpdateOrderRequest request) {
		AtomicReference<MOrder> orderReference = new AtomicReference<MOrder>();
		if(!Util.isEmpty(request.getOrderUuid())) {
			Trx.run(transactionName -> {
				MOrder salesOrder = getOrder(request.getOrderUuid(), transactionName);
				if(salesOrder == null) {
					throw new AdempiereException("@C_Order_ID@ @NotFound@");
				}
				if(!DocumentUtil.isDrafted(salesOrder)) {
					throw new AdempiereException("@C_Order_ID@ @IsCompleted@");
				}
				//	Update Date Ordered
				Timestamp now = TimeUtil.getDay(System.currentTimeMillis());
				salesOrder.setDateOrdered(now);
				salesOrder.setDateAcct(now);
				salesOrder.setDatePromised(now);
				//	POS
				if(!Util.isEmpty(request.getPosUuid())) {
					int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), transactionName);
					if(posId > 0) {
						salesOrder.setC_POS_ID(posId);
					}
				}
				//	Document Type
				if(!Util.isEmpty(request.getDocumentTypeUuid())) {
					int documentTypeId = RecordUtil.getIdFromUuid(I_C_DocType.Table_Name, request.getDocumentTypeUuid(), transactionName);
					if(documentTypeId > 0) {
						salesOrder.setC_DocTypeTarget_ID(documentTypeId);
						salesOrder.setC_DocType_ID(documentTypeId);
						//	Set Sequenced No
						String value = DB.getDocumentNo(documentTypeId, transactionName, false, salesOrder);
						if (value != null) {
							salesOrder.setDocumentNo(value);
						}
					}
				}
				//	Business partner
				if(!Util.isEmpty(request.getCustomerUuid())) {
					int businessPartnerId = RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getCustomerUuid(), transactionName);
					if(businessPartnerId > 0
							&& salesOrder.getC_POS_ID() > 0) {
						configureBPartner(salesOrder, businessPartnerId, transactionName);
					}
				}
				//	Description
				if(!Util.isEmpty(request.getDescription())) {
					salesOrder.setDescription(request.getDescription());
				}
				//	Warehouse
				int warehouseId = 0;
				int priceListId = 0;
				if(!Util.isEmpty(request.getWarehouseUuid())) {
					warehouseId = RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid(), transactionName);
				}
				//	Price List
				if(!Util.isEmpty(request.getPriceListUuid())) {
					priceListId = RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, request.getPriceListUuid(), transactionName);
				}
				if(priceListId > 0) {
					salesOrder.setM_PriceList_ID(priceListId);
					salesOrder.saveEx(transactionName);
					configurePriceList(salesOrder);
				}
				if(warehouseId > 0) {
					salesOrder.setM_Warehouse_ID(warehouseId);
					salesOrder.saveEx(transactionName);
					configureWarehouse(salesOrder);
				}
				//	Save
				salesOrder.saveEx(transactionName);
				orderReference.set(salesOrder);
			});
		}
		//	Return order
		return orderReference.get();
	}
	
	/**
	 * 	Set BPartner, update price list and locations
	 *  Configuration of Business Partner has priority over POS configuration
	 *	@param p_C_BPartner_ID id
	 */
	
	/**
	 * set BPartner and save
	 */
	public void configureBPartner(MOrder order, int businessPartnerId, String transactionName) {
		//	Valid if has a Order
		if(DocumentUtil.isCompleted(order)
				|| DocumentUtil.isVoided(order))
			return;
		log.fine( "CPOS.setC_BPartner_ID=" + businessPartnerId);
		boolean isSamePOSPartner = false;
		MPOS pos = MPOS.get(Env.getCtx(), order.getC_POS_ID());
		//	Validate BPartner
		if (businessPartnerId == 0) {
			isSamePOSPartner = true;
			businessPartnerId = pos.getC_BPartnerCashTrx_ID();
		}
		//	Get BPartner
		MBPartner partner = MBPartner.get(Env.getCtx(), businessPartnerId);
		partner.set_TrxName(transactionName);
		if (partner == null || partner.get_ID() == 0) {
			throw new AdempiereException("POS.NoBPartnerForOrder");
		} else {
			log.info("CPOS.SetC_BPartner_ID -" + partner);
			order.setBPartner(partner);
			AtomicBoolean priceListIsChanged = new AtomicBoolean(false);
			if(partner.getM_PriceList_ID() > 0) {
				MPriceList businesPartnerPriceList = MPriceList.get(Env.getCtx(), partner.getM_PriceList_ID(), transactionName);
				MPriceList currentPriceList = MPriceList.get(Env.getCtx(), pos.getM_PriceList_ID(), transactionName);
				if(currentPriceList.getC_Currency_ID() != businesPartnerPriceList.getC_Currency_ID()) {
					order.setM_PriceList_ID(currentPriceList.getM_PriceList_ID());
				} else if(currentPriceList.getM_PriceList_ID() != partner.getM_PriceList_ID()) {
					priceListIsChanged.set(true);
				}
			}
			//	
			MBPartnerLocation [] partnerLocations = partner.getLocations(true);
			if(partnerLocations.length > 0) {
				for(MBPartnerLocation partnerLocation : partnerLocations) {
					if(partnerLocation.isBillTo())
						order.setBill_Location_ID(partnerLocation.getC_BPartner_Location_ID());
					if(partnerLocation.isShipTo())
						order.setShip_Location_ID(partnerLocation.getC_BPartner_Location_ID());
				}				
			}
			//	Validate Same BPartner
			if(isSamePOSPartner) {
				if(order.getPaymentRule() == null)
					order.setPaymentRule(MOrder.PAYMENTRULE_Cash);
			}
			//	Set Sales Representative
			if (order.getC_BPartner().getSalesRep_ID() != 0) {
				order.setSalesRep_ID(order.getC_BPartner().getSalesRep_ID());
			} else {
				order.setSalesRep_ID(Env.getAD_User_ID(Env.getCtx()));
			}
			//	Save Header
			order.saveEx();
			//	Load Price List Version
			Arrays.asList(order.getLines())
			.forEach(orderLine -> {
				orderLine.setC_BPartner_ID(partner.getC_BPartner_ID());
				orderLine.setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
				orderLine.setPrice();
				orderLine.setTax();
				orderLine.saveEx();
				if(Optional.ofNullable(orderLine.getPriceActual()).orElse(Env.ZERO).signum() == 0
						&& priceListIsChanged.get()) {
					orderLine.deleteEx(true);
				}
			});
		}
		//	Change for payments
		MPayment.getOfOrder(order).forEach(payment -> {
			if(DocumentUtil.isCompleted(payment)
					|| DocumentUtil.isVoided(payment)) {
				throw new AdempiereException("@C_Payment_ID@ @Processed@ " + payment.getDocumentNo());
			}
			//	Change Business Partner
			payment.setC_BPartner_ID(order.getC_BPartner_ID());
			payment.saveEx(transactionName);
		});
	}
	
	/**
	 * Configure Warehouse after change
	 * @param order
	 */
	private void configureWarehouse(MOrder order) {
		Arrays.asList(order.getLines())
		.forEach(orderLine -> {
			orderLine.setM_Warehouse_ID(order.getM_Warehouse_ID());
			orderLine.saveEx();
		});
	}
	
	/**
	 * Configure Price List after change
	 * @param order
	 */
	private void configurePriceList(MOrder order) {
		Arrays.asList(order.getLines())
		.forEach(orderLine -> {
			orderLine.setPrice();
			orderLine.setTax();
			orderLine.saveEx();
			if(Optional.ofNullable(orderLine.getPriceActual()).orElse(Env.ZERO).signum() == 0) {
				orderLine.deleteEx(true);
			}
		});
	}
	
	/**
	 * Validate User PIN
	 * @param userPin
     */
	private Empty.Builder validatePIN(ValidatePINRequest request) {
		MPOS pos = getPOSFromUuid(request.getPosUuid());
		if(Util.isEmpty(request.getPin())) {
			throw new AdempiereException("@UserPIN@ @IsMandatory@");
		}
		int supervisorId = pos.get_ValueAsInt("Supervisor_ID");
		MUser user = MUser.get(Env.getCtx(), (supervisorId > 0? supervisorId: pos.getSalesRep_ID()));
		if(supervisorId <= 0) {
			supervisorId = user.getSupervisor_ID();
		}
		if(supervisorId > 0) {
			MUser supervisor = MUser.get(Env.getCtx(), supervisorId);
			Optional<String> superVisorName = Optional.ofNullable(supervisor.getName());
			if (Util.isEmpty(supervisor.getUserPIN())) {
				throw new AdempiereException("@Supervisor@ \"" + superVisorName.orElseGet(() -> "") + "\": @UserPIN@ @NotFound@");
			}
			//	Validate PIN
			if(!request.getPin().equals(supervisor.getUserPIN())) {
				throw new AdempiereException("@Invalid@ @UserPIN@");
			}
		} else {	//	Find a supervisor for POS
			boolean match = new Query(Env.getCtx(), I_AD_User.Table_Name, "IsPOSManager = 'Y' AND UserPIN = ?", null)
					.setParameters(String.valueOf(request.getPin()))
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.match();
			if(!match) {
				throw new AdempiereException("@Invalid@ @UserPIN@");
			}
		}
		//	Default
		return Empty.newBuilder();
	}
	
	/**
	 * Load Price List Version from Price List
	 * @param priceListId
	 * @param validFrom
	 * @param transactionName
	 * @return
	 * @return MPriceListVersion
	 */
	public MPriceListVersion loadPriceListVersion(int priceListId, Timestamp validFrom, String transactionName) {
		MPriceList priceList = MPriceList.get(Env.getCtx(), priceListId, transactionName);
		//
		return priceList.getPriceListVersion(validFrom);
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
		Trx.run(transactionName -> {
			MOrderLine orderLine = new Query(Env.getCtx(), I_C_OrderLine.Table_Name, I_C_OrderLine.COLUMNNAME_UUID + " = ?", transactionName)
					.setParameters(request.getOrderLineUuid())
					.setClient_ID()
					.first();
			if(orderLine != null
					&& orderLine.getC_OrderLine_ID() != 0) {
				//	Validate processed Order
				if(orderLine.isProcessed()) {
					throw new AdempiereException("@C_OrderLine_ID@ @Processed@");
				}
				if(orderLine != null
						&& orderLine.getC_Order_ID() >= 0) {
					orderLine.deleteEx(true);
				}
			}
		});
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
		int orderId = RecordUtil.getIdFromUuid(I_C_Order.Table_Name, request.getOrderUuid(), null);
		MOrder order = new Query(Env.getCtx(), I_C_Order.Table_Name, I_C_Order.COLUMNNAME_C_Order_ID + " = ?", null)
				.setParameters(orderId)
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
	 * Delete payment from uuid
	 * @param request
	 * @return
	 */
	private Empty.Builder deletePayment(DeletePaymentRequest request) {
		if(Util.isEmpty(request.getPaymentUuid())) {
			throw new AdempiereException("@C_Payment_ID@ @NotFound@");
		}
		int paymentId = RecordUtil.getIdFromUuid(I_C_Payment.Table_Name, request.getPaymentUuid(), null);
		MPayment payment = new Query(Env.getCtx(), I_C_Payment.Table_Name, I_C_Payment.COLUMNNAME_C_Payment_ID + " = ?", null)
				.setParameters(paymentId)
				.setClient_ID()
				.first();
		if(payment == null
				|| payment.getC_Payment_ID() == 0) {
			return Empty.newBuilder();
		}
		//	Validate drafted
		if(!DocumentUtil.isDrafted(payment)) {
			throw new AdempiereException("@C_Payment_ID@ @Processed@");
		}
		//	Validate processed Order
		if(payment.isProcessed()) {
			throw new AdempiereException("@C_Payment_ID@ @Processed@");
		}
		//	
		if(payment != null
				&& payment.getC_Payment_ID() >= 0) {
			payment.deleteEx(true);
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
		int orderLineId = RecordUtil.getIdFromUuid(I_C_OrderLine.Table_Name, request.getOrderLineUuid(), null);
		if(orderLineId <= 0) {
			return OrderLine.newBuilder();
		}
		//	Quantity
		return convertOrderLine(
				updateOrderLine(orderLineId, 
						ValueUtil.getBigDecimalFromDecimal(request.getQuantity()), 
						ValueUtil.getBigDecimalFromDecimal(request.getPrice()), 
						ValueUtil.getBigDecimalFromDecimal(request.getDiscountRate()),
						request.getIsAddQuantity(),
						RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid(), null)));
	}
	
	/**
	 * Create order line and return this
	 * @param request
	 * @return
	 */
	private OrderLine.Builder createAndConvertOrderLine(CreateOrderLineRequest request) {
		//	Validate Order
		if(Util.isEmpty(request.getOrderUuid())) {
			throw new AdempiereException("@C_Order_ID@ @NotFound@");
		}
		//	Validate Product and charge
		if(Util.isEmpty(request.getProductUuid())
				&& Util.isEmpty(request.getChargeUuid())) {
			throw new AdempiereException("@M_Product_ID@ / @C_Charge_ID@ @NotFound@");
		}
		int orderId = RecordUtil.getIdFromUuid(I_C_Order.Table_Name, request.getOrderUuid(), null);
		if(orderId <= 0) {
			return OrderLine.newBuilder();
		}
		//	Quantity
		return convertOrderLine(
				addOrderLine(orderId, 
						RecordUtil.getIdFromUuid(I_M_Product.Table_Name, request.getProductUuid(), null), 
						RecordUtil.getIdFromUuid(I_C_Charge.Table_Name, request.getChargeUuid(), null), 
						RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid(), null), 
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
				.setPriceList(ValueUtil.getDecimalFromBigDecimal(orderLine.getPriceList()))
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
		String productValue = null;
		if(key.getM_Product_ID() > 0) {
			productValue = MProduct.get(Env.getCtx(), key.getM_Product_ID()).getValue();
		}
		return Key.newBuilder()
				.setUuid(ValueUtil.validateNull(key.getUUID()))
				.setId(key.getC_POSKeyLayout_ID())
				.setName(ValueUtil.validateNull(key.getName()))
				//	TODO: Color
				.setSequence(key.getSeqNo())
				.setSpanX(key.getSpanX())
				.setSpanY(key.getSpanY())
				.setSubKeyLayoutUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_POSKeyLayout.Table_Name, key.getSubKeyLayout_ID())))
				.setQuantity(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(key.getQty()).orElse(Env.ZERO)))
				.setProductValue(ValueUtil.validateNull(productValue))
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
		//	
		AtomicReference<MOrderLine> orderLineReference = new AtomicReference<MOrderLine>();
		Trx.run(transactionName -> {
			MOrder order = new MOrder(Env.getCtx(), orderId, transactionName);
			//	Valid Complete
			if (!DocumentUtil.isDrafted(order)) {
				throw new AdempiereException("@C_Order_ID@ @IsDrafted@");
			}
			setCurrentDate(order);
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
				BigDecimal quantityToOrder = quantity;
				if(quantity == null) {
					quantityToOrder = orderLine.getQtyOrdered();
					quantityToOrder = quantityToOrder.add(Env.ONE);
				}
				orderLine.setQty(quantityToOrder);
				orderLine.setPrice(currentPrice); //	sets List/limit
				orderLine.saveEx();
				orderLineReference.set(orderLine);
			} else {
				BigDecimal quantityToOrder = quantity;
				if(quantity == null) {
					quantityToOrder = Env.ONE;
				}
		        //create new line
				MOrderLine orderLine = new MOrderLine(order);
				if(chargeId > 0) {
					orderLine.setC_Charge_ID(chargeId);
				} else if(productId > 0) {
					orderLine.setProduct(MProduct.get(order.getCtx(), productId));
				}
				orderLine.setQty(quantityToOrder);
				orderLine.setPrice();
				//	Save Line
				orderLine.saveEx();
				orderLineReference.set(orderLine);
			}
		});
		return orderLineReference.get();
			
	} //	addOrUpdateLine
	
	/***
	 * Update order line
	 * @param orderLineId
	 * @param quantity
	 * @param price
	 * @param discountRate
	 * @param isAddQuantity
	 * @param warehouseId
	 * @return
	 */
	private MOrderLine updateOrderLine(int orderLineId, BigDecimal quantity, BigDecimal price, BigDecimal discountRate, boolean isAddQuantity, int warehouseId) {
		if(orderLineId <= 0) {
			return null;
		}
		AtomicReference<MOrderLine> maybeOrderLine = new AtomicReference<MOrderLine>();
		Trx.run(transactionName -> {
			MOrderLine orderLine = new MOrderLine(Env.getCtx(), orderLineId, transactionName);
			MOrder order = orderLine.getParent();
			setCurrentDate(order);
			orderLine.setHeaderInfo(order);
			//	Valid Complete
			if (!DocumentUtil.isDrafted(order)) {
				throw new AdempiereException("@C_Order_ID@ @IsDrafted@");
			}
			//	Get if is null
			BigDecimal quantityToOrder = quantity;
			if(quantity == null
					|| quantity.equals(Env.ZERO)) {
				quantityToOrder = orderLine.getQtyEntered();
			} else if(isAddQuantity) {
				BigDecimal currentQuantity = orderLine.getQtyEntered();
				if(currentQuantity == null) {
					currentQuantity = Env.ZERO;
				}
				quantityToOrder = currentQuantity.add(quantity);
			}
			BigDecimal priceToOrder = price;
			if(price == null
					|| price.equals(Env.ZERO)) {
				BigDecimal discountAmount = orderLine.getPriceList().multiply(Optional.ofNullable(discountRate).orElse(Env.ZERO).divide(Env.ONEHUNDRED));
				priceToOrder = orderLine.getPriceList().subtract(discountAmount);
			}
			if(warehouseId > 0) {
				orderLine.setM_Warehouse_ID(warehouseId);
			}
			//	Set values
			orderLine.setPrice(priceToOrder); //	sets List/limit
			orderLine.setQty(quantityToOrder);
			orderLine.setTax();
			orderLine.saveEx();
			maybeOrderLine.set(orderLine);
		});
		return maybeOrderLine.get();
	} //	UpdateLine
	
	/**
	 * Get list from user
	 * @param request
	 * @return
	 */
	private ListPointOfSalesResponse.Builder convertPointOfSalesList(ListPointOfSalesRequest request) {
		ListPointOfSalesResponse.Builder builder = ListPointOfSalesResponse.newBuilder();
		if(Util.isEmpty(request.getUserUuid())) {
			throw new AdempiereException("@SalesRep_ID@ @NotFound@");
		}
		int salesRepresentativeId = RecordUtil.getIdFromUuid(I_AD_User.Table_Name,request.getUserUuid(), null);
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Get POS List
		boolean isListWithSharedPOS = M_Element.get(Env.getCtx(), "IsSharedPOS") != null;
		String whereClause = "(AD_Org_ID = ? OR SalesRep_ID = ?)";
		List<Object> parameters = new ArrayList<>();
		parameters.add(Env.getAD_Org_ID(Env.getCtx()));
		parameters.add(salesRepresentativeId);
		if(isListWithSharedPOS) {
			whereClause = "AD_Org_ID = ? OR SalesRep_ID = ? OR EXISTS(SELECT 1 FROM AD_User u WHERE u.AD_User_ID = ? AND IsPOSManager = 'Y') OR (AD_Org_ID = ? AND IsSharedPOS = 'Y')";
			parameters.add(salesRepresentativeId);
			parameters.add(Env.getAD_Org_ID(Env.getCtx()));
		}
		Query query = new Query(Env.getCtx() , I_C_POS.Table_Name , whereClause, null)
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.setParameters(parameters)
				.setOrderBy(I_C_POS.COLUMNNAME_Name);
		int count = query.count();
		query
			.setLimit(limit, offset)
			.<MPOS>list()
			.forEach(pos -> builder.addSellingPoints(convertPointOfSales(pos)));
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
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
	private PointOfSales.Builder getPosBuilder(PointOfSalesRequest request) {
		return convertPointOfSales(getPOSFromUuid(request.getPosUuid()));
	}
	
	/**
	 * Get POS from UUID
	 * @param uuid
	 * @return
	 */
	private MPOS getPOSFromUuid(String uuid) {
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, uuid, null);
		if(posId <= 0) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		return MPOS.get(Env.getCtx(), posId);
	}
	
	/**
	 * Convert POS
	 * @param pos
	 * @return
	 */
	private PointOfSales.Builder convertPointOfSales(MPOS pos) {
		PointOfSales.Builder builder = PointOfSales.newBuilder()
				.setUuid(ValueUtil.validateNull(pos.getUUID()))
				.setId(pos.getC_POS_ID())
				.setName(ValueUtil.validateNull(pos.getName()))
				.setDescription(ValueUtil.validateNull(pos.getDescription()))
				.setHelp(ValueUtil.validateNull(pos.getHelp()))
				.setIsModifyPrice(pos.isModifyPrice())
				.setIsPosRequiredPin(pos.isPOSRequiredPIN())
				.setSalesRepresentative(convertSalesRepresentative(MUser.get(pos.getCtx(), pos.getSalesRep_ID())))
				.setTemplateBusinessPartner(ConvertUtil.convertBusinessPartner(pos.getBPartner()))
				.setKeyLayoutUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_POSKeyLayout.Table_Name, pos.getC_POSKeyLayout_ID())))
				.setIsAisleSeller(pos.get_ValueAsBoolean("IsAisleSeller"))
				.setIsSharedPos(pos.get_ValueAsBoolean("IsSharedPOS"))
				.setConversionTypeUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_ConversionType.Table_Name, pos.get_ValueAsInt(I_C_ConversionType.COLUMNNAME_C_ConversionType_ID))));
		//	Special values
		builder
			.setIsAllowsModifyQuantity(pos.get_ValueAsBoolean("IsAllowsModifyQuantity"))
			.setIsAllowsReturnOrder(pos.get_ValueAsBoolean("IsAllowsReturnOrder"))
			.setIsAllowsCollectOrder(pos.get_ValueAsBoolean("IsAllowsCollectOrder"))
			.setIsAllowsCreateOrder(pos.get_ValueAsBoolean("IsAllowsCreateOrder"))
			.setIsDisplayTaxAmount(pos.get_ValueAsBoolean("IsDisplayTaxAmount"))
			.setIsDisplayDiscount(pos.get_ValueAsBoolean("IsDisplayDiscount"))
			.setMaximumRefundAllowed(ValueUtil.getDecimalFromBigDecimal((BigDecimal) pos.get_Value("MaximumRefundAllowed")))
			.setMaximumDailyRefundAllowed(ValueUtil.getDecimalFromBigDecimal((BigDecimal) pos.get_Value("MaximumDailyRefundAllowed")));
		if(pos.get_ValueAsInt("RefundReferenceCurrency_ID") > 0) {
			builder.setRefundReferenceCurrency(ConvertUtil.convertCurrency(MCurrency.get(Env.getCtx(), pos.get_ValueAsInt("RefundReferenceCurrency_ID"))));
		}
		//	Set Price List and currency
		if(pos.getM_PriceList_ID() != 0) {
			MPriceList priceList = MPriceList.get(Env.getCtx(), pos.getM_PriceList_ID(), null);
			builder.setPriceList(ConvertUtil.convertPriceList(priceList));
		}
		//	Bank Account
		if(pos.getC_BankAccount_ID() != 0) {
			builder.setCashBankAccount(ConvertUtil.convertBankAccount(MBankAccount.get(Env.getCtx(), pos.getC_BankAccount_ID())));
		}
		//	Bank Account to transfer
		if(pos.getCashTransferBankAccount_ID() != 0) {
			builder.setCashBankAccount(ConvertUtil.convertBankAccount(MBankAccount.get(Env.getCtx(), pos.getCashTransferBankAccount_ID())));
		}
		//	Warehouse
		if(pos.getM_Warehouse_ID() > 0) {
			MWarehouse warehouse = MWarehouse.get(Env.getCtx(), pos.getM_Warehouse_ID());
			builder.setWarehouse(ConvertUtil.convertWarehouse(warehouse));
		}
		//	Price List
		if(pos.get_ValueAsInt("DisplayCurrency_ID") > 0) {
			MCurrency displayCurency = MCurrency.get(Env.getCtx(), pos.get_ValueAsInt("DisplayCurrency_ID"));
			builder.setDisplayCurrency(ConvertUtil.convertCurrency(displayCurency));
		}
		//	Document Type
		if(pos.getC_DocType_ID() > 0) {
			builder.setDocumentType(ConvertUtil.convertDocumentType(MDocType.get(Env.getCtx(), pos.getC_DocType_ID())));
		}
		return builder;
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
	private Order.Builder createOrder(CreateOrderRequest request) {
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		AtomicReference<MOrder> maybeOrder = new AtomicReference<MOrder>();
		Trx.run(transactionName -> {
			int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), transactionName);
			if(posId <= 0) {
				throw new AdempiereException("@C_POS_ID@ @NotFound@");
			}
			MPOS pos = MPOS.get(Env.getCtx(), posId);
			//	
			MOrder salesOrder = new Query(Env.getCtx(), I_C_Order.Table_Name, 
					"DocStatus = 'DR' "
					+ "AND C_POS_ID = ? "
					+ "AND NOT EXISTS(SELECT 1 "
					+ "					FROM C_OrderLine ol "
					+ "					WHERE ol.C_Order_ID = C_Order.C_Order_ID) ", null)
					.setParameters(pos.getC_POS_ID())
					.first();
			//	Validate
			if(salesOrder == null) {
				salesOrder = new MOrder(Env.getCtx(), 0, null);
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
			int warehouseId = pos.getM_Warehouse_ID();
			int priceListId = pos.getM_PriceList_ID();
			if(!Util.isEmpty(request.getWarehouseUuid())) {
				warehouseId = RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid(), transactionName);
			}
			//	From POS
			if(warehouseId < 0) {
				warehouseId = pos.getM_Warehouse_ID();
			}
			//	Price List
			if(!Util.isEmpty(request.getPriceListUuid())) {
				priceListId = RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, request.getPriceListUuid(), transactionName);
			}
			//	From POS
			if(priceListId < 0) {
				priceListId = pos.getM_Warehouse_ID();
			}
			salesOrder.setM_PriceList_ID(priceListId);
			salesOrder.setM_Warehouse_ID(warehouseId);
			//	Document Type
			int documentTypeId = 0;
			if(!Util.isEmpty(request.getDocumentTypeUuid())) {
				documentTypeId = RecordUtil.getIdFromUuid(I_C_DocType.Table_Name, request.getDocumentTypeUuid(), transactionName);
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
			//	Conversion Type
			if(pos.get_ValueAsInt(MOrder.COLUMNNAME_C_ConversionType_ID) > 0) {
				salesOrder.setC_ConversionType_ID(pos.get_ValueAsInt(MOrder.COLUMNNAME_C_ConversionType_ID));
			}
			//	Set business partner
			setBPartner(pos, salesOrder, request.getCustomerUuid(), request.getSalesRepresentativeUuid(), transactionName);
			maybeOrder.set(salesOrder);
		});
		//	Convert order
		return convertOrder(maybeOrder.get());
	}
	
	/**
	 * Set current date to order
	 * @param salesOrder
	 * @return void
	 */
	private void setCurrentDate(MOrder salesOrder) {
		if(!salesOrder.getDateOrdered().equals(getDate())
				|| !salesOrder.getDateAcct().equals(getDate())) {
			salesOrder.setDateOrdered(getDate());
			salesOrder.setDateAcct(getDate());
			salesOrder.saveEx();
		}
	}
	
	/**
	 * Set current date to order
	 * @param payment
	 * @return void
	 */
	private void setCurrentDate(MPayment payment) {
		if(!payment.getDateTrx().equals(getDate())) {
			payment.setDateTrx(getDate());
			payment.saveEx();
		}
	}
	
	/**
	 * Set business partner from uuid
	 * @param pos
	 * @param salesOrder
	 * @param businessPartnerUuid
	 * @param salesRepresentativeUuid
	 * @param transactionName
	 */
	private void setBPartner(MPOS pos, MOrder salesOrder, String businessPartnerUuid, String salesRepresentativeUuid, String transactionName) {
		//	Valid if has a Order
		if(DocumentUtil.isCompleted(salesOrder)
				|| DocumentUtil.isVoided(salesOrder)) {
			return;
		}
		//	Get BP
		MBPartner businessPartner = null;
		if(!Util.isEmpty(businessPartnerUuid)) {
			int businessPartnerId = RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, businessPartnerUuid, transactionName);
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
		businessPartner.set_TrxName(transactionName);
		salesOrder.setBPartner(businessPartner);
		if(businessPartner.getM_PriceList_ID() > 0) {
			MPriceList businesPartnerPriceList = MPriceList.get(salesOrder.getCtx(), businessPartner.getM_PriceList_ID(), transactionName);
			MPriceList currentPriceList = MPriceList.get(salesOrder.getCtx(), pos.getM_PriceList_ID(), transactionName);
			if(currentPriceList.getC_Currency_ID() != businesPartnerPriceList.getC_Currency_ID()) {
				salesOrder.setM_PriceList_ID(currentPriceList.getM_PriceList_ID());
			}
		}
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
			if(salesOrder.getPaymentRule() == null) {
				salesOrder.setPaymentRule(MOrder.PAYMENTRULE_Cash);
			}
		}
		//	Set Sales Representative
		int salesRepresentativeId = RecordUtil.getIdFromUuid(I_AD_User.Table_Name, salesRepresentativeUuid, transactionName);
		if(salesRepresentativeId <= 0) {
			MUser currentUser = MUser.get(salesOrder.getCtx());
			if(pos.get_ValueAsBoolean("IsSharedPOS")
					|| currentUser.get_ValueAsBoolean("IsPOSManager")) {
				salesRepresentativeId = currentUser.getAD_User_ID();
			} else if (businessPartner.getSalesRep_ID() != 0) {
				salesRepresentativeId = salesOrder.getC_BPartner().getSalesRep_ID();
			} else {
				salesRepresentativeId = pos.getSalesRep_ID();
			}
		}
		//	Set
		if(salesRepresentativeId > 0) {
			salesOrder.setSalesRep_ID(salesRepresentativeId);
		}
		setCurrentDate(salesOrder);
		//	Save Header
		salesOrder.saveEx();
		//	Load Price List Version
		MPriceList priceList = MPriceList.get(Env.getCtx(), salesOrder.getM_PriceList_ID(), transactionName);
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
	 * Convert payment
	 * @param payment
	 * @return
	 */
	private Payment.Builder convertPayment(MPayment payment) {
		Payment.Builder builder = Payment.newBuilder();
		if(payment == null) {
			return builder;
		}
		//	
		MRefList reference = MRefList.get(Env.getCtx(), MPayment.DOCSTATUS_AD_REFERENCE_ID, payment.getDocStatus(), null);
		//	Convert
		builder
			.setId(payment.getC_Payment_ID())
			.setUuid(ValueUtil.validateNull(payment.getUUID()))
			.setOrderUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_Order.Table_Name, payment.getC_Order_ID())))
			.setDocumentNo(ValueUtil.validateNull(payment.getDocumentNo()))
			.setTenderTypeCode(ValueUtil.validateNull(payment.getTenderType()))
			.setPaymentMethodUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_PaymentMethod.Table_Name, payment.get_ValueAsInt("C_PaymentMethod_ID"))))
			.setReferenceNo(ValueUtil.validateNull(Optional.ofNullable(payment.getCheckNo()).orElse(payment.getDocumentNo())))
			.setDescription(ValueUtil.validateNull(payment.getDescription()))
			.setAmount(ValueUtil.getDecimalFromBigDecimal(payment.getPayAmt()))
			.setBankUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_Bank.Table_Name, payment.getC_Bank_ID())))
			.setCustomer(convertCustomer((MBPartner) payment.getC_BPartner()))
			.setCurrencyUuid(RecordUtil.getUuidFromId(I_C_Currency.Table_Name, payment.getC_Currency_ID()))
			.setPaymentDate(ValueUtil.convertDateToString(payment.getDateTrx()))
			.setIsRefund(!payment.isReceipt())
			.setPaymentAccountDate(ValueUtil.convertDateToString(payment.getDateAcct()))
			.setDocumentStatus(ConvertUtil.convertDocumentStatus(ValueUtil.validateNull(payment.getDocStatus()), 
					ValueUtil.validateNull(ValueUtil.getTranslation(reference, I_AD_Ref_List.COLUMNNAME_Name)), 
					ValueUtil.validateNull(ValueUtil.getTranslation(reference, I_AD_Ref_List.COLUMNNAME_Description))))
		;
		return builder;
	}
	
	/**
	 * Update payment if is required
	 * @param request
	 * @return
	 */
	private MPayment updatePayment(UpdatePaymentRequest request) {
		AtomicReference<MPayment> maybePayment = new AtomicReference<MPayment>();
		Trx.run(transactionName -> {
			String tenderType = request.getTenderTypeCode();
			int paymentId = RecordUtil.getIdFromUuid(I_C_Payment.Table_Name, request.getPaymentUuid(), transactionName);
			if(paymentId <= 0) {
				throw new AdempiereException("@C_Payment_ID@ @NotFound@");
			}
			MPayment payment = new MPayment(Env.getCtx(), paymentId, transactionName);
			if(!Util.isEmpty(tenderType)) {
				payment.setTenderType(tenderType);
			}
			if(!Util.isEmpty(request.getPaymentDate())) {
	        	Timestamp date = ValueUtil.getDateFromString(request.getPaymentDate());
	        	if(date != null) {
	        		payment.setDateTrx(date);
	        	}
	        }
			if(!Util.isEmpty(request.getPaymentAccountDate())) {
	        	Timestamp date = ValueUtil.getDateFromString(request.getPaymentAccountDate());
	        	if(date != null) {
	        		payment.setDateAcct(date);
	        	}
	        }
			//	Set Bank Id
			if(!Util.isEmpty(request.getBankUuid())) {
				int bankId = RecordUtil.getIdFromUuid(I_C_Bank.Table_Name, request.getBankUuid(), transactionName);
				payment.set_ValueOfColumn(MBank.COLUMNNAME_C_Bank_ID, bankId);
			}
			//	Validate reference
			if(!Util.isEmpty(request.getReferenceNo())) {
				payment.addDescription(request.getReferenceNo());
			}
			//	Set Description
			if(!Util.isEmpty(request.getDescription())) {
				payment.addDescription(request.getDescription());
			}
	        //	Amount
	        BigDecimal paymentAmount = ValueUtil.getBigDecimalFromDecimal(request.getAmount());
	        payment.setPayAmt(paymentAmount);
	        payment.setOverUnderAmt(Env.ZERO);
	        setCurrentDate(payment);
			payment.saveEx(transactionName);
			maybePayment.set(payment);
		});
		//	Return payment
		return maybePayment.get();
	}
	
	/**
	 * Create Payment based on request, transaction name and pos
	 * @param request
	 * @param pointOfSalesDefinition
	 * @param transactionName
	 * @return
	 */
	private MPayment createPayment(MOrder salesOrder, CreatePaymentRequest request, MPOS pointOfSalesDefinition, String transactionName) {
		setCurrentDate(salesOrder);
		String tenderType = request.getTenderTypeCode();
		if(Util.isEmpty(tenderType)) {
			tenderType = MPayment.TENDERTYPE_Cash;
		}
		if(pointOfSalesDefinition.getC_BankAccount_ID() <= 0) {
			throw new AdempiereException("@NoCashBook@");
		}
		if(request.getAmount() == null) {
			throw new AdempiereException("@PayAmt@ @NotFound@");
		}
		//	Order
		int currencyId = RecordUtil.getIdFromUuid(I_C_Currency.Table_Name, request.getCurrencyUuid(), transactionName);
		if(currencyId <= 0) {
			currencyId = salesOrder.getC_Currency_ID();
		}
		Optional<BigDecimal> paidAmount = MPayment.getOfOrder(salesOrder).stream().map(payment -> getConvetedAmount(salesOrder, payment, payment.getPayAmt())).collect(Collectors.reducing(BigDecimal::add));
		//	
		MPayment payment = new MPayment(Env.getCtx(), 0, transactionName);
		payment.setC_BankAccount_ID(pointOfSalesDefinition.getC_BankAccount_ID());
		payment.setC_DocType_ID(!request.getIsRefund());
		payment.setAD_Org_ID(salesOrder.getAD_Org_ID());
        String value = DB.getDocumentNo(payment.getC_DocType_ID(), transactionName, false,  payment);
        payment.setDocumentNo(value);
        if(!Util.isEmpty(request.getPaymentAccountDate())) {
        	Timestamp date = ValueUtil.getDateFromString(request.getPaymentAccountDate());
        	if(date != null) {
        		payment.setDateAcct(date);
        	}
        }
        payment.setTenderType(tenderType);
        payment.setDescription(Optional.ofNullable(request.getDescription()).orElse(salesOrder.getDescription()));
        payment.setC_BPartner_ID (salesOrder.getC_BPartner_ID());
        payment.setC_Currency_ID(currencyId);
        payment.setC_POS_ID(pointOfSalesDefinition.getC_POS_ID());
        if(salesOrder.getSalesRep_ID() > 0) {
        	payment.set_ValueOfColumn("CollectingAgent_ID", salesOrder.getSalesRep_ID());
        }
        if(pointOfSalesDefinition.get_ValueAsInt(I_C_ConversionType.COLUMNNAME_C_ConversionType_ID) > 0) {
        	payment.setC_ConversionType_ID(pointOfSalesDefinition.get_ValueAsInt(I_C_ConversionType.COLUMNNAME_C_ConversionType_ID));
        }
        //	Amount
        BigDecimal paymentAmount = ValueUtil.getBigDecimalFromDecimal(request.getAmount());
        payment.setPayAmt(paymentAmount);
        //	Order Reference
        payment.setC_Order_ID(salesOrder.getC_Order_ID());
        payment.setDocStatus(MPayment.DOCSTATUS_Drafted);
		int invoiceId = salesOrder.getC_Invoice_ID();
		if(invoiceId > 0) {
			payment.setC_Invoice_ID(invoiceId);
			MInvoice invoice = new MInvoice(Env.getCtx(), payment.getC_Invoice_ID(), transactionName);
			payment.setDescription(Msg.getMsg(Env.getCtx(), "Invoice No ") + invoice.getDocumentNo());
		} else {
			payment.setDescription(Msg.getMsg(Env.getCtx(), "Order No ") + salesOrder.getDocumentNo());
		}
		switch (tenderType) {
			case MPayment.TENDERTYPE_Check:
				//	TODO: Add references
//				payment.setAccountNo(accountNo);
//				payment.setRoutingNo(routingNo);
				payment.setCheckNo(request.getReferenceNo());
				break;
			case MPayment.TENDERTYPE_DirectDebit:
				//	TODO: Add Information
//				payment.setRoutingNo(routingNo);
//				payment.setA_Country(accountCountry);
//				payment.setCreditCardVV(cVV);
				break;
			case MPayment.TENDERTYPE_CreditCard:
				//	TODO: Add Information
//				payment.setCreditCard(MPayment.TRXTYPE_Sales, cardtype, cardNo, cvc, month, year);
				break;
			case MPayment.TENDERTYPE_MobilePaymentInterbank:
				payment.setR_PnRef(request.getReferenceNo());
				break;
			case MPayment.TENDERTYPE_Zelle:
				payment.setR_PnRef(request.getReferenceNo());
				break;
			case MPayment.TENDERTYPE_CreditMemo:
				payment.setR_PnRef(request.getReferenceNo());
				break;
			default:
				payment.setDescription(request.getDescription());
				break;
		}
		//	Payment Method
		if(!Util.isEmpty(request.getPaymentMethodUuid())) {
			int paymentMethodId = RecordUtil.getIdFromUuid(I_C_PaymentMethod.Table_Name, request.getPaymentMethodUuid(), transactionName);
			if(paymentMethodId > 0) {
				payment.set_ValueOfColumn(I_C_PaymentMethod.COLUMNNAME_C_PaymentMethod_ID, paymentMethodId);
			}
		}
		//	Set Bank Id
		if(!Util.isEmpty(request.getBankUuid())) {
			int bankId = RecordUtil.getIdFromUuid(I_C_Bank.Table_Name, request.getBankUuid(), transactionName);
			payment.set_ValueOfColumn(MBank.COLUMNNAME_C_Bank_ID, bankId);
		}
		//	Validate reference
		if(!Util.isEmpty(request.getReferenceNo())) {
			payment.setDocumentNo(request.getReferenceNo());
			payment.addDescription(request.getReferenceNo());
		}
		setCurrentDate(payment);
		//	
		BigDecimal convertedPaymentAmount = getConvetedAmount(salesOrder, payment, payment.getPayAmt());
		if(paidAmount.isPresent()) {
			payment.setOverUnderAmt(salesOrder.getGrandTotal().subtract(paidAmount.get().add(convertedPaymentAmount)));
		} else {
			payment.setOverUnderAmt(salesOrder.getGrandTotal().subtract(convertedPaymentAmount));
		}
		payment.saveEx(transactionName);
		return payment;
	}
	
	/**
	 * create Payment
	 * @param request
	 * @return
	 */
	private MPayment createPayment(CreatePaymentRequest request) {
		AtomicReference<MPayment> maybePayment = new AtomicReference<MPayment>();
		Trx.run(transactionName -> {
			int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), transactionName);
			if(posId <= 0) {
				throw new AdempiereException("@C_POS_ID@ @NotFound@");
			}
			MPOS pos = MPOS.get(Env.getCtx(), posId);
			MOrder salesOrder = getOrder(request.getOrderUuid(), transactionName);
			maybePayment.set(createPayment(salesOrder, request, pos, transactionName));
		});
		//	Return payment
		return maybePayment.get();
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
			.setDateOrdered(ValueUtil.convertDateToString(order.getDateOrdered()))
			.setCustomer(convertCustomer((MBPartner) order.getC_BPartner()));
	}
	
	/**
	 * Get Product Price Method
	 * @param context
	 * @param request
	 * @return
	 */
	private ListProductPriceResponse.Builder getProductPriceList(ListProductPriceRequest request) {
		ListProductPriceResponse.Builder builder = ListProductPriceResponse.newBuilder();
		if(Util.isEmpty(request.getPosUuid())) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		MPOS pos = MPOS.get(Env.getCtx(), posId);
		//	Validate Price List
		int priceListId = pos.getM_PriceList_ID();
		if(!Util.isEmpty(request.getPriceListUuid())) {
			priceListId = RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, request.getPriceListUuid(), null);
		}
		MPriceList priceList = MPriceList.get(Env.getCtx(), priceListId, null);
		//	Get Valid From
		AtomicReference<Timestamp> validFrom = new AtomicReference<>();
		if(!Util.isEmpty(request.getValidFrom())) {
			validFrom.set(ValueUtil.convertStringToDate(request.getValidFrom()));
		} else {
			validFrom.set(TimeUtil.getDay(System.currentTimeMillis()));
		}
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Dynamic where clause
		StringBuffer whereClause = new StringBuffer();
		//	Parameters
		List<Object> parameters = new ArrayList<Object>();
		//	For search value
		if(!Util.isEmpty(request.getSearchValue())) {
			whereClause.append("IsSold = 'Y' "
				+ "AND ("
				+ "UPPER(Value) LIKE '%' || UPPER(?) || '%'"
				+ "OR UPPER(Name) LIKE '%' || UPPER(?) || '%'"
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
		if(whereClause.length() > 0) {
			whereClause.append(" AND ");
		}
		//	Add Price List
		whereClause.append("(EXISTS(SELECT 1 FROM M_PriceList_Version plv "
				+ "INNER JOIN M_ProductPrice pp ON(pp.M_PriceList_Version_ID = plv.M_PriceList_Version_ID) "
				+ "WHERE plv.M_PriceList_ID = ? "
				+ "AND plv.ValidFrom <= ? "
				+ "AND plv.IsActive = 'Y' "
				+ "AND pp.PriceList IS NOT NULL AND pp.PriceList > 0 "
				+ "AND pp.PriceStd IS NOT NULL AND pp.PriceStd > 0 "
				+ "AND pp.M_Product_ID = M_Product.M_Product_ID))");
		//	Add parameters
		parameters.add(priceList.getM_PriceList_ID());
		parameters.add(TimeUtil.getDay(validFrom.get()));
		AtomicInteger warehouseId = new AtomicInteger(pos.getM_Warehouse_ID());
		if(!Util.isEmpty(request.getWarehouseUuid())) {
			warehouseId.set(RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid(), null));
		}
		int businessPartnerId = RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getBusinessPartnerUuid(), null);
		int displayCurrencyId = pos.get_ValueAsInt("DisplayCurrency_ID");
		int conversionTypeId = pos.get_ValueAsInt("C_ConversionType_ID");
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
			ProductPrice.Builder productPrice = convertProductPrice(
					product, 
					businessPartnerId, 
					priceList, 
					warehouseId.get(), 
					validFrom.get(),
					displayCurrencyId,
					conversionTypeId,
					null);
			if(productPrice.hasPriceList()
					&& productPrice.hasPriceStandard()
					&& productPrice.hasPriceLimit()) {
				builder.addProductPrices(productPrice);
			}
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
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
	private ProductPrice.Builder convertProductPrice(MProduct product, int businessPartnerId, MPriceList priceList, int warehouseId, Timestamp validFrom, int displayCurrencyId, int conversionTypeId, BigDecimal priceQuantity) {
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
		builder.setValidFrom(ValueUtil.validateNull(ValueUtil.convertDateToString(productPricing.getPriceDate())));
		builder.setPriceListName(ValueUtil.validateNull(priceList.getName()));
		//	Pricing
		builder.setPricePrecision(productPricing.getPrecision());
		//	Prices
		if(Optional.ofNullable(productPricing.getPriceStd()).orElse(Env.ZERO).signum() > 0) {
			builder.setPriceList(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(productPricing.getPriceList()).orElse(Env.ZERO)));
			builder.setPriceStandard(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(productPricing.getPriceStd()).orElse(Env.ZERO)));
			builder.setPriceLimit(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(productPricing.getPriceLimit()).orElse(Env.ZERO)));
			//	Get from schema
			if(displayCurrencyId > 0) {
				builder.setDisplayCurrency(ConvertUtil.convertCurrency(MCurrency.get(Env.getCtx(), displayCurrencyId)));
				//	Get
				BigDecimal conversionRate = Optional.ofNullable(MConversionRate.getRate(priceList.getC_Currency_ID(), displayCurrencyId, getDate(), conversionTypeId, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx())))
						.orElse(Env.ZERO);
				builder.setDisplayPriceList(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(productPricing.getPriceList()).orElse(Env.ZERO).multiply(conversionRate).setScale(productPricing.getPrecision(), BigDecimal.ROUND_HALF_UP)));
				builder.setDisplayPriceStandard(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(productPricing.getPriceStd()).orElse(Env.ZERO).multiply(conversionRate).setScale(productPricing.getPrecision(), BigDecimal.ROUND_HALF_UP)));
				builder.setDisplayPriceLimit(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(productPricing.getPriceLimit()).orElse(Env.ZERO).multiply(conversionRate).setScale(productPricing.getPrecision(), BigDecimal.ROUND_HALF_UP)));
			}
		}
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
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		MPOS pos = MPOS.get(Env.getCtx(), posId);
		//	Validate Price List
		int priceListId = pos.getM_PriceList_ID();
		if(!Util.isEmpty(request.getPriceListUuid())) {
			priceListId = RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, request.getPriceListUuid(), null);
		}
		MPriceList priceList = MPriceList.get(Env.getCtx(), priceListId, null);
		AtomicInteger warehouseId = new AtomicInteger(pos.getM_Warehouse_ID());
		if(!Util.isEmpty(request.getWarehouseUuid())) {
			warehouseId.set(RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid(), null));
		}
		//	Get Valid From
		AtomicReference<Timestamp> validFrom = new AtomicReference<>();
		if(!Util.isEmpty(request.getValidFrom())) {
			validFrom.set(ValueUtil.convertStringToDate(request.getValidFrom()));
		} else {
			validFrom.set(TimeUtil.getDay(System.currentTimeMillis()));
		}
		int businessPartnerId = RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getBusinessPartnerUuid(), null);
		int displayCurrencyId = pos.get_ValueAsInt("DisplayCurrency_ID");
		int conversionTypeId = pos.get_ValueAsInt("C_ConversionType_ID");
		return convertProductPrice(
				product, 
				businessPartnerId, 
				priceList, 
				warehouseId.get(), 
				validFrom.get(),
				displayCurrencyId,
				conversionTypeId,
				Env.ONE);
	}
}

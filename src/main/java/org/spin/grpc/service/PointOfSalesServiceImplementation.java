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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_Ref_List;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Bank;
import org.compiere.model.I_C_Charge;
import org.compiere.model.I_C_ConversionType;
import org.compiere.model.I_C_Currency;
import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_C_POS;
import org.compiere.model.I_C_POSKeyLayout;
import org.compiere.model.I_C_Payment;
import org.compiere.model.I_M_PriceList;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MBank;
import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MCharge;
import org.compiere.model.MConversionRate;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
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
import org.compiere.model.MStorage;
import org.compiere.model.MTax;
import org.compiere.model.MUser;
import org.compiere.model.MWarehouse;
import org.compiere.model.M_Element;
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
import org.spin.grpc.util.Charge;
import org.spin.grpc.util.CreateOrderLineRequest;
import org.spin.grpc.util.CreateOrderRequest;
import org.spin.grpc.util.CreatePaymentRequest;
import org.spin.grpc.util.DeleteOrderLineRequest;
import org.spin.grpc.util.DeleteOrderRequest;
import org.spin.grpc.util.DeletePaymentRequest;
import org.spin.grpc.util.Empty;
import org.spin.grpc.util.GetKeyLayoutRequest;
import org.spin.grpc.util.GetOrderRequest;
import org.spin.grpc.util.GetProductPriceRequest;
import org.spin.grpc.util.Key;
import org.spin.grpc.util.KeyLayout;
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
import org.spin.grpc.util.ProcessOrderRequest;
import org.spin.grpc.util.Product;
import org.spin.grpc.util.ProductPrice;
import org.spin.grpc.util.SalesRepresentative;
import org.spin.grpc.util.StoreGrpc.StoreImplBase;
import org.spin.grpc.util.UpdateOrderLineRequest;
import org.spin.grpc.util.UpdateOrderRequest;
import org.spin.grpc.util.UpdatePaymentRequest;
import org.spin.grpc.util.Warehouse;

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
	
	/**
	 * Process Order from Point of Sales
	 * @param request
	 * @return
	 */
	private MOrder processOrder(ProcessOrderRequest request) {
		AtomicReference<MOrder> orderReference = new AtomicReference<MOrder>();
		if(!Util.isEmpty(request.getOrderUuid())) {
			Trx.run(transactionName -> {
				MOrder order = getOrder(request.getOrderUuid(), transactionName);
				if(order == null) {
					throw new AdempiereException("@C_Order_ID@ @NotFound@");
				}
				if(!DocumentUtil.isDrafted(order)) {
					throw new AdempiereException("@C_Order_ID@ @IsCompleted@");
				}
				int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), transactionName);
				if(posId <= 0) {
					throw new AdempiereException("@C_POS_ID@ @NotFound@");
				}
				MPOS pos = MPOS.get(Env.getCtx(), posId);
				// In case the Order is Invalid, set to In Progress; otherwise it will not be completed
				if (order.getDocStatus().equalsIgnoreCase(MOrder.STATUS_Invalid))  {
					order.setDocStatus(MOrder.STATUS_InProgress);
				}
				//	Set Document Action
				order.setDocAction(DocAction.ACTION_Complete);
				order.setC_POS_ID(posId);
				order.saveEx();
				//	Update Process if exists
				if (!order.processIt(MOrder.DOCACTION_Complete)) {
					log.warning("@ProcessFailed@ :" + order.getDocumentInfo());
					throw new AdempiereException("@ProcessFailed@ :" + order.getDocumentInfo());
				}
				order.saveEx();
				//	Create or process payments
				if(request.getCreatePayments()) {
					if(request.getPaymentsList().size() == 0) {
						throw new AdempiereException("@C_Payment_ID@ @NotFound@");
					}
					//	Create
					request.getPaymentsList().forEach(paymentRequest -> createPayment(order, paymentRequest, pos, transactionName));
				}
				//	Get invoice if exists
				int invoiceId = order.getC_Invoice_ID();
				//	Process Payments
				MPayment.getOfOrder(order).forEach(payment -> {
					if(invoiceId > 0) {
						payment.setC_Invoice_ID(invoiceId);
						MInvoice invoice = new MInvoice(Env.getCtx(), payment.getC_Invoice_ID(), transactionName);
						payment.setDescription(Msg.parseTranslation(Env.getCtx(), "@C_Invoice_ID@ " + invoice.getDocumentNo()));
					} else {
						payment.setIsPrepayment(true);
						payment.setDescription(Msg.parseTranslation(Env.getCtx(), "@C_Invoice_ID@ " + order.getDocumentNo()));
					}
					payment.setDocAction(MPayment.DOCACTION_Complete);
					payment.saveEx(transactionName);
					if (!payment.processIt(MPayment.DOCACTION_Complete)) {
						log.warning("@ProcessFailed@ :" + payment.getDocumentInfo());
						throw new AdempiereException("@ProcessFailed@ :" + payment.getDocumentInfo());
					}
					order.saveEx(transactionName);
					MBankStatement.addPayment(payment);
				});
				//	Create
				orderReference.set(order);
			});
		}
		//	Return order
		return orderReference.get();
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
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		//	Dynamic where clause
		StringBuffer whereClause = new StringBuffer();
		//	Parameters
		List<Object> parameters = new ArrayList<Object>();
		//	Aisle Seller
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		boolean isWithAisleSeller = M_Element.get(Env.getCtx(), "IsAisleSeller") != null;
		if(isWithAisleSeller && request.getIsAisleSeller()) {
			whereClause.append("C_Order.C_POS_ID <> ? AND EXISTS(SELECT 1 FROM C_POS p WHERE p.C_POS_ID = C_Order.C_POS_ID AND p.IsAisleSeller = 'Y')");
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
		if(request.getDateOrderedFrom() > 0) {
			whereClause.append(" AND DateOrdered >= ?");
			parameters.add(new Timestamp(request.getDateOrderedFrom()));
		}
		//	Date Order To
		if(request.getDateOrderedTo() > 0) {
			whereClause.append(" AND DateOrdered <= ?");
			parameters.add(new Timestamp(request.getDateOrderedTo()));
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
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
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
				MOrder order = getOrder(request.getOrderUuid(), transactionName);
				if(order == null) {
					throw new AdempiereException("@C_Order_ID@ @NotFound@");
				}
				if(!DocumentUtil.isDrafted(order)) {
					throw new AdempiereException("@C_Order_ID@ @IsCompleted@");
				}
				//	Update if exists
				//	POS
				if(!Util.isEmpty(request.getPosUuid())) {
					int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), transactionName);
					if(posId > 0) {
						order.setC_POS_ID(posId);
					}
				}
				//	Document Type
				if(!Util.isEmpty(request.getDocumentTypeUuid())) {
					int documentTypeId = RecordUtil.getIdFromUuid(I_C_DocType.Table_Name, request.getDocumentTypeUuid(), transactionName);
					if(documentTypeId > 0) {
						order.setC_DocTypeTarget_ID(documentTypeId);
						//	Set Sequenced No
						String value = DB.getDocumentNo(documentTypeId, transactionName, false, order);
						if (value != null) {
							order.setDocumentNo(value);
						}
					}
				}
				//	Business partner
				if(!Util.isEmpty(request.getCustomerUuid())) {
					int businessPartnerId = RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getCustomerUuid(), transactionName);
					if(businessPartnerId > 0
							&& order.getC_POS_ID() > 0) {
						configureBPartner(order, businessPartnerId, transactionName);
					}
				}
				//	Description
				if(!Util.isEmpty(request.getDescription())) {
					order.setDescription(request.getDescription());
				}
				//	Save
				order.saveEx();
				orderReference.set(order);
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
			MPriceListVersion priceListVersion = loadPriceListVersion(order.getM_PriceList_ID(), order.getDateOrdered(), transactionName);
			if(priceListVersion != null) {
				MProductPrice[] productPrices = priceListVersion.getProductPrice("AND EXISTS("
						+ "SELECT 1 "
						+ "FROM C_OrderLine ol "
						+ "WHERE ol.C_Order_ID = " + order.getC_Order_ID() + " "
						+ "AND ol.M_Product_ID = M_ProductPrice.M_Product_ID)");
				//	Update Lines
				Arrays.asList(order.getLines())
					.forEach(orderLine -> {
						//	Verify if exist
						if(Arrays.asList(productPrices)
								.stream()
								.filter(productPrice -> productPrice.getM_Product_ID() == orderLine.getM_Product_ID())
								.findFirst()
								.isPresent()) {
							orderLine.setC_BPartner_ID(partner.getC_BPartner_ID());
							orderLine.setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
							orderLine.setPrice();
							orderLine.setTax();
							orderLine.saveEx();
						} else {
							orderLine.deleteEx(true);
						}
					});
			}
		}
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
						request.getIsAddQuantity()));
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
		//	
		AtomicReference<MOrderLine> orderLineReference = new AtomicReference<MOrderLine>();
		Trx.run(transactionName -> {
			MOrder order = new MOrder(Env.getCtx(), orderId, transactionName);
			//	Valid Complete
			if (!DocumentUtil.isDrafted(order))
				throw new AdempiereException("@C_Order_ID@ @IsDrafted@");
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
	 * @return
	 */
	private MOrderLine updateOrderLine(int orderLineId, BigDecimal quantity, BigDecimal price, BigDecimal discountRate, boolean isAddQuantity) {
		if(orderLineId <= 0) {
			return null;
		}
		AtomicReference<MOrderLine> maybeOrderLine = new AtomicReference<MOrderLine>();
		Trx.run(transactionName -> {
			MOrderLine orderLine = new MOrderLine(Env.getCtx(), orderLineId, transactionName);
			MOrder order = orderLine.getParent();
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
				priceToOrder = orderLine.getPriceEntered();
			}
			if(discountRate != null
					&& !discountRate.equals(Env.ZERO)) {
				BigDecimal discountAmount = orderLine.getPriceList().multiply(discountRate.divide(Env.ONEHUNDRED));
				priceToOrder = orderLine.getPriceList().subtract(discountAmount);
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
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		//	Get POS List
		boolean isListWithSharedPOS = M_Element.get(Env.getCtx(), "IsSharedPOS") != null;
		String whereClause = "(AD_Org_ID = ? OR SalesRep_ID = ?)";
		List<Object> parameters = new ArrayList<>();
		parameters.add(Env.getAD_Org_ID(Env.getCtx()));
		parameters.add(salesRepresentativeId);
		if(isListWithSharedPOS) {
			whereClause = "AD_Org_ID = ? AND (IsSharedPOS = 'Y' OR SalesRep_ID = ? OR EXISTS(SELECT 1 FROM AD_User u WHERE u.AD_User_ID = ? AND IsPOSManager = 'Y'))";
			parameters.add(salesRepresentativeId);
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
	private PointOfSales.Builder getPosBuilder(PointOfSalesRequest request) {
		int posId = RecordUtil.getIdFromUuid(I_C_POS.Table_Name, request.getPosUuid(), null);
		if(posId <= 0) {
			throw new AdempiereException("@C_POS_ID@ @NotFound@");
		}
		//	
		return convertPointOfSales(MPOS.get(Env.getCtx(), posId));
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
		//	Set Price List adn currency
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
			//	Set business partner
			setBPartner(pos, salesOrder, request.getCustomerUuid(), request.getSalesRepresentativeUuid(), transactionName);
			maybeOrder.set(salesOrder);
		});
		//	Convert order
		return convertOrder(maybeOrder.get());
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
			.setReferenceNo(ValueUtil.validateNull(payment.getCheckNo()))
			.setDescription(ValueUtil.validateNull(payment.getDescription()))
			.setAmount(ValueUtil.getDecimalFromBigDecimal(payment.getPayAmt()))
			.setBankUuid(ValueUtil.validateNull(RecordUtil.getUuidFromId(I_C_Bank.Table_Name, payment.getC_Bank_ID())))
			.setBusinessPartner(ConvertUtil.convertBusinessPartner((MBPartner) payment.getC_BPartner()))
			.setCurrencyUuid(RecordUtil.getUuidFromId(I_C_Currency.Table_Name, payment.getC_Currency_ID()))
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
		//	
		MPayment payment = new MPayment(Env.getCtx(), 0, transactionName);
		payment.setC_BankAccount_ID(pointOfSalesDefinition.getC_BankAccount_ID());
		payment.setC_DocType_ID(true);
		payment.setAD_Org_ID(salesOrder.getAD_Org_ID());
        String value = DB.getDocumentNo(payment.getC_DocType_ID(), transactionName, false,  payment);
        payment.setDocumentNo(value);
        payment.setDateAcct(salesOrder.getDateAcct());
        payment.setDateTrx(salesOrder.getDateOrdered());
        payment.setTenderType(tenderType);
        payment.setDescription(Optional.ofNullable(request.getDescription()).orElse(salesOrder.getDescription()));
        payment.setC_BPartner_ID (salesOrder.getC_BPartner_ID());
        payment.setC_Currency_ID(currencyId);
        payment.setC_POS_ID(pointOfSalesDefinition.getC_POS_ID());
        if(pointOfSalesDefinition.get_ValueAsInt(I_C_ConversionType.COLUMNNAME_C_ConversionType_ID) > 0) {
        	payment.setC_ConversionType_ID(pointOfSalesDefinition.get_ValueAsInt(I_C_ConversionType.COLUMNNAME_C_ConversionType_ID));
        }
        //	Amount
        BigDecimal paymentAmount = ValueUtil.getBigDecimalFromDecimal(request.getAmount());
        payment.setPayAmt(paymentAmount);
        payment.setOverUnderAmt(Env.ZERO);
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
			default:
				payment.setDescription(request.getDescription());
				break;
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
		//	
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
			priceList = new Query(Env.getCtx(), I_M_PriceList.Table_Name, "EXISTS(SELECT 1 FROM C_POS p WHERE p.M_PriceList_ID = M_PriceList.M_PriceList_ID AND p.AD_Org_ID IN(0, ?) AND p.SalesRep_ID = ?)", null)
					.setParameters(Env.getAD_Org_ID(Env.getCtx()), Env.getAD_User_ID(Env.getCtx()))
					.setClient_ID()
					.setOnlyActiveRecords(true)
					.first();
		} else {
			int priceListId = RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, priceListUuid, null);
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
		if(!Util.isEmpty(request.getPriceListUuid())) {
			if(whereClause.length() > 0) {
				whereClause.append(" AND ");
				whereClause.append("(EXISTS(SELECT 1 FROM M_PriceList_Version plv "
						+ "INNER JOIN M_ProductPrice pp ON(pp.M_PriceList_Version_ID = plv.M_PriceList_Version_ID) "
						+ "WHERE plv.M_PriceList_ID = ? "
						+ "AND plv.ValidFrom <= ? "
						+ "AND plv.IsActive = 'Y' "
						+ "AND pp.PriceList IS NOT NULL AND pp.PriceList > 0 "
						+ "AND pp.PriceStd IS NOT NULL AND pp.PriceStd > 0 "
						+ "AND pp.PriceLimit IS NOT NULL AND pp.PriceLimit > 0 "
						+ "AND pp.M_Product_ID = M_Product.M_Product_ID))");
				//	Add parameters
				parameters.add(RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, request.getPriceListUuid(), null));
				parameters.add(validFrom);
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
			ProductPrice.Builder productPrice = convertProductPrice(
					product, 
					RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getBusinessPartnerUuid(), null), 
					priceList, 
					RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid(), null), 
					validFrom, 
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
		//	Prices
		if(Optional.ofNullable(productPricing.getPriceList()).orElse(Env.ZERO).signum() > 0
				&& Optional.ofNullable(productPricing.getPriceStd()).orElse(Env.ZERO).signum() > 0
				&& Optional.ofNullable(productPricing.getPriceLimit()).orElse(Env.ZERO).signum() > 0) {
			builder.setPriceList(ValueUtil.getDecimalFromBigDecimal(productPricing.getPriceList()));
			builder.setPriceStandard(ValueUtil.getDecimalFromBigDecimal(productPricing.getPriceStd()));
			builder.setPriceLimit(ValueUtil.getDecimalFromBigDecimal(productPricing.getPriceLimit()));
			//	Get from schema
			int schemaCurrencyId = Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID");
			if(schemaCurrencyId > 0) {
				builder.setSchemaCurrency(ConvertUtil.convertCurrency(MCurrency.get(Env.getCtx(), schemaCurrencyId)));
				//	Get
				BigDecimal conversionRate = Optional.ofNullable(MConversionRate.getRate(priceList.getC_Currency_ID(), schemaCurrencyId, getDate(), getConversionTypeForPrice(), Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx())))
						.orElse(Env.ZERO);
				builder.setSchemaPriceList(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(productPricing.getPriceList()).orElse(Env.ZERO).multiply(conversionRate).setScale(productPricing.getPrecision(), BigDecimal.ROUND_HALF_UP)));
				builder.setSchemaPriceStandard(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(productPricing.getPriceStd()).orElse(Env.ZERO).multiply(conversionRate).setScale(productPricing.getPrecision(), BigDecimal.ROUND_HALF_UP)));
				builder.setSchemaPriceLimit(ValueUtil.getDecimalFromBigDecimal(Optional.ofNullable(productPricing.getPriceLimit()).orElse(Env.ZERO).multiply(conversionRate).setScale(productPricing.getPrecision(), BigDecimal.ROUND_HALF_UP)));
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
	 * Get Conversion Type from Sales Rep and POS
	 * @return
	 */
	private int getConversionTypeForPrice() {
		return new Query(Env.getCtx(), I_C_ConversionType.Table_Name, "EXISTS(SELECT 1 FROM C_POS p "
				+ "WHERE p.C_ConversionType_ID = C_ConversionType.C_ConversionType_ID "
				+ "AND p.AD_Org_ID IN(0, ?) "
				+ "AND p.SalesRep_ID = ?)", null)
				.setParameters(Env.getAD_Org_ID(Env.getCtx()), Env.getAD_User_ID(Env.getCtx()))
				.setOnlyActiveRecords(true)
				.firstId();
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
			priceList = getPriceList(null);
		} else {
			int priceListId = RecordUtil.getIdFromUuid(I_M_PriceList.Table_Name, request.getPriceListUuid(), null);
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
				RecordUtil.getIdFromUuid(I_C_BPartner.Table_Name, request.getBusinessPartnerUuid(), null), 
				priceList, 
				RecordUtil.getIdFromUuid(I_M_Warehouse.Table_Name, request.getWarehouseUuid(), null), 
				validFrom,
				Env.ONE);
	}
}

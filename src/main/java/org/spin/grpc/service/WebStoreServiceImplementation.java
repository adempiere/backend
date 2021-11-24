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
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_C_City;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_Region;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Storage;
import org.compiere.model.I_W_Basket;
import org.compiere.model.I_W_Store;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MBank;
import org.compiere.model.MBankAccount;
import org.compiere.model.MCharge;
import org.compiere.model.MCity;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MCountry;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MFreightCategory;
import org.compiere.model.MInvoice;
import org.compiere.model.MLocation;
import org.compiere.model.MMailText;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.MPackage;
import org.compiere.model.MPayment;
import org.compiere.model.MPriceList;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPricing;
import org.compiere.model.MRefList;
import org.compiere.model.MRegion;
import org.compiere.model.MShipper;
import org.compiere.model.MStorage;
import org.compiere.model.MStore;
import org.compiere.model.MTax;
import org.compiere.model.MUOM;
import org.compiere.model.MUser;
import org.compiere.model.MUserMail;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.model.X_C_Bank;
import org.compiere.model.X_C_Payment;
import org.compiere.model.X_W_Basket;
import org.compiere.model.X_W_BasketLine;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.EMail;
import org.compiere.util.Env;
import org.compiere.util.Login;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.eevolution.engine.freight.FreightEngine;
import org.eevolution.engine.freight.FreightEngineFactory;
import org.eevolution.engine.freight.FreightInfo;
import org.spin.base.util.ContextManager;
import org.spin.base.util.DocumentUtil;
import org.spin.base.util.RecordUtil;
import org.spin.base.util.ValueUtil;
import org.spin.grpc.store.Address;
import org.spin.grpc.store.AddressRequest;
import org.spin.grpc.store.Attribute;
import org.spin.grpc.store.Cart;
import org.spin.grpc.store.CartItem;
import org.spin.grpc.store.CartTotals;
import org.spin.grpc.store.ChangePasswordRequest;
import org.spin.grpc.store.ChangePasswordResponse;
import org.spin.grpc.store.City;
import org.spin.grpc.store.CreateCartRequest;
import org.spin.grpc.store.CreateCustomerRequest;
import org.spin.grpc.store.CreateOrderRequest;
import org.spin.grpc.store.Customer;
import org.spin.grpc.store.DeleteCartItemRequest;
import org.spin.grpc.store.Empty;
import org.spin.grpc.store.FormattedPrice;
import org.spin.grpc.store.GetCartRequest;
import org.spin.grpc.store.GetCartTotalsRequest;
import org.spin.grpc.store.GetCustomerRequest;
import org.spin.grpc.store.GetResourceRequest;
import org.spin.grpc.store.GetShippingInformationRequest;
import org.spin.grpc.store.GetStockRequest;
import org.spin.grpc.store.ListOrdersRequest;
import org.spin.grpc.store.ListOrdersResponse;
import org.spin.grpc.store.ListPaymentMethodsRequest;
import org.spin.grpc.store.ListPaymentMethodsResponse;
import org.spin.grpc.store.ListProductsRequest;
import org.spin.grpc.store.ListProductsResponse;
import org.spin.grpc.store.ListRenderProductsRequest;
import org.spin.grpc.store.ListRenderProductsResponse;
import org.spin.grpc.store.ListShippingMethodsRequest;
import org.spin.grpc.store.ListShippingMethodsResponse;
import org.spin.grpc.store.ListStocksRequest;
import org.spin.grpc.store.ListStocksResponse;
import org.spin.grpc.store.Order;
import org.spin.grpc.store.OrderLine;
import org.spin.grpc.store.PaymentMethod;
import org.spin.grpc.store.PaymentRequest;
import org.spin.grpc.store.PriceInfo;
import org.spin.grpc.store.Product;
import org.spin.grpc.store.ProductOrderLine;
import org.spin.grpc.store.Region;
import org.spin.grpc.store.RenderProduct;
import org.spin.grpc.store.ResetPasswordRequest;
import org.spin.grpc.store.ResetPasswordResponse;
import org.spin.grpc.store.ResetPasswordResponse.ResponseType;
import org.spin.grpc.store.Resource;
import org.spin.grpc.store.ShippingInformation;
import org.spin.grpc.store.ShippingMethod;
import org.spin.grpc.store.Stock;
import org.spin.grpc.store.TaxAdjustment;
import org.spin.grpc.store.TotalSegment;
import org.spin.grpc.store.UpdateCartRequest;
import org.spin.grpc.store.UpdateCustomerRequest;
import org.spin.grpc.store.WebStoreGrpc.WebStoreImplBase;
import org.spin.model.I_AD_AttachmentReference;
import org.spin.model.I_C_PaymentMethod;
import org.spin.model.I_W_DeliveryViaRuleAllocation;
import org.spin.model.MADAttachmentReference;
import org.spin.model.MADToken;
import org.spin.model.MADTokenDefinition;
import org.spin.model.MCPaymentMethod;
import org.spin.model.MWDeliveryViaRuleAllocation;
import org.spin.util.AttachmentUtil;
import org.spin.util.TokenGeneratorHandler;
import org.spin.util.VueStoreFrontUtil;

import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Service for backend of Web Store
 */
public class WebStoreServiceImplementation extends WebStoreImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(WebStoreServiceImplementation.class);
	/**	Date format	*/
	private final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	/**	Date Converter	*/
	private SimpleDateFormat dateConverter = new SimpleDateFormat(DATE_FORMAT);
	/**	Product Cache	*/
	private static CCache<String, MProduct> productCache = new CCache<String, MProduct>(I_M_Product.Table_Name, 30, 0);	//	no time-out
	
	@Override
	public void createCustomer(CreateCustomerRequest request, StreamObserver<Customer> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getEmail());
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
	public void resetPassword(ResetPasswordRequest request, StreamObserver<ResetPasswordResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getEmail());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ResetPasswordResponse.Builder resetPasswordResponse = resetPassword(request);
			responseObserver.onNext(resetPasswordResponse.build());
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
	public void changePassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ChangePasswordResponse.Builder changePasswordResponse = changePassword(request);
			responseObserver.onNext(changePasswordResponse.build());
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
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Customer.Builder customer = getCustomerInfo(request);
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
	public void getStock(GetStockRequest request, StreamObserver<Stock> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Stock.Builder stock = getStockFromSku(request);
			responseObserver.onNext(stock.build());
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
	public void listStocks(ListStocksRequest request, StreamObserver<ListStocksResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListStocksResponse.Builder stocks = listStocks(request);
			responseObserver.onNext(stocks.build());
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
	public void listProducts(ListProductsRequest request, StreamObserver<ListProductsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListProductsResponse.Builder products = listProducts(request);
			responseObserver.onNext(products.build());
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
	public void listRenderProducts(ListRenderProductsRequest request, StreamObserver<ListRenderProductsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListRenderProductsResponse.Builder products = listRenderProducts(request);
			responseObserver.onNext(products.build());
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
	public void createCart(CreateCartRequest request, StreamObserver<Cart> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			Trx.run(transactionName -> {
				Cart.Builder cart = convertCart(createCart(request.getIsGuest(), transactionName), transactionName);
				responseObserver.onNext(cart.build());
				responseObserver.onCompleted();
			});
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
	public void getCart(GetCartRequest request, StreamObserver<Cart> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Trx.run(transactionName -> {
				Cart.Builder cart = convertCart(getCart(request.getCartId(), request.getCartUuid(), request.getIsGuest(), 0, transactionName), transactionName);
				responseObserver.onNext(cart.build());
				responseObserver.onCompleted();
			});
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
	public void updateCart(UpdateCartRequest request, StreamObserver<CartItem> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			CartItem.Builder cartItem = convertCartItem(updateCart(request));
			responseObserver.onNext(cartItem.build());
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
	public void listPaymentMethods(ListPaymentMethodsRequest request, StreamObserver<ListPaymentMethodsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListPaymentMethodsResponse.Builder products = listPaymentMethods(request);
			responseObserver.onNext(products.build());
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
	public void listShippingMethods(ListShippingMethodsRequest request, StreamObserver<ListShippingMethodsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListShippingMethodsResponse.Builder freughtRules = listShippingMethods(request);
			responseObserver.onNext(freughtRules.build());
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
	public void getShippingInformation(GetShippingInformationRequest request, StreamObserver<ShippingInformation> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ShippingInformation.Builder shippingInformation = getShippingInformation(request);
			responseObserver.onNext(shippingInformation.build());
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
	public void getCartTotals(GetCartTotalsRequest request, StreamObserver<CartTotals> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			CartTotals.Builder cartInformation = getCartTotals(request);
			responseObserver.onNext(cartInformation.build());
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
	public void deleteCartItem(DeleteCartItemRequest request, StreamObserver<Empty> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			Empty.Builder deleteConfirmation = deleteCartItem(request);
			responseObserver.onNext(deleteConfirmation.build());
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
			log.fine("Object Requested = " + request.getEmail());
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
	
	@Override
	public void createOrder(CreateOrderRequest request, StreamObserver<Order> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			Trx.run(transactionName -> {
				Order.Builder orderBuilder = createOrder(request, transactionName);
				responseObserver.onNext(orderBuilder.build());
				responseObserver.onCompleted();
			});
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
			log.fine("Object Requested = " + request.getClientRequest().getSessionUuid());
			ContextManager.getContext(request.getClientRequest().getSessionUuid(), 
					request.getClientRequest().getLanguage(), 
					request.getClientRequest().getOrganizationUuid(), 
					request.getClientRequest().getWarehouseUuid());
			ListOrdersResponse.Builder orders = listOrders(request);
			responseObserver.onNext(orders.build());
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
	 * Create Order from Request
	 * @param request
	 * @param transactionName
	 * @return
	 */
	private Order.Builder createOrder(CreateOrderRequest request, String transactionName) {
		if(request.getCartId() == 0
				&& Util.isEmpty(request.getCartUuid())) {
			throw new AdempiereException("@W_Basket_ID@ @IsMandatory@");
		}
		MStore store = VueStoreFrontUtil.getDefaultStore(Env.getAD_Org_ID(Env.getCtx()));
		if(store == null) {
			throw new AdempiereException("@W_Store_ID@ @NotFound@");
		}
		//	Validate Shipping Address
		if(Util.isEmpty(request.getShippingAddress().getCountryCode())
				&& Util.isEmpty(request.getShippingAddress().getRegionName())
				&& Util.isEmpty(request.getShippingAddress().getCityName())) {
			throw new AdempiereException("@IsShipTo@ @IsMandatory@");
		}
		//	Validate Bulling Address
		if(Util.isEmpty(request.getBillingAddress().getCountryCode())
				&& Util.isEmpty(request.getBillingAddress().getRegionName())
				&& Util.isEmpty(request.getBillingAddress().getCityName())) {
			throw new AdempiereException("@IsBillTo@ @IsMandatory@");
		}
		//	Validate Payment Method Code
		if(Util.isEmpty(request.getPaymentMethodCode())) {
			throw new AdempiereException("@PaymentRule@ @IsMandatory@");
		}
		//	Validate Business Partner
		X_W_Basket basket = getCart(request.getCartId(), request.getCartUuid(), !Util.isEmpty(request.getCartUuid()), request.getUserId(), transactionName);
		if(basket == null) {
			throw new AdempiereException("@W_Basket_ID@ @IsMandatory@");
		}
		//	
		MBPartner businessPartner = null;
		if(basket.getC_BPartner_ID() <= 0) {
			businessPartner = VueStoreFrontUtil.getTemplate(Env.getCtx(), store.get_ValueAsInt(VueStoreFrontUtil.COLUMNNAME_C_TemplateBPartner_ID), transactionName);
			businessPartner.setName(request.getBillingAddress().getFirstName());
			businessPartner.setName2(request.getBillingAddress().getLastName());
			businessPartner.saveEx(transactionName);
		} else {
			businessPartner = ((MBPartner) basket.getC_BPartner());
		}
		businessPartner.set_TrxName(transactionName);
		int shippingLocationId = findAddress(businessPartner, request.getShippingAddress(), false, transactionName);
		int billingLocationId = findAddress(businessPartner, request.getBillingAddress(), true, transactionName);
		//	
		MOrder salesOrder = new MOrder(Env.getCtx(), 0, transactionName);
		salesOrder.setDateOrdered(getDate());
		salesOrder.setDateAcct(getDate());
		salesOrder.setDatePromised(getDate());
		//	Default values
		salesOrder.setIsSOTrx(true);
		salesOrder.setAD_Org_ID(store.getAD_Org_ID());
		if(store.getSalesRep_ID() > 0) {
			salesOrder.setSalesRep_ID(store.getSalesRep_ID());
		}
		//	Set default from business partner
		salesOrder.setBPartner(businessPartner);
		//	Set Billing Address
		salesOrder.setBill_Location_ID(billingLocationId);
		//	Set Shipping Address
		salesOrder.setC_BPartner_Location_ID(shippingLocationId);
		salesOrder.set_ValueOfColumn(I_W_Store.COLUMNNAME_W_Store_ID, store.getW_Store_ID());
		salesOrder.set_ValueOfColumn(I_W_Basket.COLUMNNAME_W_Basket_ID, basket.getW_Basket_ID());
		//	Warehouse
		salesOrder.setM_Warehouse_ID(store.getM_Warehouse_ID());
		//	Price List
		salesOrder.setM_PriceList_ID(store.getM_PriceList_ID());
		//	Document Type
		int documeDocTypeId = store.get_ValueAsInt("C_DocType_ID");
		if(documeDocTypeId <= 0) {
			documeDocTypeId = MDocType.getDocTypeBaseOnSubType(store.getAD_Org_ID(), MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_InvoiceOrder);
		}
		//	Validate
		if(documeDocTypeId <= 0) {
			throw new AdempiereException("@C_DocType_ID@ @IsMandatory@");
		}
		//	Validate
		salesOrder.setC_DocTypeTarget_ID(documeDocTypeId);
		//	Set Freight Rules
		MWDeliveryViaRuleAllocation deliveryViaRuleAllocation = MWDeliveryViaRuleAllocation.getFromUuid(Env.getCtx(), request.getMethodCode(), transactionName);
		if(!Util.isEmpty(deliveryViaRuleAllocation.getDeliveryViaRule())) {
			salesOrder.setDeliveryViaRule(deliveryViaRuleAllocation.getDeliveryViaRule());
		}
		if(deliveryViaRuleAllocation.getM_Shipper_ID() > 0) {
			salesOrder.setM_Shipper_ID(deliveryViaRuleAllocation.getM_Shipper_ID());
		}
		if(!Util.isEmpty(deliveryViaRuleAllocation.getFreightCostRule())) {
			salesOrder.setFreightCostRule(deliveryViaRuleAllocation.getFreightCostRule());
		}
		if(deliveryViaRuleAllocation.getM_FreightCategory_ID() > 0) {
			salesOrder.setM_FreightCategory_ID(deliveryViaRuleAllocation.getM_FreightCategory_ID());
		}
		if(!Util.isEmpty(deliveryViaRuleAllocation.getNote())) {
			salesOrder.addDescription(deliveryViaRuleAllocation.getNote());
		}
		if(Util.isEmpty(request.getPaymentMethodCode())) {
			throw new AdempiereException("@TenderType@ @IsMandatory@");
		}
		//	
		MCPaymentMethod paymentMethod = MCPaymentMethod.getByValue(Env.getCtx(), request.getPaymentMethodCode(), transactionName);
		salesOrder.setDocStatus(MOrder.DOCSTATUS_Drafted);
		salesOrder.setDocAction(MOrder.ACTION_Complete);
		salesOrder.set_ValueOfColumn("W_Store_ID", store.getW_Store_ID());
		salesOrder.saveEx();
		//	Add Lines
		request.getProductsList().forEach(product -> addLinesToOrder(salesOrder, product, transactionName));
		//	Process it
		if(!salesOrder.processIt(MOrder.ACTION_Complete)) {
			throw new AdempiereException("@Error@ " + salesOrder.getProcessMsg());
		}
		if(request.getUserId() > 0) {
			salesOrder.setAD_User_ID(request.getUserId());
		}
		//	
		salesOrder.saveEx();
		//	Update Basket
		basket.setIsActive(false);
		basket.saveEx(transactionName);
		//	Process Payments
		processPayments(paymentMethod, salesOrder, request.getPaymentsList(), transactionName);
		return convertOrder(request, salesOrder, transactionName);
	}
	
	/**
	 * Process Payments
	 * @param defaultPaymentMethod
	 * @param salesOrder
	 * @param transactionName
	 */
	private void processPayments(MCPaymentMethod defaultPaymentMethod, MOrder salesOrder, List<PaymentRequest> paymentList, String transactionName) {
		AtomicReference<BigDecimal> paymentAmount = new AtomicReference<BigDecimal>(Env.ZERO);
		//	Additional Payments
		paymentList.forEach(paymentRequest -> {
			MCPaymentMethod paymentMethod = MCPaymentMethod.getByValue(Env.getCtx(), paymentRequest.getPaymentMethodCode(), transactionName);
			int currencyId = salesOrder.getC_Currency_ID();
			if(!Util.isEmpty(paymentRequest.getCurrencyCode())) {
				MCurrency currency = MCurrency.get(Env.getCtx(), paymentRequest.getCurrencyCode());
				if(currency != null) {
					currencyId = currency.getC_Currency_ID();
				}
			}
			//	Process Payment
			processPayment(salesOrder, paymentMethod.getTenderType(), currencyId, new BigDecimal(paymentRequest.getAmount()), paymentRequest, transactionName);
			//	Cummulate
			paymentAmount.updateAndGet(amount -> amount.add(new BigDecimal(paymentRequest.getAmount())));
		});
		//	All Payments
		if(paymentAmount.get().compareTo(Env.ZERO) >= 0
				&& salesOrder.getGrandTotal().subtract(paymentAmount.get()).compareTo(Env.ZERO) > 0
				&& defaultPaymentMethod != null) {
			//	e-Commerce cash waiting for collect is generated as draft documents
			processPayment(salesOrder, defaultPaymentMethod.getTenderType(), salesOrder.getC_Currency_ID(), salesOrder.getGrandTotal().subtract(paymentAmount.get()), null, transactionName);
		}
	}
	
	/**
	 * Process Payment
	 * @param salesOrder
	 * @param tenderType
	 * @param currencyId
	 * @param paymentAmount
	 * @param paymentRequest
	 * @param transactionName
	 */
	private void processPayment(MOrder salesOrder, String tenderType, int currencyId, BigDecimal paymentAmount, PaymentRequest paymentRequest, String transactionName) {
		if(paymentRequest == null) {
			paymentRequest = PaymentRequest.newBuilder().build();
		}
		if(Util.isEmpty(tenderType)) {
			tenderType = MPayment.TENDERTYPE_Cash;
		}
		MPayment payment = createPayment(salesOrder, tenderType, currencyId, paymentAmount, transactionName);
		switch (tenderType) {
			case MPayment.TENDERTYPE_Check:
				//	TODO: Add references
//				payment.setAccountNo(accountNo);
//				payment.setRoutingNo(routingNo);
				payment.setCheckNo(paymentRequest.getReferenceNo());
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
				payment.setDescription(paymentRequest.getDescription());
				break;
		}
		//	Set Bank Id
		if(paymentRequest.getBankId() > 0) {
			payment.set_ValueOfColumn(MBank.COLUMNNAME_C_Bank_ID, paymentRequest.getBankId());
		}
		//	Validate reference
		if(!Util.isEmpty(paymentRequest.getReferenceNo())) {
			payment.addDescription(paymentRequest.getReferenceNo());
		}
		//	Set Description
		if(!Util.isEmpty(paymentRequest.getDescription())) {
			payment.addDescription(paymentRequest.getDescription());
		}
		payment.saveEx(transactionName);
		//	Process Payment
		if(payment != null) {
			if (!payment.processIt(X_C_Payment.DOCACTION_Prepare)) {
				throw new AdempiereException("@Error@: " + payment.getProcessMsg());
			}
			payment.saveEx(transactionName);
		}
	}
	
	/**
	 * Get Bank Account for Cash as Payment
	 * @param salesOrder
	 * @return
	 */
	private int getCashBankAccount(MOrder salesOrder) {
		MBankAccount bankAccount = MBankAccount.getDefault(Env.getCtx(), salesOrder.getAD_Org_ID(), X_C_Bank.BANKTYPE_CashJournal);
		if (bankAccount != null) {
			return bankAccount.getC_BankAccount_ID();
		}
		return -1;
	}
	
	/**
	 * 	Create Payment object
	 *  Refer to invoice if there is an invoice.
	 *  Otherwise refer to order (it is a prepayment)
	 * 
	 * @return Payment object
	 * 
	 */
	private MPayment createPayment(MOrder salesOrder, String tenderType, int currencyId, BigDecimal paymentAmount, String transactionName) {
		int cashAccountId = getCashBankAccount(salesOrder);
		if(cashAccountId <= 0) {
			throw new AdempiereException("@NoCashBook@");
		}
		//	
		MPayment payment = new MPayment(Env.getCtx(), 0, transactionName);
		payment.setC_BankAccount_ID(cashAccountId);
		payment.setC_DocType_ID(true);
		payment.setAD_Org_ID(salesOrder.getAD_Org_ID());
        String value = DB.getDocumentNo(payment.getC_DocType_ID(), transactionName, false,  payment);
        payment.setDocumentNo(value);
        payment.setDateAcct(salesOrder.getDateAcct());
        payment.setDateTrx(salesOrder.getDateOrdered());
        payment.setTenderType(MPayment.TENDERTYPE_Cash);
        payment.setDescription(salesOrder.getDescription());
        payment.setC_BPartner_ID (salesOrder.getC_BPartner_ID());
        payment.setC_Currency_ID(salesOrder.getC_Currency_ID());
        payment.setPayAmt(paymentAmount);
        payment.setOverUnderAmt(Env.ZERO);
        //	Order Reference
        payment.setC_Order_ID(salesOrder.getC_Order_ID());
        payment.setDocStatus(MPayment.DOCSTATUS_Drafted);
		payment.saveEx();
		int invoiceId = salesOrder.getC_Invoice_ID();
		if(invoiceId > 0) {
			payment.setC_Invoice_ID(invoiceId);
			MInvoice invoice = new MInvoice(Env.getCtx(), payment.getC_Invoice_ID(), transactionName);
			payment.setDescription(Msg.getMsg(Env.getCtx(), "Invoice No ") + invoice.getDocumentNo());
		} else {
			payment.setDescription(Msg.getMsg(Env.getCtx(), "Order No ") + salesOrder.getDocumentNo());
		}
		payment.saveEx(transactionName);
		return payment;
	}
	
	/**
	 * Convert Order
	 * @param request
	 * @param salesOrder
	 * @param transactionName
	 * @return
	 */
	private Order.Builder convertOrder(CreateOrderRequest request, MOrder salesOrder, String transactionName) {
		Order.Builder builder = Order.newBuilder();
		builder.setId(salesOrder.getC_Order_ID())
			.setDocumentNo(ValueUtil.validateNull(salesOrder.getDocumentNo()))
			.setCreated(dateConverter.format(salesOrder.getCreated()))
			.setUpdated(dateConverter.format(salesOrder.getUpdated()))
			.setTransmited(dateConverter.format(salesOrder.getUpdated()))
			.setCarrierCode(request.getCarrierCode())
			.setMethodCode(request.getMethodCode())
			.setPaymentMethodCode(request.getPaymentMethodCode())
			.setShippingAddress(convertAddress(MUser.get(Env.getCtx(), salesOrder.getAD_User_ID()), ((MBPartnerLocation) salesOrder.getC_BPartner_Location()), transactionName))
			.setShippingAddress(convertAddress(MUser.get(Env.getCtx(), salesOrder.getAD_User_ID()), ((MBPartnerLocation) salesOrder.getBill_Location()), transactionName));
		//	Add Lines
		Arrays.asList(salesOrder.getLines(true, null)).forEach(orderLine -> {
			MProduct product = MProduct.get(Env.getCtx(), orderLine.getM_Product_ID());
			builder.addOrderLines(OrderLine.newBuilder()
					.setSku(ValueUtil.validateNull(product.getSKU()))
					.setName(ValueUtil.validateNull(product.getName()))
					.setPrice(orderLine.getPriceActual().doubleValue())
					.setQuantity(orderLine.getQtyOrdered().doubleValue()));
		});
		return builder;
	}
	
	/**
	 * Convert Order
	 * @param request
	 * @param salesOrder
	 * @param transactionName
	 * @return
	 */
	private Order.Builder convertOrder(MOrder salesOrder, String transactionName) {
		Order.Builder builder = Order.newBuilder();
		builder.setId(salesOrder.getC_Order_ID())
			.setDocumentNo(ValueUtil.validateNull(salesOrder.getDocumentNo()))
			.setCreated(dateConverter.format(salesOrder.getCreated()))
			.setUpdated(dateConverter.format(salesOrder.getUpdated()))
			.setTransmited(dateConverter.format(salesOrder.getUpdated()))
			.setShippingAddress(convertAddress(MUser.get(Env.getCtx(), salesOrder.getAD_User_ID()), ((MBPartnerLocation) salesOrder.getC_BPartner_Location()), transactionName))
			.setShippingAddress(convertAddress(MUser.get(Env.getCtx(), salesOrder.getAD_User_ID()), ((MBPartnerLocation) salesOrder.getBill_Location()), transactionName));
		//	Add Lines
		Arrays.asList(salesOrder.getLines(true, null)).forEach(orderLine -> {
			MProduct product = MProduct.get(Env.getCtx(), orderLine.getM_Product_ID());
			builder.addOrderLines(OrderLine.newBuilder()
					.setSku(ValueUtil.validateNull(product.getSKU()))
					.setName(ValueUtil.validateNull(product.getName()))
					.setPrice(orderLine.getPriceActual().doubleValue())
					.setQuantity(orderLine.getQtyOrdered().doubleValue()));
		});
		return builder;
	}
	
	/**
	 * Add Line to order
	 * @param salesOrder
	 * @param product
	 * @param transactionName
	 */
	private void addLinesToOrder(MOrder salesOrder, ProductOrderLine product, String transactionName) {
		//	Valid Complete
		if (!DocumentUtil.isDrafted(salesOrder))
			throw new AdempiereException("@C_Order_ID@ @IsDrafted@");
		BigDecimal quantityToOrder = new BigDecimal(product.getQuantity());
		//create new line
		MOrderLine orderLine = new MOrderLine(salesOrder);
		orderLine.setProduct(MProduct.get(salesOrder.getCtx(), product.getId()));
		orderLine.setQty(quantityToOrder);
		orderLine.setPrice();
		//	Save Line
		orderLine.saveEx(transactionName);
	}
	
	/**
	 * Find a location and create if not exist
	 * @param customer
	 * @param address
	 * @param isBillto
	 * @param transactionName
	 * @return
	 */
	private int findAddress(MBPartner customer, AddressRequest address, boolean isBillto, String transactionName) {
		List<MBPartnerLocation> businessPartnerLocations = Arrays.asList(customer.getLocations(true));
		int businessPartnerLocationId = 0;
		if(address.getLocationId() > 0) {
			Optional<MBPartnerLocation> maybeLocation = businessPartnerLocations.stream().filter(bPLocation -> bPLocation.getC_Location_ID() == address.getLocationId()).findFirst();
			if(maybeLocation.isPresent()) {
				businessPartnerLocationId = maybeLocation.get().getC_BPartner_Location_ID();
			}
		} else {	//	Find Match
			Optional<MBPartnerLocation> maybeLocation = businessPartnerLocations
					.stream()
					.filter(bPLocation -> {
						MLocation location = MLocation.get(Env.getCtx(), bPLocation.getC_Location_ID(), transactionName);
						MCountry country = MCountry.get(Env.getCtx(), location.getC_Country_ID());
						if((bPLocation.isBillTo() == isBillto
								|| bPLocation.isShipTo() == !isBillto)
								&& Optional.ofNullable(country.getCountryCode()).orElse("").toUpperCase().trim().equals(Optional.ofNullable(address.getCountryCode()).orElse("").toUpperCase().trim())
								&& (Optional.ofNullable(location.getRegionName()).orElse("").toUpperCase().trim().equals(Optional.ofNullable(address.getRegionName()).orElse("").toUpperCase().trim())
										|| location.getC_Region_ID() == address.getRegionId())
								&& Optional.ofNullable(location.getCity()).orElse("").equals(Optional.ofNullable(address.getCityName()).orElse(""))
								&& Optional.ofNullable(location.getAddress1()).orElse("").toUpperCase().trim().equals(Optional.ofNullable(address.getAddress1()).orElse("").toUpperCase().trim())
								&& Optional.ofNullable(location.getAddress2()).orElse("").toUpperCase().trim().equals(Optional.ofNullable(address.getAddress2()).orElse("").toUpperCase().trim())
								&& Optional.ofNullable(location.getAddress3()).orElse("").toUpperCase().trim().equals(Optional.ofNullable(address.getAddress3()).orElse("").toUpperCase().trim())
								&& Optional.ofNullable(location.getAddress4()).orElse("").toUpperCase().trim().equals(Optional.ofNullable(address.getAddress4()).orElse("").toUpperCase().trim())
								&& Optional.ofNullable(location.getPostal()).orElse("").equals(Optional.ofNullable(address.getPostalCode()).orElse(""))) {
							return true;
						}
						//	Default
						return false;
					}).findFirst();
	        if (maybeLocation.isPresent()) {
	        	businessPartnerLocationId = maybeLocation.get().getC_BPartner_Location_ID();
	        } else {	//	Create new
	        	MBPartnerLocation businessPartnerLocation = createBusinessPartnerLocation(customer, address, isBillto, transactionName);
	        	businessPartnerLocationId = businessPartnerLocation.getC_BPartner_Location_ID();
	        }
		}
		//	Default location
		return businessPartnerLocationId;
	}
	
	/**
	 * Create Business Partner Location
	 * @param customer
	 * @param address
	 * @param transactionName
	 * @return
	 */
	private MBPartnerLocation createBusinessPartnerLocation(MBPartner customer, AddressRequest address, boolean isBillTo, String transactionName) {
    	//	Location
		int countryId = 0;
		if(!Util.isEmpty(address.getCountryCode())) {
			MCountry country = MCountry.get(Env.getCtx(), address.getCountryCode());
			if(Optional.ofNullable(country).isPresent()) {
				countryId = country.getC_Country_ID();
			}
		}
		if(countryId <= 0) {
			countryId = Env.getContextAsInt(Env.getCtx(), "#C_Country_ID");
		}
		//	
		int regionId = address.getRegionId();
		if(regionId <= 0 && !Util.isEmpty(address.getRegionName())) {
			Optional<MRegion> maybeRegion = Arrays.asList(MRegion.getDefault(Env.getCtx())).stream().filter(region -> region.getName().equals(address.getRegionName())).findFirst();
			if(maybeRegion.isPresent()) {
				regionId = maybeRegion.get().getC_Region_ID();
			}
		}
		String cityName = null;
		int cityId = 0;
		//	City Name
		if(!Util.isEmpty(address.getCityName())) {
			cityName = address.getCityName();
			MCity city = new Query(Env.getCtx(), I_C_City.Table_Name, "UPPER(" + I_C_City.COLUMNNAME_Name + ") = ?", transactionName).setParameters(address.getCityName().toUpperCase()).first();
			if(city != null
					&& city.getC_City_ID() > 0) {
				cityId = city.getC_City_ID();
				if(regionId <= 0
						&& city.getC_Region_ID() > 0) {
					regionId = city.getC_Region_ID();
				}
			}
		}
		if(regionId > 0) {
			MRegion region = MRegion.get(Env.getCtx(), regionId);
			countryId = region.getC_Country_ID();
		}
		//	Instance it
		MLocation location = new MLocation(Env.getCtx(), countryId, regionId, cityName, transactionName);
		if(cityId > 0) {
			location.setC_City_ID(cityId);
		}
		//	Postal Code
		if(!Util.isEmpty(address.getPostalCode())) {
			location.setPostal(address.getPostalCode());
		}
		//	Set Address
		location.setAddress1(address.getAddress1());
		location.setAddress2(address.getAddress2());
		location.setAddress3(address.getAddress3());
		location.setAddress4(address.getAddress4());
		location.saveEx(transactionName);
		//	Create BP location
		MBPartnerLocation businessPartnerLocation = new MBPartnerLocation(customer);
		businessPartnerLocation.setC_Location_ID(location.getC_Location_ID());
		//	Phone
		if(!Util.isEmpty(address.getPhone())) {
			businessPartnerLocation.setPhone(address.getPhone());
		}
		//	Set Bill
		businessPartnerLocation.setIsShipTo(true);
		businessPartnerLocation.setIsBillTo(isBillTo);
		businessPartnerLocation.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_IsDefaultBilling, address.getIsDefaultBilling());
		businessPartnerLocation.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_IsDefaultShipping, address.getIsDefaultShipping());
		//	Save
		businessPartnerLocation.saveEx(transactionName);
		return businessPartnerLocation;
	}
	
	/**
	 * Get Date
	 * @return
	 */
	private Timestamp getDate() {
		return TimeUtil.getDay(System.currentTimeMillis());
	}
	
	/**
	 * Get File from fileName
	 * @param resourceUuid
	 * @param responseObserver
	 * @throws Exception 
	 */
	private void getResource(String resourceUuid, String resourceName, StreamObserver<Resource> responseObserver) throws Exception {
		if(!AttachmentUtil.getInstance().isValidForClient(Env.getAD_Client_ID(Env.getCtx()))) {
			responseObserver.onCompleted();
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
				responseObserver.onCompleted();
				return;
			}
			resourceUuid = reference.getUUID();
		} else if(Util.isEmpty(resourceUuid)) {
			responseObserver.onCompleted();
			return;
		}
		byte[] data = AttachmentUtil.getInstance()
			.withClientId(Env.getAD_Client_ID(Env.getCtx()))
			.withAttachmentReferenceId(RecordUtil.getIdFromUuid(I_AD_AttachmentReference.Table_Name, resourceUuid, null))
			.getAttachment();
		if(data == null) {
			responseObserver.onCompleted();
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
	
	/**
	 * Update Cart from user
	 * @param request
	 * @return
	 */
	private Empty.Builder deleteCartItem(DeleteCartItemRequest request) {
		Empty.Builder builder = Empty.newBuilder();
		Trx.run(transactionName -> {
			MStore store = VueStoreFrontUtil.getDefaultStore(Env.getAD_Org_ID(Env.getCtx()));
			if(store == null) {
				throw new AdempiereException("@W_Store_ID@ @NotFound@");
			}
			MProduct product = getProductFromSku(request.getSku());
			if(product == null
					|| product.getM_Product_ID() == 0) {
				throw new AdempiereException("@M_Product_ID@ @NotFound@");
			}
			X_W_Basket basket = getCart(request.getCartId(), request.getCartUuid(), !Util.isEmpty(request.getCartUuid()), 0, transactionName);
			//	Get Lines
			List<X_W_BasketLine> items = new Query(Env.getCtx(), X_W_BasketLine.Table_Name, X_W_BasketLine.COLUMNNAME_W_Basket_ID + " = ? "
					+ "AND (M_Product_ID = ? OR EXISTS(SELECT 1 FROM M_Product p WHERE p.M_Product_ID = W_BasketLine.M_Product_ID AND p.SKU = ?))", transactionName)
					.setParameters(basket.getW_Basket_ID(), request.getProductId(), request.getSku())
					.list();
			if(items != null
					&& items.size() > 0) {
				items.forEach(item -> item.deleteEx(true));
			}
		});
		//	Get Line
		return builder;
	}
	
	/**
	 * Get Totals from cart
	 * @param request
	 * @return
	 */
	private CartTotals.Builder getCartTotals(GetCartTotalsRequest request) {
		CartTotals.Builder builder = CartTotals.newBuilder();
		Trx.run(transactionName -> {
			X_W_Basket basket = getCart(request.getCartId(), request.getCartUuid(), !Util.isEmpty(request.getCartUuid()), 0, transactionName);
			if(basket == null) {
				throw new AdempiereException("@W_Basket_ID@ @NotFound@");
			}
			Cart.Builder cart = convertCart(basket, transactionName);
			builder.setCart(cart);
			String subTotalLabel = Msg.getMsg(Env.getCtx(), "store.SubTotal");	//	TODO: Sub-Total
			String shippingLabel = Msg.getMsg(Env.getCtx(), "store.Shipping");	//	TODO: Shipping & Handling (Flat Rate - Fixed)
			String discountLabel = Msg.getMsg(Env.getCtx(), "store.Discount");	//	TODO: Discount
			String taxLabel = Msg.getMsg(Env.getCtx(), "store.Tax");	//	TODO: Taxes
			String grandTotalLabel = Msg.getMsg(Env.getCtx(), "store.GrandTotal");	//	TODO: Grand total
			//	Add segments
			builder.addTotalSegments(TotalSegment.newBuilder().setCode("subtotal").setName(subTotalLabel).setValue(cart.getSubtotal()));
			//	Shipping
			builder.addTotalSegments(TotalSegment.newBuilder().setCode("shipping").setName(shippingLabel).setValue(cart.getShippingAmount()));
			//	Discount
			builder.addTotalSegments(TotalSegment.newBuilder().setCode("discount").setName(discountLabel).setValue(cart.getDiscountAmount()));
			//	Tax
			TotalSegment.Builder taxSegment = TotalSegment.newBuilder().setCode("tax").setName(taxLabel).setArea("taxes").setValue(cart.getTaxAmount());
			//	TODO: Add tax Detail
			builder.addTotalSegments(taxSegment);
			//	Grand Total
			builder.addTotalSegments(TotalSegment.newBuilder().setCode("grand_total").setName(grandTotalLabel).setArea("footer").setValue(cart.getGrandTotal()));
		});
		return builder;
	}
	
	/**
	 * Get Shipping Information
	 * @param request
	 * @return
	 */
	private ShippingInformation.Builder getShippingInformation(GetShippingInformationRequest request) {
		ShippingInformation.Builder builder = ShippingInformation.newBuilder();
		Trx.run(transactionName -> {
			X_W_Basket basket = getCart(request.getCartId(), request.getCartUuid(), !Util.isEmpty(request.getCartUuid()), 0, transactionName);
			if(basket == null) {
				throw new AdempiereException("@W_Basket_ID@ @NotFound@");
			}
			MStore store = VueStoreFrontUtil.getDefaultStore(Env.getAD_Org_ID(Env.getCtx()));
			if(store == null) {
				throw new AdempiereException("@W_Store_ID@ @NotFound@");
			}
			MCPaymentMethod.getOfStore(Env.getCtx(), store.getW_Store_ID(), transactionName).forEach(paymentMethod -> builder.addPaymentMethods(
					PaymentMethod.newBuilder()
						.setCode(ValueUtil.validateNull(paymentMethod.getValue()))
						.setName(ValueUtil.validateNull(paymentMethod.getName())))
					);
			//	Reload Fleet calculation
			reloadShippingCalculation(basket.getW_Basket_ID(), request.getMethodCode(), transactionName);
			Cart.Builder cart = convertCart(basket, transactionName);
			builder.setCart(cart);
			String subTotalLabel = Msg.getMsg(Env.getCtx(), "store.SubTotal");	//	TODO: Sub-Total
			String shippingLabel = Msg.getMsg(Env.getCtx(), "store.Shipping");	//	TODO: Shipping & Handling (Flat Rate - Fixed)
			String discountLabel = Msg.getMsg(Env.getCtx(), "store.Discount");	//	TODO: Discount
			String taxLabel = Msg.getMsg(Env.getCtx(), "store.Tax");	//	TODO: Taxes
			String grandTotalLabel = Msg.getMsg(Env.getCtx(), "store.GrandTotal");	//	TODO: Grand total
			//	Add segments
			builder.addTotalSegments(TotalSegment.newBuilder().setCode("subtotal").setName(subTotalLabel).setValue(cart.getSubtotal()));
			//	Shipping
			builder.addTotalSegments(TotalSegment.newBuilder().setCode("shipping").setName(shippingLabel).setValue(cart.getShippingAmount()));
			//	Discount
			builder.addTotalSegments(TotalSegment.newBuilder().setCode("discount").setName(discountLabel).setValue(cart.getDiscountAmount()));
			//	Tax
			TotalSegment.Builder taxSegment = TotalSegment.newBuilder().setCode("tax").setName(taxLabel).setArea("taxes").setValue(cart.getTaxAmount());
			//	TODO: Add tax Detail
			builder.addTotalSegments(taxSegment);
			//	Grand Total
			builder.addTotalSegments(TotalSegment.newBuilder().setCode("grand_total").setName(grandTotalLabel).setArea("footer").setValue(cart.getGrandTotal()));
		});
		return builder;
	}
	
	/**
	 * Reload Shipping information
	 * @param basketId
	 * @param methodCode
	 * @param transactionName
	 */
	private void reloadShippingCalculation(int basketId, String methodCode, String transactionName) {
		if(!Util.isEmpty(methodCode)) {
			MWDeliveryViaRuleAllocation deliveryVia = MWDeliveryViaRuleAllocation.getFromUuid(Env.getCtx(), methodCode, transactionName);
			if(deliveryVia != null
					&& deliveryVia.getW_DeliveryViaRuleAllocation_ID() > 0) {
				List<X_W_BasketLine> items = new Query(Env.getCtx(), X_W_BasketLine.Table_Name, X_W_BasketLine.COLUMNNAME_W_Basket_ID + " = ?", transactionName)
						.setParameters(basketId)
						.list();
				if(items != null
						&& items.size() > 0) {
					items.forEach(basketLine -> {
						basketLine.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_DeliveryViaRule, deliveryVia.get_ValueAsString(VueStoreFrontUtil.COLUMNNAME_DeliveryViaRule));
						if(!Util.isEmpty(deliveryVia.get_ValueAsString(VueStoreFrontUtil.COLUMNNAME_FreightCostRule))) {
							basketLine.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_FreightCostRule, deliveryVia.get_ValueAsString(VueStoreFrontUtil.COLUMNNAME_FreightCostRule));
						}
						if(deliveryVia.get_ValueAsInt(VueStoreFrontUtil.COLUMNNAME_M_FreightCategory_ID) > 0) {
							basketLine.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_M_FreightCategory_ID, deliveryVia.get_ValueAsInt(VueStoreFrontUtil.COLUMNNAME_M_FreightCategory_ID));
						}
						if(deliveryVia.get_ValueAsInt(VueStoreFrontUtil.COLUMNNAME_M_Shipper_ID) > 0) {
							basketLine.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_M_Shipper_ID, deliveryVia.get_ValueAsInt(VueStoreFrontUtil.COLUMNNAME_M_Shipper_ID));
						}
						basketLine.saveEx(transactionName);
					});
					//	Create Package
					VueStoreFrontUtil.createPackagesFromBasket(Env.getCtx(), basketId, transactionName);
				}
			}
		}
	}
	
	/**
	 * List Orders from POS UUID
	 * @param request
	 * @return
	 */
	private ListOrdersResponse.Builder listOrders(ListOrdersRequest request) {
		ListOrdersResponse.Builder builder = ListOrdersResponse.newBuilder();
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		//	Get Orders list
		Query query = new Query(Env.getCtx(), I_C_Order.Table_Name, "EXISTS(SELECT 1 FROM W_Basket b WHERE b.W_Basket_ID = C_Order.W_Basket_ID AND b.AD_User_ID = ?)", null)
				.setParameters(Env.getAD_User_ID(Env.getCtx()))
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.setOrderBy(I_C_Order.COLUMNNAME_DateOrdered + " DESC");
		int count = query.count();
		query
		.setLimit(limit, offset)
		.<MOrder>list()
		.forEach(order -> {
			builder.addOrders(convertOrder(order, null));
		});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(RecordUtil.isValidNextPageToken(count, offset, limit)) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * List shipping methods and freight rules
	 * @param request
	 * @return
	 */
	private ListShippingMethodsResponse.Builder listShippingMethods(ListShippingMethodsRequest request) {
		ListShippingMethodsResponse.Builder builder = ListShippingMethodsResponse.newBuilder();
		if(request.getCartId() == 0
				&& Util.isEmpty(request.getCartUuid())) {
			throw new AdempiereException("@W_Basket_ID@ @IsMandatory@");
		}
		MStore store = VueStoreFrontUtil.getDefaultStore(Env.getAD_Org_ID(Env.getCtx()));
		if(store == null) {
			throw new AdempiereException("@W_Store_ID@ @NotFound@");
		}
		int locationToId = getLocationToId(request, store);
		//	Validate Price List
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		StringBuffer whereClause = new StringBuffer(I_M_Product.COLUMNNAME_SKU + " IN(");
		whereClause.append(")");
		Query query = new Query(Env.getCtx(), I_W_DeliveryViaRuleAllocation.Table_Name, I_W_DeliveryViaRuleAllocation.COLUMNNAME_W_Store_ID + " = ?", null)
				.setParameters(store.getW_Store_ID())
				.setClient_ID()
				.setOnlyActiveRecords(true);
		//	Count it
		int count = query.count();
		query.setLimit(limit, offset).<MWDeliveryViaRuleAllocation>list().forEach(shippmentMethod -> {
			getShippingMethod(shippmentMethod, locationToId).forEach(shippingMethod -> builder.addShippingMethods(shippingMethod));
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
	 * Get location to ID from request
	 * @param request
	 * @param store
	 * @return
	 */
	private int getLocationToId(ListShippingMethodsRequest request, MStore store) {
		if(request.getShippingAddress().getLocationId() > 0) {
			return request.getShippingAddress().getLocationId();
		}
		//	Create new from it
		if(Util.isEmpty(request.getShippingAddress().getCountryCode())
				|| Util.isEmpty(request.getShippingAddress().getRegionName())
				|| Util.isEmpty(request.getShippingAddress().getCityName())) {
			int customerTemplateId = store.get_ValueAsInt(VueStoreFrontUtil.COLUMNNAME_C_TemplateBPartner_ID);
			MBPartner defaultBPartner = null;
			if(customerTemplateId <= 0) {
				defaultBPartner = MBPartner.getBPartnerCashTrx(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()));
			} else {
				defaultBPartner = MBPartner.get(Env.getCtx(), customerTemplateId);
			}
			List<MBPartnerLocation> businessPartnerLocations = Arrays.asList(defaultBPartner.getLocations(false));
			Optional<MBPartnerLocation> maybeLocation = businessPartnerLocations.stream().filter(bPLocation -> bPLocation.isShipTo()).findFirst();
			if(maybeLocation.isPresent()) {
				return maybeLocation.get().getC_Location_ID();
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @param shippmentMethod
	 */
	private List<ShippingMethod.Builder> getShippingMethod(MWDeliveryViaRuleAllocation shippmentMethod, int locationToId) {
		List<ShippingMethod.Builder> shippingMethodList = new ArrayList<>();
		Trx.run(transactionName -> {
			MStore store = VueStoreFrontUtil.getDefaultStore(Env.getAD_Org_ID(Env.getCtx()));
			if(store == null) {
				throw new AdempiereException("@W_Store_ID@ @NotFound@");
			}
			//	Get Price List
			MPriceList priceList = MPriceList.get(Env.getCtx(), store.getM_PriceList_ID(), transactionName);
			MWarehouse warehouse = MWarehouse.get(Env.getCtx(), store.getM_Warehouse_ID());
			Timestamp validFrom = new Timestamp(System.currentTimeMillis());
			FreightEngine freightEngine = FreightEngineFactory.getFreightEngine(Env.getAD_Client_ID(Env.getCtx()));
			//	Get Methods
			if(shippmentMethod.getDeliveryViaRule().equals(MWDeliveryViaRuleAllocation.DELIVERYVIARULE_Shipper)
					&& shippmentMethod.isCalculatedFreight()) {
				if(shippmentMethod.getM_Shipper_ID() > 0) {
					MShipper shipper = (MShipper) shippmentMethod.getM_Shipper();
					FreightInfo freightInfo = freightEngine.getFreightRuleFactory(shipper, shippmentMethod.getFreightCostRule())
							.calculate(Env.getCtx(), 
									shippmentMethod.getM_Shipper_ID(), 
									warehouse.getC_Location_ID(), 
									locationToId,
									shippmentMethod.getM_FreightCategory_ID(), 
									priceList.getC_Currency_ID(), 
									validFrom, 
									Env.ZERO,	//	TODO: from weight
									Env.ZERO, 	//	TODO: from volume
									transactionName, 
									null);
					MFreightCategory freightCategory = MFreightCategory.getById(Env.getCtx(), shippmentMethod.getM_FreightCategory_ID(), transactionName);
					BigDecimal freightAmount = Env.ZERO;
					BigDecimal taxRate = Env.ZERO;
					if(freightInfo != null
							&& freightInfo.getFreightId() > 0) {
						freightAmount = freightInfo.getFreightAmount();
						if(freightCategory.getM_Product_ID() > 0) {
							MProduct product = MProduct.get(Env.getCtx(), freightCategory.getM_Product_ID());
							taxRate = VueStoreFrontUtil.getTaxRate(product.getC_TaxCategory_ID());
						}
					}
					//	
					shippingMethodList.add(ShippingMethod.newBuilder()
							.setCarrierCode(shipper.getUUID())
							.setCarrierName(shipper.getName())
							.setMethodCode(shippmentMethod.getUUID())
							.setMethodName(freightCategory.getName())
							.setAmount(freightAmount.doubleValue())
							.setTaxRate(taxRate.doubleValue())
							.setIsAvailable(true));
				} else {
					MShipper.getShippersForFreightCategory(Env.getCtx(), shippmentMethod.getM_FreightCategory_ID(), transactionName).forEach(shipper -> {
						FreightInfo freightInfo = freightEngine.getFreightRuleFactory(shipper, shippmentMethod.getFreightCostRule())
								.calculate(Env.getCtx(), 
										shipper.getM_Shipper_ID(), 
										warehouse.getC_Location_ID(), 
										0, //	TODO: Add from business partner
										shippmentMethod.getM_FreightCategory_ID(), 
										priceList.getC_Currency_ID(), 
										validFrom, 
										Env.ZERO,	//	TODO: from weight
										Env.ZERO, 	//	TODO: from volume
										transactionName, 
										null);
						MFreightCategory freightCategory = MFreightCategory.getById(Env.getCtx(), shippmentMethod.getM_FreightCategory_ID(), transactionName);
						BigDecimal freightAmount = Env.ZERO;
						BigDecimal taxRate = Env.ZERO;
						if(freightInfo != null
								&& freightInfo.getFreightId() > 0) {
							freightAmount = freightInfo.getFreightAmount();
							if(freightCategory.getM_Product_ID() > 0) {
								MProduct product = MProduct.get(Env.getCtx(), freightCategory.getM_Product_ID());
								taxRate = VueStoreFrontUtil.getTaxRate(product.getC_TaxCategory_ID());
							}
						}
						//	
						shippingMethodList.add(ShippingMethod.newBuilder()
								.setCarrierCode(shipper.getUUID())
								.setCarrierName(shipper.getName())
								.setMethodCode(shippmentMethod.getUUID())
								.setMethodName(freightCategory.getName())
								.setAmount(freightAmount.doubleValue())
								.setTaxRate(taxRate.doubleValue())
								.setIsAvailable(true));
					});
				}
			} else {
				String methodName = MRefList.getListName(Env.getCtx(), MWDeliveryViaRuleAllocation.DELIVERYVIARULE_AD_Reference_ID, shippmentMethod.getDeliveryViaRule());
				shippingMethodList.add(ShippingMethod.newBuilder()
					.setCarrierCode(shippmentMethod.getUUID())
					.setCarrierName(store.getName())
					.setMethodCode(shippmentMethod.getUUID())
					.setMethodName(methodName)
					.setAmount(0)
					.setTaxRate(0)
					.setIsAvailable(true));
			}
		});
		return shippingMethodList;
	}
	
	/**
	 * List Payment Methods
	 * @param request
	 * @return
	 */
	private ListPaymentMethodsResponse.Builder listPaymentMethods(ListPaymentMethodsRequest request) {
		ListPaymentMethodsResponse.Builder builder = ListPaymentMethodsResponse.newBuilder();
		if(request.getCartId() == 0
				&& Util.isEmpty(request.getCartUuid())) {
			throw new AdempiereException("@W_Basket_ID@ @IsMandatory@");
		}
		MStore store = VueStoreFrontUtil.getDefaultStore(Env.getAD_Org_ID(Env.getCtx()));
		if(store == null) {
			throw new AdempiereException("@W_Store_ID@ @NotFound@");
		}
		//	Validate Price List
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		Query query = new Query(Env.getCtx(), I_C_PaymentMethod.Table_Name, "EXISTS(SELECT 1 FROM C_PaymentMethodAllocation a "
				+ "WHERE a.C_PaymentMethod_ID = C_PaymentMethod.C_PaymentMethod_ID "
				+ "AND a.W_Store_ID = ?)" , null)
				.setParameters(store.getW_Store_ID())
				.setClient_ID()
				.setOnlyActiveRecords(true);
		//	Count it
		int count = query.count();
		query.setLimit(limit, offset).<MCPaymentMethod>list().forEach(paymentMethod -> builder.addPaymentMethods(
				PaymentMethod.newBuilder()
					.setCode(ValueUtil.validateNull(paymentMethod.getValue()))
					.setName(ValueUtil.validateNull(paymentMethod.getName())))
				);
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	
		return builder;
	}
	
	/**
	 * Get Cart from Id or UUID
	 * @param cartId
	 * @param cartUuid
	 * @param isGuest
	 * @param transactionName
	 * @return
	 */
	private X_W_Basket getCart(int cartId, String cartUuid, boolean isGuest, int userId, String transactionName) {
		String whereClause = I_W_Basket.COLUMNNAME_W_Basket_ID + " = ? AND " + I_W_Basket.COLUMNNAME_AD_User_ID + " = ?";
		List<Object> parameters = new ArrayList<>();
		if(isGuest) {
			whereClause = I_W_Basket.COLUMNNAME_UUID + " = ?";
			parameters.add(cartUuid);
		} else {
			parameters.add(cartId);
			if(userId > 0) {
				parameters.add(userId);
			} else {
				parameters.add(Env.getAD_User_ID(Env.getCtx()));
			}
		}
		//	Return cart
		return new Query(Env.getCtx(), I_W_Basket.Table_Name, whereClause, null)
				.setParameters(parameters)
				.first();
	}
	
	/**
	 * Create Cart from current user
	 * @param isGuest guest
	 * @param transactionName
	 * @return
	 */
	private X_W_Basket createCart(boolean isGuest, String transactionName) {
		X_W_Basket basket = null;
		if(!isGuest) {
			basket = new Query(Env.getCtx(), I_W_Basket.Table_Name, I_W_Basket.COLUMNNAME_AD_User_ID + " = ?", transactionName)
					.setParameters(Env.getAD_User_ID(Env.getCtx()))
					.setOnlyActiveRecords(true)
					.first();
		}
		//	Create instead
		if(basket == null
				|| basket.getW_Basket_ID() == 0) {
			basket = new X_W_Basket(Env.getCtx(), 0, transactionName);
			MStore store = VueStoreFrontUtil.getDefaultStore(isGuest? 0: Env.getAD_Org_ID(Env.getCtx()));
			if(store == null) {
				throw new AdempiereException("@W_Store_ID@ @NotFound@");
			}
			basket.setAD_Org_ID(store.getAD_Org_ID());
			basket.setAD_User_ID(isGuest? store.getSalesRep_ID(): Env.getAD_User_ID(Env.getCtx()));
			basket.setM_PriceList_ID(store.getM_PriceList_ID());
			//	Reference
			if(!isGuest) {
				MUser user = MUser.get(Env.getCtx(), Env.getAD_User_ID(Env.getCtx()));
				if(user.getC_BPartner_ID() != 0) {
					basket.setC_BPartner_ID(user.getC_BPartner_ID());
				}
			}
			//	Save
			basket.saveEx();
		}
		//	Get Basket
		return basket;
	}
	
	/**
	 * Update Cart from user
	 * @param request
	 * @return
	 */
	private X_W_BasketLine updateCart(UpdateCartRequest request) {
		AtomicReference<X_W_BasketLine> cartItem = new AtomicReference<X_W_BasketLine>();
		Trx.run(transactionName -> {
			MStore store = VueStoreFrontUtil.getDefaultStore(request.getIsGuest()? 0: Env.getAD_Org_ID(Env.getCtx()));
			if(store == null) {
				throw new AdempiereException("@W_Store_ID@ @NotFound@");
			}
			MProduct product = getProductFromSku(request.getSku());
			if(product == null
					|| product.getM_Product_ID() == 0) {
				throw new AdempiereException("@M_Product_ID@ @NotFound@");
			}
			X_W_Basket basket = getCart(request.getCartId(), request.getCartUuid(), request.getIsGuest(), 0, transactionName);
			//	Get Lines
			List<X_W_BasketLine> items = new Query(Env.getCtx(), X_W_BasketLine.Table_Name, X_W_BasketLine.COLUMNNAME_W_Basket_ID + " = ?", transactionName)
					.setParameters(basket.getW_Basket_ID())
					.list();
			//	Create instead
			Optional<X_W_BasketLine> basketLine = items.stream().filter(item -> MProduct.get(Env.getCtx(), item.getM_Product_ID()).getSKU().equals(request.getSku())).findFirst();
			X_W_BasketLine item = null;
			//	Set values
			if(basketLine.isPresent()) {
				item = basketLine.get();
			} else {
				item = new X_W_BasketLine(Env.getCtx(), 0, transactionName);
				item.setW_Basket_ID(basket.getW_Basket_ID());
				item.setM_Product_ID(product.getM_Product_ID());
				item.setProduct(product.getName());
			}
			item.setPrice(Env.ZERO);
			//	Set values
			item.setQty(new BigDecimal(request.getQuantity()));
			item.setIsActive(true);
			item.setDescription(Msg.parseTranslation(Env.getCtx(), "@Created@ @from@ @W_Store_ID@"));
			item.setLine((items.size() * 10) + 10);
			item.saveEx(transactionName);
			//	Set
			cartItem.set(item);
		});
		//	Get Line
		return cartItem.get();
	}
	
	/**
	 * Convert Cart to gRPC
	 * @param basket
	 * @return
	 */
	private Cart.Builder convertCart(X_W_Basket basket, String transactionName) {
		Cart.Builder builder = Cart.newBuilder();
		//	Validate null
		if(basket == null) {
			return builder;
		}
		MStore store = VueStoreFrontUtil.getDefaultStore(basket.getAD_Org_ID());
		if(store == null) {
			throw new AdempiereException("@W_Store_ID@ @NotFound@");
		}
		//	
		MPriceList priceList = MPriceList.get(Env.getCtx(), store.getM_PriceList_ID(), transactionName);
		MCurrency currency = MCurrency.get(Env.getCtx(), priceList.getC_Currency_ID());
		//	
		builder.setId(basket.getW_Basket_ID())
			.setUuid(ValueUtil.validateNull(basket.getUUID()));
		//	
		List<X_W_BasketLine> items = new Query(Env.getCtx(), X_W_BasketLine.Table_Name, X_W_BasketLine.COLUMNNAME_W_Basket_ID + " = ?", transactionName)
				.setParameters(basket.getW_Basket_ID())
				.list(); 
		if(items != null
				&& items.size() > 0) {
			AtomicReference<BigDecimal> subtotal = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> subtotalWithDiscount = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> subtotalWithTax = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> discount = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> tax = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> shipping = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> shippingTax = new AtomicReference<BigDecimal>(Env.ZERO);
			AtomicReference<BigDecimal> grandTotal = new AtomicReference<BigDecimal>(Env.ZERO);
			
			items.forEach(item -> {
				MProduct product = MProduct.get(Env.getCtx(), item.getM_Product_ID());
				MTax taxDefinition = MTax.get(Env.getCtx(), item.get_ValueAsInt(VueStoreFrontUtil.COLUMNNAME_C_Tax_ID));
				BigDecimal lineListAmount = (BigDecimal) item.get_Value(VueStoreFrontUtil.COLUMNNAME_LineListAmt);
				BigDecimal lineDiscount = (BigDecimal) item.get_Value(VueStoreFrontUtil.COLUMNNAME_LineDiscount);
				BigDecimal lineDiscountRate = (BigDecimal) item.get_Value(VueStoreFrontUtil.COLUMNNAME_LineDiscountAmt);
				BigDecimal lineNetAmount = (BigDecimal) item.get_Value(VueStoreFrontUtil.COLUMNNAME_LineNetAmt);
				BigDecimal taxRate = taxDefinition.getRate();
				BigDecimal taxAmount = (BigDecimal) item.get_Value(VueStoreFrontUtil.COLUMNNAME_TaxAmt);
				BigDecimal lineTotalAmount = (BigDecimal) item.get_Value(VueStoreFrontUtil.COLUMNNAME_LineTotalAmt);
				//	Cart Item
				CartItem.Builder cartItem = CartItem.newBuilder()
						.setProductId(product.getM_Product_ID())
						.setSku(ValueUtil.validateNull(product.getSKU()))
						.setName(ValueUtil.validateNull(product.getName()))
						.setProductTypeValue(getProductTypeFromProduct(product).getNumber())
						.setQuantity(item.getQty().doubleValue())
						.setPrice(Optional.ofNullable(item.getPrice()).orElse(Env.ZERO).doubleValue())
						.setRowTotal(Optional.ofNullable(lineListAmount).orElse(Env.ZERO).doubleValue())
						.setRowTotalWithDiscount(Optional.ofNullable(lineNetAmount).orElse(Env.ZERO).doubleValue())
						.setRowTotalInclTax(Optional.ofNullable(lineTotalAmount).orElse(Env.ZERO).doubleValue())
						.setTaxAmount(Optional.ofNullable(taxAmount).orElse(Env.ZERO).doubleValue())
						.setTaxPercent(Optional.ofNullable(taxRate).orElse(Env.ZERO).doubleValue())
						.setDiscountAmount(Optional.ofNullable(lineDiscount).orElse(Env.ZERO).doubleValue())
						.setDiscountPercent(Optional.ofNullable(lineDiscountRate).orElse(Env.ZERO).doubleValue());
				//	Add to Cart
				builder.addItems(cartItem);
				//	Totals
				subtotal.updateAndGet(value -> value.add(Optional.ofNullable(lineListAmount).orElse(Env.ZERO)));
				discount.updateAndGet(value -> value.add(Optional.ofNullable(lineDiscount).orElse(Env.ZERO)));
				tax.updateAndGet(value -> value.add(Optional.ofNullable(taxAmount).orElse(Env.ZERO)));
				grandTotal.updateAndGet(value -> value.add(Optional.ofNullable(lineTotalAmount).orElse(Env.ZERO)));
				subtotalWithDiscount.updateAndGet(value -> value.add(Optional.ofNullable(lineNetAmount).orElse(Env.ZERO)));
				subtotalWithTax.updateAndGet(value -> value.add(Optional.ofNullable(lineTotalAmount).orElse(Env.ZERO)));
			});
			//	Get Freight amount
			List<MPackage> packages = VueStoreFrontUtil.getPackagesFromBasket(Env.getCtx(), basket.getW_Basket_ID(), transactionName);
			if(packages != null
					&& packages.size() > 0) {
				packages.forEach(packageToCalculate -> {
					if(packageToCalculate.getM_FreightCategory_ID() > 0) {
						MFreightCategory freightCategory = MFreightCategory.getById(Env.getCtx(), packageToCalculate.getM_FreightCategory_ID(), transactionName);
						if(freightCategory.isInvoiced()) {
							//	Set values
							AtomicReference<BigDecimal> valueToCalulate = new AtomicReference<BigDecimal>(Env.ZERO);
				        	if(Util.isEmpty(freightCategory.getFreightCalculationType())
				        			|| freightCategory.getFreightCalculationType().equals(MFreightCategory.FREIGHTCALCULATIONTYPE_WeightBased)) {
				        		valueToCalulate.set(packageToCalculate.getWeight());
				        	} else if(freightCategory.getFreightCalculationType().equals(MFreightCategory.FREIGHTCALCULATIONTYPE_VolumeBased)) {
				        		valueToCalulate.set(packageToCalculate.getVolume());
				        	}
				        	//	Calculate
							if(freightCategory.getM_Product_ID() > 0) {
								MProduct product = MProduct.get(Env.getCtx(), freightCategory.getM_Product_ID());
								BigDecimal freightAmount = VueStoreFrontUtil.getPriceStd(product, store.getM_PriceList_ID());
								BigDecimal taxRate = VueStoreFrontUtil.getTaxRate(product.getC_TaxCategory_ID());
								//	Calculate by product
								shipping.updateAndGet(value -> value.add(Optional.ofNullable(valueToCalulate.get()).orElse(Env.ZERO).multiply(Optional.ofNullable(freightAmount).orElse(Env.ZERO))));
								shippingTax.updateAndGet(value -> value.add(Optional.ofNullable(valueToCalulate.get()).orElse(Env.ZERO).multiply(Optional.ofNullable(taxRate).orElse(Env.ZERO))));
							} else if(freightCategory.getC_Charge_ID() > 0) {
								MCharge charge = MCharge.get(Env.getCtx(), freightCategory.getC_Charge_ID());
								BigDecimal freightAmount = charge.getChargeAmt();
								BigDecimal taxRate = VueStoreFrontUtil.getTaxRate(charge.getC_TaxCategory_ID());
								//	Set values
								shipping.updateAndGet(value -> value.add(Optional.ofNullable(freightAmount).orElse(Env.ZERO)));
								shippingTax.updateAndGet(value -> value.add(Optional.ofNullable(taxRate).orElse(Env.ZERO)));
							}
						}
					}
				});
			}
			//	Set totals
			builder.setItemsQuantity(items.size())
				.setBaseCurrencyCode(ValueUtil.validateNull(currency.getISO_Code()))
				.setQuoteCurrencyCode(ValueUtil.validateNull(currency.getISO_Code()))
				.setDiscountAmount(discount.get().doubleValue())
				.setTaxAmount(tax.get().doubleValue())
				.setSubtotal(subtotal.get().doubleValue())
				.setSubtotalInclTax(subtotalWithTax.get().doubleValue())
				.setSubtotalWithDiscount(subtotalWithDiscount.get().doubleValue())
				.setGrandTotal(grandTotal.get().doubleValue())
				.setShippingAmount(shipping.get().doubleValue())
				.setShippingInclTax(shipping.get().add(shippingTax.get()).doubleValue())
				.setShippingTaxAmount(shippingTax.get().doubleValue());
		}
		//	Return cart
		return builder;
	}
	
	/**
	 * Convert cart item
	 * @param basketLine
	 * @return
	 */
	private CartItem.Builder convertCartItem(X_W_BasketLine basketLine) {
		CartItem.Builder cartItem = CartItem.newBuilder();
		if(basketLine == null) {
			return cartItem;
		}
		//	
		MProduct product = MProduct.get(Env.getCtx(), basketLine.getM_Product_ID());
		MTax taxDefinition = MTax.get(Env.getCtx(), basketLine.get_ValueAsInt(VueStoreFrontUtil.COLUMNNAME_C_Tax_ID));
		BigDecimal lineListAmount = (BigDecimal) basketLine.get_Value(VueStoreFrontUtil.COLUMNNAME_LineListAmt);
		BigDecimal lineDiscount = (BigDecimal) basketLine.get_Value(VueStoreFrontUtil.COLUMNNAME_LineDiscount);
		BigDecimal lineDiscountRate = (BigDecimal) basketLine.get_Value(VueStoreFrontUtil.COLUMNNAME_LineDiscountAmt);
		BigDecimal lineNetAmount = (BigDecimal) basketLine.get_Value(VueStoreFrontUtil.COLUMNNAME_LineNetAmt);
		BigDecimal taxRate = taxDefinition.getRate();
		BigDecimal taxAmount = (BigDecimal) basketLine.get_Value(VueStoreFrontUtil.COLUMNNAME_TaxAmt);
		BigDecimal lineTotalAmount = (BigDecimal) basketLine.get_Value(VueStoreFrontUtil.COLUMNNAME_LineTotalAmt);
		//	Cart Item
		return CartItem.newBuilder()
				.setProductId(product.getM_Product_ID())
				.setSku(ValueUtil.validateNull(product.getSKU()))
				.setName(ValueUtil.validateNull(product.getName()))
				.setProductTypeValue(getProductTypeFromProduct(product).getNumber())
				.setQuantity(basketLine.getQty().doubleValue())
				.setPrice(Optional.ofNullable(basketLine.getPrice()).orElse(Env.ZERO).doubleValue())
				.setRowTotal(Optional.ofNullable(lineListAmount).orElse(Env.ZERO).doubleValue())
				.setRowTotalWithDiscount(Optional.ofNullable(lineNetAmount).orElse(Env.ZERO).doubleValue())
				.setRowTotalInclTax(Optional.ofNullable(lineTotalAmount).orElse(Env.ZERO).doubleValue())
				.setTaxAmount(Optional.ofNullable(taxAmount).orElse(Env.ZERO).doubleValue())
				.setTaxPercent(Optional.ofNullable(taxRate).orElse(Env.ZERO).doubleValue())
				.setDiscountAmount(Optional.ofNullable(lineDiscount).orElse(Env.ZERO).doubleValue())
				.setDiscountPercent(Optional.ofNullable(lineDiscountRate).orElse(Env.ZERO).doubleValue());
	}
	
	/**
	 * Update and convert User
	 * @param request
	 * @return
	 */
	private Customer.Builder updateCustomer(UpdateCustomerRequest request) {
		AtomicReference<Customer.Builder> builder = new AtomicReference<Customer.Builder>(Customer.newBuilder());
		Trx.run(transactionName -> {
			//	EMail
			if(Util.isEmpty(request.getEmail())) {
				throw new AdempiereException("@EMail@ @IsMandatory@");
			}
			//	
			if(Util.isEmpty(request.getFirstName())) {
				throw new AdempiereException("@Name@ @IsMandatory@");
			}
			MUser customer = new Query(Env.getCtx(), I_AD_User.Table_Name, "AD_User_ID = ?", transactionName)
				.setParameters(request.getId())
				.first();
			//	Validate if exist
			if(customer == null
					|| customer.getAD_User_ID() <= 0) {
				throw new AdempiereException("@UserName@ / @EMail@ @NotFound@");
			}
			//	Create
			customer.setName(request.getFirstName());
			customer.set_ValueOfColumn(MBPartner.COLUMNNAME_Name2, request.getLastName());
			//	Add Email
			customer.setEMail(request.getEmail());
			customer.setValue(request.getEmail());			
			
			customer.saveEx(transactionName);
			//	Update Location
			request.getAddressesList().forEach(address -> {
				MBPartnerLocation businessPartnerLocation = new MBPartnerLocation(Env.getCtx(), address.getId(), transactionName);
				MCountry country = MCountry.get(Env.getCtx(), address.getCountryCode());
				if(country == null
						|| country.getC_Country_ID() <= 0) {
					country = MCountry.getDefault(Env.getCtx());
				}
				MRegion region = new Query(Env.getCtx(), I_C_Region.Table_Name, I_C_Region.COLUMNNAME_Name + " = ?", transactionName)
						.setParameters(address.getRegionName())
						.first();
				if(region == null
						|| region.getC_Region_ID() <= 0) {
					region = MRegion.getDefault(Env.getCtx());
				}
				MLocation location;
				//	Create new
				if(businessPartnerLocation.getC_BPartner_ID() <= 0) {
					location = new MLocation(country, region);
				} else {
					location = new MLocation(Env.getCtx(), businessPartnerLocation.getC_Location_ID(), transactionName);
				}
				//	Set values
				location.setAddress1(address.getAddress1());
				location.setAddress2(address.getAddress2());
				location.setAddress3(address.getAddress3());
				location.setAddress4(address.getAddress4());
				location.setPostal(address.getPostalCode());
				//	Save
				location.saveEx(transactionName);
				//	Update location of business partner
				businessPartnerLocation.setName(ValueUtil.validateNull(address.getFirstName()));
				businessPartnerLocation.set_ValueOfColumn(MBPartner.COLUMNNAME_Description, ValueUtil.validateNull(address.getLastName()));
				businessPartnerLocation.setC_Location_ID(location.getC_Location_ID());
				businessPartnerLocation.setPhone(address.getPhone());
				boolean isDefaultShipping = request.getDefaultShipping() == businessPartnerLocation.getC_BPartner_Location_ID();
				boolean isDefaultBilling = request.getDefaultBilling() == businessPartnerLocation.getC_BPartner_Location_ID();
				if(isDefaultBilling) {
					businessPartnerLocation.setIsBillTo(isDefaultBilling);
				}
				//	Default Shipping
				businessPartnerLocation.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_IsDefaultShipping, isDefaultShipping);
				businessPartnerLocation.set_ValueOfColumn(VueStoreFrontUtil.COLUMNNAME_IsDefaultBilling, isDefaultBilling);
				//	Save
				businessPartnerLocation.saveEx(transactionName);
			});
			builder.set(convertCustomer(customer));
		});
		return builder.get();
	}
	
	/**
	 * Convert Customer
	 * @param customer
	 * @return
	 */
	private Customer.Builder convertCustomer(MUser customer) {
		Customer.Builder builder = Customer.newBuilder();
		//	Set builder
		builder.setEmail(ValueUtil.validateNull(customer.getEMail()))
			.setFirstName(ValueUtil.validateNull(customer.getName()))
			.setLastName(ValueUtil.validateNull(customer.get_ValueAsString(MBPartner.COLUMNNAME_Name2)))
			.setId(customer.getAD_User_ID())
			.setCreated(dateConverter.format(customer.getCreated()))
			.setUpdated(dateConverter.format(customer.getUpdated()))
			.setOrganizationName(ValueUtil.validateNull(MOrg.get(Env.getCtx(), customer.getAD_Org_ID()).getName()));
		//	TODO: Add Web Site ID and Web Store ID
		if(customer.getC_BPartner_ID() > 0) {
			Arrays.asList(((MBPartner) customer.getC_BPartner()).getLocations(true))
				.forEach(businessPartnerLocation -> builder.addAddresses(convertAddress(customer, businessPartnerLocation, customer.get_TrxName())));
		}
		return builder;
	}
	
	/**
	 * Create and convert User
	 * @param request
	 * @return
	 */
	private Customer.Builder createCustomer(CreateCustomerRequest request) {
		AtomicReference<Customer.Builder> builder = new AtomicReference<Customer.Builder>(Customer.newBuilder());
		Trx.run(transactionName -> {
			//	EMail
			if(Util.isEmpty(request.getEmail())) {
				throw new AdempiereException("@EMail@ @IsMandatory@");
			}
			//	
			if(Util.isEmpty(request.getFirstName())) {
				throw new AdempiereException("@Name@ @IsMandatory@");
			}
			int userId = new Query(Env.getCtx(), I_AD_User.Table_Name, "Value = ? OR EMail = ?", transactionName)
				.setParameters(request.getEmail(), request.getEmail())
				.firstId();
			//	Validate if exist
			if(userId > 0) {
				throw new AdempiereException("@UserName@ / @EMail@ @AlreadyExists@");
			}
			//	Set builder
			builder.set(convertCustomer(createUser(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword(), transactionName)));
		});
		return builder.get();
	}
	
	/**
	 * Create Customer
	 * @param firtName
	 * @param lastName
	 * @param email
	 * @param password
	 * @param transactionName
	 * @return
	 */
	private MUser createUser(String firtName, String lastName, String email, String password, String transactionName) {
		MUser newUser = new MUser(Env.getCtx(), 0, transactionName);
		newUser.setName(firtName);
		newUser.set_ValueOfColumn(MBPartner.COLUMNNAME_Name2, lastName);
		//	Add Email
		newUser.setEMail(email);
		newUser.setValue(email);
		newUser.setIsLoginUser(true);
		newUser.setIsInternalUser(false);
		newUser.setIsWebstoreUser(true);
		newUser.saveEx(transactionName);
		if(!Util.isEmpty(password)) {
			newUser.setPassword(password);
			newUser.saveEx(transactionName);
		}
		return newUser;
	}
	
	/**
	 * get Customer info
	 * @param request
	 * @return
	 */
	private Customer.Builder getCustomerInfo(GetCustomerRequest request) {
		AtomicReference<Customer.Builder> builder = new AtomicReference<Customer.Builder>(Customer.newBuilder());
		//	EMail
		if(Util.isEmpty(request.getClientRequest().getSessionUuid())) {
			throw new AdempiereException("@AD_Session_ID@ @IsMandatory@");
		}
		//	
		Trx.run(transactionName -> {
			//	Create
			MUser customer = MUser.get(Env.getCtx());
			customer.set_TrxName(transactionName);
			//	Set builder
			builder.set(convertCustomer(customer));
		});
		return builder.get();
	}
	
	/**
	 * Convert address
	 * @param businessPartnerLocation
	 * @param transactionName
	 * @return
	 */
	private Address.Builder convertAddress(MUser contact, MBPartnerLocation businessPartnerLocation, String transactionName) {
		Address.Builder builder = Address.newBuilder();
		String phone = null;
		if(contact != null) {
			phone = contact.getPhone();
		}
		MLocation location = MLocation.get(Env.getCtx(), businessPartnerLocation.getC_Location_ID(), transactionName);
		builder.setId(businessPartnerLocation.getC_BPartner_Location_ID())
			.setCountryCode(MCountry.get(Env.getCtx(), location.getC_Country_ID()).getCountryCode())
			.setPostalCode(ValueUtil.validateNull(location.getPostal()))
			.setPhone(ValueUtil.validateNull(Optional.ofNullable(businessPartnerLocation.getPhone()).orElse(Optional.ofNullable(phone).orElse(""))))
			.setFirstName(ValueUtil.validateNull(businessPartnerLocation.getName()))
			.setLastName(ValueUtil.validateNull(businessPartnerLocation.get_ValueAsString(MBPartner.COLUMNNAME_Description)))
			.setAddress1(ValueUtil.validateNull(location.getAddress1()))
			.setAddress2(ValueUtil.validateNull(location.getAddress2()))
			.setAddress3(ValueUtil.validateNull(location.getAddress3()))
			.setAddress4(ValueUtil.validateNull(location.getAddress4()))
			.setIsDefaultShipping(businessPartnerLocation.isShipTo() && businessPartnerLocation.get_ValueAsBoolean(VueStoreFrontUtil.COLUMNNAME_IsDefaultShipping))
			.setIsDefaultShipping(businessPartnerLocation.isBillTo() && businessPartnerLocation.get_ValueAsBoolean(VueStoreFrontUtil.COLUMNNAME_IsDefaultBilling));
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
		return builder;
	}
	
	/**
	 * Reset password
	 * @param request
	 * @return
	 */
	private ResetPasswordResponse.Builder resetPassword(ResetPasswordRequest request) {
		//	User Name
		if(Util.isEmpty(request.getUserName())
				&& Util.isEmpty(request.getEmail())) {
			throw new AdempiereException("@UserName@ / @EMail@ @IsMandatory@");
		}
		ResetPasswordResponse.Builder builder = ResetPasswordResponse.newBuilder();
		MUser user = new Query(Env.getCtx(), I_AD_User.Table_Name, "Value type filter text= ? OR EMail = ?", null)
			.setParameters(request.getUserName(), request.getEmail())
			.first();
		//	Validate if exist
		if(user == null
				|| user.getAD_User_ID()  <= 0) {
			builder.setResponseType(ResponseType.USER_NOT_FOUND);
			throw new AdempiereException("@UserName@ / @EMail@ @NotFound@");
		}
		//	Generate reset
		try {
			MADToken token = generateToken(user);
			//	Send mail
			sendEMail(user, token);
		} catch (Exception e) {
			builder.setResponseType(ResponseType.ERROR);
			throw new AdempiereException(e.getMessage());
		}
		builder.setResponseType(ResponseType.OK);
		return builder;
	}
	
	/**
	 * Reset password
	 * @param request
	 * @return
	 */
	private ChangePasswordResponse.Builder changePassword(ChangePasswordRequest request) {
		//	User Name
		if(Util.isEmpty(request.getClientRequest().getSessionUuid())) {
			throw new AdempiereException("@UserName@ / @EMail@ @IsMandatory@");
		}
		ChangePasswordResponse.Builder builder = ChangePasswordResponse.newBuilder();
		MUser user = MUser.get(Env.getCtx());
		//	Validate if exist
		if(user == null
				|| user.getAD_User_ID()  <= 0) {
			builder.setResponseType(org.spin.grpc.store.ChangePasswordResponse.ResponseType.USER_NOT_FOUND);
			throw new AdempiereException("@UserName@ / @EMail@ @NotFound@");
		}
		Login loginTest = new Login(Env.getCtx());
		if(loginTest.getAuthenticatedUserId(user.getValue(), request.getCurrentPassword()) != -1) {
			user.setPassword(request.getNewPassword());
			user.saveEx();
			builder.setResponseType(org.spin.grpc.store.ChangePasswordResponse.ResponseType.OK);
		} else {
			builder.setResponseType(org.spin.grpc.store.ChangePasswordResponse.ResponseType.USER_NOT_FOUND);
			throw new AdempiereException("@UserName@ / @EMail@ @NotFound@");
		}
		return builder;
	}
	
	/**
	 * Send EMail for user
	 * @param user
	 * @param token
	 */
	private void sendEMail(MUser user, MADToken token) {
		MClient client = MClient.get(user.getCtx(), user.getAD_Client_ID());
		MClientInfo clientInfo = client.getInfo();
		//	Get
		int mailTextId = clientInfo.getRestorePassword_MailText_ID();
		if(mailTextId <= 0) {
			throw new AdempiereException("@RestorePassword_MailText_ID@ @NotFound@");
		}
		//	Set from mail template
		MMailText text = new MMailText (Env.getCtx(), mailTextId, null);
		text.setPO(token);
		text.setUser(user);
		//	
		EMail email = client.createEMail(user.getEMail(), null, null);
		//	
		String msg = null;
		if (!email.isValid()) {
			msg = "@RequestActionEMailError@ Invalid EMail: " + user;
			throw new AdempiereException("@RequestActionEMailError@ Invalid EMail: " + user);
		}
		//text.setUser(user);	//	variable context
		String message = text.getMailText(true);
		email.setMessageHTML(text.getMailHeader(), message);
		//
		msg = email.send();
		MUserMail userMail = new MUserMail(text, user.getAD_User_ID(), email);
		userMail.saveEx();
		if (!msg.equals(EMail.SENT_OK)) {
			throw new AdempiereException(user.getName() + " @RequestActionEMailError@ " + msg);
		}
	}
	
	/**
	 * Generate token
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private MADToken generateToken(MUser user) throws Exception {
		if(user == null) {
			throw new AdempiereException("@AD_User_ID@ @NotFound@");
		}
		//	Validate EMail
		if (Util.isEmpty(user.getEMail())) {
			throw new AdempiereException("@AD_User_ID@ - @Email@ @NotFound@");
		}
		//	
		TokenGeneratorHandler.getInstance().generateToken(MADTokenDefinition.TOKENTYPE_URLTokenUsedAsURL, user.getAD_User_ID());
		return TokenGeneratorHandler.getInstance().getToken(MADTokenDefinition.TOKENTYPE_URLTokenUsedAsURL);
	}
	
	/**
	 * Get product from SKU
	 * @param sku
	 * @return
	 */
	private MProduct getProductFromSku(String sku) {
		//	SKU
		if(Util.isEmpty(sku)) {
			throw new AdempiereException("@SKU@ @IsMandatory@");
		}
		//	
		MProduct product = null;
		String key = Env.getAD_Client_ID(Env.getCtx()) + "|";
		if(!Util.isEmpty(sku)) {
			key = key + "SKU|" + sku.trim();
			product = productCache.get(key);
			if(product == null) {
				product = new Query(Env.getCtx(), I_M_Product.Table_Name, 
						"(UPPER(SKU) = UPPER(?))", null)
						.setParameters(sku.trim())
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
		//	Default
		return product;
	}
	
	/**
	 * Get Stock from SKU or request
	 * @param request
	 * @return
	 */
	private Stock.Builder getStockFromSku(GetStockRequest request) {
		MProduct product = getProductFromSku(request.getSku());
		Stock.Builder builder = Stock.newBuilder();
		Optional<MStorage> maybeStorage = Arrays.asList(MStorage.getOfProduct(Env.getCtx(), product.getM_Product_ID(), null))
				.stream()
				.filter(storage -> storage.getQtyOnHand().signum() > 0)
				.reduce(StockSummary::add);
		if(maybeStorage.isPresent()) {
			builder = convertStock(maybeStorage.get());
		}
		//	
		return builder;
	}
	
	/**
	 * Summarize stock
	 */
	private static final class StockSummary {
		public static MStorage add(MStorage previousValue, MStorage newValue) {
			previousValue.setQtyOnHand(previousValue.getQtyOnHand().add(newValue.getQtyOnHand()));
			previousValue.setQtyReserved(previousValue.getQtyReserved().add(newValue.getQtyReserved()));
			return previousValue;
		}
	}
	
	/**
	 * List Products
	 * @param request
	 * @return
	 */
	private ListProductsResponse.Builder listProducts(ListProductsRequest request) {
		org.spin.grpc.store.ListProductsResponse.Builder builder = ListProductsResponse.newBuilder();
		if(request.getSkusCount() == 0) {
			throw new AdempiereException("@SKU@ @IsMandatory@");
		}
		//	Validate Price List
		MStore store = VueStoreFrontUtil.getDefaultStore(Env.getAD_Org_ID(Env.getCtx()));
		if(store == null) {
			throw new AdempiereException("@W_Store_ID@ @NotFound@");
		}
		//	TODO: Get from Web Store Definition
		MPriceList priceList = MPriceList.get(Env.getCtx(), store.getM_PriceList_ID(), null);
		//	Get Valid From
		Timestamp validFrom = TimeUtil.getDay(System.currentTimeMillis());
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		StringBuffer whereClause = new StringBuffer(I_M_Product.COLUMNNAME_SKU + " IN(");
		AtomicBoolean first = new AtomicBoolean(true);
		List<Object> parameters = new ArrayList<>();
		request.getSkusList().forEach(sku -> {
			if(first.get()) {
				first.set(false);
			} else {
				whereClause.append(", ");
			}
			//	
			whereClause.append("?");
			parameters.add(sku.trim());
		});
		whereClause.append(")");
		whereClause.append(" AND ").append(I_M_Product.COLUMNNAME_IsWebStoreFeatured).append(" = ").append("?");
		parameters.add(true);
		Query query = new Query(Env.getCtx(), I_M_Product.Table_Name, whereClause.toString(), null)
			.setParameters(parameters)
			.setClient_ID()
			.setOnlyActiveRecords(true);
		//	Count it
		int count = query.count();
		query.setLimit(limit, offset).<MProduct>list().forEach(product -> builder.addProducts(convertProduct(product, priceList, validFrom)));
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	
		return builder;
	}
	
	/**
	 * List Render Products
	 * @param request
	 * @return
	 */
	private ListRenderProductsResponse.Builder listRenderProducts(ListRenderProductsRequest request) {
		ListRenderProductsResponse.Builder builder = ListRenderProductsResponse.newBuilder();
		if(request.getSkusCount() == 0) {
			throw new AdempiereException("@SKU@ @IsMandatory@");
		}
		//	Validate Price List
		MStore store = VueStoreFrontUtil.getDefaultStore(Env.getAD_Org_ID(Env.getCtx()));
		if(store == null) {
			throw new AdempiereException("@W_Store_ID@ @NotFound@");
		}
		//	TODO: Get from Web Store Definition
		MPriceList priceList = MPriceList.get(Env.getCtx(), store.getM_PriceList_ID(), null);
		//	Get Valid From
		Timestamp validFrom = TimeUtil.getDay(System.currentTimeMillis());
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		StringBuffer whereClause = new StringBuffer(I_M_Product.COLUMNNAME_SKU + " IN(");
		AtomicBoolean first = new AtomicBoolean(true);
		List<Object> parameters = new ArrayList<>();
		request.getSkusList().forEach(sku -> {
			if(first.get()) {
				first.set(false);
			} else {
				whereClause.append(", ");
			}
			//	
			whereClause.append("?");
			parameters.add(sku.trim());
		});
		whereClause.append(")");
		Query query = new Query(Env.getCtx(), I_M_Product.Table_Name, whereClause.toString(), null)
			.setParameters(parameters)
			.setClient_ID()
			.setOnlyActiveRecords(true);
		//	Count it
		int count = query.count();
		query.setLimit(limit, offset).<MProduct>list().forEach(product -> builder.addRenderProducts(convertRenderProduct(product, priceList, validFrom)));
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	
		return builder;
	}
	
	/**
	 * Convert product from price list
	 * @param product
	 * @param productPriceList
	 * @param validFrom
	 * @return
	 */
	private RenderProduct.Builder convertRenderProduct(MProduct product, MPriceList productPriceList, Timestamp validFrom) {
		RenderProduct.Builder builder = RenderProduct.newBuilder();
		//	Get Price
		MProductPricing productPricing = new MProductPricing(product.getM_Product_ID(), 0, Env.ZERO, true, null);
		productPricing.setM_PriceList_ID(productPriceList.getM_PriceList_ID());
		productPricing.setPriceDate(validFrom);
		int taxCategoryId = product.getC_TaxCategory_ID();
		BigDecimal taxRate = Env.ZERO;
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
			taxRate = optionalTax.get().getRate();
		}
		//	Calculate tax rate
		taxRate = taxRate.divide(Env.ONEHUNDRED).add(Env.ONE);
		//	Calculate
		BigDecimal basePriceList = productPricing.getPriceList();
		BigDecimal priceList = productPricing.getPriceList();
		BigDecimal basePriceStd = productPricing.getPriceStd();
		BigDecimal priceStd = productPricing.getPriceStd();
		BigDecimal basePriceLimit = productPricing.getPriceLimit();
		BigDecimal priceLimit = productPricing.getPriceLimit();
		//	Calculate base
		if(taxRate.signum() != 0) {
			if(productPriceList.isTaxIncluded()) {
				basePriceList = basePriceList.divide(taxRate, MathContext.DECIMAL128);
				basePriceStd = basePriceStd.divide(taxRate, MathContext.DECIMAL128);
				basePriceLimit = basePriceLimit.divide(taxRate, MathContext.DECIMAL128);
			} else {
				priceList = basePriceList.multiply(taxRate);
				priceStd = basePriceStd.multiply(taxRate);
				priceLimit = basePriceLimit.multiply(taxRate);
			}
		}
		MCurrency currency = MCurrency.get(Env.getCtx(), productPriceList.getC_Currency_ID());
		//	Set product values
		return builder.setId(product.getM_Product_ID())
			.setName(ValueUtil.validateNull(product.getName()))
			.setStoreId(Env.getAD_Org_ID(Env.getCtx()))
			.setUrl(ValueUtil.validateNull(product.getDescriptionURL()))
			.setProductType(getProductTypeFromProduct(product))
			.setPriceInfo(
					PriceInfo.newBuilder()
						.setMaxPrice(priceList.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
						.setMaxRegularPrice(priceList.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
						
						.setFinalPrice(priceStd.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
						.setSpecialPrice(priceStd.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
						.setRegularPrice(priceStd.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
						
						.setMinimalRegularPrice(priceLimit.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
						.setMinimalPrice(priceLimit.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
						
						.setCurrencyCode(MCurrency.getISO_Code(Env.getCtx(), productPriceList.getC_Currency_ID()))
						.setFormattedPrice(
								FormattedPrice.newBuilder()
								//	TODO: Change to dynamic text message
								.setMaxPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(priceList) + "</span>")
								.setMaxRegularPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(priceList) + "</span>")
								
								.setFinalPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(priceStd) + "</span>")
								.setSpecialPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(priceStd) + "</span>")
								.setRegularPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(priceStd) + "</span>")
								
								.setMinimalRegularPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(priceLimit) + "</span>")
								.setMinimalPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(priceLimit) + "</span>")
								)
						.setTaxAdjustment(
								TaxAdjustment.newBuilder()
								.setMaxPrice(basePriceList.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
								.setMaxRegularPrice(basePriceList.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
								
								.setFinalPrice(basePriceStd.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
								.setSpecialPrice(basePriceStd.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
								.setRegularPrice(basePriceStd.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
								
								.setMinimalRegularPrice(basePriceLimit.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
								.setMinimalPrice(basePriceLimit.setScale(productPricing.getPrecision(), BigDecimal.ROUND_UP).doubleValue())
								.setFormattedPrice(
										FormattedPrice.newBuilder()
										//	TODO: Change to dynamic text message
										.setMaxPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(basePriceList) + "</span>")
										.setMaxRegularPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(basePriceList) + "</span>")
										
										.setFinalPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(basePriceStd) + "</span>")
										.setSpecialPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(basePriceStd) + "</span>")
										.setRegularPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(basePriceStd) + "</span>")
										
										.setMinimalRegularPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(basePriceLimit) + "</span>")
										.setMinimalPrice("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(basePriceLimit) + "</span>")
										)
								.setWeeeAdjustment("<span class=\"price\">" + currency.getCurSymbol() + DisplayType.getNumberFormat(DisplayType.CostPrice).format(priceStd) + "</span>"))
						);
	}
	
	/**
	 * Get Render Product Type from Product
	 * @param product
	 * @return
	 */
	private RenderProduct.ProductType getProductTypeFromProduct(MProduct product) {
		if(Util.isEmpty(product.getProductType())) {
			return RenderProduct.ProductType.SIMPLE;
		}
		//	Configurable Product
		if(product.getProductType().equals(MProduct.PRODUCTTYPE_Item)
				&& product.isStocked()
				&& product.getM_AttributeSet_ID() > 0) {
			return RenderProduct.ProductType.CONFIGURABLE;
		}
		//	Grouped Product
		if(product.getProductType().equals(MProduct.PRODUCTTYPE_Item)
				&& product.isStocked()
				&& product.isBOM()) {
			return RenderProduct.ProductType.GROUPED;
		}
		//	Virtual Product
		if(product.getProductType().equals(MProduct.PRODUCTTYPE_Service)) {
			return RenderProduct.ProductType.CONFIGURABLE;
		}
		//	Bundle Product
		if(product.getProductType().equals(MProduct.PRODUCTTYPE_Item)
				&& !product.isStocked()
				&& product.isBOM()) {
			return RenderProduct.ProductType.GROUPED;
		}
		//	Downloadable Product
		if(product.getProductType().equals(MProduct.PRODUCTTYPE_Service)
				&& product.getM_Product_Category().getA_Asset_Group_ID() > 0) {
			return RenderProduct.ProductType.DOWNLOADABLE;
		}
		//	Gift Cards
		if((product.getProductType().equals(MProduct.PRODUCTTYPE_Item)
				|| product.getProductType().equals(MProduct.PRODUCTTYPE_Service))
				&& product.getR_MailText_ID() > 0) {
			return RenderProduct.ProductType.GIFT;
		}
		//	Simple Product
		return RenderProduct.ProductType.SIMPLE;
	}
	
	/**
	 * Convert product from price list
	 * @param product
	 * @param priceList
	 * @param validFrom
	 * @return
	 */
	private Product.Builder convertProduct(MProduct product, MPriceList priceList, Timestamp validFrom) {
		Product.Builder builder = Product.newBuilder();
		//	Get Price
		MProductPricing productPricing = new MProductPricing(product.getM_Product_ID(), 0, Env.ZERO, true, null);
		productPricing.setM_PriceList_ID(priceList.getM_PriceList_ID());
		productPricing.setPriceDate(validFrom);
		//	Set product values
		return builder.setId(product.getM_Product_ID())
			.setSku(ValueUtil.validateNull(product.getSKU()))
			.setName(ValueUtil.validateNull(product.getName()))
			//	TODO: Add status from product
			.setStatus(org.spin.grpc.store.Product.Status.ENABLED)
			//	TODO: Add to product
			.setVisibility(org.spin.grpc.store.Product.Visibility.BOTH)
			.setProductGroupId(product.getM_Product_Group_ID())
			.setCreated(dateConverter.format(product.getCreated()))
			.setUpdated(dateConverter.format(product.getUpdated()))
			//	Pricing
			.setPrice(productPricing.getPriceStd().setScale(productPricing.getPrecision()).doubleValue())
			//	TODO: Get Criteria
			.addCustomAttributes(
					Attribute.newBuilder()
						.setAttributeCode("description")
						.setValue(ValueUtil.validateNull(product.getDescription()))
						);
	}
	
	/**
	 * List Stocks
	 * @param request
	 * @return
	 */
	private ListStocksResponse.Builder listStocks(ListStocksRequest request) {
		MProduct product = getProductFromSku(request.getSku());
		ListStocksResponse.Builder builder = ListStocksResponse.newBuilder();
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int limit = RecordUtil.PAGE_SIZE;
		int offset = pageNumber * RecordUtil.PAGE_SIZE;
		Query query = new Query(Env.getCtx(), I_M_Storage.Table_Name, 
				I_M_Storage.COLUMNNAME_M_Product_ID + " = ? "
						+ "AND " + I_M_Storage.COLUMNNAME_QtyOnHand + " > 0", null)
				.setParameters(product.getM_Product_ID())
				.setClient_ID()
				.setOnlyActiveRecords(true);
		int count = query.count();
		query.setLimit(limit, offset).<MStorage>list().forEach(storage -> builder.addStocks(convertStock(storage)));
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > offset && count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set next page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		//	
		return builder;
	}
	
	/**
	 * Convert stock
	 * @param storage
	 * @return
	 */
	private Stock.Builder convertStock(MStorage storage) {
		Stock.Builder builder = Stock.newBuilder();
		BigDecimal quantityOnHand = Env.ZERO;
		BigDecimal quantityReserved = Env.ZERO;
		if(storage == null) {
			return builder;
		}
		//	On hand
		if(storage.getQtyOnHand() != null) {
			quantityOnHand = storage.getQtyOnHand(); 
		}
		//	Reserved
		if(storage.getQtyReserved() != null) {
			quantityReserved = storage.getQtyReserved();
		}
		builder.setIsInStock(quantityOnHand.signum() > 0);
		builder.setQuantity(quantityOnHand.doubleValue());
		if(quantityReserved.signum() < 0) {
			builder.setQuantity(quantityReserved.abs().doubleValue());
		}
		//	
		MProduct product = MProduct.get(Env.getCtx(), storage.getM_Product_ID());
		MUOM unitOfMeasure = MUOM.get(Env.getCtx(), product.getC_UOM_ID());
		builder.setIsDecimalQuantity(unitOfMeasure.getStdPrecision() != 0);
		//	References
		builder.setProductId(storage.getM_Product_ID());
		//	Set from same organization
		MStore store = VueStoreFrontUtil.getDefaultStore(storage.getAD_Org_ID());
		if(store != null) {
			builder.setStoreId(store.getW_Store_ID());
		}
		builder.setIsManageStock(product.isStocked());
		//	
		return builder;
	}
}

/************************************************************************************
 * Copyright (C) 2018-present E.R.P. Consultores y Asociados, C.A.                  *
 * Contributor(s): Edwin Betancourt, EdwinBetanc0urt@outlook.com                    *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program. If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/

package org.spin.pos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Optional;

import org.adempiere.core.domains.models.I_AD_Ref_List;
import org.compiere.model.MBPartner;
import org.compiere.model.MCurrency;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MRefList;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.spin.backend.grpc.core_functionality.Currency;
import org.spin.backend.grpc.pos.Payment;
import org.spin.backend.grpc.pos.PaymentMethod;
import org.spin.backend.grpc.pos.PaymentReference;
import org.spin.base.util.ConvertUtil;
import org.spin.grpc.service.core_functionality.CoreFunctionalityConvert;
import org.spin.service.grpc.util.value.NumberManager;
import org.spin.service.grpc.util.value.ValueManager;
import org.spin.store.model.MCPaymentMethod;

/**
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 * Payment Convert Util from Point Of Sales
 */
public class PaymentConvertUtil {

	public static PaymentMethod.Builder convertPaymentMethod(MCPaymentMethod paymentMethod) {
		PaymentMethod.Builder paymentMethodBuilder = PaymentMethod.newBuilder();
		if(paymentMethod == null) {
			return paymentMethodBuilder;
		}
		paymentMethodBuilder
			.setId(paymentMethod.getC_PaymentMethod_ID())
			.setName(
				ValueManager.validateNull(
					paymentMethod.getName()
				)
			)
			.setValue(
				ValueManager.validateNull(
					paymentMethod.getValue()
				)
			)
			.setDescription(
				ValueManager.validateNull(
					paymentMethod.getDescription()
				)
			)
			.setTenderType(
				ValueManager.validateNull(
					paymentMethod.getTenderType()
				)
			)
			.setIsActive(paymentMethod.isActive()
		);

		return paymentMethodBuilder;
	}


	/**
	 * Conver Refund Reference to gRPC stub object
	 * @param paymentReference
	 * @return
	 * @return RefundReference.Builder
	 */
	public static PaymentReference.Builder convertPaymentReference(PO paymentReference) {
		PaymentReference.Builder builder = PaymentReference.newBuilder();
		if(paymentReference != null
				&& paymentReference.get_ID() > 0) {
			MCPaymentMethod paymentMethod = MCPaymentMethod.getById(Env.getCtx(), paymentReference.get_ValueAsInt("C_PaymentMethod_ID"), null);
			PaymentMethod.Builder paymentMethodBuilder = convertPaymentMethod(paymentMethod);

			int presicion = MCurrency.getStdPrecision(paymentReference.getCtx(), paymentReference.get_ValueAsInt("C_Currency_ID"));

			BigDecimal amount = (BigDecimal) paymentReference.get_Value("Amount");
			amount.setScale(presicion, RoundingMode.HALF_UP);

			MOrder order = new MOrder(Env.getCtx(), paymentReference.get_ValueAsInt("C_Order_ID"), null);
			BigDecimal convertedAmount = ConvertUtil.getConvetedAmount(order, paymentReference, amount)
				.setScale(presicion, RoundingMode.HALF_UP);

			builder.setAmount(
					NumberManager.getBigDecimalToString(
						amount
					)
				)
				.setDescription(
					ValueManager.validateNull(
						paymentReference.get_ValueAsString("Description")
					)
				)
				.setIsPaid(
					paymentReference.get_ValueAsBoolean("IsPaid")
				)
				.setTenderTypeCode(
					ValueManager.validateNull(
						paymentReference.get_ValueAsString("TenderType")
					)
				)
				.setCurrency(
					CoreFunctionalityConvert.convertCurrency(
						paymentReference.get_ValueAsInt("C_Currency_ID")
					)
				)
				.setCustomerBankAccountId(paymentReference.get_ValueAsInt("C_BP_BankAccount_ID"))
				.setOrderId(paymentReference.get_ValueAsInt("C_Order_ID"))
				.setPosId(paymentReference.get_ValueAsInt("C_POS_ID"))
				.setSalesRepresentative(
					CoreFunctionalityConvert.convertSalesRepresentative(
						MUser.get(Env.getCtx(), paymentReference.get_ValueAsInt("SalesRep_ID"))
					)
				)
				.setId(paymentReference.get_ID())
				.setPaymentMethod(paymentMethodBuilder)
				.setPaymentDate(
					ValueManager.getTimestampFromDate(
						(Timestamp) paymentReference.get_Value("PayDate")
					)
				)
				.setIsAutomatic(
					paymentReference.get_ValueAsBoolean("IsAutoCreatedReference")
				)
				.setIsProcessed(
					paymentReference.get_ValueAsBoolean("Processed")
				)
				.setConvertedAmount(
					NumberManager.getBigDecimalToString(
						convertedAmount
					)
				)
			;
		}
		//	
		return builder;
	}


	/**
	 * Convert payment
	 * @param payment
	 * @return
	 */
	public static Payment.Builder convertPayment(MPayment payment) {
		Payment.Builder builder = Payment.newBuilder();
		if(payment == null) {
			return builder;
		}
		//	
		MRefList reference = MRefList.get(Env.getCtx(), MPayment.DOCSTATUS_AD_REFERENCE_ID, payment.getDocStatus(), payment.get_TrxName());
		int presicion = MCurrency.getStdPrecision(payment.getCtx(), payment.getC_Currency_ID());
		BigDecimal paymentAmount = payment.getPayAmt(true);
		if(payment.getTenderType().equals(MPayment.TENDERTYPE_CreditMemo)
				&& paymentAmount.compareTo(Env.ZERO) == 0) {
			MInvoice creditMemo = new Query(payment.getCtx(), MInvoice.Table_Name, "C_Payment_ID = ?", payment.get_TrxName()).setParameters(payment.getC_Payment_ID()).first();
			if(creditMemo != null) {
				paymentAmount = creditMemo.getGrandTotal();
			}
		}
		paymentAmount = paymentAmount.setScale(presicion, RoundingMode.HALF_UP);

		MCPaymentMethod paymentMethod = MCPaymentMethod.getById(Env.getCtx(), payment.get_ValueAsInt("C_PaymentMethod_ID"), null);
		PaymentMethod.Builder paymentMethodBuilder = convertPaymentMethod(paymentMethod);
		
		Currency.Builder currencyBuilder = CoreFunctionalityConvert.convertCurrency(
			payment.getC_Currency_ID()
		);
		MOrder order = new MOrder(payment.getCtx(), payment.getC_Order_ID(), null);
		BigDecimal convertedAmount = ConvertUtil.getConvetedAmount(order, payment, paymentAmount)
			.setScale(presicion, RoundingMode.HALF_UP);
		String invoiceNo = null;
		if(payment.getC_Invoice_ID() > 0) {
			invoiceNo = payment.getC_Invoice().getDocumentNo();
		}
		
		//	Convert
		builder
			.setId(payment.getC_Payment_ID())
			.setOrderId(payment.getC_Order_ID())
			.setDocumentNo(ValueManager.validateNull(payment.getDocumentNo()))
			.setOrderDocumentNo(ValueManager.validateNull(order.getDocumentNo()))
			.setInvoiceDocumentNo(ValueManager.validateNull(invoiceNo))
			.setTenderTypeCode(ValueManager.validateNull(payment.getTenderType()))
			.setReferenceNo(ValueManager.validateNull(Optional.ofNullable(payment.getCheckNo()).orElse(payment.getDocumentNo())))
			.setDescription(ValueManager.validateNull(payment.getDescription()))
			.setAmount(
				NumberManager.getBigDecimalToString(
					paymentAmount
				)
			)
			.setConvertedAmount(
				NumberManager.getBigDecimalToString(
					convertedAmount
				)
			)
			.setBankId(payment.getC_Bank_ID())
			.setCustomer(
				POSConvertUtil.convertCustomer(
					(MBPartner) payment.getC_BPartner()
				)
			)
			.setCurrency(currencyBuilder)
			.setPaymentDate(ValueManager.getTimestampFromDate(payment.getDateTrx()))
			.setIsRefund(!payment.isReceipt())
			.setPaymentAccountDate(ValueManager.getTimestampFromDate(payment.getDateAcct()))
			.setDocumentStatus(
				ConvertUtil.convertDocumentStatus(ValueManager.validateNull(payment.getDocStatus()),
					ValueManager.validateNull(ValueManager.getTranslation(reference, I_AD_Ref_List.COLUMNNAME_Name)), 
					ValueManager.validateNull(ValueManager.getTranslation(reference, I_AD_Ref_List.COLUMNNAME_Description))
				)
			)
			.setPaymentMethod(paymentMethodBuilder)
			.setCharge(
				CoreFunctionalityConvert.convertCharge(
					payment.getC_Charge_ID()
				)
			)
			.setDocumentType(
				CoreFunctionalityConvert.convertDocumentType(
					payment.getC_DocType_ID()
				)
			)
			.setBankAccount(
				CoreFunctionalityConvert.convertBankAccount(
					payment.getC_BankAccount_ID()
				)
			)
			.setReferenceBankAccount(
				CoreFunctionalityConvert.convertBankAccount(
					payment.get_ValueAsInt("POSReferenceBankAccount_ID")
				)
			)
			.setIsProcessed(payment.isProcessed())
		;
		if(payment.getCollectingAgent_ID() > 0) {
			builder.setCollectingAgent(
				CoreFunctionalityConvert.convertSalesRepresentative(
					MUser.get(payment.getCtx(), payment.getCollectingAgent_ID())
				)
			);
		}
		return builder;
	}
}

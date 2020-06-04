/*************************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                              *
 * This program is free software; you can redistribute it and/or modify it    		 *
 * under the terms version 2 or later of the GNU General Public License as published *
 * by the Free Software Foundation. This program is distributed in the hope   		 *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 		 *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           		 *
 * See the GNU General Public License for more details.                       		 *
 * You should have received a copy of the GNU General Public License along    		 *
 * with this program; if not, write to the Free Software Foundation, Inc.,    		 *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     		 *
 * For the text or an alternative of this public license, you may reach us    		 *
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, S.A. All Rights Reserved. *
 * Contributor(s): Yamel Senih www.erpya.com				  		                 *
 *************************************************************************************/
package org.spin.grpc.util;

import java.util.Properties;

import org.compiere.model.MAttachment;
import org.compiere.model.MBPartner;
import org.compiere.model.MCharge;
import org.compiere.model.MClientInfo;
import org.compiere.model.MCountry;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MLanguage;
import org.compiere.model.MOrg;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategory;
import org.compiere.model.MUOM;
import org.compiere.model.MWarehouse;
import org.compiere.util.Env;
import org.compiere.util.MimeType;
import org.compiere.util.Util;
import org.spin.model.MADAttachmentReference;

/**
 * Class for convert any document
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class ConvertUtil {
	
	/**
	 * Convert Document Action
	 * @param value
	 * @param name
	 * @param description
	 * @return
	 */
	public static DocumentAction.Builder convertDocumentAction(String value, String name, String description) {
		return DocumentAction.newBuilder()
				.setValue(ValueUtil.validateNull(value))
				.setName(ValueUtil.validateNull(name))
				.setDescription(ValueUtil.validateNull(description));
	}
	
	/**
	 * Convert Document Status
	 * @param value
	 * @param name
	 * @param description
	 * @return
	 */
	public static DocumentStatus.Builder convertDocumentStatus(String value, String name, String description) {
		return DocumentStatus.newBuilder()
				.setValue(ValueUtil.validateNull(value))
				.setName(ValueUtil.validateNull(name))
				.setDescription(ValueUtil.validateNull(description));
	}
	
	/**
	 * Convert Document Type
	 * @param documentType
	 * @return
	 */
	public static DocumentType.Builder convertDocumentType(MDocType documentType) {
		return DocumentType.newBuilder()
				.setUuid(ValueUtil.validateNull(documentType.getUUID()))
				.setId(documentType.getC_DocType_ID())
				.setName(ValueUtil.validateNull(documentType.getName()))
				.setDescription(ValueUtil.validateNull(documentType.getDescription()))
				.setPrintName(ValueUtil.validateNull(documentType.getPrintName()));
	}
	
	/**
	 * Convert business partner
	 * @param businessPartner
	 * @return
	 */
	public static BusinessPartner.Builder convertBusinessPartner(MBPartner businessPartner) {
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
	 * Convert charge from 
	 * @param chargeId
	 * @return
	 */
	public static Charge.Builder convertCharge(MCharge charge) {
		Charge.Builder builder = Charge.newBuilder();
		if(charge == null) {
			return builder;
		}
		//	convert charge
		return builder
			.setUuid(ValueUtil.validateNull(charge.getUUID()))
			.setId(charge.getC_Charge_ID())
			.setName(ValueUtil.validateNull(charge.getName()))
			.setDescription(ValueUtil.validateNull(charge.getDescription()));
	}
	
	/**
	 * Convert Product to 
	 * @param product
	 * @return
	 */
	public static Product.Builder convertProduct(MProduct product) {
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
	
	/**
	 * Convert Language to gRPC
	 * @param language
	 * @return
	 */
	public static org.spin.grpc.util.Language.Builder convertLanguage(MLanguage language) {
		String datePattern = language.getDatePattern();
		String timePattern = language.getTimePattern();
		if(Util.isEmpty(datePattern)) {
			org.compiere.util.Language staticLanguage = org.compiere.util.Language.getLanguage(language.getAD_Language());
			if(staticLanguage != null) {
				datePattern = staticLanguage.getDateFormat().toPattern();
			}
			//	Validate
			if(Util.isEmpty(datePattern)) {
				datePattern = language.getDateFormat().toPattern();
			}
		}
		if(Util.isEmpty(timePattern)) {
			org.compiere.util.Language staticLanguage = org.compiere.util.Language.getLanguage(language.getAD_Language());
			if(staticLanguage != null) {
				timePattern = staticLanguage.getTimeFormat().toPattern();
			}
		}
		return org.spin.grpc.util.Language.newBuilder()
				.setLanguage(ValueUtil.validateNull(language.getAD_Language()))
				.setCountryCode(ValueUtil.validateNull(language.getCountryCode()))
				.setLanguageISO(ValueUtil.validateNull(language.getLanguageISO()))
				.setLanguageName(ValueUtil.validateNull(language.getName()))
				.setDatePattern(ValueUtil.validateNull(datePattern))
				.setTimePattern(ValueUtil.validateNull(timePattern))
				.setIsBaseLanguage(language.isBaseLanguage())
				.setIsSystemLanguage(language.isSystemLanguage())
				.setIsDecimalPoint(language.isDecimalPoint());
	}
	
	/**
	 * Convert Country
	 * @param context
	 * @param country
	 * @return
	 */
	public static Country.Builder convertCountry(Properties context, MCountry country) {
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
	public static Currency.Builder convertCurrency(MCurrency currency) {
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
	 * Convert organization
	 * @param organization
	 * @return
	 */
	public static Organization.Builder convertOrganization(MOrg organization) {
		MOrgInfo organizationInfo = MOrgInfo.get(Env.getCtx(), organization.getAD_Org_ID(), null);
		return Organization.newBuilder()
				.setUuid(ValueUtil.validateNull(organization.getUUID()))
				.setId(organization.getAD_Org_ID())
				.setName(ValueUtil.validateNull(organization.getName()))
				.setDescription(ValueUtil.validateNull(organization.getDescription()))
				.setDuns(ValueUtil.validateNull(organizationInfo.getDUNS()))
				.setTaxId(ValueUtil.validateNull(organizationInfo.getTaxID()))
				.setPhone(ValueUtil.validateNull(organizationInfo.getPhone()))
				.setPhone2(ValueUtil.validateNull(organizationInfo.getPhone2()))
				.setFax(ValueUtil.validateNull(organizationInfo.getFax()))
				.setIsReadOnly(false);
	}
	
	/**
	 * Convert warehouse
	 * @param warehouse
	 * @return
	 */
	public static Warehouse.Builder convertWarehouse(MWarehouse warehouse) {
		return Warehouse.newBuilder()
				.setUuid(ValueUtil.validateNull(warehouse.getUUID()))
				.setId(warehouse.getM_Warehouse_ID())
				.setName(ValueUtil.validateNull(warehouse.getName()))
				.setDescription(ValueUtil.validateNull(warehouse.getDescription()));
	}
	
	/**
	 * Convert resource
	 * @param reference
	 * @return
	 */
	public static ResourceReference.Builder convertResourceReference(MADAttachmentReference reference) {
		if(reference == null) {
			return ResourceReference.newBuilder();
		}
		return ResourceReference.newBuilder()
				.setResourceUuid(ValueUtil.validateNull(reference.getUUID()))
				.setFileName(ValueUtil.validateNull(reference.getFileName()))
				.setDescription(ValueUtil.validateNull(reference.getDescription()))
				.setTextMsg(ValueUtil.validateNull(reference.getTextMsg()))
				.setContentType(ValueUtil.validateNull(MimeType.getMimeType(reference.getFileName())))
				.setFileSize(ValueUtil.getDecimalFromBigDecimal(reference.getFileSize()));
		
	}
	
	/**
	 * Convert Attachment to gRPC
	 * @param attachment
	 * @return
	 */
	public static Attachment.Builder convertAttachment(MAttachment attachment) {
		if(attachment == null) {
			return Attachment.newBuilder();
		}
		Attachment.Builder builder = Attachment.newBuilder()
				.setAttachmentUuid(ValueUtil.validateNull(attachment.getUUID()))
				.setTitle(ValueUtil.validateNull(attachment.getTitle()))
				.setTextMsg(ValueUtil.validateNull(attachment.getTextMsg()));
		MClientInfo clientInfo = MClientInfo.get(attachment.getCtx());
		if(clientInfo.getFileHandler_ID() != 0) {
			MADAttachmentReference.getListByAttachmentId(attachment.getCtx(), clientInfo.getFileHandler_ID(), attachment.getAD_Attachment_ID(), attachment.get_TrxName())
				.forEach(attachmentReference -> builder.addResourceReferences(convertResourceReference(attachmentReference)));
		}
		return builder;
	}
}

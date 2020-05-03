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

import org.compiere.model.MDocType;

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
				.setPrinterName(ValueUtil.validateNull(documentType.getPrintName()));
	}
}

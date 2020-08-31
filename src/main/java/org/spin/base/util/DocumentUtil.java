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
package org.spin.base.util;

import org.compiere.process.DocAction;

/**
 * Class for handle records utils values
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class DocumentUtil {
	
	/**
	 * Verify if a document is completed
	 * @param document
	 * @return
	 */
	public static boolean isCompleted(DocAction document) {
		if(document == null) {
			return false;
		}
		//	
		return DocAction.STATUS_Completed.equals(document.getDocStatus());
	}
	
	/**
	 * Verify is is voided / reversed
	 * @param document
	 * @return
	 */
	public static boolean isVoided(DocAction document) {
		if(document == null) {
			return false;
		}
		//	
		return DocAction.STATUS_Voided.equals(document.getDocStatus())
				|| DocAction.STATUS_Reversed.equals(document.getDocStatus());
	}
	
	/**
	 * Validate if is drafted
	 * @return
	 * @return boolean
	 */
	public static boolean isDrafted(DocAction document) {
		if(document == null) {
			return false;
		}
		//	
		return !isCompleted(document) 
				&& !isVoided(document) 
				&& DocAction.STATUS_Drafted.equals(document.getDocStatus());
	}
}

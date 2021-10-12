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

import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;

/**
 * Class for handle records utils values
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class DictionaryUtil {
	
	/**
	 * Add references to original query from tab
	 * @param originalQuery
	 * @return
	 */
	public static String getQueryWithReferencesFromTab(MTab tab) {
		MTable table = MTable.get(Env.getCtx(), tab.getAD_Table_ID());
		String originalQuery = "SELECT " + table.getTableName() + ".* FROM " + table.getTableName() + " AS " + table.getTableName() + " ";
		int fromIndex = originalQuery.toUpperCase().indexOf(" FROM ");
		StringBuffer queryToAdd = new StringBuffer(originalQuery.substring(0, fromIndex));
		StringBuffer joinsToAdd = new StringBuffer(originalQuery.substring(fromIndex, originalQuery.length() - 1));
		Language language = Language.getLanguage(Env.getAD_Language(Env.getCtx()));
		for (MField field : tab.getFields(false, null)) {
			if(!field.isDisplayed()) {
				continue;
			}
			MColumn column = MColumn.get(Env.getCtx(), field.getAD_Column_ID());
			int displayTypeId = field.getAD_Reference_ID();
			if(displayTypeId == 0) {
				displayTypeId = column.getAD_Reference_ID();
			}
			if(DisplayType.isLookup(displayTypeId)) {
				//	Reference Value
				int referenceValueId = field.getAD_Reference_Value_ID();
				if(referenceValueId == 0) {
					referenceValueId = column.getAD_Reference_Value_ID();
				}
				//	Validation Code
				String columnName = column.getColumnName();
				String tableName = table.getTableName();
				queryToAdd.append(", ");
				ReferenceInfo referenceInfo = ReferenceUtil.getInstance(Env.getCtx()).getReferenceInfo(displayTypeId, referenceValueId, columnName, language.getAD_Language(), tableName);
				if(referenceInfo != null) {
					queryToAdd.append(referenceInfo.getDisplayValue(columnName));
					joinsToAdd.append(referenceInfo.getJoinValue(columnName, tableName));
				}
			}
		}
		queryToAdd.append(joinsToAdd);
		return queryToAdd.toString();
	}
}

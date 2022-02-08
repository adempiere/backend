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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.adempiere.model.MBrowse;
import org.adempiere.model.MBrowseField;
import org.adempiere.model.MViewColumn;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.spin.util.ASPUtil;

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
	
	/**
	 * Get Context column names from context
	 * @param context
	 * @return
	 * @return List<String>
	 */
	public static List<String> getContextColumnNames(String context) {
		if(context == null) {
			return new ArrayList<String>();
		}
		String START = "\\@";  // A literal "(" character in regex
		String END   = "\\@";  // A literal ")" character in regex

		// Captures the word(s) between the above two character(s)
		String patternValue = START + "(#|$){0,1}(\\w+)" + END;

		Pattern pattern = Pattern.compile(patternValue);
		Matcher matcher = pattern.matcher(context);
		Map<String, Boolean> columnNamesMap = new HashMap<String, Boolean>();
		while(matcher.find()) {
			columnNamesMap.put(matcher.group().replace("@", "").replace("@", ""), true);
		}
		return new ArrayList<String>(columnNamesMap.keySet());
	}
	
	/**
	 * Get SQL from View with a custom column as alias
	 * @param viewId
	 * @param columnNameForAlias
	 * @param trxName
	 * @return
	 */
	public static String getSQLFromBrowser(MBrowse browser) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT ");
		AtomicBoolean co = new AtomicBoolean(false);
		ASPUtil.getInstance().getBrowseDisplayFields(browser.getAD_Browse_ID()).forEach(field -> {
			if (co.get())
				sql.append(",");
			MViewColumn viewColumn = field.getAD_View_Column();
			if (viewColumn.getColumnSQL() != null
					&& viewColumn.getColumnSQL().length() > 0) {
				sql.append(viewColumn.getColumnSQL());
				co.set(true);
			}
			sql.append(" AS \"" + viewColumn.getColumnName() + "\"");
		});
		sql.append(" FROM ").append(browser.getAD_View().getFromClause());
		return sql.toString();
	}
	
	/**
	 * Add references to original query from smart browser
	 * @param originalQuery
	 * @return
	 */
	public static String addQueryReferencesFromBrowser(MBrowse browser) {
		String originalQuery = getSQLFromBrowser(browser);
		int fromIndex = originalQuery.toUpperCase().indexOf(" FROM ");
		StringBuffer queryToAdd = new StringBuffer(originalQuery.substring(0, fromIndex));
		StringBuffer joinsToAdd = new StringBuffer(originalQuery.substring(fromIndex, originalQuery.length() - 1));
		for (MBrowseField browseField : ASPUtil.getInstance().getBrowseDisplayFields(browser.getAD_Browse_ID())) {
			int displayTypeId = browseField.getAD_Reference_ID();
			if(DisplayType.isLookup(displayTypeId)) {
				//	Reference Value
				int referenceValueId = browseField.getAD_Reference_Value_ID();
				//	Validation Code
				String columnName = browseField.getAD_Element().getColumnName();
				String tableName = browseField.getAD_View_Column().getAD_View_Definition().getTableAlias();
				if(browseField.getAD_View_Column().getAD_Column_ID() > 0) {
					columnName = browseField.getAD_View_Column().getAD_Column().getColumnName();
				}
				queryToAdd.append(", ");
				ReferenceInfo referenceInfo = ReferenceUtil.getInstance(Env.getCtx()).getReferenceInfo(displayTypeId, referenceValueId, columnName, Env.getAD_Language(Env.getCtx()), tableName);
				if(referenceInfo != null) {
					queryToAdd.append(referenceInfo.getDisplayValue(browseField.getAD_View_Column().getColumnName()));
					joinsToAdd.append(referenceInfo.getJoinValue(columnName, tableName));
				}
			}
		}
		queryToAdd.append(joinsToAdd);
		return queryToAdd.toString();
	}
	
	/**
	 * Get Order By
	 * @param browser
	 * @return
	 */
	public static String getSQLOrderBy(MBrowse browser) {
		StringBuilder sqlOrderBy = new StringBuilder();
		for (MBrowseField field : ASPUtil.getInstance().getBrowseOrderByFields(browser.getAD_Browse_ID())) {
			if (sqlOrderBy.length() > 0) {
				sqlOrderBy.append(",");
			}
			sqlOrderBy.append(field.getAD_View_Column().getColumnSQL());
		}
		return sqlOrderBy.length() > 0 ? sqlOrderBy.toString(): "";
	}
	
	/**
	 * Get Order By Postirion for SB
	 * @param BrowserField
	 * @return
	 */
	public static int getOrderByPosition(MBrowse browser, MBrowseField BrowserField) {
		int colOffset = 1; // columns start with 1
		int col = 0;
		for (MBrowseField field : browser.getFields()) {
			int sortBySqlNo = col + colOffset;
			if (BrowserField.getAD_Browse_Field_ID() == field.getAD_Browse_Field_ID())
				return sortBySqlNo;
			col ++;
		}
		return -1;
	}
}

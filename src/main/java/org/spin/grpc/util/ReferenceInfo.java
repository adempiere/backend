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

import org.compiere.util.Language;
import org.compiere.util.Util;

/**
 * Class for store information about reference
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class ReferenceInfo {
	
	public ReferenceInfo() {
		displayColumnValue = "";
		displayColumnAlias = "";
		joinColumnName = "";
		tableName = "";
		tableAlias = "";
		columnName = "";
		referenceId = 0;
		hasJoinValue = false;
		language = Language.AD_Language_en_US;
	}
	/**	Display column Value: Test.Name	*/
	private String displayColumnValue;
	/**	Display column Alias: TestName	*/
	private String displayColumnAlias;
	/**	Column Name	*/
	private String columnName;
	/**	Join Column Name	*/
	private String joinColumnName;
	/**	Table Name	*/
	private String tableName;
	/**	Table Alias	*/
	private String tableAlias;
	/**	Language	*/
	private String language;
	/**	Reference ID	*/
	private int referenceId;
	/**	Has Join value	*/
	private boolean hasJoinValue;
	/**	Default Column And Table Alias	*/
	private final String DISPLAY_COLUMN_ALIAS = "DisplayColumn";
	
	public boolean isHasJoinValue() {
		return hasJoinValue;
	}

	public void setHasJoinValue(boolean hasJoinValue) {
		this.hasJoinValue = hasJoinValue;
	}

	public int getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public boolean isTranslated() {
		return !language.equals(Language.AD_Language_en_US);
	}

	public String getDisplayColumnValue() {
		return getDisplayColumnValue(false);
	}
	
	private String getDisplayColumnValue(boolean withTable) {
		if(withTable) {
			return getTableAlias(isTranslated()) + "." + displayColumnValue;
		}
		return displayColumnValue;
	}
	
	public void setDisplayColumnValue(String displayColumnValue) {
		this.displayColumnValue = displayColumnValue;
	}
	
	private String getDisplayColumnAlias() {
		return displayColumnAlias;
	}
	
	public void setDisplayColumnAlias(String displayColumnAlias) {
		this.displayColumnAlias = displayColumnAlias;
	}
	
	public String getJoinColumnName() {
		return getJoinColumnName(false);
	}
	
	private String getJoinColumnName(boolean withTable) {
		if(withTable) {
			return getTableAlias() + "." + joinColumnName;
		}
		return joinColumnName;
	}
	
	public void setJoinColumnName(String joinColumnName) {
		this.joinColumnName = joinColumnName;
		setHasJoinValue(true);
	}
	
	private String getTableName() {
		return getTableName(false);
	}
	
	private String getTableName(boolean translated) {
		if(translated) {
			return tableName + "_Trl";
		}
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	private String getTableAlias(boolean translated) {
		if(translated) {
			return tableAlias + "_Trl";
		}
		return tableAlias;
	}
	
	private String getTableAlias() {
		return getTableAlias(false);
	}
	
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}
	
	/**
	 * Without column name
	 */
	private void buildAlias() {
		buildAlias(null);
	}
	
	/**
	 * Create alias
	 */
	private void buildAlias(String columnName) {
		//	For table alias
		if(Util.isEmpty(tableAlias)) {
			setTableAlias(getColumnName() + "_" + getTableName());
		}
		//	For column alias
		if(Util.isEmpty(displayColumnAlias)) {
			if(Util.isEmpty(columnName)) {
				setDisplayColumnAlias(DISPLAY_COLUMN_ALIAS + "_" + getColumnName());
			} else {
				setDisplayColumnAlias(DISPLAY_COLUMN_ALIAS + "_" + columnName);
			}
		}
	}
	
	/**
	 * Get display value for Query
	 * @return
	 */
	public String getDisplayValue() {
		buildAlias();
		//	
		return getDisplayColumnValue(isHasJoinValue()) + " AS \"" + getDisplayColumnAlias() + "\"";
	}
	
	/**
	 * With custom column name
	 * @param columnName
	 * @return
	 */
	public String getDisplayValue(String columnName) {
		buildAlias(columnName);
		//	
		return getDisplayColumnValue(isHasJoinValue()) + " AS \"" + getDisplayColumnAlias() + "\"";
	}
	
	/**
	 * Get Join value for query
	 * @return
	 */
	public String getJoinValue(String baseColumnName, String baseTable) {
		buildAlias();
		if(!isHasJoinValue()) {
			return "";
		}
		StringBuffer join = new StringBuffer();
		join.append(" LEFT JOIN ")
			.append(getTableName()).append(" AS ").append(getTableAlias())
			.append(" ON(").append(getJoinColumnName(true)).append(" = ").append(baseTable).append(".").append(baseColumnName);
		//	Reference
		if(getReferenceId() > 0) {
			join.append(" AND ").append(getTableAlias() + ".AD_Reference_ID = ").append(getReferenceId());
		}
		join.append(")");
		//	Language
		if(isTranslated()) {
			join.append(" LEFT JOIN ")
				.append(getTableName(true)).append(" AS ").append(getTableAlias(true))
				.append(" ON(").append(getTableAlias(true) + "." + getTableName() + "_ID").append(" = ").append(getTableAlias() + "." + getTableName() + "_ID");
				join.append(" AND ").append(getTableAlias(true)).append(".").append("AD_Language = '").append(getLanguage()).append("'").append(")");
		}
		//	Return
		return join.toString();
	}

	@Override
	public String toString() {
		return "ReferenceInfo [displayColumnValue=" + displayColumnValue + ", displayColumnAlias=" + displayColumnAlias
				+ ", columnName=" + columnName + ", joinColumnName=" + joinColumnName + ", tableName=" + tableName
				+ ", tableAlias=" + tableAlias + ", language=" + language + ", referenceId=" + referenceId + "]";
	}
}

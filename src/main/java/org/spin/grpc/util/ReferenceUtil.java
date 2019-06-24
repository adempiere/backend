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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MTable;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;


/**
 * Class for handle reference for reports, Smart Browsers and other values with Table, List or Table Direct
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class ReferenceUtil {
	/**	Instance	*/
	private static ReferenceUtil instance = null;
	/**	Context	*/
	private Properties context;
	/**	Local cache	*/
	private Map<String, ReferenceInfo> referenceInfoMap;
	
	public static ReferenceUtil getInstance(Properties context) {
		if(instance == null) {
			instance = new ReferenceUtil(context);
		}
		return instance;
	}
	
	/**
	 * Private constructor
	 * @param context
	 */
	private ReferenceUtil(Properties context) {
		this.context = context;
		referenceInfoMap = new HashMap<String, ReferenceInfo>();
	}
	
	/**
	 * Validate reference
	 * @param referenceId
	 * @param referenceValueId
	 * @param columnName
	 * @param language
	 * @return
	 */
	public boolean validateReference(int referenceId, int referenceValueId, String columnName, String language) {
		if(!DisplayType.isLookup(referenceId)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get Reference information, can return null if reference is invalid or not exists
	 * @param referenceId
	 * @param referenceValueId
	 * @param columnName
	 * @param language
	 * @return
	 */
	public ReferenceInfo getReferenceInfo(int referenceId, int referenceValueId, String columnName, String language, String tableName) {
		if(!validateReference(referenceId, referenceValueId, columnName, language)) {
			return null;
		}
		String key = referenceId + "|" + referenceValueId + "|" + columnName + "|" + language;
		ReferenceInfo referenceInfo = referenceInfoMap.get(key);
		Language languageValue = Language.getLanguage(Env.getAD_Language(Env.getCtx()));
		if(referenceInfo == null) {
			if(DisplayType.TableDir == referenceId
					|| referenceValueId == 0) {
				//	Add Display
				referenceInfo = new ReferenceInfo();
				referenceInfo.setColumnName(columnName);
				referenceInfo.setDisplayColumnValue("(" + MLookupFactory.getLookup_TableDirEmbed(languageValue, columnName, tableName) + ")");
				referenceInfo.setHasJoinValue(false);
			} else {
				//	Get info
				MLookupInfo lookupInfo = MLookupFactory.getLookupInfo(context, 0, 0, referenceId, languageValue, columnName, referenceValueId, false, null, false);
				if(lookupInfo != null) {
					referenceInfo = new ReferenceInfo();
					referenceInfo.setColumnName(columnName);
					referenceInfo.setDisplayColumnValue((lookupInfo.DisplayColumn == null? "": lookupInfo.DisplayColumn).replace(lookupInfo.TableName + ".", ""));
					referenceInfo.setJoinColumnName((lookupInfo.KeyColumn == null? "": lookupInfo.KeyColumn).replace(lookupInfo.TableName + ".", ""));
					referenceInfo.setTableName(lookupInfo.TableName);
					if(DisplayType.List == referenceId
							&& referenceValueId != 0) {
						referenceInfo.setReferenceId(referenceValueId);
					}
					//	Translate
					if(MTable.hasTranslation(lookupInfo.TableName)) {
						referenceInfo.setLanguage(language);
					}
				}
			}
		}
		return referenceInfo;
	}
}

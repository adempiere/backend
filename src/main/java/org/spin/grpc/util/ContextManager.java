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

import java.sql.Timestamp;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_Session;
import org.compiere.model.MSession;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Util;

/**
 * Class for handle Context
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class ContextManager {
	
	/**	Session Context	*/
	private static CCache<String, Properties> sessionsContext = new CCache<String, Properties>("Session-gRPC-Service", 30, 0);	//	no time-out
	/**	Language */
	private static CCache<String, String> languageCache = new CCache<String, String>("Language-gRPC-Service", 30, 0);	//	no time-out
	/**
	 * Get context from session
	 * @param request
	 * @return
	 */
	public static Properties getContext(String sessionUuid, String language) {
		Properties context = sessionsContext.get(sessionUuid);
		if(context != null) {
			Env.setContext(context, Env.LANGUAGE, getDefaultLanguage(language));
			return context;
		}
		context = Env.getCtx();
		DB.validateSupportedUUIDFromDB();
		MSession session = new Query(context, I_AD_Session.Table_Name, I_AD_Session.COLUMNNAME_UUID + " = ?", null)
				.setParameters(sessionUuid)
				.first();
		if(session == null
				|| session.getAD_Session_ID() <= 0) {
			throw new AdempiereException("@AD_Session_ID@ @NotFound@");
		}
		Env.setContext (context, "#AD_Session_ID", session.getAD_Session_ID());
		Env.setContext(context, "#AD_User_ID", session.getCreatedBy());
		Env.setContext(context, "#AD_Role_ID", session.getAD_Role_ID());
		Env.setContext(context, "#AD_Client_ID", session.getAD_Client_ID());
		Env.setContext(context, "#AD_Org_ID", session.getAD_Org_ID());
		Env.setContext(context, "#Date", new Timestamp(System.currentTimeMillis()));
		Env.setContext(context, Env.LANGUAGE, getDefaultLanguage(language));
		//	Save to Cache
		sessionsContext.put(sessionUuid, context);
		return context;
	}
	
	/**
	 * Get Default from language
	 * @param language
	 * @return
	 */
	//	TODO: Change it for a class and reuse
	private static String getDefaultLanguage(String language) {
		String defaultLanguage = language;
		if(Util.isEmpty(language)) {
			language = Language.AD_Language_en_US;
		}
		//	Using es / en instead es_VE / en_US
		//	get default
		if(language.length() == 2) {
			defaultLanguage = languageCache.get(language);
			if(!Util.isEmpty(defaultLanguage)) {
				return defaultLanguage;
			}
			defaultLanguage = DB.getSQLValueString(null, "SELECT AD_Language "
					+ "FROM AD_Language "
					+ "WHERE LanguageISO = ? "
					+ "AND (IsSystemLanguage = 'Y' OR IsBaseLanguage = 'Y')", language);
		}
		if(Util.isEmpty(defaultLanguage)) {
			defaultLanguage = Language.AD_Language_en_US;
		}
		//	Default return
		return defaultLanguage;
	}
}

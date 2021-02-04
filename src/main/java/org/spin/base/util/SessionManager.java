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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MCountry;
import org.compiere.model.MRole;
import org.compiere.model.MSession;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Ini;
import org.compiere.util.Util;
import org.spin.model.MADToken;
import org.spin.model.MADTokenDefinition;
import org.spin.util.IThirdPartyAccessGenerator;
import org.spin.util.ITokenGenerator;
import org.spin.util.TokenGeneratorHandler;

/**
 * Class for handle Session for Third Party Access
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class SessionManager {
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(SessionManager.class);
	/**
	 * Load session from token
	 * @param tokenValue
	 */
	public static void createSessionFromToken(String tokenValue) {
		//	Get Session
		Properties context = Env.getCtx();
		//	Validate if is token based
		int userId = -1;
		int roleId = -1;
		int organizationId = -1;
		int warehouseId = -1;
		MADToken token = getsessionFromToken(tokenValue);
		if(Optional.ofNullable(token).isPresent()) {
			userId = token.getAD_User_ID();
			roleId = token.getAD_Role_ID();
			organizationId = token.getAD_Org_ID();
		}
		//	
		if(organizationId < 0) {
			organizationId = 0;
		}
		if(warehouseId < 0) {
			warehouseId = 0;
		}
		//	Get Values from role
		if(roleId < 0) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ / @AD_Org_ID@ @NotFound@");
		}
		MRole role = MRole.get(context, roleId);
		//	Warehouse / Org
		Env.setContext (context, "#M_Warehouse_ID", warehouseId);
		Env.setContext (context, "#AD_Session_ID", 0);
		//  Client Info
		MClient client = MClient.get(context, role.getAD_Client_ID());
		Env.setContext(context, "#AD_Client_ID", client.getAD_Client_ID());
		Env.setContext(context, "#AD_Org_ID", organizationId);
		//	Role Info
		Env.setContext(context, "#AD_Role_ID", roleId);
		//	User Info
		Env.setContext(context, "#AD_User_ID", userId);
		//	
		MSession session = MSession.get(context, true);
		Env.setContext (context, "#AD_Session_ID", session.getAD_Session_ID());
		Env.setContext (context, "#Session_UUID", session.getUUID());
		//	Load preferences
		loadDefaultSessionValues(context, null);
	}
	
	/**
	 * Get id of current session
	 * @return
	 */
	public static int getSessionId() {
		return Env.getContextAsInt(Env.getCtx(), "#AD_Session_ID");
	}
	
	/**
	 * Get uuid of current session
	 * @return
	 */
	public static String getSessionUuid() {
		return Env.getContext(Env.getCtx(), "#Session_UUID");
	}
	
	/**
	 * Get token object: validate it
	 * @param tokenValue
	 * @return
	 */
	public static MADToken getsessionFromToken(String tokenValue) {
		if(Util.isEmpty(tokenValue)) {
			throw new AdempiereException("@AD_Token_ID@ @NotFound@");
		}
		//	
		try {
			ITokenGenerator generator = TokenGeneratorHandler.getInstance().getTokenGenerator(MADTokenDefinition.TOKENTYPE_ThirdPartyAccess);
			if(generator == null) {
				throw new AdempiereException("@AD_TokenDefinition_ID@ @NotFound@");
			}
			//	No child of definition
			if(!IThirdPartyAccessGenerator.class.isAssignableFrom(generator.getClass())) {
				throw new AdempiereException("@AD_TokenDefinition_ID@ @Invalid@");	
			}
			//	Validate
			IThirdPartyAccessGenerator thirdPartyAccessGenerator = ((IThirdPartyAccessGenerator) generator);
			if(!thirdPartyAccessGenerator.validateToken(tokenValue)) {
				throw new AdempiereException("@Invalid@ @AD_Token_ID@");
			}
			//	Default
			MADToken token = thirdPartyAccessGenerator.getToken();
			return token;
		} catch (Exception e) {
			throw new AdempiereException(e);
		}
	}
	
	/**
	 * Load default values for session
	 * @param context
	 * @param language
	 */
	public static void loadDefaultSessionValues(Properties context, String language) {
		//  Client Info
		MClient client = MClient.get(context, Env.getContextAsInt(context, "#AD_Client_ID"));
		Env.setContext(context, "#AD_Client_Name", client.getName());
		Env.setContext(context, "#Date", new Timestamp(System.currentTimeMillis()));
		Env.setContext(context, Env.LANGUAGE, ContextManager.getDefaultLanguage(language));
		//	Role Info
		MRole role = MRole.get(context, Env.getContextAsInt(context, "#AD_Role_ID"));
		Env.setContext(context, "#AD_Role_Name", role.getName());
		//	User Info
		MUser user = MUser.get(context, Env.getContextAsInt(context, "#AD_User_ID"));
		Env.setContext(context, "#AD_User_Name", user.getName());
		Env.setContext(context, "#AD_User_Description", user.getDescription());
		//	Load preferences
		loadPreferences(context);
	}
	
	/**
	 * Get Default organization after login
	 * @param roleId
	 * @param userId
	 * @return
	 */
	public static int getDefaultOrganizationId(int roleId, int userId) {
		String organizationSQL = "SELECT o.AD_Org_ID "
				+ "FROM AD_Role r "
				+ "INNER JOIN AD_Client c ON(c.AD_Client_ID = r.AD_Client_ID) "
				+ "INNER JOIN AD_Org o ON(c.AD_Client_ID=o.AD_Client_ID OR o.AD_Org_ID=0) "
				+ "WHERE r.AD_Role_ID=? "
				+ " AND o.IsActive='Y' AND o.IsSummary='N' AND o.AD_Org_ID <> 0"
				+ " AND (r.IsAccessAllOrgs='Y' "
					+ "OR (r.IsUseUserOrgAccess='N' AND EXISTS(SELECT 1 FROM AD_Role_OrgAccess ra WHERE ra.AD_Org_ID = o.AD_Org_ID AND ra.AD_Role_ID = r.AD_Role_ID AND ra.IsActive='Y')) "
					+ "OR (r.IsUseUserOrgAccess='Y' AND EXISTS(SELECT 1 FROM AD_User_OrgAccess ua WHERE ua.AD_Org_ID = o.AD_Org_ID AND ua.AD_User_ID = ? AND ua.IsActive='Y'))"
					+ ") "
				+ "ORDER BY o.Name";
		return DB.getSQLValue(null, organizationSQL, roleId, userId);
	}
	
	/**
	 *	Load Preferences into Context for selected client.
	 *  <p>
	 *  Sets Org info in context and loads relevant field from
	 *	- AD_Client/Info,
	 *  - C_AcctSchema,
	 *  - C_AcctSchema_Elements
	 *	- AD_Preference
	 *  <p>
	 *  Assumes that the context is set for #AD_Client_ID, #AD_User_ID, #AD_Role_ID
	 *  @param context
	 *  @return AD_Message of error (NoValidAcctInfo) or ""
	 */
	private static void loadPreferences(Properties context) {
		if (context == null)
			throw new IllegalArgumentException("Required parameter missing");
		if (Env.getContext(context,"#AD_Client_ID").length() == 0)
			throw new UnsupportedOperationException("Missing Context #AD_Client_ID");
		if (Env.getContext(context,"#AD_User_ID").length() == 0)
			throw new UnsupportedOperationException("Missing Context #AD_User_ID");
		if (Env.getContext(context,"#AD_Role_ID").length() == 0)
			throw new UnsupportedOperationException("Missing Context #AD_Role_ID");
		//	Load Role Info
		MRole.getDefault(context, true);
		//	Other
		Env.setAutoCommit(context, Ini.isPropertyBool(Ini.P_A_COMMIT));
		Env.setAutoNew(context, Ini.isPropertyBool(Ini.P_A_NEW));
		if (MRole.getDefault(context, false).isShowAcct()) {
			Env.setContext(context, "#ShowAcct", Ini.getProperty(Ini.P_SHOW_ACCT));
		} else {
			Env.setContext(context, "#ShowAcct", "N");
		}
		Env.setContext(context, "#ShowTrl", Ini.getProperty(Ini.P_SHOW_TRL));
		Env.setContext(context, "#ShowAdvanced", Ini.getProperty(Ini.P_SHOW_ADVANCED));

		//	Other Settings
		Env.setContext(context, "#YYYY", "Y");
		Env.setContext(context, "#StdPrecision", 2);
		int clientId = Env.getAD_Client_ID(context);
		int orgId = Env.getAD_Org_ID(context);
		//	AccountSchema Info (first)
		String sql = "SELECT * "
			+ "FROM C_AcctSchema a, AD_ClientInfo c "
			+ "WHERE a.C_AcctSchema_ID=c.C_AcctSchema1_ID "
			+ "AND c.AD_Client_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			int acctSchemaId = 0;
			
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, clientId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				//	Accounting Info
				acctSchemaId = rs.getInt("C_AcctSchema_ID");
				Env.setContext(context, "$C_AcctSchema_ID", acctSchemaId);
				Env.setContext(context, "$C_Currency_ID", rs.getInt("C_Currency_ID"));
				Env.setContext(context, "$HasAlias", rs.getString("HasAlias"));
			}
			rs.close();
			pstmt.close();
			/**Define AcctSchema , Currency, HasAlias for Multi AcctSchema**/
			MAcctSchema[] ass = MAcctSchema.getClientAcctSchema(Env.getCtx(), clientId);
			if(ass != null && ass.length > 1) {
				for(MAcctSchema as : ass) {
					acctSchemaId  = MClientInfo.get(Env.getCtx(), clientId).getC_AcctSchema1_ID(); 			 
					if (as.getAD_OrgOnly_ID() != 0) {
						if (as.isSkipOrg(orgId)) {
							continue;
						} else {
							acctSchemaId = as.getC_AcctSchema_ID();
							Env.setContext(context, "$C_AcctSchema_ID", acctSchemaId);
							Env.setContext(context, "$C_Currency_ID", as.getC_Currency_ID());
							Env.setContext(context, "$HasAlias", as.isHasAlias());
							break;
						}
					}
				}
			}	

			//	Accounting Elements
			sql = "SELECT ElementType "
				+ "FROM C_AcctSchema_Element "
				+ "WHERE C_AcctSchema_ID=?"
				+ " AND IsActive='Y'";
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, acctSchemaId);
			rs = pstmt.executeQuery();
			while (rs.next())
				Env.setContext(context, "$Element_" + rs.getString("ElementType"), "Y");
			rs.close();
			pstmt.close();

			//	This reads all relevant window neutral defaults
			//	overwriting superseeded ones.  Window specific is read in Mainain
			sql = "SELECT Attribute, Value, AD_Window_ID "
				+ "FROM AD_Preference "
				+ "WHERE AD_Client_ID IN (0, @#AD_Client_ID@)"
				+ " AND AD_Org_ID IN (0, @#AD_Org_ID@)"
				+ " AND (AD_User_ID IS NULL OR AD_User_ID=0 OR AD_User_ID=@#AD_User_ID@)"
				+ " AND IsActive='Y' "
				+ "ORDER BY Attribute, AD_Client_ID, AD_User_ID DESC, AD_Org_ID";
				//	the last one overwrites - System - Client - User - Org - Window
			sql = Env.parseContext(context, 0, sql, false);
			if (sql.length() == 0) {
				log.log(Level.SEVERE, "loadPreferences - Missing Environment");
			} else {
				pstmt = DB.prepareStatement(sql, null);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int AD_Window_ID = rs.getInt(3);
					String at = "";
					if (rs.wasNull())
						at = "P|" + rs.getString(1);
					else
						at = "P" + AD_Window_ID + "|" + rs.getString(1);
					String va = rs.getString(2);
					Env.setContext(context, at, va);
				}
				rs.close();
				pstmt.close();
			}

			//	Default Values
			log.info("Default Values ...");
			sql = "SELECT t.TableName, c.ColumnName "
				+ "FROM AD_Column c "
				+ " INNER JOIN AD_Table t ON (c.AD_Table_ID=t.AD_Table_ID) "
				+ "WHERE c.IsKey='Y' AND t.IsActive='Y'"
				+ " AND EXISTS (SELECT * FROM AD_Column cc "
				+ " WHERE ColumnName = 'IsDefault' AND t.AD_Table_ID=cc.AD_Table_ID AND cc.IsActive='Y')";
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				loadDefault (context, rs.getString(1), rs.getString(2));
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "loadPreferences", e);
		} finally {
			DB.close(rs, pstmt);
		}
		//	Country
		MCountry country = ContextManager.getDefaultCountry();
		if(country != null) {
			Env.setContext(context, "#C_Country_ID", country.getC_Country_ID());
		}
		// Call ModelValidators afterLoadPreferences - teo_sarca FR [ 1670025 ]
		ModelValidationEngine.get().afterLoadPreferences(context);
	}	//	loadPreferences
	
	/**
	 *	Load Default Value for Table into Context.
	 *  @param tableName table name
	 *  @param columnName column name
	 */
	private static void loadDefault (Properties context, String tableName, String columnName) {
		if (tableName.startsWith("AD_Window")
			|| tableName.startsWith("AD_PrintFormat")
			|| tableName.startsWith("AD_Workflow") )
			return;
		String value = null;
		//
		String sql = "SELECT " + columnName + " FROM " + tableName	//	most specific first
			+ " WHERE IsDefault='Y' AND IsActive='Y' ORDER BY AD_Client_ID DESC, AD_Org_ID DESC";
		sql = MRole.getDefault(Env.getCtx(), false).addAccessSQL(sql, 
			tableName, MRole.SQL_NOTQUALIFIED, MRole.SQL_RO);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			if (rs.next())
				value = rs.getString(1);
			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			log.log(Level.SEVERE, tableName + " (" + sql + ")", e);
			return;
		} finally {
			DB.close(rs, pstmt);
		}
		//	Set Context Value
		if (value != null && value.length() != 0)
		{
			if (tableName.equals("C_DocType"))
				Env.setContext(context, "#C_DocTypeTarget_ID", value);
			else
				Env.setContext(context, "#" + columnName, value);
		}
	}	//	loadDefault
}

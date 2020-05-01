/************************************************************************************
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                     *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program.	If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.grpc.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.I_AD_Browse_Access;
import org.adempiere.model.MBrowse;
import org.adempiere.model.X_AD_Browse_Access;
import org.compiere.model.I_AD_Column_Access;
import org.compiere.model.I_AD_Document_Action_Access;
import org.compiere.model.I_AD_Form_Access;
import org.compiere.model.I_AD_Menu;
import org.compiere.model.I_AD_Process_Access;
import org.compiere.model.I_AD_Record_Access;
import org.compiere.model.I_AD_Role;
import org.compiere.model.I_AD_Role_OrgAccess;
import org.compiere.model.I_AD_Session;
import org.compiere.model.I_AD_Table_Access;
import org.compiere.model.I_AD_Task_Access;
import org.compiere.model.I_AD_Window_Access;
import org.compiere.model.I_AD_Workflow_Access;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MColumn;
import org.compiere.model.MColumnAccess;
import org.compiere.model.MCountry;
import org.compiere.model.MDocType;
import org.compiere.model.MForm;
import org.compiere.model.MFormAccess;
import org.compiere.model.MMenu;
import org.compiere.model.MOrg;
import org.compiere.model.MProcess;
import org.compiere.model.MProcessAccess;
import org.compiere.model.MRecordAccess;
import org.compiere.model.MRole;
import org.compiere.model.MRoleOrgAccess;
import org.compiere.model.MSession;
import org.compiere.model.MTable;
import org.compiere.model.MTableAccess;
import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.compiere.model.MUser;
import org.compiere.model.MWindow;
import org.compiere.model.MWindowAccess;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.Query;
import org.compiere.model.X_AD_Document_Action_Access;
import org.compiere.model.X_AD_Table_Access;
import org.compiere.model.X_AD_Task_Access;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Ini;
import org.compiere.util.Language;
import org.compiere.util.Login;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWorkflowAccess;
import org.spin.grpc.util.SecurityGrpc.SecurityImplBase;
import org.spin.grpc.util.TableAccess.AccessTypeRule;
import org.spin.model.I_AD_Dashboard_Access;
import org.spin.model.X_AD_Dashboard_Access;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Access service
 */
public class AccessServiceImplementation extends SecurityImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(AccessServiceImplementation.class);
	/**	Session Context	*/
	private static CCache<String, Properties> sessionsContext = new CCache<String, Properties>("AccessServiceImplementation", 30, 0);	//	no time-out
	/**	Language */
	private static CCache<String, String> languageCache = new CCache<String, String>("Language_ISO_Code", 30, 0);	//	no time-out
	/**	Menu */
	private static CCache<String, Menu.Builder> menuCache = new CCache<String, Menu.Builder>("Menu_for_User", 30, 0);
	
	
	@Override
	public void getUserInfo(LoginRequest request, StreamObserver<UserInfoValue> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("User Role Requested = " + request.getUserName());
			UserInfoValue.Builder userInfoValue = convertUserInfo(request);
			responseObserver.onNext(userInfoValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void runLogin(LoginRequest request, StreamObserver<Session> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Session Requested = " + request.getUserName());
			Session.Builder sessionBuilder = createSession(request, false);
			responseObserver.onNext(sessionBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void runLogout(LogoutRequest request, StreamObserver<Session> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Session Requested = " + request.getSessionUuid());
			Session.Builder sessionBuilder = logoutSession(request);
			responseObserver.onNext(sessionBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void getSession(SessionRequest request, StreamObserver<Session> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Session Requested = " + request.getSessionUuid());
			Session.Builder sessionBuilder = getSessionInfo(request);
			responseObserver.onNext(sessionBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void runLoginDefault(LoginRequest request, StreamObserver<Session> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Session Requested = " + request.getUserName());
			Session.Builder sessionBuilder = createSession(request, true);
			responseObserver.onNext(sessionBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void getUserInfoFromSession(UserInfoRequest request, StreamObserver<UserInfoValue> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("User Info Requested = " + request.getClientVersion());
			UserInfoValue.Builder userInfoValue = convertUserInfoFromSession(request);
			responseObserver.onNext(userInfoValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void getMenuAndChild(UserInfoRequest request, StreamObserver<Menu> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Menu Requested = " + request.getClientVersion());
			Properties context = getContext(request);
			String language = getDefaultLanguage(request.getLanguage());
			Menu.Builder menuBuilder = convertMenu(context, language);
			responseObserver.onNext(menuBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void runChangeRole(UserInfoRequest request, StreamObserver<Session> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Change Role Requested = " + request.getRoleUuid());
			Session.Builder sessionBuilder = changeRole(request);
			responseObserver.onNext(sessionBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getMessage())
					.augmentDescription(e.getMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	/**
	 * Get context from session
	 * @param request
	 * @return
	 */
	private Properties getContext(UserInfoRequest request) {
		Properties context = sessionsContext.get(request.getSessionUuid());
		if(context != null) {
			Env.setContext(context, Env.LANGUAGE, getDefaultLanguage(request.getLanguage()));
			return context;
		}
		context = Env.getCtx();
		DB.validateSupportedUUIDFromDB();
		MSession session = new Query(context, I_AD_Session.Table_Name, I_AD_Session.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getSessionUuid())
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
		Env.setContext(context, Env.LANGUAGE, getDefaultLanguage(request.getLanguage()));
		//	Save to Cache
		sessionsContext.put(request.getSessionUuid(), context);
		return context;
	}
	
	/**
	 * Get Default from language
	 * @param language
	 * @return
	 */
	//	TODO: Change it for a class and reuse
	private String getDefaultLanguage(String language) {
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
	
	/**
	 * Get User ID
	 * @param userName
	 * @param userPass
	 * @return
	 */
	private int getUserId(String userName, String userPass) {
		Login login = new Login(Env.getCtx());
		return login.getAuthenticatedUserId(userName, userPass);
	}
	
	/**
	 * Get and convert session
	 * @param request
	 * @return
	 */
	private Session.Builder createSession(LoginRequest request, boolean isDefaultRole) {
		Session.Builder builder = Session.newBuilder();
		DB.validateSupportedUUIDFromDB();
		//	Get Session
		Properties context = Env.getCtx();
		int userId = getUserId(request.getUserName(), request.getUserPass());
		//	Get Values from role
		if(userId < 0) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ / @AD_Org_ID@ @NotFound@");
		}
		int roleId = -1;
		int organizationId = -1;
		int warehouseId = -1;
		if(isDefaultRole) {
			roleId = DB.getSQLValue(null, "SELECT ur.AD_Role_ID "
					+ "FROM AD_User_Roles ur "
					+ "WHERE ur.AD_User_ID = ? AND ur.IsActive = 'Y' "
					+ "ORDER BY COALESCE(ur.IsDefault,'N') DESC", userId);
			//	Organization
			String organizationSQL = "SELECT o.AD_Org_ID "
					+ "FROM AD_Role r "
					+ "INNER JOIN AD_Client c ON(c.AD_Client_ID = r.AD_Client_ID) "
					+ "INNER JOIN AD_Org o ON(c.AD_Client_ID=o.AD_Client_ID OR o.AD_Org_ID=0) "
					+ "WHERE r.AD_Role_ID=? "
					+ " AND o.IsActive='Y' AND o.IsSummary='N'"
					+ " AND (r.IsAccessAllOrgs='Y' "
						+ "OR (r.IsUseUserOrgAccess='N' AND EXISTS(SELECT 1 FROM AD_Role_OrgAccess ra WHERE ra.AD_Org_ID = o.AD_Org_ID AND ra.AD_Role_ID = r.AD_Role_ID AND ra.IsActive='Y')) "
						+ "OR (r.IsUseUserOrgAccess='Y' AND EXISTS(SELECT 1 FROM AD_User_OrgAccess ua WHERE ua.AD_Org_ID = o.AD_Org_ID AND ua.AD_User_ID = ? AND ua.IsActive='Y'))"
						+ ") "
					+ "ORDER BY o.Name";
			organizationId = DB.getSQLValue(null, organizationSQL, roleId, userId);
			warehouseId = DB.getSQLValue(null, "SELECT M_Warehouse_ID FROM M_Warehouse WHERE IsActive = 'Y' AND AD_Org_ID = ?", organizationId);
		} else {
			roleId = DB.getSQLValue(null, "SELECT AD_Role_ID FROM AD_Role WHERE UUID = ?", request.getRoleUuid());
			organizationId = DB.getSQLValue(null, "SELECT AD_Org_ID FROM AD_Org WHERE UUID = ?", request.getOrganizationUuid());
			warehouseId = DB.getSQLValue(null, "SELECT M_Warehouse_ID FROM M_Warehouse WHERE UUID = ?", request.getWarehouseUuid());
		}
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
		MUser user = MUser.get(context, userId);
		//	Warehouse / Org
		Env.setContext (context, "#M_Warehouse_ID", warehouseId);
		Env.setContext (context, "#AD_Session_ID", 0);
		//  Client Info
		MClient client = MClient.get(context, role.getAD_Client_ID());
		Env.setContext(context, "#AD_Client_ID", client.getAD_Client_ID());
		Env.setContext(context, "#AD_Client_Name", client.getName());
		Env.setContext(context, "#AD_Org_ID", organizationId);
		Env.setContext(context, "#Date", new Timestamp(System.currentTimeMillis()));
		Env.setContext(context, Env.LANGUAGE, request.getLanguage());
		//	Role Info
		Env.setContext(context, "#AD_Role_ID", roleId);
		Env.setContext(context, "#AD_Role_Name", role.getName());
		//	User Info
		Env.setContext(context, "#AD_User_ID", userId);
		Env.setContext(context, "#AD_User_Name", user.getName());
		Env.setContext(context, "#AD_User_Description", user.getDescription());
		//	
		MSession session = MSession.get(context, true);
		if(!Util.isEmpty(request.getClientVersion())) {
			session.setWebSession(request.getClientVersion());
		}
		Env.setContext (context, "#AD_Session_ID", session.getAD_Session_ID());
		//	Load preferences
		loadPreferences(context);
		//	Session values
		builder.setId(session.getAD_Session_ID());
		builder.setUuid(ValueUtil.validateNull(session.getUUID()));
		builder.setName(ValueUtil.validateNull(session.getDescription()));
		builder.setUserInfo(convertUserInfo(MUser.get(Env.getCtx(), userId)).build());
		//	Set role
		Role.Builder roleBuilder = convertRole(role, false);
		builder.setRole(roleBuilder.build());
		//	Set default context
		context.entrySet().stream()
			.filter(keyValue -> String.valueOf(keyValue.getKey()).startsWith("#") || String.valueOf(keyValue.getKey()).startsWith("$"))
			.forEach(contextKeyValue -> {
				builder.putDefaultContext(contextKeyValue.getKey().toString(), convertObjectFromContext(contextKeyValue.getValue()).build());
			});
		//	Return session
		return builder;
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
	public void loadPreferences(Properties context) {
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
		Env.setContext(context, "#C_Country_ID", MCountry.getDefault(context).getC_Country_ID());
		// Call ModelValidators afterLoadPreferences - teo_sarca FR [ 1670025 ]
		ModelValidationEngine.get().afterLoadPreferences(context);
	}	//	loadPreferences
	
	/**
	 *	Load Default Value for Table into Context.
	 *  @param tableName table name
	 *  @param columnName column name
	 */
	private void loadDefault (Properties context, String tableName, String columnName) {
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
	
	/**
	 * Convert Values from Context
	 * @param value
	 * @return
	 */
	private ContextValue.Builder convertObjectFromContext(Object value) {
		ContextValue.Builder builder = ContextValue.newBuilder();
		if(value == null) {
			return builder;
		}
		if(value instanceof Integer) {
			builder.setValueType(ContextValue.ValueType.INTEGER);
			builder.setIntValue((Integer) value);
		} if(value instanceof Double
				|| value instanceof Float) {
			builder.setValueType(ContextValue.ValueType.DOUBLE);
			builder.setDoubleValue((Double) value);
		} if(value instanceof BigDecimal) {
			builder.setValueType(ContextValue.ValueType.DOUBLE);
			builder.setDoubleValue(((BigDecimal) value).doubleValue());
		} if(value instanceof Timestamp) {
			builder.setValueType(ContextValue.ValueType.DATE);
			builder.setLongValue(((Timestamp) value).getTime());
		} if(value instanceof Long) {
			builder.setValueType(ContextValue.ValueType.LONG);
			builder.setLongValue((Long) value);
		} else {
			builder.setValueType(ContextValue.ValueType.STRING);
			builder.setStringValue(ValueUtil.validateNull((String) value));
		}
		//	
		return builder;
	}
	
	/**
	 * Change current role
	 * @param request
	 * @return
	 */
	private Session.Builder changeRole(UserInfoRequest request) {
		Session.Builder builder = Session.newBuilder();
		DB.validateSupportedUUIDFromDB();
		//	Get / Validate Session
		Properties context = getContext(request);
		MSession currentSession = MSession.get(context, false);
		int userId = currentSession.getCreatedBy();
		int roleId = -1;
		int organizationId = -1;
		int warehouseId = -1;
		roleId = DB.getSQLValue(null, "SELECT AD_Role_ID FROM AD_Role WHERE UUID = ?", request.getRoleUuid());
		organizationId = DB.getSQLValue(null, "SELECT AD_Org_ID FROM AD_Org WHERE UUID = ?", request.getOrganizationUuid());
		warehouseId = DB.getSQLValue(null, "SELECT M_Warehouse_ID FROM M_Warehouse WHERE UUID = ?", request.getWarehouseUuid());
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
		MUser user = MUser.get(context, userId);
		Env.setContext (context, "#AD_Session_ID", 0);
		Env.setContext(context, "#AD_User_ID", userId);
		Env.setContext(context, "#AD_Role_ID", roleId);
		Env.setContext(context, "#AD_Client_ID", role.getAD_Client_ID());
		Env.setContext(context, "#AD_Org_ID", organizationId);
		Env.setContext(context, "#Date", new Timestamp(System.currentTimeMillis()));
		Env.setContext(context, Env.LANGUAGE, getDefaultLanguage(request.getLanguage()));
		MSession session = MSession.get(context, true);
		if(!Util.isEmpty(request.getClientVersion())) {
			session.setWebSession(request.getClientVersion());
		}
		//	Warehouse / Org
		Env.setContext (context, "#M_Warehouse_ID", warehouseId);
		Env.setContext (context, "#AD_Session_ID", session.getAD_Session_ID());
		//  Client Info
		MClient client = MClient.get(context, role.getAD_Client_ID());
		Env.setContext(context, "#AD_Client_ID", client.getAD_Client_ID());
		Env.setContext(context, "#AD_Client_Name", client.getName());
		Env.setContext(context, "#AD_Org_ID", organizationId);
		Env.setContext(context, "#Date", new Timestamp(System.currentTimeMillis()));
		Env.setContext(context, Env.LANGUAGE, request.getLanguage());
		//	Role Info
		Env.setContext(context, "#AD_Role_ID", roleId);
		Env.setContext(context, "#AD_Role_Name", role.getName());
		//	User Info
		Env.setContext(context, "#AD_User_ID", userId);
		Env.setContext(context, "#AD_User_Name", user.getName());
		Env.setContext(context, "#AD_User_Description", user.getDescription());
		//	Load preferences
		loadPreferences(context);
		//	Session values
		builder.setId(session.getAD_Session_ID());
		builder.setUuid(ValueUtil.validateNull(session.getUUID()));
		builder.setName(ValueUtil.validateNull(session.getDescription()));
		builder.setUserInfo(convertUserInfo(MUser.get(Env.getCtx(), userId)).build());
		//	Set default context
		context.entrySet().stream()
			.filter(keyValue -> String.valueOf(keyValue.getKey()).startsWith("#") || String.valueOf(keyValue.getKey()).startsWith("$"))
			.forEach(contextKeyValue -> {
				builder.putDefaultContext(contextKeyValue.getKey().toString(), convertObjectFromContext(contextKeyValue.getValue()).build());
			});
		//	Set role
		Role.Builder roleBuilder = convertRole(role, false);
		builder.setRole(roleBuilder.build());
		//	Logout
		LogoutRequest logoutRequest = LogoutRequest.newBuilder()
				.setSessionUuid(request.getSessionUuid())
				.setClientVersion(request.getClientVersion())
				.setLanguage(request.getLanguage())
				.build();
		logoutSession(logoutRequest);
		//	Return session
		return builder;
	}
	
	
	/**
	 * Logout session
	 * @param request
	 * @return
	 */
	private Session.Builder logoutSession(LogoutRequest request) {
		Session.Builder builder = Session.newBuilder();
		//	Get Session
		if(Util.isEmpty(request.getSessionUuid())) {
			throw new AdempiereException("@AD_Session_ID@ @NotFound@");
		}
		Properties context = Env.getCtx();
		MSession session = new Query(context, I_AD_Session.Table_Name, I_AD_Session.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getSessionUuid())
				.first();
		if(session == null
				|| session.getAD_Session_ID() <= 0) {
			throw new AdempiereException("@AD_Session_ID@ @NotFound@");
		}
		//	Logout
		session.logout();
		//	Session values
		builder.setId(session.getAD_Session_ID());
		builder.setUuid(ValueUtil.validateNull(session.getUUID()));
		builder.setName(ValueUtil.validateNull(session.getDescription()));
		builder.setUserInfo(convertUserInfo(MUser.get(context, session.getCreatedBy())).build());
		//	Return session
		return builder;
	}
	
	/**
	 * Logout session
	 * @param request
	 * @return
	 */
	private Session.Builder getSessionInfo(SessionRequest request) {
		Session.Builder builder = Session.newBuilder();
		//	Get Session
		if(Util.isEmpty(request.getSessionUuid())) {
			throw new AdempiereException("@AD_Session_ID@ @NotFound@");
		}
		Properties context = Env.getCtx();
		MSession session = new Query(context, I_AD_Session.Table_Name, I_AD_Session.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getSessionUuid())
				.first();
		if(session == null
				|| session.getAD_Session_ID() <= 0) {
			throw new AdempiereException("@AD_Session_ID@ @NotFound@");
		}
		//	Session values
		builder.setId(session.getAD_Session_ID());
		builder.setUuid(ValueUtil.validateNull(session.getUUID()));
		builder.setName(ValueUtil.validateNull(session.getDescription()));
		builder.setUserInfo(convertUserInfo(MUser.get(context, session.getCreatedBy())).build());
		//	Set role
		Role.Builder roleBuilder = convertRole(MRole.get(context, session.getAD_Role_ID()), false);
		builder.setRole(roleBuilder.build());
		//	Set default context
		context.entrySet().stream()
			.filter(keyValue -> String.valueOf(keyValue.getKey()).startsWith("#") || String.valueOf(keyValue.getKey()).startsWith("$"))
			.forEach(contextKeyValue -> {
				builder.putDefaultContext(contextKeyValue.getKey().toString(), convertObjectFromContext(contextKeyValue.getValue()).build());
			});
		//	Return session
		return builder;
	}
	
	/**
	 * Convert User entity
	 * @param user
	 * @return
	 */
	private UserInfo.Builder convertUserInfo(MUser user) {
		UserInfo.Builder userInfo = UserInfo.newBuilder();
		userInfo.setId(user.getAD_User_ID());
		userInfo.setUuid(ValueUtil.validateNull(user.getUUID()));
		userInfo.setName(ValueUtil.validateNull(user.getName()));
		userInfo.setDescription(ValueUtil.validateNull(user.getDescription()));
		userInfo.setComments(ValueUtil.validateNull(user.getComments()));
		return userInfo;
	}
	
	/**
	 * Convert User Roles
	 * @param request
	 * @return
	 */
	private UserInfoValue.Builder convertUserInfo(LoginRequest request) {
		if(Util.isEmpty(request.getUserName())
				|| Util.isEmpty(request.getUserPass())) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ / @AD_Org_ID@ @NotFound@");
		}
		int userId = getUserId(request.getUserName(), request.getUserPass());
		if(userId < 0) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ / @AD_Org_ID@ @NotFound@");
		}
		List<MRole> roleList = new Query(Env.getCtx(), I_AD_Role.Table_Name, 
				"EXISTS(SELECT 1 FROM AD_User_Roles ur "
				+ "WHERE ur.AD_Role_ID = AD_Role.AD_Role_ID "
				+ "AND ur.AD_User_ID = ?)", null)
				.setParameters(userId)
				.setOnlyActiveRecords(true)
				.<MRole>list();
		//	Validate
		if(roleList == null
				|| roleList.size() == 0) {
			return null;
		}
		//	Iterate for it
		UserInfoValue.Builder builder = UserInfoValue.newBuilder();
		builder.setUserInfo(convertUserInfo(MUser.get(Env.getCtx(), userId)).build());
		for(MRole role : roleList) {
			Role.Builder roleBuilder = convertRole(role, false);
			builder.addRoles(roleBuilder.build());
		}
		//	Return
		return builder;
	}
	
	
	/**
	 * Convert User Roles
	 * @param request
	 * @return
	 */
	private UserInfoValue.Builder convertUserInfoFromSession(UserInfoRequest request) {
		if(Util.isEmpty(request.getSessionUuid())) {
			throw new AdempiereException("@AD_Session_ID@ @NotFound@");
		}
		MSession session = new Query(Env.getCtx(), I_AD_Session.Table_Name, I_AD_Session.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getSessionUuid())
				.first();
		if(session == null
				|| session.getAD_Session_ID() <= 0) {
			throw new AdempiereException("@AD_Session_ID@ @NotFound@");
		}
		List<MRole> roleList = new Query(Env.getCtx(), I_AD_Role.Table_Name, 
				"EXISTS(SELECT 1 FROM AD_User_Roles ur "
				+ "WHERE ur.AD_Role_ID = AD_Role.AD_Role_ID "
				+ "AND ur.AD_User_ID = ?)", null)
				.setParameters(session.getCreatedBy())
				.setOnlyActiveRecords(true)
				.<MRole>list();
		//	Validate
		if(roleList == null
				|| roleList.size() == 0) {
			return null;
		}
		//	Iterate for it
		UserInfoValue.Builder builder = UserInfoValue.newBuilder();
		MUser user = MUser.get(Env.getCtx(), session.getCreatedBy());
		if(user == null
				|| user.getAD_User_ID() <= 0) {
			throw new AdempiereException("@AD_User_ID@ @NotFound@");
		}
		//	Set User Information
		builder.setUserInfo(convertUserInfo(MUser.get(Env.getCtx(), session.getCreatedBy())).build());
		//	Set Role List
		for(MRole role : roleList) {
			Role.Builder roleBuilder = convertRole(role, false);
			builder.addRoles(roleBuilder.build());
		}
		//	Return
		return builder;
	}
	
	/**
	 * Convert role from model class
	 * @param role
	 * @param withAccess
	 * @return
	 */
	private Role.Builder convertRole(MRole role, boolean withAccess) {
		Role.Builder builder = null;
		//	Validate
		if(role != null) {
			MClient client = MClient.get(Env.getCtx(), role.getAD_Client_ID());
			builder = Role.newBuilder()
					.setId(role.getAD_Role_ID())
					.setUuid(ValueUtil.validateNull(role.getUUID()))
					.setName(ValueUtil.validateNull(role.getName()))
					.setDescription(ValueUtil.validateNull(role.getDescription()))
					.setClientName(ValueUtil.validateNull(client.getName()))
					.setClientId(role.getAD_Client_ID())
					.setIsCanExport(role.isCanExport())
					.setIsCanReport(role.isCanReport())
					.setIsPersonalAccess(role.isPersonalAccess())
					.setIsPersonalLock(role.isPersonalLock());
			//	With Access
			// TODO: load from other service
			if(withAccess) {
				//	Org Access
				List<MRoleOrgAccess> orgAccessList = new Query(Env.getCtx(), I_AD_Role_OrgAccess.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MRoleOrgAccess>list();
				for(MRoleOrgAccess access : orgAccessList) {
					MOrg organization = MOrg.get(Env.getCtx(), access.getAD_Org_ID());
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(ValueUtil.validateNull(organization.getUUID()))
							.setIsReadOnly(access.isReadOnly());
					builder.addOrganizations(accessBuilder.build());
				}
				//	Process Access
				List<MProcessAccess> processAccessList = new Query(Env.getCtx(), I_AD_Process_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MProcessAccess>list();
				for(MProcessAccess access : processAccessList) {
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(ValueUtil.validateNull(access.getAD_Process().getUUID()))
							.setIsReadOnly(!access.isReadWrite());
					builder.addProcess(accessBuilder.build());
				}
				//	Window Access
				List<MWindowAccess> windowAccessList = new Query(Env.getCtx(), I_AD_Window_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MWindowAccess>list();
				for(MWindowAccess access : windowAccessList) {
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(ValueUtil.validateNull(access.getAD_Window().getUUID()))
							.setIsReadOnly(!access.isReadWrite());
					builder.addWindows(accessBuilder.build());
				}
				//	Form Access
				List<MFormAccess> formAccessList = new Query(Env.getCtx(), I_AD_Form_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MFormAccess>list();
				for(MFormAccess access : formAccessList) {
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(ValueUtil.validateNull(access.getAD_Form().getUUID()))
							.setIsReadOnly(!access.isReadWrite());
					builder.addForms(accessBuilder.build());
				}
				//	Browse Access
				List<X_AD_Browse_Access> browseAccessList = new Query(Env.getCtx(), I_AD_Browse_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<X_AD_Browse_Access>list();
				for(X_AD_Browse_Access access : browseAccessList) {
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(ValueUtil.validateNull(access.getAD_Browse().getUUID()))
							.setIsReadOnly(!access.isReadWrite());
					builder.addBrowsers(accessBuilder.build());
				}
				//	Task Access
				List<X_AD_Task_Access> taskAccessList = new Query(Env.getCtx(), I_AD_Task_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<X_AD_Task_Access>list();
				for(X_AD_Task_Access access : taskAccessList) {
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(ValueUtil.validateNull(access.getAD_Task().getUUID()))
							.setIsReadOnly(!access.isReadWrite());
					builder.addTasks(accessBuilder.build());
				}
				//	Dashboard Access
				List<X_AD_Dashboard_Access> dashboardAccessList = new Query(Env.getCtx(), I_AD_Dashboard_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<X_AD_Dashboard_Access>list();
				for(X_AD_Dashboard_Access access : dashboardAccessList) {
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(ValueUtil.validateNull(access.getPA_DashboardContent().getUUID()));
					builder.addDashboards(accessBuilder.build());
				}
				//	Workflow Access
				List<MWorkflowAccess> workflowAccessList = new Query(Env.getCtx(), I_AD_Workflow_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MWorkflowAccess>list();
				for(MWorkflowAccess access : workflowAccessList) {
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(ValueUtil.validateNull(access.getAD_Workflow().getUUID()))
							.setIsReadOnly(!access.isReadWrite());
					builder.addWorkflows(accessBuilder.build());
				}
				//	Document Action Access
				List<X_AD_Document_Action_Access> documentActionAccessList = new Query(Env.getCtx(), I_AD_Document_Action_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<X_AD_Document_Action_Access>list();
				for(X_AD_Document_Action_Access access : documentActionAccessList) {
					MDocType documentType = MDocType.get(Env.getCtx(), access.getC_DocType_ID());
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(ValueUtil.validateNull(documentType.getUUID()))
							.setAction(access.getAD_Ref_List().getValue());
					builder.addDocumentActions(accessBuilder.build());
				}
				//	Table Access
				List<MTableAccess> tableAccessList = new Query(Env.getCtx(), I_AD_Table_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MTableAccess>list();
				for(MTableAccess access : tableAccessList) {
					AccessTypeRule accessTypeRule = AccessTypeRule.ACCESSING;
					if(access.getAccessTypeRule().equals(X_AD_Table_Access.ACCESSTYPERULE_Exporting)) {
						accessTypeRule = AccessTypeRule.EXPORTING;
					} else if(access.getAccessTypeRule().equals(X_AD_Table_Access.ACCESSTYPERULE_Reporting)) {
						accessTypeRule = AccessTypeRule.REPORTING;
					}
					TableAccess.Builder accessBuilder = TableAccess.newBuilder()
							.setTableName(ValueUtil.validateNull(access.getAD_Table().getTableName()))
							.setIsExclude(access.isExclude())
							.setIsCanReport(access.isCanReport())
							.setIsCanExport(access.isCanExport())
							.setAccessTypeRules(accessTypeRule);
					builder.addTables(accessBuilder.build());
				}
				//	Column Access
				List<MColumnAccess> columnAccessList = new Query(Env.getCtx(), I_AD_Column_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MColumnAccess>list();
				for(MColumnAccess access : columnAccessList) {
					ColumnAccess.Builder accessBuilder = ColumnAccess.newBuilder()
							.setIsExclude(access.isExclude())
							.setIsReadOnly(access.isReadOnly());
					//	For Table
					if(access.getAD_Table_ID() > 0) {
						MTable table = MTable.get(Env.getCtx(), access.getAD_Table_ID());
						accessBuilder.setTableName(table.getTableName());
					}
					//	For Column
					if(access.getAD_Column_ID() > 0) {
						MColumn column = MColumn.get(Env.getCtx(), access.getAD_Column_ID());
						accessBuilder.setColumnName(column.getColumnName());
					}
					builder.addColumns(accessBuilder.build());
				}
				//	Record Access
				List<MRecordAccess> recordAccessList = new Query(Env.getCtx(), I_AD_Record_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MRecordAccess>list();
				for(MRecordAccess access : recordAccessList) {
					RecordAccess.Builder accessBuilder = RecordAccess.newBuilder()
							.setIsReadOnly(access.isReadOnly())
							.setIsExclude(access.isExclude())
							.setIsDependentEntities(access.isDependentEntities())
							.setRecordId(access.getRecord_ID());
					//	For Table
					if(access.getAD_Table_ID() > 0) {
						MTable table = MTable.get(Env.getCtx(), access.getAD_Table_ID());
						accessBuilder.setTableName(table.getTableName());
					}
					builder.addRecords(accessBuilder.build());
				}
			}
		}
		//	return
		return builder;
	}
	
	/**
	 * Convert Menu
	 * @param context
	 * @param language
	 * @return
	 */
	private Menu.Builder convertMenu(Properties context, String language) {
		int roleId = Env.getAD_Role_ID(context);
		int userId = Env.getAD_User_ID(context);
		String menuKey = roleId + "|" + userId + "|" + language;
		Menu.Builder builder = menuCache.get(menuKey);
		if(builder != null) {
			return builder;
		}
		builder = Menu.newBuilder();
		MMenu menu = new MMenu(context, 0, null);
		menu.setName(Msg.getMsg(context, "Menu"));
		//	Get Reference
		int treeId = DB.getSQLValue(null,
			"SELECT COALESCE(r.AD_Tree_Menu_ID, ci.AD_Tree_Menu_ID)" 
			+ "FROM AD_ClientInfo ci" 
			+ " INNER JOIN AD_Role r ON (ci.AD_Client_ID=r.AD_Client_ID) "
			+ "WHERE AD_Role_ID=?", roleId);
		if (treeId <= 0) {
			treeId = MTree.getDefaultTreeIdFromTableId(menu.getAD_Client_ID(), I_AD_Menu.Table_ID);
		}
		if(treeId != 0) {
			MTree tree = new MTree(Env.getCtx(), treeId, false, false, null, null);
			//	
			builder = convertMenu(context, menu, 0, language);
			//	Get main node
			MTreeNode rootNode = tree.getRoot();
			Enumeration<?> childrens = rootNode.children();
			while (childrens.hasMoreElements()) {
				MTreeNode child = (MTreeNode)childrens.nextElement();
				Menu.Builder childBuilder = convertMenu(context, MMenu.getFromId(context, child.getNode_ID()), child.getParent_ID(), language);
				//	Explode child
				addChildren(context, childBuilder, child, language);
				builder.addChilds(childBuilder.build());
			}
		}
		//	Set from DB
		menuCache.put(menuKey, builder);
		return builder;
	}
	
	/**
	 * Convert Menu to builder
	 * @param context
	 * @param menu
	 * @param parentId
	 * @param language
	 * @param withChild
	 * @return
	 */
	private Menu.Builder convertMenu(Properties context, MMenu menu, int parentId, String language) {
		String name = null;
		String description = null;
		if(!Util.isEmpty(language)) {
			name = menu.get_Translation(I_AD_Menu.COLUMNNAME_Name, language);
			description = menu.get_Translation(I_AD_Menu.COLUMNNAME_Description, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = menu.getName();
		}
		if(Util.isEmpty(description)) {
			description = menu.getDescription();
		}
		String parentUuid = null;
		if(parentId > 0) {
			parentUuid = MMenu.getFromId(context, parentId).getUUID();
		}
		Menu.Builder builder = Menu.newBuilder()
				.setId(menu.getAD_Menu_ID())
				.setUuid(ValueUtil.validateNull(menu.getUUID()))
				.setName(ValueUtil.validateNull(name))
				.setDescription(ValueUtil.validateNull(description))
				.setAction(ValueUtil.validateNull(menu.getAction()))
				.setIsSOTrx(menu.isSOTrx())
				.setIsSummary(menu.isSummary())
				.setIsReadOnly(menu.isReadOnly())
				.setIsActive(menu.isActive())
				.setParentUuid(ValueUtil.validateNull(parentUuid));
		//	Supported actions
		if(!Util.isEmpty(menu.getAction())) {
			String referenceUuid = null;
			if(menu.getAction().equals(MMenu.ACTION_Form)) {
				if(menu.getAD_Form_ID() > 0) {
					MForm form = new MForm(context, menu.getAD_Form_ID(), null);
					referenceUuid = form.getUUID();
				}
			} else if(menu.getAction().equals(MMenu.ACTION_Window)) {
				if(menu.getAD_Window_ID() > 0) {
					MWindow window = new MWindow(context, menu.getAD_Window_ID(), null);
					referenceUuid = window.getUUID();
				}
			} else if(menu.getAction().equals(MMenu.ACTION_Process)
				|| menu.getAction().equals(MMenu.ACTION_Report)) {
				if(menu.getAD_Process_ID() > 0) {
					MProcess process = MProcess.get(context, menu.getAD_Process_ID());
					referenceUuid = process.getUUID();
				}
			} else if(menu.getAction().equals(MMenu.ACTION_SmartBrowse)) {
				if(menu.getAD_Browse_ID() > 0) {
					MBrowse smartBrowser = MBrowse.get(context, menu.getAD_Browse_ID());
					referenceUuid = smartBrowser.getUUID();
				}
			}
			builder.setReferenceUuid(ValueUtil.validateNull(referenceUuid));
		}
		return builder;
	}
	
	/**
	 * Add children to menu
	 * @param context
	 * @param builder
	 * @param node
	 * @param language
	 */
	private void addChildren(Properties context, Menu.Builder builder, MTreeNode node, String language) {
		Enumeration<?> childrens = node.children();
		while (childrens.hasMoreElements()) {
			MTreeNode child = (MTreeNode)childrens.nextElement();
			Menu.Builder childBuilder = convertMenu(context, MMenu.getFromId(context, child.getNode_ID()), child.getParent_ID(), language);
			addChildren(context, childBuilder, child, language);
			builder.addChilds(childBuilder.build());
		}
	}
}

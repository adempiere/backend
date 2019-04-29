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

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.I_AD_Browse_Access;
import org.adempiere.model.X_AD_Browse_Access;
import org.compiere.model.I_AD_Column_Access;
import org.compiere.model.I_AD_Document_Action_Access;
import org.compiere.model.I_AD_Form_Access;
import org.compiere.model.I_AD_Process_Access;
import org.compiere.model.I_AD_Record_Access;
import org.compiere.model.I_AD_Role;
import org.compiere.model.I_AD_Role_OrgAccess;
import org.compiere.model.I_AD_Session;
import org.compiere.model.I_AD_Table_Access;
import org.compiere.model.I_AD_Task_Access;
import org.compiere.model.I_AD_Window_Access;
import org.compiere.model.I_AD_Workflow_Access;
import org.compiere.model.MColumn;
import org.compiere.model.MColumnAccess;
import org.compiere.model.MDocType;
import org.compiere.model.MFormAccess;
import org.compiere.model.MOrg;
import org.compiere.model.MProcessAccess;
import org.compiere.model.MRecordAccess;
import org.compiere.model.MRole;
import org.compiere.model.MRoleOrgAccess;
import org.compiere.model.MSession;
import org.compiere.model.MTable;
import org.compiere.model.MTableAccess;
import org.compiere.model.MWindowAccess;
import org.compiere.model.Query;
import org.compiere.model.X_AD_Document_Action_Access;
import org.compiere.model.X_AD_Table_Access;
import org.compiere.model.X_AD_Task_Access;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Login;
import org.compiere.util.Util;
import org.compiere.wf.MWorkflowAccess;
import org.spin.grpc.util.AccessServiceGrpc.AccessServiceImplBase;
import org.spin.grpc.util.TableAccess.AccessTypeRule;
import org.spin.model.I_AD_Dashboard_Access;
import org.spin.model.X_AD_Dashboard_Access;

import io.grpc.stub.StreamObserver;

public class AccessServiceImplementation extends AccessServiceImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(AccessServiceImplementation.class);

	@Override
	public void requestUserRoles(RoleRequest request, StreamObserver<UserRoles> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("User Role Requested = " + request.getUserName());
			UserRoles.Builder userRoles = convertUserRoles(request);
			responseObserver.onNext(userRoles.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestLogin(LoginRequest request, StreamObserver<Session> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Session Requested = " + request.getUserName());
			Session.Builder sessionBuilder = createSession(request);
			responseObserver.onNext(sessionBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestLogout(LogoutRequest request, StreamObserver<Session> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Session Requested = " + request.getSessionUuid());
			Session.Builder sessionBuilder = logoutSession(request);
			responseObserver.onNext(sessionBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
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
	private Session.Builder createSession(LoginRequest request) {
		Session.Builder builder = Session.newBuilder();
		//	Get Session
		Properties context = Env.getCtx();
		int userId = getUserId(request.getUserName(), request.getUserPass());
		int roleId = DB.getSQLValue(null, "SELECT AD_Role_ID FROM AD_Role WHERE UUID = ?", request.getRoleUuid());
		int organizationId = DB.getSQLValue(null, "SELECT AD_Org_ID FROM AD_Org WHERE UUID = ?", request.getOrganizationUuid());
		if(organizationId < 0) {
			organizationId = 0;
		}
		//	Get Values from role
		if(userId < 0
				|| roleId < 0) {
			throw new AdempiereException("@AD_User_ID@ / @AD_Role_ID@ / @AD_Org_ID@ @NotFound@");
		}
		MRole role = MRole.get(context, roleId);
		Env.setContext (context, "#AD_Session_ID", 0);
		Env.setContext(context, "#AD_User_ID", userId);
		Env.setContext(context, "#AD_Role_ID", roleId);
		Env.setContext(context, "#AD_Client_ID", role.getAD_Client_ID());
		Env.setContext(context, "#AD_Org_ID", organizationId);
		Env.setContext(context, "#Date", new Timestamp(System.currentTimeMillis()));
		Env.setContext(context, Env.LANGUAGE, request.getLanguage());
		MSession session = MSession.get(context, true);
		if(!Util.isEmpty(request.getClientVersion())) {
			session.setWebSession(request.getClientVersion());
		}
		session.saveEx();
		//	Session values
		builder.setId(session.getAD_Session_ID());
		builder.setUuid(validateNull(session.getUUID()));
		builder.setName(validateNull(session.getDescription()));
		//	Set role
		Role.Builder roleBuilder = convertRole(role, true);
		builder.setRole(roleBuilder.build());
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
		builder.setUuid(validateNull(session.getUUID()));
		builder.setName(validateNull(session.getDescription()));
		//	Return session
		return builder;
	}
	
	/**
	 * Convert User Roles
	 * @param request
	 * @return
	 */
	private UserRoles.Builder convertUserRoles(RoleRequest request) {
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
		UserRoles.Builder builder = UserRoles.newBuilder();
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
			builder = Role.newBuilder()
					.setId(role.getAD_Role_ID())
					.setUuid(validateNull(role.getUUID()))
					.setName(validateNull(role.getName()))
					.setDescription(validateNull(role.getDescription()));
			//	With Access
			if(withAccess) {
				//	Org Access
				List<MRoleOrgAccess> orgAccessList = new Query(Env.getCtx(), I_AD_Role_OrgAccess.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MRoleOrgAccess>list();
				for(MRoleOrgAccess access : orgAccessList) {
					MOrg organization = MOrg.get(Env.getCtx(), access.getAD_Org_ID());
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(validateNull(organization.getUUID()))
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
							.setUuid(validateNull(access.getAD_Process().getUUID()))
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
							.setUuid(validateNull(access.getAD_Window().getUUID()))
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
							.setUuid(validateNull(access.getAD_Form().getUUID()))
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
							.setUuid(validateNull(access.getAD_Browse().getUUID()))
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
							.setUuid(validateNull(access.getAD_Task().getUUID()))
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
							.setUuid(validateNull(access.getPA_DashboardContent().getUUID()));
					builder.addDashboards(accessBuilder.build());
				}
				//	Workflow Access
				List<MWorkflowAccess> workflowAccessList = new Query(Env.getCtx(), I_AD_Workflow_Access.Table_Name, "AD_Role_ID = ?", null)
					.setParameters(role.getAD_Role_ID())
					.setOnlyActiveRecords(true)
					.<MWorkflowAccess>list();
				for(MWorkflowAccess access : workflowAccessList) {
					Access.Builder accessBuilder = Access.newBuilder()
							.setUuid(validateNull(access.getAD_Workflow().getUUID()))
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
							.setUuid(validateNull(documentType.getUUID()))
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
							.setTableName(validateNull(access.getAD_Table().getTableName()))
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
	 * Convert null on ""
	 * @param value
	 * @return
	 */
	private String validateNull(String value) {
		if(value == null) {
			value = "";
		}
		//	
		return value;
	}
}

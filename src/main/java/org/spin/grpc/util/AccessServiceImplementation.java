package org.spin.grpc.util;

import java.util.List;

import org.adempiere.model.I_AD_Browse_Access;
import org.adempiere.model.X_AD_Browse_Access;
import org.compiere.model.I_AD_Column_Access;
import org.compiere.model.I_AD_Document_Action_Access;
import org.compiere.model.I_AD_Form_Access;
import org.compiere.model.I_AD_Process_Access;
import org.compiere.model.I_AD_Record_Access;
import org.compiere.model.I_AD_Role;
import org.compiere.model.I_AD_Role_OrgAccess;
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
import org.compiere.model.MTable;
import org.compiere.model.MTableAccess;
import org.compiere.model.MWindowAccess;
import org.compiere.model.Query;
import org.compiere.model.X_AD_Document_Action_Access;
import org.compiere.model.X_AD_Table_Access;
import org.compiere.model.X_AD_Task_Access;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
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
	public void requestUserRoles(UserRequest request, StreamObserver<UserRoles> responseObserver) {
		if(request == null) {
			log.fine("Object Request Null");
			return;
		}
		log.fine("User Role Requested = " + request.getUserName());
		UserRoles.Builder userRoles = convertUserRoles(request.getUserName());
		try {
			responseObserver.onNext(userRoles.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestRole(ObjectRequest request, StreamObserver<Role> responseObserver) {
		if(request == null) {
			log.fine("Object Request Null");
			return;
		}
		log.fine("Role Requested = " + request.getUuid());
		Role.Builder roleBuilder = convertRole(request.getUuid());
		try {
			responseObserver.onNext(roleBuilder.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	
	/**
	 * Convert Role from UUID
	 * @param uuid
	 * @param language
	 * @return
	 */
	private Role.Builder convertRole(String uuid) {
		MRole role = new Query(Env.getCtx(), I_AD_Role.Table_Name, I_AD_Role.COLUMNNAME_UUID + " = ?", null)
				.setParameters(uuid)
				.setOnlyActiveRecords(true)
				.first();
		return convertRole(role, true);
	}
	
	/**
	 * Convert User Roles
	 * @param userName
	 * @param language
	 * @return
	 */
	private UserRoles.Builder convertUserRoles(String userName) {
		if(Util.isEmpty(userName)) {
			return null;
		}
		//	TODO: Validate Access
		List<MRole> roleList = new Query(Env.getCtx(), I_AD_Role.Table_Name, 
				"EXISTS(SELECT 1 FROM AD_User_Roles ur "
				+ "INNER JOIN AD_User u ON(u.AD_User_ID = ur.AD_User_ID) "
				+ "WHERE ur.AD_Role_ID = AD_Role.AD_Role_ID "
				+ "AND u.Value = ?)", null)
				.setParameters(userName)
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

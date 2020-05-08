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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_Language;
import org.compiere.model.I_AD_Org;
import org.compiere.model.I_AD_Role;
import org.compiere.model.I_C_Country;
import org.compiere.model.I_M_Warehouse;
import org.compiere.model.MCountry;
import org.compiere.model.MLanguage;
import org.compiere.model.MOrg;
import org.compiere.model.MRole;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.grpc.util.CoreFunctionalityGrpc.CoreFunctionalityImplBase;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * https://itnext.io/customizing-grpc-generated-code-5909a2551ca1
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Core functionality
 */
public class CoreFunctionalityImplementation extends CoreFunctionalityImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(CoreFunctionalityImplementation.class);
	/**	Country */
	private static CCache<String, MCountry> countryCache = new CCache<String, MCountry>(I_C_Country.Table_Name + "_UUID", 30, 0);	//	no time-out
	
	@Override
	public void listOrganizations(ListOrganizationsRequest request,
			StreamObserver<ListOrganizationsResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListOrganizationsResponse.Builder organizationsList = convertOrganizationsList(context, request);
			responseObserver.onNext(organizationsList.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.augmentDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}

	@Override
	public void listWarehouses(ListWarehousesRequest request, StreamObserver<ListWarehousesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListWarehousesResponse.Builder organizationsList = convertWarehousesList(context, request);
			responseObserver.onNext(organizationsList.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.augmentDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void getCountry(GetCountryRequest request, StreamObserver<Country> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Country Request Null");
			}
			log.fine("Country Requested = " + request.getCountryUuid());
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			Country.Builder country = getCountry(context, request);
			responseObserver.onNext(country.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			responseObserver.onError(Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.augmentDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException());
		}
	}
	
	@Override
	public void listLanguages(ListLanguagesRequest request, StreamObserver<ListLanguagesResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			Properties context = ContextManager.getContext(request.getClientRequest().getSessionUuid(), request.getClientRequest().getLanguage(), request.getClientRequest().getOrganizationUuid(), request.getClientRequest().getWarehouseUuid());
			ListLanguagesResponse.Builder languagesList = convertLanguagesList(context, request);
			responseObserver.onNext(languagesList.build());
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
	 * Convert a Country
	 * @param context
	 * @param request
	 * @return
	 */
	private Country.Builder getCountry(Properties context, GetCountryRequest request) {
		String key = null;
		MCountry country = null;
		if(Util.isEmpty(request.getCountryUuid()) && request.getCountryId() == 0) {
			key = "Default";
			country = countryCache.get(key);
			if(country == null) {
				country = MCountry.getDefault(context);
			}
		}
		//	By UUID
		if(!Util.isEmpty(request.getCountryUuid())
				&& country == null) {
			key = request.getCountryUuid();
			country = countryCache.put(key, country);
			if(country == null) {
				country = new Query(context, I_C_Country.Table_Name, I_C_Country.COLUMNNAME_UUID + " = ?", null).first();
			}
		}
		if(request.getCountryId() != 0
				&& country == null) {
			key = "ID:|" + request.getCountryId();
			country = countryCache.put(key, country);
			if(country == null) {
				country = MCountry.get(context, request.getCountryId());
			}
		}
		if(country != null) {
			countryCache.put(key, country);
		}
		//	Return
		return ConvertUtil.convertCountry(context, country);
	}
	
	/**
	 * Convert languages to gRPC
	 * @param context
	 * @param request
	 * @return
	 */
	private ListLanguagesResponse.Builder convertLanguagesList(Properties context, ListLanguagesRequest request) {
		ListLanguagesResponse.Builder builder = ListLanguagesResponse.newBuilder();
		new Query(context, I_AD_Language.Table_Name, "(IsSystemLanguage=? OR IsBaseLanguage=?)", null)
			.setParameters(true, true)
			.setOnlyActiveRecords(true)
			.<MLanguage>list()
			.forEach(language -> {
				builder.addLanguages(ConvertUtil.convertLanguage(language));
			});
		//	Return
		return builder;
	}
	
	/**
	 * Convert Organization to list
	 * @param request
	 * @return
	 */
	private ListOrganizationsResponse.Builder convertOrganizationsList(Properties context, ListOrganizationsRequest request) {
		ListOrganizationsResponse.Builder builder = ListOrganizationsResponse.newBuilder();
		List<Object> parameters = new ArrayList<Object>();
		String whereClause = "AD_Client_ID = " + Env.getAD_Client_ID(context);
		MRole role = null;
		if(request.getRoleId() != 0) {
			role = MRole.get(context, request.getRoleId());
		} else if(!Util.isEmpty(request.getRoleUuid())) {
			role = new Query(context, I_AD_Role.Table_Name, I_AD_Role.COLUMNNAME_UUID + " = ?", null)
					.setParameters(request.getRoleUuid())
					.setOnlyActiveRecords(true)
					.first();
		}
		//	get from role access
		if(role != null) {
			if(role.isUseUserOrgAccess()) {
				whereClause = "EXISTS(SELECT 1 FROM AD_User_OrgAccess ua "
						+ "WHERE ua.AD_Org_ID = AD_Org.AD_Org_ID "
						+ "AND ua.AD_User_ID = ? "
						+ "AND ua.IsActive = 'Y')";
				parameters.add(Env.getAD_User_ID(context));
			} else {
				whereClause = "EXISTS(SELECT 1 FROM AD_Role_OrgAccess ra "
						+ "WHERE ra.AD_Org_ID = AD_Org.AD_Org_ID "
						+ "AND ra.AD_Role_ID = ? "
						+ "AND ra.IsActive = 'Y')";
				parameters.add(role.getAD_Role_ID());
			}
		}
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		Query query = new Query(context, I_AD_Org.Table_Name, whereClause, null)
				.setParameters(parameters)
				.setOnlyActiveRecords(true)
				.setLimit(limit, offset);
		//	Count
		int count = query.count();
		//	Get List
		query.<MOrg>list()
			.forEach(organization -> {
				builder.addOrganizations(ConvertUtil.convertOrganization(organization));
			});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set netxt page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
	
	/**
	 * Convert warehouses list
	 * @param context
	 * @param request
	 * @return
	 */
	private ListWarehousesResponse.Builder convertWarehousesList(Properties context, ListWarehousesRequest request) {
		ListWarehousesResponse.Builder builder = ListWarehousesResponse.newBuilder();
		//	Get page and count
		String nexPageToken = null;
		int pageNumber = RecordUtil.getPageNumber(request.getClientRequest().getSessionUuid(), request.getPageToken());
		int offset = (pageNumber > 0? pageNumber - 1: 0) * RecordUtil.PAGE_SIZE;
		int limit = (pageNumber == 0? 1: pageNumber) * RecordUtil.PAGE_SIZE;
		Query query = new Query(context, I_M_Warehouse.Table_Name, "EXISTS(SELECT 1 FROM AD_Org o WHERE o.AD_Org_ID = M_Warehouse.AD_Org_ID AND o.UUID = ?)", null)
				.setOnlyActiveRecords(true)
				.setParameters(request.getOrganizationUuid())
				.setLimit(limit, offset);
		//	Count
		int count = query.count();
		//	Get List
		query.<MWarehouse>list()
			.forEach(warehouse -> {
				builder.addWarehouses(ConvertUtil.convertWarehouse(warehouse));
			});
		//	
		builder.setRecordCount(count);
		//	Set page token
		if(count > limit) {
			nexPageToken = RecordUtil.getPagePrefix(request.getClientRequest().getSessionUuid()) + (pageNumber + 1);
		}
		//	Set netxt page
		builder.setNextPageToken(ValueUtil.validateNull(nexPageToken));
		return builder;
	}
}

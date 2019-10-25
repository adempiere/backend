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

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_User;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MMailText;
import org.compiere.model.MUser;
import org.compiere.model.MUserMail;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.EMail;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.grpc.util.EnrollmentServiceGrpc.EnrollmentServiceImplBase;
import org.spin.grpc.util.ResetPasswordResponse.ResponseType;
import org.spin.model.I_AD_Token;
import org.spin.model.MADToken;
import org.spin.model.MADTokenDefinition;
import org.spin.util.TokenGeneratorHandler;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class EnrollmentServiceImplementation extends EnrollmentServiceImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(EnrollmentServiceImplementation.class);
	
	
	@Override
	public void enrollUser(EnrollUserRequest request, StreamObserver<User> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getUserName() + " - " + request.getEMail());
			User.Builder userInfoValue = convertUser(request);
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
	public void resetPassword(ResetPasswordRequest request, StreamObserver<ResetPasswordResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getUserName() + " - " + request.getEMail());
			ResetPasswordResponse.Builder passwordResetReponse = resetPassword(request);
			responseObserver.onNext(passwordResetReponse.build());
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
	public void resetPasswordFromToken(ResetPasswordTokenRequest request,
			StreamObserver<ResetPasswordResponse> responseObserver) {
		try {
			if(request == null) {
				throw new AdempiereException("Object Request Null");
			}
			log.fine("Object Requested = " + request.getToken());
			ResetPasswordResponse.Builder passwordResetReponse = resetPasswordFromToken(request);
			responseObserver.onNext(passwordResetReponse.build());
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
	 * Reset password
	 * @param request
	 * @return
	 */
	private ResetPasswordResponse.Builder resetPasswordFromToken(ResetPasswordTokenRequest request) {
		//	User Name
		if(Util.isEmpty(request.getToken())
				&& Util.isEmpty(request.getPassword())) {
			throw new AdempiereException("@Token@ / @Password@ @IsMandatory@");
		}
		ResetPasswordResponse.Builder builder = ResetPasswordResponse.newBuilder();
		MADToken token = new Query(Env.getCtx(), I_AD_Token.Table_Name, I_AD_Token.COLUMNNAME_TokenValue + " = ?", null)
			.setParameters(request.getToken())
			.first();
		if(token == null
				|| token.getAD_Token_ID() == 0) {
			builder.setResponseType(ResponseType.TOKEN_NOT_FOUND);
			throw new AdempiereException("@Token@ @NotFound@");
		}
		//	Generate reset
		try {
			if(!TokenGeneratorHandler.getInstance().validateToken(MADTokenDefinition.TOKENTYPE_URLTokenUsedAsURL, request.getToken(), token.getAD_User_ID())) {
				throw new AdempiereException("@Token@ @NotFound@");
			}
			MUser user = MUser.get(Env.getCtx(), token.getAD_User_ID());
			user.setPassword(request.getPassword());
			user.saveEx();
		} catch (Exception e) {
			builder.setResponseType(ResponseType.ERROR);
			throw new AdempiereException(e.getMessage());
		}
		builder.setResponseType(ResponseType.OK);
		return builder;
	}
	
	/**
	 * Reset password
	 * @param request
	 * @return
	 */
	private ResetPasswordResponse.Builder resetPassword(ResetPasswordRequest request) {
		//	User Name
		if(Util.isEmpty(request.getUserName())
				&& Util.isEmpty(request.getEMail())) {
			throw new AdempiereException("@UserName@ / @EMail@ @IsMandatory@");
		}
		ResetPasswordResponse.Builder builder = ResetPasswordResponse.newBuilder();
		MUser user = new Query(Env.getCtx(), I_AD_User.Table_Name, "Value = ? OR EMail = ?", null)
			.setParameters(request.getUserName(), request.getEMail())
			.first();
		//	Validate if exist
		if(user == null
				|| user.getAD_User_ID()  <= 0) {
			builder.setResponseType(ResponseType.USER_NOT_FOUND);
			throw new AdempiereException("@UserName@ / @EMail@ @NotFound@");
		}
		//	Generate reset
		try {
			String token = generateToken(user);
			builder.setToken(validateNull(token));
		} catch (Exception e) {
			builder.setResponseType(ResponseType.ERROR);
			throw new AdempiereException(e.getMessage());
		}
		builder.setResponseType(ResponseType.OK);
		return builder;
	}
	
	/**
	 * Conver User
	 * @param request
	 * @return
	 */
	private User.Builder convertUser(EnrollUserRequest request) {
		//	User Name
		if(Util.isEmpty(request.getUserName())) {
			throw new AdempiereException("@UserName@ @IsMandatory@");
		}
		//	EMail
		if(Util.isEmpty(request.getEMail())) {
			throw new AdempiereException("@EMail@ @IsMandatory@");
		}
		//	
		if(Util.isEmpty(request.getName())) {
			throw new AdempiereException("@Name@ @IsMandatory@");
		}
		User.Builder builder = User.newBuilder();
		int userId = new Query(Env.getCtx(), I_AD_User.Table_Name, "Value = ? OR EMail = ?", null)
			.setParameters(request.getUserName(), request.getEMail())
			.firstId();
		//	Validate if exist
		if(userId > 0) {
			throw new AdempiereException("@UserName@ / @EMail@ @AlreadyExists@");
		}
		//	Create
		MUser newUser = new MUser(Env.getCtx(), 0, null);
		newUser.setName(request.getName());
		if(!Util.isEmpty(request.getLastName())) {
			newUser.setName(newUser.getName() + " " + request.getLastName());
		}
		//	Add Email
		newUser.setEMail(request.getEMail());
		newUser.setValue(request.getUserName());
		newUser.setIsLoginUser(true);
		newUser.saveEx();
		//	Request a Password
		try {
			String token = generateToken(newUser);
			builder.setToken(validateNull(token));
		} catch (Exception e) {
			throw new AdempiereException(e.getMessage());
		}
		builder.setUserName(newUser.getValue());
		builder.setName(newUser.getName());
		builder.setEMail(newUser.getEMail());
		return builder;
	}
	
	/**
	 * Generate token
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private String generateToken(MUser user) throws Exception {
		if(user == null) {
			throw new AdempiereException("@AD_User_ID@ @NotFound@");
		}
		//	Validate EMail
		if (Util.isEmpty(user.getEMail())) {
			throw new AdempiereException("@AD_User_ID@ - @Email@ @NotFound@");
		}
		MClient client = MClient.get(user.getCtx(), user.getAD_Client_ID());
		MClientInfo clientInfo = client.getInfo();
		//	
		TokenGeneratorHandler.getInstance().generateToken(MADTokenDefinition.TOKENTYPE_URLTokenUsedAsURL, user.getAD_User_ID());
		MADToken token = TokenGeneratorHandler.getInstance().getToken(MADTokenDefinition.TOKENTYPE_URLTokenUsedAsURL);
		//	Get
		int mailTextId = clientInfo.getRestorePassword_MailText_ID();
		if(mailTextId <= 0) {
			throw new AdempiereException("@RestorePassword_MailText_ID@ @NotFound@");
		}
		//	Set from mail template
		MMailText text = new MMailText (Env.getCtx(), mailTextId, null);
		text.setPO(token);
		text.setUser(user);
		//	
		EMail email = client.createEMail(user.getEMail(), null, null);
		//	
		String msg = null;
		if (!email.isValid()) {
			msg = "@RequestActionEMailError@ Invalid EMail: " + user;
			throw new AdempiereException("@RequestActionEMailError@ Invalid EMail: " + user);
		}
		//text.setUser(user);	//	variable context
		String message = text.getMailText(true);
		email.setMessageHTML(text.getMailHeader(), message);
		//
		msg = email.send();
		MUserMail um = new MUserMail(text, user.getAD_User_ID(), email);
		um.saveEx();
		if (!msg.equals(EMail.SENT_OK)) {
			throw new AdempiereException(user.getName() + " @RequestActionEMailError@ " + msg);
		}
		//	
  	  	return token.getTokenValue();
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

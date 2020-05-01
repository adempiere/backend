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

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class AccessClient {
	  private static final Logger logger = Logger.getLogger(AccessClient.class.getName());

	  private final ManagedChannel channel;
	  private final SecurityGrpc.SecurityBlockingStub blockingStub;

	  /** Construct client connecting to HelloWorld server at {@code host:port}. */
	  public AccessClient(String host, int port) {
	    this(ManagedChannelBuilder.forAddress(host, port)
	        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
	        // needing certificates.
	        .usePlaintext()
	        .build());
	  }

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  AccessClient(ManagedChannel channel) {
	    this.channel = channel;
	    blockingStub = SecurityGrpc.newBlockingStub(channel);
	  }

	  public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	  }
	  
	  /** 
	   * Request User Roles. 
	   */
	  public void requestUserInfo() {
		  LoginRequest userRequest = LoginRequest.newBuilder()
				  .setUserName("SuperUser")
				  .setUserPass("System")
				  .setClientVersion("Version Epale")
				  .build();
		  UserInfoValue response;
		  try {
			  response = blockingStub.getUserInfo(userRequest);
			  logger.info("User Roles: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }

	  /** 
	   * Request Login. 
	   */
	  public void requestLogin() {
		  LoginRequest request = LoginRequest.newBuilder()
	    		.setClientVersion("Version Epale (" + System.currentTimeMillis() + ")")
	    		.setUserName("SuperUser")
				.setUserPass("System")
				.setOrganizationUuid("a3e5c878-fb40-11e8-a479-7a0060f0aa01")
	    		.setRoleUuid("a48d2596-fb40-11e8-a479-7a0060f0aa01")
	    		.setLanguage("es_MX")
	    		.build();
		  Session response;
		  try {
			  response = blockingStub.runLogin(request);
			  logger.info("User Session: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /** 
	   * Request Login. 
	   */
	  public void requestLoginDefaultRole() {
		  LoginRequest request = LoginRequest.newBuilder()
	    		.setClientVersion("Version Epale (" + System.currentTimeMillis() + ")")
	    		.setUserName("GardenAdmin")
				.setUserPass("GardenAdmin")
	    		.setLanguage("es_MX")
	    		.build();
		  Session response;
		  try {
			  response = blockingStub.runLoginDefault(request);
			  logger.info("User Session: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /** 
	   * Request Role. 
	   */
	  public void requestLogout() {
		  LogoutRequest request = LogoutRequest.newBuilder()
	    		.setClientVersion("Version Epale (" + System.currentTimeMillis() + ")")
	    		.setSessionUuid("25f773a4-6a48-11e9-9537-070abd038317")
	    		.build();
		  Session response;
		  try {
			  response = blockingStub.runLogout(request);
			  logger.info("User Session Logout: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /**
	   * Greet server. If provided, the first element of {@code args} is the name to use in the
	   * greeting.
	   */
	  public static void main(String[] args) throws Exception {
		AccessClient client = new AccessClient("localhost", 50050);
	    try {
	    	logger.info("####################### User Roles #####################");
	    	client.requestUserInfo();
	    	logger.info("####################### Request Login #####################");
	    	client.requestLogin();
	    	logger.info("####################### Request Logout #####################");
	    	client.requestLogout();
	    	logger.info("####################### Request Login And Role #####################");
	    	client.requestLoginDefaultRole();
	    } finally {
	      client.shutdown();
	    }
	  }
}

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

public class EnrollmentClient {
	  private static final Logger logger = Logger.getLogger(EnrollmentClient.class.getName());

	  private final ManagedChannel channel;
	  private final RegisterGrpc.RegisterBlockingStub blockingStub;

	  /** Construct client connecting to HelloWorld server at {@code host:port}. */
	  public EnrollmentClient(String host, int port) {
	    this(ManagedChannelBuilder.forAddress(host, port)
	        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
	        // needing certificates.
	        .usePlaintext()
	        .build());
	  }

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  EnrollmentClient(ManagedChannel channel) {
	    this.channel = channel;
	    blockingStub = RegisterGrpc.newBlockingStub(channel);
	  }

	  public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	  }
	  
	  /** 
	   * Request Enroll User 
	   */
	  public void enrollUser() {
		  EnrollUserRequest userRequest = EnrollUserRequest.newBuilder()
				  .setUserName("yamelsenih")
				  .setName("Yamel Senih")
				  .setEMail("ysenih@erpya.com")
				  .build();
		  User response;
		  try {
			  response = blockingStub.enrollUser(userRequest);
			  logger.info("User Enrolled: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /** 
	   * Request Enroll User 
	   */
	  public void resetPassword() {
		  ResetPasswordRequest resetRequest = ResetPasswordRequest.newBuilder()
				  .setUserName("yamelsenih")
				  .setEMail("ysenih@erpya.com")
				  .build();
		  ResetPasswordResponse response;
		  try {
			  response = blockingStub.resetPassword(resetRequest);
			  logger.info("Reset Password Status: " + response.getResponseTypeValue());
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
		EnrollmentClient client = new EnrollmentClient("localhost", 50047);
	    try {
	    	logger.info("####################### Enroll User #####################");
	    	client.enrollUser();
	    	logger.info("####################### Reset Password #####################");
	    	client.resetPassword();
	    } finally {
	      client.shutdown();
	    }
	  }
}

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

public class BusinessDataClient {
	  private static final Logger logger = Logger.getLogger(BusinessDataClient.class.getName());

	  private final ManagedChannel channel;
	  private final BusinessDataGrpc.BusinessDataBlockingStub blockingStub;

	  /** Construct client connecting to HelloWorld server at {@code host:port}. */
	  public BusinessDataClient(String host, int port) {
	    this(ManagedChannelBuilder.forAddress(host, port)
	        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
	        // needing certificates.
	        .usePlaintext()
	        .build());
	  }

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  BusinessDataClient(ManagedChannel channel) {
	    this.channel = channel;
	    blockingStub = BusinessDataGrpc.newBlockingStub(channel);
	  }

	  public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	  }
	  
	  /**
	   * Request a process
	   */
	  public void requestProcess() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .setSessionUuid("53c1c836-6e47-11e9-8160-3709b250e4e1")
				  .build();
		  RunBusinessProcessRequest request = RunBusinessProcessRequest.newBuilder()
				  .setClientRequest(clientRequest)
				  .setUuid("a42acf86-fb40-11e8-a479-7a0060f0aa01")
				  .build();
		  ProcessLog response;
		  try {
			  response = blockingStub.runBusinessProcess(request);
			  logger.info("Cache Reset: " + response);
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
		BusinessDataClient client = new BusinessDataClient("localhost", 50052);
	    try {
	    	logger.info("####################### Report Output #####################");
	    	client.requestProcess();
	    	client.shutdown();
	    } catch (Exception e) {
			e.printStackTrace();
		}
	  }
}

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
package org.spin.client;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.spin.grpc.util.ListPackagesRequest;
import org.spin.grpc.util.ListPackagesResponse;
import org.spin.grpc.util.ListUpdatesRequest;
import org.spin.grpc.util.ListUpdatesResponse;
import org.spin.grpc.util.UpdateCenterGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class UpdaterClient {
	  private static final Logger logger = Logger.getLogger(UpdaterClient.class.getName());

	  private final ManagedChannel channel;
	  private final UpdateCenterGrpc.UpdateCenterBlockingStub blockingStub;

	  /** Construct client connecting to HelloWorld server at {@code host:port}. */
	  public UpdaterClient(String host, int port) {
	    this(ManagedChannelBuilder.forAddress(host, port)
	        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
	        // needing certificates.
	        .usePlaintext()
	        .build());
	  }

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  UpdaterClient(ManagedChannel channel) {
	    this.channel = channel;
	    blockingStub = UpdateCenterGrpc.newBlockingStub(channel);
	  }

	  public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	  }

	  /** 
	   * Request Packages. 
	   */
	  public void requestPackagesList() {
		  try {
			  ListPackagesResponse response = blockingStub.listPackages(
					  ListPackagesRequest.newBuilder()
					  	.setToken("7ee9d420726d71c9b2c54388b16ae5bd64b27c6cd828d48022050cd7161cfc888a8c661c5edfc563")
					  	.setVersion("")
					  	.setReleaseNo("2020-12-01")
					  	.build());
			  response.getPackagesList().forEach(packageValue -> logger.info("Package: " + packageValue));
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /** 
	   * Request Updates. 
	   */
	  public void requestUpdatesList() {
		  try {
			  ListUpdatesResponse response = blockingStub.listUpdates(
					  ListUpdatesRequest.newBuilder()
					  	.setToken("7ee9d420726d71c9b2c54388b16ae5bd64b27c6cd828d48022050cd7161cfc888a8c661c5edfc563")
					  	.setEntityType("D")
					  	.build());
			  response.getUpdatesList().forEach(updates -> logger.info("Update: " + updates));
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
		UpdaterClient client = new UpdaterClient("localhost", 50059);
	    try {
	    	logger.info("####################### Packages #####################");
	    	client.requestPackagesList();
	    	logger.info("####################### Updates #####################");
	    	client.requestUpdatesList();
	    } finally {
	      client.shutdown();
	    }
	  }
}

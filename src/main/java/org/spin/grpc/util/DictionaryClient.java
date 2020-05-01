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

public class DictionaryClient {
	  private static final Logger logger = Logger.getLogger(DictionaryClient.class.getName());

	  private final ManagedChannel channel;
	  private final DictionaryGrpc.DictionaryBlockingStub blockingStub;

	  /** Construct client connecting to HelloWorld server at {@code host:port}. */
	  public DictionaryClient(String host, int port) {
	    this(ManagedChannelBuilder.forAddress(host, port)
	        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
	        // needing certificates.
	        .usePlaintext()
	        .build());
	  }

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  DictionaryClient(ManagedChannel channel) {
	    this.channel = channel;
	    blockingStub = DictionaryGrpc.newBlockingStub(channel);
	  }

	  public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	  }

	  /** 
	   * Request Window. 
	   */
	  public void requestWindow(boolean withTabs) {
		  ApplicationRequest applicationRequest = ApplicationRequest.newBuilder()
				  .setLanguage("es_MX")
				  .build();
		  EntityRequest request = EntityRequest.newBuilder()
	    		.setUuid("a520de12-fb40-11e8-a479-7a0060f0aa01")
	    		.setApplicationRequest(applicationRequest)
	    		.build();
		  Window response;
		  try {
			  if(withTabs) {
				  response = blockingStub.getWindowAndTabs(request);
				  for(Tab tab : response.getTabsList()) {
					  logger.info("Tab: " + tab);
				  }
			  } else {
				  response = blockingStub.getWindow(request);
			  }
			  logger.info("Window: " + response);
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
		DictionaryClient client = new DictionaryClient("localhost", 50051);
	    try {
	    	logger.info("####################### Window + Tabs #####################");
	    	client.requestWindow(true);
	    } finally {
	      client.shutdown();
	    }
	  }
}

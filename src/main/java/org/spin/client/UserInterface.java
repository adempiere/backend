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

import org.spin.grpc.store.Condition.Operator;
import org.spin.grpc.util.ClientRequest;
import org.spin.grpc.util.Condition;
import org.spin.grpc.util.Criteria;
import org.spin.grpc.util.ListTabEntitiesRequest;
import org.spin.grpc.util.ListTabEntitiesResponse;
import org.spin.grpc.util.UserInterfaceGrpc;
import org.spin.grpc.util.Value;
import org.spin.grpc.util.Value.ValueType;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class UserInterface {
	  private static final Logger logger = Logger.getLogger(UserInterface.class.getName());

	  private final ManagedChannel channel;
	  private final UserInterfaceGrpc.UserInterfaceBlockingStub blockingStub;

	  /** Construct client connecting to HelloWorld server at {@code host:port}. */
	  public UserInterface(String host, int port) {
	    this(ManagedChannelBuilder.forAddress(host, port)
	        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
	        // needing certificates.
	        .usePlaintext()
	        .build());
	  }

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  UserInterface(ManagedChannel channel) {
	    this.channel = channel;
	    blockingStub = UserInterfaceGrpc.newBlockingStub(channel);
	  }

	  public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	  }
	  
	  /**
	   * Request a process
	   */
	  public void requestProcess() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .setSessionUuid("c8a1c844-35eb-472e-a461-9f06cd2680b8")
				  .build();
		  ListTabEntitiesRequest request = ListTabEntitiesRequest.newBuilder()
				  .setClientRequest(clientRequest)
				  .setTabUuid("a4a1007a-fb40-11e8-a479-7a0060f0aa01")
				  .setFilters(Criteria.newBuilder()
						  .addConditions(Condition.newBuilder()
								  .setColumnName("Value")
								  .setOperatorValue(Operator.IN_VALUE)
								  .addValues(Value.newBuilder()
										  .setStringValue("Test")
										  .setValueTypeValue(ValueType.STRING_VALUE)))
						  )
				  .build();
		  ListTabEntitiesResponse response;
		  try {
			  response = blockingStub.listTabEntities(request);
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
		UserInterface client = new UserInterface("localhost", 50059);
	    try {
	    	logger.info("####################### List Entities from tab #####################");
	    	client.requestProcess();
	    	client.shutdown();
	    } catch (Exception e) {
			e.printStackTrace();
		}
	  }
}

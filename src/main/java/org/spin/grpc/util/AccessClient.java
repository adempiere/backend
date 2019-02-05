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
	  private final AccessServiceGrpc.AccessServiceBlockingStub blockingStub;

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
	    blockingStub = AccessServiceGrpc.newBlockingStub(channel);
	  }

	  public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	  }
	  
	  /** 
	   * Request User Roles. 
	   */
	  public void requestUserRoles() {
		  UserRequest userRequest = UserRequest.newBuilder()
				  .setUserName("SuperUser")
				  .build();
		  UserRoles response;
		  try {
			  response = blockingStub.requestUserRoles(userRequest);
			  logger.info("User Roles: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }

	  /** 
	   * Request Role. 
	   */
	  public void requestRole() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .build();
		  ObjectRequest request = ObjectRequest.newBuilder()
	    		.setClientRequest(clientRequest)
	    		.setUuid("a48d2596-fb40-11e8-a479-7a0060f0aa01")
	    		.build();
		  Role response;
		  try {
			  response = blockingStub.requestRole(request);
			  logger.info("User Roles: " + response);
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
	    	client.requestUserRoles();
	    	logger.info("####################### Role #####################");
	    	client.requestRole();
	    } finally {
	      client.shutdown();
	    }
	  }
}

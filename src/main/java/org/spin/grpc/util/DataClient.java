package org.spin.grpc.util;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class DataClient {
	  private static final Logger logger = Logger.getLogger(DataClient.class.getName());

	  private final ManagedChannel channel;
	  private final DataServiceGrpc.DataServiceBlockingStub blockingStub;

	  /** Construct client connecting to HelloWorld server at {@code host:port}. */
	  public DataClient(String host, int port) {
	    this(ManagedChannelBuilder.forAddress(host, port)
	        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
	        // needing certificates.
	        .usePlaintext()
	        .build());
	  }

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  DataClient(ManagedChannel channel) {
	    this.channel = channel;
	    blockingStub = DataServiceGrpc.newBlockingStub(channel);
	  }

	  public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	  }
	  
	  /** 
	   * Request PO List. 
	   */
	  public void requestPOList() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .build();
		  ValueObjectRequest request = ValueObjectRequest.newBuilder()
	    		.setClientRequest(clientRequest)
	    		.build();
		  ValueObjectList response;
		  try {
			  response = blockingStub.requestPOList(request);
			  logger.info("PO List: " + response);
		  } catch (StatusRuntimeException e) {	
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }

	  /** 
	   * Request PO. 
	   */
	  public void requestPO() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .build();
		  Criteria.Builder criteria = Criteria.newBuilder().setTableName("AD_Element");
		  ValueObjectRequest request = ValueObjectRequest.newBuilder()
	    		.setClientRequest(clientRequest)
	    		.setUuid("8cc49692-fb40-11e8-a479-7a0060f0aa01")	// HR_JobOpening_ID
	    		.setCriteria(criteria.build())
	    		.build();
		  ValueObject response;
		  try {
			  response = blockingStub.requestPO(request);
			  logger.info("PO: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /** 
	   * Request Callout. 
	   */
	  public void requestCallout() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .build();
		  CalloutRequest request = CalloutRequest.newBuilder()
	    		.setClientRequest(clientRequest)
	    		.build();
		  CalloutResponse response;
		  try {
			  response = blockingStub.requestCallout(request);
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
		DataClient client = new DataClient("localhost", 50052);
	    try {
	    	logger.info("####################### PO #####################");
	    	client.requestPO();
//	    	logger.info("####################### PO List #####################");
//	    	client.requestPOList();
//	    	logger.info("####################### Callout #####################");
//	    	client.requestPOList();
	    } finally {
	      client.shutdown();
	    }
	  }
}

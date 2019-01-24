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
	  private final DictionaryServiceGrpc.DictionaryServiceBlockingStub blockingStub;

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
	    blockingStub = DictionaryServiceGrpc.newBlockingStub(channel);
	  }

	  public void shutdown() throws InterruptedException {
	    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	  }

	  /** 
	   * Request Window. 
	   */
	  public void requestWindow(boolean withTabs) {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .setLanguage("es_MX")
				  .build();
		  ObjectRequest request = ObjectRequest.newBuilder()
	    		.setUuid("a520de12-fb40-11e8-a479-7a0060f0aa01")
	    		.setClientRequest(clientRequest)
	    		.build();
		  Window response;
		  try {
			  if(withTabs) {
				  response = blockingStub.requestWindowAndTabs(request);
				  for(Tab tab : response.getTabsList()) {
					  logger.info("Tab Name: " + tab.getName());
				  }
			  } else {
				  response = blockingStub.requestWindow(request);
			  }
			  logger.info("Window Name: " + response.getName());
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /**
	   * Request Tab
	   */
	  public void requestTab(boolean withFields) {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .setLanguage("es_MX")
				  .build();
		  ObjectRequest request = ObjectRequest.newBuilder()
	    		.setUuid("a49fb4e0-fb40-11e8-a479-7a0060f0aa01")
	    		.setClientRequest(clientRequest)
	    		.build();
		  Tab response;
		  try {
			  if(withFields) {
				  response = blockingStub.requestTabAndFields(request);
				  for(Field field : response.getFieldsList()) {
					  logger.info("Field: " + field.getName());
				  }
			  } else {
				  response = blockingStub.requestTab(request);
			  }
			  logger.info("Tab Name: " + response.getName());
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }		  
	  }
	  
	  /**
	   * Request Field
	   */
	  public void requestField() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .setLanguage("es_MX")
				  .build();
		  ObjectRequest request = ObjectRequest.newBuilder()
	    		.setUuid("8cecee3a-fb40-11e8-a479-7a0060f0aa01")
	    		.setClientRequest(clientRequest)
	    		.build();
		  Field response;
		  try {
			  response = blockingStub.requestField(request);
			  logger.info("Field " + response.getName());
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
	    	client.requestWindow(false);
	    	client.requestWindow(true);
	    	client.requestTab(false);
	    	client.requestTab(true);
	    	client.requestField();
	    } finally {
	      client.shutdown();
	    }
	  }
}

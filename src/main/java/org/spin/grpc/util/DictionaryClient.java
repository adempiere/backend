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
				  response = blockingStub.requestWindowAndTabs(request);
				  for(Tab tab : response.getTabsList()) {
					  logger.info("Tab: " + tab);
				  }
			  } else {
				  response = blockingStub.requestWindow(request);
			  }
			  logger.info("Window: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  
	  /** 
	   * Request Window. 
	   */
	  public void requestMenu(boolean withTabs) {
		  ApplicationRequest applicationRequest = ApplicationRequest.newBuilder()
				  .setLanguage("es_MX")
				  .build();
		  EntityRequest request = EntityRequest.newBuilder()
	    		//.setUuid("8e4fd396-fb40-11e8-a479-7a0060f0aa01")
	    		.setApplicationRequest(applicationRequest)
	    		.build();
		  Menu response;
		  try {
			  if(withTabs) {
				  response = blockingStub.requestMenuAndChild(request);
				  for(Menu child : response.getChildsList()) {
					  logger.info("Menu Child: " + child);
				  }
			  } else {
				  response = blockingStub.requestMenu(request);
			  }
			  logger.info("Menu: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  
	  /**
	   * Request Tab
	   */
	  public void requestTab(boolean withFields) {
		  ApplicationRequest applicationRequest = ApplicationRequest.newBuilder()
				  .setLanguage("es_MX")
				  .build();
		  EntityRequest request = EntityRequest.newBuilder()
	    		.setUuid("a49fb4e0-fb40-11e8-a479-7a0060f0aa01")
	    		.setApplicationRequest(applicationRequest)
	    		.build();
		  Tab response;
		  try {
			  if(withFields) {
				  response = blockingStub.requestTabAndFields(request);
				  for(Field field : response.getFieldsList()) {
					  logger.info("Field: " + field);
				  }
			  } else {
				  response = blockingStub.requestTab(request);
			  }
			  logger.info("Tab: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }		  
	  }
	  
	  /**
	   * Request Field
	   */
	  public void requestField() {
		  ApplicationRequest applicationRequest = ApplicationRequest.newBuilder()
				  .setLanguage("es_MX")
				  .build();
		  EntityRequest request = EntityRequest.newBuilder()
	    		.setUuid("8cecee3a-fb40-11e8-a479-7a0060f0aa01")
	    		.setApplicationRequest(applicationRequest)
	    		.build();
		  Field response;
		  try {
			  response = blockingStub.requestField(request);
			  logger.info("Field " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }

	  /**
	   * Request Process
	   */
	  public void requestProcess() {
		  ApplicationRequest applicationRequest = ApplicationRequest.newBuilder()
				  .setLanguage("es_MX")
				  .build();
		  EntityRequest request = EntityRequest.newBuilder()
	    		.setUuid("a42adc88-fb40-11e8-a479-7a0060f0aa01")
	    		.setApplicationRequest(applicationRequest)
	    		.build();
		  Process response;
		  try {
			  response = blockingStub.requestProcess(request);
			  logger.info("Process " + response);
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
	    	/*logger.info("####################### Menu Only #####################");
	    	client.requestMenu(false);
	    	logger.info("####################### Menu + Child #####################");
	    	client.requestMenu(true);
	    	logger.info("####################### Window Only #####################");
	    	client.requestWindow(false);
	    	logger.info("####################### Window + Tabs #####################");
	    	client.requestWindow(true);
	    	logger.info("####################### Tab Only #####################");
	    	*/client.requestTab(false);
	    	logger.info("####################### Tab + Fields #####################");
	    	/*client.requestTab(true);
	    	logger.info("####################### Field Only #####################");
	    	client.requestField();
	    	logger.info("####################### Process Only #####################");
	    	client.requestProcess();*/
	    } finally {
	      client.shutdown();
	    }
	  }
}

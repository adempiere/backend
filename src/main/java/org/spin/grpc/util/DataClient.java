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

import org.spin.grpc.util.Value.ValueType;

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
			  response = blockingStub.requestObjectList(request);
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
			  response = blockingStub.requestObject(request);
			  logger.info("PO: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /** 
	   * Request Lookup. 
	   */
	  public void requestLookup() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .build();
		  Criteria.Builder criteria = Criteria.newBuilder().setTableName("C_PaymentTerm");
		  criteria.setQuery("SELECT C_PaymentTerm.C_PaymentTerm_ID,NULL,NVL(C_PaymentTerm_Trl.Name,'-1'),C_PaymentTerm.IsActive "
		  		+ "FROM C_PaymentTerm "
		  		+ "INNER JOIN C_PaymentTerm_TRL ON (C_PaymentTerm.C_PaymentTerm_ID=C_PaymentTerm_Trl.C_PaymentTerm_ID AND C_PaymentTerm_Trl.AD_Language='es_MX') "
		  		+ "WHERE C_PaymentTerm.C_PaymentTerm_ID=?");
		  criteria.addValues(Value.newBuilder().setIntValue(106));
		  ValueObjectRequest request = ValueObjectRequest.newBuilder()
	    		.setClientRequest(clientRequest)
	    		.setUuid("8cc49692-fb40-11e8-a479-7a0060f0aa01")	// HR_JobOpening_ID
	    		.setCriteria(criteria.build())
	    		.build();
		  ValueObject response;
		  try {
			  response = blockingStub.requestLookup(request);
			  logger.info("Lookup: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /** 
	   * Request Lookup List. 
	   */
	  public void requestLookupList() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .build();
		  Criteria.Builder criteria = Criteria.newBuilder().setTableName("M_DiscountSchema");
		  criteria.setQuery("SELECT M_DiscountSchema.M_DiscountSchema_ID,NULL,NVL(M_DiscountSchema.Name,'-1'),M_DiscountSchema.IsActive "
		  		+ "FROM M_DiscountSchema "
		  		+ "WHERE M_DiscountSchema.DiscountType<>'P' ORDER BY 3");
		  ValueObjectRequest request = ValueObjectRequest.newBuilder()
	    		.setClientRequest(clientRequest)
	    		.setUuid("8cc49692-fb40-11e8-a479-7a0060f0aa01")	// HR_JobOpening_ID
	    		.setCriteria(criteria.build())
	    		.build();
		  ValueObjectList response;
		  try {
			  response = blockingStub.requestLookupList(request);
			  logger.info("Lookup List: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /** 
	   * Request PO. 
	   */
	  public void requestPOWithSQL() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .build();
		  Criteria.Builder criteria = Criteria.newBuilder().setTableName("C_BPartner");
		  criteria.setWhereClause("C_BPartner.IsCustomer = 'Y'");
		  ValueObjectRequest request = ValueObjectRequest.newBuilder()
	    		.setClientRequest(clientRequest)
	    		.setCriteria(criteria.build())
	    		.build();
		  ValueObject response;
		  try {
			  response = blockingStub.requestObject(request);
			  logger.info("PO With SQL: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /**
	   * Request a process
	   */
	  public void requestProcess() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .setSessionUuid("53c1c836-6e47-11e9-8160-3709b250e4e1")
				  .build();
		  ProcessRequest request = ProcessRequest.newBuilder()
				  .setClientRequest(clientRequest)
				  .setUuid("a42acf86-fb40-11e8-a479-7a0060f0aa01")
				  .build();
		  ProcessResponse response;
		  try {
			  response = blockingStub.requestProcess(request);
			  logger.info("Cache Reset: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /**
	   * Request a process
	   */
	  public void requestReport() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .setSessionUuid("53c1c836-6e47-11e9-8160-3709b250e4e1")
				  .build();
		  //	Open Items
		  ProcessRequest.Builder request = ProcessRequest.newBuilder()
				  .setClientRequest(clientRequest)
				  .setUuid("a42b9c36-fb40-11e8-a479-7a0060f0aa01");
		  //	Add parameters
		  //	Is SOT
		  Value.Builder isSOTrx = Value.newBuilder();
		  isSOTrx.setBooleanValue(true);
		  isSOTrx.setValueType(ValueType.BOOLEAN);
		  request.putParameters("IsSOTrx", isSOTrx.build());
		  //	
		  
		  ProcessResponse response;
		  try {
			  response = blockingStub.requestProcess(request.build());
			  logger.info("Open Item Report: " + response);
		  } catch (StatusRuntimeException e) {
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		      return;
		  }
	  }
	  
	  /** 
	   * Request PO List. 
	   */
	  public void requestPOListWithSQL() {
		  ClientRequest clientRequest = ClientRequest.newBuilder()
				  .build();
		  Criteria.Builder criteria = Criteria.newBuilder().setTableName("C_Invoice");
		  criteria.setWhereClause("C_Invoice.DocStatus IN('CO', 'CL')");
		  criteria.setOrderByClause("C_Invoice.DateInvoiced DESC");
		  ValueObjectRequest request = ValueObjectRequest.newBuilder()
	    		.setClientRequest(clientRequest)
	    		.setCriteria(criteria.build())
	    		.build();
		  ValueObjectList response;
		  try {
			  response = blockingStub.requestObjectList(request);
			  logger.info("PO List With SQL: " + response);
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
//	    	logger.info("####################### PO #####################");
//	    	client.requestPO();
	    	//	
//	    	client.requestPOWithSQL();
	    	//	
//	    	client.requestPOListWithSQL();
	    	client.requestProcess();
	    	client.requestReport();
//	    	logger.info("####################### PO List #####################");
//	    	client.requestPOList();
//	    	logger.info("####################### Callout #####################");
//	    	client.requestPOList();
	    } finally {
	      client.shutdown();
	    }
	  }
}

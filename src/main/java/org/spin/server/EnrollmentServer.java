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
package org.spin.server;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.spin.base.setup.SetupLoader;
import org.spin.grpc.service.EnrollmentServiceImplementation;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;

public class EnrollmentServer {
	private static final Logger logger = Logger.getLogger(EnrollmentServer.class.getName());

	private Server server;
	/**
	   * Get SSL / TLS context
	   * @return
	   */
	  private SslContextBuilder getSslContextBuilder() {
	        SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(new File(SetupLoader.getInstance().getServer().getCertificate_chain_file()),
	                new File(SetupLoader.getInstance().getServer().getPrivate_key_file()));
	        if (SetupLoader.getInstance().getServer().getTrust_certificate_collection_file() != null) {
	            sslClientContextBuilder.trustManager(new File(SetupLoader.getInstance().getServer().getTrust_certificate_collection_file()));
	            sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
	        }
	        return GrpcSslContexts.configure(sslClientContextBuilder);
	  }
	  
	  private void start() throws IOException {
		  if(SetupLoader.getInstance().getServer().isTlsEnabled()) {
			  server = NettyServerBuilder.forPort(SetupLoader.getInstance().getServer().getPort())
		                .addService(new EnrollmentServiceImplementation())
		                .sslContext(getSslContextBuilder().build())
		                .build()
		                .start();
		  } else {
			  server = ServerBuilder.forPort(SetupLoader.getInstance().getServer().getPort())
				        .addService(new EnrollmentServiceImplementation())
				        .build()
				        .start();
		  }
		  logger.info("Server started, listening on " + SetupLoader.getInstance().getServer().getPort());
		    Runtime.getRuntime().addShutdownHook(new Thread() {
		      @Override
		      public void run() {
		        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
		    	  logger.info("*** shutting down gRPC server since JVM is shutting down");
		    	  EnrollmentServer.this.stop();
		        logger.info("*** server shut down");
		      }
		    });
	  }

	  private void stop() {
	    if (server != null) {
	      server.shutdown();
	    }
	  }

	  /**
	   * Await termination on the main thread since the grpc library uses daemon threads.
	   */
	  private void blockUntilShutdown() throws InterruptedException {
	    if (server != null) {
	      server.awaitTermination();
	    }
	  }

	  /**
	   * Main launches the server from the command line.
	 * @throws Exception 
	   */
	  public static void main(String[] args) throws Exception {
		  if(args == null) {
			  throw new Exception("Arguments Not Found");
		  }
		  //	
		  if(args == null || args.length == 0) {
			  throw new Exception("Arguments Must Be: [property file name]");
		  }
		  String setupFileName = args[0];
		  if(setupFileName == null || setupFileName.trim().length() == 0) {
			  throw new Exception("Setup File not found");
		  }
		  SetupLoader.loadSetup(setupFileName);
		  //	Validate load
		  SetupLoader.getInstance().validateLoad();
		  final EnrollmentServer server = new EnrollmentServer();
		  server.start();
		  server.blockUntilShutdown();
	  }
}

package org.spin.grpc.util;

import java.io.IOException;
import java.util.logging.Logger;

import org.compiere.Adempiere;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class DictionaryServer {
	private static final Logger logger = Logger.getLogger(DictionaryServiceImplementation.class.getName());

	  private Server server;

	  private void start() throws IOException {
	    /* The port on which the server should run */
	    int port = 50051;
	    server = ServerBuilder.forPort(port)
	        .addService(new DictionaryServiceImplementation())
	        .build()
	        .start();
	    logger.info("Server started, listening on " + port);
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	      @Override
	      public void run() {
	        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
	    	  logger.info("*** shutting down gRPC server since JVM is shutting down");
	        DictionaryServer.this.stop();
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
	   */
	  public static void main(String[] args) throws IOException, InterruptedException {
		Adempiere.startup(true);
	    final DictionaryServer server = new DictionaryServer();
	    server.start();
	    server.blockUntilShutdown();
	  }
}

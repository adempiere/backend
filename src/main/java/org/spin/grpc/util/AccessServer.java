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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.compiere.Adempiere;
import org.compiere.util.Util;

import com.google.common.util.concurrent.UncaughtExceptionHandlers;

import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;

public class AccessServer {
	private static final Logger logger = Logger.getLogger(DictionaryServiceImplementation.class.getName());

	  private Server server;
	  private final int port;
	  private final String certChainFilePath;
	  private final String privateKeyFilePath;
	  private final String trustCertCollectionFilePath;
	  private final boolean isTlsEnabled;

	  
	  /**
	   * Default values
	   * @param port
	   * @param certChainFilePath
	   * @param privateKeyFilePath
	   * @param trustCertCollectionFilePath
	   */
	  public AccessServer(int port,
              String certChainFilePath,
              String privateKeyFilePath,
              String trustCertCollectionFilePath) {
		  this.port = port;
		  this.certChainFilePath = certChainFilePath;
		  this.privateKeyFilePath = privateKeyFilePath;
		  this.trustCertCollectionFilePath = trustCertCollectionFilePath;
		  this.isTlsEnabled = !Util.isEmpty(certChainFilePath) && !Util.isEmpty(privateKeyFilePath); 
	  }
	  
	  /**
	   * With TLS disabled
	   * @param port
	   */
	  public AccessServer(int port) {
		  this(port, null, null, null);
	  }
	  
	  /**
	   * Get SSL / TLS context
	   * @return
	   */
	  private SslContextBuilder getSslContextBuilder() {
	        SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(new File(certChainFilePath),
	                new File(privateKeyFilePath));
	        if (trustCertCollectionFilePath != null) {
	            sslClientContextBuilder.trustManager(new File(trustCertCollectionFilePath));
	            sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
	        }
	        return GrpcSslContexts.configure(sslClientContextBuilder);
	  }
	  
	  private static Server start() {
		  ThreadFactory tf = new DefaultThreadFactory("server-elg-", true);
		  final EventLoopGroup boss = new NioEventLoopGroup(1, tf);
		  final EventLoopGroup worker = new NioEventLoopGroup(0, tf);
	 	  final Class<? extends ServerChannel> channelType = NioServerSocketChannel.class;
	 	  
			  NettyServerBuilder  server = NettyServerBuilder.forPort(5050)
						.bossEventLoopGroup(boss)
						.workerEventLoopGroup(worker)
						.channelType(channelType)
				        .addService(new AccessServiceImplementation())
				        .flowControlWindow(NettyChannelBuilder.DEFAULT_FLOW_CONTROL_WINDOW);

			  server.executor(getAsyncExecutor());
			  
		  logger.info("Server started, listening on " + 5050);
		  return server.build();
	  }

		private static Executor getAsyncExecutor() {
			return new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
					new ForkJoinPool.ForkJoinWorkerThreadFactory() {
						final AtomicInteger num = new AtomicInteger();

						@Override
						public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
							ForkJoinWorkerThread thread =
									ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
							thread.setDaemon(true);
							thread.setName("grpc-server-app-" + "-" + num.getAndIncrement());
							return thread;
						}
					}, UncaughtExceptionHandlers.systemExit(), true);
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
		  Adempiere.startup(false);
		  int defaultPort = 50050;
		    if(args != null) {
		    	Optional<String> parameter = Arrays.asList(args).stream()
		    			.filter(arg -> !Util.isEmpty(arg))
		    			.filter(arg -> arg.matches("[+-]?\\d*(\\.\\d+)?")).findFirst();
		    	if(parameter.isPresent()) {
		    		defaultPort = Integer.parseInt(parameter.get());
		    	}
			}
		    String certChainFilePath = (args.length > 1? args[1]: null);
		    String privateKeyFilePath = (args.length > 2? args[2]: null);
		    String trustCertCollectionFilePath = (args.length > 3? args[3]: null);
		    
			System.out.println("Netty Config Server started");
			//shutdown(server);
			
		    final AccessServer server = new AccessServer(defaultPort, 
		    		certChainFilePath,
		    		privateKeyFilePath,
		    		trustCertCollectionFilePath);
		    server.start();
		    server.blockUntilShutdown();
	  }
}

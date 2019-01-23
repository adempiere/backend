package org.spin.grpc.util;

import org.spin.grpc.util.DictionaryServiceGrpc.DictionaryServiceImplBase;

import io.grpc.stub.StreamObserver;

public class DictionaryServiceImplementation extends DictionaryServiceImplBase {
	@Override
	public void requestWindow(ClientRequest request, StreamObserver<WindowDefinition> responseObserver) {
		
	}
}

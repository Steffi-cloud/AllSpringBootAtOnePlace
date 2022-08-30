package com.example.demo.fullconfig;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.stargate.grpc.StargateBearerToken;
//import io.stargate.grpc.StargateBearerToken;
//import io.stargate.proto.StargateGrpc;
import io.stargate.proto.StargateGrpc;

public class ClusterConnection {
	private static final String ASTRA_DB_ID      = "0f466d78-e018-4ead-8dff-19e70d6922e9";
	private static final String ASTRA_DB_REGION  = "us-east1";
	private static final String ASTRA_TOKEN      = "AstraCS:DjkXEMjbRQPmJXZZRYUzlrxd:bb2a9edbbea7ab78f9bbe0e6537fb8651fe94d28b0d51f6c22637d88217f201e";
	private static final String ASTRA_KEYSPACE   = "testkeyspace";

	public static void main(String[] args)
	throws Exception {


	    //-------------------------------------
	    // 1. Initializing Connectivity
	    //-------------------------------------
	    ManagedChannel channel = ManagedChannelBuilder
	            .forAddress(ASTRA_DB_ID + "-" + ASTRA_DB_REGION + ".apps.astra.datastax.com", 443)
	            .useTransportSecurity()
	            .build();

	    // blocking stub version
	    StargateGrpc.StargateBlockingStub blockingStub =
	        StargateGrpc.newBlockingStub(channel)
	            .withDeadlineAfter(10, TimeUnit.SECONDS)
	            .withCallCredentials(new StargateBearerToken(ASTRA_TOKEN));
}
	
}

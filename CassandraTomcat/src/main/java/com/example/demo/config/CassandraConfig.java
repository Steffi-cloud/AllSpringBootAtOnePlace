package com.example.demo.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.stargate.grpc.StargateBearerToken;
import io.stargate.proto.QueryOuterClass;
import io.stargate.proto.QueryOuterClass.Row;
import io.stargate.proto.StargateGrpc;
@Configuration
public class CassandraConfig {
	/*
	 * private static final String ASTRA_DB_ID =
	 * "0f466d78-e018-4ead-8dff-19e70d6922e9"; private static final String
	 * ASTRA_DB_REGION = "us-east1"; private static final String ASTRA_TOKEN =
	 * "AstraCS:DjkXEMjbRQPmJXZZRYUzlrxd:bb2a9edbbea7ab78f9bbe0e6537fb8651fe94d28b0d51f6c22637d88217f201e";
	 * private static final String ASTRA_KEYSPACE = "testkeyspace";
	 */
	public void initialiseConnect() {
		String token = "AstraCS:DjkXEMjbRQPmJXZZRYUzlrxd:bb2a9edbbea7ab78f9bbe0e6537fb8651fe94d28b0d51f6c22637d88217f201e";
		ManagedChannel channel = ManagedChannelBuilder
				.forAddress("0f466d78-e018-4ead-8dff-19e70d6922e9-us-east1.apps.astra.datastax.com/api/rest", 443)
				.useTransportSecurity().build();

		System.out.println("connect to GCPCassandra initialised");
		createTables(channel, token);

	}

	public void createTables(ManagedChannel channel, String token) {
		StargateGrpc.StargateBlockingStub blockingStub = StargateGrpc.newBlockingStub(channel)
				.withDeadlineAfter(60, TimeUnit.SECONDS).withCallCredentials(new StargateBearerToken(token));

		blockingStub.executeQuery(
				QueryOuterClass.Query.newBuilder().setCql("DROP TABLE IF EXISTS testkeyspace.users").build());
		System.out.println("Table 'users' has been dropped.");

		blockingStub.executeQuery(QueryOuterClass.Query.newBuilder()
				.setCql("CREATE TABLE IF NOT EXISTS testkeyspace.users (firstname text PRIMARY KEY, lastname text);")
				.build());
		System.out.println("Table 'users' has been created.");
		insertDataAndUpdateColumn(channel, token);
	}

	public void insertDataAndUpdateColumn(ManagedChannel channel, String token) {

		StargateGrpc.StargateBlockingStub blockingStub = StargateGrpc.newBlockingStub(channel)
				.withDeadlineAfter(60, TimeUnit.SECONDS).withCallCredentials(new StargateBearerToken(token));

		blockingStub.executeQuery(QueryOuterClass.Query.newBuilder()
				.setCql("INSERT INTO testkeyspace.users (firstname, lastname) VALUES('Jane', 'Doe')").build());
		blockingStub.executeQuery(QueryOuterClass.Query.newBuilder()
				.setCql("INSERT INTO testkeyspace.users (firstname, lastname) VALUES('Serge', 'Provencio')").build());
		QueryOuterClass.Response queryString3 = blockingStub.executeQuery(
				QueryOuterClass.Query.newBuilder().setCql("ALTER TABLE testkeyspace.users ADD email text").build());
		QueryOuterClass.ResultSet rs3 = queryString3.getResultSet();
		for (Row row : rs3.getRowsList()) {
			System.out.println("" + "FirstName=" + row.getValues(0).getString() + ", " + "lastname="
					+ row.getValues(1).getString() + "email" + row.getValues(2).getString());
		}

		QueryOuterClass.Response queryStringForColumn = blockingStub.executeQuery(QueryOuterClass.Query.newBuilder()
				.setCql("ALTER TABLE testkeyspace.users ADD address_id varchar").build());
		QueryOuterClass.ResultSet rsForColumn = queryStringForColumn.getResultSet();
		for (Row row : rsForColumn.getRowsList()) {
			System.out.println("" + "FirstName=" + row.getValues(0).getString() + ", " + "lastname="
					+ row.getValues(1).getString() + "email" + row.getValues(2).getString());
		}

		System.out.println("2 rows have been inserted in table users.");
		// System.out.println("Table 'users' has been created.");
		updateData(channel, token);
	}

	public void updateData(ManagedChannel channel, String token) {
		StargateGrpc.StargateBlockingStub blockingStub = StargateGrpc.newBlockingStub(channel)
				.withDeadlineAfter(60, TimeUnit.SECONDS).withCallCredentials(new StargateBearerToken(token));
		QueryOuterClass.Response queryString2 = blockingStub.executeQuery(QueryOuterClass.Query.newBuilder().setCql(
				"UPDATE testkeyspace.users set lastname= 'Provencial' , email = 'abc@test.com' where firstname= 'Serge' ")
				.build());
		QueryOuterClass.ResultSet rs2 = queryString2.getResultSet();
		for (Row row : rs2.getRowsList()) {
			System.out.println("" + "FirstName=" + row.getValues(0).getString() + ", " + "lastname="
					+ row.getValues(1).getString());
		}

		blockingStub.executeBatch(QueryOuterClass.Batch.newBuilder()
				.addQueries(QueryOuterClass.BatchQuery.newBuilder().setCql(
						"UPDATE testkeyspace.users set lastname= 'Root' , email = 'abc@test.com' , address_id = '1234' where firstname= 'Jane' ")
						.build())
				.addQueries(QueryOuterClass.BatchQuery.newBuilder().setCql(
						"UPDATE testkeyspace.users set lastname= 'Provencial' , email = 'abc@test.com' ,address_id = '3456' where firstname= 'Serge' ")
						.build())
				.build());
		System.out.println("2 rows have been updated in table users.");

		QueryOuterClass.Response queryString4 = blockingStub.executeQuery(
				QueryOuterClass.Query.newBuilder().setCql("ALTER TABLE testkeyspace.users DROP email").build());
		QueryOuterClass.ResultSet rs4 = queryString4.getResultSet();
		for (Row row : rs4.getRowsList()) {
			System.out.println("" + "FirstName=" + row.getValues(0).getString() + ", " + "lastname="
					+ row.getValues(1).getString());
		}
		System.out.println("email has been dropped from table users.");
		QueryOuterClass.Response queryString1 = blockingStub.executeQuery(QueryOuterClass.Query.newBuilder()
				.setCql("DELETE FROM testkeyspace.users where firstname = 'Jane' ").build());
		QueryOuterClass.ResultSet rs1 = queryString1.getResultSet();
		for (Row row : rs1.getRowsList()) {
			System.out.println("" + "FirstName=" + row.getValues(0).getString() + ", " + "lastname="
					+ row.getValues(1).getString());
		}

		System.out.println("Everything worked!");
		
	}
}

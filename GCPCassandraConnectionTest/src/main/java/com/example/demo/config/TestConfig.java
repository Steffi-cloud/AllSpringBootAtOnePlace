package com.example.demo.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;

//import com.datastax.oss.driver.api.core.cql.Row;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpRequest;
//import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpRequest;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponse;
import io.stargate.grpc.StargateBearerToken;
import io.stargate.proto.QueryOuterClass;
import io.stargate.proto.QueryOuterClass.Row;
import io.stargate.proto.StargateGrpc;

public class TestConfig {
	// Stargate OSS configuration for locally hosted docker image
	private static final String STARGATE_USERNAME = "cassandra";
	private static final String STARGATE_PASSWORD = "cassandra";
	private static final String STARGATE_HOST = "https://0f466d78-e018-4ead-8dff-19e70d6922e9-us-east1.apps.astra.datastax.com/api/rest";
	private static final int STARGATE_GRPC_PORT = 8090;
	private static final String STARGATE_AUTH_ENDPOINT = "http://" + STARGATE_HOST + ":8081/v1/auth";

	public static void main(String[] args) throws Exception {

		// -------------------------------------
		// 1. Initializing Connectivity
		// -------------------------------------

		// Authenticate to get a token (http client jdk11)
		// String token = getTokenFromAuthEndpoint(STARGATE_USERNAME,
		// STARGATE_PASSWORD);
		String token = "AstraCS:DjkXEMjbRQPmJXZZRYUzlrxd:bb2a9edbbea7ab78f9bbe0e6537fb8651fe94d28b0d51f6c22637d88217f201e";
		// Create Grpc Channel
		// ManagedChannel channel = ManagedChannelBuilder
		// .forAddress(STARGATE_HOST, STARGATE_GRPC_PORT).usePlaintext().build();
		ManagedChannel channel = ManagedChannelBuilder
				.forAddress("0f466d78-e018-4ead-8dff-19e70d6922e9-us-east1.apps.astra.datastax.com/api/rest", 443)
				.useTransportSecurity().build();
		// channel.
		// blocking stub version
		StargateGrpc.StargateBlockingStub blockingStub = StargateGrpc.newBlockingStub(channel)
				.withDeadlineAfter(10, TimeUnit.SECONDS).withCallCredentials(new StargateBearerToken(token));

		// -------------------------------------
		// 2. Create Schema
		// -------------------------------------

		/*
		 * blockingStub.executeQuery( QueryOuterClass.Query.newBuilder() .setCql("" +
		 * "CREATE KEYSPACE IF NOT EXISTS test " + "WITH REPLICATION = {" +
		 * " 'class' : 'SimpleStrategy', " + " 'replication_factor' : 1" + "};")
		 * .build()); System.out.println("Keyspace 'test' has been created.");
		 */
		blockingStub.executeQuery(
				QueryOuterClass.Query.newBuilder().setCql("DROP TABLE IF EXISTS testkeyspace.users").build());
		System.out.println("Table 'users' has been dropped.");

		blockingStub.executeQuery(QueryOuterClass.Query.newBuilder()
				.setCql("CREATE TABLE IF NOT EXISTS testkeyspace.users (firstname text PRIMARY KEY, lastname text);")
				.build());
		System.out.println("Table 'users' has been created.");

		// -------------------------------------
		// 3. Insert 2 rows with Batch
		// -------------------------------------

		blockingStub.executeBatch(QueryOuterClass.Batch.newBuilder()
				.addQueries(QueryOuterClass.BatchQuery.newBuilder()
						.setCql("INSERT INTO testkeyspace.users (firstname, lastname) VALUES('Jane', 'Doe')").build())
				.addQueries(QueryOuterClass.BatchQuery.newBuilder()
						.setCql("INSERT INTO testkeyspace.users (firstname, lastname) VALUES('Serge', 'Provencio')")
						.build())
				.build());
		System.out.println("2 rows have been inserted in table users.");

		// -------------------------------------
		// 4. Retrieving result.
		// -------------------------------------

		QueryOuterClass.Response queryString = blockingStub.executeQuery(QueryOuterClass.Query.newBuilder()
				.setCql("SELECT firstname, lastname FROM testkeyspace.users").build());
		QueryOuterClass.ResultSet rs = queryString.getResultSet();
		for (Row row : rs.getRowsList()) {
			System.out.println("" + "FirstName=" + row.getValues(0).getString() + ", " + "lastname="
					+ row.getValues(1).getString());
		}

		QueryOuterClass.Response queryString3 = blockingStub.executeQuery(
				QueryOuterClass.Query.newBuilder().setCql("ALTER TABLE testkeyspace.users ADD email text").build());
		QueryOuterClass.ResultSet rs3 = queryString3.getResultSet();
		for (Row row : rs3.getRowsList()) {
			System.out.println("" + "FirstName=" + row.getValues(0).getString() + ", " + "lastname="
					+ row.getValues(1).getString() + "email" + row.getValues(2).getString());
		}

QueryOuterClass.Response queryStringForColumn = blockingStub.executeQuery(
				QueryOuterClass.Query.newBuilder().setCql("ALTER TABLE testkeyspace.users ADD address_id varchar").build());
		QueryOuterClass.ResultSet rsForColumn = queryStringForColumn.getResultSet();
		for (Row row : rsForColumn.getRowsList()) {
			System.out.println("" + "FirstName=" + row.getValues(0).getString() + ", " + "lastname="
					+ row.getValues(1).getString() + "email" + row.getValues(2).getString());
		}
		
		/*
		 * QueryOuterClass.Response queryStringForAlterColumn =
		 * blockingStub.executeQuery( QueryOuterClass.Query.newBuilder().
		 * setCql("ALTER TABLE testkeyspace.users ALTER address_id TYPE int").build());
		 * QueryOuterClass.ResultSet rsForAlterColumn =
		 * queryStringForAlterColumn.getResultSet(); for (Row row :
		 * rsForAlterColumn.getRowsList()) { System.out.println("" + "FirstName=" +
		 * row.getValues(0).getString() + ", " + "lastname=" +
		 * row.getValues(1).getString() + "email" + row.getValues(2).getString()); }
		 */
		/*
		 * QueryOuterClass.Response queryString2 =
		 * blockingStub.executeQuery(QueryOuterClass.Query.newBuilder().setCql(
		 * "UPDATE testkeyspace.users set lastname= 'Provencial' , email = 'abc@test.com' where firstname= 'Serge' "
		 * ) .build()); QueryOuterClass.ResultSet rs2 = queryString2.getResultSet(); for
		 * (Row row : rs2.getRowsList()) { System.out.println("" + "FirstName=" +
		 * row.getValues(0).getString() + ", " + "lastname=" +
		 * row.getValues(1).getString()); }
		 */
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

		/*
		 * QueryOuterClass.Response queryString1 =
		 * blockingStub.executeQuery(QueryOuterClass.Query.newBuilder()
		 * .setCql("DELETE FROM testkeyspace.users where firstname = 'Jane' ").build());
		 * QueryOuterClass.ResultSet rs1 = queryString1.getResultSet(); for (Row row :
		 * rs1.getRowsList()) { System.out.println("" + "FirstName=" +
		 * row.getValues(0).getString() + ", " + "lastname=" +
		 * row.getValues(1).getString()); }
		 */
		System.out.println("Everything worked!");
		System.exit(0);
	}

	/*
	 * private static String getTokenFromAuthEndpoint(String username, String
	 * password) { try { HttpRequest request = HttpRequest.newBuilder() .GET()
	 * .uri(URI.create(STARGATE_AUTH_ENDPOINT)) .setHeader("Content-Type",
	 * "application/json") .POST(HttpRequest.BodyPublishers.ofString("{" +
	 * " \"username\": \"" + STARGATE_USERNAME+ "\",\n" + " \"password\": \"" +
	 * STARGATE_PASSWORD + "\"\n" + "}'")) .build(); HttpResponse response =
	 * HttpClient.newBuilder().build() .send(request,
	 * HttpResponse.BodyHandlers.ofString()); return
	 * response.body().split(":")[1].replaceAll("\"", "").replaceAll("}", ""); }
	 * catch(Exception e) { throw new IllegalArgumentException(e); } }
	 */
}

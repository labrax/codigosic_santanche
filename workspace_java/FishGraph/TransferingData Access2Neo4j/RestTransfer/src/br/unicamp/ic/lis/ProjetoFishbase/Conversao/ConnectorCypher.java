package br.unicamp.ic.lis.ProjetoFishbase.Conversao;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ConnectorCypher {
	public static String SERVER_ROOT_URI = "http://127.0.0.1:7474/db/data/"; //<----------------------- neo4j's server
	
	// to store a transaction for multiple queries 
	private Boolean first_transaction;
	// open transactions' address
	private String open_transaction;
	
	ConnectorCypher () {
		first_transaction = true;
		open_transaction = "";
	}
	
	// server responding or not
	public Boolean isOk() {
		try {
			WebResource resource = Client.create().resource(SERVER_ROOT_URI);
			ClientResponse response = resource.get(ClientResponse.class);

			Boolean ret = false;
			if(response.getStatus() == 200) //"OK"
				ret = true;

			response.close();
			return ret;
		}
		catch (Exception e) { //if the server doesnt reply
			//e.printStackTrace();
			return false;
		}
	}
	
	// sends a query keeping the transaction alive (defaults 60s)
	public URI sendQuery(String query) {
		String nodeEntryPointUri;
		
		// whether to open a new transaction or not
		if(first_transaction == false)
			nodeEntryPointUri = open_transaction;
		else
			nodeEntryPointUri = SERVER_ROOT_URI + "transaction";
		
		WebResource resource = Client.create().resource( nodeEntryPointUri );
		ClientResponse response = 
				resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( query )
                .post( ClientResponse.class );

		// check the server response, if there is error print, otherwise not
		String answer = response.getEntity(String.class);
		String error = answer.substring(answer.lastIndexOf('[')+1, answer.lastIndexOf(']'));
		if(error.length()>0)
			System.out.println( String.format(
			        "POST [%s] to [%s], status code [%d], returned data: "
	                + System.getProperty( "line.separator" ) + "%s",
	                query, nodeEntryPointUri, response.getStatus(),
	                answer ) );
		
		// update transaction information if needed
		if(first_transaction == true) {
			first_transaction = false;
			open_transaction = response.getLocation().toString();
		}
		final URI location = response.getLocation();
		
		response.close();
		return location;
	}
	
	// commit if there is an open transaction otherwise do nothing
	public void commit() {
		if(first_transaction == false) {
			String nodeEntryPointUri = open_transaction + "/commit";
			WebResource resource = Client.create().resource( nodeEntryPointUri );
			ClientResponse response = 
					resource.accept( MediaType.APPLICATION_JSON )
	                .type( MediaType.APPLICATION_JSON )
	                .entity( "" )
	                .post( ClientResponse.class );
			
			String answer = response.getEntity(String.class);

			String error = answer.substring(answer.lastIndexOf('[')+1, answer.lastIndexOf(']'));
			if(error.length()>0)
				System.out.println( String.format(
				        "POST to [%s], status code [%d], returned data: "
		                + System.getProperty( "line.separator" ) + "%s",
		                nodeEntryPointUri, response.getStatus(),
		                answer ) );
			
			first_transaction = true;
		}
			
	}
	
	//create index for the tables
	public void createIndexes() {
		String[] indexes = {
				"CREATE INDEX ON :Class(ClassNum)", "CREATE INDEX ON :Order(Ordnum)", "CREATE INDEX ON :Family(FamCode)", "CREATE INDEX ON :Genus(GenCode)", "CREATE INDEX ON :Species(SpecCode)",
				"CREATE INDEX ON :Country(C_CODE)", "CREATE INDEX ON :FAO(AreaCode)", "CREATE INDEX ON :Ecosystem(E_CODE)",
				"CREATE INDEX ON :Predator(PredatCode)", "CREATE INDEX ON :Key(KeyCode)" 
				};
		for(int i=0; i<indexes.length; i++) {
			String query = ""
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"" + indexes[i] + "\","
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		return;
	}
	
	// create a class with some information
	public URI createClass(String id, String name, String common_name) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"CREATE (n:Class {props}) RETURN n\","
				
				+ "\"parameters\" : {"
				+ "\"props\" : {"
				+ "\"ClassNum\": " + id + ", "
				+ "\"Class\": \"" + name + "\", "
				+ "\"CommonName\" : \"" + common_name + "\""
				+ "}}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI createOrder(String id, String name, String common_name, String class_id) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (c:Class) WHERE c.ClassNum = " + class_id + " CREATE UNIQUE (n:Order {props})-[:IS_A]->(c) RETURN n\","
				
				+ "\"parameters\" : {"
				+ "\"props\" : {"
				+ "\"Ordnum\": " + id + ", "
				+ "\"Order\": \"" + name + "\", "
				+ "\"CommonName\" : \"" + common_name + "\""
				+ "}}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI createFamily(String id, String name, String common_name, String order_id) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (c:Order) WHERE c.Ordnum = " + order_id + " CREATE UNIQUE (n:Family {props})-[:IS_A]->(c) RETURN n\","
				
				+ "\"parameters\" : {"
				+ "\"props\" : {"
				+ "\"FamCode\": " + id + ", "
				+ "\"Family\": \"" + name + "\", "
				+ "\"CommonName\" : \"" + common_name + "\""
				+ "}}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI createGenus(String id, String name, String family_id) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (c:Family) WHERE c.FamCode = " + family_id + " CREATE UNIQUE (n:Genus {props})-[:IS_A]->(c) RETURN n\","
				
				+ "\"parameters\" : {"
				+ "\"props\" : {"
				+ "\"GenCode\": " + id + ", "
				+ "\"GenName\": \"" + name + "\""
				+ "}}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI createSpeciesGen(String id, String name, String common_name, String genus_id) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (c:Genus) WHERE c.GenCode = " + genus_id + " CREATE UNIQUE (n:Species {props})-[:IS_A]->(c) RETURN n\","
				
				+ "\"parameters\" : {"
				+ "\"props\" : {"
				+ "\"SpecCode\": " + id + ", "
				+ "\"Species\": \"" + name + "\", "
				+ "\"FBname\": \"" + common_name + "\""
				+ "}}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI createSpeciesFam(String id, String name, String common_name, String fam_id) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (c:Family) WHERE c.FamCode = " + fam_id + " CREATE UNIQUE (n:Species {props})-[:IS_A]->(c) RETURN n\","
				
				+ "\"parameters\" : {"
				+ "\"props\" : {"
				+ "\"SpecCode\": " + id + ", "
				+ "\"Species\": \"" + name + "\", "
				+ "\"FBname\": \"" + common_name + "\""
				+ "}}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}	
	
	public URI createCountry(String code, String name) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"CREATE (n:Country {props}) RETURN n\","
				
				+ "\"parameters\" : {"
				+ "\"props\" : {"
				+ "\"C_CODE\": \"" + code + "\", "
				+ "\"PAESE\": \"" + name + "\""
				+ "}}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI createFAO(String code, String name) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"CREATE (n:FAO {props}) RETURN n\","
				
				+ "\"parameters\" : {"
				+ "\"props\" : {"
				+ "\"AreaCode\": \"" + code + "\", "
				+ "\"FAO\": \"" + name + "\""
				+ "}}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI createEcosystem(String code, String name) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"CREATE (n:Ecosystem {props}) RETURN n\","
				
				+ "\"parameters\" : {"
				+ "\"props\" : {"
				+ "\"E_CODE\": \"" + code + "\", "
				+ "\"EcosystemName\": \"" + name + "\""
				+ "}}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI relateCountryFAO(String C_CODE, String AreaCode) { //note que a query é direcional, é o jeito de fazer esta relação em cypher
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (c:Country), (f:FAO) WHERE c.C_CODE = '" + C_CODE + "' AND f.AreaCode = '" + AreaCode + "' CREATE (c)-[r:RELATES_TO]->(f) RETURN r\","
				
				+ "\"parameters\" : {"
				+ "}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI relateEcosystemCountry(String E_CODE, String C_CODE) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (e:Ecosystem), (c:Country) WHERE c.C_CODE = '" + C_CODE + "' AND e.E_CODE = '" + E_CODE + "' CREATE (c)-[r:RELATES_TO]->(e) RETURN r\","
				
				+ "\"parameters\" : {"
				+ "}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
		
	public URI relateEcosystemFAO(String E_CODE, String AreaCode) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (e:Ecosystem), (f:FAO) WHERE f.AreaCode = '" + AreaCode + "' AND e.E_CODE = '" + E_CODE + "' CREATE (f)-[r:RELATES_TO]->(e) RETURN r\","
				
				+ "\"parameters\" : {"
				+ "}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI createPredator(String SpecCode, String PredatCode, String PredatorName, String PredatorI, String PredatorII, String PredatorGroup) {
		String query = "" //cria o predador se não existir!
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MERGE (p:Predator {PredatCode: '" + PredatCode + "'})"
				+ "ON CREATE SET p.PredatorName = {PredatorName}, p.PredatorI = {PredatorI}, p.PredatorII = {PredatorII}, p.PredatorGroup = {PredatorGroup} "
				+ "RETURN p\", "

				+ "\"parameters\" : {"
					+ "\"PredatorName\" : \"" + PredatorName + "\", "
					+ "\"PredatorI\" : \"" + PredatorI + "\", "
					+ "\"PredatorII\" : \"" + PredatorII + "\", "
					+ "\"PredatorGroup\" : \"" + PredatorGroup + "\""
				+ "}"

				+ "} ]"
				+ "}";
		sendQuery(query);
		
		query = "" //cria a relação se não existir!
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (p:Predator {PredatCode: '" + PredatCode + "'}),(s:Species {SpecCode: " + SpecCode + "})"
				+ "MERGE(p)-[r:PREDATS]->(s)"
				+ "RETURN r\", "
				
				+ "\"parameters\" : {"
				+ "}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public void createKey(String KeyCode, String OrdNum, String FamCode, String AreaCode, String C_CODE, String E_CODE) {
		String query = "" //cria a chave se não existir
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MERGE (k:Key {KeyCode: " + KeyCode + "})"
				+ "RETURN k\", "

				+ "\"parameters\" : {"
				+ "}"

				+ "} ]"
				+ "}";
		sendQuery(query);
		
		if(OrdNum != "") {
			query = "" //cria a relação se não existir!
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"MATCH (k:Key {KeyCode: " + KeyCode + "}),(o:Order {OrdNum: " + OrdNum + "}) "
					+ "MERGE(k)-[r:REFERS_TO]->(o) "
					+ "RETURN r\", "
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		if(FamCode != "") {
			query = "" //cria a relação se não existir!
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"MATCH (k:Key {KeyCode: " + KeyCode + "}),(f:Family {FamCode: " + FamCode + "}) "
					+ "MERGE(k)-[r:REFERS_TO]->(f) "
					+ "RETURN r\", "
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		if(AreaCode != "") {
			query = "" //cria a relação se não existir!
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"MATCH (k:Key {KeyCode: " + KeyCode + "}),(f:FAO {AreaCode: '" + AreaCode + "'}) "
					+ "MERGE(k)-[r:REFERS_TO]->(f) "
					+ "RETURN r\", "
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		if(C_CODE != "") {
			query = "" //cria a relação se não existir!
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"MATCH (k:Key {KeyCode: " + KeyCode + "}),(c:Country {C_CODE: '" + C_CODE + "'}) "
					+ "MERGE(k)-[r:REFERS_TO]->(c) "
					+ "RETURN r\", "
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		if(E_CODE != "") {
			query = "" //cria a relação se não existir!
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"MATCH (k:Key {KeyCode: " + KeyCode + "}),(e:Ecosystem {E_CODE: '" + E_CODE + "'}) "
					+ "MERGE(k)-[r:REFERS_TO]->(e) "
					+ "RETURN r\", "
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		
		return;
	}
	
	public void createKeyQuestion(String KeyCode, String OrdNum, String FamCode, String GenCode, String SpecCode) {
		String query = "" //cria a chave se não existir
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MERGE (k:Key {KeyCode: " + KeyCode + "})"
				+ "RETURN k\", "

				+ "\"parameters\" : {"
				+ "}"

				+ "} ]"
				+ "}";
		sendQuery(query);
		
		if(OrdNum != "") {
			query = "" //cria a relação se não existir!
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"MATCH (k:Key {KeyCode: " + KeyCode + "}),(o:Order {OrdNum: " + OrdNum + "}) "
					+ "MERGE(k)-[r:QUESTION_TO]->(o) "
					+ "RETURN r\", "
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		if(FamCode != "") {
			query = "" //cria a relação se não existir!
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"MATCH (k:Key {KeyCode: " + KeyCode + "}),(f:Family {FamCode: " + FamCode + "}) "
					+ "MERGE(k)-[r:QUESTION_TO]->(f) "
					+ "RETURN r\", "
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		if(GenCode != "") {
			query = "" //cria a relação se não existir!
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"MATCH (k:Key {KeyCode: " + KeyCode + "}),(g:Genus {GenCode: " + GenCode + "}) "
					+ "MERGE(k)-[r:QUESTION_TO]->(g) "
					+ "RETURN r\", "
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		if(SpecCode != "") {
			query = "" //cria a relação se não existir!
					+ "{"
					+ "\"statements\" : [ {"
					+ "\"statement\" : \"MATCH (k:Key {KeyCode: " + KeyCode + "}),(s:Species {SpecCode: " + SpecCode + "}) "
					+ "MERGE(k)-[r:QUESTION_TO]->(s) "
					+ "RETURN r\", "
					
					+ "\"parameters\" : {"
					+ "}"
					
					+ "} ]"
					+ "}";
			sendQuery(query);
		}
		
		return;
	}
	
	
	public URI relateSpecieCountry(String C_CODE, String SpecCode) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (c:Country), (s:Species) WHERE c.C_CODE = '" + C_CODE + "' AND s.SpecCode = " + SpecCode + " CREATE (s)-[r:HABITATS]->(c) RETURN r\","
				
				+ "\"parameters\" : {"
				+ "}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI relateSpecieFAO(String AreaCode, String SpecCode) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (f:FAO), (s:Species) WHERE f.AreaCode = '" + AreaCode + "' AND s.SpecCode = " + SpecCode + " CREATE (s)-[r:HABITATS]->(f) RETURN r\","
				
				+ "\"parameters\" : {"
				+ "}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
	
	public URI relateSpecieEcosystem(String E_CODE, String SpecCode) {
		String query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (e:Ecosystem), (s:Species) WHERE e.E_CODE = '" + E_CODE + "' AND s.SpecCode = " + SpecCode + " CREATE (s)-[r:HABITATS]->(e) RETURN r\","
				
				+ "\"parameters\" : {"
				+ "}"
				
				+ "} ]"
				+ "}";
		return sendQuery(query);
	}
}

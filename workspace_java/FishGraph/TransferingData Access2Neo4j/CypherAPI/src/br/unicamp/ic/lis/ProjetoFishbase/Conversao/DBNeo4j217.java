package br.unicamp.ic.lis.ProjetoFishbase.Conversao;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;


public class DBNeo4j217 implements DBNeo4j {	
	private static final String DB_PATH = "/home/vroth/db217.graphdb";
	
	GraphDatabaseService graphDb;
	ExecutionEngine engine;
	
	DBNeo4j217() {
		createDb();
	}
	
    void createDb()
    {
    	System.out.print("Starting database ... ");
        // START SNIPPET: startDb
        graphDb = new GraphDatabaseFactory()
        		.newEmbeddedDatabaseBuilder(DB_PATH)
        		.newGraphDatabase();
        registerShutdownHook( graphDb );
        // END SNIPPET: startDb
        engine = new ExecutionEngine(graphDb);
        System.out.println("OK");
    }

    void shutDown()
    {
        System.out.println();
        System.out.print( "Shutting down database ... " );
        // START SNIPPET: shutdownServer
        graphDb.shutdown();
        // END SNIPPET: shutdownServer
        System.out.println("OK");
    }
	
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    
    
    public void sendQuery(String query) {
    	try {
    		query = query.replaceAll("''", "'").replaceAll("'\"", "'").replaceAll("\"'", "'");
    		Transaction tx = graphDb.beginTx();
            //String query = "MATCH (n:Species)-[]-(p) return n, p";
            /*ExecutionResult result = */engine.execute(query);
            //System.out.println(query);
            //System.out.println(result.dumpToString());
        	
            tx.success();
            tx.close();
        }
        catch(Exception e) {
        	e.printStackTrace();
        	System.exit(-1);
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
			sendQuery(indexes[i]);
		}
	}
	
	// create a class with some information
	public void createClass(String id, String name, String common_name) {
		String query = "MERGE (c:Class {ClassNum: " + id  + ", Class: '" + name  + "', CommonName: '" + common_name + "'}) RETURN c";
		sendQuery(query);
	}
	
	public void createOrder(String id, String name, String common_name, String class_id) {
		String query = "MERGE (o:Order {OrdNum: " + id + ", Order : '" + name + "', CommonName: '" + common_name + "'}) RETURN o";
		sendQuery(query);

		query = "MATCH (c:Class {ClassNum: " + class_id + "}),(o:Order {OrdNum: " + id + "})"
				+ " MERGE(o)-[r:IS_A]->(c) RETURN r";
		sendQuery(query);
	}
	
	public void createFamily(String id, String name, String common_name, String order_id) {
		String query = "MERGE (f:Family {FamCode: " + id + ", Family: '" + name + "', CommonName: '" + common_name + "'}) RETURN f";
		sendQuery(query);
		
		query = "MATCH (o:Order {OrdNum: " + order_id + "}),(f:Family {FamCode: " + id + "})"
				+ " MERGE(f)-[r:IS_A]->(o) RETURN r";
		sendQuery(query);
	}
	
	public void createGenus(String id, String name, String family_id) {
		String query = "MERGE (g:Genus {GenCode: " + id + ", GenName: '" + name + "'}) RETURN g";
		sendQuery(query);
		
		query = "MATCH (f:Family {FamCode: " + family_id + "}),(g:Genus {GenCode: " + id + "})"
				+ " MERGE(g)-[r:IS_A]->(f) RETURN r";
		sendQuery(query);
	}
	
	public void createSpeciesGen(String id, String name, String common_name, String genus_id) {
		String query = "MERGE (s:Species {SpecCode: " + id + ", Species: '" + name + "', FBname: \"" + common_name + "\"}) RETURN s";
		sendQuery(query);
		
		query = "MATCH (g:Genus {GenCode: " + genus_id + "}),(s:Species {SpecCode: " + id + "})"
				+ " MERGE(s)-[r:IS_A]->(g) RETURN r";
		sendQuery(query);
	}
	
	public void createSpeciesFam(String id, String name, String common_name, String fam_id) {
		String query = "MERGE (s:Species {SpecCode: " + id + ", Species: '" + name + "', FBname: '" + common_name + "'}) RETURN s";
		sendQuery(query);
		
		query = "MATCH (f:Family {FamCode: " + fam_id + "}),(s:Species {SpecCode: " + id + "})"
				+ "MERGE(s)-[r:IS_A]->(f) RETURN r";
		sendQuery(query);
	}	
	
	public void createCountry(String code, String name) {
		String query = "MERGE (c:Country {C_CODE: '" + code  + "', PAESE: \"" + name  + "\"}) RETURN c";
		sendQuery(query);
	}
	
	public void createFAO(String code, String name) {
		String query = "MERGE (f:FAO {AreaCode: '" + code  + "', FAO: \"" + name  + "\"}) RETURN f";
		sendQuery(query);
	}
	
	public void createEcosystem(String code, String name) {
		String query = "MERGE (e:Ecosystem {E_CODE: '" + code  + "', EcosystemName: \"" + name  + "\"}) RETURN e";
		sendQuery(query);
	}
	
	public void relateCountryFAO(String C_CODE, String AreaCode) {
		String query = "MATCH (c:Country), (f:FAO) WHERE c.C_CODE = '" + C_CODE + "' AND f.AreaCode = '" + AreaCode + "' CREATE (c)-[r:RELATES_TO]->(f) RETURN r";
		sendQuery(query);
	}
	
	public void relateEcosystemCountry(String E_CODE, String C_CODE) {
		String query = "MATCH (e:Ecosystem), (c:Country) WHERE c.C_CODE = '" + C_CODE + "' AND e.E_CODE = '" + E_CODE + "' CREATE (c)-[r:RELATES_TO]->(e) RETURN r";
		sendQuery(query);
	}
		
	public void relateEcosystemFAO(String E_CODE, String AreaCode) {
		String query = "MATCH (e:Ecosystem), (f:FAO) WHERE f.AreaCode = '" + AreaCode + "' AND e.E_CODE = '" + E_CODE + "' CREATE (f)-[r:RELATES_TO]->(e) RETURN r";
		sendQuery(query);
	}
	
	public void createPredator(String SpecCode, String PredatCode, String PredatorName, String PredatorI, String PredatorII, String PredatorGroup) {
		//cria o predator
		String query = "MERGE (p:Predator {PredatCode: '" + PredatCode + "'})"
				+ "ON CREATE SET p.PredatorName = '" + PredatorName + "', p.PredatorI = '" + PredatorI +"', p.PredatorII = '" + PredatorII +"', p.PredatorGroup = '" + PredatorGroup + "' "
				+ "RETURN p";
		sendQuery(query);
		
		//cria a relação
		query = "MATCH (p:Predator {PredatCode: '" + PredatCode + "'}),(s:Species {SpecCode: " + SpecCode + "})"
				+ "MERGE(p)-[r:PREDATS]->(s)"
				+ "RETURN r";
		sendQuery(query);
	}
	
	public void createKey(String KeyCode, String OrdNum, String FamCode, String AreaCode, String C_CODE, String E_CODE) {
		String query = "MERGE (k:Key {KeyCode: " + KeyCode + "})"
				+ "RETURN k";
		sendQuery(query);
		
		if(OrdNum != "") {
			query = "MATCH (k:Key {KeyCode: " + KeyCode + "}),(o:Order {OrdNum: " + OrdNum + "}) "
					+ "MERGE(k)-[r:REFERS_TO]->(o) "
					+ "RETURN r";
			sendQuery(query);
		}
		if(FamCode != "") {
			query = "MATCH (k:Key {KeyCode: " + KeyCode + "}),(f:Family {FamCode: " + FamCode + "}) "
					+ "MERGE(k)-[r:REFERS_TO]->(f) "
					+ "RETURN r";
			sendQuery(query);
		}
		if(AreaCode != "") {
			query = "MATCH (k:Key {KeyCode: " + KeyCode + "}),(f:FAO {AreaCode: '" + AreaCode + "'}) "
					+ "MERGE(k)-[r:REFERS_TO]->(f) "
					+ "RETURN r";
			sendQuery(query);
		}
		if(C_CODE != "") {
			query = "MATCH (k:Key {KeyCode: " + KeyCode + "}),(c:Country {C_CODE: '" + C_CODE + "'}) "
					+ "MERGE(k)-[r:REFERS_TO]->(c) "
					+ "RETURN r";
			sendQuery(query);
		}
		if(E_CODE != "") {
			query = "MATCH (k:Key {KeyCode: " + KeyCode + "}),(e:Ecosystem {E_CODE: '" + E_CODE + "'}) "
					+ "MERGE(k)-[r:REFERS_TO]->(e) "
					+ "RETURN r";
			sendQuery(query);
		}
		
		return;
	}
	
	public void createKeyQuestion(String KeyCode, String OrdNum, String FamCode, String GenCode, String SpecCode) {
		String query = "MERGE (k:Key {KeyCode: " + KeyCode + "})"
				+ "RETURN k";
		sendQuery(query);
		
		if(OrdNum != "") {
			query = "MATCH (k:Key {KeyCode: " + KeyCode + "}),(o:Order {OrdNum: " + OrdNum + "}) "
					+ "MERGE(k)-[r:QUESTION_TO]->(o) "
					+ "RETURN r";
			sendQuery(query);
		}
		if(FamCode != "") {
			query = "MATCH (k:Key {KeyCode: " + KeyCode + "}),(f:Family {FamCode: " + FamCode + "}) "
					+ "MERGE(k)-[r:QUESTION_TO]->(f) "
					+ "RETURN r";
			sendQuery(query);
		}
		if(GenCode != "") {
			query = "MATCH (k:Key {KeyCode: " + KeyCode + "}),(g:Genus {GenCode: " + GenCode + "}) "
					+ "MERGE(k)-[r:QUESTION_TO]->(g) "
					+ "RETURN r";
			sendQuery(query);
		}
		if(SpecCode != "") {
			query = "MATCH (k:Key {KeyCode: " + KeyCode + "}),(s:Species {SpecCode: " + SpecCode + "}) "
					+ "MERGE(k)-[r:QUESTION_TO]->(s) "
					+ "RETURN r";
			sendQuery(query);
		}
		
		return;
	}
	
	public void relateSpecieCountry(String C_CODE, String SpecCode) {
		String query = "MATCH (c:Country), (s:Species) WHERE c.C_CODE = '" + C_CODE + "' AND s.SpecCode = " + SpecCode + " CREATE (s)-[r:HABITATS]->(c) RETURN r";
		sendQuery(query);
	}
	
	public void relateSpecieFAO(String AreaCode, String SpecCode) {
		String query = "MATCH (f:FAO), (s:Species) WHERE f.AreaCode = '" + AreaCode + "' AND s.SpecCode = " + SpecCode + " CREATE (s)-[r:HABITATS]->(f) RETURN r";
		sendQuery(query);
	}
	
	public void relateSpecieEcosystem(String E_CODE, String SpecCode) {
		String query = "MATCH (e:Ecosystem), (s:Species) WHERE e.E_CODE = '" + E_CODE + "' AND s.SpecCode = " + SpecCode + " CREATE (s)-[r:HABITATS]->(e) RETURN r";
		sendQuery(query);
	}
}

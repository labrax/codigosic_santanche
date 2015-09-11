package br.unicamp.ic.fishgraph.algoritms;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

public class Database
{
	private GraphDatabaseService graphDb;
	private ExecutionEngine engine;
	
	private static String DB_PATH = "/home/vroth/workspace/testen4oj/graph.db";
	
	long last_querytime = 0;
	
	public Database()
	{
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
		
		engine = new ExecutionEngine( graphDb );
	}
	
	
    private void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    
    public ExecutionResult sendQuery(String query) {
    	//String query = "MATCH (s:Species),(s2:Species) WHERE (s)-[:PREDATS]-(s2) RETURN s, s2 LIMIT 1000";
    	long inicio = System.currentTimeMillis();
    	try {
    		query = query.replaceAll("''", "'").replaceAll("'\"", "'").replaceAll("\"'", "'");
    		// Transaction tx = graphDb.beginTx();
            ExecutionResult result = engine.execute(query);
        	
            // tx.success();
            // tx.close();
            return result;
        }
        catch(Exception e) {
        	e.printStackTrace();
        	System.exit(-1);
        }
    	last_querytime  = System.currentTimeMillis() - inicio;
    	return null;
    }
    
    public long getLastQueryTime()
    {
    	return last_querytime;
    }
    
    public GraphDatabaseService getGraphDatabaseService()
    {
    	return graphDb;
    }
    
    @Deprecated //não funciona
    /*
		IndexHits<Node> ih = db.getResults("Species", "*:*");
		System.out.println(db.getLastQueryTime() + "ms");
		
		for(Node node : ih)
		{
			System.out.println(node.getId() + ": " + node.getProperty("SpecCode") + node.getProperty("Species"));
		}
		ih.close();
     */
    public IndexHits<Node> getResults(String node_name, String query)
    {
    	IndexHits<Node> ih = null;
    	long inicio = System.currentTimeMillis();
    	try
    	{
	    	Transaction tx = graphDb.beginTx();
	    	Index<Node> index = graphDb.index().forNodes(node_name);
			ih = index.query(query);
			tx.success();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	last_querytime  = System.currentTimeMillis() - inicio;
		return ih;
    }
}

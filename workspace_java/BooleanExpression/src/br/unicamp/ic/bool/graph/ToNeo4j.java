package br.unicamp.ic.bool.graph;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import br.unicamp.ic.bool.Main;

public class ToNeo4j implements ConnectDB{
	BatchInserter inserter = null;
	BatchInserterIndexProvider indexProvider;
	BatchInserterIndex index;
	
	Label nodeLabel = DynamicLabel.label("Node");
	RelationshipType likes = DynamicRelationshipType.withName("LIKES");
	
	public ToNeo4j()
	{
		try
		{
		    inserter = BatchInserters.inserter(
		            new File( Main.DB_PATH ).getAbsolutePath() );
		    
		    indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		    index = indexProvider.nodeIndex("Node-exact", MapUtil.stringMap( "type", "exact" ));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void flush_index()
	{
		index.flush();
	}

    public void shutdown()
    {
    	flush_index();
        System.out.println();
        System.out.print( "Shutting down database ... " );
        if(inserter != null)
        	inserter.shutdown();
        if(indexProvider != null)
        	indexProvider.shutdown();
        System.out.println("OK");
    }
	
	public void createIndexes()
	{
	    inserter.createDeferredSchemaIndex( nodeLabel ).on( "id" ).create();
	}
	
	public void createNode(Integer id)
	{
		Map<String, Object> properties = new HashMap<>();
		properties.put("id", id);
		Long created = inserter.createNode(properties, nodeLabel);
		index.add(created, properties);
	}
	
	public void createRelationship(Integer source, Integer target)
	{
		inserter.createRelationship(index.get("id", source).getSingle(), index.get("id", target).getSingle(), likes, null);
	}
}

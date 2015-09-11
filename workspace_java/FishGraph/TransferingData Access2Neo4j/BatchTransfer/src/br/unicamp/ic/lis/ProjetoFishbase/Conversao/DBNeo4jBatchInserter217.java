package br.unicamp.ic.lis.ProjetoFishbase.Conversao;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.*;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
//import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;


public class DBNeo4jBatchInserter217 implements DBNeo4j{	
	private static final String DB_PATH = "/home/vroth/batchinserter217";
	
	BatchInserter inserter = null;
	BatchInserterIndexProvider indexProvider;
	BatchInserterIndex index;
	
    Label classLabel = DynamicLabel.label("Class");
    Label orderLabel = DynamicLabel.label("Order");
    Label familyLabel = DynamicLabel.label("Family");
    Label genusLabel = DynamicLabel.label("Genus");
    Label speciesLabel = DynamicLabel.label("Species");
    RelationshipType is_a = DynamicRelationshipType.withName( "IS_A" );
    
    Label countryLabel = DynamicLabel.label("Country");
    Label faoLabel = DynamicLabel.label("FAO");
    Label ecosystemLabel = DynamicLabel.label("Ecosystem");
    RelationshipType relates_to = DynamicRelationshipType.withName( "RELATES_TO" );
    
    RelationshipType habitats = DynamicRelationshipType.withName( "HABITATS" );
    
    Label predatorLabel = DynamicLabel.label("Predator");
    RelationshipType predats = DynamicRelationshipType.withName( "PREDATS" );
    Label keyLabel = DynamicLabel.label("Key");
    RelationshipType refers_to = DynamicRelationshipType.withName( "REFERS_TO" );
    RelationshipType question_to = DynamicRelationshipType.withName( "QUESTION_TO" );
	
	DBNeo4jBatchInserter217() throws Exception {
	//	FileUtils.deleteRecursively( new File(DB_PATH) );
	    inserter = BatchInserters.inserter(
	            new File( DB_PATH ).getAbsolutePath() );
	    
	    indexProvider = new LuceneBatchInserterIndexProvider(inserter);
	    index = indexProvider.nodeIndex("Node-exact", MapUtil.stringMap( "type", "exact" ));
	}
	
	public void flush_index() {
		index.flush();
	}

    public void shutdown() {
    	flush_index();
        System.out.println();
        System.out.print( "Shutting down database ... " );
        if(inserter != null)
        	inserter.shutdown();
        if(indexProvider != null)
        	indexProvider.shutdown();
        System.out.println("OK");
    }
    
	//create index for the tables
	public void createIndexes() {
	    inserter.createDeferredSchemaIndex( classLabel ).on( "ClassNum" ).create();
	    inserter.createDeferredSchemaIndex( orderLabel ).on( "Ordnum" ).create();
	    inserter.createDeferredSchemaIndex( familyLabel ).on( "FamCode" ).create();
	    inserter.createDeferredSchemaIndex( genusLabel ).on( "GenCode" ).create();
	    inserter.createDeferredSchemaIndex( speciesLabel ).on( "SpecCode" ).create();
	    
	    inserter.createDeferredSchemaIndex( countryLabel ).on( "C_CODE" ).create();
	    inserter.createDeferredSchemaIndex( faoLabel ).on( "AreaCode" ).create();
	    inserter.createDeferredSchemaIndex( ecosystemLabel ).on( "E_CODE" ).create();
	    
	    inserter.createDeferredSchemaIndex( predatorLabel ).on( "PredatCode" ).create();
	    inserter.createDeferredSchemaIndex( keyLabel ).on( "KeyCode" ).create();
	}
	
	public void createClass(String id, String name, String common_name) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("ClassNum", id);
		properties.put("Class", name);
		properties.put("CommonName", common_name);
		Long created = inserter.createNode(properties, classLabel);
		index.add(created, properties);
	}
	
	public void createOrder(String id, String name, String common_name, String class_id) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("OrdNum", id);
		properties.put("Order", name);
		properties.put("CommonName", common_name);
		Long created = inserter.createNode(properties, orderLabel);
		index.add(created, properties);

		inserter.createRelationship(created, index.get("ClassNum", class_id).getSingle(), is_a, null);
	}
	
	public void createFamily(String id, String name, String common_name, String order_id) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("FamCode", id);
		if(!name.equals(""))
			properties.put("Family", name);
		if(common_name != null)
			properties.put("CommonName", common_name);
		Long created = inserter.createNode(properties, familyLabel);
		index.add(created, properties);

		inserter.createRelationship(created, index.get("OrdNum", order_id).getSingle(), is_a, null);
	}
	
	public void createGenus(String id, String name, String family_id) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("GenCode", id);
		properties.put("GenName", name);
		Long created = inserter.createNode(properties, genusLabel);
		index.add(created, properties);

		if(family_id != null)
			inserter.createRelationship(created, index.get("FamCode", family_id).getSingle(), is_a, null);
	}
	
	public void createSpeciesGen(String id, String name, String common_name, String genus_id) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("SpecCode", id);
		properties.put("Species", name);
		if(common_name != null)
			properties.put("FBname", common_name);
		Long created = inserter.createNode(properties, speciesLabel);
		index.add(created, properties);

		if(genus_id != null)
			inserter.createRelationship(created, index.get("GenCode", genus_id).getSingle(), is_a, null);
	}
	
	public void createSpeciesFam(String id, String name, String common_name, String fam_id) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("SpecCode", id);
		if(name != null)
			properties.put("Species", name);
		if(common_name != null)
			properties.put("FBname", common_name);
		Long created = inserter.createNode(properties, speciesLabel);
		index.add(created, properties);

		if(fam_id != null)
			inserter.createRelationship(created, index.get("FamCode", fam_id).getSingle(), is_a, null);
	}	
	
	public void createCountry(String code, String name) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("C_CODE", code);
		properties.put("PAESE", name);
		Long created = inserter.createNode(properties, countryLabel);
		index.add(created, properties);
	}
	
	public void createFAO(String code, String name) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("AreaCode", code);
		properties.put("FAO", name);
		Long created = inserter.createNode(properties, faoLabel);
		index.add(created, properties);
	}
	
	public void createEcosystem(String code, String name) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("E_CODE", code);
		if(name != null)
			properties.put("EcosystemName", name);
		Long created = inserter.createNode(properties, ecosystemLabel);
		index.add(created, properties);
	}
	
	public void relateCountryFAO(String C_CODE, String AreaCode) {
		if(index.get("C_CODE", C_CODE).getSingle() != null && index.get("AreaCode", AreaCode).getSingle() != null)
			inserter.createRelationship(index.get("C_CODE", C_CODE).getSingle(), index.get("AreaCode", AreaCode).getSingle(), relates_to, null);
	}
	
	public void relateEcosystemCountry(String E_CODE, String C_CODE) {
		if(index.get("E_CODE", E_CODE).getSingle() != null && index.get("C_CODE", C_CODE).getSingle() != null)
			inserter.createRelationship(index.get("E_CODE", E_CODE).getSingle(), index.get("C_CODE", C_CODE).getSingle(), relates_to, null);
	}
		
	public void relateEcosystemFAO(String E_CODE, String AreaCode) {
		if(index.get("E_CODE", E_CODE).getSingle() != null && index.get("AreaCode", AreaCode).getSingle() != null)
			inserter.createRelationship(index.get("E_CODE", E_CODE).getSingle(), index.get("AreaCode", AreaCode).getSingle(), relates_to, null);
	}
	
	public void createPredator(String SpecCode, String PredatCode, String PredatorName, String PredatorI, String PredatorII, String PredatorGroup) {
		long search = 0;
		if(PredatCode != null)
			if(index.get("SpecCode", PredatCode) != null)
				if(index.get("SpecCode", PredatCode).getSingle() != null)
					search = index.get("SpecCode", PredatCode).getSingle();
		
		if(search != 0)
		{
			inserter.createRelationship(search, index.get("SpecCode", SpecCode).getSingle(), predats, null);
		}
		else
		{
			Map<String, Object> properties = new HashMap<>();
			if(PredatCode != null)
				properties.put("PredatCode", PredatCode);
			if(PredatorName != null)
				properties.put("PredatorName", PredatorName);
			if(PredatorI != null)
				properties.put("PredatorI", PredatorI);
			if(PredatorII != null)
				properties.put("PredatorII", PredatorII);
			if(PredatorGroup != null)
				properties.put("PredatorGroup", PredatorGroup);
			Long created = inserter.createNode(properties, predatorLabel);
			
			inserter.createRelationship(created, index.get("SpecCode", SpecCode).getSingle(), predats, null);
		}
	}
	
	public void createKey(String KeyCode, String OrdNum, String FamCode, String AreaCode, String C_CODE, String E_CODE) {
		Long created = index.get("KeyCode", KeyCode).getSingle();
		if(created == null) {
			Map<String, Object> properties = new HashMap<>();
			properties.put("KeyCode", KeyCode);
			created = inserter.createNode(properties, keyLabel);
			index.add(created, properties);
		}
		
		if(OrdNum != null) {
			if(index.get("OrdNum", OrdNum).getSingle() != null)
				inserter.createRelationship(created, index.get("OrdNum", OrdNum).getSingle(), refers_to, null);
		}
		if(FamCode != null) {
			if(index.get("FamCode", FamCode).getSingle() != null)
				inserter.createRelationship(created, index.get("FamCode", FamCode).getSingle(), refers_to, null);
		}
		if(AreaCode != null) {
			if(index.get("AreaCode", AreaCode).getSingle() != null)
				inserter.createRelationship(created, index.get("AreaCode", AreaCode).getSingle(), refers_to, null);
		}
		if(C_CODE != null) {
			if(index.get("C_CODE", C_CODE).getSingle() != null)
				inserter.createRelationship(created, index.get("C_CODE", C_CODE).getSingle(), refers_to, null);
		}
		if(E_CODE != null) {
			if(index.get("E_CODE", E_CODE).getSingle() != null)
				inserter.createRelationship(created, index.get("E_CODE", E_CODE).getSingle(), refers_to, null);
		}
		
		return;
	}
	
	public void createKeyQuestion(String KeyCode, String OrdNum, String FamCode, String GenCode, String SpecCode) {
		Long created = index.get("KeyCode", KeyCode).getSingle();
		if(created == null) {
			Map<String, Object> properties = new HashMap<>();
			properties.put("KeyCode", KeyCode);
			created = inserter.createNode(properties, keyLabel);
			index.add(created, properties);
		}
		
		if(OrdNum != null) {
			if(index.get("OrdNum", OrdNum).getSingle() != null)
				inserter.createRelationship(created, index.get("OrdNum", OrdNum).getSingle(), question_to, null);
		}
		if(FamCode != null) {
			if(index.get("FamCode", FamCode).getSingle() != null)
				inserter.createRelationship(created, index.get("FamCode", FamCode).getSingle(), question_to, null);
		}
		if(GenCode != null) {
			if(index.get("GenCode", GenCode).getSingle() != null)
				inserter.createRelationship(created, index.get("GenCode", GenCode).getSingle(), question_to, null);
		}
		if(SpecCode != null) {
			if(index.get("SpecCode", SpecCode).getSingle() != null)
				inserter.createRelationship(created, index.get("SpecCode", SpecCode).getSingle(), question_to, null);
		}
		
		return;
	}
	
	public void relateSpecieCountry(String C_CODE, String SpecCode) {
		if(index.get("SpecCode", SpecCode).getSingle() != null && index.get("C_CODE", C_CODE).getSingle() != null)
			inserter.createRelationship(index.get("SpecCode", SpecCode).getSingle(), index.get("C_CODE", C_CODE).getSingle(), habitats, null);
	}
	
	public void relateSpecieFAO(String AreaCode, String SpecCode) {
		if(index.get("SpecCode", SpecCode).getSingle() != null && index.get("AreaCode", AreaCode).getSingle() != null)
			inserter.createRelationship(index.get("SpecCode", SpecCode).getSingle(), index.get("AreaCode", AreaCode).getSingle(), habitats, null);
	}
	
	public void relateSpecieEcosystem(String E_CODE, String SpecCode) {
		if (index.get("SpecCode", SpecCode).getSingle() != null && index.get("E_CODE", E_CODE).getSingle() != null)
			inserter.createRelationship(index.get("SpecCode", SpecCode).getSingle(), index.get("E_CODE", E_CODE).getSingle(), habitats, null);
	}
}

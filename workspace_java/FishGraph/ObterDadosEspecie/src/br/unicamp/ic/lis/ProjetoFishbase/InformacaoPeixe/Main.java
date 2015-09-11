package br.unicamp.ic.lis.ProjetoFishbase.InformacaoPeixe;

import org.json.*;


public class Main {
	ConnectorCypher connCyp = null;
	
	public static void main(String [] args) {
		Main a = new Main();
		
		//System.out.println("Starting...");
		
		long start = System.currentTimeMillis();
		a.start();
		
		// test neo4j node structure
		a.test();
		
		a.end();
		long end = System.currentTimeMillis();

		//System.out.println("Finished after " + ((end - start) / 1000d) + " seconds.");
	}
	
	public void start() {
		// first neo4j connection because its faster
		connCyp = new ConnectorCypher();
		if(connCyp.isOk() == true) {
			//System.out.println("Neo4j: OK!");
		}
		else {
			System.out.println("Neo4j: NOT OK!");
			System.out.println("Exited.");
			System.exit(-1);
		}
		connCyp.commit();
	}
	
	public void end() {
		connCyp.commit();
	}
	
	public void test() {
		//SpecCode | Species
		JSONArray arr = connCyp.sendQuery("{\"statements\":[{\"statement\":\"MATCH (s:Species) RETURN MAX(s.SpecCode)\"}],\"parameters\":{}}");
		Integer amount_species = Integer.parseInt(arr.getJSONObject(0).getJSONArray("row").get(0).toString());
		//System.out.println(amount_species);
		for(int i=0; i<amount_species; i++) {
			if(i == (amount_species-1))
				connCyp.getInformationFromSpecie(amount_species-(amount_species % 20), amount_species);
			else if((i % 20) == 0)
				connCyp.getInformationFromSpecie(i-20, i);
		}
		
		//connCyp.getInformationFromSpecie("", "grypus");
	}
	
}

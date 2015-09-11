package br.unicamp.ic.lis.ProjetoFishbase.InformacaoPeixe;

import org.json.*;
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
	public JSONArray sendQuery(String query) {
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

		String src = response.getEntity(String.class);
		//System.out.println(src);
		
		JSONObject obj = new JSONObject(src);
		
		JSONArray errArr = obj.getJSONArray("errors");
		//System.out.println("Number of errors is: " + errArr.length());
		for(int i=0; i<errArr.length(); i++) {
			System.out.println("Err: " + errArr.getJSONObject(i).toString());
		}

		JSONArray arr = obj.getJSONArray("results").getJSONObject(0).getJSONArray("data");
		
		// update transaction information if needed
		if(first_transaction == true) {
			first_transaction = false;
			open_transaction = response.getLocation().toString();
		}
		
		response.close();
		return arr;
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
	
	public void getInformationFromSpecie(Integer min_inclusive, Integer max) {
		String query;
		query = ""
				+ "{"
				+ "\"statements\" : [ {"
				+ "\"statement\" : \"MATCH (s:Species)-[]-(g:Genus)-[]-(f:Family)-[]-(o:Order)-[]-(c:Class) WHERE s.SpecCode >= " + min_inclusive + " AND s.SpecCode < " + max + " OPTIONAL MATCH (s:Species)-[]-(f:Family)-[]-(o:Order)-[]-(c:Class) WHERE s.SpecCode >= " + min_inclusive + " AND s.SpecCode < " + max + " RETURN s, g, f, o, c\","
				
				+ "\"parameters\" : {"
				+ "}"
				
				+ "} ]"
				+ "}";
		
		JSONArray arr = sendQuery(query);
		if(arr.length() != 0) 
		{
			int[] scores = new int[arr.length()];
			
		    //System.out.println("Number of results is: " + arr.length());
			for (int i = 0; i < arr.length(); i++)
			{
			    JSONArray result = arr.getJSONObject(i).getJSONArray("row");
			   // System.out.println("row " + result.toString());
			    
			    for(int j = 0; j < result.length(); j++) {
			    	if(j == 1) {
			    		try {
			    			result.getJSONObject(j);
			    		}
			    		catch(Exception e) {
			    			continue;
			    		}
			    	}
			    	if(result.getJSONObject(j).has("SpecCode"))
			    		scores[i]++;
			    	if(result.getJSONObject(j).has("GenCode"))
			    		scores[i]++;
			    	if(result.getJSONObject(j).has("FamCode"))
			    		scores[i]++;
			    	if(result.getJSONObject(j).has("Ordnum"))
			    		scores[i]++;
			    	if(result.getJSONObject(j).has("ClassNum"))
			    		scores[i]++;
			    }
			}
			
			int maxIndex = 0; // "BEST MATCH" --- not used
			for (int i = 1; i < scores.length; i++) {
				int newnumber = scores[i];
				if ((newnumber > scores[maxIndex]))
					maxIndex = i;
			}
			
			for(int i = 0; i < scores.length; i++) {
				if(scores[i] < 2)
					continue;
				//System.out.println();

			    JSONArray result = arr.getJSONObject(i).getJSONArray("row");
			   // System.out.println("row " + result.toString());
			    for(int j = 0; j < result.length(); j++) {
			    	try {
				    	//System.out.print("SpecCode: " + result.getJSONObject(j).getLong("SpecCode"));
				    	//System.out.print(", Species: " + result.getJSONObject(j).getString("Species"));
				    	//System.out.println(", FBname: " + result.getJSONObject(j).getString("FBname"));
			    		System.out.print(result.getJSONObject(j).getString("Species") + " ");
			    		continue;
			    	}
			    	catch (Exception e) {
			    	}
			    	try {
				    	//System.out.print("GenCode: " + result.getJSONObject(j).getLong("GenCode"));
				    	//System.out.print(", GenName: " + result.getJSONObject(j).getString("GenName"));
			    		System.out.print(result.getJSONObject(j).getString("GenName") + " ");
			    		continue;
			    	}
			    	catch (Exception e) {
			    	}
			    	try {
				    	//System.out.print("FamCode: " + result.getJSONObject(j).getLong("FamCode"));
				    	//System.out.print(", Family: " + result.getJSONObject(j).getString("Family"));
				    	//System.out.println(", CommonName: " + result.getJSONObject(j).getString("CommonName"));
			    		System.out.print(result.getJSONObject(j).getString("Family") + " ");
			    		continue;
			    	}
			    	catch (Exception e) {
			    	}
			    	try {
				    	//System.out.print("Ordnum: " + result.getJSONObject(j).getLong("Ordnum"));
				    	//System.out.print(", Order: " + result.getJSONObject(j).getString("Order"));
				    	//System.out.println(", CommonName: " + result.getJSONObject(j).getString("CommonName"));
			    		System.out.print(result.getJSONObject(j).getString("Order") + " ");
			    		continue;
			    	}
			    	catch (Exception e) {
			    	}
			    	try {
				    	//System.out.print("ClassNum: " + result.getJSONObject(j).getLong("ClassNum"));
				    	//System.out.print(", Class: " + result.getJSONObject(j).getString("Class"));
				    	//System.out.println(", CommonName: " + result.getJSONObject(j).getString("CommonName"));
			    		System.out.print(result.getJSONObject(j).getString("Class") + " ");
			    		continue;
			    	}
			    	catch (Exception e) {
			    	}
			    }
			    System.out.println();
			}
		}
		else {
			//System.out.println("No result.");
		}
	}
}

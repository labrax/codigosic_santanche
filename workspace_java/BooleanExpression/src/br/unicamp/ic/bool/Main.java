package br.unicamp.ic.bool;

import br.unicamp.ic.bool.expression.BooleanGenGraph;
import br.unicamp.ic.bool.graph.CSVDB;
import br.unicamp.ic.bool.graph.ConnectDB;
import br.unicamp.ic.bool.graph.DummyDB;
import br.unicamp.ic.bool.graph.ToNeo4j;

public class Main {
	//configuration
	public static final Integer AMOUNT_VARIABLES = 10;
	public static final Integer BOOLEANSTORE_SIZE = AMOUNT_VARIABLES;
	public static final Integer AMOUNT_NODES = 4000;
	public static final Integer AMOUNT_MATCHES = 20000; //5x AMOUNT_NODES
	public static final Boolean MATCH_ALL = false;
	public static final Boolean SAVEGRAPHDB = false;
	public static final Boolean CSVFILE = true;
	
	public static final String DB_PATH = "/home/vroth/booleanGraph";
	public static final String TARGET_CSV_FILE = "/home/vroth/source_target.csv";
	
	public static Boolean PrintFirst = true;
	
	public static void main(String[] args)
	{
		ConnectDB tj = null;
		if(Main.SAVEGRAPHDB == true)
			tj = new ToNeo4j();
		else if(Main.CSVFILE == true)
			tj = new CSVDB();
		else
			tj = new DummyDB();
		
		BooleanGenGraph bgg = new BooleanGenGraph(tj);
		bgg.run();
	}
}

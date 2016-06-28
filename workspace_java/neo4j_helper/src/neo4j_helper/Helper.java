package neo4j_helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Helper
{
	private int neo4j_process_id = 0;
	private boolean running = false;
	private String folder = "";
	
	public static void main(String[] args)
	{
		Helper a = new Helper();
		try
		{
			a.stopNeo4j();
			a.checkRunning();
			a.getFolder();
			a.printStatus();
			a.startNeo4j();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	boolean checkRunning() throws Exception
	{
		if(System.getProperty("os.name").equals("Linux"))
		{
			//from http://stackoverflow.com/questions/54686/how-to-get-a-list-of-current-open-windows-process-with-java
		    String line;
		    Process p = Runtime.getRuntime().exec("ps -u");
		    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    while ((line = input.readLine()) != null)
		    {
		    	if(line.contains("neo4j"))
		    	{
		    		String[] valores = line.split(" ");
		    		for(int i = 0; i < valores.length; i++)
		    		{
		    			if(valores[i].length() > 1 && i > 0)
		    			{
		    				//System.out.println("" + i + " " + valores[i]);
		    				neo4j_process_id = Integer.parseInt(valores[i]);
		    				running = true;
		    				break;
		    			}
		    		}
		    	}
		    }
		    input.close();
		}
		else if(System.getProperty("os.name").equals("Windows"))
		{
			//TODO: implement for Windows and ETC...
			Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
		}
		else
		{
			throw new Exception("Cannot check if Neo4j is running on " + System.getProperty("os.name") + " OS.");
		}
		
		return running;
	}
	
	void getFolder() throws Exception
	{
		if(running == true && neo4j_process_id != 0)
		{
		    Process p = Runtime.getRuntime().exec("pwdx " + neo4j_process_id);
		    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    
		    folder = input.readLine().split(" ")[1];
		    input.close();
		}
	}
	
	void printStatus()
	{
		System.out.println("" + running + " " + neo4j_process_id + " at \"" + folder + "\"");
	}
	
	void startNeo4j() throws Exception
	{
		if(checkRunning() == true)
			throw new Exception("Neo4j is already running!");

		System.out.print("Starting Neo4j...");
		
		Process p = Runtime.getRuntime().exec(folder + "/bin/neo4j start");
		p.waitFor();
		
		System.out.println(" Neo4j started!");
	}
	
	void stopNeo4j() throws Exception
	{
		if(checkRunning() == false)
			throw new Exception("Neo4j is already stopped!");
		
		getFolder();
		
		System.out.print("Stopping Neo4j...");
		
		Process p = Runtime.getRuntime().exec(folder + "/bin/neo4j stop");
		p.waitFor();
		System.out.println(" Neo4j stopped!");
		
		running = false;
	}
}

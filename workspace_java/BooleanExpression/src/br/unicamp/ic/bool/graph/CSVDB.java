package br.unicamp.ic.bool.graph;

import java.io.PrintWriter;

import br.unicamp.ic.bool.Main;

public class CSVDB implements ConnectDB{
	PrintWriter writer = null;
	
	public CSVDB()
	{
		try {
			writer = new PrintWriter(Main.TARGET_CSV_FILE, "UTF-8");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			writer = null;
		}
		
		if(writer != null)
		{
			writer.println("Source,Target");
		}
		else
		{
			System.out.println("Source,Target");			
		}
		
	}
	
	public void flush_index()
	{
		
	}

	public void shutdown()
	{
		writer.flush();
		writer.close();
	}

	public void createIndexes()
	{
		
	}

	public void createNode(Integer id)
	{
		
	}

	public void createRelationship(Integer source, Integer target)
	{
		if(writer != null)
			writer.println(source + "," + target);
		else
			System.out.println(source + "," + target);
	}

}

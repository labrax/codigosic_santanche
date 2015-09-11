package br.unicamp.ic.bool.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.unicamp.ic.bool.BooleanStore;
import br.unicamp.ic.bool.BooleanStoreVector;
import br.unicamp.ic.bool.Main;
import br.unicamp.ic.bool.RandomGenerator;
import br.unicamp.ic.bool.graph.ConnectDB;
import br.unicamp.ic.bool.graph.DummyDB;
import br.unicamp.ic.bool.graph.Node;

public class BooleanGenGraph {
	//run information
	public Integer countFirst = 0;
	public Integer countSecond = 0;
	public Integer countBoth = 0;
	public Integer countNone = 0;
	
	public ArrayList<Node> all_nodes;
	public Map<Integer, Integer> nodesExpression; //amount of nodes for each amount of edges
	
	private ConnectDB db;

	private BooleanExpressionFactory bef;
	
	private long startTime, endTime, totalTime;
	
	public BooleanGenGraph(ConnectDB tj)
	{
		bef = new BooleanExpressionFactory();
		reset();
		db = tj;
	}
	
	public void reset()
	{
		all_nodes = new ArrayList<Node>();
		nodesExpression = new HashMap<Integer, Integer>();
		db = new DummyDB();
		countFirst = 0;
		countSecond = 0;
		countBoth = 0;
		countNone = 0;
	}
	
	public void genNode()
	{
		
	}
	
	public void startTimer()
	{
		startTime = System.currentTimeMillis();
	}
	
	public void endTimer(String message)
	{
		endTime   = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println(message + " " + totalTime + "ms");
	}
	
	public void createNodes()
	{
		//at first create all nodes with properties and expressions
		Node curr_node = null;
		for(Integer n = 0; n < Main.AMOUNT_NODES; n++)
		{
			BooleanNode bn = bef.newExpression(); //create random expression
			
			BooleanStore bs = new BooleanStoreVector();//set random variables
			for(Integer v = 0; v < Main.AMOUNT_VARIABLES; v++)
			{
				bs.setVariable(v, RandomGenerator.getRandomBoolean());
				//System.out.println("X" + v + "=" + bs.getVal(v));
			}
			
			if(Main.PrintFirst == true)
			{
				System.out.println("Variables: " + bs.toString());
				System.out.println("Expression: " + bn.toString());
				Main.PrintFirst = false;
			}
			
			curr_node = new Node(n, bn, bs);
			all_nodes.add(curr_node);
			
			if((n%50) == 0) //display progress
				System.out.println(n + "/" + Main.AMOUNT_NODES);
			
			db.createNode(n);
		}
	}
	
	public void matchnodes()
	{
		//iterate nodes
		if(Main.MATCH_ALL == true) //iterate all against all
		{
			//between everyone
			for(Iterator<Node> it = all_nodes.iterator(); it.hasNext(); )
			{
				Node curr = it.next();
				for(Iterator<Node> it2 = all_nodes.iterator(); it2.hasNext();)
				{
					Node curr2 = it2.next();
					if(curr.try_friendship(curr2))
					{
						db.createRelationship(curr.getId(), curr2.getId());
						countFirst++;
					}
				}
				
				//curr.printInformation();
			}	
		}
		else
		{
			for(Integer i = 0; i < Main.AMOUNT_MATCHES; i++) //iterate randomly
			{
				Integer first = RandomGenerator.getRandomInteger(Main.AMOUNT_NODES);
				Integer second = RandomGenerator.getRandomInteger(Main.AMOUNT_NODES);
				if(first == second)
					i--;
				else
				{
					Boolean f1 = all_nodes.get(first).try_friendship(all_nodes.get(second));
					Boolean f2 = all_nodes.get(second).try_friendship(all_nodes.get(first));
					if(f1 && f2)
						countBoth++;
					else if(!f1 && !f2)
						countNone++;
					else if(f1)
						countFirst++;
					else if(f2)
						countSecond++;
					
					if(f1)
						db.createRelationship(first, second);
					if(f2)
						db.createRelationship(second, first);
				}
			}
		}
	}
	
	public void countLinks()
	{
		for(Iterator<Node> it = all_nodes.iterator(); it.hasNext();)
		{
			Node curr = it.next();
			Integer amount = curr.getAmountConnections();
			if(nodesExpression.containsKey(amount))
				nodesExpression.put(amount, nodesExpression.get(amount)+1);
			else
				nodesExpression.put(amount, 1);
		}
	}
	
	public void printMatchingInformation()
	{
		System.out.println("countFirst: " + countFirst);
		System.out.println("countSecond: " + countSecond);
		System.out.println("countBoth: " + countBoth);
		System.out.println("countNone: " + countNone);
	}
	
	public void printLinksInformation()
	{
		System.out.println("Amount connections,Amount nodes");
		for(Entry<Integer,Integer> entry : nodesExpression.entrySet())
		{
			System.out.println(entry.getKey() + "," + entry.getValue());
		}
	}
	
	public void run()
	{
		db.createIndexes();
		startTimer();
		createNodes();
		db.flush_index();
		endTimer("Nodes created!");
		
		
		startTimer();
		matchnodes();
		db.shutdown();
		endTimer("Nodes interacted!");
		
		printMatchingInformation();
		countLinks();
		printLinksInformation();
	}
}

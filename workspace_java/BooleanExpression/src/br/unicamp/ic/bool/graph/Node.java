package br.unicamp.ic.bool.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import br.unicamp.ic.bool.BooleanStore;
import br.unicamp.ic.bool.expression.BooleanNode;

public class Node {
	private Integer id;
	private BooleanNode likes;
	private BooleanStore this_node_is;
	
	private Set<Node> connections = new HashSet<Node>();
	
	public Node(Integer id, BooleanNode likes, BooleanStore this_node_is)
	{
		this.id = id;
		this.likes = likes;
		this.this_node_is = this_node_is;
	}
	
	public Integer getId()
	{
		return id;
	}
	
	public BooleanStore getInformation()
	{
		return this_node_is;
	}
	
	public Boolean likes(Node other)
	{
		return likes.compute(other.getInformation());
	}
	
	public Boolean try_friendship(Node other)
	{
		if(likes(other))
		{
			connections.add(other);
			return true;
		}
		return false;
	}
	
	public void printInformation()
	{
		System.out.println("Node : " + id);
		for(Iterator<Node> it = connections.iterator(); it.hasNext(); )
		{
			Node e = it.next();
			System.out.print(e.getId() + " ");
		}
		System.out.println();
	}
	
	public Integer getAmountConnections()
	{
		return connections.size();
	}
	
	/*public ArrayList<Node> getFriends()
	{
		return connections;
	}*/
}

import java.util.ArrayList;


public class Node {
	private boolean modified = false;
	private int id;
	private ArrayList<String> old_traits;
	private ArrayList<String> traits;
	private ArrayList<Node> neighbour = new ArrayList<Node>();
	
	private ArrayList<Integer> modified_from = new ArrayList<Integer>();
	
	public Node(int id, ArrayList<String> traits) {
		this.id = id;
		this.traits = traits;
		this.old_traits = new ArrayList<String>(traits);
	}
	
	public int getId()
	{
		return id;
	}
	
	public void addNeighbour(Node other)
	{
		neighbour.add(other);
	}
	
	public ArrayList<Node> getNeighbours()
	{
		return neighbour;
	}
	
	public ArrayList<String> getTraits()
	{
		return traits;
	}
	
	public ArrayList<String> getOldTraits()
	{
		return old_traits;
	}
	
	public ArrayList<Integer> getModifiers()
	{
		return modified_from;
	}
	
	public boolean getModified()
	{
		return modified;
	}
	
	public boolean test(Node other)
	{
		ArrayList<String> other_traits = other.getTraits();
		
		int count = 0;
		for(int i = 0; i < traits.size(); i++)
		{
			if(traits.get(i).equals(other_traits.get(i)))
				count++;
		}
		
		//nothing to be done, they are equal
		if(count == traits.size())
			return true;
		
		//test if they pass traits
		if(Randomer.getRandom(100) < (100*count)/traits.size())
		{
			modified_from.add(other.getId());
			while(true)
			{
				int t = Randomer.getRandom(traits.size());
				if(!traits.get(t).equals(other_traits.get(t)))
				{
					traits.set(t, other_traits.get(t));
					modified = true;
					break;
				}
			}
		}
		else
			return false;
		
		return true;
	}
}

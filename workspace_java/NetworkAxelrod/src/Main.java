import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main
{
	public static String FILE_DATA = "/home/vroth/Downloads/petster-friendships-hamster/ent.petster-friendships-hamster-uniq";
	public static String FILE_RELATION = "/home/vroth/Downloads/petster-friendships-hamster/out.petster-friendships-hamster-uniq";
	public static int AMOUNT_ITERATIONS = 20;

	HashMap<Integer, Node> map = new HashMap<Integer, Node>();
	ArrayList<Node> nodes = new ArrayList<Node>();
	
	public static void main(String[] args)
	{
		Main m = new Main();
		m.processNodes();
		m.processRelations();
		//m.checkRelations();
		m.axelrodify();
		m.verifyChanges();
	}
	
	public void processNodes()
	{
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_DATA)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				processNodeLine(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void processRelations()
	{
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_RELATION)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				if(line.startsWith("%"))
					continue;
				String[] elems = line.split(" ");
				
				//add relationship oneway!
				if(map.containsKey(Integer.parseInt(elems[0])) && map.containsKey(Integer.parseInt(elems[1])))
					map.get(Integer.parseInt(elems[0])).addNeighbour(map.get(Integer.parseInt(elems[1])));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void checkRelations()
	{
		int total = 0;
		ArrayList<Integer> count = new ArrayList<Integer>();
		for(int i = 0; i < 100; i++)
		{
			count.add(0);
		}
		
		for(int i = 0; i < nodes.size(); i++)
		{
			count.set(count.get(nodes.get(i).getNeighbours().size()), count.get(nodes.get(i).getNeighbours().size())+1);
		}
		
		for(int i = 0; i < 100; i++)
		{
			if(count.get(i) != 0)
				System.out.println(i + " " + count.get(i));
			total += count.get(i);
		}
		System.out.println("total = " + total);
	}
	
	public void axelrodify()
	{
		for(int i = 0; i < AMOUNT_ITERATIONS; i++)
		{
			int node_id = Randomer.getRandom(nodes.size());
			if(nodes.get(node_id).getNeighbours().size() == 0)
				continue;
			int next_node = Randomer.getRandom(nodes.get(node_id).getNeighbours().size());
			nodes.get(node_id).test(nodes.get(node_id).getNeighbours().get(next_node));
		}
	}
	
	public void verifyChanges()
	{
		int a_mod = 0;
		for(int i = 0; i < nodes.size(); i++)
		{
			if(nodes.get(i).getModified() == true)
			{
				a_mod++;
				System.out.println(nodes.get(i).getId());
				System.out.println(nodes.get(i).getOldTraits());
				System.out.println(nodes.get(i).getModifiers());
				System.out.println(nodes.get(i).getTraits());
				System.out.println("----------------------------");
			}
		}
		System.out.println("Amount of modified nodes is: " + a_mod);
	}

	public void processNodeLine(String line)
	{
		if(line.startsWith("%"))
			return;
		
		ArrayList<String> elems = new ArrayList<String>();
		
		//process data
		String curr = "";
		boolean inside_elem = false;
		for(int i = 0; i < line.length(); i++)
		{
			if(inside_elem == false && line.charAt(i) == '\"')
			{
				inside_elem = true;
			}
			else if(inside_elem == true && line.charAt(i) == '\"')
			{
				elems.add(curr);
				inside_elem = false;
				curr = new String("");
			}
			else
			{
				curr += line.charAt(i);
			}
		}
		
		//get id
		int id = Integer.parseInt(elems.get(0));
		elems.remove(0);
		//create node with properties
		Node node = new Node(id, elems);
		nodes.add(node);
		map.put(id, node);
	}
}

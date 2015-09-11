package br.unicamp.ic.bool;

public class BooleanStoreVector implements BooleanStore {
	//public Map<Integer, Boolean> map;
	private boolean[] map = new boolean[Main.BOOLEANSTORE_SIZE];
	
	public BooleanStoreVector()
	{
		//map = new HashMap<Integer, Boolean>();
	}
	
	public void setVariable(Integer id, Boolean bool)
	{
		//map.put(id, bool);
		map[id] = bool;
	}
	
	public boolean getVal(Integer id)
	{
		//return map.get(id);
		return map[id];
	}
	
	public String toString()
	{
		String a = "";
		for(int i = 0; i < Main.BOOLEANSTORE_SIZE; i++)
		{
			a += "X" + i + " = " + map[i] + " ";
		}
		return a;
	}
}

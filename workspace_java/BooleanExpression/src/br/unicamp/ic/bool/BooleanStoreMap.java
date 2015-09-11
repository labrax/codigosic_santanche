package br.unicamp.ic.bool;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BooleanStoreMap {
	private Map<Integer, Boolean> map;
	
	public BooleanStoreMap()
	{
		map = new HashMap<Integer, Boolean>();
	}
	
	public void setVariable(Integer id, Boolean bool)
	{
		map.put(id, bool);
	}
	
	public boolean getVal(Integer id)
	{
		return map.get(id);
	}
	
	public String toString()
	{
		String a = "";
		for(Entry<Integer, Boolean> entry : map.entrySet())
		{
			a += "X" + entry.getKey() + " = " + entry.getValue() + " ";
		}
		return a;
	}
}

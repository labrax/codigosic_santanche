package br.unicamp.ic.bool.expression;

import br.unicamp.ic.bool.BooleanStore;

public abstract class BooleanNode {
	public enum bool_type { AND, OR, NOT, VARIABLE };
	
	bool_type NodeType;
	
	BooleanNode(bool_type type)
	{
		NodeType = type;
	}
	
	public bool_type getType()
	{
		return NodeType;
	}
	
	public abstract boolean compute(BooleanStore bs);
}

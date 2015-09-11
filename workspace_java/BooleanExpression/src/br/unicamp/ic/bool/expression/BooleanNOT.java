package br.unicamp.ic.bool.expression;

import br.unicamp.ic.bool.BooleanStore;

public class BooleanNOT extends BooleanNode{
	private BooleanNode node;
	
	public BooleanNOT(BooleanNode points)
	{
		super(bool_type.NOT);
		this.node = points;
	}
	
	public BooleanNode getElem()
	{
		return node;
	}
	
	public boolean compute(BooleanStore bs)
	{
		return !node.compute(bs);
	}
	
	public String toString()
	{
		return "!" + node.toString();
	}
}

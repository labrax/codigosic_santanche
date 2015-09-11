package br.unicamp.ic.bool.expression;

import br.unicamp.ic.bool.BooleanStore;

public class BooleanOR extends BooleanNode{
	private BooleanNode left, right;
	
	public BooleanOR(BooleanNode left, BooleanNode right)
	{
		super(bool_type.OR);
		this.left = left;
		this.right = right;
	}
	
	public BooleanNode getLeft()
	{
		return left;
	}
	
	public BooleanNode getRight()
	{
		return right;
	}
	
	public boolean compute(BooleanStore bs)
	{
		return (left.compute(bs) || right.compute(bs));
	}
	
	public String toString()
	{
		return "(" + left.toString() + " || " + right.toString() + ")";
	}
}

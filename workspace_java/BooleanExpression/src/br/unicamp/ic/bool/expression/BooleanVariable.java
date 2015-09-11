package br.unicamp.ic.bool.expression;

import br.unicamp.ic.bool.BooleanStore;

public class BooleanVariable extends BooleanNode{
	private Integer id;
	
	public BooleanVariable(Integer id)
	{
		super(bool_type.VARIABLE);
		this.id = id;
	}
	
	public boolean compute(BooleanStore bs) {
		return bs.getVal(id);
	}
	
	public String toString()
	{
		return "X" + id;
	}
}

package br.unicamp.ic.bool;

public interface BooleanStore {
	public void setVariable(Integer id, Boolean bool);
	public boolean getVal(Integer id);
	public String toString();
}

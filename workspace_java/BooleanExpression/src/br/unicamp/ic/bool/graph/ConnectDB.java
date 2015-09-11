package br.unicamp.ic.bool.graph;

public interface ConnectDB {
	public abstract void flush_index();
	public abstract void shutdown();
	public abstract void createIndexes();
	public abstract void createNode(Integer id);
	public abstract void createRelationship(Integer source, Integer target);
}

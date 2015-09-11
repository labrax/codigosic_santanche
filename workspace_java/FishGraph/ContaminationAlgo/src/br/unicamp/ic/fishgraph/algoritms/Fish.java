package br.unicamp.ic.fishgraph.algoritms;

import java.util.ArrayList;

public class Fish
{
	private String SpecCode, name;
	private ArrayList<Fish> predators;
	private ArrayList<Fish> preys;
	
	private int contaminacao;
	private int depth;
	boolean visited;
	
	public Fish(String SpecCode, String name)
	{
		this.SpecCode = SpecCode;
		this.name = name;
		
		this.predators = new ArrayList<Fish>();
		this.preys = new ArrayList<Fish>();
		
		contaminacao = 0;
		depth = 0;
		visited = false;
	}
	
	public String getSpecCode()
	{
		return SpecCode;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void addPredator(Fish e)
	{
		predators.add(e);
	}
	
	public void addPrey(Fish e)
	{
		preys.add(e);
	}
	
	public ArrayList<Fish> getPredators()
	{
		return predators;
	}
	
	public ArrayList<Fish> getPreys()
	{
		return preys;
	}
	
	public void setDepth(int depth)
	{
		this.depth = depth;
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	public void setContaminacao(int contaminacao)
	{
		this.contaminacao = contaminacao;
	}
	
	public int getContaminacao()
	{
		return contaminacao;
	}
}

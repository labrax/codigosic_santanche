package br.unicamp.ic.fishgraph.algoritms;

public class Main {
	public static final void main(String args[])
	{
		Main m = new Main();
		m.run();
	}
	
	public void run()
	{
		Database db = new Database();
		ContaminationAlgo ca = new ContaminationAlgo(db);
		ca.run();
	}
}

package br.unicamp.ic.fishgraph.algoritms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.ResourceIterator;


//adicionar: match(e:Ecosystem{E_CODE: '967'})--(s:Species) return e, s
public class ContaminationAlgo
{
	private Database db;
	private Map<String, Fish> sp;
	
	private int amount_fish;
	
	private int iteration_id;
	private int amount_contaminated;
	private int lastDepth;
	
	public ContaminationAlgo(Database db)
	{
		this.db = db;
		this.sp = new HashMap<String, Fish>();
		iteration_id = 0;
	}
	
	public void getAll()
	{
		String query = "MATCH (s1:Species)-[:PREDATS]->(s2:Species) RETURN s1.SpecCode AS PredatorCode, s1.Species AS PredatorName, s2.SpecCode AS PreyCode, s2.Species AS PreyName";
		
		ExecutionResult ers = db.sendQuery(query);
		ResourceIterator<Map<String, Object>> er = ers.iterator();
		while (er.hasNext()) {
			Map<String, Object> curr = er.next();

			//System.out.println(curr.get("PredatorCode") + ";" + curr.get("PreyCode"));
			
			if( !sp.containsKey(curr.get("PredatorCode").toString()) )
			{
				sp.put(curr.get("PredatorCode").toString(), new Fish(curr.get("PredatorCode").toString(), curr.get("PredatorName").toString()));
				amount_fish++;
			}
			if(!sp.containsKey(curr.get("PreyCode").toString()))
			{
				sp.put(curr.get("PreyCode").toString(), new Fish(curr.get("PreyCode").toString(), curr.get("PreyName").toString()));
				amount_fish++;
			}
			
			sp.get(curr.get("PreyCode").toString()).addPredator(sp.get(curr.get("PredatorCode").toString()));
			sp.get(curr.get("PredatorCode").toString()).addPrey(sp.get(curr.get("PreyCode").toString()));
		}
		//System.out.println("Query time: " + db.getLastQueryTime() + "ms");
	}
	
	public void contamina(Fish e)
	{
		e.setContaminacao(iteration_id);
		amount_contaminated += 1;
	}
	
	public void BFS(Fish initial_fish)
	{
		lastDepth = 0;
		Queue<Fish> queue = new LinkedList<Fish>();
		initial_fish.setDepth(0);
		queue.add(initial_fish);
		while(!queue.isEmpty())
		{
			Fish item = queue.remove();
			lastDepth = item.getDepth();
			if(item.getContaminacao() != iteration_id)
			{
				contamina(item);
				ArrayList<Fish> adj = item.getPredators();
				for(Iterator<Fish> it = adj.iterator(); it.hasNext();)
				{
					Fish e = it.next();
					if(e.getContaminacao() != iteration_id)
					{
						e.setDepth(item.getDepth()+1);
						queue.add(e);
					}
				}
			}
		}
	}
	
	public void runContamina()
	{
		System.out.println("SpecCode,Name,Contaminated,Fraction,MaximumDepth");
		for (Map.Entry<String, Fish> entry : sp.entrySet())
		{
			Fish e = entry.getValue();
			iteration_id += 1;
			amount_contaminated = 0;
			BFS(e);
			System.out.format("%s,%s,%d,%f,%d\n", e.getSpecCode(), e.getName(), amount_contaminated, amount_contaminated/(double)amount_fish, lastDepth);
		}
	}
	
	//indo pelas presas
	public void contaLigacoes()
	{
		Map<Integer, Integer> contagem = new HashMap<Integer, Integer>(); 
		
		for(Map.Entry<String, Fish> entry : sp.entrySet())
		{
			Fish e = entry.getValue();
			Integer v = e.getPreys().size();
			
			if(contagem.containsKey(v))
			{
				contagem.put(v, contagem.get(v)+1);
			}
			else
				contagem.put(v, 1);
		}
		
		System.out.println("Quantia de arestas, Quantia de peixes");
		for(Map.Entry<Integer, Integer> entry : contagem.entrySet())
		{
			System.out.format("%d, %d\n", entry.getKey(), entry.getValue());
		}
	}
	
	//PageRank implementation as in https://en.wikipedia.org/wiki/PageRank#Algorithm
	//está percorrendo pelas presas!
	public void pageRankSimplified(int amount_iterations, double damping_factor, boolean percorre_presas)
	{
		Map<Fish, Double> contagemIteracao = new HashMap<Fish, Double>();
		Map<Fish, Double> contagemNovaIteracao = new HashMap<Fish, Double>();
		
		for(Map.Entry<String, Fish> entry : sp.entrySet())
		{
			contagemIteracao.put(entry.getValue(), Double.valueOf(1/(double) amount_fish));
		}
		
		for(int i = 0; i < amount_iterations; i++)
		{
			//zera os dados de atualização
			contagemNovaIteracao = new HashMap<Fish, Double>();
			//gera novos valores
			for(Map.Entry<Fish, Double> entry : contagemIteracao.entrySet())
			{
				Fish current;
				Fish e = entry.getKey();
				Integer v;
				if(percorre_presas == true)
					v = e.getPreys().size();
				else
					v = e.getPredators().size();
				
				if(v!=0)
				{
					Iterator<Fish> it;
					if(percorre_presas == true)
						it = e.getPreys().iterator();
					else
						it = e.getPredators().iterator();
					
					for(; it.hasNext(); )
					{
						current = it.next();
						
						if(contagemNovaIteracao.containsKey(current))
							contagemNovaIteracao.put(current, Double.valueOf(contagemNovaIteracao.get(current) + entry.getValue()/v));
						else
							contagemNovaIteracao.put(current, Double.valueOf(entry.getValue()/v));
					}
				}
				else //caso não tenha arestas adiciona um pouco do valor para todos os outros nós
				{
					Double cv = entry.getValue();
					for(Map.Entry<String, Fish> this_entry : sp.entrySet())
					{
						if(contagemNovaIteracao.containsKey(this_entry.getValue()))
							contagemNovaIteracao.put(this_entry.getValue(), contagemNovaIteracao.get(this_entry.getValue()) + cv/(double) amount_fish);
						else
							contagemNovaIteracao.put(this_entry.getValue(), cv/(double) amount_fish);
					}
				}
				
				entry.setValue(Double.valueOf(0));//será da contagem atual
			}
			
			//atualiza os valores copiando de volta
			for(Map.Entry<Fish, Double> entry : contagemIteracao.entrySet())
			{
				Fish e = entry.getKey();
				if(contagemNovaIteracao.containsKey(e))
				{
					Double antigo_valor = entry.getValue();
					Double novo_valor = (1-damping_factor)/(double)amount_fish + damping_factor*contagemNovaIteracao.get(e);
					contagemIteracao.put(e, antigo_valor + novo_valor);
				}
				else
				{
					//contagemIteracao.put(e, Double.valueOf(0));
				}
			}
		}
		
		//somatório para normalizar
		Double sum = Double.valueOf(0);
		for(Map.Entry<Fish, Double> entry : contagemIteracao.entrySet())
		{
			sum += entry.getValue();
		}
		
		//normaliza
		for(Map.Entry<Fish, Double> entry : contagemIteracao.entrySet())
		{
			entry.setValue(entry.getValue()/sum);
		}
		
		//sum = Double.valueOf(0);
		System.out.println("Peixe,Taxa");
		//imprime resultados
		for(Map.Entry<Fish, Double> entry : contagemIteracao.entrySet())
		{
			System.out.format("%s,%f\n", entry.getKey().getName(), entry.getValue());
			//sum += entry.getValue();
		}
		
		//System.out.println("A soma é " + sum); //avaliação somatório
	}
	
	public void run()
	{	
		getAll();

		//contaLigacoes();
		//runContamina();
		
		pageRankSimplified(100, 0.85, true);
	}
}

package br.unicamp.ic.bool.expression;

import br.unicamp.ic.bool.Main;
import br.unicamp.ic.bool.RandomGenerator;

/*
 * Com valores: 1000 variáveis; 400 nós; 2000 pares de encontros
 * Análises: aumentando o desbalanceamento para o OR puxa a distribuição para a direita!
 * 
 * Aumentando o desbalanceamento para o AND leva a uma distribuição semelhante a power-law! (quando mais bool_structure_size mais elementos estarão em 0,
 * deslocando o gráfico)
 * 
 * (02/09/2015)
 * Inicialmente deixei com as opções AND, OR e NOT.
 * Modifico para AND e NOT (operações mínimas para se fazer toda a lógica booleana) 
 */

public class BooleanExpressionFactory {
	public int curr_variable = 0;
	//public static Integer bool_structure_size = 2; //and, or, not
	
	public BooleanNode newExpression()
	{	
		curr_variable = 0;
		BooleanNode curr = createVariable();
		
		//iterate creating nodes until all variables are used
		while(curr_variable < Main.AMOUNT_VARIABLES)
		{
			//int val = getRandomStructure();
			Boolean val = RandomGenerator.getRandomBoolean();
			if(val == true) //and
			{
				curr = new BooleanAND(createVariable(), curr);
			}
			else if(val == false) //not
			{
				curr = new BooleanNOT(curr);
			}
			/*else if(val == 2)//if val == 2 not
			{
				curr = new BooleanOR(createVariable(), curr);
			}*/
		}
		return curr;
	}
	
	private BooleanVariable createVariable()
	{
		curr_variable++;
		return new BooleanVariable(curr_variable-1);
	}
	
	/*private Integer getRandomStructure()
	{
		return RandomGenerator.getRandomInteger(bool_structure_size);
	}*/
}

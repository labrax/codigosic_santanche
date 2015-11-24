//============================================================================
// Name        : BooleanGraph.cpp
// Author      : Victor Roth Cardoso
// Version     :
// Copyright   : 
// Description :
//============================================================================

#include <cstdio>
#include <set>
#include <string>
#include <vector>

#include <sys/resource.h>

#include "boolean/boolean_expression_factory.hpp"
#include "boolean/boolean_store.hpp"
#include "boolean/nodes/boolean_node.hpp"
#include "graph/node.hpp"
#include "graph/graph.hpp"
#include "graph/statistics.hpp"
#include "random_generator.hpp"

#include "graph/csv_output.hpp"

#include "recipes/recipes_loader.hpp"
#include "recipes/recipe.hpp"

void createGraph();
void loadRecipes();
void limits();

int main(int argc, char * argv[]) {
	//teste();

	limits();
	createGraph();
	//loadRecipes();

	return 0;
}

void limits() {
	struct rlimit limits;
	limits.rlim_cur = 3*1000*1000*1000; //limit to 3GB!
	limits.rlim_max = 3*1000*1000*1000;
	setrlimit(RLIMIT_DATA, &limits);
}

void loadRecipes() {
	RandomGenerator gs = RandomGenerator();
	RecipesLoader rl = RecipesLoader();

	printf("seed = %u\n", gs.getSeed());

	rl.readFile();
	//rl.printIngredientNamesId();

	Graph<Recipe> graph(rl.getRecipes());

	std::size_t amount_recipes = graph.size();
	printf("we have %ld recipes!\n", amount_recipes);

	graph.matching(amount_recipes * 4, gs);

	Statistics<Recipe> sta(graph);
	sta.getAll();
	sta.printAll();

	graph.free();
}

void createGraph() {
	//printf("starting to create graph\n");

	BooleanExpressionFactory bef = BooleanExpressionFactory();
	RandomGenerator gs = RandomGenerator();

	printf("\"seed\",%u\n", gs.getSeed());

	//printf("creating nodes!\n");
	std::vector<Node*> nodes;
	Graph<Node> graph(nodes);

	/*generate random properties and functions*/
#pragma omp parallel for
	for(unsigned int i = 0; i < AMOUNT_NODES; i++) {
		BooleanStore * bs = new BooleanStore();

		for(unsigned int j = 0; j < AMOUNT_VARIABLES; j++)
		{
			bool n = gs.getBoolean();
			bs->addBool(j, n);
		}

		BooleanNode * exp = bef.createExpression(gs);

#pragma omp critical
		{
			graph.insert(new Node(i, bs, exp));
		}
		/*if(i%10000 == 0)
			printf("[%u/%u] Nodes\n", i, AMOUNT_NODES);*/
	}
	//printf("%d nodes created!\n", AMOUNT_NODES);

	graph.matching(AMOUNT_MATCHES, gs);

	Statistics<Node> sta(graph);

	sta.getAll();
	sta.printAll();

	CSVOutput csv;
	csv.insertNodes( graph.getNodes() );

	graph.free();
}

void teste() {
	//teste randomseed
	BooleanExpressionFactory bef = BooleanExpressionFactory();
	RandomGenerator gs = RandomGenerator();
	printf("Random seed is: %u\n", gs.getSeed());
	printf("%s\n", bef.createExpression(gs)->toString().c_str());

	//teste random
	for(int i = 0; i < 100; i++)
	{
		printf("%ld ", gs.getInteger(5));
	}
	printf("\n");

	//teste booleanstore
	BooleanStore bs = BooleanStore();
	for(int i = 0; i < 100; i++)
	{
		bool n = gs.getBoolean();
		bs.addBool(i, n);
		printf("%d ", n);
	}
	printf("\n");

	//teste troca de valores
	bs.addBool(0, 0);
	bs.addBool(1, 1);

	//teste obtenção valores
	for(int i = 0; i < 100; i++)
	{
		printf("%d ", bs.getBool(i));
	}
	printf("\n");
}

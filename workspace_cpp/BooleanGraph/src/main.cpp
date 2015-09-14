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

#include "boolean/boolean_expression_factory.hpp"
#include "boolean/boolean_store.hpp"
#include "boolean/nodes/boolean_node.hpp"
#include "graph/node.hpp"
#include "random_generator.hpp"

#include "recipes/recipes_loader.hpp"
#include "recipes/recipe.hpp"

void createGraph();
void loadRecipes();

int main(int argc, char * argv[]) {
	//printf("Hello World!\n");
	//teste();

	//createGraph();
	loadRecipes();

	return 0;
}

void loadRecipes() {
	RandomGenerator gs = RandomGenerator();
	RecipesLoader rl = RecipesLoader();

	rl.readFile();
	//rl.printIngredientNamesId();
	std::vector<Recipe *> recipes = rl.getRecipes();

	unsigned int amount_recipes = recipes.size();
	unsigned int matches_recipes = amount_recipes * 4;
	printf("we have %u recipes! will do %u matchings (num x 5)\n", amount_recipes, matches_recipes);

	unsigned int count_both = 0, count_first = 0, count_second = 0, count_none = 0;
#pragma omp parallel for
	for(long int i = 0; i < matches_recipes; i++)
	{
		unsigned int first = gs.getInteger(amount_recipes);
		unsigned int second = gs.getInteger(amount_recipes);

		bool first_try, second_try;
		first_try = recipes[first]->try_friendship(recipes[second]);
		second_try = recipes[second]->try_friendship(recipes[first]);

#pragma omp critical
		{
			if(first_try && second_try)
				count_both++;
			else if(first_try)
				count_first++;
			else if(second_try)
				count_second++;
			else
				count_none++;
		}
	}

	/*
	//match all
#pragma omp parallel for
	for(long int i = 0 ; i < amount_recipes; i++)
	{
		for(long int j = i+1 ; j < amount_recipes; j++)
		{
			unsigned int first = i;
			unsigned int second = j;

			bool first_try, second_try;
			first_try = recipes[first]->try_friendship(recipes[second]);
			second_try = recipes[second]->try_friendship(recipes[first]);

#pragma omp critical
			{
				if(first_try && second_try)
					count_both++;
				else if(first_try)
					count_first++;
				else if(second_try)
					count_second++;
				else
					count_none++;
			}
		}
	}*/
	printf("%d matchings done!\n", matches_recipes);

	unsigned int * amount_vertex = new unsigned int[amount_recipes];

	for(unsigned int i = 0; i < amount_recipes; i++) {
		amount_vertex[i] = 0;
	}
	for(unsigned int i = 0; i < amount_recipes; i++) {
		amount_vertex[recipes[i]->getSet().size()]++;
	}

	printf("countFirst: %u\ncountSecond: %u\ncountBoth: %u\ncountNone: %u\n", count_first, count_second, count_both, count_none);

	printf("Amount connections,Amount nodes\n");
	for(unsigned int i = 0; i < amount_recipes; i++) {
		if(amount_vertex[i] != 0)
			printf("%d,%u\n", i, amount_vertex[i]);
	}

	//cleanup!
	delete amount_vertex;
	for(unsigned int i; i < recipes.size(); i++)
	{
		delete(recipes[i]);
	}
}

void createGraph() {
	printf("starting to create graph\n");

	BooleanExpressionFactory bef = BooleanExpressionFactory();
	RandomGenerator gs = RandomGenerator();

	printf("creating nodes!\n");
	std::vector<Node*> nodes;

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
			nodes.insert(nodes.end(), new Node(i, bs, exp));
		}
		/*if(i%10000 == 0)
			printf("[%u/%u] Nodes\n", i, AMOUNT_NODES);*/
	}
	printf("%d nodes created!\n", AMOUNT_NODES);

	unsigned int count_both = 0, count_first = 0, count_second = 0, count_none = 0;

	printf("matching!\n");
#pragma omp parallel for
	for(long i = 0 ; i < AMOUNT_MATCHES; i++) {
		unsigned int first = gs.getInteger(AMOUNT_NODES);
		unsigned int second = gs.getInteger(AMOUNT_NODES);

		bool first_try, second_try;
		first_try = nodes[first]->try_friendship(nodes[second]);
		second_try = nodes[second]->try_friendship(nodes[first]);

#pragma omp critical
		{
			if(first_try && second_try)
				count_both++;
			else if(first_try)
				count_first++;
			else if(second_try)
				count_second++;
			else
				count_none++;
		}

		/*if(i%(AMOUNT_MATCHES/10) == 0)
			printf("[%ld/%d] Matches\n", i, AMOUNT_MATCHES);*/
	}
	printf("%d matchings done!\n", AMOUNT_MATCHES);

	unsigned int * amount_vertex = new unsigned int[AMOUNT_NODES];
	for(unsigned int i = 0; i < AMOUNT_NODES; i++) {
		amount_vertex[i] = 0;
	}
	for(unsigned int i = 0; i < AMOUNT_NODES; i++) {
		amount_vertex[nodes[i]->getSet().size()]++;
	}

	printf("countFirst: %u\ncountSecond: %u\ncountBoth: %u\ncountNone: %u\n", count_first, count_second, count_both, count_none);

	printf("Amount connections,Amount nodes\n");
	for(unsigned int i = 0; i < AMOUNT_NODES; i++) {
		if(amount_vertex[i] != 0)
			printf("%d,%u\n", i, amount_vertex[i]);
	}

	//cleanup!
	delete amount_vertex;
	for(unsigned int i = 0; i < AMOUNT_NODES; i++) {
		delete nodes[i];
	}
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

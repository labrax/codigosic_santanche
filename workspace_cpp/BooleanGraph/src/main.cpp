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

#include "boolean/boolean_expression_factory.hpp"
#include "boolean/boolean_store.hpp"
#include "boolean/nodes/boolean_node.hpp"
#include "graph/node.hpp"
#include "random_generator.hpp"

void createGraph();

int main(int argc, char * argv[]) {
	//printf("Hello World!\n");
	//teste();
	createGraph();

	return 0;
}

void createGraph() {
	printf("starting to create graph\n");

	BooleanExpressionFactory bef = BooleanExpressionFactory();
	RandomGenerator gs = RandomGenerator();

	printf("creating nodes!\n");
	Node ** nodes = new Node * [AMOUNT_NODES];
	for(unsigned int i = 0; i < AMOUNT_NODES; i++) {
		BooleanStore * bs = new BooleanStore();

		for(unsigned int j = 0; j < AMOUNT_VARIABLES; j++)
		{
			bool n = gs.getBoolean();
			bs->addBool(j, n);
		}

		BooleanNode * exp = bef.createExpression(gs);

		nodes[i] = new Node(i, bs, exp);
		if(i%10000 == 0)
			printf("[%u/%u] Nodes\n", i, AMOUNT_NODES);
	}
	printf("nodes created!\n");

	unsigned int count_both = 0, count_first = 0, count_second = 0, count_none = 0;

	printf("matching!\n");
	for(long i = 0 ; i < AMOUNT_MATCHES; i++) {
		unsigned int first = gs.getInteger(AMOUNT_NODES);
		unsigned int second = gs.getInteger(AMOUNT_NODES);

		bool first_try = nodes[first]->try_friendship(nodes[second]);
		bool second_try = nodes[second]->try_friendship(nodes[first]);

		if(first_try && second_try)
			count_both++;
		else if(first_try)
			count_first++;
		else if(second_try)
			count_second++;
		else
			count_none++;

		if(i%10000 == 0)
			printf("[%ld/%d] Matches\n", i, AMOUNT_MATCHES);
	}
	printf("matching done!\n");

	unsigned int amount_vertex[AMOUNT_NODES];
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
	for(unsigned int i = 0; i < AMOUNT_NODES; i++) {
		delete nodes[i];
	}

	delete nodes;
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

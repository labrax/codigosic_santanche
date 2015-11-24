/*
 * statistics.cpp
 *
 *  Created on: Oct 20, 2015
 *      Author: vroth
 */

#ifndef _STATISTICS_IMPL_
#define _STATISTICS_IMPL_

#include "statistics.hpp"

#include <queue>
#include <set>
#include <limits>

#include <unordered_set>

using std::queue;
using std::pair;
using std::set;

using std::unordered_set;

template <class T>
Statistics<T>::Statistics(Graph<T> & g) : graph(g), amount_vertex(NULL), amount_nodes(0), amount_edges(0), distances(NULL), max_distance(0) {

}

template <class T>
Statistics<T>::~Statistics() {
	if(amount_vertex != NULL)
		delete(amount_vertex);
	if(distances != NULL) {
		delete(distances);
	}
}

template <class T>
void Statistics<T>::getAll() {
	edgesDistribution();
	//distanceToNodes(); //ineficiente
}

template <class T>
void Statistics<T>::printAll() {
	printf("\"countFirst\",%ld\n\"countSecond\",%ld\n\"countBoth\",%ld\n\"countNone\",%ld\n", graph.getCountFirst(), graph.getCountSecond(), graph.getCountBoth(), graph.getCountNone());

	if(amount_vertex != NULL)
	{
		printf("\"amount outgoing edges\",\"amount nodes\"\n");
		for(std::size_t i = 0; i < graph.size(); i++) {
			if(amount_vertex[i] != 0)
				printf("%ld,%ld\n", i, amount_vertex[i]);
		}
	}

	printf("\"amount_nodes\",%ld\n", amount_nodes);
	printf("\"amount_edges\",%ld\n", amount_edges);
	printf("\"<k>\",%lf\n", ((double) amount_edges)/amount_nodes);

	/*printf("\"node id\",\"avg distance\"\n");
	for(std::size_t i = 0; i < graph.size(); i++)
	{
		printf("%ld,%lf\n", i, distances[i]);
	}
	printf("\"dmax\",%ld\n", max_distance);*/
}

template <class T>
void Statistics<T>::edgesDistribution() {
	if(amount_vertex != NULL)
		delete amount_vertex;
	amount_vertex = new long int[graph.size()];

	for(std::size_t i = 0; i < graph.size(); i++) {
		amount_vertex[i] = 0;
	}

	amount_edges = 0;
	amount_nodes = graph.size();
	for(std::size_t i = 0; i < graph.size(); i++) {
		amount_vertex[graph.getNode(i)->getSet().size()]++;

		amount_edges += graph.getNode(i)->getSet().size();
		//printf("%ld ", graph.getNode(i)->getSet().size());
	}
	//printf("\n");
}

template <class T>
void Statistics<T>::distanceToNodes() {
	if(distances != NULL) {
		delete(distances);
	}

	/*dar um jeito sem alocar a matriz toda!*/
	distances = new double [graph.size()];

#pragma omp parallel for
	for(std::size_t i = 0; i < graph.size(); i++)
	{
		long int amount = 0;
		distances[i] = 0;
		for(std::size_t j = 0; j < graph.size(); j++)
		{
			if(j == i)
				continue;

			std::size_t curr_dist = distanceBetweenNodes(graph[i], graph[j]);
			if(curr_dist != -1)
			{
#pragma omp critical
				{
					if(amount == 0)
						distances[i] = curr_dist;
					else
						distances[i] = (distances[i]*amount + curr_dist )/(amount + 1);
					amount++;

					if(curr_dist > max_distance)
						max_distance = curr_dist;

					/*printf("curr_dist = %ld\n", curr_dist);
					printf("curr mean distance[%ld] = %ld\n", i, curr_dist);
					fflush(stdout);*/
				}
			}
		}
	}
}

template <class T>
inline std::size_t Statistics<T>::distanceBetweenNodes(T * src, T * dest) {
	unordered_set<Node *> visited;

	queue< pair<std::size_t, Node *> > queue;
	queue.push( pair<std::size_t, Node *> (0, (Node *) src) );
	while(!queue.empty()) {
		pair<std::size_t, Node *> curr = queue.front();
		queue.pop();
		if(curr.second == dest)
			return curr.first;

		set<Node *> neighbours = ((Node *)curr.second)->getSet();

		for(set<Node *>::iterator it = neighbours.begin(); it != neighbours.end(); ++it) {
			if(visited.find(*it) == visited.end()) {
				visited.insert(*it);
				queue.push( pair< std::size_t, Node * > (curr.first+1, *it) );
			}
		}
	}
	return std::numeric_limits<std::size_t>::max();
}
#endif

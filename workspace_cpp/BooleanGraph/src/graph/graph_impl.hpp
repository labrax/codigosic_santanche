/*
 * graph.cpp
 *
 *  Created on: Oct 20, 2015
 *      Author: vroth
 */

#ifndef _GRAPH_IMPL_
#define _GRAPH_IMPL_

#include "graph.hpp"

template <class T>
Graph<T>::Graph(std::vector<T *> source_graph) : graph_data(source_graph) {

}

template <class T>
Graph<T>::~Graph() {

}

template <class T>
void Graph<T>::free() {
	while(graph_data.size() > 0) {
		delete(graph_data.back());
		graph_data.pop_back();
	}
}

template <class T>
void Graph<T>::matching(std::size_t amount_matches, RandomGenerator & gs) {
	count_both = 0;
	count_first = 0;
	count_second = 0;
	count_none = 0;

#ifdef MATCH_SOME
	//match only some nodes (random)
#pragma omp parallel for
	for(std::size_t i = 0; i < amount_matches; i++)
	{
		std::size_t first = gs.getInteger(graph_data.size());
		std::size_t second = gs.getInteger(graph_data.size());

		bool first_try, second_try;
		first_try = graph_data[first]->try_friendship(graph_data[second]);
		second_try = graph_data[second]->try_friendship(graph_data[first]);

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
	printf("\"RANDOM MATCHING\",%ld\n", amount_matches);
#else
	//match all
	for(std::size_t i = 0 ; i < graph_data.size(); i++)
	{
#pragma omp parallel for
		for(std::size_t j = i+1 ; j < graph_data.size(); j++)
		{
			std::size_t first = i;
			std::size_t second = j;

			bool first_try, second_try;
			first_try = graph_data[first]->try_friendship(graph_data[second]);
			second_try = graph_data[second]->try_friendship(graph_data[first]);

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
		/*if(i%(AMOUNT_MATCHES/10) == 0)
			printf("[%ld/%d] Matches\n", i, AMOUNT_MATCHES);*/
	}
	printf("\"ALL MATCHING\",%ld\n", amount_matches*(amount_matches-1)/2);
#endif
}

template <class T>
std::size_t Graph<T>::size() {
	return graph_data.size();
}

template <class T>
void Graph<T>::insert(T * node) {
	graph_data.insert(graph_data.end(), node);
}

template <class T>
long int Graph<T>::getCountBoth() {
	return count_both;
}

template <class T>
long int Graph<T>::getCountFirst() {
	return count_first;
}

template <class T>
long int Graph<T>::getCountSecond() {
	return count_second;
}

template <class T>
long int Graph<T>::getCountNone() {
	return count_none;
}

template <class T>
T * Graph<T>::getNode(std::size_t id) {
	return graph_data[id];
}

template <class T>
T * Graph<T>::operator[](std::size_t id) {
	return graph_data[id];
}

template <class T>
std::vector<T *> & Graph<T>::getNodes() {
	return graph_data;
}

#endif

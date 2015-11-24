/*
 * statistics.hpp
 *
 *  Created on: Oct 20, 2015
 *      Author: vroth
 */

#ifndef SRC_GRAPH_STATISTICS_HPP_
#define SRC_GRAPH_STATISTICS_HPP_

#include "graph.hpp"

template <class T>
class Statistics {
private:
	Graph<T> graph;
	long int * amount_vertex;

	long int amount_nodes;
	long int amount_edges;

	double * distances; //mean distance for each node
	unsigned long int max_distance;
public:
	Statistics(Graph<T> & g);
	virtual ~Statistics();
	void getAll();
	void printAll();
	void edgesDistribution();
	void distanceToNodes();
	inline std::size_t distanceBetweenNodes(T * src, T * dest);
};

#include "statistics_impl.hpp"
#endif /* SRC_GRAPH_STATISTICS_HPP_ */

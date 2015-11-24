/*
 * graph.hpp
 *
 *  Created on: Oct 20, 2015
 *      Author: vroth
 */

#ifndef SRC_GRAPH_GRAPH_HPP_
#define SRC_GRAPH_GRAPH_HPP_

#include <vector>
#include <cstdio>

#include "node.hpp"
#include "../random_generator.hpp"
#include "../config.hpp"

using std::vector;

template <class T>
class Graph {
private:
	std::vector<T *> graph_data;
	long int count_both = 0, count_first = 0, count_second = 0, count_none = 0;
public:
	Graph(std::vector<T *>);
	virtual ~Graph();
	void free();
	void matching(std::size_t amount_matches, RandomGenerator & gs);
	std::size_t size();
	void insert(T *);
	long int getCountBoth();
	long int getCountFirst();
	long int getCountSecond();
	long int getCountNone();
	T * getNode(std::size_t id);
	T * operator[](std::size_t id);
	std::vector<T *> & getNodes();
};

#include "graph_impl.hpp"
#endif /* SRC_GRAPH_GRAPH_HPP_ */

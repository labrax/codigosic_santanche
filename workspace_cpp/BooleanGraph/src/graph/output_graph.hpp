/*
 * OutputGraph.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef GRAPH_OUTPUT_GRAPH_HPP_
#define GRAPH_OUTPUT_GRAPH_HPP_

#include <vector>
#include "node.hpp"

class OutputGraph {
public:
	OutputGraph() {} ;
	virtual ~OutputGraph() {} ;
	virtual void insertNodes(std::vector<Node *> &) = 0;
};

#endif /* GRAPH_OUTPUT_GRAPH_HPP_ */

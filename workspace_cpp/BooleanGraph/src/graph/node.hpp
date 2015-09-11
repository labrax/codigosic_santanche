/*
 * Node.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef GRAPH_NODE_HPP_
#define GRAPH_NODE_HPP_

#include <set>

#include "../boolean/boolean_store.hpp"
#include "../boolean/nodes/boolean_node.hpp"

class BooleanStore;

class BooleanNode;

class Node {
private:
	std::set<Node *> neighbours;
	unsigned int id;
	BooleanStore * boolean_store;
	BooleanNode * boolean_node;
public:
	Node(unsigned int id, BooleanStore * boolean_store, BooleanNode * boolean_node);
	virtual ~Node();
	unsigned int getId();
	void insertNeighbour(Node * elem);
	std::set<Node *> getSet();
	BooleanStore * getBooleanStore();
	bool try_friendship(Node * other);
};

#endif /* GRAPH_NODE_HPP_ */

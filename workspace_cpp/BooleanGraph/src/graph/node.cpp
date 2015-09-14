/*
 * Node.cpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#include "node.hpp"

Node::Node(unsigned int id, BooleanStore * boolean_store, BooleanNode * boolean_node) : id(id), boolean_store(boolean_store), boolean_node(boolean_node) {
	neighbours = std::set<Node *>();
}

Node::~Node() {
	delete boolean_node;
	delete boolean_store;
}

unsigned int Node::getId() {
	return id;
}

void Node::insertNeighbour(Node * elem) {
	neighbours.insert(elem);
}

std::set<Node *> Node::getSet() {
	return neighbours;
}

BooleanStore * Node::getBooleanStore() {
	return boolean_store;
}

bool Node::try_friendship(Node * other) {
	if(boolean_node->compute(other->getBooleanStore()))
	{
		#pragma omp critical
		{
			neighbours.insert(other);
		}
		return true;
	}
	return false;
}

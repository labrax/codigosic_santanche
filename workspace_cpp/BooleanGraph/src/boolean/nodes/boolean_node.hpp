/*
 * BooleanNode.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef BOOLEAN_BOOLEANNODE_HPP_
#define BOOLEAN_BOOLEANNODE_HPP_

#include <string>

#include "../boolean_store.hpp"

typedef enum {NONE, BOOLEAN_NOT, BOOLEAN_AND, BOOLEAN_OR, BOOLEAN_VAR} boolean_node_type;

class BooleanNode {
protected:
	BooleanNode() { btype = NONE; };
	boolean_node_type btype;
public:
	virtual ~BooleanNode() {} ;
	virtual std::string toString() { return std::string(); } ;
	virtual bool compute(BooleanStore * bs) { return false; } ;
	boolean_node_type getType() { return btype; };
};

#endif /* BOOLEAN_BOOLEANNODE_HPP_ */

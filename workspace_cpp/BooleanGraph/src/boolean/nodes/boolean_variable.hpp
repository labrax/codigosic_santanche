/*
 * boolean_variable.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef BOOLEAN_VARIABLE_HPP_
#define BOOLEAN_VARIABLE_HPP_

#include <string>

#include "boolean_node.hpp"
#include "../boolean_store.hpp"

class BooleanVariable: public BooleanNode {
private:
	unsigned int id;
public:
	BooleanVariable(unsigned int id);
	virtual ~BooleanVariable();
	virtual std::string toString();
	virtual bool compute(BooleanStore *bs);
};

#endif /* BOOLEAN_VARIABLE_HPP_ */

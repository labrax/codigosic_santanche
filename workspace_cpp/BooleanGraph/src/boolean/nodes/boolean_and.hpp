/*
 * BooleanAND.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef BOOLEAN_BOOLEANAND_HPP_
#define BOOLEAN_BOOLEANAND_HPP_

#include <string>

#include "boolean_node.hpp"

class BooleanAND: public BooleanNode {
private:
	BooleanNode *right, *left;
public:
	BooleanAND(BooleanNode *right, BooleanNode *left);
	virtual ~BooleanAND();
	virtual std::string toString();
	virtual bool compute(BooleanStore *bs);
};

#endif /* BOOLEAN_BOOLEANAND_HPP_ */

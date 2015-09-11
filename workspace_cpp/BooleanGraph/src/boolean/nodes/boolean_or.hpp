/*
 * BooleanOR.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef BOOLEAN_BOOLEANOR_HPP_
#define BOOLEAN_BOOLEANOR_HPP_

#include <string>

#include "boolean_node.hpp"

class BooleanOR: public BooleanNode {
private:
	BooleanNode *left, *right;
public:
	BooleanOR(BooleanNode *right, BooleanNode *left);
	virtual ~BooleanOR();
	virtual std::string toString();
	virtual bool compute(BooleanStore *bs);
};

#endif /* BOOLEAN_BOOLEANOR_HPP_ */

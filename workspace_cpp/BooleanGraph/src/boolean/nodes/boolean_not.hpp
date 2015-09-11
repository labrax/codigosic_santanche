/*
 * BooleanNOT.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef BOOLEAN_BOOLEANNOT_HPP_
#define BOOLEAN_BOOLEANNOT_HPP_

#include "boolean_node.hpp"

class BooleanNOT: public BooleanNode {
private:
	BooleanNode *down;
public:
	BooleanNOT(BooleanNode *down);
	virtual ~BooleanNOT();
	virtual std::string toString();
	virtual bool compute(BooleanStore *bs);
	BooleanNode * getDown();
};

#endif /* BOOLEAN_BOOLEANNOT_HPP_ */

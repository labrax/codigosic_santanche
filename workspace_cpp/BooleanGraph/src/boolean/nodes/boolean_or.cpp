/*
 * BooleanOR.cpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#include "boolean_or.hpp"

BooleanOR::BooleanOR(BooleanNode * left, BooleanNode * right) : left(left), right(right) { btype = BOOLEAN_OR; }

BooleanOR::~BooleanOR() { }

std::string BooleanOR::toString() {
	return std::string("( " + left->toString() + " || " + right->toString() + ")");
}

bool BooleanOR::compute(BooleanStore *bs) {
	return left->compute(bs) || right->compute(bs);
}

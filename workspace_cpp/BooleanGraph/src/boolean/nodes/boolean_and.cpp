/*
 * BooleanAND.cpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#include "boolean_and.hpp"

BooleanAND::BooleanAND(BooleanNode * right, BooleanNode * left) : right(right), left(left) { btype = BOOLEAN_AND; }

BooleanAND::~BooleanAND() { }

std::string BooleanAND::toString() {
	return std::string("(" + left->toString() + " && " + right->toString() + ")");
}

bool BooleanAND::compute(BooleanStore *bs) {
	return left->compute(bs) && right->compute(bs);
}

/*
 * BooleanNOT.cpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#include "boolean_not.hpp"

BooleanNOT::BooleanNOT(BooleanNode * down) : down(down) { btype = BOOLEAN_NOT; }

BooleanNOT::~BooleanNOT() { }

std::string BooleanNOT::toString() {
	return std::string("!" + down->toString());
}

bool BooleanNOT::compute(BooleanStore *bs) {
	return !down->compute(bs);
}

BooleanNode * BooleanNOT::getDown() {
	return down;
}

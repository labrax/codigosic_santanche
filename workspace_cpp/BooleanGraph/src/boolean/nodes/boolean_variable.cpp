/*
 * boolean_variable.cpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#include <cstdio>

#include "boolean_variable.hpp"

BooleanVariable::BooleanVariable(unsigned int id) : id(id) { btype = BOOLEAN_VAR; }

BooleanVariable::~BooleanVariable() { }

std::string BooleanVariable::toString() {
	char num[16];
	sprintf(num, "X%u", id);
	num[15] = '\0';
	return std::string(num);
}

bool BooleanVariable::compute(BooleanStore *bs) {
	return bs->getBool(id);
}

/*
 * boolean_store.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef BOOLEAN_BOOLEAN_STORE_HPP_
#define BOOLEAN_BOOLEAN_STORE_HPP_

#include <unordered_map>

#include "../config.hpp"

class BooleanStore {
private:
#ifdef UNORDERED_MAP
	std::unordered_map<unsigned int, bool> map;
#else
	bool map[AMOUNT_VARIABLES];
#endif

public:
	BooleanStore();
	virtual ~BooleanStore() {} ;
	void addBool(unsigned int id, bool value);
	bool getBool(unsigned int id);
};

#endif /* BOOLEAN_BOOLEAN_STORE_HPP_ */

/*
 * boolean_store.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef BOOLEAN_BOOLEAN_STORE_HPP_
#define BOOLEAN_BOOLEAN_STORE_HPP_

#include <map>
#include <bitset>

#include "../config.hpp"

class BooleanStore {
private:
#ifdef MAP
	std::map<unsigned int, bool> map;
#else
	std::bitset<AMOUNT_VARIABLES> map;
#endif

public:
	BooleanStore();
	virtual ~BooleanStore();
	void addBool(unsigned int id, bool value);
	bool getBool(unsigned int id);
};

#endif /* BOOLEAN_BOOLEAN_STORE_HPP_ */

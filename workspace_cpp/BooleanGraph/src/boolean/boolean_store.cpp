/*
 * boolean_store.cpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#include "boolean_store.hpp"

BooleanStore::BooleanStore() {
#ifdef UNORDERED_MAP
	 map = std::unordered_map<unsigned int, bool>();
#endif
}

void BooleanStore::addBool(unsigned int id, bool value) {
	#ifdef UNORDERED_MAP
		if(map.find(id) != map.end()) {
			std::unordered_map<unsigned int, bool>::iterator it = map.find(id);
			it->second = value;
		}
		else {
			map.insert({id, value});
		}
	#else
		map[id] = value;
	#endif
}

bool BooleanStore::getBool(unsigned int id) {
#ifdef UNORDERED_MAP
	if(map.find(id) != map.end())
		return map.at(id);
#else
	return map[id];
#endif
	return false;
}

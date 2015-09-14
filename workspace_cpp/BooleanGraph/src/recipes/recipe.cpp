/*
 * Recipe.cpp
 *
 *  Created on: Sep 14, 2015
 *      Author: vroth
 */

#include "recipe.hpp"

Recipe::Recipe(unsigned int id, std::string region_name, BooleanStore * boolean_store, BooleanNode * boolean_node) : Node(id, boolean_store, boolean_node), region_name(region_name){

}

Recipe::~Recipe() {

}

std::string Recipe::getRegionName() {
	return region_name;
}

/*
 * Recipe.hpp
 *
 *  Created on: Sep 14, 2015
 *      Author: vroth
 */

#ifndef SRC_RECIPE_HPP_
#define SRC_RECIPE_HPP_

#include <string>

#include "../graph/node.hpp"

class Recipe : public Node {
private:
	std::string region_name;
public:
	Recipe(unsigned int id, std::string region_name, BooleanStore * boolean_store, BooleanNode * boolean_node);
	virtual ~Recipe();
	std::string getRegionName();
};

#endif /* SRC_RECIPE_HPP_ */

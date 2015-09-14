/*
 * RecipesLoader.h
 *
 *  Created on: Sep 14, 2015
 *      Author: vroth
 */

#ifndef SRC_RECIPESLOADER_HPP_
#define SRC_RECIPESLOADER_HPP_

#include <map>
#include <string>
#include <vector>

#include "recipe.hpp"

class RecipesLoader {
private:
	unsigned int names_counter;
	unsigned int recipes_counter;

	FILE * fp;
	std::map<std::string, unsigned int> map_names;

	std::vector<Recipe *> recipes;

	unsigned int getIngredientId(std::string name);
public:
	RecipesLoader();
	virtual ~RecipesLoader();

	void readFile();
	void computeLine(char * pais, char * line);

	void printIngredientNamesId();
	std::vector<Recipe *> getRecipes();
};

#endif /* SRC_RECIPESLOADER_HPP_ */

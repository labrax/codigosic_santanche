/*
 * RecipesLoader.cpp
 *
 *  Created on: Sep 14, 2015
 *      Author: vroth
 */

#include "recipes_loader.hpp"

#include <cstdio>
#include <cstring>

#include "../boolean/nodes/boolean_and.hpp"
#include "../boolean/nodes/boolean_or.hpp"
#include "../boolean/nodes/boolean_variable.hpp"

const char RECIPES_FILE[256] = "/home/vroth/Desktop/Dados para pesquisa/scirep-cuisines-detail/allr_recipes.txt";

RecipesLoader::RecipesLoader() {
	names_counter = 0;
	recipes_counter = 0;
	fp = fopen(RECIPES_FILE, "r");
	if(!fp)
		printf("erro ao abrir arquivo %s\n", RECIPES_FILE);
}

RecipesLoader::~RecipesLoader() {
	if(fp)
		fclose(fp);
}

void RecipesLoader::readFile() {
	char pais[64], buff[4096], str[4096];
	while(fgets (buff, 4095, fp))
	{
		buff[4095] = '\0';
		sscanf(buff, "%s %[^\n]s", pais, str);

		if(strlen(str) > 3)
			computeLine(pais, str);
	}
}

void RecipesLoader::computeLine(char * pais, char * line) {
	char current_ingredient[64];
	unsigned int curr = 0;
	unsigned int max = strlen(line);

	BooleanStore * bs = new BooleanStore();
	BooleanNode * node = NULL;

	unsigned int i;
	for(i = 0 ; i <= max; i++) {
		if(line[i] == '\t' || line[i] == '\n' || line[i] == '\0')
		{
			current_ingredient[curr] = '\0';
			curr = 0;
			std::string a = std::string(current_ingredient);
			unsigned int id = getIngredientId(a);
			bs->addBool(id, true);

			if(node == NULL)
			{
				node = new BooleanVariable(id);
			}
			else
			{
				node = new BooleanAND(node, new BooleanVariable(id));
			}

			if(line[i] == '\0')
				break;
		}
		else
		{
			current_ingredient[curr] = line[i];
			curr++;
		}
	}

	if(node == NULL)
	{
		printf("something went wrong :(");
		printf("%s^%s\n", pais, line);
	}

	std::string s_pais = std::string(pais);
	recipes.insert(recipes.end(), new Recipe(recipes_counter, s_pais, bs, node));
	recipes_counter++;
}

unsigned int RecipesLoader::getIngredientId(std::string name) {
	std::map<std::string, unsigned int>::iterator it = map_names.find(name);
	if(it != map_names.end()) {
		return it->second;
	}

	map_names.insert({name, names_counter});
	names_counter++;
	return names_counter;
}

void RecipesLoader::printIngredientNamesId() {
	printf("Ingredient,Id\n");
	for(std::map<std::string, unsigned int>::iterator it = map_names.begin(); it != map_names.end(); it++)
	{
		printf("%s,%u\n", it->first.c_str(), it->second);
	}
}

std::vector<Recipe *> RecipesLoader::getRecipes() {
	return recipes;
}

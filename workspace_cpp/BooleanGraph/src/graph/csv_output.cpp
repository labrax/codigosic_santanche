/*
 * csv_output.cpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#include "csv_output.hpp"

CSVOutput::CSVOutput() {
	fp = fopen(CSV_FILE, "w");
	if(!fp)
		printf("erro ao abrir arquivos %s\n", CSV_FILE);
}

CSVOutput::~CSVOutput() {
	fclose(fp);
	fp = NULL;
}

void CSVOutput::printToFile(char * str) {
	if(fp)
		fprintf(fp, "%s", str);
}

void CSVOutput::insertNodes(std::vector<Node *> & nodes) {
	unsigned long int i = 0;
	unsigned long int amount = 0;
	for(std::vector<Node *>::iterator it = nodes.begin(); it != nodes.end(); ++it)
	{
		std::set<Node *> curr_set = (*it)->getSet();

		amount += curr_set.size();
		unsigned long int a = 0;
		for(std::set<Node *>::iterator it2 = curr_set.begin(); it2 != curr_set.end(); ++it2)
		{
			char str[64];
			sprintf(str, "%d,%d\n", (*it)->getId(), (*it2)->getId());
			printToFile(str);
			i++;
			a++;
		}
		if(a != curr_set.size())
		{
			printf("%ld %ld\n", a, curr_set.size());
		}
	}
	printf("to file: %ld/%ld\n", i, amount);
	fflush(fp);
}

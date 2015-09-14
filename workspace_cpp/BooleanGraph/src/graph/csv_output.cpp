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
	for(std::vector<Node *>::iterator it = nodes.begin(); it != nodes.end(); it++) {
		for(std::set<Node *>::iterator it2 = (*it)->getSet().begin(); it2 != (*it)->getSet().end(); it2++) {
			char str[64];
			sprintf(str, "%d,%d", (*it)->getId(), (*it2)->getId());
			printToFile(str);
		}
	}
}

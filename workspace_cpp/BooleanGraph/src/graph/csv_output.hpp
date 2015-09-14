/*
 * csv_output.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef CSV_OUTPUT_HPP_
#define CSV_OUTPUT_HPP_

#include <cstdio>
#include <string>
#include "output_graph.hpp"

class CSVOutput: public OutputGraph {
private:
	void printToFile(char * str);
	FILE * fp;
public:
	CSVOutput();
	virtual ~CSVOutput();
	virtual void insertNodes(std::vector<Node *> & nodes);
};

#endif /* CSV_OUTPUT_HPP_ */

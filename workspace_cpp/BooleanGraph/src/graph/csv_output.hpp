/*
 * csv_output.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef CSV_OUTPUT_HPP_
#define CSV_OUTPUT_HPP_

#include "output_graph.hpp"

class CSVOutput: public OutputGraph {
public:
	CSVOutput();
	virtual ~CSVOutput();
};

#endif /* CSV_OUTPUT_HPP_ */

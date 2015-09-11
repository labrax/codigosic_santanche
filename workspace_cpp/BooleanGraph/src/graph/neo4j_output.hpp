/*
 * neo4j_output.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef NEO4J_OUTPUT_HPP_
#define NEO4J_OUTPUT_HPP_

#include "output_graph.hpp"

class Neo4jOutput: public OutputGraph {
public:
	Neo4jOutput();
	virtual ~Neo4jOutput();
};

#endif /* NEO4J_OUTPUT_HPP_ */

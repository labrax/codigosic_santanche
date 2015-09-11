/*
 * BooleanExpressionFactory.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef BOOLEAN_BOOLEAN_EXPRESSION_FACTORY_HPP_
#define BOOLEAN_BOOLEAN_EXPRESSION_FACTORY_HPP_
class RandomGenerator;

class BooleanNode;

class BooleanExpressionFactory {
public:
	BooleanExpressionFactory();
	virtual ~BooleanExpressionFactory();

	BooleanNode * createExpression(RandomGenerator &rg);
};

#endif /* BOOLEAN_BOOLEAN_EXPRESSION_FACTORY_HPP_ */

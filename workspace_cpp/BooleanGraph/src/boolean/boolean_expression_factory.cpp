/*
 * BooleanExpressionFactory.cpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#include "boolean_expression_factory.hpp"

#include "../config.hpp"
#include "../random_generator.hpp"
#include "nodes/boolean_and.hpp"
#include "nodes/boolean_not.hpp"
#include "nodes/boolean_variable.hpp"

BooleanExpressionFactory::BooleanExpressionFactory() { }

BooleanExpressionFactory::~BooleanExpressionFactory() { }

BooleanNode * BooleanExpressionFactory::createExpression(RandomGenerator &rg)
{
	BooleanNode * curr = new BooleanVariable(0);
	for(unsigned int i = 1; i < AMOUNT_VARIABLES; )
	{
		bool random = rg.getBoolean();
		if(random == true)
		{
			curr = new BooleanAND(curr, new BooleanVariable(i));
			i++;
		}
		else
		{
			if(curr->getType() == BOOLEAN_NOT)
			{
				BooleanNode * tmp = ((BooleanNOT *) curr)->getDown();
				delete(curr);
				curr = tmp;
			}
			else
				curr = new BooleanNOT(curr);
		}
	}
	return curr;
}

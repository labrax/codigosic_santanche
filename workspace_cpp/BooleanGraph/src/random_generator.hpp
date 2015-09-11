/*
 * RandomGenerator.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef RANDOM_GENERATOR_HPP_
#define RANDOM_GENERATOR_HPP_

class RandomGenerator {
private:
	unsigned int seed;
public:
	RandomGenerator();
	virtual ~RandomGenerator();

	long rand();
	int getSeed();
	bool getBoolean();
	long getInteger(long max);
};

#endif /* RANDOM_GENERATOR_HPP_ */

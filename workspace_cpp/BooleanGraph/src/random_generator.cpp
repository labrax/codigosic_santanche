/*
 * RandomGenerator.cpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#include "random_generator.hpp"

#include <cstdio>
#include <cstdlib>
#include <ctime>
#include <random>

#include "config.hpp"

RandomGenerator::RandomGenerator() {
	if(RANDOM_SEED == 0)
		seed = time(NULL);
	else
		seed = RANDOM_SEED;
	srand(seed);
}

RandomGenerator::~RandomGenerator() { }

long RandomGenerator::rand()
{
	std::random_device rd;

	long dice_roll = rd();
	return dice_roll;
}

int RandomGenerator::getSeed() {
	return seed;
}

bool RandomGenerator::getBoolean() {
	long random = this->rand();

	if(random%2 == 0)
		return false;
	return true;
}

long RandomGenerator::getInteger(long max) {
	return (this->rand()%max);
}

/*
 * config.hpp
 *
 *  Created on: 5 de set de 2015
 *      Author: vroth
 */

#ifndef CONFIG_HPP_
#define CONFIG_HPP_

#define AMOUNT_VARIABLES 4
extern const unsigned int AMOUNT_NODES;
extern const unsigned int AMOUNT_MATCHES;

extern const int RANDOM_SEED;
extern const char CSV_FILE[256];

//#define MAP

#ifndef MATCH_ALL
	#define MATCH_SOME
#endif

#ifdef MATCH_SOME
	#undef MATCH_ALL
#endif

#endif /* CONFIG_HPP_ */

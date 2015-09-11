package br.unicamp.ic.bool;

import java.util.Random;

public class RandomGenerator {
	static Random randomGenerator = new Random();
	
	public static int getRandomInteger(int rangeHigh)
	{
		return randomGenerator.nextInt(rangeHigh);
	}
	
	public static boolean getRandomBoolean()
	{
		return randomGenerator.nextBoolean();
	}
}

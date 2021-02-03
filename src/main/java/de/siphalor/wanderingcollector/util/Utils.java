package de.siphalor.wanderingcollector.util;

import java.util.Random;

public class Utils {
	public static int randInclusive(Random random, int a, int b) {
		return Math.min(a, b) + (int) (random.nextFloat() * (Math.abs(a - b)));
	}
}

import java.util.Random;


public class Randomer {
	public static Random rand = new Random();
	
	public static int getRandom(int max)
	{
		return rand.nextInt(max);
	}
}

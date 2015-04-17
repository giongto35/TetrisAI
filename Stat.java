import java.io.*;
import java.util.*;

public class Stat {

	public static void main(String[] args) {
		PlayerSkeleton p = new PlayerSkeleton();
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Stat.txt", true)))) {
		    for (int i = 0; i <= 1000; i++) {
		    	int res = p.run(PlayerSkeleton.OPTMIZED_WEIGHT);
		    	System.out.println(res);
		    	out.println(res);
		    }
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
}

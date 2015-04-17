import java.io.*;
import java.util.*;

public class StatWithLookAhead {

	public static void main(String[] args) {
		PlayerSkeletonLookAhead p = new PlayerSkeletonLookAhead();
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("StatLookAhead.txt", true)))) {
		    for (int i = 0; i <= 100; i++) {
		    	int res = p.run(PlayerSkeletonLookAhead.OPTMIZED_WEIGHT);
		    	System.out.println(res);
		    	out.println(res);
		    }
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
}

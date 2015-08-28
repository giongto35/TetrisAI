import java.io.*;
import java.util.*;
public class GETraining {
	private static int POP_SIZE = 100;
	private static int WEAKEST_PERCENT = 10;
	private static final int NUM_WEIGHT = PlayerSkeleton2.NUM_WEIGHT;
	private static int MUTATION_PROB = 5; //%
	private static final int CNT_PRINT = 1;
	private static double[][] weightPop = new double[POP_SIZE][NUM_WEIGHT];
	private static double[] fitnessFunction = new double[POP_SIZE];
	private static double[] maxW = new double[POP_SIZE];

	private static double calcAVG(double[] weight) {
		double sum = 0;
		for (int i = 1; i <= 20; i++)
			sum += PlayerSkeleton2.run(weight);
		return sum / 20;
	}

	private static void createPopulation() throws FileNotFoundException{
		// -1 to 1
		Scanner sc = new Scanner(new File("GETraining.txt"));
		for (int i = 0; i < POP_SIZE; i++) {
			String line;
			if (sc.hasNext())
				line = sc.nextLine();
			else
				line = "";

			Scanner lineScanner = new Scanner(line);
			for (int j = 0; j < NUM_WEIGHT; j++) {
				if (lineScanner.hasNext()) { 
				weightPop[i][j] = lineScanner.nextDouble();
				} else {
					if (Arrays.asList(0).contains(j)) //positive start
						weightPop[i][j] = (Math.random() );
					else 
						weightPop[i][j] = (- Math.random() ); 
				}
			}
			lineScanner.close();
		}
		sc.close();
	}

	private static void printWeight(double[] maxW, double maxF) {
		for (int i = 0; i < NUM_WEIGHT; i++) {
			System.out.print(maxW[i] + " ");
		}
		System.out.println(": " + maxF);
	}

	private static double calcFitnessFunction() {
		double maxF = 0;
		for (int i = 0; i < POP_SIZE; i++) {
			fitnessFunction[i] = calcAVG(weightPop[i]);
			if (maxF < fitnessFunction[i]) {
				maxF = fitnessFunction[i];
				maxW = weightPop[i];
			}
		}
		printWeight(maxW, maxF);
		return maxF;
	}

	private static double[][] selectParents() { 
		double[][] res = new double[POP_SIZE][NUM_WEIGHT];
		double maxF = calcFitnessFunction();

		for (int i = 0; i < POP_SIZE; i++) {
			Boolean notAccepted = true;
			int index = 0;
			while (notAccepted) {
				index = (int)(POP_SIZE * Math.random());
				if (Math.random() < fitnessFunction[index] / maxF) notAccepted = false;
			}
			System.arraycopy(weightPop[index], 0, res[i], 0, weightPop[index].length);
		}	
		return res;
	}



	private static void crossOver() {
		for (int i = 0; i < POP_SIZE; i+=2) {
			for (int j = 0; j < NUM_WEIGHT; j++) {
				if (Math.random() <= 0.5) {
					double temp = weightPop[i][j];
					weightPop[i][j] = weightPop[i+1][j];
					weightPop[i+1][j] = temp;
				}
			}
		}
	}

	private static void mutation() {
		for (int i = 0; i < POP_SIZE; i++) {
			for (int j = 0; j < NUM_WEIGHT; j++) {
				if (Math.random() * 100 <= MUTATION_PROB) {
					weightPop[i][j] = weightPop[i][j] + (Math.random() * 0.4 - 0.2);
				}				
			}
		}
	}

	private static void trackHistory() throws FileNotFoundException{
        PrintWriter printer = new PrintWriter("GETraining.txt");
		for (int i = 0; i < POP_SIZE; i++) {
			for (int j = 0; j < NUM_WEIGHT; j++) {
				printer.write(weightPop[i][j] + " ");
			}
			printer.write("\n");
		}
		printer.close();
	}

	public static void main(String[] args) throws FileNotFoundException{
		createPopulation();
        	int cnt = 0;
		while (true) {

			System.arraycopy( selectParents(), 0, weightPop, 0, weightPop.length);
			cnt++;
			if (cnt % CNT_PRINT == 0) {
				trackHistory();
			}

			MUTATION_PROB = 1;

			crossOver();
			mutation();
		}
	}	
}

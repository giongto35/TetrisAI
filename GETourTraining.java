import java.io.*;
import java.util.*;
public class GETourTraining {
	private static int POP_SIZE = 30;
	private static int TOUR_SIZE =5;
	private static int CROSS_SIZE = (int) (POP_SIZE * 60 / 100);
	private static int NEW_SIZE = (int) (POP_SIZE * 0 / 100);
	private static final int NUM_WEIGHT = PlayerSkeleton.NUM_THETA + 1;
	private static int MUTATION_PROB = 5; //%
	private static final int CNT_PRINT = 1;
	private static double[][] weightPop = new double[POP_SIZE][NUM_WEIGHT + 1]; // last element is the fitness
	private static double[] maxW = new double[POP_SIZE];

	private static double calcAVG(double[] weight) {
		double sum = 0;
		for (int i = 1; i <= 20; i++)
			sum += PlayerSkeleton.run(weight);
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
			normalize(weightPop[i]);
			weightPop[i][NUM_WEIGHT] = calcAVG(weightPop[i]);
		}
		sc.close();
	}

	private static void printWeight(double[] maxW, double maxF) {
		for (int i = 0; i < NUM_WEIGHT; i++) {
			System.out.print(maxW[i] + " ");
		}
		System.out.println(": " + maxF);
	}

	private static void calcFitnessFunction() {
		double maxF = 0;
		for (int i = 0; i < POP_SIZE; i++) {
			weightPop[i][NUM_WEIGHT] = calcAVG(weightPop[i]);
			if (maxF < weightPop[i][NUM_WEIGHT] ) {
				maxF = weightPop[i][NUM_WEIGHT] ;
				maxW = weightPop[i];
			}
		}
		printWeight(maxW, maxF);
	}

	private static double[] tournamentSelection() {
		double[] res = null;
		for (int i = 0; i < TOUR_SIZE; i++) {
			int randomID = (int) (Math.random() * POP_SIZE);
			if (res == null || res[NUM_WEIGHT] < weightPop[randomID][NUM_WEIGHT]) {
				res = weightPop[randomID];
			}
		}
		return res;
	}

	private static void normalize(double[] weight) {
		double sum = 0;
		for (int j = 0; j < NUM_WEIGHT; j++) {
			sum += weight[j] * weight[j];
		}
		for (int j = 0; j < NUM_WEIGHT; j++) {
			weight[j] /= Math.sqrt(sum);
		}
	}

	private static void normalizeAll() {
		//normalized
		for (int i = 0; i < POP_SIZE; i++) {
			normalize(weightPop[i]);
		}		
	}

	private static double[][] selectParents() { 
		double[][] res = new double[POP_SIZE][NUM_WEIGHT + 1];
		// calcFitnessFunction();
		// for (int i = 0; i < POP_SIZE; i++) {
		// 	for (int j = 0; j <= NUM_WEIGHT; j++) {
		// 		System.out.print(weightPop[i][j] + " ");
		// 	}
		// 	System.out.println();
		// }
		// 	System.out.println();
		//replace 30% by crossover and the rest are top fittest
		//30% crossover
		for (int i = 0; i < CROSS_SIZE; i++) {
			double[] w1 = tournamentSelection();
			double[] w2 = tournamentSelection();
			if (Math.random() <= 0.5) {
				res[i] = crossover(w1, w2);
			}	
			else {
				res[i] = crossover2(w1, w2);
			}
			normalize(res[i]);
			res[i][NUM_WEIGHT] = calcAVG(res[i]);
		}
		//60% top fittes
		Arrays.sort(weightPop, new java.util.Comparator<double[]>() {
		    public int compare(double[] w1, double[] w2) {
		        return Double.compare(w1[NUM_WEIGHT], w2[NUM_WEIGHT]);
		    }
		});

		for (int i = CROSS_SIZE ; i < POP_SIZE; i++) {
			System.arraycopy(weightPop[i], 0, res[i], 0, weightPop[i].length);
		}
		printWeight(weightPop[POP_SIZE-1], weightPop[POP_SIZE-1][NUM_WEIGHT]);
		return res;
	}


	private static double[] crossover2(double[] w1, double w2[]) {
		double[] res = new double[NUM_WEIGHT + 1];
		for (int j = 0; j < NUM_WEIGHT; j++) {
				res[j] = w1[j] * w1[NUM_WEIGHT] + w2[j] * w2[NUM_WEIGHT];
		}
		return res;
	}

	private static double[] crossover(double[] w1, double w2[]) {
		double[] res = new double[NUM_WEIGHT + 1];
		for (int j = 0; j < NUM_WEIGHT; j++) {
			if (Math.random() <= 0.5) {
				res[j] = w1[j];
			} else {
				res[j] = w2[j];
			}
		}
		return res;
	}

	private static void mutation() {
		for (int i = 0; i < POP_SIZE; i++) {
			Boolean change = false;
			for (int j = 0; j < NUM_WEIGHT; j++) {
				if (Math.random() * 100 <= MUTATION_PROB) {
					weightPop[i][j] = weightPop[i][j] + (Math.random() * 0.4 - 0.2);
					change = true;
				}				
			}
			if (change) {
				normalize(weightPop[i]);
				weightPop[i][NUM_WEIGHT] = calcAVG(weightPop[i]);
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
			// for (int i = 0; i < POP_SIZE; i++) {
			// 	for (int j = 0; j <= NUM_WEIGHT; j++) {
			// 		System.out.print(weightPop[i][j] + " ");
			// 	}
			// 	System.out.println();
			// }
			// System.out.println();

			System.arraycopy(selectParents(), 0, weightPop, 0, weightPop.length);
			cnt++;
			if (cnt % CNT_PRINT == 0) {
				trackHistory();
			}

			mutation();

			// for (int i = 0; i < POP_SIZE; i++) {
			// 	for (int j = 0; j <= NUM_WEIGHT; j++) {
			// 		System.out.print(weightPop[i][j] + " ");
			// 	}
			// 	System.out.println();
			// }
			// System.out.println();
			// return;
		}
	}	
}
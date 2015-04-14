public class Training {

	private static double[] weight = new double[]{-0.1579998550004218,0.05999920700789061,-0.3409989000082214,-2.9999997000484946E-9,-0.040000104999804824 };
	private static double STEP = 0.1;

	private static void printWeightAndRes(double res) {
		for (int i = 0; i < weight.length; i++) {
			System.out.print(weight[i] + " ");
		}
		System.out.print(" : " + res);
		System.out.println();
	}

	private static double calcAVG(double[] weight) {
		double sum = 0;
		for (int i = 1; i <= 20; i++)
			sum += PlayerSkeleton.run(weight);
		return sum / 20;
	}

	private static void simpleLocalSearch(double[] weight) {
		double curRes = calcAVG(weight);
		printWeightAndRes(curRes);
		while(true) {
			for (int i = 0; i < weight.length; i++) {
				for (int d = -1; d <= 1; d+= 2) {
					weight[i] += d * STEP;
					double temp = calcAVG(weight);
					if (temp > curRes) {
						curRes = temp;
						printWeightAndRes(curRes);
					} else {
						weight[i] -= d * STEP;
					}
				}
			}
			STEP = STEP * 0.9999999;
		}
	}

	public static void main(String[] args) {
		simpleLocalSearch(weight);
	}	
}
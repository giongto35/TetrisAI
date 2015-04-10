public class Training {

	private static double[] weight = new double[]{0, -0.00, -0.00, -0.0, -0.0};
	private static double STEP = 0.001;

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
		for (int cnt = 0; cnt < 1000; cnt++) {
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
		}
	}

	public static void main(String[] args) {
		simpleLocalSearch(weight);
	}	
}
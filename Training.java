public class Training {

	private static double[] weight = new double[]{-0.09800000000000002,0.0,-0.16099999999999998,-0.01,-0.15000000000000002};
	private static double STEP = 0.05;

	private static void printWeightAndRes(double res) {
		for (int i = 0; i < weight.length; i++) {
			System.out.print(weight[i] + " ");
		}
		System.out.print(" : " + res);
		System.out.println();
	}

	private static double calcAVG(double[] weight) {
		double sum = 0;
		for (int i = 1; i <= 100; i++)
			sum += PlayerSkeleton.run(weight);
		return sum / 100;
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
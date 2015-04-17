import java.util.Scanner;
import java.io.*;

public class PlayerSkeletonLookAhead {
	public static final double[] OPTMIZED_WEIGHT = {0.20613015761866432,-0.05536433443977351,
		-0.3188696096899921,-0.05172347551762019,-0.3484492519285215,-1.4842574404943563,
		-0.10837874114553062,0.024071812333622405,0.29179709784558416};
	private static final int oo = 1000000;
	private static final int COLS = 10;
	private static final int ROWS = State.ROWS;
	private static final int OFFSET_HEIGHT = 1;
	private static final int OFFSET_DIFF = OFFSET_HEIGHT + COLS;
	private static final int OFFSET_MAX_HEIGHT = OFFSET_DIFF + COLS - 1;
	private static final int OFFSET_MAX_DIFF = OFFSET_MAX_HEIGHT + 1;
	private static final int OFFSET_NUM_HOLES = OFFSET_MAX_DIFF + 1;
	private static final int OFFSET_SUM_HOLES = OFFSET_NUM_HOLES + 1;
	private static final int OFFSET_DEEPEST_HOLES = OFFSET_SUM_HOLES + 1;
	private static final int OFFSET_TOUCH = OFFSET_DEEPEST_HOLES + 1;
	private static final int MAX_THETA = OFFSET_TOUCH;
	private static final int N_PIECES = 7;
	public static final int NUM_WEIGHT = 9;

	private double[] weight = new double[MAX_THETA + 1];
	
	protected static int[][][] legalMoves = new int[N_PIECES][][];
	
	private static final int ORIENT = 0;
	private static final int SLOT = 1;
	private static int[] pOrients = State.getpOrients();
	private static int[][] pWidth = State.getpWidth();
	private static int[][] pHeight = State.getpHeight();
	private static int[][][] pBottom = State.getpBottom();
	private static int[][][] pTop = State.getpTop();
	//initialize legalMoves
	{
		//for each piece type
		for(int i = 0; i < N_PIECES; i++) {
			//figure number of legal moves
			int n = 0;
			for(int j = 0; j < pOrients[i]; j++) {
				//number of locations in this orientation
				n += COLS+1-pWidth[i][j];
			}
			//allocate space
			legalMoves[i] = new int[n][2];
			//for each orientation
			n = 0;
			for(int j = 0; j < pOrients[i]; j++) {
				//for each slot
				for(int k = 0; k < COLS+1-pWidth[i][j];k++) {
					legalMoves[i][n][ORIENT] = j;
					legalMoves[i][n][SLOT] = k;
					n++;
				}
			}
		}
	
	}

	private double calc(int[][] field, int top[], int orient, int slot, int nextPiece) {
		// calc reward		
		int height = top[slot]-pBottom[nextPiece][orient][0];

		for(int c = 1; c < pWidth[nextPiece][orient];c++) {
			height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
		}
		
		if(height+pHeight[nextPiece][orient] >= ROWS) {
			return -oo;
		}

		for(int i = 0; i < pWidth[nextPiece][orient]; i++) {
			for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
				field[h][i+slot] = 1;
			}
		}
		
		for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
			top[slot+c]=height+pTop[nextPiece][orient][c];
		}
		
		int rowsCleared = 0;
		
		for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
			boolean full = true;
			for(int c = 0; c < COLS; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
			}
			if(full) {
				rowsCleared++;
				for(int c = 0; c < COLS; c++) {

					for(int i = r; i < top[c]; i++) {
						field[i][c] = field[i+1][c];
					}
					top[c]--;
					while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
				}
			}
		}
		
		// calc utility
		double utility = calcUtility(calcTheta(rowsCleared, field, top));

		// DEBUG
		// for (int i = 0; i < ROWS; i++) {
		// 	for (int j = 0; j < COLS; j++) {
		// 		System.out.print(field[i][j]);
		// 	}
		// 	System.out.println();
		// }
		// System.out.println();
		// for (int j = 0; j < COLS; j++) {
		// 	System.out.print(top[j]);
		// }
		// System.out.println();
		// System.out.println();

		return utility;
	}

	private double calcUtility(double[] theta) {
		double res = 0;
		for (int i = 0; i <= MAX_THETA; i++) {
			res = res + weight[i] * theta[i];
		}
		return res;
	}

	private double[] calcTheta(int rowsCleared, int[][] field, int[] top) {
		int maxHeight = 0;
		int maxDeep = 0;
		int numHoles = 0;
		int sumHoles = 0;
		int maxDiff = 0;
		int touchGround = 0;
		double[] theta = new double[MAX_THETA + 1];

		theta[0] = rowsCleared;

		for (int i = 0; i < COLS; i++) {
			theta[OFFSET_HEIGHT + i] = top[i];
			if (top[i] > 0) touchGround++;
			maxHeight = Math.max(maxHeight, top[i]);
		}

		for (int i = 1; i < COLS; i++) {
			theta[OFFSET_DIFF + i - 1] = Math.abs(top[i] - top[i-1]); 
			maxDiff = Math.max(maxDiff, Math.abs(top[i] - top[i-1]));
		}

		theta[OFFSET_MAX_HEIGHT] = maxHeight;
		theta[OFFSET_MAX_DIFF] = maxDiff;
		for (int j = 0; j < COLS; j++) {
			for (int i = 0; i < top[j]; i++) {
				if (field[i][j] == 0) {
					numHoles++;
					sumHoles += top[j] - i;
					maxDeep = Math.max(maxDeep, top[j] - i);
				}
			}
		}
		theta[OFFSET_NUM_HOLES] = numHoles;
		theta[OFFSET_SUM_HOLES] = sumHoles;
		theta[OFFSET_DEEPEST_HOLES] = maxDeep;
		theta[OFFSET_TOUCH] = top[0] + top[COLS-1] + touchGround;
		// for (int i = 0; i < theta.length; i++) {
		// 	System.out.print(theta[i] + " " );
		// }
		// System.out.println();
		return theta;
	}

	private int[][] cloneField(int[][] field) {
		int[][] newField = new int[ROWS][COLS];
		for(int i = 0; i < ROWS; i++)
			for (int j = 0; j < COLS; j++) 
				newField[i][j] = field[i][j];

		return newField;
	}

	private int[] cloneTop(int[] top) {
		int[] newTop = new int[COLS];
		for (int j = 0; j < COLS; j++)
			newTop[j] = top[j];

		return newTop;
	}

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		int[][] field = s.getField();
		int[] top = s.getTop();

		int bestMove = 0;
		double maxfval = -oo;
		for (int i = 0; i < legalMoves.length; i++) {
			int[][] clonedField = cloneField(field);
			int[] clonedTop = cloneTop(top);
			double fval = calc(clonedField, clonedTop, legalMoves[i][ORIENT], legalMoves[i][SLOT], s.getNextPiece());

			double sumLookAhead = 0;
			for (int j = 0; j < N_PIECES; j++) {
				double maxfval2 = -oo;
				for (int k = 0; k < this.legalMoves[j].length; k++) {
					maxfval2 = Math.max(maxfval2, calc(cloneField(clonedField), cloneTop(clonedTop), this.legalMoves[j][k][ORIENT], this.legalMoves[j][k][SLOT], j));
				}
				sumLookAhead = sumLookAhead + maxfval2;
			}

			if (fval + sumLookAhead / N_PIECES > maxfval) {
				bestMove = i;
				maxfval = fval + sumLookAhead / N_PIECES;
			}
		}
		// System.out.println();
		// System.out.println();
		return bestMove;
	}

	void fetchWeight(double[] inputWeight) {
		weight[0] = inputWeight[0];

		for (int i = 0; i < COLS; i++) {
			weight[OFFSET_HEIGHT + i] = inputWeight[1];
		}

		for (int i = 1; i < COLS; i++) {
			weight[OFFSET_DIFF + i] = inputWeight[2];
		}

		weight[OFFSET_MAX_HEIGHT] = inputWeight[3];
		weight[OFFSET_DIFF] = inputWeight[4];
		weight[OFFSET_NUM_HOLES] = inputWeight[5];
		weight[OFFSET_SUM_HOLES] = inputWeight[6];
		weight[OFFSET_DEEPEST_HOLES] = inputWeight[7];
		weight[OFFSET_TOUCH] = inputWeight[8];
	}

// This function is not for demonstration. Receive weight and return number of rows cleared.
	public static int run(double[] weight) {
		PlayerSkeletonLookAhead p = new PlayerSkeletonLookAhead();
		p.fetchWeight(weight);
		State s = new State();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s, s.legalMoves()));
		}
		return s.getRowsCleared();
	}
	
	public static double[] readWeight() {
		Scanner sc = new Scanner(System.in);
		double[] inputWeight = new double[NUM_WEIGHT];
		for (int i = 0; i < NUM_WEIGHT; i++) {
			inputWeight[i] = sc.nextDouble();
		}
		// sc.close();
		return inputWeight;
	}

	private static double calcAVG(double[] weight) {
		double sum = 0;
		for (int i = 1; i <= 20; i++)
			sum += PlayerSkeletonLookAhead.run(weight);
		return sum / 20;
	}

	public static void main(String[] args) {
		// PlayerSkeleton p = new PlayerSkeleton();

		// System.out.println(calcAVG(OPTMIZED_WEIGHT));

		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();

		p.fetchWeight(OPTMIZED_WEIGHT);
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");	
	}
}

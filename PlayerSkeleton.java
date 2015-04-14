import java.util.Scanner;
import java.io.*;

public class PlayerSkeleton {
	private static final int oo = 1000000;
	private static final int COLS = 10;
	private static final int ROWS = State.ROWS;
	private static final int OFFSET_HEIGHT = 1;
	private static final int OFFSET_DIFF = COLS + 1;
	private static final int OFFSET_MAX_HEIGHT = COLS * 2;
	private static final int OFFSET_NUM_HOLES = COLS * 2 + 1;
	private static final int MAX_THETA = 21;
	private static final int N_PIECES = 7;
	private static final int NUM_WEIGHT = 5;

	private double[] weight = new double[MAX_THETA + 1];
	
	protected int nextPiece;	
	protected static int[][][] legalMoves = new int[N_PIECES][][];
	
	private static final int ORIENT = 0;
	private static final int SLOT = 1;
	protected static int[] pOrients = {1,2,4,4,4,2,2};

	protected static int[][] pWidth = {
			{2},
			{1,4},
			{2,3,2,3},
			{2,3,2,3},
			{2,3,2,3},
			{3,2},
			{3,2}
	};
	private static int[][] pHeight = {
			{2},
			{4,1},
			{3,2,3,2},
			{3,2,3,2},
			{3,2,3,2},
			{2,3},
			{2,3}
	};
	private static int[][][] pBottom = {
		{{0,0}},
		{{0},{0,0,0,0}},
		{{0,0},{0,1,1},{2,0},{0,0,0}},
		{{0,0},{0,0,0},{0,2},{1,1,0}},
		{{0,1},{1,0,1},{1,0},{0,0,0}},
		{{0,0,1},{1,0}},
		{{1,0,0},{0,1}}
	};
	private static int[][][] pTop = {
		{{2,2}},
		{{4},{1,1,1,1}},
		{{3,1},{2,2,2},{3,3},{1,1,2}},
		{{1,3},{2,1,1},{3,3},{2,2,2}},
		{{3,2},{2,2,2},{2,3},{1,2,1}},
		{{1,2,2},{3,2}},
		{{2,2,1},{2,3}}
	};

	private double calc(int[][] field, int top[], int orient, int slot) {
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
		double utility = calcUtility(calcTheta(field, top, rowsCleared));

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

		return  utility;
	}

	private int calcNumHoles(int[][] field, int[] top) {
		int res = 0;
		for (int j = 0; j < COLS; j++) {
			for (int i = 0; i < top[j]; i++) {
				if (field[i][j] == 0) {
					res++;
				}
			}
		}
		return res;
	}

	private double calcUtility(int[] theta) {
		double res = 0;
		for (int i = 0; i <= MAX_THETA; i++) {
			res = res + weight[i] * theta[i];
			// System.out.print(weight[i] + " " + theta[i] + "|");
		}
		// System.out.println();
		// System.out.println(res);
		return res;
	}

	private int[] calcTheta(int[][] field, int[] top, int reward) {
		int maxHeight = 0;
		int[] theta =  new int[MAX_THETA + 1];

		theta[0] = reward;

		for (int i = 0; i < COLS; i++) {
			theta[OFFSET_HEIGHT + i] = top[i];
		}

		for (int i = 1; i < COLS; i++) {
			maxHeight = Math.max(maxHeight, Math.abs(top[i] - top[i-1]));
			theta[OFFSET_DIFF + i - 1] = (int)Math.pow(Math.abs(top[i] - top[i-1]),1) ; 
		}

		theta[OFFSET_MAX_HEIGHT] = (int)Math.pow(maxHeight,1);
		theta[OFFSET_NUM_HOLES] = (int)Math.pow(calcNumHoles(field, top),1);

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
		nextPiece = s.getNextPiece(); //global

		int bestMove = 0;
		double maxfval = -oo;
		for (int i = 0; i < legalMoves.length; i++) {
			double fval = calc(cloneField(field), cloneTop(top), legalMoves[i][ORIENT], legalMoves[i][SLOT]);
			// System.out.print(i + " " + fval + "|");
			if (fval > maxfval) {
				bestMove = i;
				maxfval = fval;
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
			weight[OFFSET_DIFF + i - 1] = inputWeight[2];
		}

		weight[OFFSET_MAX_HEIGHT] = inputWeight[3];
		weight[OFFSET_NUM_HOLES] = inputWeight[4];
	}

// This function is not for demonstration. Receive weight and return number of rows cleared.
	public static int run(double[] weight) {
		PlayerSkeleton p = new PlayerSkeleton();
		p.fetchWeight(weight);
		State s = new State();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s, s.legalMoves()));
		}
		p = null;
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

	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();

		p.fetchWeight(readWeight());
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			int[] temp = p.calcTheta(s.getField(), s.getTop(), 1);
			// FOR DEBUG THETA
			// for (int i = 0; i <= MAX_THETA; i++) {
			// 	System.out.print(temp[i] + " ");
			// }
			// System.out.println();
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}

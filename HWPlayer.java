import java.util.Arrays;

public class HWPlayer extends AlphaBetaPlayer {
	private double[][][][][] genome;

	public HWPlayer(int maxDepth) {
		this(maxDepth, GenomeFactory.getRandomGenome(2, 6, 7, 3, RunDirection.values().length));
	}

	public HWPlayer(int maxDepth, double[][][][][] genome) {
		super(maxDepth);
		this.maxDepth = maxDepth;
		this.genome = genome;
	}

	public double[][][][][] getGenome() {
		return genome;
	}

	private int maxDepth;

	public int getMaxDepth() {
		return this.maxDepth;
	}

	public String getName() {
		return "Darwin_" + Arrays.deepHashCode(genome);
	}

	public double evaluateUtility(ConnectFourBoard board) {
		if (board.isWinner(myDisc))
			return Double.POSITIVE_INFINITY;
		if (board.isWinner(myDisc.getOpponent()))
			return Double.NEGATIVE_INFINITY;

		double totalRunUtility = 0;

		for (int r = 0; r < board.getRows(); r++)
			for (int c = 0; c < board.getColumns(); c++) {
				Disc d = board.getDisc(r, c);
				if (d == null)
					continue;

				if (r + 4 < board.getRows())
					totalRunUtility += evaluateVerticalRunUtility(board, r, c, d);
				if (c + 4 < board.getColumns()) {
					totalRunUtility += evaluateHorizontalRunUtility(board, r, c, d);
					if (r + 4 < board.getColumns())
						totalRunUtility += evaluateUpDiagonalUtility(board, r, c, d);
					if (r - 4 >= 0)
						totalRunUtility += evaluateDownDiagonalUtility(board, r, c, d);
				}
			}

		return totalRunUtility;
	}

	public boolean equals(Object other) {
		return other instanceof HWPlayer && Arrays.deepEquals(genome, ((HWPlayer) other).genome);
	}

	private enum RunDirection {
		Horizontal(0), Vertical(1), Diagonal(2);
		private final int index;

		private RunDirection(int index) {
			this.index = index;
		}
	}

	private double evaluateVerticalRunUtility(ConnectFourBoard board, int row, int column, Disc disc) {
		int[][] points = {{row, column}, {row + 1, column}, {row + 2, column}, {row + 3, column}};
		return evaluateRunUtility(disc, ((row) * 4 + 6) / 4, column, countRunLength(board, points, disc), RunDirection.Vertical);
	}

	private double evaluateHorizontalRunUtility(ConnectFourBoard board, int row, int column, Disc disc) {
		int[][] points = {{row, column}, {row, column + 1}, {row, column + 2}, {row, column + 3}};
		return evaluateRunUtility(disc, row, (column * 4 + 6) / 4, countRunLength(board, points, disc), RunDirection.Horizontal);
	}

	private double evaluateUpDiagonalUtility(ConnectFourBoard board, int row, int column, Disc disc) {
		int[][] points = {{row, column}, {row + 1, column + 1}, {row + 2, column + 2}, {row + 3, column + 3}};
		return evaluateRunUtility(disc, ((row) * 4 + 6) / 4, (column * 4 + 6) / 4, countRunLength(board, points, disc), RunDirection.Diagonal);
	}

	private double evaluateDownDiagonalUtility(ConnectFourBoard board, int row, int column, Disc disc) {
		int[][] points = {{row, column}, {row - 1, column + 1}, {row - 2, column + 2}, {row - 3, column + 3}};
		return evaluateRunUtility(disc, ((row) * 4 - 6) / 4, (column * 4 + 6) / 4, countRunLength(board, points, disc), RunDirection.Diagonal);
	}

	private int countRunLength(ConnectFourBoard board, int[][] points, Disc disc) {
		int totalDiscs = 0;

		for (int[] p : points) {
			Disc d = board.getDisc(p[0], p[1]);
			if (d != null && d != disc)
				return 0;
			else if (d == disc)
				totalDiscs++;
		}

		return totalDiscs;
	}

	private double evaluateRunUtility(Disc disc, int row, int column, int runLength, RunDirection direction) {
		if (row < 0 || row >= genome[0].length || column < 0 || column >= genome[0][0].length || runLength <= 0 || runLength >= genome[0][0][0].length)
			return 0;

		return genome[disc == this.myDisc ? 0 : 1][row][column][runLength - 1][direction.index];
	}
}
import java.util.*;

public class AlphaBetaPlayer extends Player {
	private static class ColumnUtilityPair {
		public int column;
		public double utility;

		public ColumnUtilityPair(int column, double utility) {
			this.column = column;
			this.utility = utility;
		}

		public String toString() {
			return "(" + column + "," + utility + ")";
		}
	}

	private int maxDepth;

	public AlphaBetaPlayer(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public final int getMove(ConnectFourBoard board) throws InterruptedException {
		return getMove(board, true, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).column;
	}

	private final ColumnUtilityPair getMove(ConnectFourBoard board, boolean maximize, int depth, double alpha, double beta) throws InterruptedException {
		if (Thread.currentThread().isInterrupted())
			throw new InterruptedException();
		if (board.isGameOver() || depth == maxDepth)
			return new ColumnUtilityPair(-1, evaluateUtility(board));

		ArrayList<ColumnUtilityPair> bestMoves = new ArrayList<>();
		bestMoves.add(new ColumnUtilityPair(-1, maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY));
		ArrayList<Integer> columns = new ArrayList<>(board.getColumns());
		for (int i = 0; i < board.getColumns(); i++)
			columns.add(i);
		Collections.shuffle(columns);
		for (int column : columns) {
//		for (int column = 0; column < board.getColumns(); column++) {
			ConnectFourBoard copy = board.clone();
			try {
				copy.placeDisc(maximize ? myDisc : myDisc.getOpponent(), column);
				ColumnUtilityPair submove = getMove(copy, !maximize, depth + 1, alpha, beta);
				submove.column = column;
				if (submove.utility == bestMoves.get(0).utility)
					bestMoves.add(submove);
				else if (maximize) {
					if (submove.utility > bestMoves.get(0).utility) {
						if (submove.utility >= beta) {
							submove.utility = Double.POSITIVE_INFINITY;
							return submove;	// Terminate early
						}
						if (submove.utility > alpha)
							alpha = submove.utility;
						bestMoves.clear();
						bestMoves.add(submove);
					}
				} else
					if (submove.utility < bestMoves.get(0).utility) {
						if (submove.utility <= alpha) {
							submove.utility = Double.NEGATIVE_INFINITY;
							return submove;	// Terminate early
						}
						if (submove.utility < beta)
							beta = submove.utility;
						bestMoves.clear();
						bestMoves.add(submove);
					}
			} catch (IllegalArgumentException e) {
				continue;
			}
		}
		Collections.shuffle(bestMoves);
		return bestMoves.get(0);
	}

	public double evaluateUtility(ConnectFourBoard board) {
		if (board.isWinner(myDisc))
			return 1;
		if (board.isWinner(myDisc.getOpponent()))
			return -1;
		return 0;
	}

	public String getName() {
		return "AlphaBeta";
	}
}

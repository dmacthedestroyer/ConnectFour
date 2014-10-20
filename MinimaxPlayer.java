import java.util.*;

public class MinimaxPlayer extends Player {
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

	public MinimaxPlayer(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public final int getMove(ConnectFourBoard board) throws InterruptedException {
		return getMove(board, true, 0).column;
	}
	
	private final ColumnUtilityPair getMove(ConnectFourBoard board, boolean maximize, int depth) throws InterruptedException {
		if (Thread.currentThread().isInterrupted())
			throw new InterruptedException();
		if (board.isGameOver() || depth == maxDepth)
			return new ColumnUtilityPair(-1, evaluateUtility(board));
		
		ArrayList<ColumnUtilityPair> bestMoves = new ArrayList<>();
		for (int column = 0; column < board.getColumns(); column++) {
			ConnectFourBoard child = board.clone();
			try {
				child.placeDisc(maximize ? myDisc : myDisc.getOpponent(), column);
			} catch (IllegalArgumentException e) {
				continue;
			}
			ColumnUtilityPair move = getMove(child, !maximize, depth + 1);
			move.column = column;
			if (maximize) {
				if (bestMoves.isEmpty())
					bestMoves.add(move);
				else if (move.utility > bestMoves.get(0).utility) {
					bestMoves.clear();
					bestMoves.add(move);
				} else if (move.utility == bestMoves.get(0).utility)
					bestMoves.add(move);
			} else {
				if (bestMoves.isEmpty()) 
					bestMoves.add(move);
				else if (move.utility < bestMoves.get(0).utility) {
					bestMoves.clear();
					bestMoves.add(move);
				} else if (move.utility == bestMoves.get(0).utility)
					bestMoves.add(move);
			}
		}
		
		if (depth == 0)
			System.out.println(bestMoves);
		Collections.shuffle(bestMoves);
		return bestMoves.get(0);
	}

	public double evaluateUtility(ConnectFourBoard board) {
		if (board.isWinner(myDisc))
			return 100;
		if (board.isWinner(myDisc.getOpponent()))
			return -100;
		return -board.getOpenRuns(myDisc.getOpponent(), 2).size();
	}
	
	public String getName() {
		return "Minimax";
	}
}

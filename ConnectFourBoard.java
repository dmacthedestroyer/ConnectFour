import java.awt.Point;
import java.util.*;

public class ConnectFourBoard implements Cloneable {
	private final int rows;
	private final int columns;
	private Disc[][] board;
	
	public ConnectFourBoard() {
		this(6, 7);
	}

	public ConnectFourBoard(int rows, int columns) {
		if (rows < 1 || columns < 1)
			throw new IllegalArgumentException();
		
		this.rows = rows;
		this.columns = columns;
		board = new Disc[rows][columns];
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}

	public Disc getDisc(int row, int column) {
		if (row < 0 || column < 0 || row >= rows || column >= columns)
			throw new IllegalArgumentException();

		return board[row][column];
	}
	
	public void placeDisc(Disc disc, int column) {
		if (disc == null || column < 0 || column >= columns)
			throw new IllegalArgumentException();
		if (board[0][column] != null)
			throw new IllegalArgumentException();
		
		int row = 1;
		while (row < rows && board[row][column] == null)
			row++;
		board[row-1][column] = disc;
	}
	
	public boolean isGameOver() {
		int col = 0;
		while (col < columns && board[0][col] != null)
			col++;
		if (col == columns)
			return true;
		return isWinner(Disc.RED) || isWinner(Disc.YELLOW);
	}

	public boolean isWinner(Disc disc) {
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < columns; col++) {
				if (board[row][col] != disc)
					continue;
				if (col <= columns - 4)
					if (board[row][col+1] == disc && board[row][col+2] == disc && board[row][col+3] == disc)
						return true;
				if (row <= rows - 4)
					if (board[row+1][col] == disc && board[row+2][col] == disc && board[row+3][col] == disc)
						return true;
				if (row <= rows - 4 && col <= columns - 4)
					if (board[row+1][col+1] == disc && board[row+2][col+2] == disc && board[row+3][col+3] == disc)
						return true;
				if (row <= rows - 4 && col >= 3)
					if (board[row+1][col-1] == disc && board[row+2][col-2] == disc && board[row+3][col-3] == disc)
						return true;
			}
		return false;
	}
	
	private static int[] deltas = {-1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, 1, -1, 1};
	
	public Collection<Point> getOpenRuns(Disc disc, int length) {
		if (length < 1 || length > 3)
			throw new IllegalArgumentException();
		
		ArrayList<Point> points = new ArrayList<Point>();
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < columns; col++) {
				if (board[row][col] != null)
					continue;
				for (int i = 0; i < deltas.length; i += 2) {
					int j = 1;
					for (; j <= length; j++) {
						int testRow = row + j * deltas[i];
						int testCol = col + j * deltas[i+1];
						if (testRow < 0 || testRow >= rows || testCol < 0 || testCol >= columns)
							break;
						if (board[testRow][testCol] != disc)
							break;
					}
					if (j == length + 1) {
						points.add(new Point(col, row));
						break;
					}
				}
			}

		return points;
	}
	
	public ConnectFourBoard clone() {
		try {
			ConnectFourBoard copy = (ConnectFourBoard)super.clone();
			copy.board = new Disc[rows][columns];
			for (int row = 0; row < rows; row++)
				for (int col = 0; col < columns; col++)
					copy.board[row][col] = board[row][col];
			return copy;
		} catch (CloneNotSupportedException e) {
			assert false;
			return null;
		}
	}
}

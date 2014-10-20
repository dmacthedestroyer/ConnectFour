import javax.swing.event.*;

public class ConnectFourGameLogic implements Runnable {
	private final Player[] players;
	private int currentPlayer;
	private final ConnectFourBoard board;
	private final EventListenerList listenerList;
	private final GameUpdateEvent event;

	public ConnectFourGameLogic(int rows, int columns, Player player1, Player player2) {
		if (player1 == null || player2 == null)
			throw new IllegalArgumentException();

		players = new Player[]{player1, player2};
		player1.setDisc(Disc.values()[0]);
		player2.setDisc(Disc.values()[1]);
		board = new ConnectFourBoard(rows, columns);
		listenerList = new EventListenerList();
		event = new GameUpdateEvent(this);
	}

	public void run() {
		try {
			runGame();
		} catch (InterruptedException e) {
		}
	}
	
	private void runGame() throws InterruptedException {
		fireGameUpdate();
		while (!board.isGameOver()) {
			int move = players[currentPlayer].getMove(board.clone());
			try {
				board.placeDisc(Disc.values()[currentPlayer], move);
			} catch (IllegalArgumentException e) {
				continue;
			}
			currentPlayer = 1 - currentPlayer;
			fireGameUpdate();
		}
	}

	public Player getCurrentPlayer() {
		return players[currentPlayer];
	}

	public Disc getDiscForPlayer(Player player) {
		if (player == players[0])
			return Disc.values()[0];
		if (player == players[1])
			return Disc.values()[1];
		return null;
	}

	public Player getWinner() {
		if (!board.isGameOver())
			throw new IllegalStateException();
		
		if (board.isWinner(Disc.values()[0]))
			return players[0];
		if (board.isWinner(Disc.values()[1]))
			return players[1];
		return null;
	}

	public ConnectFourBoard getBoard() {
		return board;
	}
	
	public void addGameUpdateListener(GameUpdateListener listener) {
		listenerList.add(GameUpdateListener.class, listener);
	}
	
	public void removeGameUpdateListener(GameUpdateListener listener) {
		listenerList.remove(GameUpdateListener.class, listener);
	}

	private void fireGameUpdate() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2)
			if (listeners[i] == GameUpdateListener.class)
				((GameUpdateListener)listeners[i+1]).gameUpdated(event);
	}
}

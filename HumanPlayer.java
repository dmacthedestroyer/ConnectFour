public class HumanPlayer extends Player {
	public HumanPlayer(int maxDepth) {
	}
	
	public int getMove(ConnectFourBoard board) throws InterruptedException {
		ConnectFour.clickQueue.clear();
		return ConnectFour.clickQueue.take().getColumn();
	}

	public String getName() {
		return "Human";
	}
}

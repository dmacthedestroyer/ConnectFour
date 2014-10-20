public abstract class Player {
	protected Disc myDisc;
	
	public void setDisc(Disc disc) {
		myDisc = disc;
	}

	abstract public String getName();
	abstract public int getMove(ConnectFourBoard board) throws InterruptedException;
}

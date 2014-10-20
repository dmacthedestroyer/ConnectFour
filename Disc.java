public enum Disc {
	RED,
	YELLOW;
	
	public Disc getOpponent() {
		if (this == RED)
			return YELLOW;
		return RED;
	}
}

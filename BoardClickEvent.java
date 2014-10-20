import java.util.*;

@SuppressWarnings("serial")
public class BoardClickEvent extends EventObject {
	private int column;
	
	public BoardClickEvent(BoardPanel source, int column) {
		super(source);
		this.column = column;
	}

	public BoardClickEvent getSource() {
		return (BoardClickEvent)super.getSource();
	}
	
	public int getColumn() {
		return column;
	}
}

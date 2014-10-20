import java.util.*;

@SuppressWarnings("serial")
public class GameUpdateEvent extends EventObject {
	public GameUpdateEvent(ConnectFourGameLogic source) {
		super(source);
	}
	
	public ConnectFourGameLogic getSource() {
		return (ConnectFourGameLogic)super.getSource();
	}
}

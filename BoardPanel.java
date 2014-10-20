import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class BoardPanel extends JPanel implements ComponentListener,MouseListener {
	private ConnectFourBoard board;
	private double xOffset;
	private double yOffset;
	private double cellSize;
	
	public BoardPanel() {
		addComponentListener(this);
		addMouseListener(this);
	}

	public void registerBoard(ConnectFourBoard board) {
		this.board = board;
		componentResized(null);
	}

	public void paintComponent(Graphics g) {
		if (board == null) {
			super.paintComponent(g);
			return;
		}

		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Dimension dim = getSize();
		final double cellBorder = cellSize / 10;

		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 0, dim.width, dim.height);
		g2d.setColor(Color.BLUE);
		g2d.fillRect((int)xOffset, (int)yOffset,
		             (int)(board.getColumns() * cellSize), (int)(board.getRows() * cellSize));
		for (int row = 0; row < board.getRows(); row++)
			for (int col = 0; col < board.getColumns(); col++) {
				if (board.getDisc(row, col) == Disc.RED)
					g2d.setColor(Color.RED);
				else if (board.getDisc(row, col) == Disc.YELLOW)
					g2d.setColor(Color.YELLOW);
				else
					g2d.setColor(Color.LIGHT_GRAY);
				g2d.fillOval((int)(xOffset + col * cellSize + cellBorder),
				             (int)(yOffset + row * cellSize + cellBorder),
				             (int)(cellSize - 2 * cellBorder), (int)(cellSize - 2 * cellBorder));
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(2));
				g2d.drawOval((int)(xOffset + col * cellSize + cellBorder),
			             (int)(yOffset + row * cellSize + cellBorder),
			             (int)(cellSize - 2 * cellBorder), (int)(cellSize - 2 * cellBorder));
			}
	}

	// ComponentListener events

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
    }

	public void componentResized(ComponentEvent e) {
		if (board == null)
			return;

		Dimension dim = getSize();
		if (dim.width * board.getRows() > dim.height * board.getColumns())
			cellSize = dim.height / board.getRows();
		else
			cellSize = dim.width / board.getColumns();
		xOffset = (dim.width - board.getColumns() * cellSize) / 2;
		yOffset = (dim.height - board.getRows() * cellSize) / 2;
		repaint();
	}

	public void componentShown(ComponentEvent e) {
	}
	
	// MouseListener events
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		if (board == null)
			return;
		if (e.getButton() != MouseEvent.BUTTON1)
			return;

		Point point = e.getPoint();
		int column = (int)((point.x - xOffset) / cellSize);
		if (column >= 0 && column < board.getColumns())
			try {
				ConnectFour.clickQueue.put(new BoardClickEvent(this, column));
			} catch (InterruptedException ex) {
			}
	}
}

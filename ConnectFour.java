import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

public class ConnectFour implements ActionListener,GameUpdateListener {
	private static JFrame appFrame;
	private static BoardPanel boardPanel;
	private static JLabel infoBar;
	private static ConnectFourGameLogic game;
	private static Thread logicThread;
	public static final BlockingQueue<BoardClickEvent> clickQueue = new LinkedBlockingQueue<BoardClickEvent>();
	private ArrayList<Class<?>> playerClasses;
	private JPanel playerDialog;
	private JComboBox<String> difficultyBoxL;
	private JComboBox<String> playerBoxL;
	private JComboBox<String> playerBoxR;
	private JComboBox<String> difficultyBoxR;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new ConnectFour()).createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		appFrame = new JFrame("TCSS 435 Connect Four");
		appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		appFrame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				if (logicThread != null)
					logicThread.interrupt();
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Game");
		menuBar.add(menu);
		JMenuItem menuItem = new JMenuItem("New Game...");
		menuItem.setActionCommand("newgame");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		appFrame.setJMenuBar(menuBar);
		
		boardPanel = new BoardPanel();
		infoBar = new JLabel("Waiting for a new game to start...", SwingConstants.LEFT);

		appFrame.getContentPane().setLayout(new BoxLayout(appFrame.getContentPane(), BoxLayout.Y_AXIS));
		appFrame.add(boardPanel);
		appFrame.add(infoBar);
		
		appFrame.pack();
		if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
			appFrame.setExtendedState(appFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		appFrame.setVisible(true);
		
		playerClasses = new ArrayList<>();
		playerClasses.add(HumanPlayer.class);
		for (String dir : new String[]{".", "bin"}) {
			File[] files = new File(dir).listFiles(new FilenameFilter() {
		        public boolean accept(File dir, String name) {
		                return name.endsWith(".class");
		        }});
			if (files == null)
				continue;
			for (File file : files) {
				Class<?> aClass;
				try {
					aClass = ClassLoader.getSystemClassLoader().loadClass(file.getName().substring(0, file.getName().length() - 6));
				} catch (ClassNotFoundException e) {
					continue;
				}
				if (Player.class.isAssignableFrom(aClass) && aClass != Player.class && aClass != HumanPlayer.class)
					playerClasses.add(aClass);
			}
		}
			
		String[] playerNames = new String[playerClasses.size()];
		for (int i = 0; i < playerClasses.size(); i++)
			try {
				playerNames[i] = ((Player)playerClasses.get(i).getConstructor(int.class).newInstance(new Integer(1))).getName();
			} catch (Exception e) {
				System.exit(-1);
			}
		playerDialog = new JPanel();
		playerDialog.setAlignmentX(Component.CENTER_ALIGNMENT);
		playerDialog.setLayout(new BoxLayout(playerDialog, BoxLayout.X_AXIS));
		String difficultyOptions[] = {"4 (Easy)", "7 (Medium)", "10 (Hard)"};
		difficultyBoxL = new JComboBox<>(difficultyOptions);
		playerDialog.add(difficultyBoxL);
		playerBoxL = new JComboBox<>(playerNames);
		playerDialog.add(playerBoxL);
		playerDialog.add(new JLabel("  vs.  "));
		playerBoxR = new JComboBox<>(playerNames);
		playerDialog.add(playerBoxR);
		difficultyBoxR = new JComboBox<>(difficultyOptions);
		playerDialog.add(difficultyBoxR);
	}

	@SuppressWarnings("resource")
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("newgame")) {
			int answer = JOptionPane.showConfirmDialog(appFrame, playerDialog, "Choose Players", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
			if (answer == JOptionPane.CANCEL_OPTION)
				return;
			if (logicThread != null)
				logicThread.interrupt();
			try {
				int difficultyL = (new Scanner((String)difficultyBoxL.getSelectedItem())).nextInt();
				int difficultyR = (new Scanner((String)difficultyBoxR.getSelectedItem())).nextInt();
				game = new ConnectFourGameLogic(6, 7,
				                                (Player)playerClasses.get(playerBoxL.getSelectedIndex()).getConstructor(int.class).newInstance(new Integer(difficultyL)),
				                                (Player)playerClasses.get(playerBoxR.getSelectedIndex()).getConstructor(int.class).newInstance(new Integer(difficultyR)));
			} catch (Exception e) {
				System.exit(-1);
			}
			game.addGameUpdateListener(this);
			boardPanel.registerBoard(game.getBoard());
			logicThread = new Thread(game);
			logicThread.start();
		}
	}

	public void gameUpdated(GameUpdateEvent event) {
		boardPanel.repaint();
		if (game.getBoard().isGameOver()) {
			Player winner = game.getWinner();
			if (winner == null)
				infoBar.setText("Draw.");
			else
				infoBar.setText(winner.getName() + " [" + game.getDiscForPlayer(winner) + "] wins.");
		} else
			infoBar.setText("Your turn " + game.getCurrentPlayer().getName() + " [" + game.getDiscForPlayer(game.getCurrentPlayer()) + "].");
	}
}
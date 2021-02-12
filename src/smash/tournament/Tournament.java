package smash.tournament;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

public class Tournament extends JFrame {
	private static final long serialVersionUID = 1L;

	public JTextArea newPlayers;
	public JButton newPlayer, startButton;
//	JButton selected;
	public JList<String> playerList;
	public DefaultListModel<String> playerNames;

	public Tournament(String title) {
		// TODO Auto-generated constructor stub
		super(title);

//		JPanel container = new JPanel();
//		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		// top part of JFrame
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));

		// addPlayer, left side of top
		JPanel addPlayer = new JPanel();
		addPlayer.setLayout(new BoxLayout(addPlayer, BoxLayout.Y_AXIS));

		newPlayer = new JButton("Add player");
		newPlayers = new JTextArea(20, 30);
		newPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
		newPlayers.setAlignmentX(Component.CENTER_ALIGNMENT);

		newPlayers.setMargin(new Insets(5, 5, 5, 5));

		addPlayer.add(new JScrollPane(newPlayers));
		addPlayer.add(newPlayer);

		content.add(addPlayer);

		// selectPlayer, right side of top
		JPanel selectPlayer = new JPanel();
		selectPlayer.setLayout(new BoxLayout(selectPlayer, BoxLayout.Y_AXIS));

		playerNames = new DefaultListModel<String>();
		playerList = new JList<String>(playerNames);

		playerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		selectPlayer.add(new JScrollPane(playerList));

		startButton = new JButton("Start");
		startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		selectPlayer.add(startButton);
//		selected = new JButton("Select Players");
//		selectPlayer.add(selected);

		content.add(selectPlayer);

//		container.add(content);

		// bottom of JFrame
//		JPanel start = new JPanel();
//		startButton = new JButton("Start");
//		start.add(startButton);
//		container.add(start);

		add(content);

		// set constants
		setVisible(true);
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
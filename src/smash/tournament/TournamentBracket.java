package smash.tournament;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;

public class TournamentBracket extends JFrame {
	private static final long serialVersionUID = 2L;

	private int participants, byPassSlots, losersByPassSlots;
	private int numOfBrackets, numOfLosersBrackets, numOfRounds, numOfLosersRounds, divider;
	private mxGraph graph;
	private mxGraphComponent graphComponent;

	private JTextField player1Score, player2Score;
	private JLabel bracketID, player1, player2, warning;
	public JButton updateTable;

	private int selected;

	private Integer[][] winners;
	private Integer[][] losers;
	private Bracket[] brackets;

	private HashMap<Integer, LinkedList<Integer>> map;

	public TournamentBracket(LinkedList<String> players) {
		super("Tournament");
		init(players.size());

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());

		// canvas, tournament bracket display
		graph = new mxGraph() {
			public boolean isCellFoldable(Object cell, boolean collapse) {
				return false;
			}
		};

		// JFrame, graph Panel
		graphComponent = new mxGraphComponent(graph);
		graphComponent.getGraphControl().addMouseListener(new CellClicker());
		graphComponent.getVerticalScrollBar().setUnitIncrement(16);
		graphComponent.getHorizontalScrollBar().setUnitIncrement(16);

		addBrackets(players);

		container.add(graphComponent, BorderLayout.CENTER);

		// score updater
		JPanel scorePanel = new JPanel();
		scorePanel.setLayout(null);
		scorePanel.setMinimumSize(new Dimension(390, 0));
		scorePanel.setMaximumSize(new Dimension(390, 0));
		scorePanel.setPreferredSize(new Dimension(390, 0));

		// score Input
		player1 = new JLabel("Player 1");
		player2 = new JLabel("Player 2");
		bracketID = new JLabel("#.", JLabel.RIGHT);
		player1Score = new JTextField();
		player2Score = new JTextField();

		player1.setBounds(25, 20, 150, 20);
		player2.setBounds(225, 20, 150, 20);
		bracketID.setBounds(0, 50, 20, 20);
		player1Score.setBounds(20, 50, 150, 20);
		player2Score.setBounds(220, 50, 150, 20);

//		JLabel versus = new JLabel("vs.", JLabel.CENTER);
		JLabel to = new JLabel(":", JLabel.CENTER);
//		versus.setBounds(170, 20, 50, 20);
		to.setBounds(170, 50, 50, 20);

		scorePanel.add(player1);
//		scorePanel.add(versus);
		scorePanel.add(player2);
		scorePanel.add(bracketID);
		scorePanel.add(player1Score);
		scorePanel.add(to);
		scorePanel.add(player2Score);

		// update button for scores and error label
		warning = new JLabel();
		warning.setBounds(25, 85, 245, 35);
		warning.setVerticalAlignment(SwingConstants.TOP);

		updateTable = new JButton("Update");
		updateTable.setBounds(270, 80, 100, 30);
		updateTable.addActionListener(new Updater());

		scorePanel.add(warning);
		scorePanel.add(updateTable);

		container.add(scorePanel, BorderLayout.EAST);

		add(container);

		//
		setVisible(true);
		setSize(1200, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void generateGraph() {
		// initialize size
		for (int i = 0; i < numOfRounds; i++) {
			winners[i] = new Integer[(int) (Math.pow(2, numOfRounds - i) / 2)];
			for (int j = 0; j < winners[i].length; j++) {
				winners[i][j] = 0;
			}
		}

		// byPassSlots null
		int count = 0;
		int interval = (int) (winners[0].length / 2);
		while (count < byPassSlots) {
			for (int i = 0; i < winners[0].length && count < byPassSlots; i += interval) {
				if (winners[0][i] != null) {
					winners[0][i] = null;
					count += 1;
				}
			}
			interval = (int) (interval / 2);
		}

		// loser bracket generation
		int a = numOfLosersRounds - (numOfRounds - 2) * 2;
		for (int i = 0; i < numOfLosersRounds; i++) {
			if (i == 0) {
				losers[i] = new Integer[(int) (Math.pow(2, numOfRounds - 1) / 2)];
			} else if ((int) i % 2 == a) {
				losers[i] = new Integer[losers[i - 1].length];
			} else {
				losers[i] = new Integer[(int) losers[i - 1].length / 2];
			}

			// initialize lists
			for (int j = 0; j < losers[i].length; j++) {
				losers[i][j] = 0;
			}
		}

		losersByPassSlots = 2 * losers[0].length - (int) (Math.pow(2, numOfRounds - 1) - byPassSlots);
		if (losersByPassSlots >= losers[0].length) {
			losersByPassSlots = losers[0].length - (int) (Math.pow(2, numOfRounds - 1) - byPassSlots);
		}

		// byPassSlots null
		count = 0;
		interval = (int) (losers[0].length / 2);
		while (count < losersByPassSlots) {
			for (int i = 0; i < losers[0].length && count < losersByPassSlots; i += interval) {
				if (losers[0][i] != null) {
					losers[0][i] = null;
					count += 1;
				}
			}
			interval = (int) (interval / 2);
		}

		// number of losers brackets
		for (int i = 0; i < losers.length; i++) {
			numOfLosersBrackets += losers[i].length;
		}
		numOfLosersBrackets -= losersByPassSlots;

		// initialize brackets list
		brackets = new Bracket[numOfBrackets + numOfLosersBrackets + 1];
	}

	private void createBrackets(LinkedList<String> players) {
		int firstRound = (int) Math.pow(2, numOfRounds - 1);

		// find if there is extra space in between, and also indicator for losers
		// bracket generation
		int a = 0;
		if (byPassSlots >= firstRound / 2) {
			a++;
		}

		// create winners bracket
		int n = 1;
		for (int i = 0; i < winners.length; i++) {
			// loop the size of the round
			for (int j = 0; j < winners[i].length; j++) {
				if (winners[i][j] != null) {

					// set tournament id to winners
					winners[i][j] = n;

					// get x, y
					int x = (250 * i) + 20;
					int y = (int) (80 * Math.pow(2, i - a) * j) + 20 + (int) (40 * Math.pow(2, i - a));

					// initialize players for the bracket
					String player1 = null, player2 = null;

					// add nodes starting at second round
					if (i != 0) {

						// add nodes from previous round
						if (winners[i - 1][j * 2] != null) {
							// 2 parents
							map.put(winners[i - 1][j * 2], new LinkedList<Integer>());
							map.get(winners[i - 1][j * 2]).add(n);
							map.put(winners[i - 1][j * 2 + 1], new LinkedList<Integer>());
							map.get(winners[i - 1][j * 2 + 1]).add(n);

						} else if (winners[i - 1][j * 2 + 1] != null) {
							// 1 parent
							map.put(winners[i - 1][j * 2 + 1], new LinkedList<Integer>());
							map.get(winners[i - 1][j * 2 + 1]).add(n);

							player1 = players.poll();
						} else {
							player1 = players.poll();
							player2 = players.poll();
						}
					} else {
						// get random name from list
						player1 = players.poll();
						player2 = players.poll();
					}
					brackets[n] = new Bracket(n, x, y, player1, player2);
					n += 1;
				}
			}
		}
		// endOfWinners is the start of where losers bracket will be drawn
		int endOfWinners = (int) (80 * Math.pow(2, -a) * (winners[0].length + 1)) + 20 + (int) (40 * Math.pow(2, -a))
				+ 40;
		// create losers bracket
		a = 0;
		for (int i = 0; i < losers.length; i++) {

			// a fixes spacing of rounds with equal brackets as the round before
			if (i != 0 && losers[i].length == losers[i - 1].length) {
				a++;
			}

			for (int j = 0; j < losers[i].length; j++) {
				// check if null
				if (losers[i][j] != null) {

					// update winners
					losers[i][j] = n;

					int x = (250 * i) + 20;
					int y = (int) (80 * Math.pow(2, i - a) * j) + (endOfWinners - 40 * a)
							+ (int) (40 * Math.pow(2, i - a));
					// add nodes from previous round
					if (i != 0) {

						// checking to see whether losers[i] == is equal to losers[i - 1]
						if (losers[i].length < losers[i - 1].length) {

							if (losers[i - 1][j * 2] != null) {
								// 2 parents
								map.put(losers[i - 1][j * 2], new LinkedList<Integer>());
								map.get(losers[i - 1][j * 2]).add(n);
								map.put(losers[i - 1][j * 2 + 1], new LinkedList<Integer>());
								map.get(losers[i - 1][j * 2 + 1]).add(n);

							} else if (losers[i - 1][j * 2 + 1] != null) {
								// 1 parent
								map.put(losers[i - 1][j * 2 + 1], new LinkedList<Integer>());
								map.get(losers[i - 1][j * 2 + 1]).add(n);

								// possible hashmap link with winners bracket (only if i == 1)
							} else {
								// possible hashmap link with winners bracket (only if i == 1)
							}
						} else {
							if (losers[i - 1][j] != null) {
								map.put(losers[i - 1][j], new LinkedList<Integer>());
								map.get(losers[i - 1][j]).add(n);
							} else {
								// possible hashmap link with winners bracket
							}
						}
					}
					brackets[n] = new Bracket(n, x, y, null, null);
					n += 1;
				}
			}
		}

		// display map
		System.out.println(brackets.length - 1);
		for (int i = 1; i < brackets.length - 1; i++) {
			if (map.containsKey(i)) {
				System.out.println(i + ": " + map.get(i).toString());
			}
		}
	}

	private void addBrackets(LinkedList<String> players) {
		// generate tournament graph
		generateGraph();

		// create Brackets
		createBrackets(players);

		// styles before drawing
		Map<String, Object> style = graph.getStylesheet().getDefaultEdgeStyle();
		style.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
		style.put(mxConstants.STYLE_STROKEWIDTH, 2);
		style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
		style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);

		// get all brackets and draw on graph
		for (int i = 1; i < brackets.length; i++) {
			brackets[i].addToGraph(graph);
		}

		// draw lines
		for (int i = 1; i < brackets.length - 1; i++) {
			if (map.containsKey(i)) {
				brackets[i].addEdge(graph, brackets[map.get(i).get(0)]);
			}
		}
	}

	private void init(int n) {
		int i = 0;
		int j = 0;
		while (Math.pow(2, i) < n) {
			i += 1;
			j += Math.pow(2, i);
		}
		participants = n;
		byPassSlots = (int) (Math.pow(2, i) - n);

		numOfBrackets = (int) (j / 2) - byPassSlots;
		numOfRounds = i;

		// losers bracket variables
		numOfLosersRounds = (numOfRounds - 2) * 2;
		divider = numOfBrackets + 1;

		if (byPassSlots < Math.pow(2, numOfRounds - 1) / 2) {
			numOfLosersRounds++;
		}

		map = new HashMap<Integer, LinkedList<Integer>>();
		winners = new Integer[numOfRounds][];
		losers = new Integer[numOfLosersRounds][];
	}

	public int getNumPlayers() {
		return participants;
	}

	public static void main(String[] args) {
		LinkedList<String> temp = new LinkedList<String>();
		for (int i = 1; i <= 10; i++) {
			temp.add(Integer.toString(i));
		}

		TournamentBracket window = new TournamentBracket(temp);

	}

	private class Updater implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Update")) {

				if (selected == 0) {
					// make sure bracket is selected
					System.out.println("no bracket selected");
					warning.setText("Please select a Bracket to update");

				} else if (!bracketFull()) {
					// make sure that current bracket has 2 players
					System.out.println("Bracket does not have enough players");
					warning.setText("<html>This bracket does not contain enough players</html>");

				} else if (!fullyEntered()) {
					// make sure both boxes entered integers
					System.out.println("Not entered");
					warning.setText("<html>Make sure both text boxes contain integers</html>");

				} else if (!isInteger()) {
					// make sure input numbers are integers
					System.out.println("not integer");
					warning.setText("Make sure to enter only integers");

				} else {
					int nextBracket = map.get(selected).get(0);
					String[] winner = brackets[selected].getPlayers();

					if (brackets[nextBracket].contains(winner[0]) || brackets[nextBracket].contains(winner[1])) {
						// make sure that the same person isn't entered twice into next bracket
						System.out.println("Already finished");
						warning.setText("Bracket already finished");
					} else {
						System.out.println("works");

						if (Integer.parseInt(player1Score.getText()) > Integer.parseInt(player2Score.getText())) {

							brackets[nextBracket].addPlayer(winner[0]);

						} else {
							brackets[nextBracket].addPlayer(winner[1]);
						}

						returnNormal();
					}
				}
				graphComponent.refresh();
			}
		}

		private boolean bracketFull() {
			if (brackets[selected].getPlayers()[1] == null) {
				return false;
			} else {
				return true;
			}
		}

		private boolean fullyEntered() {
			if (player1Score.getText().equals("") || player2Score.getText().equals("")) {
				return false;
			} else {
				return true;
			}
		}

		private boolean isInteger() {
			try {
				Integer.parseInt(player1Score.getText());
				Integer.parseInt(player2Score.getText());
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

	private void returnNormal() {
		// update score panel
		player1.setText("Player 1");
		player2.setText("Player 2");
		bracketID.setText("#.");
		warning.setText("");
		player1Score.setText("");
		player2Score.setText("");

		// update selected
		selected = 0;
	}

	private class CellClicker extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());

			if (cell != null) {
				// update selected
				selected = Integer.parseInt(cell.getParent().getId());

				// update score panel
				graph.setSelectionCell(cell.getParent());
				System.out.println("cell=" + cell.getParent().getId());

				player1.setText(brackets[selected].getPlayers()[0]);
				player2.setText(brackets[selected].getPlayers()[1]);
				bracketID.setText(cell.getParent().getId() + ".");
			} else {
				returnNormal();
			}
		}
	}
}

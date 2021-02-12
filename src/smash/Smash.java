package smash;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import smash.playerCollections.Player;
import smash.playerStatsConverter.PlayerStatReader;
import smash.tournament.Tournament;
import smash.tournament.TournamentBracket;

public class Smash {
	private static Tournament tourney;
	private static TournamentBracket display;

	private static HashMap<String, Player> players;
	private static LinkedList<String> temp, tourneyPlayers;

	private static void init() {
		// add action listeners to buttons
		tourney.newPlayer.addActionListener(new Listener());
//		tourney.selected.addActionListener(new Listener());
		tourney.startButton.addActionListener(new Listener());
	}

	private static void addPlayers() {
		temp = new LinkedList<String>(players.keySet());
		Collections.sort(temp);

		for (String e : temp) {
			tourney.playerNames.addElement(e);
		}
	}

	public static void main(String[] args) {
		tourney = new Tournament("Central Smash");
		init();

		players = new HashMap<String, Player>();
		PlayerStatReader reader = new PlayerStatReader("temp");

		// read in players
		while (reader.hasNext()) {
			Player nextPlayer = reader.getPlayer();
			players.put(nextPlayer.getName(), nextPlayer);
		}

		// add players to list
		addPlayers();
	}

	static private class Listener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// What to do when button is pressed
			if (e.getActionCommand().equals("Add player")) {
				// get player names from text area and add them to list
				for (String p : tourney.newPlayers.getText().split("\n")) {
					players.put(p, new Player(p));
				}

				// refresh text area and list
				tourney.playerNames.removeAllElements();
				tourney.newPlayers.selectAll();
				tourney.newPlayers.replaceSelection("");
				addPlayers();
			} else if (e.getActionCommand().equals("Start")) {
				tourneyPlayers = new LinkedList<String>(tourney.playerList.getSelectedValuesList());
				System.out.println(tourneyPlayers.toString());
				display = new TournamentBracket(tourneyPlayers);
			}

//			else if (e.getActionCommand().equals("Select players")) {}
		}
	}
}

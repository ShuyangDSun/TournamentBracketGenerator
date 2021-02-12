package smash.playerStatsConverter;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import smash.playerCollections.Player;

public class PlayerStatReader {
//	URL location;
	File location;
	Scanner reader;

	public PlayerStatReader(String url) {
		// TODO Auto-generated constructor stub
		try {
//			location = new URL(url);
			location = new File(url);
			reader = new Scanner(location);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("URL not found");
		}
	}

	public boolean hasNext() {
		return reader.hasNextLine();
	}

	public Player getPlayer() {
		// Name Points Wins Losses
		Player p = new Player(reader.next(), reader.nextInt(), reader.nextInt(), reader.nextInt());
		return p;
	}
}

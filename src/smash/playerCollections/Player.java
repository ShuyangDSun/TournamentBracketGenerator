package smash.playerCollections;

public class Player implements Comparable<Player> {
	private String name;
	int wins = 0, losses = 0;
	int points = 1;

	public Player(String n) {
		name = n;
	}

	public Player(String n, int p, int w, int l) {
		name = n;
		points = p;
		wins = w;
		losses = l;
	}

	public String toString() {
		return "Name: " + this.getName() + "\nPoints: " + points + "\nWins/Losses: " + wins + "/" + losses + ", "
				+ this.winLossRatio();
	}

	public String getName() {
		return name;
	}

	public void addWin() {
		wins += 1;
	}

	public void addLoss() {
		losses += 1;
	}

	public int getPoints() {
		return points;
	}

	public double winLossRatio() {
		return wins / losses;
	}

	public void levelPoints(int otherPlayer, int score) {
		if (points == 1 && score != -1) {
			points = otherPlayer + 1;
		} else if (Math.abs(points - otherPlayer) > points * 3 / 4) {
			// add/subtract half otherPlayer points
			points += (int) (otherPlayer / 2) * score;
		} else {
			// add/subtract half of difference of points
			points += (int) (Math.abs(otherPlayer - points) / 2 + 0.5) * score;
		}
	}

	@Override
	public int compareTo(Player player) {
		// TODO Auto-generated method stub
		if (this.winLossRatio() < player.winLossRatio())
			return -1;

		else if (this.winLossRatio() > player.winLossRatio())
			return 1;

		return 0;
	}

//	@Override
//	public int compareTo(Player player) {
//		// TODO Auto-generated method stub
//		if (points < player.getPoints())
//			return -1;
//
//		else if (points > player.getPoints())
//			return 1;
//
//		return 0;
//	}
}

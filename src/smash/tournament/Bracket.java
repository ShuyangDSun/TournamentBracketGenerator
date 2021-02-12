package smash.tournament;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

public class Bracket {
	String[] players;
	String id;
	int x, y;

	mxCell player1;
	mxCell player2;
	mxCell startConnector;
	mxCell endConnector;

	public Bracket(int tourneyNumber, int positionx, int positiony) {
		this(tourneyNumber, positionx, positiony, null);
	}

	public Bracket(int tourneyNumber, int positionx, int positiony, String player) {
		this(tourneyNumber, positionx, positiony, player, null);
	}

	public Bracket(int tourneyNumber, int positionx, int positiony, String player1, String player2) {
		id = Integer.toString(tourneyNumber);
		players = new String[] { player1, player2 };
		x = positionx;
		y = positiony;
	}

	public String toString() {
		return id + ") " + players[0] + " vs. " + players[1];
	}

	public String[] getPlayers() {
		return players;
	}

	public mxCell getStartConnector() {
		return startConnector;
	}

	public mxCell getEndConnector() {
		return endConnector;
	}

	public int getId() {
		return Integer.parseInt(id);
	}

	public void addPlayer(String player) {
		if (players[0] == null) {
			players[0] = player;
			player1.setValue(player);
		} else if (players[1] == null) {
			players[1] = player;
			player2.setValue(player);
		}
	}

	public boolean contains(String player) {
		try {
			if (players[0].equals(player) || players[1].equals(player)) {
				return true;
			}
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public void addToGraph(mxGraph graph) {

		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			// main container
			mxCell container = (mxCell) graph.insertVertex(parent, null, "", x, y, 100, 60, "");
			container.setId(id);
			graph.setCellsLocked(true);

			container.setConnectable(false);

			// add Bracket Label
			mxGeometry circle = new mxGeometry(0, 0.5, 20, 20);
			circle.setOffset(new mxPoint(-10, -10));
			circle.setRelative(true);
			mxCell label = new mxCell(id, circle, "shape=ellipse;perimter=ellipsePerimeter");
			label.setVertex(true);

			label.setConnectable(false);

			// add bracket players
//			ROUNDED;strokeColor=red;fillColor=green
			player1 = new mxCell(players[0], new mxGeometry(0, 0, 100, 30), "");
			player2 = new mxCell(players[1], new mxGeometry(0, 30, 100, 30), "");
			player1.setVertex(true);
			player2.setVertex(true);
			player1.setConnectable(false);
			player2.setConnectable(false);

			// set connect points
			mxGeometry geo1 = new mxGeometry(0, 0.5, 20, 20);
			geo1.setOffset(new mxPoint(-10, -10));
			geo1.setRelative(true);
			startConnector = new mxCell(null, geo1, "shape=ellipse;perimter=ellipsePerimeter");
			startConnector.setVertex(true);

			mxGeometry geo2 = new mxGeometry(1.0, 0.5, 20, 20);
			geo2.setOffset(new mxPoint(-10, -10));
			geo2.setRelative(true);
			endConnector = new mxCell(null, geo2, "shape=ellipse;perimter=ellipsePerimeter");
			endConnector.setVertex(true);

			// set connect points invisible

//			startConnector.setVisible(false);
			endConnector.setVisible(false);

			graph.addCell(startConnector, container);
			graph.addCell(endConnector, container);
			graph.addCell(player1, container);
			graph.addCell(player2, container);
			graph.addCell(label, container);
		} finally {
			graph.getModel().endUpdate();
		}
	}

	public void addEdge(mxGraph graph, Bracket child) {
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			graph.insertEdge(parent, null, null, endConnector, child.getStartConnector());
		} finally {
			graph.getModel().endUpdate();
		}

	}

}

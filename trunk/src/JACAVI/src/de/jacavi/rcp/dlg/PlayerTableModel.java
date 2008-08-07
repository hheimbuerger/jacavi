package de.jacavi.rcp.dlg;

import java.util.ArrayList;

import de.jacavi.appl.valueobjects.Player;

/**
 * This class contains the data model for the PlayerTable
 */

public class PlayerTableModel {

	private ArrayList<Player> player;
	private int modCount = -1;

	/**
	 * Constructs a PlayerTableModel Fills the model with data
	 */
	public PlayerTableModel() {
		player = new ArrayList<Player>();

		addPlayer(new Player());
		addPlayer(new Player());
	}

	public void addPlayer(Player newPlayer) {
		modCount++;
		newPlayer.setId(modCount);
		newPlayer.setName(newPlayer.getName()+ " "+modCount);
		player.add(newPlayer);
	}

	public void removePlayer(int id) {
		for (int i = 0; i < player.size(); i++) {
			if(player.get(i).getId()==id)
				player.remove(i);
		}
	}

	public ArrayList<Player> getPlayer() {
		return player;
	}
}
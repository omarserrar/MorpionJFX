package models.tictactoe;

import java.io.Serializable;

import models.tictactoe.TicTacToe.Player;

public class Move implements Serializable, Cloneable {
	private int position;
	private TicTacToe.Player player;
	public Move(int position, Player player) {
		this.player = player;
		this.position = position;
	}
	public int getPosition() {
		return position;
	}
	public TicTacToe.Player getPlayer() {
		return player;
	}
	public Move clone() throws CloneNotSupportedException {
		return (Move)super.clone();
	}
}

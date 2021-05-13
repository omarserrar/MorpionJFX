package models.tictactoe;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import javafx.scene.paint.Color;
import models.exceptions.CaseNonVideException;
import models.exceptions.TourException;
import models.ia.MultiLayerPerceptron;
import models.ia.SigmoidalTransferFunction;

public class TicTacToe implements Serializable, Cloneable{
	private ArrayList<int[]> partie; // Tableau de matrice contenant tous les etat de la grille durant la partie
	private int tourJoue = 0; // Nombre de tour jouer
	private ArrayList<Move> allMoves = new ArrayList<Move>(); // Tableau contenant tous les coups jouees dans la partie
	private Player dernierJoueur = Player.EMPTY; // Dernier Joueur qui a jouer
	private Player role = Player.EMPTY; // Role actuel
	private int[] winningCase; // Les case qui forment une ligne si la partie est gagnante
	public static enum Player { X(-1),EMPTY(0), O(1);
		private final int id;
		Player(int id) { this.id = id; }
	    public int getValue() { return id; }
	    // Return X si O et O si X et EMPLY si EMPTY
	    public Player getOppostirePlayer() { if(this.id==0) return Player.EMPTY;return (this.getValue()==-1)?Player.O:X; }
	};
	
	
	private boolean full = false; // true si la grille est remplie false sinon
	/**
	* Retourne l'etat actuel de la grille  
	*
	* @return   Matrice d'entier represantant la grille. 
	*/
	public int[] getLastRound() {
		return partie.get(partie.size()-1).clone();
	}
	/**
	* Retourne tous les coups jouee dans la partie
	*
	* @return      Tableau des Move joues dans la partie
	*/
	public ArrayList<Move> getAllMoves() {
		return allMoves;
	}
	public TicTacToe() {
		role = Player.X; // Par defaut X commence 
		partie = new ArrayList<int[]>();
		partie.add(new int[9]); // Initialiser l'ArrayList avec a un tableau de 0
	}
	/**
	* Initialise une partie en precisant le joueur qui commence.
	*
	* @param player  Joueur qui commence
	*/
	public TicTacToe(Player player) {
		if(player==Player.EMPTY) this.role = Player.X;
		else this.role = player;
		partie = new ArrayList<int[]>();
		partie.add(new int[9]); // Initialiser l'ArrayList avec a un tableau de 0
	}
	/**
	* Retourne le tableau qu'il faut passer au MLP en entrer
	* @param botPlayer Le Player du IA (X ou O)
	* @param round Le tour pour lequel récuperer l'input
	*/
	// 
	// Les case jouer par le robot doivent avoir la valeur de 1 et ceux de son adversaire  -1
	public int[] getBotInput(Player botPlayer, int round) {
		if(botPlayer.getValue()==1) return this.getPartie().get(round);
		int[] input = new int[9];
		int[] roundData = this.getPartie().get(round);
		for(int i=0;i<9;i++) {
			input[i] = roundData[i] *-1;
		}
		return input;
	}
	
	/**
	* Verifie si il y a un gagnant et le retourne ( ou return EMPTY si aucun gagnant)
	*
	* @return EMPTY si partie nulle ou en cours ou retourne le joueur qui a gagne (O ou X)
	*/
	public Player verifierGrille() {
		//if(tourJoue<5) return Player.EMPTY;  // Si moins de 5 tour ont ete jouer aucun joueur ne peut gagner
		int[] lastTour = partie.get(partie.size()-1);
		winningCase = new int[] {-1,-1,-1};
		// Verifier les lignes
		for(int i=0;i<3;i++) {
			int somme = 0;
			for(int j=0;j<3;j++) {
				somme += lastTour[i*3 + j];
				winningCase[j] = i*3+j;
			}
			if(somme==3) return Player.O;
			if(somme==-3) return Player.X;
		}
		// Verifier les colonnes
		for(int i=0;i<3;i++) {
			int somme = 0;
			for(int j=0;j<3;j++) {
				somme += lastTour[j*3 + i];
				winningCase[j] = j*3 + i;
			}
			if(somme==3) return Player.O;
			if(somme==-3) return Player.X;
		}
		{
			int somme = 0;
			somme = lastTour[0] + lastTour[4] + lastTour[8];
			winningCase = new int[]{0,4,8};
			if(somme==3) return Player.O;
			if(somme==-3) return Player.X;
		}
		{
			int somme = 0;
			somme = lastTour[2] + lastTour[4] + lastTour[6];
			winningCase = new int[]{2,4,6};
			if(somme==3) return Player.O;
			if(somme==-3) return Player.X;
		}
		// Aucun Gagnant
		winningCase = null;
		return Player.EMPTY;
	}
	public int[] getWinningCase() {
		return winningCase;
	}
	/**
	* Appeler à chaque fois pour executer un coup
	* Throw CaseNonVideException si la case n'est pas vide
	* @param position position du coup (0-8)
	* @return L'etat actuel de la grille
	*/
	public int[] update(int position) throws CaseNonVideException {
		int[] lastTour = partie.get(partie.size()-1).clone();// On clone l'etat de la grille qui sera retourner pour empecher les modification
		if(lastTour[position]!=0) throw new CaseNonVideException(); // On test si la case n'est pas vide 
		lastTour[position] = role.getValue();
		partie.add(lastTour); // On ajoute l'etat de la grille au tableau des etats de la grille
		tourJoue ++;
		dernierJoueur = role;
		role = role.getOppostirePlayer(); // On mets a jour le role actuel
		allMoves.add(new Move(position, role)); // On ajoute le coup dans le tableau des coups
		if(tourJoue==9) full = true;
		return lastTour;
	}
	public ArrayList<int[]> getPartie() {
		return partie;
	}
	public Player getRole() {
		return this.role;
	}
	public int getTourJoue() {
		return tourJoue;
	}
	public Player getDernierJoueur() {
		return dernierJoueur;
	}
	public boolean isFull() {
		return full;
	}
	/**
	* Inverse la partie les X devienent des O et vice versa
	*/
	public void invert() {
		for(int i=0;i<partie.size();i++) {
			for(int j=0;j<9;j++) {
				partie.get(0)[j]*=-1;
			}
		}
	}
	// Afficher la table à l'etat tour

	
	/**
	* Clone une partie
	* @return retourne le clone de la partie
	*/
	  public TicTacToe clone() throws
		      CloneNotSupportedException 
		{ 
		  TicTacToe cloned= (TicTacToe)super.clone();
		  cloned.partie = (ArrayList<int[]>) this.partie.clone();
		  cloned.allMoves =  (ArrayList<Move>) this.allMoves.clone();
		return cloned; 
		} 
}

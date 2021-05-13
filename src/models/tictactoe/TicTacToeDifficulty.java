package models.tictactoe;

import java.io.File;
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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.text.AbstractDocument.BranchElement;

import javafx.scene.control.SelectionMode;
import models.exceptions.CaseNonVideException;
import models.exceptions.TourException;
import models.ia.MultiLayerPerceptron;
import models.ia.SigmoidalTransferFunction;
import models.tictactoe.TicTacToe.Player;


public class TicTacToeDifficulty implements Serializable {

	
	private MultiLayerPerceptron brain; // Le MLP de la difficulte
	protected String name;
	public TicTacToeDifficulty(String name, MultiLayerPerceptron brain) {
		this.brain = brain;
		this.name = name;
	}
	/**
	* Forward Propagation pour trouver quel case choisir
	* @param game La partie actuel
	* @return La case choisie
	*/
	public int getBotOutput(TicTacToe game) {
		double[] output = brain.forwardPropagation(Arrays.stream(game.getPartie().get(game.getPartie().size()-1)).asDoubleStream().toArray());
    	int[] posOrder = new int[9];
		double max = -1;int maxPos = 0;
		for(int i=0;i<output.length;i++) {
			if(max<output[i] && game.getLastRound()[i] == 0) {
				max = output[i];
				maxPos = i;
			}
		}
		return maxPos;
	}
	/**
	* Apprendre les coups du joueur qui a commencé la partie
	* @param game La partie
	*/
	public double learnFirstPlayerWin(TicTacToe game) {
		ArrayList<TicTacToe> games = new ArrayList<TicTacToe>();
		games.add(game);
		return learnDataSet(games,null); // Appelle la fonction en dessous en spécifiant seulement le premier parametre (firstPlayer)
	}
	/**
	* Apprendre les coups du deuxieme joueur
	* @param game La partie
	* @return Retourne le taux d'erreur
	*/
	public double learnSecondPlayerWin(TicTacToe game) {
		ArrayList<TicTacToe> games = new ArrayList<TicTacToe>();
		games.add(game);
		return learnDataSet(null,games); // Appelle la fonction en dessous en spécifiant seulement le deuxieme parametre (secondPlayer)
	}
	/**
	* Apprend à partir des jeu de donnée passer en parametre
	* @param firstPlayer La liste des partie pour lequelles apprendre les coup du premier joueur
	* @param secondPlayer La liste des partie pour lequelles apprendre les coup du deuxieme joueur
	* @return Retourne le taux d'erreur
	*/
	public double learnDataSet(ArrayList<TicTacToe> firstPlayer, ArrayList<TicTacToe> secondPlayer) {
		double error = 0.0 ;
		if(firstPlayer!=null)
		for(TicTacToe winningGame: firstPlayer) { 
			for(int i = 0; i < winningGame.getTourJoue(); i+=2){ // Apprendre les coups du premier joueur
				double[] inputs = Arrays.stream(winningGame.getPartie().get(i)).asDoubleStream().toArray();
				double[] output = new double[9];
				output[winningGame.getAllMoves().get(i).getPosition()] = 1;
				
				
				error += brain.backPropagate(inputs, output);
			}
		}
		if(secondPlayer!=null)
		for(TicTacToe winningGame: secondPlayer) {
			for(int i = 1; i < winningGame.getTourJoue(); i+=2){ // Apprendre les coups du deuxieme joueur
				double[] inputs = Arrays.stream(winningGame.getPartie().get(i)).asDoubleStream().toArray();
				double[] output = new double[9];
				output[winningGame.getAllMoves().get(i).getPosition()] = 1;
				
				
				error += brain.backPropagate(inputs, output);

			}
		}
		return error;
		
	}
	public MultiLayerPerceptron getBrain() {
		return brain;
	}
	public String getName() {
		return name;
	}
	/**
	* Enregistre la difficulte modifier par l'utilisateur
	* @return true si la difficulte a ete enregistrer false sinon
	*/
	public boolean serialize() {
        try { 
            FileOutputStream file = new FileOutputStream 
                                           ("User Data\\difficulties\\"+name+".lvl"); 
            ObjectOutputStream out = new ObjectOutputStream 
                                           (file); 
            out.writeObject(this); 
  
            out.close(); 
            file.close(); 
            return true;
        } 
  
        catch (IOException ex) { 
            System.out.println("IOException is caught"); 
            return false;
        } 
	}
}

package models.tictactoe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import controllers.LearningController;
import models.exceptions.CaseNonVideException;
import models.exceptions.TourException;
import models.ia.MultiLayerPerceptron;
import models.ia.SigmoidalTransferFunction;
import models.tictactoe.TicTacToe.Player;

/**
 * Les difficulte utilise dans le jeu sont gerer par cette classe utilitaire qui les charge / enregistre et les stock dans des attributs static lors de l'execution du jeu
 *
 */
public class DifficultiesManager {

	public static TicTacToeDifficulty easy = loadDifficulty("easy"); // La difficulte Facile
	public static TicTacToeDifficulty medium = loadDifficulty("medium"); // La difficulte Moyenne
	
	public static MiniMaxDifficulty pro = new MiniMaxDifficulty("Invincible", 6); // La difficulte Invincible
	/**
	 * Recharge les difficulte
	 */
	public static void reload() {
		easy = loadDifficulty("easy");
		medium = loadDifficulty("medium");
	}
	
	public static TicTacToeDifficulty deserializeDifficulty(File path) {
		try { 	
            FileInputStream file = new FileInputStream 
                                         (path); 
            ObjectInputStream in = new ObjectInputStream 
                                         (file); 
  
            TicTacToeDifficulty object = (TicTacToeDifficulty)in.readObject(); 
  
            in.close(); 
            file.close(); 
            
            return object;
        } 
  
        catch (IOException ex) { 
            System.out.println(ex.getMessage()+" Level does not exist! "+path.getAbsolutePath());
            return null;
        } 
        catch (ClassNotFoundException ex) { 
            System.out.println("ClassNotFoundException" + 
                                " is caught"); 
            return null;
        } 
	}
	/**
	 * Charge le fichier de la difficulte modifie par l'utilisateur 
	 * 
	 * @param name nom de la difficulte
	 * @return le fichier de la difficulte
	 */
	public static File loadUserDifficultyFile(String name) {
		return new File("User Data\\difficulties\\"+name+".lvl");
	}
	/**
	 * Charge une difficulte modifie par l'utilisateur 
	 * 
	 * @param name nom de la difficulte
	 * @return la difficulte modifie par l'utilisateur
	 */
	public static TicTacToeDifficulty loadUserDifficulty(String name) {
		
		return deserializeDifficulty(new File("User Data\\difficulties\\"+name+".lvl"));
	}
	/**
	 * Charge une difficulte par default 
	 * 
	 * @param name nom de la difficulte
	 * @return la difficulte 
	 */
	public static TicTacToeDifficulty getDefaultDifficulty(String name) {
		return deserializeDifficulty(new File("data\\difficulties\\"+name+".lvl"));
	}
	/**
	 * Charge une difficulte 
	 * 
	 * @param name nom de la difficulte
	 * @return Si une difficulte modifier par l'utilisateur existe elle sera retourner sinon la difficulte par defaut sera retourner
	 */
	public static TicTacToeDifficulty loadDifficulty(String name) {
		TicTacToeDifficulty userDifficulty = loadUserDifficulty(name);
		if(userDifficulty!=null) return userDifficulty;
		else return getDefaultDifficulty(name); 
	}
	/**
	 * Simule plusieurs partie contre un IA invincible pour l'apprentissage de la difficulte
	 * @param difficulty difficulte a apprendre
	 * @param nbPartie nombre de partie a jouer
	 */
	public static void difficultyEditor(TicTacToeDifficulty difficulty, int nbPartie) {
		Player player = Player.O;
		Player botPlayer = player.getOppostirePlayer();
		Scanner sc = new Scanner(System.in);TicTacToeDifficulty miniMaxTeacher = new MiniMaxDifficulty("teacher",5);
		int allGame = 0;
		int allDraw = 0;
		TicTacToe game = new TicTacToe(botPlayer);
		Player startPlayer = botPlayer;
		while(allGame<nbPartie) {
			int pos= 0;
			if(game.getRole()==player) {
				pos = difficulty.getBotOutput(game);
			}
			else {
				pos = ((MiniMaxDifficulty)miniMaxTeacher).getBotOutput(game);
			}
			try {
				game.update(pos);
				Player winner = game.verifierGrille();
				if(game.isFull() || winner != Player.EMPTY) {
					allGame++;
					int gameRound = game.getTourJoue();
					if(botPlayer == startPlayer && winner != player) {
						if(winner == Player.EMPTY) {
							difficulty.learnSecondPlayerWin(game);
						}
						difficulty.learnFirstPlayerWin(game);
						
					}
					else if(winner != player){
						if(winner == Player.EMPTY) {
							difficulty.learnFirstPlayerWin(game);
						}
						difficulty.learnSecondPlayerWin(game);
					}
					else {
						if(player==startPlayer) {
							difficulty.learnFirstPlayerWin(game);
						}
						else {
							difficulty.learnSecondPlayerWin(game);
						}
					}
					startPlayer = startPlayer.getOppostirePlayer();
					game = new TicTacToe(startPlayer);
					if(winner != botPlayer) {
						allDraw++;
					}
					if(allGame%50==0) {
						LearningController.getInstance().learningState(((double)allDraw/(double)allGame)*100, allGame, nbPartie);
					}
				}
			}
			catch(CaseNonVideException e) {
				
			}
	//		LearningController.getInstance().learningState(((double)allDraw/(double)allGame)*100, allGame, nbPartie);
	}	
		LearningController.getInstance().onLearningEnd();
	}
}

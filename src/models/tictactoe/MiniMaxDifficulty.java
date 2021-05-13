package models.tictactoe;

import java.util.ArrayList;
import java.util.Random;

import models.exceptions.CaseNonVideException;
import models.exceptions.TourException;
import models.ia.MultiLayerPerceptron;
import models.tictactoe.TicTacToe.Player;
/**
* Classe de difficulte mais qui utilise l'algorithme MiniMax et non l'MLP
*/
public class MiniMaxDifficulty extends TicTacToeDifficulty {
	private int depth = 8;
	public MiniMaxDifficulty(String name, int depth) {
		super(name, null);
		this.depth = depth;
	}
	// Redefinition de la methode getDecision pour la classe MiniMaxDifficulty
	@Override
	public int getBotOutput(TicTacToe game) {
		try {
			return getDecision(game);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CaseNonVideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Trouve la meilleur case a choisir en utilisant l'algorithme du MiniMax
	 * @param game La partie actuelle
	 * @return la case choisie
	 */
	private int getDecision(TicTacToe game) throws CloneNotSupportedException, CaseNonVideException {
		Player player = game.getRole();
		int bestScore = Integer.MIN_VALUE;
		int bestMove = -1;
		ArrayList<Integer> possibleMoves= new ArrayList<Integer>();
		for(int i=0;i<9;i++) { // Pour toutes les case vides actuel on execute:
			TicTacToe clone = game.clone(); // On creer une copie de la partie actuel pour la modifier
			if(clone.getLastRound()[i] !=0) continue; // Si la case n'est pas vide on continue
			clone.update(i); // On fait un coup virtuelle (Dans la partie copie) dans la case actuelle
			int score = miniMax(clone, depth, player); // On appelle la fonction du miniMax avec la profondeur de la difficulte
			//System.out.println(score);
			if(score>bestScore) { // Si le score est meilleur que celui d'avant on enregistre le coup
				bestScore = score;
				bestMove = i;
				possibleMoves = new ArrayList<Integer>();
				possibleMoves.add(bestMove);
			}
			else if(score==bestScore) { // Si le score est le meme que le meilleur score on enregistre les deux
				possibleMoves.add(i);
			}
		}
		int decision = new Random().nextInt(possibleMoves.size()); // Si on trouve plusieur coup optimale en prend un aleatoirement ( Pour eviter que l'IA refait les meme coups a chaque fois )
		return possibleMoves.get(decision);
	}
	/**
	 * Trouve la meilleur case a choisir en utilisant l'algorithme du MiniMax pour la partie passe en parametre
	 */
	private int miniMax(TicTacToe game, int depth, Player player) {
		if(depth==0 || game==null) return 0;
		Player winner = game.verifierGrille();
		if(game.isFull() || winner != Player.EMPTY) {
			
			if(winner == player) {
				
				return 1;
			}
			else if(winner== player.getOppostirePlayer()) {
				return -1;
			}
			return 0;
		}
		int bestScore = 0;
		if(game.getRole()==player) {
			bestScore = Integer.MIN_VALUE;
		}
		else {
			bestScore = Integer.MAX_VALUE;
		}
		
		for(int i=0;i<9;i++) {
			if(game.getLastRound()[i] == 0) {
				try {
					TicTacToe clone = game.clone();
					try {
						clone.update(i);
					} catch (CaseNonVideException e) {
						e.printStackTrace();
					}
					
					int score = miniMax(clone, depth-1, player);
					if(game.getRole()==player) {
						bestScore = Math.max(bestScore, score);
					}
					else {
						bestScore = Math.min(bestScore, score);
					}
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		return bestScore;
	}

}

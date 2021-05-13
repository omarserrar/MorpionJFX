package controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import models.ia.MultiLayerPerceptron;
import models.ia.SigmoidalTransferFunction;
import models.tictactoe.DifficultiesManager;
import models.tictactoe.TicTacToeDifficulty;

public class LearningController extends Controller implements Helpable{
	public static LearningController instance;
	public boolean background = false;
	public boolean learning = false;
	public Thread learningThread;
	public TicTacToeDifficulty difficulty;
	  @FXML
	    private ResourceBundle resources;

	    @FXML
	    private URL location;

	    @FXML
	    private Text hiddenLayer;

	    @FXML
	    private Text intelligence;

	    @FXML
	    private Text learningRateText;

	    @FXML
	    private Text nameDifficulte;

	    @FXML
	    private Text nbHiddenLayer;

	    @FXML
	    private Text partieJouees;

	    @FXML
	    private Text pourcentage;
	    @FXML
	    private Text successText;

	    @FXML
	    private ProgressBar progressBar;

	    @FXML 
	    private Button apprendreEnArrierePlan;
	    
	    @FXML
	    private Button saveButton;
	    @FXML
	    private Button stopLearning;
	    @FXML
	    private BackroundLearnController backroundLearnController;
	    
	    @FXML
	    private ImageView facileImg;
	    
	    @FXML
	    private ImageView moyenneImg;
	    @FXML
    void initialize() {
    	System.out.println("Init");
    	instance = this;
        assert hiddenLayer != null : "fx:id=\"hiddenLayer\" was not injected: check your FXML file 'LearnProgress.fxml'.";
        assert intelligence != null : "fx:id=\"intelligence\" was not injected: check your FXML file 'LearnProgress.fxml'.";
        assert learningRateText != null : "fx:id=\"learningRate\" was not injected: check your FXML file 'LearnProgress.fxml'.";
        assert nbHiddenLayer != null : "fx:id=\"nbHiddenLayer\" was not injected: check your FXML file 'LearnProgress.fxml'.";
        assert partieJouees != null : "fx:id=\"partieJouees\" was not injected: check your FXML file 'LearnProgress.fxml'.";
        assert pourcentage != null : "fx:id=\"pourcentage\" was not injected: check your FXML file 'LearnProgress.fxml'.";
    }
	    /**
	     * Récupere l'instance de la classe (Singleton)
	     * @return l'instance de la classe
	     */
    public static LearningController getInstance() {
    	return instance;
    }
    /**
     * Commence l'apprentissage
     * @param name Nom de la difficulté à modifier
     * @param learningRate
     * @param layers
     * @param nbPartie
     */
	public void startLearning(String name, double learningRate, int[] layers, int nbPartie) {
		learningRateText.setText(learningRate+"");
		//nameDifficulte.setText((name.equals("easy")?"Facile":"Moyen"));
		if(name.equals("easy")) {
			moyenneImg.setVisible(false);
			facileImg.setVisible(true);
		}
		else {
			moyenneImg.setVisible(true);
			facileImg.setVisible(false);
		}
		nbHiddenLayer.setText(layers.length+"");
		hiddenLayer.setText(Arrays.toString(layers));
		
		pourcentage.setText("0 %");
		partieJouees.setText(0+" / "+nbPartie);
		int[] allLayers = new int[2+layers.length]; // On initialise le tableau des layers 
		allLayers[0] = 9; allLayers[allLayers.length-1] = 9; // On ajoute les layers Input et Output (9 neuronne chacun)
		for(int i=1;i<layers.length+1;i++) {
			allLayers[i] = layers[i-1]; // On ajoute les hidden layers définie par l'utilisateur
		}
		difficulty = new TicTacToeDifficulty(name, new MultiLayerPerceptron(allLayers, learningRate, new SigmoidalTransferFunction()));
		learningThread = new Thread(()->{ // On commence l'apprentissage sur un nouveau thread 
			DifficultiesManager.difficultyEditor(difficulty, nbPartie);
		});
		learningThread.start();
	}
	/**
	 * Appelé chaque fois pour afficher l'état de l'apprentissage
	 */
	public void learningState(double intelligence, int partieJoue, int nbPartie) {
		if(background) { // Si l'apprentissage se fait en arrière plan 
			BackroundLearnController.getInstance().updateState(intelligence, partieJoue, nbPartie);
			return;
		}
		// Si l'apprentissage ne se fait pas en arrière plan
		double prc = 0;
		if(partieJoue>0) {
			prc = ((double)partieJoue/(double)nbPartie);
			pourcentage.setText(String.format("%.2f %%", prc*100)); 
		}
		else pourcentage.setText("0 %");
		progressBar.setProgress(prc);
		this.intelligence.setText(String.format("%.2f %%", intelligence));
		this.partieJouees.setText(String.format("%d / %d", partieJoue, nbPartie));
	}
	/**
	 * Methode appellé quand l'utilisateur arrete l'apprentissage
	 */
	@FXML
	public void stopLearning() {
		if(learningThread.isAlive()) learningThread.stop();
		instance = null;
		ApplicationController.getApplicationController().chargerAccueil();
		
	}
	/**
	 * Methode appellé quand l'utilisateur enregistre l'apprentissage
	 */
	@FXML
	public void stopAndSaveLearning() {
		if(learningThread.isAlive()) learningThread.stop();
		difficulty.serialize();
		instance = null;
		ApplicationController.getApplicationController().chargerAccueil();
	}
	/**
	 * Methode appellé quand l'utilisateur clique sur apprentissage en arrière plan
	 */
	@FXML
	public void backGroundLearn() {
		
		ApplicationController.getApplicationController().chargerBackGroundLearn();
		ApplicationController.getApplicationController().chargerAccueil();
		background = true;
		
	}
	/**
	 * Methode appellé quand l'apprentissage est terminé
	 */
	public void onLearningEnd() {
		if(background) { // Si l'apprentissage se fait en arrière plan 
			BackroundLearnController.getInstance().onLearnEnd();
			return;
		} // Sinon
		successText.setVisible(true);
		Platform.runLater(()->{
			stopLearning.setText("Quitter Sans Enregistrer");
			saveButton.setText("Enregistrer et Quitter");
		});
		apprendreEnArrierePlan.setVisible(false);
		background = false;
	}
	/**
	 * Methode appellé quand l'apprentissage en arriere plan est terminé
	 */
	public void onBackGroundLearnEnd() {
		instance = null;
		ApplicationController.getApplicationController().removeBottom();
	}
	 /**
     * Methode de l'interface Helpable permet de récuperer le message d'aide pour l'afficher dans la vue Aide
     */
	@Override
	public String getHelpMessage() {
		return new String("Déroulement de l'apprentissage:\r\n" + 
				"L'apprentissage se fait en jouant N parties (Nombre de parties définies dans le formulaire) contre un IA invincible\r\n" + 
				"qui utilise l'algorithme de miniMax.\r\n" + 
				"le IA que vous créer apprendra des bons coups de l'IA adverse ainsi que les bons coups qu'il fait.\r\n" + 
				"\r\n" + 
				"Le niveau d'intelligence est le taux de partie nulle de votre IA contre l'IA invincible.\r\n" + 
				"Plus il est élevé plus l'IA est intelligente.\r\n" + 
				"\r\n" + 
				"Vous pouvez enregistrer l'IA à tout moment en cliquant sur \"Enregistrer et arrêter\".\r\n" + 
				"Vous pouvez laisser l'apprentissage continuer en arrière-plan en cliquant sur \"Apprendre en arrière-plan\" l'état de l'apprentissage sera affiché en dessous de la fenêtre.\r\n" + 
				"Vous pouvez annuler l'apprentissage à tout moment en cliquant sur annuler.\r\n" + 
				"\r\n" + 
				"Quand l'apprentissage sera terminé vous pourriez soit enregistrer l'IA ou de quitter sans enregistrer.\r\n"
				);
	}
}

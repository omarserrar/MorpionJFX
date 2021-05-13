package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import controllers.GameViewController.GameType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Main;
import models.tictactoe.TicTacToe.Player;
import models.tictactoe.TicTacToeDifficulty;

/**
 * Le controlleur de la vue principal qui va contenir les autres vue dans la zone centre, ou la zone du bas
 *
 */
public class ApplicationController{

	private Controller currentPageController; // Le controlleur de la page actuelle (Celle affiche au centre)
	private static ApplicationController instance; // L'instance actuelle de la classe (Singleton)
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML 
    private BorderPane applicationPane;
    
    
    @FXML
    private Button home;
    @FXML
    private Button helpButton;
    
    // Les URL de tous les vues 
    public final URL MAIN_PAGE = getClass().getResource("../views/MainPageView.fxml");
    public final URL NEW_GAME_PAGE = getClass().getResource("../views/NewGameView.fxml");
    public final URL GAME_PAGE = getClass().getResource("../views/GameView.fxml");
    public final URL LEARN_PAGE = getClass().getResource("../views/LearningPageView.fxml");
    public final URL LEARN_PROGRESS_PAGE = getClass().getResource("../views/LearnProgress.fxml");
    public final URL BACKGROUND_LEARN = getClass().getResource("../views/BackgroundLearn.fxml");
    public final URL HELP = getClass().getResource("../views/helpView.fxml");
  
    @FXML
    void initialize() {
    	instance = this;
    	chargerPage(MAIN_PAGE); // On charge la vue Main lors du démarage du programme

    }
    /**
     * Affiche l'etat de l'apprentissage dans la fenetre
     */
    public void chargerBackGroundLearn() {
    	chargerPageBottom(BACKGROUND_LEARN); // Affiche la vue dans la zone du bas
    }
    /**
     * Supprime le contenue de la zone du bas
     */
    public void removeBottom() {
    	Main.stage.setHeight(Main.stage.getHeight()-((VBox)applicationPane.getBottom()).getPrefHeight());
    	applicationPane.setBottom(null);
    }
    /**
     * Affiche une vue dans la zone du bas de la fenetre
     * @param pageLoader URL de la vue a afficher
     */
    private void chargerPageBottom(URL pageLoader) {
    	try {
    		VBox bottom = new FXMLLoader(pageLoader).load();
    		applicationPane.setBottom(bottom);
    		System.out.println(Main.stage.getHeight());
    		Main.stage.setHeight(Main.stage.getHeight()+bottom.getPrefHeight());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Affiche une vue dans la zone du centre de la fenetre
     * @param pageLoader URL de la vue a afficher
     */
    private FXMLLoader chargerPage(URL pageLoader) {
    	home.setVisible(pageLoader != MAIN_PAGE); // Le boutton accueil s'affiche seulement si on est pas dans la page d'accueil
    	try {
    		FXMLLoader loader = new FXMLLoader(pageLoader);
			applicationPane.setCenter(loader.load());
			currentPageController = loader.getController(); // Recupere le controlleur de la vue
			// Si le controller est de type Helpable c'est que cette vue contient une aide donc on affiche le bouton aide dans la barre du haut
			if(currentPageController instanceof Helpable) helpButton.setVisible(true); 
			else helpButton.setVisible(false);
			return loader;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    /**
     * Charge la vue du jeu
     * @param gameType type de la partie (1 Joueur ou 2 Joueur
     * @param difficulty Difficulte de la partie
     * @param player Le joueur qui commence
     */
    public void chargerJeu(GameType gameType,TicTacToeDifficulty difficulty,Player player) {
    	FXMLLoader loader = chargerPage(GAME_PAGE);
    	GameViewController controller = loader.<GameViewController>getController();
    	controller.initGame(gameType, difficulty, player);
    }
    /**
     * Affiche la vue d'accueil dans la zone du centre
     */
    public void chargerAccueil() {
    	chargerPage(MAIN_PAGE);
    }
    /**
     * Affiche la vue de nouvelle partie dans la zone du centre
     */
    public void chargerNouvellePartie() {
    	chargerPage(NEW_GAME_PAGE);
    }
    /**
     * Affiche la page de modification de difficulte dans la zone du centre si aucune difficulte n'est entrain d'etre modifier en arriere plan
     */
    public void chargerDifficulte() {
    	if(LearningController.getInstance()!=null) { // On test si un apprentissage est en cours en arriere plan
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Impossible de modifier les difficultés");
    		alert.setHeaderText(null);
    		alert.setContentText("Vous pouvez modifiez une seule difficulté à la fois !\n Veuillez annuler l'apprentissage en cours !");

    		alert.showAndWait();
    	}
    	else chargerPage(LEARN_PAGE);
    }
    /**
     * Charge la vue qui affiche l'etat de l'apprentissage au centre et lance l'apprentissage
     * @param name nom de la difficulte
     * @param learningRate learningRate
     * @param layers layers
     * @param nbPartie nombre de partie
     */
    public void startLearning(String name, double learningRate, int[] layers, int nbPartie) {
    	chargerPage(LEARN_PROGRESS_PAGE);
    	LearningController.getInstance().startLearning(name, learningRate, layers, nbPartie);
    }
    /**
     * Recupere l'instance de la classe (Singleton)
     * @return l'instance actuelle de cette classe
     */
    public static ApplicationController getApplicationController() {
    	return instance;
    }
    /**
     * Charge la page d'accueil lors d'un clique sur le bouton accueil
     */
    @FXML
    void homeClick() {
    	chargerAccueil();
    }
    /**
     * Affiche l'aide si le controlleur de la vue actuelle est de type Helpable
     */
    @FXML
    void helpClick() {
    	if(!(currentPageController instanceof Helpable)) return; // Si le controlleur n'est pas de type Helpable on sort
    	Parent aideRoot;
    	try {
    		FXMLLoader loader = new FXMLLoader(HELP); // On charge la vue de l'Aide
			aideRoot = loader.load(); 
			((HelpController)loader.getController()).setHelpMessage((Helpable)currentPageController); // On recupere le controlleur de la vue d'aide et on appelle la methode setHelpMessage qui affiche le message d'aide du Helpable passe en parametre
			Stage stage = new Stage();
	        stage.setTitle("Aide");
	        stage.setScene(new Scene(aideRoot, 500, 500));
	        stage.show(); // On affiche la vue aide dans une nouvelle fenetre
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}

package controllers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import models.animations.AnimationsManager;
import models.exceptions.CaseNonVideException;
import models.exceptions.TourException;
import models.ia.MultiLayerPerceptron;
import models.tictactoe.DifficultiesManager;
import models.tictactoe.MiniMaxDifficulty;
import models.tictactoe.TicTacToe;
import models.tictactoe.TicTacToeDifficulty;
import models.tictactoe.TicTacToe.Player;


public class GameViewController extends Controller implements Helpable{
	public enum GameType { PLAYER_VS_PLAYER, PLAYER_VS_COMPUTER }; // Enum des deux type de partie
	Image xWinLine; // L'image de ligne blue qui relie les case gagnante pour X
	Image oWinLine;	// L'image de ligne orange qui relie les case gagnante pour O
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text draw; // Text: nombre de partie nulle
    @FXML
    private Text xWin; // Text: nombre de partie gagné par X
    @FXML
    private Text oWin; // Text: nombre de partie gagné par O

    @FXML
    private Button easyButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button hardButton;

    @FXML
    private Button twoPlayersButton;

    @FXML
    private Text lose;

    @FXML
    private Text win;
    
    @FXML
    private Text oText;
    
    @FXML
    private Text xText;
    
    @FXML
    private GridPane grille;
     
    private GameType gameType;
    private Player botPlayer;
    private Player startingPlayer = Player.O;
	private Image xImage;

	private Image oImage;
	private TicTacToe game;
	TicTacToeDifficulty botBrain;
	private int xWinCount, drawCount, oWinCount; // Score
	
	
	
    @FXML
    void initialize() {
        assert draw != null : "fx:id=\"draw\" was not injected: check your FXML file 'page1.fxml'."; assert lose != null : "fx:id=\"lose\" was not injected: check your FXML file 'page1.fxml'.";
        assert win != null : "fx:id=\"win\" was not injected: check your FXML file 'page1.fxml'.";
        xImage = new Image(new File("src/assets/img/x.png").toURI().toString());
    	oImage = new Image(new File("src/assets/img/o.png").toURI().toString());
    	xWinLine = new Image(new File("src/assets/img/xWinLine.png").toURI().toString());
    	oWinLine = new Image(new File("src/assets/img/oWinLine.png").toURI().toString());
    }
    /**
     * Appeler la premiere fois lors du chargement de la vue avec les parametre remplie dans le formulaire de nouvelle partie
     * Et appeler chaque fois que l'utilisateur change de difficulté ou de nombre de joueur durant le jeu 
     */
    void initGame(GameType gameType, TicTacToeDifficulty difficulty, Player player) {
    	startingPlayer = player;
    	this.gameType = gameType;
    	if(gameType == GameType.PLAYER_VS_COMPUTER) { // Si le mode est 1 joueur on definie la botPlayer (X ou O) et la difficulté
    		botPlayer = player.getOppostirePlayer();
    		botBrain = difficulty;
    	}
    	changeMode(difficulty);
    	game = new TicTacToe(startingPlayer);
    	resetGame();
    	resetScore();
    	int i = 0;
    }
    /**
     * Dessine dans une case
     * @param casePane la case
     * @param player (X ou O)
     */
    void drawCase(Pane casePane, Player player) {
    	ImageView x;
		if(player==Player.X)
    		x = new ImageView(xImage);
    	else
    		x = new ImageView(oImage);
        x.setFitHeight(63); 
        x.setFitWidth(63);
        x.setLayoutX(11.5);
        x.setLayoutY(2);
        casePane.getChildren().add(x);
        AnimationsManager.playScaleAnimation(x,50,.2,2);
    }
    /**
     * Remet le score à zéro
     */
    void resetScore() {
    	drawCount = 0;
    	xWinCount = 0;
    	oWinCount = 0;
    	draw.setText("0");
    	oWin.setText("0");
    	xWin.setText("0");
    }
    /**
     * Vide la grille pour commencer une nouvelle partie et change le joueur qui commence
     */
    void resetGame() {
    		startingPlayer = startingPlayer.getOppostirePlayer(); // Change le joueur qui commence pour eviter qu'un joueur joue en premier a chaque fois
    		game = new TicTacToe(startingPlayer);
    		for(Node casePane: grille.getChildren()) { // Vide les cases de la grille
        		if(!(casePane instanceof Group))
        		((Pane)casePane).getChildren().clear(); 
        	}
    		if((gameType == GameType.PLAYER_VS_COMPUTER&&game.getRole()==botPlayer)) {
    			playBot(); // Si c'est le tour de l'ordinateur on le fait jouer
    		}
    }
    /**
     * Récupere l'objet Pane de la case spécifiée
     * @param pos La position de la case
     * @return L'objet Pane de la case ou null si la position est incorrect
     */
    Pane getCasePane(int pos) {
    	int i =0;
    	for(Node nd : grille.getChildren()) {
    		if(! (nd instanceof ImageView)) {
    			if(i++==pos)
    				return(Pane) nd;
    		}
    	}
    	return null;
    }
    /**
     * Methode utilisé quand c'est le role de l'ordinateur
     */
    void playBot() {
    	if(gameType == GameType.PLAYER_VS_PLAYER) return; // Verifier si c'est vraiment le role de l'ordinateur sinon on sort
    	new Thread(()->{ // On créer un nouveau Thread pour ajouter un peu de temps de "reflexion" de 200ms 
    		int pos = 0;
        	pos = botBrain.getBotOutput(game); // On récupere le choix de l'ordinateur
        	final int p = pos;
    		try {
				Thread.sleep(200); // On attend 200ms pour eviter que l'ordinateur joue exactement au meme moment que le joueur
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
    		
        	try {
    			game.update(pos); // On mets a jours la grille de la partie
    			Platform.runLater(()->{
    				drawCase(getCasePane(p), botPlayer); // On applique le coup de l'ordinateur dans la grille du jeu
        			verifyGame(); // On verifie si il y a un gagnant
    			});
    			
    			
    		} catch (CaseNonVideException e) {
    			e.printStackTrace();
    			resetGame(); // Si une erreur se produit on recommence la partie
    		} 
    	}).start();
    	
    	
    	
    }
   /**
    * Methode utilisé pour verifier si il y a un gagnant ou non
    * Si un gagnant est trouve ou si c'est une partie nulle on incremente le score On affiche les animations puis on recommence la partie
    * Si c'est la partie est en cours elle ne fais rien
    * @return true si la partie est terminée false sinon
    */
    boolean verifyGame() {
    	Player winner = game.verifierGrille();
		if(game.isFull()||winner!=Player.EMPTY) {
			new Thread(()-> { 
				if(winner==Player.O) {  // Incremente le nombre de victoire de O
					oWin.setText(++oWinCount+"");
					
				}
				else if(winner==Player.X){ 	 // Incremente le nombre de victoire de X
					xWin.setText(++xWinCount+"");
				}
				else {	 // Incremente le nombre de partie nulle
					draw.setText(++drawCount+"");
				}
				endGameAnimation(); // Affiche les animations
				Platform.runLater(()->{
					resetGame(); // Recommence la partie
				});
				
			}).start();
			
			return true;
		}
		
		return false;
    }
    /**
     * Appellée à chaque fois que l'utilisateur clique sur une case
     */
    @FXML
    void onCaseClick(MouseEvent event) {
    	if(game.getRole()==botPlayer && gameType == GameType.PLAYER_VS_COMPUTER) return; // Si ce n'est pas le tour du joueur on sort
    	if(game.verifierGrille() != Player.EMPTY) return; // Si la partie est terminé on sort
    	Pane clickedNode = (Pane)event.getSource(); // On récupere l'objet Pane de la case sur laquel l'utilisateur a cliqué
        Integer colIndex = GridPane.getColumnIndex(clickedNode); // On récupere l'index de la colonne de la case
        Integer rowIndex = GridPane.getRowIndex(clickedNode);	// On récupere l'index de la ligne de la case
        try {
			game.update(rowIndex*3 + colIndex); // On mets à jour la grille de la partie
			drawCase(clickedNode, game.getDernierJoueur()); // On dessine sur la case
			if(!verifyGame()) { // On verifie si il y a un gagnant ou si la partie est terminée
				if(gameType == GameType.PLAYER_VS_COMPUTER) { // Si aucun gagnant et que le mode de jeu est 1 Joueur on passe le tour à l'ordinateur
					playBot();
				}
			}
		} catch (CaseNonVideException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
               
    }
    /**
     * Change la difficulté du jeu et modifie les style des boutons
     * @param difficulty nouvelle difficulté selectionée
     */
    void changeMode(TicTacToeDifficulty difficulty) {
    	if(difficulty == DifficultiesManager.easy) {
    		easyButton.getStyleClass().add("selected");
    		mediumButton.getStyleClass().removeAll("selected");
    		hardButton.getStyleClass().removeAll("selected");
    		twoPlayersButton.getStyleClass().removeAll("selected");
    	}
    	else if(difficulty == DifficultiesManager.medium){
    		easyButton.getStyleClass().removeAll("selected");
    		mediumButton.getStyleClass().add("selected");
    		hardButton.getStyleClass().removeAll("selected");
    		twoPlayersButton.getStyleClass().removeAll("selected");
    	}
    	else if(difficulty == DifficultiesManager.pro){
    		easyButton.getStyleClass().removeAll("selected");
    		mediumButton.getStyleClass().removeAll("selected");
    		hardButton.getStyleClass().add("selected");
    		twoPlayersButton.getStyleClass().removeAll("selected");
    	}
    	else {
    		easyButton.getStyleClass().removeAll("selected");
    		mediumButton.getStyleClass().removeAll("selected");
    		hardButton.getStyleClass().removeAll("selected");
    		twoPlayersButton.getStyleClass().add("selected");
    	}
    }
    /**
     * Méthode appellée lorsque l'utilisateur clique sur un bouton pour changer la difficulté ou le mode de jeu (2 Joueur ou 1 Joueur)
     */
    @FXML
    void onChangeMode(ActionEvent event) {
    	if(event.getSource() == easyButton ) {
    		changeMode(DifficultiesManager.easy);
    		initGame(GameType.PLAYER_VS_COMPUTER, DifficultiesManager.easy, startingPlayer);
    	}
    	else if(event.getSource() == mediumButton) {
    		changeMode(DifficultiesManager.medium);
    		initGame(GameType.PLAYER_VS_COMPUTER, DifficultiesManager.medium, startingPlayer);
    	}
    	else if(event.getSource() == hardButton){
    		changeMode(DifficultiesManager.pro);
    		initGame(GameType.PLAYER_VS_COMPUTER, DifficultiesManager.pro, startingPlayer);
    	}
    	else if(event.getSource() == twoPlayersButton){
    		changeMode(null);
    		initGame(GameType.PLAYER_VS_PLAYER, null, startingPlayer);
    	}
    } 
    
    @FXML
    private AnchorPane anchorParent;
    
    @FXML
    private Text oWinText;

    @FXML
    private Text xWinText;

    @FXML
    private Text youLoseText;

    @FXML
    private Text youWinText;
    
    @FXML
    private Text drawText;
    
    /**
     * Affiche les animations quand la partie est terminé
     */
    void endGameAnimation() {
    	int[] winningCase = game.getWinningCase(); // On récuperer les case gagnante
    	Player winner = game.verifierGrille();
    	ImageView line = null;
    	Text msgText = null;
    	if(winningCase!=null){ // Si les case gagnante sont égale à null c'est une partie nulle
    		String id = "line"+winningCase[0]+""+winningCase[2];
    		if(gameType == GameType.PLAYER_VS_COMPUTER && winner==botPlayer){ // Si l'ordinateur gagne
    			msgText = youLoseText; // On affiche le message "PERDU"
    			msgText.setVisible(true);
    			AnimationsManager.playScaleAnimation(msgText, 1000, 0.2, 2);
    		}
    		else { // Sinon 
    			if(gameType==GameType.PLAYER_VS_PLAYER) { // Si le mode est 2 Joueurs
    				if(winner == Player.O)
    					msgText = oWinText; // On affiche "O Gagne"
    				else
    					msgText = xWinText; // On affiche "X Gagne"
    			}
    			else { // Sinon si le mode est 1 Joueur et que ce n'est pas l'ordinateur qui a gagné donc c'est le joueur qui gagne
    				msgText = youWinText; // On affiche "Gagné"
    			}
    			msgText.setVisible(true);
    			AnimationsManager.playScaleAnimation(msgText, 1000, 0.2, 2);
    		}
    		// On affiche la ligne qui relie les case gagnante avec une animation en rotation
    		for(Node nd: anchorParent.getChildren()) {
    			if(nd.getId()!=null && nd.getId().equals(id)) {
    				line = ((ImageView)nd);
    				if(winner == Player.O)
    					line.setImage(oWinLine);
    				else if(winner == Player.X)
    					line.setImage(xWinLine);
    				line.setVisible(true);
    				AnimationsManager.playRotateAnimation(line);
    			}
    		}
    		// On ajoute deux animations au case gagnante (Rotation et Scale)
    		for(int caseid: winningCase) {
    			//getCasePane(caseid).setBackground(new Background(new BackgroundFill(Color.LIGHTGOLDENRODYELLOW, null, null)));
    			ImageView img = (ImageView) getCasePane(caseid).getChildren().get(0);
    			AnimationsManager.playScaleAnimation(img,1000,.2,1);
    			AnimationsManager.playRotateAnimation(img);
    		}
    		System.out.println("End Search "+id);
    	}
    	
    	else {// Sinon si la partie est nulle
    		if(winner==Player.EMPTY) { 
    			msgText = drawText; // On affiche "Partie Nulle"
    			msgText.setVisible(true);
    			AnimationsManager.playScaleAnimation(msgText, 500, 0.2, 2); // On ajoute l'animation
    		}
    	}
    	try {
			Thread.sleep(2000); // On attend 2 secondes 
			 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(line!=null) // Après 2 secondes on arretes les animations et on supprime le message
			line.setVisible(false);
    	if(msgText!=null)
    		msgText.setVisible(false);
    }
    /**
     * Methode de l'interface Helpable permet de récuperer le message d'aide pour l'afficher dans la vue Aide
     */
	@Override
	public String getHelpMessage() {
		// TODO Auto-generated method stub
		return new String("Vous pouvez changer la difficulté du jeu à tout moment en cliquant sur la difficulté.\r\n" + 
				"Vous pouvez aussi changer le mode de jeu (1 joueur ou 2 joueurs) à tout moment en cliquant sur 2 joueurs ou sur une difficulté (Pour 1 joueur).\r\n" + 
				"En changeant le mode de jeu ou la difficulté vous réinitialisez le score à 0.");
	}
}

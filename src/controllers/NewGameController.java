package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import controllers.GameViewController.GameType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.tictactoe.DifficultiesManager;
import models.tictactoe.TicTacToe;
import models.tictactoe.TicTacToe.Player;
import models.tictactoe.TicTacToeDifficulty;

/**
 * Controlleur de la vue de la page de nouvelle partie
 */
public class NewGameController extends Controller{

	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button easyButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button hardButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button oButton;

    @FXML
    private Pane onePlayerPane;

    @FXML
    private Button onePlayerButton;

    @FXML
    private Button startButton;

    @FXML
    private Button twoPlayersButton;

    @FXML
    private Button xButton;

    private Player player = Player.O;
    private GameType gameType = GameType.PLAYER_VS_COMPUTER;
    private TicTacToeDifficulty difficulty = DifficultiesManager.easy;
    @FXML
    void initialize() {
        assert easyButton != null : "fx:id=\"easyButton\" was not injected: check your FXML file 'Begin.fxml'.";
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'Begin.fxml'.";
        assert hardButton != null : "fx:id=\"hardButton\" was not injected: check your FXML file 'Begin.fxml'.";
        assert helpButton != null : "fx:id=\"helpButton\" was not injected: check your FXML file 'Begin.fxml'.";
        assert mediumButton != null : "fx:id=\"mediumButton\" was not injected: check your FXML file 'Begin.fxml'.";
        assert oButton != null : "fx:id=\"oButton\" was not injected: check your FXML file 'Begin.fxml'.";
        assert onePlayerPane != null : "fx:id=\"onePlayerPane\" was not injected: check your FXML file 'Begin.fxml'.";
        assert onePlayerButton != null : "fx:id=\"onePlayersButton\" was not injected: check your FXML file 'Begin.fxml'.";
        assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'Begin.fxml'.";
        assert twoPlayersButton != null : "fx:id=\"twoPlayersButton\" was not injected: check your FXML file 'Begin.fxml'.";
        assert xButton != null : "fx:id=\"xButton\" was not injected: check your FXML file 'Begin.fxml'.";

    }
    
    @FXML
    void onStart(ActionEvent event) throws IOException {
    	if(gameType == null) return;
    	if(gameType==GameType.PLAYER_VS_COMPUTER && difficulty == null) return;
    	if(gameType==GameType.PLAYER_VS_PLAYER)
    		ApplicationController.getApplicationController().chargerJeu(gameType, null, player);
    	else
    		ApplicationController.getApplicationController().chargerJeu(gameType, difficulty, player);
    }
    
    
    @FXML
    void onLevelSelect(ActionEvent event) {
    	if(event.getSource() == easyButton ) {
    		difficulty = DifficultiesManager.easy;
    		easyButton.getStyleClass().add("selected");
    		mediumButton.getStyleClass().removeAll("selected");
    		hardButton.getStyleClass().removeAll("selected");
    	}
    	else if(event.getSource() == mediumButton) {
    		difficulty = DifficultiesManager.medium;
    		easyButton.getStyleClass().removeAll("selected");
    		mediumButton.getStyleClass().add("selected");
    		hardButton.getStyleClass().removeAll("selected");
    	}
    	else if(event.getSource() == hardButton){
    		difficulty = DifficultiesManager.pro;
    		easyButton.getStyleClass().removeAll("selected");
    		mediumButton.getStyleClass().removeAll("selected");
    		hardButton.getStyleClass().add("selected");
    	}
    }
    @FXML
    void onCharSelect(ActionEvent event) {
    	if(event.getSource() == xButton ) {
    		player = Player.X;
    		xButton.getStyleClass().add("selected");
    		oButton.getStyleClass().removeAll("selected");
    	}
    	else if(event.getSource() == oButton){
    		player = Player.O;
    		oButton.getStyleClass().add("selected");
    		xButton.getStyleClass().removeAll("selected");
    	}
    }
    @FXML
    void onModeSelect(ActionEvent event) {
    	if(event.getSource() == onePlayerButton ) {
    		gameType = GameType.PLAYER_VS_COMPUTER;
    		onePlayerPane.setVisible(true);
    		onePlayerButton.getStyleClass().add("selected");
    		twoPlayersButton.getStyleClass().removeAll("selected");
    	}
    	else if(event.getSource() == twoPlayersButton){
    		gameType = GameType.PLAYER_VS_PLAYER;
    		twoPlayersButton.getStyleClass().add("selected");
    		onePlayerButton.getStyleClass().removeAll("selected");
    		onePlayerPane.setVisible(false);
    	}
    }
}

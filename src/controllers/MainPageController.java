package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;
import models.animations.AnimationsManager;

/**
 * Controller de la vue de la premiere page 
 */
public class MainPageController extends Controller{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button jouerButton;

    @FXML
    private Button modifierDifficulteButton;


    @FXML
    void initialize() {
        assert jouerButton != null : "fx:id=\"jouerButton\" was not injected: check your FXML file 'MainPageView.fxml'.";
        assert modifierDifficulteButton != null : "fx:id=\"modifierDifficulteButton\" was not injected: check your FXML file 'MainPageView.fxml'.";
        ScaleTransition scaleTransition = new ScaleTransition();
		scaleTransition.setNode(jouerButton);
		scaleTransition.setByX(0.2);
		scaleTransition.setByY(0.2);

		scaleTransition.setDuration(Duration.seconds(1));
		scaleTransition.setCycleCount(Timeline.INDEFINITE);
		scaleTransition.setAutoReverse(true);
		scaleTransition.play(); 
    }
    @FXML
    void newGame(ActionEvent actionEvent) {
    	ApplicationController.getApplicationController().chargerNouvellePartie();
    }
    @FXML
    void learn(ActionEvent actionEvent) {
    	ApplicationController.getApplicationController().chargerDifficulte();
    }
}

package controllers;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.text.Text;
import models.tictactoe.DifficultiesManager;

/**
 * Controller de la vue de modification de difficulté
 *
 */
public class DifficultiesEditorController extends Controller implements Helpable{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ToggleGroup difficulte;

    @FXML
    private RadioButton difficulteFacile;

    @FXML
    private RadioButton difficulteMoyen;

    @FXML
    private Text errorText;

    @FXML
    private TextField hiddenLayerText;

    @FXML
    private ToggleGroup jeuDonne;

    @FXML
    private TextField learningRateText;

    @FXML
    private RadioButton miniMaxCB;

    @FXML
    private TextField nbNeuronText0;
    @FXML
    private TextField nbNeuronText1;
    @FXML
    private TextField nbNeuronText2;

    @FXML
    private TextField nbPartieText;

    @FXML
    private RadioButton randomCB;

    @FXML
    private Button startButton;
    
    @FXML
    private Button reset;

    
    @FXML
    void initialize() {
        assert difficulte != null : "fx:id=\"difficulte\" was not injected: check your FXML file 'Learn.fxml'.";
        assert difficulteFacile != null : "fx:id=\"difficulteFacile\" was not injected: check your FXML file 'Learn.fxml'.";
        assert difficulteMoyen != null : "fx:id=\"difficulteMoyen\" was not injected: check your FXML file 'Learn.fxml'.";
        assert errorText != null : "fx:id=\"errorText\" was not injected: check your FXML file 'Learn.fxml'.";
        assert hiddenLayerText != null : "fx:id=\"hiddenLayoutText\" was not injected: check your FXML file 'Learn.fxml'.";
        assert jeuDonne != null : "fx:id=\"jeuDonne\" was not injected: check your FXML file 'Learn.fxml'.";
        assert learningRateText != null : "fx:id=\"learningRateText\" was not injected: check your FXML file 'Learn.fxml'.";
        assert miniMaxCB != null : "fx:id=\"miniMaxCB\" was not injected: check your FXML file 'Learn.fxml'.";
        assert nbNeuronText0 != null : "fx:id=\"nbNeuronText\" was not injected: check your FXML file 'Learn.fxml'.";
        assert nbPartieText != null : "fx:id=\"nbPartieText\" was not injected: check your FXML file 'Learn.fxml'.";
        assert randomCB != null : "fx:id=\"randomCB\" was not injected: check your FXML file 'Learn.fxml'.";
        assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'Learn.fxml'.";
        
        hiddenLayerText.textProperty().addListener((Observable, oldValue, newValue) -> {
        	try {
	            if(Integer.parseInt(newValue)>1) {
	            	nbNeuronText1.setDisable(false);
	            }
	            else {
	            	nbNeuronText1.setDisable(true);
	            }
	            if(Integer.parseInt(newValue)>2) {
	            	nbNeuronText2.setDisable(false);
	            }
	            else {
	            	nbNeuronText2.setDisable(true);
	            }
        	}
        	catch(NumberFormatException e) {
        		nbNeuronText2.setDisable(true);
        		nbNeuronText1.setDisable(true);
        	}
        });
        difficulteChange();
    }
    @FXML
    void startLearning(ActionEvent actionEvent) {
    	if(!(verifyNbLayer() & verifyNbPartie() & learningRateVerify())) return;
    	String difficulte=null;
    	if(difficulteFacile.isSelected())
    		difficulte = "easy";
    	else if(difficulteMoyen.isSelected())
    		difficulte = "medium";
    	
    	if(difficulte!=null) {
    		int nbPartie = Integer.parseInt(nbPartieText.getText());
    		double learningRate = Double.parseDouble(learningRateText.getText());
    		int nbHiddenLayer = Integer.parseInt(hiddenLayerText.getText());
    		int[] neuronByLayer = new int[nbHiddenLayer];
			System.out.println("Diff "+difficulte);
			System.out.println("NbPartie "+nbPartie);
			System.out.println("Learning Rate "+learningRate);
			System.out.println("NB Layer "+nbHiddenLayer);
			neuronByLayer[0] = Integer.parseInt(nbNeuronText0.getText());
			if(nbHiddenLayer>1) neuronByLayer[1] = Integer.parseInt(nbNeuronText1.getText());
			if(nbHiddenLayer>2) neuronByLayer[2] = Integer.parseInt(nbNeuronText2.getText());
			System.out.println(Arrays.toString(neuronByLayer));
			if(miniMaxCB.isSelected()) {
    			ApplicationController.getApplicationController().startLearning(difficulte, learningRate, neuronByLayer, nbPartie);
    		}
    		else {
    			System.out.println("Random");
    		}
		}
    	
    }
    boolean verifyDouble(String text, double max, double min) {
    	try {
    		if(Double.parseDouble(text)>min && Double.parseDouble(text)<max) 
    			return true;
    	}
    	catch(Exception e) {
    		
    	}
    	return false;
    }
    boolean verifyInt(String text, int max, int min) {
    	try {
    		if(Integer.parseInt(text)>min && Integer.parseInt(text) < max) 
    			return true;
    	}
    	catch(Exception e) {
    		
    	}
    	return false;
    }
    boolean verifyNbLayer() {
    	if(verifyInt(hiddenLayerText.getText(), 4, 0)){
    		removeErrorFromInput(hiddenLayerText);
    		int nb = Integer.parseInt(hiddenLayerText.getText());
    		if(!verifyInt(nbNeuronText0.getText(), 601, 2))
    			addErrorToInput(nbNeuronText0, "");
    		else
    			removeErrorFromInput(nbNeuronText0);
    		if(nb>1 && !verifyInt(nbNeuronText1.getText(), 601, 2))
    			addErrorToInput(nbNeuronText1, "");
    		else
    			removeErrorFromInput(nbNeuronText1);
    		if(nb>2 && !verifyInt(nbNeuronText2.getText(), 601, 2))
    			addErrorToInput(nbNeuronText2, "");
    		else
    			removeErrorFromInput(nbNeuronText2);
    		return true;
    	}
    	else {
    		addErrorToInput(hiddenLayerText, "Le nombre de hidden layer doit étre entre 1 et 3 !");
    		return false;
    	}
    }
    boolean verifyNbPartie() {
    	if(verifyInt(nbPartieText.getText(), 500001, 99)){
    		removeErrorFromInput(nbPartieText);
    		return true;
    	}
    	else {
    		addErrorToInput(nbPartieText, "Le nombre de partie doit étre entre 10 et 500000 !");
    		return false;
    	}
    }
    boolean learningRateVerify() {
    	if(verifyDouble(learningRateText.getText(), Double.MAX_VALUE, 0)){
    		removeErrorFromInput(learningRateText);
    		return true;
    	}
    	else {
    		addErrorToInput(learningRateText, "Learning rate doit étre supérieure à 0 !");
    		return false;
    	}
    }
    void removeErrorFromInput(TextField text) {
    	text.setPromptText("");
    	text.getStyleClass().removeAll("text-error");
    }
    void addErrorToInput(TextField text, String error) {
    	text.setText("");
    	text.setPromptText(error);
    	text.getStyleClass().add("text-error");
    }
    @FXML
    void difficulteChange() {
    	String difficulte=null;
    	if(difficulteFacile.isSelected()) {
    		difficulte = "easy";
    	}
    	else if(difficulteMoyen.isSelected()) {
    		difficulte = "medium";
    	}
    		
    	if(difficulte!=null) {
    		File file = DifficultiesManager.loadUserDifficultyFile(difficulte);
    		if(file.exists()) {
    			reset.setDisable(false);
    		}
    		else {
    			reset.setDisable(true);
    		}
    	}
    }
    /**
     * Methode permet de réinitialiser la difficulté choisie en supprimant la difficulté créer par l'utilisateur
     */
    @FXML
    void resetLevel() {
    	String difficulte=null;
    	String difficulteMsg = null;
    	if(difficulteFacile.isSelected()) {
    		difficulte = "easy";
    		difficulteMsg = "Facile";
    	}
    	else if(difficulteMoyen.isSelected()) {
    		difficulte = "medium";
    		difficulteMsg = "Moyen";
    	}
    		
    	if(difficulte!=null) {
    		File file = DifficultiesManager.loadUserDifficultyFile(difficulte);
    		if(file.exists()) {
    			Alert alertReset = new Alert(AlertType.CONFIRMATION);
    			alertReset.setTitle("Réinitialition de la difficulté "+difficulteMsg);
    			alertReset.setContentText("Êtes-vous sur(e) de vouloir réinitialiser la difficulté "+difficulteMsg+" ?");
    			ButtonType buttonOui = new ButtonType("Oui");
    			ButtonType buttonNon = new ButtonType("Non", ButtonData.CANCEL_CLOSE);

    			alertReset.getButtonTypes().setAll(buttonOui, buttonNon);

    			Optional<ButtonType> result = alertReset.showAndWait();
    			if (result.get() == buttonOui){
    			    if(file.delete()) {
    			    	Alert infoResetSuccessAlert = new Alert(AlertType.INFORMATION);
    			    	infoResetSuccessAlert.setTitle("La difficulté a été reinisialitée ! ");
    			    	infoResetSuccessAlert.setHeaderText(null);
    			    	infoResetSuccessAlert.setContentText("Vous avez reinisialité la difficulté: "+difficulteMsg);
    			    	infoResetSuccessAlert.showAndWait();
    			    }
    			} 
    			
    		}
    	}
    		
    }
    /**
     * Methode de l'interface Helpable permet de récuperer le message d'aide pour l'afficher dans la vue Aide
     */
	@Override
	public String getHelpMessage() {
		return new String("Modification d'une difficulté:\r\n" + 
    			"étape 1:\r\n" + 
    			"sélectionner la difficulté que vous voulez modifier Facile où moyenne la diffucilté invincible ne peut pas être modifiées.\r\n" + 
    			"étape 2:\r\n" + 
    			"Entrez la valeur du learning rate (Supérieure à 0)\r\n" + 
    			"étape 3:\r\n" + 
    			"Entrez le nombre des hidden layer (entre 1 et 3) les layer d'entrée (input layer) et les layer de sortie(output layer) sont déja définies.\r\n" + 
    			"étape 4:\r\n" + 
    			"Entrez le nombre de neurones dans chaque hidden layer(Entre 3 et 600)\r\n" + 
    			"étape 5:\r\n" + 
    			"Entrez le nombre de parties qui seront jouées contre le niveau invincible pour apprendre (Plus d'info en dessous).\r\n" + 
    			"\r\n" + 
    			"Déroulement de l'apprentissage:\r\n" + 
    			"L'apprentissage se fait en jouant N parties (Nombre de parties définies dans le formulaire) contre un IA invincible " + 
    			"qui utilise l'algorithme de miniMax.\r\n" + 
    			"le IA que vous créer apprendra des bons coups de l'IA adverse ainsi que les bons coups qu'il fait.\r\n"
    			+ "Réinitialiser une difficulté:\r\n" + 
    			"vous pouvez réinitialiser une difficulté a celle par défaut en choisissant la difficulté et puis en cliquant sur Réinitialiser en bas à droite");
	}
}

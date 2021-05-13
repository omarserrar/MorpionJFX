package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

/**
 *	Controlleur de la vue qui affiche l'etat de l'apprentissage en arriere plan
 */
public class BackroundLearnController extends Controller{

	private static BackroundLearnController instance;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text intelligence;

    @FXML
    private Text partieJoueesText;

    @FXML
    private Text pourcentage;

    @FXML
    private Button saveButton;

    @FXML
    private Button stopButton;
    
    @FXML
    private ProgressBar progressBar;


    private LearningController controller;
    
    public static BackroundLearnController getInstance() {
    	return instance;
    }
    @FXML
    void initialize() {
        assert intelligence != null : "fx:id=\"intelligence\" was not injected: check your FXML file 'BackgroundLearn.fxml'.";
        assert partieJoueesText != null : "fx:id=\"partieJoueesText\" was not injected: check your FXML file 'BackgroundLearn.fxml'.";
        assert pourcentage != null : "fx:id=\"pourcentage\" was not injected: check your FXML file 'BackgroundLearn.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'BackgroundLearn.fxml'.";
        assert stopButton != null : "fx:id=\"stopButton\" was not injected: check your FXML file 'BackgroundLearn.fxml'.";

        instance = this;
    }
    /**
     * Modifie l'etat de l'apprentissage
     * @param intelligence taux d'intelligence
     * @param partieJoue nombre de partie joue
     * @param nbPartie nombre total de partie
     */
    public void updateState(double intelligence, int partieJoue, int nbPartie) {
		double prc = 0;
		if(partieJoue>0) {
			prc = ((double)partieJoue/(double)nbPartie);
			pourcentage.setText(String.format("%.2f %%", prc*100)); 
		}
		else pourcentage.setText("0 %");
		progressBar.setProgress(prc);
		this.intelligence.setText(String.format("%.2f %%", intelligence));
		this.partieJoueesText.setText(String.format("%d / %d", partieJoue, nbPartie));
    }
    /**
     * Appeler quand l'apprentissage est termine
     */
    public void onLearnEnd() {
    	Platform.runLater(()->{
	    	stopButton.setText("Quitter Sans Enregistrer");
	    	saveButton.setText("Enregistrer");
    	});
    }
    /**
     * Appeler quand l'apprentissage est arreter
     */
    @FXML
	public void stopLearning() {
		if(LearningController.getInstance().learningThread.isAlive()) LearningController.getInstance().learningThread.stop();
		LearningController.getInstance().onBackGroundLearnEnd();
    }
    /**
     * Appeler quand l'apprentissage est enregistrer
     */
	@FXML
	public void stopAndSaveLearning() {
		System.out.println("heeere");
		if(LearningController.getInstance().learningThread.isAlive()) LearningController.getInstance().learningThread.stop();
		LearningController.getInstance().difficulty.serialize();
		LearningController.getInstance().onBackGroundLearnEnd();
	}
}

package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

/**
 * Controlleur de la vue d'aide
 */
public class HelpController extends Controller{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label aideText;
    
    @FXML
    void initialize() {
    	

    }
    /**
     * Affiche le message text de la vue du Helpable
     * @param helpable Le helpable de la vue de laquel on veut afficher le message d'aide
     */
    void setHelpMessage(Helpable helpable) {
    	aideText.setText(helpable.getHelpMessage()); // On récupere le message d'aide du Helpable et on l'affiche
    }

}

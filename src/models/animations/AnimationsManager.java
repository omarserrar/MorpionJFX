package models.animations;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationsManager {
	
	public static void playRotateAnimation(Node obj) {
		RotateTransition rotateTransition = new RotateTransition(); 
		rotateTransition.setDuration(Duration.millis(200)); 
		rotateTransition.setNode(obj);     
		rotateTransition.setByAngle(10);
		rotateTransition.setCycleCount(2);
		rotateTransition.setAutoReverse(true); 
		rotateTransition.play(); 
	}
	public static void playScaleAnimation(Node obj, double duration, double scale, int cycle) {
		ScaleTransition scaleTransition = new ScaleTransition();
		scaleTransition.setNode(obj);
		scaleTransition.setByX(scale);
		scaleTransition.setByY(scale);
		scaleTransition.setCycleCount(cycle);
		scaleTransition.setDuration(Duration.millis(duration));  
		scaleTransition.setAutoReverse(true);
		scaleTransition.play(); 
	}
}

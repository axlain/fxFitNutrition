package clienteescritoriofitnutrition;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClienteEscritorioFitNutrition extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent vista =
                    FXMLLoader.load(getClass().getResource("FXMLInicioSesion.fxml"));
            
            //crear la escena 
            Scene escenaLogin = new Scene(vista); 
            escenaLogin.setFill(Color.TRANSPARENT);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setScene(escenaLogin); 
            primaryStage.setTitle("Login");
            primaryStage.show(); 
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(ClienteEscritorioFitNutrition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   
    public static void main(String[] args) {
        launch(args);
    }
    
}

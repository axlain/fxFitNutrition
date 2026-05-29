package clienteescritoriofitnutrition;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClienteEscritorioFitNutrition extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent vista =
                    FXMLLoader.load(getClass().getResource("FXMLInicioSesion.fxml"));
            
            //crear la escena 
            Scene escenaLogin = new Scene(vista); 
            primaryStage.setScene(escenaLogin); 
            primaryStage.setTitle("FIT NUTRITION - Iniciar sesion");
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

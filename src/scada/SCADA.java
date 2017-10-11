/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Jakob
 */
public class SCADA extends Application {

    private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        FXMLDocumentController controller = loader.getController();
        Scada_Controller scadCon = new Scada_Controller();

        controller.setScadaCon(scadCon);
        scadCon.setGUICon(controller);
        
        // Initiates Timer with a 1 minute recurring call. 
        timer.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("Updated at: " + new Date());
                scadCon.updateAll();
            }
        }, 0, 10, TimeUnit.SECONDS);

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

}

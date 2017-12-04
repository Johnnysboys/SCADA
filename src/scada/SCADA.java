package scada;

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

        controller.setController(scadCon);
        
        // Initiates Timer with a 2 second recurring call. 
        timer.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                // Updates the GUI every 2 seconds. 
                controller.updateWindows();
            }
        }, 0, 2, TimeUnit.SECONDS);

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }

}

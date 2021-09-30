package MVC;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * JavaFX App
 */



public class App extends Application {

    public static Parent root;
    public static String way = System.getProperty("user.dir");

    private static MVC.Model model;

    public static void setModel(MVC.View view) {
        model = new MVC.Model(view);
    }

    public static MVC.Model getModel() {
        return model;
    }

    @Override
    public void start(Stage stage) {
        root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        } catch (IOException e) {
            e.getMessage();
        }
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
package dev.bong.view;

import dev.bong.control.ControlDeconnexionMoteur;
import dev.bong.control.ControlIndexation;
import dev.bong.control.ControlRechercheFichier;
import dev.bong.control.ControlRechercheMotCle;
import fr.dgac.ivy.IvyException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.text.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RechercheApplication extends Application {
    public static Stage stage;

    public static void changerScene(String view) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RechercheApplication.class.getResource("/layout/" + view));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RechercheApplication.class.getResource("/layout/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 450);

        RechercheApplication.stage = stage;

        stage.setTitle("Bong - By DESQL");
        stage.getIcons().add(new Image(RechercheApplication.class.getResource("/images/1f50d.png").toExternalForm()));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch();

        try {
            ControlDeconnexionMoteur.deconnexionMoteurs();
        } catch (IvyException e){
            e.printStackTrace();
        }

        /* TEST INDEXATION
        ControlIndexation controlIndexation = new ControlIndexation();
        controlIndexation.run();

         */

        /* TEST FICHIER + MODE
        ArrayList<String> l = new ArrayList<>();
        ArrayList<String> li = new ArrayList<>();

        l.add("26-Lyon_prend_ses_aises_en.xml");

        ControlRechercheFichier controlRechercheFichier = new ControlRechercheFichier(l,li,true);
        controlRechercheFichier.start();
        */
    }
}
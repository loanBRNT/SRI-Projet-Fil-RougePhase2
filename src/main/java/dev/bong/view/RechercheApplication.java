package dev.bong.view;

import dev.bong.control.ControlIndexation;
import dev.bong.control.ControlRechercheMotCle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RechercheApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RechercheApplication.class.getResource("/layout/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 450);

         /*StackPane root = new StackPane();
        Image img = new Image("fil:lodo.png");
        BackgroundImage bImg = new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background bGround = new Background(bImg);
        fxmlLoader.load().setBackground(bGround);*/

        stage.setTitle("Bienvenue");
        stage.getIcons().add(new Image(RechercheApplication.class.getResource("/images/1f50d.png").toExternalForm()));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
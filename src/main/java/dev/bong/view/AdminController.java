package dev.bong.view;


    import dev.bong.control.ControlSidentifier;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.stage.Stage;
    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;

public class AdminController{
    public TextField inputPseudo;// implements Initializable {
    public PasswordField inputPwd;
    public ControlSidentifier controlSidentifier = new ControlSidentifier();
    private int essai=3;

    @FXML
    private Label connexionAdministrateur;

    @FXML
    protected void onBackParam() throws IOException {
        essai=3;
        Parent param = FXMLLoader.load(AdminController.class.getResource("/layout/parametre.fxml"));
        Scene scene = new Scene(param);
        Stage thisStage = (Stage) connexionAdministrateur.getScene().getWindow();
        thisStage.setTitle("Paramètres");
        thisStage.setScene(scene);
        thisStage.show();
        System.out.println("Boutton retour appuyé");
    }

    @FXML
    protected void onConnexion() throws IOException {
        String id = inputPseudo.getText();
        String pwd = inputPwd.getText();
        if(controlSidentifier.verifieridentite(id,pwd)){
            Parent param = FXMLLoader.load(AdminController.class.getResource("/layout/paramAdmin.fxml"));
            Scene scene = new Scene(param);
            Stage thisStage = (Stage) connexionAdministrateur.getScene().getWindow();
            thisStage.setTitle("Panneau de configuration");
            thisStage.setScene(scene);
            thisStage.show();
            System.out.println("Connexion autorisée");
        }
        else{
            essai--;
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur Connexion");
            alert.setHeaderText("Identifiant ou mot de passe incorrect");
            alert.setContentText("il vous reste "+essai+" essai(s)");
            alert.showAndWait();
            if (essai==0){
                onBackParam();
            }
        }

    }

}

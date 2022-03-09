import boundary.Clavier;
import control.ControlCommunicationIvy;
import entity.CommunicationIvy;

public class Main{
    //Attributs

    //Constructeur

    //Méthodes
    public static void main(String[] args) {
        ControlCommunicationIvy communicationIvy = new ControlCommunicationIvy();

        communicationIvy.lancerCommunication();

        int choix=1;
        while(choix != 0){
            if(communicationIvy.envoyerMessage("Interface message=rechercheMotCle source=monde type=texte")){
                System.out.println("mess envoyé");
            } else {
                System.out.println("echec envoie");
            }
            choix = Clavier.entrerClavierInt();
        }
        communicationIvy.envoyerMessage("Stop");
        communicationIvy.stopperCommunication();
    }
}

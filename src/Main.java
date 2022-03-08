import boundary.Clavier;
import entity.CommunicationIvy;

public class Main{
    //Attributs

    //Constructeur

    //Méthodes
    public static void main(String[] args) {
        CommunicationIvy communicationIvy = new CommunicationIvy();

        communicationIvy.lancerCommunication();

        int choix=1;
        while(choix != 0){
            if(communicationIvy.envoieMessage("Interface message=rechercheMotCle source=banane type=texte")){
                System.out.println("mess envoyé");
            } else {
                System.out.println("echec envoie");
            }
            choix = Clavier.entrerClavierInt();
        }
        communicationIvy.envoieMessage("Stop");
        communicationIvy.fermerCommunication();
    }
}

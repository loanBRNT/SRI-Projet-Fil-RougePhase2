import boundary.Clavier;
import control.ControlCommunicationIvy;
import entity.CommunicationIvy;

public class Main{
    //Attributs

    //Constructeur

    //Méthodes
    public static void main(String[] args) {
        String motTape = "";
        ControlCommunicationIvy communicationIvy = new ControlCommunicationIvy();

        communicationIvy.lancerCommunication();

        while(!motTape.equals("Stop")){
            motTape = Clavier.entrerClavierString();
            communicationIvy.envoyerMessage("Interface message=rechercheMotCle source=" + motTape);
        }

        communicationIvy.envoyerMessage("Stop");
        communicationIvy.stopperCommunication();
    }
}

import fr.dgac.ivy.*;

import java.util.Scanner;

public class Main{
    private static Ivy bus;
    private static String message;
    private static Scanner scanner = new Scanner(System.in);
    //Attributs

    //Constructeur

    //Méthodes
    public static void main(String[] args) {
        try
        {
            bus = new Ivy("sender", " sender_processing is ready", null);
            bus.start("127.255.255.255:2010");

            bus.bindMsg("^Demo_Processing Feedback=ok", new IvyMessageListener()
            {
                public void receive(IvyClient client,String[] args)
                {
                    message = "message sent successfully!";
                }
            });

        }
        catch (IvyException ie)
        {
            System.out.println("echec setup");
        }

        System.out.println("Hello World !");
        int choix=1;
        while(choix != 0){
            try
            {
                bus.sendMsg("Demo_Processing Command=YO");
                System.out.println("mess envoyé");
            }
            catch (IvyException ie)
            {
                System.out.println("echec envoie");
            }
            choix = scanner.nextInt();
        }
        bus.stop();
    }
}

import processing.core.PApplet;
import processing.core.PImage;

public class Main extends PApplet{
    //Attributs
    public static PApplet processing;
    //Constructeur

    //Méthodes
    public static void main(String[] args) {
        PApplet.main("Main",args);
    }

    public void settings(){
        size(900, 600);
    }

    public void setup(){
        processing = this;
        surface.setTitle("Processing on IntellijIDEA");

        colorMode(HSB,255,255,255);
    }

    public void draw(){
        PImage imageChaudasse = loadImage("../Chaudasse.png");

        image(imageChaudasse,0,0);



    }
}

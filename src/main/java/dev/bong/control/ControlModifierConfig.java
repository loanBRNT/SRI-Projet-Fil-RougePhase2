package dev.bong.control;

import dev.bong.entity.Config;
import fr.dgac.ivy.IvyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControlModifierConfig {


    public static void modifConfig(List<Integer>listeValeur) throws Exception {
        Config config = Config.getInstance();
        try{
            config.majConfig(listeValeur);
            config.chargementConfig();
        }catch (Exception e){
            throw e;
        }

    }
}

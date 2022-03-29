package dev.bong.control;

import fr.dgac.ivy.IvyException;

public class ControlMenuAdmin {
    private ControlIndexation controlIndexation;

    public  ControlMenuAdmin(ControlIndexation controlIndexation){
        this.controlIndexation=controlIndexation;
    }

    public void indexation() throws Exception {
        ControlIndexation.indexationForcee();
    }
}

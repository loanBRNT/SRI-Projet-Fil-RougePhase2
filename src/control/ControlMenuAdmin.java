package control;

public class ControlMenuAdmin {
    private ControlIndexation controlIndexation;

    public  ControlMenuAdmin(ControlIndexation controlIndexation){
        this.controlIndexation=controlIndexation;
    }
    public void indexation() {
        this.controlIndexation.indexation();
    }
}

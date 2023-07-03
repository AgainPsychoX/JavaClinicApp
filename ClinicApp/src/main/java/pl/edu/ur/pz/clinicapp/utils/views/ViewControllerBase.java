package pl.edu.ur.pz.clinicapp.utils.views;

public abstract class ViewControllerBase implements ViewController {
    private ViewsContainerController parentController;
    public void setParentController(ViewsContainerController parent) {
        this.parentController = parent;
    }
    public ViewsContainerController getParentController() {
        return parentController;
    }
}

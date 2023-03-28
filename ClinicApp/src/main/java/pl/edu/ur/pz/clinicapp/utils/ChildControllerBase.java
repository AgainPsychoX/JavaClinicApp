package pl.edu.ur.pz.clinicapp.utils;

public abstract class ChildControllerBase<T> implements ChildController<T> {
    private T parentController;
    public void setParentController(T parent) {
        this.parentController = parent;
    }
    public T getParentController() {
        return parentController;
    }
}

package pl.edu.ur.pz.clinicapp.utils;

public interface ChildController<T> extends Populatable, Refreshable {
    void setParentController(T parent);
    T getParentController();
    default void dispose() {};
}

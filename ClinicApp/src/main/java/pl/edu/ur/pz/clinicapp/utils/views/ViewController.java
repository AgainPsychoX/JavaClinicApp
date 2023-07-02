package pl.edu.ur.pz.clinicapp.utils.views;

public interface ViewController {
    ViewsContainerController getParentController();

    /**
     * Fills the view with relevant data. Called each time view is displayed,
     * but might be called to repopulate, for example: as part of refreshing.
     * @param context context passed to the view
     */
    default void populate(Object... context) {}

    /**
     * Refreshes the view to keep it consistent with actual underlying state,
     * for example by fetching data from database and/or repopulating.
     */
    default void refresh() {}

    /**
     * Disposes of view stuff. Called when view is about to be replaced by another one.
     */
    default void dispose() {};

    /**
     * Called on/before navigation attempts, allows conditionally preventing navigation.
     * @param which which view to navigate to
     * @param context optional context
     * @return true to allow the navigation, false to prevent
     */
    default boolean onNavigation(Class<? extends ViewController> which, Object... context) {
        return true; // always allow navigation by default
    }
}

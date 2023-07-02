package pl.edu.ur.pz.clinicapp.utils.views;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pl.edu.ur.pz.clinicapp.utils.JPAUtils.getExplanatoryStringWithoutInitializing;

abstract public class ViewsContainerController implements Initializable {
    private static final Logger logger = Logger.getLogger(ViewsContainerController.class.getName());

    private Map<Class<? extends ViewController>, ViewDefinition> views;

    private HistoryTracker<Class<? extends ViewController>> historyTracker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        views = new HashMap<>(20);
        historyTracker = new HistoryTracker<>();
    }

    abstract protected URL getViewResource(Class<? extends ViewController> which);

    protected ViewDefinition getView(Class<? extends ViewController> which) {
        final var cached = views.get(which);
        if (cached == null) {
            try {
                logger.fine("Loading view: " + which);
                final var loader = new FXMLLoader(getViewResource(which));
                final Node node = loader.load();
                final ViewControllerBase controller = loader.getController();
                controller.setParentController(this);
                final var def = new ViewDefinition(node, controller);
                views.put(which, def);
                return def;
            }
            catch (Exception e) {
                throw new RuntimeException("Error while loading view: " + which.getCanonicalName(), e);
            }
        } else {
            return cached;
        }
    }

    protected ViewDefinition getCurrentView() {
        return getView(historyTracker.getCurrent().which);
    }

    protected ViewDefinition getPreviousView() {
        final var point = historyTracker.getPrevious();
        if (point == null) {
            return null;
        }
        return getView(point.which);
    }

    /**
     * Sets content area to given node.
     * @param node root node of view visualization
     */
    abstract protected void setContent(Node node);

    /**
     * Navigates to view without pushing history stack.
     *
     * @param which   Which view to navigate to.
     * @param context Additional context parameter(s).
     */
    public void goToViewRaw(Class<? extends ViewController> which, Object... context) {
        logger.info("Navigation to view: %s".formatted(which));
        if (logger.isLoggable(Level.FINE)) {
            if (context.length > 0) {
                for (int i = 0; i < context.length; i++) {
                    logger.fine("context[%d] == %s".formatted(i, getExplanatoryStringWithoutInitializing(context[i])));
                }
            } else {
                logger.fine("(context empty)");
            }
        }

        final var newView = getView(which);
        final var oldView = getPreviousView();

        if (oldView != null && oldView.controller != null) {
            if (!oldView.controller.onNavigation(which, context)) {
                logger.info("Cancelled");
                return;
            }

            oldView.controller.dispose();
        }
        setContent(newView.node);
        if (newView.controller != null) {
            newView.controller.populate(context);
        }
    }

    /**
     * Navigates to view.
     *
     * @param which   Which view to navigate to.
     * @param context Additional context parameter(s).
     */
    public void goToView(Class<? extends ViewController> which, Object... context) {
        historyTracker.go(which, context);
        goToViewRaw(which, context);
    }

    /**
     * Navigates to specific history point (without pushing history stack).
     *
     * @param point Point in history to navigate to.
     */
    public void goToHistoryPoint(HistoryTracker.HistoryPoint<Class<? extends ViewController>> point) {
        goToViewRaw(point.which, point.context);
    }

    /**
     * Navigates back in the history (if possible).
     */
    public void goBack() {
        goToHistoryPoint(historyTracker.back());
    }

    /**
     * @return List of history points, as views with their contexts.
     */
    public List<HistoryTracker.HistoryPoint<Class<? extends ViewController>>> getHistory() {
        return historyTracker.getHistory();
    }

    /**
     * Calls for refresh on current view.
     */
    public void refreshCurrentView() {
        final var view = getCurrentView();
        if (view.controller != null) {
            view.controller.refresh();
        }
    }
}

package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

import static pl.edu.ur.pz.clinicapp.utils.ProblemHandlingUtils.reportExceptionNicely;

/**
 * Base class for edit dialogs, that allow consistent creating, editing and deleting actions.
 */
public abstract class BaseEditDialog extends Stage {
    /**
     * Mode of the edit dialog, describing set of possible actions.
     */
    public enum Mode {
        /**
         * Dialog working with fresh instance of model, allowing to edit and save.
         */
        NEW,
        /**
         * Dialog working with existing instance of model, allowing editing (incl. saving) and deleting.
         */
        EDIT,
        /**
         * Dialog working with instance marked to be deleted, allowing deleting.
         */
        DELETE,
    }

    /**
     * State of the edit dialog, describing somewhat dialog lifecycle and its relation to represented model.
     */
    public enum State {
        /**
         * Fresh dialog, awaiting user interaction.
         */
        FRESH,
        /**
         * Data in dialog was modified by user, awaiting further edits, saving or canceling.
         * Not implemented.
         * TODO: DIRTY state for edit dialog after user input, need confirmation before cancelling.
         */
        DIRTY,
        /**
         * New instance of model was committed.
         */
        NEW_COMMITTED,
        /**
         * Related instance of model was edited.
         */
        EDIT_COMMITTED,
        /**
         * Related instance of model was deleted.
         */
        DELETE_COMMITTED,
        /**
         * Dialog was dismissed, no action was taken.
         */
        CANCELED,
    }

    final protected BorderPane pane;

    @FXML
    protected Button deleteButton;

    final protected Mode mode;
    public Mode getMode() {
        return mode;
    }

    private State state;
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Constructor for base edit dialog, to be used internally for implementations.
     * @param fxml URL to FXML resource to use for the dialog.
     * @param mode Mode of the dialog.
     */
    protected BaseEditDialog(URL fxml, Mode mode) {
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setController(this);

        try {
            pane = fxmlLoader.load();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        // Default styling & window (stage) properties
        initModality(Modality.APPLICATION_MODAL);
        minWidthProperty().bind(pane.minWidthProperty());
        maxWidthProperty().bind(pane.maxWidthProperty());
        minHeightProperty().bind(pane.minHeightProperty());
        maxHeightProperty().bind(pane.maxHeightProperty());
        setResizable(pane.getMinHeight() == pane.getMaxHeight() && pane.getMinWidth() == pane.getMaxWidth());
        setScene(new Scene(pane));

        setState(State.FRESH);
        this.mode = mode;

        deleteButton.setVisible(this.mode != Mode.NEW);
    }

    @Override
    public void close() {
        // TODO: ask for confirmation (possibly only if anything changed)
        super.close();
    }

    /**
     * Semi-abstract method (default always return success) for canceling operation
     * @return Success flag. If false, the (default) cancel action will abort.
     */
    protected boolean cancel() {
        // Nothing here, but used for optional overriding.
        return true;
    }

    @FXML
    protected void cancelAction(ActionEvent event) {
        if (cancel()) {
            setState(State.CANCELED);
            close();
        }
    }

    /**
     * Abstract method (default always return success) for saving the entry.
     * @return Success flag. If false, the (default) save action will abort.
     */
    abstract protected boolean save();

    @FXML
    protected void saveAction(ActionEvent event) {
        try {
            if (save()) {
                setState(this.mode == Mode.NEW ? State.NEW_COMMITTED : State.EDIT_COMMITTED);
                close();
            }
        }
        catch (RuntimeException exception) {
            reportExceptionNicely("Błąd podczas zapisywania", exception);
        }
    }

    /**
     * Abstract method (default always return success) for deleting the entry.
     * @return Success flag. If false, the (default) delete action will abort.
     */
    abstract protected boolean delete();

    @FXML
    protected void deleteAction(ActionEvent event) {
        try {
            if (delete()) {
                setState(State.DELETE_COMMITTED);
                close();
            }
        }
        catch (RuntimeException exception) {
            reportExceptionNicely("Błąd podczas usuwania", exception);
        }
    }
}

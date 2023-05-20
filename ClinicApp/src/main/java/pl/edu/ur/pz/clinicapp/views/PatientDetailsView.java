package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PatientDetailsView extends ChildControllerBase<MainWindowController> implements Initializable {

    /**
     * Available window modes (details of existing referral or creation of a new one).
     */
    public enum RefMode {DETAILS, CREATE};

    /**
     * Current view mode.
     */
    private ReferralDetailsView.RefMode currMode;

    private static BooleanProperty editState = new SimpleBooleanProperty(false);

    /**
     * Get current edit state (fields editable or non-editable).
     */
    public static boolean getEditState() {
        return editState.getValue();
    }

//    /**
//     * Set current edit state (fields editable or non-editable).
//     */
//    public static void setEditState(boolean editState) {
//        ReferralDetailsView.editState.set(editState);
//    }

    /**
     * Default dispose method.
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Displays alert about unsaved changes and returns whether user wants to discard them or not.
     */
    public static Boolean exitConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Niezapisane zmiany");
        alert.setHeaderText("Widok w trybie edycji");
        alert.setContentText("Czy na pewno chcesz opuścić ten widok? Wszystkie niezapisane zmiany zostaną utracone.");
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    /**
     * Checks if window is in edit state and accordingly displays alert and/or changes view to previous one.
     */
    public void onBackClick() {
        if (editState.getValue()) {
            if (exitConfirm()) {
                editState.setValue(!editState.getValue());
                this.getParentController().goBack();
            }
        } else {
            this.getParentController().goBack();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void populate(Object... context) {

    }

    @Override
    public void refresh() {

    }
}

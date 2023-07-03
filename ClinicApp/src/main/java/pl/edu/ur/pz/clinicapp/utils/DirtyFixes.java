package pl.edu.ur.pz.clinicapp.utils;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class DirtyFixes {
    /**
     * Fix for date picker always editable via button.
     * See <a href="https://bugs.openjdk.org/browse/JDK-8096254">JDK-8096254</a>.
     *
     * This method should be called when date picker skin is already created,
     * so you might want to delay it away after initialization.
     *
     * @param datePicker data picker to be fixed
     */
    public static void fixDatePickerAlwaysEditableViaButton(DatePicker datePicker) {
        // Prevent (fake) button from working
        Node arrowButton = datePicker.lookup("#arrow-button");
        final EventHandler<MouseEvent> arrowButtonEventFilter = event -> {
            if (!datePicker.isEditable()) {
                event.consume();
            }
        };
        arrowButton.addEventFilter(MouseEvent.MOUSE_ENTERED,  arrowButtonEventFilter);
        arrowButton.addEventFilter(MouseEvent.MOUSE_PRESSED,  arrowButtonEventFilter);
        arrowButton.addEventFilter(MouseEvent.MOUSE_RELEASED, arrowButtonEventFilter);
        arrowButton.addEventFilter(MouseEvent.MOUSE_EXITED,   arrowButtonEventFilter);
        // TODO: investigate more why ComboBoxBaseSkin::updateArrowButtonListeners
        //  is not removing the listeners in the first place...

        // Prevent enter key changing value when focused on non-editable field (by removing focus)
        datePicker.focusTraversableProperty().bind(datePicker.editableProperty());
        datePicker.focusedProperty().addListener((observable, oldValue, focused) -> {
            if (focused && !datePicker.isEditable()) {
                datePicker.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "\t", "", KeyCode.TAB,
                        false, false, false, false));
                datePicker.fireEvent(new KeyEvent(KeyEvent.KEY_RELEASED, "\t", "", KeyCode.TAB,
                        false, false, false, false));
            }
        });
        // TODO: Prevent enter key changing value when focused on non-editable field.
        //  ComboBoxPopupControl::setTextFromTextFieldIntoComboBoxValue calls updateDisplayNode.
        //  Cannot use event filter because ComboBoxPopupControl (that DatePickerSkin extends)
        //  already add it's own event filter first. Maybe customize DatePickerSkin?
    }
}

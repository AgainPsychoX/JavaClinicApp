package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
import org.jetbrains.annotations.Nullable;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.controls.LocalTimeSpinner;
import pl.edu.ur.pz.clinicapp.models.Timetable;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class TimetableEntryEditDialog extends BaseEditDialog {
    @FXML private ChoiceBox<DayOfWeek> dayChoiceBox;
    @FXML private LocalTimeSpinner startSpinner;
    @FXML private LocalTimeSpinner endSpinner;

    private Timetable.Entry entry;

    /**
     * Before saved, or if deleted: it keeps old entry (to remove it when adding new one).
     * After saved: holds newly created entry, to ease referencing it outside the dialog.
     * @return the entry
     */
    public Timetable.Entry getEntry() {
        return entry;
    }


    private final Timetable timetable;

    /**
     * @return Timetable to be affected, or null if detached from timetable.
     */
    @Nullable
    public Timetable getTimetable() {
        return timetable;
    }

    /**
     * Constructor for timetable entry related dialog.
     * @param entry Entry to affect, or null for creating new entry.
     * @param timetable Timetable to affect, or null if detached from timetable.
     */
    public TimetableEntryEditDialog(Timetable.Entry entry, Timetable timetable) {
        super(ClinicApplication.class.getResource("dialogs/TimetableEntryEditDialog.fxml"),
                entry == null ? Mode.NEW : Mode.EDIT);

        this.timetable = timetable;

        if (entry == null) {
            entry = new Timetable.Entry(DayOfWeek.MONDAY, 8 * 60, 16 * 60);
        }

        if (mode == Mode.NEW) {
            this.entry = null;
            setTitle("Dodawanie wpisu harmonogramu");
        }
        else {
            this.entry = entry; // keep to replace old entry when editing
            setTitle("Edytowanie wpisu harmonogramu");
        }

        dayChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(DayOfWeek object) {
                return object.getDisplayName(TextStyle.FULL, Locale.getDefault());
            }
            @Override
            public DayOfWeek fromString(String string) {
                return DayOfWeek.valueOf(string);
            }
        });
        dayChoiceBox.setItems(FXCollections.observableArrayList(DayOfWeek.values()));
        dayChoiceBox.setValue(entry.getDayOfWeek());

        startSpinner.getValueFactory().setValue(entry.getStartAsLocalTime());
        endSpinner.getValueFactory().setValue(entry.getEndAsLocalTime());
    }

    @Override
    protected boolean save() {
        var newEntry = new Timetable.Entry(dayChoiceBox.getValue(), startSpinner.getValue(), endSpinner.getValue());
        if (newEntry.getDurationMinutes() < 15) {
            final var dialog = new Alert(Alert.AlertType.WARNING);
            dialog.setTitle("Nieprawidłowe dane");
            dialog.setHeaderText(null);
            dialog.setContentText("Wybrana długość wpisu jest za krótka.");
            dialog.showAndWait();
            return false;
        }

        // Remove existing one and create new one to auto-merge entries if possible
        if (entry != null) {
            if (timetable != null) {
                timetable.remove(entry);
            }
        }
        entry = newEntry;
        if (timetable != null) {
            timetable.add(newEntry);
        }
        return true;
    }

    @Override
    protected boolean delete() {
        // No confirm: it's simple to just create new entry & the delete button is hidden away from the timetable view.
        if (timetable != null) {
            timetable.remove(entry);
        }
        return true;
    }
}

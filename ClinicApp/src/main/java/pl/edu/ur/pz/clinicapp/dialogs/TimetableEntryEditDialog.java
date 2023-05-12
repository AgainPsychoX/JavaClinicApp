package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
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

    private final Timetable.Entry oldEntry; // keep to replace old entry when editing
    private final Timetable timetable;

    /**
     * Shorthand for full constructor, for opening the dialog
     * for existing entry (timetable is deduced).
     * @param entry Entry to edit.
     */
    public TimetableEntryEditDialog(Timetable.Entry entry) {
        this(entry, entry.getTimetable());
    }

    /**
     * Constructor for timetable entry related dialog.
     * @param entry Entry to affect;
     *              or preset data for fields if timetable is null;
     *              or null for creating new entry.
     * @param timetable Timetable to affect.
     */
    public TimetableEntryEditDialog(Timetable.Entry entry, Timetable timetable) {
        super(ClinicApplication.class.getResource("dialogs/TimetableEntryEditDialog.fxml"),
                entry == null ? Mode.NEW : (entry.getTimetable() == null ? Mode.NEW : Mode.EDIT));

        if (timetable == null) {
            throw new IllegalArgumentException("Timetable must be provided when adding new entry");
        }
        this.timetable = timetable;

        if (entry == null) {
            //
            entry = new Timetable.Entry(DayOfWeek.MONDAY, 8 * 60, 16 * 60);
        }

        if (mode == Mode.NEW) {
            this.oldEntry = null;
            setTitle("Dodawanie wpisu harmonogramu");
        }
        else {
            this.oldEntry = entry; // keep to replace old entry when editing
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

        startSpinner.setPattern("HH:mm");
        startSpinner.getValueFactory().setValue(entry.startAsLocalTime());

        endSpinner.setPattern("HH:mm");
        endSpinner.getValueFactory().setValue(entry.startAsLocalTime());
    }

    @Override
    protected boolean save() {
        // Timetable entries are immutable (start minute is part of composite key),
        // so we remove existing one and create new one.
        if (oldEntry != null) {
            timetable.remove(oldEntry);
        }
        timetable.add(new Timetable.Entry(dayChoiceBox.getValue(), startSpinner.getValue(), endSpinner.getValue()));
        return true;
    }

    @Override
    protected boolean delete() {
        timetable.remove(oldEntry);
        return true;
    }
}

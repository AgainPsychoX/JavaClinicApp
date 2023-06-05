package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
import org.jetbrains.annotations.Nullable;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.controls.DateTimePicker;
import pl.edu.ur.pz.clinicapp.models.Schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static pl.edu.ur.pz.clinicapp.utils.JPAUtils.transaction;
import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.requireConfirmation;

public class ScheduleSimpleEntryEditDialog extends BaseEditDialog {
    @FXML private ChoiceBox<Schedule.Entry.Type> typeChoiceBox;
    @FXML private DateTimePicker beginDateTimePicker;
    @FXML private DateTimePicker endDateTimePicker;


    private final Schedule.SimpleEntry entry;

    /**
     * Before saved, or if deleted: it keeps old entry (to remove it when adding new one).
     * After saved: holds newly created entry, to ease referencing it outside the dialog.
     * @return the entry
     */
    public Schedule.SimpleEntry getEntry() {
        return entry;
    }


    private final Schedule schedule;

    /**
     * @return Schedule to be affected, or null if detached from timetable.
     */
    @Nullable
    public Schedule getSchedule() {
        return schedule;
    }


    /**
     * Shorthand constructor for editing schedule entry (related user schedule will be assumed).
     * @param entry Entry to affect.
     */
    public ScheduleSimpleEntryEditDialog(Schedule.SimpleEntry entry) {
        this(entry, Schedule.of(entry.getUser()));
    }

    /**
     * Constructor for schedule entry related dialog.
     * @param entry Entry to affect, or null for creating new entry.
     * @param schedule Schedule to affect.
     */
    public ScheduleSimpleEntryEditDialog(Schedule.SimpleEntry entry, Schedule schedule) {
        super(ClinicApplication.class.getResource("dialogs/ScheduleSimpleEntryEditDialog.fxml"),
                entry == null ? Mode.NEW : Mode.EDIT);

        if (schedule == null) throw new NullPointerException();
        this.schedule = schedule;

        if (mode == Mode.NEW) {
            this.entry = new Schedule.SimpleEntry();
            setTitle("Dodawanie wpisu terminarza");
        }
        else {
            this.entry = entry;
            setTitle("Edytowanie wpisu terminarza");
        }

        typeChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Schedule.Entry.Type object) {
                return object.localizedName(); // TODO: capitalize
            }
            @Override
            public Schedule.Entry.Type fromString(String string) {
                return Schedule.Entry.Type.valueOf(string);
            }
        });
        typeChoiceBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(Schedule.Entry.Type.values()).filter(this::shouldShowType).toList()));

        beginDateTimePicker.setEditable(false);
        beginDateTimePicker.setOnMouseClicked(event -> openDialogToPickBeginTime());
        endDateTimePicker.setEditable(false);
        endDateTimePicker.setOnMouseClicked(event -> openDialogToPickEndTime());

        // Default populate
        typeChoiceBox.setValue(entry != null ? entry.getType() : Schedule.Entry.Type.VACATION);
        beginDateTimePicker.setDateTimeValue(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        endDateTimePicker.setDateTimeValue(LocalDateTime.now().plusHours(1));
    }

    public void populate(Schedule.SimpleEntry entry) {
        typeChoiceBox.setValue(entry.getType());
        beginDateTimePicker.setDateTimeValue(entry.getBeginTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
        endDateTimePicker.setDateTimeValue(entry.getEndTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public void populate(LocalDateTime time) {
        beginDateTimePicker.setDateTimeValue(time);
        endDateTimePicker.setDateTimeValue(time.plusHours(1));
    }

    private boolean shouldShowType(Schedule.Entry.Type type) {
        return switch (type) {
            case CLOSED, VACATION, HOLIDAYS, SICK_LEAVE, EMERGENCY_LEAVE, EXTRA -> true;
            default -> false;
        };
    }

    protected void openDialogToPickBeginTime() {
        beginDateTimePicker.hide();
        // FIXME: Somewhere here bug happens, the edit dialog closes, schedule point picker remains, but bugged
        //  (UI refreshing only on window resize, wtf?). What is going on...?
        final var dialog = new SchedulePointPickerDialog(schedule, beginDateTimePicker.getDateTimeValue());
        dialog.setHeaderText("Wybierz początek czasu trwania");
        // TODO: show end time?
        dialog.showAndWait();
        dialog.getResultDateTime().ifPresent(dateTimeValue -> {
            beginDateTimePicker.setDateTimeValue(dateTimeValue);
            keepBeginEndSorted();
        });
    }

    protected void openDialogToPickEndTime() {
        // FIXME: fix begin dialog first
//        endDateTimePicker.hide();
//        final var dialog = new SchedulePointPickerDialog(schedule, endDateTimePicker.getDateTimeValue());
//        dialog.setHeaderText("Wybierz koniec czasu trwania");
//        // TODO: show end time?
//        dialog.showAndWait();
//        dialog.getResultDateTime().ifPresent(dateTimeValue -> {
//            endDateTimePicker.setDateTimeValue(dateTimeValue);
//            keepBeginEndSorted();
//        });
    }

    protected void keepBeginEndSorted() {
        // TODO: reorder if end < begin
    }

    protected Duration getDuration() {
        return Duration.between(beginDateTimePicker.getDateTimeValue(), endDateTimePicker.getDateTimeValue()).abs();
    }

    @Override
    protected boolean save() {
        if (getDuration().toMinutes() < 10) {
            final var dialog = new Alert(Alert.AlertType.WARNING);
            dialog.setTitle("Nieprawidłowe dane");
            dialog.setHeaderText(null);
            dialog.setContentText("Wybrana długość wpisu jest za krótka.");
            dialog.showAndWait();
            return false;
        }

        entry.setType(typeChoiceBox.getValue());
        entry.setBeginTime(beginDateTimePicker.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant());
        entry.setEndTime(endDateTimePicker.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant());
        entry.setUser(schedule.getUser());

        transaction(em -> em.persist(entry));
        return true;
    }

    @Override
    protected boolean delete() {
        if (getDuration().toHours() > 10) {
            if (!requireConfirmation("Potwierdzenie usunięcia", "Dany", ButtonType.CANCEL)) {
                return false;
            }
        }

        transaction(em -> em.remove(entry));
        return true;
    }
}

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
import java.time.ZonedDateTime;
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
            final var aboutNow = ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1);
            this.entry = new Schedule.SimpleEntry(
                    Schedule.Entry.Type.VACATION,
                    aboutNow.toInstant(),
                    aboutNow.plusHours(1).toInstant());
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
        beginDateTimePicker.setOnMouseClicked(event -> openDialogToPickTimeRange());
        endDateTimePicker.setOnKeyPressed(event -> {
            event.consume();
            beginDateTimePicker.hide();
            openDialogToPickTimeRange();
        });

        endDateTimePicker.setEditable(false);
        endDateTimePicker.setOnMouseClicked(event -> openDialogToPickTimeRange());
        endDateTimePicker.setOnKeyPressed(event -> {
            event.consume();
            endDateTimePicker.hide();
            openDialogToPickTimeRange();
        });

        assert this.entry != null; // should be always true
        populate(this.entry);
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

    protected void openDialogToPickTimeRange() {
        final var dialog = new ScheduleSlotPickerDialog(
                schedule, beginDateTimePicker.getDateTimeValue(), endDateTimePicker.getDateTimeValue());
        dialog.showAndWait();
        dialog.getResult().ifPresent(entry -> {
            beginDateTimePicker.setDateTimeValue(entry.getBeginTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
            endDateTimePicker.setDateTimeValue(entry.getEndTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
        });
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

        transaction(em -> {
            entry.setType(typeChoiceBox.getValue());
            entry.setBeginTime(beginDateTimePicker.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant());
            entry.setEndTime(endDateTimePicker.getDateTimeValue().atZone(ZoneId.systemDefault()).toInstant());
            entry.setUser(schedule.getUserReference().asUser());

            if (entry.getId() == null) {
                em.persist(entry);
            }
        });
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

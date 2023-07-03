package pl.edu.ur.pz.clinicapp.controls;

import javafx.scene.text.TextAlignment;
import pl.edu.ur.pz.clinicapp.models.Appointment;
import pl.edu.ur.pz.clinicapp.models.Schedule;

import java.time.ZoneId;

/**
 * Cell for displaying schedule entries on week pane.
 */
public class WeekPaneScheduleEntryCell<T extends WeekPane.Entry> extends WeekPane.EntryCell<T> {
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().retainAll("cell", "entry");

        if (empty || item == null) {
            setText("?");
        }
        else {
            if (item instanceof Schedule.ScheduleWeekPaneEntry proxy) {
                final var scheduleEntry = proxy.getScheduleEntry();

                // TODO: if isTall & isWide -> 8:00 - 8:15 (15 minut)\nMarin Kowalski ?
                if (scheduleEntry instanceof Appointment appointment) {
                    final var patient = appointment.getPatient();
                    final var beginDateTime = appointment.getBeginInstant().atZone(ZoneId.systemDefault());
                    setTextAlignment(TextAlignment.LEFT);
                    getStyleClass().add("appointment");
                    setText("%s %s. %s".formatted(
                            beginDateTime.toLocalTime().toString().replaceFirst("^0+(?!$)", ""),
                            patient.getName().charAt(0), patient.getSurname()
                    ));
                } else if (scheduleEntry instanceof Schedule.SimpleEntry simpleEntry) {
                    setTextAlignment(TextAlignment.CENTER);
                    if (simpleEntry.getType() == Schedule.Entry.Type.APPOINTMENT) {
                        // If it's simple entry appointment, it means user (most likely patient)
                        // doesn't have permissions to know about details of not-theirs appointment.
                        setText("(inna wizyta)");
                        getStyleClass().addAll("appointment", "other");
                    } else {
                        setText("(" + simpleEntry.getType().localizedName() + ")");
                        getStyleClass().add(simpleEntry.getType().name().toLowerCase());
                    }
                }

                if (scheduleEntry.getDuration().toMinutes() < 15) {
                    getStyleClass().add("small");
                }
            }
        }
    }
}

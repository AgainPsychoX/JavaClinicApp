package pl.edu.ur.pz.clinicapp.controls;

import javafx.scene.text.TextAlignment;
import pl.edu.ur.pz.clinicapp.models.Appointment;
import pl.edu.ur.pz.clinicapp.models.Schedule;

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
            // TODO: if isTall & isWide -> 8:00 - 8:15 (15 minut)\nMarin Kowalski ?
            if (item instanceof Appointment appointment) {
                final var patient = appointment.getPatient();
                setTextAlignment(TextAlignment.LEFT);
                getStyleClass().add("appointment");
                setText("%s %s. %s".formatted(
                        appointment.getStartAsLocalTime().toString().replaceFirst("^0+(?!$)", ""),
                        patient.getName().charAt(0), patient.getSurname()
                ));
                return;
            }

            Schedule.Entry original = null;
            if (item instanceof Schedule.ProxyWeekPaneEntry proxy) {
                original = proxy.getOriginal();
            } else if (item instanceof Schedule.Entry entry) {
                original = entry;
            }
            if (original != null) {
                setTextAlignment(TextAlignment.CENTER);
                if (original.getType() == Schedule.Entry.Type.APPOINTMENT) {
                    // If it's simple entry appointment, it means user (most likely patient)
                    // doesn't have permissions to know about details of not-theirs appointment.
                    setText("(inna wizyta)");
                    getStyleClass().addAll("appointment", "other");
                } else {
                    setText("(" + original.getType().localizedName() + ")");
                    getStyleClass().add(original.getType().name().toLowerCase());
                }
                return;
            }

            assert false;
        }
    }
}

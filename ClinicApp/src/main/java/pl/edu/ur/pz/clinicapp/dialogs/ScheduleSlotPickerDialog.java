package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.controls.LocalTimeSpinner;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;
import pl.edu.ur.pz.clinicapp.controls.WeekPaneFreeSelectionModel;
import pl.edu.ur.pz.clinicapp.controls.WeekPaneScheduleEntryCell;
import pl.edu.ur.pz.clinicapp.models.Doctor;
import pl.edu.ur.pz.clinicapp.models.Schedule;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.linkStageSizeToPane;
import static pl.edu.ur.pz.clinicapp.utils.TemporalUtils.alignDateToWeekStart;

/**
 * Dialog for picking slot on schedule (i.e. for appointments).
 */
public class ScheduleSlotPickerDialog extends Stage {
    private static final Logger logger = Logger.getLogger(ScheduleSlotPickerDialog.class.getName());

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Elements and initialization
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML protected Text headerText;
    @FXML protected DatePicker datePicker;
    @FXML protected Button previousWeekButton;
    @FXML protected Button nextWeekButton;

    @FXML protected LocalTimeSpinner startTimeSpinner;
    @FXML protected LocalTimeSpinner endTimeSpinner;

    @FXML protected WeekPane<WeekPane.Entry> weekPane;
    protected WeekPaneFreeSelectionModel<WeekPane.Entry> weekPaneSelectionModel;

    @FXML protected Text extraTextBelow;
    @FXML protected Button acceptButton;
    @FXML protected Button cancelButton;

    final protected BorderPane pane;

    private static Duration getDefaultDuration(Schedule schedule) {
        if (schedule.getUserReference() instanceof Doctor doctor) {
            return doctor.getDefaultVisitDuration();
        }
        return Duration.ofMinutes(15);
    }

    public ScheduleSlotPickerDialog(Schedule schedule) {
        this(schedule, null);
    }

    public ScheduleSlotPickerDialog(Schedule schedule, LocalDateTime dateTime) {
        this(schedule, dateTime, null);
    }

    public ScheduleSlotPickerDialog(Schedule schedule, LocalDateTime dateTime, Duration duration) {
        var fxml = ClinicApplication.class.getResource("dialogs/ScheduleSlotPickerDialog.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setController(this);

        try {
            pane = fxmlLoader.load();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        initModality(Modality.APPLICATION_MODAL);
        linkStageSizeToPane(this, pane);
        setScene(new Scene(pane));
        setTitle("Wybór miejsca w terminarzu");

        if (dateTime == null) dateTime = LocalDateTime.now();
        if (duration == null) duration = getDefaultDuration(schedule);

        this.schedule = schedule;

        /* Changes to start/end time spinners should be propagated to free selection model and vice versa.
         * There is no binding possible, as selection model properties are read-only, so listeners are used.
         * Far away from my best piece of code... :Aware.gif:
         *
         * TODO: remember selection despite switching weeks? (and maybe refactor)
         * TODO: add some accessibility (keyboard) controls to move the week pane free selection
         */

        weekPaneSelectionModel = new WeekPaneFreeSelectionModel<>(weekPane);
        weekPane.setEntryCellFactory(weekPane -> new WeekPaneScheduleEntryCell<>());
        weekPaneSelectionModel.selectedDayOfWeekProperty().addListener((o, oldDayOfWeek, newDayOfWeek) -> {
            logger.finer("Selected day of week: " + newDayOfWeek);
            if (newDayOfWeek == null) {
                acceptButton.setDisable(false);
                return;
            }
            datePicker.setValue(alignDateToWeekStart(getDate()).plusDays(newDayOfWeek.ordinal()));
            updateAcceptButton();
        });
        weekPaneSelectionModel.selectedTimeOfDayProperty().addListener((o, oldTimeOfDay, newTimeOfDay) -> {
            logger.finer("Selected time of day: " + newTimeOfDay);
            if (newTimeOfDay != null) {
                startTimeSpinner.getValueFactory().setValue(newTimeOfDay);
            }
            updateAcceptButton();
        });

        // TODO: allow selection of all hours, work needed in week pane code...
        final var weekPaneStartTime = weekPane.getRowGenerationParams().startTimeOfDay();
        var time = dateTime.toLocalTime();
        if (time.isBefore(weekPaneStartTime)) {
            time = weekPaneStartTime;
        }

        startTimeSpinner.getValueFactory().setValue(time);
        startTimeSpinner.valueProperty().addListener((o, oldValue, newValue) -> {
            logger.finer("Start time spinner: " + newValue);

            // TODO: allow selection of all hours, work needed in week pane code...
            if (newValue.isBefore(weekPaneStartTime)) {
                // Prevent change
                logger.finer("Prevent change, back to: " + oldValue);
                startTimeSpinner.getValueFactory().setValue(oldValue);
                return;
            }

            /* Weird thing happens here. Everything seems to work finer, but once you add debugger breakpoint
             * the spinner keeps being updated, firing change events as it was held.
             */
            weekPaneSelectionModel.select(getDate().getDayOfWeek(), newValue);

            final var d = Duration.between(oldValue, endTimeSpinner.getValue()).abs();
            endTimeSpinner.getValueFactory().setValue(newValue.plus(d));
        });
        endTimeSpinner.getValueFactory().setValue(time.plus(duration));
        endTimeSpinner.valueProperty().addListener((o, oldValue, newValue) -> {
            logger.finer("End time spinner: " + newValue);
            updateSelectorHeight();
        });
        // TODO: add minValueProperty and maxValueProperty in LocalTimeSpinner to prevent reordering here

        datePicker.valueProperty().addListener((o, oldValue, newValue) -> {
            final var newWeekStart = alignDateToWeekStart(newValue);

            boolean sameWeek = false;
            if (oldValue != null) {
                final var oldWeekStart = alignDateToWeekStart(oldValue);
                if (oldWeekStart.isEqual(newWeekStart)) {
                    sameWeek = true;
                }
            }

            if (sameWeek) {
                logger.finer("Date picker (same week): " + newValue);
                weekPaneSelectionModel.select(newValue.getDayOfWeek(), startTimeSpinner.getValue());
            }
            else {
                logger.finer("Date picker (new week): " + newValue);
                weekPane.setEntries(schedule.generateWeekPaneEntriesForSchedule(newWeekStart));
                weekPaneSelectionModel.clearSelection();
            }
        });
        datePicker.setValue(dateTime.toLocalDate());
        weekPaneSelectionModel.select(dateTime.getDayOfWeek(), time);
        updateSelectorHeight();
    }

    private void updateSelectorHeight() {
        final var rgp = weekPane.getRowGenerationParams();
        final var height = rgp.calculateEntryHeight(Math.max(getDuration().toMinutes(), 1));
        weekPaneSelectionModel.selector.setPrefHeight(height);
    }

    public void setHeaderText(String text) {
        this.headerText.setText(text);
    }

    public void setExtraTextBelow(String text) {
        this.extraTextBelow.setText(text);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * State
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    final protected Schedule schedule;

    protected LocalDate getDate() {
        return datePicker.getValue();
    }

    protected LocalDateTime getDateTime() {
        return getDate().atTime(startTimeSpinner.getValue());
    }

    protected Duration getDuration() {
        return Duration.between(endTimeSpinner.getValue(), startTimeSpinner.getValue()).abs();
    }

    protected LocalDateTime resultDateTime;
    protected Duration resultDuration;

    /**
     * @return selected slot date time, empty if cancelled.
     */
    public Optional<LocalDateTime> getResultDateTime() {
        return Optional.ofNullable(resultDateTime);
    }

    /**
     * @return selected slot duration, empty if cancelled.
     */
    public Optional<Duration> getResultDuration() {
        return Optional.ofNullable(resultDuration);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Action handlers (other than listeners)
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    protected void updateAcceptButton() {
        acceptButton.setDisable(!validateSelection());
    }

    protected boolean validateSelection() {
        final var startTime = weekPaneSelectionModel.getSelectedTimeOfDay();
        if (startTime == null) {
            return false;
        }
        final var endTime = startTime.plus(getDuration());

        // TODO: checkbox to force selection? only for admin/owner (doctor)

        for (final var entry : weekPane.getEntries()) {
            if (entry.getDayOfWeek() == getDate().getDayOfWeek()
                    && entry.getEndAsLocalTime().isAfter(startTime)
                    && !entry.getStartAsLocalTime().isAfter(endTime)) {
                Schedule.Entry original;
                if (entry instanceof Schedule.ProxyWeekPaneEntry proxy) {
                    original = proxy.getOriginal();
                } else if (entry instanceof Schedule.Entry simple) {
                    original = simple;
                } else {
                    // Should never happen...?
                    assert false;
                    return false;
                }

                if (original.getType().isBusy()) {
                    logger.finest("Validation failed, overlapping with " + original);
                    return false;
                }
            }
        }

        logger.finest("Validation succeed");
        return true;
    }

    @FXML
    protected void datePickerAction(ActionEvent actionEvent) {
        // Not used, logic handled by listener as old value was needed too
    }

    @FXML
    protected void goPreviousWeekAction(ActionEvent actionEvent) {
        datePicker.setValue(getDate().minusDays(7)); // will also cause `datePickerAction`
    }

    @FXML
    protected void goNextWeekAction(ActionEvent actionEvent) {
        datePicker.setValue(getDate().plusDays(7)); // will also cause `datePickerAction`
    }

    @FXML
    void acceptAction(ActionEvent event) {
        resultDateTime = getDateTime();
        resultDuration = getDuration();
        close();
    }

    @FXML
    void cancelAction(ActionEvent event) {
        close();
    }
}

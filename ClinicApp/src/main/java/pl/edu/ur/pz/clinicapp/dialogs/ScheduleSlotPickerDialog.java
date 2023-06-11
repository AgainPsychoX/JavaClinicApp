package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.beans.value.ChangeListener;
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
import pl.edu.ur.pz.clinicapp.controls.WeekPaneScheduleEntryCell;
import pl.edu.ur.pz.clinicapp.models.Doctor;
import pl.edu.ur.pz.clinicapp.models.Schedule;
import pl.edu.ur.pz.clinicapp.utils.InteractionGuard;
import pl.edu.ur.pz.clinicapp.utils.javafx.Debouncer;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
    @FXML protected DatePicker beginDatePicker;
    @FXML protected LocalTimeSpinner beginTimeSpinner;

    @FXML protected DatePicker endDatePicker;
    @FXML protected LocalTimeSpinner endTimeSpinner;

    @FXML protected WeekPane<WeekPane.Entry> weekPane;

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
        this(schedule, LocalDateTime.now());
    }

    public ScheduleSlotPickerDialog(Schedule schedule, LocalDateTime dateTime) {
        this(schedule, dateTime, getDefaultDuration(schedule));
    }

    public ScheduleSlotPickerDialog(Schedule schedule, LocalDateTime beginDateTime, Duration duration) {
        this(schedule, beginDateTime, beginDateTime.plus(duration));
    }

    public ScheduleSlotPickerDialog(Schedule schedule, LocalDateTime beginDateTime, LocalDateTime endDateTime) {
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

        this.schedule = schedule;

        weekPane.setEntryCellFactory(weekPane -> new WeekPaneScheduleEntryCell<>() {
            @Override
            public void updateItem(WeekPane.Entry item, boolean empty) {
                super.updateItem(item, empty);

                Schedule.Entry original = null;
                if (item instanceof Schedule.ProxyWeekPaneEntry proxy) {
                    original = proxy.getOriginal();
                } else if (item instanceof Schedule.Entry entry) {
                    original = entry;
                }
                if (original instanceof SelectionEntry) {
                    getStyleClass().add("selection");
                    setText("");
                }
            }
        });

        // TODO: allow mouse controls
        //  + click = expand/cut closest direction if above 4 hours, or move if equal/shorter than 4 hours;
        //  + SHIFT to always expand/cut, CTRL to always move
        // TODO: add some accessibility (keyboard) controls to move the week pane free selection
        //  + arrows + modifiers: SHIFT expand, ALT cut, CTRL move.

        beginTimeSpinner.getValueFactory().setValue(beginDateTime.toLocalTime());
        beginTimeSpinner.valueProperty().addListener(beginTimeListener);

        endTimeSpinner.getValueFactory().setValue(endDateTime.toLocalTime());
        endTimeSpinner.valueProperty().addListener(endTimeListener);

        final ChangeListener<? super Boolean> beginFocusListener = (observable, wasFocused, isFocused) -> {
            if (isFocused) {
                showWeek(beginDatePicker.getValue());
            }
        };
        beginDatePicker.focusedProperty().addListener(beginFocusListener);
        beginTimeSpinner.focusedProperty().addListener(beginFocusListener);

        final ChangeListener<? super Boolean> endFocusListener = (observable, wasFocused, isFocused) -> {
            if (isFocused) {
                showWeek(endDatePicker.getValue());
            }
        };
        endDatePicker.focusedProperty().addListener(endFocusListener);
        endTimeSpinner.focusedProperty().addListener(endFocusListener);

        beginDatePicker.setValue(beginDateTime.toLocalDate());
        endDatePicker.setValue(endDateTime.toLocalDate());

        showWeek(beginDateTime.toLocalDate());
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

    private LocalDate currentWeekStart = LocalDate.ofEpochDay(0);

    protected LocalDate getCurrentWeekStart() {
        return currentWeekStart;
    }

    final protected Schedule.Entry selectionScheduleEntry = new SelectionEntry();

    private class SelectionEntry implements Schedule.Entry {
        @Override
        public Type getType() {
            return Type.NONE;
        }

        @Override
        public Instant getBeginTime() {
            return getBeginDateTime().atZone(ZoneId.systemDefault()).toInstant();
        }

        @Override
        public int getStartMinute() {
            return beginTimeSpinner.getValue().toSecondOfDay() / 60;
        }

        @Override
        public Instant getEndTime() {
            return getEndDateTime().atZone(ZoneId.systemDefault()).toInstant();
        }

        @Override
        public int getEndMinute() {
            if (beginDatePicker.getValue().equals(endDatePicker.getValue())) {
                return endTimeSpinner.getValue().toSecondOfDay() / 60;
            }
            else {
                return 1440;
            }
        }
    }

    protected LocalDateTime getBeginDateTime() {
        return beginDatePicker.getValue().atTime(beginTimeSpinner.getValue());
    }

    protected LocalDateTime getEndDateTime() {
        return endDatePicker.getValue().atTime(endTimeSpinner.getValue());
    }

    /**
     * List of {@link WeekPane.Entry}ies currently used to represent the selected range on the week pane.
     * Might include one original {@link Schedule.Entry} and many {@link Schedule.ProxyWeekPaneEntry}ies.
     * Should be kept in natural order.
     */
    private List<WeekPane.Entry> selectionWeekPaneEntries;

    protected void showWeek(LocalDate weekStart) {
        weekStart = alignDateToWeekStart(weekStart);

        if (currentWeekStart.isEqual(weekStart)) {
            logger.finest("Showing the same week, no need to (re)generate week pane entries.");
        }
        else {
            logger.finer("Showing different week: " + weekStart);
            currentWeekStart = weekStart;
            final var entries = schedule.generateWeekPaneEntriesForSchedule(weekStart);
            selectionWeekPaneEntries = schedule.generateWeekPaneEntriesForScheduleEntries(
                    weekStart, List.of(selectionScheduleEntry));
            entries.addAll(selectionWeekPaneEntries);
            weekPane.setEntries(entries);
        }
    }

    protected void refreshWeekPaneAfterDateChanges() {
        // Change in dates means there are new week pane entries need to be added, or some need to be removed,
        // so let's regenerate all of them.
        logger.finest("Refreshing week pane after date changes");
        weekPane.getEntries().removeAll(selectionWeekPaneEntries);
        selectionWeekPaneEntries = schedule.generateWeekPaneEntriesForScheduleEntries(
                getCurrentWeekStart(), List.of(selectionScheduleEntry));
        weekPane.getEntries().addAll(selectionWeekPaneEntries);
    }

    /**
     * Performs reordering of begin & end - date pickers and time spinners if necessary.
     * @return true if reordering was needed (refreshing week pane already performed after reordering),
     *         false otherwise (refreshing week pane might be required).
     */
    protected boolean reorderIfNecessary() {
        final var endDateTime = getEndDateTime();
        final var beginDateTime = getBeginDateTime();

        if (beginDateTime.isAfter(endDateTime)) {
            logger.finer("Reordering");
            beginTimeSpinner.getValueFactory().setValue(endDateTime.toLocalTime());
            beginDatePicker.setValue(endDateTime.toLocalDate());
            endTimeSpinner.getValueFactory().setValue(beginDateTime.toLocalTime());
            endDatePicker.setValue(beginDateTime.toLocalDate());
            refreshWeekPaneAfterDateChanges();
            return true;
        } else {
            return false;
        }
    }

    protected Schedule.Entry result;

    /**
     * @return selected area in the schedule as {@link Schedule.Entry} (type: {@link Schedule.Entry.Type#NONE})
     */
    public Optional<Schedule.Entry> getResult() {
        return Optional.ofNullable(result);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Action handlers
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private final InteractionGuard interactionGuard = new InteractionGuard();

    final protected ChangeListener<LocalTime> beginTimeListener = (o, oldValue, newValue) -> {
        if (interactionGuard.begin()) return;
        logger.finer("Begin time changed: " + newValue);
        // TODO: roll over the date if overflow
        if (!reorderIfNecessary()) {
            logger.finest("Refreshing first entry");
            weekPane.refreshEntry(selectionScheduleEntry);
        }
        updateAcceptButtonDebounced();
        interactionGuard.end();
    };

    final protected ChangeListener<LocalTime> endTimeListener = (o, oldValue, newValue) -> {
        if (interactionGuard.begin()) return;
        logger.finer("End time changed: " + newValue);
        // TODO: roll back the date if underflow
        if (!reorderIfNecessary()) {
            logger.finest("Refreshing last entry");
            final var last = selectionWeekPaneEntries.get(selectionWeekPaneEntries.size() - 1);
            if (last instanceof Schedule.ProxyWeekPaneEntry proxy) {
                proxy.endMinute = newValue.toSecondOfDay() / 60;
            }
            weekPane.refreshEntry(last);
        }
        updateAcceptButtonDebounced();
        interactionGuard.end();
    };

    @FXML
    protected void beginDatePickerAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;
        final var newDateTime = getBeginDateTime();
        logger.finer("Begin date changed: " + newDateTime);
        if (!reorderIfNecessary()) {
            refreshWeekPaneAfterDateChanges();
        }
        updateAcceptButtonDebounced();
        interactionGuard.end();
    }

    @FXML
    protected void endDatePickerAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;
        final var newDateTime = getEndDateTime();
        logger.finer("End date changed: " + newDateTime);
        if (!reorderIfNecessary()) {
            refreshWeekPaneAfterDateChanges();
        }
        updateAcceptButtonDebounced();
        interactionGuard.end();
    }

    final private Debouncer acceptButtonUpdateDebouncer = new Debouncer(this::updateAcceptButton);

    protected void updateAcceptButtonDebounced() {
        acceptButtonUpdateDebouncer.call();
    }

    protected void updateAcceptButton() {
        acceptButton.setDisable(!validate());
    }

    /**
     * Helper method to find overlapping entries from currently selected (loaded) week for early validation.
     * To be used for early validation to avoid running queries.
     * @return stream of the overlapping entries from currently selected week
     */
    protected Stream<Schedule.Entry> getEarlyOverlapping() {
        return weekPane.getEntries().stream().map((Function<WeekPane.Entry, Optional<Schedule.Entry>>) entry -> {
            Schedule.Entry original;
            if (entry instanceof Schedule.ProxyWeekPaneEntry proxy) {
                original = proxy.getOriginal();
            } else if (entry instanceof Schedule.SimpleEntry simple) {
                original = simple;
            } else {
                assert entry == selectionScheduleEntry;
                return Optional.empty();
            }

            if (selectionScheduleEntry.overlaps(original)) {
                return Optional.of(original);
            } else {
                return Optional.empty();
            }
        }).flatMap(Optional::stream);
    }

    protected boolean validate() {
        return true;
    }

    @FXML
    void acceptAction(ActionEvent event) {
        result = selectionScheduleEntry;
        close();
    }

    @FXML
    void cancelAction(ActionEvent event) {
        close();
    }
}

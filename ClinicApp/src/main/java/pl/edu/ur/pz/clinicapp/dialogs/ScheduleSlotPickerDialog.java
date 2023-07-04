package pl.edu.ur.pz.clinicapp.dialogs;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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

                if (item instanceof Schedule.ScheduleWeekPaneEntry proxy) {
                    final var scheduleEntry = proxy.getScheduleEntry();
                    if (scheduleEntry instanceof SelectionEntry) {
                        setTextAlignment(TextAlignment.CENTER);
                        getStyleClass().add("selection");
                        setText("");
                    }
                }
            }
        });
        weekPane.getGrid().setOnMouseClicked(mouseClickedEventHandler);

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
        updateAcceptButton();
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
        public Instant getBeginInstant() {
            return getBeginDateTime().atZone(ZoneId.systemDefault()).toInstant();
        }

        @Override
        public Instant getEndInstant() {
            return getEndDateTime().atZone(ZoneId.systemDefault()).toInstant();
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
     * Should be kept in natural order.
     */
    private List<Schedule.ScheduleWeekPaneEntry> selectionWeekPaneEntries;

    protected void showWeek(LocalDate weekStart) {
        weekStart = alignDateToWeekStart(weekStart);

        if (currentWeekStart.isEqual(weekStart)) {
            logger.finest("Showing the same week, no need to (re)generate week pane entries.");
        }
        else {
            logger.finer("Showing different week: " + weekStart);
            currentWeekStart = weekStart;
            final var entries = schedule.generateWeekPaneEntriesForSchedule(weekStart);
            selectionWeekPaneEntries = Schedule.generateWeekPaneEntriesForScheduleEntries(
                    weekStart, List.of(selectionScheduleEntry));
            entries.addAll(selectionWeekPaneEntries);
            weekPane.setEntries(entries);
            weekPane.displayDatesInHeader(weekStart);
        }
    }

    /**
     * Refreshes all week pane entries after date changes by regenerating all of them,
     * as change in dates means there are new week pane entries need to be added or some need to be removed.
     */
    protected void refreshAllWeekPaneEntriesAfterDateChanges() {
        logger.finest("Refreshing week pane after date changes");
        weekPane.getEntries().removeAll(selectionWeekPaneEntries);
        selectionWeekPaneEntries = Schedule.generateWeekPaneEntriesForScheduleEntries(
                getCurrentWeekStart(), List.of(selectionScheduleEntry));
        weekPane.getEntries().addAll(selectionWeekPaneEntries);
    }

    /**
     * Refreshes the first week pane entry, to be used if only begin time changes without the dates.
     */
    protected void refreshFirstWeekPaneEntry() {
        logger.finest("Refreshing first entry");
        final var first = selectionWeekPaneEntries.get(0);
        first.startMinute = beginTimeSpinner.getValue().toSecondOfDay() / 60;
        weekPane.refreshEntry(first);
    }

    /**
     * Refreshes the last week pane entry, to be used if only end time changes without the dates.
     */
    protected void refreshLastWeekPaneEntry() {
        logger.finest("Refreshing last entry");
        final var last = selectionWeekPaneEntries.get(selectionWeekPaneEntries.size() - 1);
        last.endMinute = endTimeSpinner.getValue().toSecondOfDay() / 60;
        weekPane.refreshEntry(last);
    }

    /**
     * Performs reordering of begin and end - date pickers and time spinners if necessary.
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
            refreshAllWeekPaneEntriesAfterDateChanges();
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

    protected ChangeListener<LocalTime> beginTimeListener = (o, oldValue, newValue) -> {
        if (interactionGuard.begin()) return;
        logger.finer("Begin time changed: " + newValue);
        // TODO: roll over the date if overflow
        if (!reorderIfNecessary()) {
            refreshFirstWeekPaneEntry();
        }
        updateAcceptButtonDebounced();
        interactionGuard.end();
    };

    protected ChangeListener<LocalTime> endTimeListener = (o, oldValue, newValue) -> {
        if (interactionGuard.begin()) return;
        logger.finer("End time changed: " + newValue);
        // TODO: roll back the date if underflow
        if (!reorderIfNecessary()) {
            refreshLastWeekPaneEntry();
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
            refreshAllWeekPaneEntriesAfterDateChanges();
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
            refreshAllWeekPaneEntriesAfterDateChanges();
        }
        updateAcceptButtonDebounced();
        interactionGuard.end();
    }

    protected EventHandler<MouseEvent> mouseClickedEventHandler = event -> {
        // TODO: allow mouse controls
        //  + click = expand/cut closest direction if above 4 hours, or move if equal/shorter than 4 hours;
        //  + SHIFT to always expand/cut, CTRL to always move
        if (event.getButton() == MouseButton.PRIMARY) {
            final var dayOfWeek = weekPane.findDayOfWeekForMouseEvent(event);
            final var clickedDate = getCurrentWeekStart().plusDays(dayOfWeek.ordinal());
            final var minuteOfDay = weekPane.findVagueMinuteOfDayForMouseEvent(event);
            final var clickedTime = LocalTime.ofSecondOfDay(minuteOfDay * 60L);

            if (interactionGuard.begin()) return;

            final var oldBeginDate = beginDatePicker.getValue();
            final var oldEndDate = endDatePicker.getValue();
            final var oldDuration = selectionScheduleEntry.getDuration();

            if (!event.isControlDown() && oldDuration.toHours() > 4 || event.isShiftDown()) {
                final var clickedDateTime = clickedDate.atTime(clickedTime);

                // Select affected edge as closest to clicked minute
                boolean useBeginEdge;
                final var beginDateTime = getBeginDateTime();
                if (clickedDateTime.isBefore(beginDateTime)) {
                    useBeginEdge = true;
                } else {
                    final var endDateTime = getEndDateTime();
                    if (clickedDateTime.isAfter(endDateTime)) {
                        useBeginEdge = false;
                    } else {
                        final var toBegin = Duration.between(getBeginDateTime(), clickedDateTime).abs().toMinutes();
                        final var toEnd = Duration.between(getEndDateTime(), clickedDateTime).abs().toMinutes();
                        useBeginEdge = toBegin < toEnd;
                    }
                }

                // Flip affected edge if Alt pressed
                if (event.isAltDown()) {
                    useBeginEdge = !useBeginEdge;
                }

                if (useBeginEdge) {
                    logger.finer("Mouse clicked, moving begin to: " + clickedDate + " " + clickedTime);
                    beginDatePicker.setValue(clickedDate);
                    beginTimeSpinner.getValueFactory().setValue(clickedTime);
                } else {
                    logger.finer("Mouse clicked, moving end to: " + clickedDate + " " + clickedTime);
                    endDatePicker.setValue(clickedDate);
                    endTimeSpinner.getValueFactory().setValue(clickedTime);
                }
            }
            else {
                logger.finer("Mouse clicked, moving whole entry to start at: " + clickedDate + " " + clickedTime);
                // Move the selection, preserve the duration
                beginDatePicker.setValue(clickedDate);
                beginTimeSpinner.getValueFactory().setValue(clickedTime);
                final var newEndDateTime = clickedDate.atTime(clickedTime).plus(oldDuration);
                endDatePicker.setValue(newEndDateTime.toLocalDate());
                endTimeSpinner.getValueFactory().setValue(newEndDateTime.toLocalTime());
            }

            if (!reorderIfNecessary()) {
                if (!oldBeginDate.equals(beginDatePicker.getValue())
                        || !oldEndDate.equals(endDatePicker.getValue())) {
                    refreshAllWeekPaneEntriesAfterDateChanges();
                } else {
                    refreshFirstWeekPaneEntry();
                    refreshLastWeekPaneEntry();
                }
            }
            updateAcceptButtonDebounced();

            interactionGuard.end();
        }
    };

    final private Debouncer acceptButtonUpdateDebouncer = new Debouncer(this::updateAcceptButton);

    protected void updateAcceptButtonDebounced() {
        acceptButton.setDisable(true); // avoid being enabled when waiting for debouncer
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
        return weekPane.getEntries().stream().map((Function<WeekPane.Entry, Optional<Schedule.Entry>>) weekPaneEntry -> {
            if (weekPaneEntry instanceof Schedule.ScheduleWeekPaneEntry proxy) {
                Schedule.Entry scheduleEntry = proxy.getScheduleEntry();

                if (selectionScheduleEntry != scheduleEntry && selectionScheduleEntry.overlaps(scheduleEntry)) {
                    return Optional.of(scheduleEntry);
                } else {
                    return Optional.empty();
                }
            } else {
                assert false;
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

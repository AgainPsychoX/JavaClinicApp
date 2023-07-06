package pl.edu.ur.pz.clinicapp.controls;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

/**
 * Class responsible for managing selection inside week pane, with ability to free selection of minute of week.
 */
public class WeekPaneFreeSelectionModel<T extends WeekPane.Entry> extends WeekPaneSelectionModel<T> {
    final public Region selector;

    // TODO: animate the selector somehow? https://stackoverflow.com/questions/17676274/how-to-make-an-animation-with-css-in-javafx

    static private Region createDefaultSelector() {
        final var selectorHeight = 20;
        final var selector = new Region();
        selector.getStyleClass().add("free-selector");
        selector.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        selector.setPrefSize(WeekPane.DEFAULT_DAY_COLUMN_WIDTH, selectorHeight);
        selector.setMaxSize(Double.MAX_VALUE, USE_PREF_SIZE);
        GridPane.setValignment(selector, VPos.TOP);
        return selector;
    }

    protected void setShowingSelector(boolean show) {
        selector.setVisible(show);
        selector.setManaged(show);
        selector.toFront();
    }

    protected void updateFreeSelector() {
        setShowingSelector(getSelectedItem() == null && getSelectedDayOfWeek() != null);
    }

    public WeekPaneFreeSelectionModel(WeekPane<T> weekPane) {
        super(weekPane);

        selector = createDefaultSelector();
        setShowingSelector(false);

        final var grid = weekPane.getGrid();

        // Make sure the selector reminds inside the grid (in case the week pane was rebuilt)
        grid.getChildren().add(selector);
        weekPane.entriesProperty().addListener(observable -> {
            if (!grid.getChildren().contains(selector)) {
                grid.getChildren().add(selector);
            }
        });

        // Select on click; should be not called in case hit on entry (should be consumed by entry click handler)
        grid.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                // TODO: allowing selecting whole day only (no specific minute of day) would be easy

                final var dayOfWeek = weekPane.findDayOfWeekForMouseEvent(event);
                final var minuteOfDay = weekPane.findVagueMinuteOfDayForMouseEvent(event);

                // TODO: allow aligning to previous/next entry too; btw: the exact value can be specified in a form anyways

                select((T) null); // deselect entry
                select(dayOfWeek, minuteOfDay);
            }
        });

        // Move the selector element on select
        // TODO: shouldn't those be a weak listeners?
        selectedItemProperty().addListener((observable, oldEntry, newEntry) -> {
            if (newEntry != null) {
                select(newEntry.getDayOfWeek(), newEntry.getStartMinute());
            }
            updateFreeSelector();
        });
        selectedDayOfWeekProperty().addListener((observable, oldDayOfWeek, dayOfWeek) -> {
            if (dayOfWeek != null) {
                GridPane.setColumnIndex(selector, dayOfWeek.ordinal() + 1);
            }
            updateFreeSelector();
        });
        selectedTimeOfDayProperty().addListener((observable, oldTimeOfDay, timeOfDay) -> {
            final var rgp = weekPane.getRowGenerationParams();
            if (timeOfDay == null || timeOfDay.isBefore(rgp.startTimeOfDay())) {
                GridPane.setRowIndex(selector, 0);
                GridPane.setMargin(selector, new Insets(0, 0, 0, 0));
            }
            else {
                GridPane.setRowIndex(selector, rgp.calculateRowIndex(timeOfDay));
                GridPane.setMargin(selector, new Insets(rgp.calculateRowOffset(timeOfDay), 0, 0, 0));
            }
            updateFreeSelector();
        });
    }

    /**
     * @return read-only property for selected day of week; null if there is no selection.
     */
    public final ReadOnlyObjectProperty<DayOfWeek> selectedDayOfWeekProperty() {
        return selectedDayOfWeek.getReadOnlyProperty();
    }
    final private ReadOnlyObjectWrapper<DayOfWeek> selectedDayOfWeek =
            new ReadOnlyObjectWrapper<>(this, "selectedDayOfWeek", null);
    protected final void setSelectedDayOfWeek(DayOfWeek value) {
        selectedDayOfWeek.set(value);
    }
    /**
     * @return selected day of week, or null if no selection.
     */
    public final DayOfWeek getSelectedDayOfWeek() {
        return selectedDayOfWeekProperty().get();
    }

    /**
     * @return read-only property for selected time of day; null if whole day is selected or no selection.
     */
    public final ReadOnlyObjectProperty<LocalTime> selectedTimeOfDayProperty() {
        return selectedTimeOfDay.getReadOnlyProperty();
    }
    final private ReadOnlyObjectWrapper<LocalTime> selectedTimeOfDay =
            new ReadOnlyObjectWrapper<>(this, "selectedTimeOfDay", null);
    protected final void setSelectedTimeOfDay(LocalTime value) {
        selectedTimeOfDay.set(value);
    }
    /**
     * @return selected time of the day, or -1 if whole day is selected or no selection.
     */
    public final LocalTime getSelectedTimeOfDay() {
        return selectedTimeOfDayProperty().get();
    }

    // TODO: include duration (or end time) here as well?

    public void select(DayOfWeek dayOfWeek) {
        select(dayOfWeek, null);
    }

    /**
     *
     * @param dayOfWeek day of the week, or null if no selection.
     * @param minuteOfDay minute of the day, or -1 if whole day (or no selection).
     */
    public void select(DayOfWeek dayOfWeek, long minuteOfDay) {
        select(dayOfWeek, minuteOfDay < 0 ? null : LocalTime.ofSecondOfDay(minuteOfDay * 60));
    }

    public void select(DayOfWeek dayOfWeek, LocalTime timeOfDay) {
        if (dayOfWeek == null) {
            setSelectedDayOfWeek(null);
            setSelectedTimeOfDay(null);
            return;
        }

        setSelectedDayOfWeek(dayOfWeek);
        setSelectedTimeOfDay(timeOfDay);
        // TODO: select the entry if matching?
    }

    @Override
    public void clearSelection() {
        super.clearSelection();
        setSelectedDayOfWeek(null);
        setSelectedTimeOfDay(null);
    }

    /**
     * @param mondayDate start of the week as monday date
     * @return potential local date time of the selection in given week
     */
    public LocalDateTime calculatePotentialDateTimeInWeek(LocalDate mondayDate) {
        final var entry = getSelectedItem();
        if (entry != null) {
            return entry.calculatePotentialStartInWeek(mondayDate);
        }
        if (getSelectedDayOfWeek() == null) {
            return null;
        }
        return mondayDate.plusDays(getSelectedDayOfWeek().ordinal()).atTime(getSelectedTimeOfDay());

    }
}

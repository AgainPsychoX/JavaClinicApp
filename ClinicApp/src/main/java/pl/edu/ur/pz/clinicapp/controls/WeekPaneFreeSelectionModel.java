package pl.edu.ur.pz.clinicapp.controls;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.time.DayOfWeek;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

/**
 * Class responsible for managing selection inside week pane, with ability to free selection of minute of week.
 */
public class WeekPaneFreeSelectionModel<T extends WeekPane.Entry> extends WeekPaneSelectionModel<T> {
    public Region selector;

    protected void createSelector() {
        final var selectorHeight = 20;
        final var grid = weekPane.getGrid();
        selector = new Region();
        selector.getStyleClass().add("free-selector");
        selector.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        selector.setPrefSize(WeekPane.DEFAULT_DAY_COLUMN_WIDTH, selectorHeight);
        selector.setMaxSize(Double.MAX_VALUE, USE_PREF_SIZE);
        grid.getChildren().add(selector);
        GridPane.setValignment(selector, VPos.TOP);
    }

    protected void setShowingSelector(boolean show) {
        selector.setVisible(show);
        selector.setManaged(show);
    }

    public WeekPaneFreeSelectionModel(WeekPane<T> weekPane) {
        super(weekPane);
        final var grid = weekPane.getGrid();

        createSelector();
        setShowingSelector(false);

        // Make sure the selector reminds inside the grid (in case the week pane was rebuilt)
        weekPane.entriesProperty().addListener(observable -> {
            if (!grid.getChildren().contains(selector)) {
                grid.getChildren().add(selector);
            }
        });

        // Select on click; should be not called in case hit on entry (should be consumed by entry click handler)
        grid.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            // TODO: allowing selecting whole day only (no specific minute of day) would be easy

            final var columnBackground = grid.getChildren().stream()
                    .filter(n -> n.getStyleClass().contains("column")
                            && n.getBoundsInParent().contains(event.getX(), event.getY()))
                    .findFirst();
            if (columnBackground.isEmpty()) return;

            final var rowBackground = grid.getChildren().stream()
                    .filter(n -> n.getStyleClass().contains("row")
                            && n.getBoundsInParent().contains(event.getX(), event.getY()))
                    .findFirst();
            if (rowBackground.isEmpty()) return;

            final var dayOfWeek = DayOfWeek.of(GridPane.getColumnIndex(columnBackground.get()));
            final var rgp = weekPane.getRowGenerationParams();
            final var minuteOfDay = rgp.startMinuteOfDay() +
                    rgp.stepInMinutes() * GridPane.getRowIndex(rowBackground.get());

            // TODO: allow aligning to previous/next entry too; btw: the exact value be can specified in form anyways

            setShowingSelector(true); // required, despite selected entry listener sets it, as null to null is no change
            select(null); // deselect entry
            select(dayOfWeek, minuteOfDay);
        });

        // Move the selector element on select
        // TODO: shouldn't those be a weak listeners?
        selectedItemProperty().addListener((observable, oldEntry, newEntry) -> {
            setShowingSelector(newEntry == null);
            if (newEntry != null) {
                select(newEntry.getDayOfWeek(), newEntry.getStartMinute());
            }
        });
        selectedDayOfWeekProperty().addListener((observable, oldDayOfWeek, dayOfWeek) -> {
            if (dayOfWeek == null) return;
            GridPane.setColumnIndex(selector, dayOfWeek.ordinal() + 1);
        });
        selectedMinuteOfDayProperty().addListener((observable, oldMinuteOfDay, minuteOfDay) -> {
            if (minuteOfDay == -1) return;
            GridPane.setRowIndex(selector, weekPane.calculateRowIndex(minuteOfDay));
            GridPane.setMargin(selector, new Insets(weekPane.calculateRowOffset(minuteOfDay), 0, 0, 0));
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
     * @return read-only property for selected minute of day; -1 if whole day is selected or no selection.
     */
    public final ReadOnlyObjectProperty<Integer> selectedMinuteOfDayProperty() {
        return selectedMinuteOfDay.getReadOnlyProperty();
    }
    final private ReadOnlyObjectWrapper<Integer> selectedMinuteOfDay =
            new ReadOnlyObjectWrapper<>(this, "selectedMinuteOfDay", -1);
    protected final void setSelectedMinuteOfDay(int value) {
        selectedMinuteOfDay.set(value);
    }
    /**
     * @return selected minute of the day, or -1 if whole day is selected or no selection.
     */
    public final int getSelectedMinuteOfDay() {
        return selectedMinuteOfDayProperty().get();
    }

    public void select(DayOfWeek dayOfWeek, int minuteOfDay) {
        if (dayOfWeek == null) {
            setSelectedDayOfWeek(null);
            setSelectedMinuteOfDay(-1);
            return;
        }
        if (minuteOfDay < -1 || 1440 < minuteOfDay) return;
        setSelectedDayOfWeek(dayOfWeek);
        setSelectedMinuteOfDay(minuteOfDay);
        // TODO: select the entry if matching?
    }

    @Override
    public void clearSelection() {
        super.clearSelection();
        setSelectedDayOfWeek(null);
        setSelectedMinuteOfDay(-1);
    }
}

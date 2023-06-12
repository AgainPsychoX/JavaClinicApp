package pl.edu.ur.pz.clinicapp.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.CellSkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.time.*;
import java.util.List;
import java.util.ResourceBundle;

public class WeekPane<T extends WeekPane.Entry> extends VBox {
    /**
     * Interface for entries to be placed on WeekPane.
     *
     * Entries longer than day should be divided into multiple entries.
     */
    public interface Entry extends Comparable<Entry> {
        DayOfWeek getDayOfWeek();
        int getStartMinute();
        int getEndMinute();

        default int getDurationMinutes() {
            return getEndMinute() - getStartMinute();
        }

        @Override
        default int compareTo(@NotNull Entry other) {
            if (this.getDayOfWeek().ordinal() < other.getDayOfWeek().ordinal()) return -1;
            if (this.getDayOfWeek().ordinal() > other.getDayOfWeek().ordinal()) return 1;
            if (this.getStartMinute() < other.getStartMinute()) return -1;
            if (this.getStartMinute() > other.getStartMinute()) return -1;
            return 0; // kinda illegal state (overlapping)
        }

        default LocalTime getStartAsLocalTime() {
            final var startMinute = getStartMinute();
            return LocalTime.of(startMinute / 60, startMinute % 60);
        }

        default LocalTime getEndAsLocalTime() {
            final var endMinute = getEndMinute();
            return LocalTime.of(endMinute / 60, endMinute % 60);
        }

        /**
         * Calculates potential entry start moment as zoned date time, asserting it's the same the day of week.
         * @param date Date & zone to be used.
         * @return Zoned date time for potential entry start.
         */
        default ZonedDateTime calculatePotentialStartAtDate(ZonedDateTime date) {
            assert date.getDayOfWeek() == getDayOfWeek();
            final var startMinute = getStartMinute();
            return date.toLocalDate()
                    .atTime(startMinute / 60, startMinute % 60)
                    .atZone(date.getZone());
        }

        /**
         * Calculates potential entry end moment as zoned date time, asserting it's the same the day of week.
         * @param date Date & zone to be used.
         * @return Zoned date time for potential entry end.
         */
        default ZonedDateTime calculatePotentialEndAtDate(ZonedDateTime date) {
            assert date.getDayOfWeek() == getDayOfWeek();
            final var endMinute = getEndMinute();
            return date.toLocalDate()
                    .atTime(endMinute / 60, endMinute % 60)
                    .atZone(date.getZone());
        }

        default LocalDateTime calculatePotentialStartInWeek(LocalDate mondayDate) {
            return mondayDate.atStartOfDay()
                    .plusDays(getDayOfWeek().ordinal()).plusMinutes(getStartMinute());
        }
    }

    @FXML protected GridPane headerGridPane;
    @FXML protected ScrollPane scrollPane;
    @FXML protected GridPane gridPane;

    /**
     * Record for row generation parameters.
     * @param startMinuteOfDay Minute mark to start row generation will start with (inclusive).
     * @param endMinuteOfDay Minute mark row generation will end with (inclusive).
     * @param stepInMinutes Step in minutes between next rows.
     */
    public record RowGenerationParams(int startMinuteOfDay, int endMinuteOfDay, int stepInMinutes, double rowHeight) {
        public RowGenerationParams {
            if (startMinuteOfDay < 0 || 24 * 60 < endMinuteOfDay) {
                throw new IllegalArgumentException("Minute of day must be between 0 and 1440.");
            }
            if (endMinuteOfDay < startMinuteOfDay) {
                throw new IllegalArgumentException("The start must come before the end.");
            }
            if (stepInMinutes < 0 && (endMinuteOfDay - startMinuteOfDay) % stepInMinutes != 0) {
                throw new IllegalStateException("Minutes range must be dividable by step.");
            }
        }

        public LocalTime startTimeOfDay() {
            return LocalTime.ofSecondOfDay(startMinuteOfDay / 60);
        }

        public LocalTime endTimeOfDay() {
            return LocalTime.ofSecondOfDay(startMinuteOfDay / 60);
        }

        public int calculateRowIndex(LocalTime timeOfDay) {
            return calculateRowIndex(timeOfDay.toSecondOfDay() / 60);
        }

        public int calculateRowIndex(int minuteOfDay) {
            assert (minuteOfDay >= startMinuteOfDay);
            return (minuteOfDay - startMinuteOfDay) / stepInMinutes;
        }

        public double calculateRowOffset(LocalTime timeOfDay) {
            return calculateRowOffset(timeOfDay.toSecondOfDay() / 60);
        }

        public double calculateRowOffset(int minuteOfDay) {
            return (double) minuteOfDay % stepInMinutes / stepInMinutes * rowHeight;
        }

        public double calculateEntryHeight(long minutes) {
            return (double) minutes / stepInMinutes * rowHeight;
        }
    }

    /**
     * The row generation parameters for the WeekPane table.
     * @return the row generation params property
     */
    public final ObjectProperty<RowGenerationParams> rowGenerationParamsProperty() {
        return rowGenerationParams;
    }
    private final ObjectProperty<RowGenerationParams> rowGenerationParams =
            new SimpleObjectProperty<>(this, "rowGenerationParams") {
                @Override
                protected void invalidated() {
                    WeekPane.this.generateRows();
                    WeekPane.this.layoutEntries();
                    WeekPane.this.updateWeekendColumns();
                }
            };
    public final void setRowGenerationParams(RowGenerationParams value) {
        rowGenerationParamsProperty().set(value);
    }
    public final RowGenerationParams getRowGenerationParams() {
        return rowGenerationParams.get();
    }

    /**
     * The underlying data model for the WeekPane. Note that it has a generic
     * type that must match the type of the WeekPane itself.
     * @return the entries property
     */
    public final ObjectProperty<ObservableList<T>> entriesProperty() {
        return entries;
    }
    private final ObjectProperty<ObservableList<T>> entries = new SimpleObjectProperty<>(this, "entries") {
        private WeakReference<ObservableList<T>> weakEntriesRef = new WeakReference<>(null);
        private WeakListChangeListener<T> weakChangeListener = null;

        @Override
        protected void invalidated() {
            final var newEntries = getValue();
            final var oldEntries = weakEntriesRef.get();
            weakEntriesRef = new WeakReference<>(newEntries);
            if (oldEntries != null) {
                oldEntries.removeListener(weakChangeListener);
            }
            if (newEntries != null) {
                weakChangeListener = new WeakListChangeListener<>(entriesListChangeListener);
                getValue().addListener(weakChangeListener);
            }
            // TODO: make there are enough rows
            WeekPane.this.layoutEntries();
            WeekPane.this.updateWeekendColumns();
        }
    };
    public final void setEntries(ObservableList<T> value) {
        entriesProperty().set(value);
    }
    public final void setEntries(List<T> value) {
        entriesProperty().set(FXCollections.observableList(value));
    }
    /**
     * Provides access observable list of entries. Where possible use {@link WeekPane#setEntries}.
     * @return observable list of entries
     */
    public final ObservableList<T> getEntries() {
        return entries.get();
    }

    private final ListChangeListener<T> entriesListChangeListener = change -> {
        // Why sorted? TimetableView operates on managed collection, which is expected to be kept sorted.
        // TODO: move it to TimetableView or Timetable? wrap timetables.getEntries into observable to keep it sorted
        //  WeekPane should be kept as much as possible independent from the business details.
        // TODO (OLD): ensure entries sorted:
        //  + if single entry added - insert at binary searched index
        //  + if more entries added - full sort

        boolean needSorting = false;
        boolean needRelayout = false;
        while (change.next()) {
            if (change.wasAdded()) {
                needSorting = true;
                needRelayout = true;
            }
            if (change.wasRemoved()) {
                for (final var entry : change.getRemoved()) {
                    gridPane.getChildren().remove(findEntryCell(entry));
                }
            }
        }

        if (needSorting) {
            WeekPane.this.getEntries().sort(WeekPane.Entry::compareTo);
        }
        if (needRelayout) {
            WeekPane.this.layoutEntries();
        }
        WeekPane.this.updateWeekendColumns();
    };

    /**
     * Setting cell factory allows to customize entry cell creation.
     *
     * @return the cell factory property
     */
    public final ObjectProperty<Callback<WeekPane<T>, EntryCell<T>>> entryCellFactoryProperty() {
        return entryCellFactory;
    }
    private final ObjectProperty<Callback<WeekPane<T>, EntryCell<T>>> entryCellFactory =
            new SimpleObjectProperty<>(this, "displayFactory", new DefaultEntryCellFactory<T>());
    public final void setEntryCellFactory(Callback<WeekPane<T>, EntryCell<T>> value) {
        entryCellFactoryProperty().set(value);
    }
    public final Callback<WeekPane<T>, EntryCell<T>> getEntryCellFactory() {
        return entryCellFactory.get();
    }

    public static class EntryCell<T> extends Cell<T> {
        @Override
        protected Skin<?> createDefaultSkin() {
            return new CellSkinBase<>(this);
        }

        /** {@inheritDoc} */
        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
        }
    }

    static public class DefaultEntryCellFactory<T extends WeekPane.Entry>
            implements Callback<WeekPane<T>, EntryCell<T>> {
        @Override
        public EntryCell<T> call(WeekPane<T> entry) {
            return new EntryCell<>() {
                @Override
                public void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText("");
                    }
                    else {
                        setText(entry.toString());
                    }
                }
            };
        }
    }

    /* * */

    /**
     * Creates default WeekPane with default rows from 7 AM to 7 PM every 15 minutes.
     */
    public WeekPane() {
        this(
                FXCollections.observableArrayList(),
                new RowGenerationParams(7 * 60, 19 * 60, 15, 20)
        );
    }

    /**
     * Creates WeekPane provided configuration and parameters.
     * @param rowGenerationParams Initial row generation params, required early to avoid recreating rows multiple times.
     */
    public WeekPane(ObservableList<T> entries, RowGenerationParams rowGenerationParams) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(WeekPane.class.toString().substring(6));
        FXMLLoader fxmlLoader = new FXMLLoader(WeekPane.class.getResource("WeekPane.fxml"), resourceBundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        setRowGenerationParams(rowGenerationParams); // will generate rows
        setEntries(entries); // will generate entries cells
        // TODO: menu
        // TODO: allow hide weekend
        // TODO: show current time line?
    }

    protected Pane generateRowBackgroundPane(boolean even) {
        final var pane = new Pane();
        pane.getStyleClass().addAll("row", even ? "even" : "odd");
        return pane;
    }

    protected Pane generateRowHourLabelCell(boolean even, int minuteOfDay) {
        final var box = new HBox();
        box.setPadding(new Insets(4));
        box.setAlignment(Pos.CENTER);
        box.getChildren().setAll(new Label("%d:%02d".formatted(minuteOfDay / 60, minuteOfDay % 60)));
        box.getStyleClass().addAll("hour", even ? "even" : "odd");
        return box;
    }

    protected Pane generateColumnBackgroundPane(DayOfWeek day) {
        final var pane = new Pane();
        pane.getStyleClass().addAll("column", "day", day.toString().toLowerCase());
        return pane;
    }

    protected void generateRows() {
        final var rgp = getRowGenerationParams();

        gridPane.getRowConstraints().clear();
        gridPane.getChildren().clear();
        int rowCount = 0;

        for (int minuteOfDay = rgp.startMinuteOfDay, index = 0;
             minuteOfDay <= rgp.endMinuteOfDay;
             minuteOfDay += rgp.stepInMinutes, index++
        ) {
            final var isEvenRow = index % 2 == 0;

            final var rowConstraints = new RowConstraints(rgp.rowHeight);
            rowConstraints.setVgrow(Priority.SOMETIMES);
            gridPane.getRowConstraints().add(rowConstraints);

            final var rowBackgroundPane = generateRowBackgroundPane(isEvenRow);
            gridPane.add(rowBackgroundPane, 0, index);
            GridPane.setColumnSpan(rowBackgroundPane, 8);

            final var rowHourLabelCell = generateRowHourLabelCell(isEvenRow, minuteOfDay);
            gridPane.add(rowHourLabelCell, 0, index);

            rowCount += 1;
        }

        for (int i = 1; i <= 7; i++) {
            final var day = DayOfWeek.of(i);

            final var columnBackgroundPane = generateColumnBackgroundPane(day);
            gridPane.add(columnBackgroundPane, i, 0);
            GridPane.setRowSpan(columnBackgroundPane, rowCount);
        }

        // TODO: instead requiring laying out entries cell again (remove & recreate), just move them around
    }

    protected static final int DEFAULT_DAY_COLUMN_WIDTH = 80;

    protected void layoutEntries() {

        // TODO: instead removing & recreating all the entries cells remove/recreate only changed ones

        gridPane.getChildren().removeIf(n -> n instanceof Cell);
        if (getEntries() == null) return;

        final var cellFactory = getEntryCellFactory();
        for (final var entry : getEntries()) {
            layoutEntry(entry, cellFactory.call(this));
        }
    }

    protected void layoutEntry(T entry, EntryCell<T> cell) {
        final var rgp = getRowGenerationParams();
        var startMinute = entry.getStartMinute();
        var durationMinutes = entry.getDurationMinutes();
        if (entry.getStartMinute() < rgp.startMinuteOfDay) {
            durationMinutes -= (rgp.startMinuteOfDay - startMinute);
            startMinute = rgp.startMinuteOfDay;
        }
        cell.getStyleClass().add("entry");
        cell.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        cell.setPrefSize(DEFAULT_DAY_COLUMN_WIDTH, rgp.calculateEntryHeight(durationMinutes));
        cell.setMaxSize(Double.MAX_VALUE, USE_PREF_SIZE);
        gridPane.add(cell, entry.getDayOfWeek().ordinal() + 1, rgp.calculateRowIndex(startMinute));
        GridPane.setValignment(cell, VPos.TOP);
        GridPane.setMargin(cell, new Insets(rgp.calculateRowOffset(startMinute), 0, 0, 0));
        cell.updateItem(entry, false); // late, to allow overriding and querying size
    }

    /**
     * @return Modifiable list of children of whole week pane (not really useful unless extending the class)
     */
    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    /**
     * Exposed to ease customizations, as {@link WeekPane#getChildren} is already exposed anyway.
     * @return Grid part of the week pane (not really useful unless extending the class)
     */
    public GridPane getGrid() {
        return gridPane;
    }

    /**
     * @return Unmodifiable list of entries cells on the grid, ordering not guaranteed.
     */
    @SuppressWarnings("unchecked")
    public List<EntryCell<T>> getEntriesCells() {
        return gridPane.getChildren().stream()
                .filter(node -> node instanceof WeekPane.EntryCell<?>)
                .map(node -> (EntryCell<T>) node)
                .toList();
    }

    /**
     * Tries to find the entry cell for given entry.
     * @param item entry to look cell for.
     * @return entry cell representing the entry, or null if not found
     */
    @SuppressWarnings("unchecked")
    public EntryCell<T> findEntryCell(T item) {
        for (final var node : gridPane.getChildren()) {
            if (node instanceof WeekPane.EntryCell<?> cell) {
                if (cell.getItem().equals(item)) {
                    return (EntryCell<T>) cell;
                }
            }
        }
        return null;
    }

    /**
     * Refreshes the entry cell (i.e. after modifying).
     * @param item entry to update cell for
     */
    public void refreshEntry(T item) {
        final var cell = findEntryCell(item);
        if (cell == null) throw new IllegalArgumentException();
        gridPane.getChildren().remove(cell);
        layoutEntry(item, cell);
    }

    public void scrollToRow(int index) {
        final var rgp = getRowGenerationParams();
        scrollPane.setHvalue(index * rgp.rowHeight);
    }

    protected void showColumn(int index) {
        final var hcc = headerGridPane.getColumnConstraints().get(index);
        hcc.setHgrow(Priority.ALWAYS);
        hcc.setPrefWidth(DEFAULT_DAY_COLUMN_WIDTH);
        final var rcc = gridPane.getColumnConstraints().get(index);
        rcc.setHgrow(Priority.ALWAYS);
        rcc.setPrefWidth(DEFAULT_DAY_COLUMN_WIDTH);
    }

    protected void hideColumn(int index) {
        final var hcc = headerGridPane.getColumnConstraints().get(index);
        hcc.setHgrow(Priority.NEVER);
        hcc.setPrefWidth(0);
        final var rcc = gridPane.getColumnConstraints().get(index);
        rcc.setHgrow(Priority.NEVER);
        rcc.setPrefWidth(0);
    }

    private boolean isWeekend(DayOfWeek day) {
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    protected void updateWeekendColumns() {
        if (getEntries() == null) return;
        boolean hasEntriesOnWeekend = this.getEntries().stream().anyMatch(e -> isWeekend(e.getDayOfWeek()));
        if (!hasEntriesOnWeekend) {
            hideColumn(6);
            hideColumn(7);
        } else {
            showColumn(6);
            showColumn(7);
        }
    }

    /**
     * Tries to find day of week on the week pane for mouse event.
     * @param event event with X/Y cords to test for
     * @return found day of day or null if not found
     */
    public DayOfWeek findDayOfWeekForMouseEvent(MouseEvent event) {
        final var columnBackground = gridPane.getChildren().stream()
                .filter(n -> n.getStyleClass().contains("column")
                        && n.getBoundsInParent().contains(event.getX(), event.getY()))
                .findFirst();
        if (columnBackground.isEmpty()) {
            return null;
        } else {
            return DayOfWeek.of(GridPane.getColumnIndex(columnBackground.get()));
        }
    }

    /**
     * Tries to find minute of day on the week pane for mouse event, vaguely (based using only row index).
     * @param event event with X/Y cords to test for
     * @return found minute of day or -1 if not found
     */
    public int findVagueMinuteOfDayForMouseEvent(MouseEvent event) {
        final var rowBackground = gridPane.getChildren().stream()
                .filter(n -> n.getStyleClass().contains("row")
                        && n.getBoundsInParent().contains(event.getX(), event.getY()))
                .findFirst();
        if (rowBackground.isEmpty()) {
            return -1;
        }

        final var rgp = getRowGenerationParams();
        return rgp.startMinuteOfDay() + rgp.stepInMinutes() * GridPane.getRowIndex(rowBackground.get());
    }

    /**
     * Tries to find exact minute of day on the week pane for mouse event.
     * @param event event with X/Y cords to test for
     * @return found minute of day or -1 if not found
     */
    public int findExactMinuteOfDayForMouseEvent(MouseEvent event) {
        final var rgp = getRowGenerationParams();
        for (final var node : gridPane.getChildren()) {
            if (node.getStyleClass().contains("row")) {
                final var bounds = node.getBoundsInParent();
                if (bounds.contains(event.getX(), event.getY())) {
                    final var offset = (int) ((event.getY() - bounds.getMinY()) / rgp.rowHeight * rgp.stepInMinutes);
                    return rgp.startMinuteOfDay() + rgp.stepInMinutes() * GridPane.getRowIndex(node) + offset;
                }
            }
        }
        return -1;
    }
}

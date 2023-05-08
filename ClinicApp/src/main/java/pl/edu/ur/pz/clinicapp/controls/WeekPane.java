package pl.edu.ur.pz.clinicapp.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.WeakSetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.DayOfWeek;
import java.util.ResourceBundle;

public class WeekPane<T extends WeekPane.Entry> extends VBox {
    /**
     * Interface for entries to be placed on WeekPane.
     */
    public interface Entry {
        DayOfWeek getDayOfWeek();
        int getStartMinute();
        int getEndMinute();

        default int getDurationMinutes() {
            return getEndMinute() - getStartMinute();
        }

        // TODO: what about entries longer than day or across midnight?
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
    public final ObjectProperty<ObservableSet<T>> entriesProperty() {
        return entries;
    }
    private final ObjectProperty<ObservableSet<T>> entries = new SimpleObjectProperty<>(this, "entries") {
        private WeakReference<ObservableSet<T>> weakEntriesRef = new WeakReference<>(null);

        @Override
        protected void invalidated() {
            final var newEntries = getValue();
            final var oldEntries = weakEntriesRef.get();
            weakEntriesRef = new WeakReference<>(newEntries);
            if (oldEntries != null) {
                oldEntries.removeListener(entriesSetChangeListener);
            }
            if (newEntries != null) {
                getValue().addListener(entriesSetChangeListener);
            }
            WeekPane.this.layoutEntries();
            WeekPane.this.updateWeekendColumns();
        }
    };
    public final void setEntries(ObservableSet<T> value) {
        entriesProperty().set(value);
    }
    public final ObservableSet<T> getEntries() {
        return entries.get();
    }

    private final WeakSetChangeListener<? super T> entriesSetChangeListener =
            new WeakSetChangeListener<>(c -> {
                WeekPane.this.layoutEntries();
                WeekPane.this.updateWeekendColumns();
            });

    /**
     * Setting cell factory allows to customize entry cell creation.
     *
     * Why {@link Region} is used? To allow both controls and more complex panes.
     * while also allowing manipulation of size and margin to position the cell.
     *
     * @return the cell factory property
     */
    public final ObjectProperty<Callback<T, Region>> entriesDisplayFactoryProperty() {
        return entriesDisplayFactory;
    }
    private final ObjectProperty<Callback<T, Region>> entriesDisplayFactory =
            new SimpleObjectProperty<>(this, "displayFactory", new BaseEntryDisplayFactory<T>());
    public final void setEntriesDisplayFactory(Callback<T, Region> value) {
        entriesDisplayFactoryProperty().set(value);
    }
    public final Callback<T, Region> getEntriesDisplayFactory() {
        return entriesDisplayFactory.get();
    }

    static public class BaseEntryDisplayFactory<T extends WeekPane.Entry> implements Callback<T, Region> {
        @Override
        public Region call(T entry) {
            return new VBox() {{
                setPadding(new Insets(4));
                getChildren().setAll(
                        new TextFlow(
                                new Text(entry.toString())
                        )
                );
            }};
        }
    }

    /* * */

    /**
     * Creates default WeekPane with default rows from 7 AM to 7 PM every 15 minutes.
     */
    @SuppressWarnings("unchecked") // TODO: WHY THE FUCK? ListView has no such issue...
    public WeekPane() {
        this(
                FXCollections.<T>observableSet(),
                new RowGenerationParams(7 * 60, 19 * 60, 15, 20)
        );
    }

    /**
     * Creates WeekPane provided configuration and parameters.
     * @param rowGenerationParams Initial row generation params, required early to avoid recreating rows multiple times.
     */
    public WeekPane(ObservableSet<T> entries, RowGenerationParams rowGenerationParams) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(WeekPane.class.toString().substring(6));
        FXMLLoader fxmlLoader = new FXMLLoader(WeekPane.class.getResource("WeekPane.fxml"), resourceBundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setRowGenerationParams(rowGenerationParams); // will generate rows
        setEntries(entries); // will generate entries cells
        // TODO: menu
        // TODO: allow hide weekend
        // TODO: show current time line?
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

            gridPane.getRowConstraints().add(new RowConstraints(rgp.rowHeight) {{
                setVgrow(Priority.SOMETIMES);
            }});

            final var rowBackgroundPane = new Pane() {{
                final var sc = getStyleClass();
                sc.add("row");
                sc.add(isEvenRow ? "even" : "odd");
            }};
            gridPane.add(rowBackgroundPane, 0, index);
            GridPane.setColumnSpan(rowBackgroundPane, 8);

            final var hourText = String.format("%d:%02d", minuteOfDay / 60, minuteOfDay % 60);
            final var rowHourLabelCell = new HBox() {{
                setPadding(new Insets(4));
                setAlignment(Pos.CENTER);
                getChildren().setAll(new Label(hourText));
                final var sc = getStyleClass();
                sc.add("hour");
                sc.add(isEvenRow ? "even" : "odd");
            }};
            gridPane.add(rowHourLabelCell, 0, index);

            rowCount += 1;
        }

        for (int i = 1; i <= 7; i++) {
            final var day = DayOfWeek.of(i);

            final var columnBackgroundPane = new Pane() {{
                final var sc = getStyleClass();
                sc.add("column");
                sc.add("day");
                sc.add(day.toString().toLowerCase());
            }};
            gridPane.add(columnBackgroundPane, i, 0);
            GridPane.setRowSpan(columnBackgroundPane, rowCount);
        }

        // TODO: instead requiring laying out entries cell again (remove & recreate), just move them around
    }

    protected static final int DEFAULT_DAY_COLUMN_WIDTH = 80;

    protected void layoutEntries() {
        // TODO: instead removing & recreating all the entries cells remove/recreate only changed ones

        gridPane.getChildren().removeIf(n -> n.getStyleClass().contains("entry"));
        if (getEntries() == null) return;

        final var displayFactory = getEntriesDisplayFactory();
        for (final var entry : getEntries()) {
            final var display = displayFactory.call(entry);
            display.getStyleClass().add("entry");
            display.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            display.setPrefSize(DEFAULT_DAY_COLUMN_WIDTH, calculateEntryHeight(entry));
            display.setMaxSize(Double.MAX_VALUE, USE_PREF_SIZE);
            gridPane.add(display, entry.getDayOfWeek().ordinal() + 1, calculateRowIndex(entry.getStartMinute()));
            GridPane.setValignment(display, VPos.TOP);
            GridPane.setMargin(display, new Insets(calculateRowOffset(entry.getStartMinute()), 0, 0, 0));
        }
    }

    protected int calculateRowIndex(int minuteOfDay) {
        final var rgp = getRowGenerationParams();
        return (minuteOfDay - rgp.startMinuteOfDay) / rgp.stepInMinutes;
    }

    protected double calculateRowOffset(int minuteOfDay) {
        final var rgp = getRowGenerationParams();
        return (double) minuteOfDay % rgp.stepInMinutes / rgp.stepInMinutes * rgp.rowHeight;
    }
    
    protected double calculateEntryHeight(Entry entry) {
        final var rgp = getRowGenerationParams();
        return (double) entry.getDurationMinutes() / rgp.stepInMinutes * rgp.rowHeight;
    }

    public void scrollToRow(int index) {
        final var rgp = getRowGenerationParams();
        scrollPane.setHvalue(index * rgp.rowHeight);
    }

    protected void showColumn(int index) {
        final var hcc = headerGridPane.getColumnConstraints().get(index);
        hcc.setHgrow(Priority.SOMETIMES);
        hcc.setPrefWidth(DEFAULT_DAY_COLUMN_WIDTH);
        final var rcc = gridPane.getColumnConstraints().get(index);
        rcc.setHgrow(Priority.SOMETIMES);
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
}

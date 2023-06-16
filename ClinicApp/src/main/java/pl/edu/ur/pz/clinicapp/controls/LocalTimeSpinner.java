package pl.edu.ur.pz.clinicapp.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.InputEvent;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

/**
 * Specialized spinner to allow time of the day input.
 *
 * Adapted from Spinner and SpinnerValueFactory.LocalTimeSpinnerValueFactory
 * source code (which is package-private, yet not used anywhere in the package
 * for some fucking reason) and <a href="https://stackoverflow.com/a/32617768/4880243">...</a>.
 */
public class LocalTimeSpinner extends Spinner<LocalTime> {
    public enum SelectedUnit {
        HOURS {
            @Override
            LocalTime increment(LocalTime time, int steps) {
                return time.plusHours(steps);
            }
            @Override
            void select(LocalTimeSpinner spinner) {
                final var text = spinner.getEditor().getText();
                int index = text.indexOf(':');
                spinner.getEditor().selectRange(0, index != -1 ? index : text.length());
            }
        },
        MINUTES {
            @Override
            LocalTime increment(LocalTime time, int steps) {
                return time.plusMinutes(steps);
            }
            @Override
            void select(LocalTimeSpinner spinner) {
                if (spinner.getPattern().contains(":mm")) {
                    final var text = spinner.getEditor().getText();
                    int firstColonIndex = text.indexOf(':');
                    int secondColonIndex = text.indexOf(':', firstColonIndex + 1);
                    int caretPosition = secondColonIndex != -1 ? secondColonIndex : text.length();
                    spinner.getEditor().selectRange(firstColonIndex + 1, caretPosition);
                }
            }
        },
        SECONDS {
            @Override
            LocalTime increment(LocalTime time, int steps) {
                return time.plusSeconds(steps);
            }
            @Override
            void select(LocalTimeSpinner spinner) {
                if (spinner.getPattern().contains(":ss")) {
                    final var text = spinner.getEditor().getText();
                    int lastColonIndex = text.lastIndexOf(':');
                    int commaIndex = text.lastIndexOf(',');
                    if (commaIndex == -1) {
                        commaIndex = text.lastIndexOf('.');
                    }
                    spinner.getEditor().selectRange(lastColonIndex + 1, commaIndex != -1 ? commaIndex : text.length());
                }
            }
        };
        // TODO: might want add MILLISECONDS, MICROSECONDS, NANOSECONDS - but not useful for our app

        abstract LocalTime increment(LocalTime time, int steps);
        abstract void select(LocalTimeSpinner spinner);
        LocalTime decrement(LocalTime time, int steps) {
            return increment(time, -steps);
        }
    }

    public ObjectProperty<SelectedUnit> selectedUnitProperty() {
        return selectedUnit;
    }
    private final ObjectProperty<SelectedUnit> selectedUnit = new SimpleObjectProperty<>(SelectedUnit.HOURS);
    public final SelectedUnit getSelectedUnit() {
        return selectedUnitProperty().get();
    }
    public final void setSelectedUnit(SelectedUnit selectedUnit) {
        selectedUnitProperty().set(selectedUnit);
    }

    /**
     * Pattern property allow to control display format for the spinner
     * along with the smallest unit of time managed by the spinner.
     *
     * Valid values include: `HH:mm, `HH:mm:ss`, `HH:mm:ss,SSS` etc
     *
     * @return pattern property
     */
    public StringProperty patternProperty() {
        return pattern;
    }
    private final StringProperty pattern = new SimpleStringProperty("HH:mm:ss");
    public final String getPattern() {
        return patternProperty().get();
    }
    public final void setPattern(String selectedUnit) {
        patternProperty().set(selectedUnit);
    }

    private final ObjectProperty<DateTimeFormatter> formatter
            = new SimpleObjectProperty<>(prepareFormatter(getPattern()));

    static private DateTimeFormatter prepareFormatter(String pattern) {
        return new DateTimeFormatterBuilder().parseLenient().appendPattern(pattern).toFormatter();
    }


    public LocalTimeSpinner() {
        this(LocalTime.now());
    }

    public LocalTimeSpinner(LocalTime time) {
        setEditable(true);

        pattern.addListener((observable, oldValue, newValue) -> formatter.set(prepareFormatter(newValue)));

        final var localTimeConverter = new StringConverter<LocalTime>() {
            @Override
            public String toString(LocalTime time) {
                return formatter.get().format(time);
            }
            @Override
            public LocalTime fromString(String string) {
                return LocalTime.parse(string, formatter.get());
            }
        };

        this.setValueFactory(new SpinnerValueFactory<>() {
            {
                setConverter(localTimeConverter);
                setValue(time);
            }

            @Override
            public void decrement(int steps) {
                setValue(selectedUnit.get().decrement(getValue(), steps));
                selectedUnit.get().select(LocalTimeSpinner.this);
            }

            @Override
            public void increment(int steps) {
                setValue(selectedUnit.get().increment(getValue(), steps));
                selectedUnit.get().select(LocalTimeSpinner.this);
            }

        });
        this.getEditor().setTextFormatter(new TextFormatter<>(
                localTimeConverter,
                time,
                change -> {
                    String newText = change.getControlNewText();
                    try {
                        LocalTime.parse(newText, formatter.get());
                        return change;
                    }
                    catch (DateTimeParseException e) {
                        return null;
                    }
                }
        ));

        // Update the mode when the user interacts with the editor.
        // This is a bit of a hack, e.g. calling spinner.getEditor().positionCaret()
        // could result in incorrect state. Directly observing the caretPosition
        // didn't work well though; getting that to work properly might be
        // a better approach in the long run.
        this.getEditor().addEventHandler(InputEvent.ANY, e -> {
            final String text =  this.getEditor().getText();
            final int caretPosition = this.getEditor().getCaretPosition();

            int firstColonIndex = text.indexOf(':');
            if (caretPosition <= firstColonIndex) {
                selectedUnit.set(SelectedUnit.HOURS);
                return;
            }

            int secondColonIndex = text.indexOf(':', firstColonIndex + 1);
            if (secondColonIndex == -1 || caretPosition <= secondColonIndex) {
                selectedUnit.set(SelectedUnit.MINUTES);
                return;
            }

            int commaIndex = text.lastIndexOf(',');
            if (commaIndex == -1) {
                commaIndex = text.lastIndexOf('.');
            }
            if (commaIndex == -1 || caretPosition <= commaIndex) {
                selectedUnit.set(SelectedUnit.SECONDS);
            }
        });

        selectedUnit.addListener((obs, oldSelectedUnit, newSelectedUnit) -> newSelectedUnit.select(this));
    }
}
package pl.edu.ur.pz.clinicapp.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Helper control that allows picking both time and date.
 *
 * TODO: allow spinning using up/down arrow keys like {@link LocalTimeSpinner}
 */
public class DateTimePicker extends DatePicker {
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm";

    private DateTimeFormatter formatter;

    final private ObjectProperty<LocalDateTime> dateTimeValue = new SimpleObjectProperty<>(LocalDateTime.now());
    public ObjectProperty<LocalDateTime> dateTimeValueProperty() {
        return dateTimeValue;
    }
    public LocalDateTime getDateTimeValue() {
        return dateTimeValue.get();
    }
    public void setDateTimeValue(LocalDateTime dateTimeValue) {
        if (dateTimeValue.isAfter(LocalDateTime.of(1971, 6, 30, 12, 00))) {
            this.dateTimeValue.set(dateTimeValue);
        }
        else {
            this.dateTimeValue.set(null);
        }
    }

    final private ObjectProperty<String> format = new SimpleObjectProperty<>() {
        public void set(String newValue) {
            super.set(newValue);
            formatter = DateTimeFormatter.ofPattern(newValue);
        }
    };
    public ObjectProperty<String> formatProperty() {
        return format;
    }
    public String getFormat() {
        return format.get();
    }
    public void setFormat(String format) {
        this.format.set(format);
    }

    public DateTimePicker() {
        getStyleClass().add("datetime-picker");
        setFormat(DEFAULT_PATTERN);
        setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate object) {
                LocalDateTime value = getDateTimeValue();
                return (value != null) ? value.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String value) {
                if (value == null) {
                    dateTimeValue.set(null);
                    return null;
                }
                dateTimeValue.set(LocalDateTime.parse(value, formatter));
                return dateTimeValue.get().toLocalDate();
            }
        });

        // Synchronize changes to the underlying date value back to the dateTimeValue
        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                dateTimeValue.set(null);
            } else {
                if (dateTimeValue.get() == null) {
                    dateTimeValue.set(LocalDateTime.of(newValue, LocalTime.now()));
                } else {
                    LocalTime time = dateTimeValue.get().toLocalTime();
                    dateTimeValue.set(LocalDateTime.of(newValue, time));
                }
            }
        });

        // Synchronize changes to dateTimeValue back to the underlying date value
        dateTimeValue.addListener((observable, oldValue, newValue) -> {
            setValue(newValue == null ? null : newValue.toLocalDate());
        });

        // Persist changes on blur
        getEditor().focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused) {
                simulateEnterPressed();
            }
        });
    }

    private void simulateEnterPressed() {
        getEditor().fireEvent(new KeyEvent(getEditor(), getEditor(), KeyEvent.KEY_PRESSED,
                null, null, KeyCode.ENTER, false, false, false, false));
    }
}
package pl.edu.ur.pz.clinicapp.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Converter(autoApply = false)
public class DurationMinutesConverter implements AttributeConverter<Duration, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Duration attribute) {
        return (int) attribute.toMinutes();
    }

    @Override
    public Duration convertToEntityAttribute(Integer duration) {
        return Duration.of(duration, ChronoUnit.MINUTES);
    }
}

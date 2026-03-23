package com.jordanrobin.financial_erp.infrastructure.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class MonthDayConverter implements AttributeConverter<MonthDay, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("--MM-dd");

    @Override
    public String convertToDatabaseColumn(MonthDay monthDay) {
        return (monthDay == null) ? null : monthDay.format(FORMATTER);
    }

    @Override
    public MonthDay convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : MonthDay.parse(dbData, FORMATTER);
    }
}

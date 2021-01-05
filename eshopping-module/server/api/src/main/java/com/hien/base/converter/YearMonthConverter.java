package com.hien.base.converter;

import java.time.YearMonth;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {

	@Override
	public String convertToDatabaseColumn(YearMonth date) {
		return date.toString();
	}

	@Override
	public YearMonth convertToEntityAttribute(String date) {
		return YearMonth.parse(date);
	}
}

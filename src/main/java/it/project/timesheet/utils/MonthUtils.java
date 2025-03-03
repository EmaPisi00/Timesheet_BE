package it.project.timesheet.utils;

import java.text.DateFormatSymbols;

public class MonthUtils {
    private static final String[] MONTH_NAMES = new DateFormatSymbols().getMonths();

    public static String getMonthName(int month) {
        if (month < 1 || month > 12) {
            return "Mese non valido";
        }
        return MONTH_NAMES[month - 1];
    }
}
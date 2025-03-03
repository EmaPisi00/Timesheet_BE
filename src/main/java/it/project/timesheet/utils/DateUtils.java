package it.project.timesheet.utils;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class DateUtils {
    private static final String[] MONTH_NAMES = new DateFormatSymbols().getMonths();

    public static Set<LocalDate> getHolidays(int year) {
        Set<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(year, 1, 1));     // Capodanno
        holidays.add(LocalDate.of(year, 1, 6));     // Epifania
        holidays.add(LocalDate.of(year, 4, 25));    // Festa della Liberazione
        holidays.add(LocalDate.of(year, 5, 1));     // Festa dei lavoratori
        holidays.add(LocalDate.of(year, 6, 2));     // Festa della Repubblica
        holidays.add(LocalDate.of(year, 8, 15));    // Ferragosto
        holidays.add(LocalDate.of(year, 11, 1));    // Tuttisanti
        holidays.add(LocalDate.of(year, 12, 25));   // Natale
        holidays.add(LocalDate.of(year, 12, 26));   // Santo Stefano
        return holidays;
    }

    public static String getMonthName(int month) {
        if (month < 1 || month > 12) {
            return "Mese non valido";
        }
        return MONTH_NAMES[month - 1];
    }



}
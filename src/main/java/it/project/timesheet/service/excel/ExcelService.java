package it.project.timesheet.service.excel;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
public class ExcelService {

    public static void createExcelFile(ByteArrayOutputStream outputStream, int year, int month, List<LocalTime> entryTimes, List<LocalTime> exitTimes, List<String> notes, List<Double> totalHoursList) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orario Lavorativo");

        // Creazione font e stile per l'intestazione
        XSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);
        CellStyle headerStyle = createHeaderStyle(workbook, boldFont);

        // Creazione intestazione
        createHeader(sheet, headerStyle);

        // Popolamento dati
        LocalDate date = LocalDate.of(year, month, 1);
        int rowIndex = 1;
        int timeIndex = 0;

        while (date.getMonthValue() == month) {
            Row row = sheet.createRow(rowIndex);
            fillRowWithStyles(row, date, entryTimes, exitTimes, notes, totalHoursList, timeIndex, workbook);
            date = date.plusDays(1);
            rowIndex++;
            timeIndex++;
        }

        // Riga Totale
        createTotalRow(sheet, rowIndex, totalHoursList);

        // Auto-dimensionamento colonne
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(outputStream);
        workbook.close();
    }

    // Metodo per la creazione degli stili dell'intestazione
    private static CellStyle createHeaderStyle(XSSFWorkbook workbook, XSSFFont boldFont) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(boldFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    // Metodo per la creazione dell'intestazione
    private static void createHeader(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Giorno", "Orario Entrata", "Orario Uscita", "Ore Lavorate", "Note"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    // Metodo per la creazione degli stili di formattazione comuni
    private static CellStyle createCellStyle(XSSFWorkbook workbook, String format, short color) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat(format));
        if (color != -1) { // -1 indica che non vogliamo applicare colore
            style.setFillForegroundColor(color);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }

    // Metodo che popola una riga con dati e stili
    private static void fillRowWithStyles(Row row, LocalDate date, List<LocalTime> entryTimes, List<LocalTime> exitTimes, List<String> notes, List<Double> totalHours, int timeIndex, XSSFWorkbook workbook) {
        // Determina se è weekend
        boolean isWeekend = date.getDayOfWeek().getValue() >= 6;

        // Crea gli stili
        CellStyle dateStyle = createCellStyle(workbook, "dd/MM/yyyy", isWeekend ? IndexedColors.RED.getIndex() : -1);
        CellStyle timeStyle = createCellStyle(workbook, "HH:mm", isWeekend ? IndexedColors.RED.getIndex() : -1);
        CellStyle textStyle = createCellStyle(workbook, "@", isWeekend ? IndexedColors.RED.getIndex() : -1);
        CellStyle numberStyle = createCellStyle(workbook, "0.0", isWeekend ? IndexedColors.RED.getIndex() : -1);

        // Giorno con formattazione
        Cell dayCell = row.createCell(0);
        dayCell.setCellValue(java.sql.Date.valueOf(date));
        dayCell.setCellStyle(dateStyle);

        // Orario Entrata
        if (timeIndex < entryTimes.size()) {
            Cell entryCell = row.createCell(1);
            entryCell.setCellValue(StringUtils.isBlank(entryTimes.get(timeIndex).toString()) ? "00:00" : entryTimes.get(timeIndex).toString());
            entryCell.setCellStyle(timeStyle);
        }

        // Orario Uscita
        if (timeIndex < exitTimes.size()) {
            Cell exitCell = row.createCell(2);
            exitCell.setCellValue(StringUtils.isBlank(exitTimes.get(timeIndex).toString()) ? "00:00" : exitTimes.get(timeIndex).toString());
            exitCell.setCellStyle(timeStyle);
        }

        // Totale Ore
        if (timeIndex < totalHours.size()) {
            Cell totalHoursCell = row.createCell(3);
            totalHoursCell.setCellValue(StringUtils.isBlank(totalHours.get(timeIndex).toString()) ? "0.0" : totalHours.get(timeIndex).toString());
            totalHoursCell.setCellStyle(numberStyle);
        }

        // Note
        if (timeIndex < notes.size()) {
            Cell notesCell = row.createCell(4);
            notesCell.setCellValue(StringUtils.isBlank(notes.get(timeIndex)) ? "" : notes.get(timeIndex));
            notesCell.setCellStyle(textStyle);
        }
    }

    // Metodo per la creazione della riga di totale ore lavorate
    private static void createTotalRow(Sheet sheet, int rowIndex, List<Double> totalHoursList) {
        Row totalRow = sheet.createRow(rowIndex);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("Totale Ore Lavorate:");
        Cell totalHoursCell = totalRow.createCell(3);

        double totalHours = totalHoursList.stream().reduce(0d, Double::sum);
        totalHoursCell.setCellValue(totalHours);
    }
}

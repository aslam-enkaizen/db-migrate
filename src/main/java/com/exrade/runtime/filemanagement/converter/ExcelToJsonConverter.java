package com.exrade.runtime.filemanagement.converter;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author: Md. Aslam Hossain
 *
 */
public class ExcelToJsonConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelToJsonConverter.class);
	private File file = null;

	public ExcelToJsonConverter(File file) {
		this.file = file;
	}

	public static String convert(File file) throws IOException, EncryptedDocumentException {
		LOGGER.info("Enter into convert() in ExcelToJsonConverter class");
		String json = null;
		try {
			if (isExcelOrCsv(file)) {
				json = new ExcelToJsonConverter(file).convert();
				return json;
			}
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			LOGGER.error(e.getMessage());
		}
		return json;
	}

	public String convert() throws IOException, EncryptedDocumentException, InvalidFormatException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		InputStream inputStream = new FileInputStream(file);
		Workbook workbook = WorkbookFactory.create(inputStream);
		int numberOfSheets = workbook.getNumberOfSheets();
		boolean isNextSheet = true;
		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			if (sheet == null) {
				isNextSheet = false;
				continue;
			}
			stringBuilder.append("\"");
			stringBuilder.append(sheet.getSheetName().trim());
			stringBuilder.append("\":[");
			List<Object> headerData = new ArrayList<Object>();
			for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
				Row row = sheet.getRow(j);
				if (row == null) {
					continue;
				}
				boolean hasValues = false;
				Map<Object, Object> rowData = new HashMap<>();
				for (int k = 0; k < row.getLastCellNum(); k++) {
					Cell cell = row.getCell(k);
					if (cell != null) {
						Object value = cellToObject(cell);
						hasValues = hasValues || value != null;
						if (j == 0) {
							if (value != null) {
								headerData.add("\"" + getProperStringValue(value) + "\"");
							} else {
								headerData.add("\"" + "EMPTY_HEADER" + "\"");
							}
						} else {
							if (value != null) {
								rowData.put(headerData.get(k), "\"" + getProperStringValue(value) + "\"");
							} else {
								rowData.put(headerData.get(k), null);
							}
						}
					} else {
						if (j == 0) {
							headerData.add("\"" + "EMPTY_HEADER" + "\"");
						} else {
							rowData.put(headerData.get(k), null);
						}
					}
				}
				if (hasValues) {
					if (!rowData.isEmpty()) {
						stringBuilder.append(rowData);
						if (j != sheet.getLastRowNum()) {
							stringBuilder.append(",");
						}
					}
				}
				if (j == sheet.getLastRowNum()) {
					String lastCharacter = stringBuilder.toString().substring(stringBuilder.length() - 1);
					if (lastCharacter.equals(",")) {
						stringBuilder.deleteCharAt(stringBuilder.length() - 1);
					}
					stringBuilder.append("]");
				}
			}
			if (isNextSheet) {
				stringBuilder.append(",");
			}
		}
		stringBuilder.append("}");
		String finalJson = stringBuilder.toString().replace("=", ":").replace(",}", "}");
		return finalJson;
	}

	private Object cellToObject(Cell cell) {
		int type = cell.getCellType();
		if (type == Cell.CELL_TYPE_STRING) {
			return cleanString(cell.getStringCellValue());
		}
		if (type == Cell.CELL_TYPE_BOOLEAN) {
			return cell.getBooleanCellValue();
		}
		if (type == Cell.CELL_TYPE_NUMERIC) {
			if (cell.getCellStyle().getDataFormatString().contains("%")) {
				return cell.getNumericCellValue() * 100;
			}
			return numeric(cell);
		}
		if (type == Cell.CELL_TYPE_FORMULA) {
			switch (cell.getCachedFormulaResultType()) {
			case Cell.CELL_TYPE_NUMERIC:
				return numeric(cell);
			case Cell.CELL_TYPE_STRING:
				return cleanString(cell.getRichStringCellValue().toString());
			}
		}
		return null;
	}

	private String cleanString(String str) {
		return str.replace("\n", "").replace("\r", "");
	}

	private String getProperStringValue(Object value) {
		// left,right double quotation mark
		return value.toString().trim().replaceAll("[\u201c\u201d]", "\"").replace("\"", "\\\"");
	}

	private Object numeric(Cell cell) {
		if (HSSFDateUtil.isCellDateFormatted(cell)) {
			return cell.getDateCellValue();
		}
		return cell.getNumericCellValue();
	}

	private static Boolean isExcelOrCsv(File file) {
		LOGGER.info("Enter into isExcelOrCsv() in ExcelToJsonConverter class");
		boolean isExcelOrCsv = false;
		if (file.toString().endsWith(".xls") || file.toString().endsWith(".xlsx") || file.toString().endsWith(".csv")) {
			isExcelOrCsv = true;
		}
		return isExcelOrCsv;
	}

}

package reUsables;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class CommonScripts {
    final static Logger APP_LOGS = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
    static CommonScripts obj = new CommonScripts();

    private CommonScripts() {
    }

    public static CommonScripts on() {
        return obj;
    }

    public HashMap<String, String> string2Map(String string, String splitBy, String deLimtiBy) {
        HashMap<String, String> map = new HashMap<>();
        String[] pairs = string.split(splitBy);
        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            if (pair.contains(deLimtiBy)) {
                String[] keyValue = pair.split(deLimtiBy);
                map.put(keyValue[0], keyValue[1]);
            }
        }

        return map;
    }

    public boolean stringIsNullOrEmpty(String str) {
        boolean nullOrEmpty = false;
        if (str == null || str.isEmpty() || str.equalsIgnoreCase("null")
                || str.equals("") || str.equals(" ")) {
            nullOrEmpty = true;
        }
        return nullOrEmpty;
    }

    public String setStringasCellType(Cell cell) {
        String value = "";
        if (!cell.getCellTypeEnum().equals(CellType.BLANK)) {
            if (cell.getCellTypeEnum().equals(CellType.NUMERIC)
                    && DateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                value = dateFormat.format(cell.getDateCellValue());
            } else {
                cell.setCellType(CellType.STRING);
                value = cell.getStringCellValue();
            }
        } else {
            cell.setCellType(CellType.STRING);
            value = "null";
        }
        return value;
    }

    protected boolean hasSheet(Workbook workbook, String sheetName) {
        return workbook.getSheet(sheetName) != null;
    }

    public List<HashMap<String, String>> readExcel(String excelPath, String sheetName) {
        List<HashMap<String, String>> excelList = new ArrayList<>();
        Workbook wb = null;
        try {
            if (excelPath.toUpperCase().contains("xlsx".toUpperCase())) {

                wb = new XSSFWorkbook(new FileInputStream(excelPath));
            } else if (excelPath.toUpperCase().contains("xls".toUpperCase())) {
                wb = new HSSFWorkbook(new FileInputStream(excelPath));
            }
            if (hasSheet(wb, sheetName)) {
                Sheet sheet = wb.getSheet(sheetName);
                String[] headers = new String[(sheet.getRow(0).getLastCellNum())];
                for (int i = 0; i < headers.length; i++) {
                    if (!sheet.getRow(0).getCell(i).getCellTypeEnum().equals(CellType.BLANK)) {
                        headers[i] = setStringasCellType(
                                sheet.getRow(0)
                                        .getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                    }
                }
                int rows = sheet.getLastRowNum();
                for (int row = 1; row <= rows; row++) {
                    HashMap<String, String> excelValues = new HashMap<>(headers.length);
                    for (int column = 0; column < headers.length; column++) {
                        String value = setStringasCellType(
                                sheet.getRow(row)
                                        .getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                        excelValues.put(headers[column], value);
                    }
                    excelList.add(excelValues);
                }
            } else {
                APP_LOGS.info("Sheet is not available in the excel " + excelPath);
            }
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return excelList;
    }

    public HashMap<String, String> readExcelByKey(String excelPath, String sheetName, String key, String value) {
        List<HashMap<String, String>> excelList = new ArrayList<>();
        Workbook wb = null;
        HashMap<String, String> mapper = new HashMap<>();
        APP_LOGS.info(excelPath);
        try {
            if (excelPath.toUpperCase().contains("xlsx".toUpperCase())) {
                wb = new XSSFWorkbook(new FileInputStream(excelPath));
            } else if (excelPath.toUpperCase().contains("xls".toUpperCase())) {
                wb = new HSSFWorkbook(new FileInputStream(excelPath));
            }
            if (hasSheet(wb, sheetName)) {
                Sheet sheet = wb.getSheet(sheetName);
                String[] headers = new String[(sheet.getRow(0).getLastCellNum())];
                for (int i = 0; i < headers.length; i++) {
                    if (!sheet.getRow(0).getCell(i).getCellTypeEnum().equals(CellType.BLANK)) {
                        headers[i] = setStringasCellType(
                                sheet.getRow(0)
                                        .getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                    }
                }
                int rows = sheet.getLastRowNum();
                for (int row = 1; row <= rows; row++) {
                    HashMap<String, String> excelValues = new HashMap<>(headers.length);
                    for (int column = 0; column < headers.length; column++) {
                        String string = setStringasCellType(
                                sheet.getRow(row)
                                        .getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                        excelValues.put(headers[column], string);
                    }
                    excelList.add(excelValues);
                }
                for (int i = 0; i < excelList.size(); i++) {
                    if (excelList.get(i).containsKey(key) && excelList.get(i).get(key).equalsIgnoreCase(value)) {
                        mapper = excelList.get(i);
                        excelList.clear();
                        break;
                    }
                }
            } else {
                APP_LOGS.info("Sheet is not available in the excel " + excelPath);
            }
            wb.close();
        }
        catch (FileNotFoundException e) {
            APP_LOGS.info("File is not Available");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return mapper;
    }

    public boolean stringContainsNumber(String s) {
        return Pattern.compile("[0-9]").matcher(s).find();
    }

    public void writeExcel(List<HashMap<String, String>> datalist, String excelFilePath, String sheetName) throws Exception {
        FileHandler.on().createFreshFileorFolder(excelFilePath, false);
        Workbook workbook = null;
        if (excelFilePath.toUpperCase().contains("xlsx".toUpperCase())) {
            workbook = new XSSFWorkbook();
        } else if (excelFilePath.toUpperCase().contains("xls".toUpperCase())) {
            workbook = new HSSFWorkbook();
        }
        Sheet sheet = workbook.createSheet(sheetName);
        int col = 0;
        Row row = sheet.createRow(0);
        for (String key : datalist.get(0).keySet()) {
            row.createCell(col).setCellValue(key);
            for (int i = 0; i < datalist.size(); i++) {
                String value = datalist.get(i).get(key);
                if (!stringIsNullOrEmpty(value)) {
                    Row r = sheet.getRow(i + 1);
                    if (r == null) {
                        r = sheet.createRow(i + 1);
                    }
                    r.createCell(col).setCellValue(value);
                }
            }
            col++;
        }
        try (OutputStream outputStream = new FileOutputStream(excelFilePath)) {
            workbook.write(outputStream);
        }
    }

    public List deepCloneListOfHashMap(List<HashMap<String, String>> source) {
        List<HashMap<String, String>> result = new ArrayList<>();
        Iterator iterator = source.iterator();
        while (iterator.hasNext()) {
            HashMap<String, String> mapp = (HashMap<String, String>) iterator.next();
            HashMap<String, String> tempMap = new HashMap<>();
            for (Map.Entry<String, String> entry : mapp.entrySet()) {
                tempMap.put(entry.getKey(), entry.getValue());
            }
            result.add(tempMap);
        }
        return result;
    }
    public boolean checkAPPDB(String columnName,String existingValue,String actualValue){
        boolean status=false;
        if(MasterDriver.testData.get("Flag_Consol_Criteria")
                .equalsIgnoreCase("Computation based on Solo data")){
            status=existingValue.equalsIgnoreCase(actualValue);
        }
        return status;
    }
}

package com.brandslink.cloud.inbound.utils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.inbound.enums.ResponseCodeEnum;
import com.brandslink.cloud.inbound.service.impl.InboundClientOrderServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * excel解析
 * */
public class SimpleExcelUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleExcelUtil.class);
	 /**
     * 读取.xlsx 内容 
     * @param file
     * @return
     */
    public static List<ArrayList<String>> readXlsx (MultipartFile file) {
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        InputStream input = null;
        XSSFWorkbook wb = null;
        try {
            input = file.getInputStream();
            //创建文档
            wb = new XSSFWorkbook(input);
            ArrayList<String> rowList = null;
            int totoalRows = 0;//总行数
            int totalCells = 0;//总列数
            //读取sheet(页)
            for (int sheetIndex = 0 ; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
                XSSFSheet xssfSheet = wb.getSheetAt(sheetIndex);

                if (xssfSheet == null) {
                    continue;
                }
                totoalRows = xssfSheet.getLastRowNum();
                //读取行 
                for (int rowIndex = 1; rowIndex <= totoalRows; rowIndex++) {
                    XSSFRow xssfRow = xssfSheet.getRow(rowIndex);

                    if (xssfRow == null) {
                        continue;
                    }
                    rowList = new ArrayList<String>();
                    totalCells = xssfRow.getLastCellNum();

                    //读取列
                    for (int cellIndex = 0; cellIndex < totalCells; cellIndex++) {
                        XSSFCell xssfCell = xssfRow.getCell(cellIndex);
                        if (xssfCell == null) {
                            rowList.add("");
                        } else {
                            xssfCell.setCellType(CellType.STRING);
                            rowList.add(String.valueOf(xssfCell.getStringCellValue()));
                        }
                    }

                    list.add(rowList);

                }
            }
        } catch (OfficeXmlFileException e){
            LOGGER.error("上传的execl格式不正确");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200601);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if ( wb != null) {
                    wb.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
            }
        }

        return list;
    }


    /**
     * 读取 .xls内容
     * @param file
     * @return
     */
    public static List<ArrayList<String>> readXls (MultipartFile file)  {
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

        //创建输入流
        InputStream input = null;
        //创建文档
        HSSFWorkbook wb = null;

        try {
            input = file.getInputStream();
            //创建文档
            wb = new HSSFWorkbook(input);

            ArrayList<String> rowList = null;
            int totoalRows = 0;//总行数
            int totalCells = 0;//总列数
            //读取sheet(页)
            for (int sheetIndex = 0 ; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
                HSSFSheet hssfSheet = wb.getSheetAt(sheetIndex);

                if (hssfSheet == null) {
                    continue;
                }

                totoalRows = hssfSheet.getLastRowNum();
                //读取row
                for (int rowIndex = 1; rowIndex <= totoalRows; rowIndex++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowIndex);

                    if (hssfRow == null) {
                        continue;
                    }
                    rowList = new ArrayList<String>();
                    totalCells = hssfRow.getLastCellNum();

                    //读取列
                    for (int cellIndex = 0; cellIndex < totalCells; cellIndex++) {
                        HSSFCell hssfCell = hssfRow.getCell(cellIndex);
                        if (hssfCell == null) {
                            rowList.add("");
                        } else {
                            hssfCell.setCellType(CellType.STRING);
                            rowList.add(String.valueOf(hssfCell.getStringCellValue()));
                        }
                    }

                    list.add(rowList);

                }
            }
        } catch (OfficeXmlFileException e){
            LOGGER.error("上传的execl格式不正确");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_RK_200601);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if ( wb != null) {
                    wb.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
            }
        }
        return list;
    }



    /**
     * 获取文件类型
     * @param path
     * @return
     */
    public static String getPostfix (String path) {
        if (StringUtils.isBlank(path) || !path.contains(".")) {
            return null;
        }
        return path.substring(path.lastIndexOf(".") + 1, path.length()).trim();
    }

    

	/*
     * 获取当前时间作为号码批次
     */
    public static String getNumberBatch(){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String numberBatch = sdf.format(System.currentTimeMillis());
    	String[] splits = numberBatch.split(" ");
    	String nianYueRi = splits[0];
    	String[] nianYueRis = nianYueRi.split("-");
    	String NianYueRi = nianYueRis[0] + nianYueRis[1] + nianYueRis[2];
    	
    	String shiFenMiao = splits[1];
    	String[] shiFens = shiFenMiao.split(":");
    	String shiFen = shiFens[0] + shiFens[1];
    	
    	numberBatch = NianYueRi + shiFen;
    	return numberBatch;
    }

    /**
     * @description: 设置某些列的值只能输入预制的数据,显示下拉框.
     * @param sheet: 要设置的sheet.
     * @param explicitListValues: 下拉框显示的内容
     * @param firstRow: 开始行
     * @param lastRow: 结束行
     * @param firstCol: 开始列
     * @param lastCol: 结束列
     * @return 设置好的sheet.
     */
    public static void setValidationData(Sheet sheet, int firstRow, int lastRow,
                                         int firstCol, int lastCol, String[] explicitListValues) throws IllegalArgumentException {
        if (firstRow < 0 || lastRow < 0 || firstCol < 0 || lastCol < 0 || lastRow < firstRow || lastCol < firstCol) {
            throw new IllegalArgumentException("Wrong Row or Column index : " + firstRow + ":" + lastRow + ":" + firstCol + ":" + lastCol);
        }
        if (sheet instanceof XSSFSheet) {
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
            XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                    .createExplicitListConstraint(explicitListValues);
            CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
            XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
        } else if (sheet instanceof HSSFSheet) {
            CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
            DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(explicitListValues);
            DataValidation validation = new HSSFDataValidation(addressList, dvConstraint);
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
        }
    }

}

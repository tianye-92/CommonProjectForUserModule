package com.brandslink.cloud.inbound.utils;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.params.ExcelForEachParams;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

/**
 * @Author: luozheng
 * @BelongsPackage:com.rondaful.college.utils
 * @Date: 2019-06-20 11:09:46
 * @FileName:${FILENAME}
 * @Description:
 */
public class ExcelStyle  implements IExcelExportStyler {
//public class ExcelStyle {
//    public ExcelStyle(Workbook workbook) {
//        super(workbook);
//    }
//
//    @Override
//    public CellStyle getTitleStyle(short color) {
//        CellStyle titleStyle = workbook.createCellStyle();
//        titleStyle.setAlignment(HorizontalAlignment.CENTER);
//        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        titleStyle.setWrapText(true);
//        return titleStyle;
//    }
//
//    @Override
//    public CellStyle stringSeptailStyle(Workbook workbook, boolean isWarp) {
//        CellStyle style = workbook.createCellStyle();
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setDataFormat(STRING_FORMAT);
//        if (isWarp) {
//            style.setWrapText(true);
//        }
//        return style;
//    }
//
//    @Override
//    public CellStyle getHeaderStyle(short color) {
//        CellStyle titleStyle = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setFontHeightInPoints((short) 20);
//        titleStyle.setFont(font);
//        titleStyle.setAlignment(HorizontalAlignment.CENTER);
//        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        return titleStyle;
//    }
//
//    @Override
//    public CellStyle stringNoneStyle(Workbook workbook, boolean isWarp) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
////        font.setFontHeightInPoints((short) 15);
//        font.setFontName("楷体");
//        font.setBold(true);
//        style.setFont(font);
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setDataFormat(STRING_FORMAT);
//        if (isWarp) {
//            style.setWrapText(true);
//        }
//        return style;
//    }

    private static final short STRING_FORMAT = (short) BuiltinFormats.getBuiltinFormat("TEXT");
    private static final short FONT_SIZE_TEN = 10;
    //每列标题字体大小样式
    private static final short FONT_SIZE_ELEVEN = 12;
    //大标题字体大小样式
    private static final short FONT_SIZE_TWELVE = 16;
    /**
     * 大标题样式
     */
    private CellStyle headerStyle;
    /**
     * 每列标题样式
     */
    private CellStyle titleStyle;
    /**
     * 数据行样式
     */
    private CellStyle styles;

    public ExcelStyle(Workbook workbook) {
        super();
        this.init(workbook);
    }

    /**
     * 初始化样式
     *
     * @param workbook
     */
    private void init(Workbook workbook) {
        this.headerStyle = initHeaderStyle(workbook);
        this.titleStyle = initTitleStyle(workbook);
        this.styles = initStyles(workbook);
    }

    /**
     * 大标题样式
     *
     * @param color
     * @return
     */
    @Override
    public CellStyle getHeaderStyle(short color) {
        return headerStyle;
    }

    /**
     * 每列标题样式
     *
     * @param color
     * @return
     */
    @Override
    public CellStyle getTitleStyle(short color) {
        return titleStyle;
    }

    /**
     * 数据行样式
     *
     * @param parity 可以用来表示奇偶行
     * @param entity 数据内容
     * @return 样式
     */
    @Override
    public CellStyle getStyles(boolean parity, ExcelExportEntity entity) {
        return styles;
    }

    /**
     * 获取样式方法
     *
     * @param dataRow 数据行
     * @param obj     对象
     * @param data    数据
     */
    @Override
    public CellStyle getStyles(Cell cell, int dataRow, ExcelExportEntity entity, Object obj, Object data) {
        return getStyles(true, entity);
    }

    /**
     * 模板使用的样式设置
     */
    @Override
    public CellStyle getTemplateStyles(boolean isSingle, ExcelForEachParams excelForEachParams) {
        return null;
    }

    /**
     * 初始化--大标题样式
     *
     * @param workbook
     * @return
     */
    private CellStyle initHeaderStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());// 设置背景色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(getFont(workbook, FONT_SIZE_TWELVE, true));
        return style;
    }

    /**
     * 初始化--每列标题样式
     *
     * @param workbook
     * @return
     */
    private CellStyle initTitleStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFont(getFont(workbook, FONT_SIZE_ELEVEN, true));
        //背景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 初始化--数据行样式
     *
     * @param workbook
     * @return
     */
    private CellStyle initStyles(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFont(getFont(workbook, FONT_SIZE_TEN, false));
        style.setDataFormat(STRING_FORMAT);
        return style;
    }

    /**
     * 基础样式
     *
     * @return
     */
    private CellStyle getBaseCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //下边框
        style.setBorderBottom(BorderStyle.THIN);
        //左边框
        style.setBorderLeft(BorderStyle.THIN);
        //上边框
        style.setBorderTop(BorderStyle.THIN);
        //右边框
        style.setBorderRight(BorderStyle.THIN);
        //水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        //上下居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置自动换行
        style.setWrapText(true);
        return style;
    }

    /**
     * 字体样式
     *
     * @param size   字体大小
     * @param isBold 是否加粗
     * @return
     */
    private Font getFont(Workbook workbook, short size, boolean isBold) {
        Font font = workbook.createFont();
        //字体样式
        font.setFontName("楷体");
        //是否加粗
        font.setBold(isBold);
        //字体大小
        font.setFontHeightInPoints(size);
        return font;
    }
}

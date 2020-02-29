package com.yy.framework.commons.xls;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * 类名称: ExcelWriteUtils<br>
 * 类描述: excle导出工具类<br>
 * 修改时间: 2016年12月1日上午11:09:56<br>
 * @author mateng@eversec.cn
 */
public class ExcelWriteUtils {
	
	private static Logger logger = Logger.getLogger(ExcelWriteUtils.class.getName());
	
	/**
	 * 导出数据至excel
	 * @param headers excel的标题（标题包括标题和标题映射的bean属性）
	 * @param dataList 数据
	 * @param out 文件输出字节流
	 */
	public static void exportExcel(ExcelTitle[] headers, Collection<?> dataList, OutputStream out) {
		// 声明一个工作薄
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 生成一个表格
		XSSFSheet sheet = workbook.createSheet();
		
		// 标题的样式
		XSSFCellStyle titleStyle = getTitleCellStyle2007(workbook);
		// 内容的样式
		XSSFCellStyle contentStyle = getContentStyle2007(workbook);
		
		// 主标题
		XSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(headers[i].getTitle());
			cell.setCellStyle(titleStyle);
		}
		
		Iterator<?> iterator = dataList.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			index ++;
			row = sheet.createRow(index);
			Object t = iterator.next();
			for (short i = 0; i < headers.length; i++) {
				XSSFCell cell = row.createCell(i);
				String value = null;
				try {
					value = BeanUtils.getProperty(t, headers[i].getFiled());
				} catch (Exception e) {
					logger.warning(e.getMessage());
				}
				cell.setCellValue(value);
				cell.setCellStyle(contentStyle);
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 导出数据至excel
	 * @param headers excel的标题
	 * @param dataList 数据
	 * @param out 文件输出字节流
	 */
	public static void exportExcel(String[] headers, Collection<List<String>> dataList, OutputStream out) {
		// 声明一个工作薄
		XSSFWorkbook workBook = new XSSFWorkbook();
		// 生成一个表格
		XSSFSheet sheet = workBook.createSheet();
		
		// 标题的样式
		XSSFCellStyle titleStyle = getTitleCellStyle2007(workBook);
		// 内容的样式
		XSSFCellStyle contentStyle = getCellStyle2007(workBook);
		
		// 主标题
		XSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(titleStyle);
		}
		
		Iterator<List<String>> iterator = dataList.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			index ++;
			row = sheet.createRow(index);
			List<String> t = iterator.next();
			int size = t.size();
			for (short i = 0; i < headers.length; i++) {
				XSSFCell cell = row.createCell(i);
				String value = size > i ? t.get(i) : null;
				cell.setCellValue(value);
				cell.setCellStyle(contentStyle);
			}
		}
		try {
			workBook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <p>方法名: getCellStyle2007</p>
	 * <p>描述: 设置单元格样式</p>
	 * <p>修改时间: 2017年6月23日 下午6:06:44</p>  
	 * @author wangle@eversec.cn  
	 * @param workBook
	 * @return
	 */
	public static XSSFCellStyle getCellStyle2007(XSSFWorkbook workBook) {
		// 设置样式
		XSSFCellStyle cellStyle = workBook.createCellStyle();// 创建样式
		cellStyle.setAlignment(CellStyle.ALIGN_LEFT);// 水平方向对其方式 居左
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直方向的对齐方式
		// 边框实线
		cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);// 下边框
		cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);// 左边框
		cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);// 右边框
		cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);// 上边框
		
		//设置自动换行
		cellStyle.setWrapText(true);
		
		return cellStyle;
	}

	/**
	 * <p>方法名: getTitleCellStyle2007</p>
	 * <p>描述: 设置标题样式</p>
	 * <p>修改时间: 2017年6月23日 下午6:07:00</p>  
	 * @author wangle@eversec.cn  
	 * @param workBook
	 * @return
	 */
	private static XSSFCellStyle getTitleCellStyle2007(XSSFWorkbook workBook) {
		// 设置样式
		XSSFCellStyle cellStyle = workBook.createCellStyle();// 创建样式
		cellStyle.setAlignment(CellStyle.ALIGN_LEFT);// 居左
		// 边框实线
		cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);// 下边框
		cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);// 左边框
		cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);// 右边框
		cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);// 上边框
		
		//背景色
		cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		//字体
		XSSFFont font = workBook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);//粗体显示
		cellStyle.setFont(font);
		
		return cellStyle;
	}

	/**
	 * <p>方法名: getContentStyle2007</p>
	 * <p>设置内容样式</p>
	 * <p>修改时间: 2017年6月23日 下午6:07:16</p>  
	 * @author wangle@eversec.cn  
	 * @param workbook
	 * @return
	 */
	private static XSSFCellStyle getContentStyle2007(XSSFWorkbook workbook) {
		XSSFCellStyle contentStyle = workbook.createCellStyle();
		contentStyle.setFillForegroundColor(HSSFColor.WHITE.index);
		contentStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		contentStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		contentStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		
		// 内容的字体
		XSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.BLACK.index);
		font.setFontHeightInPoints((short) 12);
//		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		contentStyle.setFont(font);
		return contentStyle;
	}


	

	public static void main(String[] args) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < 10; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			for (int j = 0; j < 5; j++) {
				map.put("filed" + j, j);
			}
			dataList.add(map);
		}
		
		ExcelTitle[] headers = new ExcelTitle[] {
			new ExcelTitle("第一列", "filed0"),
			new ExcelTitle("第二列", "filed1"),
			new ExcelTitle("第三列", "filed2"),
			new ExcelTitle("第四列", "filed3")
		};
		
		try {
			System.out.println("============开始导出============");
			FileOutputStream out = new FileOutputStream("D:/导出测试.xls");
			ExcelWriteUtils.exportExcel(headers, dataList, out);
			System.out.println("============导出成功============");
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

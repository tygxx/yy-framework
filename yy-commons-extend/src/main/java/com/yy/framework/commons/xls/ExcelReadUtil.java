package com.yy.framework.commons.xls;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 类名称: ExcelReadUtil<br>
 * 类描述: xls解析工具类<br>
 * 修改时间: 2016年12月1日上午11:09:25<br>
 * @author mateng@eversec.cn
 */
public class ExcelReadUtil {
	
	private static Logger logger = Logger.getLogger(ExcelReadUtil.class.getName());
	
	/**
	 * 读文件为list，目前只支持读第一个sheet
	 * @param file xls文件
	 * @param startRow 从第几行还是读
	 * @param clazz	每一行的数据需要映射的bean
	 * @param fileds	需要映射的bean的属性数组
	 * @return
	 * @throws IOException
	 */
	public static <T> List<T> loadDataFromExcel(File file, int startRow, Class<T> clazz, String[] fileds) throws IOException {
		List<List<Object>> list = loadDataFromExcel(file, startRow);
		Field[] fs = clazz.getDeclaredFields();
		List<T> dataList = new ArrayList<>(list.size());
		for (List<Object> row : list) {
			int count = Math.min(row.size(), fileds.length);
			dataList.add(parseRowToBean(row, clazz, fs, fileds, count));
		}
		return dataList;
	}
	
	/**
	 * 读xls文件为list，目前只支持读第一个sheet
	 * @param file xls文件
	 * @param startRow 从第几行还是读
	 * @return
	 * @throws IOException
	 */
	public static List<List<Object>> loadDataFromExcel(File file, int startRow) throws IOException {
		String fileName = file.getName();
		InputStream is = new FileInputStream(file);
		List<List<Object>> list = loadDataFromExcel(is, fileName, startRow);
		IOUtils.closeQuietly(is);
		return list;
	}
	
	/**
	 * @param is xls文件的字节流
	 * @param fileName 原始的文件名称
	 * @param startRow 从第几行开始读
	 * @param clazz 每一行的数据需要映射的bean
	 * @param fileds 需要映射的bean的属性数组
	 * @return
	 * @throws IOException
	 */
	public static <T> List<T> loadDataFromExcel(InputStream is, String fileName, int startRow, Class<T> clazz, String[] fileds) throws IOException {
		List<List<Object>> list = loadDataFromExcel(is, fileName, startRow);
		Field[] fs = clazz.getDeclaredFields();
		List<T> dataList = new ArrayList<>(list.size());
		for (List<Object> row : list) {
			int count = Math.min(row.size(), fileds.length);
			dataList.add(parseRowToBean(row, clazz, fs, fileds, count));
		}
		return dataList;
	}
	
	/**
	 * 读xls文件为List，目前只支持读第一个sheet
	 * @param inputStream xls文件的字节流
	 * @param fileName 原始的文件名称
	 * @param startRow	从第几行开始读
	 * @return
	 * @throws IOException
	 */
	public static List<List<Object>> loadDataFromExcel(InputStream inputStream, String fileName, int startRow) throws IOException {
		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
		Workbook wb = null;
		if ("xls".equals(extension)) {
			wb = new HSSFWorkbook(inputStream);
		} else if ("xlsx".equals(extension) || "xlsm".equals(extension)) {
			wb = new XSSFWorkbook(inputStream);
		}else {
			throw new RuntimeException("无法解析改文件类型：" + fileName);
		}
		
		List<List<Object>> dataset = new ArrayList<List<Object>>();
		
		Sheet sheet = wb.getSheetAt(0);
		int rowNum = sheet.getLastRowNum();
		Row row = sheet.getRow(startRow);
		int colNum = row.getLastCellNum();
		List<Object> rowData = null;
		for (int i = startRow; i <= rowNum; i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			rowData = new ArrayList<Object>();
			int j = 0;
			while (j < colNum) {
				Object s = getCellValue(row.getCell(j));
				rowData.add(s == null ? null : s);
				j++;
			}
			dataset.add(rowData);
		}
		wb = null;
		return dataset;
	}
	
	private static Object getCellValue(Cell hssfCell) {
		if(hssfCell == null) {
			return null;
		}
		if (hssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return hssfCell.getBooleanCellValue();
		} 
		if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return hssfCell.getNumericCellValue();
		} 
		return StringUtils.trim(hssfCell.getStringCellValue());
	}
	
	private static <T> T parseRowToBean(List<Object> row, Class<T> clazz, Field[] fs, String[] fileds, int count) {
		T bean = null;
		try {
			bean = (T) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("实例化模板类发生异常。", e);
		}
		for (int i = 0; i < count; i++) {
			Object value = row.get(i);
			if(value == null) {
				continue;
			}
			if(value instanceof String && StringUtils.isBlank((String) value)) {
				continue;
			}
			String fieldName = fileds[i];
			try {
				if(fieldName.indexOf(".") != -1) {
					StringBuffer fn = new StringBuffer();
					String[] fieldArray = fieldName.split("\\.");
					for (int j = 0; j < fieldArray.length; j++) {
						if(j == 0) {
							fn.append(fieldArray[j]);
						}else if(j < fieldArray.length - 1) {
							fn.append("." + fieldArray[j]);
						}else {
							continue;
						}
						if(PropertyUtils.getNestedProperty(bean, fn.toString()) == null) {
							Object o = PropertyUtils.getPropertyType(bean, fn.toString()).newInstance();
							PropertyUtils.setNestedProperty(bean, fn.toString(), o);
						}
					}
				}
				BeanUtils.setProperty(bean, fieldName, value);
			} catch (Exception e) {
				logger.warning(e.getMessage());
			}
		}
		return bean;
	}
	
	public static void main(String[] args) {
		File file = new File("D:/wKiJZ1ljir6ACSoTAAnHPRXjZT880.xlsx");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		List<List<Object>> list = null;
		try {
			list = loadDataFromExcel(fis, file.getName(), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(list);
		
		List<ExcelTestDto> dtos = null;
		try {
			dtos = loadDataFromExcel(file, 1, ExcelTestDto.class, new String[]{
					"filed1", "filed2", "filed3"
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(dtos);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

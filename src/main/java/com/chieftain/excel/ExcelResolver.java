/**
 * 
 */
package com.chieftain.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Goofy
 * 解析Excel文件
 * *日期万能处理方案：
	所有日期格式都可以通过getDataFormat()值来判断
	yyyy-MM-dd-----	14
	yyyy年m月d日---	31
	yyyy年m月-------	57
	m月d日  ----------	58
	HH:mm-----------	20
	h时mm分  -------	32
 */
public class ExcelResolver {

	/**
	 * 
	 * @param file Excel文件
	 * @param rowStart 起始行号
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static List<Object[]> read(File file,int rowStart) throws FileNotFoundException, IOException {

		InputStream is = new FileInputStream(file);

		Workbook wb = new XSSFWorkbook(is);

		Sheet sheet = wb.getSheetAt(0);

		List<Object[]> list = new ArrayList<Object[]>();


		int rowIndex = 0;
		int columnIndex=0;
		
		Cell cell = null;
		Row row = null;
		Object o[]=null;
		for (Iterator<Row> it = sheet.rowIterator(); it.hasNext();) {
			row = it.next();
			
			columnIndex=row.getLastCellNum();
			o=new Object[columnIndex];
			rowIndex=row.getRowNum();
			
			if (rowIndex<rowStart) {
				continue;
			}

			if (row == null) {
				break;
			}

			for (int i = 0; i < columnIndex; i++) {
				cell = row.getCell(i);
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BLANK:
					o[i]=null;
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					o[i]=cell.getBooleanCellValue();
					break;
				case Cell.CELL_TYPE_ERROR:
					o[i]=cell.getErrorCellValue();
					break;
				case Cell.CELL_TYPE_FORMULA:
					o[i]=cell.getCellFormula();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if(HSSFDateUtil.isCellDateFormatted(cell)){
						  SimpleDateFormat sdf = null;  
			                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat  
			                        .getBuiltinFormat("h:mm")) {  
			                    sdf = new SimpleDateFormat("HH:mm");  
			                } else {// 日期  
			                    sdf = new SimpleDateFormat("yyyy-MM-dd");  
			                }  
			                Date date = cell.getDateCellValue();  
			                o[i] = sdf.format(date);
					}else if(cell.getCellStyle().getDataFormat() == 58){
						// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)  
		                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		                double value = cell.getNumericCellValue();  
		                Date date = org.apache.poi.ss.usermodel.DateUtil  
		                        .getJavaDate(value);  
		                o[i] = sdf.format(date); 
					}else {  
		                double value = cell.getNumericCellValue();  
		                CellStyle style = cell.getCellStyle();  
		                DecimalFormat format = new DecimalFormat();  
		                String temp = style.getDataFormatString();  
		                // 单元格设置成常规  
		                if (temp.equals("General")) {  
		                    format.applyPattern("#");  
		                }  
		                o[i] = format.format(value);  
		            }  
					break;
				case Cell.CELL_TYPE_STRING:
					o[i]=cell.getStringCellValue();
					break;
				default:
					o[i]=null;
					break;
				}
			}
			list.add(o);
		}
		return list;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		List<Object[]> list=ExcelResolver.read(new File("D:\\x.xlsx"), 0);
		System.out.println(list);
	}
	
}

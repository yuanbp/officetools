/**
 *
 */
package com.chieftain.excel;

import com.chieftain.utils.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Goofy
 *         Excel内容转化成Bean
 */
public class ExcelToBean {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sd2 = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat sd3 = new SimpleDateFormat("yyyy.MM.dd");
    private int etimes = 0;

    /**
     * 处理经过校验后的workbook
     * @param edf
     * @param wb
     * @param clazz
     * @param readSheetIndex
     * @param titleRowIndex
     * @param skipRows
     * @param <E>
     * @return
     * @throws Exception
     */
    public <E> List<E> readFromFile(ExcelDataFormatter edf, Workbook wb, Class<?> clazz, Integer readSheetIndex, Integer titleRowIndex, List<Integer> skipRows) throws Exception {

        List<Field> fields = ReflectUtils.getFields(clazz, true);

        Map<String, String> textToKey = new HashMap<String, String>();

        Excel _excel = null;
        for (Field field : fields) {
            _excel = field.getAnnotation(Excel.class);
            if (_excel == null || _excel.skip() == true) {
                continue;
            }
            textToKey.put(_excel.name(), field.getName());
        }

        Sheet sheet = wb.getSheetAt(readSheetIndex);
        Row title = sheet.getRow(titleRowIndex);
        // 标题数组，后面用到，根据索引去标题名称，通过标题名称去字段名称用到 textToKey
        String[] titles = new String[title.getPhysicalNumberOfCells()];
        for (int i = 0; i < title.getPhysicalNumberOfCells(); i++) {
            titles[i] = title.getCell(i).getStringCellValue();
        }

        List<E> list = new ArrayList<E>();

        E e = null;

        int rowIndex = 0;
        int columnCount = titles.length;
        Cell cell = null;
        Row row = null;

        for (Iterator<Row> it = sheet.rowIterator(); it.hasNext(); ) {

            row = it.next();
            if (rowIndex == titleRowIndex) {
                rowIndex++;
                continue;
            }

            //跳过设置好的行
            if (null != skipRows && skipRows.size() > 0) {
                boolean isSkip = false;
                for (int s = 0; s < skipRows.size(); s++) {
                    if (rowIndex == skipRows.get(s)) {
                        rowIndex++;
                        isSkip = true;
                        break;
                    }
                }
                if (isSkip) {
                    continue;
                }
            }

            rowIndex++;


            if (row == null) {
                break;
            }

            //判断当前行是否空行
            if (isRowEmpty(row)) {
                continue;
            }

            e = (E) clazz.newInstance();

            for (int i = 0; i < columnCount; i++) {
                //System.out.println("@#####EXCEL读取到第[" + readSheetIndex + "]读取到第[" + rowIndex + "]行！");
                cell = row.getCell(i);
                etimes = 0;
                if (null != cell) {
                    //强制转换所有的列类型为文本类型
                    if (cell.getCellType() != XSSFCell.CELL_TYPE_STRING) {
                        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    }
                    readCellContent(textToKey.get(titles[i]), fields, cell, e, edf);
                }
            }
            list.add(e);
        }
        return list;
    }


    /**
     * 从文件读取数据，最好是所有的单元格都是文本格式，日期格式要求yyyy-MM-dd HH:mm:ss,布尔类型0：真，1：假
     *
     * @param edf  数据格式化
     * @param file Excel文件，支持xlsx后缀，xls的没写，基本一样
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <E> List<E> readFromFile(ExcelDataFormatter edf, File file, Class<?> clazz, Integer readSheetIndex, Integer titleRowIndex, List<Integer> skipRows) throws Exception {

        List<Field> fields = ReflectUtils.getFields(clazz, true);

        Map<String, String> textToKey = new HashMap<String, String>();

        Excel _excel = null;
        for (Field field : fields) {
            _excel = field.getAnnotation(Excel.class);
            if (_excel == null || _excel.skip() == true) {
                continue;
            }
            textToKey.put(_excel.name(), field.getName());
        }

        InputStream is = new FileInputStream(file);

        Workbook wb = create(is);

        Sheet sheet = wb.getSheetAt(readSheetIndex);
        Row title = sheet.getRow(titleRowIndex);
        // 标题数组，后面用到，根据索引去标题名称，通过标题名称去字段名称用到 textToKey
        String[] titles = new String[title.getPhysicalNumberOfCells()];
        for (int i = 0; i < title.getPhysicalNumberOfCells(); i++) {
            titles[i] = title.getCell(i).getStringCellValue();
        }

        List<E> list = new ArrayList<E>();

        E e = null;

        int rowIndex = 0;
        int columnCount = titles.length;
        Cell cell = null;
        Row row = null;

        for (Iterator<Row> it = sheet.rowIterator(); it.hasNext(); ) {

            row = it.next();
            if (rowIndex == titleRowIndex) {
                rowIndex++;
                continue;
            }

            //跳过设置好的行
            if (null != skipRows && skipRows.size() > 0) {
                boolean isSkip = false;
                for (int s = 0; s < skipRows.size(); s++) {
                    if (rowIndex == skipRows.get(s)) {
                        rowIndex++;
                        isSkip = true;
                        break;
                    }
                }
                if (isSkip) {
                    continue;
                }
            }

            rowIndex++;


            if (row == null) {
                break;
            }

            //判断当前行是否空行
            if (isRowEmpty(row)) {
                continue;
            }

            e = (E) clazz.newInstance();

            for (int i = 0; i < columnCount; i++) {
                //System.out.println("@#####EXCEL读取到第[" + readSheetIndex + "]读取到第[" + rowIndex + "]行！");
                cell = row.getCell(i);
                etimes = 0;
                if (null != cell) {
                    //强制转换所有的列类型为文本类型
                    if (cell.getCellType() != XSSFCell.CELL_TYPE_STRING) {
                        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    }
                    readCellContent(textToKey.get(titles[i]), fields, cell, e, edf);
                }
            }
            list.add(e);
        }
        return list;
    }

    public <E> List<E> readFromFile(ExcelDataFormatter edf, InputStream inputStream, Class<?> clazz, Integer readSheetIndex, Integer titleRowIndex, List<Integer> skipRows) throws Exception {

        List<Field> fields = ReflectUtils.getFields(clazz, true);

        Map<String, String> textToKey = new HashMap<String, String>();

        Excel _excel = null;
        for (Field field : fields) {
            _excel = field.getAnnotation(Excel.class);
            if (_excel == null || _excel.skip() == true) {
                continue;
            }
            textToKey.put(_excel.name(), field.getName());
        }

        InputStream is = inputStream;

        Workbook wb = create(is);

        Sheet sheet = wb.getSheetAt(readSheetIndex);
        Row title = sheet.getRow(titleRowIndex);
        // 标题数组，后面用到，根据索引去标题名称，通过标题名称去字段名称用到 textToKey
        String[] titles = new String[title.getPhysicalNumberOfCells()];
        for (int i = 0; i < title.getPhysicalNumberOfCells(); i++) {
            titles[i] = title.getCell(i).getStringCellValue();
        }

        List<E> list = new ArrayList<E>();

        E e = null;

        int rowIndex = 0;
        int columnCount = titles.length;
        Cell cell = null;
        Row row = null;

        for (Iterator<Row> it = sheet.rowIterator(); it.hasNext(); ) {

            row = it.next();
            if (rowIndex == titleRowIndex) {
                rowIndex++;
                continue;
            }

            //跳过设置好的行
            if (null != skipRows && skipRows.size() > 0) {
                boolean isSkip = false;
                for (int s = 0; s < skipRows.size(); s++) {
                    if (rowIndex == skipRows.get(s)) {
                        rowIndex++;
                        isSkip = true;
                        break;
                    }
                }
                if (isSkip) {
                    continue;
                }
            }

            rowIndex++;


            if (row == null) {
                break;
            }

            //判断当前行是否空行
            if (isRowEmpty(row)) {
                continue;
            }

            e = (E) clazz.newInstance();

            for (int i = 0; i < columnCount; i++) {
                //System.out.println("@#####EXCEL读取到第[" + readSheetIndex + "]读取到第[" + rowIndex + "]行！");
                cell = row.getCell(i);
                etimes = 0;
                if (null != cell) {
                    //强制转换所有的列类型为文本类型
                    if (cell.getCellType() != XSSFCell.CELL_TYPE_STRING) {
                        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    }
                    readCellContent(textToKey.get(titles[i]), fields, cell, e, edf);
                }
            }
            list.add(e);
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
    }

    /**
     * 从单元格读取数据，根据不同的数据类型，使用不同的方式读取<br>
     * 有时候经常和我们期待的数据格式不一样，会报异常，<br>
     * 我们这里采取强硬的方式<br>
     * 使用各种方法，知道尝试到读到数据为止，然后根据Bean的数据类型，进行相应的转换<br>
     * 如果尝试完了（总共7次），还是不能得到数据，那么抛个异常出来，没办法了
     *
     * @param key    当前单元格对应的Bean字段
     * @param fields Bean所有的字段数组
     * @param cell   单元格对象
     * @param obj
     * @throws Exception
     */
    public void readCellContent(String key, List<Field> fields, Cell cell, Object obj, ExcelDataFormatter edf) throws Exception {

        Object o = null;
        try {
            switch (cell.getCellType()) {
                case XSSFCell.CELL_TYPE_BOOLEAN:
                    o = cell.getBooleanCellValue();
                    break;
                case XSSFCell.CELL_TYPE_NUMERIC:
                    o = cell.getNumericCellValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        o = DateUtil.getJavaDate(cell.getNumericCellValue());
                    }
                    o = doPointLongValue(o);
                    break;
                case XSSFCell.CELL_TYPE_STRING:
                    o = cell.getStringCellValue().trim();
                    break;
                case XSSFCell.CELL_TYPE_ERROR:
                    o = cell.getErrorCellValue();
                    break;
                case XSSFCell.CELL_TYPE_BLANK:
                    o = null;
                    break;
                case XSSFCell.CELL_TYPE_FORMULA:
                    o = cell.getCellFormula();
                    break;
                default:
                    o = null;
                    break;
            }

            if (o == null) {
                return;
            }
            if ("".equals(o)) {
                return;
            }
            if ("".equals(o.toString().trim())) {
                return;
            }
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().equals(key)) {
                    Boolean bool = true;
                    Map<String, String> map = null;
                    if (edf == null) {
                        bool = false;
                    } else {
                        map = edf.get(field.getName());
                        if (map == null) {
                            bool = false;
                        }
                    }

                    if (field.getType().equals(Date.class)) {
                        if (o.getClass().equals(Date.class)) {
                            field.set(obj, o);
                        } else {
                            //field.set(obj, sdf.parse(o.toString()));
                            if (o.toString().indexOf("/") != -1) {
                                field.set(obj, sd2.parse(o.toString()));
                            } else if (o.toString().indexOf("-") != -1) {
                                field.set(obj, sd.parse(o.toString()));
                            } else if(o.toString().indexOf(":") != -1){
                                field.set(obj, sdf.parse(o.toString()));
                            }else if(o.toString().indexOf(".") != -1){
                                field.set(obj, sd3.parse(o.toString()));
                            }else {
                                field.set(obj, sd.parse(o.toString()));
                            }
                        }
                    } else if (field.getType().equals(String.class)) {
                        if (o.getClass().equals(String.class)) {
                            field.set(obj, o);
                        } else {
                            field.set(obj, o.toString());
                        }
                    } else if (field.getType().equals(Long.class)) {
                        if (o.getClass().equals(Long.class)) {
                            field.set(obj, o);
                        } else if (o.getClass().equals(Double.class)) {
                            field.set(obj, new Double(0).longValue());
                        } else {
                            field.set(obj, Long.parseLong(o.toString()));
                        }
                    } else if (field.getType().equals(Integer.class)) {
                        if (o.getClass().equals(Integer.class)) {
                            field.set(obj, o);
                        } else {
                            // 检查是否需要转换
                            if (bool) {
                                field.set(obj, map.get(o.toString()) != null ? Integer.parseInt(map.get(o.toString())) : Integer.parseInt(o.toString()));
                            } else {
                                field.set(obj, Integer.parseInt(o.toString()));
                            }

                        }
                    } else if (field.getType().equals(BigDecimal.class)) {
                        if (o.getClass().equals(BigDecimal.class)) {
                            field.set(obj, o);
                        } else {
                            field.set(obj, BigDecimal.valueOf(Double.parseDouble(o.toString())));
                        }
                    } else if (field.getType().equals(Boolean.class)) {
                        if (o.getClass().equals(Boolean.class)) {
                            field.set(obj, o);
                        } else {
                            // 检查是否需要转换
                            if (bool) {
                                field.set(obj, map.get(o.toString()) != null ? Boolean.parseBoolean(map.get(o.toString())) : Boolean.parseBoolean(o.toString()));
                            } else {
                                field.set(obj, Boolean.parseBoolean(o.toString()));
                            }
                        }
                    } else if (field.getType().equals(Float.class)) {
                        if (o.getClass().equals(Float.class)) {
                            field.set(obj, o);
                        } else {
                            field.set(obj, Float.parseFloat(o.toString()));
                        }
                    } else if (field.getType().equals(Double.class)) {
                        if (o.getClass().equals(Double.class)) {
                            field.set(obj, o);
                        } else {
                            field.set(obj, Double.parseDouble(o.toString()));
                        }

                    }

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // 如果还是读到的数据格式还是不对，只能放弃了
            if (etimes > 7) {
                throw ex;
            }
            etimes++;
            if (o == null) {
                readCellContent(key, fields, cell, obj, edf);
            }
            throw ex;
        }
    }

    /**
     * 更高效的与模板比对title，检查数据excel是否与模板一致
     * @param wb
     * @param modelWb
     * @param readSheetIndex
     * @param titleRowIndex
     * @return
     * @throws Exception
     */
    public boolean checkTitle(Workbook wb, Workbook modelWb, Integer readSheetIndex, Integer titleRowIndex) throws Exception {
        boolean flag = true;

        Sheet sheet = wb.getSheetAt(readSheetIndex);
        Row title = sheet.getRow(titleRowIndex);
        Sheet modelSheet = modelWb.getSheetAt(readSheetIndex);
        Row modelTitle = modelSheet.getRow(titleRowIndex);

        Map<String, String> titleMap = new HashMap<String, String>();
        for (int i = 0; i < modelTitle.getPhysicalNumberOfCells(); i++) {
            titleMap.put(modelTitle.getCell(i).getStringCellValue(),null);
        }

        for (int i = 0; i < title.getPhysicalNumberOfCells(); i++) {
            if(!titleMap.containsKey(title.getCell(i).getStringCellValue())){
                flag = false;
                break;
            }
        }

        return flag;
    }

    public boolean checkTitle(ExcelDataFormatter edf, File file, File modelFile, Integer readSheetIndex, Integer titleRowIndex) throws Exception {
        boolean flag = true;

        InputStream is = new FileInputStream(file);
        Workbook wb = create(is);
        Sheet sheet = wb.getSheetAt(readSheetIndex);
        Row title = sheet.getRow(titleRowIndex);
        // 标题数组，后面用到，根据索引去标题名称，通过标题名称去字段名称用到 textToKey
        String[] titles = new String[title.getPhysicalNumberOfCells()];
        for (int i = 0; i < title.getPhysicalNumberOfCells(); i++) {
            titles[i] = title.getCell(i).getStringCellValue();
        }
        is.close();
        InputStream modelIs = new FileInputStream(modelFile);

        Workbook modelWb = create(modelIs);
        Sheet modelSheet = modelWb.getSheetAt(readSheetIndex);
        Row modelTitle = modelSheet.getRow(titleRowIndex);
        // 标题数组，后面用到，根据索引去标题名称，通过标题名称去字段名称用到 textToKey
        String[] modelTitles = new String[modelTitle.getPhysicalNumberOfCells()];
        for (int i = 0; i < modelTitle.getPhysicalNumberOfCells(); i++) {
            modelTitles[i] = modelTitle.getCell(i).getStringCellValue();
        }
        modelIs.close();

        if(title.getPhysicalNumberOfCells() != modelTitle.getPhysicalNumberOfCells()){
            flag=false;
            return flag;
        }

        for (int f = 0; f < titles.length; f++) {
            boolean cellflag = false;
            for (int m = 0; m < modelTitles.length; m++) {
                if (modelTitles[m].equals(titles[f])) {
                    cellflag = true;
                    break;
                }
            }
            if (!cellflag) {
                flag = false;
                break;
            } else {
                continue;
            }
        }
        return flag;
    }

    /**
     * 根据类别创建workbook（xls xlsx）
     * @param in
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public Workbook create(InputStream in) throws IOException, InvalidFormatException {
        if (!in.markSupported()) {
            in = new PushbackInputStream(in, 8);
        }
        if (POIFSFileSystem.hasPOIFSHeader(in)) {
            return new HSSFWorkbook(in);
        }
        if (POIXMLDocument.hasOOXMLHeader(in)) {
            return new XSSFWorkbook(OPCPackage.open(in));
        }
        throw new IllegalArgumentException("你的excel版本目前poi解析不了");
    }

    /**
     * @param object
     * @return
     * doPointLongValue
     * 对于取EXCEL中数值小数位后n.99999999999这类问题进行四舍五入保留5位小数进行处理
     * @return: Object
     */
    public Object doPointLongValue(Object object) {
        Object o = object;
        String str = null;
        if (o != null) {
            str = o.toString();
        }

        if (StringUtils.isNotBlank(str)) {
            if (str.indexOf(".") != -1) {
                if (str.substring(str.indexOf(".") + 1).length() > 5) {
                    BigDecimal bg = new BigDecimal(o.toString());
                    bg = bg.setScale(5, BigDecimal.ROUND_HALF_UP);
                    o = bg;
                }
            }
        }
        return o;
    }

    /**
     * 判断是否空行
     *
     * @param row
     * @return
     */
    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    public int rowCount(File file, Integer readSheetIndex, Integer titleRowIndex,List<Integer> skipRows) throws Exception {
        int rowCount = 0;
        InputStream is = null;
        try{
            is = new FileInputStream(file);
            Workbook wb = create(is);
            Sheet sheet = wb.getSheetAt(readSheetIndex);
            rowCount = sheet.getLastRowNum()+1;
            if(null != skipRows && skipRows.size()>0){
                rowCount = rowCount-skipRows.size();
            }
            if(titleRowIndex>=0){
                rowCount = rowCount-1;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            if(null != is){
                is.close();
            }
        }
        return rowCount;
    }

}

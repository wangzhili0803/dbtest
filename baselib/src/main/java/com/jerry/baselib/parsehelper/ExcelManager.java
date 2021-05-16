package com.jerry.baselib.parsehelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.jerry.baselib.common.util.CollectionUtils;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by 请叫我张懂 on 2017/9/25.
 */

public class ExcelManager {

    /**
     * 将list集合转成Excel文件
     *
     * @param list 对象集合
     * @param fileName 输出路径
     * @return 返回文件路径
     */
    public static boolean createExcel(List<?> list, String fileName) {
        if (!CollectionUtils.isEmpty(list)) {
            Object o = list.get(0);
            Class<?> clazz = o.getClass();
            String className = clazz.getSimpleName();
            Field[] fields = clazz.getDeclaredFields();    //这里通过反射获取字段数组

            WritableWorkbook book = null;
            File file = null;
            try {
                file = new File(fileName);
                book = Workbook.createWorkbook(file);  //创建xls文件
                WritableSheet sheet = book.createSheet(className, 0);
                int i = 0;  //列
                int j = 0;  //行
                for (Field f : fields) {
                    j = 0;
                    Label label = new Label(i, j, f.getName());   //这里把字段名称写入excel第一行中
                    sheet.addCell(label);
                    j = 1;
                    for (Object obj : list) {
                        Object temp = getFieldValueByName(f.getName(), obj);
                        String strTemp = "";
                        if (temp != null) {
                            strTemp = temp.toString();
                        }
                        sheet.addCell(new Label(i, j, strTemp));  //把每个对象此字段的属性写入这一列excel中
                        j++;
                    }
                    i++;
                }
                book.write();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (book != null) {
                    try {
                        book.close();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;   //最后输出文件路径
    }

    /**
     * 获取属性值
     *
     * @param fieldName 字段名称
     * @param o 对象
     * @return Object
     */
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);    //获取方法名
            Method method = o.getClass().getMethod(getter);  //获取方法对象
            Object value = method.invoke(o);    //用invoke调用此对象的get字段方法
            return value;  //返回值
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

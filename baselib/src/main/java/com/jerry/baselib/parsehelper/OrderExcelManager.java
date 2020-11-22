package com.jerry.baselib.parsehelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.os.Environment;
import androidx.collection.ArraySet;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.jerry.baselib.common.bean.Order;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.ToastUtil;
import com.jerry.baselib.greendao.OrderDao.Properties;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.xmlpull.v1.XmlPullParser;

/**
 * Created by 请叫我张懂 on 2017/9/25.
 */

public class OrderExcelManager {

    private static final String SHAREDSTRINGS = "xl/sharedStrings.xml";
    private static final String DIRSHEET = "xl/worksheets/";
    private static final String ENDXML = ".xml";

    private static final String ADDR = "addr";
    private static final String ALIPAYID = "aliPayId";
    private static final String ISSEL = "isSel";
    private static final String LOGISTICSCM = "logisticscm";
    private static final String LOGISTICSID = "logisticsid";
    private static final String NAME = "name";
    private static final String NICKNAME = "nickname";
    private static final String ORDERID = "orderId";
    private static final String TEL = "tel";
    private static final String TIME = "time";
    private static final String TITLE = "title";

    private static int I_ADDR = 0;
    private static int I_ALIPAYID = 1;
    private static int I_ISSEL = 2;
    private static int I_LOGISTICSCM = 3;
    private static int I_LOGISTICSID = 4;
    private static int I_NAME = 5;
    private static int I_NICKNAME = 6;
    private static int I_ORDERID = 7;
    private static int I_TEL = 8;
    private static int I_TIME = 9;
    private static int I_TITLE = 10;

    private OrderExcelManager() {
    }

    public static void main(String[] strs) {
        Map<String, List<List<String>>> na = analyzeXls("/Users/jerry/Desktop/share/一尘闲鱼助理/Excel导入模板2.xls");
        parseToDb(na);
    }

    /**
     * 解析Excel并存入数据库
     *
     * @param path excel路径
     */
    public static boolean importExcel(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        // 小米特殊处理
        path = path.replace("/external_files", Environment.getExternalStorageDirectory().getAbsolutePath());
        if (path.endsWith("xls")) {
            return parseToDb(analyzeXls(path));
        }
        return parseToDb(analyzeXlsx(path));
    }

    /**
     * 解析数据
     *
     * @param na 第0行为项目 下面的为数据
     * @return 成功插入
     */
    private static boolean parseToDb(Map<String, List<List<String>>> na) {
        Iterator<String> s = na.keySet().iterator();
        List<List<String>> goodsList = na.get(s.next());
        if (CollectionUtils.isEmpty(goodsList)) {
            ToastUtil.showShortText("Excel中数据为空");
            return false;
        }
        List<Order> products = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
            List<String> items = goodsList.get(i);
            // 第0解析项目，使名称与索引对应
            if (i == 0) {
                for (int j = 0; j < items.size(); j++) {
                    String item = items.get(j);
                    switch (item) {
                        case ADDR:
                            I_ADDR = j;
                            break;
                        case ALIPAYID:
                            I_ALIPAYID = j;
                            break;
                        case ISSEL:
                            I_ISSEL = j;
                            break;
                        case LOGISTICSCM:
                            I_LOGISTICSCM = j;
                            break;
                        case LOGISTICSID:
                            I_LOGISTICSID = j;
                            break;
                        case NAME:
                            I_NAME = j;
                            break;
                        case NICKNAME:
                            I_NICKNAME = j;
                            break;
                        case ORDERID:
                            I_ORDERID = j;
                            break;
                        case TEL:
                            I_TEL = j;
                            break;
                        case TIME:
                            I_TIME = j;
                            break;
                        case TITLE:
                        default:
                            I_TITLE = j;
                            break;
                    }
                }
            } else {
                // 第1+解析数据
                Order product = new Order();
                for (int j = 0; j < items.size(); j++) {
                    String item = items.get(j);
                    if (j == I_ORDERID) {
                        product.setOrderId(item);
                    } else if (j == I_LOGISTICSID) {
                        product.setLogisticsid(item);
                    } else if (j == I_LOGISTICSCM) {
                        product.setLogisticscm(item);
                    } else if (j == I_ADDR) {
                        product.setAddr(item);
                    } else if (j == I_ALIPAYID) {
                        product.setAliPayId(item);
                    } else if (j == I_NAME) {
                        product.setName(item);
                    } else if (j == I_NICKNAME) {
                        product.setNickname(item);
                    } else if (j == I_TEL) {
                        product.setTel(item);
                    } else if (j == I_TIME) {
                        product.setTime(item);
                    } else if (j == I_TITLE) {
                        product.setTitle(item);
                    }
                }
                products.add(product);
            }
        }
        List<Order> locals = ProManager.getInstance().queryAll(Order.class, null, Properties.OrderId);
        for (int i = 0; i < products.size(); i++) {
            Order product = products.get(i);
            boolean find = false;
            for (Order local : locals) {
                if (local.getOrderId().equals(product.getOrderId())) {
                    find = true;
                    local.setLogisticscm(product.getLogisticscm());
                    local.setLogisticsid(product.getLogisticsid());
                    ProManager.getInstance().update(local);
                    break;
                }
            }
            if (!find) {
                ProManager.getInstance().insertObject(product);
            }
        }
        return true;
    }

    private static Map<String, List<List<String>>> analyzeXlsx(String fileName) {
        Map<String, List<List<String>>> map = new HashMap<>();
        InputStream isShareStrings = null;
        InputStream isXlsx = null;
        ZipInputStream zipInputStream = null;
        List<String> listCells = new ArrayList<>();
        try {
            ZipFile zipFile = new ZipFile(new File(fileName));
            ZipEntry sharedStringXML = zipFile.getEntry(SHAREDSTRINGS);
            isShareStrings = zipFile.getInputStream(sharedStringXML);
            //
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(isShareStrings, "utf-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tag = xmlPullParser.getName();
                    if ("t".equals(tag)) {
                        listCells.add(xmlPullParser.nextText());
                    }
                }
                eventType = xmlPullParser.next();
            }
            Log.i("zzz", "analyze: list --> " + listCells);
            //
            isXlsx = new BufferedInputStream(new FileInputStream(fileName));
            zipInputStream = new ZipInputStream(isXlsx);
            ZipEntry zipDir;
            while ((zipDir = zipInputStream.getNextEntry()) != null) {
                String dirName = zipDir.getName();
                if (!zipDir.isDirectory() && dirName.endsWith(ENDXML)) {
                    if (dirName.contains(DIRSHEET)) {
                        parseSheet(listCells, zipFile, dirName, map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (zipInputStream != null) {
                    zipInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (isXlsx != null) {
                    isXlsx.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (isShareStrings != null) {
                    isShareStrings.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, List<List<String>>> next : map.entrySet()) {
            for (List<String> strings : next.getValue()) {
                Log.i("zzz", "analyzeXls: sheet --> " + next.getKey() + " row --> " + strings);
            }
        }
        return map;
    }

    private static void parseSheet(List<String> listCells, ZipFile zipFile, String entryName, Map<String, List<List<String>>> map) {
        int lastIndexOf = entryName.lastIndexOf(File.separator);
        String sheetName = entryName.substring(lastIndexOf + 1, entryName.length() - 4);
        //
        String v = null;
        List<String> colums = null;
        List<List<String>> rows = new ArrayList<>();
        InputStream inputStreamSheet = null;
        try {
            ZipEntry sheet = zipFile.getEntry(entryName);
            inputStreamSheet = zipFile.getInputStream(sheet);
            XmlPullParser xmlPullParserSheet = Xml.newPullParser();
            xmlPullParserSheet.setInput(inputStreamSheet, "utf-8");
            int evenTypeSheet = xmlPullParserSheet.getEventType();
            while (XmlPullParser.END_DOCUMENT != evenTypeSheet) {
                switch (evenTypeSheet) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlPullParserSheet.getName();
                        if ("row".equalsIgnoreCase(tag)) {
                            colums = new ArrayList<>();
                        } else if ("v".equalsIgnoreCase(tag)) {
                            v = xmlPullParserSheet.nextText();
                            if (colums != null) {
                                if (v != null) {
                                    colums.add(listCells.get(ParseUtil.parseInt(v)));
                                } else {
                                    colums.add(v);
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("row".equalsIgnoreCase(xmlPullParserSheet.getName()) && v != null) {
                            rows.add(colums);
                        }
                        break;
                    default:
                        break;
                }
                evenTypeSheet = xmlPullParserSheet.next();
            }
            if (rows.size() > 0) {
                map.put(sheetName, rows);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStreamSheet != null) {
                    inputStreamSheet.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Map<String, List<List<String>>> analyzeXls(String fileName) {
        Map<String, List<List<String>>> map = new HashMap<>();
        List<List<String>> rows;
        List<String> columns = null;
        try {
            WorkbookSettings workbooksetting = new WorkbookSettings();
            workbooksetting.setCellValidationDisabled(true);//去掉验证数据有效性
            Workbook workbook = Workbook.getWorkbook(new File(fileName), workbooksetting);
            Sheet[] sheets = workbook.getSheets();
            Set<String> addeds = new ArraySet<>();
            StringBuilder sb = new StringBuilder();
            for (Sheet sheet : sheets) {
                rows = new ArrayList<>();
                String sheetName = sheet.getName();
                sb.delete(0, sb.length());
                for (int i = 0; i < sheet.getRows(); i++) {
                    Cell[] sheetRow = sheet.getRow(i);
                    int columnNum = sheet.getColumns();
                    for (int j = 0; j < sheetRow.length; j++) {
                        if (j % columnNum == 0) {
                            columns = new ArrayList<>();
                        }
                        String content = sheetRow[j].getContents();
                        columns.add(content);
                        sb.append(content);
                    }
                    String added = sb.toString();
                    if (!addeds.contains(added)) {
                        rows.add(columns);
                        addeds.add(sb.toString());
                    }
                }
                map.put(sheetName, rows);
            }

            for (Map.Entry<String, List<List<String>>> next : map.entrySet()) {
                for (List<String> strings : next.getValue()) {
                    Log.i("zzz", "analyzeXls: sheet --> " + next.getKey() + " row --> " + strings);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

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

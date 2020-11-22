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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import androidx.collection.ArraySet;

import org.xmlpull.v1.XmlPullParser;

import com.jerry.baselib.common.bean.Product;
import com.jerry.baselib.common.bean.TypeManager;
import com.jerry.baselib.common.dbhelper.ProManager;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.FileUtil;
import com.jerry.baselib.common.util.ListCacheUtil;
import com.jerry.baselib.common.util.ParseUtil;
import com.jerry.baselib.common.util.Patterns;
import com.jerry.baselib.common.util.PreferenceHelp;
import com.jerry.baselib.common.util.StringUtil;
import com.jerry.baselib.common.util.ToastUtil;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by 请叫我张懂 on 2017/9/25.
 */

public class ExcelManager {

    private static final String SHAREDSTRINGS = "xl/sharedStrings.xml";
    private static final String DIRSHEET = "xl/worksheets/";
    private static final String ENDXML = ".xml";

    private static final String TITLE = "标题";
    private static final String CONTENT = "描述";
    private static final String REPLAY = "自动回复";
    private static final String SHOPTYPE = "类目名称";
    private static final String PICTURE = "图片路径";
    private static final String TAGS = "标签";
    private static final String PRICE = "价格";
    private static final String PROVICE = "省";
    private static final String CITY = "市";
    private static final String DISTRICT = "县";
    private static final String LINK = "链接";
    private static final String TRANSFEE = "运费";
    private static final String HIRETYPE = "出租类型";
    private static final String VILLAGE = "小区";
    private static final String ROOM = "室";
    private static final String HALL = "厅";
    private static final String TOILET = "卫";
    private static final String ACREAGE = "面积";
    private static final String RENOVATION = "装修程度";
    private static final String ROOMTYPE = "卧室类型";
    private static final String GENERALTAGS = "普通标签";
    private static final String MORETAG = "短租更多标签";
    private static int I_TITLE = -1;
    private static int I_CONTENT = -1;
    private static int I_REPLAY = -1;
    private static int I_SHOPTYPE = -1;
    private static int I_PICTURE = -1;
    private static int I_TAGS = -1;
    private static int I_PRICE = -1;
    private static int I_PROVICE = -1;
    private static int I_CITY = -1;
    private static int I_DISTRICT = -1;
    private static int I_LINK = -1;
    private static int I_TRANSFEE = -1;
    private static int I_HIRETYPE = -1;
    private static int I_VILLAGE = -1;
    private static int I_ROOM = -1;
    private static int I_HALL = -1;
    private static int I_TOILET = -1;
    private static int I_ACREAGE = -1;
    private static int I_RENOVATION = -1;
    private static int I_ROOMTYPE = -1;
    private static int I_GENERALTAGS = -1;
    private static int I_MORETAG = -1;

    private ExcelManager() {
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
        List<List<String>> goodsList = na.get("goodsList");
        if (CollectionUtils.isEmpty(goodsList)) {
            ToastUtil.showShortText("Excel中数据为空");
            return false;
        }
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
            List<String> items = goodsList.get(i);
            // 第0解析项目，使名称与索引对应
            if (i == 0) {
                for (int j = 0; j < items.size(); j++) {
                    String item = items.get(j);
                    switch (item) {
                        case TITLE:
                            I_TITLE = j;
                            break;
                        case CONTENT:
                            I_CONTENT = j;
                            break;
                        case REPLAY:
                            I_REPLAY = j;
                            break;
                        case SHOPTYPE:
                            I_SHOPTYPE = j;
                            break;
                        case PICTURE:
                            I_PICTURE = j;
                            break;
                        case TAGS:
                            I_TAGS = j;
                            break;
                        case PRICE:
                            I_PRICE = j;
                            break;
                        case PROVICE:
                            I_PROVICE = j;
                            break;
                        case CITY:
                            I_CITY = j;
                            break;
                        case DISTRICT:
                            I_DISTRICT = j;
                            break;
                        case LINK:
                            I_LINK = j;
                            break;
                        case TRANSFEE:
                            I_TRANSFEE = j;
                            break;
                        case VILLAGE:
                            I_VILLAGE = j;
                            break;
                        case HIRETYPE:
                            I_HIRETYPE = j;
                            break;
                        case ROOM:
                            I_ROOM = j;
                            break;
                        case HALL:
                            I_HALL = j;
                            break;
                        case TOILET:
                            I_TOILET = j;
                            break;
                        case ACREAGE:
                            I_ACREAGE = j;
                            break;
                        case RENOVATION:
                            I_RENOVATION = j;
                            break;
                        case ROOMTYPE:
                            I_ROOMTYPE = j;
                            break;
                        case GENERALTAGS:
                            I_GENERALTAGS = j;
                            break;
                        case MORETAG:
                        default:
                            I_MORETAG = j;
                            break;
                    }
                }
            } else {
                // 第1+解析数据
                Product product = new Product();
                for (int j = 0; j < items.size(); j++) {
                    String item = items.get(j);
                    if (j == I_TITLE) {
                        product.setTitle(item);
                    } else if (j == I_CONTENT) {
                        product.setContent(item);
                    } else if (j == I_REPLAY) {
                        product.setReplay(item);
                    } else if (j == I_PICTURE) {
                        StringBuilder sb = new StringBuilder();
                        String[] paths = StringUtil.safeSplit(item, " ");
                        for (String path : paths) {
                            String spliter = "/";
                            if (path.contains("\\")) {
                                spliter = "\\\\";
                            }
                            String[] names = StringUtil.safeSplit(path, spliter);
                            if (names.length > 0) {
                                String name = names[names.length - 1];
                                int index = name.lastIndexOf(".");
                                if (index > 0) {
                                    String st1 = name.substring(0, index);
                                    String st2 = name.substring(index);
                                    sb.append(FileUtil.getAppExternalPath()).append(st1).append(st2).append(" ");
                                }
                            }
                        }
                        product.setPicPath(sb.toString().trim());

                    } else if (j == I_TAGS) {
                        product.setTag(item);
                    } else if (j == I_SHOPTYPE) {
                        product.setType(TypeManager.findIndex(item));
                    } else if (j == I_PRICE) {
                        if (PreferenceHelp.getBoolean(PreferenceHelp.FREESEND)) {
                            int index = item.indexOf(".");
                            if (index > 0) {
                                product.setPrice(item.substring(0, index));
                            } else {
                                product.setPrice(item);
                            }
                        } else {
                            product.setPrice(item);
                        }
                    } else if (j == I_PROVICE) {
                        product.setProvice(item);
                    } else if (j == I_CITY) {
                        product.setCity(item);
                    } else if (j == I_DISTRICT) {
                        product.setDistrict(item);
                    } else if (j == I_LINK) {
                        product.setLink(item);
                    } else if (j == I_TRANSFEE) {
                        if (Patterns.isNumber(item) || "auto".equals(item) || "free".equals(item)) {
                            product.setTrans(item);
                        } else {
                            product.setTrans(PreferenceHelp.getString(PreferenceHelp.PUBLISH_TRANS_FEE));
                        }
                    } else if (j == I_VILLAGE) {
                        product.setVillage(item);
                    } else if (j == I_HIRETYPE) {
                        product.setIsHouse(true);
                        product.setHireType(TypeManager.findRent(item));
                    } else if (j == I_ROOM) {
                        product.setHouseRoom(Math.max(0, Math.min(5, ParseUtil.parseInt(item))));
                    } else if (j == I_HALL) {
                        product.setHouseHall(Math.max(0, Math.min(3, ParseUtil.parseInt(item))));
                    } else if (j == I_TOILET) {
                        product.setHouseToilet(Math.max(0, Math.min(3, ParseUtil.parseInt(item))));
                    } else if (j == I_ACREAGE) {
                        product.setAcreage(ParseUtil.parseDouble(item));
                    } else if (j == I_RENOVATION) {
                        product.setHouseRenovation(TypeManager.findRenovation(item));
                    } else if (j == I_ROOMTYPE) {
                        product.setRoomType(TypeManager.findRoomType(item));
                    } else if (j == I_GENERALTAGS) {
                        product.setHouseLabels(item);
                    } else if (j == I_MORETAG) {
                        product.setMoreHouseLabels(item);
                    }
                }
                if (TextUtils.isEmpty(product.getTitle())
                    || TextUtils.isEmpty(product.getContent())
                    || TextUtils.isEmpty(product.getPrice())) {
                    continue;
                }
                if (TextUtils.isEmpty(product.getReplay())) {
                    String replay = PreferenceHelp.getString(PreferenceHelp.AUTO_REPLAY, Product.AUTO_REPLAY);
                    product.setReplay(TextUtils.isEmpty(replay) ? Product.AUTO_REPLAY : replay);
                }
                product.setReplyPic(ListCacheUtil.getValueFromJsonFile(ListCacheUtil.AUTO_REPLAY_IMG));
                product.setIsEntity(PreferenceHelp.getBoolean(PreferenceHelp.ISENTITY));
                product.setIsNew(PreferenceHelp.getBoolean(PreferenceHelp.ISNEW, true));
                product.setAdRandom(PreferenceHelp.getBoolean(PreferenceHelp.RANDOM_ADDRESS));
                product.setFishRandom(PreferenceHelp.getBoolean(PreferenceHelp.RANDOM_FISH));
                product.setFishPond(PreferenceHelp.getString(PreferenceHelp.FISH_POND));
                product.setIsFree(PreferenceHelp.getBoolean(PreferenceHelp.FREESEND));
                product.setCount(Math.max(1, PreferenceHelp.getInt(PreferenceHelp.PUBLISH_COUNT, 1)));
                products.add(product);
            }
        }
        long time = System.currentTimeMillis();
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            product.setUpdateTime(time - i);
        }
        return ProManager.getInstance().insertMultObject(products);
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

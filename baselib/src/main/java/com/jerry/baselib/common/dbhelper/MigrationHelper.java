package com.jerry.baselib.common.dbhelper;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.internal.DaoConfig;

import com.jerry.baselib.greendao.DaoMaster;
import com.jerry.baselib.common.util.LogUtils;

public final class MigrationHelper {

    private static final String SQLITE_MASTER = "sqlite_master";
    private static final String SQLITE_TEMP_MASTER = "sqlite_temp_master";

    private static WeakReference<ReCreateAllTableListener> weakListener;

    public interface ReCreateAllTableListener {

        void onCreateAllTables(Database db, boolean ifNotExists);

        void onDropAllTables(Database db, boolean ifExists);
    }

    @SafeVarargs
    public static void migrate(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        LogUtils.d("【The Old Database Version】" + db.getVersion());
        Database database = new StandardDatabase(db);
        migrate(database, daoClasses);
    }

    @SafeVarargs
    public static void migrate(SQLiteDatabase db, ReCreateAllTableListener listener, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        weakListener = new WeakReference<>(listener);
        migrate(db, daoClasses);
    }

    @SafeVarargs
    public static void migrate(Database database, ReCreateAllTableListener listener, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        weakListener = new WeakReference<>(listener);
        migrate(database, daoClasses);
    }

    @SafeVarargs
    public static void migrate(Database database, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        LogUtils.d("【Generate temp table】start");
        generateTempTables(database, daoClasses);
        LogUtils.d("【Generate temp table】complete");

        ReCreateAllTableListener listener = null;
        if (weakListener != null) {
            listener = weakListener.get();
        }

        if (listener != null) {
            listener.onDropAllTables(database, true);
            LogUtils.d("【Drop all table by listener】");
            listener.onCreateAllTables(database, false);
            LogUtils.d("【Create all table by listener】");
        } else {
            dropAllTables(database, true, daoClasses);
            createAllTables(database, false, daoClasses);
        }
        LogUtils.d("【Restore data】start");
        restoreData(database, daoClasses);
        LogUtils.d("【Restore data】complete");
    }

    @SafeVarargs
    private static void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (Class<? extends AbstractDao<?, ?>> daoClass : daoClasses) {
            String tempTableName = null;

            DaoConfig daoConfig = new DaoConfig(db, daoClass);
            String tableName = daoConfig.tablename;
            if (!isTableExists(db, false, tableName)) {
                LogUtils.d("【New Table】" + tableName);
                continue;
            }
            try {
                tempTableName = daoConfig.tablename.concat("_TEMP");
                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName).append(";");
                db.execSQL(dropTableStringBuilder.toString());

                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("CREATE TEMPORARY TABLE ").append(tempTableName);
                insertTableStringBuilder.append(" AS SELECT * FROM ").append(tableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());
                LogUtils.d("【Table】" + tableName + "\n ---Columns-->" + getColumnsStr(daoConfig));
                LogUtils.d("【Generate temp table】" + tempTableName);
            } catch (SQLException e) {
                LogUtils.e("【Failed to generate temp table】" + tempTableName);
            }
        }
    }

    private static boolean isTableExists(Database db, boolean isTemp, String tableName) {
        if (db == null || TextUtils.isEmpty(tableName)) {
            return false;
        }
        String dbName = isTemp ? SQLITE_TEMP_MASTER : SQLITE_MASTER;
        String sql = "SELECT COUNT(*) FROM " + dbName + " WHERE type = ? AND name = ?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[]{"table", tableName});
            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }
            count = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count > 0;
    }


    private static String getColumnsStr(DaoConfig daoConfig) {
        if (daoConfig == null) {
            return "no columns";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < daoConfig.allColumns.length; i++) {
            builder.append(daoConfig.allColumns[i]);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }


    @SafeVarargs
    private static void dropAllTables(Database db, boolean ifExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
//        reflectMethod(db, "dropTable", ifExists, daoClasses);
        DaoMaster.dropAllTables(db, true);
        LogUtils.d("【Drop all table by reflect】");
    }

    @SafeVarargs
    private static void createAllTables(Database db, boolean ifNotExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
//        reflectMethod(db, "createTable", ifNotExists, daoClasses);
        DaoMaster.createAllTables(db, true);
        LogUtils.d("【Create all table by reflect】");
    }

    /**
     * dao class already define the sql exec method, so just invoke it
     */
    @SafeVarargs
    private static void reflectMethod(Database db, String methodName, boolean isExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (daoClasses.length < 1) {
            return;
        }
        try {
            for (Class cls : daoClasses) {
                Method method = cls.getDeclaredMethod(methodName, Database.class, boolean.class);
                method.invoke(null, db, isExists);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SafeVarargs
    private static void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");

            if (!isTableExists(db, true, tempTableName)) {
                continue;
            }

            try {
                // get all columns from tempTable, take careful to use the columns list
                List<String> columns = getColumns(db, tempTableName);
                ArrayList<String> properties = new ArrayList<>(columns.size());
                for (int j = 0; j < daoConfig.properties.length; j++) {
                    String columnName = daoConfig.properties[j].columnName;
                    if (!columns.contains(columnName)) {
                        StringBuilder insertTableStringBuilder = new StringBuilder();
                        insertTableStringBuilder.append("ALTER TABLE " + tempTableName + " ADD COLUMN " + columnName +
                            getTableType(daoConfig.properties[j].type));
                        db.execSQL(insertTableStringBuilder.toString());
                    }
                    properties.add(columnName);
                }
                if (properties.size() > 0) {
                    final String columnSQL = TextUtils.join(",", properties);

                    StringBuilder insertTableStringBuilder = new StringBuilder();
                    insertTableStringBuilder.append("REPLACE INTO ").append(tableName).append(" (");
                    insertTableStringBuilder.append(columnSQL);
                    insertTableStringBuilder.append(") SELECT ");
                    insertTableStringBuilder.append(columnSQL);
                    insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");
                    db.execSQL(insertTableStringBuilder.toString());
                    LogUtils.d("【Restore data】 to " + tableName);
                }
                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
                db.execSQL(dropTableStringBuilder.toString());
                LogUtils.d("【Drop temp table】" + tempTableName);
            } catch (SQLException e) {
                LogUtils.e("【Failed to restore data from temp table 】" + tempTableName);
            }
        }
    }

    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = Arrays.asList(cursor.getColumnNames());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (null == columns) {
                columns = new ArrayList<>();
            }
        }
        return columns;
    }

    private static Object getTableType(Class<?> type) {
        if (type.equals(int.class)) {
            return " INTEGER DEFAULT 0";
        }
        if (type.equals(long.class)) {
            return " Long DEFAULT 0";
        }
        if (type.equals(String.class)) {
            return " TEXT ";
        }
        if (type.equals(boolean.class)) {
            return " NUMERIC DEFAULT 0";
        }
        return " TEXT";
    }

}
//INSERT INTO PRODUCT (_id,TITLE,CONTENT,TYPE,PPRICE,IS_ENTITY,PRICE,TAG,REPLAY,LINK,PIC_PATH,PROVICE,CITY,DISTRICT,TRANS) SELECT _id,TITLE,CONTENT,TYPE,PPRICE,IS_ENTITY,PRICE,TAG,REPLAY,LINK,PIC_PATH,PROVICE,CITY,DISTRICT,TRANS FROM PRODUCT_TEMP;
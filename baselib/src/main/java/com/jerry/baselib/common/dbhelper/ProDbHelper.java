
package com.jerry.baselib.common.dbhelper;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import com.jerry.baselib.greendao.DaoMaster;
import com.jerry.baselib.greendao.LookUrlDao;
import com.jerry.baselib.greendao.OrderDao;
import com.jerry.baselib.greendao.ProductDao;
import com.jerry.baselib.greendao.XyProductDao;
import com.jerry.baselib.greendao.XyUserDao;

/**
 * Created by wzl on 2018/9/26.
 *
 * 数据库辅助类 1.创建数据库 2.创建数据库表 3.对数据库进行增删查改 4.对数据库进行升级
 */
public class ProDbHelper extends DaoMaster.DevOpenHelper {

    public ProDbHelper(Context context, String name) {
        super(context, name, null);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // 需要进行数据迁移更新的实体类 ，新增的不用加
            MigrationHelper.migrate(db, ProductDao.class, OrderDao.class, LookUrlDao.class, XyUserDao.class, XyProductDao.class);
        } else {
            // 默认操作
            DaoMaster.dropAllTables(db, true);
            onCreate(db);
        }
    }
}
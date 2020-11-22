package com.jerry.baselib.common.dbhelper;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import com.jerry.baselib.BaseApp;
import com.jerry.baselib.common.util.CollectionUtils;
import com.jerry.baselib.common.util.LogUtils;
import com.jerry.baselib.greendao.DaoMaster;
import com.jerry.baselib.greendao.DaoSession;

/**
 * Created by wzl on 2018/9/26. 进行数据库的管理 1.创建数据库 2.创建数据库表 3.对数据库进行增删查改 4.对数据库进行升级
 */
public class ProManager {

    private DaoMaster.DevOpenHelper mHelper;
    private DaoSession mDaoSession;

    private static final String DB_PRO = "pro.db";
    private static ProManager proManager;


    public static ProManager getInstance() {
        if (proManager == null) {
            synchronized (ProManager.class) {
                if (proManager == null) {
                    proManager = new ProManager(DB_PRO);
                }
            }
        }
        return proManager;
    }

    private ProManager() {
    }

    private ProManager(String dbName) {
        mHelper = new ProDbHelper(BaseApp.getInstance(), dbName);
        DaoMaster daoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
        setDebug();
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     */
    private void setDebug() {
        QueryBuilder.LOG_SQL = BaseApp.Config.DEBUG;
        QueryBuilder.LOG_VALUES = BaseApp.Config.DEBUG;
    }

    /**************************数据库插入操作***********************/
    /**
     * 插入单个对象
     */
    public boolean insertObject(Object object) {
        boolean flag = false;
        try {
            flag = mDaoSession.insert(object) != -1;
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return flag;
    }

    /**
     * 插入否则更新单个对象
     */
    public boolean insertOrReplaceObject(Object object) {
        boolean flag = false;
        try {
            flag = mDaoSession.insertOrReplace(object) != -1;
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return flag;
    }

    /**
     * 插入否则更新单个对象
     */
    public boolean update(Object object) {
        try {
            mDaoSession.update(object);
            return true;
        } catch (Exception e) {
            LogUtils.e(e.toString());
            return false;
        }
    }

    /**
     * 插入多个对象，并开启新的线程
     */
    public boolean insertMultObject(final List<?> objects) {
        boolean flag = false;
        try {
            if (!CollectionUtils.isEmpty(objects)) {
                for (Object object : objects) {
                    mDaoSession.insertOrReplace(object);
                }
                flag = true;
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return flag;
    }


    /**
     * 数据库删除操作 删除某个数据库表
     */
    public boolean delete(Object obj) {
        boolean flag;
        try {
            mDaoSession.delete(obj);
            flag = true;
        } catch (Exception e) {
            LogUtils.e(e.toString());
            flag = false;
        }
        return flag;
    }

    /**
     * 数据库删除操作 删除某个数据库表
     */
    public boolean deleteAll(Class<?> clss) {
        boolean flag;
        try {
            mDaoSession.deleteAll(clss);
            flag = true;
        } catch (Exception e) {
            LogUtils.e(e.toString());
            flag = false;
        }
        return flag;
    }

    /**
     * 查询所有对象
     */
    public <T> T queryObj(Class<T> object, WhereCondition cond) {
        try {
            QueryBuilder queryBuilder = mDaoSession.getDao(object).queryBuilder();
            if (cond != null) {
                queryBuilder.where(cond);
            }
            return (T) queryBuilder.list().get(0);
        } catch (Throwable e) {
            LogUtils.e(e.toString());
        }
        return null;
    }

    /**
     * 查询所有对象
     */
    public <T> List<T> queryAll(Class<T> object, WhereCondition cond, Property order) {
        List<T> objects = new ArrayList<>();
        try {
            QueryBuilder queryBuilder = mDaoSession.getDao(object).queryBuilder();
            if (cond != null) {
                queryBuilder.where(cond);
            }
            if (order != null) {
                queryBuilder.orderDesc(order);
            }
            objects = queryBuilder.list();
        } catch (Throwable e) {
            LogUtils.e(e.toString());
        }
        return objects;
    }

    /***************************关闭数据库*************************/
    /**
     * 关闭数据库一般在Odestory中使用
     */
    public void closeDataBase() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (null != mDaoSession) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }
}

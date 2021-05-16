package com.jerry.baselib.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.jerry.baselib.common.bean.XyUser;
import com.jerry.baselib.common.bean.Order;
import com.jerry.baselib.common.bean.Praiser;
import com.jerry.baselib.common.bean.Link;
import com.jerry.baselib.common.bean.Product;
import com.jerry.baselib.common.bean.XyProduct;
import com.jerry.baselib.common.bean.CoinOrder;
import com.jerry.baselib.common.bean.LookUrl;
import com.jerry.baselib.common.bean.DyUser;

import com.jerry.baselib.greendao.XyUserDao;
import com.jerry.baselib.greendao.OrderDao;
import com.jerry.baselib.greendao.PraiserDao;
import com.jerry.baselib.greendao.LinkDao;
import com.jerry.baselib.greendao.ProductDao;
import com.jerry.baselib.greendao.XyProductDao;
import com.jerry.baselib.greendao.CoinOrderDao;
import com.jerry.baselib.greendao.LookUrlDao;
import com.jerry.baselib.greendao.DyUserDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig xyUserDaoConfig;
    private final DaoConfig orderDaoConfig;
    private final DaoConfig praiserDaoConfig;
    private final DaoConfig linkDaoConfig;
    private final DaoConfig productDaoConfig;
    private final DaoConfig xyProductDaoConfig;
    private final DaoConfig coinOrderDaoConfig;
    private final DaoConfig lookUrlDaoConfig;
    private final DaoConfig dyUserDaoConfig;

    private final XyUserDao xyUserDao;
    private final OrderDao orderDao;
    private final PraiserDao praiserDao;
    private final LinkDao linkDao;
    private final ProductDao productDao;
    private final XyProductDao xyProductDao;
    private final CoinOrderDao coinOrderDao;
    private final LookUrlDao lookUrlDao;
    private final DyUserDao dyUserDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        xyUserDaoConfig = daoConfigMap.get(XyUserDao.class).clone();
        xyUserDaoConfig.initIdentityScope(type);

        orderDaoConfig = daoConfigMap.get(OrderDao.class).clone();
        orderDaoConfig.initIdentityScope(type);

        praiserDaoConfig = daoConfigMap.get(PraiserDao.class).clone();
        praiserDaoConfig.initIdentityScope(type);

        linkDaoConfig = daoConfigMap.get(LinkDao.class).clone();
        linkDaoConfig.initIdentityScope(type);

        productDaoConfig = daoConfigMap.get(ProductDao.class).clone();
        productDaoConfig.initIdentityScope(type);

        xyProductDaoConfig = daoConfigMap.get(XyProductDao.class).clone();
        xyProductDaoConfig.initIdentityScope(type);

        coinOrderDaoConfig = daoConfigMap.get(CoinOrderDao.class).clone();
        coinOrderDaoConfig.initIdentityScope(type);

        lookUrlDaoConfig = daoConfigMap.get(LookUrlDao.class).clone();
        lookUrlDaoConfig.initIdentityScope(type);

        dyUserDaoConfig = daoConfigMap.get(DyUserDao.class).clone();
        dyUserDaoConfig.initIdentityScope(type);

        xyUserDao = new XyUserDao(xyUserDaoConfig, this);
        orderDao = new OrderDao(orderDaoConfig, this);
        praiserDao = new PraiserDao(praiserDaoConfig, this);
        linkDao = new LinkDao(linkDaoConfig, this);
        productDao = new ProductDao(productDaoConfig, this);
        xyProductDao = new XyProductDao(xyProductDaoConfig, this);
        coinOrderDao = new CoinOrderDao(coinOrderDaoConfig, this);
        lookUrlDao = new LookUrlDao(lookUrlDaoConfig, this);
        dyUserDao = new DyUserDao(dyUserDaoConfig, this);

        registerDao(XyUser.class, xyUserDao);
        registerDao(Order.class, orderDao);
        registerDao(Praiser.class, praiserDao);
        registerDao(Link.class, linkDao);
        registerDao(Product.class, productDao);
        registerDao(XyProduct.class, xyProductDao);
        registerDao(CoinOrder.class, coinOrderDao);
        registerDao(LookUrl.class, lookUrlDao);
        registerDao(DyUser.class, dyUserDao);
    }
    
    public void clear() {
        xyUserDaoConfig.clearIdentityScope();
        orderDaoConfig.clearIdentityScope();
        praiserDaoConfig.clearIdentityScope();
        linkDaoConfig.clearIdentityScope();
        productDaoConfig.clearIdentityScope();
        xyProductDaoConfig.clearIdentityScope();
        coinOrderDaoConfig.clearIdentityScope();
        lookUrlDaoConfig.clearIdentityScope();
        dyUserDaoConfig.clearIdentityScope();
    }

    public XyUserDao getXyUserDao() {
        return xyUserDao;
    }

    public OrderDao getOrderDao() {
        return orderDao;
    }

    public PraiserDao getPraiserDao() {
        return praiserDao;
    }

    public LinkDao getLinkDao() {
        return linkDao;
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public XyProductDao getXyProductDao() {
        return xyProductDao;
    }

    public CoinOrderDao getCoinOrderDao() {
        return coinOrderDao;
    }

    public LookUrlDao getLookUrlDao() {
        return lookUrlDao;
    }

    public DyUserDao getDyUserDao() {
        return dyUserDao;
    }

}

package com.jerry.baselib.common.bean;

import java.util.List;

import com.alibaba.fastjson.JSONArray;

/**
 * @author Jerry
 * @createDate 2019-07-10
 * @description
 */
public class TmallBean {

    /**
     * seller : {"shopName":"蒙奴莎旗舰店"}
     * mock : {"price":{"price":{"priceText":"79.00"}}}
     * rate : {"enable":true,"totalCount":39225,"keywords":[{"attribute":"522-11","word":"式样好","count":2792,"type":1},{"attribute":"1822-11","word":"穿着效果好","count":2175,"type":1},{"attribute":"620-11","word":"衣服不错","count":1861,"type":1},{"attribute":"1722-11","word":"衣服很舒服","count":1441,"type":1},{"attribute":"222-11","word":"尺码很好","count":1164,"type":1},{"attribute":"222-13","word":"尺寸有偏差","count":391,"type":-1}]}
     * props : {"groupProps":[{"基本信息":[{"品牌":"蒙奴莎 "},{"适用年龄":"18-24周岁 "},{"尺寸":"25 26 27 28 29 30 31 "},{"面料分类":"全棉牛仔布 "},{"腰型":"高腰 "},{"工艺":"水洗 拉链 纽扣 多口袋 其他 "},{"颜色分类":"复古蓝 烟灰色 "},{"货号":"B201 "},{"成分含量":"96%及以上 "},{"年份季节":"2019年夏季 "},{"厚薄":"常规 "},{"裤长":"短裤 "},{"款式":"阔脚裤 "},{"颜色":"深色 "},{"销售渠道类型":"纯电商(只在线上销售) "},{"材质成分":"棉98% 其他2% "}]}]}
     * detailDesc : {"newWapDescJson":[{"component":"mui/shopactivity/index","moduleType":0,"enable":true,"moduleName":"店铺活动","moduleKey":"shop_discount"},{"component":"mui/shopcoupon/index","moduleType":0,"enable":true,"moduleName":"优惠券","moduleKey":"coupon"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i2/3039690011/O1CN01RHrJjv1Bx4sjsE3Og_!!3039690011.jpg","width":"620","height":"687"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i1/3039690011/O1CN014zZPZ31Bx4skXoyEP_!!3039690011.jpg","width":"620","height":"755"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i3/3039690011/O1CN010YQQjq1Bx4sjA053d_!!3039690011.jpg","width":"620","height":"611"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i4/3039690011/O1CN01Tp9FFD1Bx4sjUJaqI_!!3039690011.jpg","width":"620","height":"544"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i3/3039690011/O1CN01VqJVVi1Bx4tpPmOuU_!!3039690011.jpg","width":"620","height":"639"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i3/3039690011/O1CN01Aigr2U1Bx4sXgj4j8_!!3039690011.jpg","width":"620","height":"815"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i2/3039690011/O1CN01gq2BhD1Bx4sUoiPrM_!!3039690011.jpg","width":"620","height":"712"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i4/3039690011/O1CN01owR6l81Bx4sWeukoH_!!3039690011.jpg","width":"620","height":"706"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i4/3039690011/O1CN01Kq37AB1Bx4sZXpg2q_!!3039690011.jpg","width":"620","height":"750"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i4/3039690011/O1CN01S21k6D1Bx4sYvRp9F_!!3039690011.jpg","width":"620","height":"710"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i1/3039690011/O1CN01mKqVoz1Bx4sXgg7oa_!!3039690011.jpg","width":"620","height":"740"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i1/3039690011/O1CN01BVGmR01Bx4sPTIG5z_!!3039690011.jpg","width":"620","height":"714"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i1/3039690011/O1CN015yAAtQ1Bx4sXggviB_!!3039690011.jpg","width":"620","height":"744"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i1/3039690011/O1CN01pXBKEd1Bx4sW2huzg_!!3039690011.jpg","width":"620","height":"689"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i2/3039690011/O1CN01ThKcww1Bx4sYGxnbB_!!3039690011.jpg","width":"620","height":"799"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/wpimagetext/index","data":[{"img":"https://img.alicdn.com/imgextra/i4/3039690011/O1CN01jMsbSi1Bx4sU0X8dn_!!3039690011.jpg","width":"620","height":"787"}],"moduleType":1,"enable":true,"moduleName":"图文","moduleKey":"image_text"},{"component":"mui/custommodule/index","data":[{"img":"https://gw.alicdn.com/tfs/TB1k9XsQpXXXXXLXpXXXXXXXXXX-750-368.png","width":750,"height":368}],"moduleType":1,"enable":true,"moduleKey":"custompriceDesc"}]}
     */

    private SellerBean seller;
    private MockBean mock;
    private RateBean rate;
    private PropsBean props;
    private DetailDescBean detailDesc;

    public SellerBean getSeller() {
        return seller;
    }

    public void setSeller(SellerBean seller) {
        this.seller = seller;
    }

    public MockBean getMock() {
        return mock;
    }

    public void setMock(MockBean mock) {
        this.mock = mock;
    }

    public RateBean getRate() {
        return rate;
    }

    public void setRate(RateBean rate) {
        this.rate = rate;
    }

    public PropsBean getProps() {
        return props;
    }

    public void setProps(PropsBean props) {
        this.props = props;
    }

    public DetailDescBean getDetailDesc() {
        return detailDesc;
    }

    public void setDetailDesc(DetailDescBean detailDesc) {
        this.detailDesc = detailDesc;
    }

    public static class SellerBean {

        /**
         * shopName : 蒙奴莎旗舰店
         */

        private String shopName;

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }
    }

    public static class MockBean {

        /**
         * price : {"price":{"priceText":"79.00"}}
         */

        private PriceBeanX price;

        public PriceBeanX getPrice() {
            return price;
        }

        public void setPrice(PriceBeanX price) {
            this.price = price;
        }

        public static class PriceBeanX {

            /**
             * price : {"priceText":"79.00"}
             */

            private PriceBean price;

            public PriceBean getPrice() {
                return price;
            }

            public void setPrice(PriceBean price) {
                this.price = price;
            }

            public static class PriceBean {

                /**
                 * priceText : 79.00
                 */

                private String priceText;

                public String getPriceText() {
                    return priceText;
                }

                public void setPriceText(String priceText) {
                    this.priceText = priceText;
                }
            }
        }
    }

    public static class RateBean {

        /**
         * enable : true
         * totalCount : 39225
         * keywords : [{"attribute":"522-11","word":"式样好","count":2792,"type":1},{"attribute":"1822-11","word":"穿着效果好","count":2175,"type":1},{"attribute":"620-11","word":"衣服不错","count":1861,"type":1},{"attribute":"1722-11","word":"衣服很舒服","count":1441,"type":1},{"attribute":"222-11","word":"尺码很好","count":1164,"type":1},{"attribute":"222-13","word":"尺寸有偏差","count":391,"type":-1}]
         */

        private List<KeywordsBean> keywords;

        public List<KeywordsBean> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<KeywordsBean> keywords) {
            this.keywords = keywords;
        }

        public static class KeywordsBean {

            /**
             * attribute : 522-11
             * word : 式样好
             * count : 2792
             * type : 1
             */

            private String word;
            private int type;

            public String getWord() {
                return word;
            }

            public void setWord(String word) {
                this.word = word;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }
    }

    public static class PropsBean {

        private JSONArray groupProps;

        public JSONArray getGroupProps() {
            return groupProps;
        }

        public void setGroupProps(JSONArray groupProps) {
            this.groupProps = groupProps;
        }

    }

    public static class DetailDescBean {

        private List<NewWapDescJsonBean> newWapDescJson;

        public List<NewWapDescJsonBean> getNewWapDescJson() {
            return newWapDescJson;
        }

        public void setNewWapDescJson(List<NewWapDescJsonBean> newWapDescJson) {
            this.newWapDescJson = newWapDescJson;
        }

        public static class NewWapDescJsonBean {

            /**
             * component : mui/shopactivity/index
             * moduleType : 0
             * enable : true
             * moduleName : 店铺活动
             * moduleKey : shop_discount
             * data : [{"img":"https://img.alicdn.com/imgextra/i2/3039690011/O1CN01RHrJjv1Bx4sjsE3Og_!!3039690011.jpg","width":"620","height":"687"}]
             */

            private String component;
            private int moduleType;
            private String moduleName;
            private String moduleKey;
            private List<DataBean> data;

            public String getComponent() {
                return component;
            }

            public void setComponent(String component) {
                this.component = component;
            }

            public int getModuleType() {
                return moduleType;
            }

            public void setModuleType(int moduleType) {
                this.moduleType = moduleType;
            }

            public String getModuleName() {
                return moduleName;
            }

            public void setModuleName(String moduleName) {
                this.moduleName = moduleName;
            }

            public String getModuleKey() {
                return moduleKey;
            }

            public void setModuleKey(String moduleKey) {
                this.moduleKey = moduleKey;
            }

            public List<DataBean> getData() {
                return data;
            }

            public void setData(List<DataBean> data) {
                this.data = data;
            }

            public static class DataBean {

                /**
                 * img : https://img.alicdn.com/imgextra/i2/3039690011/O1CN01RHrJjv1Bx4sjsE3Og_!!3039690011.jpg
                 * width : 620
                 * height : 687
                 */

                private String img;

                public String getImg() {
                    return img;
                }

                public void setImg(String img) {
                    this.img = img;
                }
            }
        }
    }
}



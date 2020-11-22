package com.jerry.baselib.common.bean;

/**
 * @author Jerry
 * @createDate 2019-05-06
 * @description
 */
public class LocationBean {


    /**
     * status : OK
     * result : {"location":{"lng":121.406146,"lat":31.112159},"formatted_address":"上海市闵行区春申路2812号","business":"春申,莘庄,莘庄工业区","addressComponent":{"city":"上海市","direction":"西北","distance":"82","district":"闵行区","province":"上海市","street":"春申路","street_number":"2812号"},"cityCode":289}
     */

    private String status;
    private ResultBean result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * location : {"lng":121.406146,"lat":31.112159}
         * formatted_address : 上海市闵行区春申路2812号
         * business : 春申,莘庄,莘庄工业区
         * addressComponent : {"city":"上海市","direction":"西北","distance":"82","district":"闵行区","province":"上海市","street":"春申路","street_number":"2812号"}
         * cityCode : 289
         */

        private LocationBean location;
        private AddressComponentBean addressComponent;

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public AddressComponentBean getAddressComponent() {
            return addressComponent;
        }

        public void setAddressComponent(AddressComponentBean addressComponent) {
            this.addressComponent = addressComponent;
        }

        public static class AddressComponentBean {
            /**
             * city : 上海市
             * direction : 西北
             * distance : 82
             * district : 闵行区
             * province : 上海市
             * street : 春申路
             * street_number : 2812号
             */

            private String city;
            private String district;
            private String province;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }
        }
    }
}

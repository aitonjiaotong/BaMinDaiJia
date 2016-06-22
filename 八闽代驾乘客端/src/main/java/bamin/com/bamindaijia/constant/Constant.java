package bamin.com.bamindaijia.constant;

/**
 * Created by zjb on 2016/6/12.
 */
public class Constant {
    public static class PERMISSION {
        public static final int ACCESS_COARSE_LOCATION = 0;
        public static final int ACCESS_FINE_LOCATION = 1;
        public static final int WRITE_EXTERNAL_STORAGE = 2;
        public static final int READ_EXTERNAL_STORAGE = 3;
        public static final int READ_PHONE_STATE = 4;
        public static final int READ_CONTACTS = 5;
    }

    public static class INTENT_KEY {
        public static final String START_SITE = "startSite";
        public static final String CITY = "city";
        public static final String CITY_CODE = "cityCode";
        public static final String START_SITE_BACK = "startSiteBack";
        public static final String END_SITE_BACK = "endSiteBack";
        public static final String START_LATLNG_BACK = "startLatlngBack";
        public static final String CHOSSE_TYPE = "chosse_type";
        public static final String START = "start";
        public static final String END = "end";
        public static final String THE_CONTACTS= "the_contacts";
    }

    public static class REQUEST_RESULT_CODE {
        public static final int CHOSSE_START_SITE = 1;
        public static final int CHOSSE_END_SITE = 2;
        public static final int PICK_CONTACT = 3;
        public static final int CHOSSE_CONTACTS = 4;
    }

    public static class SP {
        public static final String CONTACTS = "contacts";//存储常用联系电话
        public static final String PHONE_NUM = "phoneNum";
    }
}

package com.nice.fincent.define;

import com.nice.fincent.util.Util;

public class URL {

    public static final String PROTOCOL = "http";
    public static final String PORT     = "8080";
    public static final String SERVER	= PROTOCOL + "some url..:" + PORT;
    public static final String WEBURL  	= PROTOCOL + "some url..:";



    /*
     * 서버 API
     */
    public static final String TEST_URL  	= "file:///android_asset/test.html";
    // 로그인
    public static final String LOGIN = "/api/v1/auth/login";
    // 회원가입
    public static final String JOIN = "/api/v1/registeruser";
    // status
    public static final String STATUS = "/api/v1/auth/status";
    // 메뉴
    public static final String ACC_MAIN_CATEGORY = "/api/v1/account_main_category";
    // 알림 갯수 _GET
    public static final String ALERT_CNT = "/api/v1/purchase_queues_count";


    // 품목
    public static final String ITEM_ARRAY = "/api/v1/item";
    // 창고
    public static final String STORE_ARRAY = "/api/v1/warehouse";
    // 매출처
    public static final String SALES_ARRAY = "/api/v1/customer";
    // 거래처
    public static final String CUST_ARRAY = "/api/v1/customer";
    // 매입처
    public static final String PURCHASE_ARRAY = "/api/v1/customer";
    // 거래은행
    public static final String BANK_ARRAY = "/api/v1/bank";

}

package com.dcyx.app.global.config;

/**
 * Created by xue on 2017/8/10.
 */

public class HttpParamsConstants {
    //存储资源文件目录
    public static final  String RESOURCE_DIRECTORY = "";
    //存储图片目录
    public static final  String  CACHE_IMAGE_DIR = "";
    //存储数据目录
    public static final  String  CACHE_MESSAGE_DIR = "";
    //存储视频音像目录
    public static final  String  CACHE_AUDIO_DIR = "";
    /*登录请求路径*/
    public static final  String  LOGIN_URL = "SystemConfigService/json/";//QueryDepartmentInfoById
    /*入库的请求路径*/
    public static final String STOCK_IN_URL = "MaterialManagementService/json/";

    /*出库计划 新增保存*/
    public static final String STOCK_PLAN_ADD = "api/Stock/AddStockOutPlan";
    /*出库计划 更新保存*/
    public static final String STOCK_PLAN_UPDATE = "api/Stock/UpdateStockOutPlan";

    /*出库计划 扫描试剂二维码查询试剂套装接口*/
    public static final String STOCK_PLAN_ITEM_KIT = "api/Stock/QueryItemKit";

    /*查询出库计划接口*/
    public static final String GET_STOCK_PLAN = "api/Stock/GetStockOutPlan";
    /*查询出库计划明细接口*/
    public static final String GET_STOCK_PLAN_ITEM = "api/Stock/GetStockOutPlanItem";
    /*删除出库计划接口*/
    public static final String DELETE_STOCK_PLAN = "api/Stock/GetDeleteStockOutPlan";


    /*入库的请求路径*/
    public static final String PURCHASE_IN_URL = "OrderService/json/";
    /*手势密码key*/
    public static final String GESTURE_PASSWORD = "GesturePassword";
    /*Lerm or LermCloud*/
    public static final String SETTING_LERM_CLOUD = "SETTING_LERM_CLOUD";
    /*一键报修与一键采购的开关*/
    public static final String SETTING_ONE_KEY_REPCH = "SETTING_ONE_KEY_REPCH";

    public static final String CITY_NAME ="CityName";
    public static final String HISTORY_LOGIN_NAME="HISTORY_LOGIN_NAME";
    public static final String LOGIN_NAME="LOGIN_NAME";
    public static final String LOGIN_PASS="LOGIN_PASS";

    public static final String LOCATION_URL = "http://api.map.baidu.com/geocoder";//"http://www.google.com/loc/json";
    public static final String LOCATION_HOST = "api.map.baidu.com";//"maps.google.com";

    public static final String LOCATION = "location";
    public static final String LOCATION_ACTION = "locationAction";
    /*手势创建完毕后是否跳转的标识 key*/
    public static final String IS_JUMP = "is_jump";

    /*屏幕是否黑屏或者按home键超过指定时间的标识*/
    public static boolean isScreeOffOrPreHomeKey = false;
    /*引导图层*/
    public static final String HOME_GUIDE_VIEW = "HOME_GUIDE_VIEW";
    public static final String STOCK_IN_GUIDE_VIEW = "STOCK_IN_GUIDE_VIEW";
    public static final String STOCK_OUT_GUIDE_VIEW = "STOCK_OUT_GUIDE_VIEW";
    public static final String PURCHASE_GUIDE_VIEW = "PURCHASE_GUIDE_VIEW";
    /*入/出库界面点击非标题条目项时是否弹出框显示点击的信息  默认为false*/
    public static final boolean IS_SHOW_STOCK_INFOR = true;

}

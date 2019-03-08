package com.dcyx.app.global.config;

/**
 * Created by xue on 2017/8/11.
 */

public class HandlerWhat {
    public static final int SAVA = 0;// 保存
    public static final int DELETE = 1;// 删除
    public static final int UPDATAE=2;//更新
    public static final int SELECT=3;//查询
    public static final int SCAN_RESULT=4;//扫描结果标识
    public static final int SUCCESS = 100;//请求成功标识
    public static final int FAILURE = 101;//请求失败标识
    public static final int NO_DATA = 102;//数据库没有相关数据

    public static final int QUERY_SUCCESS= 103;//查询成功
    public static final int QUERY_FAILURE= 104;//查询成功
    /*单一权限申请码*/
    public static final int REQUEST_CODE_PERMISSION_SINGLE = 105;
    /* 多个权限申请码*/
    public static final int REQUEST_CODE_PERMISSION_MULTI = 106;
    public static final int LOING_NO_USER = 108;//用户不存在
    public static final int LOING_NO_PASSWORD = 109;//密码不正确
    public static final int LOING_REQUEST_FAILURE=110;//与服务端请求失败
    public static final int LOING_REQUEST_PARSE_FAILURE=111;//数据解析错误
    public static final int SERVER_NOT_FOUND = 112;//找不到服务

    /*设置界面返回标识码*/
    public static final int REQUEST_CODE_SETTING = 300;
    /*延迟*/
    public static final int DELAY_TIME = 107;

}

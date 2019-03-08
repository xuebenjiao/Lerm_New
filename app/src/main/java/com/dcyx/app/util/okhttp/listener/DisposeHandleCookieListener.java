package com.dcyx.app.util.okhttp.listener;

import java.util.ArrayList;
/**
 * Created by xue on 2017/8/16.
 * 当需要专门处理Cookie时创建此回调接口
 */
public interface DisposeHandleCookieListener extends DisposeDataListener
{
	public void onCookie(ArrayList<String> cookieStrLists);
}

package com.dcyx.app.util.okhttp.response;

import java.io.IOException;
import java.util.ArrayList;

import com.dcyx.app.mvp.ui.Iview.IBaseView;
import com.dcyx.app.util.CommonUtils;
import com.dcyx.app.util.StringUtils;
import com.dcyx.app.util.ToastUtils;
import com.dcyx.app.util.okhttp.HttpMessageEnum;
import com.dcyx.app.util.okhttp.exception.OkHttpException;
import com.dcyx.app.util.okhttp.listener.DisposeDataHandle;
import com.dcyx.app.util.okhttp.listener.DisposeDataListener;
import com.dcyx.app.util.okhttp.listener.DisposeHandleCookieListener;

import android.os.Handler;
import android.os.Looper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
/**
 * @author vision
 * @function 专门处理JSON的回调
 */
public class CommonJsonCallback implements Callback {
	/**
	 * the logic layer exception, may alter in different app
	 */
	protected final String COOKIE_STORE = "Set-Cookie"; // decide the server it
	/**
	 * 将其它线程的数据转发到UI线程
	 */
	private Handler mDeliveryHandler;
	private DisposeDataListener mListener;
	private Class<?> mClass;
	private IBaseView msgView;
	public CommonJsonCallback(DisposeDataHandle handle,IBaseView view) {
		this.mListener = handle.mListener;
		this.mClass = handle.mClass;
		this.mDeliveryHandler = new Handler(Looper.getMainLooper());
		msgView = view;
	}
	/**
	 * 请求失败回调接口
	 * @param call
	 * @param ioexception
	 */
	@Override
	public void onFailure(final Call call, final IOException ioexception) {
		/**
		 * 此时还在非UI线程，因此要转发
		 */
		mDeliveryHandler.post(new Runnable() {
			@Override
			public void run() {
				if(msgView!=null){
					msgView.closeProgressDialog();
				}
				ToastUtils.showLongToast(HttpMessageEnum.NETWORK_ERROR.getmInforDesc());
				mListener.onFailure(new OkHttpException(HttpMessageEnum.NETWORK_ERROR));
			}
		});
	}
	/**
	 * 请求成功回调接口
	 * @param call
	 * @param response
	 * @throws IOException
	 */
	@Override
	public void onResponse(final Call call, final Response response) throws IOException {
		final String result = response.body().string();
		final int requestCode = response.code();
		final ArrayList<String> cookieLists = handleCookie(response.headers());
		mDeliveryHandler.post(new Runnable() {
			@Override
			public void run() {
				if(msgView!=null){
					msgView.closeProgressDialog();
				}

							handleResponse(result,requestCode);
				/**
				 * handle the cookie
				 */
				if (mListener instanceof DisposeHandleCookieListener) {
					((DisposeHandleCookieListener) mListener).onCookie(cookieLists);
				}
			}
		});
	}
	private ArrayList<String> handleCookie(Headers headers) {
		ArrayList<String> tempList = new ArrayList<String>();
		for (int i = 0; i < headers.size(); i++) {
			if (headers.name(i).equalsIgnoreCase(COOKIE_STORE)) {
				tempList.add(headers.value(i));
			}
		}
		return tempList;
	}
	/**
	 * 处理请求返回的数据
	 * @param responseObj
	 */
	private void handleResponse(Object responseObj,int code) {
		if (responseObj == null) {
			ToastUtils.showLongToast(HttpMessageEnum.RESPONSE_EMPTY_ERROR.getmInforDesc());
			mListener.onFailure(new OkHttpException(HttpMessageEnum.RESPONSE_EMPTY_ERROR));
			return;
		}
		if(code==404){//找不到服务
			ToastUtils.showLongToast(HttpMessageEnum.SERVER_NOT_FOUND.getmInforDesc());
			mListener.onFailure(new OkHttpException(HttpMessageEnum.SERVER_NOT_FOUND));
			return ;
		}
		if(CommonUtils.pull2xml(responseObj.toString())){//判断后台是否成功返回
			mListener.onSuccess(responseObj.toString());
		}
		else if(CommonUtils.isGetHttpDataSuccess(responseObj.toString())){
			mListener.onSuccess(responseObj.toString());
		}
		else if(!StringUtils.isEmpty(CommonUtils.getXmlMessageInfo(responseObj.toString()))){
			ToastUtils.showLongToast(CommonUtils.getXmlMessageInfo(responseObj.toString()));
			mListener.onFailure(responseObj.toString());
			return;
		}
		else{
			ToastUtils.showLongToast(HttpMessageEnum.SERVERS_ERROR.getmInforDesc());
			mListener.onFailure(new OkHttpException(HttpMessageEnum.SERVERS_ERROR));//成功返回  服务端没有查询到数据
			return;
		}
	}
}
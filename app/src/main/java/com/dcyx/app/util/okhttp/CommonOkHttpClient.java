package com.dcyx.app.util.okhttp;

import com.dcyx.app.R;
import com.dcyx.app.mvp.ui.Iview.IBaseView;
import com.dcyx.app.util.NetworkUtils;
import com.dcyx.app.util.StringUtils;
import com.dcyx.app.util.ToastUtils;
import com.dcyx.app.util.okhttp.cookie.SimpleCookieJar;
import com.dcyx.app.util.okhttp.listener.DisposeDataHandle;
import com.dcyx.app.util.okhttp.response.CommonFileCallback;
import com.dcyx.app.util.okhttp.response.CommonJsonCallback;
import com.dcyx.app.util.okhttp.ssl.HttpsUtils;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author vision
 * @function 用来发送get,post请求的工具类，包括设置一些请求的共用参数
 */
public class CommonOkHttpClient
{
	private static final int TIME_OUT = 20;
	private static OkHttpClient mOkHttpClient;
	// private static CommonOkHttpClient mClient = null;

	static
	{

		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
		okHttpClientBuilder.hostnameVerifier(new HostnameVerifier()
		{
			@Override
			public boolean verify(String hostname, SSLSession session)
			{
				return true;
			}
		});

		okHttpClientBuilder.cookieJar(new SimpleCookieJar());
		okHttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
		okHttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
		okHttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
		okHttpClientBuilder.followRedirects(true);
		/**
		 * trust all the https point
		 */
//		okHttpClientBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory());
		mOkHttpClient = okHttpClientBuilder.build();
	}

	/**
	 * 指定cilent信任指定证书
	 *
	 * @param certificates
	 */
	public static void setCertificates(InputStream... certificates)
	{
		mOkHttpClient.newBuilder().sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null)).build();
	}

	/**
	 * 指定client信任所有证书
	 */
	public static void setCertificates()
	{
		mOkHttpClient.newBuilder().sslSocketFactory(HttpsUtils.getSslSocketFactory());
	}

	/**
	 *
	 * @param request 请求实体
	 * @param handle 回调接口
	 * @param view 当前视图
	 * @param msg 进度条提示内容
	 * @return
	 */
	public static Call get(Request request, DisposeDataHandle handle, IBaseView view, String msg) {
		if (NetworkUtils.isConnected()) {
			isInMainThread(view,msg);
			Call call = mOkHttpClient.newCall(request);
			call.enqueue(new CommonJsonCallback(handle,view));
			return call;
		} else {
			ToastUtils.showLongToastSafe(R.string.network_note);
			return null;
		}

	}

	/**
	 *
	 * @param request 请求实体
	 * @param handle 回调接口
	 * @param view 当前视图
	 * @param msg 进度条提示内容
	 * @return
	 */
	public static Call post(Request request, DisposeDataHandle handle,IBaseView view,String msg)
	{
		if(NetworkUtils.isConnected()) {
			isInMainThread(view,msg);
			Call call = mOkHttpClient.newCall(request);
			call.enqueue(new CommonJsonCallback(handle,view));
			return call;
		}
		else{
			ToastUtils.showLongToastSafe(R.string.network_note);
			return null;
		}
	}
	/**
	 *
	 * @param request 请求实体
	 * @param handle 回调接口
	 * @param view 当前视图
	 * @param msg 进度条提示内容
	 * @return
	 */
	public static Call downloadFile(Request request, DisposeDataHandle handle,IBaseView view,String msg)//
	{

		if(NetworkUtils.isConnected()) {
			isInMainThread(view,msg);
			Call call = mOkHttpClient.newCall(request);
			call.enqueue(new CommonFileCallback(handle));
			return call;
		}
		else{
			ToastUtils.showLongToastSafe(R.string.network_note);
			return null;
		}
	}

	/**
	 * 判断是否在主线程中调用
	 * @param view 如果不要加载进度条传null
	 */
	private static void isInMainThread(IBaseView view,String msg) {
		if (view != null&& !StringUtils.isEmpty(msg)) {
			view.showProgressDialog(msg);
			/*boolean flag = Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId() ? true : false;
			if (flag) {//防止在非主线程内调用该封装请求类报错
				view.showProgressDialog(msg);
			} else {
				Looper.prepare();
				view.showProgressDialog(msg);
				Looper.loop();
			}*/
		}
	}
}
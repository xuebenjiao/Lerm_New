package com.dcyx.app.util;

/**
 * 网络相关工具类
 * Created by xue on 2017/8/10.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dcyx.app.global.CustomApplication;
import com.dcyx.app.global.config.HttpParamsConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 跟网络相关的工具类
 *
 *
 *
 */
public class NetworkUtils
{
    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public enum NetworkType {
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    /**
     * 打开网络设置界面
     * <p>3.0以下打开设置界面</p>
     */
    public static void openWirelessSettings() {

        if (android.os.Build.VERSION.SDK_INT > 10) {
            CustomApplication.getInstance().context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            CustomApplication.getInstance().context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    /**
     * 获取活动网络信息
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) CustomApplication.getInstance().context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * 判断网络是否连接
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 判断网络是否可用
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return {@code true}: 可用<br>{@code false}: 不可用
     */
    public static boolean isAvailableByPing() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("ping -w 2 123.125.114.144", false);// ping -c 1 -w 1 223.5.5.5
        boolean ret = result.result == 0;
        if (result.errorMsg != null) {
            LogUtils.d("isAvailableByPing errorMsg", result.errorMsg);
        }
        if (result.successMsg != null) {
            LogUtils.d("isAvailableByPing successMsg", result.successMsg);
        }
        return ret;
    }

    /**
     * 判断移动数据是否打开
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean getDataEnabled() {
        try {
            TelephonyManager tm = (TelephonyManager) CustomApplication.getInstance().context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 打开或关闭移动数据
     * <p>需系统应用 需添加权限{@code <uses-permission android:name="android.permission.MODIFY_PHONE_STATE"/>}</p>
     *
     * @param enabled {@code true}: 打开<br>{@code false}: 关闭
     */
    public static void setDataEnabled(boolean enabled) {
        try {
            TelephonyManager tm = (TelephonyManager) CustomApplication.getInstance().context.getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (null != setMobileDataEnabledMethod) {
                setMobileDataEnabledMethod.invoke(tm, enabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断网络是否是4G
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean is4G() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * 判断wifi是否打开
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean getWifiEnabled() {
        WifiManager wifiManager = (WifiManager) CustomApplication.getInstance().context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 打开或关闭wifi
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>}</p>
     *
     * @param enabled {@code true}: 打开<br>{@code false}: 关闭
     */
    public static void setWifiEnabled( boolean enabled) {
        WifiManager wifiManager = (WifiManager)CustomApplication.getInstance().context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (enabled) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    /**
     * 判断wifi是否连接状态
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 连接<br>{@code false}: 未连接
     */
    public static boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) CustomApplication.getInstance().context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断wifi数据是否可用
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isWifiAvailable() {
        return getWifiEnabled() && isAvailableByPing();
    }

    /**
     * 获取网络运营商名称
     * <p>中国移动、如中国联通、中国电信</p>
     *
     * @return 运营商名称
     */
    public static String getNetworkOperatorName() {
        TelephonyManager tm = (TelephonyManager) CustomApplication.getInstance().context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkOperatorName() : null;
    }

    private static final int NETWORK_TYPE_GSM      = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN    = 18;

    /**
     * 获取当前网络类型
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return 网络类型
     * <ul>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_WIFI   } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_4G     } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_3G     } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_2G     } </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_UNKNOWN} </li>
     * <li>{@link NetworkUtils.NetworkType#NETWORK_NO     } </li>
     * </ul>
     */
    public static NetworkType getNetworkType() {
        NetworkType netType = NetworkType.NETWORK_NO;
        NetworkInfo info = getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {

                    case NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netType = NetworkType.NETWORK_2G;
                        break;

                    case NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netType = NetworkType.NETWORK_3G;
                        break;

                    case NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netType = NetworkType.NETWORK_4G;
                        break;
                    default:

                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            netType = NetworkType.NETWORK_3G;
                        } else {
                            netType = NetworkType.NETWORK_UNKNOWN;
                        }
                        break;
                }
            } else {
                netType = NetworkType.NETWORK_UNKNOWN;
            }
        }
        return netType;
    }

    /**
     * 获取IP地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @param useIPv4 是否用IPv4
     * @return IP地址
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); ) {
                NetworkInterface ni = nis.nextElement();
                // 防止小米手机返回10.0.2.15
                if (!ni.isUp()) continue;
                for (Enumeration<InetAddress> addresses = ni.getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        boolean isIPv4 = hostAddress.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4) return hostAddress;
                        } else {
                            if (!isIPv4) {
                                int index = hostAddress.indexOf('%');
                                return index < 0 ? hostAddress.toUpperCase() : hostAddress.substring(0, index).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取域名ip地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @param domain 域名
     * @return ip地址
     */
    public static String getDomainAddress(final String domain) {
        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            Future<String> fs = exec.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    InetAddress inetAddress;
                    try {
                        inetAddress = InetAddress.getByName(domain);
                        return inetAddress.getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
            return fs.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }
    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity)
    {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }
    /* 获取获取外网外网地址  需在异步线程中访问
    * @param ipaddr 提供外网服务的服务器ip地址
    * @return       外网IP
    */
    public static String GetOuterNetIp(String ipaddr) {
        URL infoUrl = null;
        String str  = "";
        InputStream inStream = null;
        try {
            infoUrl = new URL(ipaddr);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strber.append(line + "\n");

                }
                inStream.close();
                str =  strber.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 通过CmyIP获取获取外网外网地址  需在异步线程中访问
     * @return 外网IP
     */
    public static String  getOuterNetFormCmyIP() {
        //http请求或者网页访问 https://cmyip.com/ 就能获取本地的外网IP   公司这种方式不行，由于获取的外网ip 请求回来的数据不正确
        String response = GetOuterNetIp("https://cmyip.com/");//https://cmyip.com/  http://www.cmyip.com/
        Pattern pattern = Pattern
                .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
        Matcher matcher = pattern.matcher(response.toString());
        if (matcher.find()) {
            String group = matcher.group();
//            locateCityName(group);
            return group;
        }
        return "";
    }

    /**
     * 根据网络IP进行定位
     */
    public static final String sGetAddrUrl = "http://ip-api.com/json/"; //根据此网址加外网ip就可以返回定位到城市信息
    public static void locateCityName(final String foreignIPString) {
        new Thread() {
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    StringBuffer requestStr =  new StringBuffer();
                    if(StringUtils.isEmpty(foreignIPString)){
                        requestStr.append(sGetAddrUrl+GetOutNetIp());
                    }
                    else{
                        requestStr.append(sGetAddrUrl + foreignIPString);
                    }
                    HttpGet request = new HttpGet(requestStr.toString());
                    HttpResponse response = httpClient.execute(request);
                    if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                        String json = EntityUtils.toString(response.getEntity());
                        JSONObject jsonObject = new JSONObject(json);
//                        updateWithNewLocation((double)(jsonObject.get("lat")),(double)(jsonObject.get("lat")));
                        getCityNameFromLoca((double)(jsonObject.get("lat")),(double)(jsonObject.get("lon")));
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }.start();

    }
    /**
     * 获取外网IP地址
     * @return
     */
    public static String GetOutNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String line = "";
        try {
            infoUrl = new URL("http://1irw6am4qfsjf66p7mt8c1jwk6qid99x.edns.ip-api.com/json?lang=en");//"http://pv.sohu.com/cityjson?ie=utf-8"获取的外网IP 不能正确的获取定位信息
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    strber.append(line + "\n");
                }
                inStream.close();
                // 从反馈的结果中提取出IP地址
                if (strber.toString() != null) {
                    JSONObject jsonObject = new JSONObject(strber.toString());
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("dns"));
                    line = jsonObject1.optString("ip");
                }
                return line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return line;
    }
    /**
     * 获取外网IP地址
     * @return
     */
    public static String GetNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String line = "";
        try {
            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");//"http://pv.sohu.com/cityjson?ie=utf-8"获取的外网IP 不能正确的获取定位信息
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                // 从反馈的结果中提取出IP地址
                int start = strber.indexOf("{");
                int end = strber.indexOf("}");
                String json = strber.substring(start, end + 1);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        line = jsonObject.optString("cip");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    /**
     * 获取wifi 信息
     * @param context
     * @return
     */
    public static Map<String,String> getWifiNetInfo(Context context){
        Map<String,String> wifiInfo = new HashMap<>();
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifi  != null){
            DhcpInfo info = wifi.getDhcpInfo();
            wifiInfo.put("wifi-dns", intToIp(info.dns1) + ";" + intToIp(info.dns2));
            wifiInfo.put("wifi-gateway", intToIp(info.gateway));
            wifiInfo.put("wifi-ip", intToIp(info.ipAddress));
            wifiInfo.put("wifi-netmask", intToIp(info.netmask));
            wifiInfo.put("wifi-leaseTime", String.valueOf(info.leaseDuration));
            wifiInfo.put("wifi-dhcpServer", intToIp(info.serverAddress));
        }
        return wifiInfo;
    }

    public static String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }
    public static  String getLocalDNS(){
        Process cmdProcess = null;
        BufferedReader reader = null;
        String dnsIP = "";
        try {
            cmdProcess = Runtime.getRuntime().exec("getprop net.dns1");
            reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
            dnsIP = reader.readLine();
            return dnsIP;
        } catch (IOException e) {
            return null;
        } finally{
            try {
                reader.close();
            } catch (IOException e) {
            }
            cmdProcess.destroy();
        }
    }
    /**
     * 根据 经纬度获取城市名称
     * @param location
     * @return
     */
    public static String updateWithNewLocation(Location location) {

        double lat  = 0;
        double lng = 0;
        String latLongString ="";
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {
            return latLongString = "无法获取地理信息";
        }
        List<Address> addList = null;
        Geocoder ge = new Geocoder(CustomApplication.getInstance().context);
        try {
            addList = ge.getFromLocation(lat,lng, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(addList!=null && addList.size()>0){
            for(int i=0; i<addList.size(); i++){
                Address ad = addList.get(i);
                latLongString += ad.getLocality();
            }
        }
        return latLongString;
    }
    /**
     * 根据 经纬度获取城市名称
     * @param lat 经度
     *  @param lng 纬度
     * @return
     */
    public static String updateWithNewLocation(double lat,double lng) {
        String latLongString ="";
        if(lat == 0|| lng== 0){
            return latLongString;
        }
        List<Address> addList = null;
        Geocoder ge = new Geocoder(CustomApplication.getInstance().context);
        try {
            addList = ge.getFromLocation(lat,lng, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(addList!=null && addList.size()>0){
            for(int i=0; i<addList.size(); i++){
                Address ad = addList.get(i);
                latLongString += ad.getLocality();
            }
        }
        return latLongString;
    }

    //http://2s7zjrk3ewhq0vfeaw5uup0h0gc5z2da.edns.ip-api.com/json?lang=en

    /**
     * 根据定位城市
     * @param latitude
     * @param longitude
     */
    public static void getCityNameFromLoca(double latitude,double longitude ){
        String result;
        String uriStr = MessageFormat.format("http://maps.google.cn/maps/api/geocode/json?latlng={0},{1}&sensor=true&language=zh-CN", latitude, longitude);
        HttpGet httpGet = new HttpGet(uriStr);//生成一个请求对象

        HttpClient httpClient = new DefaultHttpClient(); //生成一个Http客户端对象
        try {
            HttpResponse   httpResponse = httpClient.execute(httpGet); //使用Http客户端发送请求对象
            HttpEntity httpEntity = httpResponse.getEntity(); //获取响应内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
            result = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            getCiTyNameFromJson(result);
            Log.v("地址:",result );

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 解析json数据，找出对应的城市名称
     * @param json
     * @return
     */
    public static String getCiTyNameFromJson(String json){
        String cityName = "";
        if(StringUtils.isEmpty(json)){
            return cityName;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonObject1 =jsonObject.getJSONArray("results").getJSONObject(0);
            JSONArray jsonArray = jsonObject1.getJSONArray("address_components");
            int length = jsonArray.length();
            for(int i=0;i<length;i++){
                JSONObject jsonObject2 = (JSONObject)(jsonArray.get(i));
                String string = jsonObject2.getString("types");
                int len =  jsonObject2.getJSONArray("types").length();
                if(string.contains("locality")&&string.contains("political")&&len==2){
                    cityName = jsonObject2.getString("long_name");
                    cityName = cityName.replace("市","");
                    CustomApplication.CityName = cityName;
                    Intent intent = new Intent();
                    intent.setAction(HttpParamsConstants.LOCATION_ACTION);
                    intent.putExtra(HttpParamsConstants.LOCATION,cityName);
                    CustomApplication.getInstance().context.sendBroadcast(intent);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return cityName;
    }

    /**
     *根据当前ip定位城市
     */
    public static void getCityNameFromIp(final Handler handler,final int msgCode){
        //http://1212.ip138.com/ic.asp
        new Thread() {
            public void run() {
                URL infoUrl = null;
                InputStream inStream = null;
                String ipLine = "";
                HttpURLConnection httpConnection = null;
                try {

//            infoUrl = new URL("http://ip168.com/");  http://pv.sohu.com/cityjson?ie=utf-8  http://1212.ip138.com/ic.asp     http://whois.pconline.com.cn/ip.jsp
                    infoUrl = new URL("http://whois.pconline.com.cn/ip.jsp");
                    URLConnection connection = infoUrl.openConnection();
                    httpConnection = (HttpURLConnection) connection;
                    int responseCode = httpConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inStream = httpConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inStream, "gbk"));
//                                new InputStreamReader(inStream, "UTF-8"));
                        StringBuilder strber = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            strber.append(line + "\n");
                        }
                        StringBuffer cityName = new StringBuffer();
                        if(strber.toString().contains("省")&&strber.toString().contains("市")){
                            cityName.append(strber.substring(strber.indexOf("省")+1,strber.indexOf("市")));
                        }
                       else{
                            cityName.append(AddressUtils.getAddresses("ip="+getOuterNetFormCmyIP(),"utf-8"));
                        }
                     /*   Intent intent = new Intent();
                        intent.setAction(HandlerWhat.LOCATION_ACTION);
                        intent.putExtra(HandlerWhat.LOCATION,cityName);*/
                        Message message = new Message();
                        message.what = msgCode;
                        message.obj = cityName.toString();
                        handler.sendMessage(message);

//                        CustomApplication.getInstance().context.sendBroadcast(intent);
//                        Pattern pattern = Pattern
//                                .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
//                        Matcher matcher = pattern.matcher(strber.toString());
//                        if (matcher.find()) {
//                            ipLine = matcher.group();
//                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inStream.close();
                        httpConnection.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
package com.dcyx.app.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xue on 2017/8/10.
 */

public class HttpUtils {
    public static final MediaType JSON=MediaType.parse("application/json;charset=utf-8");//text/plain; charset=UTF-8
    public static String doJsonPost(String urlPath, String Json) {
        // HttpClient 6.0被抛弃了
        String result = "";
//        urlPath = "http://imedlab.udesk.cn/api/v1/tickets.json?";
//        Json  = "{\"sign\":\"996f0a9eda51a150161fbc74d7bff540\",\"ticket\":{\"subject\":\"Lerm设备一键报修\",\"content\":\"设备名称：12333；\\n 生产厂商：艾普瑞（上海）精密光电有限公司 \\n 供应商：上海岛昌医学科技股份有限公司 \\n 备注：测试数据\",\"email\":\"\",\"cellphone\":null,\"priority\":\"标准\",\"status\":\"解决中\"}}";
        BufferedReader reader = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("Content-Type","application/json;charset=utf-8");
            // 设置接收类型否则返回415错误
            conn.setRequestProperty("accept","*/*");//此处为暴力方法设置接受所有类型，以此来防范返回415;
//            conn.setRequestProperty("accept","application/json");
            // 往服务器里面发送数据
            if (Json != null && !TextUtils.isEmpty(Json)) {
                byte[] writebytes = Json.getBytes();
                // 设置文件长度
                conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(Json.getBytes());
                outwritestream.flush();
                outwritestream.close();
                Log.d("hlhupload", "doJsonPost: conn"+conn.getResponseCode());
            }
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    /**
     * @Title: methodPost
     * @Description: httpclient方法中post提交数据的使用
     * @param @return
     * @param @throws Exception
     * @return String
     * @throws
     */
    public static String methodPost(String url,String title,String content){
        String body = "";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        // 目标地址
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-type","application/json");
        httppost.addHeader("Accept", "application/json");
        try {
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();

            jsonObject.put("subject", title);
            jsonObject.put("content", content);
            jsonObject.put("email", "");
            jsonObject.put("cellphone", "");
            jsonObject.put("status", "解决中");
            jsonObject.put("priority", "标准");
            jsonObject2.put("sign","996f0a9eda51a150161fbc74d7bff540");
            jsonObject2.put("ticket",jsonObject);

            httppost.setEntity(new StringEntity(jsonObject2.toString(),"UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            body = EntityUtils.toString(response.getEntity());
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(e.toString());
        }
        catch (IOException e){
            LogUtils.e(e.toString());
        }
        catch (JSONException e){
            LogUtils.e(e.toString());
        }
        return body;

    }
    public static  String postJson(String url,String json) {
        //申明给服务端传递一个json串
        String result = "";
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
//        RequestBody body=RequestBody.create(MediaType.parse("text/html"),"1");
        FormBody.Builder params=new FormBody.Builder();
        try {
        JSONObject jsonObject = new JSONObject(json);
        params.add("ticket",jsonObject.toString());
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*")
                .post(requestBody)//requestBody params.build()
                .build();
        //发送请求获取响应

            Response response=okHttpClient.newCall(request).execute();
            //判断请求是否成功
            if(response.isSuccessful()){
                //打印服务端返回结果
                result =  response.body().string();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }
    public static String httpPostWithJSON(String url,String sign){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        String respContent = null;

//        json方式
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();

            jsonObject.put("subject", "Lerm设备一键报修");
            jsonObject.put("content", "设备名称：12333；\\n 生产厂商：艾普瑞（上海）精密光电有限公司 \\n 供应商：上海岛昌医学科技股份有限公司 \\n 备注：测试数据");
            jsonObject.put("subject", "Lerm设备一键报修");
            jsonObject.put("cellphone", "");
            jsonObject.put("status", "解决中");
            jsonObject.put("priority", "标准");
            jsonObject2.put("sign","996f0a9eda51a150161fbc74d7bff540");
            jsonObject2.put("ticket",jsonObject);
            StringEntity entity = new StringEntity(jsonObject2.toString(), "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);


            HttpResponse resp = httpclient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = resp.getEntity();
                respContent = EntityUtils.toString(he, "UTF-8");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return respContent;
    }

}

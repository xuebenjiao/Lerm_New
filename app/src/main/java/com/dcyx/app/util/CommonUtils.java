package com.dcyx.app.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.dcyx.app.R;

import com.dcyx.app.global.CustomApplication;
import com.dcyx.app.global.config.HttpParamsConstants;
import com.dcyx.app.mvp.model.entity.DepartmentInfoModel;
import com.dcyx.app.mvp.model.entity.StockIn.DialogStockInModel;
import com.dcyx.app.mvp.model.entity.MenuEntity;
import com.dcyx.app.mvp.model.entity.UserInfo;

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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by xue on 2017/8/10.
 */

public class CommonUtils {
    private final static int PIC_ERROR = R.drawable.ic_launcher;// 未知模块图标
    private final static String CLASS_ERROR = "TestFragment";// 未知模块类名
    private static CommonUtils sCommonUtils = null;
 /*   // 工具类单例模式
    private CommonUtils(){
        super();
    }
    public static CommonUtils getInstanse(){
        if(sCommonUtils == null){
            sCommonUtils = new CommonUtils();
        }
        return sCommonUtils;
    }*/
    /**
     * 判断字符串是否是纯数字
     * @param string
     * @return true 是   false 不是
     */
    public static boolean isNumeric1(String string){
        return string.matches("\\d+\\.?\\d*");
    }
    /**
     * @desc 去除字符串中的回车、换行符、制表符 和空格
     * @param str  要处理特殊字符的源字符串
     * @return 返回去空格、回车符和制表符后的字符串
     */
    public static String replaceBlankALLSpace(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取data/data/package/files目录
     * @param context
     * @return
     */
    public static String getAppFilesDir(Context context){
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 获取data/data/package/cache目录
     * @param context
     * @return
     */
    public static String getAppCacheDir(Context context){
        return context.getCacheDir().getAbsolutePath();
    }
    /**
     * 判断对象是否为空
     * @param obj 对象
     * @return {@code true}: 为空<br>{@code false}: 不为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String && obj.toString().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof SparseArray && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseBooleanArray && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseIntArray && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (obj instanceof SparseLongArray && ((SparseLongArray) obj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断对象是否非空
     *
     * @param obj 对象
     * @return {@code true}: 非空<br>{@code false}: 空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
    /**
     * @Description: 获取菜单选项
     * @param context 上下文
     * @return ArrayList<MenuEntity>
     */
    public static ArrayList<MenuEntity> getMenuList(Context context) {
        boolean isOpenOneKey = (boolean) SharedPreUtils.get(context, HttpParamsConstants.SETTING_ONE_KEY_REPCH,false);//默认是关
        ArrayList<MenuEntity> menuList = new ArrayList<MenuEntity>();

        String[] nameArray = context.getResources().getStringArray(
                R.array.main_menu_name);// 从array.xml资源文件中取模块名称列

        String[] iconArray = context.getResources().getStringArray(
                R.array.main_menu_icon);// 从array.xml资源文件中取模块图标列

        String[] classNameArray = context.getResources().getStringArray(
                R.array.main_menu_class);// 从array.xml资源文件中取模块实现类名列

        if (nameArray == null) {

            return null;
        }

        int name_item_length = nameArray.length;
        int icon_item_length = 0;
        int class_item_length = 0;

        if (iconArray != null) {
            icon_item_length = iconArray.length;
        }
        if (classNameArray != null) {

            class_item_length = classNameArray.length;
        }

        MenuEntity item = null;
        for (int i = 0; i < name_item_length; i++) {

            item = new MenuEntity();
            item.NAME = nameArray[i];
            if("一键报修,一键采购".contains(item.NAME)&&!isOpenOneKey){//一键报修与一键采购暂时屏蔽（LermCloud暂时屏蔽）
                continue;
            }

            //判断如果是LermCloud则加载盘库，Lerm不加载
            if(!CustomApplication.IS_LERM_CLOUND&&item.NAME.equals("盘库")) {
                continue;
            }
            if(item.NAME.equals("出库计划")&&!CustomApplication.sUserInfo.isShowItemKit()){
                continue;
            }
            if (icon_item_length < i + 1) {

                item.PIC_ID = PIC_ERROR;
            } else {

                item.PIC_ID = getImageID(iconArray[i]);// 通过字符串取对应的图片资源id
            }
            if (class_item_length < i + 1) {

                item.CLASSNAME = CLASS_ERROR;
            } else {
                item.CLASSNAME = classNameArray[i];
            }

            menuList.add(item);
        }
        return menuList;
    }
    /**
     * @Description:获取指定名称的图片资源的id
     * @param pic
     * @return 图片资源的id
     */
    public static int getImageID(String pic) {

        if (pic == null || pic.trim().equals("")) {
            return PIC_ERROR;
        }
        @SuppressWarnings("rawtypes")
        Class draw = R.drawable.class;
        try {

            Field field = draw.getDeclaredField(pic);
            return field.getInt(pic);
        } catch (Exception e) {
            return PIC_ERROR;
        }
    }
    /**
     * 根据类名获得类
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstanceByName(String className,boolean isActivity) {
        String classPath = "";
        if(isActivity){
            classPath = "com.dcyx.app.mvp.ui.activity.";
        }
        else{
            classPath = "com.dcyx.app.mvp.ui.fragment.";
        }
        Class<?> t = null;
        try {
            t = Class.forName("com.dcyx.app.mvp.ui.activity." + className);
            return (T) t;
//            return (T) t.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 获取地理位置
     *
     * @throws Exception
     */
    public static String getLocation(String latitude, String longitude) throws Exception {
        String resultString = "";

        /** 这里采用get方法，直接将参数加到URL上  http://api.map.baidu.com/geocoder?  http://maps.google.cn/maps/geo?*/
        String urlString = String.format("http://api.map.baidu.com/geocoder?output=json&location=%s,%s", latitude, longitude);
        LogUtils.i("Util: getLocation: URL: " + urlString);

        /** 新建HttpClient */
        HttpClient client = new DefaultHttpClient();
        /** 采用GET方法 */
        HttpGet get = new HttpGet(urlString+"&ak=esNPFDwwsXWtsQfw4NMNmur1");
        try {
            /** 发起GET请求并获得返回数据 */
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuffer strBuff = new StringBuffer();
            String result = null;
            while ((result = buffReader.readLine()) != null) {
                strBuff.append(result);
            }
            resultString = strBuff.toString();

            /** 解析JSON数据，获得物理地址 */
            if (resultString != null && resultString.length() > 0) {
                JSONObject jsonobject = new JSONObject(resultString);
                JSONArray jsonArray = new JSONArray(jsonobject.get("Placemark").toString());
                resultString = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    resultString = jsonArray.getJSONObject(i).getString("address");
                }
            }
        } catch (Exception e) {
            throw new Exception("获取物理位置出现错误:" + e.getMessage());
        } finally {
            get.abort();
            client = null;
        }

        return resultString;
    }
    public static String getIpUrl = "http://www.hao123.com";

    public static void getWebIp(final Handler handler) {
        new Thread() {
            public void run() {
                String strForeignIP = "";
                try {
                    URL url = new URL(getIpUrl);

                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

                    String s = "";
                    StringBuffer sb = new StringBuffer("");
                    while ((s = br.readLine()) != null) {
                        sb.append(s + "\r\n");
                    }
                    br.close();

                    String webContent = "";
                    webContent = sb.toString();
                    String flagofForeignIPString = "IPMessage";
                    int startIP = webContent.indexOf(flagofForeignIPString)
                            + flagofForeignIPString.length() + 2;
                    int endIP = webContent.indexOf("</span>", startIP);
                    strForeignIP = webContent.substring(startIP, endIP);
                    locateCityName(strForeignIP,handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();

    }
    public static final String sGetAddrUrl = "http://ip-api.com/json/";

    public static void locateCityName(final String foreignIPString,final Handler handler) {
        new Thread() {
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String requestStr = sGetAddrUrl + foreignIPString;
                    HttpGet request = new HttpGet(requestStr);
                    HttpResponse response = httpClient.execute(request);
                    if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                        String cityName = EntityUtils.toString(response.getEntity());
                        Message message = new Message();
                        message.obj = cityName;
                        handler.sendMessage(message);

                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }.start();

    }
    /**
     * 从网络返回的json数据中提取ResultData[num].DataTable字段
     * @param json
     * @return
     */
    public static String getJsonDataFromResult1(String json,String nodeName){
        if(StringUtils.isEmpty(json)){
            return "";
        }
        try {
            JSONObject object = new JSONObject(json);
            String data = object.getString("Data");
            if(!StringUtils.isEmpty(data)){
                JSONObject object1 = new JSONObject(data);
                String result =  object1.getString(nodeName);
                if(!StringUtils.isEmpty(result)){
                    return result;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }
    /**
     * 根据json判断后台查询是否成功
     *
     * @param json
     * @return
     */
    public static boolean isGetHttpDataSuccess(String json) {
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        try {
            JSONObject object = new JSONObject(json);
            String str = object.getString("Data");
            if(!StringUtils.isEmpty(str)){
                JSONObject object1 = new JSONObject(str);
                String success = object1.getString("Success");
                if (success.equals("true")) {
                    return true;
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }
   /* *//**
     * 根据json判断后台查询是否成功
     *
     * @param json
     * @return
     *//*
    public static String isGetHttpDataSuccess(String json) {
        if (StringUtils.isEmpty(json)) {
            return "";
        }
        try {
            JSONObject object = new JSONObject(json);
            String str = object.getString("Data");
            if(!StringUtils.isEmpty(str)){
                JSONObject object1 = new JSONObject(str);
                String success = object1.getString("Success");
                if (success.equals("true")) {
                    return true;
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }*/
    /**
     * pull解析xml数据 判断是否真确返回
     * @param result 需要解析的字符串
     * @return
     */
    public static boolean pull2xml(String result){
        try {
            InputStream  is= new ByteArrayInputStream(result.getBytes());
            //创建xmlPull解析器
            XmlPullParser parser = Xml.newPullParser();
            ///初始化xmlPull解析器
            parser.setInput(is, "utf-8");
            //读取文件的类型
            int type = parser.getEventType();
            //无限判断文件类型进行读取
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    //开始标签
                    case XmlPullParser.START_TAG:
                        if ("Success".equals(parser.getName())) {
                            return   (parser.nextText()).equals("true")?true:false;
                        }
                        break;
                    //结束标签
                    case XmlPullParser.END_TAG:
                        if ("UserInfoModel".equals(parser.getName())) {
                        }
                        break;
                }
                //继续往下读取标签类型
                type = parser.next();
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
            return false;
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * pull解析xml数据 判断是否真确返回
     * @param result 需要解析的字符串
     * @return
     */
    public static String getXmlMessageInfo(String result){
        try {
            InputStream  is= new ByteArrayInputStream(result.getBytes());
            //创建xmlPull解析器
            XmlPullParser parser = Xml.newPullParser();
            ///初始化xmlPull解析器
            parser.setInput(is, "utf-8");
            //读取文件的类型
            int type = parser.getEventType();
            //无限判断文件类型进行读取
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    //开始标签
                    case XmlPullParser.START_TAG:
                        if ("Message".equals(parser.getName())) {
                            return   parser.nextText();
                        }
                        break;
                    //结束标签
                    case XmlPullParser.END_TAG:
                        break;
                }
                //继续往下读取标签类型
                type = parser.next();
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
            return "";
        }
        catch (IOException e){
            e.printStackTrace();
            return "";
        }
        return "";
    }
    /**
     * dom解析xml数据
     * @param is
     * @return
     * @throws Exception
     */
    public List<UserInfo> dom2xml(InputStream is) throws Exception {
        //一系列的初始化
        List<UserInfo> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        //获得Document对象
        Document document = builder.parse(is);
        //获得student的List
        NodeList studentList = document.getElementsByTagName("a:UserInfoModel");
        //遍历student标签
        for (int i = 0; i < studentList.getLength(); i++) {
            //获得student标签
            Node node_student = studentList.item(i);
            //获得student标签里面的标签
            NodeList childNodes = node_student.getChildNodes();
            //新建student对象
            UserInfo student = new UserInfo();
            //遍历student标签里面的标签
            for (int j = 0; j < childNodes.getLength(); j++) {
                //获得name和nickName标签
                Node childNode = childNodes.item(j);
                //判断是name还是nickName
                if ("a:Birthday".equals(childNode.getNodeName())) {
                    String birth = childNode.getTextContent();
//					student.setName(name);
                    //获取name的属性
//					NamedNodeMap nnm = childNode.getAttributes();
//					childNodes.item(9).getTextContent();
                    //获取sex属性，由于只有一个属性，所以取0
//					Node n = nnm.item(0);
                    student.setBirthday(birth);
                } else if ("a:SexShow".equals(childNode.getNodeName())) {
                    String sex = childNode.getTextContent();
                    student.setSex(sex);
                }
            }
            //加到List中
            list.add(student);
        }
        return list;
    }

    /**
     * sax解析xml数据
     * @param is
     * @return
     * @throws Exception
     */
    public List<UserInfo> sax2xml(InputStream is) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        //初始化Sax解析器
        SAXParser sp = spf.newSAXParser();
        //新建解析处理器
        MyHandler handler = new MyHandler();
        //将解析交给处理器
        sp.parse(is, handler);
        //返回List
        return handler.getList();
    }
    public class MyHandler extends DefaultHandler {

        private List<UserInfo> list;
        private UserInfo student;
        //用于存储读取的临时变量
        private String tempString;

        /**
         * 解析到文档开始调用，一般做初始化操作
         *
         * @throws SAXException
         */
        @Override
        public void startDocument() throws SAXException {
            list = new ArrayList<>();
            super.startDocument();
        }

        /**
         * 解析到文档末尾调用，一般做回收操作
         *
         * @throws SAXException
         */
        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        /**
         * 每读到一个元素就调用该方法
         *
         * @param uri
         * @param localName
         * @param qName
         * @param attributes
         * @throws SAXException
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("a:UserInfoModel".equals(qName)) {
                //读到student标签
                student = new UserInfo();
//			} else if ("a:Birthday".equals(qName)) {
//				//获取name里面的属性
//				String sex = attributes.getValue("sex");
//				student.setSex(sex);
            }
            super.startElement(uri, localName, qName, attributes);
        }

        /**
         * 读到元素的结尾调用
         *
         * @param uri
         * @param localName
         * @param qName
         * @throws SAXException
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("a:UserInfoModel".equals(qName)) {
                list.add(student);
            }
            if ("a:Birthday".equals(qName)) {
                student.setBirthday(qName);
            } else if ("a:SexShow".equals(qName)) {
                student.setSex(qName);
            }
            super.endElement(uri, localName, qName);
        }

        /**
         * 读到属性内容调用
         *
         * @param ch
         * @param start
         * @param length
         * @throws SAXException
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            tempString = new String(ch, start, length);
            super.characters(ch, start, length);
        }

        /**
         * 获取该List
         *
         * @return
         */
        public List<UserInfo> getList() {
            return list;
        }
    }

    /**
     * 根据扫描码请求数据
     * 码格式 01.01.03.001.125363/777//20171231
     * 01.01.03.001.lkbm1/20180228//2019-01-01///GY0002
     * 二维码 = 商品码 + 批号 + 序号 + 有效期 + 注册证号 + 收货单号 + 订单备注组成
     */
    public static DialogStockInModel splitScanResult(String scanCode){
        DialogStockInModel model = new DialogStockInModel();
        if(StringUtils.isEmpty(scanCode)){
            return model;
        }
        else{
            String[] scanArr = scanCode.split("/");
            if(scanArr.length>=1&&scanArr[0]!=null) {
                model.setSearchCode(scanArr[0]);
            }
            if(scanArr.length>=2&&scanArr[1]!=null) {
                model.setLotNumber(scanArr[1]);
            }
            if(scanArr.length>=4&&scanArr[3]!=null){

                if(scanArr[3].contains(".")){
                    String str = scanArr[3].replace(".","-");
                    model.setExpiryDate(str);
                }
                else if(scanArr[3].contains("-")){
                    model.setExpiryDate(scanArr[3]);
                }
                else {
                    StringBuilder sb = new StringBuilder();
                    if (scanArr[3].length() == 8) {//正确的时间为8位
                        sb.append(scanArr[3].substring(0, 4) + "/" + scanArr[3].substring(4, 6) + "/" + scanArr[3].substring(6, 8));
                        model.setExpiryDate(sb.toString());
                    }
                }

            }
            if(scanArr.length>=7&&!StringUtils.isEmpty(scanArr[6])){
                model.setSupplierSearchCode(scanArr[6]);
            }
        }
        return model;
    }

    /**
     * 判断字符是否是空或者null 如果是就返回0 反之返回字符   为了解析服务器返回来的字符转换时间报错
     * @param str
     * @return
     */
    public static String isStrIsNullOrEmpty(String str){
        if(StringUtils.isEmpty(str)){
            return "0";
        }
        else{
            return str;
        }
    }
    /**
     * 根据部门名称获取部门id
     * @return
     */
    public static String getGroupIdByDeptName(String deptName){
        if(StringUtils.isEmpty(deptName)){
            return "";
        }
        if(CustomApplication.sAllDepartmentInfoModels !=null) {
            int length = CustomApplication.sAllDepartmentInfoModels.size();
            for (int i = 0; i < length; i++) {
                DepartmentInfoModel model = CustomApplication.sAllDepartmentInfoModels.get(i);
                if (model.getName().equals(deptName)) {
                    return model.getDepartmentCode();
                }
            }
        }
        return deptName;
    }
    /**
     * 根据所有组别名称，获取组别数据
     * @param list 组别集合
     * @param groupName 组别名称
     * @return 组别对象
     */
    public static DepartmentInfoModel getGroupInfoFromSelete(List<DepartmentInfoModel> list,String groupName){
        DepartmentInfoModel model = null;
        if(list!=null&&list.size()>0&&!StringUtils.isEmpty(groupName)){
            for(DepartmentInfoModel item:list){
                if(groupName.equals(item.getName())){
                    model = item;
                    break;
                }
            }
        }
        return model;

    }

    /**
     * @Description: 隐藏软键盘
     * @param view 控件
     */
    public static void HideSoftInputByContext(View view) {

        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        context.getWindow().getDecorView().getWindowToken();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    /**
     * 提取字符中的数字
     *
     */
    public static int getOneNumFromStr(String str) {
        Pattern  p = Pattern.compile("(\\d+(\\.\\d+)?)");//在这里，编译 成一个正则。
        Matcher m = p.matcher(str);//获得匹
        if(m.find()){
            return Integer.parseInt(m.group());
        }
        else{
            return 0;
        }
    }

    /**
     * 获取字符创中的所有数字，将数字以数组的形式返回
     * @param str
     * @return
     */
    public static int[] getAllNumFromStr(String str) {
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String result = m.replaceAll("#");
        String[] res = result.split("#");
        int[] arr = new int[res.length];
        int length = res.length;
        for(int i=0;i<length;i++){
            if(!StringUtils.isEmpty(res[i])){
                arr[i] = Integer.parseInt(res[i]);
            }
        }
        return arr;
    }

    /**
     *
     * @param bQuery
     * @param bAllGroup
     * @param IsNotNeedPablicGroup
     * @return
     */
    public static List<DepartmentInfoModel> GetDepartments(boolean bQuery , boolean bAllGroup , boolean IsNotNeedPablicGroup ){
        List<DepartmentInfoModel> models = new ArrayList<DepartmentInfoModel>();
        if(CustomApplication.sUserInfo!=null) {
            if (CustomApplication.sUserInfo.getSuperUser()) {
                if (!IsNotNeedPablicGroup) {
                    models = GetDepartmentFromType("3");//如果是管理员且需要添加公共组别
                } else {//如果是管理员且不需要添加公共组别
                    models = GetDepartmentFromTypeAndName("3", "公共组别");
                }
            } else if (CustomApplication.sUserInfo.getGroupLst()!=null
                    &&CustomApplication.sUserInfo.getGroupLst().size()>0&&!bAllGroup) {
                models = GetDepartmentFromInstanceId();
                if(!IsNotNeedPablicGroup){
                    models.addAll(GetDepartmentFromName("公共组别"));
                }
            }
            else{
                models = GetDepartmentFromDeptId(CustomApplication.sUserInfo.getDepartmentId(),"公共组别");
                if(!IsNotNeedPablicGroup){
                    models.addAll(GetDepartmentFromName("公共组别"));
                }
            }
        }
        return models;
    }


    /**
     * 根据类型查找组别信息
     * @param type 类别
     * @return
     */
    public static List<DepartmentInfoModel> GetDepartmentFromType(String type){
        List<DepartmentInfoModel> list = new ArrayList<DepartmentInfoModel>();
        if(CustomApplication.sAllDepartmentInfoModels!=null&&!StringUtils.isEmpty(type)) {
            for (DepartmentInfoModel model : CustomApplication.sAllDepartmentInfoModels) {
                if(!StringUtils.isEmpty(model.getType())&&model.getType().equals(type)){
                    list.add(model);
                }
            }
        }
        return list;
    }

    /**
     * 根据InstanceId查找组别信息
     * @return
     */
    public static List<DepartmentInfoModel> GetDepartmentFromInstanceId() {
        List<DepartmentInfoModel> list = new ArrayList<DepartmentInfoModel>();
        if (CustomApplication.sAllDepartmentInfoModels != null) {
            for (DepartmentInfoModel groupModel : CustomApplication.sUserInfo.getGroupLst()) {
                for (DepartmentInfoModel deptModel : CustomApplication.sAllDepartmentInfoModels) {
                    if (!StringUtils.isEmpty(groupModel.getInstanceId())&&
                            !StringUtils.isEmpty(deptModel.getInstanceId())&&
                            groupModel.getInstanceId().equals(deptModel.getInstanceId())) {
                        list.add(deptModel);
                    }
                }
            }
        }
        return list;
    }
    /**
     * 根据类型查找组别信息
     * @param type 类别
     * @param deptName 组别名称
     * @return
     */
    public static List<DepartmentInfoModel> GetDepartmentFromTypeAndName(String type,String deptName){
        List<DepartmentInfoModel> list = new ArrayList<DepartmentInfoModel>();
        if(CustomApplication.sAllDepartmentInfoModels!=null&&!StringUtils.isEmpty(type)&&!StringUtils.isEmpty(deptName)) {
            for (DepartmentInfoModel model : CustomApplication.sAllDepartmentInfoModels) {
                if(!StringUtils.isEmpty(model.getType())&&model.getType().equals(type)
                        &&!StringUtils.isEmpty(model.getName())&&!model.getName().equals(deptName)){
                    list.add(model);
                }
            }
        }
        return list;
    }
    /**
     * 根据类型查找组别信息
     * @param deptName 组别名称
     * @return
     */
    public static List<DepartmentInfoModel> GetDepartmentFromName(String deptName){
        List<DepartmentInfoModel> list = new ArrayList<DepartmentInfoModel>();
        if(CustomApplication.sAllDepartmentInfoModels!=null&&!StringUtils.isEmpty(deptName)) {
            for (DepartmentInfoModel model : CustomApplication.sAllDepartmentInfoModels) {
                if(!StringUtils.isEmpty(model.getName())&&model.getName().equals(deptName)){
                    list.add(model);
                }
            }
        }
        return list;
    }
    /**
     * 根据类型查找组别信息
     * @param Name 组别名称
     * @param deptId 部门id
     * @return
     */
    public static List<DepartmentInfoModel> GetDepartmentFromDeptId(String deptId,String Name){
        List<DepartmentInfoModel> list = new ArrayList<DepartmentInfoModel>();
        if(CustomApplication.sAllDepartmentInfoModels!=null&&!StringUtils.isEmpty(Name)&&!StringUtils.isEmpty(deptId)) {
            for (DepartmentInfoModel model : CustomApplication.sAllDepartmentInfoModels) {
                if(!StringUtils.isEmpty(model.getParentID())&&model.getParentID().equals(deptId)
                        &&!StringUtils.isEmpty(model.getName())&&!model.getName().equals(Name)){
                    list.add(model);
                }
            }
        }
        return list;
    }

    /**
     * 判断是否是超级用户或者全组者
     * @return
     */
    public static boolean IsAllGroupOrSuperUser()
    {
        if (CustomApplication.sUserInfo!=null){
            if((CustomApplication.sUserInfo.isSuperUser()) ||
                    (CustomApplication.sUserInfo.getCompetenceModelsInstanceId()!=null&&
                            CustomApplication.sUserInfo.getCompetenceModelsInstanceId().contains("F970E7E3-A4A1-415D-B3A7-F6CCCF70CFB4"))){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    /**
     * 获取ugid
     * @return
     */
    public static String getGuid(){
        return java.util.UUID.randomUUID()+"";
    }
    public static CharSequence getColorToast(String message){
        return Html.fromHtml("<font color='#c70c1e'>"+message+"</font>");
    }
    /**
     * 根据名称创建文件夹路径并返回（如果存在SD卡且可用，则将文件夹创建在SD中，反之在应用安装的data/data/package/files下创建，）
     * @param fileName 文件夹名称
     * @return
     */
    public static String getFilePath(String fileName) {
        String file_dir = "";
        // SD卡是否存在
        if (SDCardUtils.isSDCardEnable()) {
            file_dir = SDCardUtils.getSDCardPath() + File.separator + fileName+ File.separator;
        } else {
//             CustomApplication.getInstance().getFilesDir()返回的路劲为/data/data/PACKAGE_NAME/files，其中的包就是我们建立的主Activity所在的包
            file_dir = CommonUtils.getAppFilesDir(CustomApplication.getInstance().context) + File.separator + fileName + File.separator;
        }
        return file_dir;
    }
    /**
     *
     * @Title getOutputStream
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param filePath
     * @param fileName
     * @throws IOException
     * @return FileOutputStream
     * @author xbj
     * @date 2017/11/4
     */
    public static FileOutputStream getOutputStream(String filePath, String fileName) throws IOException{

        FileOutputStream fileOutputStream = null;

        File destDir = new File(filePath);//Environment.getExternalStorageDirectory(),
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file = new File(filePath + fileName);//Environment.getExternalStorageDirectory(),
        if(file.exists()){
            file.createNewFile();
        }
        fileOutputStream = new FileOutputStream(file);
        return fileOutputStream;
    }
    /**
     * 判断当前应用是否是debug状态
     */

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * @param message 文本
     * @param color  需要设置的颜色
     * @param startIndex 开始下标
     * @param endIndex 结束下标
     *@param tv 目标View
     * 设置TextView中部分文字有不同颜色的实现
     * 其中，”默认颜色红颜色” 为你要改变的文本。setSpan方法有四个参数，ForegroundColorSpan是为文本设置前景色，也就是文字颜色。如果要为文字添加背景颜色，可替换为BackgroundColorSpan。4为文本颜色改变的起始位置，spannableString.length()为文本颜色改变的结束位置。最后一个参数为布尔型，可以传入以下四种。
     * Spanned.SPAN_INCLUSIVE_EXCLUSIVE 从起始下标到终了下标，包括起始下标
     * Spanned.SPAN_INCLUSIVE_INCLUSIVE 从起始下标到终了下标，同时包括起始下标和终了下标
     * Spanned.SPAN_EXCLUSIVE_EXCLUSIVE 从起始下标到终了下标，但都不包括起始下标和终了下标
     * Spanned.SPAN_EXCLUSIVE_INCLUSIVE 从起始下标到终了下标，包括终了下标
     */
    public static void setTargetTextColor(String message ,int color,int startIndex,int endIndex,TextView tv){
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new ForegroundColorSpan(CustomApplication.getInstance().context.getResources().getColor(color)), startIndex,endIndex,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);
    }
    /**
     * 获取当前用户组别的guid
     */
    public static List<String> getUserGroupId(){
        List<String> strList = new ArrayList<String>();
        List<DepartmentInfoModel> list = CustomApplication.sUserInfo.getGroupLst();
        if(list!=null||list.size()<=0){
            return strList;
        }
        for(int i=0;i<list.size();i++){
            strList.add(list.get(i).getInstanceId());
        }
        return strList;
    }
}

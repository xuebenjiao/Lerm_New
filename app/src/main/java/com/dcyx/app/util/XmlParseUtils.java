package com.dcyx.app.util;

import android.content.res.AssetManager;
import android.util.Log;
import android.util.Xml;

import com.dcyx.app.global.CustomApplication;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;


/**
 * @author xue
 * @desc Desctription
 * @date 2017/10/17.
 * 对pull解析xml进行了封装，不用给每个xml，再创建一个解析类
 *
 */
public class XmlParseUtils {
    /**
     * 解析XML
     * @param result       xml字节流
     * @param clazz     字节码      如：Object.class
     * @param startName       开始位置
     * @return          返回List列表
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List getXmlList(String result, Class<?> clazz, String startName) {
        InputStream is= new ByteArrayInputStream(result.getBytes());
        List list = null;
        XmlPullParser parser = Xml.newPullParser();
        Object object = null;
        try {
            parser.setInput(is, "UTF-8");
            //事件类型
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        list = new ArrayList<Object>();
                        break;
                    case XmlPullParser.START_TAG:
                        //获得当前节点元素的名称
                        String name = parser.getName();
                        if (startName.equals(name)) {
                            object = clazz.newInstance();
                            //判断标签里是否有属性，如果有，则全部解析出来
                         /*   int count = parser.getAttributeCount();
                            for(int i=0; i<count; i++) {
                                setXmlValue(object, parser.getAttributeName(i), parser.getAttributeValue(i));
                            }*/
                        } else if (object != null) {
                            try {//过滤子节点
                                setXmlValue(object, name, parser.nextText());
                            } catch (Exception e) {
                                LogUtils.e("xml pull error", e.toString());
                                break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (startName.equals(parser.getName())) {
                            list.add(object);
                            object = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("xml pull error", e.toString());
        }
        return list;
    }

    /**
     * 解析XML
     * @param result       xml 字符串
     * @param clazz     字节码      如：Object.class
     * @param startName       开始位置
     * @return          返回List列表
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object getObjectFormXml(String  result, Class<?> clazz, String startName) {
        InputStream is= new ByteArrayInputStream(result.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        Object object = null;
        List<Object> list = null;
        try {

//            parser.setInput(context.getResources().getAssets().open("QueryUerInfoResponse.xml"), "utf-8");
            parser.setInput(is, "UTF-8");
            //事件类型
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //获得当前节点元素的名称
                        String name = parser.getName();
                        if (startName.equals(name)) {
                            object  = clazz.newInstance();
                            //判断标签里是否有属性，如果有，则全部解析出来
                            int count = parser.getAttributeCount();
                            for(int i=0; i<count; i++) {
                                setXmlValue(object, parser.getAttributeName(i), parser.getAttributeValue(i));
                            }
                        } else if (object != null) {
                            try{//过滤子节点
                                setXmlValue(object, name, parser.nextText());
                            }
                            catch (Exception e){
                                LogUtils.e("xml pull error", e.toString());
                                break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (startName.equals(parser.getName())) {

                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            LogUtils.e("xml pull error", e.toString());
        }
        return object;
    }

    /**
     * 解析XML
     * @param is        xml字节流
     * @param clazz     字节码      如：Object.class
     * @return          返回Object
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getXmlObject(InputStream is, Class<?> clazz) {
        XmlPullParser parser = Xml.newPullParser();
        Object object = null;
        List list = null;
        Object subObject = null;
        String subName = null;
        try {
//            parser.setInput(is, "UTF-8");
            parser.setInput(CustomApplication.getInstance().context.getResources().getAssets().open("QueryUerInfoResponse.xml"), "utf-8");
            //事件类型
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        object = clazz.newInstance();
                        break;
                    case XmlPullParser.START_TAG:
                        //获得当前节点元素的名称
                        String name = parser.getName();
                        Field[] f = null;
                        if(subObject == null){
                            f = object.getClass().getDeclaredFields();
                            //判断标签里是否有属性，如果有，则全部解析出来
                            int count = parser.getAttributeCount();
                            for(int j=0; j<count; j++) {
                                setXmlValue(object, parser.getAttributeName(j), parser.getAttributeValue(j));
                            }
                        }else{
                            f = subObject.getClass().getDeclaredFields();
                        }

                        for(int i = 0; i < f.length; i++){
                            if(f[i].getName().equalsIgnoreCase(name)){
                                //判断是不是List类型
                                if(f[i].getType().getName().equals("java.util.List")){
                                    Type type = f[i].getGenericType();
                                    if (type instanceof ParameterizedType) {
                                        //获得泛型参数的实际类型
                                        Class<?> subClazz = (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
                                        subObject = subClazz.newInstance();
                                        subName = f[i].getName();
                                        //判断标签里是否有属性，如果有，则全部解析出来
                                        int count = parser.getAttributeCount();
                                        for(int j=0; j<count; j++)
                                            setXmlValue(subObject, parser.getAttributeName(j), parser.getAttributeValue(j));

                                        if(list == null){
                                            list = new ArrayList<Object>();
                                            f[i].setAccessible(true);
                                            f[i].set(object, list);
                                        }
                                    }
                                }else{   //普通属性
                                    if(subObject != null){
                                        setXmlValue(subObject, name, parser.nextText());
                                    }else{
                                        setXmlValue(object, name, parser.nextText());
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (subObject != null && subName.equalsIgnoreCase(parser.getName())) {
                            list.add(subObject);
                            subObject = null;
                            subName = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("xml pull error", e.getMessage());
        }
        return object;
    }


    /**
     * 把xml标签的值，转换成对象里属性的值
     * @param  t    对象
     * @param name   xml标签名
     * @param value  xml标签名对应的值
     */
    public static void setXmlValue(Object t, String name, String value){
        if(StringUtils.isEmpty(value)){
            return ;
        }
        try {
            Field[] f = t.getClass().getDeclaredFields();
            for(int i = 0; i < f.length; i++){
                if(f[i].getName().equalsIgnoreCase(name)){
                    f[i].setAccessible(true);
                    //获得属性类型
                    Class<?> fieldType = f[i].getType();

                    if(fieldType == String.class) {
                        f[i].set(t, value);
                        break;
                    }else if(fieldType == Integer.TYPE) {
                        f[i].set(t, Integer.parseInt(value));
                        break;
                    }else if(fieldType == Float.TYPE) {
                        f[i].set(t, Float.parseFloat(value));
                        break;
                    }else if(fieldType == Double.TYPE) {
                        f[i].set(t, Double.parseDouble(value));
                        break;
                    }else if(fieldType == Long.TYPE) {
                        f[i].set(t, Long.parseLong(value));
                        break;
                    }else if(fieldType == Short.TYPE) {
                        f[i].set(t, Short.parseShort(value));
                        break;
                    } else if(fieldType == Boolean.class||fieldType == boolean.class) {
                        f[i].set(t, Boolean.parseBoolean(value));
                        break;
                    }
                    else{
                        f[i].set(t, value);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("xml error", e.toString());
        }
    }


    /**
     * 将xml 字符串转换成Json字符串
     * @return
     */
    public static String  getJsonFromXml(){
        AssetManager assetManager = CustomApplication.getInstance().context.getAssets();
        XmlToJson xmlToJson = null;
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("QueryUerInfoResponse.xml");
            xmlToJson = new XmlToJson.Builder(inputStream, null).build();
            inputStream.close();
        }catch (IOException e){
            LogUtils.e(e.toString());
        }
        return xmlToJson.toString();
    }
    /**
     * 解析XML
     * @param result       xml 字符串
     * @param clazz     字节码      如：Object.class
     * @param startName       开始位置
     * @param clazz1     字节码      如：Object对象中字段对应list中的对象
     * @param fieldName       Object对象中对应list的字段名称
     * @return          返回List列表
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object getObjectFormXml(String  result, Class<?> clazz, String startName, Class<?> clazz1,String fieldName) {
        InputStream is= new ByteArrayInputStream(result.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        Object object = null;
        Object object1 = null;
        List<Object> list = null;
        boolean flag = false;
        try {

//            parser.setInput(context.getResources().getAssets().open("QueryUerInfoResponse.xml"), "utf-8");
            parser.setInput(is, "UTF-8");
            //事件类型
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //获得当前节点元素的名称
                        String name = parser.getName();
                        if(name.equals("UserDepartmentRoleMappingModels")||name.equals("CompetenceModels")){
                            flag = true;
                            break;
                        }
                        if(flag){//为了过滤UserDepartmentRoleMappingModels中节点的解析，避免该节点中的InstanceId 覆盖用户本身的InstanceId
                            break;
                        }
                        if(fieldName.equals(name)){
                            list = new ArrayList<Object>();
                            break;
                        }
                        if((clazz1.getName()).contains(name)){//解析对象相中list字段对应的对象类
                            object1 = clazz1.newInstance();
                            break;
                        }
                        if (startName.equals(name)) {
                            object  = clazz.newInstance();
                            //判断标签里是否有属性，如果有，则全部解析出来
                            int count = parser.getAttributeCount();
                            for(int i=0; i<count; i++) {
                                setXmlValue(object, parser.getAttributeName(i), parser.getAttributeValue(i));
                            }
                        } else if (object != null) {
                            try{//过滤子节点
                                if(object1!=null){
                                    setXmlValue(object1, name, parser.nextText());
                                }
                                else{
                                    setXmlValue(object, name, parser.nextText());
                                }
                            }
                            catch (Exception e){
                                LogUtils.e("xml pull error", e.toString());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(object1!=null&&"DepartmentInfoModel".equals(parser.getName())){
                            list.add(object1);
                            object1= null;
                        }
                        if("GroupLst".equals(parser.getName())){
                            //TODO 添加子项
                            setXmlValue(object, parser.getName(), list);
                        }
                        if("UserDepartmentRoleMappingModels".equals(parser.getName())||"CompetenceModels".equals(parser.getName())){
                            flag = false;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            LogUtils.e("xml pull error", e.toString());
        }
        return object;
    }

    /**
     * 给对象字段中是list的赋值
     * @param t
     * @param name
     * @param list
     */
    public static void setXmlValue(Object t, String name,List<Object> list){
        try {
            Field[] f = t.getClass().getDeclaredFields();
            for (int i = 0; i < f.length; i++) {
                if (f[i].getName().equalsIgnoreCase(name)) {
                    f[i].setAccessible(true);
                    f[i].set(t, list);
                }
            }
        } catch (Exception e) {
            Log.e("xml error", e.toString());
        }
    }
}

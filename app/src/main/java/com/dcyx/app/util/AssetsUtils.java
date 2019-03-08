package com.dcyx.app.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xue
 * @desc Assets获取的相关操作类
 * @date 2017/11/7.
 */

public class AssetsUtils { private static final String ENCODING = "UTF-8";
    /**
     * 从assets获取文件
     *
     * @param context
     * @param fileName
     * @return
     * @throws IOException
     */
    public static InputStream getFileFromAssets(Context context, String fileName) throws IOException {
        AssetManager am = context.getAssets();
        return am.open(fileName);
    }

    /**
     * 从assets获取文本文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getTextFromAssets(Context context, String fileName) {
        String result = null;
        try {
            InputStream in = getFileFromAssets(context, fileName);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            result = new String(buffer, Charset.forName(ENCODING));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从assets获取文本文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getTextFromAssets2(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getFileFromAssets(context, fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder result = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parse a json file in the assets to ArrayList<?>
     * 解析json文件成ArrayList，在assets中
     * @param context
     * @param jsonName
     * @return
     */
    public static <T> ArrayList<T> parseJsonToArrayList(Context context, String jsonName) {
        String json = getTextFromAssets(context, jsonName);
        return JsonUtils.jsonToArrayList(json);
    }

    /**
     * Parse a json file in the assets to List<?>
     * 解析json文件成List，在assets中
     * @param context
     * @param jsonName
     * @return
     */
    public static <T> List<T> parseJsonToList(Context context, String jsonName) {
        String json = getTextFromAssets(context, jsonName);
        return JsonUtils.jsonToList(json);
    }

    /**
     * Parse a json file in the assets to Bean
     * 解析assets中的json文件
     * @param context
     * @param jsonName
     * @param clazz
     * @return
     */
    public static <T> T parseJsonToObject(Context context, String jsonName, Class<T> clazz) {
        String json = getTextFromAssets(context, jsonName);
        return JsonUtils.jsonToObject(json, clazz);
    }
    /**
     *
     * @Title: translationDataForDemo
     * @Description: TODO(将asserts目录下的文件输出到指定目标目录下)
     * @return void    返回类型
     * @author xbj
     */
    public static void shiftAssetFileToDestination(Context context, String fromPath,
                                              String toPath, String toFileName) {
        if (null != fromPath && null != toPath) {
            AssetManager am = context.getResources().getAssets();
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = am.open(fromPath);
                fos = CommonUtils.getOutputStream(toPath, toFileName);
                byte[] buf = new byte[1024];
                int ch = 0;
                @SuppressWarnings("unused")
                int count = 0;
                while ((ch = is.read(buf)) > 0) {
                    count += ch;
                    fos.write(buf, 0, ch);
                }
                fos.flush();
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
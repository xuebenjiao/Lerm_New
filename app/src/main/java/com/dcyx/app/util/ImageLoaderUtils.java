package com.dcyx.app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.dcyx.app.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xue on 2017/8/15.
 */

public class ImageLoaderUtils {
    private LruCache<String, Bitmap> mCache;//LruCache缓存对象
    private ListView mListView;// ListView的实例
    private Set<IamgeLoaderTask> mTask;//下载任务的集合
    public ImageLoaderUtils(){
        initLruCache();
    }
    public ImageLoaderUtils(ListView listView) {
        mListView = listView;
        initLruCache();
    }
    /**
     * 初始化缓存设置
     */
    private void initLruCache() {
        mTask = new HashSet<>();
        //获取最大可用内存  官方推荐是用当前app可用内存的八分之一
        int maxMemory = (int) Runtime.getRuntime().maxMemory()/1024;
        //设置缓存的大小
        int cacheSize = maxMemory / 8;
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存的时候调用
                return value.getByteCount()/1024;
            }
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if(evicted){
                    if(oldValue!=null){
                        oldValue.recycle();
                    }
                }
            }
        };
    }

    /**
     * 将bitmap加入到缓存中
     *
     * @param url LruCache的键，即图片的下载路径
     * @param bitmap LruCache的值，即图片的Bitmap对象
     */
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapByImageUrl(url) == null) {
            mCache.put(url, bitmap);
        }
    }

    /**
     * 从缓存中获取bitmap
     * @param url LruCache的键，即图片的下载路径
     * @return 对应传入键的Bitmap对象，或者null
     */
    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap = mCache.get(url);
        return bitmap;
    }

    /**
     * 加载Bitmap对象。
     *
     * @param start 第一个可见的ImageView的下标
     * @param end   最后一个可见的ImageView的下标
     */
    public void showIamges(int start, int end) {
        for (int i = start; i < end; i++) {
            String imageUrl = "";//ImageAdapter.URLS[i];
            //从缓存中取图片
            Bitmap bitmap = getBitmapFromCache(imageUrl);
            //如果缓存中没有，则去下载
            if (bitmap == null) {
                IamgeLoaderTask task = new IamgeLoaderTask(imageUrl);
                task.execute();
                mTask.add(task);
            } else {
                ImageView imageView = (ImageView) mListView.findViewWithTag(imageUrl);
                imageView.setImageBitmap(bitmap);
            }

        }
    }

    /**
     * 取消所有下载任务
     */
    public void cancelAllTask() {
        if (mTask != null) {
            for (IamgeLoaderTask task : mTask) {
                task.cancel(false);
            }
        }
    }

    /**
     * 显示图片
     *
     * @param imageView
     * @param imageUrl
     */
    public void showImage(ImageView imageView, String imageUrl) {
        //从缓存中取图片
        Bitmap bitmap = getBitmapFromCache(imageUrl);
        //如果缓存中没有，则去下载
        if (bitmap == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
            IamgeLoaderTask task = new IamgeLoaderTask(imageUrl);
            task.execute();
            mTask.add(task);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }
    /**
     * 下载并显示图片
     */
    private class IamgeLoaderTask extends AsyncTask<Void, Void, Bitmap> {
        private String mImageUrl;


        IamgeLoaderTask(String imageUrl) {
            mImageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = getBitmapByImageUrl(mImageUrl);
            if (bitmap != null) {
                addBitmapToCache(mImageUrl, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(mImageUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }

    /**
     * 根据图片路径下载图片Bitmap
     *
     * @param imageUrl 图片网络路径
     * @return
     */
    public Bitmap getBitmapByImageUrl(String imageUrl) {
        Bitmap bitamp = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL(imageUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(10 * 1000);
            con.setReadTimeout(10 * 1000);
            bitamp = BitmapFactory.decodeStream(con.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return bitamp;
    }
}

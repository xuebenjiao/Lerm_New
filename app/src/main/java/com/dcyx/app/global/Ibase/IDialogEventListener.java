package com.dcyx.app.global.Ibase;

import android.app.Dialog;

/**
 * @author xue
 * @desc 弹出框dialog 界面接口
 * @date 2017/9/14.
 */

public interface IDialogEventListener<T> {
    //取消
    void cancle(Dialog dialog);
    //确认
    void confirm(T t,Dialog dialog);

}

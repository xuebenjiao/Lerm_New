package com.dcyx.app.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.widget.TextView;

import com.dcyx.app.R;
import com.dcyx.app.mvp.ui.Iview.IDialogView;

import java.lang.reflect.Field;

/**
 * @author xue
 * @desc dialog封装类
 * @date 2017/9/13.
 */

public class DialogUtils {
    public static    Dialog dialog = null;
    public static final int NO_ICON = -1;  //无图标
    public static final int NO_LAYOUT = -2;  //无布局
    /**
     *
     * @param context 上下文
     * @param callBack 回调接口
     * @param layoutId 布局文件id
     */
    public static Dialog  showDialog(Context context, final IDialogView callBack, int layoutId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(layoutId, null);//获取自定义布局
        //显示自定义布局内容
        builder.setView(layout);
        //回调接口中的方法，将初始化及事件处理交由调用者
        callBack.initDiaViewAndEvent(layout,builder);
        //点击确定按钮不退出
        if(builder.create().getButton(AlertDialog.BUTTON_POSITIVE)!=null) {
            builder.create().getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        destoryDialog();
        dialog = builder.create();
        //处理Dialog 物理返回键
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                callBack.handlerDialogKeyBack();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 创建消息对话框
     *
     * @param context 上下文 必填
     * @param iconId 图标，如：R.drawable.icon 或 DialogTool.NO_ICON 必填
     * @param title 标题 必填
     * @param message 显示内容 必填
     * @param btnPostName 按钮名称 必填
     * @param postListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @param negeListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @return
     */
    public static void createMessageDialog(Context context, String title, String message,
                                           String btnPostName, OnClickListener postListener,OnClickListener negeListener, int iconId)
    {
//        destoryDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);

        if (iconId != NO_ICON)
        {
            //设置对话框图标
            builder.setIcon(iconId);
        }
        //设置对话框标题
        builder.setTitle(title);
        //设置对话框消息
        builder.setMessage(message);
        //设置按钮
        builder.setPositiveButton(btnPostName, postListener);
        builder.setNegativeButton("取消",negeListener);
        //创建一个消息对话框
        Dialog  dialog = builder.create();
        //点击弹框外围不退出
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    /**
     * 创建消息对话框
     *
     * @param context 上下文 必填
     * @param title 标题 必填
     * @param message 显示内容 必填
     * @param btnPostName 按钮名称 必填
     * @param postListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @return
     */
    public static void createMessagePostDialog(Context context, String title, String message,
                                           String btnPostName, OnClickListener postListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);

        //设置对话框标题
        builder.setTitle(title);
        //设置对话框消息
        builder.setMessage(message);
        //设置按钮
        builder.setPositiveButton(btnPostName, postListener);
        //创建一个消息对话框
        Dialog  dialog = builder.create();
        //点击弹框外围不退出
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    /**
     * 创建消息对话框
     *
     * @param context 上下文 必填
     * @param title 标题 必填
     * @param message 显示内容 必填
     * @param startIndex 显示不一样颜色字体的开始下标
     * @param endIndex 显示不一样颜色字体的结束下标
     * @param color 字体的颜色
     * @param btnPostName 按钮名称 必填
     * @param postListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @param negeListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @return
     */
    public static void createMessageDialog(Context context, String title, String message,int startIndex,int endIndex,int color,
                                           String btnPostName, OnClickListener postListener,OnClickListener negeListener)
    {
//        destoryDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
        //设置对话框标题
        builder.setTitle(title);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_message_layout, null);//获取自定义布局
        TextView tv = (TextView) layout.findViewById(R.id.dialog_tv_message);
        CommonUtils.setTargetTextColor(message,color,startIndex,endIndex,tv);
        //显示自定义布局内容
        builder.setView(layout);

        //设置按钮
        builder.setPositiveButton(btnPostName, postListener);
        builder.setNegativeButton("取消",negeListener);
        //创建一个消息对话框
        Dialog  dialog = builder.create();
        //点击弹框外围不退出
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 创建警示（确认、取消）对话框
     *
     * @param context 上下文 必填
     * @param iconId 图标，如：R.drawable.icon 或 DialogTool.NO_ICON 必填
     * @param title 标题 必填
     * @param message 显示内容 必填
     * @param positiveBtnName 确定按钮名称 必填
     * @param negativeBtnName 取消按钮名称 必填
     * @param positiveBtnListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @param negativeBtnListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @return
     */
    public static void createConfirmDialog(Context context, String title, String message,
                                           String positiveBtnName, String negativeBtnName, OnClickListener positiveBtnListener,
                                           OnClickListener negativeBtnListener, int iconId)
    {
        destoryDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);

        if (iconId != NO_ICON)
        {
            //设置对话框图标
            builder.setIcon(iconId);
        }
        //设置对话框标题
        builder.setTitle(title);
        //设置对话框消息
        builder.setMessage(message);
        //设置确定按钮
        builder.setPositiveButton(positiveBtnName, positiveBtnListener);
        //设置取消按钮
        builder.setNegativeButton(negativeBtnName, negativeBtnListener);
        //创建一个消息对话框
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }



    /**
     * 创建单选对话框
     *
     * @param context 上下文 必填
     * @param iconId 图标，如：R.drawable.icon 或 DialogTool.NO_ICON 必填
     * @param title 标题 必填
     * @param itemsString 选择项 必填
     * @param positiveBtnName 确定按钮名称 必填
     * @param negativeBtnName 取消按钮名称 必填
     * @param positiveBtnListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @param negativeBtnListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @param itemClickListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @return
     */
    public static Dialog createSingleChoiceDialog(Context context, String title, String[] itemsString,
                                                  String positiveBtnName, String negativeBtnName, OnClickListener positiveBtnListener,
                                                  OnClickListener negativeBtnListener, OnClickListener itemClickListener, int iconId)
    {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (iconId != NO_ICON)
        {
            //设置对话框图标
            builder.setIcon(iconId);
        }
        //设置对话框标题
        builder.setTitle(title);
        //设置单选选项, 参数0: 默认第一个单选按钮被选中
        builder.setSingleChoiceItems(itemsString, 0, itemClickListener);
        //设置确定按钮
        builder.setPositiveButton(positiveBtnName, positiveBtnListener);
        //设置确定按钮
        builder.setNegativeButton(negativeBtnName, negativeBtnListener);
        //创建一个消息对话框
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }



    /**
     * 创建复选对话框
     *
     * @param context 上下文 必填
     * @param iconId 图标，如：R.drawable.icon 或 DialogTool.NO_ICON 必填
     * @param title 标题 必填
     * @param itemsString 选择项 必填
     * @param positiveBtnName 确定按钮名称 必填
     * @param negativeBtnName 取消按钮名称 必填
     * @param positiveBtnListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @param negativeBtnListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @param itemClickListener 监听器，需实现android.content.DialogInterface.OnMultiChoiceClickListener;接口 必填
     * @return
     */
    public static Dialog createMultiChoiceDialog(Context context, String title, String[] itemsString,
                                                 String positiveBtnName, String negativeBtnName, OnClickListener positiveBtnListener,
                                                 OnClickListener negativeBtnListener, OnMultiChoiceClickListener itemClickListener, int iconId)
    {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (iconId != NO_ICON)
        {
            //设置对话框图标
            builder.setIcon(iconId);
        }
        //设置对话框标题
        builder.setTitle(title);
        //设置选项
        builder.setMultiChoiceItems(itemsString, null, itemClickListener);
        //设置确定按钮
        builder.setPositiveButton(positiveBtnName, positiveBtnListener);
        //设置确定按钮
        builder.setNegativeButton(negativeBtnName, negativeBtnListener);
        //创建一个消息对话框
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }



    /**
     * 创建列表对话框
     *
     * @param context 上下文 必填
     * @param iconId 图标，如：R.drawable.icon 或 DialogTool.NO_ICON 必填
     * @param title 标题 必填
     * @param itemsString 列表项 必填
     * @param negativeBtnName 取消按钮名称 必填
     * @param negativeBtnListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @return
     */
    public static Dialog createListDialog(Context context, String title, String[] itemsString,
                                          String negativeBtnName, OnClickListener negativeBtnListener,
                                          OnClickListener itemClickListener, int iconId)
    {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (iconId != NO_ICON)
        {
            //设置对话框图标
            builder.setIcon(iconId);
        }
        //设置对话框标题
        builder.setTitle(title);
        //设置列表选项
        builder.setItems(itemsString, itemClickListener);
        //设置确定按钮
        builder.setNegativeButton(negativeBtnName, negativeBtnListener);
        //创建一个消息对话框
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    /**
     * 创建自定义（含确认、取消）对话框
     *
     * @param context 上下文 必填
     * @param iconId 图标，如：R.drawable.icon 或 DialogTool.NO_ICON 必填
     * @param title 标题 必填
     * @param positiveBtnName 确定按钮名称 必填
     * @param negativeBtnName 取消按钮名称 必填
     * @param positiveBtnListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @param negativeBtnListener 监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
     * @param view 对话框中自定义视图 必填
     * @return
     */
    public static Dialog createRandomDialog(Context context, String title, String positiveBtnName,
                                            String negativeBtnName, OnClickListener positiveBtnListener,
                                            OnClickListener negativeBtnListener, View view, int iconId)
    {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (iconId != NO_ICON)
        {
            //设置对话框图标
            builder.setIcon(iconId);
        }
        //设置对话框标题
        builder.setTitle(title);
        builder.setView(view);
        //设置确定按钮
        builder.setPositiveButton(positiveBtnName, positiveBtnListener);
        //设置确定按钮
        builder.setNegativeButton(negativeBtnName, negativeBtnListener);
        //创建一个消息对话框
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 销毁Dialog 避免冲突弹出
     */
    public static void destoryDialog(){
        if(dialog!=null){
            dialog.dismiss();
            dialog  = null;
        }
    }
    /**
     *
     * @param dialog 弹出框
     * @param flag 是否关闭弹出框  false 不关闭，true 关闭
     */
    public static  void setDialogDismiss(DialogInterface dialog,boolean flag) {
        try
        {
            Field field = dialog.getClass()
                    .getSuperclass().getDeclaredField(
                            "mShowing" );
            field.setAccessible( true );
            // 将mShowing变量设为false，表示对话框已关闭
            field.set(dialog, flag);
            dialog.dismiss();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

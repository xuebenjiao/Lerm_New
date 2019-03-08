

package com.dcyx.app.global.Ibase;

/**
 * @name BaseScan
 * @author xbj
 * @date 2017/9/14
 * @desc  扫描接口类
 */
public interface BaseScan {
	/**
	 * 初始化
	 * @Title: init 
	 * @Description: 初始化 
	 * @return void
	 */
	public void init();
	/**
	 * 扫描
	 * @Title: scan 
	 * @Description: 扫描  
	 * @return void
	 */
	public void scan();
	/**
	 * 释放资源
	 * @Title: destory 
	 * @Description: 程序退出时销毁释放资源    
	 * @return void
	 */
	public void destory();
	/**
	 * 取消正在进行的扫描
	 * @Title: cancleScan 
	 * @Description: 取消正在进行的扫描
	 * @return void
	 */
	public void cancleScan();

}

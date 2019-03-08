package com.dcyx.app.global.Ibase;

/**
* @name BaseScanView
* @author xbj
* @date 2017/9/21
* @desc 扫描接口
*/

public interface BaseScanView {
	
	/**
	 * 是否处理扫描结果
	 */
	public boolean doScanner(String resultData);	
}

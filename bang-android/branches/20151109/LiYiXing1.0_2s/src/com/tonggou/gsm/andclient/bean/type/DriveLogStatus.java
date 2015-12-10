package com.tonggou.gsm.andclient.bean.type;

/**
 * 记录的状态
 * @author lwz
 *
 */
public enum DriveLogStatus {
	/**
	 * 有效的记录
	 */
	ENABLED,
	/**
	 * 无效的记录
	 */
	DISABLE,
	/**
	 * 记录正在继续读写中，（数据库中不存）
	 */
	DRIVING
}
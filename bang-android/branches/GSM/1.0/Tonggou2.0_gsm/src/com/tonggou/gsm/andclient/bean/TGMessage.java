package com.tonggou.gsm.andclient.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tonggou.gsm.andclient.bean.type.MessageActionType;
import com.tonggou.gsm.andclient.bean.type.MessageType;

@DatabaseTable(tableName = "tg_message")
public class TGMessage implements Serializable {
	private static final long serialVersionUID = -6593546385850592060L;

	/** 数据库名 */
	public static final String DB_NAME = "tonggou_message.db";
	/** 数据库版本号 */
	public static final int DB_VERSION = 3;

	/** 消息 id */
	public static final String COLUMN_MSG_ID = "msgId";
	/** 用户名 */
	public static final String COLUMN_USER_NO = "userNo";
	/** 消息类型 */
	public static final String COLUMN_MSG_TYPE = "type";
	/** 时间戳 */
	public static final String COLUMN_TIMESTAMP = "timestamp";
	/** 是否读过 */
	public static final String COLUMN_IS_READ = "isRead";

	@DatabaseField(columnName = "_ID", generatedId = true)
	private long _id;

	@DatabaseField(columnName = COLUMN_MSG_ID, dataType = DataType.LONG_STRING)
	private String id; // 唯一标识号

	@DatabaseField(columnName = COLUMN_USER_NO, dataType = DataType.STRING)
	private String userNo;

	@DatabaseField(columnName = COLUMN_MSG_TYPE, dataType = DataType.ENUM_STRING)
	private MessageType type; // 消息类型

	@DatabaseField(dataType = DataType.STRING)
	private String title; // 标题
	
	@DatabaseField(dataType = DataType.LONG_STRING)
	private String content; // 内容描述

	@DatabaseField(dataType = DataType.ENUM_STRING)
	private MessageActionType actionType; // 操作类型
	
	@DatabaseField(dataType = DataType.LONG_STRING)
	private String params; // actionType所依赖的业务数据
	
	@DatabaseField(columnName=COLUMN_IS_READ, dataType=DataType.BOOLEAN)
	private boolean isRead;
	
	@Expose(deserialize = false, serialize = false)
	@DatabaseField(columnName=COLUMN_TIMESTAMP, dataType=DataType.LONG)
	private long timestamp; // 时间戳

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MessageActionType getActionType() {
		return actionType;
	}

	public void setActionType(MessageActionType actionType) {
		this.actionType = actionType;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

}

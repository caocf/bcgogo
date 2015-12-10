package com.tonggou.gsm.andclient.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 违章记录
 * @author lwz
 *
 */
public class ViolationRecord implements Parcelable {
	
	private String date;		// 违章日期
	private String area;		// 违章地点
	private String act;			// 违章原因
	private String code;		// 违章码
	private String fen;			// 扣分
	private String money;		// 罚款金额
	
	
	public static Parcelable.Creator<ViolationRecord> CREATOR = new Creator<ViolationRecord>() {
		
		@Override
		public ViolationRecord[] newArray(int size) {
			return new ViolationRecord[size];
		}
		
		@Override
		public ViolationRecord createFromParcel(Parcel source) {
			ViolationRecord record = new ViolationRecord();
			record.date = source.readString();
			record.area = source.readString();
			record.act = source.readString();
			record.code = source.readString();
			record.fen = source.readString();
			record.money = source.readString();
			return record;
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(date);
		dest.writeString(area);
		dest.writeString(act);
		dest.writeString(code);
		dest.writeString(fen);
		dest.writeString(money);
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}
	public String getCode() {
		code = code.trim();
		if( TextUtils.isEmpty(code) 
				|| "-".equals(code) || "null".equalsIgnoreCase(code)) {
			code = "--";
		}
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFen() {
		return fen == null ? "0" : fen;
	}
	public void setFen(String fen) {
		this.fen = fen;
	}
	public String getMoney() {
 		return money == null ? "0" : money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	
	public int getIntFen() {
		int intFen = 0;
		try {
			intFen = Integer.valueOf(getFen().trim());
		} catch (NumberFormatException e) {
		}
		return intFen;
	}
	
	public float getFloatMoney() {
		float floatMoney = 0F;
		try {
			floatMoney = Float.valueOf(getMoney().trim());
		} catch (NumberFormatException e) {
		}
		
		return floatMoney;
	}
}

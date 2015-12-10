package com.tonggou.gsm.andclient.net.response;

import java.util.List;

import com.tonggou.gsm.andclient.bean.GasStation;

/**
 * 聚合加油站返回数据类
 * @author lwz
 *
 */
public class JuheGasStationResponse implements IResponse {

	private int resultcode;
	private String reason;
	private Result result;
	private int error_code;
	
	@Override
	public boolean isSuccess() {
		return resultcode == 200;
	}

	@Override
	public String getMessage() {
		return reason;
	}

	@Override
	public int getMsgCode() {
		return error_code;
	}
	
	public int getResultcode() {
		return resultcode;
	}

	public void setResultcode(int resultcode) {
		this.resultcode = resultcode;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public class Result {
		public List<GasStation> data;
		public PageInfo pageinfo;
	}
		
	public class PageInfo {
		public int pnums;
		public int current;
	}

}

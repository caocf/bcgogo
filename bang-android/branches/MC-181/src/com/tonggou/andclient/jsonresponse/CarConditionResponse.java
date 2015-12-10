package com.tonggou.andclient.jsonresponse;

import java.util.ArrayList;

import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.Pager;

public class CarConditionResponse extends BaseResponse {

	private static final long serialVersionUID = -805227647385088742L;

	private ArrayList<CarCondition> result;
	private Pager pager;
	public ArrayList<CarCondition> getResult() {
		return result;
	}
	public void setResult(ArrayList<CarCondition> result) {
		this.result = result;
	}
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
}

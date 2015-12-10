package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.JuheTransgress;

public class JuheTransgressResponse extends BaseResponse {

	private static final long serialVersionUID = 1528601781624468467L;

	private String resultcode;
	private String reason;
	private JuheTransgressList result;

	public String getResultcode() {
		return resultcode;
	}
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}

	public JuheTransgressList getResult() {
		return result;
	}

	public void setResult(JuheTransgressList result) {
		this.result = result;
	}

	public class JuheTransgressList {
		private List<JuheTransgress> lists;

		public List<JuheTransgress> getLists() {
			return lists;
		}

		public void setLists(List<JuheTransgress> lists) {
			this.lists = lists;
		}
	}
	
	
}

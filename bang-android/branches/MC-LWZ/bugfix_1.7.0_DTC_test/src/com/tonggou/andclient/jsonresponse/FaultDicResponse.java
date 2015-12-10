package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.FaultCodeInfo;

public class FaultDicResponse extends BaseResponse {
	
	private static final long serialVersionUID = 3473601602695361948L;
	
	private List<FaultCodeInfo> faultCodeList;
	private String dictionaryId;
	private String dictionaryVersion;
	private String isCommon;
	
	public List<FaultCodeInfo> getFaultCodeList() {
		return faultCodeList;
	}
	public void setFaultCodeList(List<FaultCodeInfo> faultCodeList) {
		this.faultCodeList = faultCodeList;
	}
	public String getDictionaryId() {
		return dictionaryId;
	}
	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}
	public String getDictionaryVersion() {
		return dictionaryVersion;
	}
	public void setDictionaryVersion(String dictionaryVersion) {
		this.dictionaryVersion = dictionaryVersion;
	}
	public String getIsCommon() {
		return isCommon;
	}
	public void setIsCommon(String isCommon) {
		this.isCommon = isCommon;
	}
}

package com.tonggou.andclient.jsonresponse;

import java.util.List;
import com.tonggou.andclient.vo.ServiceCategoryDTO;

public class ServiceCategoryResponse {
	private String status; 
	private String msgCode;  
	private String message ;          
	private String data ;
	private List<ServiceCategoryDTO>  serviceCategoryDTOList;

	public List<ServiceCategoryDTO> getServiceCategoryDTOList() {
		return serviceCategoryDTOList;
	}
	public void setServiceCategoryDTOList(
			List<ServiceCategoryDTO> serviceCategoryDTOList) {
		this.serviceCategoryDTOList = serviceCategoryDTOList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}

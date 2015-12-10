package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.PlaceResponse;
import com.tonggou.andclient.jsonresponse.ServiceCategoryResponse;
/**
 * 取服务类型列表解析类
 * @author think
 *
 */
public class ServiceCategoryParser extends TonggouBaseParser{
	ServiceCategoryResponse serviceCategoryResponse;
	 
	public ServiceCategoryResponse getPlaceResponse() {
		return serviceCategoryResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 serviceCategoryResponse = gson.fromJson(dataFormServer, ServiceCategoryResponse.class);
			 if(serviceCategoryResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(serviceCategoryResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = serviceCategoryResponse.getMessage();
					 parseSuccessfull = false;
				 }
			 }else{
				 parseSuccessfull = false;
			 }
		}catch(Exception ex){
			 parseSuccessfull = false;
		}
	}
}


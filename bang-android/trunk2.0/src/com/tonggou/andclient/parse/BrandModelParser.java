package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.BrandModelResponse;

public class BrandModelParser extends TonggouBaseParser{
	BrandModelResponse brandModelResponse;
	 
	public BrandModelResponse getBrandModelResponse() {
		return brandModelResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 brandModelResponse = gson.fromJson(dataFormServer, BrandModelResponse.class);
			 if(brandModelResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(brandModelResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = brandModelResponse.getMessage();
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

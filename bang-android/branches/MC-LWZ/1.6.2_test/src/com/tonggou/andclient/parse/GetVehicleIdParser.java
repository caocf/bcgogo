package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.GetVehicleIdResponse;
import com.tonggou.andclient.jsonresponse.LoginResponse;
import com.tonggou.andclient.jsonresponse.SearchPasswordResponse;

public class GetVehicleIdParser extends TonggouBaseParser{
	GetVehicleIdResponse getVehicleIdResponse;
	 
	public GetVehicleIdResponse getSearchPasswordResponse() {
		return getVehicleIdResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 getVehicleIdResponse = gson.fromJson(dataFormServer, GetVehicleIdResponse.class);
			 if(getVehicleIdResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(getVehicleIdResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = getVehicleIdResponse.getMessage();
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

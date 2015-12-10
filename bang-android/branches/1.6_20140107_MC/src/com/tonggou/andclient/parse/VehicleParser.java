package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.VehicleResponse;

public class VehicleParser  extends TonggouBaseParser{
	VehicleResponse vehicleResponse;
	 
	public VehicleResponse getVehicleResponse() {
		return vehicleResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 vehicleResponse = gson.fromJson(dataFormServer, VehicleResponse.class);
			 if(vehicleResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(vehicleResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = vehicleResponse.getMessage();
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

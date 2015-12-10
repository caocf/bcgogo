package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.FaultDicResponse;

public class GetFaultDicParser extends TonggouBaseParser{
	FaultDicResponse faultDicResponse;
	 
	public FaultDicResponse getFaultDicResponse() {
		return faultDicResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 faultDicResponse = gson.fromJson(dataFormServer, FaultDicResponse.class);
			 if(faultDicResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(faultDicResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = faultDicResponse.getMessage();
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
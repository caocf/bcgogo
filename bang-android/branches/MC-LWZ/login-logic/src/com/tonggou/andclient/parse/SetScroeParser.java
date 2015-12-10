package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.SetScroeResponse;

public class SetScroeParser extends TonggouBaseParser{
	SetScroeResponse setScroeResponse;
	 
	public SetScroeResponse getSetScroeResponse() {
		return setScroeResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 setScroeResponse = gson.fromJson(dataFormServer, SetScroeResponse.class);
			 if(setScroeResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(setScroeResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = setScroeResponse.getMessage();
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

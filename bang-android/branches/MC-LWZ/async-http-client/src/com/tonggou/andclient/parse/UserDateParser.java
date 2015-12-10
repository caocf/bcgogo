package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.LoginResponse;
import com.tonggou.andclient.jsonresponse.UserDateResponse;

public class UserDateParser extends TonggouBaseParser{
	UserDateResponse userDateResponse;
	 
	public UserDateResponse getUserDateResponse() {
		return userDateResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 userDateResponse = gson.fromJson(dataFormServer, UserDateResponse.class);
			 if(userDateResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(userDateResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = userDateResponse.getMessage();
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

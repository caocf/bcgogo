package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.LoginResponse;

public class LoginParser extends TonggouBaseParser{
	LoginResponse loginResponse;
	 
	public LoginResponse getLoginResponse() {
		return loginResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 loginResponse = gson.fromJson(dataFormServer, LoginResponse.class);
			 if(loginResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(loginResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = loginResponse.getMessage();
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

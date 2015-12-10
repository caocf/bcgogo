package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.UserDataChangePasswordResponse;

public class UserDataChangePasswordParser extends TonggouBaseParser{
	UserDataChangePasswordResponse userDataChangePasswordResponse;
	 
	public UserDataChangePasswordResponse getUserDataChangePasswordResponse() {
		return userDataChangePasswordResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 userDataChangePasswordResponse = gson.fromJson(dataFormServer, UserDataChangePasswordResponse.class);
			 if(userDataChangePasswordResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(userDataChangePasswordResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = userDataChangePasswordResponse.getMessage();
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

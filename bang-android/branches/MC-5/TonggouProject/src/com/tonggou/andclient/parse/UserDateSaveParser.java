package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.UserDateSaveResponse;

public class UserDateSaveParser  extends TonggouBaseParser{
	UserDateSaveResponse userDateSaveResponse;
	 
	public UserDateSaveResponse getUserDateSaveResponse() {
		return userDateSaveResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 userDateSaveResponse = gson.fromJson(dataFormServer,UserDateSaveResponse.class);
			 if(userDateSaveResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(userDateSaveResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = userDateSaveResponse.getMessage();
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

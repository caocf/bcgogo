package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.LoginResponse;
import com.tonggou.andclient.jsonresponse.SearchPasswordResponse;

public class SearchPasswordParser extends TonggouBaseParser{
	SearchPasswordResponse searchPasswordResponse;
	 
	public SearchPasswordResponse getSearchPasswordResponse() {
		return searchPasswordResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 searchPasswordResponse = gson.fromJson(dataFormServer, SearchPasswordResponse.class);
			 if(searchPasswordResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(searchPasswordResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = searchPasswordResponse.getMessage();
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

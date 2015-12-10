package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.FeedBackResponse;

public class FeedBackParser extends TonggouBaseParser{
	FeedBackResponse feedBackResponse;
	 
	public FeedBackResponse getFeedBackResponse() {
		return feedBackResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 feedBackResponse = gson.fromJson(dataFormServer, FeedBackResponse.class);
			 if(feedBackResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(feedBackResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = feedBackResponse.getMessage();
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

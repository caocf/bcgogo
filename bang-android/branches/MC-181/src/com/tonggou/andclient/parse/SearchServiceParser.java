package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.SearchServiceResponse;

/**
 * 查询服务解析类
 * @author think
 *
 */
public class SearchServiceParser extends TonggouBaseParser{
	SearchServiceResponse searchServiceResponse;
	 
	public SearchServiceResponse getStoreQueryResponse() {
		return searchServiceResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 searchServiceResponse = gson.fromJson(dataFormServer, SearchServiceResponse.class);
			 if(searchServiceResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(searchServiceResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = searchServiceResponse.getMessage();
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

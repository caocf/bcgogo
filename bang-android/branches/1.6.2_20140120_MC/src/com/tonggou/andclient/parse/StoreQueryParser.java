package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.StoreQueryResponse;
import com.tonggou.andclient.vo.MemberInfo;

public class StoreQueryParser extends TonggouBaseParser{
	StoreQueryResponse storeQueryResponse;
	 
	public StoreQueryResponse getStoreQueryResponse() {
		return storeQueryResponse;
	}
	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		
		try{
			 Gson gson = new Gson();
			 MemberInfo myClass = new MemberInfo();
			 String jsonString = gson.toJson(myClass);
			 dataFormServer = dataFormServer.replace("memberInfo\":\"\"", "memberInfo\":null");
			 dataFormServer = dataFormServer.replace("productCategoryList\":\"\"", "productCategoryList\":null");
			 storeQueryResponse = gson.fromJson(dataFormServer, StoreQueryResponse.class);
			 if(storeQueryResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(storeQueryResponse.getStatus())){
					//注册成功
					parseSuccessfull = true;
				 }else{
					 errorMessage = storeQueryResponse.getMessage();
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

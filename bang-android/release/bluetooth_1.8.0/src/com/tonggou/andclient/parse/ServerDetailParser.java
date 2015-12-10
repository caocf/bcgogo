package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.ServerDetailResponse;


/**
 * 单据详情解析类
 * @author think
 *
 */
public class ServerDetailParser extends TonggouBaseParser{
	ServerDetailResponse serverDetailReponse;
	 
	public ServerDetailResponse getServerDetailReponse() {
		return serverDetailReponse;
	}

	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		try{
			 Gson gson = new Gson();
			 serverDetailReponse = gson.fromJson(dataFormServer, ServerDetailResponse.class);
			 if(serverDetailReponse!=null){
				 if("SUCCESS".equalsIgnoreCase(serverDetailReponse.getStatus())){
					 //注册成功
					 parseSuccessfull = true;
				 }else{
					 errorMessage = serverDetailReponse.getMessage();
					 parseSuccessfull = false;
				 }
			 }else{
				 parseSuccessfull = false;
			 }
		}catch(Exception ex){
			 errorMessage = "解析出错";
			 parseSuccessfull = false;
		}
		
	}
}

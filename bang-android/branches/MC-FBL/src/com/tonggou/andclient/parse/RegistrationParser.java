package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.RegistrationResponse;

/**
 * 注册解析类
 * @author think
 *
 */
public class RegistrationParser extends TonggouBaseParser{
	RegistrationResponse registrationReponse;
	 
	public RegistrationResponse getRegistrationReponse() {
		return registrationReponse;
	}

	/**
	 * 具体的解析方法
	 */
	public void parsing(String dataFormServer) {
		try{
			 Gson gson = new Gson();
			 registrationReponse = gson.fromJson(dataFormServer, RegistrationResponse.class);
			 if(registrationReponse!=null){
				 if("SUCCESS".equalsIgnoreCase(registrationReponse.getStatus())){
					 //注册成功
					 parseSuccessfull = true;
				 }else{
					 errorMessage = registrationReponse.getMessage();
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

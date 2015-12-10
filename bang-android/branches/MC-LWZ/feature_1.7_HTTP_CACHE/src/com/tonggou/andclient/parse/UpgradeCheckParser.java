package com.tonggou.andclient.parse;

import com.google.gson.Gson;
import com.tonggou.andclient.jsonresponse.UpgradeCheckResponse;
import com.tonggou.andclient.vo.Version;

public class UpgradeCheckParser extends TonggouBaseParser{
	UpgradeCheckResponse upgradeCheckResponse;
	private Version verson ;
	
	
	
	public UpgradeCheckParser(){
		
	}
	
	public UpgradeCheckResponse getLoginResponse() {
		return upgradeCheckResponse;
	}
	
	@Override
	public void parsing(String dataFormServer) {
		try{
			 Gson gson = new Gson();
			 upgradeCheckResponse = gson.fromJson(dataFormServer, UpgradeCheckResponse.class);
			 if(upgradeCheckResponse!=null){
				 if("SUCCESS".equalsIgnoreCase(upgradeCheckResponse.getStatus())){
					parseSuccessfull = true;
					
					verson = new Version();
					if("force".equals(upgradeCheckResponse.getAction())){
						verson.setAction(Version.UPDATE_ACTION_FORCE);
					}else if("alert".equals(upgradeCheckResponse.getAction())){
						verson.setAction(Version.UPDAATE_ACTION_ALERT);
					}else if("normal".equals(upgradeCheckResponse.getAction())){
						verson.setAction(Version.UPDATE_ACTION_NORMAL);
					}
					verson.setCode(upgradeCheckResponse.getMsgCode() + "");
					verson.setMessage(upgradeCheckResponse.getMessage());
					verson.setUrl(upgradeCheckResponse.getUrl());
				 }else{
					 errorMessage = upgradeCheckResponse.getMessage();
					 parseSuccessfull = false;
				 }
			 }else{
				 parseSuccessfull = false;
			 }
		}catch(Exception ex){
			 parseSuccessfull = false;
		}
	}


	public Version getVersion() {
		return verson;
	}

}

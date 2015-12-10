package com.tonggou.andclient.app;

import java.util.List;

import org.apache.http.Header;

import android.content.Context;

import com.tonggou.andclient.jsonresponse.FaultDicResponse;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.UpdateVehicleFaultDicRequest;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.vo.FaultCodeInfo;

public class UpdateFaultDic {
	private UpdateFaultDic(Context myCon){
		myContext = myCon;
	}
	
	/**
	 * 返回单态实例
	 */
	private static UpdateFaultDic singleUpdateFaultDic;
	private Context myContext;
	public static UpdateFaultDic getUpdateFaultDic(Context con){
		 if(singleUpdateFaultDic == null){
			 singleUpdateFaultDic = new UpdateFaultDic(con);
		 }
		 return singleUpdateFaultDic;
	}
	
	/**
	 * 更新故障码
	 */
	public void updateFaultDic(final String modleId){
		if(modleId==null || "".equals(modleId)){
        	return;
        }
        String changeModleId;
        if("common".equals(modleId)){
        	changeModleId = "NULL";
        }else{
        	changeModleId = modleId;
        }		
        
        String versionStr = SaveDB.getSaveDB(myContext).getFaultCodesVersionByModle(modleId);
		if(versionStr==null){
			versionStr = "NULL";
		}
		final String lastVersionStr = versionStr;
 
		UpdateVehicleFaultDicRequest request = new UpdateVehicleFaultDicRequest(); 
		request.setApiParams(versionStr, changeModleId );
		request.doRequest(myContext, new AsyncJSONResponseParseHandler<FaultDicResponse>() {

			@Override
			public void onParseSuccess(FaultDicResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				String nowVersion = result.getDictionaryVersion();
				if (nowVersion != null && !"".equals(nowVersion) && !lastVersionStr.equals(nowVersion)){
					//版本不一样
					List<FaultCodeInfo> codes = result.getFaultCodeList();
					SaveDB.getSaveDB(myContext).updateFaultCode(modleId, nowVersion, codes);
				}
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
//				super.onParseFailure(errorCode, errorMsg);
				TongGouApplication.showLog(errorMsg);
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				TongGouApplication.showLog("request onFailure @ " + arg3.getMessage());
			}
			
			@Override
			public Class<FaultDicResponse> getTypeClass() {
				return FaultDicResponse.class;
			}
			
		});
//		@delete by lwz
//		@cause 使用新的网络模块
//		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/faultDic/dicVersion/"+versionStr+"/vehicleModelId/"+changeModleId;
//		GetFaultDicParser getFaultDicParserParser = new GetFaultDicParser();		
//		NetworkState ns = Network.getNetwork(myContext).httpGetUpdateString(url,getFaultDicParserParser);	
//		if(ns.isNetworkSuccess()){
//			if(getFaultDicParserParser.isSuccessfull()){
//				result = true;
//				String nowVersion = getFaultDicParserParser.getFaultDicResponse().getDictionaryVersion();
//				if (nowVersion != null && !"".equals(nowVersion) && !versionStr.equals(nowVersion)){
//					//版本不一样
//					List<FaultCodeInfo> codes = getFaultDicParserParser.getFaultDicResponse().getFaultCodeList();
//					result = SaveDB.getSaveDB(myContext).updateFaultCode(modleId, nowVersion, codes);
//				}
//			}
//		}
	}
	
	
	
}

package com.tonggou.andclient.app;

import java.util.List;

import com.tonggou.andclient.MainActivity;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.GetFaultDicParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.vo.FaultCodeInfo;

import android.content.Context;



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
	public boolean updateFaultDic(String modleId){
		boolean result = false;
		if(modleId==null || "".equals(modleId)){
        	return result;
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
 
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/faultDic/dicVersion/"+versionStr+"/vehicleModelId/"+changeModleId;
		GetFaultDicParser getFaultDicParserParser = new GetFaultDicParser();		
		NetworkState ns = Network.getNetwork(myContext).httpGetUpdateString(url,getFaultDicParserParser);	
		if(ns.isNetworkSuccess()){
			if(getFaultDicParserParser.isSuccessfull()){
				result = true;
				String nowVersion = getFaultDicParserParser.getFaultDicResponse().getDictionaryVersion();
				if (nowVersion != null && !"".equals(nowVersion) && !versionStr.equals(nowVersion)){
					//版本不一样
					List<FaultCodeInfo> codes = getFaultDicParserParser.getFaultDicResponse().getFaultCodeList();
					result = SaveDB.getSaveDB(myContext).updateFaultCode(modleId, nowVersion, codes);
				}
			}
		}
		return result;
	}
	
	
	
}

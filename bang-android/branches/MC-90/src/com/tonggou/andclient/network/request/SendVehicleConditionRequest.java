package com.tonggou.andclient.network.request;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.network.API;

public class SendVehicleConditionRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.SEND_VEHICLE_CONDITION;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}
	
	public void setRequestParams(String userNo, String oilWear,String currentMileage,String instantOilWear,
            String oilWearPerHundred,String oilMassStr,String engineCoolantTemperature,String batteryVoltage) {
		
		HttpRequestParams params = new HttpRequestParams();
		params.put("userNo", userNo);
		params.put("vehicleId", TongGouApplication.connetedVIN);
		params.put("obdSN", TongGouApplication.connetedObdSN);
		params.put("vehicleId", TongGouApplication.connetedVehicleID);
		params.put("reportTime", System.currentTimeMillis());
		
		if(oilWear!=null&&!"".equals(oilWear)&&!"N/A".equals(oilWear)){
			params.put("oilWear", Double.valueOf(oilWear));
		}
		if(currentMileage!=null&&!"".equals(currentMileage)&&!"N/A".equals(currentMileage)){
			params.put("currentMileage", (int)Math.floor(Float.valueOf(currentMileage)));
		}
		if(instantOilWear!=null&&!"".equals(instantOilWear)&&!"N/A".equals(instantOilWear)){
			params.put("instantOilWear", Double.valueOf(instantOilWear));
		}
		if(oilWearPerHundred!=null&&!"".equals(oilWearPerHundred)&&!"N/A".equals(oilWearPerHundred)){
			params.put("oilWearPerHundred", Double.valueOf(oilWearPerHundred)) ;
		}
		if(oilMassStr!=null&&!"%".equals(oilMassStr)&&!"N/A%".equals(oilMassStr)){
			params.put("oilMass", oilMassStr);
		}
		if(engineCoolantTemperature!=null&&!"".equals(engineCoolantTemperature)&&!"N/A".equals(engineCoolantTemperature)){
			params.put("engineCoolantTemperature", Double.valueOf(engineCoolantTemperature));
		}
		if(batteryVoltage!=null&&!"".equals(batteryVoltage)&&!"N/A".equals(batteryVoltage)){
			params.put("batteryVoltage", Double.valueOf(batteryVoltage));
		}
		
		super.setRequestParams(params);
	}

}

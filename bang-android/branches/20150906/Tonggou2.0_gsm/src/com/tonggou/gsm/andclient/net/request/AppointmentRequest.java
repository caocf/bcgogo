package com.tonggou.gsm.andclient.net.request;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonggou.gsm.andclient.bean.DTCInfo;
import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpRequest;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;

public class AppointmentRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.APPOINTMENT_SERVICE;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	/**
	 * 
	 * @param shopId			预约的店铺 ID
	 * @param serviceCategoryId	预约的服务 ID
	 * @param mobile	预约人电话
	 * @param contact	预约人姓名
	 * @param userNo	用户名
	 * @param appointTime 预约时间
	 * @param vehicleNo	车牌号
	 * @param vehicleVin 车架号
	 * @param vehicleBrand 车品牌
	 * @param vehicleBrandId 品牌 ID
	 * @param vehicleModel	车型
	 * @param vehicleModelId 车型 ID
	 * @param remark 备注
	 * @param faultInfoItems	故障信息集合
	 */
	public void setRequestParams(String shopId, String serviceCategoryId, 
			String mobile, String contact, String userNo, long appointTime,
			String vehicleNo, String vehicleVin, String vehicleBrand, 
			String vehicleBrandId, String vehicleModel,  String vehicleModelId, 
			String remark, List<DTCInfo> dtcList) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("shopId", shopId);
		params.put("serviceCategoryId", serviceCategoryId);
		params.put("appointTime", appointTime);
		params.put("mobile", mobile);
		params.put("contact", contact);
		params.put("userNo", userNo);
		params.put("vehicleNo", vehicleNo);
		params.put("vehicleVin", vehicleVin);
		params.put("vehicleBrand", vehicleBrand);
		params.put("vehicleBrandId", vehicleBrandId);
		params.put("vehicleModel", vehicleModel);
		params.put("vehicleModelId", vehicleModelId);
		params.put("remark", remark);
		params.put("faultInfoItems", convert2FaultInfo(dtcList));
		super.setRequestParams(params);
	}
	
	public JsonArray convert2FaultInfo(List<DTCInfo> dtcList) {
		if(dtcList == null || dtcList.isEmpty()) {
			return null;
		}
		JsonArray infoArray = new JsonArray();
		for( DTCInfo item : dtcList ) {
			JsonObject infoJsonObj = new JsonObject();
			infoJsonObj.addProperty("faultCode", item.getErrorCode());
			infoJsonObj.addProperty("appVehicleId", item.getAppVehicleId());
			infoJsonObj.addProperty("description", item.getContent());
			infoArray.add(infoJsonObj);
		}
		return infoArray;
	}
}

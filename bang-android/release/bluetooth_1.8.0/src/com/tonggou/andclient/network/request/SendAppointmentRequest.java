package com.tonggou.andclient.network.request;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.tonggou.andclient.network.API;

/**
 * 发送在线预约请求
 * @author lwz
 *
 */
public class SendAppointmentRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.SEND_APPOINTMENT;
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
			String remark, List<Map<String, String>> faultInfoItems) {
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
		params.put("faultInfoItems", faultInfoItems == null 
				|| faultInfoItems.isEmpty() ? null : new Gson().toJsonTree(faultInfoItems));
		super.setRequestParams(params);
	}

}

package com.tonggou.gsm.andclient.net.response;

import com.tonggou.gsm.andclient.bean.ServiceDetail;

/**
 * 服务详情
 * @author lwz
 *
 */
public class ServiceHistoryDetailResponse extends BaseResponse {

	private static final long serialVersionUID = 1291865451536351083L;

	private ServiceDetail serviceDetail;

	public ServiceDetail getServiceDetail() {
		return serviceDetail;
	}

	public void setServiceDetail(ServiceDetail serviceDetail) {
		this.serviceDetail = serviceDetail;
	}

}
